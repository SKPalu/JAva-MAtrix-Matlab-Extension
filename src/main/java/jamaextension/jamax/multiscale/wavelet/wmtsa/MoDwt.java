/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.multiscale.wavelet.wmtsa;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.multiscale.wavelet.wmtsa.filter.modwt.MoDwtLA8;

//import com.feynmanperceptron.jnumeric.jamax.*;  
//import com.feynmanperceptron.jnumeric.jamax.constants.*;
//import com.feynmanperceptron.jnumeric.jamax.datafun.*; 

//import com.feynmanperceptron.jnumeric.wavelet.wmtsa.filter.modwt.*;

/**
 * 
 * @author Feynman Perceptrons
 */
public class MoDwt
{

    private Matrix X;
    private WaveletFilter filter = new MoDwtLA8();
    private Boundary boundary = Boundary.reflection;
    private boolean retainVJ = false;
    private Object nthLevel = NthLevelChoice.conservative;

    private int NX = 0;
    private int NW = 0;
    private int J0 = 0;
    private int NChan = 0;
    private boolean aligned = false;

    private Matrix[] WJt;
    private Matrix[] VJt;
    private boolean hasTransformed = false;

    public MoDwt(Matrix X)
    {// , WaveletFilter wtf, Object nlevels, String boundary, Object...
     // varargin){

        if (X == null)
        {
            throw new IllegalArgumentException(" MoDwt : Parameter 'X' must be non-null.");
        }
        if (X.isVector())
        {
            if (X.isRowVector())
            {
                this.X = X.toColVector();
            }
            else
            {
                this.X = X;
            }
        }
        else
        {
            this.X = X;
        }

        // if(wtf==null){
        // throw new
        // IllegalArgumentException(" MoDwt : Parameter 'wtf' must be non-null.");
        // }

        /*
         * if(wtf instanceof String){ } else if(wtf instanceof
         * WaveletTransform){ } else{ throw new IllegalArgumentException(
         * " MoDwt : Parameter 'wtf' must be an instanceof a \"String\" or \"WaveletTransform\"."
         * ); }
         */

    }

    public void setRetainVJ(boolean tf)
    {
        this.retainVJ = tf;
    }

    public void setFilter(WaveletFilter filter)
    {
        if (filter == null)
        {
            return;
        }
        if (filter.getTransform() != Transform.MODWT)
        {
            throw new IllegalArgumentException(" setFilter : Parameter 'filter' must be a \"MODWT\" choice.");
        }
        this.filter = filter;
    }

    public void setBoundary(Boundary boundary)
    {
        if (boundary == null)
        {
            return;
        }
        this.boundary = boundary;
    }

    public void setNthLevel(Object nthLevel)
    {
        if (nthLevel == null)
        {
            return;
        }
        if (!(nthLevel instanceof Integer) && !(nthLevel instanceof NthLevelChoice))
        {
            throw new IllegalArgumentException(
                    " setNthLevel : Parameter 'nthLevel' must be an instanceof \"Integer\" or \"NthLevelChoice\".");
        }
    }

    private int chooseNthlevel(NthLevelChoice choice, int N)
    {
        int L = filter.getL();
        int j0 = 0;
        if (choice == NthLevelChoice.conservative)
        {
            double num = ((double) N / ((double) L - 1.0)) - 1.0;
            j0 = (int) Math.floor(MathUtil.log2(num));
        }
        else if (choice == NthLevelChoice.max)
        {
            j0 = (int) Math.floor(MathUtil.log2((double) N));
        }
        else if (choice == NthLevelChoice.supermax)
        {
            j0 = (int) Math.floor(MathUtil.log2(1.5 * (double) N));
        }
        else
        {
            throw new ConditionalException(
                    " chooseNthlevel : Invalid choices found. Valid ones are \"conservative\" , \"max\" and \"supermax\".");
        }
        return j0;
    }

