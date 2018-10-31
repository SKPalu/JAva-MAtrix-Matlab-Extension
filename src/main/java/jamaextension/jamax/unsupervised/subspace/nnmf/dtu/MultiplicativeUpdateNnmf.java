/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.unsupervised.subspace.nnmf.dtu;

import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.NumericConditionalException;
import jamaextension.jamax.datafun.JDatafun;

/**
 * 
 * @author Sione
 */
public class MultiplicativeUpdateNnmf extends DtuBaseNnmf
{

    public MultiplicativeUpdateNnmf(Matrix data)
    {
        super(data);
    }

    public void extractFeatures()
    {
        if (isDataSampleNull())
        {
            throw new NumericConditionalException("extractFeatures : Data sample is null.");
        }
        // if (this.isNonNegative()) {
        // throw new
        // NumericConditionalException("extractFeatures : Data sample elements must be all be positive.");
        // }

        /*
         * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%% % initialize random W and H
         * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
         */
        Matrix X = dataSample;
        // [D,N]=size(X);
        int n = X.getRowDimension();
        int m = X.getColumnDimension();
        // double Xscale = JDatafun.sum(JDatafun.sum(X)).start();
        int K = this.numberOfComponents;
        // INIT

        if (this.isSvdInitialized())
        {
            Object[] obj = null;
            if(this.isOptimalSvdInitialized())// (svdInitializedOptimized)
            {
                obj = DtuNnmfUtil.nmfSvdInitialization(X);
            }
            else
            {
                obj = DtuNnmfUtil.nmfSvdInitialization(X, K);
            }
            
            W = (Matrix)obj[0];
            H = (Matrix)obj[1];
            this.numberOfComponents = (Integer)obj[2];
        }
        else
        {
            W = Matrix.random(n, K);
            H = Matrix.random(K, m);
        }
        
        
        // [n,m]=size(X);
        //W = Matrix.random(n, K);
        //H = Matrix.random(K, m);

        // Use W*H to test for convergence
        Matrix Xr_old = W.times(H);
        int Nitsmax = maxIteration;
        int print_iter = 50; // iterations between print on screen
        boolean speak = printInternal;

        for (int iter = 1; iter <= Nitsmax; iter++)
        {

            if (iter == 5)
            {
                // int stop = 0;
            }

            // Euclidean multiplicative method
            Matrix WtX = W.transpose().times(X);
            Matrix num = H.arrayTimes(WtX);
            // num.printInLabel("1) num_" + iter);
            Matrix den = W.transpose().times(W).times(H).plus(MathUtil.EPS);
            // den.printInLabel("1) den_" + iter);
            H = num.arrayRightDivide(den);// H.*(W'*X)./((W'*W)*H+eps);

            Matrix HXt = H.times(X.transpose()).transpose();
            num = W.arrayTimes(HXt);
            // num.printInLabel("2) num_" + iter);
            den = W.times(H.times(H.transpose())).plus(MathUtil.EPS);
            // den.printInLabel("2) den_" + iter);
            W = num.arrayRightDivide(den);// W.*(H*X')'./(W*(H*H')+eps);

            // System.out.println("=================================================");

            // print to screen
            if (iter % print_iter == 0 && speak)
            {
                Matrix Xr = W.times(H);
                double diff = JDatafun.sum(JDatafun.sum(Xr_old.minus(Xr).abs())).start();
                Xr_old = Xr;
                Matrix temp = W.times(H);
                double eucl_dist = DtuNnmfUtil.nmfEuclid(X, temp);// nmf_euclidean_dist(X,W*H);
                Matrix temp2 = X.minus(temp).abs();
                double errorx = JDatafun.mean(JDatafun.mean(temp2)).start() / JDatafun.mean(JDatafun.mean(X)).start();// mean(mean(abs(X-W*H)))/mean(mean(X));

                System.out.println("Iter = " + iter + ", relative error = " + errorx + ", diff = " + diff
                        + ", eucl dist = " + eucl_dist);

                double lim = Math.pow(10.0, -5.0);
                if (errorx < lim)
                {
                    break;
                }
            }

        }// end for

        this.buildFeatures = true;

        // this.orderComponents();

    }// end method

    static Matrix getWset()
    {
        double[][] x =
        {
                {
                        0.3922, 0.0318, 0.8235
                },
                {
                        0.6555, 0.2769, 0.6948
                },
                {
                        0.1712, 0.0462, 0.3171
                },
                {
                        0.7060, 0.0971, 0.9502
                }
        };
        return new Matrix(x);
    }

    static Matrix getHset()
    {
        double[][] x =
        {
                {
                        0.0344, 0.7655, 0.4898, 0.7094, 0.6797
                },
                {
                        0.4387, 0.7952, 0.4456, 0.7547, 0.6551
                },
                {
                        0.3816, 0.1869, 0.6463, 0.2760, 0.1626
                }
        };

        return new Matrix(x);
    }

    public static void main(String[] args)
    {
        // Matrix A = generateTestData();
        double[][] aa = new double[][]
        {
                {
                        19, 18, 16, 18, 19
                },
                {
                        5, 15, 9, 15, 18
                },
                {
                        12, 9, 12, 4, 8
                },
                {
                        10, 0, 16, 8, 18
                }
        };

        Matrix A = new Matrix(aa);

        System.out.print("\n-------- A --------\n");
        A.print(4, 0);// print to a precision of 0 decimal places

        MultiplicativeUpdateNnmf NNMF = new MultiplicativeUpdateNnmf(A);
        // NNMF.setData(A);
        NNMF.setNumberOfComponents(3);
        // NNMF.setRank(3);//reduce the original data dimension from 5 (columns)
        // to 3;
        // NNMF.computeNNMF();
        NNMF.setMaxIteration(10);
        NNMF.setPrintInternal(true);

        NNMF.extractFeatures();

        Matrix W = NNMF.getW();
        W.printInLabel("W");

        Matrix H = NNMF.getH();
        H.printInLabel("H");

        /*
         * Matrix A_reconstructed = W.times(H);
         * //System.out.print("\n-------- A_reconstructed --------\n");
         * A_reconstructed.printInLabel("A_reconstructed");//.print(4,
         * 4);//print to a precision of 4 decimal pl
         * 
         * Matrix difference = A.minus(A_reconstructed);
         * //System.out.print("\n-------- difference --------\n");
         * //difference.print(4, 4);//print to a precision of 4 decimal pl
         * difference.printInLabel("difference");
         * 
         * System.out.print("\n\n");
         */
    }
}
