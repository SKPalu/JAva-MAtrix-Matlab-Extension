/*
 * SumInd.java
 *
 * Created on 7 November 2007, 17:56
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
public class SumInd extends Sum
{

    public SumInd(Indices A)
    {
        this(A, null);
    }

    /**
     * Creates a new instance of SumMat
     */
    public SumInd(Indices A, Dimension dim)
    {
        if (dim == null)
        {
            if (A.isVector())
            {
                sumVector(A);
            }
            else
            {
                sumIndices(A, Dimension.ROW);
            }
        }
        else
        {
            sumIndices(A, dim);
        }
    }

    private void sumIndices(Indices A, Dimension dim)
    {
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {
                    sumObject = A.copy();
                }
                else
                {
                    sumVector(A);
                }
            }
            else
            {// column vector
                if (dim == Dimension.COL)
                {
                    sumObject = A.copy();
                }
                else
                {
                    sumVector(A);
                }
            }
        }
        else
        {
            sumIndicesAll(A, dim);
        }
    }

    private void sumIndicesAll(Indices A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
        int t = 0;

        switch (dim)
        {
        case ROW:
            sumObject = new Indices(1, cols);
            for (int j = 0; j < cols; j++)
            {
                t = 0;
                for (int i = 0; i < rows; i++)
                {
                    t += A.get(i, j);
                }
                ((Indices) sumObject).set(0, j, t);
            }
            break;
        case COL:
            sumObject = new Indices(rows, 1);
            for (int i = 0; i < rows; i++)
            {
                t = 0;
                for (int j = 0; j < cols; j++)
                {
                    t += A.get(i, j);
                }
                ((Indices) sumObject).set(i, 0, t);
            }
            break;
        default:
            throw new IllegalArgumentException("sumIndicesAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
    }

    private void sumVector(Indices A)
    {
        if (!A.isVector())
        {
            throw new IllegalArgumentException(" sumVector :  Parameter \"A\" must be a vector and not a matrix.");
        }
        int temp = 0;

        int row = A.getRowDimension();
        int col = A.getColumnDimension();

        int c = A.length();
        if (c == 1)
        {// if only one element , return this element.
            sumObject = new Indices(1, 1, A.get(0, 0));
            return;
        }

        if (A.isRowVector())
        {
            for (int j = 0; j < c; j++)
            {
                temp += A.get(0, j);
            }
        }
        else
        {
            for (int j = 0; j < c; j++)
            {
                temp += A.get(j, 0);
            }
        }
        sumObject = new Indices(1, 1, temp);
    }

    public static void main(String[] args)
    {
        Indices A = SampleMatrices.generateIndices();
        Indices row = A.getRowAt(2);
        Indices col = A.getColumnAt(4);
        SumInd M = new SumInd(A);
        Indices Mr = (Indices) M.getSumObject();
        System.out.println("----- Mr -----");
        Mr.print(4, 0);
    }

}
