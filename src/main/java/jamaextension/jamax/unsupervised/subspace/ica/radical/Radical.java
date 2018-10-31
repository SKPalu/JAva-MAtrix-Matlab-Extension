/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.unsupervised.subspace.ica.radical;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.SvdJLapack;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.Sort;
import jamaextension.jamax.datafun.SortMat;
import jamaextension.jamax.elfun.JElfun;

/**
 * 
 * @author Sione
 */
public class Radical
{

    private Matrix mixingData;
    private Matrix unmixingMatrix;
    private Matrix unmixingComponents;
    private int numAnglesK = 150;
    private boolean augmented = true;
    private int numReplicatedPoints = 20;
    private double stdDeviation = 0.175;
    private boolean built = false;
    private boolean verbose = false;

    public Radical(Matrix mixingData)
    {
        String msg = "";
        if (mixingData == null)
        {
            msg = "Parameter \"mixingData\" must be non-null.";
            throw new ConditionalRuleException("Radical", msg);
        }
        if (mixingData.isVector())
        {
            msg = "Data parameter \"mixingData\" must be a matrix and not a vector.";
            throw new ConditionalRuleException("Radical", msg);
        }

        int m = mixingData.getRowDimension();
        int n = mixingData.getColumnDimension();
        if (m < 3)
        {
            msg = "Parameter \"mixingData\" must have at least 3 rows (ie, 3 mixed components).";
            throw new ConditionalRuleException("Radical", msg);
        }
        if (n < 20)
        {
            msg = "Parameter \"mixingData\" must have at least 20 columns (ie, 20 points).";
            throw new ConditionalRuleException("Radical", msg);
        }
        this.mixingData = mixingData;

    }

    public void build()
    {

        int K = this.numAnglesK;
        boolean AUG_FLAG = this.augmented;
        int reps = this.numReplicatedPoints;
        double stdev = this.stdDeviation;
        Matrix X = this.mixingData;

        int dim = X.getRowDimension();
        int N = X.getColumnDimension();
        int m = (int) Math.floor(Math.sqrt((double) N)); // m for use in
                                                         // m-spacing estimator.

        /*
         * Whiten the data. Store the whitening operation to combine with %
         * rotation matrix for total solution.
         */

        // [u,s,v]=svd(cov(X'));
        Matrix covX = JDatafun.cov(X.transpose());
        SvdJLapack svd = new SvdJLapack(covX);
        Matrix u = svd.getU();
        Matrix s = svd.getS();
        Matrix v = svd.getV();

        Matrix Whitening_mat = v.times(JElfun.pow(s, -0.5)).times(u.transpose());// v*s^(-.5)*u';
        Matrix X_white = Whitening_mat.times(X);

        int sweeps = dim - 1;
        Matrix oldTotalRot = Matrix.eye(dim);
        int sweepIter = 0; // Current sweep number.
        Matrix totalRot = Matrix.eye(dim);
        Matrix xcur = X_white;

        /*
         * K represents the number of rotations to examine on the FINAL % sweep.
         * To optimize performance, we start with a smaller number of %
         * rotations to examine. Then, we increase the % number of angles to get
         * better resolution as we get closer to the % solution. For the first
         * half of the sweeps, we use a constant % number for K. Then we
         * increase it exponentially toward the finish.
         */
        int finalK = K;
        double val = Math.ceil((double) sweeps / 2.0);
        val = Math.pow(1.3, val);
        double startKfloat = (double) finalK / val;// (finalK/1.3^(ceil(sweeps/2)));
        double newKfloat = startKfloat;

        for (int sweepNum = 0; sweepNum < sweeps; sweepNum++)
        {

            if (verbose)
            {
                System.out.println("Radical : build() -> Sweep #" + (sweepNum + 1) + " of " + sweeps + " ;\n");
            }
            double range = Math.PI / 2.0;

            // Compute number of angle samples for this sweep.
            int newK = 0;
            if ((sweepNum + 1) > (sweeps / 2))
            {
                newKfloat = newKfloat * 1.3;
                newK = (int) Math.floor(newKfloat);
            }
            else
            {
                newKfloat = startKfloat;
                newK = Math.max(30, (int) Math.floor(newKfloat));
            }

            /* *********************************************************
             * % Iterate over all possible Jacobi rotations. %
             * ********************************************************
             */
            for (int i = 0; i < (dim - 1); i++)
            {
                for (int j = i + 1; j < dim; j++)
                {

                    // fprintf(1,'Unmixing dimensions %02d and %02d ...',i,j);
                    if (verbose)
                    {
                        System.out.println("Radical : build() -> Unmixing dimensions " + i + " and " + j + " ...\n");
                    }

                    /* **********************************************
                     * % Extract dimensions (i,j) from the current data. %
                     * *********************************************
                     */
                    Matrix curSubSpace = xcur.getRowAt(i).mergeV(xcur.getRowAt(j));// [xcur(i,:);xcur(j,:)];

                    /* ***************************************************
                     * % Find the best angle theta for this Jacobi rotation. %
                     * **************************************************
                     */

                    // [thetaStar,rotStar]=radicalOptTheta(curSubSpace,stdev,m,reps,newK,range);
                    double thetaStar = radicalOptTheta(curSubSpace, m, newK, range);

                    /* *****************************************
                     * % Incorporate Jacobi rotation into solution. %
                     * *****************************************
                     */

                    Matrix newRotComponent = Matrix.eye(dim);
                    newRotComponent.set(i, i, Math.cos(thetaStar));
                    newRotComponent.set(i, j, -Math.sin(thetaStar));
                    newRotComponent.set(j, i, Math.sin(thetaStar));
                    newRotComponent.set(j, j, Math.cos(thetaStar));
                    totalRot = newRotComponent.times(totalRot);
                    xcur = totalRot.times(X_white);

                }
            }

            oldTotalRot = totalRot;

        }// end outermost for

        Matrix Wopt = totalRot.times(Whitening_mat);
        this.unmixingMatrix = Wopt;
        Matrix Yopt = Wopt.times(X);
        this.unmixingComponents = Yopt;

        built = true;
    }

