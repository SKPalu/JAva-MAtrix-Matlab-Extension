/*
 * MinInd_old.java
 *
 * Created on 7 November 2007, 17:34
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
public class MinInd_old extends Min
{

    public MinInd_old(Indices A)
    {
        this(A, null);
    }

    /**
     * Creates a new instance of MinMat
     */
    public MinInd_old(Indices A, Dimension dim)
    {
        if (dim == null)
        {
            if (A.isVector())
            {
                minVector(A);
            }
            else
            {
                minIndices(A, Dimension.ROW);
            }
        }
        else
        {
            minIndices(A, dim);
        }
    }

    private void minIndices(Indices A, Dimension dim)
    {
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {// return the same
                    minObject = A.copy();
                    minIndices = new Indices(A.getRowDimension(), A.getColumnDimension());
                }
                else
                {
                    minVector(A);
                }
            }
            else
            {// column vector
                if (dim == Dimension.COL)
                {
                    minObject = A.copy();
                    minIndices = new Indices(A.getRowDimension(), A.getColumnDimension());
                }
                else
                {
                    minVector(A);
                }
            }
        }
        else
        {
            minIndicesAll(A, dim);
        }
    }

    private void minIndicesAll(Indices A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
        int t = 0;
        int val = 0;
        int T = 0;
        int Tval = 0;

        switch (dim)
        {
        case ROW:
            minObject = new Indices(1, cols);
            minIndices = new Indices(1, cols);
            for (int j = 0; j < cols; j++)
            {
                t = A.get(0, j);
                T = 0;
                for (int i = 1; i < rows; i++)
                {
                    val = A.get(i, j);
                    Tval = i;
                    if (t <= val)
                    {
                        t = t;
                        T = T;
                        // System.out.println("1) t = "+t+"  :  T = "+T);
                    }
                    else
                    {
                        t = val;
                        T = Tval;
                        // System.out.println("2) t = "+t+"  :  T = "+T);
                    }
                    // t = t > X.get(i,j) ? t : X.get(i,j) ;
                }
                ((Indices) minObject).set(0, j, t);
                minIndices.set(0, j, T);
            }
            break;
        case COL:
            minObject = new Indices(rows, 1);
            minIndices = new Indices(rows, 1);
            for (int i = 0; i < rows; i++)
            {
                t = A.get(i, 0);
                T = 0;
                for (int j = 1; j < cols; j++)
                {
                    val = A.get(i, j);
                    Tval = j;
                    if (t <= val)
                    {
                        t = t;
                        T = T;
                    }
                    else
                    {
                        t = val;
                        T = Tval;
                    }
                    // t = t > X.get(i,j)? t : X.get(i,j) ;
                }
                ((Indices) minObject).set(i, 0, t);
                minIndices.set(i, 0, T);
            }
            break;
        default:
            throw new IllegalArgumentException("minIndicesAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
    }

    private void minVector(Indices A)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(" minVector :  Parameter \"A\" must be a vector and not a matrix.");
        }
        int t = 0;
        int val = 0;
        int T = 0;
        int Tval = 0;

        minObject = new Indices(1, 1);

        int c = A.length();
        if (c == 1)
        {// if only one element , then return.
            ((Indices) minObject).set(0, 0, A.get(0, 0));
            minIndices = new Indices(1, 1);
            return;
        }

        t = A.get(0, 0);
        T = 0;

        if (A.isRowVector())
        {
            for (int j = 1; j < c; j++)
            {
                val = A.get(0, j);
                Tval = j;
                if (t <= val)
                {
                    t = t;
                    T = T;
                }
                else
                {
                    t = val;
                    T = Tval;
                }
            }
        }
        else
        {
            for (int i = 1; i < c; i++)
            {
                val = A.get(i, 0);
                Tval = i;
                if (t <= val)
                {
                    t = t;
                    T = T;
                }
                else
                {
                    t = val;
                    T = Tval;
                }
            }
        }
        ((Indices) minObject).set(0, 0, t);
        minIndices = new Indices(1, 1, T);
    }

    public static void main(String[] args)
    {
        Indices A = SampleMatrices.generateIndices();
        Indices row = A.getRowAt(2);
        Indices col = A.getColumnAt(4);
        MinInd_old M = new MinInd_old(col, Dimension.COL);
        Indices Mr = (Indices) M.getMinObject();
        System.out.println("----- Mr -----");
        Mr.print(4, 0);
        Indices Ir = M.getIndices();
        System.out.println("----- Ir -----");
        Ir.plus(1).print(4, 0);
    }
}
