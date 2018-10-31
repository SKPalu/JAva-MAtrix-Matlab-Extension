package jamaextension.jamax;

/**
 * Title:        Extended JAMA
 * Description:  Extension of JAMA package with more functionalities to be as close to MatLab
 * Copyright:    Copyright (c) 2005
 * Company:      FP
 * @author Sione Palu
 * @version 1.0
 */

/**
 * Cholesky Decomposition.
 * <P>
 * For a symmetric, positive definite matrix A, the Cholesky decomposition is an
 * lower triangular matrix LR so that A = LR*LR'.
 * <P>
 * If the matrix is not symmetric or positive definite, the constructor returns
 * a partial decomposition and sets an internal flag that may be queried by the
 * isSPD() method.
 */

public class CholeskyDecomposition implements java.io.Serializable
{

    /*
     * ------------------------ Class variables ------------------------
     */

    /**
     * Array for internal storage of decomposition.
     * 
     * @serial internal array storage.
     */
    private double[][] LR;

    /**
     * Row and column dimension (square matrix).
     * 
     * @serial matrix dimension.
     */
    private int n;

    /**
     * Symmetric and positive definite flag.
     * 
     * @serial is symmetric and positive definite flag.
     */
    private boolean isspd;

    private boolean upperTriangle;

    private double tol = 1.0E-6;

    /*
     * ------------------------ Constructor ------------------------
     */

    /**
     * Cholesky algorithm for symmetric and positive definite matrix.
     * 
     * @param A
     *            Square, symmetric matrix.
     * @return Structure to access LR and isspd flag.
     */

    public CholeskyDecomposition(Matrix Arg)
    {

        this(Arg, false);
        /*
         * // Initialize. double[][] A = Arg.getArray(); n =
         * Arg.getRowDimension(); LR = new double[n][n]; isspd =
         * (Arg.getColumnDimension() == n); // Main loop. for (int j = 0; j < n;
         * j++) { double[] Lrowj = LR[j]; double d = 0.0; for (int k = 0; k < j;
         * k++) { double[] Lrowk = LR[k]; double s = 0.0; for (int i = 0; i < k;
         * i++) { s += Lrowk[i]*Lrowj[i]; } Lrowj[k] = s = (A[j][k] -
         * s)/LR[k][k]; d = d + s*s; isspd = isspd & (A[k][j] == A[j][k]); } d =
         * A[j][j] - d; isspd = isspd & (d > 0.0); LR[j][j] =
         * Math.sqrt(Math.max(d,0.0)); for (int k = j+1; k < n; k++) { LR[j][k]
         * = 0.0; } }
         */
    }

    public CholeskyDecomposition(Matrix Arg, boolean rightflag)
    {
        this(Arg, rightflag, 1.0E-6);
    }

    public CholeskyDecomposition(Matrix Arg, boolean rightflag, double tolerance)
    {
        if (tolerance <= 0.0)
        {
            throw new IllegalArgumentException(
                    "CholeskyDecomposition : Parameter \"tolerance\" must be a positive number.");
        }
        this.tol = tolerance;
        if (rightflag)
        {
            computeUpper(Arg);
        }
        else
        {
            computeLower(Arg);
        }
        upperTriangle = rightflag;
    }

    private void computeLower(Matrix Arg)
    {
        // Initialize.
        double[][] A = Arg.getArray();
        n = Arg.getRowDimension();
        LR = new double[n][n];
        isspd = (Arg.getColumnDimension() == n);
        // Main loop.
        for (int j = 0; j < n; j++)
        {
            double[] Lrowj = LR[j];
            double d = 0.0;
            for (int k = 0; k < j; k++)
            {
                double[] Lrowk = LR[k];
                double s = 0.0;
                for (int i = 0; i < k; i++)
                {
                    s += Lrowk[i] * Lrowj[i];
                }
                Lrowj[k] = s = (A[j][k] - s) / LR[k][k];
                d = d + s * s;
                // isspd = isspd & (A[k][j] == A[j][k]); ==> this line has been
                // replaced by the following 2 lines.
                boolean eqWithTol = MathUtil.equalsWithTol(A[k][j], A[j][k], tol);
                isspd = isspd && eqWithTol;
            }
            d = A[j][j] - d;
            isspd = isspd && (d > 0.0);
            LR[j][j] = Math.sqrt(Math.max(d, 0.0));
            for (int k = j + 1; k < n; k++)
            {
                LR[j][k] = 0.0;
            }
        }
    }

    private void computeUpper(Matrix Arg)
    {
        // Initialize.
        double[][] A = Arg.getArray();
        n = Arg.getColumnDimension();
        LR = new double[n][n];
        isspd = (Arg.getColumnDimension() == n);
        // Main loop.
        for (int j = 0; j < n; j++)
        {
            double d = 0.0;
            for (int k = 0; k < j; k++)
            {
                double s = A[k][j];
                for (int i = 0; i < k; i++)
                {
                    s = s - LR[i][k] * LR[i][j];
                }
                LR[k][j] = s = s / LR[k][k];
                d = d + s * s;
                // isspd = isspd & (A[k][j] == A[j][k]);
                boolean eqWithTol = MathUtil.equalsWithTol(A[k][j], A[j][k], tol);
                isspd = isspd && eqWithTol;
            }
            d = A[j][j] - d;
            isspd = isspd & (d > 0.0);
            LR[j][j] = Math.sqrt(Math.max(d, 0.0));
            for (int k = j + 1; k < n; k++)
            {
                LR[k][j] = 0.0;
            }
        }
    }

