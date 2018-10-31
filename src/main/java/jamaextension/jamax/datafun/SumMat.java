/*
 * SumMat.java
 *
 * Created on 7 November 2007, 03:43
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
public class SumMat extends Sum
{

    private boolean ignoreNaN = false;

    public SumMat(Matrix A)
    {
        this(A, null, false);
    }

    public SumMat(Matrix A, boolean ignoreNaN)
    {
        this(A, null, ignoreNaN);
    }

    /**
     * Creates a new instance of SumMat
     * 
     * @param A
     * @param dim
     */
    public SumMat(Matrix A, Dimension dim, boolean ignoreNaN)
    {
        this.ignoreNaN = ignoreNaN;
        if (dim == null)
        {
            if (A.isVector())
            {
                sumVector(A);
            }
            else
            {
                sumMatrix(A, Dimension.ROW);
            }
        }
        else
        {
            sumMatrix(A, dim);
        }
    }

    // sumNanVec(Matrix vec)

    public SumMat(Matrix A, Dimension dim)
    {
        this(A, dim, false);
    }

    private void sumMatrix(Matrix A, Dimension dim)
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
            sumMatrixAll(A, dim);
        }
    }

    private void sumMatrixAll(Matrix A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
        double t = 0.0;

        switch (dim)
        {
        case ROW:
            sumObject = new Matrix(1, cols);
            for (int j = 0; j < cols; j++)
            {
                t = 0.0;
                if (this.ignoreNaN)
                {
                    Matrix colMat = A.getColumnAt(j);
                    t = JDatafun.sumNanVec(colMat);
                }
                else
                {
                    for (int i = 0; i < rows; i++)
                    {
                        t += A.get(i, j);
                    }
                }
                ((Matrix) sumObject).set(0, j, t);
            }
            break;
        case COL:
            sumObject = new Matrix(rows, 1);
            for (int i = 0; i < rows; i++)
            {
                t = 0.0;
                if (this.ignoreNaN)
                {
                    Matrix rowMat = A.getRowAt(i);
                    t = JDatafun.sumNanVec(rowMat);
                }
                else
                {
                    for (int j = 0; j < cols; j++)
                    {
                        t += A.get(i, j);
                    }
                }
                ((Matrix) sumObject).set(i, 0, t);
            }
            break;
        default:
            throw new IllegalArgumentException("sumMatrixAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
    }

    private void sumVector(Matrix A)
    {

        if (A == null || A.isNull())
        {
            sumObject = new Matrix(1, 1);
            return;
        }

        if (this.ignoreNaN)
        {
            double val = JDatafun.sumNanVec(A);
            sumObject = new Matrix(1, 1, val);
            return;
        }

        if (!A.isVector())
        {
            throw new IllegalArgumentException(" sumVector :  Parameter \"A\" must be a vector and not a matrix.");
        }
        // double[][] internal = A.getArray();
        // double[][] summing = null;
        double temp = 0.0;

        // int row = A.getRowDimension();
        // int col = A.getColumnDimension();

        int c = A.length();
        if (c == 1)
        {// if only one element , return this element.
            double start = A.start();
            // System.out.println("start = "+start);
            sumObject = new Matrix(1, 1, start);
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
        sumObject = new Matrix(1, 1, temp);
    }

    public static void main(String[] args)
    {
        Matrix A = SampleMatrices.generateMatrix();
        A.printInLabel("A", 0);
        Matrix A2 = A.copy();

        int[] ind = A.LTEQ(5.0).findIJ().getIndex();
        A2.setElements(ind, Double.NaN);
        A2.printInLabel("A2", 0);

        Matrix row = A.getRowAt(2);
        Matrix col = A.getColumnAt(4);
        SumMat M = new SumMat(A2, Dimension.ROW, true);
        Matrix Mr = (Matrix) M.getSumObject();
        System.out.println("----- Mr -----");
        Mr.print(4, 0);
    }
}
