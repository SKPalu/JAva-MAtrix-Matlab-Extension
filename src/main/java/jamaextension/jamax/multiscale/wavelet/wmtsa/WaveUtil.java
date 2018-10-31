/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.multiscale.wavelet.wmtsa;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;

//import com.feynmanperceptron.jnumeric.jamax.*; 
//import com.feynmanperceptron.jnumeric.jamax.datafun.*;

/**
 * 
 * @author Feynman Perceptrons
 */
public final class WaveUtil
{

    private WaveUtil()
    {
    }

    public static Matrix qmf(Matrix a)
    {
        return qmf(a, false);
    }

    public static Matrix qmf(Matrix a, boolean inverse)
    {

        if (a == null)
        {
            throw new IllegalArgumentException(" qmf : Parameter 'a' must be non-null.");
        }
        if (a.numel() < 2)
        {
            throw new IllegalArgumentException(" qmf : Parameter 'a' must contain at least 2 elements.");
        }

        Matrix b = null;
        if (a.isVector())
        {
            if (a.isRowVector())
            {
                b = a.flipLR();
            }
            else
            {
                b = a.flipUD();
            }
        }
        else
        {
            b = a.flipUD();
        }

        Matrix temp = null;
        int[] arr = null;
        if (inverse)
        {
            if (b.isVector())
            {
                arr = Indices.linspace(0, b.length() - 1, 2).getRowPackedCopy();
            }
            else
            {
                arr = Indices.linspace(0, b.numel() - 1, 2).getRowPackedCopy();
            }
        }
        else
        {
            if (b.isVector())
            {
                arr = Indices.linspace(1, b.length() - 1, 2).getRowPackedCopy();
            }
            else
            {
                arr = Indices.linspace(1, b.numel() - 1, 2).getRowPackedCopy();
            }
        }

        temp = b.getElements(arr).uminus();
        b.setElements(arr, temp);
        // b(first:2:end) = -b(first:2:end);

        return b;
    }

    /*
     * Wavelet threshold (hard & soft) with tolerance 't'
     */
    public static Matrix wthres(Matrix x, WaveletThreshold sorh, double t)
    {
        // wthresh2(x,sorh,t)

        if (sorh == null)
        {
            sorh = WaveletThreshold.SOFT;
        }
        if (x == null || x.isNull())
        {
            throw new ConditionalException("waveletThres : Input matrix argument \"x\" must be a non-null vector.");
        }

        if (!x.isVector())
        {
            throw new ConditionalException(
                    "waveletThres : Input matrix argument \"x\" must be a vector and not a matrix.");
        }

        Matrix y = null;

        if (sorh == WaveletThreshold.SOFT)
        {
            Matrix tmp = x.abs().minus(t);// (abs(x)-t);
            tmp = tmp.abs().plus(tmp).arrayRightDivide(2.0);// (tmp+abs(tmp))/2;
            y = x.sign().arrayTimes(tmp);// sign(x).*tmp;
        }
        else
        {
            y = x.arrayTimes(x.abs().GT(t));// x.*(abs(x)>t);
        }

        return y;
    }

    public static double thselect(Matrix xx, WaveThresSelection tptr)
    {

        if (xx == null || xx.isNull())
        {
            throw new ConditionalException("thselect : Input matrix argument \"xx\" must be a non-null vector.");
        }

        if (!xx.isVector())
        {
            throw new ConditionalException("thselect : Input matrix argument \"xx\" must be a vector and not a matrix.");
        }

        double thr = 0.01;
        Matrix x = xx.toRowVector();// x(:)';
        int n = x.length();// length(x);

        switch (tptr)
        {
        case rigrsure:
        {
            // sx2 = sort(abs(x)).^2;
            // risks = (n-(2*(1:n))+(cumsum(sx2)+(n-1:-1:0).*sx2))/n;
            // [risk,best] = min(risks);
            // thr = sqrt(sx2(best));
            break;
        }
        case heursure:
        {
            double hthr = Math.sqrt(2 * Math.log(n));
            double nmx = x.norm();
            double eta = (nmx * nmx - n) / n;// (norm(x).^2-n)/n;
            double val = (Math.log(n) / Math.log(2));
            double crit = Math.pow(val, 1.5) / Math.sqrt(n);
            if (eta < crit)
            {
                thr = hthr;
            }
            else
            {
                thr = Math.min(thselect(x, WaveThresSelection.rigrsure), hthr);
            }
            break;
        }
        case sqtwolog:
        {
            thr = Math.sqrt(2 * Math.log(n));
            break;
        }
        case minimaxi:
        {
            if (n <= 32)
            {
                thr = 0;
            }
            else
            {
                thr = 0.3936 + 0.1829 * (Math.log(n) / Math.log(2));
            }
            break;
        }
        default:
        {
            throw new ConditionalException("thselect : Wavelet threshold method \"" + tptr
                    + "\" not found or not implemented.");
        }
        }
        return thr;
    }

    public static void main(String[] args)
    {

        double[][] a =
        {
                {
                        8, 3, 10, 8, 7, 7, 7, 8
                },
                {
                        9, 5, 5, 10, 8, 0, 3, 8
                },
                {
                        1, 10, 8, 7, 7, 3, 10, 2
                },
                {
                        9, 10, 1, 0, 4, 0, 0, 5
                },
                {
                        6, 2, 4, 8, 7, 1, 4, 4
                },
                {
                        1, 10, 9, 9, 2, 8, 4, 6
                }
        };

        /*
         * Matrix Q = new Matrix(a);
         * 
         * Q = Q.getColumns(new int[]{0,1}).toColVector().transpose();
         * 
         * Q.printInLabel("Q",0);
         * 
         * Matrix F = qmf(Q); F.printInLabel("F1",0);
         * 
         * F = qmf(Q, true); F.printInLabel("F2",0);
         */

        double[] b =
        {
                -0.0757657147893407, -0.0296355276459541, 0.4976186676324578, 0.8037387518052163, 0.2978577956055422,
                -0.0992195435769354, -0.0126039672622612, 0.0322231006040713
        };

        Matrix g = new Matrix(b);
        g.printInLabel("g");
        Matrix h = qmf(g);
        h.printInLabel("h");

        g.transpose().mergeH(h.transpose()).printInLabel("[g-h]");
    }

}// ------------------------------ End Class Definition
// -------------------------
