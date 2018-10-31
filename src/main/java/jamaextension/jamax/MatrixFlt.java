/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * 
 * @author Sione
 */
public class MatrixFlt implements java.io.Serializable, java.lang.Cloneable
{

    /*
     * ------------------------ Class variables ------------------------
     */
    /**
     * Array for internal storage of elements.
     * 
     * @serial internal array storage.
     */
    private float[][] A;
    /**
     * Row AND column dimensions.
     * 
     * @serial i dimension.
     * @serial column dimension.
     */
    private int m, n;
    private boolean sorted = false;

    /*
     * ------------------------ Constructors ------------------------
     */
    public MatrixFlt()
    {
        this.m = 0;
        this.n = 0;
        this.A = null;
    }

    /**
     * Construct an m-by-lenC matrix of zeros.
     * 
     * @param m
     *            Number of rows.
     * @param n
     */
    public MatrixFlt(int m, int n)
    {
        this.m = m;
        this.n = n;
        boolean warn = m < 1 || n < 1;
        if (warn)
        {
            throw new ConditionalException("Matrix : Both integer parameters \"m\" (= " + m + ") and \"n\" (= " + n
                    + ") must be at least 1.");
        }
        A = new float[m][n];
    }

    public MatrixFlt(int[] mn)
    {

        if (mn == null)
        {
            throw new ConditionalException("Matrix : Parameter \"mn\" must be non-null.");
        }
        if (mn.length != 2)
        {
            throw new ConditionalException("Matrix : Length of integer array parameter \"mn\" must be 2;");
        }
        Indices ind = new Indices(mn);
        if (ind.LTEQ(0).anyBoolean())
        {
            throw new ConditionalException("Matrix : Elements of integer array parameter \"mn\" must be positive.");
        }
        this.m = mn[0];
        this.n = mn[1];
        A = new float[mn[0]][mn[1]];

    }

    /**
     * 
     * @param x
     */
    public MatrixFlt(float[] x)
    {

        A = new float[1][];
        A[0] = x;
        m = 1;
        n = x.length;
    }

