/*
 * Minimum.java
 *
 * Created on 8 November 2007, 14:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.constants.SortingMode;

/**
 * 
 * @author Feynman Perceptrons
 */
final class Minimum
{

    /** Creates a new instance of Minimum */
    private Minimum()
    {
    }

    protected static Matrix min(Matrix A)
    {
        return min(A, null);
    }

    /**
     * Creates a new instance of MinMat
     */
    protected static Matrix min(Matrix A, Dimension dim)
    {
        Matrix minObject = null;
        if (dim == null)
        {
            if (A.isVector())
            {
                minObject = minMatrixVector(A);
            }
            else
            {
                minObject = minMatrix(A, Dimension.ROW);
            }
        }
        else
        {
            minObject = minMatrix(A, dim);
        }
        return minObject;
    }

    private static Matrix minMatrix(Matrix A, Dimension dim)
    {
        Matrix minObject = null;
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {
                    minObject = A.copy();
                }
                else
                {
                    minObject = minMatrixVector(A);
                }
            }
            else
            {// column vector
                if (dim == Dimension.COL)
                {
                    minObject = A.copy();
                }
                else
                {
                    minObject = minMatrixVector(A);
                }
            }
        }
        else
        {
            minObject = minMatrixAll(A, dim);
        }
        return minObject;
    }

    private static Matrix minMatrixAll(Matrix A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
        double t = 0.0;
        double val = 0.0;

        Matrix minObject = null;

        switch (dim)
        {
        case ROW:
            minObject = new Matrix(1, cols);

            for (int j = 0; j < cols; j++)
            {
                t = A.get(0, j);
                for (int i = 1; i < rows; i++)
                {
                    val = A.get(i, j);
                    if (!MathUtil.compareDoubles(t, val, SortingMode.DESCENDING))
                    { // ===> New line
                      // if(t<=val) {
                      // t = t;
                      // }
                      // else {
                        t = val;
                    }
                }
                minObject.set(0, j, t);
            }
            break;
        case COL:
            minObject = new Matrix(rows, 1);

            for (int i = 0; i < rows; i++)
            {
                t = A.get(i, 0);
                for (int j = 1; j < cols; j++)
                {
                    val = A.get(i, j);
                    if (!MathUtil.compareDoubles(t, val, SortingMode.DESCENDING))
                    { // ===> New line
                      // if(t<=val) {
                      // t = t;
                      // }
                      // else {
                        t = val;
                    }
                }
                minObject.set(i, 0, t);
            }
            break;
        default:
            throw new IllegalArgumentException("minMatrixAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch

        return minObject;
    }

    private static Matrix minMatrixVector(Matrix A)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(" minVector :  Parameter \"A\" must be a vector and not a matrix.");
        }
        double t = 0.0;
        double val = 0.0;

        Matrix minObject = new Matrix(1, 1);

        int c = A.length();
        if (c == 1)
        {// if only one element , then return.
            minObject.set(0, 0, A.get(0, 0));
            return minObject;
        }

        t = A.get(0, 0);

        if (A.isRowVector())
        {
            for (int j = 1; j < c; j++)
            {
                val = A.get(0, j);
                if (!MathUtil.compareDoubles(t, val, SortingMode.DESCENDING))
                { // ===> New line
                  // if(t<=val) {
                  // t = t;
                  // }
                  // else{
                    t = val;
                }
            }
        }
        else
        {
            for (int i = 1; i < c; i++)
            {
                val = A.get(i, 0);
                if (!MathUtil.compareDoubles(t, val, SortingMode.DESCENDING))
                { // ===> New line
                  // if(t<=val) {
                  // t = t;
                  // }
                  // else{
                    t = val;
                }
            }
        }

        minObject.set(0, 0, t);

        return minObject;
    }

    // //////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////

    protected static Indices min(Indices A)
    {
        return min(A, null);
    }

    /**
     * Creates a new instance of MinMat
     */
    protected static Indices min(Indices A, Dimension dim)
    {
        Indices minObject = null;
        if (dim == null)
        {
            if (A.isVector())
            {
                minObject = minIndicesVector(A);
            }
            else
            {
                minObject = minIndices(A, Dimension.ROW);
            }
        }
        else
        {
            minObject = minIndices(A, dim);
        }
        return minObject;
    }

    private static Indices minIndices(Indices A, Dimension dim)
    {
        Indices minObject = null;
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {
                    minObject = A.copy();
                } // return the same
                else
                {
                    minObject = minIndicesVector(A);
                }
            }
            else
            {// column vector
                if (dim == Dimension.COL)
                {
                    minObject = A.copy();
                }
                else
                {
                    minObject = minIndicesVector(A);
                }
            }
        }
        else
        {
            minObject = minIndicesAll(A, dim);
        }
        return minObject;
    }

    private static Indices minIndicesAll(Indices A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
        int t = 0;
        int val = 0;

        Indices minObject = null;

        switch (dim)
        {
        case ROW:
            minObject = new Indices(1, cols);

            for (int j = 0; j < cols; j++)
            {
                t = A.get(0, j);
                for (int i = 1; i < rows; i++)
                {
                    val = A.get(i, j);
                    if (t <= val)
                    {
                        t = t;
                    }
                    else
                    {
                        t = val;
                    }
                }
                minObject.set(0, j, t);

            }
            break;
        case COL:
            minObject = new Indices(rows, 1);

            for (int i = 0; i < rows; i++)
            {
                t = A.get(i, 0);
                for (int j = 1; j < cols; j++)
                {
                    val = A.get(i, j);
                    if (t <= val)
                    {
                        t = t;
                    }
                    else
                    {
                        t = val;
                    }
                }
                minObject.set(i, 0, t);

            }
            break;
        default:
            throw new IllegalArgumentException("minIndicesAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch

        return minObject;
    }

    private static Indices minIndicesVector(Indices A)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(
                    " minIndicesVector :  Parameter \"A\" must be a vector and not a matrix.");
        }
        int t = 0;
        int val = 0;
        Indices minObject = new Indices(1, 1);

        int c = A.length();
        if (c == 1)
        {// if only one element , then return.
            minObject.set(0, 0, A.get(0, 0));
            return minObject;
        }

        t = A.get(0, 0);

        if (A.isRowVector())
        {
            for (int j = 1; j < c; j++)
            {
                val = A.get(0, j);
                if (t <= val)
                {
                    t = t;
                }
                else
                {
                    t = val;
                }
            }
        }
        else
        {
            for (int i = 1; i < c; i++)
            {
                val = A.get(i, 0);
                if (t <= val)
                {
                    t = t;
                }
                else
                {
                    t = val;
                }
            }
        }

        minObject.set(0, 0, t);

        return minObject;
    }

    public static void main(String[] args)
    {
        double[][] a =
        {
                {
                        14, 13, 12, Double.NaN, 14
                },
                {
                        3, Double.NaN, 7, 11, 14
                },
                {
                        9, 7, 9, 3, 6
                },
                {
                        7, 0, 12, 6, 13
                }
        };

        Matrix A = new Matrix(a);

        Matrix min = min(A, Dimension.COL);

        System.out.println("----- min -----");
        min.print(4, 0);

        /*
         * Indices A = SampleMatrices.generateIndices(); Indices row =
         * A.getRowAt(2); Indices col = A.getColumnAt(4); MinInd M = new
         * MinInd(col, Dimension.COL); Indices Mr = (Indices)M.getMinObject();
         * System.out.println("----- Mr -----"); Mr.print(4,0); Indices Ir =
         * M.getIndices(); System.out.println("----- Ir -----");
         * Ir.plus(1).print(4,0);
         */
    }

}// ////////////////////////// End Class Definition
// /////////////////////////////
