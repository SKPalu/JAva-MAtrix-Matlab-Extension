/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun.filtering.denoising;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;

/**
 * 
 * @author Sione
 */
public class FlatFacetFilter
{

    private Matrix sigOut;

    public FlatFacetFilter(Matrix sig_in, int p, double k)
    {
        if (!sig_in.isVector())
        {
            throw new ConditionalException("FlatFacetFilter : Parameter \"sigIn\" must be a vector and not a matrix.");
        }
        boolean transp = false;
        if (sig_in.isRowVector())
        {
            sig_in = sig_in.toColVector();
            transp = true;
        }
        int L = sig_in.length();// .getRowDimension();
        int p2 = (p - 1) / 2;
        Indices n = Indices.linspace(-p2, p2);// [-p2 : 1 : p2];
        Matrix sig_out = Matrix.zeros(L, 1);

        for (int i = 0; i < (L - p - 1); i++)
        {
            double xm = sig_in.getElementAt(i + p2);
            int[] ind = Indices.linspace(i, i + p2 - 1).getRowPackedCopy();
            Matrix xl = sig_in.getElements(ind);// (i:i+p2-1);
            ind = Indices.linspace(i + p - p2, i + p - 1).getRowPackedCopy();
            Matrix xr = sig_in.getElements(ind);// (i+p-p2:i+p-1);
            Matrix xt = xl.transpose().mergeH(xr.transpose()).transpose();// [xl'
                                                                          // xr']';
            ind = Indices.linspace(i, i + p - 1).getRowPackedCopy();
            Matrix N = Matrix.indicesToMatrix(n);
            double a = N.times(sig_in.getElements(ind)).arrayRightDivide(N.times(N.transpose()).start()).start();// (n*sig_in(i:i+p-1))
                                                                                                                 // /
                                                                                                                 // (n*n');
            double g = JDatafun.mean(xt).start();
            Matrix est = N.arrayTimes(a).plus(g);// (a*n + g);
            double val = (est.getElementAt(p2 + 1) - xm);
            val = val * val;
            ind = Indices.linspace(i, i + p - 1).getRowPackedCopy();
            Matrix var = est.times(sig_in.getElements(ind)).minus(val);// est*sig_in(i:i+p-1)
                                                                       // -
                                                                       // (est(p2+1)-xm)^2;
            val = xm - g;
            val = val * val;
            boolean cond = val > k * k * var.start();
            if (cond)
            {// (xm - g)^2 > k*k*var;
             // sig_out(i+p2) = g;
                sig_out.setElementAt(i + p2, g);
            }
            else
            {
                // sig_out(i+p2) = xm;
                sig_out.setElementAt(i + p2, xm);
            }// end; % if
        }// end; % i loop
        this.sigOut = sig_out;
    }

    /**
     * @return the sigOut
     */
    public Matrix getSigOut()
    {
        return sigOut;
    }

    public static void main(String[] args)
    {
        double[] kk =
        {
                81, 91, 13, 91, 63, 10, 28, 55, 96, 96, 16, 97, 96, 49, 80, 14
        };
        Matrix SI = new Matrix(kk);
        SI.printInLabel("SI");
        FlatFacetFilter FFF = new FlatFacetFilter(SI, 5, 0.1);
        Matrix SO = FFF.getSigOut();
        SO.toRowVector().printInLabel("SO");
    }
}
