/*
 * Mean.java
 *
 * Created on 8 November 2007, 21:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.TestData;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Sione
 */
public class Mean
{

    private Matrix mean;
    private boolean ignoreNaN = false;

    public Mean(Matrix A)
    {
        this(A, null);
    }

    public Mean(Matrix A, Dimension dim)
    {
        this(A, dim, false);
    }

    public Mean(Matrix A, boolean ignoreNaN)
    {
        this(A, null, ignoreNaN);
    }

    /**
     * Creates a new instance of SumMat
     */
    public Mean(Matrix A, Dimension dim, boolean ignoreNaN)
    {
        this.ignoreNaN = ignoreNaN;
        if (dim == null)
        {
            if (A.isVector())
            {
                meanVector(A);
            }
            else
            {
                meanMatrix(A, Dimension.ROW);
            }
        }
        else
        {
            meanMatrix(A, dim);
        }
    }

    private void meanMatrix(Matrix A, Dimension dim)
    {
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {
                    mean = A.copy();
                }
                else
                {
                    meanVector(A);
                }
            }
            else
            {// column vector
                if (dim == Dimension.COL)
                {
                    mean = A.copy();
                }
                else
                {
                    meanVector(A);
                }
            }
        }
        else
        {
            meanMatrixAll(A, dim);
        }
    }

    private void meanMatrixAll(Matrix A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
        double t = 0.0;
        double len = 1.0;
        int count = 0;
        // double temp = 0.0;
        double val = 0.0;

        switch (dim)
        {
        case ROW:
            mean = new Matrix(1, cols);
            for (int j = 0; j < cols; j++)
            {
                t = 0.0;
                count = 0;
                for (int i = 0; i < rows; i++)
                {
                    val = A.get(i, j);
                    if (ignoreNaN)
                    {// && !Double.isNaN(val)) {
                        if (!Double.isNaN(val))
                        {
                            count++;
                            t += val;
                        }
                    }
                    else
                    {
                        t += val;
                    }
                }
                len = rows;

                if (ignoreNaN)
                {
                    if (count != 0)
                    {
                        t = t / (double) count;
                    }
                    else
                    {
                        t = Double.NaN;
                    }
                }
                else
                {
                    t = t / len;
                }
                mean.set(0, j, t);
            }
            break;
        case COL:
            mean = new Matrix(rows, 1);
            for (int i = 0; i < rows; i++)
            {
                t = 0.0;
                count = 0;
                for (int j = 0; j < cols; j++)
                {
                    val = A.get(i, j);
                    if (ignoreNaN)
                    {// && !Double.isNaN(val)) {
                        if (!Double.isNaN(val))
                        {
                            count++;
                            t += val;
                        }
                    }
                    else
                    {
                        t += val;
                    }
                }
                len = cols;
                // t = t / len;
                if (ignoreNaN)
                {
                    if (count != 0)
                    {
                        t = t / (double) count;
                    }
                    else
                    {
                        t = Double.NaN;
                    }
                }
                else
                {
                    t = t / len;
                }
                mean.set(i, 0, t);
            }
            break;
        default:
            throw new IllegalArgumentException("meanMatrixAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
    }

    private void meanVector(Matrix A)
    {
        if (!A.isVector())
        {
            throw new IllegalArgumentException(" meanVector :  Parameter \"A\" must be a vector and not a matrix.");
        }
        double[][] internal = A.getArray();
        double[][] summing = null;
        double temp = 0.0;

        int row = A.getRowDimension();
        int col = A.getColumnDimension();

        int c = A.length();
        if (c == 1)
        {// if only one element , return this element.
            mean = new Matrix(1, 1, A.get(0, 0));
            return;
        }

        double val = 0.0;
        int count = 0;

        if (A.isRowVector())
        {
            for (int j = 0; j < c; j++)
            {
                val = A.get(0, j);
                if (ignoreNaN)
                {// && !Double.isNaN(val)) {
                    if (!Double.isNaN(val))
                    {
                        count++;
                        temp += val;
                    }
                }
                else
                {
                    temp += val;
                }
            }
        }
        else
        {
            for (int j = 0; j < c; j++)
            {
                val = A.get(j, 0);
                ;
                if (ignoreNaN)
                {// && !Double.isNaN(val)) {
                    if (!Double.isNaN(val))
                    {
                        count++;
                        temp += val;
                    }
                }
                else
                {
                    temp += val;
                }
            }
        }
        double len = c;
        if (ignoreNaN)
        {
            if (count != 0)
            {
                temp = temp / (double) count;
            }
            else
            {
                temp = Double.NaN;
            }
        }
        else
        {
            temp = temp / len;
        }
        mean = new Matrix(1, 1, temp);
    }

    public Matrix getMean()
    {
        return this.mean;
    }

    public static void main(String[] args)
    {

        Matrix X = TestData.testMatNan(2);
        X.printInLabel("X", 0);
        Mean M = new Mean(X, Dimension.COL, true);
        Matrix mean = M.getMean();
        mean.printInLabel("mean-colwise");
        M = new Mean(X, Dimension.ROW, true);
        mean = M.getMean();
        mean.printInLabel("mean-rowise");

        /*
         * Matrix A = SampleMatrices.generateMatrix(); Matrix row =
         * A.getRowAt(2); Matrix col = A.getColumnAt(4); Mean M = new Mean(col,
         * Dimension.COL);
         * 
         * System.out.println("----- Mean -----"); M.getMean().print(4, 4);
         */
    }
}
