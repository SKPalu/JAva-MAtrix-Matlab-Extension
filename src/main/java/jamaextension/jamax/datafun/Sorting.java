/*
 * Sorting.java
 *
 * Created on 8 November 2007, 15:01
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
 ** @deprecated Use Classes <B>QuickSortInd</B> and <B>QuickSortMat</B> instead.
 * 
 * @author Feynman Perceptrons
 */
final class Sorting
{

    /** Creates a new instance of Sorting */
    private Sorting()
    {
    }

    protected static Matrix sort(Matrix A)
    {
        return sort(A, null);
    }

    protected static Matrix sort(Matrix A, Dimension dim)
    {
        return sort(A, dim, SortingMode.ASCENDING);
    }

    /**
     * Creates a new instance of SortMat
     */
    protected static Matrix sort(Matrix A, Dimension dim, SortingMode mode)
    {
        Matrix sortedObject = null;
        if (dim == null)
        {
            if (A.isVector())
            {
                sortedObject = sortMatrixVector(A, mode);
            }
            else
            {
                sortedObject = sortMatrix(A, Dimension.ROW, mode);
            }
        }
        else
        {
            sortedObject = sortMatrix(A, dim, mode);
        }
        sortedObject.setSorted(true);
        return sortedObject;
    }

    private static Matrix sortMatrix(Matrix A, Dimension dim, SortingMode mode)
    {
        Matrix sortedObject = null;
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {
                    sortedObject = A.copy();
                }// return the same
                else
                {
                    sortedObject = sortMatrixVector(A, mode);
                }
            }
            else
            {// column vector
                if (dim == Dimension.COL)
                {
                    sortedObject = A.copy();
                }
                else
                {
                    sortedObject = sortMatrixVector(A, mode);
                }
            }
        }
        else
        {
            sortedObject = sortMatrixAll(A, dim, mode);
        }
        return sortedObject;
    }

    private static Matrix sortMatrixAll(Matrix A, Dimension dim, SortingMode mode)
    {
        int m = A.getRowDimension();
        int n = A.getColumnDimension();
        Matrix sortedObject = A.copy();
        double[][] S = sortedObject.getArray();

        double hold = 0.0;

        if (dim == Dimension.ROW)
        {

            for (int j = 0; j < n; j++)
            {
                for (int pass = 1; pass < m; pass++)
                {
                    for (int i = 0; i < (m - 1); i++)
                    {
                        if (compareDouble(S[i][j], S[i + 1][j], mode))
                        {
                            hold = S[i][j];
                            S[i][j] = S[i + 1][j];
                            S[i + 1][j] = hold;
                        }// end if
                    }// end for
                }// end for
            }// end for
        }// end if
        else if (dim == Dimension.COL)
        {

            for (int i = 0; i < m; i++)
            {
                for (int pass = 1; pass < n; pass++)
                {
                    for (int j = 0; j < (n - 1); j++)
                    {
                        if (compareDouble(S[i][j], S[i][j + 1], mode))
                        {
                            hold = S[i][j];
                            S[i][j] = S[i][j + 1];
                            S[i][j + 1] = hold;
                        }
                    }// end for
                }// end for
            }// end for
        }// end else if
        else
        {
            throw new IllegalArgumentException("sortMatrix : Dimension  " + dim.toString() + " , not supported.");
        }

        return sortedObject;
    }

    private static Matrix sortMatrixVector(Matrix A, SortingMode mode)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(" sortVector :  Parameter \"A\" must be a vector and not a matrix.");
        }
        double hold = 0.0;
        // int holdInt = 0;
        Matrix sortedObject = null;
        // indices = A.generateIndices();
        int c = A.length();
        if (c == 1)
        {// if only one element , return this element.
            sortedObject = new Matrix(1, 1, A.get(0, 0));
            return sortedObject; // new Object[]{singleElement,indices};
        }

        sortedObject = A.copy();
        double[][] S = sortedObject.getArray();

        if (A.isRowVector())
        {
            for (int pass = 1; pass < c; pass++)
            {
                for (int j = 0; j < (c - 1); j++)
                {
                    if (compareDouble(S[0][j], S[0][j + 1], mode))
                    { // S[0][j] > S[0][j+1]){
                        hold = S[0][j];
                        S[0][j] = S[0][j + 1];
                        S[0][j + 1] = hold;
                    }// end if
                }// end for
            }// end for
        }
        else
        {
            for (int pass = 1; pass < c; pass++)
            {
                for (int i = 0; i < (c - 1); i++)
                {
                    if (compareDouble(S[i][0], S[i + 1][0], mode))
                    { // S[i][0] > S[i+1][0]){
                        hold = S[i][0];
                        S[i][0] = S[i + 1][0];
                        S[i + 1][0] = hold;
                    }// end if
                }// end for
            }// end for
        }

        return sortedObject;
    }

    private static boolean compareDouble(double A1, double A2, SortingMode mode)
    {
        boolean tf = false;
        if (mode == null || mode == SortingMode.ASCENDING)
        {
            tf = A1 > A2;
        }
        else if (mode == SortingMode.DESCENDING)
        {
            tf = A1 < A2;
        }
        else
        {
            throw new IllegalArgumentException(" compareDouble :  Unknown \"SortingMode\" ( = " + mode.toString()
                    + ").");
        }
        return tf;
    }

    // /////////////////////////////////////////////////////////////////////////
    protected static Indices sort(Indices A)
    {
        return sort(A, null);
    }

    protected static Indices sort(Indices A, Dimension dim)
    {
        return sort(A, dim, SortingMode.ASCENDING);
    }

    /**
     * Creates a new instance of SortMat
     */
    protected static Indices sort(Indices A, Dimension dim, SortingMode mode)
    {
        // if(mode!=null){ this.mode = mode; }
        Indices sortedObject = null;
        if (dim == null)
        {
            if (A.isVector())
            {
                sortedObject = sortIndicesVector(A, mode);
            }
            else
            {
                sortedObject = sortIndices(A, Dimension.ROW, mode);
            }
        }
        else
        {
            sortedObject = sortIndices(A, dim, mode);
        }
        sortedObject.setSorted(true);
        return sortedObject;
    }

    private static Indices sortIndices(Indices A, Dimension dim, SortingMode mode)
    {
        Indices sortedObject = null;
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {
                    sortedObject = A.copy();
                } // return the same
                else
                {
                    sortedObject = sortIndicesVector(A, mode);
                }
            }
            else
            {// column vector
                if (dim == Dimension.COL)
                {
                    sortedObject = A.copy();
                }
                else
                {
                    sortedObject = sortIndicesVector(A, mode);
                }
            }
        }
        else
        {
            sortedObject = sortIndicesAll(A, dim, mode);
        }
        return sortedObject;
    }

    private static Indices sortIndicesAll(Indices A, Dimension dim, SortingMode mode)
    {

        int m = A.getRowDimension();
        int n = A.getColumnDimension();
        Indices sortedObject = A.copy();
        int[][] S = sortedObject.getArray();
        int hold = 0;

        if (dim == Dimension.ROW)
        {
            for (int j = 0; j < n; j++)
            {
                for (int pass = 1; pass < m; pass++)
                {
                    for (int i = 0; i < (m - 1); i++)
                    {
                        if (compareInteger(S[i][j], S[i + 1][j], mode))
                        {
                            hold = S[i][j];
                            S[i][j] = S[i + 1][j];
                            S[i + 1][j] = hold;
                        }// end if
                    }// end for
                }// end for
            }// end for
        }// end if
        else if (dim == Dimension.COL)
        {
            for (int i = 0; i < m; i++)
            {
                for (int pass = 1; pass < n; pass++)
                {
                    for (int j = 0; j < (n - 1); j++)
                    {
                        if (compareInteger(S[i][j], S[i][j + 1], mode))
                        {
                            hold = S[i][j];
                            S[i][j] = S[i][j + 1];
                            S[i][j + 1] = hold;
                        }
                    }// end for
                }// end for
            }// end for
        }// end else if
        else
        {
            throw new IllegalArgumentException("sortMatrix : Dimension  " + dim.toString() + " , not supported.");
        }
        return sortedObject;
    }

    private static Indices sortIndicesVector(Indices A, SortingMode mode)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(
                    " sortIndicesVector :  Parameter \"A\" must be a vector and not a matrix.");
        }

        int hold = 0;
        Indices sortedObject = null;

        int c = A.length();
        if (c == 1)
        {// if only one element , return this element.
            sortedObject = new Indices(1, 1, A.get(0, 0));
            return sortedObject; // new Object[]{singleElement,indices};
        }

        sortedObject = A.copy();
        int[][] S = sortedObject.getArray();

        if (A.isRowVector())
        {
            for (int pass = 1; pass < c; pass++)
            {
                for (int j = 0; j < (c - 1); j++)
                {
                    if (compareInteger(S[0][j], S[0][j + 1], mode))
                    { // S[0][j] > S[0][j+1]){
                        hold = S[0][j];
                        S[0][j] = S[0][j + 1];
                        S[0][j + 1] = hold;
                    }// end if
                }// end for
            }// end for
        }
        else
        {

            for (int pass = 1; pass < c; pass++)
            {
                for (int i = 0; i < (c - 1); i++)
                {
                    if (compareInteger(S[i][0], S[i + 1][0], mode))
                    { // S[i][0] > S[i+1][0]){
                        hold = S[i][0];
                        S[i][0] = S[i + 1][0];
                        S[i + 1][0] = hold;
                    }// end if
                }// end for
            }// end for
        }

        return sortedObject;
    }

    private static boolean compareInteger(int A1, int A2, SortingMode mode)
    {
        boolean tf = false;
        if (mode == null || mode == SortingMode.ASCENDING)
        {
            tf = A1 > A2;
        }
        else if (mode == SortingMode.DESCENDING)
        {
            tf = A1 < A2;
        }
        else
        {
            throw new IllegalArgumentException(" compareInteger :  Unknown \"SortingMode\" ( = " + mode.toString()
                    + ").");
        }
        return tf;
    }

    public static void main(String[] args)
    {
        Matrix A = SampleMatrices.generateMatrix();
        Matrix row = A.getRowAt(2);
        Matrix col = A.getColumnAt(4);
        SortMat M = new SortMat(col, Dimension.COL, SortingMode.DESCENDING);
        Matrix Mr = (Matrix) M.getSortedObject();
        System.out.println("----- Mr -----");
        Mr.print(4, 0);
        Indices Ir = M.getIndices();
        System.out.println("----- Ir -----");
        Ir.plus(1).print(4, 0);

        /*
         * Indices A = SampleMatrices.generateIndices(); Indices row =
         * A.getRowAt(2); Indices col = A.getColumnAt(4); SortInd M = new
         * SortInd(col,Dimension.COL , SortingMode.DESCENDING); Indices Mr =
         * (Indices)M.getSortedObject(); System.out.println("----- Mr -----");
         * Mr.print(4,0); Indices Ir = M.getIndices();
         * System.out.println("----- Ir -----"); Ir.plus(1).print(4,0);
         */
    }
}// /////////////////////////////// End Class Definition
// ////////////////////////

