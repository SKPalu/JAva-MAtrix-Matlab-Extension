/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.unsupervised.subspace.nnmf.dtu;

import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.matfun.JMatfun;

/**
 * 
 * @author Sione
 */
public class AlternateLeastSqNnmf extends DtuBaseNnmf
{

    public AlternateLeastSqNnmf(Matrix data)
    {
        super(data);
    }

    public void extractFeatures()
    {

        Matrix X = dataSample;
        // [D,N]=size(X);
        int D = X.getRowDimension();
        int N = X.getColumnDimension();
        double Xscale = JDatafun.sum(JDatafun.sum(X)).start();
        int K = this.numberOfComponents;
        // INIT
        W = Matrix.random(D, K);
        H = Matrix.random(K, N);
        Matrix Xr_old = W.times(H);
        double Rscale = JDatafun.sum(JDatafun.sum(Xr_old)).start();
        double sqrnorm = Math.sqrt(Rscale / Xscale);
        H = H.arrayRightDivide(sqrnorm);
        W = W.arrayRightDivide(sqrnorm);

        int Nitsmax = maxIteration;
        int print_iter = 50; // iterations between print on screen
        boolean speak = printInternal;

        // ITERATE
        for (int n = 1; n <= Nitsmax; n++)
        {
            // W=X*(pinv(H*H')*H)'; % old updates

            // W = ((pinv(H*H')*H)*X')';
            Matrix HHT = H.times(H.transpose());
            Matrix pinv = JMatfun.pinv(HHT);
            Matrix temp = pinv.times(H).times(X.transpose());
            W = temp.transpose();
            // W=(W>0).*W;
            W = W.arrayTimes(W.GT(0.0));
            // W=W./(repmat(sum(W),D,1)+eps); % normalize columns to unit length
            temp = JDatafun.sum(W);
            temp = temp.repmat(D, 1).plus(MathUtil.EPS); // normalize columns to
                                                         // unit length

            // H=(W*pinv(W'*W))'*X;
            Matrix WTW = W.transpose().times(W);
            pinv = JMatfun.pinv(WTW);
            temp = W.times(pinv);
            H = temp.transpose().times(X);
            // H=H.*(H>0);
            H = H.arrayTimes(H.GT(0.0));

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
