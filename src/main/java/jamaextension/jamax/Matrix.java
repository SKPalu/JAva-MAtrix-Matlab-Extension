package jamaextension.jamax;

/**
 * @author Sione Palu
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
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.LogicalAnyMat;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortMat;
import jamaextension.jamax.ops.JOps;
import jamaextension.jamax.ops.Unique;
import jamaextension.jamax.ops.UniqueSet;
import jamaextension.jamax.strfun.StrUtil;

//import com.feynmanperceptron.numerical.jamax.LUDecomposition;
public class Matrix implements java.io.Serializable, java.lang.Cloneable
{

    /*
     * ------------------------ Class variables ------------------------
     */
    /**
     * Array for internal storage of elements.
     * 
     * @serial internal array storage.
     */
    private double[][] A;
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
    public Matrix()
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
    public Matrix(int m, int n)
    {
        this.m = m;
        this.n = n;
        boolean warn = m < 1 || n < 1;
        if (warn)
        {
            throw new ConditionalException("Matrix : Both integer parameters \"m\" (= " + m + ") and \"n\" (= " + n
                    + ") must be at least 1.");
        }
        A = new double[m][n];
    }

    public Matrix(int[] mn)
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
        A = new double[mn[0]][mn[1]];

    }

    /**
     * 
     * @param x
     */
    public Matrix(double[] x)
    {

        A = new double[1][];
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
    public Matrix(int[] mn, double s)
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
        A = new double[m][n];
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
    public Matrix(int m, int n, double s)
    {
        this.m = m;
        this.n = n;
        A = new double[m][n];
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
    public Matrix(double[][] A)
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
     * Construct a matrix from a 2-D array.
     * 
     * @param A
     *            Two-dimensional array of doubles.
     * @exception IllegalArgumentException
     *                All rows must have the same length
     * @see #constructWithCopy
     */
    public Matrix(float[][] A)
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

        this.A = new double[m][n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                this.A[i][j] = A[i][j];
            }
        }
    }

    public Matrix(float[] A)
    {
        m = 1;
        n = A.length;
        this.A = new double[1][n];

        for (int j = 0; j < n; j++)
        {
            this.A[0][j] = A[j];
        }

        /*
         * for (int i = 0; i < m; i++) { if (A[i].length != n) { throw new
         * IllegalArgumentException("All rows must have the same length."); } }
         * 
         * this.A = new double[m][n]; for (int i = 0; i < m; i++) { for (int j =
         * 0; j < n; j++) { this.A[i][j] = A[i][j]; } }
         */
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
    public Matrix(double[][] A, int m, int n)
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
    public Matrix(double vals[], int m)
    {
        this.m = m;
        n = (m != 0 ? vals.length / m : 0);
        if (m * n != vals.length)
        {
            throw new IllegalArgumentException("Array length must be a multiple of m.");
        }
        A = new double[m][n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = vals[i + j * m];
            }
        }
    }

    public void set(double[][] doubleArr)
    {
        if (doubleArr == null)
        {
            this.m = 0;
            this.n = 0;
        }
        else
        {
            this.m = doubleArr.length;
            this.n = doubleArr[0].length;
        }
        this.A = doubleArr;
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
    public void set(int i, int j, double s)
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
    public double get(int i, int j)
    {
        return A[i][j];
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

    public Matrix getColumns(int from, int to)
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
    public Matrix getColumns(int[] indices)
    {
        int len = indices.length;
        Matrix val = new Matrix(m, len);
        for (int i = 0; i < len; i++)
        {
            if (indices[i] > (n - 1) || indices[i] < 0)
            {
                throw new IllegalArgumentException(" getColumns: Array out-of-bounds.");
            }
            Matrix temp = this.getMatrix(0, m - 1, indices[i], indices[i]);
            val.setMatrix(0, m - 1, i, i, temp);
        }
        return val;
    }// end method

    /**
     * 
     * @param colIndex
     *            int
     * @return Matrix
     */
    public Matrix getColumnAt(int colIndex)
    {
        return getColumns(new int[]
        {
            colIndex
        });
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
    public Matrix getMatrix(int i0, int i1, int j0, int j1)
    {
        Matrix X = new Matrix(i1 - i0 + 1, j1 - j0 + 1);
        double[][] B = X.getArray();
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
    public Matrix getMatrix(int[] r, int[] c)
    {
        Matrix X = new Matrix(r.length, c.length);
        double[][] B = X.getArray();
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
    public Matrix getMatrix(ArrayList arrayList)
    {
        int siz = arrayList.size();
        if (siz == 0)
        {
            return null;
        }
        Matrix matrix = new Matrix(siz, 1);
        double[][] X = matrix.getArray();
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
    public Matrix getMatrix(int i0, int i1, int[] c)
    {
        Matrix X = new Matrix(i1 - i0 + 1, c.length);
        double[][] B = X.getArray();
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
    public Matrix getMatrix(int i0, int[] c)
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
    public Matrix getMatrix(int[] r, int j0, int j1)
    {
        Matrix X = new Matrix(r.length, j1 - j0 + 1);
        double[][] B = X.getArray();
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
    public Matrix getMatrix(int[] r, int j0)
    {
        return getMatrix(r, j0, j0);
    }

    /**
     * Set a submatrix.
     * 
     * @param i0
     *            Initial i index
     * @param i1
     *            Final i index
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @param X
     *            A(i0:i1,j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int i0, int i1, int j0, int j1, Object X)
    {
        if (!(X instanceof Matrix) && !(X instanceof Indices))
        {
            throw new ArrayIndexOutOfBoundsException(
                    "setMatrix : Object \"X\" must either be of type \"Matrix\" or \"Indices\".");
        }
        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    if (X instanceof Matrix)
                    {
                        A[i][j] = ((Matrix) X).get(i - i0, j - j0);
                    }
                    else
                    {
                        A[i][j] = ((Indices) X).get(i - i0, j - j0);
                    }

                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("setMatrix : Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param r
     *            Array of i indices.
     * @param c
     *            Array of column indices.
     * @param X
     *            A(r(:),c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int[] r, int[] c, Object X)
    {
        if (!(X instanceof Matrix) && !(X instanceof Indices))
        {
            throw new ArrayIndexOutOfBoundsException(
                    "setMatrix : Object \"X\" must either be of type \"Matrix\" or \"Indices\".");
        }
        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    if (X instanceof Matrix)
                    {
                        A[r[i]][c[j]] = ((Matrix) X).get(i, j);
                    }
                    else
                    {
                        A[r[i]][c[j]] = ((Indices) X).get(i, j);
                    }

                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("setMatrix : Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param r
     *            Array of i indices.
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @param X
     *            A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int[] r, int j0, int j1, Object X)
    {
        if (!(X instanceof Matrix) && !(X instanceof Indices))
        {
            throw new ArrayIndexOutOfBoundsException(
                    "setMatrix : Object \"X\" must either be of type \"Matrix\" or \"Indices\".");
        }
        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    if (X instanceof Matrix)
                    {
                        A[r[i]][j] = ((Matrix) X).get(i, j - j0);
                    }
                    else
                    {
                        A[r[i]][j] = ((Indices) X).get(i, j - j0);
                    }

                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("setMatrix : Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param r
     *            Array of i indices.
     * @param j0
     *            Initial column index
     * @param X
     *            A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int[] r, int j0, Object X)
    {
        if (!(X instanceof Matrix) && !(X instanceof Indices))
        {
            throw new ArrayIndexOutOfBoundsException(
                    "setMatrix : Object \"X\" must either be of type \"Matrix\" or \"Indices\".");
        }
        setMatrix(r, j0, j0, X);
    }

    /**
     * Set a submatrix.
     * 
     * @param i0
     *            Initial i index
     * @param i1
     *            Final i index
     * @param c
     *            Array of column indices.
     * @param X
     *            A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int i0, int i1, int[] c, Object X)
    {
        if (!(X instanceof Matrix) && !(X instanceof Indices))
        {
            throw new ArrayIndexOutOfBoundsException(
                    "setMatrix : Object \"X\" must either be of type \"Matrix\" or \"Indices\".");
        }
        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    if (X instanceof Matrix)
                    {
                        A[i][c[j]] = ((Matrix) X).get(i - i0, j);
                    }
                    else
                    {
                        A[i][c[j]] = ((Indices) X).get(i - i0, j);
                    }

                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("setMatrix : Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param i0
     *            Initial i index
     * @param c
     *            Array of column indices.
     * @param X
     *            A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int i0, int[] c, Object X)
    {
        if (!(X instanceof Matrix) && !(X instanceof Indices))
        {
            throw new ArrayIndexOutOfBoundsException(
                    "setMatrix : Object \"X\" must either be of type \"Matrix\" or \"Indices\".");
        }
        setMatrix(i0, i0, c, X);
    }

    /**
     * Matrix transpose.
     * 
     * @return A'
     */
    public Matrix transpose()
    {
        Matrix X = new Matrix(n, m);
        double[][] C = X.getArray();
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
     * Access the internal two-dimensional array.
     * 
     * @return Pointer to the two-dimensional array of matrix elements.
     */
    public double[][] getArray()
    {
        return A;
    }

    /**
     * Copy the internal two-dimensional array.
     * 
     * @return Two-dimensional array copy of matrix elements.
     */
    public double[][] getArrayCopy()
    {
        double[][] C = new double[m][n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j];
            }
        }
        return C;
    }

    public Cell toCell()
    {
        Cell C = new Cell(m, n);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Double val = new Double(A[i][j]);
                C.set(i, j, val);
            }
        }
        return C;
    }

    public Cell toCellString()
    {
        return toCellString(2);
    }

    public Cell toCellString(int decplaces)
    {
        boolean full = decplaces < 0;
        Cell C = new Cell(m, n);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                // Double val = new Double(A[i][j]);
                if (!full)
                {
                    String str = String.format("%." + decplaces + "f", A[i][j]);
                    C.set(i, j, str);
                }
                else
                {
                    Double val = new Double(A[i][j]);
                    C.set(i, j, val.toString());
                }
            }
        }
        return C;
    }

    /**
     * Cholesky Decomposition
     * 
     * @return CholeskyDecomposition
     * @see CholeskyDecomposition
     */
    public CholeskyDecomposition chol()
    {
        return new CholeskyDecomposition(this);
    }

    /**
     * Singular Value Decomposition
     * 
     * @return SingularValueDecomposition
     * @see SingularValueDecomposition
     */
    public SingularValueDecomposition svd()
    {
        return new SingularValueDecomposition(this);
    }

    /**
     * Eigenvalue Decomposition
     * 
     * @return EigenvalueDecomposition
     * @see EigenvalueDecomposition
     */
    public EigenvalueDecomposition eig()
    {
        return new EigenvalueDecomposition(this);
    }

    /**
     * LU Decomposition
     * 
     * @return LUDecomposition
     * @see LUDecomposition
     */
    public LUDecomposition lu()
    {
        return new LUDecomposition(this);
    }

    /**
     * QR Decomposition
     * 
     * @return QRDecomposition
     * @see QRDecomposition
     */
    public QRDecomposition qr()
    {
        return new QRDecomposition(this);
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

    public String mat2str()
    {
        return mat2str(4);
    }

    public String mat2str(int d)
    {
        return mat2str(0, d);
    }

    public String mat2str(int newLineEmptyPad, int d)
    {
        return mat2str(newLineEmptyPad, 10, d);
    }

    public String mat2str(int newLineEmptyPad, int width, int d)
    {
        DecimalFormat format = new DecimalFormat();
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);

        StringBuilder output = new StringBuilder();
        String emptyPadStr = "";
        if (newLineEmptyPad > 0)
        {
            emptyPadStr = StrUtil.padwidth(newLineEmptyPad);
        }

        // output.append("\n"); // start on new line.
        if (this.A != null)
        {
            for (int i = 0; i < m; i++)
            {
                output.append(emptyPadStr).append("");
                for (int j = 0; j < n; j++)
                {
                    String s = format.format(A[i][j]); // format the number

                    int padding = Math.max(1, width - s.length()); // At _least_
                                                                   // 1 space

                    if (j > 0)
                    {
                        for (int k = 0; k < padding; k++)
                        {
                            output.append(' ');
                        }
                    }
                    output.append(s);
                }
                if (m > 1)
                {
                    output.append("\n");
                }
            }
        }
        else
        {
            String empty = "Empty matrix: " + m + "-by-" + n;
            output.append(empty);
        }
        // output.append("\n"); // end with blank line.

        return output.toString();
    }

    /**
     * Matrix inverse OR pseudoinverse
     * 
     * @return inverse(A) if A is square, pseudoinverse otherwise.
     */
    public Matrix inverse()
    {
        return solve(identity(m, m));
    }

    /**
     * Overloaded identity method
     * 
     * @param s
     * @return
     */
    public static Matrix identity(int s)
    {
        return identity(s, s);
    }

    /**
     * Generate identity matrix
     * 
     * @param m
     *            Number of rows.
     * @param n
     * @return An m-by-n matrix with ones on the diagonal AND zeros elsewhere.
     */
    public static Matrix identity(int m, int n)
    {
        Matrix A = new Matrix(m, n);
        double[][] X = A.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                X[i][j] = (i == j ? 1.0 : 0.0);
            }
        }
        return A;
    }

    public static Matrix eye(int[] rc)
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

    public static Matrix eye(int s)
    {
        return identity(s, s);
    }

    public static Matrix eye(int m, int n)
    {
        return identity(m, n);
    }

    public static Matrix ones(int[] rc)
    {
        if (rc == null)
        {
            throw new ConditionalException("ones : Parameter \"rc\" must be non-null.");
        }
        if (rc.length != 2)
        {
            throw new ConditionalException("ones : Length of integer array parameter \"rc\" must be 2;");
        }
        Indices ind = new Indices(rc);
        if (ind.LTEQ(0).anyBoolean())
        {
            throw new ConditionalException("ones : Elements of integer array parameter \"rc\" must be positive.");
        }
        return ones(rc[0], rc[1]);
    }

    /**
     * 
     * @param m
     * @param n
     * @return
     */
    public static Matrix ones(int m, int n)
    {
        return new Matrix(m, n, 1.0);
    }

    /**
     * 
     * @param m
     * @return
     */
    public static Matrix ones(int m)
    {
        return ones(m, m);
    }

    /**
     * Solve A*X = B
     * 
     * @param B
     *            right hand side
     * @return solution if A is square, least squares solution otherwise
     */
    public Matrix solve(Matrix B)
    {

        return (m == n ? (new LUDecomposition(this)).solve(B) : (new QRDecomposition(this)).solve(B));

    }

    public Matrix solveJLap(Matrix B)
    {
        return (m == n ? (new LUDecomposition(this)).solve(B) : (new QRDecomposition(this)).solve(B));
    }

    /**
     * Matrix transpose.
     * 
     * @return A'
     */
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
    public void prettyPrint(PrintWriter output, int w, int d)
    {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        prettyPrint(output, format, w + 2);
    }

    public String matrixToString()
    {
        return matrixToString(8, 4);
    }

    public String matrixToString(int w, int d)
    {

        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);

        int width = w + 2;

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                String s = format.format(A[i][j]); // format the number

                int padding = Math.max(1, width - s.length()); // At _least_ 1
                                                               // space

                for (int k = 0; k < padding; k++)
                {
                    // output.print(' ');
                    builder = builder.append(" ");
                }
                // output.print(s);
                builder = builder.append(s);
            }

            if (i < m - 1)
            {
                builder = builder.append("\n");
            }
        }

        return builder.toString();
    }

    /**
     * Print the matrix to the output stream. Line the elements up in columns.
     * Use the format object, AND right justify within columns of width
     * characters. Note that is the matrix is to be read back in, you probably
     * will want to use a NumberFormat that is set to US Locale.
     * 
     * 
     * @param output
     *            the output stream.
     * @param format
     *            A formatting object to format the matrix elements
     * @param width
     *            Column width.
     * @see java.text.DecimalFormat#setDecimalFormatSymbols
     */
    public void prettyPrint(PrintWriter output, NumberFormat format, int width)
    {

        // int m = matrix.getRowDimension();
        // int lenC = matrix.getColumnDimension();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                String s = format.format(A[i][j]); // format the number

                int padding = Math.max(1, width - s.length()); // At _least_ 1
                                                               // space

                for (int k = 0; k < padding; k++)
                {
                    output.print(' ');
                }
                output.print(s);
            }

            if (i < m - 1)
            {
                output.println();
            }
        }
    }

    public double norm()
    {
        return norm(null);
    }

    public double norm(Object p)
    {
        if (p != null)
        {
            if (!(p instanceof Integer) && !(p instanceof String))
            {
                throw new ConditionalException(
                        "norm : Object parameter \"p\" must an instanceof \"Integer\" or \"String\".");
            }
            if (p instanceof Integer)
            {
                int val = ((Integer) p).intValue();
                if (val != 1 && val != 2)
                {
                    throw new ConditionalException("norm : Value for Integer parameter \"p\" must either be 1 or 2 ;");
                }
            }
            else
            {
                String val = ((String) p).toLowerCase();
                if ("".equals(val))
                {
                    throw new ConditionalException("norm : Valid strings for \"p\" must either be \"inf\" or \"fro\".");
                }
                boolean tf = !"inf".equals(val) && !"fro".equals(val);
                if (tf)
                {
                    throw new ConditionalException("norm : Valid strings for \"p\" must either be \"inf\" or \"fro\".");
                }
            }
        }

        double norm = 0.5;

        if (p == null)
        {
            p = new Integer(2);
        }

        if (p instanceof Integer)
        {
            int pInt = ((Integer) p).intValue();
            if (pInt == 1)
            {
                norm = this.norm1();
            }
            else
            {
                norm = this.norm2();
            }
        }
        else
        {
            String pStr = (String) p;
            if ("inf".equals(pStr))
            {
                norm = this.normInf();
            }
            else
            {
                norm = this.normF();
            }
        }

        return norm;
    }

    /**
     * One norm
     * 
     * @return maximum column sum.
     */
    public double norm1()
    {
        double f = 0;
        for (int j = 0; j < n; j++)
        {
            double s = 0;
            for (int i = 0; i < m; i++)
            {
                s += Math.abs(A[i][j]);
            }
            f = Math.max(f, s);
        }
        return f;
    }

    /**
     * Two norm
     * 
     * @return maximum singular value.
     */
    public double norm2()
    {
        return (new SingularValueDecomposition(this).norm2());
    }

    /**
     * Infinity norm
     * 
     * @return maximum i sum.
     */
    public double normInf()
    {
        double f = 0;
        for (int i = 0; i < m; i++)
        {
            double s = 0;
            for (int j = 0; j < n; j++)
            {
                s += Math.abs(A[i][j]);
            }
            f = Math.max(f, s);
        }
        return f;
    }

    /*
     * DOT  Vector dot product.
     * C = DOT(A,B) returns the scalar product of the vectors A and B.
     * A and B must be vectors of the same length.  When A and B are both
     * column vectors, DOT(A,B) is the same as A'*B.
     */
    public Matrix dot(Matrix B, Dimension dim){
        	 	
    	Matrix dotVal = null;
    	Matrix a = this;
    	Matrix b = B;
    	
    	 if(!this.isVector() && !B.isVector() && dim==null){
    		 if(JDatafun.min(a.sizeIndices()).start()==1){
    			a = a.toColVector(); 
    		 }
    		 if(JDatafun.min(b.sizeIndices()).start()==1){
    			b = b.toColVector();
    		 }
    	 }
    	 
    	 // Check dimensions
			if(a.sizeIndices().NEQ(b.sizeIndices()).anyBoolean()){// any(size(a)~=size(b)),
			   //error(message('MATLAB:dot:InputSizeMismatch'));
				throw new ConditionalRuleException("Mismatch matrix sizes");
			}
			
			if(dim==null){
				dotVal = JDatafun.sum(a.arrayTimes(b));
			}
			else{
				dotVal = JDatafun.sum(a.arrayTimes(b),dim);
			}
    	
    	
    	return dotVal;
    }
    
    public Matrix dot(Matrix B){
    	return dot(B,null);
    }
    
    /*public double dot(Matrix B)
    {
        double val = 0.0;

        if (!this.isVector())
        {
            throw new ConditionalRuleException("dot : This object must be a row or column vector and not a matrix.");
        }
        if (!B.isVector())
        {
            throw new ConditionalRuleException("dot : Input argument must be a row or column vector and not a matrix.");
        }

        int len = this.length();
        int len2 = B.length();

        if (len != len2)
        {
            throw new ConditionalRuleException("dot : The lengths of 2 vectors must be the same.");
        }

        Matrix tmp = null;
        if (this.isRowVector())
        {
            if (B.isRowVector())
            {
                tmp = this.arrayTimes(B);
            }
            else
            {
                tmp = this.arrayTimes(B.transpose());
            }
        }
        else
        {
            if (B.isRowVector())
            {
                tmp = this.arrayTimes(B.transpose());
            }
            else
            {
                tmp = this.arrayTimes(B);
            }
        }

        val = JDatafun.sum(tmp).start();

        return val;
    }*/

    /**
     * Frobenius norm
     * 
     * @return sqrt of sum of squares of ALL elements.
     */
    public double normF()
    {
        double f = 0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                f = MathUtil.hypot(f, A[i][j]);
            }
        }
        return f;
    }

    /**
     * Unary minusCP
     * 
     * @return -A
     */
    public Matrix uminus()
    {
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = -A[i][j];
            }
        }
        return X;
    }

    /**
     * C = A + B
     * 
     * @param B
     *            another matrix
     * @return A + B
     */
    public Matrix plus(Matrix B)
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
        checkMatrixDimensions(B);
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j] + B.A[i][j];
            }
        }
        return X;
    }

    /**
     * 
     * @param B
     * @return
     */
    public Matrix plus(Indices B)
    {
        // ---------- modified/added on 16/01/11 ---------
        if (B.numel() == 1)
        {
            return this.plus((double) B.start());
        }
        if (this.numel() == 1)
        {
            return Matrix.indicesToMatrix(B).plus(A[0][0]);
        }
        // -----------------------------------------------
        checkMatrixDimensions(B);
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j] + (double) B.get(i, j);
            }
        }
        return X;
    }

    /**
     * C = A + b
     * 
     * @param b
     * @return A + b
     */
    public Matrix plus(double b)
    {

        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j] + b;
            }
        }
        return X;
    }

    /**
     * A = A + B
     * 
     * @param B
     *            another matrix
     * @return A + B
     */
    public Matrix plusEquals(Matrix B)
    {
        checkMatrixDimensions(B);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = A[i][j] + B.A[i][j];
            }
        }
        return this;
    }

    /**
     * C = A - B
     * 
     * @param B
     *            another matrix
     * @return A - B
     */
    public Matrix minus(Matrix B)
    {
        // ---------- modified/added on 16/01/11 ---------
        if (B.numel() == 1)
        {
            return this.minus(B.start());
        }
        if (this.numel() == 1)
        {
            return B.uminus().plus(A[0][0]);
        }
        // -----------------------------------------------
        checkMatrixDimensions(B);
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j] - B.A[i][j];
            }
        }
        return X;
    }

    /**
     * 
     * @param B
     * @return
     */
    public Matrix minus(Indices B)
    {
        // ---------- modified/added on 16/01/11 ---------
        if (B.numel() == 1)
        {
            return this.minus((double) B.start());
        }
        if (this.numel() == 1)
        {
            return Matrix.indicesToMatrix(B).uminus().plus(A[0][0]);
        }
        // -----------------------------------------------
        checkMatrixDimensions(B);
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j] - (double) B.get(i, j);
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
    public Matrix minus(double B)
    {

        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j] - B;
            }
        }
        return X;
    }

    /**
     * A = A - B
     * 
     * @param B
     *            another matrix
     * @return A - B
     */
    public Matrix minusEquals(Matrix B)
    {
        checkMatrixDimensions(B);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = A[i][j] - B.A[i][j];
            }
        }
        return this;
    }

    /**
     * Element-by-element multiplication, C = A.*B
     * 
     * @param B
     *            another matrix
     * @return A.*B
     */
    public Matrix arrayTimes(Matrix B)
    {
        if (this.length() == 1)
        {
            return B.arrayTimes(A[0][0]);
        }
        else if (B.length() == 1)
        {
            return this.arrayTimes(B.get(0, 0));
        }

        checkMatrixDimensions(B); // Matrix.indicesToMatrix(null)
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j] * B.A[i][j];
            }
        }
        return X;
    }

    /**
     * 
     * @param B
     * @return
     */
    public Matrix arrayTimes(Indices B)
    {
        if (this.length() == 1)
        {
            return Matrix.indicesToMatrix(B).arrayTimes(A[0][0]);
        }
        else if (B.length() == 1)
        {
            return this.arrayTimes((double) B.get(0, 0));
        }

        checkMatrixDimensions(B);
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j] * (double) B.get(i, j);
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
    public Matrix arrayTimes(Number B)
    {

        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j] * B.doubleValue();
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
    public Matrix arrayTimes(double B)
    {

        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j] * B;
            }
        }
        return X;
    }

    /**
     * Element-by-element multiplication in place, A = A.*B
     * 
     * @param B
     *            another matrix
     * @return A.*B
     */
    public Matrix arrayTimesEquals(Matrix B)
    {
        checkMatrixDimensions(B);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = A[i][j] * B.A[i][j];
            }
        }
        return this;
    }

    /**
     * Element-by-element right division, C = A./B
     * 
     * @param B
     *            another matrix
     * @return A./B
     */
    public Matrix arrayRightDivide(Matrix B)
    {
        checkMatrixDimensions(B);
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j] / B.A[i][j];
            }
        }
        return X;
    }

    /**
     * 
     * @param B
     * @return
     */
    public Matrix arrayRightDivide(Indices B)
    {
        checkMatrixDimensions(B);
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j] / (double) B.get(i, j);
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
    public Matrix arrayRightDivide(double B)
    {

        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = A[i][j] / B;
            }
        }

        return X;
    }

    /**
     * Element-by-element right division in place, A = A./B
     * 
     * @param B
     *            another matrix
     * @return A./B
     */
    public Matrix arrayRightDivideEquals(Matrix B)
    {
        checkMatrixDimensions(B);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = A[i][j] / B.A[i][j];
            }
        }
        return this;
    }

    /**
     * Element-by-element left division, C = A.\B
     * 
     * @param B
     *            another matrix
     * @return A.\B
     */
    public Matrix arrayLeftDivide(Matrix B)
    {
        checkMatrixDimensions(B);
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = B.A[i][j] / A[i][j];
            }
        }
        return X;
    }

    /**
     * Element-by-element left division, C = A.\B
     * 
     * @param B
     *            double number
     * @return A.\B
     */
    public Matrix arrayLeftDivide(double B)
    {
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = B / A[i][j];
            }
        }
        return X;
    }

    /**
     * Element-by-element left division in place, A = A.\B
     * 
     * @param B
     *            another matrix
     * @return A.\B
     */
    public Matrix arrayLeftDivideEquals(Matrix B)
    {
        checkMatrixDimensions(B);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = B.A[i][j] / A[i][j];
            }
        }
        return this;
    }

    /**
     * Multiply a matrix by a scalar in place, A = s*A
     * 
     * @param s
     *            scalar
     * @return replace A by s*A
     */
    public Matrix timesEquals(double s)
    {
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = s * A[i][j];
            }
        }
        return this;
    }

    /**
     * Linear algebraic matrix multiplication, A * B
     * 
     * @param B
     *            another matrix
     * @return Matrix product, A * B
     * @exception IllegalArgumentException
     *                Matrix inner dimensions must agree.
     */
    public Matrix times(Matrix B)
    {
        if (this.length() == 1)
        {
            // System.out.println("times : this.length()==1");
            return B.arrayTimes(A[0][0]);
        }
        else if (B.length() == 1)
        {
            // System.out.println("times : B.length()==1");
            return this.arrayTimes(B.get(0, 0));
        }

        if (B.m != n)
        {
            throw new IllegalArgumentException("Matrix inner dimensions must agree.");
        }
        Matrix X = new Matrix(m, B.n);
        double[][] C = X.getArray();
        double[] Bcolj = new double[n];
        for (int j = 0; j < B.n; j++)
        {
            for (int k = 0; k < n; k++)
            {
                Bcolj[k] = B.A[k][j];
            }
            for (int i = 0; i < m; i++)
            {
                double[] Arowi = A[i];
                double s = 0;
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
     * @param B
     * @return
     */
    public Matrix times(Indices B)
    {
        if (B.getRowDimension() != n)
        {
            throw new IllegalArgumentException("'Matrix' & 'Indices' inner dimensions must agree.");
        }
        Matrix X = new Matrix(m, B.getColumnDimension());
        double[][] C = X.getArray();
        double[] Bcolj = new double[n];
        for (int j = 0; j < B.getColumnDimension(); j++)
        {
            for (int k = 0; k < n; k++)
            {
                Bcolj[k] = (double) B.get(k, j);
            }
            for (int i = 0; i < m; i++)
            {
                double[] Arowi = A[i];
                double s = 0;
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
     * Multiply a matrix by a scalar, C = s*A
     * 
     * @param s
     *            scalar
     * @return s*A
     */
    public Matrix times(double s)
    {
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = s * A[i][j];
            }
        }

        return X;
    }

    /**
     * Check if size(A) == size(B) *
     */
    private void checkMatrixDimensions(Matrix B)
    {
        if (B.m != m || B.n != n)
        {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
    }

    private void checkMatrixDimensions(Indices B)
    {
        if (B.getRowDimension() != m || B.getColumnDimension() != n)
        {
            throw new IllegalArgumentException("'Matrix' & 'Indices' dimensions must agree.");
        }
    }

    /**
     * Make a deep copy of a matrix
     * 
     * @return
     */
    public Matrix copy()
    {

        if (A == null)
        {
            return new Matrix(null, m, n);
        }

        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
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
     * Clone the Matrix object.
     */
    @Override
    public Object clone()
    {
        return this.copy();
    }

    /**
     * Matrix determinant
     * 
     * @return determinant
     */
    public double det()
    {
        return new LUDecomposition(this).det();
    }

    /**
     * Matrix rank
     * 
     * @return effective numerical rank, obtained from SVD.
     */
    public int rank()
    {
        return new SingularValueDecomposition(this).rank();
    }

    /**
     * Matrix condition (2 norm)
     * 
     * @return ratio of largest to smallest singular value.
     */
    public double cond_original()
    {
        return new SingularValueDecomposition(this).cond();
    }

    public double rcond()
    {
        return cond(1);
    }

    public double cond()
    {
        return cond(null);// new SingularValueDecomposition(this).norm();
    }

    /**
     * Matrix condition (2 norm)
     * 
     * @param p
     * @return ratio of largest to smallest singular value.
     */
    public double cond(Object p)
    {
        // return new SingularValueDecomposition(this).norm();

        if (p != null)
        {
            if (!(p instanceof Integer) && !(p instanceof String))
            {
                throw new ConditionalException(
                        "cond : Object parameter \"p\" must an instanceof \"Integer\" or \"String\".");
            }
            if (p instanceof Integer)
            {
                int val = ((Integer) p).intValue();
                if (val != 1 && val != 2)
                {
                    throw new ConditionalException("cond : Value for Integer parameter \"p\" must either be 1 or 2 ;");
                }
            }
            else
            {
                String val = ((String) p).toLowerCase();
                if ("".equals(val))
                {
                    throw new ConditionalException("cond : Valid strings for \"p\" must either be \"inf\" or \"fro\".");
                }
                boolean tf = !"inf".equals(val) && !"fro".equals(val);
                if (tf)
                {
                    throw new ConditionalException("cond : Valid strings for \"p\" must either be \"inf\" or \"fro\".");
                }
            }
        }

        double cond = 0.5;

        if (p == null)
        {
            p = new Integer(2);
        }
        // /[m, n] = size(A);
        if (m != n && (p instanceof Integer) && (((Integer) p).intValue() != 2))
        {
            // error('MATLAB:norm:normMismatchSizeA', 'A is rectangular. Use the
            // 2 norm.')
            System.out.println("cond : This matrix 'A' is rectangular, therefore use the 2 norm.");
            return cond(new Integer(2));
        }// end

        if ((p instanceof Integer) && (((Integer) p).intValue() == 2))
        {// p == 2
            Matrix s = this.svd().getS();// svd(A);
            if (s.EQ(0.0).anyBoolean())
            {// any(s == 0) // Handle singular matrix
                cond = Double.POSITIVE_INFINITY;// Inf(class(A));
            }
            else
            {
                cond = JDatafun.max(s).start() / JDatafun.min(s).start();
            }// end
        }
        else
        {
            // We'll let NORM pick up any invalid p argument.
            // c = norm(A, p) * norm(inv(A), p);
            if (p instanceof Integer)
            {
                int pInt = ((Integer) p).intValue();
                if (pInt == 1)
                {
                    cond = this.norm1() * this.inverse().norm1();
                }
                else
                {
                    cond = this.norm2() * this.inverse().norm2();
                }
            }
            else
            {
                String pStr = (String) p;
                if ("inf".equals(pStr))
                {
                    cond = this.normInf() * this.inverse().normInf();
                }
                else
                {
                    cond = this.normF() * this.inverse().normF();
                }
            }
        }// end

        return cond;
    }

    /**
     * Matrix trace.
     * 
     * @return sum of the diagonal elements.
     */
    public double trace()
    {
        double t = 0;
        for (int i = 0; i < Math.min(m, n); i++)
        {
            t += A[i][i];
        }
        return t;
    }

    /**
     * 
     * @param lower
     * @param upper
     * @return
     */
    public static int[] intLinspace(int lower, int upper)
    {
        if (lower > upper)
        {
            throw new IllegalArgumentException("intLinspace : 'lower' must be less or equal to 'upper'.");
        }
        else if (lower == upper)
        {
            return new int[]
            {
                lower
            };
        }
        int len = upper - lower + 1;
        int[] nums = new int[len];
        for (int i = 0; i < len; i++)
        {
            if (i == 0)
            {
                nums[i] = lower;
            }
            else
            {
                nums[i] = nums[i - 1] + 1;
            }
        }
        return nums;
    }

    /**
     * linspace - generates a i vector of "nPoints" linearly equally spaced
     * points between x1 AND x2.
     * 
     * @param leftBound
     *            left end point
     * @param rightBound
     *            right end point
     * @param nPoints
     *            number of points between the boundary limits , [leftBound,
     *            rightBound]
     * @return JElmat i-vector
     */
    public static Matrix linspace(double leftBound, double rightBound, int nPoints)
    {
        double startX = 0.0, endX = 0.0;
        double[][] linVector;
        boolean flip = false;

        if (nPoints < 1)
        {
            throw new IllegalArgumentException("linspace : Number of points should be at least 1 .");
        }

        if (nPoints == 1)
        {
            linVector = new double[][]
            {
                {
                    rightBound
                }
            };
            return new Matrix(linVector);
        }
        else if (leftBound == rightBound)
        {
            linVector = new double[1][nPoints];
            for (int i = 0; i < nPoints; i++)
            {
                linVector[0][i] = leftBound;
            }
            return new Matrix(linVector);
        }

        if (rightBound < leftBound)
        {
            startX = rightBound;
            endX = leftBound;
            flip = true;
        }
        else
        {
            startX = leftBound;
            endX = rightBound;
        }

        double[] temp = new double[nPoints];
        double[] result = new double[nPoints];

        for (int p = 0; p < nPoints; p++)
        {
            if (p == 0)
            {
                temp[p] = 0.0;
                result[p] = startX;
            }
            else if (p == (nPoints - 1))
            {
                result[p] = endX;
            }
            else
            {
                temp[p] = temp[p - 1] + 1.0;
                result[p] = startX + temp[p] * (endX - startX) / ((double) (nPoints - 1));
            }
        }// end for

        if (flip)
        {
            result = MathUtil.flip(result);
        }

        double[][] finalResult = new double[1][];
        finalResult[0] = result;

        return new Matrix(finalResult);
    }

    /**
     * Tile this matrix into an m-rows by lenC-columns pattern
     * 
     * @param r
     *            number of i patterns
     * @param c
     *            number of column patterns. return Matrix from tiling A[][]
     *            into r-rows AND c-columns tile pattern.
     * @return
     */
    public Matrix repmat(int r, int c)
    {
        if (r == 1 && c == 1)
        {
            return copy();
        }
        double[][] X = getArrayCopy();
        int countRow = 0, countColumn = 0;
        // double[] tempHolder;
        if (r < 1 || c < 1)
        {
            throw new ArrayIndexOutOfBoundsException("repmat : Array index is out-of-bound.");
        }
        int newRowDim = m * r;
        int newColDim = n * c;
        double[][] result = new double[newRowDim][];
        double[] tempHolder = new double[newColDim];

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
            // reset the i-index to zero to avoid reference to out-of-bound
            // index in a[][]
            if (countRow == m)
            {
                countRow = 0;
            }

            result[i] = tempHolder;
            // reassign the tempHolder to a new array
            tempHolder = new double[newColDim];
        }// end for

        return new Matrix(result);
    }// end method

    public Matrix reshape(int[] newRowCol)
    {
        if (newRowCol == null)
        {
            throw new ConditionalException("reshape : Parameter \"mn\" must be non-null.");
        }
        if (newRowCol.length != 2)
        {
            throw new ConditionalException("reshape : Length of integer array parameter \"newRowCol\" must be 2;");
        }
        Indices ind = new Indices(newRowCol);
        if (ind.LTEQ(0).anyBoolean())
        {
            throw new ConditionalException(
                    "reshape : Elements of integer array parameter \"newRowCol\" must be positive.");
        }
        return reshape(newRowCol[0], newRowCol[1]);
    }

    public Matrix3D reshape(int newrow, int newcol, int newpage)
    {
        // if (true) {
        // throw new
        // ConditionalException("reshape : Method is to be implemented.");
        // }
        if (this.isNull())
        {
            throw new ConditionalException("reshape : Can't reshape this matrix since it is null.");
        }
        int[] arr =
        {
                newrow, newcol, newpage
        };
        Indices ind = new Indices(arr);
        ind = ind.LTEQ(0);
        // FindInd find = ind.findIJ();
        if (ind.anyBoolean())
        {
            throw new ConditionalException("reshape : All integer parameters \"newrow\" (= " + newrow
                    + "), \"newcol\" (= " + newcol + ") and \"newpage\" (= " + newpage + ") must be positive.");
        }

        int total = newrow * newcol * newpage;
        int matTotal = newrow * newcol;
        if (this.numel() != total)
        {
            throw new ConditionalException("reshape : The new dimension must equal the total elements of this matrix.");
        }

        Matrix3D mat3 = new Matrix3D(newrow, newcol, newpage);
        Matrix colMat = this.toColVector();
        int start = 0;
        int end = 0;

        for (int k = 0; k < newpage; k++)
        {
            // System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ k = " +
            // k + " $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            if (k == 0)
            {
                start = k;
                end = matTotal - 1;
            }
            else
            {
                start = end + 1;
                end = start + matTotal - 1;
            }

            // System.out.println("");
            Matrix colPage = colMat.getMatrix(start, end, 0, 0);
            double[] darray = colPage.getColumnPackedCopy();

            Matrix page = new Matrix(darray, newrow);// = colMat.get;//new
                                                     // Matrix(newrow, newcol);
            mat3.setPageAt(k, page);
        }

        return mat3;
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
    public Matrix reshape(int newrow, int newcol)
    {
        int count = 0;
        double[] columnVector = getColumnPackedCopy();// JElmat.toColumnVector(matrix);

        if ((m * n != newrow * newcol) || newrow < 1 || newcol < 1)
        {
            throw new ArrayIndexOutOfBoundsException("reshape : Array index is out-of-bound.");
        }
        Matrix result = new Matrix(newrow, newcol);
        double[][] C = result.getArray();

        /*
         * ======= Old loop ============ for(int i=0 ; i<newrow ; i++){ for(int
         * j=0 ; j<newcol ; j++){ C[i][j] = columnVector[count++]; } }
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

    public Matrix3D permute(Dimension dim1, Dimension dim2, Dimension dim3)
    {
        Matrix3D perm = new Matrix3D(m, n, 1);
        Matrix copy = this.copy();
        perm.setPageAt(0, copy);
        perm = perm.permute(dim1, dim2, dim3);
        return perm;
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
     * @param indicesSorted
     * @return
     */
    public Matrix getMatrix(Indices indicesSorted)
    {
        // TO BE TESTED
        int row = indicesSorted.getRowDimension();
        int col = indicesSorted.getColumnDimension();

        int I = 0;
        int J = 0;
        double[][] M = null;
        double val = 0.0;
        Matrix R = null;

        if (Math.max(row, col) == row)
        {
            R = new Matrix(row, 1);
            M = R.getArray();
            for (int i = 0; i < row; i++)
            {
                I = indicesSorted.get(i, 0);
                J = indicesSorted.get(i, 1);
                val = A[I][J];
                M[i][0] = val;
            }
        }
        else
        {
            R = new Matrix(1, col);
            M = R.getArray();
            for (int i = 0; i < row; i++)
            {
                I = indicesSorted.get(i, 0);
                J = indicesSorted.get(i, 1);
                val = A[I][J];
                M[0][i] = val;
            }
        }
        return R;
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
     * 
     * @return
     */
    public Matrix toRowVector()
    {
        double[] val = getRowPackedCopy();
        double[][] R = new double[1][];
        R[0] = val;
        Matrix temp = new Matrix(R);
        if (this.isVector() && this.sorted == true)
        {
            temp.setSorted(true);
        }
        return temp;
    }

    public Matrix3D toMatrix3D()
    {
        Matrix copy = this.copy();
        Matrix3D mat3 = new Matrix3D(m, n, 1);
        mat3.setPageAt(0, copy);
        return mat3;
    }

    /**
     * 
     * @return
     */
    public Matrix toColVector()
    {
        double[] val = getColumnPackedCopy();
        double[][] R = new double[val.length][1];
        for (int j = 0; j < val.length; j++)
        {
            R[j][0] = val[j];
        }
        Matrix temp = new Matrix(R);
        if (this.isVector() && this.sorted == true)
        {
            temp.setSorted(true);
        }
        return temp;
    }

    /**
     * Make a one-dimensional column packed copy of the internal array.
     * 
     * @return Matrix elements packed in a one-dimensional array by columns.
     */
    public double[] getColumnPackedCopy()
    {
        double[] vals = new double[m * n];
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
     * Make a one-dimensional i packed copy of the internal array.
     * 
     * @return Matrix elements packed in a one-dimensional array by rows.
     */
    public double[] getRowPackedCopy()
    {
        double[] vals = new double[m * n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                vals[i * n + j] = A[i][j];
            }
        }
        return vals;
    }

    public Matrix horzcat(Object... B)
    {
        if (B == (Object[]) null || B.length == 0)
        {
            return copy();
        }
        int lenB = B.length;
        int numR = 0;

        ArrayList<Object> objList = new ArrayList<Object>();
        // if (!this.isNull()) {
        // objList.add(this.copy());
        // }

        for (int i = 0; i < lenB; i++)
        {
            if (B[i] != null)
            {
                if (!(B[i] instanceof Matrix) && !(B[i] instanceof Indices))
                {
                    throw new IllegalArgumentException(" horzcat : Input argument " + (i + 1)
                            + " must be either a \"Matrix\" or \"Indices\".");
                }
                boolean cond = false;
                if ((B[i] instanceof Matrix))
                {
                    cond = ((Matrix) B[i]).isNull();
                    // numR += ((Matrix) B[i]).getRowDimension();
                    // numC += ((Matrix) B[i]).getColumnDimension();
                }
                else
                {
                    cond = ((Indices) B[i]).isNull();
                    // numR += ((Indices) B[i]).getRowDimension();
                    // numC += ((Indices) B[i]).getColumnDimension();
                }
                if (!cond)
                {
                    objList.add(B[i]);
                }
            }
        }

        if (objList.isEmpty())
        {
            return this.copy();// new Matrix();
        }
        else if (objList.size() == 1)
        {
            Object OB = objList.get(0);
            if (OB instanceof Matrix)
            {
                return this.mergeHoriz((Matrix) OB);
            }
            else
            {
                return this.mergeHoriz(Matrix.indicesToMatrix((Indices) OB));
            }
        }

        // check that all rows are the same.
        for (int i = 0; i < objList.size(); i++)
        {
            Object OJT = objList.get(i);
            int curRow = 0;
            if (i == 0)
            {
                if (OJT instanceof Matrix)
                {
                    numR = ((Matrix) OJT).getRowDimension();
                }
                else
                {
                    numR = ((Indices) OJT).getRowDimension();
                }
            }
            else
            {
                if (OJT instanceof Matrix)
                {
                    curRow = ((Matrix) OJT).getRowDimension();
                }
                else
                {
                    curRow = ((Indices) OJT).getRowDimension();
                }
                if (curRow != numR)
                {
                    throw new ConditionalRuleException("horzcat", "Number of rows must be the same for all.");
                }
            }
        }

        Object[] list = new Object[objList.size()];
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
     *         Merge this matrix AND Object array 'B horizontally where each
     *         type are either 'Matrix' or 'Indices' to form a larger matrix.
     *         Row size must agree.
     *         </p>
     */
    public Matrix mergeHoriz(Object... B)
    {
        if (B == (Object[]) null || B.length == 0)
        {
            return copy();
        }

        int lenB = B.length;
        ArrayList<Object> objList = new ArrayList<Object>();
        if (!this.isNull())
        {
            objList.add(this.copy());
        }

        for (int i = 0; i < lenB; i++)
        {
            if (B[i] != null)
            {
                if (!(B[i] instanceof Matrix) && !(B[i] instanceof Indices))
                {
                    throw new IllegalArgumentException(" mergeHoriz : Input argument " + (i + 1)
                            + " must be either a \"Matrix\" or \"Indices\".");
                }
                objList.add(B[i]);
            }
        }

        // if all input arguments is null, then just return a copy of this
        // matrix.
        if (objList.size() == 1)
        {
            return copy();
        }

        for (int i = 0; i < objList.size(); i++)
        {
            Object matIndObj = objList.get(i);
            if (matIndObj instanceof Matrix)
            {
                if (m != ((Matrix) matIndObj).getRowDimension())
                {
                    throw new IllegalArgumentException(" mergeHoriz : Matrix row dimensions must agree.");
                }
            }
            else
            {
                if (m != ((Indices) matIndObj).getRowDimension())
                {
                    throw new IllegalArgumentException(" mergeHoriz : Indices row dimensions must agree.");
                }
            }
        }

        int newCol = 0;
        for (int i = 0; i < objList.size(); i++)
        {
            Object matIndObj = objList.get(i);
            if (matIndObj instanceof Matrix)
            {
                newCol += ((Matrix) matIndObj).getColumnDimension();
            }
            else
            {
                newCol += ((Indices) matIndObj).getColumnDimension();
            }
        }

        Matrix R = new Matrix(m, newCol);

        int begInd = 0;
        int endInd = 0;
        int numCol = 0;

        for (int i = 0; i < objList.size(); i++)
        {
            Object matIndObj = objList.get(i);
            if (i == 0)
            {// first object is guranteed to be a matrix, because 'this' matrix
             // is the first one to be added.
                numCol = ((Matrix) matIndObj).getColumnDimension();
                endInd = numCol - 1;
                // System.out.println("BLOCK #1 : [begin,end] = [" + begInd +
                // "," + endInd + "]");
                R.setMatrix(0, m - 1, begInd, endInd, (Matrix) matIndObj);
            }
            else
            {
                if (matIndObj instanceof Matrix)
                {
                    begInd += numCol;
                    numCol = ((Matrix) matIndObj).getColumnDimension();
                    endInd = begInd + numCol - 1;
                    // System.out.println("BLOCK #2 : [begin,end] = [" + begInd
                    // + "," + endInd + "]");
                    R.setMatrix(0, m - 1, begInd, endInd, (Matrix) matIndObj);
                }
                else
                {
                    begInd += numCol;
                    numCol = ((Indices) matIndObj).getColumnDimension();
                    endInd = begInd + numCol - 1;
                    // System.out.println("BLOCK #3 : [begin,end] = [" + begInd
                    // + "," + endInd + "]");
                    R.setMatrix(0, m - 1, begInd, endInd, (Indices) matIndObj);
                }
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
    public Matrix mergeH(Matrix B)
    {
        return mergeHoriz(B);
        /*
         * if (m != B.m) { throw new IllegalArgumentException(" mergeH : Matrix
         * i dimensions must agree."); }
         * 
         * int row = n + B.n; Matrix R = new Matrix(m, row); R.setMatrix(0, m -
         * 1, 0, n - 1, this); R.setMatrix(0, m - 1, n, n + B.n - 1, B); return
         * R;
         */
    }

    public Matrix mergeH(Indices B)
    {
        return mergeHoriz(B);
        /*
         * if (m != B.getRowDimension()) { throw new IllegalArgumentException("
         * mergeH : Matrix i dimensions must agree."); }
         * 
         * int row = n + B.getColumnDimension(); Matrix R = new Matrix(m, row);
         * R.setMatrix(0, m - 1, 0, n - 1, this); R.setMatrix(0, m - 1, n, n +
         * B.getColumnDimension() - 1, Matrix.indicesToMatrix(B)); return R;
         */
    }

    public static Matrix mergeH(Matrix A, Matrix B)
    {
        if (A == null && B == null)
        {
            return null;
        }
        else if (A != null && B == null)
        {
            return A.copy();
        }
        else if (A == null && B != null)
        {
            return B.copy();
        }
        return A.mergeH(B);
    }

    public Matrix vertcat(Object... B)
    {
        if (B == (Object[]) null || B.length == 0)
        {
            return copy();
        }
        int lenB = B.length;
        int numR = 0;

        ArrayList<Object> objList = new ArrayList<Object>();
        // if (!this.isNull()) {
        // objList.add(this.copy());
        // }

        for (int i = 0; i < lenB; i++)
        {
            if (B[i] != null)
            {
                if (!(B[i] instanceof Matrix) && !(B[i] instanceof Indices))
                {
                    throw new IllegalArgumentException(" vertcat : Input argument " + (i + 1)
                            + " must be either a \"Matrix\" or \"Indices\".");
                }
                boolean cond = false;
                if ((B[i] instanceof Matrix))
                {
                    cond = ((Matrix) B[i]).isNull();
                    // numR += ((Matrix) B[i]).getRowDimension();
                    // numC += ((Matrix) B[i]).getColumnDimension();
                }
                else
                {
                    cond = ((Indices) B[i]).isNull();
                    // numR += ((Indices) B[i]).getRowDimension();
                    // numC += ((Indices) B[i]).getColumnDimension();
                }
                if (!cond)
                {
                    objList.add(B[i]);
                }
            }
        }

        if (objList.isEmpty())
        {
            return this.copy();
        }
        else if (objList.size() == 1)
        {
            Object OB = objList.get(0);
            if (OB instanceof Matrix)
            {
                return this.mergeVerti((Matrix) OB);
            }
            else
            {
                return this.mergeVerti(Matrix.indicesToMatrix((Indices) OB));
            }
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

        Object[] list = new Object[objList.size()];
        for (int i = 0; i < objList.size(); i++)
        {
            list[i] = objList.get(i);
        }

        return mergeVerti(list);
    }

    public Matrix mergeVerti(Object... B)
    {
        if (B == (Object[]) null || B.length == 0)
        {
            return copy();
        }

        int lenB = B.length;
        ArrayList<Object> objList = new ArrayList<Object>();
        objList.add(this);

        for (int i = 0; i < lenB; i++)
        {
            if (B[i] != null)
            {
                if (!(B[i] instanceof Matrix) && !(B[i] instanceof Indices))
                {
                    throw new IllegalArgumentException(" mergeVerti : Input argument " + (i + 1)
                            + " must either be a \"Matrix\" or \"Indices\".");
                }
                objList.add(B[i]);
            }
        }

        // if all input arguments is null, then just return a copy of this
        // matrix.
        if (objList.size() == 1)
        {
            return copy();
        }

        for (int i = 0; i < objList.size(); i++)
        {
            Object matIndObj = objList.get(i);
            if (matIndObj instanceof Matrix)
            {
                if (n != ((Matrix) matIndObj).getColumnDimension())
                {
                    throw new IllegalArgumentException(" mergeVerti : Matrix column dimensions must agree.");
                }
            }
            else
            {
                if (n != ((Indices) matIndObj).getColumnDimension())
                {
                    throw new IllegalArgumentException(" mergeVerti : Indices column dimensions must agree.");
                }
            }
        }

        int newRow = 0;
        for (int i = 0; i < objList.size(); i++)
        {
            Object matIndObj = objList.get(i);
            if (matIndObj instanceof Matrix)
            {
                newRow += ((Matrix) matIndObj).getRowDimension();
            }
            else
            {
                newRow += ((Indices) matIndObj).getRowDimension();
            }
        }

        Matrix R = new Matrix(newRow, n);

        int begInd = 0;
        int endInd = 0;
        int numRow = 0;

        for (int i = 0; i < objList.size(); i++)
        {
            Object matIndObj = objList.get(i);
            if (i == 0)
            {// first object is guranteed to be a matrix, because 'this' matrix
             // is the first one to be added.
                numRow = ((Matrix) matIndObj).getRowDimension();
                endInd = numRow - 1;
                // System.out.println("vert BLOCK #1 : [begin,end] = [" + begInd
                // + "," + endInd + "]");
                R.setMatrix(begInd, endInd, 0, n - 1, (Matrix) matIndObj);
            }
            else
            {
                if (matIndObj instanceof Matrix)
                {
                    begInd += numRow;
                    numRow = ((Matrix) matIndObj).getRowDimension();
                    endInd = begInd + numRow - 1;
                    // System.out.println("vert BLOCK #2 : [begin,end] = [" +
                    // begInd + "," + endInd + "]");
                    R.setMatrix(begInd, endInd, 0, n - 1, (Matrix) matIndObj);
                }
                else
                {
                    begInd += numRow;
                    numRow = ((Indices) matIndObj).getRowDimension();
                    endInd = begInd + numRow - 1;
                    // System.out.println("vert BLOCK #3 : [begin,end] = [" +
                    // begInd + "," + endInd + "]");
                    R.setMatrix(begInd, endInd, 0, n - 1, (Indices) matIndObj);
                }
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
    public Matrix mergeV(Matrix B)
    {
        return mergeVerti(B);
        /*
         * if (n != B.n) { throw new IllegalArgumentException(" mergeV : Matrix
         * column dimensions must agree."); } int row = m + B.m; Matrix R = new
         * Matrix(row, n); R.setMatrix(0, m - 1, 0, n - 1, this); R.setMatrix(m,
         * m + B.m - 1, 0, n - 1, B); return R;
         */
    }

    public Matrix mergeV(Indices B)
    {
        return mergeVerti(B);
        /*
         * if (n != B.getColumnDimension()) { throw new
         * IllegalArgumentException(" mergeV : Matrix column dimensions must
         * agree."); } int row = m + B.getRowDimension(); Matrix R = new
         * Matrix(row, n); R.setMatrix(0, m - 1, 0, n - 1, this); R.setMatrix(m,
         * m + B.getRowDimension() - 1, 0, n - 1, Matrix.indicesToMatrix(B));
         * return R;
         */
    }

    public static Matrix mergeV(Matrix A, Matrix B)
    {
        if (A == null && B == null)
        {
            return null;
        }
        else if (A != null && B == null)
        {
            return A.copy();
        }
        else if (A == null && B != null)
        {
            return B.copy();
        }
        return A.mergeV(B);
    }

    public static Matrix zeros(int[] rc)
    {
        if (rc == null)
        {
            throw new ConditionalException("zeros : Parameter \"rc\" must be non-null.");
        }
        if (rc.length != 2)
        {
            throw new ConditionalException("zeros : Length of integer array parameter \"rc\" must be 2;");
        }
        Indices ind = new Indices(rc);
        if (ind.LTEQ(0).anyBoolean())
        {
            throw new ConditionalException("zeros : Elements of integer array parameter \"rc\" must be positive.");
        }
        return zeros(rc[0], rc[1]);
    }

    /**
     * 
     * @param r
     * @param c
     * @return
     */
    public static Matrix zeros(int r, int c)
    {
        return new Matrix(r, c);
    }

    /**
     * 
     * @param n
     * @return
     */
    public static Matrix zeros(int n)
    {
        return zeros(n, n);
    }

    public void setColumns(int from, int to, double valColumns)
    {
        if (from > to)
        {
            throw new IllegalArgumentException(" setColumns: Value for parameter \"from\" (= " + from
                    + ") must be equal to or less than that of \"to\" (= " + to + ").");
        }
        int[] indices = Indices.linspace(from, to).getRowPackedCopy();
        int len = indices.length;
        Matrix matColumns = new Matrix(m, len, valColumns);
        setColumns(indices, matColumns);
    }

    public void setColumns(int from, int to, Matrix matColumns)
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
    public void setColumns(int[] indices, Matrix matColumns)
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
            Matrix temp = matColumns.getColumnAt(i);
            setMatrix(0, m - 1, indices[i], indices[i], temp);
        }
    }

    /**
     * 
     * @param indices
     * @param valColumns
     */
    public void setColumns(int[] indices, double valColumns)
    {
        int len = indices.length;
        Matrix matColumns = new Matrix(m, len, valColumns);
        setColumns(indices, matColumns);
    }

    /**
     * 
     * @param index
     *            int
     * @param matColumns
     *            Matrix
     */
    public void setColumnAt(int index, Matrix matColumns)
    {
        if (matColumns.numel() == 1)
        {
            setColumns(new int[]
            {
                index
            }, matColumns.start());
        }
        else
        {
            setColumns(new int[]
            {
                index
            }, matColumns);
        }
    }

    public void setAllTo(double val)
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
     * @param valColumns
     */
    public void setColumnAt(int index, double valColumns)
    {
        setColumns(new int[]
        {
            index
        }, valColumns);
    }

    public Matrix setXtendedColAt(int index, Matrix matCols)
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
        Matrix mat = null;
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

        Matrix X = null;
        if ((index + 1) <= this.n)
        {
            X = this.copy();
        }
        else
        {
            int ind = (index + 1) - n;

            X = this.mergeH(Matrix.zeros(m, ind));
        }// new Matrix(index+1,n);

        X.setColumnAt(index, mat);

        return X;
    }

    /**
     * 
     * @param indices
     *            int[]
     * @param matCols
     *            Matrix
     */
    public void setRows(int[] indices, Matrix matRows)
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
            Matrix temp = matRows.getRowAt(i);
            setMatrix(indices[i], indices[i], 0, n - 1, temp);
        }
    }

    /**
     * 
     * @param indices
     * @param valRows
     */
    public void setRows(int[] indices, double valRows)
    {
        int len = indices.length;
        Matrix matRows = new Matrix(len, n, valRows);
        setRows(indices, matRows);
    }

    /**
     * 
     * @param index
     *            int
     * @param matRows
     */
    public void setRowAt(int index, Matrix matRows)
    {
        if (matRows.numel() == 1)
        {
            setRows(new int[]
            {
                index
            }, matRows.start());
        }
        else
        {
            setRows(new int[]
            {
                index
            }, matRows);
        }
    }

    /**
     * 
     * @param index
     * @param valRows
     */
    public void setRowAt(int index, double valRows)
    {
        setRows(new int[]
        {
            index
        }, valRows);
    }

    public Matrix setXtendedRowAt(int index, Matrix matRows)
    {
        if (matRows == null)
        {
            throw new IllegalArgumentException(" setXtendedRowAt: Parameter \"matRows\" must be non-null.");
        }
        if (!matRows.isVector())
        {
            throw new IllegalArgumentException(
                    " setXtendedRowAt: Parameter \"matRows\" must be a vector and not a matrix.");
        }
        Matrix mat = null;
        if (matRows.isColVector())
        {
            mat = matRows.toRowVector();
        }
        else
        {
            mat = matRows;
        }
        int n2 = matRows.length();
        if (this.n != n2)
        {
            throw new IllegalArgumentException(" setXtendedRowAt: Inconsistent column dimensions.");
        }

        Matrix X = null;
        if ((index + 1) <= this.m)
        {
            X = this.copy();
        }
        else
        {
            int ind = (index + 1) - m;

            X = this.mergeV(Matrix.zeros(ind, n));
        }// new Matrix(index+1,n);

        X.setRowAt(index, mat);

        return X;
    }

    /**
     * 
     * @param indices
     *            int[]
     * @return Matrix
     */
    public Matrix getRows(int[] indices)
    {
        int len = indices.length;
        Matrix val = new Matrix(len, n);
        for (int i = 0; i < len; i++)
        {
            if (indices[i] > (m - 1) || indices[i] < 0)
            {
                throw new IllegalArgumentException(" getRows: Array out-of-bounds.");
            }
            Matrix temp = this.getMatrix(indices[i], indices[i], 0, n - 1);
            val.setMatrix(i, i, 0, n - 1, temp);
        }
        return val;
    }// end method

    /**
     * 
     * @param indices
     *            int[]
     * @return Matrix
     */
    public Matrix getRows(int from, int to)
    {
        if (to < from)
        {
            throw new IllegalArgumentException(
                    " getRows: Second input integer argument must equal or greater than the second one.");
        }
        int[] indices = Indices.linspace(from, to).getRowPackedCopy();
        int len = indices.length;
        Matrix val = new Matrix(len, n);
        for (int i = 0; i < len; i++)
        {
            if (indices[i] > (m - 1) || indices[i] < 0)
            {
                throw new IllegalArgumentException(" getRows: Array out-of-bounds.");
            }
            Matrix temp = this.getMatrix(indices[i], indices[i], 0, n - 1);
            val.setMatrix(i, i, 0, n - 1, temp);
        }
        return val;
    }// end method

    /**
     * 
     * @param rowIndex
     *            int
     * @return Matrix
     */
    public Matrix getRowAt(int rowIndex)
    {
        return getRows(new int[]
        {
            rowIndex
        });
    }

    /**
     * 
     * @param indices
     * @return
     */
    public static Matrix intArrayToMatrix(int[] indices)
    {
        int len = indices.length;
        Matrix mat = new Matrix(1, len);
        double[][] C = mat.getArray();
        for (int i = 0; i < len; i++)
        {
            C[0][i] = indices[i];
        }
        return mat;
    }

    // ////////////////////////////////////////////////////////////////////////////
    /**
     * 
     * @param B
     * @return
     */
    public Indices LT(Matrix B)
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

        checkMatrixDimensions(B);
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        double[][] M = B.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!MathUtil.nan(A[i][j], M[i][j]))
                {
                    if (A[i][j] < M[i][j])
                    {
                        C[i][j] = 1;
                    }
                    // else {C[i][j] = 0.0d;}
                }
            }
        }
        return X;
    }

    /**
     * 
     * @return
     */
    public Indices logical()
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!Double.isNaN(A[i][j]))
                {
                    if (A[i][j] != 0.0)
                    {
                        C[i][j] = 1;
                    }
                    // else {C[i][j] = 0.0d;}
                }
            }
        }
        return X;
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices LT(double b)
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!MathUtil.nan(A[i][j], b))
                {
                    if (A[i][j] < b)
                    {
                        C[i][j] = 1;
                    }
                    // else {C[i][j] = 0.0d;}
                }
            }
        }
        return X;
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices LT(Double b)
    {
        if (b == null)
        {
            throw new IllegalArgumentException(" lt: Parameter \"b\" must be non-null.");
        }
        double val = b.doubleValue();
        return LT(val);
    }

    /**
     * 
     * @param B
     * @return
     */
    public Indices LTEQ(Matrix B)
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

        checkMatrixDimensions(B);
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        double[][] M = B.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!MathUtil.nan(A[i][j], M[i][j]))
                {
                    if (A[i][j] <= M[i][j])
                    {
                        C[i][j] = 1;
                    }
                    // else {C[i][j] = 0.0d;}
                }
            }
        }
        return X;
    }

    /**
     * 
     * @param d
     * @return
     */
    public Indices LTEQ(double d)
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!MathUtil.nan(A[i][j], d))
                {
                    if (A[i][j] <= d)
                    {
                        C[i][j] = 1;
                    }
                    // else {C[i][j] = 0.0d;}
                }
            }
        }
        return X;
    }

    /**
     * 
     * @param d
     * @return
     */
    public Indices LTEQ(Double d)
    {
        if (d == null)
        {
            throw new IllegalArgumentException(" lteq: Parameter \"b\" must be non-null.");
        }
        double val = d.doubleValue();
        return LTEQ(val);
    }

    /**
     * 
     * @param B
     * @return
     */
    public Indices GT(Matrix B)
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

        checkMatrixDimensions(B);
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        double[][] M = B.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!MathUtil.nan(A[i][j], M[i][j]))
                {
                    if (A[i][j] > M[i][j])
                    {
                        C[i][j] = 1;
                    }
                    // else {C[i][j] = 0.0d;}
                }
            }
        }
        return X;
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices GT(double b)
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!MathUtil.nan(A[i][j], b))
                {
                    if (A[i][j] > b)
                    {
                        C[i][j] = 1;
                    }
                    // else {C[i][j] = 0.0d;}
                }
            }
        }
        return X;
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices GT(Double b)
    {
        if (b == null)
        {
            throw new IllegalArgumentException(" gt: Parameter \"b\" must be non-null.");
        }
        double val = b.doubleValue();

        return GT(val);
    }

    /**
     * 
     * @param B
     * @return
     */
    public Indices GTEQ(Matrix B)
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

        checkMatrixDimensions(B);
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        double[][] M = B.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!MathUtil.nan(A[i][j], M[i][j]))
                {
                    if (A[i][j] >= M[i][j])
                    {
                        C[i][j] = 1;
                    }
                    // else {C[i][j] = 0.0d;}
                }
            }
        }
        return X;
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices GTEQ(double b)
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!MathUtil.nan(A[i][j], b))
                {
                    if (A[i][j] >= b)
                    {
                        C[i][j] = 1;
                    }
                    // else {C[i][j] = 0.0d;}
                }
            }
        }
        return X;
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices GTEQ(Double b)
    {
        if (b == null)
        {
            throw new IllegalArgumentException(" gteq: Parameter \"b\" must be non-null.");
        }
        double val = b.doubleValue();
        return GTEQ(val);
    }

    /**
     * 
     * @param B
     * @param tolerance
     * @return
     */
    public Indices EQ(Matrix B, double tolerance)
    {

        /**
         * ********************* Old Block ****************************
         * checkMatrixDimensions(B); Indices X = new Indices(m, n);
         * X.setLogical(true); int[][] C = X.getArray();
         * 
         * if (tolerance != 0.0d) { //Matrix addTolerance = B.plusCP(tolerance);
         * //double[][] upperT = addTolerance.getArray(); //Matrix subTolerance
         * = B.plusCP(-tolerance); //double[][] lowerT =
         * subTolerance.getArray(); double[][] C_B = B.getArray();
         * 
         * for (int i = 0; i < m; i++) { for (int j = 0; j < n; j++) { if
         * (!MathUtil.nan(A[i][j], C_B[i][j])) { if
         * (MathUtil.equalsWithTol(A[i][j], C_B[i][j], tolerance)) { C[i][j] =
         * 1; } //else {C[i][j] = 0.0d;} } }//end for
         * 
         * }//end for
         * 
         * }//end if else { double[][] M = B.getArray(); for (int i = 0; i < m;
         * i++) { for (int j = 0; j < n; j++) { if (!MathUtil.nan(A[i][j],
         * M[i][j])) { if (A[i][j] == M[i][j]) { C[i][j] = 1; } //else {C[i][j]
         * = 0.0d;} } }//end for
         * 
         * }//end for
         * 
         * }//end else *****************************************************
         */
        // ---------- The following line replaced the old block shown above
        // (7/7/10) ------------
        Indices X = equalAll(B, tolerance);
        // --------------------------------------------------------------------------------------

        return X;
    }// end method

    /**
     * 
     * @param B
     * @return
     */
    public Indices EQ(Matrix B)
    {
        return EQ(B, 0.0d);
    }

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public Indices EQ(double b, double tolerance)
    {
        Matrix X = new Matrix(m, n, b);
        return EQ(X, tolerance);
    }// end method

    /**
     * 
     * @param d
     * @return
     */
    public Indices EQ(double d)
    {
        // Matrix X = new Matrix(m,lenC,d);
        return EQ(d, 0.0d);
    }// end method

    /**
     * 
     * @param d
     * @return
     */
    public Indices EQ(Double d)
    {
        // Matrix X = new Matrix(m,lenC,d);
        if (d == null)
        {
            throw new IllegalArgumentException(" eq: Parameter \"b\" must be non-null.");
        }
        double val = d.doubleValue();
        return EQ(val, 0.0d);
    }// end method

    // ------------------------------------------------------------
    /**
     * 
     * @param B
     * @param tolerance
     * @return
     */
    public Indices equalAll(Matrix B, double tolerance)
    {

        // ---- added 3/3/11 ----
        /*
         * if (this.numel() == 1) { return B.EQ(A[0][0], tolerance); } if
         * (B.numel() == 1) { return this.EQ(B.start(), tolerance); }
         */
        // ----------------------
        checkMatrixDimensions(B);
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        double[][] C_B = B.getArray();
        // System.out.println("=====Execute====");
        if (tolerance != 0.0d)
        {
            // Matrix addTolerance = B.plusCP(tolerance);
            // double[][] upperT = addTolerance.getArray();
            // Matrix subTolerance = B.plusCP(-tolerance);
            // double[][] lowerT = subTolerance.getArray();

            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!MathUtil.nan(A[i][j], C_B[i][j]))
                    {
                        if (A[i][j] == Double.NEGATIVE_INFINITY && C_B[i][j] == Double.NEGATIVE_INFINITY)
                        {
                            C[i][j] = 1;
                        }
                        else if (A[i][j] == Double.NEGATIVE_INFINITY && C_B[i][j] != Double.NEGATIVE_INFINITY)
                        {
                        }
                        else if (A[i][j] != Double.NEGATIVE_INFINITY && C_B[i][j] == Double.NEGATIVE_INFINITY)
                        {
                        }
                        else if (A[i][j] == Double.POSITIVE_INFINITY && C_B[i][j] == Double.POSITIVE_INFINITY)
                        {
                            C[i][j] = 1;
                        }
                        else if (A[i][j] == Double.POSITIVE_INFINITY && C_B[i][j] != Double.POSITIVE_INFINITY)
                        {
                        }
                        else if (A[i][j] != Double.POSITIVE_INFINITY && C_B[i][j] == Double.POSITIVE_INFINITY)
                        {
                        }
                        else if (MathUtil.equalsWithTol(A[i][j], C_B[i][j], tolerance))
                        {
                            C[i][j] = 1;
                        }
                        // else {C[i][j] = 0.0d;}
                    }
                    else if (Double.isNaN(A[i][j]) && (Double.isNaN(C_B[i][j])))
                    {
                        C[i][j] = 1;
                    }
                }// end for

            }// end for

        }// end if
        else
        {
            // double[][] M = B.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!MathUtil.nan(A[i][j], C_B[i][j]))
                    {
                        if (A[i][j] == Double.NEGATIVE_INFINITY && C_B[i][j] == Double.NEGATIVE_INFINITY)
                        {
                            C[i][j] = 1;
                        }
                        else if (A[i][j] == Double.NEGATIVE_INFINITY && C_B[i][j] != Double.NEGATIVE_INFINITY)
                        {
                        }
                        else if (A[i][j] != Double.NEGATIVE_INFINITY && C_B[i][j] == Double.NEGATIVE_INFINITY)
                        {
                        }
                        else if (A[i][j] == Double.POSITIVE_INFINITY && C_B[i][j] == Double.POSITIVE_INFINITY)
                        {
                            C[i][j] = 1;
                        }
                        else if (A[i][j] == Double.POSITIVE_INFINITY && C_B[i][j] != Double.POSITIVE_INFINITY)
                        {
                        }
                        else if (A[i][j] != Double.POSITIVE_INFINITY && C_B[i][j] == Double.POSITIVE_INFINITY)
                        {
                        }
                        else if (A[i][j] == C_B[i][j])
                        {
                            C[i][j] = 1;
                        }
                        // System.out.println("=====Execute 1====");
                    }
                    else if (Double.isNaN(A[i][j]) && Double.isNaN(C_B[i][j]))
                    {
                        C[i][j] = 1;
                        // System.out.println("=====Execute 2====");
                    }
                    // else{ System.out.println("=====Execute 3===="); }
                }// end for

            }// end for

        }// end else

        return X;
    }// end method

    /**
     * 
     * @param B
     * @return
     */
    public Indices equalAll(Matrix B)
    {
        return equalAll(B, 0.0d);
    }

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public Indices equalAll(double b, double tolerance)
    {
        Matrix X = new Matrix(m, n, b);
        return equalAll(X, tolerance);
    }// end method

    /**
     * 
     * @param d
     * @return
     */
    public Indices equalAll(double d)
    {
        // Matrix X = new Matrix(m,lenC,d);
        return equalAll(d, 0.0d);
    }// end method

    /**
     * 
     * @param d
     * @return
     */
    public Indices equalAll(Double d)
    {
        // Matrix X = new Matrix(m,lenC,d);
        if (d == null)
        {
            throw new IllegalArgumentException(" equalAll: Parameter \"b\" must be non-null.");
        }
        double val = d.doubleValue();
        return equalAll(val, 0.0d);
    }// end method

    // ------------------------------------------------------------
    /**
     * 
     * @param B
     * @param tolerance
     * @return
     */
    public Indices NEQ(Matrix B, double tolerance)
    {

        // ---- added 3/3/11 ----
        if (this.numel() == 1)
        {
            return B.NEQ(A[0][0], tolerance);
        }
        if (B.numel() == 1)
        {
            return this.NEQ(B.start(), tolerance);
        }
        // ----------------------

        checkMatrixDimensions(B);
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();

        if (tolerance != 0.0d)
        {
            Matrix addTolerance = B.plus(tolerance);
            double[][] upperT = addTolerance.getArray();
            Matrix subTolerance = B.plus(-tolerance);
            double[][] lowerT = subTolerance.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!MathUtil.nan(new double[]
                    {
                            A[i][j], lowerT[i][j], upperT[i][j]
                    }))
                    {
                        if (!(A[i][j] >= lowerT[i][j] && A[i][j] <= upperT[i][j]))
                        {
                            C[i][j] = 1;
                        }
                        // else {C[i][j] = 1.0d;}
                    }
                }// end for

            }// end for

        }// end if
        else
        {
            double[][] M = B.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!MathUtil.nan(A[i][j], M[i][j]))
                    {
                        if (!(A[i][j] == M[i][j]))
                        {
                            C[i][j] = 1;
                        }
                        // else {C[i][j] = 1.0d;}
                    }
                }// end for

            }// end for

        }// end else

        return X;
    }// end method

    public Matrix reflectOnMainDiag()
    {
        Matrix reflect = this.copy();
        if (m > n)
        {
            for (int j = 0; j < n; j++)
            {
                for (int i = 0; i <= j; i++)
                {
                    double val = A[i][j];
                    reflect.set(i, j, val);
                    reflect.set(j, i, val);
                }
            }
        }
        else if (m == n)
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j <= i; j++)
                {
                    double val = A[i][j];
                    reflect.set(i, j, val);
                    reflect.set(j, i, val);
                }
            }
        }
        else
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j <= i; j++)
                {
                    double val = A[i][j];
                    reflect.set(i, j, val);
                    reflect.set(j, i, val);
                }
            }
        }
        return reflect;
    }

    /**
     * 
     * @param B
     * @return
     */
    public Indices NEQ(Matrix B)
    {
        return NEQ(B, 0.0d);
    }// end method

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public Indices NEQ(double b, double tolerance)
    {
        Matrix X = new Matrix(m, n, b);
        return NEQ(X, tolerance);
    }// end method

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public Indices NEQ(Double b, double tolerance)
    {
        return NEQ(b.doubleValue(), tolerance);
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices NEQ(double b)
    {
        Matrix X = new Matrix(m, n, b);
        return NEQ(X, 0.0d);
    }// end method

    /**
     * 
     * @param B
     * @param tolerance
     * @return
     */
    public Indices AND(Matrix B, double tolerance)
    {

        // ---- added 3/3/11 ----
        if (this.numel() == 1)
        {
            return B.AND(A[0][0], tolerance);
        }
        if (B.numel() == 1)
        {
            return this.AND(B.start(), tolerance);
        }
        // ----------------------

        checkMatrixDimensions(B);

        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        double[][] matB = B.getArray();

        if (tolerance != 0.0d)
        {
            Matrix zeroMat = new Matrix(m, n);
            Matrix addTolerance = zeroMat.plus(tolerance);
            double[][] upperT = addTolerance.getArray();
            Matrix subTolerance = zeroMat.plus(-tolerance);
            double[][] lowerT = subTolerance.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!MathUtil.nan(new double[]
                    {
                            A[i][j], lowerT[i][j], upperT[i][j], matB[i][j]
                    }))
                    {
                        if (!(A[i][j] >= lowerT[i][j] && A[i][j] <= upperT[i][j])
                                && !(matB[i][j] >= lowerT[i][j] && matB[i][j] <= upperT[i][j]))
                        {
                            C[i][j] = 1;
                        }
                        // else {C[i][j] = 0.0d;}
                    }
                }// end for

            }// end for

        }// end if
        else
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!MathUtil.nan(A[i][j], matB[i][j]))
                    {
                        if (A[i][j] != 0.0d && matB[i][j] != 0.0d)
                        {
                            C[i][j] = 1;
                        }
                        // else { C[i][j] = 0.0d; }
                    }
                }
            }// end for

        }// end else

        return X;
    }// end method

    /**
     * 
     * @param B
     * @return
     */
    public Indices AND(Matrix B)
    {
        return AND(B, 0.0d);
    }

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public Indices AND(double b, double tolerance)
    {
        Matrix X = new Matrix(m, n, b);
        return AND(X, tolerance);
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices AND(double b)
    {
        return AND(b, 0.0d);
    }

    /**
     * 
     * @param dim
     * @return
     */
    public Indices ANY(Dimension dim)
    {
        return (new LogicalAnyMat(this, dim)).getIndices();
    }

    /**
     * 
     * @return
     */
    public Indices ANY()
    {
        return (new LogicalAnyMat(this)).getIndices();
    }

    /**
     * 
     * @param B
     * @param tolerance
     * @return
     */
    public Indices OR(Matrix B, double tolerance)
    {
        checkMatrixDimensions(B);

        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        double[][] matB = B.getArray();

        if (tolerance != 0.0d)
        {
            Matrix zeroMat = new Matrix(m, n);
            Matrix addTolerance = zeroMat.plus(tolerance);
            double[][] upperT = addTolerance.getArray();
            Matrix subTolerance = zeroMat.plus(-tolerance);
            double[][] lowerT = subTolerance.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!MathUtil.nan(new double[]
                    {
                            A[i][j], lowerT[i][j], upperT[i][j], matB[i][j]
                    }))
                    {
                        if (!(A[i][j] >= lowerT[i][j] && A[i][j] <= upperT[i][j])
                                || !(matB[i][j] >= lowerT[i][j] && matB[i][j] <= upperT[i][j]))
                        {
                            C[i][j] = 1;
                        }
                        // else {C[i][j] = 0.0d;}
                    }
                }// end for

            }// end for

        }// end if
        else
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!MathUtil.nan(A[i][j], matB[i][j]))
                    {
                        if (A[i][j] != 0.0d || matB[i][j] != 0.0d)
                        {
                            C[i][j] = 1;
                        }
                        // else { C[i][j] = 0.0d; }
                    }
                }
            }// end for

        }// end else

        return X;
    }// end method

    /**
     * 
     * @param B
     * @return
     */
    public Indices OR(Matrix B)
    {
        return OR(B, 0.0d);
    }

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public Indices OR(double b, double tolerance)
    {
        Matrix X = new Matrix(m, n, b);
        return OR(X, tolerance);
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices OR(double b)
    {
        return OR(b, 0.0d);
    }

    /**
     * 
     * @param tolerance
     * @return
     */
    public Indices NOT(double tolerance)
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();

        if (tolerance != 0.0d)
        {
            Matrix addTolerance = plus(tolerance);
            double[][] upperT = addTolerance.getArray();
            Matrix subTolerance = plus(-tolerance);
            double[][] lowerT = subTolerance.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!MathUtil.nan(new double[]
                    {
                            A[i][j], lowerT[i][j], upperT[i][j]
                    }))
                    {
                        if (A[i][j] >= lowerT[i][j] && A[i][j] <= upperT[i][j])
                        {
                            C[i][j] = 1;
                        }
                        // else {C[i][j] = 0.0d;}
                    }
                }// end for

            }// end for

        }// end if
        else
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!Double.isNaN(A[i][j]))
                    {
                        if (A[i][j] == 0.0d)
                        {
                            C[i][j] = 1;
                        }
                        // else {C[i][j] = 0.0d;}
                    }
                }// end for

            }// end for

        }// end else

        return X;
    }// end method

    /**
     * 
     * @return
     */
    public Indices NOT()
    {
        return NOT(0.0d);
    }

    /**
     * 
     * @param B
     * @param tolerance
     * @return
     */
    public Indices XOR(Matrix B, double tolerance)
    {
        checkMatrixDimensions(B);

        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        double[][] matB = B.getArray();

        if (tolerance != 0.0d)
        {
            Matrix zeroMat = new Matrix(m, n);
            Matrix addTolerance = zeroMat.plus(tolerance);
            double[][] upperT = addTolerance.getArray();
            Matrix subTolerance = zeroMat.plus(-tolerance);
            double[][] lowerT = subTolerance.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!MathUtil.nan(new double[]
                    {
                            A[i][j], lowerT[i][j], upperT[i][j], matB[i][j]
                    }))
                    {
                        if (!((A[i][j] >= lowerT[i][j] && A[i][j] <= upperT[i][j]) && (matB[i][j] >= lowerT[i][j] && matB[i][j] <= upperT[i][j])))
                        {
                            C[i][j] = 1;
                        }
                        // else {C[i][j] = 1.0d;}
                    }
                }// end for

            }// end for

        }// end if
        else
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!MathUtil.nan(A[i][j], matB[i][j]))
                    {
                        if (!(A[i][j] == 0.0d && matB[i][j] == 0.0d))
                        {
                            C[i][j] = 1;
                        }
                        // else { C[i][j] = 1.0d; }
                    }
                }
            }// end for

        }// end else

        return X;
    }

    /**
     * 
     * @param B
     * @return
     */
    public Indices XOR(Matrix B)
    {
        return XOR(B, 0.0d);
    }

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public Indices XOR(double b, double tolerance)
    {
        Matrix X = new Matrix(m, n, b);
        return XOR(X, tolerance);
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices XOR(double b)
    {
        return XOR(b, 0.0d);
    }

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

        switch (Dim)
        {
        case ROW:
        {
            allInd = new Indices(1, n);
            allInd.setLogical(true);
            for (int j = 0; j < n; j++)
            {
                for (int i = 0; i < m; i++)
                {
                    if (A[i][j] != 0.0)
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
            break;
        }
        case COL:
        {
            allInd = new Indices(m, 1);
            allInd.setLogical(true);
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (A[i][j] != 0.0)
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
            break;
        }
        default:
        {
            throw new IllegalArgumentException("all : Dimension  " + Dim.toString() + " , not supported.");
        }
        }

        return allInd;
    }

    /**
     * ISNAN True for Not-a-Number. ISNAN(X) returns an array that contains 1's
     * where the elements of X are NaN's AND 0's where they are NOT. For
     * example, ISNAN([pi NaN Inf -Inf]) is [0 1 0 0]
     * 
     * @return
     */
    public boolean isnanBoolean()
    {
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (Double.isNaN(A[i][j]))
                {
                    return true;
                }
            }
        }// end for

        return false;
    }// end method

    public boolean isfiniteBoolean()
    {
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                // if(Double.isNaN(A[i][j])){ return true; }
                if (!MathUtil.nanOrInf(A[i][j]))
                {
                    return true;
                }
            }
        }// end for

        return false;
    }// end method

    public boolean isNull()
    {
        return this.A == null;
    }

    /**
     * ISNAN True for Not-a-Number. ISNAN(X) returns an array that contains 1's
     * where the elements of X are NaN's AND 0's where they are NOT. For
     * example, ISNAN([pi NaN Inf -Inf]) is [0 1 0 0]
     * 
     * @return
     */
    public Indices isnan()
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (Double.isNaN(A[i][j]))
                {
                    C[i][j] = 1;
                }
            }
        }// end for

        return X;
    }// end method

    public Indices isNonNans()
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!Double.isNaN(A[i][j]))
                {
                    C[i][j] = 1;
                }
            }
        }// end for

        return X;
    }

    public boolean isNonNansBoolean()
    {
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!Double.isNaN(A[i][j]))
                {
                    return true;
                }
            }
        }// end for

        return false;
    }

    /*
     * Return true if NaNs OR Infinitys element in the data.
     */
    /**
     * 
     * @return
     */
    public boolean isNotFinite()
    {
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (MathUtil.nanOrInf(A[i][j]))
                {
                    return true;
                }
            }
        }// end for

        return false;
    }

    /**
     * ISFINITE True for finite elements. ISFINITE(X) returns an array that
     * contains 1's where the elements of X are finite AND 0's where they are
     * NOT. For example, ISFINITE([pi NaN Inf -Inf]) is [1 0 0 0].
     * 
     * @return
     */
    public Indices isfinite()
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                // if(!Double.isNaN(A[i][j])){ C[i][j] = 1; }
                // else if(!Double.isInfinite(A[i][j])) { C[i][j] = 1; }
                if (!MathUtil.nanOrInf(A[i][j]))
                {
                    C[i][j] = 1;
                }
            }
        }// end for

        return X;
    }

    /**
     * ISINF True for infinite elements. ISINF(X) returns an array that contains
     * 1's where the elements of X are +Inf OR -Inf AND 0's where they are NOT.
     * For example, ISINF([pi NaN Inf -Inf]) is [0 0 1 1].
     * 
     * @return
     */
    public Indices isinf()
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (Double.isInfinite(A[i][j]))
                {
                    C[i][j] = 1;
                }
            }
        }// end for

        return X;
    }

    /**
     * Main diagonal matrix, eg: ( 3,2,4 ) ( 3 ) If matrix A = | 6,4,1 | then
     * A.diag() = | 4 | , a 3 x 1 matrix is returned. ( 9,1,5 ) ( 5 )
     * 
     * @return the main Diagonal matrix
     */
    public Matrix diag()
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
    public Matrix diag(int K)
    {
        int smallDim = m < n ? m : n;
        Matrix temp = copy();
        double[][] C = temp.getArray();
        Matrix result = null;
        double[][] B = null;

        if (temp.isVector())
        { // column OR i vector

            int len = length();
            int k = Math.abs(K);
            int M = len + k, N = M;
            result = new Matrix(M, N);
            B = result.getArray();
            double[] rowpacked = temp.getRowPackedCopy();

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
                result = new Matrix(smallDim, 1);
                B = result.getArray();
                for (int i = 0; i < smallDim; i++)
                {
                    B[i][0] = C[i][i];
                }
            }
            else if (K > 0)
            {
                u = smallDim - K;
                result = new Matrix(u + 1, 1);
                B = result.getArray();
                for (int v = 0; v < (u + 1); v++)
                {
                    B[v][0] = C[v][v + K];
                }
            }
            else if (K < 0)
            {
                u = smallDim + K;
                result = new Matrix(u, 1);
                B = result.getArray();
                for (int v = 0; v < (u); v++)
                {
                    B[v][0] = C[v - K][v];
                }
            }
        }
        return result;
    }// end

    /**
     * 
     * @param from
     * @param to
     * @return
     */
    public Matrix getEls(int from, int to)
    {
        if (from > to)
        {
            throw new ConditionalException("getEls : Value for integer parameter \"from\" (= " + from
                    + ") must \nequal or less than that of \"to\" (= " + to + ") ;");
        }
        int[] ind = Indices.linspace(from, to).getRowPackedCopy();
        Matrix R = null;
        R = this.getEls(ind);
        return R;
    }

    /**
     * 
     * @param from
     * @param to
     * @return
     * @deprecated Use the method <B>getEls()</B>, instead.
     */
    public Matrix getElements(int from, int to)
    {
        if (from > to)
        {
            throw new ConditionalException("getElements : Value for integer parameter \"from\" (= " + from
                    + ") must \nequal or less than that of \"to\" (= " + to + ") ;");
        }
        int[] ind = Indices.linspace(from, to).getRowPackedCopy();
        Matrix R = null;
        R = this.getElements(ind);
        return R;
    }

    public Matrix getEls(int[] ind)
    {
        int len = ind.length;
        Matrix R = new Matrix(len, 1);
        double val = 0.0;

        if (this.isRowVector())
        {
            R = new Matrix(1, len);
            for (int i = 0; i < len; i++)
            {
                val = this.getElementAt(ind[i]);
                R.set(0, i, val);
            }
        }
        else
        {
            R = new Matrix(len, 1);
            for (int i = 0; i < len; i++)
            {
                val = this.getElementAt(ind[i]);
                R.set(i, 0, val);
            }
        }

        return R;
    }

    /**
     * 
     * @param ind
     * @return
     * @deprecated Use the method <B>getEls()</B>, instead.
     */
    public Matrix getElements(int[] ind)
    {
        int len = ind.length;
        Matrix R = new Matrix(len, 1);
        double val = 0.0;
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
    public double getElementAt(int ind)
    {
        double val = 1.0;
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

    public void setElements(int[] ind, double val)
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

    public void setElements(int from, int to, Matrix val)
    {
        if (from > to)
        {
            throw new IllegalArgumentException("setElements: Integer parameter \"from\" (= " + from
                    + ") must equal or less than \"to\" (= " + to + ").");
        }
        int[] ind = null;
        if (from == to)
        {
            ind = new int[]
            {
                from
            };
        }
        else
        {
            ind = Indices.linspace(from, to).getRowPackedCopy();
        }
        setElements(ind, val);
    }

    /**
     * 
     * @param ind
     * @param val
     */
    public void setElements(int[] ind, Matrix val)
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

        if (val.numel() == 1)
        {
            // this.setElements(ind, val.start());
            // return;
        }

        if (ind.length != val.length())
        {
            throw new IllegalArgumentException("setElements: Parameter \"ind\" and \"val\" must have the same lengths.");
        }
        int len = ind.length;
        Matrix rowMat = null;
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
    public void setElementAt(int ind, double val)
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
     * 
     * @return
     */
    public Matrix round()
    {
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = Math.round(A[i][j]);
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
    public static Matrix random(int m, int n)
    {
        Matrix A = new Matrix(m, n);
        double[][] X = A.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                X[i][j] = Math.random();
            }
        }
        return A;
    }

    /**
     * Generate matrix with random elements
     * 
     * @param m
     *            Number of rows.
     * @param n
     * @return An m-by-n matrix with uniformly distributed random elements.
     */
    public static Matrix rand(int m, int n)
    {
        return random(m, n);
    }

    /**
     * Element at last entry (m-th,lenC-th)
     * 
     * @return
     */
    public double end()
    {
        return A[m - 1][n - 1];
    }

    public double start()
    {
        return A[0][0];
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
     * 
     * @return @deprecated Use the method <B>generateIndices()</B>, instead.
     */
    public Matrix generateIndex()
    {
        Matrix index = new Matrix(m, n);
        double[][] M = index.getArray();

        if (this.isVector())
        {
            int len = this.length();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    M[i][j] = (double) (i + j);
                }
            }// end for

            return index;
        }// end if

        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                M[i][j] = (double) i;
            }
        }// end method

        return index;
    }// end method

    /**
     * 
     * @param start
     * @param incr
     * @param points
     * @return
     */
    public static Matrix linIncrement(double start, double incr, int points)
    {
        if (points < 1)
        {
            throw new IllegalArgumentException(" linIncrement: The minimum number of points (=" + points
                    + ") must be at least 1 :");
        }
        if (incr <= 0.0)
        {
            throw new IllegalArgumentException(" linIncrement: The increment (=" + incr
                    + ") must be a positive number :");
        }
        if (points == 1)
        {
            return new Matrix(1, 1, start);
        }
        Matrix R = new Matrix(1, points);
        double[][] C = R.getArray();
        C[0][0] = start;
        for (int i = 1; i < points; i++)
        {
            C[0][i] = C[0][i - 1] + incr;
        }
        R.setSorted(true);
        return R;
    }// end method

    /**
     * 
     * @param Upper
     * @param Lower
     * @param Decr
     * @return
     */
    public static Matrix linDecrement(double Upper, double Lower, double Decr)
    {
        double lower = Lower;
        double upper = Upper;
        double decr = Decr;
        double result = 0.0;
        if (upper < lower)
        {
            throw new IllegalArgumentException(" linIncrement: Upper-bound (= " + upper
                    + ") , must be greater than the Lower-bound (= " + lower + ") :");
        }

        if (Decr <= 0.0)
        {
            throw new IllegalArgumentException(" linIncrement: The increment amount (= " + Decr
                    + ") , must be greater than zero :");
        }

        if ((decr > (upper - lower)) || (upper == lower))
        {
            // throw new
            // IllegalArgumentException(" linIncrement: Incr = "+incr+" , must be a positive number, which is less than "+(upper-lower)+"");
            return new Matrix(1, 1, upper);
        }

        ArrayList<Double> vector = new ArrayList<Double>();
        // vector.add(new Double(lower));
        int count = 0;
        result = upper;
        while (result >= lower)
        {
            vector.add(new Double(result));
            count++;
            result = upper - ((double) count) * decr;
        }
        int siz = vector.size();
        Matrix X = new Matrix(1, siz);
        double[][] C = X.getArray();
        // Object[] ob = vector.toArray();
        for (int i = 0; i < siz; i++)
        {
            C[0][i] = vector.get(i).doubleValue();
        }
        return X;

    }

    /**
     * 
     * @param Lower
     * @param Upper
     * @param Incr
     * @return
     */
    public static Matrix linIncrement(double Lower, double Upper, double Incr)
    {
        double lower = Lower;
        double upper = Upper;
        double incr = Incr;
        double result = 0.0;
        if (upper < lower)
        {
            throw new IllegalArgumentException(" linIncrement: Upper-bound (= " + upper
                    + ") , must be greater than the Lower-bound (= " + lower + ") :");
        }

        if (Incr <= 0.0)
        {
            throw new IllegalArgumentException(" linIncrement: The increment amount (= " + Incr
                    + ") , must be greater than zero :");
        }

        if ((incr > (upper - lower)) || (upper == lower))
        {
            // throw new
            // IllegalArgumentException(" linIncrement: Incr = "+incr+" , must be a positive number, which is less than "+(upper-lower)+"");
            return new Matrix(1, 1, lower);
        }

        ArrayList<Double> vector = new ArrayList<Double>();
        // vector.add(new Double(lower));
        int count = 0;
        result = lower;
        while (result <= upper)
        {
            vector.add(new Double(result));
            count++;
            result = lower + ((double) count) * incr;
        }
        int siz = vector.size();
        Matrix X = new Matrix(1, siz);
        double[][] C = X.getArray();
        // Object[] ob = vector.toArray();
        for (int i = 0; i < siz; i++)
        {
            C[0][i] = vector.get(i).doubleValue();
        }
        X.setSorted(true);
        return X;
    }// end method

    /**
     * 
     * @return
     */
    public boolean isRectangularMatrix()
    {
        return (m > 1 && n > 1);
    }

    /**
     * 
     * @param indices
     * @return
     */
    public static Matrix floatToMatrix(MatrixFlt indices)
    {
        if (indices == null)
        {
            return null;
        }
        int m = indices.getRowDimension();
        int n = indices.getColumnDimension();
        double val = 0.0;
        Matrix mat = new Matrix(m, n);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                val = (double) indices.get(i, j);
                /*
                 * if (indices.get(i, j) != Integer.MAX_VALUE) { mat.set(i, j,
                 * val); } else { mat.set(i, j, Double.NaN); }
                 */
                mat.set(i, j, val);
            }
        }
        return mat;
    }

    /**
     * 
     * @param indices
     * @return
     */
    public static Matrix indicesToMatrix(Indices indices)
    {
        if (indices == null)
        {
            return null;
        }
        int m = indices.getRowDimension();
        int n = indices.getColumnDimension();
        double val = 0.0;
        Matrix mat = new Matrix(m, n);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                val = (double) indices.get(i, j);
                if (indices.get(i, j) != Integer.MAX_VALUE)
                {
                    mat.set(i, j, val);
                }
                else
                {
                    mat.set(i, j, Double.NaN);
                }
            }
        }
        return mat;
    }

    /**
     * 
     * @param M
     *            Matrix
     * @return Vector
     *         <p>
     *         Return a container with elements of matrix 'M' as objects in a
     *         vector.
     *         </p>
     */
    public static ArrayList<Double> toCollection(Matrix M)
    {
        ArrayList<Double> v = new ArrayList<Double>();
        // Matrix m = M.toRowVector(); //collapse to a i.
        int m = M.getRowDimension();
        int n = M.getColumnDimension();
        // int len = m.length();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                v.add(new Double(M.get(i, j)));
            }
        }
        return v;
    }// end method

    public void printSize(String name)
    {
        System.out.print("\n " + name + " = [ " + m + " x " + n + " ]\n");
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

    /*
     * $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
     * 
     * $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
     */
    /**
     * 
     * @param B
     * @return
     */
    public boolean ltBoolean(Matrix B)
    {
        /*
         * //---- added 3/3/11 ---- if (this.numel() == 1) { return
         * B.ltBoolean(A[0][0]); } if (B.numel() == 1) { return
         * this.ltBoolean(B.start()); } //----------------------
         */

        checkMatrixDimensions(B);
        double[][] M = B.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] >= M[i][j])
                {
                    return false;
                }
                // else {C[i][j] = 0.0d;}
            }
        }
        return true;
    }

    /**
     * 
     * @param b
     * @return
     */
    public boolean ltBoolean(double b)
    {
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] >= b)
                {
                    return false;
                }
                // else {C[i][j] = 0.0d;}
            }
        }
        return true;
    }

    /**
     * 
     * @param B
     * @return
     */
    public boolean lteqBoolean(Matrix B)
    {

        /*
         * //---- added 3/3/11 ---- if (this.numel() == 1) { return
         * B.lteqBoolean(A[0][0]); } if (B.numel() == 1) { return
         * this.lteqBoolean(B.start()); } //----------------------
         */
        checkMatrixDimensions(B);
        double[][] M = B.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] > M[i][j])
                {
                    return false;
                }
                // else {C[i][j] = 0.0d;}
            }
        }
        return true;
    }

    /**
     * 
     * @param d
     * @return
     */
    public boolean lteqBoolean(double d)
    {
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] > d)
                {
                    return false;
                }
                // else {C[i][j] = 0.0d;}
            }
        }
        return true;
    }

    /**
     * 
     * @param B
     * @return
     */
    public boolean gtBoolean(Matrix B)
    {
        checkMatrixDimensions(B);
        double[][] M = B.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] <= M[i][j])
                {
                    return false;
                }
                // else {C[i][j] = 0.0d;}
            }
        }
        return true;
    }

    /**
     * 
     * @param b
     * @return
     */
    public boolean gtBoolean(double b)
    {

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] <= b)
                {
                    return false;
                }
                // else {C[i][j] = 0.0d;}
            }
        }
        return true;
    }

    /**
     * 
     * @param B
     * @return
     */
    public boolean gteqBoolean(Matrix B)
    {
        checkMatrixDimensions(B);
        double[][] M = B.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] < M[i][j])
                {
                    return false;
                }
                // else {C[i][j] = 0.0d;}
            }
        }
        return true;
    }

    /**
     * 
     * @param b
     * @return
     */
    public boolean gteqBoolean(double b)
    {

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] < b)
                {
                    return false;
                }
                // else {C[i][j] = 0.0d;}
            }
        }
        return true;
    }

    /**
     * 
     * @param B
     * @param tolerance
     * @return
     */
    public boolean eqBoolean(Matrix B, double tolerance)
    {

        // ---- added 3/3/11 ----
        if (this.numel() == 1)
        {
            // return B.eqBoolean(A[0][0], tolerance);
            for (int i = 0; i < B.m; i++)
            {
                for (int j = 0; j < B.n; j++)
                {
                    if (!MathUtil.equalsWithTol(A[0][0], B.A[i][j], tolerance))
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        if (B.numel() == 1)
        {
            // return this.eqBoolean(B.start(), tolerance);
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!MathUtil.equalsWithTol(B.A[0][0], A[i][j], tolerance))
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        // ----------------------

        checkMatrixDimensions(B);

        if (tolerance != 0.0d)
        {
            // Matrix addTolerance = B.plusCP(tolerance);
            // double[][] upperT = addTolerance.getArray();
            // Matrix subTolerance = B.plusCP(-tolerance);
            // double[][] lowerT = subTolerance.getArray();
            double[][] C_B = B.getArray();

            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!MathUtil.equalsWithTol(A[i][j], C_B[i][j], tolerance))
                    {
                        return false;
                    }
                    // else {C[i][j] = 0.0d;}
                }// end for

            }// end for

        }// end if
        else
        {
            double[][] M = B.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (A[i][j] != M[i][j])
                    {
                        return false;
                    }
                    // else {C[i][j] = 0.0d;}
                }// end for

            }// end for

        }// end else

        return true;
    }// end method

    /**
     * 
     * @param B
     * @return
     */
    public boolean equalSize(Matrix B)
    {
        boolean tf = (m == B.m && n == B.n);
        return tf;
    }

    /**
     * 
     * @param B
     * @return
     */
    public boolean equalSize(Indices B)
    {
        boolean tf = (m == B.getRowDimension() && n == B.getColumnDimension());
        return tf;
    }

    /**
     * 
     * @param B
     * @return
     */
    public boolean eqBoolean(Matrix B)
    {
        return eqBoolean(B, 0.0d);
    }

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public boolean eqBoolean(double b, double tolerance)
    {
        Matrix X = new Matrix(m, n, b);
        return eqBoolean(X, tolerance);
    }// end method

    /**
     * 
     * @param d
     * @return
     */
    public boolean eqBoolean(double d)
    {
        // Matrix X = new Matrix(m,lenC,d);
        return eqBoolean(d, 0.0d);
    }// end method

    /**
     * 
     * @param B
     * @param tolerance
     * @return
     */
    public boolean neqBoolean(Matrix B, double tolerance)
    {
        checkMatrixDimensions(B);

        if (tolerance != 0.0d)
        {
            Matrix addTolerance = B.plus(tolerance);
            double[][] upperT = addTolerance.getArray();
            Matrix subTolerance = B.plus(-tolerance);
            double[][] lowerT = subTolerance.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if ((A[i][j] >= lowerT[i][j] && A[i][j] <= upperT[i][j]))
                    {
                        return false;
                    }
                    // else {C[i][j] = 1.0d;}
                }// end for

            }// end for

        }// end if
        else
        {
            double[][] M = B.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if ((A[i][j] == M[i][j]))
                    {
                        return false;
                    }
                    // else {C[i][j] = 1.0d;}
                }// end for

            }// end for

        }// end else

        return true;
    }// end method

    /**
     * 
     * @param B
     * @return
     */
    public boolean neqBoolean(Matrix B)
    {
        return neqBoolean(B, 0.0d);
    }// end method

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public boolean neqBoolean(double b, double tolerance)
    {
        Matrix X = new Matrix(m, n, b);
        return neqBoolean(X, tolerance);
    }// end method

    /**
     * 
     * @param b
     * @return
     */
    public boolean neqBoolean(double b)
    {
        Matrix X = new Matrix(m, n, b);
        return neqBoolean(X, 0.0d);
    }// end method

    /**
     * 
     * @param B
     * @param tolerance
     * @return
     */
    public boolean andBoolean(Matrix B, double tolerance)
    {
        checkMatrixDimensions(B);

        double[][] matB = B.getArray();

        if (tolerance != 0.0d)
        {
            Matrix zeroMat = new Matrix(m, n);
            Matrix addTolerance = zeroMat.plus(tolerance);
            double[][] upperT = addTolerance.getArray();
            Matrix subTolerance = zeroMat.plus(-tolerance);
            double[][] lowerT = subTolerance.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!(!(A[i][j] >= lowerT[i][j] && A[i][j] <= upperT[i][j]) && !(matB[i][j] >= lowerT[i][j] && matB[i][j] <= upperT[i][j])))
                    {
                        return false;
                    }
                    // else {C[i][j] = 0.0d;}
                }// end for

            }// end for

        }// end if
        else
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!(A[i][j] != 0.0d && matB[i][j] != 0.0d))
                    {
                        return false;
                    }
                    // else { C[i][j] = 0.0d; }
                }
            }// end for

        }// end else

        return true;
    }// end method

    /**
     * 
     * @param B
     * @return
     */
    public boolean andBoolean(Matrix B)
    {
        return andBoolean(B, 0.0d);
    }

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public boolean andBoolean(double b, double tolerance)
    {
        Matrix X = new Matrix(m, n, b);
        return andBoolean(X, tolerance);
    }

    /**
     * 
     * @param b
     * @return
     */
    public boolean andBoolean(double b)
    {
        return andBoolean(b, 0.0d);
    }

    /**
     * 
     * @param B
     * @param tolerance
     * @return
     */
    public boolean orBoolean(Matrix B, double tolerance)
    {
        checkMatrixDimensions(B);

        double[][] matB = B.getArray();

        if (tolerance != 0.0d)
        {
            Matrix zeroMat = new Matrix(m, n);
            Matrix addTolerance = zeroMat.plus(tolerance);
            double[][] upperT = addTolerance.getArray();
            Matrix subTolerance = zeroMat.plus(-tolerance);
            double[][] lowerT = subTolerance.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!(!(A[i][j] >= lowerT[i][j] && A[i][j] <= upperT[i][j]) || !(matB[i][j] >= lowerT[i][j] && matB[i][j] <= upperT[i][j])))
                    {
                        return false;
                    }
                    // else {C[i][j] = 0.0d;}
                }// end for

            }// end for

        }// end if
        else
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!(A[i][j] != 0.0d || matB[i][j] != 0.0d))
                    {
                        return false;
                    }
                    // else { C[i][j] = 0.0d; }
                }
            }// end for

        }// end else

        return true;
    }// end method

    /**
     * 
     * @param B
     * @return
     */
    public boolean orBoolean(Matrix B)
    {
        return orBoolean(B, 0.0d);
    }

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public boolean orBoolean(double b, double tolerance)
    {
        Matrix X = new Matrix(m, n, b);
        return orBoolean(X, tolerance);
    }

    /**
     * 
     * @param b
     * @return
     */
    public boolean orBoolean(double b)
    {
        return orBoolean(b, 0.0d);
    }

    /**
     * 
     * @param tolerance
     * @return
     */
    public boolean notBoolean(double tolerance)
    {

        if (tolerance != 0.0d)
        {
            Matrix addTolerance = plus(tolerance);
            double[][] upperT = addTolerance.getArray();
            Matrix subTolerance = plus(-tolerance);
            double[][] lowerT = subTolerance.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!(A[i][j] >= lowerT[i][j] && A[i][j] <= upperT[i][j]))
                    {
                        return false;
                    }
                    // else {C[i][j] = 0.0d;}
                }// end for

            }// end for

        }// end if
        else
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (A[i][j] != 0.0d)
                    {
                        return false;
                    }
                    // else {C[i][j] = 0.0d;}
                }// end for

            }// end for

        }// end else

        return true;
    }// end method

    public List<double[]> toRowListArray()
    {
        List<double[]> rowArrays = new ArrayList<double[]>();
        for (int i = 0; i < m; i++)
        {
            rowArrays.add(this.A[i]);
        }
        return rowArrays;
    }

    public List<double[]> toColumnListArray()
    {
        List<double[]> columnArrays = new ArrayList<double[]>();
        for (int j = 0; j < n; j++)
        {
            double[] colArr = this.getColumnAt(j).getColumnPackedCopy();
            columnArrays.add(colArr);
        }
        return columnArrays;
    }

    /**
     * 
     * @return
     */
    public boolean notBoolean()
    {
        return notBoolean(0.0d);
    }

    private static void writeBuffered(Matrix mat, Writer baseWriter) throws java.io.IOException
    {

        if (mat == null || mat.getRowDimension() == 0 || mat.getColumnDimension() == 0)
        {
            return;
        }

        BufferedWriter writer = new BufferedWriter(baseWriter);

        double[][] AA = mat.getArray();
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

    public static void write(Matrix mat, String fileName) throws java.io.IOException
    {
        Writer writer = new FileWriter(fileName);
        writeBuffered(mat, writer);
        writer.close();
    }

    /**
     * 
     * @param B
     * @param tolerance
     * @return
     */
    public boolean xorBoolean(Matrix B, double tolerance)
    {
        checkMatrixDimensions(B);

        double[][] matB = B.getArray();

        if (tolerance != 0.0d)
        {
            Matrix zeroMat = new Matrix(m, n);
            Matrix addTolerance = zeroMat.plus(tolerance);
            double[][] upperT = addTolerance.getArray();
            Matrix subTolerance = zeroMat.plus(-tolerance);
            double[][] lowerT = subTolerance.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!(!((A[i][j] >= lowerT[i][j] && A[i][j] <= upperT[i][j]) && (matB[i][j] >= lowerT[i][j] && matB[i][j] <= upperT[i][j]))))
                    {
                        return false;
                    }
                    // else {C[i][j] = 1.0d;}
                }// end for

            }// end for

        }// end if
        else
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!(!(A[i][j] == 0.0d && matB[i][j] == 0.0d)))
                    {
                        return false;
                    }
                    // else { C[i][j] = 1.0d; }
                }
            }// end for

        }// end else

        return true;
    }

    /**
     * 
     * @param B
     * @return
     */
    public boolean xorBoolean(Matrix B)
    {
        return xorBoolean(B, 0.0d);
    }

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public boolean xorBoolean(double b, double tolerance)
    {
        Matrix X = new Matrix(m, n, b);
        return xorBoolean(X, tolerance);
    }

    /**
     * 
     * @param b
     * @return
     */
    public boolean xorBoolean(double b)
    {
        return xorBoolean(b, 0.0d);
    }

    public boolean isScalar()
    {
        // return (numel() == 1);
        return isscalar();
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
        Matrix sortedMat = null;
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
         // QuickSortMat(Matrix A, Dimension dim, boolean computeSortedIndex,
         // boolean sortedCopy)
            QuickSort sort = null;

            if (indexFlag)
            {
                sort = new QuickSortMat(this, dim, indexFlag);
                sortedInd = sort.getIndices();
            }
            else
            {
                sort = new QuickSortMat(this, dim);
            }
            sortedMat = (Matrix) sort.getSortedObject();
        }
        else
        {
            Matrix firstColumn = this.getColumnAt(0);
            QuickSort sortCol = null;

            // --------------------------------------------------
            sortCol = new QuickSortMat(firstColumn, dim, true);

            if (indexFlag)
            {

                sortedInd = sortCol.getIndices();
            }

            // --------------------------------------------------
            int[] rowsOrder = sortCol.getIndices().getRowPackedCopy();

            sortedMat = this.getRows(rowsOrder);
        }

        System.out
                .println("sortRows : This method needs to be updated so it implements the choice of \nsorting each rows by vectors of flags (integer in matlab, eg [-2 3], but should use 'enum' in java).");

        return new Object[]
        {
                sortedMat, sortedInd
        };
    }

    /**
     * 
     * @return
     */
    public Matrix abs()
    {
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j] = Math.abs(A[i][j]);
            }
        }
        return X;
    }

    /**
     * 
     * @param fromFind
     * @param val
     */
    public void setFindIj(FindInd fromFind, double val)
    {
        if (fromFind == null || fromFind.isNull())
        {
            return;
        }
        int len = fromFind.numel();
        Matrix mat = new Matrix(1, len, val);
        setFindIj(fromFind, mat);
    }

    /**
     * 
     * @param fromFind
     * @param values
     */
    public void setFindIj(FindInd fromFind, Matrix values)
    {
        if (fromFind == null || fromFind.isNull())
        {
            return;
        }

        if (values.isVector() == false)
        {
            throw new IllegalArgumentException("setFromFindIj : Parameter 'values' must be a row or column vector.");
        }

        // if (!fromFind.isFindIndex()) {
        // throw new
        // IllegalArgumentException("setValuesAtIndices : Parameter 'indices' must be an object from a \"find()\" method call.");
        // }
        int indRowLen = fromFind.numel();// .getRowDimension();
        int lenValues = values.length();
        if (indRowLen != lenValues)
        {
            throw new IllegalArgumentException("setFromFindIj : Lengths of 'indices' and 'values' must be the same.");
        }

        if (!fromFind.equalSize(new int[]
        {
                m, n
        }))
        {
            throw new IllegalArgumentException(
                    "setFromFindIj : Object \"fromFind\" must have the same size as this matrix.");
        }

        double val = 0.0;
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
     * @param val
     */
    public void setFromFind(Indices fromFind, double val)
    {
        if (fromFind == null)
        {
            return;
        }
        int len = fromFind.getRowDimension();
        Matrix mat = new Matrix(1, len, val);
        setFromFind(fromFind, mat);
    }

    /**
     * 
     * @param fromFind
     * @param values
     */
    public void setFromFind(Indices fromFind, Matrix values)
    {
        if (fromFind == null)
        {
            return;
        }

        if (values.isVector() == false)
        {
            throw new IllegalArgumentException(
                    "setValuesAtIndices : Parameter 'values' must be a row or column vector.");
        }

        if (!fromFind.isFindIndex())
        {
            throw new IllegalArgumentException(
                    "setValuesAtIndices : Parameter 'indices' must be an object from a \"find()\" method call.");
        }
        int indRowLen = fromFind.getRowDimension();
        int lenValues = values.length();
        if (indRowLen != lenValues)
        {
            throw new IllegalArgumentException(
                    "setValuesAtIndices : Lengths of 'indices' and 'values' must be the same.");
        }

        double val = 0.0;
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
     * This method supplies the fromFind of (i,j) in an Indices object where the
     * i-th are stored in column[0] AND the j-th in column[1].
     * 
     * @param fromFind
     *            Indices
     * @param values
     *            Matrix
     * @deprecated Use method <B>setFromFind</B> instead.
     */
    public void setValuesAtIndices(Indices fromFind, Matrix values)
    {
        setFromFind(fromFind, values);
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
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * @param diagonal
     *            Diagonal to specify for the lower triangular of this matrix
     * @return lower triangular matrix
     */
    public Matrix tril(int diagonal)
    {
        Matrix X = copy();
        double[][] temp = X.getArray();

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
                            temp[i][j] = 0.0d;
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
                            temp[i][j] = 0.0d;
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
                            temp[i][j] = 0.0d;
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
                    temp[i][j] = 0.0d;
                }
            }
        }

        return X;
    }// ---------end method tril-----------

    /**
     * Extract lower triangular matrix. ( 3,2,4,-2 ) ( 3,0,0,0 ) If matrix A = |
     * 6,4,1, 3 | , A.tril() = | 6,4,0,0 | ( 9,1,5,-1 ) ( 9,1,5,0 )
     * 
     * @return lower triangular matrix
     */
    public Matrix tril()
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
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * @param diagonal
     * @return upper triangular matrix
     */
    public Matrix triu(int diagonal)
    {
        Matrix X = copy();
        double[][] temp = X.getArray();

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
                            temp[i][j] = 0.;
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
                            temp[i][j] = 0.;
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
                            temp[i][j] = 0.;
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
                    temp[i][j] = 0.;
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
    public Matrix triu()
    {
        return triu(0);
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
    public Matrix flipLR()
    {
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
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
    public Matrix flipUD()
    {
        Matrix X = new Matrix(m, n);
        double[][] C = X.getArray();
        double[][] temp = this.getArrayCopy();
        double[] D = null;
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

    public Matrix flipdim(Dimension dim)
    {
        Matrix flip = null;
        if (dim == Dimension.ROW)
        {
            flip = this.flipUD();
        }
        else
        {
            flip = this.flipLR();
        }
        return flip;
    }

    /**
     * Rotate this matrix 90-degrees (one quadrant) anti-clockwise
     * 
     * @return 90-degrees anti-clockwise rotated matrix.
     */
    public Matrix rot90()
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
    public Matrix rot90(int quadrant)
    {
        int K = Math.abs(quadrant);
        K = K % 4; // four quadrants.

        // Matrix matCopy = copy();
        Matrix X = null;

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
     * 
     * @param rowIndex
     * @return
     */
    public double[] toArrayFromRowAt(int rowIndex)
    {
        double[][] X = getArrayCopy();
        if (rowIndex > (X.length - 1))
        {
            throw new IllegalArgumentException(" toArrayFromRowAt : Row index is out-of-bound.");
        }
        return X[rowIndex];
    }

    /**
     * 
     * @param colIndex
     * @return
     */
    public double[] toArrayFromColAt(int colIndex)
    {
        int rows = getRowDimension();
        int cols = getColumnDimension();
        if (colIndex > (cols - 1))
        {
            throw new IllegalArgumentException("toArrayFromColAt : Column index is out-of-bound.");
        }
        double[][] X = getMatrix(0, rows - 1, colIndex, colIndex).getArrayCopy();
        int len = X.length;
        double[] val = new double[len];

        for (int i = 0; i < len; i++)
        {
            val[i] = X[i][0];
        }
        return val;
    }

    /**
     * 
     * @return
     */
    public Indices toIndices()
    {
        if (A == null)
        {
            return new Indices(null, m, n);
        }
        Indices indices = new Indices(m, n);
        int[][] I = indices.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (Double.isNaN(A[i][j]) || Double.isInfinite(A[i][j]))
                {
                    I[i][j] = Integer.MAX_VALUE;
                }
                else
                {
                    I[i][j] = (int) A[i][j];
                }
            }
        }
        return indices;
    }

    /**
     * 
     * @param indices
     * @return
     */
    public Matrix getFindIj(FindInd indices)
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
        Matrix R = null;
        if (this.isRowVector())
        {
            R = new Matrix(1, r);
        }
        else
        {
            R = new Matrix(r, 1);
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
    public Matrix getFromFind(Indices findIndices)
    {
        if (findIndices.isFindIndex() == false)
        {
            throw new IllegalArgumentException(
                    " getFromFind : Parameter \"findIndices\" must be logical, ie, it must come from a logical operation such <, <=, > , >= , not, and, eq, neq, etc....");
        }
        int r = findIndices.getRowDimension();
        Matrix R = new Matrix(r, 1);
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

    /**
     * 
     * @deprecated Use the method <B>getFromFind</B> method instead.
     * @param findIndices
     * @return
     */
    public Matrix matrixFromFind(Indices findIndices)
    {
        return getFromFind(findIndices);
    }

    public Matrix uniqueSet()
    {
        return uniqueSet(false);
    }

    public Matrix uniqueSet(boolean rowVec)
    {
        Matrix mat = new UniqueSet(this).getUniqueData();
        if (rowVec)
        {
            mat = mat.toRowVector();
        }
        return mat;
    }

    /**
     * 
     * @return
     */
    public Unique unique()
    {

        return new Unique(this);

    }

    public boolean isUnique()
    {
        if (!this.isVector())
        {
            return false;
        }

        int len = this.length();
        if (len == 1)
        {
            return true;
        }

        ArrayList<Double> uniqueElements = new ArrayList<Double>();

        uniqueElements.add(A[0][0]);
        if (this.isRowVector())
        {
            for (int j = 1; j < len; j++)
            {
                if (!uniqueElements.contains(new Double(A[0][j])))
                {
                    uniqueElements.add(A[0][j]);
                }
            }
        }
        else
        {
            for (int i = 1; i < len; i++)
            {
                if (!uniqueElements.contains(new Double(A[i][0])))
                {
                    uniqueElements.add(A[i][0]);
                }
            }
        }

        boolean tf = (uniqueElements.size() == len);

        return tf;
    }

    public ArrayList<Double> toArrayList()
    {
        ArrayList<Double> list = new ArrayList<Double>();
        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                list.add(new Double(A[i][j]));
            }
        }
        return list;
    }

    public ArrayList<Number> toArrayListNumber()
    {
        ArrayList<Number> list = new ArrayList<Number>();
        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                list.add(new Double(A[i][j]));
            }
        }
        return list;
    }

    public Cell num2cell()
    {
        Cell cellArr = new Cell(m, n);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                // Cell C = new Cell(1, 1, A[i][j]);
                cellArr.set(i, j, A[i][j]);
            }
        }
        return cellArr;
    }

    public Cell mat2Cell()
    {
        return num2cell();
    }

    public Cell mat2cell(int[] row, int[] col)
    {
        // if(row.length!)
        if (row == null || row.length == 0)
        {
            throw new ConditionalException("mat2cell : Parameter \"row\" must be non-null or empty.");
        }
        if (col == null || col.length == 0)
        {
            throw new ConditionalException("mat2cell : Parameter \"col\" must be non-null or empty.");
        }

        Indices rowInd = new Indices(row);
        int sumRow = JDatafun.sum(rowInd).start();
        if (sumRow != m)
        {
            throw new ConditionalException("mat2cell : Elements of array \"row\" must add up to total rows (m = " + m
                    + ").");
        }
        if (rowInd.LTEQ(0).anyBoolean())
        {
            throw new ConditionalException("mat2cell : All elements of array parameter \"row\" must be positive.");
        }

        Indices colInd = new Indices(col);
        int sumCol = JDatafun.sum(colInd).start();
        if (sumCol != n)
        {
            throw new ConditionalException("mat2cell : Elements of array \"col\" must add up to total columns (n = "
                    + n + ").");
        }
        if (colInd.LTEQ(0).anyBoolean())
        {
            throw new ConditionalException("mat2cell : All elements of array parameter \"col\" must be positive.");
        }

        int lenRow = row.length;
        int lenCol = col.length;
        Cell cellMat = new Cell(lenRow, lenCol);
        for (int i = 0; i < lenRow; i++)
        {
            for (int j = 0; j < lenCol; j++)
            {
                int[] arr1 = Indices.linspace(0, i).getRowPackedCopy();
                int[] arr2 = Indices.linspace(0, j).getRowPackedCopy();
                Matrix mat = this.getMatrix(arr1, arr2);
                cellMat.set(i, j, mat);
            }
        }

        return cellMat;
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
                if (A[i][j] != 0.0)
                {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 
     * @param indices
     * @param entryValue
     */
    public void setEntriesAtIndicesTo(Indices indices, double entryValue)
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
            this.set(I, J, entryValue);
        }
    }

    public Double getD(int i, int j)
    {
        return new Double(A[i][j]);
    }

    /**
     * 
     * @param row
     * @return
     */
    public Matrix removeRowAt(int row)
    {
        return this.removeRowsAt(new int[]
        {
            row
        });
    }

    /**
     * 
     * @param rows
     * @return
     */
    public Matrix removeRowsAt(int[] rows)
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
            // arrayListRowCount.add(new Integer(count++));
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

        Matrix R = this.getRows(newRows);
        return R;
    }

    /**
     * 
     * @param col
     * @return
     */
    public Matrix removeColAt(int col)
    {
        return this.removeColsAt(new int[]
        {
            col
        });
    }

    /**
     * 
     * @param cols
     * @return
     */
    public Matrix removeColsAt(int[] cols)
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
            // arrayListColCount.add(new Integer(count++));
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

        Matrix R = this.getColumns(newCols);
        return R;
    }

    public Matrix removeElAt(int row, int col)
    {
        if (row >= this.m)
        {
            throw new ConditionalRuleException("removeElAt : Index parameter \"row\" (= " + row + ") is out of bound.");
        }
        if (col >= this.n)
        {
            throw new ConditionalRuleException("removeElAt : Index parameter \"col\" (= " + col + ") is out of bound.");
        }
        Matrix result = null;
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

    public Matrix removeEls(int[] numEls)
    {
        Matrix result = null;
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

        ArrayList<Double> R = new ArrayList<Double>();

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
            Double DB = null;
            R.set(indRem, DB);
        }

        int siz = R.size();
        ArrayList<Double> RnonNull = new ArrayList<Double>();

        for (int k = 0; k < siz; k++)
        {
            Double DB = R.get(k);
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
            result = new Matrix(1, siz);
            for (int i = 0; i < siz; i++)
            {
                result.set(0, i, RnonNull.get(i).doubleValue());
            }
        }
        else
        {// vector
            if (this.isRowVector())
            {
                result = new Matrix(1, siz);
                for (int i = 0; i < siz; i++)
                {
                    result.set(0, i, RnonNull.get(i).doubleValue());
                }
            }
            else
            {
                result = new Matrix(siz, 1);
                for (int i = 0; i < siz; i++)
                {
                    result.set(i, 0, RnonNull.get(i).doubleValue());
                }
            }
        }
        return result;
    }

    public Matrix removeElAt(int ind)
    {
        Matrix result = null;
        ArrayList<Double> R = new ArrayList<Double>();
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
                result = new Matrix(1, newLen);
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
                result = new Matrix(newLen, 1);
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
            Matrix newMat = this.toColVector();
            for (int i = 0; i < len; i++)
            {
                if (i != ind)
                {
                    R.add(newMat.get(i, 0));
                }
            }
            newLen = len - 1;
            result = new Matrix(1, newLen);
            for (int i = 0; i < newLen; i++)
            {
                result.set(0, i, R.get(i));
            }
        }
        return result;
    }

    public Matrix finargcat(Dimension dim, Matrix... argMat)
    {
        if (dim == null)
        {
            throw new ConditionalException("finargcat : Parameter \"dim\" must be non-null.");
        }
        if (argMat == null || argMat.length == 0)
        {
            throw new ConditionalException(
                    "finargcat : Matrix array parameter \"argMat\" must be non-null or non-empty.");
        }
        int len = argMat.length;
        // check the ones that are not null.
        ArrayList<Matrix> nonNull = new ArrayList<Matrix>();
        nonNull.add(this);
        int count = 0;
        for (int i = 0; i < len; i++)
        {
            if (argMat[i] != null)
            {
                nonNull.add(argMat[i]);
                count++;
            }
        }

        if (count == 0)
        {
            throw new ConditionalException("finargcat : All elements of Matrix array parameter \"argMat\" are null.");
        }

        Matrix catMat = null;
        int siz = nonNull.size();
        int maxNum = 0;
        Matrix tmp = null;
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
            catMat = new Matrix(totRow, maxNum, Double.NaN);
            for (int i = 0; i < siz; i++)
            {
                tmp = nonNull.get(i);
                if (i == 0)
                {
                    end = tmp.getRowDimension() - 1;
                    catMat.setMatrix(beg, end, 0, tmp.getColumnDimension() - 1, tmp);

                }
                else
                {
                    beg = end + 1;
                    end = end + tmp.getRowDimension();
                    catMat.setMatrix(beg, end, 0, tmp.getColumnDimension() - 1, tmp);
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
            catMat = new Matrix(maxNum, totCol, Double.NaN);
            for (int i = 0; i < siz; i++)
            {
                tmp = nonNull.get(i);
                if (i == 0)
                {
                    end = tmp.getColumnDimension() - 1;
                    catMat.setMatrix(0, tmp.getRowDimension() - 1, beg, end, tmp);

                }
                else
                {
                    beg = end + 1;
                    end = end + tmp.getColumnDimension();
                    catMat.setMatrix(0, tmp.getRowDimension() - 1, beg, end, tmp);
                }
                // System.out.println((i+1)+" ) begin = "+beg+" ;  end = "+end);
            }
        }

        return catMat;
    }

    public Matrix syncSortByRows(int[] ind)
    {
        if (ind == null || ind.length == 0)
        {
            throw new ConditionalRuleException(
                    "syncSortByRows : Integer array input argument must be non-null or non-empty.");
        }

        int len = ind.length;

        if (m != n)
        {
            throw new ConditionalRuleException(
                    "syncSortByRows : Integer array input's length must be the same as the number of rows.");
        }

        // check if unique.

        // check i

        Matrix sortRows = null;
        return sortRows;
    }

    public double sumAll()
    {
        double sum = JDatafun.sum(JDatafun.sum(this)).start();
        return sum;
    }

    /**
     * 
     * @return
     */
    public Matrix size()
    {
        return new Matrix(new double[][]
        {
            {
                    (double) m, (double) n
            }
        });
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

    public Matrix sign()
    {
        return JDatafun.sign(this);
    }

    /**
     * 
     * @return
     */
    public boolean anyBoolean()
    {
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] != 0.0)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     * @return
     */
    public ComplexMatrix complexRoots()
    {
        if (!isVector())
        {
            throw new IllegalArgumentException("complexRoots : Matrix must be either a column or row vector.");
        }
        Matrix c = toRowVector();
        int lenC = c.length();

        if (lenC == 1)
        {
            return null;
        }

        Indices inz = c.NEQ(0.0).find();
        // System.out.println("inz = "+inz.getRowDimension());
        Matrix printINZ = Matrix.indicesToMatrix(inz);
        // System.out.println("\lenC-----------printINZ----------");
        // printINZ.print(1,0);
        // All elements are zero
        if (inz == null)
        {
            return null;
        }

        // Strip leading zeros AND throw away.
        // Strip trailing zeros, but remember them as roots at zero.
        int nnz = inz.getRowDimension();
        int[] range = Matrix.intLinspace(inz.get(0, 1), inz.get(nnz - 1, 1));
        c = c.getColumns(range);// c = c(inz(1):inz(nnz));

        int t = lenC - inz.get(nnz - 1, 1) - 1;
        System.out.println("t = " + t);
        // r = zeros(lenC-inz(nnz),1,class(c));
        // if(t<=0) { return null; }

        ComplexMatrix r = null;
        if (t > 0)
        {
            r = new ComplexMatrix(t, 1);
        }

        lenC = c.length();
        System.out.println("n = " + lenC);

        if (lenC > 1)
        {
            Matrix a = Matrix.ones(1, lenC - 2).diag(-1);// diag(ones(1,lenC-2,class(c)),-1);

            range = Matrix.intLinspace(1, lenC - 1);
            Matrix temp = c.getColumns(range).uminus().arrayRightDivide(c.get(0, 0)); // a(1,:)
                                                                                      // =
                                                                                      // -c(2:lenC)
                                                                                      // ./
                                                                                      // c(1);

            a.setRowAt(0, temp);

            // System.out.println("\lenC------- a --------");
            // a.print(4,4);
            EigenvalueDecomposition ed = a.eig();
            double[] real = ed.getRealEigenvalues();
            double[] imag = ed.getImagEigenvalues();
            int len = real.length;
            Matrix re = new Matrix(len, 1);
            Matrix im = new Matrix(len, 1);
            for (int i = 0; i < len; i++)
            {
                re.set(i, 0, real[i]);
                im.set(i, 0, imag[i]);
            }

            /*
             * System.out.println("\lenC------- re --------"); re.print(4,4);
             * System.out.println("\lenC------- im --------"); im.print(4,4);
             */
            ComplexMatrix eigMat = new ComplexMatrix(re, im);
            if (r != null)
            {
                r = r.mergeV(eigMat);
            }
            else
            {
                r = eigMat;
            }
        }

        return r;
    }

    public boolean isscalar()
    {
        if (this.A == null)
        {
            return false;
        }
        return (this.numel() == 1);
    }

    /**
     * 
     * @return
     */
    public Matrix roots()
    {
        return roots(MathUtil.EPS);
    }

    /*
     * Return an 'object' array of length 3 with object[0] the unique matrix
     * (collapsed into a vector), object[1] and indices vector of the i-position
     * and object[2] is an indices vector of the column-position.
     */
    public Object[] getUnique()
    {
        return JOps.uniqueMat(this);
    }

    public void scaleRows(Matrix colVec)
    {
        if (!colVec.isVector())
        {
            throw new ConditionalException("scaleRows : colVec must be a vector and not a matrix.");
        }
        if (m != colVec.length())
        {
            throw new ConditionalException("scaleRows : colVec must a length equal to (= " + m
                    + "), number of rows of this matrix object.");
        }
        double val = 0.0;
        for (int i = 0; i < m; i++)
        {
            if (colVec.isRowVector())
            {
                val = colVec.get(0, i);
            }
            else
            {
                val = colVec.get(i, 0);
            }
            for (int j = 0; j < n; j++)
            {
                A[i][j] = A[i][j] * val;
            }
        }
    }

    /**
     * 
     * @param i
     * @param j
     * @param s
     */
    public void set(int i, int j, Double s)
    {
        A[i][j] = s.doubleValue();
    }

    public void setEnd(double end)
    {
        A[m - 1][n - 1] = end;
    }

    /**
     * 
     * @return
     */
    public boolean isSquare()
    {
        return m == n;
    }

    public boolean ishermitian(double tol)
    {
        boolean tf = this.EQ(this.transpose(), tol).allBoolean();
        return tf;
    }

    /*
     * A matrix is hermitian if X=X'
     */
    public boolean ishermitian()
    {
        return ishermitian(0.0);
    }

    public ComplexMatrix rootsC()
    {
        // Matrix c = this.copy();
        if (!isVector())
        {
            throw new IllegalArgumentException("roots : Matrix must be either a column or row vector.");
        }

        Matrix c = null; // change to i

        if (this.isColVector())
        {
            c = toRowVector();
        }
        else
        {
            c = this;
        }

        Indices INZ = c.NEQ(0.0).find();
        // Matrix inz = c.find();

        // All elements are zero
        if (INZ == null)
        {
            return null;
        }

        int lenC = c.length();
        if (lenC > 1)
        {
            while (c.get(0, 0) == 0.0)
            {// Strip leading zeros

                lenC = c.length();
                c = c.getMatrix(0, 0, 1, lenC - 1);
            }// end while

            lenC = c.length();
        }// end if

        if (lenC == 1)
        {
            return null;
        }// a constant value like 'y=4, y=-7' , has no root OR infinite roots as
         // in y=0.

        Matrix C_copy = c.flipLR();
        int count = 0;
        if (lenC > 1)
        {
            while (C_copy.get(0, 0) == 0.0)
            { // Strip trailing zeros, but flag as root at zeros.

                int N = C_copy.length();
                C_copy = C_copy.getMatrix(0, 0, 1, N - 1);
                count++;
            }// end while

            lenC = c.length();
        }// end if

        if (lenC == 1 && count != 0)
        { // a function like 'y=x, y=-7*x' , has a root at zero

            return new ComplexMatrix(1, 1);// Matrix(1,1,0.0);

        }

        if (count != 0)
        {
            c = C_copy.flipLR();
            lenC = c.length();
        }

        double[][] rB = c.getArray();
        // System.out.println("***** Matrix c *****");
        // c.print(4,0);

        ComplexMatrix RE = null;// Matrix.toMatrix(real);
        // System.out.println(" lenC = "+lenC);

        if (lenC > 1)
        {
            Matrix ones = new Matrix(1, lenC - 2, 1.0d);
            Matrix a = ones.diag(-1);
            double[][] C = a.getArray();
            int cols = a.getColumnDimension();

            for (int i = 0; i < cols; i++)
            {
                if (i > (cols - 1))
                {
                    break;
                }
                else
                {
                    C[0][i] = -rB[0][i + 1] / rB[0][0];
                }
            }// end for

            EigenvalueDecomposition ed = a.eig();
            double[] real = ed.getRealEigenvalues();
            double[] imag = ed.getImagEigenvalues();

            Matrix Re = new Matrix(real).toColVector();
            Matrix Im = new Matrix(imag).toColVector();

            /*
             * ArrayList<Double> realRootVector = new ArrayList<Double>();
             * 
             * for(int i=0; i<imag.length; i++){
             * if(MathUtil.equalsWithTol(imag[i],0.0d,tol)){
             * realRootVector.add(new Double(real[i])); } }
             * 
             * if(count!=0){ //include ALL multiplicity roots at zeros. for(int
             * k=0; k<count; k++){ realRootVector.add(new Double(0.0d)); } }
             * 
             * int sizVec = realRootVector.size(); if(sizVec==0) { return null;
             * } //ALL roots are complex numbers.
             * 
             * double[][] realR = new double[sizVec][1]; for(int i=0; i<sizVec;
             * i++) { realR[i][0] = realRootVector.get(i).doubleValue(); }
             */
            RE = new ComplexMatrix(Re, Im);
        }// end if

        return RE;
    }// end method

    /**
     * Check for NULL value, that would likely to return;
     * 
     * @param tol
     * @return
     */
    public Matrix roots(double tol)
    {
        // Matrix c = this.copy();
        if (!isVector())
        {
            throw new IllegalArgumentException("roots : Matrix must be either a column or row vector.");
        }

        Matrix c = null; // change to i

        if (this.isColVector())
        {
            c = toRowVector();
        }
        else
        {
            c = this;
        }

        Indices INZ = c.NEQ(0.0).find();
        // Matrix inz = c.find();

        // All elements are zero
        if (INZ == null)
        {
            return null;
        }

        int lenC = c.length();
        if (lenC > 1)
        {
            while (c.get(0, 0) == 0.0)
            {// Strip leading zeros

                lenC = c.length();
                c = c.getMatrix(0, 0, 1, lenC - 1);
            }// end while

            lenC = c.length();
        }// end if

        if (lenC == 1)
        {
            return null;
        }// a constant value like 'y=4, y=-7' , has no root OR infinite roots as
         // in y=0.

        Matrix C_copy = c.flipLR();
        int count = 0;
        if (lenC > 1)
        {
            while (C_copy.get(0, 0) == 0.0)
            { // Strip trailing zeros, but flag as root at zeros.

                int N = C_copy.length();
                C_copy = C_copy.getMatrix(0, 0, 1, N - 1);
                count++;
            }// end while

            lenC = c.length();
        }// end if

        if (lenC == 1 && count != 0)
        {
            return new Matrix(1, 1, 0.0);
        } // a function like 'y=x, y=-7*x' , has a root at zero

        if (count != 0)
        {
            c = C_copy.flipLR();
            lenC = c.length();
        }

        double[][] rB = c.getArray();
        // System.out.println("***** Matrix c *****");
        // c.print(4,0);

        Matrix RE = null;// Matrix.toMatrix(real);
        // System.out.println(" lenC = "+lenC);

        if (lenC > 1)
        {
            Matrix ones = new Matrix(1, lenC - 2, 1.0d);
            Matrix a = ones.diag(-1);
            double[][] C = a.getArray();
            int cols = a.getColumnDimension();

            for (int i = 0; i < cols; i++)
            {
                if (i > (cols - 1))
                {
                    break;
                }
                else
                {
                    C[0][i] = -rB[0][i + 1] / rB[0][0];
                }
            }// end for

            EigenvalueDecomposition ed = a.eig();
            double[] real = ed.getRealEigenvalues();
            double[] imag = ed.getImagEigenvalues();

            ArrayList<Double> realRootVector = new ArrayList<Double>();

            for (int i = 0; i < imag.length; i++)
            {
                if (MathUtil.equalsWithTol(imag[i], 0.0d, tol))
                {
                    realRootVector.add(new Double(real[i]));
                }
            }

            if (count != 0)
            { // include ALL multiplicity roots at zeros.

                for (int k = 0; k < count; k++)
                {
                    realRootVector.add(new Double(0.0d));
                }
            }

            int sizVec = realRootVector.size();
            if (sizVec == 0)
            {
                return null;
            } // ALL roots are complex numbers.

            double[][] realR = new double[sizVec][1];
            for (int i = 0; i < sizVec; i++)
            {
                realR[i][0] = realRootVector.get(i).doubleValue();
            }

            RE = new Matrix(realR);
        }// end if

        return RE;
    }// end method

    /**
     * 
     * @param i
     * @param j
     * @return
     */
    public Matrix getAsMatrix(int i, int j)
    {
        double val = get(i, j);
        return new Matrix(1, 1, val);
    }

    /**
     * 
     * @return
     */
    public Matrix flipTopLToRightB()
    {
        if (this.isSquare() == false)
        {
            throw new IllegalArgumentException(" flipTopLToRightB  :  Matrix must be square.");
        }

        Matrix R = new Matrix(m, n);
        double lower = 0.0;
        double upper = 0.0;

        if (m == 1)
        {
            return this.copy();
        }

        R.set(m - 1, m - 1, A[m - 1][m - 1]);

        for (int i = 0; i < m - 1; i++)
        {
            R.set(i, i, A[i][i]);
            for (int k = i + 1; k < m; k++)
            {
                lower = A[k][i];
                upper = A[i][k];
                R.set(k, i, upper);
                R.set(i, k, lower);
            }
        }
        return R;
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

    public static ArrayList<String> readTextFile(String fileName) throws FileNotFoundException, IOException
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

        ArrayList<String> fromFile = null;

        fromFile = Matrix.readTextFile(bufReader);

        /*
         * try { fromFile = Matrix.read(bufReader);
         * 
         * 
         * } catch (IOException io) { throw io; }
         */
        return fromFile;
    }

    public static ArrayList<String> readTextFile(BufferedReader input) throws java.io.IOException
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

        ArrayList<String> strList = new ArrayList<String>();

        // String nanStr = "nan";
        // String infStr = "inf";
        // Ignore initial empty lines
        while (tokenizer.nextToken() == StreamTokenizer.TT_EOL)
        {
        }
        if (tokenizer.ttype == StreamTokenizer.TT_EOF)
        {
            throw new java.io.IOException("Unexpected EOF on file read.");
        }
        do
        {
            String ts = tokenizer.sval;
            /*
             * Double dbObj = null; if (ts.toLowerCase().contains(nanStr)) {
             * dbObj = new Double(Double.NaN); } else if
             * (ts.toLowerCase().contains(infStr)) { if (ts.charAt(0) == '-') {
             * dbObj = new Double(Double.NEGATIVE_INFINITY); } else { dbObj =
             * new Double(Double.POSITIVE_INFINITY); } } else { dbObj =
             * Double.valueOf(ts); }
             */
            strList.add(ts); // Read & store 1st i.

        }
        while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);

        /*
         * int n = strList.size(); // Now we've got the number of columns!
         * 
         * double row[] = new double[n]; for (int j = 0; j < n; j++) { //
         * extract the elements of the 1st i. row[j] = ((Double)
         * strList.elementAt(j)).doubleValue(); } strList.removeAllElements();
         * strList.addElement(row); // Start storing rows instead of columns.
         * 
         * while (tokenizer.nextToken() == StreamTokenizer.TT_WORD) { // While
         * non-empty lines strList.addElement(row = new double[n]); int j = 0;
         * do { if (j >= n) { throw new java.io.IOException("Row " +
         * strList.size() + " is too long."); } String ts = tokenizer.sval;
         * double dbObj = 0.0; if (ts.toLowerCase().contains(nanStr)) { dbObj =
         * Double.NaN; } else if (ts.toLowerCase().contains(infStr)) { if
         * (ts.charAt(0) == '-') { dbObj = Double.NEGATIVE_INFINITY; } else {
         * dbObj = Double.POSITIVE_INFINITY; } } else { dbObj =
         * Double.valueOf(ts).doubleValue(); }
         * 
         * row[j++] = dbObj;
         * 
         * } while (tokenizer.nextToken() == StreamTokenizer.TT_WORD); if (j <
         * n) { throw new java.io.IOException("Row " + strList.size() + " is too
         * short."); } }
         */
        // int m = strList.size(); // Now we've got the number of rows.
        // double[][] A = new double[m][];
        // strList.copyInto(A); // copy the rows out of the vector
        return strList;
    }

    public static Matrix readMat(String filePath, String fileName)
    {
        Matrix matArr = null;

        System.out.println("\nINPUTS READINGS STARTS : " + fileName);
        // s,q,Hq1,h1,Dh1,logFq1
        try
        {
            matArr = Matrix.read(filePath + fileName);
        }
        catch (FileNotFoundException fe)
        {
            // fe.printStackTrace();
            return null;
        }
        catch (IOException io)
        {
            // io.printStackTrace();
            return null;
        }

        System.out.println("INPUTS READINGS SUCCESSFUL : " + fileName + "\n");

        return matArr;
    }

    /*
     * There should be a single space that separates each element of a row. If
     * more than 2 spaces is found, then there will be an error.
     */
    public static Matrix readSemiColMat(String matNumericStr)
    {
        if (matNumericStr == null || "".equals(matNumericStr.trim()))
        {

            return new Matrix();
        }
        Matrix readMat = new Matrix();

        // Take of the square brackets if they exist
        int first = matNumericStr.indexOf('[');
        int second = matNumericStr.indexOf(']');
        if (first != -1)
        {
            if (second != -1)
            {
                matNumericStr = matNumericStr.substring(first + 1, second);
            }
            else
            {
                matNumericStr = matNumericStr.substring(first + 1);
            }
        }
        else
        {
            if (second != -1)
            {
                matNumericStr = matNumericStr.substring(0, second);
            }
            else
            {
                matNumericStr = matNumericStr + "";
            }
        }
        // -----------------------------------------

        String[] result = matNumericStr.split(";");
        int numRow = result.length;
        Indices eachRow = new Indices(1, numRow);
        ArrayList<double[]> db = new ArrayList<double[]>();

        for (int i = 0; i < numRow; i++)
        {
            String resultInd = result[i].trim();
            String[] elements = resultInd.split("\\s");

            int numEl = elements.length;
            eachRow.set(0, i, numEl);
            double[] thisRow = new double[numEl];
            for (int j = 0; j < numEl; j++)
            {
                try
                {
                    String elTrim = elements[j].trim();
                    if ("Inf".equals(elTrim))
                    {
                        thisRow[j] = Double.POSITIVE_INFINITY;
                    }
                    else if ("-Inf".equals(elTrim))
                    {
                        thisRow[j] = Double.NEGATIVE_INFINITY;
                    }
                    else if ("NaN".equals(elTrim))
                    {
                        thisRow[j] = Double.NEGATIVE_INFINITY;
                    }
                    else
                    {
                        thisRow[j] = Double.parseDouble(elTrim);
                    }
                }
                catch (NumberFormatException nfe)
                {
                    nfe.printStackTrace();
                }
            }
            db.add(thisRow);
        }

        if (numRow > 1)
        {
            eachRow = JDatafun.diff(eachRow);
            boolean eq = eachRow.EQ(0).allBoolean();
            if (!eq)
            {
                throw new ConditionalException("readSemiColMat : Matrix rows have unequal lengths.");
            }
        }

        // int numCol = eachRow.start();
        double[][] collect = new double[numRow][];
        for (int k = 0; k < db.size(); k++)
        {
            collect[k] = db.get(k);
        }

        readMat = new Matrix(collect);
        return readMat;
    }

    /**
     * 
     * @param input
     * @throws java.io.IOException
     * @return
     */
    public static Matrix read(BufferedReader input) throws java.io.IOException
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
            Double dbObj = null;
            if (ts.toLowerCase().contains(nanStr))
            {
                dbObj = new Double(Double.NaN);
            }
            else if (ts.toLowerCase().contains(infStr))
            {
                if (ts.charAt(0) == '-')
                {
                    dbObj = new Double(Double.NEGATIVE_INFINITY);
                }
                else
                {
                    dbObj = new Double(Double.POSITIVE_INFINITY);
                }
            }
            else
            {
                try
                {
                    dbObj = Double.parseDouble(ts);
                }
                catch (NumberFormatException nfe)
                {
                    throw nfe;
                }
                // throws NumberFormatException//Double.valueOf(ts);
            }
            v.addElement(dbObj); // Read & store 1st i.

        }
        while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);

        int n = v.size(); // Now we've got the number of columns!

        double row[] = new double[n];
        for (int j = 0; j < n; j++)
        { // extract the elements of the 1st i.
            row[j] = ((Double) v.elementAt(j)).doubleValue();
        }
        v.removeAllElements();
        v.addElement(row); // Start storing rows instead of columns.

        while (tokenizer.nextToken() == StreamTokenizer.TT_WORD)
        {
            // While non-empty lines
            v.addElement(row = new double[n]);
            int j = 0;
            do
            {
                if (j >= n)
                {
                    throw new java.io.IOException("Row " + v.size() + " is too long.");
                }
                String ts = tokenizer.sval;
                double dbObj = 0.0;
                if (ts.toLowerCase().contains(nanStr))
                {
                    dbObj = Double.NaN;
                }
                else if (ts.toLowerCase().contains(infStr))
                {
                    if (ts.charAt(0) == '-')
                    {
                        dbObj = Double.NEGATIVE_INFINITY;
                    }
                    else
                    {
                        dbObj = Double.POSITIVE_INFINITY;
                    }
                }
                else
                {
                    // dbObj = Double.valueOf(ts).doubleValue();
                    try
                    {
                        dbObj = Double.parseDouble(ts);
                    }
                    catch (NumberFormatException nfe)
                    {
                        throw nfe;
                    }
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

        double[][] A = new double[m][];
        v.copyInto(A); // copy the rows out of the vector

        return new Matrix(A);
    }

    public static Vector<Object> readCvsInput(BufferedReader input) throws java.io.IOException
    {
        StreamTokenizer tokenizer = new StreamTokenizer(input);

        // Although StreamTokenizer will parse numbers, it doesn't recognize
        // scientific notation (E OR D); however, Double.valueOf does.
        // The strategy here is to disable StreamTokenizer's number parsing.
        // We'll only get whitespace delimited words, EOL's AND EOF's.
        // These words should ALL be numbers, for Double.valueOf to parse.
        tokenizer.resetSyntax();
        tokenizer.wordChars(0, 255);
        tokenizer.whitespaceChars(0, ',');
        tokenizer.eolIsSignificant(true);

        Vector<Object> objectVector = new Vector<Object>();

        // Ignore initial empty lines
        while (tokenizer.nextToken() == StreamTokenizer.TT_EOL)
            ;
        if (tokenizer.ttype == StreamTokenizer.TT_EOF)
        {
            throw new java.io.IOException("Unexpected EOF on file read.");
        }
        do
        {
            objectVector.addElement(tokenizer.sval); // Read & store 1st i.

        }
        while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);

        return objectVector;
    }

    public Matrix ones()
    {
        return ones(1.0);
    }

    public Matrix ones(double val)
    {
        return new Matrix(m, n, val);
    }

    public Matrix zeros()
    {
        return new Matrix(m, n);
    }

    public boolean isSorted()
    {
        return sorted;
    }

    public void setSorted(boolean sorted)
    {
        this.sorted = sorted;
    }

    public MatrixFlt toMatrixFlt()
    {
        MatrixFlt MFL = new MatrixFlt(m, n);
        float[][] CM = MFL.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                CM[i][j] = (float) A[i][j];
            }
        }

        return MFL;
    }

    public Matrix scale(Number num)
    {
        return this.arrayTimes(num);
    }

    public Matrix reciprocate(int val)
    {
        return reciprocate((double) val);
    }

    public Matrix reciprocate(double val)
    {
        Matrix M = new Matrix(m, n);

        double[][] arr = M.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                arr[i][j] = val / A[i][j];
            }
        }
        return M;
    }

    public Matrix reciprocate()
    {
        return reciprocate(1.0);
    }

    public void setInfTo(double val)
    {
        Indices indInf = this.isnan();
        boolean inf = indInf.anyBoolean();
        if (inf)
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (!MathUtil.isfinite(A[i][j]))
                    {
                        A[i][j] = val;
                    }
                }
            }
        }

    }

    public Matrix blkdiagOld(Object... varargin)
    {
        if (varargin == null || varargin.length == 0)
        {
            return this.copy();
        }

        int len = varargin.length;
        // check if elements are only instances of "Matrix" and "Indices".
        for (int i = 0; i < len; i++)
        {
            boolean cond = !(varargin[i] instanceof Matrix) && !(varargin[i] instanceof Indices);
            if (cond)
            {
                throw new ConditionalRuleException("blkdiag",
                        "Elements of array \"varargin\" must be instances of \"Matrix\" or \"Indices\".");
            }
        }

        int row = 0;
        int col = 0;
        for (int i = 0; i < len; i++)
        {
            if (varargin[i] != null)
            {
                if (varargin[i] instanceof Matrix)
                {
                    Matrix mat = (Matrix) varargin[i];
                    if (!mat.isNull())
                    {
                        row += mat.getRowDimension();
                        col += mat.getColumnDimension();
                    }
                }
                else
                {
                    Indices ind = (Indices) varargin[i];
                    if (!ind.isNull())
                    {
                        row += ind.getRowDimension();
                        col += ind.getColumnDimension();
                    }
                }
            }
        }

        if ((row + m) == 0 || (col + n) == 0)
        {
            return new Matrix(null, row + m, col + n);
        }

        // collect all matrices to be blockwise concatenated
        ArrayList<Object> list = new ArrayList<Object>();
        if (!this.isNull())
        {
            list.add(this.copy());
        }

        for (int i = 0; i < len; i++)
        {
            if (varargin[i] != null)
            {
                if (varargin[i] instanceof Matrix)
                {
                    Matrix mat = (Matrix) varargin[i];
                    if (!mat.isNull())
                    {
                        // row += mat.getRowDimension();
                        // col += mat.getColumnDimension();
                        list.add(mat);
                    }
                }
                else
                {
                    Indices ind = (Indices) varargin[i];
                    if (!ind.isNull())
                    {
                        // row += ind.getRowDimension();
                        // col += ind.getColumnDimension();
                        list.add(ind);
                    }
                }
            }
        }

        Matrix concat = new Matrix(row + m, col + n);

        int rowStart = 0;
        int rowEnd = 0;

        int colStart = 0;
        int colEnd = 0;

        int siz = list.size();
        for (int i = 0; i < siz; i++)
        {
            Object obj = list.get(i);
            if (obj instanceof Matrix)
            {
                Matrix mat = (Matrix) obj;
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
            }
            else
            {
                Indices mat = (Indices) obj;
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
            }

            concat.setMatrix(rowStart, rowEnd, colStart, colEnd, obj);
        }// end for

        return concat;
    }

    public Matrix blkdiag(Object... varargin)
    {
        if (varargin == null || varargin.length == 0)
        {
            return this.copy();
        }

        int len = varargin.length;
        // check if elements are only instances of "Matrix" and "Indices".
        for (int i = 0; i < len; i++)
        {
            boolean cond = !(varargin[i] instanceof Matrix) && !(varargin[i] instanceof Indices);
            if (cond)
            {
                throw new ConditionalRuleException("blkdiag",
                        "Elements of array \"varargin\" must be instances of \"Matrix\" or \"Indices\".");
            }
        }

        int row = 0;
        int col = 0;
        for (int i = 0; i < len; i++)
        {
            if (varargin[i] != null)
            {
                if (varargin[i] instanceof Matrix)
                {
                    Matrix mat = (Matrix) varargin[i];
                    // if (!mat.isNull()) {
                    row += mat.getRowDimension();
                    col += mat.getColumnDimension();
                    // }
                }
                else
                {
                    Indices ind = (Indices) varargin[i];
                    // if (!ind.isNull()) {
                    row += ind.getRowDimension();
                    col += ind.getColumnDimension();
                    // }
                }
            }
        }

        if ((row + m) == 0 || (col + n) == 0)
        {
            return new Matrix(null, row + m, col + n);
        }

        // collect all matrices to be blockwise concatenated
        ArrayList<Object> list = new ArrayList<Object>();
        // if (!this.isNull()) {
        list.add(this.copy());
        // }

        for (int i = 0; i < len; i++)
        {
            if (varargin[i] != null)
            {
                if (varargin[i] instanceof Matrix)
                {
                    Matrix mat = (Matrix) varargin[i];
                    // if (!mat.isNull()) {
                    // row += mat.getRowDimension();
                    // col += mat.getColumnDimension();
                    list.add(mat);
                    // }
                }
                else
                {
                    Indices ind = (Indices) varargin[i];
                    // if (!ind.isNull()) {
                    // row += ind.getRowDimension();
                    // col += ind.getColumnDimension();
                    list.add(ind);
                    // }
                }
            }
        }

        Matrix concat = new Matrix(row + m, col + n);

        int rowStart = 0;
        int rowEnd = 0;

        int colStart = 0;
        int colEnd = 0;

        int siz = list.size();
        for (int i = 0; i < siz; i++)
        {
            Object obj = list.get(i);
            boolean condNull = false;
            if (obj instanceof Matrix)
            {
                Matrix mat = (Matrix) obj;
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
                condNull = mat.isNull();
            }
            else
            {
                Indices mat = (Indices) obj;
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
                condNull = mat.isNull();
            }

            if (!condNull)
            {
                concat.setMatrix(rowStart, rowEnd, colStart, colEnd, obj);
            }
        }// end for

        return concat;
    }

    public static Matrix randn(int rows, int cols)
    {
        Matrix rand = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                rand.set(i, j, MathUtil.randn());
            }
        }
        return rand;
    }

    public static Matrix randn(int sqRows)
    {
        return randn(sqRows, sqRows);
    }

    public static Matrix mergeH(List<Matrix> matList)
    {

        int siz = matList.size();
        if (siz == 1)
        {
            return matList.get(0);
        }

        Matrix mat1 = matList.get(0);
        Object[] obj = new Object[siz - 1];
        for (int i = 1; i < siz; i++)
        {
            obj[i - 1] = matList.get(i);
        }

        mat1 = mat1.mergeHoriz(obj);

        return mat1;
    }

    public static Matrix mergeV(List<Matrix> matList)
    {

        int siz = matList.size();
        if (siz == 1)
        {
            return matList.get(0);
        }

        Matrix mat1 = matList.get(0);
        Object[] obj = new Object[siz - 1];
        for (int i = 1; i < siz; i++)
        {
            obj[i - 1] = matList.get(i);
        }

        mat1 = mat1.mergeVerti(obj);// .mergeHoriz(obj);

        return mat1;
    }

    public static Object listNum2Mat(List<Number> list)
    {
        if (list == null || list.isEmpty())
        {
            throw new ConditionalException("arrayList2Mat : ArrayList object \"list\" is found to be null or empty.");
        }

        Number num = list.get(0);

        int siz = list.size();
        Object mat = null;

        if (num instanceof Double)
        {
            mat = new Matrix(1, siz);
            for (int i = 0; i < siz; i++)
            {
                ((Matrix) mat).set(0, i, list.get(i).doubleValue());
            }
        }
        else if (num instanceof Integer)
        {
            mat = new Indices(1, siz);
            for (int i = 0; i < siz; i++)
            {
                ((Indices) mat).set(0, i, list.get(i).intValue());
            }
        }
        else if (num instanceof Float)
        {
            mat = new MatrixFlt(1, siz);
            for (int i = 0; i < siz; i++)
            {
                ((MatrixFlt) mat).set(0, i, list.get(i).floatValue());
            }
        }
        else
        {
            throw new ConditionalException(
                    "listNum2Mat : List object \"list\" must be a collection of \"Double\", \"Integer\" or \"Float\".");
        }

        return mat;
    }

    /*
     * public static Matrix arrayList2Mat(List<Double> list) { if (list == null
     * || list.isEmpty()) { throw new ConditionalException(
     * "arrayList2Mat : ArrayList object \"list\" is found to be null or empty."
     * ); }
     * 
     * int siz = list.size(); Matrix mat = new Matrix(1, siz); double val = 0.0;
     * for (int i = 0; i < siz; i++) { val = list.get(i); mat.set(0, i, val); }
     * 
     * return mat; }
     */

    public static Matrix arrayList2Mat(List<? extends Number> list)
    {
        if (list == null || list.isEmpty())
        {
            throw new ConditionalException("arrayList2Mat : ArrayList object \"list\" is found to be null or empty.");
        }

        int siz = list.size();
        Matrix mat = new Matrix(1, siz);
        double val = 0.0;
        for (int i = 0; i < siz; i++)
        {
            val = list.get(i).doubleValue();
            mat.set(0, i, val);
        }

        return mat;
    }

    public static List<Double> numberArrayList2Double(List<Number> list)
    {
        if (list == null || list.isEmpty())
        {
            throw new ConditionalException("arrayList2Mat : ArrayList object \"list\" is found to be null or empty.");
        }

        int siz = list.size();
        // Matrix mat = new Matrix(1, siz);
        List<Double> dbList = new ArrayList<Double>();
        double val = 0.0;
        for (int i = 0; i < siz; i++)
        {
            val = list.get(i).doubleValue();
            // mat.set(0, i, val);
            dbList.add(val);
        }

        return dbList;
    }

    public static List<Matrix> collectionMatrixFromIndices(List<Matrix> list, int[] indices)
    {
        int numEl = indices.length;
        List<Matrix> listSub = new ArrayList<Matrix>();
        for (int i = 0; i < numEl; i++)
        {
            Matrix T = list.get(indices[i]);
            listSub.add(T);
        }
        return listSub;
    }

    public static List<String> collectionFromIndices(List<String> list, int[] indices)
    {
        int numEl = indices.length;
        List<String> listSub = new ArrayList<String>();
        for (int i = 0; i < numEl; i++)
        {
            String T = list.get(indices[i]);
            listSub.add(T);
        }
        return listSub;
    }

    /*
     * public Indices toIndices(){ Indices ind = new Indices(m,lenC); int[][] I
     * = new int[m][lenC]; for(int i=0; i<m; i++){ for(int j=0; j<lenC; j++){
     * I[i][j] = (int)A[i][j]; } } ind.setIndexValues(I); return ind; }
     * 
     * 
     * public int[] toRowIndices(){ int[] r = new int[m]; if(m==1) { r[0] = 0;
     * return r;} Matrix ind = linspace(0.0,(double)m,m-1); for(int i=0; i<m;
     * i++){ r[i] = (int)ind.get(0,i); } return r; }
     * 
     * 
     * public int[] toColIndices(){ int[] c = new int[lenC]; if(lenC==1) { c[0]
     * = 0; return c;} Matrix ind = linspace(0.0,(double)lenC,lenC-1); for(int
     * i=0; i<lenC; i++){ c[i] = (int)ind.get(0,i); } return c; }
     */
    // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(A);
        result = prime * result + m;
        result = prime * result + n;
        result = prime * result + (sorted ? 1231 : 1237);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Matrix other = (Matrix) obj;
        if (!Arrays.deepEquals(A, other.A))
            return false;
        if (m != other.m)
            return false;
        if (n != other.n)
            return false;
        if (sorted != other.sorted)
            return false;
        return true;
    }
    
    /**
     * 
     * @param args
     */
    public static void main(String[] args)
    {

        double[][] a =
        {
                {
                        2, 5, 4
                },
                {
                        -3, 9, 7
                }
        };
        Matrix A = new Matrix(a);
        A.printInLabel("A", 0);

        double[][] b =
        {
                {
                        2, 4, 0
                },
                {
                        -3, 7, 0
                }
        };
        Matrix B = new Matrix(b);
        B.printInLabel("B", 0);

        double[][] c =
        {
                {
                        -2, 4, 6
                },
                {
                        4, 7, -2
                }
        };
        Matrix C = new Matrix(c);
        C.printInLabel("C", 0);

        Matrix D = A.horzcat(B, C);
        D.printInLabel("D", 0);
        
        Matrix.rand(4, 6);
        //Math.r

        /*
         * //finargcat(Dimension dim, Matrix... argMat) Matrix Array1 =
         * Matrix.ones(2, 3); Matrix Array2 = new Matrix(1, 4, 2.0); Matrix
         * Array3 = new Matrix(3, 1, 3.0); Matrix Array =
         * Array1.finargcat(Dimension.COL, Array2, Array3);
         * Array.printInLabel("Array", 0);
         */

        /*
         * double[][] a = {{2, 5, 4}, {-3, 9, 7}}; Matrix A = new Matrix(a);
         * 
         * double[][] b = {{2, 4, 0}, {-3, 7, 0}}; Matrix B = new Matrix(b);
         * 
         * int[][] c = {{-5, -4, 15}}; Indices C = new Indices(c);
         * 
         * double[][] d = new double[][]{{-1, 0}, {0, -1}, {0, -1}};
         * 
         * Matrix D = null;//new Matrix(d);
         * 
         * String sione = null;//"Sione"; Object[] obj = {};
         * 
         * Matrix R = A.mergeVerti(B, sione, C, D);
         * 
         * R.printInLabel("R", 0);
         */

        /*
         * Matrix A = Matrix.linDecrement(12.0, 6.5, 1);
         * System.out.println("------- A --------"); A.print(8, 0);
         * 
         * Matrix A = new Matrix(new double[][]{{-1, 0, 0}, {0, -1, 0}, {0, 0,
         * -1}}); int[] arr = {0,1,2}; A = A.removeColsAt(arr);//
         * .removeRowsAt(arr);
         * 
         * System.out.println("------- A --------"); A.print(8, 0);
         * 
         * 
         * double[][] arr = {{2, 5, 4},{-3, 9, 7}}; Matrix mat = new
         * Matrix(arr); System.out.println("------- mat --------"); mat.print(8,
         * 0);
         * 
         * Matrix num = new Matrix(new double[][]{{-2},{-1}});
         * System.out.println("------- num --------"); num.print(8, 0);
         * 
         * 
         * Matrix A = mat.setXtendedColAt(0, num);
         * System.out.println("\n\n------- A --------"); A.print(8, 0);
         * 
         * Matrix B = mat.setXtendedColAt(1, num); System.out.println("------- B
         * --------"); B.print(8, 0);
         * 
         * Matrix C = mat.setXtendedColAt(2, num); System.out.println("------- C
         * --------"); C.print(8, 0);
         * 
         * Matrix D = mat.setXtendedColAt(5, num); System.out.println("------- D
         * --------"); D.print(8, 0);
         * 
         * double[] val = {-99.2654,
         * -107.5768,0,4.4375,100.0000,0,0,4.4375,0,104.4375}; Matrix M = new
         * Matrix(val,2); System.out.println("------- M --------"); M.print(8,
         * 4);
         * 
         * //Matrix (double vals[], int m)
         * 
         * 
         * boolean t = Double.NaN == Double.NaN; //System.out.println("------- t
         * = "+t+" --------");
         * 
         * double[][] A = {{Double.NaN, 3 , 9, Double.NEGATIVE_INFINITY}};
         * double[][] B = {{Double.NaN, -3 , 6, 2.0}};
         * 
         * Matrix a = new Matrix(A); Matrix b = new Matrix(B);
         * 
         * Indices aeqb = a.equalAll(b, 0.000001); System.out.println("-------
         * aeqb --------"); aeqb.print(4);
         * 
         * double[][] mat = new double[][]{{5, 2, 2, 2}, {1, 4, 0, 3}, {3, 4, 4,
         * 4}};
         * 
         * Matrix A = new Matrix(mat); //A = A.toRowVector(); //A =
         * A.toColVector(); Indices AindColwise = A.generateIndices(true);
         * Indices AindRowwise = A.generateIndices(false);
         * 
         * System.out.println("\lenC------- AindColwise --------");
         * AindColwise.print(4,0);
         * 
         * System.out.println("\lenC------- AindRowwise --------");
         * AindRowwise.print(4,0);
         * 
         * A = A.transpose(); System.out.println("\lenC------- A --------");
         * A.print(4,0);
         * 
         * int[] ind = {1,3};
         * 
         * A = A.removeRowAt(ind); System.out.println("\lenC------- A_removed
         * --------"); A.print(4,0);
         * 
         * 
         * 
         * Indices allA = A.ALL(Matrix.DIM_COL);
         * 
         * System.out.println("\lenC------- A --------"); A.print(4,0);
         * 
         * System.out.println("\lenC------- allA --------"); allA .print(4,0);
         * 
         * double[][] mat = {{ 1, 2, Double.NEGATIVE_INFINITY, 5}, {
         * Double.POSITIVE_INFINITY, 5, 5, Double.NEGATIVE_INFINITY}, { 5, 1, 4,
         * 6}};
         * 
         * Matrix rn = new
         * Matrix(mat);//Matrix.random(3,4).arrayTimesCP(10.0).round();
         * 
         * System.out.println("\lenC------- rn --------"); rn.print(4,0);
         * 
         * 
         * Object[] obj = rn.unique();
         * 
         * Matrix R = (Matrix)obj[0]; Indices I = (Indices)obj[1]; Matrix Imat =
         * Matrix.indicesToMatrix(I);
         * 
         * System.out.println("\lenC------- R --------"); R.print(4,0);
         * 
         * System.out.println("\lenC------- I --------"); Imat.print(4,0);
         */

        /*
         * Indices ind = rn.GT(6.0).find(); if(ind!=null){
         * System.out.println("\lenC------- ind --------");
         * Matrix.indicesToMatrix(ind).print(4,0); //rn.print(4,0); }
         * 
         * double[][] c = {{0, 2, 0, 5, 3, 0, 0}}; Matrix M = new Matrix(c);
         * ComplexMatrix cm = M.complexRoots(); if(cm!=null){ Matrix[] RI =
         * cm.getRealAndImaginaryMatrices(); Matrix mergeRI =
         * RI[0].mergeH(RI[1]);
         * 
         * System.out.println("\lenC------- RI[0] --------"); RI[0].print(4,6);
         * System.out.println("\lenC------- RI[1] --------"); RI[1].print(4,6);
         * 
         * System.out.println("\lenC------- mergeRI --------");
         * mergeRI.print(4,6); } else{ System.out.println("\lenC------- NULL (no
         * roots) --------"); }
         */
    }

    
}// --------------------------- End Class Definition
// ----------------------------