    private void computeAlgorithm6_6(Matrix Arg)
    {
    }

    public boolean isUpperTriangle()
    {
        return upperTriangle;
    }

    /*
     * ------------------------ Temporary, experimental code.
     * ------------------------ *\
     * 
     * \** Right Triangular Cholesky Decomposition. <P> For a symmetric,
     * positive definite matrix A, the Right Cholesky decomposition is an upper
     * triangular matrix LR so that A = LR'*LR. This constructor computes LR
     * with the Fortran inspired column oriented algorithm used in LINPACK and
     * MATLAB. In Java, we suspect a row oriented, lower triangular
     * decomposition is faster. We have temporarily included this constructor
     * here until timing experiments confirm this suspicion.\
     * 
     * \** Array for internal storage of right triangular decomposition. **\
     * private transient double[][] LR;
     * 
     * \** Cholesky algorithm for symmetric and positive definite matrix.
     * 
     * @param A Square, symmetric matrix.
     * 
     * @param rightflag Actual value ignored.
     * 
     * @return Structure to access LR and isspd flag.\
     * 
     * public CholeskyDecomposition (Matrix Arg, int rightflag) { // Initialize.
     * double[][] A = Arg.getArray(); n = Arg.getColumnDimension(); LR = new
     * double[n][n]; isspd = (Arg.getColumnDimension() == n); // Main loop. for
     * (int j = 0; j < n; j++) { double d = 0.0; for (int k = 0; k < j; k++) {
     * double s = A[k][j]; for (int i = 0; i < k; i++) { s = s -
     * LR[i][k]*LR[i][j]; } LR[k][j] = s = s/LR[k][k]; d = d + s*s; isspd =
     * isspd & (A[k][j] == A[j][k]); } d = A[j][j] - d; isspd = isspd & (d >
     * 0.0); LR[j][j] = Math.sqrt(Math.max(d,0.0)); for (int k = j+1; k < n;
     * k++) { LR[k][j] = 0.0; } } }
     * 
     * \** Return upper triangular factor.
     * 
     * @return LR\
     * 
     * public Matrix getR () { return new Matrix(LR,n,n); }
     * 
     * \* ------------------------ End of temporary code.
     * ------------------------
     */

    /*
     * ------------------------ Public Methods ------------------------
     */

    /**
     * Is the matrix symmetric and positive definite?
     * 
     * @return true if A is symmetric and positive definite.
     */

    public boolean isSPD()
    {
        return isspd;
    }

    /**
     * Return triangular factor.
     * 
     * @return LR
     */

    public Matrix getL()
    {
        return new Matrix(LR, n, n);
    }

    public Matrix getLR()
    {
        return getL();
    }

    /**
     * Solve A*X = B
     * 
     * @param B
     *            A Matrix with as many rows as A and any number of columns.
     * @return X so that LR*LR'*X = B
     * @exception IllegalArgumentException
     *                Matrix row dimensions must agree.
     * @exception RuntimeException
     *                Matrix is not symmetric positive definite.
     */

    public Matrix solve(Matrix B)
    {
        if (B.getRowDimension() != n)
        {
            throw new IllegalArgumentException("Matrix row dimensions must agree.");
        }
        if (!isspd)
        {
            throw new RuntimeException("Matrix is not symmetric positive definite.");
        }

        // Copy right hand side.
        double[][] X = B.getArrayCopy();
        int nx = B.getColumnDimension();

        // Solve LR*Y = B;
        for (int k = 0; k < n; k++)
        {
            for (int j = 0; j < nx; j++)
            {
                for (int i = 0; i < k; i++)
                {
                    X[k][j] -= X[i][j] * LR[k][i];
                }
                X[k][j] /= LR[k][k];
            }
        }

        // Solve LR'*X = Y;
        for (int k = n - 1; k >= 0; k--)
        {
            for (int j = 0; j < nx; j++)
            {
                for (int i = k + 1; i < n; i++)
                {
                    X[k][j] -= X[i][j] * LR[i][k];
                }
                X[k][j] /= LR[k][k];
            }
        }

        return new Matrix(X, n, nx);
    }

    public static void main(String[] args)
    {
        double[][] d =
        {
                {
                        0.0442, -0.0232
                },
                {
                        -0.0232, 0.0222
                }
        };// {{1, -1},{ -1, 2}};
        Matrix A = new Matrix(d);
        CholeskyDecomposition chol = new CholeskyDecomposition(A, true);
        Matrix R = chol.getLR();

        System.out.println("--------------- R ---------------");
        R.print(8, 4);

        Matrix Arecon = R.transpose().times(R);
        System.out.println("--------------- R'*R ---------------");
        Arecon.print(8, 4);

        System.out.println(" chol.isSPD = " + chol.isSPD());
    }

}
