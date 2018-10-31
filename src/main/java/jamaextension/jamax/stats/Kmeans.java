/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.stats;

import java.io.FileNotFoundException;
import java.io.IOException;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.KeyValue;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.Matrix3D;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.constants.KmnDist;
import jamaextension.jamax.constants.KmnEmptyAction;
import jamaextension.jamax.constants.KmnStart;
import jamaextension.jamax.constants.OptDisplay;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.Max;
import jamaextension.jamax.datafun.MaxMat;
import jamaextension.jamax.datafun.Min;
import jamaextension.jamax.datafun.MinMat;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortInd;
import jamaextension.jamax.datafun.QuickSortMat;
import jamaextension.jamax.elfun.JElfun;
import jamaextension.jamax.elmat.AccumArray;
import jamaextension.jamax.ops.JOps;
import jamaextension.jamax.specfun.JSpecfun;
import jamaextension.jamax.stats.statsutil.StatInsertNan;
import jamaextension.jamax.stats.statsutil.StatRemoveNan;

/**
 * 'Distance = cityblock' has been tested and compatible. 'Distance =
 * sqeuclidean' has been tested and compatible. 'Distance = correlation' has
 * been tested and compatible. 'Distance = cosine' has been tested and
 * compatible
 * 
 * @author Sione
 */
public class Kmeans
{
    // inputs

    private KmnDist distance = KmnDist.sqeuclidean;
    private Object start = KmnStart.sample;
    private Integer replicates;
    private KmnEmptyAction emptyaction = KmnEmptyAction.error;
    private boolean onlinephase = true;
    //
    private Matrix input;
    // private Matrix X;
    private Integer k;
    private KeyValue keyVal;
    private StatsOption opts = new StatsOption();
    // outputs
    private Indices idx;
    private Matrix C;
    private Matrix sumD;
    private Matrix D;
    // internal
    private int iter = 0;
    private int iter1;
    private int n;
    private int p;
    private Matrix X;
    private Indices m;
    private int maxit;
    private String dispfmt = "%6d\t%6d\t%8d\t%12g\n";
    private Integer reps;
    private int rep;
    private Indices moved;
    private Matrix Del;
    private double totsumD;
    private Indices changed;
    private Matrix3D Xmid;
    private Matrix Xsum;
    private int countM = 1;
    private boolean converged;
    private Indices oidx;
    private Indices nidx;
    private int nargout;
    private boolean useRandSam = false;

    public Kmeans(int nargout, Matrix X)
    {
        this(nargout, X, null);
    }

    public Kmeans(int nargout, Matrix X, Integer k)
    {
        String msg = "";
        if (X == null || X.isNull())
        {
            msg = "Matrix parameter \"X\" must be non-null or non-empty.";
            throw new ConditionalRuleException("Kmeans", msg);
        }
        if (k != null)
        {
            if (k.intValue() < 1)
            {
                msg = "Integer parameter \"k\" (= " + k + ") must be at least 1;";
                throw new ConditionalRuleException("Kmeans", msg);
            }
        }

        if (nargout < 1 || nargout > 4)
        {
            msg = "Integer parameter \"nargout\" must be 1 to 4 (output arguments).";
            throw new ConditionalRuleException("Kmeans", msg);
        }

        this.nargout = nargout;
        this.input = X;
        this.k = k;

    }

