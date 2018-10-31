package jamaextension.jamax;

import jamaextension.jamax.constants.Dimension;

public class ComplexMatrix
{

    private Complex[][] C;
    private int m;
    private int n;

    public ComplexMatrix(int m, int n)
    {
        /*----------- Old Block --------------
         * this.m = m;
        this.n = n;
        R = new Complex[m][n];
        for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
        R[i][j] = new Complex();
        }
        }
         *-------------------------------------*/
        this(new int[]
        {
                m, n
        });
    }

    public ComplexMatrix(int[] mn)
    {
        if (mn == null)
        {
            throw new ConditionalException("ComplexMatrix : Parameter \"mn\" must be non-null.");
        }
        if (mn.length != 2)
        {
            throw new ConditionalException("ComplexMatrix : Length of integer array parameter \"mn\" must be 2;");
        }
        Indices ind = new Indices(mn);
        if (ind.LTEQ(0).anyBoolean())
        {
            throw new ConditionalException(
                    "ComplexMatrix : Elements of integer array parameter \"mn\" must be positive.");
        }
        this.m = mn[0];
        this.n = mn[1];
        this.C = new Complex[mn[0]][mn[1]];
        for (int i = 0; i < this.m; i++)
        {
            for (int j = 0; j < this.n; j++)
            {
                this.C[i][j] = new Complex();
            }
        }
    }

