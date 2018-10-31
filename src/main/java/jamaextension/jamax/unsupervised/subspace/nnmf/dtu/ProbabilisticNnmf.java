/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.unsupervised.subspace.nnmf.dtu;

import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.NumericConditionalException;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elfun.JElfun;

/**
 * 
 * @author Sione
 */
public class ProbabilisticNnmf extends DtuBaseNnmf
{

    public ProbabilisticNnmf(Matrix data)
    {
        super(data);
    }

    public void extractFeatures()
    {
        if (isDataSampleNull())
        {
            throw new NumericConditionalException("extractFeatures : Data sample is null.");
        }
        if (this.isNonNegative())
        {
            throw new NumericConditionalException("extractFeatures : Data sample elements must be all be positive.");
        }

        Matrix X = dataSample;
        // [D,N]=size(X);
        int D = X.getRowDimension();
        int N = X.getColumnDimension();
        // double Xscale = JDatafun.sum(JDatafun.sum(X)).start();
        int K = this.numberOfComponents;
        int maxiter = maxIteration;

        Matrix powers = Matrix.linIncrement(1.0, (double) maxiter, 1.0).minus(1.0)
                .arrayRightDivide((double) maxiter - 1.0).plus(0.5);// .5+(2.5-1.5)*((1:maxiter)-1)/(maxiter-1);

        double X_factor = JDatafun.sum(JDatafun.sum(X)).start();
        Matrix X_org = X.copy();
        X = dataSample.arrayRightDivide(X_factor);

        // INIT
        W = Matrix.random(D, K);
        Matrix sumMat = JDatafun.sum(W);
        W = W.arrayRightDivide(sumMat.repmat(D, 1));// W./repmat(sum(W,1),D,1);
        H = Matrix.random(K, N);
        sumMat = JDatafun.sum(H, Dimension.COL);
        H = H.arrayRightDivide(sumMat.repmat(1, N));// H./repmat(sum(H,2),1,N);

        Matrix P = new Matrix(K, 1, 1.0 / (double) K);// ones(K,1);
        // P=P/sum(P);
        Matrix W1 = W.copy();
        Matrix H1 = H.copy();

        // Use W*H to test for convergence
        Matrix Xr_old = W.times(H);

        int print_iter = 50; // iterations between print on screen
        boolean speak = printInternal;

        for (int n = 1; n <= maxiter; n++)
        {
            // E-step
            Matrix Qnorm = W.times(P.diag()).times(H);

            for (int k = 0; k < (K - 1); k++)
            {
                // E-step
                Matrix temp = W.getColumnAt(k).times(H.getRowAt(k)).arrayTimes(P.get(k, 0));
                Matrix Q = temp.arrayRightDivide(Qnorm.plus(MathUtil.EPS));// (W(:,k)*H(k,:)*P(k))./(Qnorm+eps);
                Matrix XQ = X.arrayTimes(Q);

                // M-step W
                Matrix dummy = JDatafun.sum(XQ, Dimension.COL);
                temp = dummy.arrayRightDivide(JDatafun.sum(dummy).start());
                W.setColumnAt(k, temp);
                dummy = JDatafun.sum(XQ);
                temp = dummy.arrayRightDivide(JDatafun.sum(dummy).start());
                H.setRowAt(k, temp);
            }// end inner for

            W = W1;
            H = H1;

            // print to screen
            if (n % print_iter == 0 && speak)
            {
                Matrix Xr = W.times(H);
                double diff = JDatafun.sum(JDatafun.sum(Xr_old.minus(Xr).abs())).start();
                Xr_old = Xr;
                Matrix temp = W.times(H);
                double eucl_dist = DtuNnmfUtil.nmfEuclid(X, temp);// nmf_euclidean_dist(X,W*H);
                Matrix temp2 = X.minus(temp).abs();
                double errorx = JDatafun.mean(JDatafun.mean(temp2)).start() / JDatafun.mean(JDatafun.mean(X)).start();// mean(mean(abs(X-W*H)))/mean(mean(X));

                System.out.println("Iter = " + n + ", relative error = " + errorx + ", diff = " + diff + ", errorx = "
                        + eucl_dist);

                double lim = Math.pow(10.0, -5.0);
                if (errorx < lim)
                {
                    break;
                }
            }// end if

            W = W.times(JElfun.sqrt(P).diag()).times(X_factor);// W*diag(sqrt(P))*X_factor;
            H = JElfun.sqrt(P).diag().times(H);// diag(sqrt(P))*H;

        }// end outer for

    }// end method
}