    public void build()
    {
        // [ignore,wasnan,X] = statremovenan(X);
        StatRemoveNan stn = new StatRemoveNan(input.copy());
        Indices wasnan = stn.getWasnan();
        boolean hadNaNs = wasnan.anyBoolean();
        X = (Matrix) stn.getVarargout().start();

        // X.printInLabel("X",15);
        if (hadNaNs)
        {
            // warning('stats:kmeans:MissingDataRemoved','Ignoring rows of X
            // with missing data.');
            System.out.println("build : Ignoring rows of X with missing data.");
        }// end

        // N points in P dimensional space
        n = X.getRowDimension();
        p = X.getColumnDimension();
        boolean cond = false;
        Matrix tmp = null;
        String msg = "";

        // pnames = { 'distance' 'start' 'replicates' 'emptyaction'
        // 'onlinephase' 'options' 'maxiter' 'display'};
        // dflts = {'sqeuclidean' 'sample' [] 'error' 'on' [] [] []};
        // [eid,errmsg,distance,start,reps,emptyact,online,options,maxit,display]
        // ...
        // = statgetargs(pnames, dflts, varargin{:});
        reps = this.replicates;

        Matrix Xnorm = null;
        double val1 = 0.0;
        double val2 = 0.0;

        if (this.distance == KmnDist.cosine)
        {
            tmp = JElfun.pow(X, 2.0);
            tmp = JDatafun.sum(tmp, Dimension.COL);
            Xnorm = JElfun.sqrt(tmp);// sqrt(sum(X.^2, 2));
            val1 = JDatafun.min(Xnorm).start();
            val2 = JDatafun.max(Xnorm).start();
            val2 = JSpecfun.EPS(val2);
            cond = val1 <= val2;
            if (cond)
            {// any(min(Xnorm) <= eps(max(Xnorm)))
             // error('stats:kmeans:ZeroDataForCos', ...
             // ['Some points have small relative magnitudes, making them ',
             // ...
             // 'effectively zero.\nEither remove those points, or choose a
             // ', ...
             // 'distance other than ''cosine''.']);
                msg = "Some points have small relative magnitudes, making them effectively zero."
                        + "\nEither remove those points, or choose a distance other than \"cosine\".";
                throw new ConditionalRuleException("build", msg);
            }// end
            int[] ones1Pind = new Indices(1, p).getRowPackedCopy();
            tmp = Xnorm.getColumns(ones1Pind); // X = X ./ Xnorm(:,ones(1,P));
            X = X.arrayRightDivide(tmp);
        }
        else if (this.distance == KmnDist.correlation)
        {
            tmp = JDatafun.mean(X, Dimension.COL);
            tmp = tmp.repmat(1, p);
            X = X.minus(tmp);// X = X - repmat(mean(X,2),1,P);
            tmp = JElfun.pow(X, 2.0);
            tmp = JDatafun.sum(tmp, Dimension.COL);
            Xnorm = JElfun.sqrt(tmp);// sqrt(sum(X.^2, 2));
            val1 = JDatafun.min(Xnorm).start();
            val2 = JDatafun.max(Xnorm).start();
            val2 = JSpecfun.EPS(val2);
            cond = val1 <= val2;
            if (cond)
            {// any(min(Xnorm) <= eps(max(Xnorm)))
             // error('stats:kmeans:ConstantDataForCorr', ...
             // ['Some points have small relative standard deviations, making
             // them ', ...
             // 'effectively constant.\nEither remove those points, or choose
             // a ', ...
             // 'distance other than ''correlation''.']);
                msg = "Some points have small relative standard deviations, making them effectively constant."
                        + "\nEither remove those points, or choose a distance other than \"correlation\".";
                throw new ConditionalRuleException("build", msg);
            }// end

            int[] ones1Pind = new Indices(1, p).getRowPackedCopy();
            tmp = Xnorm.getColumns(ones1Pind); // X = X ./ Xnorm(:,ones(1,P));
            X = X.arrayRightDivide(tmp);
        }
        else if (this.distance == KmnDist.hamming)
        {
            tmp = X.toColVector();
            cond = !tmp.EQ(0.0).OR(tmp.EQ(1.0)).allBoolean();
            if (cond)
            {
                msg = "Non-binary data cannot be clustered using \"hamming\" distance.";
                throw new ConditionalRuleException("build", msg);
            }
        }

        Matrix Xmins = null;
        Matrix Xmaxs = null;
        boolean startNumeric = false;
        Object CC = null;

        if (this.start instanceof KmnStart)
        {
            // } else if (this.start instanceof Matrix) {
            // } else if (this.start instanceof Number) {
            if (this.k == null)
            {
                msg = "The number of clusters, K must be specified.";
                throw new ConditionalRuleException("build", msg);
            }

            KmnStart startObj = (KmnStart) this.start;
            if (startObj == KmnStart.uniform)
            {
                if (this.distance == KmnDist.hamming)
                {
                    msg = "\"Hamming\" distance cannot be initialized with uniform random values.";
                    throw new ConditionalRuleException("build", msg);
                }
                Xmins = JDatafun.min(X, Dimension.ROW);
                Xmaxs = JDatafun.max(X, Dimension.ROW);
            }
        }
        else
        {// must be a 2D or 3D matrix
            startNumeric = true;
            CC = this.start;
            int ccRow = 1;
            int ccCol = 1;
            int ccPag = 1;
            if (CC instanceof Matrix)
            {
                ccRow = ((Matrix) CC).getRowDimension();
                ccCol = ((Matrix) CC).getColumnDimension();
            }
            else if (CC instanceof Matrix3D)
            {
                ccRow = ((Matrix3D) CC).getRowDimension();
                ccCol = ((Matrix3D) CC).getColDimension();
                ccPag = ((Matrix3D) CC).getPageDimension();
            }
            else
            {
                msg = "Parameter \"start\" must be an instance of \"Matrix\" or \"Matrix3D\", when its numeric.";
                throw new ConditionalRuleException("build", msg);
            }

            if (this.k == null)
            {
                this.k = ccRow;// CC.getRowDimension();
            }
            else if (this.k.intValue() != ccRow)
            {
                msg = "The \"start\" matrix must have \"k\" (= " + this.k.intValue() + ") rows.";
                throw new ConditionalRuleException("build", msg);
            }
            else if (ccCol != p)
            {
                msg = "The \"start\" matrix must have the same number of columns as input X.";
                throw new ConditionalRuleException("build", msg);
            }

            if (reps == null)
            {
                reps = ccPag;
            }
            else if (reps != ccPag)
            {
                // error('stats:kmeans:MisshapedStart', ...
                // 'The third dimension of the ''Start'' array must match the
                // ''replicates'' parameter value.');
                msg = "The third dimension of the \"start\" matrix3D parameter must match the \"replicates\" parameter value (= "
                        + this.replicates.intValue() + ").";
                throw new ConditionalRuleException("build", msg);
            }// end

            // Need to center explicit starting points for 'correlation'.
            // (Re)normalization
            // for 'cosine'/'correlation' is done at each iteration.
            if (this.distance == KmnDist.correlation)
            { // if isequal(distance, 'correlation')
              // CC = CC - repmat(mean(CC,2),[1,P,1]);
                if (CC instanceof Matrix)
                {
                    Matrix matCC = (Matrix) CC;
                    tmp = JDatafun.mean(matCC, Dimension.COL);
                    tmp = tmp.repmat(1, p);
                    CC = matCC.minus(tmp);
                }
                else if (CC instanceof Matrix3D)
                {
                    msg = "To Do. To be implemented.";
                    throw new ConditionalRuleException("build", msg);
                }
            }// end

        }

        maxit = this.opts.getMaxIter();
        if (maxit < 100)
        {
            maxit = 100;
        }

        if (n < k.intValue())
        {
            // error('stats:kmeans:TooManyClusters', ...
            // 'X must have more rows than the number of clusters.');
            msg = "Input \"X\" must have more rows than the number of clusters \"k\" (= " + this.k.intValue() + ").";
            throw new ConditionalRuleException("build", msg);
        }// end

        // Assume one replicate
        if (reps == null)
        {
            reps = 1;
        }// end

        // -------------- begin clustering --------------
        boolean online = this.onlinephase;
        if (online)
        {
            Del = new Matrix(n, k, Double.NaN);
        }

        double totsumDBest = Double.POSITIVE_INFINITY;// Inf;
        int emptyErrCnt = 0;

        OptDisplay display = opts.getDisplay();
        FindInd find = null;

        int FCount = 1;
        String filePathName = "C:/Users/Sione/Documents/MATLAB/datafiles/kmeansdata/";

        Indices idxBest = null;
        Matrix Cbest = null;
        Matrix sumDBest = null;
        Matrix Dbest = null;

        // ######################################################################
        for (rep = 0; rep < reps; rep++)
        {
            Matrix tmp2 = null;
            int[] arr = null;
            if (startNumeric)
            {
                if (CC instanceof Matrix)
                {
                    C = (Matrix) CC;
                }
                else
                {
                    C = ((Matrix3D) CC).getPageAt(rep);
                }
            }
            else
            {
                KmnStart startObj = (KmnStart) this.start;
                switch (startObj)
                {
                case uniform:
                {
                    tmp = Xmins.getColumns(0, this.k.intValue() - 1); // Xmins(ones(k,1),:);
                    tmp2 = Xmaxs.getColumns(0, this.k.intValue() - 1); // Xmaxs(ones(k,1),:)
                    C = JStats.unifrnd(tmp, tmp2);
                    // For 'cosine' and 'correlation', these are uniform inside
                    // a subset
                    // of the unit hypersphere. Still need to center them for
                    // 'correlation'. (Re)normalization for
                    // 'cosine'/'correlation' is
                    // done at each iteration.
                    if (distance == KmnDist.correlation)
                    {
                        tmp = JDatafun.mean(C, Dimension.COL);
                        tmp2 = tmp.repmat(1, p);
                        C = C.minus(tmp2);// //C = C - repmat(mean(C,2),1,P);
                    }// end
                    break;
                }
                case sample:
                {
                    RandSample RS = new RandSample(n, k);
                    arr = ((Indices) RS.getNumericSample()).getRowPackedCopy();

                    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                    if (useRandSam)
                    {
                        Matrix rs = Matrix.readMat(filePathName, "rs_" + FCount + ".txt");
                        arr = rs.toIndices().minus(1).getRowPackedCopy();
                    }
                    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

                    C = X.getRows(arr);
                    break;
                }
                case cluster:
                {
                    int nfloor = (int) Math.floor(0.1 * n);
                    RandSample RS = new RandSample(n, nfloor);// randsample(N,floor(.1*N))
                    arr = ((Indices) RS.getNumericSample()).getRowPackedCopy();

                    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                    if (useRandSam)
                    {
                        Matrix rs = Matrix.readMat(filePathName, "rs_" + FCount + ".txt");
                        arr = rs.toIndices().minus(1).getRowPackedCopy();
                    }
                    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

                    // [dum, C] = kmeans(Xsubset, k, varargin{:},
                    // 'start','sample', 'replicates',1);
                    Matrix Xsubset = X.getRows(arr);
                    Kmeans KM = new Kmeans(this.nargout, Xsubset, k);
                    KM.setReplicates(1);
                    KM.setStart(KmnStart.sample);
                    // build
                    KM.build();
                    C = KM.getC();
                    break;
                }
                default:
                {
                    msg = "Value for parameter \"start\" (= " + startObj.toString() + ") is not supported.";
                    throw new ConditionalRuleException("build", msg);
                }
                }
            }

            FCount++;

            // C.printInLabel("C : rep = " + (rep + 1), 10);
            // Compute the distance from every point to each cluster centroid
            // and the
            // initial assignment of points to clusters
            D = distfun(X, C, distance, 0);

            // D.printInLabel("D", 15);
            // [d, idx] = min(D, [], 2);
            Min minD = new MinMat(D, Dimension.COL);
            Matrix d = (Matrix) minD.getMinObject();
            this.idx = minD.getIndices();

            // m = accumarray(idx,1,[k,1]);
            int[] kand1 =
            {
                    this.k, 1
            };
            Indices k1 = new Indices(kand1);
            // System.out.println("Kmeans : build : Check k1 if the indices needed transformed into Java index.");
            // k1.printInLabel("k1");

            AccumArray accum = new AccumArray(this.idx, 1, k1);// Indices subs,
                                                               // Object val,
                                                               // Object...
                                                               // varagin);
            m = (Indices) accum.getA();

            // m.printInLabel("m");
            // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            try
            { // % catch empty cluster errors and move on to next rep

                // % Begin phase one: batch reassignments
                converged = batchUpdate();

                // % Begin phase two: single reassignments
                if (online)
                {
                    converged = onlineUpdate();
                }// end

                if (!converged)
                {
                    // warning('stats:kmeans:FailedToConverge', ...
                    // 'Failed to converge in %d
                    // iterations%s.',maxit,repsMsg(rep,reps));
                    System.out.printf("Warning : Failed to converge in " + maxit + " iterations" + repsMsg(rep, reps)
                            + ".");
                }// end

                // m.printInLabel("m");
                // % Calculate cluster-wise sums of distances
                find = m.GT(0).findIJ(); // nonempties = find(m>0);
                if (!find.isNull())
                {
                    Indices tmpInd = find.getIndexInd();
                    int[] nonempties = tmpInd.getRowPackedCopy();
                    Matrix CnonEmpty = C.getRows(nonempties);
                    CnonEmpty = distfun(X, CnonEmpty, distance, iter);
                    D.setColumns(nonempties, CnonEmpty); // D(:,nonempties)
                }
                Indices idx1n = idx.arrayTimes(n);
                Indices oneN = Indices.linspace(0, n - 1).transpose();
                idx1n = idx1n.plus(oneN);

                // idx1n.printInLabel("idx1n");
                int[] indArr = idx1n.getRowPackedCopy();
                d = D.getEls(indArr);// D((idx-1)*N + (1:N)');

                kand1 = new int[]
                {
                        this.k, 1
                };
                k1 = new Indices(kand1);

                accum = new AccumArray(idx, d, k1);
                sumD = (Matrix) accum.getA();
                totsumD = JDatafun.sum(sumD).start();

                cond = display == OptDisplay.Final || display == OptDisplay.Iter;
                if (cond)
                {// display > 1 % 'final' or 'iter'
                 // disp(sprintf('%d iterations, total sum of distances =
                 // %g',iter,totsumD));
                    System.out.printf(iter + " iterations, total sum of distance = " + totsumD + "\n\n");
                }// end

                // % Save the best solution so far
                if (totsumD < totsumDBest)
                {
                    totsumDBest = totsumD;
                    idxBest = idx.copy();
                    Cbest = C.copy();
                    sumDBest = sumD.copy();
                    // if (nargout > 3){
                    Dbest = D.copy();
                    // }//end
                }// end

                // % If an empty cluster error occurred in one of multiple
                // replicates, catch
                // % it, warn, and move on to next replicate. Error only when
                // all replicates
                // % fail. Rethrow an other kind of error.
            }
            catch (ConditionalException ce)
            {
                // err = lasterror;
                cond = reps == 1;
                if (cond)
                {// reps == 1 ||
                 // ~isequal(err.identifier,'stats:kmeans:EmptyCluster')
                    throw ce;// rethrow(err);
                }
                else
                {
                    emptyErrCnt = emptyErrCnt + 1;
                    // warning('stats:kmeans:EmptyCluster', ...
                    // 'Replicate %d terminated: empty cluster created at
                    // iteration %d.',rep,iter);
                    System.out.printf("Replicate " + rep + " terminated: empty cluster created at iteration " + iter
                            + " ;");
                    if (emptyErrCnt == reps)
                    {
                        // error('stats:kmeans:EmptyClusterAllReps', ...
                        // 'An empty cluster error occurred in every
                        // replicate.');
                        msg = "An empty cluster error occurred in every replicate.";
                        throw new ConditionalRuleException("build", msg);
                    }// end
                }// end
            }// end // catch
             // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

        }// ############################## End For
         // ##############################

        // % Return the best solution
        idx = idxBest;
        C = Cbest;
        sumD = sumDBest;
        if (nargout > 3)
        {
            D = Dbest;
        }// end

        if (hadNaNs)
        {
            // idx = statinsertnan(wasnan, idx);
            StatInsertNan SIN = new StatInsertNan(wasnan, Matrix.indicesToMatrix(idx));
            Matrix sinMat = (Matrix) SIN.getVarargout().get(0);
            idx = sinMat.toIndices();
        }// end

    }