    public ComplexMatrix(int m, int n, Complex s)
    {
        this.m = m;
        this.n = n;
        C = new Complex[m][n];
        if (s != null)
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    C[i][j] = s.copy();
                }
            }
        }
    }

    public ComplexMatrix(Complex[] A)
    {
        m = 1;
        n = A.length;
        C = new Complex[1][];
        C[0] = A;
    }

    public ComplexMatrix(Complex[][] A)
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
        this.C = A;
    }

    /**
     * Construct a matrix quickly without checking arguments.
     * 
     * @param A
     *            Two-dimensional array of doubles.
     * @param m
     *            Number of rows.
     * @param n
     */
    public ComplexMatrix(Complex[][] A, int m, int n)
    {
        this.C = A;

        boolean warn = m < 0 || n < 0;
        if (warn)
        {
            throw new ConditionalException("ComplexMatrix : Both integer parameters \"m\" (= " + m + ") and \"n\" (= "
                    + n + ") must be non-negative.");
        }

        this.m = m;
        this.n = n;
    }

    public ComplexMatrix(Matrix re)
    {
        int rowRe = re.getRowDimension();
        int colRe = re.getColumnDimension();

        this.m = rowRe;
        this.n = colRe;

        C = new Complex[rowRe][colRe];
        for (int i = 0; i < rowRe; i++)
        {
            for (int j = 0; j < colRe; j++)
            {
                C[i][j] = new Complex(re.get(i, j), 0.0);
            }
        }
    }

    public ComplexMatrix(double[] re, double[] im)
    {
        this(new Matrix(re), new Matrix(im));
    }

    public ComplexMatrix(double re, Matrix im)
    {
        this(new Matrix(im.getRowDimension(), im.getColumnDimension(), re), im);
    }

    public ComplexMatrix(Matrix re, double im)
    {
        this(re, new Matrix(re.getRowDimension(), re.getColumnDimension(), im));
    }

    public ComplexMatrix(Matrix re, Matrix im)
    {
        int rowRe = re.getRowDimension();
        int colRe = re.getColumnDimension();

        int rowIm = im.getRowDimension();
        int colIm = im.getColumnDimension();

        if (rowRe != rowIm || colRe != colIm)
        {
            throw new IllegalArgumentException(
                    "ComplexMatrix dimensions for matrix parameters \"re\" and \"im\" must agree.");
        }

        this.m = rowRe;
        this.n = colRe;

        C = new Complex[rowRe][colRe];
        for (int i = 0; i < rowRe; i++)
        {
            for (int j = 0; j < colRe; j++)
            {
                C[i][j] = new Complex(re.get(i, j), im.get(i, j));
            }
        }
    }

    public ComplexMatrix(Indices re, Indices im)
    {
        int rowRe = re.getRowDimension();
        int colRe = re.getColumnDimension();

        int rowIm = im.getRowDimension();
        int colIm = im.getColumnDimension();

        if (rowRe != rowIm || colRe != colIm)
        {
            throw new IllegalArgumentException(
                    "ComplexMatrix dimensions for matrix parameters \"re\" and \"im\" must agree.");
        }

        this.m = rowRe;
        this.n = colRe;

        C = new Complex[rowRe][colRe];
        for (int i = 0; i < rowRe; i++)
        {
            for (int j = 0; j < colRe; j++)
            {
                C[i][j] = new Complex((double) re.get(i, j), (double) im.get(i, j));
            }
        }
    }

    public ComplexMatrix(Indices re, Number im)
    {
        int rowRe = re.getRowDimension();
        int colRe = re.getColumnDimension();

        this.m = rowRe;
        this.n = colRe;

        C = new Complex[rowRe][colRe];
        for (int i = 0; i < rowRe; i++)
        {
            for (int j = 0; j < colRe; j++)
            {
                C[i][j] = new Complex((double) re.get(i, j), im.doubleValue());
            }
        }
    }

    public ComplexMatrix(Number re, Indices im)
    {
        int rowRe = im.getRowDimension();
        int colRe = im.getColumnDimension();

        this.m = rowRe;
        this.n = colRe;

        C = new Complex[rowRe][colRe];
        for (int i = 0; i < rowRe; i++)
        {
            for (int j = 0; j < colRe; j++)
            {
                C[i][j] = new Complex(re.doubleValue(), (double) im.get(i, j));
            }
        }
    }

    /*
     * public Matrix getReal(){ Matrix Re = new Matrix(m,n); double[][] real =
     * Re.getArray(); for(int i=0; i<m; i++){ for(int j=0; j<n; j++){
     * //complex[i][j] = new Complex(); real[i][j] = complex[i][j].re(); } }
     * return Re; }
     * 
     * public Matrix getImag(){ Matrix Re = new Matrix(m,n); double[][] imag =
     * Re.getArray(); for(int i=0; i<m; i++){ for(int j=0; j<n; j++){
     * //complex[i][j] = new Complex(); imag[i][j] = complex[i][j].im(); } }
     * return Re; }
     */
    /**
     * 
     * @param indices
     *            int[]
     * @return Matrix
     */
    public ComplexMatrix getRows(int[] indices)
    {
        int len = indices.length;
        ComplexMatrix val = new ComplexMatrix(len, n);
        for (int i = 0; i < len; i++)
        {
            if (indices[i] > (m - 1) || indices[i] < 0)
            {
                throw new IllegalArgumentException(" getRows: Array out-of-bounds.");
            }
            ComplexMatrix temp = this.getMatrix(indices[i], indices[i], 0, n - 1);
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
    public ComplexMatrix getRowAt(int rowIndex)
    {
        return getRows(new int[]
        {
            rowIndex
        });
    }

    /**
     * Get row dimension.
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
     * @return n, the number of columns.
     */
    public int getColumnDimension()
    {
        return n;
    }

    /** Check if size(A) == size(B) **/
    private void checkComplexMatrixDimensions(ComplexMatrix B)
    {
        if (B.m != m || B.n != n)
        {
            throw new IllegalArgumentException("ComplexMatrix dimensions must agree.");
        }
    }

    private void checkComplexMatrixDimensions(Matrix B)
    {
        if (B.getRowDimension() != m || B.getColumnDimension() != n)
        {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
    }

    public Matrix abs()
    {
        Matrix M = new Matrix(m, n);
        double[][] R = M.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                R[i][j] = Complex.abs(C[i][j]);
            }
        }
        return M;
    }

    public Matrix arg()
    {
        Matrix M = new Matrix(m, n);
        double[][] R = M.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                R[i][j] = Complex.arg(C[i][j]);
            }
        }
        return M;
    }

    public ComplexMatrix conjugate()
    {
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].conj();
            }
        }
        return X;
    }

    public ComplexMatrix copy()
    {
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].copy();
            }
        }
        return X;
    }

    public boolean equals(ComplexMatrix B)
    {
        return equals(B, MathUtil.EPS);
    }

    public boolean equals(ComplexMatrix B, double tol)
    {
        checkComplexMatrixDimensions(B);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (!C[i][j].equals(B.C[i][j], tol))
                {
                    return false;
                }
            }
        }
        return true;
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
    public ComplexMatrix flipLR()
    {
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] R = X.getArray();
        for (int i = 0; i < m; i++)
        {
            R[i] = MathUtil.flip(C[i], true);
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
    public ComplexMatrix flipUD()
    {
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] R = X.getArray();
        Complex[][] temp = this.getArrayCopy();
        Complex[] D = null;
        for (int i = 0; i < m; i++)
        {
            D = temp[(m - 1) - i];
            for (int j = 0; j < n; j++)
            {
                R[i][j] = D[j];
            }// end for

        }// end for

        return X;
    }

    public Complex[][] getArray()
    {
        return C;
    }

    public boolean isNull()
    {
        return this.C == null;
    }

    public Complex start()
    {
        return C[0][0].copy();
    }

    public Complex[][] getArrayCopy()
    {
        Complex[][] cp = new Complex[m][n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                cp[i][j] = C[i][j].copy();
            }
        }
        return cp;
    }

    /**
     * 
     * @param indices
     *            int[]
     * @return Matrix
     */
    public ComplexMatrix getColumns(int[] indices)
    {
        int len = indices.length;
        ComplexMatrix val = new ComplexMatrix(m, len);
        for (int i = 0; i < len; i++)
        {
            if (indices[i] > (n - 1) || indices[i] < 0)
            {
                throw new IllegalArgumentException(" getColumns: Array out-of-bounds.");
            }
            ComplexMatrix temp = this.getMatrix(0, m - 1, indices[i], indices[i]);
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
    public ComplexMatrix getColumnAt(int colIndex)
    {
        return getColumns(new int[]
        {
            colIndex
        });
    }

    public ComplexMatrix getEls(int from, int to)
    {
        if (from > to)
        {
            throw new ConditionalException("getEls : Value for integer parameter \"from\" (= " + from
                    + ") must \nequal or less than that of \"to\" (= " + to + ") ;");
        }
        int[] ind = Indices.linspace(from, to).getRowPackedCopy();
        ComplexMatrix R = null;
        R = this.getEls(ind);
        return R;
    }

    public ComplexMatrix getEls(int[] ind)
    {
        int len = ind.length;
        ComplexMatrix R = new ComplexMatrix(len, 1);
        Complex val = null;

        if (this.isRowVector())
        {
            R = new ComplexMatrix(1, len);
            for (int i = 0; i < len; i++)
            {
                val = this.getElementAt(ind[i]);
                R.set(0, i, val);
            }
        }
        else
        {
            R = new ComplexMatrix(len, 1);
            for (int i = 0; i < len; i++)
            {
                val = this.getElementAt(ind[i]);
                R.set(i, 0, val);
            }
        }

        return R;
    }

    public int numel()
    {
        return m * n;
    }

    public void setElements(int[] ind, Complex val)
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

    /**
     * 
     * @param ind
     * @param val
     */
    public void setElements(int[] ind, ComplexMatrix val)
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
        ComplexMatrix rowMat = null;
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
    public void setElementAt(int ind, Complex val)
    {
        int count = 0;
        int i = ind;
        if (ind >= m * n)
        {
            throw new IllegalArgumentException("setElementAt: ArrayIndex is out-of-bound.");
        }
        if (val == null)
        {
            throw new IllegalArgumentException("setElementAt: Parameter \"val\" must be non-null.");
        }
        if (i < m)
        {
            C[ind][0] = val.copy();
        }
        else
        {
            while (m <= i)
            {
                i = i - m;
                count++;
            }
            C[i][count] = val.copy();
        }
    }// end method

    /**
     * 
     * @param ind
     * @return
     */
    public ComplexMatrix getElements(int[] ind)
    {
        int len = ind.length;
        ComplexMatrix R = new ComplexMatrix(len, 1);
        for (int i = 0; i < len; i++)
        {
            Complex val = getElementAt(ind[i]);
            R.set(i, 0, val);
        }
        return R;
    }

    /**
     * Retrieve an element as an index from column-like vector reference
     */
    public Complex getElementAt(int ind)
    {
        Complex val = null;
        int count = 0;
        int i = ind;
        if (ind >= m * n)
        {
            throw new IllegalArgumentException("getElementAt : ArrayIndex is out-of-bound.");
        }
        if (i < m)
        {
            val = C[ind][0].copy();
        }
        else
        {
            while (m <= i)
            {
                i = i - m;
                count++;
            }
            val = C[i][count].copy();
        }

        return val;
    }// end method.

    // ////////////////////////////////////////////////////////////////////
    /**
     * Get a submatrix.
     * 
     * @param i0
     *            Initial row index
     * @param i1
     *            Final row index
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @return A(i0:i1,j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public ComplexMatrix getMatrix(int i0, int i1, int j0, int j1)
    {
        ComplexMatrix X = new ComplexMatrix(i1 - i0 + 1, j1 - j0 + 1);
        Complex[][] B = X.getArray();
        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    B[i - i0][j - j0] = C[i][j].copy();
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
     *            Array of row indices.
     * @param c
     *            Array of column indices.
     * @return A(r(:),c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public ComplexMatrix getMatrix(int[] r, int[] c)
    {
        ComplexMatrix X = new ComplexMatrix(r.length, c.length);
        Complex[][] B = X.getArray();
        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    B[i][j] = C[r[i]][c[j]].copy();
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
     *            Initial row index
     * @param i1
     *            Final row index
     * @param c
     *            Array of column indices.
     * @return A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public ComplexMatrix getMatrix(int i0, int i1, int[] c)
    {
        ComplexMatrix X = new ComplexMatrix(i1 - i0 + 1, c.length);
        Complex[][] B = X.getArray();
        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    B[i - i0][j] = C[i][c[j]].copy();
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
     *            Initial row index
     * @param i1
     *            Final row index
     * @param c
     *            Array of column indices.
     * @return A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public ComplexMatrix getMatrix(int i0, int[] c)
    {
        return getMatrix(i0, i0, c);
    }

    /**
     * Get a submatrix.
     * 
     * @param r
     *            Array of row indices.
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @return A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public ComplexMatrix getMatrix(int[] r, int j0, int j1)
    {
        ComplexMatrix X = new ComplexMatrix(r.length, j1 - j0 + 1);
        Complex[][] B = X.getArray();
        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    B[i][j - j0] = C[r[i]][j].copy();
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
     *            Array of row indices.
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @return A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public ComplexMatrix getMatrix(int[] r, int j0)
    {
        return getMatrix(r, j0, j0);
    }

    public ComplexMatrix plus(Object B)
    {
        if (B == null)
        {
            throw new ConditionalException("plus : Object parameter \"B\" must be non-null.");
        }
        if (!(B instanceof Matrix) && !(B instanceof ComplexMatrix) && !(B instanceof Indices)
                && !(B instanceof Complex) && !(B instanceof Double) && !(B instanceof Integer))
        {
            throw new ConditionalException(
                    "plus : Object parameter \"B\" must be and instance of \"Matrix\", \"ComplexMatrix\", \"Indices\", \"Complex\", \"Double\" and \"Integer\".");
        }

        ComplexMatrix bCM = null;
        if (B instanceof Matrix)
        {
            bCM = new ComplexMatrix((Matrix) B);
        }
        else if (B instanceof ComplexMatrix)
        {
            bCM = (ComplexMatrix) B;
        }
        else if (B instanceof Indices)
        {
            bCM = new ComplexMatrix(Matrix.indicesToMatrix((Indices) B));
        }
        else if (B instanceof Complex)
        {
            bCM = new ComplexMatrix(this.m, this.n, (Complex) B);
        }
        else if (B instanceof Double)
        {
            bCM = new ComplexMatrix(this.m, this.n, new Complex(((Double) B).doubleValue(), 0.0));
        }
        else
        {
            bCM = new ComplexMatrix(this.m, this.n, new Complex(((Integer) B).doubleValue(), 0.0));
        }

        return plusCP(bCM);
    }

    // ////////////////////////////////////////////////////////////////////
    private ComplexMatrix plusCP(ComplexMatrix B)
    {
        checkComplexMatrixDimensions(B);
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].add(B.C[i][j]);
            }
        }
        return X;
    }

    /**
     * Element-by-element addition of complex array to a single complex number,
     * complex array = A+B
     * 
     * @param B
     *            another complex number
     * @return A+B
     * 
     * @deprecated Use the method <B>plus(Object B)</B>, instead.
     * 
     */
    public ComplexMatrix plus(Complex B)
    {
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].add(B);
            }
        }
        return X;
    }

    /**
     * Element-by-element addition of complex array to a single real number,
     * complex array = A+B
     * 
     * @param B
     *            another real number
     * @return A+B
     * 
     * @deprecated Use the method <B>plus(Object B)</B>, instead.
     * 
     */
    public ComplexMatrix plus(double b)
    {
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].add(new Complex(b));
            }
        }
        return X;
    }

    public ComplexMatrix arrayTimes(Object B)
    {
        if (B == null)
        {
            throw new ConditionalException("arrayTimes : Object parameter \"B\" must be non-null.");
        }
        if (!(B instanceof Matrix) && !(B instanceof ComplexMatrix) && !(B instanceof Indices)
                && !(B instanceof Complex) && !(B instanceof Number))
        {
            throw new ConditionalException(
                    "arrayTimes : Object parameter \"B\" must be and instance of \"Matrix\", \"ComplexMatrix\", \"Indices\", \"Complex\", and \"Number\".");
        }

        ComplexMatrix bCM = null;
        if (B instanceof Matrix)
        {
            bCM = new ComplexMatrix((Matrix) B);
        }
        else if (B instanceof ComplexMatrix)
        {
            bCM = (ComplexMatrix) B;
        }
        else if (B instanceof Indices)
        {
            bCM = new ComplexMatrix(Matrix.indicesToMatrix((Indices) B));
        }
        else if (B instanceof Complex)
        {
            bCM = new ComplexMatrix(this.m, this.n, (Complex) B);
        }
        else if (B instanceof Number)
        {
            bCM = new ComplexMatrix(this.m, this.n, new Complex(((Number) B).doubleValue(), 0.0));
        } // else {
          // bCM = new ComplexMatrix(this.m, this.n, new Complex(((Integer)
          // B).doubleValue(), 0.0));
          // }

        return arrayTimesCP(bCM);
    }

    /**
     * Element-by-element multiplication, complex = A.*B
     * 
     * @param B
     *            another matrix
     * @return A.*B
     */
    private ComplexMatrix arrayTimesCP(ComplexMatrix B)
    {
        checkComplexMatrixDimensions(B);
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].mul(B.C[i][j]);
            }
        }
        return X;
    }

    /**
     * Element-by-element multiplication, complex = A.*B
     * 
     * @param B
     *            another matrix
     * @return A.*B
     * 
     * @deprecated Use the method <B>arrayTimesCP(Object B)</B>, instead.
     * 
     */
    public ComplexMatrix arrayTimes(Matrix B)
    {
        checkComplexMatrixDimensions(B);
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].mul(B.get(i, j));
            }
        }
        return X;
    }

    /**
     * Element-by-element multiplication by a complex,ie, complex = A.*B
     * 
     * @param B
     *            another matrix
     * @return A.*B
     * 
     * @deprecated Use the method <B>arrayTimesCP(Object B)</B>, instead.
     * 
     */
    public ComplexMatrix arrayTimes(Complex B)
    {
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].mul(B);
            }
        }
        return X;
    }

    /**
     * Element-by-element multiplication by a double, complex = A.*B
     * 
     * @param B
     *            another matrix
     * @return A.*B
     * 
     * @deprecated Use the method <B>arrayDivideCP(Object B)</B>, instead.
     * 
     */
    public ComplexMatrix arrayTimes(double B)
    {
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].mul(B);
            }
        }
        return X;
    }

    public ComplexMatrix uminus()
    {
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].neg();
            }
        }
        return X;
    }

    public Matrix norm2()
    {
        Matrix M = new Matrix(m, n);
        double[][] R = M.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                R[i][j] = Complex.norm(C[i][j]);
            }
        }
        return M;
    }

    public ComplexMatrix minus(Object B)
    {
        if (B == null)
        {
            throw new ConditionalException("minus : Object parameter \"B\" must be non-null.");
        }
        if (!(B instanceof Matrix) && !(B instanceof ComplexMatrix) && !(B instanceof Indices)
                && !(B instanceof Complex) && !(B instanceof Double) && !(B instanceof Integer))
        {
            throw new ConditionalException(
                    "minus : Object parameter \"B\" must be and instance of \"Matrix\", \"ComplexMatrix\", \"Indices\", \"Complex\", \"Double\" and \"Integer\".");
        }

        ComplexMatrix bCM = null;
        if (B instanceof Matrix)
        {
            bCM = new ComplexMatrix((Matrix) B);
        }
        else if (B instanceof ComplexMatrix)
        {
            bCM = (ComplexMatrix) B;
        }
        else if (B instanceof Indices)
        {
            bCM = new ComplexMatrix(Matrix.indicesToMatrix((Indices) B));
        }
        else if (B instanceof Complex)
        {
            bCM = new ComplexMatrix(this.m, this.n, (Complex) B);
        }
        else if (B instanceof Double)
        {
            bCM = new ComplexMatrix(this.m, this.n, new Complex(((Double) B).doubleValue(), 0.0));
        }
        else
        {
            bCM = new ComplexMatrix(this.m, this.n, new Complex(((Integer) B).doubleValue(), 0.0));
        }

        return minusCP(bCM);
    }

    private ComplexMatrix minusCP(ComplexMatrix B)
    {
        checkComplexMatrixDimensions(B);
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].sub(B.C[i][j]);
            }
        }
        return X;
    }

    /**
     * Element-by-element subtraction of complex array to a single complex
     * number, complex array = A-B
     * 
     * @param B
     *            another complex number
     * @return A-B
     * 
     * @deprecated Use the method <B>minus(Object B)</B>, instead.
     * 
     */
    public ComplexMatrix minus(Complex B)
    {
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].sub(B);
            }
        }
        return X;
    }

    /**
     * Element-by-element subtraction of complex array to a real number, complex
     * array = A-B
     * 
     * @param B
     *            a real number
     * @return A-B
     * 
     * @deprecated Use the method <B>minus(Object B)</B>, instead.
     * 
     */
    public ComplexMatrix minus(double B)
    {
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].sub(B);
            }
        }
        return X;
    }

    private ComplexMatrix arrayDivideCP(ComplexMatrix B)
    {
        checkComplexMatrixDimensions(B);
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].div(B.C[i][j]);
            }
        }
        return X;
    }

    public ComplexMatrix arrayDivide(Object B)
    {
        if (B == null)
        {
            throw new ConditionalException("arrayDivide : Object parameter \"B\" must be non-null.");
        }
        if (!(B instanceof Matrix) && !(B instanceof ComplexMatrix) && !(B instanceof Indices)
                && !(B instanceof Complex) && !(B instanceof Double) && !(B instanceof Integer))
        {
            throw new ConditionalException(
                    "arrayDivide : Object parameter \"B\" must be and instance of \"Matrix\", \"ComplexMatrix\", \"Indices\", \"Complex\", \"Double\" and \"Integer\".");
        }

        ComplexMatrix bCM = null;
        if (B instanceof Matrix)
        {
            bCM = new ComplexMatrix((Matrix) B);
        }
        else if (B instanceof ComplexMatrix)
        {
            bCM = (ComplexMatrix) B;
        }
        else if (B instanceof Indices)
        {
            bCM = new ComplexMatrix(Matrix.indicesToMatrix((Indices) B));
        }
        else if (B instanceof Complex)
        {
            bCM = new ComplexMatrix(this.m, this.n, (Complex) B);
        }
        else if (B instanceof Double)
        {
            bCM = new ComplexMatrix(this.m, this.n, new Complex(((Double) B).doubleValue(), 0.0));
        }
        else
        {
            bCM = new ComplexMatrix(this.m, this.n, new Complex(((Integer) B).doubleValue(), 0.0));
        }

        return arrayDivideCP(bCM);

    }

    /**
     * Element-by-element division, complex = A.*B
     * 
     * @param B
     *            another matrix
     * @return A.*B
     * 
     * @deprecated Use the method <B>arrayDivideCP(Object B)</B>, instead.
     * 
     */
    public ComplexMatrix arrayDivide(Complex B)
    {
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].div(B);
            }
        }
        return X;
    }

    /**
     * Element-by-element multiplication, complex = A.*B
     * 
     * @param B
     *            another matrix
     * @return A.*B
     * 
     * @deprecated Use the method <B>arrayDivideCP(Object B)</B>, instead.
     * 
     */
    public ComplexMatrix arrayDivide(double B)
    {
        ComplexMatrix X = new ComplexMatrix(m, n);
        Complex[][] A = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = C[i][j].div(B);
            }
        }
        return X;
    }

    public Matrix re()
    {
        Matrix M = new Matrix(m, n);
        double[][] R = M.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                R[i][j] = C[i][j].re();
            }
        }
        return M;
    }

    public Matrix im()
    {
        Matrix M = new Matrix(m, n);
        double[][] R = M.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                R[i][j] = C[i][j].im();
            }
        }
        return M;
    }

    public void setRe(Matrix real)
    {
        checkComplexMatrixDimensions(real);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j].setRe(real.get(i, j));
            }
        }

    }

    public void setIm(Matrix imag)
    {
        checkComplexMatrixDimensions(imag);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j].setIm(imag.get(i, j));
            }
        }
    }

    /**
     * 
     * @param fromFind
     * @param val
     */
    public void setFromFind(Indices fromFind, Complex val)
    {
        if (fromFind == null)
        {
            return;
        }
        int len = fromFind.getRowDimension();
        ComplexMatrix mat = new ComplexMatrix(1, len, val);
        setFromFind(fromFind, mat);
    }

    /**
     * 
     * @param fromFind
     * @param values
     */
    public void setFromFind(Indices fromFind, ComplexMatrix values)
    {
        if (fromFind == null)
        {
            return;
        }

        if (values.isVector() == false)
        {
            throw new IllegalArgumentException("setFromFind : Parameter 'values' must be a row or column vector.");
        }

        if (!fromFind.isFindIndex())
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

        // Complex val = null;
        int i, j;
        if (values.isRowVector())
        {
            for (int k = 0; k < indRowLen; k++)
            {
                i = fromFind.get(k, 0);
                j = fromFind.get(k, 1);
                Complex val = values.get(0, k).copy();
                set(i, j, val);
            }
        }
        else
        {
            for (int k = 0; k < indRowLen; k++)
            {
                i = fromFind.get(k, 0);
                j = fromFind.get(k, 1);
                Complex val = values.get(k, 0).copy();
                set(i, j, val);
            }
        }

    }

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

    public void setZ(Matrix real, Matrix imag)
    {
        checkComplexMatrixDimensions(real);
        checkComplexMatrixDimensions(imag);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[i][j].setZ(real.get(i, j), imag.get(i, j));
            }
        }
    }

    public ComplexMatrix reciprocate(Complex scale)
    {
        ComplexMatrix R = new ComplexMatrix(m, n);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Complex recip = C[i][j].reciprocate(scale);
                R.set(i, j, recip);
            }
        }
        return R;
    }

    public ComplexMatrix reciprocate(double scale)
    {
        ComplexMatrix R = new ComplexMatrix(m, n);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Complex recip = C[i][j].reciprocate(scale);
                R.set(i, j, recip);
            }
        }
        return R;
    }

    public ComplexMatrix reciprocate()
    {
        ComplexMatrix R = new ComplexMatrix(m, n);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Complex recip = C[i][j].reciprocate();
                R.set(i, j, recip);
            }
        }
        return R;
    }

    /**
     * Tile this matrix into an m-rows by lenC-columns pattern
     * 
     * @param r
     *            number of row patterns
     * @param c
     *            number of column patterns. return Matrix from tiling A[][]
     *            into r-rows AND c-columns tile pattern.
     */
    public ComplexMatrix repmat(int r, int c)
    {
        if (r == 1 && c == 1)
        {
            return copy();
        }
        Complex[][] X = getArrayCopy();
        int countRow = 0, countColumn = 0;
        // double[] tempHolder;
        if (r < 1 || c < 1)
        {
            throw new ArrayIndexOutOfBoundsException("repmat : Array index is out-of-bound.");
        }
        int newRowDim = m * r;
        int newColDim = n * c;
        Complex[][] result = new Complex[newRowDim][];
        Complex[] tempHolder = new Complex[newColDim];

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
            tempHolder = new Complex[newColDim];
        }// end for

        return new ComplexMatrix(result);
    }// end method

    /**
     * 
     * @param B
     *            Matrix
     * @return Matrix
     *         <p>
     *         Merge this matrix and matrix 'B' horizontally to form a larger
     *         matrix. Row size must agree.
     *         </p>
     */
    public ComplexMatrix mergeH(ComplexMatrix B)
    {
        if (m != B.m)
        {
            throw new IllegalArgumentException(" mergeH : ComplexMatrix row dimensions must agree.");
        }

        int newCol = n + B.n;
        ComplexMatrix R = new ComplexMatrix(m, newCol);
        R.setMatrix(0, m - 1, 0, n - 1, this.copy());
        R.setMatrix(0, m - 1, n, n + B.n - 1, B);
        return R;
    }

    /**
     * 
     * @param B
     *            Matrix
     * @return Matrix
     *         <p>
     *         Merge this matrix and matrix 'B' vertically to form a larger
     *         matrix. Column size must agree.
     *         </p>
     */
    public ComplexMatrix mergeV(ComplexMatrix B)
    {
        if (n != B.n)
        {
            throw new IllegalArgumentException(" mergeV : ComplexMatrix column dimensions must agree.");
        }
        int newRow = m + B.m;
        ComplexMatrix R = new ComplexMatrix(newRow, n);
        R.setMatrix(0, m - 1, 0, n - 1, this.copy());
        R.setMatrix(m, m + B.m - 1, 0, n - 1, B);
        return R;
    }

    /**
     * 
     * @param indices
     *            int[]
     * @param matColumns
     *            Matrix
     */
    public void setColumns(int[] indices, ComplexMatrix matColumns)
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
            ComplexMatrix temp = matColumns.getColumnAt(i);
            setMatrix(0, m - 1, indices[i], indices[i], temp);
        }
    }

    /**
     * 
     * @param indices
     * @param valColumns
     */
    public void setColumns(int[] indices, Complex valColumns)
    {
        int len = indices.length;
        ComplexMatrix matColumns = new ComplexMatrix(m, len, valColumns);
        setColumns(indices, matColumns);
    }

    /**
     * 
     * @param index
     *            int
     * @param matColumns
     *            Matrix
     */
    public void setColumnAt(int index, ComplexMatrix matColumns)
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
    public void setColumnAt(int index, Complex valColumns)
    {
        setColumns(new int[]
        {
            index
        }, valColumns);
    }

    public void set(int i, int j, Complex s)
    {
        C[i][j] = s;
    }

    // ////////////////////////////////////////////////////////////////////////////
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
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int i0, int i1, int j0, int j1, ComplexMatrix X)
    {
        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    C[i][j] = X.get(i - i0, j - j0);
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
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int[] r, int[] c, ComplexMatrix X)
    {
        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    C[r[i]][c[j]] = X.get(i, j);
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
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @param X
     *            A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int[] r, int j0, int j1, ComplexMatrix X)
    {
        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    C[r[i]][j] = X.get(i, j - j0);
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
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @param X
     *            A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int[] r, int j0, ComplexMatrix X)
    {
        setMatrix(r, j0, j0, X);
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
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int i0, int i1, int[] c, ComplexMatrix X)
    {
        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    C[i][c[j]] = X.get(i - i0, j);
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
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int i0, int[] c, ComplexMatrix X)
    {
        setMatrix(i0, i0, c, X);
    }

    /**
     * 
     * @param indices
     *            int[]
     * @param matCols
     *            Matrix
     */
    public void setRows(int[] indices, ComplexMatrix matRows)
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
            ComplexMatrix temp = matRows.getRowAt(i);
            setMatrix(indices[i], indices[i], 0, n - 1, temp);
        }
    }

    /**
     * 
     * @param indices
     * @param valRows
     */
    public void setRows(int[] indices, Complex valRows)
    {
        int len = indices.length;
        ComplexMatrix matRows = new ComplexMatrix(len, n, valRows);
        setRows(indices, matRows);
    }

    /**
     * 
     * @param index
     *            int
     * @param matCols
     *            Matrix
     */
    public void setRowAt(int index, ComplexMatrix matRows)
    {
        setRows(new int[]
        {
            index
        }, matRows);
    }

    /**
     * 
     * @param index
     * @param valRows
     */
    public void setRowAt(int index, Complex valRows)
    {
        setRows(new int[]
        {
            index
        }, valRows);
    }

    // ////////////////////////////////////////////////////////////////////////////
    public Complex get(int i, int j)
    {
        return C[i][j].copy();
    }

    public int length()
    {
        return m > n ? m : n;
    }

    public boolean isVector()
    {
        return (isRowVector() || isColVector());
    }

    /**
     * Test if this matrix is a row vector, eg: If A = (6,8,9,-1) , a 1 x 4
     * matix, A.isRowVector() returns true;
     * 
     * @return True if this matrix is a row vector or otherwise false.
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
     * @return True if this matrix is a column vector or otherwise false.
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

    public boolean isReal()
    {
        return isReal(0.0);
    }

    public boolean isReal(double tol)
    {
        if (tol < 0.0 || tol > 1.0E-3)
        {
            throw new IllegalArgumentException(" isReal : Parameter \"tol\" must fall in the interval [0 , 1.0E-3].");
        }
        Matrix Im = this.im();
        int numEls = Im.numel();
        Indices find = Im.EQ(0.0, tol).find();
        if (find != null && find.getRowDimension() == numEls)
        {
            return true;
        }
        return false;
    }

    public boolean isImaginary()
    {
        return isImaginary(0.0);
    }

    public boolean isImaginary(double tol)
    {
        if (tol < 0.0 || tol > 1.0E-3)
        {
            throw new IllegalArgumentException(
                    " isImaginary : Parameter \"tol\" must fall in the interval [0 , 1.0E-3].");
        }
        Matrix Re = this.re();
        int numEls = Re.numel();
        Indices find = Re.EQ(0.0, tol).find();
        if (find != null && find.getRowDimension() == numEls)
        {
            return true;
        }
        return false;
    }

    /**
     * ISFINITE True for finite elements. ISFINITE(X) returns an array that
     * contains 1's where the elements of X are finite AND 0's where they are
     * NOT. For example, ISFINITE([pi NaN Inf -Inf]) is [1 0 0 0].
     */
    public Indices isfinite()
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] cInt = X.getArray();

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                // if(!Double.isNaN(A[i][j])){ cInt[i][j] = 1; }
                // else if(!Double.isInfinite(A[i][j])) { cInt[i][j] = 1; }
                if (!MathUtil.nanOrInf(C[i][j].re()) || !MathUtil.nanOrInf(C[i][j].im()))
                {
                    cInt[i][j] = 1;
                }
            }
        }// end for
        return X;
    }

    /**
     * ISINF True for infinite elements. ISINF(X) returns an array that contains
     * 1's where the elements of X are +Inf OR -Inf AND 0's where they are NOT.
     * For example, ISINF([pi NaN Inf -Inf]) is [0 0 1 1].
     */
    public Indices isinf()
    {
        Indices X = new Indices(m, n);
        X.setLogical(true);
        int[][] cInt = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (Double.isInfinite(C[i][j].re()) || Double.isInfinite(C[i][j].im()))
                {
                    cInt[i][j] = 1;
                }
            }
        }// end for
        return X;
    }

    public ComplexMatrix toRowVector()
    {
        Complex[] val = getRowPackedCopy();
        Complex[][] R = new Complex[1][];
        R[0] = val;
        return new ComplexMatrix(R);
    }

    public ComplexMatrix toColVector()
    {
        Complex[] val = getColumnPackedCopy();
        Complex[][] R = new Complex[val.length][1];
        for (int j = 0; j < val.length; j++)
        {
            R[j][0] = val[j];
        }
        return new ComplexMatrix(R);
    }

    public Complex[] getColumnPackedCopy()
    {
        Complex[] vals = new Complex[m * n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                vals[i + j * m] = C[i][j].copy();
            }
        }
        return vals;
    }

    public ComplexMatrix transpose()
    {
        ComplexMatrix X = new ComplexMatrix(n, m);
        Complex[][] complex = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                complex[j][i] = C[i][j].copy();
            }
        }
        return X;
    }

    public ComplexMatrix times(ComplexMatrix B)
    {
        if (this.length() == 1)
        {
            return B.arrayTimes(C[0][0]);
        }
        else if (B.length() == 1)
        {
            return this.arrayTimes(B.get(0, 0));
        }

        if (B.m != n)
        {
            throw new IllegalArgumentException("ComplexMatrix inner dimensions must agree.");
        }
        ComplexMatrix X = new ComplexMatrix(m, B.n);
        Complex[][] complex = X.getArray();
        Complex[] Bcolj = new Complex[n];
        for (int j = 0; j < B.n; j++)
        {
            for (int k = 0; k < n; k++)
            {
                Bcolj[k] = B.C[k][j];
            }
            for (int i = 0; i < m; i++)
            {
                Complex[] Arowi = C[i];
                Complex s = new Complex();
                for (int k = 0; k < n; k++)
                {
                    s = s.add(Arowi[k].mul(Bcolj[k]));
                }
                complex[i][j] = s;
            }
        }
        return X;
    }

    /**
     * Make a one-dimensional row packed copy of the internal array.
     * 
     * @return Matrix elements packed in a one-dimensional array by rows.
     */
    public Complex[] getRowPackedCopy()
    {
        Complex[] vals = new Complex[m * n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                vals[i * n + j] = C[i][j].copy();
            }
        }
        return vals;
    }

    /**
     * 
     * @param findIndices
     * @return
     */
    public ComplexMatrix getFromFind(Indices findIndices)
    {
        if (findIndices.isFindIndex() == false)
        {
            throw new IllegalArgumentException(
                    " getFromFind : Parameter \"findIndices\" must be logical, ie, it must come from a logical operation such <, <=, > , >= , not, and, eq, neq, etc....");
        }
        int r = findIndices.getRowDimension();
        ComplexMatrix R = new ComplexMatrix(r, 1);
        int row = 0;
        int col = 0;

        for (int i = 0; i < r; i++)
        {
            row = findIndices.get(i, 0);
            col = findIndices.get(i, 1);
            R.set(i, 0, C[row][col].copy());
        }

        return R;
    }

    public ComplexMatrix toReal(Matrix X)
    {
        return new ComplexMatrix(X);
    }

    public ComplexMatrix toImag(Matrix X)
    {
        int row = X.getRowDimension();
        int col = X.getColumnDimension();
        ComplexMatrix imCM = new ComplexMatrix(row, col);
        imCM.setIm(X);
        return imCM;
    }

    /**
     * Main diagonal matrix, eg: ( 3,2,4 ) ( 3 ) If matrix A = | 6,4,1 | then
     * A.diag() = | 4 | , a 3 x 1 matrix is returned. ( 9,1,5 ) ( 5 )
     * 
     * @return the main Diagonal matrix
     */
    public ComplexMatrix diag()
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
    public ComplexMatrix diag(int K)
    {
        int smallDim = m < n ? m : n;
        ComplexMatrix temp = copy();
        Complex[][] C = temp.getArray();
        ComplexMatrix result = null;
        Complex[][] B = null;

        if (temp.isVector())
        { // column OR i vector

            int len = length();
            int k = Math.abs(K);
            int M = len + k, N = M;
            result = new ComplexMatrix(M, N);
            B = result.getArray();
            Complex[] rowpacked = temp.getRowPackedCopy();

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
                result = new ComplexMatrix(smallDim, 1);
                B = result.getArray();
                for (int i = 0; i < smallDim; i++)
                {
                    B[i][0] = C[i][i];
                }
            }
            else if (K > 0)
            {
                u = smallDim - K;
                result = new ComplexMatrix(u + 1, 1);
                B = result.getArray();
                for (int v = 0; v < (u + 1); v++)
                {
                    B[v][0] = C[v][v + K];
                }
            }
            else if (K < 0)
            {
                u = smallDim + K;
                result = new ComplexMatrix(u, 1);
                B = result.getArray();
                for (int v = 0; v < (u); v++)
                {
                    B[v][0] = C[v - K][v];
                }
            }
        }
        return result;
    }// end

    public void printCP(String name)
    {
        Matrix Real = this.re();
        Matrix Imag = this.im();
        Real.printInLabel(name + ": Real Part");
        Imag.printInLabel(name + ": Imag Part");
    }
}// -------------------------- End Class Definition
// -----------------------------

