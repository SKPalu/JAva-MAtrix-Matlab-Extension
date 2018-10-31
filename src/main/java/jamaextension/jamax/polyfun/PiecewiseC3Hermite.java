/*
 * PiecewiseC3Hermite.java
 *
 * Created on 26 January 2008, 10:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.polyfun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;

/**
 * 
 * @author Feynman Perceptrons
 */
public class PiecewiseC3Hermite
{

    private PiecewisePolynomial PP;

    public PiecewiseC3Hermite(Matrix x, Matrix y, Matrix s)
    {
        this(x, y, s, null);
    }

    public PiecewiseC3Hermite(Matrix x, Matrix y, Matrix s, Matrix dx)
    {
        this(x, y, s, dx, null);
    }

    /**
     * Creates a new instance of PiecewiseC3Hermite
     */
    public PiecewiseC3Hermite(Matrix x, Matrix y, Matrix s, Matrix dx, Matrix divdif)
    {
        if (x == null)
        {
            throw new IllegalArgumentException("PiecewisePolynomial : Parameter \"x\" must be non-null.");
        }
        if (!x.isVector())
        {
            throw new IllegalArgumentException(
                    "PiecewisePolynomial : Parameter \"x\" must be a vector and not a matrix.");
        }
        if (x.isColVector())
        {
            x = x.toRowVector();
        }

        if (y == null)
        {
            throw new IllegalArgumentException("PiecewisePolynomial : Parameter \"y\" must be non-null.");
        }
        if (!y.isVector())
        {
            throw new IllegalArgumentException(
                    "PiecewisePolynomial : Parameter \"y\" must be a vector and not a matrix.");
        }
        if (y.isColVector())
        {
            y = y.toRowVector();
        }

        int n = x.numel();

        if (n != y.length())
        {
            throw new IllegalArgumentException("PiecewisePolynomial : Lengths of \"x\" and \"y\" must be the same.");
        }

        if (s == null)
        {
            throw new IllegalArgumentException("PiecewisePolynomial : Parameter \"s\" must be non-null.");
        }
        if (!s.isVector())
        {
            throw new IllegalArgumentException(
                    "PiecewisePolynomial : Parameter \"y\" must be a vector and not a matrix.");
        }
        if (s.isColVector())
        {
            s = s.toRowVector();
        }

        if (dx == null)
        {
            dx = JDatafun.diff(x);
        }
        else
        {
            if (!dx.isVector())
            {
                throw new IllegalArgumentException(
                        "PiecewisePolynomial : Parameter \"dx\" must be a vector and not a matrix.");
            }
            if (dx.isColVector())
            {
                dx = dx.toRowVector();
            }
        }

        int d = y.getRowDimension();
        Matrix dxd = dx.repmat(d, 1);// repmat(dx,d,1)

        // System.out.println("---------------- dxd ----------------");
        // dxd.print(8, 0);

        if (divdif == null)
        {
            Matrix dify = JDatafun.diff(y);
            divdif = dify.arrayRightDivide(dxd);// diff(y,1,2)./dxd
        }
        else
        {
            if (!divdif.isVector())
            {
                throw new IllegalArgumentException(
                        "PiecewisePolynomial : Parameter \"divdif\" must be a vector and not a matrix.");
            }
            if (divdif.isColVector())
            {
                divdif = divdif.toRowVector();
            }
        }

        int[] arr = Indices.intLinspaceIncrement(0, n - 2).getRowPackedCopy();
        Matrix dzzdx = divdif.minus(s.getElements(arr).toRowVector()); // dzzdx
                                                                       // =
                                                                       // (divdif-s(:,1:n-1))./dxd;
        dzzdx = dzzdx.arrayRightDivide(dxd);

        arr = Indices.intLinspaceIncrement(1, n - 1).getRowPackedCopy();
        Matrix dzdxdx = s.getElements(arr).toRowVector().minus(divdif).arrayRightDivide(dxd); // dzdxdx
                                                                                              // =
                                                                                              // (s(:,2:n)-divdif)./dxd;
        int dnm1 = d * (n - 1);

        Matrix coefs = null;
        Matrix temp1 = dzdxdx.minus(dzzdx).arrayRightDivide(dxd).reshape(dnm1, 1); // reshape(
                                                                                   // (dzdxdx-dzzdx)./dxd,dnm1,1)
        Matrix temp2 = dzzdx.arrayTimes(2.0).minus(dzdxdx).reshape(dnm1, 1);// reshape(2*dzzdx-dzdxdx,dnm1,1)
        arr = Indices.intLinspaceIncrement(0, n - 2).getRowPackedCopy();
        Matrix temp3 = s.getElements(arr).reshape(dnm1, 1);// reshape(s(:,1:n-1),dnm1,1)
        Matrix temp4 = y.getElements(arr).reshape(dnm1, 1);// reshape(y(:,1:n-1),dnm1,1)
        coefs = temp1.mergeH(temp2).mergeH(temp3).mergeH(temp4);

        PP = new PiecewisePolynomial(x, coefs, new Indices(1, 1, d));
    }// end constructor

    public PiecewisePolynomial getPP()
    {
        return this.PP;
    }

    public double evaluate(double xx)
    {
        if (PP == null)
        {
            throw new IllegalArgumentException("evaluate : Object \"PP\" is null.");
        }
        return PP.evaluate(xx);
    }

    public Matrix evaluate(Matrix A)
    {
        if (PP == null)
        {
            throw new IllegalArgumentException("evaluate : Object \"PP\" is null.");
        }
        return PP.evaluate(A);
    }
}// ------------------------ End Class Definition
// -------------------------------