    public void transform()
    {
        String wtfname = filter.getName();// wtf_s.Name;
        // System.out.println("wtfname = "+wtfname);
        Matrix gt = filter.getG();// wtf_s.g;
        Matrix ht = filter.getH();// wtf_s.h;

        // gt.transpose().mergeH(ht.transpose()).printInLabel("gt-ht");

        NX = X.getRowDimension();
        NChan = X.getColumnDimension();

        J0 = 0;
        if (nthLevel instanceof NthLevelChoice)
        {
            J0 = chooseNthlevel((NthLevelChoice) nthLevel, NX);
        }
        else if (nthLevel instanceof Integer)
        {
            if (((Integer) nthLevel).intValue() > 0)
            {
                J0 = ((Integer) nthLevel).intValue();
            }
            else
            {
                throw new ConditionalException(
                        " transform : Parameter 'nthLevel' must be an \"Integer\" with value greater than zero.");
            }
        }
        else
        {
            throw new ConditionalException(
                    " transform : Parameter 'nthLevel' must be an instanceof \"Integer\" or \"NthLevelChoice\".");
        }

        if (J0 < 0)
        {
            throw new ConditionalException(" transform : J0 must be greater than zero.");
        }

        if (Math.pow(2.0, (double) J0) > (double) NX)
        {
            System.out.println(" transform : WARNING - JO > log2(Number of samples)");
        }

        Matrix Xin = null;
        if (boundary == Boundary.reflection)
        {
            Xin = X.mergeV(X.flipdim(Dimension.ROW));
        }
        else if (boundary == Boundary.circular)
        {
            Xin = X;
        }
        else
        {
            throw new ConditionalException(" transform : Unknown boundary condition.");
        }

        NW = Xin.getRowDimension();

        WJt = new Matrix[NChan];// NaN([NW, j0, NChan], 'double');
        for (int i = 0; i < NChan; i++)
        {
            WJt[i] = new Matrix(NW, J0);
        }

        VJt = new Matrix[NChan];
        for (int i = 0; i < NChan; i++)
        {
            if (retainVJ)
            {
                VJt[i] = new Matrix(NW, J0);
            }
            else
            {
                VJt[i] = new Matrix(NW, 1);
            }
        }

        for (int i = 0; i < NChan; i++)
        {
            Matrix Vin = Xin.getColumnAt(i);
            Matrix Vout = null;
            for (int j = 1; j <= J0; j++)
            {
                Matrix[] mod = modwtjm(Vin, ht, gt, j);
                Matrix Wt_j = mod[0];
                Vout = mod[1];
                WJt[i].setColumnAt(j - 1, Wt_j);// WJt(:,j,i) = Wt_j;
                Vin = Vout;
                // Matrix Wt_j_Vout = Wt_j.mergeH(Vout);
                // //Wt_j_Vout.printInLabel("Wt_j_Vout");
                if (retainVJ)
                {
                    VJt[i].setColumnAt(j - 1, Vout);// VJt(:,j,i) = Vout;
                }
            }// end inner for
            if (!retainVJ)
            {
                VJt[i].setColumnAt(0, Vout);// VJt(:,1,i) = Vout;
            }
        }// end outer for

        hasTransformed = true;

    }// end method

    public boolean isTransformed()
    {
        return hasTransformed;
    }

    private Matrix[] modwtjm(Matrix Vtin, Matrix ht, Matrix gt, int j)
    {
        int N = Vtin.numel();
        int L = ht.numel();

        Matrix Wtout = new Matrix(N, 1, Double.NaN);
        Matrix Vtout = new Matrix(N, 1, Double.NaN);

        int k, n, t;
        double val = 0.0;

        for (t = 0; t < N; t++)
        {
            k = t;
            val = ht.start() * Vtin.getElementAt(k);
            Wtout.set(t, 0, val);
            val = gt.start() * Vtin.getElementAt(k);
            Vtout.set(t, 0, val);

            for (n = 1; n < L; n++)
            {
                // k = k - 2^(j-1);
                k -= (int) Math.pow(2.0, (double) j - 1.0);
                if (k < 0)
                {
                    k = k + N;
                }
                val = Wtout.get(t, 0) + ht.get(0, n) * Vtin.getElementAt(k);
                Wtout.set(t, 0, val);
                val = Vtout.get(t, 0) + gt.get(0, n) * Vtin.getElementAt(k);
                Vtout.set(t, 0, val);
            }// end inner for
        }// end outer for

        /*
         * for (t = 0; t < N; t++) { k = t; val = ht.get(0,0) *
         * Vtin.getElementAt(k); Wout.setElementAt(t, val); val = gt.get(0,0) *
         * Vtin.getElementAt(k); Vout.setElementAt(t, val); for (n = 1; n < L;
         * n++) { k -= (int) Math.pow(2.0, (double)j - 1.0); if (k < 0) { k +=
         * N; } val = Wout.getElementAt(t) + ht.get(0,n) * Vtin.getElementAt(k);
         * Wout.setElementAt(t, val); val = Vout.getElementAt(t) + gt.get(0,n) *
         * Vtin.getElementAt(k); Vout.setElementAt(t, val); } }
         */

        return new Matrix[]
        {
                Wtout, Vtout
        };
    }

    public Matrix[] getWJt()
    {
        if (WJt == null)
        {
            System.err.println("getWJt :  Warning, the method \"transform\" must be called first.");
        }
        return WJt;
    }

    public Matrix[] getVJt()
    {
        if (VJt == null)
        {
            System.err.println("getVJt :  Warning, the method \"transform\" must be called first.");
        }
        return VJt;
    }

    public WaveletFilter getFilter()
    {
        return this.filter;
    }

    public int getNX()
    {
        return NX;
    }

    public int getNW()
    {
        return NW;
    }

    public int getJ0()
    {
        return J0;
    }

    public int getNChan()
    {
        return NChan;
    }

    public boolean isAligned()
    {
        return aligned;
    }

    public Boundary getBoundary()
    {
        return this.boundary;
    }

    public boolean isRetainVJ()
    {
        return retainVJ;
    }

    public static void main(String[] args)
    {
        double[] a =
        {
                14, 7, 12, 2, 6, 14, 12, 14, 10, 1, 13, 14, 10, 11, 11,

                6, 10, 3, 11, 0, 4, 1
        };

        Matrix x = new Matrix(a);

        MoDwt Mo = new MoDwt(x);
        Mo.transform();

        int len = 0;

        Matrix[] W = Mo.getWJt();
        len = W.length;
        // for(int i=0; i<len; i++){
        // / W[i].printInLabel("W["+i+"]");
        // }

        Matrix[] V = Mo.getVJt();
        // len = V.length;
        for (int i = 0; i < len; i++)
        {
            Matrix mat = W[i].mergeH(V[i]);
            mat.printInLabel("W[" + i + "]-V[" + i + "]");
        }

    }

}// --------------------------- End Class Definition
// ----------------------------
