/*
 * LogicalAnyMat.java
 *
 * Created on 28 November 2007, 06:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Feynman Perceptrons
 */
public class LogicalAnyMat extends LogicalAny
{

    public LogicalAnyMat(Matrix A)
    {
        this(A, null);
    }

    /**
     * Creates a new instance of MaxMat
     * 
     * @param A
     * @param dim
     */
    public LogicalAnyMat(Matrix A, Dimension dim)
    {
        if (dim == null)
        {
            if (A.isVector())
            {
                anyVector(A);
            }
            else
            {
                anyMatrix(A, Dimension.ROW);
            }
        }
        else
        {
            anyMatrix(A, dim);
        }
    }

    private void anyMatrix(Matrix A, Dimension dim)
    {
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {
                    anyMatrixAll(A, Dimension.ROW);
                }
                else
                {
                    anyVector(A);
                }
            }
            else
            {// column vector
                if (dim == Dimension.COL)
                {
                    anyMatrixAll(A, Dimension.COL);
                }
                else
                {
                    anyVector(A);
                }
            }
        }
        else
        {
            anyMatrixAll(A, dim);
        }
    }

    private void anyMatrixAll(Matrix A, Dimension dim)
    {
        int m = A.getRowDimension();
        int n = A.getColumnDimension();
        double[][] B = A.getArray();
        int[][] X = null;

        switch (dim)
        {
        case ROW:
            logicalAnyObject = new Indices(1, n);
            X = logicalAnyObject.getArray();
            logicalAnyObject.setLogical(true);
            for (int j = 0; j < n; j++)
            {
                for (int i = 0; i < m; i++)
                {
                    if (B[i][j] != 0)
                    {
                        X[0][j] = 1;
                        continue;
                    }
                }
            }
            break;
        case COL:
            logicalAnyObject = new Indices(m, 1);
            X = logicalAnyObject.getArray();
            logicalAnyObject.setLogical(true);
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (B[i][j] != 0)
                    {
                        X[i][0] = 1;
                        continue;
                    }
                }
            }
            break;
        default:
            throw new IllegalArgumentException("anyMatrixAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
    }

    private void anyVector(Matrix A)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(" anyVector :  Parameter \"A\" must be a vector and not a matrix.");
        }

        double[][] B = A.getArray();
        logicalAnyObject = new Indices(1, 1);
        logicalAnyObject.setLogical(true);

        int c = A.length();

        if (A.isRowVector())
        {
            for (int j = 0; j < c; j++)
            {
                if (B[0][j] != 0)
                {
                    logicalAnyObject.set(0, 0, 1);
                    break;
                }
            }
        }
        else
        {
            for (int i = 0; i < c; i++)
            {
                if (B[i][0] != 0)
                {
                    logicalAnyObject.set(0, 0, 1);
                    break;
                }
            }
        }
    }

    public static void main(String[] args)
    {
        Matrix A = SampleMatrices.generateMatrix();
        Matrix row = A.getRowAt(3);
        Matrix col = A.getColumnAt(0);
        LogicalAnyMat M = new LogicalAnyMat(col, Dimension.COL);
        Indices Mr = M.getIndices();
        System.out.println("----- Mr -----");
        Mr.print(4, 0);
    }
}