    private double radicalOptTheta(Matrix x, int m, int K, double range)
    {
        double stdev = this.stdDeviation;
        int reps = this.numReplicatedPoints;

        /*
         * m is the number of intervals in an m-spacing % reps is the number of
         * points used in smoothing % K is the number of angles theta to check
         * for each Jacobi rotation.
         */
        int d = x.getRowDimension();
        int N = x.getColumnDimension();

        /*
         * This routine assumes that it gets whitened data. % First, we augment
         * the points with reps near copies of each point.
         */
        Matrix xAug = null;
        if (reps == 1)
        {
            xAug = x;
        }
        else
        {
            xAug = JDatafun.randn(d, N * reps).arrayTimes(stdev).plus(x.repmat(1, reps));
        }

        /*
         * Then rotate this data to various angles, evaluate the sum of % the
         * marginals, and take the min.
         */
        double perc = range / (Math.PI / 2.0);
        double numberK = perc * K;
        int start = (int) Math.floor((double) (K - numberK) / 2.0) + 1;// Math.floor(K/2-numberK/2)+1;
        int endPt = (int) Math.ceil((double) (K + numberK) / 2.0);

        Matrix ent = new Matrix(1, K);
        double val = 0.0;

        for (int i = 0; i < K; i++)
        {
            /*
             * Map theta from -pi/4 to pi/4 instead of 0 to pi/2. % This will
             * allow us to use Amari-distance for test of % convergence.
             */
            double theta = (double) (i - 1) / ((double) K - 1.0) * Math.PI / 2.0 - Math.PI / 4.0;
            double[][] rotDouble =
            {
                    {
                            Math.cos(theta), -Math.sin(theta)
                    },
                    {
                            Math.sin(theta), Math.cos(theta)
                    }
            };
            Matrix rot = new Matrix(rotDouble);
            Matrix rotPts = rot.times(xAug);

            Matrix marginalAtTheta = new Matrix(1, d);
            for (int j = 0; j < d; j++)
            {
                // marginalAtTheta(j)=vasicekm(rotPts(j,:),m);
                val = vasicekm(rotPts.getRowAt(j), m);
                marginalAtTheta.set(0, j, val);
            }
            // ent(i)=sum(marginalAtTheta);
            val = JDatafun.sum(marginalAtTheta).start();
            ent.set(0, i, val);
        }

        Sort sort = new SortMat(ent);
        Indices ind = sort.getIndices();

        double thetaStar = (double) (ind.start() - 1) / ((double) K - 1.0) * Math.PI / 2.0 - Math.PI / 4.0;
        ;

        return thetaStar;
    }

    private double vasicekm(Matrix v, int m)
    {

        int len = v.length();
        Sort sort = new SortMat(v);// vals=sort(v);
        Matrix vals = (Matrix) sort.getSortedObject();

        // Note that the intervals overlap for this estimator.
        int[] indArr1 = Indices.linspace(m, len - 1).getRowPackedCopy();
        int[] indArr2 = Indices.linspace(0, len - m - 1).getRowPackedCopy();
        Matrix intvals = vals.getElements(indArr1).minus(vals.getElements(indArr2));// vals(m+1:len)-vals(1:len-m);
        Matrix hvec = JElfun.log(intvals);
        double h = JDatafun.sum(hvec).start();
        return h;
    }

    /**
     * @return the mixingData
     */
    public Matrix getMixingData()
    {
        return mixingData;
    }

    /**
     * @return the unmixingComponents
     */
    public Matrix getUnmixingComponents()
    {
        if (!isBuilt())
        {
            throw new IllegalArgumentException("getUnmixingComponents : The method \"build\" must be invoked first.");
        }
        return unmixingComponents;
    }

    /**
     * @return the numAnglesK
     */
    public int getNumAnglesK()
    {
        return numAnglesK;
    }

    /**
     * @param numAnglesK
     *            the numAnglesK to set
     */
    public void setNumAnglesK(int numAnglesK)
    {
        this.numAnglesK = numAnglesK;
    }

    /**
     * @return the augmented
     */
    public boolean isAugmented()
    {
        return augmented;
    }

    /**
     * @param augmented
     *            the augmented to set
     */
    public void setAugmented(boolean augmented)
    {
        this.augmented = augmented;
    }

    /**
     * @return the numReplicatedPoints
     */
    public int getNumReplicatedPoints()
    {
        return numReplicatedPoints;
    }

    /**
     * @param numReplicatedPoints
     *            the numReplicatedPoints to set
     */
    public void setNumReplicatedPoints(int numReplicatedPoints)
    {
        this.numReplicatedPoints = numReplicatedPoints;
    }

    /**
     * @return the stdDeviation
     */
    public double getStdDeviation()
    {
        return stdDeviation;
    }

    /**
     * @param stdDeviation
     *            the stdDeviation to set
     */
    public void setStdDeviation(double stdDeviation)
    {
        this.stdDeviation = stdDeviation;
    }

    /**
     * @return the built
     */
    public boolean isBuilt()
    {
        return built;
    }

    /**
     * @return the verbose
     */
    public boolean isVerbose()
    {
        return verbose;
    }

    /**
     * @param verbose
     *            the verbose to set
     */
    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    /**
     * @return the unmixingMatrix
     */
    public Matrix getUnmixingMatrix()
    {
        return unmixingMatrix;
    }
}
