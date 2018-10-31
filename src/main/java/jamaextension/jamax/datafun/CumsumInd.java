/*
 * CumsumInd.java
 *
 * Created on 8 November 2007, 23:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Feynman Perceptrons
 */
public final class CumsumInd
{

    private Indices cumsum;

    public CumsumInd(Indices A)
    {
        this(A, null);
    }

    /**
     * Creates a new instance of SumMat
     */
    public CumsumInd(Indices A, Dimension dim)
    {
        if (dim == null)
        {
            if (A.isVector())
            {
                cumsumIndicesVector(A);
            }
            else
            {
                cumsumIndices(A, Dimension.ROW);
            }
        }
        else
        {
            cumsumIndices(A, dim);
        }
    }

    private void cumsumIndices(Indices A, Dimension dim)
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
                    cumsumIndicesVector(A);
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
                    cumsumIndicesVector(A);
                }
            }
        }
        else
        {
            cumsumIndicesAll(A, dim);
        }
    }

    private void cumsumIndicesAll(Indices A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
        int t = 0;
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
            throw new IllegalArgumentException("cumsumIndicesAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
    }

    private void cumsumIndicesVector(Indices A)
    {
        if (!A.isVector())
        {
            throw new IllegalArgumentException(
                    " cumsumIndicesVector :  Parameter \"A\" must be a vector and not a matrix.");
        }

        int c = A.length();
        if (c == 1)
        {// if only one element , return this element.
            cumsum = new Indices(1, 1, A.get(0, 0));
            return;
        }

        cumsum = new Indices(A.getRowDimension(), A.getColumnDimension());
        int temp = A.get(0, 0);
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

    public Indices getCumsum()
    {
        return this.cumsum;
    }

    public static void main(String[] args)
    {
        Indices A = SampleMatrices.generateIndices();
        Indices row = A.getRowAt(2);
        Indices col = A.getColumnAt(4);
        CumsumInd M = new CumsumInd(A);

        System.out.println("----- M -----");
        M.getCumsum().print(4, 0);
    }

}