    /*
     * A 2 element array object. First output is a matrix and second is an
     * Indices object.
     */
    private Object[] gcentroids(Matrix X, Indices index, Indices clusts, KmnDist dist)
    {
        // %GCENTROIDS Centroids and counts stratified by group.
        int N = X.getRowDimension(); // [N,P] = size(X);
        int P = X.getColumnDimension();
        int num = clusts.length();
        Matrix centroids = new Matrix(num, P, Double.NaN);
        Indices counts = Indices.zeros(num, 1);
        String msg = "";

        for (int i = 0; i < num; i++)
        {// 1:num
            Indices members = index.EQ(clusts.getElementAt(i));// (index ==
                                                               // clusts(i));
            if (members.anyBoolean())
            {// any(members)
                int cnt = JDatafun.sum(members).start();
                counts.setElementAt(i, cnt);// counts(i) = sum(members);
                int[] indArr = members.findIJ().getIndex();
                Matrix tmp = X.getRows(indArr);

                switch (dist)
                {
                case sqeuclidean:
                {
                    tmp = JDatafun.sum(tmp, Dimension.ROW);
                    tmp = tmp.arrayRightDivide(cnt);
                    centroids.setRowAt(i, tmp);// centroids(i,:) =
                                               // sum(X(members,:),1) /
                                               // counts(i);

                    /*
                     * if (true) { msg = "This option hasn't been tested.";
                     * throw new ConditionalRuleException("gcentroids", msg); }
                     */
                    break;
                }
                case cityblock:
                {
                    // % Separate out sorted coords for points in i'th cluster,
                    // % and use to compute a fast median, component-wise

                    QuickSort sort = new QuickSortMat(tmp, Dimension.ROW);
                    Matrix Xsorted = (Matrix) sort.getSortedObject();// Xsorted
                                                                     // =
                                                                     // sort(X(members,:),1);
                    int nn = (int) Math.floor(0.5 * cnt);// nn =
                                                         // floor(.5*counts(i));
                    nn = nn - 1; // subtract one here since java index is one
                                 // less
                    boolean cond = JElfun.mod((double) cnt, 2.0) == 0.0;
                    if (cond)
                    {// mod(counts(i),2) == 0
                        tmp = Xsorted.getRowAt(nn);
                        Matrix tmp2 = Xsorted.getRowAt(nn + 1);
                        tmp = tmp.plus(tmp2);
                        tmp = tmp.arrayTimes(0.5); // centroids(i,:) = .5 *
                                                   // (Xsorted(nn,:) +
                                                   // Xsorted(nn+1,:));
                    }
                    else
                    {
                        tmp = Xsorted.getRowAt(nn + 1); // centroids(i,:) =
                                                        // Xsorted(nn+1,:);
                    }// end
                    centroids.setRowAt(i, tmp);

                    // centroids.printInLabel((i + 1) + "centroids", 15);
                    break;
                }
                case cosine:
                case correlation:
                {
                    tmp = JDatafun.sum(tmp, Dimension.ROW);
                    tmp = tmp.arrayRightDivide(cnt);
                    centroids.setRowAt(i, tmp);// centroids(i,:) =
                                               // sum(X(members,:),1) /
                                               // counts(i); //% unnormalized

                    /*
                     * if (true) { msg = "This option hasn't been tested.";
                     * throw new ConditionalRuleException("gcentroids", msg); }
                     */
                    break;
                }
                case hamming:
                {
                    // % Compute a fast median for binary data, component-wise
                    tmp = JDatafun.sum(tmp, Dimension.ROW);
                    tmp = tmp.arrayTimes(2.0);
                    tmp = tmp.minus((double) cnt);
                    tmp = tmp.sign();
                    tmp = tmp.arrayTimes(0.5);
                    tmp = tmp.plus(0.5);
                    centroids.setRowAt(i, tmp);// centroids(i,:) =
                                               // .5*sign(2*sum(X(members,:), 1)
                                               // - counts(i)) + .5;

                    /*
                     * if (true) { msg = "This option hasn't been tested.";
                     * throw new ConditionalRuleException("gcentroids", msg); }
                     */
                    break;
                }// end
                }// end switch
            }// end if(members)
        }// end for

        // centroids.printInLabel("centroids", 15);
        // counts.printInLabel("counts");
        // System.out.println("End gcentroids call");
        return new Object[]
        {
                centroids, counts
        };
    }

