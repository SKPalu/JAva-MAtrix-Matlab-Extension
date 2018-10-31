package jamaextension.jamax;

/**
 * <p>
 * Title: </p>
 * <p>
 * Description: </p>
 * <p>
 * Copyright: Copyright (c) 2005</p>
 * <p>
 * Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.LogicalAnyInd;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortInd;
import jamaextension.jamax.datafun.QuickSortMat;
import jamaextension.jamax.ops.JOps;

public class Indices
{

    private int[][] A;
    private int m;
    private int n;
    private boolean logical = false;
    private boolean findIndex = false;
    private boolean sorted = false;

    public Indices()
    {
        this.m = 0;
        this.n = 0;
        this.A = null;
    }

    public Indices(int[][] Ain, int mIn, int nIn)
    {
        this.A = Ain;

        boolean warn = mIn < 0 || nIn < 0;
        if (warn)
        {
            throw new ConditionalException("Matrix : Both integer parameters \"mIn\" (= " + mIn + ") and \"nIn\" (= "
                    + nIn + ") must be non-negative.");
        }

        this.m = mIn;
        this.n = nIn;
    }

    /**
     * 
     * @param m
     * @param n
     */
    public Indices(int m, int n)
    {
        this(m, n, true);
    }

    /**
     * 
     * @param m
     * @param n
     * @param initializeToZeros
     */
    public Indices(int m, int n, boolean initializeToZeros)
    {
        if (m < 1 || n < 1)
        {
            throw new IllegalArgumentException("Indices : Dimensions must be greater than one.");
        }

        A = new int[m][n];

        if (!initializeToZeros)
        {
            for (int j = 0; j < n; j++)
            {
                for (int i = 0; i < m; i++)
                {
                    A[i][j] = i;
                }
            }
        }
        this.m = m;
        this.n = n;
    }

    /**
     * 
     * @param m
     * @param n
     * @param val
     */
    public Indices(int m, int n, int val)
    {
        if (m < 1 || n < 1)
        {
            throw new IllegalArgumentException("Indices : Dimensions must be at least one.");
        }

        A = new int[m][n];

        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                A[i][j] = val;
            }
        }

        this.m = m;
        this.n = n;
    }

    /**
     * 
     * @param m
     * @param n
     * @param val
     * @deprecated DO NOT use this contructor <B>Indices(int m, int n, Boolean
     *             val)</B>, because it is ambiquous. Use call such as 'new
     *             Indices(m,n)' for "FALSE" OR 'new Indices(m,n,1)' for "TRUE"
     *             then follow "makeLogical()"
     * 
     */
    public Indices(int m, int n, Boolean val)
    {
        if (m < 1 || n < 1)
        {
            throw new IllegalArgumentException("Indices : Dimensions must be greater than one.");
        }

        A = new int[m][n];

        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                if (val.booleanValue() == true)
                {
                    A[i][j] = 1;
                }
            }
        }

        this.m = m;
        this.n = n;
        logical = true;
    }

    /**
     * 
     * @param val
     */
    public Indices(int[][] val)
    {
        if (val == null)
        {
            throw new IllegalArgumentException("Indices : Array parameter \"val\" must be must be non-null.");
        }
        if (!checkIndicesDimensions(val))
        {
            throw new IllegalArgumentException("Indices : Array must be must be rectangular and not jagged.");
        }

        A = val;

        this.m = val.length;
        this.n = val[0].length;
    }

    /**
     * 
     * @param values
     */
    public Indices(int[] values)
    {
        if (values == null)
        {
            throw new IllegalArgumentException("Indices : Parameter \"values\" must be non-null.");
        }

        A = new int[1][values.length];

        for (int j = 0; j < values.length; j++)
        {
            A[0][j] = values[j]; // System.out.println("A[0]["+j+"] = "+A[0][j]);

        }

        this.m = 1;
        this.n = values.length;
    }

    protected void setFindIndex(boolean find)
    {
        this.findIndex = find;
    }

    /**
     * 
     * @param logical
     */
    public void setLogical(boolean logical)
    {
        this.logical = logical;
    }

    public void makeLogical()
    {
        setLogical(true);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] != 0)
                {
                    A[i][j] = 1;
                }
                else
                {
                    A[i][j] = 0;
                }
            }
        }
    }

    /**
     * 
     * @return
     */
    public Indices abs()
    {
        Indices X = new Indices(m, n);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = Math.abs(A[i][j]);
            }
        }
        return X;
    }

    public Indices blkdiag(Indices... varargin)
    {
        if (varargin == null || varargin.length == 0)
        {
            return this.copy();
        }

        int len = varargin.length;

        int row = 0;
        int col = 0;
        for (int i = 0; i < len; i++)
        {
            if (varargin[i] != null)
            {

                Indices ind = varargin[i];
                if (!ind.isNull())
                {
                    row += ind.getRowDimension();
                    col += ind.getColumnDimension();
                }

            }
        }

        if ((row + m) == 0 || (col + n) == 0)
        {
            return new Indices(null, row + m, col + n);
        }

        // collect all matrices to be blockwise concatenated
        ArrayList<Indices> list = new ArrayList<Indices>();
        if (!this.isNull())
        {
            list.add(this.copy());
        }

        for (int i = 0; i < len; i++)
        {
            if (varargin[i] != null)
            {
                Indices ind = varargin[i];
                if (!ind.isNull())
                {
                    list.add(ind);
                }
            }
        }

        Indices concat = new Indices(row + m, col + n);

        int rowStart = 0;
        int rowEnd = 0;

        int colStart = 0;
        int colEnd = 0;

        int siz = list.size();
        for (int i = 0; i < siz; i++)
        {

            Indices mat = list.get(i);
            if (i == 0)
            {
                rowStart = 0;
                rowEnd = mat.getRowDimension() - 1;
                colStart = 0;
                colEnd = mat.getColumnDimension() - 1;
            }
            else
            {
                rowStart = rowEnd + 1;
                rowEnd += mat.getRowDimension();
                colStart = colEnd + 1;
                colEnd += mat.getColumnDimension();
            }
            concat.setIndices(rowStart, rowEnd, colStart, colEnd, mat);
        }// end for

        return concat;
    }

    /**
     * 
     * @return
     */
    public boolean isLogical()
    {
        return logical;
    }

    /**
     * 
     * @return
     */
    public int getRowDimension()
    {
        return m;
    }

    /**
     * 
     * @return
     */
    public int getColumnDimension()
    {
        return n;
    }

    /**
     * 
     * @param m
     * @param n
     */
    public void setDimension(int m, int n)
    {
        if (m < 1 || n < 1)
        {
            throw new IllegalArgumentException("Indices : Dimensions must be greater than one.");
        }
        A = new int[m][n];
        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                A[i][j] = i;
            }
        }
        this.m = m;
        this.n = n;
    }

    /**
     * 
     * @return @deprecated Use the method <B>getArray</B> instead.
     */
    public int[][] getIndexValues()
    {
        return getArray();
    }

    /**
     * 
     * @return
     */
    public int[][] getArray()
    {
        return A;
    }

    /**
     * 
     * @return
     */
    public int[][] getArrayCopy()
    {
        int[][] C = new int[m][n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j];
            }
        }
        return C;
    }

    /**
     * 
     * @param i
     * @param j
     * @return
     */
    public int get(int i, int j)
    {
        int ind = 0;
        try
        {
            ind = A[i][j];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("get : Out-of-bound indices.");
        }
        return ind;
    }

    /**
     * 
     * @param indexValues
     */
    public void setIndexValues(int[][] indexValues)
    {
        if (checkIndicesDimensions(indexValues) == false)
        {
            throw new ArrayIndexOutOfBoundsException("setIndexValues : Non-rectangular indices value.");
        }
        this.A = indexValues;
        this.m = indexValues.length;
        this.n = indexValues[0].length;
    }

    /**
     * 
     * @param B
     * @return
     */
    public Indices times(Indices B)
    {
        if (B.getRowDimension() != n)
        {
            throw new IllegalArgumentException("'Indices' inner dimensions must agree.");
        }
        Indices X = new Indices(m, B.getColumnDimension());
        int[][] C = X.getArray();
        int[] Bcolj = new int[n];
        for (int j = 0; j < B.getColumnDimension(); j++)
        {
            for (int k = 0; k < n; k++)
            {
                Bcolj[k] = B.get(k, j);
            }
            for (int i = 0; i < m; i++)
            {
                int[] Arowi = A[i];
                int s = 0;
                for (int k = 0; k < n; k++)
                {
                    s += Arowi[k] * Bcolj[k];
                }
                C[i][j] = s;
            }
        }
        return X;
    }

    /**
     * 
     * @return
     */
    public Indices transpose()
    {
        Indices X = new Indices(n, m, false);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[j][i] = A[i][j];
            }
        }
        return X;
    }

    /**
     * 
     * @param findIndices
     * @param val
     */
    public void findIndicesSetValueAt(Indices findIndices, int val)
    {
        if (findIndices == null)
        {
            return;
        }
        if (findIndices.isFindIndex() == false)
        {
            throw new IllegalArgumentException(
                    "findIndicesSetValueAt : Parameter \"findIndices\" must be and Indices object from a \"findIndices()\" operation.");
        }
        int len = findIndices.getRowDimension();
        int I = 0;
        int J = 0;
        for (int i = 0; i < len; i++)
        {
            I = findIndices.get(i, 0);
            J = findIndices.get(i, 1);
            A[I][J] = val;
        }
    }

    /**
     * 
     * @param findIndices
     * @param values
     */
    public void findIndicesSetValueAt(Indices findIndices, Indices values)
    {
        if (findIndices == null)
        {
            return;
        }
        if (findIndices.isFindIndex() == false)
        {
            throw new IllegalArgumentException(
                    "findIndicesSetValueAt : Parameter \"findIndices\" must be and Indices object from a \"findIndices()\" operation.");
        }
        if (values.isVector() == false)
        {
            throw new IllegalArgumentException(
                    "findIndicesSetValueAt : Parameter \"values\" must be a row or column vector and not a matrix.");
        }
        int len = findIndices.getRowDimension();
        int len2 = values.length();

        if (len != len2)
        {
            throw new IllegalArgumentException(
                    "findIndicesSetValueAt : Parameter \"findIndices\" and \"values\" must have the same length.");
        }

        int I = 0;
        int J = 0;
        int val = 0;

        for (int i = 0; i < len; i++)
        {
            I = findIndices.get(i, 0);
            J = findIndices.get(i, 1);
            if (values.isColumnVector())
            {
                val = values.get(i, 0);
            }
            else
            {
                val = values.get(0, i);
            }
            try
            {
                A[I][J] = val;
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                throw new ArrayIndexOutOfBoundsException("setValueAt : Out-of-bound indices.");
            }
        }
    }

    /**
     * 
     * @param findIndices
     * @param values
     */
    public void findIndicesSetValueAt(Indices findIndices, int[] values)
    {
        if (findIndices == null)
        {
            return;
        }
        if (findIndices.isFindIndex() == false)
        {
            throw new IllegalArgumentException(
                    "findIndicesSetValueAt : Parameter \"findIndices\" must be and Indices object from a \"findIndices()\" operation.");
        }

        int len = findIndices.getRowDimension();
        int len2 = values.length;

        if (len != len2)
        {
            throw new IllegalArgumentException(
                    "findIndicesSetValueAt : Parameter \"findIndices\" and \"values\" must have the same length.");
        }

        int I = 0;
        int J = 0;

        for (int i = 0; i < len; i++)
        {
            I = findIndices.get(i, 0);
            J = findIndices.get(i, 1);
            try
            {
                A[I][J] = values[i];
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                throw new ArrayIndexOutOfBoundsException("setValueAt : Out-of-bound indices.");
            }
        }
    }

    /**
     * 
     * @param dim
     * @return
     */
    public int size(Dimension dim)
    {
        if (dim == Dimension.ROW)
        {
            return m;
        }
        return n;
    }

    /**
     * 
     * @return
     */
    public int[] sizeIntArr()
    {
        return new int[]
        {
                m, n
        };
    }

    /**
     * 
     * @return
     */
    public Indices size()
    {
        return new Indices(sizeIntArr());
    }

    /**
     * 
     * @return
     */
    public Indices sizeIndices()
    {
        return new Indices(sizeIntArr());
    }

    /**
     * 
     * @param i
     * @param j
     * @param val
     */
    public void set(int i, int j, int val)
    {
        try
        {
            A[i][j] = val;
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("set : Out-of-bound indices.");
        }
    }

    /**
     * 
     * @param i
     * @param j
     * @param val
     */
    public void set(int i, int j, Boolean val)
    {
        if (!logical)
        {
            throw new IllegalArgumentException(
                    "set : This method must be only applied to Indices object which are boolean (0 or 1).");
        }
        try
        {
            A[i][j] = val.booleanValue() ? 1 : 0;
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("set : Out-of-bound indices.");
        }
    }

    /**
     * 
     * @return
     */
    public boolean isVector()
    {
        return ((m == 1 && n >= 1) || (m >= 1 && n == 1));
    }

    /**
     * 
     * @return
     */
    public int length()
    {
        if (A == null)
        {
            return 0;
        }
        return m > n ? m : n;
    }

    /**
     * 
     * @return
     */
    public boolean isRowVector()
    {
        return (m == 1 && n >= 1);
    }

    /**
     * 
     * @return
     */
    public boolean isColVector()
    {
        boolean r = false;
        if (n == 1)
        {
            r = true;
        }
        return r;
    }

    /**
     * Return true if this Indices object is a column vector otherwise false.
     * 
     * @return True if it is a column , otherwise false.
     * @deprecated Use the method <B>isColVector</B> instead.
     */
    public boolean isColumnVector()
    {
        return isColVector();
    }

    /**
     * 
     * @return
     */
    public Indices sumRows()
    {
        Indices ind = new Indices(1, n, true);
        int[][] summing = ind.A;
        int sum = 0;
        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                if (Integer.MAX_VALUE == A[i][j])
                {
                    sum = Integer.MAX_VALUE;
                    break;
                }
                else
                {
                    sum += A[i][j];
                }
            }
            summing[0][j] = sum;
            sum = 0;
        }
        return ind;
    }

    /**
     * 
     * @return
     */
    public Indices sumColumns()
    {
        Indices ind = new Indices(m, 1, true);
        int[][] summing = ind.A;
        int sum = 0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (Integer.MAX_VALUE == A[i][j])
                {
                    sum = Integer.MAX_VALUE;
                    break;
                }
                else
                {
                    sum += A[i][j];
                }
            }
            summing[i][0] = sum;
            sum = 0;
        }
        return ind;
    }

    /**
     * 
     * @return
     */
    public int sumAll()
    {
        int sum = 0;
        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                if (Integer.MAX_VALUE == A[i][j])
                {
                    return Integer.MAX_VALUE;
                }
                else
                {
                    sum += A[i][j];
                }
            }
        }
        return sum;
    }

    /**
     * 
     * @param rowInd
     * @return
     */
    public int[] getRowIndicesAt(int rowInd)
    {
        int[] ind;
        try
        {
            ind = copyIndices(A[rowInd]);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("getRowIndicesAt : Out-of-bound indices.");
        }
        return ind;
    }

    /**
     * 
     * @param rowInd
     * @return
     * @deprecated Use the method <B>getRowIndicesAt</B> instead.
     */
    public int[] getRowIndicesCopyAt(int rowInd)
    {
        int[] ind;
        try
        {
            ind = copyIndices(A[rowInd]);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("getRowIndicesCopyAt : Out-of-bound indices.");
        }
        return ind;
    }

    /**
     * 
     * @param colInd
     * @return
     */
    public int[] getColIndicesAt(int colInd)
    {
        if (colInd < 0 || colInd > (n - 1))
        {
            throw new ArrayIndexOutOfBoundsException("getColumnIndicesAt : Out-of-bound indices.");
        }
        int[] ind = new int[m];
        // System.out.println(" m = "+m+"  :   colInd = "+colInd);
        for (int i = 0; i < m; i++)
        {
            // System.out.println("A["+m+"]["+colInd+"] = "+A[i][colInd]);
            ind[i] = A[i][colInd];
        }
        return ind;
    }

    /**
     * Main diagonal matrix, eg: ( 3,2,4 ) ( 3 ) If matrix A = | 6,4,1 | then
     * A.diag() = | 4 | , a 3 x 1 matrix is returned. ( 9,1,5 ) ( 5 )
     * 
     * @return the main Diagonal matrix
     */
    public Indices diag()
    {
        return diag(0);
    }

    /**
     * If this matrix is a vector (i OR column) then diag gives a square matrix
     * OR size i-by-i OR column-by-column AND puts this matrix on the main
     * diagonal. If this matrix is is NOT a vector then diag gives a matrix
     * column vector formed from the elements of the K-th diagonal of X. (
     * 3,2,4, 7 ) ( 2 ) If matrix A = | 6,4,1,-3 | then A.diag(1) = | 1 | , a 3
     * x 1 matrix is returned. ( 9,1,5, 4 ) ( 4 )
     * 
     * A.diag(-1) = ( 6 ) , return a 2 x 1 matrix ( 1 )
     * 
     * 
     * 
     * @param K
     *            diagonal integer
     * @return Diagonal matrix OR null;
     */
    public Indices diag(int K)
    {
        int smallDim = m < n ? m : n;
        Indices temp = copy();
        int[][] C = temp.getArray();
        Indices result = null;
        int[][] B = null;

        if (temp.isVector())
        { // column OR i vector

            int len = length();
            int k = Math.abs(K);
            int M = len + k, N = M;
            result = new Indices(M, N);
            B = result.getArray();
            int[] rowpacked = temp.getRowPackedCopy();

            if (K == 0)
            {
                for (int i = 0; i < M; i++)
                {
                    B[i][i] = rowpacked[i];
                }
            }
            else if (K >= 1)
            {
                for (int i = 0; i < len; i++)
                {
                    B[i][i + k] = rowpacked[i];
                }
            }
            else
            {
                for (int i = 0; i < len; i++)
                {
                    B[i + k][i] = rowpacked[i];
                }
            }

            return result;
        }

        int u = 0;

        if ((-(m - 1) <= K) && (K <= (n - 1)))
        {
            if (K == 0)
            {
                result = new Indices(smallDim, 1);
                B = result.getArray();
                for (int i = 0; i < smallDim; i++)
                {
                    B[i][0] = C[i][i];
                }
            }
            else if (K > 0)
            {
                u = smallDim - K;
                result = new Indices(u + 1, 1);
                B = result.getArray();
                for (int v = 0; v < (u + 1); v++)
                {
                    B[v][0] = C[v][v + K];
                }
            }
            else if (K < 0)
            {
                u = smallDim + K;
                result = new Indices(u, 1);
                B = result.getArray();
                for (int v = 0; v < (u); v++)
                {
                    B[v][0] = C[v - K][v];
                }
            }
        }
        return result;
    }// end

    private int[] copyIndices(int[] ind)
    {
        int[] copy = new int[ind.length];
        for (int i = 0; i < ind.length; i++)
        {
            copy[i] = ind[i];
        }
        return copy;
    }

    /**
     * Check if size(A) == size(B) *
     */
    private boolean checkIndicesDimensions(int[][] B)
    {
        int rows = B.length;
        int colOne = B[0].length;
        if (rows > 1)
        {
            for (int i = 1; i < rows; i++)
            {
                if (B[i - 1].length != B[i].length)
                {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkIndicesDimensions(Indices B)
    {
        int rows = B.getRowDimension();
        int cols = B.getColumnDimension();
        if (rows != m && cols != n)
        {
            return false;
        }
        return true;
    }

    /**
     * Element at last entry (m-th,n-th)
     * 
     * @return
     */
    public int end()
    {
        return A[m - 1][n - 1];
    }

    /**
     * Element at first entry (0-th,0-th)
     * 
     * @return
     */
    public int start()
    {
        return A[0][0];
    }

    /**
     * 
     * @param B
     * @return
     */
    public boolean equalSize(Indices B)
    {
        boolean tf = (m == B.m && n == B.n);
        return tf;
    }

    /**
     * 
     * @return
     */
    public int endInd()
    {
        return Math.max(m - 1, n - 1);
    }

    /**
     * 
     * @param num
     * @return
     */
    public boolean eqBoolean(int num)
    {
        if (isIntMaxMin(num))
        {
            return false;
        }
        boolean maxMin = false;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                maxMin = isIntMaxMin(A[i][j]);
                if (maxMin)
                {
                    return false;
                }
                else if (this.A[i][j] != num)
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 
     * @param num
     * @return
     */
    public Indices NEQ(Indices num)
    {

        // ---- added 3/3/11 ----
        if (this.numel() == 1)
        {
            return num.NEQ(A[0][0]);
        }
        if (num.numel() == 1)
        {
            return this.NEQ(num.start());
        }
        // ----------------------

        int mr = num.getRowDimension();
        int nc = num.getColumnDimension();

        if (mr != m || nc != n)
        {
            throw new IllegalArgumentException("neq : Dimensions must be the same.");
        }

        Indices ind = new Indices(m, n);
        int[][] X = ind.getArray();
        ind.setLogical(true);
        // boolean flag = false;
        int B = 0;

        // flag = false;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                B = num.get(i, j);
                if (!isIntMaxMin(A[i][j]) && !isIntMaxMin(B) && A[i][j] != B)
                {
                    X[i][j] = 1;
                }
                // else { continue; }
            }
            // if(flag) { X[i][j] = 1;}
        }
        return ind;
    }

    /**
     * 
     * @param num
     * @return
     */
    public Indices NEQ(int num)
    {
        Indices ind = new Indices(m, n);
        int[][] X = ind.getArray();
        ind.setLogical(true);
        if (isIntMaxMin(num))
        {
            return ind;
        }
        // boolean flag = false;

        // flag = false;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!isIntMaxMin(A[i][j]) && A[i][j] != num)
                {
                    X[i][j] = 1;
                }
                // else { continue; }
            }
            // if(flag) { X[i][j] = 1;}
        }
        return ind;
    }

    private ArrayList<int[]> findArrayListIJ()
    {
        ArrayList<int[]> entryIJ = new ArrayList<int[]>();
        // ArrayList<Integer> entryColInd = new ArrayList<Integer>();
        int count = 0;
        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                if (!isIntMaxMin(A[i][j]) && A[i][j] != 0)
                {
                    entryIJ.add(new int[]
                    {
                            i, j, count
                    });
                    // System.out.println("["+i+" , "+j+" , "+count+"]");
                    // entryColInd.add(new Integer(count++));
                }
                count++;
            }
        }
        // findIndex = true;
        return entryIJ;
    }

    /**
     * 
     * @return
     */
    public ArrayList<int[]> findArrayList()
    {
        ArrayList<int[]> arrayList = new ArrayList<int[]>();

        // new for loop to traverse column-wise as opposed to row-wise as done
        // previously(added in 6th, Jan, 2008)
        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                // The first conditional statement below, ie,
                // '!isIntMaxMin(A[i][j])&&' was added on 12/7/10
                if (!isIntMaxMin(A[i][j]) && A[i][j] != 0)
                {
                    arrayList.add(new int[]
                    {
                            i, j
                    });
                }
            }
        }

        /**
         * ****** old block of codes ********* (6th, Jan, 2008) for(int i=0;
         * i<m; i++){ for(int j=0; j<n; j++){ if(A[i][j]!=0) { entryIJ.add(new
         * int[]{i,j});} } }
         */
        findIndex = true;
        return arrayList;
    }

    /**
     * 
     * @return
     */
    public boolean isFindIndex()
    {
        return findIndex;
    }

    /**
     * 
     * @param B
     * @return
     */
    public Indices arrayRightDivide(Indices B)
    {
        checkIndicesDimensions(B);
        Indices X = new Indices(m, n);
        int[][] C = X.getArray();
        int val = 0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                val = B.get(i, j);
                if (val == 0)
                {
                    throw new ConditionalException("arrayRightDivide : Element at index position = B[" + i + " , " + j
                            + "] = 0, (ie, found a division by zero).");
                }
                else if (isIntMaxMin(val) || isIntMaxMin(A[i][j]))
                {
                    C[i][j] = Integer.MAX_VALUE;
                }
                else
                {
                    C[i][j] = A[i][j] / val;
                }
            }
        }
        return X;
    }

    /**
     * Element-by-element right division, C = A./B
     * 
     * @param B
     *            another matrix
     * @return A./B
     */
    public Indices arrayRightDivide(int B)
    {

        if (B == 0)
        {
            throw new ConditionalException("arrayRightDivide : Integer B = 0, (ie, a division by zero is undefined).");
        }
        boolean b1 = isIntMaxMin(B);
        boolean b2 = false;
        Indices X = new Indices(m, n);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                b2 = isIntMaxMin(A[i][j]);
                if (!b1 && !b2)
                {
                    C[i][j] = A[i][j] / B;
                }
                else
                {
                    C[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        return X;
    }

    /**
     * 
     * @param B
     * @return
     */
    public Indices arrayTimes(int B)
    {
        boolean b1 = isIntMaxMin(B);
        boolean b2 = false;
        Indices X = new Indices(m, n);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                b2 = isIntMaxMin(A[i][j]);
                if (!b1 && !b2)
                {
                    C[i][j] = A[i][j] * B;
                }
                else
                {
                    C[i][j] = Integer.MAX_VALUE;
                }
            }
        }
        return X;
    }

    /**
     * Element-by-element multiplication, C = A.*B
     * 
     * @param B
     *            another matrix
     * @return A.*B
     */
    public Indices arrayTimes(Number B)
    {

        Indices X = new Indices(m, n);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j] * B.intValue();
            }
        }
        return X;
    }

    public static boolean isIntMaxMin(int num)
    {
        boolean b1 = num == Integer.MAX_VALUE;
        boolean b2 = num == Integer.MIN_VALUE;
        return (b1 || b2);
    }

    /**
     * 
     * @param B
     * @return
     */
    public Indices arrayTimes(Indices B)
    {
        checkIndicesDimensions(B);
        Indices X = new Indices(m, n);
        int[][] C = X.getArray();

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!isIntMaxMin(A[i][j]) && !isIntMaxMin(B.get(i, j)))
                {
                    C[i][j] = A[i][j] * B.get(i, j);
                }
                else
                {
                    C[i][j] = Integer.MAX_VALUE;
                }
            }
        }
        return X;
    }

    public int[] findFirst(int K)
    {
        if (K < 1)
        {
            throw new IllegalArgumentException("findFirst : Parameter \"K\" must be a positive number.");
        }
        int countInd = 0;
        ArrayList<Integer> numInt = new ArrayList<Integer>();

        outer: for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                if (A[i][j] != 0)
                {
                    numInt.add(countInd);
                    if (numInt.size() >= K)
                    {
                        break outer;
                    }
                }
                countInd++;
            }
        }

        if (numInt.isEmpty())
        {
            return null;
        }

        int siz = numInt.size();
        int[] firstK = new int[siz];
        for (int i = 0; i < siz; i++)
        {
            firstK[i] = numInt.get(i).intValue();
        }

        return firstK;
    }

    public int[] findLast(int K)
    {
        if (K < 1)
        {
            throw new IllegalArgumentException("findFirst : Parameter \"K\" must be a positive number.");
        }

        int countInd = 0;
        ArrayList<Integer> numInt = new ArrayList<Integer>();

        int mn = m * n;

        outer: for (int j = (n - 1); j >= 0; j--)
        {
            for (int i = (m - 1); i >= 0; i--)
            {
                if (A[i][j] != 0)
                {
                    numInt.add(mn - countInd - 1);
                    if (numInt.size() >= K)
                    {
                        break outer;
                    }
                }
                countInd++;
            }
        }

        if (numInt.size() == 0)
        {
            return null;
        }

        int siz = numInt.size();
        int[] lastK = new int[siz];
        for (int i = 0; i < siz; i++)
        {
            lastK[i] = numInt.get(siz - 1 - i).intValue();
        }

        return lastK;
    }

    public Indices findFirstI(int K)
    {
        if (K < 1)
        {
            throw new IllegalArgumentException("findFirst : Parameter \"K\" must be a positive number.");
        }
        int countInd = 0;
        ArrayList<Integer> numInt = new ArrayList<Integer>();

        outer: for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                if (A[i][j] != 0)
                {
                    numInt.add(countInd);
                    if (numInt.size() >= K)
                    {
                        break outer;
                    }
                }
                countInd++;
            }
        }

        if (numInt.isEmpty())
        {
            return null;
        }

        int siz = numInt.size();
        int[] firstK = new int[siz];
        for (int i = 0; i < siz; i++)
        {
            firstK[i] = numInt.get(i).intValue();
        }

        return new Indices(firstK);
    }

    public Indices findLastI(int K)
    {
        if (K < 1)
        {
            throw new IllegalArgumentException("findFirst : Parameter \"K\" must be a positive number.");
        }

        int countInd = 0;
        ArrayList<Integer> numInt = new ArrayList<Integer>();

        int mn = m * n;

        outer: for (int j = (n - 1); j >= 0; j--)
        {
            for (int i = (m - 1); i >= 0; i--)
            {
                if (A[i][j] != 0)
                {
                    numInt.add(mn - countInd - 1);
                    if (numInt.size() >= K)
                    {
                        break outer;
                    }
                }
                countInd++;
            }
        }

        if (numInt.isEmpty())
        {
            return null;
        }

        int siz = numInt.size();
        int[] lastK = new int[siz];
        for (int i = 0; i < siz; i++)
        {
            lastK[i] = numInt.get(siz - 1 - i).intValue();
        }

        return new Indices(lastK);
    }

    public FindInd findIJ()
    {
        ArrayList<int[]> objFind = findArrayListIJ();
        // {entryIJ, entryColInd, new Integer(m), new Integer(n)}
        // ArrayList<int[]> entryIJ = (ArrayList<int[]>) objFind[0];
        // ArrayList<Integer> entryColInd = (ArrayList<Integer>) objFind[1];
        FindInd find = FindInd.create();
        if (objFind.isEmpty())
        {
            return find;
        }

        int siz = objFind.size();
        int[] index = new int[siz];
        Indices indices = new Indices(siz, 2);
        int[][] X = indices.getArray();
        int[] indVal = null;
        for (int i = 0; i < siz; i++)
        {
            indVal = objFind.get(i);
            X[i][0] = indVal[0];
            X[i][1] = indVal[1];
            index[i] = indVal[2];
        }
        indices.findIndex = true;

        find.setFindEntries(indices);
        find.setIndex(index);
        find.setFindSize(new int[]
        {
                m, n
        });

        return find;
    }

    /**
     * An Indice object is returned with size [m x 2] where indexes of the
     * logical elements are stored as { ith = Indice[i][0] AND jth =
     * Indice[i][1] }
     * 
     * 
     * 
     * @return Indices
     */
    public Indices find()
    {
        ArrayList<int[]> arrayList = findArrayList();
        if (arrayList.isEmpty())
        {
            return null;
        }
        Indices indices = new Indices(arrayList.size(), 2);
        int[][] X = indices.getArray();
        int[] indVal = null;
        for (int i = 0; i < arrayList.size(); i++)
        {
            indVal = (int[]) arrayList.get(i);
            X[i][0] = indVal[0];
            X[i][1] = indVal[1];
        }
        indices.findIndex = true;
        return indices;
    }

    /**
     * 
     * @return
     */
    public Indices findColSwap()
    {
        Indices R = find();
        if (R == null)
        {
            return null;
        }
        R = R.flipLR();
        R.findIndex = true;
        return R;
    }

    /**
     * 
     * @return
     */
    public Indices flipLR()
    {
        Indices X = new Indices(m, n);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            C[i] = MathUtil.flip(A[i]);
        }
        return X;
    }

    public static Indices randperm(int n)
    {
        // sort(rand(1, 100));
        Matrix mat = Matrix.rand(1, n);
        // Indices perm = null;
        QuickSort sort = new QuickSortMat(mat, null, true);// QuickSortMat(mat,true);
        Indices perm = sort.getIndices();
        return perm;
    }

    /**
     * 
     * @return
     */
    public Indices flipUD()
    {
        Indices X = new Indices(m, n);
        int[][] C = X.getArray();
        int[][] temp = this.getArrayCopy();
        int[] D = null;
        for (int i = 0; i < m; i++)
        {
            D = temp[(m - 1) - i];
            for (int j = 0; j < n; j++)
            {
                C[i][j] = D[j];
            }// end for

        }// end for

        return X;
    }

    /**
     * 
     * @return
     */
    public Indices findColumnwiseIndices()
    {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        int count = 0;
        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                if (A[i][j] != 0)
                {
                    arrayList.add(new Integer(count));
                }
                count++;
            }
        }
        int siz = arrayList.size();
        if (siz == 0)
        {
            return null;
        }
        Indices find = new Indices(siz, 1);
        int[][] iV = find.A;

        for (int i = 0; i < siz; i++)
        {
            iV[i][0] = arrayList.get(i).intValue();
        }
        return find;
    }

    /**
     * 
     * @return
     */
    public Indices NOT()
    {
        if (this.logical == false)
        {
            this.makeLogical();
        }
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] == 0)
                {
                    C[i][j] = 1;
                }
            }// end for

        }// end for

        return X;
    }// end method

    /**
     * 
     * @param num
     * @return
     */
    public Indices EQ(int num)
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        boolean b1 = isIntMaxMin(num);
        if (b1)
        {
            return X;
        }
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!isIntMaxMin(A[i][j]) && A[i][j] == num)
                {
                    C[i][j] = 1;
                }
            }// end for

        }// end for

        return X;
    }

    public Indices EQ(Indices B)
    {
        // ---- added 3/3/11 ----
        if (this.numel() == 1)
        {
            return B.EQ(A[0][0]);
        }
        if (B.numel() == 1)
        {
            return this.EQ(B.start());
        }
        // ----------------------

        checkIndicesDimensions(B);
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        boolean b1 = false;
        boolean b2 = false;

        int[][] M = B.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                b1 = isIntMaxMin(A[i][j]);
                b2 = isIntMaxMin(M[i][j]);
                if ((!b1 && !b2) && A[i][j] == M[i][j])
                {
                    C[i][j] = 1;
                }
            }// end for

        }// end for

        return X;
    }// end method

    /**
     * 
     * @param Y
     * @return
     */
    public Indices AND(Indices Y)
    {
        int mY = Y.m;
        int nY = Y.n;
        if (m != mY || n != nY)
        {
            throw new ArrayIndexOutOfBoundsException("and : Indices dimensions must be the same.");
        }
        Indices X = new Indices(m, n);
        X.setLogical(true);

        if (Y.isLogical() == false)
        {
            Y.makeLogical();
        }
        if (this.isLogical() == false)
        {
            this.makeLogical();
        }

        int[][] xArr = X.getArray();
        int[][] yArr = Y.getArray();

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] == 1 && yArr[i][j] == 1)
                {
                    xArr[i][j] = 1;
                }
            }
        }

        return X;
    }

    /**
     * 
     * @param Y
     * @return
     */
    public Indices AND(int Y)
    {

        Indices X = new Indices(m, n);
        X.setLogical(true);

        if (this.isLogical() == false)
        {
            this.makeLogical();
        }

        int[][] xArr = X.getArray();
        int yArr = Y == 0 ? 0 : 1;

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] == 1 && yArr == 1)
                {
                    xArr[i][j] = 1;
                }
            }
        }

        return X;
    }

    /**
     * 
     * @return
     */
    public Indices copy()
    {
        if (A == null)
        {
            return new Indices(null, m, n);
        }
        Indices ind = new Indices(m, n);
        ind.findIndex = findIndex;
        ind.logical = logical;
        int[][] X = ind.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                X[i][j] = A[i][j];
            }
        }
        return ind;
    }

    /**
     * 
     * @return
     */
    public boolean trueAll()
    {
        if (this.logical == false)
        {
            this.makeLogical();
        }
        int total = m * n;
        int count = 0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (this.A[i][j] == 1)
                {
                    count++;
                }
                else
                {
                    return false;
                }
            }
        }
        if (total == count)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public Indices isnan()
    {
        return ismaxinteger();
    }

    /**
     * 
     * @return
     */
    public Indices ismaxinteger()
    {
        Indices ind = new Indices(m, n);
        int[][] X = ind.getArray();
        ind.setLogical(true);
        // boolean flag = false;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                // new line (added on 6th, Jan, 2008)
                // if(A[i][j]==Integer.MAX_VALUE || A[i][j]>Integer.MAX_VALUE ||
                // A[i][j]> 2100000000) { X[i][j] = 1; }
                if (isIntMaxMin(A[i][j]) /*
                                          * == Integer.MAX_VALUE
                                          */)
                {
                    X[i][j] = 1;
                } // old line

            }
            // if(flag) { X[i][j] = 1;}
        }
        return ind;
    }

    public boolean isScalar()
    {
        if (this.A == null)
        {
            return false;
        }
        return (numel() == 1);
    }

    public boolean isSquare()
    {
        return m == n;
    }

    public void set(int[][] intArr)
    {
        if (intArr == null)
        {
            this.m = 0;
            this.n = 0;
        }
        else
        {
            this.m = intArr.length;
            this.n = intArr[0].length;
        }
        this.A = intArr;
    }

    public boolean isNull()
    {
        return this.A == null;
    }

    /**
     * 
     * @param Dim
     * @return
     */
    public Indices anyvec(Dimension Dim)
    {

        int[][] any = null;
        int temp = 0;

        if (isVector() == false)
        {
            throw new IllegalArgumentException("anyvec : Data must be a vector and not a matrix. ");
        }

        if (Dim == Dimension.ROW)
        {
            any = new int[1][n];
            for (int j = 0; j < n; j++)
            {
                for (int i = 0; i < m; i++)
                {
                    if (A[i][j] != 0)
                    {
                        any[0][j] = 1;
                    }
                }
            }
        }
        else if (Dim == Dimension.COL)
        {
            any = new int[m][1];
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (A[i][j] != 0)
                    {
                        any[i][0] = 1;
                    }
                }
            }
        }
        else
        {
            throw new IllegalArgumentException("anyvec : Dimension  " + Dim.toString() + " , not supported.");
        }
        return new Indices(any);
    }

    /**
     * 
     * @param dim
     * @return
     */
    public Indices ANY(Dimension dim)
    {
        return (new LogicalAnyInd(this, dim)).getIndices();
    }

    /**
     * 
     * @return
     */
    public Indices ANY()
    {
        return (new LogicalAnyInd(this)).getIndices();
    }

    /**
     * 
     * @return
     */
    public Indices ANY_old()
    {
        // if(this.logical==false) { this.makeLogical(); }
        Indices ind = new Indices(1, n);
        int[][] X = ind.getArray();
        ind.setLogical(true);
        // boolean flag = false;
        for (int j = 0; j < n; j++)
        {
            // flag = false;
            for (int i = 0; i < m; i++)
            {
                if (A[i][j] != 0)
                {
                    X[0][j] = 1;
                }
                // else { continue; }
            }
            // if(flag) { X[i][j] = 1;}
        }
        return ind;

    }

    /**
     * 
     * @return
     */
    public boolean anyBoolean()
    {

        // int countInd = 0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] != 0)
                {
                    return true;
                }
                // else { countInd++; }
            }
        }
        return false;

    }

    /**
     * 
     * @return
     */
    public boolean allBoolean()
    {

        // int countInd = 0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] == 0)
                {
                    return false;
                }
                // else { countInd++; }
            }
        }
        return true;

    }

    /**
     * 
     * @param i
     * @param j
     * @return
     */
    public Indices getAsIndices(int i, int j)
    {
        int val = get(i, j);
        return new Indices(1, 1, val);
    }

    /**
     * Get a submatrix index.
     * 
     * @return A(i0:i1,j0:j1)
     * @param i0
     *            Initial row index
     * @param i1
     *            Final row index
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     */
    public Indices getIndices(int i0, int i1, int j0, int j1)
    {
        Indices X = new Indices(i1 - i0 + 1, j1 - j0 + 1);
        int[][] B = X.getArray();
        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    B[i - i0][j - j0] = A[i][j];
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
     * Get a submatrix.
     * 
     * @return A(i0:i1,c(:))
     * @param i0
     *            Initial row index
     * @param i1
     *            Final row index
     * @param c
     *            Array of column indices.
     */
    public Indices getIndices(int i0, int i1, int[] c)
    {
        Indices X = new Indices(i1 - i0 + 1, c.length);
        int[][] B = X.getArray();
        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    B[i - i0][j] = A[i][c[j]];
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
     * Get a submatrix.
     * 
     * @return A(r(:),j0:j1)
     * @param r
     *            Array of row indices.
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     */
    public Indices getIndices(int[] r, int j0, int j1)
    {
        Indices X = new Indices(r.length, j1 - j0 + 1);
        int[][] B = X.getArray();
        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    B[i][j - j0] = A[r[i]][j];
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
     * Get a submatrix.
     * 
     * @return A(r(:),c(:))
     * @param r
     *            Array of row indices.
     * @param c
     *            Array of column indices.
     */
    public Indices getIndices(int[] r, int[] c)
    {
        Indices X = new Indices(r.length, c.length);
        int[][] B = X.getArray();
        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    B[i][j] = A[r[i]][c[j]];
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
     * Set a submatrix.
     * 
     * @param i0
     *            Initial row index
     * @param i1
     *            Final row index
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @param X
     *            A(i0:i1,j0:j1)
     */
    public void setIndices(int i0, int i1, int j0, int j1, Indices X)
    {
        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    A[i][j] = X.get(i - i0, j - j0);
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param i0
     *            Initial row index
     * @param i1
     *            Final row index
     * @param c
     *            Array of column indices.
     * @param X
     *            A(i0:i1,c(:))
     */
    public void setIndices(int i0, int i1, int[] c, Indices X)
    {
        /*
         * System.out.println("------ X-----"); X.print(4);
         * System.out.println("------ this 1-----"); this.print(4);
         */
        int intc = 0;
        int xval = 0;

        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    // System.out.println("c["+j+"] = X.get("+(i-i0)+","+j+") = "+X.get(i-i0,j));
                    intc = c[j];
                    xval = X.get(i - i0, j);
                    A[i][intc] = xval;
                    // System.out.println(" j = "+j+"  ;   intc = "+intc+"  ;   xval = "+xval);
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        // System.out.println("------ this 2-----");
        // this.print(4);

    }

    /**
     * Set a submatrix.
     * 
     * @param r
     *            Array of row indices.
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @param X
     *            A(r(:),j0:j1)
     */
    public void setIndices(int[] r, int j0, int j1, Indices X)
    {
        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    A[r[i]][j] = X.get(i, j - j0);
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param r
     *            Array of row indices.
     * @param c
     *            Array of column indices.
     * @param X
     *            A(r(:),c(:))
     */
    public void setIndices(int[] r, int[] c, int X)
    {
        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    A[r[i]][c[j]] = X;
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param r
     *            Array of row indices.
     * @param c
     *            Array of column indices.
     * @param X
     *            A(r(:),c(:))
     */
    public void setIndices(int[] r, int[] c, Indices X)
    {
        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    A[r[i]][c[j]] = X.get(i, j);
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
     * 
     * @param fromFind
     * @param values
     * @deprecated Use the method <B>setFromFind</B> instead.
     */
    public void setValuesAtIndices(Indices fromFind, Indices values)
    {
        setFromFind(fromFind, values);
    }

    /**
     * 
     * @param fromFind
     * @param val
     */
    public void setFindIj(FindInd fromFind, int val)
    {
        if (fromFind == null || fromFind.isNull())
        {
            return;
        }
        int len = fromFind.numel();
        Indices mat = new Indices(1, len, val);
        setFindIj(fromFind, mat);
    }

    /**
     * 
     * @param fromFind
     * @param values
     */
    public void setFindIj(FindInd fromFind, Indices values)
    {
        if (fromFind == null || fromFind.isNull())
        {
            return;
        }

        if (values.isVector() == false)
        {
            throw new IllegalArgumentException("setFindIj : Parameter 'values' must be a row or column vector.");
        }

        // if (!fromFind.isFindIndex()) {
        // throw new
        // IllegalArgumentException("setValuesAtIndices : Parameter 'indices' must be an object from a \"find()\" method call.");
        // }
        int indRowLen = fromFind.numel();// .getRowDimension();
        int lenValues = values.length();
        if (indRowLen != lenValues)
        {
            throw new IllegalArgumentException("setFindIj : Lengths of 'indices' and 'values' must be the same.");
        }

        if (!fromFind.equalSize(new int[]
        {
                m, n
        }))
        {
            throw new IllegalArgumentException(
                    "setFindIj : Object \"fromFind\" must have the same size as this indices.");
        }

        int val = 0;
        int i = 0, j = 0;
        Indices find = fromFind.getFindEntries();
        if (values.isRowVector())
        {
            for (int k = 0; k < indRowLen; k++)
            {
                i = find.get(k, 0);
                j = find.get(k, 1);
                val = values.get(0, k);
                set(i, j, val);
            }
        }
        else
        {
            for (int k = 0; k < indRowLen; k++)
            {
                i = find.get(k, 0);
                j = find.get(k, 1);
                val = values.get(k, 0);
                set(i, j, val);
            }
        }

    }

    /**
     * 
     * @param fromFind
     * @param values
     */
    public void setFromFind(Indices fromFind, Indices values)
    {
        if (fromFind == null)
        {
            return;
        }

        if (values.isVector() == false)
        {
            throw new IllegalArgumentException("setFromFind : Parameter 'values' must be a row or column vector.");
        }

        if (!fromFind.findIndex)
        {
            throw new IllegalArgumentException(
                    "setFromFind : Parameter 'indices' must be an object from a \"find()\" method call.");
        }
        int indRowLen = fromFind.getRowDimension();
        int lenValues = values.length();
        if (indRowLen != lenValues)
        {
            throw new IllegalArgumentException("setFromFind : Lengths of 'indices' and 'values' must be the same.");
        }

        int val = 0;
        int i, j;
        if (values.isRowVector())
        {
            for (int k = 0; k < indRowLen; k++)
            {
                i = fromFind.get(k, 0);
                j = fromFind.get(k, 1);
                val = values.get(0, k);
                set(i, j, val);
            }
        }
        else
        {
            for (int k = 0; k < indRowLen; k++)
            {
                i = fromFind.get(k, 0);
                j = fromFind.get(k, 1);
                val = values.get(k, 0);
                set(i, j, val);
            }
        }
    }

    /**
     * 
     * @param fromFind
     * @param val
     */
    public void setFromFind(Indices fromFind, int val)
    {
        if (fromFind == null)
        {
            return;
        }
        int len = fromFind.getRowDimension();
        Indices mat = new Indices(1, len, val);
        setFromFind(fromFind, mat);
    }

    /**
     * 
     * @param fromFind
     * @param bool
     */
    public void setFromFind(Indices fromFind, boolean bool)
    {
        if (!isLogical())
        {
            throw new IllegalArgumentException("setFromFind : This object must be a logical.");
        }
        int val = (bool == true ? 1 : 0);
        setFromFind(fromFind, val);
    }

    public Indices setXtendedColAt(int index, Indices matCols)
    {
        if (matCols == null)
        {
            throw new IllegalArgumentException(" setXtendedColAt: Parameter \"matCols\" must be non-null.");
        }
        if (!matCols.isVector())
        {
            throw new IllegalArgumentException(
                    " setXtendedColAt: Parameter \"matCols\" must be a vector and not a matrix.");
        }
        Indices mat = null;
        if (matCols.isRowVector())
        {
            mat = matCols.toColVector();
        }
        else
        {
            mat = matCols;
        }
        int m2 = matCols.length();
        if (this.m != m2)
        {
            throw new IllegalArgumentException(" setXtendedColAt: Inconsistent row dimensions.");
        }

        Indices X = null;
        if ((index + 1) <= this.n)
        {
            X = this.copy();
        }
        else
        {
            int ind = (index + 1) - n;

            X = this.mergeH(Indices.zeros(m, ind));
        }// new Matrix(index+1,n);

        X.setColumnAt(index, mat);

        return X;
    }

    public void setAllTo(int val)
    {
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                this.A[i][j] = val;
            }
        }
    }

    /**
     * 
     * @param index
     *            int
     * @param matColumns
     *            Matrix
     */
    public void setColumnAt(int index, Indices matColumns)
    {
        setColumns(new int[]
        {
            index
        }, matColumns);
    }

    /**
     * 
     * @param index
     * @param valColumns
     */
    public void setColumnAt(int index, int valColumns)
    {
        setColumns(new int[]
        {
            index
        }, valColumns);
    }

    public void setColumns(int from, int to, int valColumns)
    {
        if (from > to)
        {
            throw new IllegalArgumentException(" setColumns: Value for parameter \"from\" (= " + from
                    + ") must be equal to or less than that of \"to\" (= " + to + ").");
        }
        int[] indices = Indices.linspace(from, to).getRowPackedCopy();
        int len = indices.length;
        Indices matColumns = new Indices(m, len, valColumns);
        setColumns(indices, matColumns);
    }

    public void setColumns(int from, int to, Indices matColumns)
    {
        if (from > to)
        {
            throw new IllegalArgumentException(" setColumns: Value for parameter \"from\" (= " + from
                    + ") must be equal to or less than that of \"to\" (= " + to + ").");
        }
        int[] arr = Indices.linspace(from, to).getRowPackedCopy();
        this.setColumns(arr, matColumns);
    }

    /**
     * 
     * @param indices
     *            int[]
     * @param matColumns
     *            Matrix
     */
    public void setColumns(int[] indices, Indices matColumns)
    {
        if (matColumns.getRowDimension() != m)
        {
            throw new IllegalArgumentException(" setColumns: Inconsistent size of columns.");
        }
        int len = indices.length;
        if (matColumns.getColumnDimension() != len)
        {
            throw new IllegalArgumentException(" setColumns: Inconsistent size of 'indices' and 'Columns'.");
        }
        for (int i = 0; i < len; i++)
        {
            if (indices[i] > (n - 1) || indices[i] < 0)
            {
                throw new IllegalArgumentException(" setColumns: Array out-of-bounds.");
            }
            Indices temp = matColumns.getColumnAt(i);
            setIndices(0, m - 1, indices[i], indices[i], temp);
        }
    }

    /**
     * 
     * @param indices
     * @param valColumns
     */
    public void setColumns(int[] indices, int valColumns)
    {
        int len = indices.length;
        Indices matColumns = new Indices(m, len, valColumns);
        setColumns(indices, matColumns);
    }

    /**
     * 
     * @param indices
     *            int[]
     * @return Matrix
     */
    public Indices getRows(int[] indices)
    {
        int len = indices.length;
        Indices val = new Indices(len, n);
        for (int i = 0; i < len; i++)
        {
            if (indices[i] > (m - 1) || indices[i] < 0)
            {
                throw new IllegalArgumentException(" getRows: Array out-of-bounds.");
            }
            Indices temp = getIndices(indices[i], indices[i], 0, n - 1);
            val.setIndices(i, i, 0, n - 1, temp);
        }
        return val;
    }// end method

    public Indices getRows(int from, int to)
    {
        if (from > to)
        {
            throw new IllegalArgumentException(" getRows: Value for parameter \"from\" (= " + from
                    + ") must be equal to or less than that of \"to\" (= " + to + ").");
        }
        int[] ind = Indices.linspace(from, to).getRowPackedCopy();
        return getRows(ind);
    }

    /**
     * 
     * @param ind
     * @return
     */
    public Indices getRowAt(int ind)
    {
        return getRows(new int[]
        {
            ind
        });
    }

    /**
     * 
     * @param from
     * @param to
     * @return
     */
    public Indices getColumns(int from, int to)
    {
        if (from > to)
        {
            throw new IllegalArgumentException(" getColumns: Value for parameter \"from\" (= " + from
                    + ") must be equal to or less than that of \"to\" (= " + to + ").");
        }
        int[] arr = Indices.linspace(from, to).getRowPackedCopy();
        return this.getColumns(arr);
    }

    /**
     * 
     * @param indices
     *            int[]
     * @return Matrix
     */
    public Indices getColumns(int[] indices)
    {
        int len = indices.length;
        Indices val = new Indices(m, len);
        for (int i = 0; i < len; i++)
        {
            if (indices[i] > (n - 1) || indices[i] < 0)
            {
                throw new IllegalArgumentException(" getColumns: Array out-of-bounds.");
            }
            Indices temp = this.getIndices(0, m - 1, indices[i], indices[i]);
            val.setIndices(0, m - 1, i, i, temp);
        }
        return val;
    }// end method

    /**
     * 
     * @param ind
     * @return
     */
    public Indices getColumnAt(int ind)
    {
        return getColumns(new int[]
        {
            ind
        });
    }

    /**
     * 
     * @return
     */
    public Indices generateIndices()
    {
        return generateIndices(true);
    }

    /**
     * 
     * @param fromTopDown
     * @return
     */
    public Indices generateIndices(boolean fromTopDown)
    {
        Indices indices = new Indices(m, n);

        if (this.isVector())
        {
            if (this.isRowVector())
            {
                for (int i = 0; i < n; i++)
                {
                    indices.set(0, i, i);
                }
            }
            else
            {
                for (int j = 0; j < m; j++)
                {
                    indices.set(j, 0, j);
                }
            }
            return indices;
        }

        int[][] M = indices.getArray();
        if (fromTopDown)
        {
            for (int j = 0; j < n; j++)
            {
                for (int i = 0; i < m; i++)
                {
                    M[i][j] = i;
                }
            }
        }
        else
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    M[i][j] = j;
                }
            }
        }
        return indices;
    }

    public boolean hasNegatives()
    {
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] < 0)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * Return an 'object' array of length 3 with object[0] the unique matrix
     * (collapsed into a vector), object[1] and indices vector of the
     * row-position and object[2] is an indices vector of the column-position.
     */
    public Object[] getUnique()
    {
        return JOps.uniqueInd(this);
    }

    /**
     * 
     * @param Y
     * @return
     */
    public Indices OR(Indices Y)
    {
        int mY = Y.m;
        int nY = Y.n;
        if (m != mY || n != nY)
        {
            throw new ArrayIndexOutOfBoundsException("or : Indices dimensions must be the same.");
        }
        Indices X = new Indices(m, n);
        X.setLogical(true);

        if (Y.isLogical() == false)
        {
            Y.makeLogical();
        }
        if (this.isLogical() == false)
        {
            this.makeLogical();
        }

        int[][] xArr = X.getArray();
        int[][] yArr = Y.getArray();

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] == 1 || yArr[i][j] == 1)
                {
                    xArr[i][j] = 1;
                }
            }
        }

        return X;
    }

    /**
     * 
     * @param findIndices
     * @return
     * @deprecated Use the method <B>getFromFind( )</B>, instead.
     */
    public Indices indicesFromFind(Indices findIndices)
    {
        if (findIndices.isFindIndex() == false)
        {
            throw new IllegalArgumentException(
                    " indicesFromFindIndices : Parameter \"findIndices\" must be logical as a result of method call to 'findIndices()'.");
        }
        int r = findIndices.getRowDimension();
        Indices R = new Indices(r, 1);
        int row = 0;
        int col = 0;

        for (int i = 0; i < r; i++)
        {
            row = findIndices.get(i, 0);
            col = findIndices.get(i, 1);
            R.set(i, 0, this.A[row][col]);
        }

        return R;
    }

    /**
     * 
     * @param w
     */
    public void print(int w)
    {
        print(w, 0);
    }

    /**
     * 
     * @param w
     * @param d
     */
    public void print(int w, int d)
    {
        print(new PrintWriter(System.out, true), w, d);
    }

    /**
     * Print the matrix to the output stream. Line the elements up in columns
     * with a Fortran-like 'Fw.d' style format.
     * 
     * @param output
     *            Output stream.
     * @param w
     *            Column width.
     * @param d
     *            Number of digits after the decimal.
     */
    public void print(PrintWriter output, int w, int d)
    {
        DecimalFormat format = new DecimalFormat();
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        print(output, format, w + 2);
    }

    /**
     * Print the matrix to stdout. Line the elements up in columns. Use the
     * format object, AND right justify within columns of width characters.
     * 
     * @param format
     *            A Formatting object for individual elements.
     * @param width
     *            Field width for each column.
     */
    public void print(NumberFormat format, int width)
    {
        print(new PrintWriter(System.out, true), format, width);
    }

    // DecimalFormat is a little disappointing coming from Fortran OR C's
    // printf.
    // Since it doesn't pad on the left, the elements will come out different
    // widths. Consequently, we'll pass the desired column width in as an
    // argument AND do the extra padding ourselves.
    /**
     * Print the matrix to the output stream. Line the elements up in columns.
     * Use the format object, AND right justify within columns of width
     * characters.
     * 
     * @param output
     *            the output stream.
     * @param format
     *            A formatting object to format the matrix elements
     * @param width
     *            Column width.
     */
    public void print(PrintWriter output, NumberFormat format, int width)
    {
        output.println(); // start on new line.
        if (this.A != null)
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    String s = "";
                    if (isIntMaxMin(A[i][j]))
                    {
                        s = "?";
                    }
                    else
                    {
                        s = format.format(A[i][j]);
                    }// format the number

                    int padding = Math.max(1, width - s.length()); // At _least_
                                                                   // 1 space

                    for (int k = 0; k < padding; k++)
                    {
                        output.print(' ');
                    }
                    output.print(s);
                }
                output.println();
            }
        }
        else
        {
            String empty = "Empty matrix: " + m + "-by-" + n;
            output.print(empty);
        }

        output.println(); // end with blank line.

    }

    public void printSize(String name)
    {
        System.out.print("\n " + name + " = [ " + m + " x " + n + " ]\n");
    }

    public void printInLabel(String name)
    {
        printInLabel(name, 6);
    }

    public void printInLabel(String name, int width)
    {
        printInLabel(name, width, '-', 25);
    }

    public void printInLabel(String name, int width, char ch, int numChar)
    {
        String line = MathUtil.genStringChars(ch, numChar);
        System.out.print("\n" + line + " " + name + " " + line);
        this.print(width);
    }

    /**
     * 
     * @param B
     * @return
     */
    public Indices minus(Indices B)
    {
        // ---------- modified/added on 16/01/11 ---------
        if (B.numel() == 1)
        {
            return this.minus(B.start());
        }
        if (this.numel() == 1)
        {
            return B.minus(A[0][0]);
        }
        // -----------------------------------------------
        checkIndicesDimensions(B);
        Indices X = new Indices(m, n);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                // C[i][j] = A[i][j] - B.get(i,j);
                boolean bool1 = Integer.MAX_VALUE == A[i][j];
                boolean bool2 = Integer.MAX_VALUE == B.get(i, j);
                boolean bool3 = Integer.MIN_VALUE == A[i][j];
                boolean bool4 = Integer.MIN_VALUE == B.get(i, j);
                if (bool1 || bool2 || bool3 || bool4)
                {
                    C[i][j] = Integer.MAX_VALUE;
                }
                else
                {
                    C[i][j] = A[i][j] - B.get(i, j);
                }
            }
        }
        return X;
    }

    /**
     * C = A - B
     * 
     * @param B
     *            another matrix
     * @return A - B
     */
    public Indices minus(int B)
    {

        Indices X = new Indices(m, n);
        int[][] C = X.getArray();
        boolean bool2 = Integer.MAX_VALUE == B;
        boolean bool3 = Integer.MIN_VALUE == B;

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                // C[i][j] = A[i][j] - B ;
                boolean bool1 = Integer.MAX_VALUE == A[i][j];
                boolean bool4 = Integer.MIN_VALUE == A[i][j];
                if (bool1 || bool2 || bool3 || bool4)
                {
                    C[i][j] = Integer.MAX_VALUE;
                }
                else
                {
                    C[i][j] = A[i][j] - B;
                }
            }
        }
        return X;
    }

    /**
     * 
     * @param B
     * @return
     */
    public Indices plus(Indices B)
    {
        // ---------- modified/added on 16/01/11 ---------
        if (B.numel() == 1)
        {
            return this.plus(B.start());
        }
        if (this.numel() == 1)
        {
            return B.plus(A[0][0]);
        }
        // -----------------------------------------------

        checkIndicesDimensions(B);
        Indices X = new Indices(m, n);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                /*
                 * boolean bool1 = Integer.MAX_VALUE == A[i][j]; boolean bool2 =
                 * Integer.MAX_VALUE == B.get(i, j); if (bool1 || bool2) {
                 * C[i][j] = Integer.MAX_VALUE; } else { C[i][j] = A[i][j] +
                 * B.get(i, j); }
                 */

                boolean bool1 = Integer.MAX_VALUE == A[i][j];
                boolean bool2 = Integer.MAX_VALUE == B.get(i, j);
                boolean bool3 = Integer.MIN_VALUE == A[i][j];
                boolean bool4 = Integer.MIN_VALUE == B.get(i, j);
                if (bool1 || bool2 || bool3 || bool4)
                {
                    C[i][j] = Integer.MAX_VALUE;
                }
                else
                {
                    C[i][j] = A[i][j] + B.get(i, j);
                }
            }
        }
        return X;
    }

    /**
     * C = A - B
     * 
     * @param B
     *            another matrix
     * @return A - B
     */
    public Indices plus(int B)
    {

        Indices X = new Indices(m, n);
        int[][] C = X.getArray();
        boolean bool2 = Integer.MAX_VALUE == B;
        boolean bool3 = Integer.MIN_VALUE == B;

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                // C[i][j] = A[i][j] + B ;
                boolean bool1 = Integer.MAX_VALUE == A[i][j];
                boolean bool4 = Integer.MIN_VALUE == A[i][j];
                if (bool1 || bool2 || bool3 || bool4)
                {
                    C[i][j] = Integer.MAX_VALUE;
                }
                else
                {
                    C[i][j] = A[i][j] + B;
                }
            }
        }
        return X;
    }

    public static Indices linspace(int Lower, int Upper)
    {
        return linspace(Lower, Upper, 1);
    }

    public static Indices linspace(int Lower, int Upper, int Incr)
    {
        int lower = Lower;
        int upper = Upper;
        int incr = Incr;
        int result = 0;
        if (upper < lower)
        {
            throw new IllegalArgumentException(" linIncrement: Upper-bound (= " + upper
                    + ") , must be greater than the Lower-bound (= " + lower + ") :");
        }

        if (Incr < 1)
        {
            throw new IllegalArgumentException(" linIncrement: The increment amount (= " + Incr
                    + ") , must be at least one (allowable minimum).");
        }

        if ((incr > (upper - lower)) || (upper == lower))
        {
            // throw new
            // IllegalArgumentException(" linIncrement: Incr = "+incr+" , must be a positive number, which is less than "+(upper-lower)+"");
            return new Indices(1, 1, lower);
        }
        ArrayList<Integer> vector = new ArrayList<Integer>();
        // vector.add(new Double(lower));
        int count = 0;
        result = lower;
        while (result <= upper)
        {
            vector.add(new Integer(result));
            count++;
            result = lower + count * incr;
        }
        int siz = vector.size();
        Indices X = new Indices(1, siz);
        int[][] C = X.getArray();
        // Object[] ob = vector.toArray();
        for (int i = 0; i < siz; i++)
        {
            C[0][i] = vector.get(i).intValue();
        }
        return X;
    }// end method

    public static List<Integer> linspaceList(int Lower, int Upper)
    {
        return linspaceList(Lower, Upper, 1);
    }

    public static List<Integer> linspaceList(int Lower, int Upper, int Incr)
    {
        int lower = Lower;
        int upper = Upper;
        int incr = Incr;
        int result = 0;
        if (upper < lower)
        {
            throw new IllegalArgumentException(" linIncrement: Upper-bound (= " + upper
                    + ") , must be greater than the Lower-bound (= " + lower + ") :");
        }

        if (Incr < 1)
        {
            throw new IllegalArgumentException(" linIncrement: The increment amount (= " + Incr
                    + ") , must be at least one (allowable minimum).");
        }

        ArrayList<Integer> vector = new ArrayList<Integer>();

        if ((incr > (upper - lower)) || (upper == lower))
        {
            // throw new
            // IllegalArgumentException(" linIncrement: Incr = "+incr+" , must be a positive number, which is less than "+(upper-lower)+"");
            vector.add(lower);
            return vector;// new Indices(1, 1, lower);
        }

        int count = 0;
        result = lower;
        while (result <= upper)
        {
            vector.add(result);
            count++;
            result = lower + count * incr;
        }

        return vector;
    }// end method

    /**
     * 
     * @param Upper
     * @param Lower
     * @param Decr
     * @return
     */
    public static Indices linDecrement(int Upper, int Lower, int Decr)
    {
        int lower = Lower;
        int upper = Upper;
        int decr = Decr;
        int result = 0;
        if (upper < lower)
        {
            throw new IllegalArgumentException(" linIncrement: Upper-bound (= " + upper
                    + ") , must be greater than the Lower-bound (= " + lower + ") :");
        }

        if (Decr <= 0)
        {
            throw new IllegalArgumentException(" linIncrement: The increment amount (= " + Decr
                    + ") , must be greater than zero :");
        }

        if ((decr > (upper - lower)) || (upper == lower))
        {
            // throw new
            // IllegalArgumentException(" linIncrement: Incr = "+incr+" , must be a positive number, which is less than "+(upper-lower)+"");
            return new Indices(1, 1, upper);
        }

        ArrayList<Integer> vector = new ArrayList<Integer>();
        // vector.add(new Double(lower));
        int count = 0;
        result = upper;
        while (result >= lower)
        {
            vector.add(result);
            count++;
            result = upper - count * decr;
        }
        int siz = vector.size();
        Indices X = new Indices(1, siz);
        int[][] C = X.getArray();
        // Object[] ob = vector.toArray();
        for (int i = 0; i < siz; i++)
        {
            C[0][i] = vector.get(i).intValue();
        }
        return X;

    }

    /**
     *
     */
    public static Indices linIncrement(int Lower, int Upper, int Incr)
    {
        int lower = Lower;
        int upper = Upper;
        int incr = Incr;
        int result = 0;
        if (upper < lower)
        {
            throw new IllegalArgumentException(" linIncrement: Upper-bound (= " + upper
                    + ") , must be greater than the Lower-bound (= " + lower + ") :");
        }

        if (Incr <= 0)
        {
            throw new IllegalArgumentException(" linIncrement: The increment amount (= " + Incr
                    + ") , must be greater than zero :");
        }

        if ((incr > (upper - lower)) || (upper == lower))
        {
            // throw new
            // IllegalArgumentException(" linIncrement: Incr = "+incr+" , must be a positive number, which is less than "+(upper-lower)+"");
            return new Indices(1, 1, lower);
        }

        ArrayList<Integer> vector = new ArrayList<Integer>();
        // vector.add(new Double(lower));
        int count = 0;
        result = lower;
        while (result <= upper)
        {
            vector.add(new Integer(result));
            count++;
            result = lower + count * incr;
        }
        int siz = vector.size();
        Indices X = new Indices(1, siz);
        int[][] C = X.getArray();
        // Object[] ob = vector.toArray();
        for (int i = 0; i < siz; i++)
        {
            C[0][i] = vector.get(i).intValue();
        }
        X.setSorted(true);
        return X;
    }// end method

    /**
     * 
     * @param Lower
     * @param Upper
     * @param Incr
     * @return
     * @deprecated Use the class <B>linspace</B>, instead.
     */
    public static Indices intLinspaceIncrement(int Lower, int Upper, int Incr)
    {
        int lower = Lower;
        int upper = Upper;
        int incr = Incr;
        int result = 0;
        if (upper < lower)
        {
            throw new IllegalArgumentException(" linIncrement: Upper-bound (= " + upper
                    + ") , must be greater than the Lower-bound (= " + lower + ") :");
        }

        if (Incr < 1)
        {
            throw new IllegalArgumentException(" linIncrement: The increment amount (= " + Incr
                    + ") , must be at least one.");
        }

        if ((incr > (upper - lower)) || (upper == lower))
        {
            // throw new
            // IllegalArgumentException(" linIncrement: Incr = "+incr+" , must be a positive number, which is less than "+(upper-lower)+"");
            return new Indices(1, 1, lower);
        }
        ArrayList<Integer> vector = new ArrayList<Integer>();
        // vector.add(new Double(lower));
        int count = 0;
        result = lower;
        while (result <= upper)
        {
            vector.add(new Integer(result));
            count++;
            result = lower + count * incr;
        }
        int siz = vector.size();
        Indices X = new Indices(1, siz);
        int[][] C = X.getArray();
        // Object[] ob = vector.toArray();
        for (int i = 0; i < siz; i++)
        {
            C[0][i] = vector.get(i).intValue();
        }
        return X;
    }// end method

    public static Indices arrayList2Mat(List<Integer> list)
    {
        Matrix matOfInt = Matrix.arrayList2Mat(list);
        return matOfInt.toIndices();
    }

    /**
     * 
     * @param Lower
     * @param Upper
     * @return
     */
    public static Indices intLinspaceIncrement(int Lower, int Upper)
    {
        return intLinspaceIncrement(Lower, Upper, 1);
    }// end method

    /**
     * 
     * @return
     */
    public Indices toRowVector()
    {
        int[] val = getRowPackedCopy();
        int[][] R = new int[1][];
        R[0] = val;
        return new Indices(R);
    }

    /**
     * 
     * @return
     */
    public Indices toColVector()
    {
        int[] val = getColumnPackedCopy();
        int[][] R = new int[val.length][1];
        for (int j = 0; j < val.length; j++)
        {
            R[j][0] = val[j];
        }
        return new Indices(R);
    }

    /**
     * 
     * @return
     */
    public int[] getColumnPackedCopy()
    {
        int[] vals = new int[m * n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                vals[i + j * m] = A[i][j];
            }
        }
        return vals;
    }

    /**
     * Make a one-dimensional row packed copy of the internal array.
     * 
     * @return Matrix elements packed in a one-dimensional array by rows.
     */
    public int[] getRowPackedCopy()
    {
        int[] vals = new int[m * n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                vals[i * n + j] = A[i][j];
            }
        }
        return vals;
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices LT(int b)
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        if (isIntMaxMin(b))
        {
            return X;
        }
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                // if(! MathsUtil.nan(A[i][j], b )){
                if (!isIntMaxMin(A[i][j]) && A[i][j] < b)
                {
                    C[i][j] = 1;
                }
                // else {C[i][j] = 0.0d;}
                // }
            }
        }
        return X;
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices LTEQ(int b)
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        if (isIntMaxMin(b))
        {
            return X;
        }
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                // if(! MathsUtil.nan(A[i][j], b )){
                if (!isIntMaxMin(A[i][j]) && A[i][j] <= b)
                {
                    C[i][j] = 1;
                }
                // else {C[i][j] = 0.0d;}
                // }
            }
        }
        return X;
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices GT(int b)
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        if (isIntMaxMin(b))
        {
            return X;
        }
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!isIntMaxMin(A[i][j]) && A[i][j] > b)
                {
                    C[i][j] = 1;
                }
                // else {C[i][j] = 0.0d;}
            }
        }
        return X;
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices LT(Indices B)
    {

        // ---- added 3/3/11 ----
        if (this.numel() == 1)
        {
            return B.GT(A[0][0]);
        }
        if (B.numel() == 1)
        {
            return this.LT(B.start());
        }
        // ----------------------

        checkIndicesDimensions(B);
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        boolean b1 = false;
        boolean b2 = false;

        int[][] M = B.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                b1 = isIntMaxMin(A[i][j]);
                b2 = isIntMaxMin(M[i][j]);
                if ((!b1 && !b2) && A[i][j] < M[i][j])
                {
                    C[i][j] = 1;
                }
            }// end for

        }// end for

        return X;

        /*
         * Indices X = new Indices(m, n); X.setLogical(true); if
         * (isIntMaxMin(b)) { return X; } int[][] C = X.getArray(); for (int i =
         * 0; i < m; i++) { for (int j = 0; j < n; j++) { if
         * (!isIntMaxMin(A[i][j]) && A[i][j] > b) { C[i][j] = 1; } //else
         * {C[i][j] = 0.0d;} } } return X;
         */
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices LTEQ(Indices B)
    {

        // ---- added 3/3/11 ----
        if (this.numel() == 1)
        {
            return B.GTEQ(A[0][0]);
        }
        if (B.numel() == 1)
        {
            return this.LTEQ(B.start());
        }
        // ----------------------

        checkIndicesDimensions(B);
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        boolean b1 = false;
        boolean b2 = false;

        int[][] M = B.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                b1 = isIntMaxMin(A[i][j]);
                b2 = isIntMaxMin(M[i][j]);
                if ((!b1 && !b2) && A[i][j] <= M[i][j])
                {
                    C[i][j] = 1;
                }
            }// end for

        }// end for

        return X;

        /*
         * Indices X = new Indices(m, n); X.setLogical(true); if
         * (isIntMaxMin(b)) { return X; } int[][] C = X.getArray(); for (int i =
         * 0; i < m; i++) { for (int j = 0; j < n; j++) { if
         * (!isIntMaxMin(A[i][j]) && A[i][j] > b) { C[i][j] = 1; } //else
         * {C[i][j] = 0.0d;} } } return X;
         */
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices GT(Indices B)
    {

        // ---- added 3/3/11 ----
        if (this.numel() == 1)
        {
            return B.LT(A[0][0]);
        }
        if (B.numel() == 1)
        {
            return this.GT(B.start());
        }
        // ----------------------

        checkIndicesDimensions(B);
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        boolean b1 = false;
        boolean b2 = false;

        int[][] M = B.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                b1 = isIntMaxMin(A[i][j]);
                b2 = isIntMaxMin(M[i][j]);
                if ((!b1 && !b2) && A[i][j] > M[i][j])
                {
                    C[i][j] = 1;
                }
            }// end for

        }// end for

        return X;

        /*
         * Indices X = new Indices(m, n); X.setLogical(true); if
         * (isIntMaxMin(b)) { return X; } int[][] C = X.getArray(); for (int i =
         * 0; i < m; i++) { for (int j = 0; j < n; j++) { if
         * (!isIntMaxMin(A[i][j]) && A[i][j] > b) { C[i][j] = 1; } //else
         * {C[i][j] = 0.0d;} } } return X;
         */
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices GTEQ(int b)
    {
        Indices X = new Indices(m, n);

        X.setLogical(true);
        if (isIntMaxMin(b))
        {
            return X;
        }
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!isIntMaxMin(A[i][j]) && A[i][j] >= b)
                {
                    C[i][j] = 1;
                }
                // else {C[i][j] = 0.0d;}
            }
        }
        return X;
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices GTEQ(Indices B)
    {

        // ---- added 3/3/11 ----
        if (this.numel() == 1)
        {
            return B.LTEQ(A[0][0]);
        }
        if (B.numel() == 1)
        {
            return this.GTEQ(B.start());
        }
        // ----------------------

        checkIndicesDimensions(B);
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        boolean b1 = false;
        boolean b2 = false;

        int[][] M = B.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                b1 = isIntMaxMin(A[i][j]);
                b2 = isIntMaxMin(M[i][j]);
                if ((!b1 && !b2) && A[i][j] >= M[i][j])
                {
                    C[i][j] = 1;
                }
            }// end for

        }// end for

        return X;

        /*
         * Indices X = new Indices(m, n); X.setLogical(true); if
         * (isIntMaxMin(b)) { return X; } int[][] C = X.getArray(); for (int i =
         * 0; i < m; i++) { for (int j = 0; j < n; j++) { if
         * (!isIntMaxMin(A[i][j]) && A[i][j] > b) { C[i][j] = 1; } //else
         * {C[i][j] = 0.0d;} } } return X;
         */
    }

    public Indices horzcat(Indices... B)
    {
        if (B == (Indices[]) null || B.length == 0)
        {
            return copy();
        }
        int lenB = B.length;
        int numR = 0;

        ArrayList<Indices> objList = new ArrayList<Indices>();
        // if (!this.isNull()) {
        // objList.add(this.copy());
        // }

        for (int i = 0; i < lenB; i++)
        {
            if (B[i] != null)
            {
                boolean cond = B[i].isNull();
                if (!cond)
                {
                    objList.add(B[i]);
                }
            }
        }

        if (objList.isEmpty())
        {
            return this.copy();// new Indices();
        }
        else if (objList.size() == 1)
        {
            Indices OB = objList.get(0);
            return this.mergeHoriz(OB);// OB.copy();
        }

        // check that all rows are the same.
        for (int i = 0; i < objList.size(); i++)
        {
            Indices OJT = objList.get(i);
            int curRow = 0;
            if (i == 0)
            {
                numR = OJT.getRowDimension();
            }
            else
            {
                curRow = OJT.getRowDimension();
                if (curRow != numR)
                {
                    throw new ConditionalRuleException("horzcat", "Number of rows must be the same for all.");
                }
            }

        }

        Indices[] list = new Indices[objList.size()];
        for (int i = 0; i < objList.size(); i++)
        {
            list[i] = objList.get(i);
        }

        return mergeHoriz(list);
    }

    /**
     * 
     * 
     * @param B
     *            Matrix
     * @return Matrix
     *         <p>
     *         Merge this Indices AND Indices array 'B' horizontally to form a
     *         larger matrix. Row size must agree.
     *         </p>
     */
    public Indices mergeHoriz(Indices... B)
    {
        if (B == (Indices[]) null || B.length == 0)
        {
            return copy();
        }

        int lenB = B.length;
        ArrayList<Indices> indList = new ArrayList<Indices>();
        indList.add(this);

        for (int i = 0; i < lenB; i++)
        {
            if (B[i] != null)
            {
                indList.add(B[i]);
            }
        }

        // if all input arguments is null, then just return a copy of this
        // matrix.
        if (indList.size() == 1)
        {
            return copy();
        }

        for (int i = 0; i < indList.size(); i++)
        {
            Indices matIndObj = indList.get(i);
            if (m != matIndObj.getRowDimension())
            {
                throw new IllegalArgumentException(" mergeHoriz : Indices row dimensions must agree.");
            }

        }

        int newCol = 0;
        for (int i = 0; i < indList.size(); i++)
        {
            Indices matIndObj = indList.get(i);
            newCol += matIndObj.getColumnDimension();
        }

        Indices R = new Indices(m, newCol);

        int begInd = 0;
        int endInd = 0;
        int numCol = 0;

        for (int i = 0; i < indList.size(); i++)
        {
            Indices matIndObj = indList.get(i);
            if (i == 0)
            {// first object is guranteed to be a matrix, because 'this' matrix
             // is the first one to be added.
                numCol = matIndObj.getColumnDimension();
                endInd = numCol - 1;
                // System.out.println("BLOCK #1 : [begin,end] = [" + begInd +
                // "," + endInd + "]");
                R.setIndices(0, m - 1, begInd, endInd, matIndObj);
            }
            else
            {
                begInd += numCol;
                numCol = matIndObj.getColumnDimension();
                endInd = begInd + numCol - 1;
                // System.out.println("BLOCK #3 : [begin,end] = [" + begInd +
                // "," + endInd + "]");
                R.setIndices(0, m - 1, begInd, endInd, matIndObj);
            }
        }

        return R;
    }

    /**
     * 
     * 
     * @param B
     *            Matrix
     * @return Matrix
     *         <p>
     *         Merge this matrix AND matrix 'B' horizontally to form a larger
     *         matrix. Row size must agree.
     *         </p>
     */
    public Indices mergeH(Indices B)
    {
        return mergeHoriz(B); // new line added on 23/03/2010

        /*
         * --------------- old code block added on 23/03/2010
         * ------------------- if (m != B.m) { throw new
         * IllegalArgumentException(" mergeH : Indices row dimensions must
         * agree."); }
         * 
         * int newCol = n + B.n; Indices R = new Indices(m, newCol);
         * R.setIndices(0, m - 1, 0, n - 1, this); R.setIndices(0, m - 1, n, n +
         * B.n - 1, B); return R;
         * ------------------------------------------------
         * -----------------------
         */
    }

    public Indices vertcat(Indices... B)
    {
        if (B == (Object[]) null || B.length == 0)
        {
            return copy();
        }
        int lenB = B.length;
        int numR = 0;

        ArrayList<Indices> objList = new ArrayList<Indices>();
        // if (!this.isNull()) {
        // objList.add(this.copy());
        // }

        for (int i = 0; i < lenB; i++)
        {
            if (B[i] != null)
            {
                boolean cond = ((Indices) B[i]).isNull();
                // numR += ((Indices) B[i]).getRowDimension();
                // numC += ((Indices) B[i]).getColumnDimension();

                if (!cond)
                {
                    objList.add(B[i]);
                }
            }
        }

        if (objList.isEmpty())
        {
            return this.copy();// new Indices();
        }
        else if (objList.size() == 1)
        {
            Indices OB = objList.get(0);
            return this.mergeVerti(OB);// OB.copy();
        }

        // check that all columns are the same.
        for (int i = 0; i < objList.size(); i++)
        {
            Object OJT = objList.get(i);
            int curRow = 0;
            if (i == 0)
            {
                if (OJT instanceof Matrix)
                {
                    numR = ((Matrix) OJT).getColumnDimension();
                }
                else
                {
                    numR = ((Indices) OJT).getColumnDimension();
                }
            }
            else
            {
                if (OJT instanceof Matrix)
                {
                    curRow = ((Matrix) OJT).getColumnDimension();
                }
                else
                {
                    curRow = ((Indices) OJT).getColumnDimension();
                }
                if (curRow != numR)
                {
                    throw new ConditionalRuleException("vertcat", "Number of columns must be the same for all.");
                }
            }
        }

        Indices[] list = new Indices[objList.size()];
        for (int i = 0; i < objList.size(); i++)
        {
            list[i] = objList.get(i);
        }

        return mergeVerti(list);
    }

    public Indices mergeVerti(Indices... B)
    {
        if (B == (Indices[]) null || B.length == 0)
        {
            return copy();
        }

        int lenB = B.length;
        ArrayList<Indices> indList = new ArrayList<Indices>();
        indList.add(this);

        for (int i = 0; i < lenB; i++)
        {
            if (B[i] != null)
            {
                indList.add(B[i]);
            }
        }

        // if all input arguments is null, then just return a copy of this
        // matrix.
        if (indList.size() == 1)
        {
            return copy();
        }

        for (int i = 0; i < indList.size(); i++)
        {
            Indices matIndObj = indList.get(i);
            if (n != matIndObj.getColumnDimension())
            {
                throw new IllegalArgumentException(" mergeVerti : Indices column dimensions must agree.");
            }
        }

        int newRow = 0;
        for (int i = 0; i < indList.size(); i++)
        {
            Indices matIndObj = indList.get(i);
            newRow += matIndObj.getRowDimension();
        }

        Indices R = new Indices(newRow, n);

        int begInd = 0;
        int endInd = 0;
        int numRow = 0;

        for (int i = 0; i < indList.size(); i++)
        {
            Indices matIndObj = indList.get(i);
            if (i == 0)
            {// first object is guranteed to be a matrix, because 'this' matrix
             // is the first one to be added.
                numRow = matIndObj.getRowDimension();
                endInd = numRow - 1;
                // System.out.println("vert BLOCK #1 : [begin,end] = [" + begInd
                // + "," + endInd + "]");
                R.setIndices(begInd, endInd, 0, n - 1, matIndObj);
            }
            else
            {
                begInd += numRow;
                numRow = matIndObj.getRowDimension();
                endInd = begInd + numRow - 1;
                // System.out.println("vert BLOCK #3 : [begin,end] = [" + begInd
                // + "," + endInd + "]");
                R.setIndices(begInd, endInd, 0, n - 1, matIndObj);
            }
        }

        return R;
    }

    /**
     * 
     * 
     * @param B
     *            Matrix
     * @return Matrix
     *         <p>
     *         Merge this matrix AND matrix 'B' vertically to form a larger
     *         matrix. Column size must agree.
     *         </p>
     */
    public Indices mergeV(Indices B)
    {

        return mergeVerti(B); // new line added on 23/03/2010

        /*
         * ----------- This code block was commented on 23/03/2010 ------------
         * if (n != B.n) { throw new IllegalArgumentException(" mergeV : Indices
         * column dimensions must agree."); } int newRow = m + B.m;
         * //System.out.println(" m = "+m+" ; B.m = "+B.m+" ; newRow =
         * "+newRow);
         * 
         * Indices R = new Indices(newRow, n); R.setIndices(0, m - 1, 0, n - 1,
         * this); R.setIndices(m, m + B.m - 1, 0, n - 1, B); return R;
         * ---------------------------------------------------------------------
         */
    }

    public Indices getEls(int[] ind)
    {
        int len = ind.length;
        Indices R = null;
        int val = 0;

        if (this.isRowVector())
        {
            R = new Indices(1, len);
            for (int i = 0; i < len; i++)
            {
                val = this.getElementAt(ind[i]);
                R.set(0, i, val);
            }
        }
        else
        {
            R = new Indices(len, 1);
            for (int i = 0; i < len; i++)
            {
                val = this.getElementAt(ind[i]);
                R.set(i, 0, val);
            }
        }

        return R;
    }

    public Indices getEls(int from, int to)
    {
        if (from > to)
        {
            throw new ConditionalException("getEls : Value for integer parameter \"from\" (= " + from
                    + ") must \nequal or less than that of \"to\" (= " + to + ") ;");
        }
        int[] ind = Indices.linspace(from, to).getRowPackedCopy();
        Indices R = null;
        R = this.getEls(ind);
        return R;
    }

    /**
     * 
     * @param ind
     * @return
     * @deprecated Use the method <B>getEls()</B>, instead.
     */
    public Indices getElements(int[] ind)
    {
        int len = ind.length;
        Indices R = new Indices(len, 1);
        int val = 0;
        for (int i = 0; i < len; i++)
        {
            val = this.getElementAt(ind[i]);
            R.set(i, 0, val);
        }
        return R;
    }

    /**
     * Retrieve an element as an index from column-like vector reference
     * 
     * @param ind
     * @return
     */
    public int getElementAt(int ind)
    {
        int val = 1;
        int count = 0;
        int i = ind;
        if (ind >= m * n)
        {
            throw new IllegalArgumentException("getElementAt : ArrayIndex is out-of-bound.");
        }
        if (i < m)
        {
            val = A[ind][0];
        }
        else
        {
            while (m <= i)
            {
                i = i - m;
                count++;
            }
            val = A[i][count];
        }

        return val;
    }// end method.

    public boolean getBooleanAt(int ind)
    {
        int val = getElementAt(ind);
        boolean bool = val == 0 ? false : true;
        return bool;
    }

    /**
     * 
     * @param find
     * @return
     */
    public Indices getFindIj(FindInd indices)
    {
        // if (find.isFindIndex() == false) {
        // throw new
        // IllegalArgumentException(" getFromFind : Parameter \"find\" must be logical, ie, it must come from a logical operation such <, <=, > , >= , not, and, eq, neq, etc....");
        // }
        if (indices == null)
        {
            return null;
        }
        if (!indices.equalSize(new int[]
        {
                m, n
        }))
        {
            throw new IllegalArgumentException("getFindIj : Object \"indices\" must have the same size as this matrix.");
        }
        int r = indices.numel();
        Indices R = null;
        if (this.isRowVector())
        {
            R = new Indices(1, r);
        }
        else
        {
            R = new Indices(r, 1);
        }
        int row = 0;
        int col = 0;

        if (this.isRowVector())
        {
            int[] findArr = indices.getIndex();
            for (int i = 0; i < r; i++)
            {
                col = findArr[i];
                R.set(0, i, A[0][col]);
            }
        }
        else
        {
            Indices find = indices.getFindEntries();
            for (int i = 0; i < r; i++)
            {
                row = find.get(i, 0);
                col = find.get(i, 1);
                R.set(i, 0, A[row][col]);
            }
        }

        return R;
    }

    /**
     * 
     * @param findIndices
     * @return
     */
    public Indices getFromFind(Indices findIndices)
    {
        if (findIndices.isFindIndex() == false)
        {
            throw new IllegalArgumentException(
                    " getFromFind : Parameter \"getFromFind\" must be logical, ie, it must come from a logical operation such <, <=, > , >= , not, and, eq, neq, etc....");
        }
        int r = findIndices.getRowDimension();
        Indices R = new Indices(r, 1);
        int row = 0;
        int col = 0;

        for (int i = 0; i < r; i++)
        {
            row = findIndices.get(i, 0);
            col = findIndices.get(i, 1);
            R.set(i, 0, A[row][col]);
        }

        return R;
    }

    public void setElements(int[] ind, int val)
    {
        if (ind == null)
        {
            throw new IllegalArgumentException("setElements: Parameter \"ind\" must be non-null.");
        }
        int len = ind.length;
        for (int i = 0; i < len; i++)
        {
            setElementAt(ind[i], val);
        }
    }

    public void setElements(int from, int to, int val)
    {
        if (to < from)
        {
            throw new IllegalArgumentException("setElements: Parameter \"to\" must be equal or greater than \"from\".");
        }
        int[] ind = Indices.linspace(from, to).getRowPackedCopy();
        setElements(ind, val);
    }

    public void setElements(int from, int to, Indices val)
    {
        if (to < from)
        {
            throw new IllegalArgumentException("setElements: Parameter \"to\" must be equal or greater than \"from\".");
        }
        int[] ind = Indices.linspace(from, to).getRowPackedCopy();
        setElements(ind, val);
    }

    public void setElements(int[] ind, Indices val)
    {
        if (ind == null)
        {
            throw new IllegalArgumentException("setElements: Parameter \"ind\" must be non-null.");
        }
        if (val == null)
        {
            throw new IllegalArgumentException("setElements: Parameter \"val\" must be non-null.");
        }
        if (!val.isVector())
        {
            throw new IllegalArgumentException("setElements: Parameter \"val\" must be a vector and not a matrix.");
        }
        if (ind.length != val.length())
        {
            throw new IllegalArgumentException("setElements: Parameter \"ind\" and \"val\" must have the same lengths.");
        }
        int len = ind.length;
        Indices rowMat = null;
        if (val.isRowVector())
        {
            rowMat = val;
        }
        else
        {
            rowMat = val.toRowVector();
        }
        for (int i = 0; i < len; i++)
        {
            setElementAt(ind[i], rowMat.get(0, i));
        }
    }

    /**
     * 
     * @param ind
     * @param val
     */
    public void setElementAt(int ind, int val)
    {
        int count = 0;
        int i = ind;
        if (ind >= m * n)
        {
            throw new IllegalArgumentException("setElementAt: ArrayIndex is out-of-bound.");
        }
        if (i < m)
        {
            A[ind][0] = val;
        }
        else
        {
            while (m <= i)
            {
                i = i - m;
                count++;
            }
            A[i][count] = val;
        }
    }// end method

    /**
     * Tile this matrix into an m-rows by n-columns pattern
     * 
     * 
     * @param r
     *            number of row patterns
     * @param c
     *            number of column patterns. return Matrix from tiling A[][]
     *            into r-rows AND c-columns tile pattern.
     * @return
     */
    public Indices repmat(int r, int c)
    {
        Indices xcopy = copy();
        int[][] X = xcopy.getArray();
        int countRow = 0;
        int countColumn = 0;
        // double[] tempHolder;
        if (r < 1 || c < 1)
        {
            throw new ArrayIndexOutOfBoundsException("repmat : Array index is out-of-bound.");
        }
        int newRowDim = m * r;
        int newColDim = n * c;
        int[][] result = new int[newRowDim][];
        int[] tempHolder = new int[newColDim];

        for (int i = 0; i < newRowDim; i++)
        {
            for (int j = 0; j < newColDim; j++)
            {
                tempHolder[j] = X[countRow][countColumn++];
                // reset the column-index to zero to avoid reference to
                // out-of-bound index in a[][]
                if (countColumn == n)
                {
                    countColumn = 0;
                }
            }// end for

            countRow++;
            // reset the row-index to zero to avoid reference to out-of-bound
            // index in a[][]
            if (countRow == m)
            {
                countRow = 0;
            }

            result[i] = tempHolder;
            // reassign the tempHolder to a new array
            tempHolder = new int[newColDim];
        }// end for

        return new Indices(result);
    }// end method

    /**
     * 
     * @return
     */
    public Indices ALL()
    {
        return ALL(Dimension.ROW);
    }

    /**
     * 
     * @param Dim
     * @return
     */
    public Indices ALL(Dimension Dim)
    {

        Indices allInd = null;
        int count = 0;
        // double val = 0.0;

        if (Dim == Dimension.ROW)
        {
            allInd = new Indices(1, n);
            allInd.setLogical(true);
            for (int j = 0; j < n; j++)
            {
                for (int i = 0; i < m; i++)
                {
                    if (A[i][j] != 0)
                    {
                        count++;
                    }
                    else
                    {
                        continue;
                    }
                }
                if (count == m)
                {
                    allInd.set(0, j, 1);
                }
                count = 0;
            }
        }
        else if (Dim == Dimension.COL)
        {
            allInd = new Indices(m, 1);
            allInd.setLogical(true);
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (A[i][j] != 0)
                    {
                        count++;
                    }
                    else
                    {
                        continue;
                    }
                }
                if (count == n)
                {
                    allInd.set(i, 0, 1);
                }
                count = 0;
            }
        }
        else
        {
            throw new IllegalArgumentException("ALL : Dimension  " + Dim.toString() + " , not supported.");
        }
        return allInd;
    }

    /**
     * 
     * @return
     */
    public int numel()
    {
        return m * n;
    }

    /**
     * 
     * @return
     */
    public int nnz()
    {
        int count = 0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] != 0)
                {
                    count++;
                }
            }
        }
        return count;
    }

    public Cell toCell()
    {
        Cell C = new Cell(m, n);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Integer val = new Integer(A[i][j]);
                C.set(i, j, val);
            }
        }
        return C;
    }

    public Cell toCellString()
    {
        Cell C = new Cell(m, n);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Integer val = new Integer(A[i][j]);
                C.set(i, j, val.toString());
            }
        }
        return C;
    }

    public ArrayList<Integer> toArrayList()
    {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                list.add(new Integer(A[i][j]));
            }
        }
        return list;
    }

    /**
     * 
     * @param row
     * @return
     */
    public Indices removeRowAt(int row)
    {
        return this.removeRows(new int[]
        {
            row
        });
    }

    /**
     * 
     * @param rows
     * @return
     */
    public Indices removeRows(int[] rows)
    {
        int lenRows = rows.length;
        ArrayList<Integer> arrayListRowCount = new ArrayList<Integer>();
        int count = 0;
        for (int i = 0; i < lenRows; i++)
        {
            if (rows[i] < 0 || rows[i] >= m)
            {
                throw new ArrayIndexOutOfBoundsException("removeRowsAt : Row indices is out-of-bound.");
            }
            // arrayListRowCount.add(new Integer(countInd++));
        }

        for (int i = 0; i < m; i++)
        {
            arrayListRowCount.add(new Integer(count++));
        }

        for (int i = 0; i < lenRows; i++)
        {
            Integer countInt = new Integer(rows[i]);
            if (arrayListRowCount.contains(countInt))
            {
                arrayListRowCount.set(rows[i], null);
            }
        }

        count = 0;
        ArrayList<Integer> nonNullRowsList = new ArrayList<Integer>();

        for (int i = 0; i < m; i++)
        {
            Integer obj = arrayListRowCount.get(i);
            if (obj != null)
            {
                nonNullRowsList.add(obj);
            }
            else
            {
                count++;
            }
        }

        if (count == m)
        {
            return null;
        }

        int numRows = nonNullRowsList.size();
        int[] newRows = new int[numRows];
        for (int i = 0; i < numRows; i++)
        {
            newRows[i] = nonNullRowsList.get(i).intValue();
        }

        Indices R = this.getRows(newRows);
        return R;
    }

    public Indices removeEls(int[] numEls)
    {
        Indices result = null;
        if (numEls == null || numEls.length == 0)
        {
            return copy();
        }
        int totEl = this.numel();

        if (numEls.length > totEl)
        {
            throw new ConditionalException(
                    "removeEls : The total number of elements to be removed exceeds total matrix elements.");
        }

        // int[] index = Indices.linspace(0, totEl - 1).getRowPackedCopy();
        // check if valid entries
        int lenRemov = numEls.length;
        for (int i = 0; i < lenRemov; i++)
        {
            if (numEls[i] < 0 || numEls[i] >= totEl)
            {
                throw new ConditionalException("removeEls : Element \"numEls[" + i + "] = " + numEls[i]
                        + "\" to be removed is out-of-bound.");
            }
        }

        ArrayList<Integer> R = new ArrayList<Integer>();

        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                R.add(A[i][j]);
            }
        }

        for (int i = 0; i < lenRemov; i++)
        {// mark the position to be removed.
            int indRem = numEls[i];
            Integer DB = null;
            R.set(indRem, DB);
        }

        int siz = R.size();
        ArrayList<Integer> RnonNull = new ArrayList<Integer>();

        for (int k = 0; k < siz; k++)
        {
            Integer DB = R.get(k);
            if (DB != null)
            {
                RnonNull.add(DB);
            }
        }

        siz = RnonNull.size();
        if (siz == 0)
        {// everything has been removed.
            return null;
        }

        if (!this.isVector())
        {// matrix
            result = new Indices(1, siz);
            for (int i = 0; i < siz; i++)
            {
                result.set(0, i, RnonNull.get(i).intValue());
            }
        }
        else
        {// vector
            if (this.isRowVector())
            {
                result = new Indices(1, siz);
                for (int i = 0; i < siz; i++)
                {
                    result.set(0, i, RnonNull.get(i).intValue());
                }
            }
            else
            {
                result = new Indices(siz, 1);
                for (int i = 0; i < siz; i++)
                {
                    result.set(i, 0, RnonNull.get(i).intValue());
                }
            }
        }
        return result;
    }

    /**
     * 
     * @param col
     * @return
     */
    public Indices removeColAt(int col)
    {
        return this.removeCols(new int[]
        {
            col
        });
    }

    /**
     * 
     * @param cols
     * @return
     */
    public Indices removeCols(int[] cols)
    {
        int lenCols = cols.length;
        ArrayList<Integer> arrayListColCount = new ArrayList<Integer>();
        int count = 0;
        for (int i = 0; i < lenCols; i++)
        {
            if (cols[i] < 0 || cols[i] >= n)
            {
                throw new ArrayIndexOutOfBoundsException("removeColsAt : Column indices is out-of-bound.");
            }
            // arrayListColCount.add(new Integer(countInd++));
        }

        for (int i = 0; i < n; i++)
        {
            arrayListColCount.add(new Integer(count++));
        }

        for (int i = 0; i < lenCols; i++)
        {
            Integer countInt = new Integer(cols[i]);
            if (arrayListColCount.contains(countInt))
            {
                arrayListColCount.set(cols[i], null);
            }
        }

        count = 0;
        ArrayList<Integer> nonNullColsList = new ArrayList<Integer>();

        for (int i = 0; i < n; i++)
        {
            Integer obj = arrayListColCount.get(i);
            if (obj != null)
            {
                nonNullColsList.add(obj);
            }
            else
            {
                count++;
            }
        }

        if (count == n)
        {
            return null;
        }

        int numCols = nonNullColsList.size();
        int[] newCols = new int[numCols];
        for (int i = 0; i < numCols; i++)
        {
            newCols[i] = nonNullColsList.get(i).intValue();
        }

        Indices R = this.getColumns(newCols);
        return R;
    }

    public Indices removeElAt(int row, int col)
    {
        if (row >= this.m)
        {
            throw new ConditionalRuleException("removeElAt : Index parameter \"row\" (= " + row + ") is out of bound.");
        }
        if (col >= this.n)
        {
            throw new ConditionalRuleException("removeElAt : Index parameter \"col\" (= " + col + ") is out of bound.");
        }
        Indices result = null;
        int count = 0;
        label: for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                count++;
                if (i == row && j == col)
                {
                    break label;
                }
            }
        }
        result = removeElAt(count);
        return result;
    }

    public Indices removeElAt(int ind)
    {
        Indices result = null;
        ArrayList<Integer> R = new ArrayList<Integer>();
        int len = 0;
        int newLen = 0;
        if (this.isVector())
        {
            len = this.length();
            if (ind >= len)
            {
                throw new ConditionalRuleException("removeElAt : Index parameter \"ind\" (= " + ind
                        + ") is out of bound.");
            }
            if (this.isRowVector())
            {
                for (int i = 0; i < len; i++)
                {
                    if (i != ind)
                    {
                        R.add(A[0][i]);
                    }
                }
                newLen = len - 1;
                result = new Indices(1, newLen);
                for (int i = 0; i < newLen; i++)
                {
                    result.set(0, i, R.get(i));
                }
            }
            else
            {
                for (int i = 0; i < len; i++)
                {
                    if (i != ind)
                    {
                        R.add(A[i][0]);
                    }
                }
                newLen = len - 1;
                result = new Indices(newLen, 1);
                for (int i = 0; i < newLen; i++)
                {
                    result.set(i, 0, R.get(i));
                }
            }
        }
        else
        {
            len = this.numel();
            if (ind >= len)
            {
                throw new ConditionalRuleException("removeElAt : Index parameter \"ind\" (= " + ind
                        + ") is out of bound colum-wise ( = " + len + ").");
            }
            Indices newMat = this.toColVector();
            for (int i = 0; i < len; i++)
            {
                if (i != ind)
                {
                    R.add(newMat.get(i, 0));
                }
            }
            newLen = len - 1;
            result = new Indices(1, newLen);
            for (int i = 0; i < newLen; i++)
            {
                result.set(0, i, R.get(i));
            }
        }
        return result;
    }

    /**
     * 
     * @return
     */
    public Indices uminus()
    {
        Indices X = new Indices(m, n);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = -A[i][j];
            }
        }
        return X;
    }

    public static Matrix read(String fileName) throws FileNotFoundException, IOException
    {
        BufferedReader bufReader = null;
        // System.out.println("### Begin File Reading ###");

        try
        {
            bufReader = new BufferedReader(new FileReader(fileName));

        }
        catch (FileNotFoundException fne)
        {
            throw fne;
        }

        Matrix fromFile = null;
        try
        {
            fromFile = Matrix.read(bufReader);

        }
        catch (IOException io)
        {
            throw io;
        }

        return fromFile;
    }

    /**
     * 
     * @param input
     * @throws java.io.IOException
     * @return
     */
    public static Indices read(BufferedReader input) throws java.io.IOException
    {
        StreamTokenizer tokenizer = new StreamTokenizer(input);

        // Although StreamTokenizer will parse numbers, it doesn't recognize
        // scientific notation (E OR D); however, Double.valueOf does.
        // The strategy here is to disable StreamTokenizer's number parsing.
        // We'll only get whitespace delimited words, EOL's AND EOF's.
        // These words should ALL be numbers, for Double.valueOf to parse.
        tokenizer.resetSyntax();
        tokenizer.wordChars(0, 255);
        tokenizer.whitespaceChars(0, ' ');
        tokenizer.eolIsSignificant(true);

        java.util.Vector<Object> v = new java.util.Vector<Object>();

        String nanStr = "nan";
        String infStr = "inf";

        // Ignore initial empty lines
        while (tokenizer.nextToken() == StreamTokenizer.TT_EOL)
            ;
        if (tokenizer.ttype == StreamTokenizer.TT_EOF)
        {
            throw new java.io.IOException("Unexpected EOF on matrix read.");
        }
        do
        {
            String ts = tokenizer.sval;
            Integer dbObj = null;
            if (ts.toLowerCase().contains(nanStr))
            {
                // dbObj = new Double(Double.NaN);
                throw new ConditionalRuleException("read : Not suppose to have \"nan\".");
            }
            else if (ts.toLowerCase().contains(infStr))
            {
                if (ts.charAt(0) == '-')
                {
                    // dbObj = new Double(Double.NEGATIVE_INFINITY);
                    throw new ConditionalRuleException("read : Not suppose to have \"NEGATIVE_INFINITY\".");
                }
                else
                {
                    // dbObj = new Double(Double.POSITIVE_INFINITY);
                    throw new ConditionalRuleException("read : Not suppose to have \"POSITIVE_INFINITY\".");
                }
            }
            else
            {
                dbObj = Integer.valueOf(ts);
            }
            v.addElement(dbObj); // Read & store 1st i.

        }
        while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);

        int n = v.size(); // Now we've got the number of columns!

        int row[] = new int[n];
        for (int j = 0; j < n; j++)
        { // extract the elements of the 1st i.
            row[j] = ((Integer) v.elementAt(j)).intValue();
        }
        v.removeAllElements();
        v.addElement(row); // Start storing rows instead of columns.

        while (tokenizer.nextToken() == StreamTokenizer.TT_WORD)
        {
            // While non-empty lines
            v.addElement(row = new int[n]);
            int j = 0;
            do
            {
                if (j >= n)
                {
                    throw new java.io.IOException("Row " + v.size() + " is too long.");
                }
                String ts = tokenizer.sval;
                int dbObj = 0;
                if (ts.toLowerCase().contains(nanStr))
                {
                    // dbObj = Double.NaN;
                    throw new ConditionalRuleException("read : Not suppose to have \"nan\".");
                }
                else if (ts.toLowerCase().contains(infStr))
                {
                    if (ts.charAt(0) == '-')
                    {
                        // dbObj = Double.NEGATIVE_INFINITY;
                        throw new ConditionalRuleException("read : Not suppose to have \"NEGATIVE_INFINITY\".");
                    }
                    else
                    {
                        // dbObj = Double.POSITIVE_INFINITY;
                        throw new ConditionalRuleException("read : Not suppose to have \"POSITIVE_INFINITY\".");
                    }
                }
                else
                {
                    dbObj = Integer.valueOf(ts).intValue();
                }

                row[j++] = dbObj;

            }
            while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);
            if (j < n)
            {
                throw new java.io.IOException("Row " + v.size() + " is too short.");
            }
        }
        int m = v.size(); // Now we've got the number of rows.

        int[][] A = new int[m][];
        v.copyInto(A); // copy the rows out of the vector

        return new Indices(A);
    }

    public static void write(Indices mat, String fileName) throws java.io.IOException
    {
        Writer writer = new FileWriter(fileName);
        writeBuffered(mat, writer);
        writer.close();
    }

    private static void writeBuffered(Indices mat, Writer baseWriter) throws java.io.IOException
    {

        if (mat == null || mat.getRowDimension() == 0 || mat.getColumnDimension() == 0)
        {
            return;
        }

        BufferedWriter writer = new BufferedWriter(baseWriter);

        int[][] AA = mat.getArray();
        int mm = mat.getRowDimension();
        int nn = mat.getColumnDimension();
        // System.out.println("m ="+mm+" , n = "+nn);

        for (int i = 0; i < mm; i++)
        {
            for (int j = 0; j < nn; j++)
            {
                if (nn > 1)
                {
                    if (j != (nn - 1))
                    {
                        writer.write(AA[i][j] + "\t"); // System.out.println("BLOCK #1 \n");
                    }
                    else
                    {
                        writer.write(AA[i][j] + ""); // System.out.println("BLOCK #2 \n");
                    }
                }
                else
                {
                    writer.write(AA[i][j] + ""); // System.out.println("BLOCK #3 \n");
                }
            }
            writer.newLine();
            // System.out.println("BLOCK #4 \n");
            // System.out.println("------------------");
        }
        writer.flush();
    }

    /**
     * 
     * @param indices
     * @param entryValue
     */
    public void setEntriesAtIndicesTo(Indices indices, int entryValue)
    {
        // indices is a mi number of elements AND only 2 columns which stores
        // the value of the index for entries (i,j).
        int mi = indices.getRowDimension();
        // int ni = indices.getColumnDimension();
        int I = 0;
        int J = 0;
        for (int i = 0; i < mi; i++)
        {
            I = indices.get(i, 0);
            J = indices.get(i, 1);
            set(I, J, entryValue);
        }
    }

    public void setRowAt(int ind, int val)
    {
        setRows(new int[]
        {
            ind
        }, val);
    }

    public void setRows(int[] indices, int val)
    {
        int len = indices.length;
        Indices matRows = new Indices(len, n, val);
        setRows(indices, matRows);
    }

    /**
     * 
     * @param indices
     *            int[]
     * @param matRows
     *            Matrix
     */
    public void setRows(int[] indices, Indices matRows)
    {
        if (matRows.getColumnDimension() != n)
        {
            throw new IllegalArgumentException(" setRows: Inconsistent size of rows.");
        }
        int len = indices.length;
        if (matRows.getRowDimension() != len)
        {
            throw new IllegalArgumentException(" setRows: Inconsistent size of 'indices' and 'matRows'.");
        }
        for (int i = 0; i < len; i++)
        {
            if (indices[i] > (m - 1) || indices[i] < 0)
            {
                throw new IllegalArgumentException(" setRows: Array out-of-bounds.");
            }
            Indices temp = matRows.getRowAt(i);
            setIndices(indices[i], indices[i], 0, n - 1, temp);
        }
    }

    /**
     * 
     * @param index
     *            int
     * @param matRows
     *            Matrix
     */
    public void setRowAt(int index, Indices matRows)
    {
        setRows(new int[]
        {
            index
        }, matRows);
    }

    public boolean isSorted()
    {
        return sorted;
    }

    public void setSorted(boolean sorted)
    {
        this.sorted = sorted;
    }

    public Object[] sortRows()
    {
        return sortRows(Dimension.ROW);
    }

    public Object[] sortRows(Dimension dim)
    {
        return sortRows(dim, false);
    }

    public Object[] sortRows(boolean indexFlag)
    {
        return sortRows(Dimension.ROW, indexFlag);
    }

    public Object[] sortRows(Dimension dim, boolean indexFlag)
    {
        Indices sortedMat = null;
        Indices sortedInd = null;
        if (this.numel() == 1)
        {
            if (indexFlag)
            {
                return new Object[]
                {
                        this.copy(), new Indices(1, 1, 0)
                };
            }
            else
            {
                return new Object[]
                {
                    this.copy()
                };
            }
        }
        else if (this.isVector())
        {// vector
            QuickSort sort = new QuickSortInd(this, dim);
            sortedMat = (Indices) sort.getSortedObject();
            if (indexFlag)
            {
                sortedInd = sort.getIndices();
            }
        }
        else
        {
            Indices firstColumn = this.getColumnAt(0);
            QuickSort sortCol = new QuickSortInd(firstColumn);
            if (indexFlag)
            {
                sortedInd = sortCol.getIndices();
            }
            int[] rowsOrder = sortCol.getIndices().getRowPackedCopy();
            sortedMat = this.getRows(rowsOrder);
        }
        return new Object[]
        {
                sortedMat, sortedInd
        };
    }

    public static Tuple<List<int[]>, Indices> permuteSymmetricUniquePair(int N)
    {
        if (N < 2)
        {
            throw new IllegalArgumentException("Input argument \"N\" (= " + N + "), must be at least 2.");
        }
        List<int[]> list = new ArrayList<int[]>();
        int nr = N - 1;
        for (int i = 0; i <= nr; i++)
        {
            for (int j = i + 1; j <= nr; j++)
            {
                int[] ij =
                {
                        i, j
                };
                list.add(ij);
                // new Indices(ij).printInLabel("IJ");
            }
        }

        int numrows = list.size();
        Indices IJind = new Indices(numrows, 2);
        for (int i = 0; i < numrows; i++)
        {
            Indices rowI = new Indices(list.get(i));
            IJind.setRowAt(i, rowI);
        }

        // IJind.printInLabel("IJind");
        Tuple<List<int[]>, Indices> TP = new Tuple<List<int[]>, Indices>(list, IJind);

        return TP;
    }

    public Indices finargcat(Dimension dim, Indices... argInd)
    {
        if (dim == null)
        {
            throw new ConditionalException("finargcat : Parameter \"dim\" must be non-null.");
        }
        if (argInd == null || argInd.length == 0)
        {
            throw new ConditionalException(
                    "finargcat : Indices array parameter \"argMat\" must be non-null or non-empty.");
        }
        int len = argInd.length;
        // check the ones that are not null.
        ArrayList<Indices> nonNull = new ArrayList<Indices>();
        nonNull.add(this);
        int count = 0;
        for (int i = 0; i < len; i++)
        {
            if (argInd[i] != null)
            {
                nonNull.add(argInd[i]);
                count++;
            }
        }

        if (count == 0)
        {
            throw new ConditionalException("finargcat : All elements of Indices array parameter \"argMat\" are null.");
        }

        Indices catInd = null;
        int siz = nonNull.size();
        int maxNum = 0;
        Indices tmp = null;
        int beg = 0;
        int end = 0;
        if (dim == Dimension.ROW)
        {
            int totRow = 0;
            for (int i = 0; i < siz; i++)
            {
                tmp = nonNull.get(i);
                totRow = totRow + tmp.getRowDimension();
                maxNum = Math.max(maxNum, tmp.getColumnDimension());
            }
            catInd = new Indices(totRow, maxNum, Integer.MAX_VALUE);
            for (int i = 0; i < siz; i++)
            {
                tmp = nonNull.get(i);
                if (i == 0)
                {
                    end = tmp.getRowDimension() - 1;
                    catInd.setIndices(beg, end, 0, tmp.getColumnDimension() - 1, tmp);

                }
                else
                {
                    beg = end + 1;
                    end = end + tmp.getRowDimension();
                    catInd.setIndices(beg, end, 0, tmp.getColumnDimension() - 1, tmp);
                }
                // System.out.println((i+1)+" ) begin = "+beg+" ;  end = "+end);
            }

        }
        else
        {
            int totCol = 0;
            for (int i = 0; i < siz; i++)
            {
                tmp = nonNull.get(i);
                totCol = totCol + tmp.getColumnDimension();
                maxNum = Math.max(maxNum, tmp.getRowDimension());
            }
            catInd = new Indices(maxNum, totCol, Integer.MAX_VALUE);
            for (int i = 0; i < siz; i++)
            {
                tmp = nonNull.get(i);
                if (i == 0)
                {
                    end = tmp.getColumnDimension() - 1;
                    catInd.setIndices(0, tmp.getRowDimension() - 1, beg, end, tmp);

                }
                else
                {
                    beg = end + 1;
                    end = end + tmp.getColumnDimension();
                    catInd.setIndices(0, tmp.getRowDimension() - 1, beg, end, tmp);
                }
                // System.out.println((i+1)+" ) begin = "+beg+" ;  end = "+end);
            }
        }

        return catInd;
    }

    public Indices modulo(int mod)
    {
        Indices modInd = new Indices(m, n);
        int[][] modArr = modInd.getArray();

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                modArr[i][j] = this.A[i][j] % mod;
            }
        }

        return modInd;
    }

    /**
     * 
     * @param m
     * @param n
     * @return
     */
    public static Indices ones(int m, int n)
    {
        return new Indices(m, n, 1);
    }

    /**
     * 
     * @param m
     * @return
     */
    public static Indices ones(int m)
    {
        return ones(m, m);
    }

    /**
     * 
     * @param r
     * @param c
     * @return
     */
    public static Indices zeros(int r, int c)
    {
        return new Indices(r, c, 0);
    }

    /**
     * 
     * @param n
     * @return
     */
    public static Indices zeros(int n)
    {
        return zeros(n, n);
    }

    /**
     * Extract lower triangular part. diagonal = 0 is the main diagonal,
     * diagonal > 0 is above the main matrix-diagonal AND diagonal < 0 is below
     * the main matrix-diagonal. ( 3,2,4,-2 ) ( 3,2,0, 0 ) If matrix A = |
     * 6,4,1, 3 | , A.tril(1) = | 6,4,1, 0 | ( 9,1,5,-1 ) ( 9,1,5,-1 )
     * 
     * 
     * 
     * 
     * 
     * @param diagonal
     *            Diagonal to specify for the lower triangular of this matrix
     * @return lower triangular matrix
     */
    public Indices tril(int diagonal)
    {
        Indices X = copy();
        int[][] temp = X.getArray();

        if (diagonal >= n)
        {
            return X;
        }
        else if ((-(m - 1) <= diagonal) && (diagonal <= (n - 1)))
        {
            if (diagonal == 0)
            {
                for (int i = 0; i < m; i++)
                {
                    for (int j = 0; j < n; j++)
                    {
                        if (j > i)
                        {
                            temp[i][j] = 0;
                        }
                    }// end for

                }// end for

            }// end else if
            else if (diagonal < 0)
            {
                for (int i = 0; i < m; i++)
                {
                    for (int j = 0; j < n; j++)
                    {
                        if (j > (i + diagonal))
                        {
                            temp[i][j] = 0;
                        }
                    }// end for

                }// end for

            }// end else if
            else if (diagonal > 0)
            {
                for (int i = 0; i < m; i++)
                {
                    for (int j = 0; j < n; j++)
                    {
                        if (j > (i + diagonal))
                        {
                            temp[i][j] = 0;
                        }
                    }// end for

                }// end for

            }// end else if

        }// end else if
        else if (diagonal <= -m)
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    temp[i][j] = 0;
                }
            }
        }

        return X;
    }// ---------end method tril-----------

    public static String vectorToString(int[] vec)
    {
        if (vec == null || vec.length == 0)
        {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        int len = vec.length;

        for (int i = 0; i < len; i++)
        {
            if (i != len - 1)
            {
                builder = builder.append(vec[i] + " ");
            }
            else
            {
                builder = builder.append(vec[i]);
            }
        }

        return builder.toString();
    }

    /**
     * Extract lower triangular matrix. ( 3,2,4,-2 ) ( 3,0,0,0 ) If matrix A = |
     * 6,4,1, 3 | , A.tril() = | 6,4,0,0 | ( 9,1,5,-1 ) ( 9,1,5,0 )
     * 
     * @return lower triangular matrix
     */
    public Indices tril()
    {
        return tril(0);
    }

    /**
     * Extract upper triangular matrix. diagonal = 0 is the main
     * matrix-diagonal, diagonal > 0 is above the main matrix-diagonal AND
     * diagonal < 0 is below the main matrix-diagonal. ( 3,2,4,-2 ) ( 0,0,4,-2 )
     * If matrix A = | 6,4,1, 3 | , A.triu(2) = | 0,0,0, 3 | ( 9,1,5,-1 ) (
     * 0,0,0, 0 )
     * 
     * 
     * 
     * 
     * 
     * @return upper triangular matrix
     */
    public Indices triu(int diagonal)
    {
        Indices X = copy();
        int[][] temp = X.getArray();

        if (diagonal <= -m)
        {
            return X;
        }
        else if ((-(m - 1) <= diagonal) && (diagonal <= (n - 1)))
        {
            if (diagonal == 0)
            {
                for (int i = 0; i < m; i++)
                {
                    for (int j = 0; j < n; j++)
                    {
                        if (i > j)
                        {
                            temp[i][j] = 0;
                        }
                    }// end for

                }// end for

            }// else if
            else if (diagonal < 0)
            {
                for (int i = 0; i < m; i++)
                {
                    for (int j = 0; j < n; j++)
                    {
                        if ((i + diagonal) > j)
                        {
                            temp[i][j] = 0;
                        }
                    }// end for

                }// end for

            }// else if
            else if (diagonal > 0)
            {
                for (int i = 0; i < m; i++)
                {
                    for (int j = 0; j < n; j++)
                    {
                        if ((i + diagonal) > j)
                        {
                            temp[i][j] = 0;
                        }
                    }// end for

                }// end for

            }// else if

        }// else if
        else if (diagonal >= n)
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    temp[i][j] = 0;
                }// end for

            }// end for

        }// else if

        return X;
    }// ---------end method triu-----------

    /**
     * Extract lower triangular matrix . ( 3,2,4,-2 ) ( 3,2,4,-2 ) If matrix A =
     * | 6,4,1, 3 | , A.triu() = | 0,4,1, 3 | ( 9,1,5,-1 ) ( 0,0,5,-1 )
     * 
     * @return lower triangular matrix
     */
    public Indices triu()
    {
        return triu(0);
    }

    public Indices reshape(int[] newRowCol)
    {
        if (newRowCol == null)
        {
            new IllegalArgumentException("Input integer array parameter must be non-null.");
        }
        if (newRowCol.length != 2)
        {
            new IllegalArgumentException("Length of input integer array parameter must be 2;");
        }
        int newrow = newRowCol[0];
        int newcol = newRowCol[1];

        return reshape(newrow, newcol);
    }

    /**
     * Reshape the matrix into a matrix with dimensions changed to
     * newrow-by-newcol ( 3,2,4,-2 ) If matrix A = | 6,4,1, 3 | , reshape(A,2,6)
     * = (3, 6, 9, 2, 4, 1) ( 9,1,5,-1 ) (4, 1, 5,-2, 3,-1) ,return a 2 x 6
     * matrix
     * 
     * @param newrow
     *            number of rows of the new shape Matrix
     * @param newcol
     *            number of columns of the new shape Matrix
     * @return reshaped matrix
     */
    public Indices reshape(int newrow, int newcol)
    {
        int count = 0;
        int[] columnVector = getColumnPackedCopy();// JElmat.toColumnVector(matrix);

        if ((m * n != newrow * newcol) || newrow < 1 || newcol < 1)
        {
            throw new ArrayIndexOutOfBoundsException("reshape : Array index is out-of-bound.");
        }
        Indices result = new Indices(newrow, newcol);
        int[][] C = result.getArray();

        /*
         * ======= Old loop ============ for(int i=0 ; i<newrow ; i++){ for(int
         * j=0 ; j<newcol ; j++){ C[i][j] = columnVector[countInd++]; } }
         */
        // ======== This loop replace the 'Old-loop' above
        for (int j = 0; j < newcol; j++)
        {
            for (int i = 0; i < newrow; i++)
            {
                C[i][j] = columnVector[count++];
            }
        }
        return result;
    }// end method

    /**
     * Rotate this matrix 90-degrees (one quadrant) anti-clockwise
     * 
     * @return 90-degrees anti-clockwise rotated matrix.
     */
    public Indices rot90()
    {
        return rot90(1);
    }

    /**
     * Rotate this matrix 90-degrees times quadrant in any direction. Negative
     * value for quadrant means clockwise rotation AND positive is
     * anti-clockwise.
     * 
     * @param quadrant
     *            number of 90-degrees lots that this matrix has to rotate.
     * @return 90*quadrant rotated matrix.
     */
    public Indices rot90(int quadrant)
    {
        int K = Math.abs(quadrant);
        K = K % 4; // four quadrants.

        // Matrix matCopy = copy();
        Indices X = null;

        switch (K)
        {
        case 0:
        { // System.out.println("CASE 0");

            X = copy();// matCopy;

            break;
        }
        case 1:
        {// System.out.println("CASE 1");

            if (quadrant > 0)
            {
                X = this.flipLR().transpose();
            }
            else
            {
                X = this.flipUD().transpose();
            }
            break;
        }
        case 2:
        { // System.out.println("CASE 2");

            if (quadrant > 0)
            {
                X = this.flipLR().transpose().flipLR().transpose();
            }
            else
            {
                X = this.flipUD().transpose().flipUD().transpose();
            }
            break;
        }
        case 3:
        {// System.out.println("CASE 3");

            if (quadrant > 0)
            {
                X = this.flipUD().transpose();
            }
            else
            {
                X = this.flipLR().transpose();
            }
            break;
        }
        }// end switch

        return X;
    }// end method

    /**
     * Generate matrix with random elements
     * 
     * @param m
     *            Number of rows.
     * @param lenC
     *            Number of colums.
     * @return An m-by-lenC matrix with uniformly distributed random elements.
     */
    public static Indices random(int m, int n, int scale)
    {
        Indices A = new Indices(m, n);
        int[][] X = A.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                X[i][j] = (int) Math.round(scale * Math.random());
            }
        }
        return A;
    }

    public static Indices eye(int[] rc)
    {
        if (rc == null)
        {
            throw new ConditionalException("eye : Parameter \"rc\" must be non-null.");
        }
        if (rc.length != 2)
        {
            throw new ConditionalException("eye : Length of integer array parameter \"rc\" must be 2;");
        }
        Indices ind = new Indices(rc);
        if (ind.LTEQ(0).anyBoolean())
        {
            throw new ConditionalException("eye : Elements of integer array parameter \"rc\" must be positive.");
        }
        return eye(rc[0], rc[1]);
    }

    public static Indices eye(int s)
    {
        return identity(s, s);
    }

    public static Indices eye(int m, int n)
    {
        return identity(m, n);
    }

    public static Indices boolean2Ind(boolean[] bool)
    {
        Indices indBool = new Indices(1, bool.length);
        indBool.setLogical(true);
        for (int i = 0; i < bool.length; i++)
        {
            boolean tf = bool[i];
            if (tf)
            {
                indBool.set(0, i, Boolean.TRUE);
            }
        }
        return indBool;
    }

    /**
     * Overloaded identity method
     */
    public static Indices identity(int s)
    {
        return identity(s, s);
    }

    /**
     * Generate identity matrix
     * 
     * @param m
     *            Number of rows.
     * @param lenC
     *            Number of colums.
     * @return An m-by-lenC matrix with ones on the diagonal AND zeros
     *         elsewhere.
     */
    public static Indices identity(int m, int n)
    {
        Indices A = new Indices(m, n);
        int[][] X = A.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                X[i][j] = (i == j ? 1 : 0);
            }
        }
        return A;
    }

    static void testPermute()
    {
        Tuple<List<int[]>, Indices> perm = Indices.permuteSymmetricUniquePair(10);
    }

    static void test1()
    {
        int[] aa =
        {
                4, 1, 3
        };
        Indices A = new Indices(aa);
        Indices Aeq = A.EQ(1);
        Aeq.printInLabel("Aeq");
        FindInd find = Aeq.findIJ();
        Indices findI = find.getIndexInd();
        findI.printInLabel("findI");

        // Indices A = Indices.random(8, 10, 100);
        // A.printInLabel("A");
        // Indices Amod = A.modulo(12);
        // Amod.printInLabel("Amod");

        /*
         * int[][] I = {{1, 1, 1, 1, 0}, {1, 0, 1, 0, 1}, {0, 0, 0, 1, 1}, {1,
         * 1, 1, 0, 1} };
         * 
         * Indices Ind = new Indices(I).NOT(); System.out.println("--------- Ind
         * ---------"); Ind.print(4);
         * 
         * int[] first = Ind.findFirst(2); Indices disp = new
         * Indices(first).toColVector(); System.out.println("--------- First
         * ---------"); disp.print(4);
         * 
         * 
         * int[] last = Ind.findLast(5); disp = new Indices(last).toColVector();
         * System.out.println("--------- Last ---------"); disp.print(4);
         */
    }

    public static void main(String[] args)
    {
        // test1();
        testPermute();
    }
}// -------------------------- End Class Definition
// -----------------------------

