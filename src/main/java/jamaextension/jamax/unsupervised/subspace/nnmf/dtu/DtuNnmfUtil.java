/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.unsupervised.subspace.nnmf.dtu;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.SvdJLapack;
import jamaextension.jamax.SvdJLapack.Economy;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elfun.JElfun;

/**
 * 
 * @author Sione
 */
public final class DtuNnmfUtil
{

    public static double nmfEuclid(Matrix X, Matrix Y)
    {
        Matrix diffSqr = X.minus(Y);
        diffSqr = JElfun.pow(diffSqr, 2.0);
        double err = JDatafun.sum(JDatafun.sum(diffSqr)).start();
        return err;
    }

    private static Object[] ChoosingR(Matrix Z)
    {
        // [u,s,v] = svd(Z);
        SvdJLapack svd = new SvdJLapack(Z, Economy.ECON);
        Matrix U = svd.getU();
        Matrix S = svd.getS();
        Matrix V = svd.getV();
        // Matrix s = null;
        Matrix sum1 = JDatafun.sum(S);
        double sum2 = JDatafun.sum(sum1).start();
        double extract = 0;
        int p = 0;
        double dsum = 0;
        while (extract / sum2 < 0.90)
        {
            // p = p + 1;
            dsum = dsum + S.get(p, p);
            extract = dsum;
            p++;
        }
        p = p + 1;
        return new Object[]
        {
                U, S, V, p
        };
    }

    public static Object[] nmfSvdInitialization(Matrix A)
    {
        return nmfSvdInitialization(A, null);
    }

    /*
     * From Paper
     * "New SVD based initialization strategy for Non-negative matrix factorization"
     * by Hanli Qiao.
     */
    public static Object[] nmfSvdInitialization(Matrix A, Integer K)
    {

        Matrix U = null;
        Matrix S = null;
        Matrix V = null;
        Integer p = K;

        if (K == null)
        {
            Object[] obj = ChoosingR(A);
            U = (Matrix) obj[0];
            S = (Matrix) obj[1];
            V = (Matrix) obj[2];
            p = (Integer) obj[3];
        }
        else
        {
            SvdJLapack svd = new SvdJLapack(A, Economy.ECON);
            U = svd.getU();
            S = svd.getS();
            V = svd.getV();
        }

        int[] parr = Indices.linspace(0, p - 1).getRowPackedCopy();

        Matrix W = U.getColumns(parr).abs();// abs(u(:,1:p));
        Matrix srows = S.getRows(parr);
        Matrix H = srows.times(V.transpose()).abs();// abs(s(1:p,:)*v’);

        return new Object[]
        {
                W, H, p
        };
    }

}
