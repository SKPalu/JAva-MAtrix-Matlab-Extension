/*
 * LogicalAnyInd.java
 *
 * Created on 28 November 2007, 07:12
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
public class LogicalAnyInd extends LogicalAny
{

    public LogicalAnyInd(Indices A)
    {
        this(A, null);
    }

    /**
     * Creates a new instance of MaxMat
     * 
     * @param A
     * @param dim
     */
    public LogicalAnyInd(Indices A, Dimension dim)
    {
        if (dim == null)
        {
            if (A.isVector())
            {
                anyVector(A);
            }
            else
            {
                anyIndices(A, Dimension.ROW);
            }
        }
        else
        {
            anyIndices(A, dim);
        }
    }

    private void anyIndices(Indices A, Dimension dim)
    {
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {
                    anyIndicesAll(A, Dimension.ROW);
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
                    anyIndicesAll(A, Dimension.COL);
                }
                else
                {
                    anyVector(A);
                }
            }
        }
        else
        {
            anyIndicesAll(A, dim);
        }
    }

    private void anyIndicesAll(Indices A, Dimension dim)
    {
        int m = A.getRowDimension();
        int n = A.getColumnDimension();
        int[][] B = A.getArray();
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

    private void anyVector(Indices A)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(" anyVector :  Parameter \"A\" must be a vector and not a matrix.");
        }

        int[][] B = A.getArray();
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
        Indices A = SampleMatrices.generateIndices();
        Indices row = A.getRowAt(3);
        Indices col = A.getColumnAt(0);
        LogicalAnyInd M = new LogicalAnyInd(col, Dimension.COL);
        Indices Mr = M.getIndices();
        System.out.println("----- Mr -----");
        Mr.print(4, 0);
    }
}