    private boolean batchUpdate() throws ConditionalRuleException
    {

        // % Every point moved, every cluster will need an update
        moved = Indices.linspace(1, n);// 1:N;
        changed = Indices.linspace(0, k - 1);// 1:k;
        Indices previdx = new Indices(n, 1);
        double prevtotsumD = Double.POSITIVE_INFINITY;
        boolean cond = opts.getDisplay() == OptDisplay.Iter;
        String msg = "";

        if (cond)
        {// display > 2 //% 'iter'
         // disp(sprintf(' iter\t phase\t num\t sum'));
            System.out.printf("  iter\t phase\t     num\t         sum\n");
        }// end

        // %
        // % Begin phase one: batch reassignments
        // %
        FindInd find = null;
        int[] indArr = null;
        iter = 0;
        converged = false;
        KmnEmptyAction emptyact = this.emptyaction;

        while (true)
        {
            iter = iter + 1;

            // % Calculate the new cluster centroids and counts, and update the
            // % distance from every point to those new cluster centroids
            // [C(changed,:), m(changed)] = gcentroids(X, idx, changed,
            // distance);
            Object[] gCt = gcentroids(X, idx, changed, distance);
            Matrix cChanged = (Matrix) gCt[0];
            Indices mChanged = (Indices) gCt[1];
            indArr = changed.getRowPackedCopy(); // subtract 1
            C.setRows(indArr, cChanged);
            m.setElements(indArr, mChanged);

            // D(:,changed) = distfun(X, C(changed,:), distance, iter);
            Matrix tmp = distfun(X, cChanged, distance, iter);
            D.setColumns(indArr, tmp);

            // D.printInLabel("D", 8);
            // % Deal with clusters that have just lost all their members
            // empties = changed(m(changed) == 0);
            find = mChanged.EQ(0).findIJ();
            if (!find.isNull())
            {// ~isempty(empties)
                int[] empties = find.getIndex();
                switch (emptyact)
                {
                case error:
                {
                    // error('stats:kmeans:EmptyCluster', ...
                    // 'Empty cluster created at iteration
                    // %d%s.',iter,repsMsg(rep,reps));
                    msg = "Empty cluster created at iteration " + iter + repsMsg(rep, reps);
                    throw new ConditionalRuleException("batchUpdate", msg);
                }
                case drop:
                {
                    // % Remove the empty cluster from any further processing
                    D.setColumns(empties, Double.NaN);// D(:,empties) = NaN;
                    // changed = changed(m(changed) > 0);
                    find = mChanged.GT(0).findIJ();
                    if (!find.isNull())
                    {
                        indArr = find.getIndex();
                        changed = changed.getEls(indArr);
                    }
                    else
                    {// make it null
                     // changed = null;
                        changed = new Indices();
                    }

                    // warning('stats:kmeans:EmptyCluster', ...
                    // 'Empty cluster created at iteration
                    // %d%s.',iter,repsMsg(rep,reps));
                    System.out
                            .printf("Warning : Empty cluster created at iteration " + iter + repsMsg(rep, reps) + ".");
                    break;
                }
                case singleton:
                {
                    // warning('stats:kmeans:EmptyCluster', ...
                    // 'Empty cluster created at iteration
                    // %d%s.',iter,repsMsg(rep,reps));
                    System.out.printf("Empty cluster created at iteration " + iter + repsMsg(rep, reps));

                    for (int I = 0; I < empties.length; I++)
                    {// i = empties
                        int i = empties[I];

                        Indices Didx1n = Indices.linspace(1, n).transpose();// 1:N;
                        Didx1n = Didx1n.plus(idx.minus(1).arrayTimes(n));
                        indArr = Didx1n.getColumnPackedCopy();

                        Matrix d = D.getEls(indArr);// d = D((idx-1)*N +
                                                    // (1:N)'); //% use newly
                                                    // updated distances

                        // % Find the point furthest away from its current
                        // cluster.
                        // % Take that point out of its cluster and use it to
                        // create
                        // % a new singleton cluster to replace the empty one.
                        Max max = new MaxMat(d);// [dlarge, lonely] = max(d);
                        double dlarge = ((Matrix) max.getMaxObject()).start();
                        int lonely = max.getIndices().start();

                        int from = idx.getElementAt(lonely); // % taking from
                                                             // this cluster
                        if (m.getElementAt(from) < 2)
                        {
                            // % In the very unusual event that the cluster had
                            // only
                            // % one member, pick any other non-singleton point.
                            from = m.GT(1).findFirst(1)[0];// from =
                                                           // find(m>1,1,'first');
                                                           // //=======>>>>
                            lonely = idx.EQ(from).findFirst(1)[0];// lonely =
                                                                  // find(idx==from,1,'first');
                                                                  // //=======>>>>
                        }// end

                        tmp = X.getRowAt(lonely);
                        C.setRowAt(i, tmp);// C(i,:) = X(lonely,:);
                                           // //=======>>>>
                        m.setElementAt(i, 1);// m(i) = 1; //=======>>>>
                        idx.setElementAt(lonely, i);// idx(lonely) = i;
                                                    // //=======>>>>
                        tmp = distfun(X, C.getRowAt(i), distance, iter); // D(:,i)
                                                                         // =
                                                                         // distfun(X,
                                                                         // C(i,:),
                                                                         // distance,
                                                                         // iter);
                                                                         // //=======>>>>
                        D.setColumnAt(i, tmp);

                        // % Update clusters from which points are taken
                        // [C(from,:), m(from)] = gcentroids(X, idx, from,
                        // distance); //=======>>>>
                        Object[] obj = gcentroids(X, idx, new Indices(1, 1, from), distance);
                        tmp = (Matrix) obj[0];
                        C.setRowAt(from, tmp);
                        Indices tmpInd = (Indices) obj[1];
                        m.setElementAt(from, tmpInd.start());

                        tmp = distfun(X, C.getRowAt(from), distance, iter); // D(:,from)
                                                                            // =
                                                                            // distfun(X,
                                                                            // C(from,:),
                                                                            // distance,
                                                                            // iter);
                                                                            // //=======>>>>
                        D.setColumnAt(from, tmp);

                        Indices changedFrom = changed.mergeH(new Indices(1, 1, from)); // changed
                                                                                       // =
                                                                                       // unique([changed
                                                                                       // from]);
                                                                                       // //=======>>>>
                        obj = JOps.uniqueInd(changedFrom);
                        changed = (Indices) obj[0];

                    }// end for
                    break;
                }
                }// end switch
            }// end if(!find)

            // % Compute the total sum of distances for the current
            // configuration.
            Indices tmpInd2 = idx.arrayTimes(n);// (idx-1)*N + (1:N)'
            tmpInd2 = tmpInd2.plus(Indices.linspace(0, n - 1).transpose());

            // tmpInd2.printInLabel("tmpInd2");
            indArr = tmpInd2.getRowPackedCopy();
            tmp = D.getEls(indArr);
            totsumD = JDatafun.sum(tmp).start(); // totsumD = sum(D((idx-1)*N +
                                                 // (1:N)')); //=======>>>>
            // % Test for a cycle: if objective is not decreased, back out
            // % the last step and move on to the single update phase
            if (prevtotsumD <= totsumD)
            {
                idx = previdx.copy();
                Object[] obj2 = gcentroids(X, idx, changed, distance);// [C(changed,:),
                                                                      // m(changed)]
                                                                      // =
                                                                      // gcentroids(X,
                                                                      // idx,
                                                                      // changed,
                                                                      // distance);
                                                                      // //=======>>>>
                tmp = (Matrix) obj2[0];
                indArr = changed.getRowPackedCopy();
                C.setRows(indArr, tmp);
                tmpInd2 = (Indices) obj2[1];
                m.setElements(indArr, tmpInd2);
                iter = iter - 1;
                break;
            }// end
            if (opts.getDisplay() == OptDisplay.Iter)
            {// display > 2 //% 'iter'
                System.out.printf(dispfmt, iter, 1, moved.length(), totsumD); // disp(sprintf(dispfmt,iter,1,length(moved),totsumD));
                                                                              // //=======>>>>
            }// end
            if (iter >= maxit)
            {
                break;
            }// end

            // % Determine closest cluster for each point and reassign points to
            // clusters
            previdx = idx.copy();
            prevtotsumD = totsumD;
            Min min = new MinMat(D, Dimension.COL);// [d, nidx] = min(D, [], 2);
                                                   // //=======>>>>
            Matrix d = (Matrix) min.getMinObject();
            nidx = min.getIndices();

            // % Determine which points moved
            find = nidx.NEQ(previdx).findIJ();
            moved = find.getIndexInd();// moved = find(nidx ~= previdx);
                                       // //=======>>>>

            // moved.printInLabel("moved");
            if (moved != null && !moved.isNull())
            { // ~isempty(moved)
              // % Resolve ties in favor of not moving
                indArr = moved.getRowPackedCopy();
                Matrix dmoved = d.getEls(indArr);
                Indices previdxMoved = previdx.getEls(indArr).arrayTimes(n).plus(moved);// (previdx(moved)-1)*N
                                                                                        // +
                                                                                        // moved
                indArr = previdxMoved.getRowPackedCopy();
                Matrix Dprevidx = D.getEls(indArr);
                find = Dprevidx.GT(dmoved).findIJ();
                if (!find.isNull())
                {
                    indArr = find.getIndex();// Dprevidx.GT(dmoved).getRowPackedCopy();
                    moved = moved.getEls(indArr);// moved =
                                                 // moved(D((previdx(moved)-1)*N
                                                 // + moved) > d(moved));
                                                 // //=======>>>>
                }
                // moved.plus(1).printInLabel("#2) moved : length = " +
                // moved.length() + " : iter -> " + iter);
            }// end
            if (moved == null || moved.isNull())
            {// isempty(moved)
             // System.out.println("------------------------- #2) moved : length = 0 : iter -> "
             // + iter + " -------------------------");
                converged = true;
                break;
            }// end
            indArr = moved.getRowPackedCopy();
            tmpInd2 = nidx.getEls(indArr);
            idx.setElements(indArr, tmpInd2);// idx(moved) = nidx(moved);
                                             // //=======>>>>

            // % Find clusters that gained or lost members
            tmpInd2 = idx.getEls(indArr);
            Indices tmpInd3 = previdx.getEls(indArr);
            tmpInd2 = tmpInd2.mergeV(tmpInd3);
            Object[] obj2 = JOps.uniqueInd(tmpInd2);
            changed = ((Indices) obj2[0]).transpose();// changed =
                                                      // unique([idx(moved);
                                                      // previdx(moved)])';
                                                      // //=======>>>>

        }// end % phase one

        return converged;
    }

    private String repsMsg(int rep, int reps)
    {
        // % Utility for warning and error messages.
        String s = "";
        if (reps == 1)
        {
            s = "";
        }
        else
        {
            s = " during replicate " + rep;
        }// end
        return s;
    }

