/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.datafun;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Sione
 */
public final class CumprodMat
{

    private Matrix cumprod;

    public CumprodMat(Matrix A)
    {
        this(A, null);
    }

    /**
     * Creates a new instance of SumMat
     */
    public CumprodMat(Matrix A, Dimension dim)
    {
        if (dim == null)
        {
            if (A.isVector())
            {
                cumprodMatrixVector(A);
            }
            else
            {
                cumprodMatrix(A, Dimension.ROW);
            }
        }
        else
        {
            cumprodMatrix(A, dim);
        }
    }

    private void cumprodMatrix(Matrix A, Dimension dim)
    {
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {
                    cumprod = A.copy();
                }
                else
                {
                    cumprodMatrixVector(A);
                }
            }
            else
            {// column vector
                if (dim == Dimension.COL)
                {
                    cumprod = A.copy();
                }
                else
                {
                    cumprodMatrixVector(A);
                }
            }
        }
        else
        {
            cumprodMatrixAll(A, dim);
        }
    }

    private void cumprodMatrixAll(Matrix A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
        double t = 0.0;
        cumprod = A.copy();// new Matrix(rows,cols);

        switch (dim)
        {
        case ROW:
            for (int j = 0; j < cols; j++)
            {
                for (int i = 1; i < rows; i++)
                {
                    t = A.get(i, j) * cumprod.get(i - 1, j);
                    cumprod.set(i, j, t);
                }
            }
            break;
        case COL:
            for (int i = 0; i < rows; i++)
            {
                for (int j = 1; j < cols; j++)
                {
                    t = A.get(i, j) * cumprod.get(i, j - 1);
                    cumprod.set(i, j, t);
                }
            }
            break;
        default:
            throw new IllegalArgumentException("cumprodMatrixAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
    }

    private void cumprodMatrixVector(Matrix A)
    {
        if (!A.isVector())
        {
            throw new IllegalArgumentException(" cumprodVector :  Parameter \"A\" must be a vector and not a matrix.");
        }

        int c = A.length();
        if (c == 1)
        {// if only one element , return this element.
            cumprod = new Matrix(1, 1, A.get(0, 0));
            return;
        }

        cumprod = new Matrix(A.getRowDimension(), A.getColumnDimension());
        double temp = A.get(0, 0);
        cumprod.set(0, 0, temp); // set the first element

        if (A.isRowVector())
        {
            for (int j = 1; j < c; j++)
            {
                temp = A.get(0, j) * cumprod.get(0, j - 1);
                cumprod.set(0, j, temp);
            }
        }
        else
        {
            for (int j = 1; j < c; j++)
            {
                temp = A.get(j, 0) * cumprod.get(j - 1, 0);
                cumprod.set(j, 0, temp);
            }
        }

    }

    public Matrix getCumprod()
    {
        return this.cumprod;
    }

    public static void main(String[] args)
    {
        Matrix A = SampleMatrices.generateMatrix();
        Matrix row = A.getRowAt(2);
        Matrix col = A.getColumnAt(4);
        CumprodMat M = new CumprodMat(row, Dimension.COL);

        row.printInLabel("row");
        // System.out.println("----- M -----");
        M.getCumprod().printInLabel("M");
    }

}
