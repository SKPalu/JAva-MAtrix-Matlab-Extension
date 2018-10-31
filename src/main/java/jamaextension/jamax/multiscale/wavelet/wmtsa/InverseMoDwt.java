/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.multiscale.wavelet.wmtsa;

import jamaextension.jamax.Matrix;

//import com.feynmanperceptron.jnumeric.jamax.*;  
//import com.feynmanperceptron.jnumeric.jamax.constants.*;
//import com.feynmanperceptron.jnumeric.jamax.datafun.*; 

//import com.feynmanperceptron.jnumeric.wavelet.wmtsa.filter.modwt.*;

/**
 * 
 * @author Feynman Perceptrons
 */
public class InverseMoDwt
{

    private Matrix X;

    public InverseMoDwt(MoDwt MD)
    {
        if (MD == null)
        {
            throw new IllegalArgumentException(" InvMoDwt : Parameter 'MD' must be non-null.");
        }
        if (!MD.isTransformed())
        {
            throw new IllegalArgumentException(
                    " InvMoDwt : Method \"transform\" in parameter 'MD' must be called first.");
        }

        WaveletFilter wtf_s = MD.getFilter();
        Matrix ht = wtf_s.getH();
        Matrix gt = wtf_s.getG();
        int L = ht.numel();

        int N = MD.getNX();// = att.NX;
        // int NW = MD.getNW();// = att.NW;
        int J0 = MD.getJ0();// = att.J0;
        int NChan = MD.getNChan();// = att.NChan;
        // Boundary boundary = MD.getBoundary();
        boolean RetainVJ = MD.isRetainVJ();

        Matrix[] WJt = MD.getWJt();
        Matrix[] VJt = MD.getVJt();

        Matrix Vout = new Matrix(N, NChan, Double.NaN);
        X = new Matrix(N, NChan, Double.NaN);

        int i, j;

        for (i = 0; i < NChan; i++)
        {
            Matrix Vin = null;
            if (RetainVJ)
            {
                Vin = VJt[i].getColumnAt(J0);// VJt(:,J0,i);
            }
            else
            {
                Vin = VJt[i].getColumnAt(0);// VJt(:,1,i);
            }

            for (j = J0; j >= 1; j--)
            {
                Vout = imodwtj(WJt[i].getColumnAt(j - 1), Vin, ht, gt, j, N, L);
                Vin = Vout;
            }// end inner for

            // X(:,i) = Vout(1:N);
            X.setColumnAt(i, Vout);
        }// end outer for

    }

    protected static Matrix imodwtj(Matrix Win, Matrix Vin, Matrix ht, Matrix gt, int j, int N, int L)
    {
        int k, n, t;
        double val = 0.0;
        Matrix Vout = new Matrix(N, 1);

        for (t = 0; t < N; t++)
        {
            k = t;
            val = (ht.start() * Win.getElementAt(k)) + (gt.start() * Vin.getElementAt(k));
            Vout.set(t, 0, val);
            for (n = 1; n < L; n++)
            {
                k += (int) Math.pow(2.0, (double) j - 1.0);
                if (k >= N)
                {
                    k -= N;
                }
                val = Vout.get(t, 0) + (ht.get(0, n) * Win.getElementAt(k)) + (gt.get(0, n) * Vin.getElementAt(k));
                Vout.set(t, 0, val);
            }
        }
        return Vout;
    }

}
