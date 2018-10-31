/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.polyfun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;

/**
 * 
 * @author Sione
 */
public class PolyVal
{

    private Matrix y;
    private Matrix delta;
    private Matrix p; // coefficients

    public PolyVal(Matrix pCoeff, Object x)
    {
        this(pCoeff, x, (Object[]) null);
    }

    public PolyVal(Matrix pCoeff, Object x, Object... Smu)
    {
        if (pCoeff == null)
        {
            throw new IllegalArgumentException("PolyVal : Parameter \"pCoeff\" must be non-null.");
        }
        if (!pCoeff.isVector())
        {
            throw new IllegalArgumentException("PolyVal : Parameter \"pCoeff\" must be a vector and not a matrix.");
        }
        this.p = pCoeff;
        int nc = p.length();

        if (x == null)
        {
            throw new IllegalArgumentException("PolyVal : Parameter \"x\" must be non-null.");
        }
        if (!(x instanceof Double) && !(x instanceof Matrix))
        {
            throw new IllegalArgumentException(
                    "PolyVal : Parameter \"x\" must either an instance of \"Double\" or \"Matrix\".");
        }

        if (Smu != null)
        {
            if (Smu.length > 2)
            {
                throw new IllegalArgumentException(
                        "PolyVal : Length of array parameter \"Smu\" must not be greater than 2 ; ");
            }
            Object mu = Smu[1];
            if (mu != null)
            {
                if (!(mu instanceof double[]) && !(mu instanceof Matrix))
                {
                    throw new IllegalArgumentException(
                            "PolyVal : Parameter \"mu\" must either be an instance of \"double[]\" or \"Matrix\".");
                }
                if ((mu instanceof double[]) && (((double[]) mu).length != 2))
                {
                    throw new IllegalArgumentException("PolyVal : Parameter \"mu\" must have a length of 2 ; ");
                }
                else
                {
                    if (!((Matrix) mu).isVector())
                    {
                        throw new IllegalArgumentException(
                                "PolyVal : Parameter \"mu\" must be a vector and not a matrix.");
                    }
                    if (((Matrix) mu).length() != 2)
                    {
                        throw new IllegalArgumentException("PolyVal : Parameter \"mu\" must have a length of 2 ; ");
                    }
                }
            }
        }

        boolean xFinite = (x instanceof Double) && !Double.isInfinite(((Double) x).doubleValue())
                && !Double.isNaN(((Double) x).doubleValue());

        if ((x instanceof Double) && (Smu == null || Smu.length == 0) && (nc > 0) && xFinite)
        {// && (nargin < 3) && nc>0 && isfinite(x)
         // Make it scream for scalar x. Polynomial evaluation can be
         // implemented as a recursive digital filter.
         // y = filter(1,[1 -x],p);
            this.y = JDatafun.filter(new Matrix(new double[]
            {
                1.0
            }), new Matrix(new double[]
            {
                    1.0, -((Double) x).doubleValue()
            }), this.p);
            int[] ncInd = Indices.linspace(0, nc - 1).getRowPackedCopy();
            this.y = this.y.getEls(ncInd);// y(nc);
            return;
        }

        int[] siz_x = null;
        if (x instanceof Double)
        {
            siz_x = new int[]
            {
                    1, 1
            };
        }
        else
        {
            siz_x = ((Matrix) x).sizeIntArr();
        }

        Matrix XX = null;
        if (x instanceof Double)
        {
            XX = new Matrix(1, 1, ((Double) x).doubleValue());
        }
        else
        {
            XX = (Matrix) x;
        }

        if (Smu != null && Smu.length == 2)
        {// nargin == 4
            double[] mu = null;
            if (Smu[1] instanceof double[])
            {
                mu = (double[]) Smu[1];
            }
            else
            {
                mu = ((Matrix) Smu[1]).getRowPackedCopy();
            }
            // x = (x - mu(1))/mu(2);
            XX = XX.minus(mu[0]).arrayRightDivide(mu[1]);
        }// end

        // Use Horner's method for general case where X is an array.
        y = Matrix.zeros(siz_x);// zeros(siz_x, superiorfloat(x,p));
        if (nc > 0)
        {
            // y(:) = p(1);
            y = new Matrix(siz_x[0], siz_x[1], p.start());
        }
        for (int i = 1; i < nc; i++)
        {
            // y = x .* y + p(i);
            y = XX.arrayTimes(y).plus(p.getElementAt(i));
        }// end

    }

    /**
     * @return the y
     */
    public Matrix getY()
    {
        return y;
    }

    /**
     * @return the delta
     */
    public Matrix getDelta()
    {
        if (true)
        {
            throw new IllegalArgumentException(
                    "getDelta : This method is not yet implemented (See matlab file \"polyval\").");
        }
        return delta;
    }
}