    /**
     * Construct an m-by-1 constant matrix.
     * 
     * @param mn
     * @param s
     *            Fill the matrix with this scalar value.
     */
    public MatrixFlt(int[] mn, float s)
    {
        if (mn == null)
        {
            throw new ConditionalException("Matrix : Parameter \"mn\" must be non-null.");
        }
        if (mn.length != 2)
        {
            throw new ConditionalException("Matrix : Length of integer array parameter \"mn\" must be 2;");
        }
        Indices ind = new Indices(mn);
        if (ind.LTEQ(0).anyBoolean())
        {
            throw new ConditionalException("Matrix : Elements of integer array parameter \"mn\" must be positive.");
        }

        this.m = mn[0];
        this.n = mn[1];
        A = new float[m][n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = s;
            }
        }
    }

    /**
     * Construct an m-by-n constant matrix.
     * 
     * @param m
     *            Number of rows.
     * @param n
     * @param s
     *            Fill the matrix with this scalar value.
     */
    public MatrixFlt(int m, int n, float s)
    {
        this.m = m;
        this.n = n;
        A = new float[m][n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = s;
            }
        }
    }

    /**
     * Construct a matrix from a 2-D array.
     * 
     * @param A
     *            Two-dimensional array of doubles.
     * @exception IllegalArgumentException
     *                All rows must have the same length
     * @see #constructWithCopy
     */
    public MatrixFlt(double[][] A)
    {
        m = A.length;
        n = A[0].length;
        this.A = new float[m][n];
        for (int i = 0; i < m; i++)
        {
            if (A[i].length != n)
            {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
            for (int j = 0; j < n; j++)
            {
                this.A[i][j] = (float) A[i][j];
            }
        }

    }

    /**
     * Construct a matrix from a 2-D array.
     * 
     * @param A
     *            Two-dimensional array of doubles.
     * @exception IllegalArgumentException
     *                All rows must have the same length
     * @see #constructWithCopy
     */
    public MatrixFlt(double[] A)
    {
        m = 1;// A.length;
        n = A.length;
        this.A = new float[m][n];

        for (int j = 0; j < n; j++)
        {
            this.A[0][j] = (float) A[j];
        }
    }

    /**
     * Construct a matrix from a 2-D array.
     * 
     * @param A
     *            Two-dimensional array of doubles.
     * @exception IllegalArgumentException
     *                All rows must have the same length
     * @see #constructWithCopy
     */
    public MatrixFlt(float[][] A)
    {
        m = A.length;
        n = A[0].length;
        for (int i = 0; i < m; i++)
        {
            if (A[i].length != n)
            {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
        }
        this.A = A;
    }

    /**
     * Construct a matrix quickly without checking arguments.
     * 
     * @param A
     *            Two-dimensional array of doubles.
     * @param m
     *            Number of rows.
     * @param n
     * 
     */
    public MatrixFlt(float[][] A, int m, int n)
    {
        this.A = A;

        boolean warn = m < 0 || n < 0;
        if (warn)
        {
            throw new ConditionalException("Matrix : Both integer parameters \"m\" (= " + m + ") and \"n\" (= " + n
                    + ") must be non-negative.");
        }

        this.m = m;
        this.n = n;
    }

    /**
     * Construct a matrix from a one-dimensional packed array
     * 
     * @param vals
     *            One-dimensional array of doubles, packed by columns (ala
     *            Fortran).
     * @param m
     *            Number of rows.
     * @exception IllegalArgumentException
     *                Array length must be a multiple of m.
     */
    public MatrixFlt(float vals[], int m)
    {
        this.m = m;
        n = (m != 0 ? vals.length / m : 0);
        if (m * n != vals.length)
        {
            throw new IllegalArgumentException("Array length must be a multiple of m.");
        }
        A = new float[m][n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = vals[i + j * m];
            }
        }
    }

    /**
     * Make a deep copy of a matrix
     * 
     * @return
     */
    public MatrixFlt copy()
    {

        if (A == null)
        {
            return new MatrixFlt(null, m, n);
        }

        MatrixFlt X = new MatrixFlt(m, n);
        float[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j];
            }
        }
        // Matrix temp = new Matrix(R);
        if (this.isVector() && this.sorted == true)
        {
            X.setSorted(true);
        }
        return X;
    }

    /**
     * 
     * @return
     */
    public boolean isVector()
    {
        return (isRowVector() || isColVector());
    }

    /**
     * Test if this matrix is a i vector, eg: If A = (6,8,9,-1) , a 1 x 4 matix,
     * A.isRowVector() returns true;
     * 
     * @return True if this matrix is a i vector OR otherwise false.
     */
    public boolean isRowVector()
    {
        boolean r = false;
        if (m == 1)
        {
            r = true;
        }
        return r;
    }

    /**
     * Test if this matrix is a column vector , eg: ( 3 ) If matrix A = | 4 | ,
     * a 3 x 1 matrix is returned. ( 5 ) Matrix A is a column vector matrix,
     * which A.isColumnVector() returns true.
     * 
     * @return True if this matrix is a column vector OR otherwise false.
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
     * Access the internal two-dimensional array.
     * 
     * @return Pointer to the two-dimensional array of matrix elements.
     */
    public float[][] getArray()
    {
        return A;
    }

    /**
     * Copy the internal two-dimensional array.
     * 
     * @return Two-dimensional array copy of matrix elements.
     */
    public float[][] getArrayCopy()
    {
        float[][] C = new float[m][n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j];
            }
        }
        return C;
    }

    public void setSorted(boolean sorted)
    {
        this.sorted = sorted;
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
        Indices indices = null;
        int[][] M = null;

        if (this.isVector())
        {
            if (this.isRowVector())
            {
                indices = new Indices(1, n);
                M = indices.getArray();
                for (int j = 0; j < n; j++)
                {
                    M[0][j] = j;
                }
            }
            else
            {
                indices = new Indices(m, 1);
                M = indices.getArray();
                for (int i = 0; i < m; i++)
                {
                    M[i][0] = i;
                }
            }
            return indices;
        }

        indices = new Indices(m, n);
        M = indices.getArray();
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

    /**
     * Get i dimension.
     * 
     * @return m, the number of rows.
     */
    public int getRowDimension()
    {
        return m;
    }

    /**
     * Get column dimension.
     * 
     * @return lenC, the number of columns.
     */
    public int getColumnDimension()
    {
        return n;
    }

    /**
     * Set a single element.
     * 
     * @param i
     *            Row index.
     * @param j
     *            Column index.
     * @param s
     *            A(i,j).
     * @exception ArrayIndexOutOfBoundsException
     */
    public void set(int i, int j, float s)
    {
        A[i][j] = s;
    }

    /**
     * Get a single element.
     * 
     * @param i
     *            Row index.
     * @param j
     *            Column index.
     * @return A(i,j)
     * @exception ArrayIndexOutOfBoundsException
     */
    public float get(int i, int j)
    {
        return A[i][j];
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
    public Indices sizeIndices()
    {
        return new Indices(sizeIntArr());
    }

    /**
     * 
     * @param ind
     * @param val
     */
    public void setElementAt(int ind, float val)
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
     * Retrieve an element as an index from column-like vector reference
     * 
     * @param ind
     * @return
     */
    public float getElementAt(int ind)
    {
        float val = 1.0f;
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

    /**
     * 
     * @return
     */
    public MatrixFlt round()
    {
        MatrixFlt X = new MatrixFlt(m, n);
        float[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = (float) Math.round(A[i][j]);
            }
        }
        return X;
    }

    /**
     * Generate matrix with random elements
     * 
     * @param m
     *            Number of rows.
     * @param n
     * @return An m-by-n matrix with uniformly distributed random elements.
     */
    public static MatrixFlt random(int m, int n)
    {
        MatrixFlt A = new MatrixFlt(m, n);
        float[][] X = A.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                X[i][j] = (float) Math.random();
            }
        }
        return A;
    }

    /**
     * Element-by-element multiplication, C = A.*B
     * 
     * @param B
     *            another matrix
     * @return A.*B
     */
    public MatrixFlt arrayTimes(double B)
    {

        MatrixFlt X = new MatrixFlt(m, n);
        float[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = (float) (A[i][j] * B);
            }
        }
        return X;
    }

    public void printInLabel(String name)
    {
        printInLabel(name, 12, 4);
    }

    public void printInLabel(String name, int d)
    {
        printInLabel(name, 12, d);
    }

    public void printInLabel(String name, int width, int d)
    {
        printInLabel(name, width, d, '-', 25);
    }

    public void printInLabel(String name, int width, int d, char ch, int numChar)
    {
        String line = MathUtil.genStringChars(ch, numChar);
        System.out.print("\n" + line + " " + name + " " + line);
        this.print(width, d);
    }

    /**
     * Print the matrix to stdout. Line the elements up in columns with a
     * Fortran-like 'Fw.d' style format.
     * 
     * @param w
     *            Column width.
     * @param d
     *            Number of digits after the decimal.
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
                    String s = format.format(A[i][j]); // format the number

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

    /**
     * Get a submatrix.
     * 
     * @param i0
     *            Initial i index
     * @param i1
     *            Final i index
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @return A(i0:i1,j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public MatrixFlt getMatrix(int i0, int i1, int j0, int j1)
    {
        MatrixFlt X = new MatrixFlt(i1 - i0 + 1, j1 - j0 + 1);
        float[][] B = X.getArray();
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
     * @param r
     *            Array of i indices.
     * @param c
     *            Array of column indices.
     * @return A(r(:),c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public MatrixFlt getMatrix(int[] r, int[] c)
    {
        MatrixFlt X = new MatrixFlt(r.length, c.length);
        float[][] B = X.getArray();
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
     * 
     * @param arrayList
     * @return
     */
    public MatrixFlt getMatrix(ArrayList<int[]> arrayList)
    {
        int siz = arrayList.size();
        if (siz == 0)
        {
            return null;
        }
        MatrixFlt matrix = new MatrixFlt(siz, 1);
        float[][] X = matrix.getArray();
        int[] indices = null;
        for (int i = 0; i < siz; i++)
        {
            indices = (int[]) arrayList.get(i);
            X[i][0] = A[indices[0]][indices[1]];
        }
        return matrix;
    }

    /**
     * Get a submatrix.
     * 
     * @param i0
     *            Initial i index
     * @param i1
     *            Final i index
     * @param c
     *            Array of column indices.
     * @return A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public MatrixFlt getMatrix(int i0, int i1, int[] c)
    {
        MatrixFlt X = new MatrixFlt(i1 - i0 + 1, c.length);
        float[][] B = X.getArray();
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
     * @param i0
     *            Initial i index
     * @param c
     *            Array of column indices.
     * @return A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public MatrixFlt getMatrix(int i0, int[] c)
    {
        return getMatrix(i0, i0, c);
    }

    /**
     * Get a submatrix.
     * 
     * @param r
     *            Array of i indices.
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @return A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public MatrixFlt getMatrix(int[] r, int j0, int j1)
    {
        MatrixFlt X = new MatrixFlt(r.length, j1 - j0 + 1);
        float[][] B = X.getArray();
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
     * @param r
     *            Array of i indices.
     * @param j0
     *            Initial column index
     * @return A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public MatrixFlt getMatrix(int[] r, int j0)
    {
        return getMatrix(r, j0, j0);
    }

    /**
     * Matrix transpose.
     * 
     * @return A'
     */
    public MatrixFlt transpose()
    {
        MatrixFlt X = new MatrixFlt(n, m);
        float[][] C = X.getArray();
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
     * Make a one-dimensional i packed copy of the internal array.
     * 
     * @return Matrix elements packed in a one-dimensional array by rows.
     */
    public float[] getRowPackedCopy()
    {
        float[] vals = new float[m * n];
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
     * Make a one-dimensional column packed copy of the internal array.
     * 
     * @return Matrix elements packed in a one-dimensional array by columns.
     */
    public float[] getColumnPackedCopy()
    {
        float[] vals = new float[m * n];
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
     * Flip this matrix from Left to Right, eg: ( 5, 10, 15 ) If A = | 4, 9, 14
     * | , ( 3, 8, 13 )
     * 
     * flipLR(); ( 15, 10, 5 ) A = | 14, 9, 4 | , matrix A has been flipped
     * left-to-right. ( 13, 8, 3 )
     * 
     * @return flipped Matrix
     */
    public MatrixFlt flipLR()
    {
        MatrixFlt X = new MatrixFlt(m, n);
        float[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            C[i] = MathUtil.flip(A[i]);
        }
        return X;
    }

    /**
     * Flip this matrix from Top to Bottom OR Upside-Down, eg: ( 5, 10, 15 ) If
     * A = | 4, 9, 14 | , ( 3, 8, 13 )
     * 
     * flipUD(); ( 3, 8, 13 ) A = | 4, 9, 14 | , matrix A has been flipped
     * upside down. ( 5, 10, 15 )
     * 
     * 
     * @return flipped Matrix
     */
    public MatrixFlt flipUD()
    {
        MatrixFlt X = new MatrixFlt(m, n);
        float[][] C = X.getArray();
        float[][] temp = this.getArrayCopy();
        float[] D = null;
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

    public MatrixFlt scale(Number num)
    {
        return this.arrayTimes(num.doubleValue());
    }

    public static MatrixFlt randn(int rows, int cols)
    {
        MatrixFlt rand = new MatrixFlt(rows, cols);
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                rand.set(i, j, (float) MathUtil.randn());
            }
        }
        return rand;
    }

    public static MatrixFlt randn(int sqRows)
    {
        return randn(sqRows, sqRows);
    }

    /**
     * Generate matrix with random elements
     * 
     * @param m
     *            Number of rows.
     * @param n
     * @return An m-by-n matrix with uniformly distributed random elements.
     */
    public static MatrixFlt rand(int m, int n)
    {
        return random(m, n);
    }
}