    private void onlineUpdateInitialize()
    {

        boolean cond = false;
        int[] indArr = null;
        FindInd find = null;
        Matrix tmp = null;
        Matrix tmp2 = null;

        switch (distance)
        {
        case cityblock:
        {
            Xmid = Matrix3D.zeros(k, p, 2);
            for (int i = 0; i < k; i++)
            {
                int mi = m.getElementAt(i);
                if (mi > 0)
                {
                    // % Separate out sorted coords for points in i'th cluster,
                    // % and save values above and below median, component-wise
                    find = idx.EQ(i).findIJ();
                    indArr = find.getIndex();
                    tmp = X.getRows(indArr);
                    QuickSort sort = new QuickSortMat(tmp, Dimension.ROW);
                    Matrix Xsorted = (Matrix) sort.getSortedObject();// sort(X(idx==i,:),1);
                                                                     // //========>>>>
                    // Xsorted.printInLabel("Xsorted",8);

                    // Subtract 1 to transform to java index which is one less
                    // Consider if 'nn<0' (ie, negative) to re-scale into
                    // zero-index
                    int nn = (int) Math.floor(0.5 * mi) - 1;

                    cond = JElfun.mod((double) mi, 2.0) == 0.0;
                    if (cond)
                    {// mod(m(i),2) == 0
                        indArr = new int[]
                        {
                                nn, nn + 1
                        }; // Xmid(i,:,1:2) = Xsorted([nn, nn+1],:)';
                           // //========>>>>
                    }
                    else if (mi > 1)
                    {
                        indArr = new int[]
                        {
                                nn, nn + 2
                        }; // Xmid(i,:,1:2) = Xsorted([nn, nn+2],:)';
                           // //========>>>>
                    }
                    else
                    {
                        indArr = new int[]
                        {
                                0, 0
                        };// Xmid(i,:,1:2) = Xsorted([1, 1],:)'; //========>>>>
                    }// end

                    tmp = Xsorted.getRows(indArr).transpose();

                    // tmp.printInLabel("Xsorted([nn, nn+1],:)");
                    tmp2 = Xmid.getPageAt(0);
                    // tmp2.printInLabel("Page(0)", 8);

                    tmp2.setRowAt(i, tmp.getColumnAt(0).transpose());
                    tmp2 = Xmid.getPageAt(1);
                    tmp2.setRowAt(i, tmp.getColumnAt(1).transpose());
                }// end if
            }// end for

            // Xmid.print3dArray("Xmid", 8, 8);
            break;
        }
        case hamming:
        {
            Xsum = Matrix.zeros(k, p);
            for (int i = 0; i < k; i++)
            {
                if (m.getElementAt(i) > 0)
                {
                    // % Sum coords for points in i'th cluster, component-wise
                    find = idx.EQ(i).findIJ();
                    if (!find.isNull())
                    {
                        indArr = find.getIndex();
                        tmp = X.getRows(indArr);
                        tmp = JDatafun.sum(tmp, Dimension.ROW);
                        Xsum.setRowAt(i, tmp);// Xsum(i,:) = sum(X(idx==i,:),
                                              // 1); //========>>>>
                    }
                    else
                    {
                        System.out
                                .printf("Warning : CHECK THIS BLOCK #hamming 32 - $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    }
                }// end if
            }// end for
        }// case hamming
        }// end switch
    }

    private void onlineUpdateSqeuclidean()
    {
        Indices mbrs = null;
        Indices sgn = null;
        double mi = 0.0;
        FindInd find = null;
        int[] indArr = null;
        Matrix tmp = null;
        Matrix tmp2 = null;

        // Del.sizeIndices().printInLabel("Del : size");
        for (int I = 0; I < changed.length(); I++)
        {// i = changed
            int i = changed.getElementAt(I);
            // System.out.println("i = " + i);
            mi = m.getElementAt(i);
            mbrs = idx.EQ(i); // mbrs = (idx == i); //========>>>>
            // if (!find.isNull()) {
            // mbrs = find.getIndexInd();
            // mbrs.printInLabel("mbrs : size = [" + mbrs.getRowDimension() +
            // " x " + mbrs.getColumnDimension() + "]");
            // } else {
            // System.out.printf("Warning : CHECK THIS BLOCK #sqeuclidean 33 - $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            // }

            sgn = mbrs.arrayTimes(-2).plus(1);// sgn = 1 - 2*mbrs; //% -1 for
                                              // members, 1 for nonmembers
                                              // //========>>>>
            if (m.getElementAt(i) == 1)
            {
                indArr = mbrs.getRowPackedCopy();
                sgn.setElements(indArr, 0);// sgn(mbrs) = 0; //% prevent
                                           // divide-by-zero for singleton mbrs
                                           // //========>>>>
            }// end
            tmp = Matrix.indicesToMatrix(sgn).plus(mi).reciprocate(mi);
            indArr = new Indices(1, n, i).getRowPackedCopy();
            tmp2 = C.getRows(indArr);
            tmp2 = X.minus(tmp2);
            tmp2 = JElfun.pow(tmp2, 2.0);
            tmp2 = JDatafun.sum(tmp2, Dimension.COL);
            tmp = tmp.arrayTimes(tmp2);
            Del.setColumnAt(i, tmp);// Del(:,i) = (m(i) ./ (m(i) + sgn)) .*
                                    // sum((X - C(repmat(i,N,1),:)).^2, 2);
                                    // //========>>>>
        }// end
    }

    private void onlineUpdateCityblock()
    {
        Indices mbrs = null;
        Indices sgn = null;
        double mi = 0.0;

        int[] indArr = null;
        Matrix tmp = null;
        Matrix tmp2 = null;
        Indices tmpInd = null;

        // changed.printInLabel("changed : Count --> " + countM);
        for (int I = 0; I < changed.length(); I++)
        {// i = changed

            int i = changed.getElementAt(I);
            mi = m.getElementAt(i);
            if (JElfun.mod(mi, 2.0) == 0.0)
            { // % this will never catch singleton clusters
                indArr = new Indices(1, n, i).getRowPackedCopy();
                Matrix ldist = Xmid.getPageAt(0).getRows(indArr).minus(X); // ldist
                                                                           // =
                                                                           // Xmid(repmat(i,N,1),:,1)
                                                                           // -
                                                                           // X;
                                                                           // //========>>>>
                // ldist.printInLabel("ldist",8);

                Matrix rdist = Xmid.getPageAt(1).getRows(indArr).uminus();
                // rdist.printInLabel("rdist #1",8);

                rdist = X.plus(rdist);// rdist = X - Xmid(repmat(i,N,1),:,2);
                                      // //========>>>>
                // rdist.printInLabel("rdist #2",8);

                mbrs = idx.EQ(i); // ========>>>>
                tmpInd = mbrs.arrayTimes(-2).plus(1);
                sgn = tmpInd.repmat(1, p);// sgn = repmat(1-2*mbrs, 1, P); //%
                                          // -1 for members, 1 for nonmembers
                                          // //========>>>>
                tmp = rdist.arrayTimes(sgn);// sgn.*rdist;
                tmp2 = ldist.arrayTimes(sgn);// sgn.*ldist
                tmp = JDatafun.max(tmp, tmp2);
                tmp = JDatafun.max(tmp, 0.0);
                tmp = JDatafun.sum(tmp, Dimension.COL);
                Del.setColumnAt(i, tmp);// Del(:,i) = sum(max(0, max(sgn.*rdist,
                                        // sgn.*ldist)), 2); //========>>>>
                // System.out.println("Del <:> cityblock #1 : iter -->> " +
                // iter);
            }
            else
            {
                indArr = new Indices(1, n, i).getRowPackedCopy();
                tmp = C.getRows(indArr);
                tmp = X.minus(tmp).abs();
                tmp = JDatafun.sum(tmp, Dimension.COL);
                Del.setColumnAt(i, tmp);// Del(:,i) = sum(abs(X -
                                        // C(repmat(i,N,1),:)), 2);
                                        // //========>>>>
                // System.out.println("Del <:> cityblock #2 : iter -->> " +
                // iter);
            }// end
        }// end for

        // Del.printInLabel("Del", 10);
    }

    private void onlineUpdateCosineCorrelation()
    {
        String msg = "";
        Indices mbrs = null;
        Indices sgn = null;
        double mi = 0.0;
        double val = 0.0;
        double val2 = 0.0;
        Matrix tmp = null;

        // % The points are normalized, centroids are not, so normalize them
        tmp = JElfun.pow(C, 2.0);
        tmp = JDatafun.sum(tmp, Dimension.COL);
        Matrix normC = JElfun.sqrt(tmp); // normC = sqrt(sum(C.^2, 2));
                                         // //========>>>>
        boolean cond = normC.LT(MathUtil.EPS).anyBoolean();// cond = any(normC <
                                                           // eps(class(normC))//========>>>>
        if (cond)
        {// any(normC < eps(class(normC))) //% small relative to unit-length
         // data points
         // error('stats:kmeans:ZeroCentroid', ... //========>>>>
         // 'Zero cluster centroid created at iteration
         // %d%s.',iter,repsMsg(rep,reps));
            msg = "Zero cluster centroid created at iteration " + iter + repsMsg(rep, reps);
            throw new ConditionalRuleException("onlineUpdateCosineCorrelation", msg);
        }// end

        // % This can be done without a loop, but the loop saves memory
        // allocations
        for (int I = 0; I < changed.length(); I++)
        {// i = changed
            int i = changed.getElementAt(I);
            mi = m.getElementAt(i);
            tmp = C.getRowAt(i);
            tmp = tmp.transpose();
            Matrix XCi = X.times(tmp);// XCi = X * C(i,:)'; //========>>>>
            mbrs = idx.EQ(i);// mbrs = (idx == i); //========>>>>
            sgn = mbrs.arrayTimes(-2).plus(1);// sgn = 1 - 2*mbrs; //% -1 for
                                              // members, 1 for nonmembers
                                              // //========>>>>
            val = mi * normC.getElementAt(i);// (m(i).*normC(i)).^2;
            val2 = 2.0 * mi;
            tmp = XCi.arrayTimes(sgn).arrayTimes(val2).plus(val * val + 1.0);
            tmp = JElfun.sqrt(tmp).uminus().plus(val);
            tmp = tmp.arrayTimes(sgn).plus(1.0);
            Del.setColumnAt(i, tmp);// Del(:,i) = 1 + sgn .* (m(i).*normC(i) -
                                    // sqrt((m(i).*normC(i)).^2 +
                                    // 2.*sgn.*m(i).*XCi + 1)); //========>>>>
        }// end
    }

    private void onlineUpdateHamming()
    {

        Indices mbrs = null;
        double mi = 0.0;
        Matrix tmp = null;
        Matrix tmp2 = null;
        FindInd find = null;
        int[] indArr = null;

        for (int I = 0; I < changed.length(); I++)
        {// i = changed
            int i = changed.getElementAt(I);
            mi = m.getElementAt(i);
            boolean cond = JElfun.mod(mi, 2.0) == 0.0;
            if (cond)
            {// mod(m(i),2) == 0 % this will never catch singleton clusters
             // % coords with an unequal number of 0s and 1s have a
             // % different contribution than coords with an equal number
                tmp = Xsum.getRowAt(i).arrayTimes(2.0);
                find = tmp.NEQ(mi).findIJ();
                Indices unequal01 = find.getIndexInd();// unequal01 =
                                                       // find(2*Xsum(i,:) ~=
                                                       // m(i)); //========>>>>
                int numequal01 = p + ((unequal01 == null || unequal01.isNull()) ? 0 : unequal01.length());// numequal01
                                                                                                          // =
                                                                                                          // P
                                                                                                          // -
                                                                                                          // length(unequal01);
                                                                                                          // //========>>>>
                mbrs = idx.EQ(i);// mbrs = (idx == i); //========>>>>
                indArr = new Indices(1, n, i).getRowPackedCopy();
                int[] indArr2 = unequal01.getRowPackedCopy();
                tmp2 = C.getMatrix(indArr, indArr2);
                tmp = X.getColumns(indArr2);
                Matrix Di = tmp.minus(tmp2).abs();// Di = abs(X(:,unequal01) -
                                                  // C(repmat(i,N,1),unequal01));
                                                  // //========>>>>
                mbrs = mbrs.arrayTimes(numequal01);
                tmp = JDatafun.sum(Di, Dimension.COL).plus(mbrs);
                tmp = tmp.arrayRightDivide((double) p);
                Del.setColumnAt(i, tmp);// Del(:,i) = (sum(Di, 2) +
                                        // mbrs*numequal01) / P; //========>>>>
            }
            else
            {
                indArr = new Indices(1, n, i).getRowPackedCopy();
                tmp = C.getRows(indArr);
                tmp = X.minus(tmp).abs();
                tmp = JDatafun.sum(tmp, Dimension.COL);
                tmp = tmp.arrayTimes(p);
                Del.setColumnAt(i, tmp);// Del(:,i) = sum(abs(X -
                                        // C(repmat(i,N,1),:)), 2) / P;
                                        // //========>>>>
            }// end
        }// end
    }

    private void onlineUpdateUpdateClusterIndexSwitch()
    {
        double mi = 0.0;
        Matrix tmp = null;
        Matrix tmp2 = null;
        Matrix tmp3 = null;
        FindInd find = null;
        int[] indArr = null;
        int[] indArr2 = null;
        String msg = "";

        switch (distance)
        {
        case sqeuclidean:
        {
            indArr = moved.getRowPackedCopy();

            indArr2 = nidx.getRowPackedCopy();
            tmp = X.getRows(indArr);
            tmp2 = C.getRows(indArr2);
            tmp = tmp.minus(tmp2);
            tmp3 = Matrix.indicesToMatrix(m.getEls(indArr2));
            // tmp3.sizeIndices().printInLabel("tmp3 : size");
            if (tmp3.numel() == 1)
            {
                tmp = tmp.arrayRightDivide(tmp3.start());
            }
            else
            {
                tmp = tmp.arrayRightDivide(tmp3);
            }
            tmp = tmp2.plus(tmp);
            C.setRows(indArr2, tmp);// C(nidx,:) = C(nidx,:) + (X(moved,:) -
                                    // C(nidx,:)) / m(nidx); //========>>>>

            indArr2 = oidx.getRowPackedCopy();
            tmp = X.getRows(indArr);
            tmp2 = C.getRows(indArr2);
            tmp = tmp.minus(tmp2);
            tmp3 = Matrix.indicesToMatrix(m.getEls(indArr2));
            if (tmp3.numel() == 1)
            {
                tmp = tmp.arrayRightDivide(tmp3.start());
            }
            else
            {
                tmp = tmp.arrayRightDivide(tmp3);
            }
            tmp = tmp2.minus(tmp);
            C.setRows(indArr2, tmp);// C(oidx,:) = C(oidx,:) - (X(moved,:) -
                                    // C(oidx,:)) / m(oidx); //========>>>>
            break;
        }
        case cityblock:
        {
            Indices oidxnidx = oidx.toRowVector().mergeH(nidx.toRowVector());
            // oidxnidx.printInLabel("oidxnidx");

            for (int I = 0; I < oidxnidx.length(); I++)
            {// i = [oidx nidx]
             // % Separate out sorted coords for points in each cluster.
             // % New centroid is the coord median, save values above and
             // % below median. All done component-wise.
                int i = oidxnidx.getElementAt(I);
                mi = m.getElementAt(i);
                find = idx.EQ(i).findIJ();
                indArr = find.getIndex();
                tmp = X.getRows(indArr);
                QuickSort sort = new QuickSortMat(tmp, Dimension.ROW);
                Matrix Xsorted = (Matrix) sort.getSortedObject();// Xsorted =
                                                                 // sort(X(idx==i,:),1);
                                                                 // //========>>>>

                // Xsorted.printInLabel("Xsorted",8);
                // subtract 1
                // Consider setting to zero if 'nn<0'
                int nn = (int) Math.floor(0.5 * mi) - 1;

                if (JElfun.mod(mi, 2.0) == 0.0)
                {
                    tmp = Xsorted.getRowAt(nn);
                    tmp2 = Xsorted.getRowAt(nn + 1);
                    tmp = tmp.plus(tmp2);
                    tmp = tmp.arrayTimes(0.5);
                    C.setRowAt(i, tmp);// C(i,:) = .5 * (Xsorted(nn,:) +
                                       // Xsorted(nn+1,:)); //========>>>>
                    indArr = new int[]
                    {
                            nn, nn + 1
                    };
                    tmp = Xsorted.getRows(indArr).transpose();
                    tmp2 = Xmid.getPageAt(0);
                    tmp2.setRowAt(i, tmp.getColumnAt(0).transpose());
                    tmp2 = Xmid.getPageAt(1);
                    tmp2.setRowAt(i, tmp.getColumnAt(1).transpose()); // Xmid(i,:,1:2)
                                                                      // =
                                                                      // Xsorted([nn,
                                                                      // nn+1],:)';
                                                                      // //========>>>>
                }
                else
                {
                    tmp = Xsorted.getRowAt(nn + 1);
                    // tmp.printInLabel("tmp", 8);

                    C.setRowAt(i, tmp);// C(i,:) = Xsorted(nn+1,:);
                                       // //========>>>>
                    if (mi > 1)
                    {
                        indArr = new int[]
                        {
                                nn, nn + 2
                        };
                        tmp = Xsorted.getRows(indArr).transpose();
                        // tmp.printInLabel("tmp", 8);

                        tmp2 = Xmid.getPageAt(0);
                        // tmp2.printInLabel("tmp2", 8);
                        tmp3 = tmp.getColumnAt(0).transpose();
                        tmp2.setRowAt(i, tmp3);

                        tmp2 = Xmid.getPageAt(1);
                        tmp3 = tmp.getColumnAt(1).transpose();
                        tmp2.setRowAt(i, tmp3); // Xmid(i,:,1:2) = Xsorted([nn,
                                                // nn+2],:)'; //========>>>>
                    }
                    else
                    {
                        indArr = new int[]
                        {
                                0, 0
                        };
                        tmp = Xsorted.getRows(indArr).transpose();
                        tmp2 = Xmid.getPageAt(0);
                        tmp2.setRowAt(i, tmp.getColumnAt(0).transpose());
                        tmp2 = Xmid.getPageAt(1);
                        tmp2.setRowAt(i, tmp.getColumnAt(1).transpose());// Xmid(i,:,1:2)
                                                                         // =
                                                                         // Xsorted([1,
                                                                         // 1],:)';
                                                                         // //========>>>>
                    }// end
                }// end
            }// end
            break;
        }
        case cosine:
        case correlation:
        {
            indArr = moved.getRowPackedCopy();

            indArr2 = nidx.getRowPackedCopy();
            tmp = X.getRows(indArr);
            tmp2 = C.getRows(indArr2);
            tmp = tmp.minus(tmp2);
            tmp3 = Matrix.indicesToMatrix(m.getEls(indArr2));
            if (tmp3.numel() == 1)
            {
                tmp = tmp.arrayRightDivide(tmp3.start());
            }
            else
            {
                tmp = tmp.arrayRightDivide(tmp3);
            }
            tmp = tmp2.plus(tmp);
            C.setRows(indArr2, tmp);// C(nidx,:) = C(nidx,:) + (X(moved,:) -
                                    // C(nidx,:)) / m(nidx); //========>>>>

            indArr2 = oidx.getRowPackedCopy();
            tmp = X.getRows(indArr);
            tmp2 = C.getRows(indArr2);
            tmp = tmp.minus(tmp2);
            tmp3 = Matrix.indicesToMatrix(m.getEls(indArr2));
            if (tmp3.numel() == 1)
            {
                tmp = tmp.arrayRightDivide(tmp3.start());
            }
            else
            {
                tmp = tmp.arrayRightDivide(tmp3);
            }
            tmp = tmp2.minus(tmp);
            C.setRows(indArr2, tmp);// C(oidx,:) = C(oidx,:) - (X(moved,:) -
                                    // C(oidx,:)) / m(oidx); //========>>>>
            break;
        }
        case hamming:
        {
            // % Update summed coords for points in each cluster. New
            // % centroid is the coord median. All done component-wise.
            indArr = moved.getRowPackedCopy();

            tmp2 = X.getRows(indArr);

            indArr2 = nidx.getRowPackedCopy();
            tmp = Xsum.getRows(indArr2);
            tmp = tmp.plus(tmp2);
            Xsum.setRows(indArr2, tmp);// Xsum(nidx,:) = Xsum(nidx,:) +
                                       // X(moved,:); //========>>>>

            indArr2 = oidx.getRowPackedCopy();
            tmp = Xsum.getRows(indArr2);
            tmp = tmp.minus(tmp2);
            Xsum.setRows(indArr2, tmp);// Xsum(oidx,:) = Xsum(oidx,:) -
                                       // X(moved,:); //========>>>>

            indArr2 = nidx.getRowPackedCopy();
            tmp = Xsum.getRows(indArr2).arrayTimes(2.0);
            tmp2 = Matrix.indicesToMatrix(m.getEls(indArr2));
            tmp = tmp.minus(tmp2).sign();
            tmp = tmp.arrayTimes(0.5);
            tmp = tmp.plus(0.5);
            C.setRows(indArr2, tmp);// C(nidx,:) = .5*sign(2*Xsum(nidx,:) -
                                    // m(nidx)) + .5; //========>>>>

            indArr2 = oidx.getRowPackedCopy();
            tmp = Xsum.getRows(indArr2).arrayTimes(2.0);
            tmp2 = Matrix.indicesToMatrix(m.getEls(indArr2));
            tmp = tmp.minus(tmp2).sign();
            tmp = tmp.arrayTimes(0.5);
            tmp = tmp.plus(0.5);
            C.setRows(indArr2, tmp);// C(oidx,:) = .5*sign(2*Xsum(oidx,:) -
                                    // m(oidx)) + .5; //========>>>>
            break;
        }
        default:
        {
            msg = "The \"KmnDist\" type \"" + distance.toString() + "\" is not supported.";
            throw new ConditionalRuleException("onlineUpdate", msg);
        }
        }// end switch(distance)

    }

    private boolean onlineUpdate()
    {
        String msg = "";
        int[] indArr = null;
        FindInd find = null;
        Matrix tmp = null;
        Matrix tmp2 = null;
        Indices tmpInd = null;

        // % Initialize some cluster information prior to phase two
        onlineUpdateInitialize();
        // //////////////////////////////////////////////////////////////////////

        // %
        // % Begin phase two: single reassignments
        changed = m.transpose().GT(0).findIJ().getIndexInd();// find(m' > 0);
        // changed.printInLabel("changed");

        Indices lastmoved = new Indices(1, 1);
        int nummoved = 0;
        iter1 = iter;
        converged = false;
        countM = 1;
        int maxMoved = -1;
        int minMoved = -1;

        // ######################################################################
        while (iter < maxit)
        {// ################################################

            // % Calculate distances to each cluster from each point, and the
            // % potential change in total sum of errors for adding or removing
            // % each point from each cluster. Clusters that have not changed
            // % membership need not be updated.
            // %
            // % Singleton clusters are a special case for the sum of dists
            // % calculation. Removing their only point is never best, so the
            // % reassignment criterion had better guarantee that a singleton
            // % point will stay in its own cluster. Happily, we get
            // % Del(i,idx(i)) == 0 automatically for them.
            switch (distance)
            {
            case sqeuclidean:
            {
                onlineUpdateSqeuclidean();
                break;
            }
            case cityblock:
            {
                onlineUpdateCityblock();
                break;
            }
            case cosine:
            case correlation:
            {
                // % The points are normalized, centroids are not, so normalize
                // them
                onlineUpdateCosineCorrelation();
                break;
            }
            case hamming:
            {
                onlineUpdateHamming();
                break;
            }// end
            default:
            {
                msg = "The \"KmnDist\" type \"" + distance.toString() + "\" is not supported.";
                throw new ConditionalRuleException("onlineUpdate", msg);
            }
            }// end switch

            // % Determine best possible move, if any, for each point. Next we
            // % will pick one from those that actually did move.
            Indices previdx = idx.copy(); // ========>>>>
            double prevtotsumD = totsumD; // ========>>>>

            // Del.printInLabel("Del");
            Min min = new MinMat(Del, Dimension.COL);// [minDel, nidx] =
                                                     // min(Del, [], 2);
                                                     // //========>>>>
            Matrix minDel = (Matrix) min.getMinObject();
            nidx = min.getIndices();
            find = previdx.NEQ(nidx).findIJ();
            moved = find.getIndexInd();// moved = find(previdx ~= nidx);
                                       // //========>>>>

            // if (moved != null && !moved.isNull()) {
            // moved.printInLabel("moved_1 : countM = " + countM);
            // }
            if (moved != null && !moved.isNull())
            {// ~isempty(moved)
             // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
                /*
                 * maxMoved = JDatafun.max(moved).start(); if (maxMoved >= n) {
                 * msg = "#1) \"maxMoved\" = " + maxMoved +
                 * " :  occurred at rep = " + rep + " : countM = " + countM;
                 * throw new ConditionalRuleException("onlineUpdate", msg); }
                 * minMoved = JDatafun.min(moved).start(); if (minMoved < 0) {
                 * msg = "#1) \"minMoved\" = " + minMoved +
                 * " :  occurred at rep = " + rep + " : countM = " + countM;
                 * throw new ConditionalRuleException("onlineUpdate", msg); }
                 */
                // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

                // moved.printInLabel("moved");
                // % Resolve ties in favor of not moving
                indArr = moved.getRowPackedCopy();
                tmpInd = previdx.getEls(indArr).arrayTimes(n).plus(moved);
                // tmpInd.printInLabel("tmpInd");

                tmp2 = minDel.getEls(indArr);

                indArr = tmpInd.getRowPackedCopy();
                tmp = Del.getEls(indArr);
                find = tmp.GT(tmp2).findIJ();
                if (!find.isNull())
                {
                    indArr = find.getIndex();
                    moved = moved.getEls(indArr);// moved =
                                                 // moved(Del((previdx(moved)-1)*N
                                                 // + moved) > minDel(moved));
                                                 // //========>>>>

                    // moved.printInLabel("moved_2 : countM = " + countM);

                    /*
                     * maxMoved = JDatafun.max(moved).start(); if (maxMoved >=
                     * n) { msg = "#2) \"maxMoved\" = " + maxMoved +
                     * " :  occurred at rep = " + rep + " : countM = " + countM;
                     * throw new ConditionalRuleException("onlineUpdate", msg);
                     * } minMoved = JDatafun.min(moved).start(); if (minMoved <
                     * 0) { msg = "#2) \"minMoved\" = " + minMoved +
                     * " :  occurred at rep = " + rep + " : countM = " + countM;
                     * throw new ConditionalRuleException("onlineUpdate", msg);
                     * }
                     */
                }
                else
                {
                    // moved = null;
                    msg = "Instance variable \"moved\" is null. Uncomment the line above to assign : \"moved = null\".";
                    throw new ConditionalRuleException("onlineUpdate", msg);
                }
            }// end
            if (moved == null || moved.isNull())
            {// isempty(moved)
             // % Count an iteration if phase 2 did nothing at all, or if
             // we're
             // % in the middle of a pass through all the points
                if ((iter == iter1) || nummoved > 0)
                {
                    iter = iter + 1;
                    if (opts.getDisplay() == OptDisplay.Iter)
                    {// display > 2 //% 'iter'
                        System.out.printf(dispfmt, iter, 2, nummoved, totsumD);// disp(sprintf(dispfmt,iter,2,nummoved,totsumD));
                                                                               // //========>>>>
                    }// end
                }// end
                converged = true;
                break;
            }// end

            // ==================================================================
            // % Pick the next move in cyclic order
            tmpInd = moved.minus(lastmoved).minus(1);
            tmp = JElfun.mod(Matrix.indicesToMatrix(tmpInd), (double) n);
            tmp = tmp.plus(lastmoved);
            tmp = JDatafun.min(tmp);

            // Is the adding 1 below, ie, [plus(1.0)] necessary? Comment out if
            // it's not.
            tmp = JElfun.mod(tmp, (double) n).plus(1.0);

            // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            tmp = JElfun.mod(tmp, (double) n);
            // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

            moved = tmp.toIndices();// moved = mod(min(mod(moved - lastmoved -
                                    // 1, N) + lastmoved), N) + 1;
                                    // //========>>>>

            /*
             * maxMoved = JDatafun.max(moved).start(); if (maxMoved >= n) { msg
             * = "#3) \"maxMoved\" = " + maxMoved + " :  occurred at rep = " +
             * rep + " : countM = " + countM; throw new
             * ConditionalRuleException("onlineUpdate", msg); } minMoved =
             * JDatafun.min(moved).start(); if (minMoved < 0) { msg =
             * "#3) \"minMoved\" = " + minMoved + " :  occurred at rep = " + rep
             * + " : countM = " + countM; throw new
             * ConditionalRuleException("onlineUpdate", msg); }
             */
            // moved.printInLabel("moved_3 : countM = " + countM);
            // % If we've gone once through all the points, that's an iteration
            if (moved.LTEQ(lastmoved).allBoolean())
            {// (moved <= lastmoved)
                iter = iter + 1;
                if (opts.getDisplay() == OptDisplay.Iter)
                {// display > 2 % 'iter'
                    System.out.printf(dispfmt, iter, 2, nummoved, totsumD);// disp(sprintf(dispfmt,iter,2,nummoved,totsumD));
                                                                           // //========>>>>
                }// end
                if (iter >= maxit)
                {
                    break;
                }

                nummoved = 0;
            }// end

            nummoved = nummoved + 1;
            lastmoved = moved.copy(); // ========>>>>

            // if (moved != null && !moved.isNull()) {
            indArr = moved.getRowPackedCopy();
            // moved.printInLabel("moved : countM = " + countM);

            oidx = idx.getEls(indArr);// oidx = idx(moved); //========>>>>
            nidx = nidx.getEls(indArr); // nidx = nidx(moved)//========>>>>
            // }

            // if (countM == 2) {
            // oidx.printInLabel("oidx");
            // nidx.printInLabel("nidx");
            // }
            int[] indArr2 = nidx.getRowPackedCopy();
            tmp = Del.getMatrix(indArr, indArr2);
            indArr2 = oidx.getRowPackedCopy();
            tmp2 = Del.getMatrix(indArr, indArr2);
            tmp = tmp.minus(tmp2);
            // check if tmp is not a scalar
            if (tmp.numel() != 1)
            {
                msg = "\"tmp\" has more than 1 element.";
                throw new ConditionalRuleException("onlineUpdate", msg);
            }
            totsumD = totsumD + tmp.start();// totsumD = totsumD +
                                            // Del(moved,nidx) -
                                            // Del(moved,oidx); //========>>>>

            // if (countM == 2) {
            // }
            // System.out.println("################################  [Count : "
            // + countM + "]  ################################");
            // % Update the cluster index vector, and the old and new cluster
            // % counts and centroids
            // m.printInLabel("m1 : iter --> " + iter);
            idx.setElements(indArr, nidx);// idx(moved) = nidx; //========>>>>
            indArr2 = nidx.getRowPackedCopy();
            tmpInd = m.getEls(indArr2).plus(1);
            m.setElements(indArr2, tmpInd);// m(nidx) = m(nidx) + 1;
                                           // //========>>>>
            // m.printInLabel("m2 : iter --> " + iter);
            indArr2 = oidx.getRowPackedCopy();
            tmpInd = m.getEls(indArr2).minus(1);
            m.setElements(indArr2, tmpInd);// m(oidx) = m(oidx) - 1;
                                           // //========>>>>
            // m.printInLabel("m3 : iter --> " + iter);

            countM++;

            // moved.printInLabel("moved : PRE");
            // //////////////////////////////////////////////////////////////////
            onlineUpdateUpdateClusterIndexSwitch();
            // //////////////////////////////////////////////////////////////////
            // moved.printInLabel("moved : POST");

            tmpInd = oidx.toRowVector().mergeH(nidx.toRowVector());
            // tmpInd.printInLabel("[oidx nidx] - [Count : " + countM + "]");

            QuickSort sort = new QuickSortInd(tmpInd, true, true);
            changed = (Indices) sort.getSortedObject();// changed = sort([oidx
                                                       // nidx]); //========>>>>

        }// end while //% phase two
         // //###########################################
         // ######################################################################

        return converged;
    }

    private Matrix distfun(Matrix X, Matrix C, KmnDist dist, int iter)
    {
        // DISTFUN Calculate point to cluster centroid distances.
        int N = X.getRowDimension();// [N,P] = size(X);
        int P = X.getColumnDimension();
        int rowC = C.getRowDimension();
        Matrix DD = Matrix.zeros(N, rowC);
        int nclusts = rowC;// size(C,1);
        double val = 0.0;
        Matrix tmp = null;
        boolean cond = false;
        String msg = "";

        switch (dist)
        {
        case sqeuclidean:
        {
            for (int i = 0; i < nclusts; i++)
            {
                tmp = X.getColumnAt(0);
                val = C.get(i, 0);
                tmp = tmp.minus(val);
                tmp = JElfun.pow(tmp, 2.0);
                DD.setColumnAt(i, tmp);// D(:,i) = (X(:,1) - C(i,1)).^2;
                for (int j = 1; j < P; j++)
                {
                    tmp = X.getColumnAt(j);
                    val = C.get(i, j);
                    tmp = tmp.minus(val);
                    tmp = JElfun.pow(tmp, 2.0);
                    tmp = DD.getColumnAt(i).plus(tmp);
                    DD.setColumnAt(i, tmp);// D(:,i) = DD(:,i) + (X(:,j) -
                                           // C(i,j)).^2;
                }// end

                // % DD(:,i) = sum((X - C(repmat(i,N,1),:)).^2, 2);
            }// end
            break;
        }
        case cityblock:
        {
            for (int i = 0; i < nclusts; i++)
            {
                tmp = X.getColumnAt(0);
                val = C.get(i, 0);
                tmp = tmp.minus(val);
                tmp = tmp.abs();
                DD.setColumnAt(i, tmp);// D(:,i) = abs(X(:,1) - C(i,1));
                for (int j = 1; j < P; j++)
                {
                    tmp = X.getColumnAt(j);
                    val = C.get(i, j);
                    tmp = tmp.minus(val);
                    tmp = tmp.abs();
                    tmp = DD.getColumnAt(i).plus(tmp);
                    DD.setColumnAt(i, tmp);// D(:,i) = DD(:,i) + abs(X(:,j) -
                                           // C(i,j));
                }// end

                // % DD(:,i) = sum(abs(X - C(repmat(i,N,1),:)), 2);
            }// end
            break;
        }
        case correlation:
        case cosine:
        {// ,'correlation'}
         // The points are normalized, centroids are not, so normalize them
            tmp = JElfun.pow(C, 2.0);
            tmp = JDatafun.sum(tmp, Dimension.COL);
            Matrix normC = JElfun.sqrt(tmp); // normC = sqrt(sum(C.^2, 2));
            cond = normC.LT(MathUtil.EPS).anyBoolean();
            if (cond)
            {// any(normC < eps(class(normC))) % small relative to unit-length
             // data points
             // error('stats:kmeans:ZeroCentroid', ...
             // 'Zero cluster centroid created at iteration %d.',iter);
                msg = "Zero cluster centroid created at iteration #" + iter + " :";
                throw new ConditionalRuleException("distfun", msg);
            }// end

            for (int i = 0; i < nclusts; i++)
            {
                val = normC.getElementAt(i);
                tmp = C.getRowAt(i);
                tmp = tmp.arrayRightDivide(val); // C(i,:)./normC(i)
                tmp = tmp.transpose();
                tmp = X.uminus().times(tmp).plus(1.0);
                tmp = JDatafun.max(tmp, 0.0);
                DD.setColumnAt(i, tmp);// D(:,i) = max(1 - X *
                                       // (C(i,:)./normC(i))', 0);
            }// end
            break;
        }
        case hamming:
        {
            for (int i = 0; i < nclusts; i++)
            {
                tmp = X.getColumnAt(0);
                val = C.get(i, 0);
                tmp = tmp.minus(val);
                tmp = tmp.abs();
                DD.setColumnAt(i, tmp);// D(:,i) = abs(X(:,1) - C(i,1));
                for (int j = 1; j < P; j++)
                {
                    tmp = X.getColumnAt(j);
                    val = C.get(i, j);
                    tmp = tmp.minus(val);
                    tmp = tmp.abs();
                    tmp = DD.getColumnAt(i).plus(tmp);
                    DD.setColumnAt(i, tmp);// D(:,i) = DD(:,i) + abs(X(:,j) -
                                           // C(i,j));
                }// end
                tmp = DD.getColumnAt(i).arrayRightDivide((double) P);
                DD.setColumnAt(i, tmp);// D(:,i) = DD(:,i) / P;

                // % DD(:,i) = sum(abs(X - C(repmat(i,N,1),:)), 2) / P;
            }// end
            break;
        }// end
        default:
        {
            msg = "Distance parameter \"dist\" (= " + dist.toString() + ") is not supported.";
            throw new ConditionalRuleException("distfun", msg);
        }
        }
        return DD;

    }

    /**
     * @return the X
     */
    public Matrix getInput()
    {
        return this.input;
    }

    /**
     * @return the k
     */
    public int getK()
    {
        return k;
    }

    /**
     * @return the keyVal
     */
    public KeyValue getKeyVal()
    {
        return keyVal;
    }

    /**
     * @param keyVal
     *            the keyVal to set
     */
    public void setKeyVal(KeyValue keyVal)
    {
        this.keyVal = keyVal;
    }

    /**
     * @return the opts
     */
    public StatsOption getOpts()
    {
        return opts;
    }

    /**
     * @param opts
     *            the opts to set
     */
    public void setOpts(StatsOption opts)
    {
        this.opts = opts;
    }

    /**
     * @return the idx
     */
    public Indices getIdx()
    {
        return idx;
    }

    /**
     * @return the C
     */
    public Matrix getC()
    {
        return C;
    }

    /**
     * @return the sumD
     */
    public Matrix getSumD()
    {
        return sumD;
    }

    /**
     * @return the DD
     */
    public Matrix getD()
    {
        return D;
    }

    /**
     * @param distance
     *            the distance to set
     */
    public void setDistance(KmnDist distance)
    {
        this.distance = distance;
    }

    /**
     * @param start
     *            the start to set
     */
    public void setStart(Object start)
    {
        if ((this.start instanceof KmnStart) || (this.start instanceof Matrix) || (this.start instanceof Matrix3D))
        {
            this.start = start;
        }
        else if (this.start instanceof Number)
        {
            double val = ((Number) start).doubleValue();
            this.start = new Matrix(1, 1, val);
        }
        else
        {
            String msg = "Parameter \"start\" must be an instance of \"KmnStart\", \"Matrix\", \"Matrix3D\" or \"Number\".";
            throw new ConditionalRuleException("setStart", msg);
        }

    }

    /**
     * @param replicates
     *            the replicates to set
     */
    public void setReplicates(Integer replicates)
    {
        this.replicates = replicates;
    }

    /**
     * @param emptyaction
     *            the emptyaction to set
     */
    public void setEmptyaction(KmnEmptyAction emptyaction)
    {
        this.emptyaction = emptyaction;
    }

    /**
     * @param k
     *            the k to set
     */
    public void setK(Integer k)
    {
        if (k != null)
        {
            if (k.intValue() < 2)
            {
                String msg = "Integer parameter \"k\" (= " + k + ") must be at least 2;";
                throw new ConditionalRuleException("setK", msg);
            }
        }
        this.k = k;
    }

    public void setUseRandSam(boolean useRandSam)
    {
        this.useRandSam = useRandSam;
    }

    /**
     * @param onlinephase
     *            the onlinephase to set
     */
    public void setOnlinephase(boolean onlinephase)
    {
        this.onlinephase = onlinephase;
    }

    public static void main(String[] args)
    {
        /*
         * String dispfmt = "%6d\t%6d\t%8d\t%12g\n"; int n = 10; Matrix mat =
         * Matrix.randn(1, n).times(20.0);
         * 
         * for (int i = 0; i < n; i++) { System.out.printf(dispfmt, i + 1, 1, 4,
         * mat.getElementAt(i)); }
         */
        String fileName = "C:/Users/Sione/Documents/MATLAB/datafiles/kmeansdata/Xkmeans.txt";
        Matrix X = null;
        try
        {
            X = Matrix.read(fileName);
        }
        catch (FileNotFoundException fne)
        {
            fne.printStackTrace();
        }
        catch (IOException ex)
        {
            // Logger.getLogger(Kmeans.class.getName()).log(Level.SEVERE, null,
            // ex);
            ex.printStackTrace();
        }

        boolean binary = false;
        if (binary)
        {
            // neg = find(X<0);
            int[] neg = X.LT(0.0).findIJ().getIndex();
            X.setElements(neg, 0.0);
            int[] pos = X.GTEQ(0.0).findIJ().getIndex();
            X.setElements(pos, 1.0);
        }// end

        Kmeans KM = new Kmeans(4, X, 4);
        StatsOption stopt = new StatsOption();

        // stopt.setDisplay(OptDisplay.Final);
        stopt.setDisplay(OptDisplay.Iter);
        KM.setOpts(stopt);

        // KmnDist dist = KmnDist.cityblock;
        // KM.setDistance(dist);
        KM.setReplicates(5);

        KM.build();

        KM.getIdx().plus(1).printInLabel("idx");

        KM.getC().printInLabel("C", 10);

        KM.getSumD().printInLabel("sumD", 10);

        KM.getD().printInLabel("D", 10);

        System.out.println("\n\n$$$$$$$$$$$$$$$$$$$$$$$  Execute Successfully $$$$$$$$$$$$$$$$$$$$$$$");

    }
}
