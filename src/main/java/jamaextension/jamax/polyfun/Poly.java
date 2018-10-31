/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.polyfun;

import jamaextension.jamax.ComplexMatrix;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortMat;

/**
 * 
 * @author Feynman Perceptrons
 */
public class Poly
{

    private ComplexMatrix polynomial;
    private boolean real = false;

    public Poly(Roots roots)
    {
        if (roots == null)
        {
            throw new IllegalArgumentException("RootsToPoly : Parameter \"roots\" must be non-null.");
        }
        if (roots.getRoots() != null)
        {
            polynomial(roots.getRoots());
        }
    }

    public Poly(ComplexMatrix cplxRoots)
    {
        if (cplxRoots == null)
        {
            throw new IllegalArgumentException("RootsToPoly : Parameter \"cplxRoots\" must be non-null.");
        }
        polynomial(cplxRoots);
    }

    public ComplexMatrix getPolynomial()
    {
        return polynomial;
    }

    public Matrix getRealPolynomial()
    {
        return polynomial.re();
    }

    public boolean isReal()
    {
        return real;
    }

    private void polynomial(ComplexMatrix roots)
    {
        if (roots == null)
        {
            throw new IllegalArgumentException("poly : Parameter \"roots\" must be non-null.");
        }
        if (!roots.isVector())
        {
            throw new IllegalArgumentException("poly : Parameter \"roots\" must be a vector and not a matrix.");
        }

        ComplexMatrix cplxRoots = null;
        if (roots.isRowVector())
        {
            cplxRoots = roots.toColVector();
        }
        else
        {
            cplxRoots = roots;
        }

        Indices finite = cplxRoots.isfinite().find();
        if (finite != null)
        {
            int[] arr = finite.getColIndicesAt(0);
            cplxRoots = cplxRoots.getElements(arr);
        }
        ComplexMatrix e = cplxRoots;
        int n = e.length();

        // Expand recursion formula
        Matrix mat = Matrix.ones(1).mergeH(Matrix.zeros(1, n));// [1
                                                               // zeros(1,n,class(x))];
        ComplexMatrix c = new ComplexMatrix(mat, 0.0);

        int[] arr = null;
        int[] arr2 = null;

        for (int j = 1; j <= n; j++)
        {
            arr = Indices.linspace(1, j).getRowIndicesAt(0);
            arr2 = Indices.linspace(0, j - 1).getRowIndicesAt(0);
            ComplexMatrix R = c.getMatrix(0, arr2).arrayTimes(e.getElementAt(j - 1));
            R = c.getMatrix(0, arr).minus(R);// c(2:(j+1)) = c(2:(j+1)) -
                                             // e(j).*c(1:j);
            c.setMatrix(0, arr, R);
        }
        this.polynomial = c;

        // -------------------------------------
        // System.out.println("------ c -----");
        // c.re().transpose().mergeH(c.im().transpose()).print(8, 8);
        // -------------------------------------

        // The result should be real if the roots are complex conjugates.
        Matrix ImE = e.im();
        Indices eGtZero = ImE.GT(0.0).find(); // sort(e(imag(e)>0)),
        Indices eLtZero = ImE.LT(0.0).find(); // sort(conj(e(imag(e)<0)))

        boolean cond = (eGtZero != null && eLtZero != null) && (eGtZero.getRowDimension() == eLtZero.getRowDimension());
        if (cond)
        {
            ComplexMatrix E1 = e.getFromFind(eGtZero);
            mat = E1.abs();
            QuickSort sort = new QuickSortMat(mat, true, false);
            arr = sort.getIndices().getRowPackedCopy();
            E1 = E1.getElements(arr);
            // System.out.println("------ E1 -----");
            // E1.re().mergeH(E1.im()).print(8, 8);

            ComplexMatrix E2 = e.getFromFind(eLtZero).conjugate();
            mat = E2.abs();
            sort = new QuickSortMat(mat, true, false);
            arr = sort.getIndices().getRowPackedCopy();
            E2 = E2.getElements(arr);
            // System.out.println("------ E2 -----");
            // E2.re().mergeH(E2.im()).print(8, 8);

            if (E1.equals(E2))
            {
                this.polynomial = new ComplexMatrix(c.re(), 0.0);
                real = true;
            }
        }

    }

    public static void main(String[] args)
    {
        double[] v =
        {
                2, 5, 4, 6, 7, 8
        };
        Matrix mat = new Matrix(v);
        Roots R = new Roots(mat);
        System.out.println("------ R -----");
        R.getRoots().re().mergeH(R.getRoots().im()).print(8, 4);

        Poly poly = new Poly(R);
        if (poly.isReal())
        {
            System.out.println("------ real poly -----");
            poly.getRealPolynomial().print(8, 4);
        }
        else
        {
            System.out.println("------ complex poly -----");
        }

    }
}// /////////////////////////// End Class Definition ///////////////////////////

