/*
 * SortMat.java
 *
 * Created on 5 November 2007, 10:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.constants.SortingMode;

/**
 * 
 * @author Feynman Perceptrons
 * 
 * @deprecated Use the Class <B>QuickSortMat</B>, instead.
 * 
 */
public class SortMat extends Sort
{

    public SortMat(Matrix A)
    {
        this(A, null);
    }

    public SortMat(Matrix A, Dimension dim)
    {
        this(A, dim, null);
    }

    /**
     * Creates a new instance of SortMat
     */
    public SortMat(Matrix A, Dimension dim, SortingMode mode)
    {
        if (mode != null)
        {
            this.mode = mode;
        }
        // sortMatrix(A,dim);
        if (dim == null)
        {
            if (A.isVector())
            {
                sortVector(A);
            }
            else
            {
                sortMatrix(A, Dimension.ROW);
            }
        }
        else
        {
            sortMatrix(A, dim);
        }
        ((Matrix) sortedObject).setSorted(true);
    }

    private void sortMatrix(Matrix A, Dimension dim)
    {
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {// return the same
                    sortedObject = A.copy();
                    sortedIndices = new Indices(A.getRowDimension(), A.getColumnDimension());
                }
                else
                {
                    sortVector(A);
                }
            }
            else
            {// column vector
                if (dim == Dimension.COL)
                {
                    sortedObject = A.copy();
                    sortedIndices = new Indices(A.getRowDimension(), A.getColumnDimension());
                }
                else
                {
                    sortVector(A);
                }
            }
        }
        else
        {
            sortMatrixAll(A, dim);
        }
    }

    private void sortMatrixAll(Matrix A, Dimension dim)
    {
        // Indices indices = null;
        int[][] I = null;
        int m = A.getRowDimension();
        int n = A.getColumnDimension();
        sortedObject = A.copy();
        double[][] S = ((Matrix) sortedObject).getArray();

        double hold = 0.0;
        int holdInt = 0;

        if (dim == Dimension.ROW)
        {
            sortedIndices = A.generateIndices();
            I = sortedIndices.getArray();
            for (int j = 0; j < n; j++)
            {
                for (int pass = 1; pass < m; pass++)
                {
                    for (int i = 0; i < (m - 1); i++)
                    {
                        if (compare(S[i][j], S[i + 1][j]))
                        {
                            hold = S[i][j];
                            S[i][j] = S[i + 1][j];
                            S[i + 1][j] = hold;
                            // reorder 'B'
                            holdInt = I[i][j];
                            I[i][j] = I[i + 1][j];
                            I[i + 1][j] = holdInt;
                        }// end if
                    }// end for
                }// end for
            }// end for
        }// end if
        else if (dim == Dimension.COL)
        {
            sortedIndices = A.generateIndices(false);
            I = sortedIndices.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int pass = 1; pass < n; pass++)
                {
                    for (int j = 0; j < (n - 1); j++)
                    {
                        if (compare(S[i][j], S[i][j + 1]))
                        {
                            hold = S[i][j];
                            S[i][j] = S[i][j + 1];
                            S[i][j + 1] = hold;
                            // reorder 'B'
                            holdInt = I[i][j];
                            I[i][j] = I[i][j + 1];
                            I[i][j + 1] = holdInt;
                        }
                    }// end for
                }// end for
            }// end for
        }// end else if
        else
        {
            throw new IllegalArgumentException("sortMatrix : Dimension  " + dim.toString() + " , not supported.");
        }
    }

    private void sortVector(Matrix A)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(" sortVector :  Parameter \"A\" must be a vector and not a matrix.");
        }
        int[][] I = null;
        double hold = 0.0;
        int holdInt = 0;

        // indices = A.generateIndices();
        int c = A.length();
        if (c == 1)
        {// if only one element , return this element.
            sortedObject = new Matrix(1, 1, A.get(0, 0));
            sortedIndices = new Indices(1, 1);
            return; // new Object[]{singleElement,indices};
        }

        sortedObject = A.copy();
        double[][] S = ((Matrix) sortedObject).getArray();

        if (A.isRowVector())
        {
            sortedIndices = A.generateIndices(false);
            I = sortedIndices.getArray();
            for (int pass = 1; pass < c; pass++)
            {
                for (int j = 0; j < (c - 1); j++)
                {
                    if (compare(S[0][j], S[0][j + 1]))
                    { // S[0][j] > S[0][j+1]){
                        hold = S[0][j];
                        S[0][j] = S[0][j + 1];
                        S[0][j + 1] = hold;
                        // reorder 'B'
                        holdInt = I[0][j];
                        I[0][j] = I[0][j + 1];
                        I[0][j + 1] = holdInt;
                    }// end if
                }// end for
            }// end for
        }
        else
        {
            sortedIndices = A.generateIndices();
            I = sortedIndices.getArray();
            for (int pass = 1; pass < c; pass++)
            {
                for (int i = 0; i < (c - 1); i++)
                {
                    if (compare(S[i][0], S[i + 1][0]))
                    { // S[i][0] > S[i+1][0]){
                        hold = S[i][0];
                        S[i][0] = S[i + 1][0];
                        S[i + 1][0] = hold;
                        // reorder 'I'
                        holdInt = I[i][0];
                        I[i][0] = I[i + 1][0];
                        I[i + 1][0] = holdInt;
                    }// end if
                }// end for
            }// end for
        }

    }

    private boolean compare(double A1, double A2)
    {
        boolean nanA1 = Double.isNaN(A1);
        boolean nanA2 = Double.isNaN(A2);
        boolean tf = false;
        if (mode == null || mode == SortingMode.ASCENDING)
        {
            if (nanA1 && !nanA2)
            {
                tf = true;
            }
            else if (!nanA1 && nanA2)
            {
                tf = false;
            }
            else
            {
                tf = A1 > A2;
            }
        }
        else if (mode == SortingMode.DESCENDING)
        {
            if (nanA1 && !nanA2)
            {
                tf = false;
            }
            else if (!nanA1 && nanA2)
            {
                tf = true;
            }
            else
            {
                tf = A1 < A2;
            }
        }
        else
        {
            throw new IllegalArgumentException(" compare :  Unknown \"SortingMode\" ( = " + mode.toString() + ").");
        }
        return tf;
    }

    // public Matrix getSortedMatrix() { return (Matrix)getSortedObject(); }

    public static void main(String[] args)
    {

        double[][] a =
        {
                {
                        14, 13, 12, Double.NaN, 14
                },
                {
                        3, Double.NaN, 7, 11, 14
                },
                {
                        9, 7, 9, 3, 6
                },
                {
                        7, 0, Double.NEGATIVE_INFINITY, 6, 13
                }
        };
        // {7, 0, 12, 6, 13}};

        Matrix A = new Matrix(a);

        SortMat S = new SortMat(A, Dimension.COL);
        Matrix Asort = (Matrix) S.getSortedObject();

        System.out.println("----- A -----");
        A.print(4, 0);

        System.out.println("----- Asort -----");
        Asort.print(4, 0);

        /*
         * Matrix A = SampleMatrices.generateMatrix(); Matrix row =
         * A.getRowAt(2); Matrix col = A.getColumnAt(4);
         * 
         * SortMat M = new SortMat(A); Matrix Mr = (Matrix)M.getSortedObject();
         * System.out.println("----- Mr -----"); Mr.print(4,0); Indices Ir =
         * M.getIndices(); System.out.println("----- Ir -----");
         * Ir.plus(1).print(4,0);
         */
    }

}
