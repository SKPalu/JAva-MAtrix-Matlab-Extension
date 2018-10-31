/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.unsupervised.subspace.nnmf.dtu;

import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.NumericConditionalException;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.matfun.JMatfun;

/**
 * 
 * @author Sione
 */
public class OptimalBrainSurgeonNnmf extends DtuBaseNnmf
{

    public OptimalBrainSurgeonNnmf(Matrix data)
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

        int print_iter = 50; // iterations between print on screen and
                             // convergence test
        int obs_steps = 15; // number of OBS steps to run before truncation
        int maxiter = this.maxIteration;

        Matrix X = dataSample;
        // [D,N]=size(X);
        int D = X.getRowDimension();
        int N = X.getColumnDimension();
        double Xscale = JDatafun.sum(JDatafun.sum(X)).start();
        int K = this.numberOfComponents;
        // INIT

        // [D,N]=size(X);
        W = Matrix.random(D, K);
        H = Matrix.random(K, N);
        // Use W*H to test for convergence
        Matrix Xr_old = W.times(H);
        int Nitsmax = maxIteration;
        boolean speak = printInternal;

        /*
         * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% % Alternating least squares
         * with % optimal brain surgeon iterations.
         * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
         */

        for (int n = 1; n <= maxiter; n++)
        {
            W = JMatfun.pinv(H.times(H.transpose())).times(H).times(X.transpose()).transpose();// ((pinv(H*H')*H)*X')';
            // %%% OSB %%%
            int count = 1;
            Matrix invHesW = H.times(H.transpose()).reciprocate();// (H*H')^-1;

            // ////////////////////////
            while (count < obs_steps)
            {
                if (JDatafun.min(JDatafun.min(W)).start() < -MathUtil.EPS)
                {
                    Matrix dw = Matrix.zeros(W.getRowDimension(), W.getColumnDimension());
                    for (int i = 0; i < W.getRowDimension(); i++)
                    {
                        Indices ei = W.getRowAt(i).LT(0.0);// (W(i,:)<0);
                        if (JDatafun.sum(ei).start() > 0)
                        {
                            int[] arr = ei.find().getRowPackedCopy();
                            Matrix w_neg = W.getMatrix(i, i, arr);// W(i,ei);
                            Matrix h = invHesW.getMatrix(arr, arr);// invHesW(ei,ei);
                            Matrix la = h.reciprocate().times(w_neg.transpose());// h^-1*w_neg';
                            // dw(i,:) = -invHesW(:,find(ei))*la;
                            Matrix temp = invHesW.getColumns(arr).times(la).uminus();
                            dw.setRowAt(i, temp);
                        }// end if
                    }// end for
                    W = W.plus(dw);
                }// end if
                count = count + 1;
            }// end while
             // ////////////////////////

            // OSB END %%%
            W = W.arrayTimes(W.GT(0.0));// (W>0).*W; // truncate negative
                                        // elements
            Matrix temp = JDatafun.sum(W).repmat(D, 1);
            W = W.arrayRightDivide(temp);// W./repmat(sum(W),D,1); // normalize
                                         // columns to unit length

            /*
             * %%%%%%%%%%%%%%% % H update %%%%%%%%%%%%%%%
             */
            Matrix WtW = W.transpose().times(W);
            H = W.times(JMatfun.pinv(WtW)).transpose().times(X);// (W*pinv(W'*W))'*X;
            // %% OSB %%%
            count = 1;
            Matrix invHesH = WtW.reciprocate();// (W'*W)^-1;

            while (count < obs_steps)
            {
                if (JDatafun.min(JDatafun.min(H)).start() < -MathUtil.EPS)
                {
                    Matrix dh = Matrix.zeros(H.getRowDimension(), H.getColumnDimension());
                    for (int i = 0; i < H.getColumnDimension(); i++)
                    {
                        Indices ei = H.getColumnAt(i).LT(0.0);// (H(:,i)<0);
                        if (JDatafun.sum(ei).start() > 0)
                        {
                            int[] arr = ei.find().getRowPackedCopy();
                            Matrix h_neg = H.getMatrix(arr, i, i);// H(ei,i);
                            Matrix h = invHesH.getMatrix(arr, arr);// invHesH(ei,ei);
                            Matrix la = h.reciprocate().times(h_neg);// h^-1*h_neg;
                            // dh(:,i) = -invHesH(:,find(ei))*la;
                            temp = invHesH.getColumns(arr).times(la).uminus();
                            dh.setColumnAt(i, temp);
                        }
                    }
                    H = H.plus(dh);
                }// end if(JDatafun.min(JDatafun.min(H))<-eps){
                count = count + 1;
            }// end while

            // % END OBS %%%
            H = H.arrayTimes(H.GT(0.0));// H.*(H>0); // truncate negative
                                        // elements

            // print to screen
            if (n % print_iter == 0 && speak)
            {
                Matrix Xr = W.times(H);
                double diff = JDatafun.sum(JDatafun.sum(Xr_old.minus(Xr).abs())).start();
                Xr_old = Xr;
                temp = W.times(H);
                double eucl_dist = DtuNnmfUtil.nmfEuclid(X, temp);// nmf_euclidean_dist(X,W*H);
                Matrix temp2 = X.minus(temp).abs();
                double errorx = JDatafun.mean(JDatafun.mean(temp2)).start() / JDatafun.mean(JDatafun.mean(X)).start();// mean(mean(abs(X-W*H)))/mean(mean(X));
                /*
                 * disp(['Iter = ',int2str(n),... ', relative error =
                 * ',num2str(errorx),... ', diff = ', num2str(diff),... ', eucl
                 * dist ' num2str(eucl_dist)])
                 */
                double lim = Math.pow(10.0, -5.0);
                if (errorx < lim)
                {
                    break;
                }
            }

        }// end for

    }// end method
}
