/*
 * CumsumMat.java
 *
 * Created on 8 November 2007, 22:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.datafun;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Feynman Perceptrons
 */
public final class CumsumMat
{

    private Matrix cumsum;

    public CumsumMat(Matrix A)
    {
        this(A, null);
    }

    /**
     * Creates a new instance of SumMat
     */
    public CumsumMat(Matrix A, Dimension dim)
    {
        if (dim == null)
        {
            if (A.isVector())
            {
                cumsumMatrixVector(A);
            }
            else
            {
                cumsumMatrix(A, Dimension.ROW);
            }
        }
        else
        {
            cumsumMatrix(A, dim);
        }
    }

    private void cumsumMatrix(Matrix A, Dimension dim)
    {
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {
                    cumsum = A.copy();
                }
                else
                {
                    cumsumMatrixVector(A);
                }
            }
            else
            {// column vector
                if (dim == Dimension.COL)
                {
                    cumsum = A.copy();
                }
                else
                {
                    cumsumMatrixVector(A);
                }
            }
        }
        else
        {
            cumsumMatrixAll(A, dim);
        }
    }

    private void cumsumMatrixAll(Matrix A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
        double t = 0.0;
        cumsum = A.copy();// new Matrix(rows,cols);

        switch (dim)
        {
        case ROW:
            for (int j = 0; j < cols; j++)
            {
                for (int i = 1; i < rows; i++)
                {
                    t = A.get(i, j) + cumsum.get(i - 1, j);
                    cumsum.set(i, j, t);
                }
            }
            break;
        case COL:
            for (int i = 0; i < rows; i++)
            {
                for (int j = 1; j < cols; j++)
                {
                    t = A.get(i, j) + cumsum.get(i, j - 1);
                    cumsum.set(i, j, t);
                }
            }
            break;
        default:
            throw new IllegalArgumentException("cumsumMatrixAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
    }

    private void cumsumMatrixVector(Matrix A)
    {
        if (!A.isVector())
        {
            throw new IllegalArgumentException(" cumsumVector :  Parameter \"A\" must be a vector and not a matrix.");
        }

        int c = A.length();
        if (c == 1)
        {// if only one element , return this element.
            cumsum = new Matrix(1, 1, A.get(0, 0));
            return;
        }

        cumsum = new Matrix(A.getRowDimension(), A.getColumnDimension());
        double temp = A.get(0, 0);
        cumsum.set(0, 0, temp); // set the first element

        if (A.isRowVector())
        {
            for (int j = 1; j < c; j++)
            {
                temp = A.get(0, j) + cumsum.get(0, j - 1);
                cumsum.set(0, j, temp);
            }
        }
        else
        {
            for (int j = 1; j < c; j++)
            {
                temp = A.get(j, 0) + cumsum.get(j - 1, 0);
                cumsum.set(j, 0, temp);
            }
        }

    }

    public Matrix getCumsum()
    {
        return this.cumsum;
    }

    public static void main(String[] args)
    {
        Matrix A = SampleMatrices.generateMatrix();
        Matrix row = A.getRowAt(2);
        Matrix col = A.getColumnAt(4);
        CumsumMat M = new CumsumMat(col, Dimension.COL);

        System.out.println("----- M -----");
        M.getCumsum().print(4, 0);
    }

}
