/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.control;

import jamaextension.jamax.Matrix;

/**
 * 
 * @author Feynman Perceptrons
 */
public class LtiResponseKernel
{

    private Matrix systemTimeResponse;

    public LtiResponseKernel(Matrix a, Matrix b, Matrix u)
    {
        this(a, b, u, null);
    }

    public LtiResponseKernel(Matrix a, Matrix b, Matrix u, Matrix x0)
    {
        if (a == null)
        {
            throw new IllegalArgumentException("LtiResponseKernel : Parameter \"a\" must be non-null.");
        }
        if (!a.isSquare())
        {
            throw new IllegalArgumentException("LtiResponseKernel : Parameter \"a\" must be a square matrix.");
        }
        if (a.isVector())
        {
            throw new IllegalArgumentException(
                    "LtiResponseKernel : Parameter \"a\" must be a square matrix and not a vector.");
        }

        if (b == null)
        {
            throw new IllegalArgumentException("LtiResponseKernel : Parameter \"b\" must be non-null.");
        }
        /*
         * if(!b.isSquare()){ throw new IllegalArgumentException(
         * "LtiResponseKernel : Parameter \"a\" must be a square matrix."); }
         * if(b.isVector()){ throw new IllegalArgumentException(
         * "LtiResponseKernel : Parameter \"a\" must be a square matrix and not a vector."
         * ); }
         */

        if (u == null)
        {
            throw new IllegalArgumentException("LtiResponseKernel : Parameter \"u\" must be non-null.");
        }
        /*
         * if(!u.isSquare()){ throw new IllegalArgumentException(
         * "LtiResponseKernel : Parameter \"a\" must be a square matrix."); }
         * if(u.isVector()){ throw new IllegalArgumentException(
         * "LtiResponseKernel : Parameter \"a\" must be a square matrix and not a vector."
         * ); }
         */

        if (a.getRowDimension() != b.getRowDimension())
        {
            throw new IllegalArgumentException(
                    "LtiResponseKernel : Number of rows in \"b\" must equal the size (either row or col) of square matrix \"a\".");
        }

        if (b.getColumnDimension() != u.getColumnDimension())
        {
            throw new IllegalArgumentException(
                    "LtiResponseKernel : Number of columns in \"b\" must equal the number of columns in \"u\".");
        }

        int m = a.getRowDimension();
        int n = u.getRowDimension();
        Matrix x = Matrix.zeros(m, n);

        if (x0 != null)
        {
            if (!x0.isVector())
            {
                throw new IllegalArgumentException(
                        "LtiResponseKernel : Parameter \"x0\" must be a vector and not a matrix.");
            }
            if (x0.length() != m)
            {
                throw new IllegalArgumentException(
                        "LtiResponseKernel : The length of vector \"x0\" must equal the size (either row or col) of square matrix \"a\".");
            }
            if (x0.isRowVector())
            {
                x0 = x0.toColVector();
            }
        }
        else
        {
            x0 = Matrix.zeros(m, 1);
        }

        for (int i = 0; i < n; i++)
        {
            // x(:,i) = x0;
            x.setColumnAt(i, x0);
            // x0 = a * x0 + b * u(i,:).';
            x0 = a.times(x0).plus(b.times(u.getRowAt(i).transpose()));
        }

        x = x.transpose();
        systemTimeResponse = x;
    }

    public Matrix getSystemTimeResponse()
    {
        return systemTimeResponse;
    }
}// ------------------------------ End Class Definition
// -------------------------

