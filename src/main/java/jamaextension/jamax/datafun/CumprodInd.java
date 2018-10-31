/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Sione
 */
public final class CumprodInd
{

    private Indices cumprod;

    public CumprodInd(Indices A)
    {
        this(A, null);
    }

    /**
     * Creates a new instance of SumMat
     */
    public CumprodInd(Indices A, Dimension dim)
    {
        if (dim == null)
        {
            if (A.isVector())
            {
                cumprodIndicesVector(A);
            }
            else
            {
                cumprodIndices(A, Dimension.ROW);
            }
        }
        else
        {
            cumprodIndices(A, dim);
        }
    }

    private void cumprodIndices(Indices A, Dimension dim)
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
                    cumprodIndicesVector(A);
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
                    cumprodIndicesVector(A);
                }
            }
        }
        else
        {
            cumprodIndicesAll(A, dim);
        }
    }

    private void cumprodIndicesAll(Indices A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
        int t = 0;
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
            throw new IllegalArgumentException("cumprodIndicesAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
    }

    private void cumprodIndicesVector(Indices A)
    {
        if (!A.isVector())
        {
            throw new IllegalArgumentException(
                    " cumprodIndicesVector :  Parameter \"A\" must be a vector and not a matrix.");
        }

        int c = A.length();
        if (c == 1)
        {// if only one element , return this element.
            cumprod = new Indices(1, 1, A.get(0, 0));
            return;
        }

        cumprod = new Indices(A.getRowDimension(), A.getColumnDimension());
        int temp = A.get(0, 0);
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

    public Indices getCumprod()
    {
        return this.cumprod;
    }

    public static void main(String[] args)
    {
        int[] aa =
        {
                1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3
        };
        Indices A = new Indices(aa).toColVector();
        /*
         * Indices A = SampleMatrices.generateIndices(); Indices row =
         * A.getRowAt(2); Indices col = A.getColumnAt(4);
         */
        CumprodInd M = new CumprodInd(A);

        System.out.println("----- M -----");
        M.getCumprod().print(4, 0);
    }

}
