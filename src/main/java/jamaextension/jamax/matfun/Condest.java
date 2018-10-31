/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.matfun;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.LUDecomposition;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;

/**
 * 
 * @author Sione
 */
public class Condest
{

    private double c;
    private Matrix v;

    public Condest(Matrix A, int t)
    {
        if (A == null)
        {
            return;
        }
        if (!A.isSquare())
        {
            throw new ConditionalException("Condest : Matrix parameter \"A\" must be square.");
        }

        LUDecomposition LU = A.lu();
        Matrix L = LU.getL();
        Matrix U = LU.getU();

        Matrix uabsdiag = U.diag().abs();
        FindInd k = uabsdiag.EQ(0.0).findIJ();

        if (!k.isNull())
        {
            c = Double.POSITIVE_INFINITY;
            int n = A.length();
            v = Matrix.zeros(n, 1);
            int K = JDatafun.min(k.getIndexInd()).start();
            // v(K) = 1;
            v.setElementAt(K, 1);
            if (K > 1)
            {
                // v(1:k-1) = -U(1:k-1,1:k-1)\U(1:k-1,k);
            }
        }
        else
        {
        }
    }
}
