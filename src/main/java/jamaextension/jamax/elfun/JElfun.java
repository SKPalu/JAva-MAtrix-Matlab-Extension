package jamaextension.jamax.elfun;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.Matrix3D;
import jamaextension.jamax.datafun.JDatafun;

/**
 * Title:        Extension of JAMA library
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      Feynman Perceptrons
 * @author       Sione Palu
 * @version 1.0
 */
/**
 * JElfun - Elementary math functions.
 * <P>
 * This class is final and not to be extended as all current and future methods,
 * would be static.
 */
public final class JElfun
{

    private JElfun()
    {
    }

    public static double cosAngleVectors(Matrix A, Matrix B)
    {
        return cosAngleVectors(A, B, false);
    }

    public static double cosAngleVectors(Matrix A, Matrix B, boolean norm)
    {

        double angle = 0.0;

        if (!A.isVector())
        {
            throw new ConditionalRuleException("First input argument \"A\" must be a vector & not a matrix.");
        }
        if (!B.isVector())
        {
            throw new ConditionalRuleException("Second input argument \"B\" must be a vector & not a matrix.");
        }
        if (A.length() != B.length())
        {
            throw new ConditionalRuleException("Lengths of vectors \"A\" and \"B\" must be the same.");
        }

        if (norm)
        {
            double nm = A.norm();
            A = A.arrayLeftDivide(nm);
            nm = B.norm();
            B = B.arrayLeftDivide(nm);
        }

        double sumdot = 0.0;
        Matrix dot = null;
        double magA = A.norm();
        double magB = B.norm();

        if (A.isRowVector())
        {
            if (B.isRowVector())
            {
                dot = A.arrayTimes(B);
            }
            else
            {
                dot = A.arrayTimes(B.toRowVector());
            }
        }
        else
        {
            if (B.isRowVector())
            {
                dot = A.toRowVector().arrayTimes(B);
            }
            else
            {
                dot = A.arrayTimes(B);
            }
        }

        sumdot = JDatafun.sum(dot).start();

        angle = sumdot / (magA * magB);

        return angle;

    }

    /**
     * Returns the step function Matrix matrix.
     * 
     * @param Matrix
     *            The input matrix data set
     * @return Matrix Contains the step function matrix at '0' .
     */
    public static Matrix sign(Matrix matrix)
    {
        return sign(matrix, 0.0d);
    }

    /**
     * Returns the step function Matrix matrix.
     * 
     * @param Matrix
     *            The input matrix data set
     * @return Matrix Contains the step function matrix at 'h' .
     */
    public static Matrix sign(Matrix matrix, double h)
    {
        double d[][] = matrix.getArray();
        int row = matrix.getRowDimension(), col = matrix.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                if (d[i][j] > h)
                {
                    C[i][j] = 1.0d;
                }
                else if (d[i][j] == h)
                {
                    C[i][j] = 0.0d;
                }
                else if (d[i][j] < h)
                {
                    C[i][j] = -1.0d;
                }
            }
        }
        return X;
    }

    public static double sign(double a)
    {
        double c = 0.0;
        if (a > 0.0)
        {
            c = 1.0d;
        }
        else if (a == 0.0)
        {
            c = 0.0d;
        }
        else if (a < 0.0)
        {
            c = -1.0d;
        }
        return c;
    }

    /**
     * Returns the power Matrix matrix. Overloaded method.
     * 
     * @param base
     *            The input base matrix data set
     * @param index
     *            The input index matrix
     * @return Matrix Contains the power matrix .
     */
    public static Matrix pow(Matrix base, Indices index)
    {
        int b_m = base.getRowDimension(), b_n = base.getColumnDimension();
        int x_m = index.getRowDimension(), x_n = index.getColumnDimension();
        if (b_m != x_m || b_n != x_n)
        {
            throw new IllegalArgumentException("pow : Matrix dimensions must agree.");
        }

        Matrix X = new Matrix(b_m, b_n);
        double[][] C = X.getArray();
        double val = 1.0;
        double[][] bs = base.getArray();
        int[][] id = index.getArray();

        for (int i = 0; i < b_m; i++)
        {
            for (int j = 0; j < b_n; j++)
            {
                val = Math.pow(bs[i][j], (double) id[i][j]);
                if (val > Double.MAX_VALUE)
                {
                    C[i][j] = Double.MAX_VALUE;
                }
                else
                {
                    C[i][j] = val;
                }
            }
        }
        return X;
    }// end method

    /**
     * Returns the power Matrix matrix. Overloaded method.
     * 
     * @param base
     *            The input base matrix data set
     * @param index
     *            The input index matrix
     * @return Matrix Contains the power matrix .
     */
    public static Matrix pow(Matrix base, Matrix index)
    {
        int b_m = base.getRowDimension(), b_n = base.getColumnDimension();
        int x_m = index.getRowDimension(), x_n = index.getColumnDimension();
        if (b_m != x_m || b_n != x_n)
        {
            throw new IllegalArgumentException("pow : Matrix dimensions must agree.");
        }

        Matrix X = new Matrix(b_m, b_n);
        double[][] C = X.getArray();
        double val = 1.0;
        double[][] bs = base.getArray();
        double[][] id = index.getArray();

        for (int i = 0; i < b_m; i++)
        {
            for (int j = 0; j < b_n; j++)
            {
                val = Math.pow(bs[i][j], id[i][j]);
                if (val > Double.MAX_VALUE)
                {
                    C[i][j] = Double.MAX_VALUE;
                }
                else
                {
                    C[i][j] = val;
                }
            }
        }
        return X;
    }// end method

    /**
     * Returns the power Matrix matrix. Overloaded method.
     * 
     * @param base
     *            The input base matrix data set
     * @param index
     *            double value as its index
     * @return Matrix Contains the power matrix of input Matrix matrix "matrix".
     */
    public static Matrix pow(double base, Matrix index)
    {
        double[][] d = index.getArray();
        int row = index.getRowDimension(), col = index.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        double val = 1.0;

        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                val = Math.pow(base, d[i][j]);
                if (val > Double.MAX_VALUE)
                {
                    C[i][j] = Double.MAX_VALUE;
                }
                else
                {
                    C[i][j] = val;
                }
            }
        }
        return X;
    }// end method

    public static Matrix pow(double base, Indices index)
    {
        int[][] d = index.getArray();
        int row = index.getRowDimension(), col = index.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        double val = 1.0;

        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                val = Math.pow(base, (double) d[i][j]);
                if (val > Double.MAX_VALUE)
                {
                    C[i][j] = Double.MAX_VALUE;
                }
                else
                {
                    C[i][j] = val;
                }
            }
        }
        return X;
    }// end method

    /**
     * Returns the power Matrix matrix.
     * 
     * @param base
     *            The input base matrix data set
     * @param index
     *            double value as its index
     * @return Matrix Contains the power matrix of input Matrix matrix "matrix".
     */
    public static Matrix pow(Matrix base, double index)
    {
        double[][] d = base.getArray();
        int row = base.getRowDimension(), col = base.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        // double[][] result = new double[row][col];
        double val = 1.0;
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                val = Math.pow(d[i][j], index); // System.out.println("val = "+val);
                if (val > Double.MAX_VALUE)
                {
                    C[i][j] = Double.MAX_VALUE;
                }
                else
                {
                    C[i][j] = val;
                }
            }
        }
        return X;
    }

    public static Matrix pow(Indices base, double index)
    {
        int[][] d = base.getArray();
        int row = base.getRowDimension(), col = base.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        // double[][] result = new double[row][col];
        double val = 1.0;
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                val = Math.pow((double) d[i][j], index); // System.out.println("val = "+val);
                if (val > Double.MAX_VALUE)
                {
                    C[i][j] = Double.MAX_VALUE;
                }
                else
                {
                    C[i][j] = val;
                }
            }
        }
        return X;
    }

    /**
     * Returns the power Matrix matrix.
     * 
     * @param base
     *            The input base matrix data set
     * @param index
     *            double value as its index
     * @return Matrix Contains the power matrix of input Matrix matrix "matrix".
     */
    public static Matrix3D pow(Matrix3D base, double index)
    {

        int row = base.getRowDimension();
        int col = base.getColDimension();
        int page = base.getPageDimension();
        Matrix3D X = new Matrix3D(row, col, page);
        // double[][] C = X.getArray();
        // double[][] result = new double[row][col];
        /*
         * double val = 1.0; for (int i = 0; i < row; i++) { for (int j = 0; j <
         * col; j++) { val = Math.pow(d[i][j], index);
         * //System.out.println("val = "+val); if (val > Double.MAX_VALUE) {
         * C[i][j] = Double.MAX_VALUE; } else { C[i][j] = val; } } }
         */
        for (int k = 0; k < page; k++)
        {
            Matrix baseMatI = base.getPageAt(k);
            baseMatI = pow(baseMatI, index);
            X.setPageAt(k, baseMatI);
        }

        return X;
    }

    /**
     * Returns the exponential Matrix matrix.
     * 
     * @param Matrix
     *            The input matrix data set
     * @return Matrix Contains the exponential matrix .
     */
    public static Matrix exp(Matrix matrix)
    {
        int row = matrix.getRowDimension(), col = matrix.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        double[][] MT = matrix.getArray();
        double val = 1.0;

        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                val = Math.exp(MT[i][j]);
                if (val > Double.MAX_VALUE)
                {
                    C[i][j] = Double.MAX_VALUE;
                }
                else
                {
                    C[i][j] = val;
                }
            }
        }
        return X;
    }

    /**
     * Returns the sine Matrix matrix.
     * 
     * @param matrix
     * @return Matrix Contains the sine matrix of input Matrix matrix "matrix".
     */
    public static Matrix sin(Matrix matrix)
    {
        int row = matrix.getRowDimension(), col = matrix.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        double[][] M = matrix.getArray();
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                C[i][j] = Math.sin(M[i][j]);
            }
        }
        return X;
    }

    /**
     * Returns the arcsine Matrix matrix.
     * 
     * @param Matrix
     *            The input matrix data set
     * @return Matrix Contains the arcsine matrix of input Matrix matrix
     *         "matrix".
     */
    public static Matrix asin(Matrix matrix)
    {
        double[][] internal = matrix.getArray();
        int row = matrix.getRowDimension(), col = matrix.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        double[][] M = matrix.getArray();
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                if (M[i][j] <= 1.0d || M[i][j] >= -1.0d)
                {
                    C[i][j] = Math.asin(M[i][j]);
                }
                else
                {
                    C[i][j] = Double.NaN;
                }
            }
        }
        return X;
    }

    /**
     * Returns the cosine Matrix matrix.
     * 
     * @param Matrix
     *            The input matrix data set
     * @return Matrix Contains the cosine matrix of input Matrix matrix
     *         "matrix".
     */
    public static Matrix cos(Matrix matrix)
    {
        int row = matrix.getRowDimension(), col = matrix.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        double[][] M = matrix.getArray();
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                C[i][j] = Math.cos(M[i][j]);
            }
        }
        return X;
    }

    /**
     * Returns the arccosine Matrix matrix.
     * 
     * @param matrix
     * @return Matrix Contains the arccosine matrix of input Matrix matrix
     *         "matrix".
     */
    public static Matrix acos(Matrix matrix)
    {
        double[][] M = matrix.getArray();
        int row = matrix.getRowDimension(), col = matrix.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                if (M[i][j] <= 1.0 || M[i][j] >= -1.0)
                {
                    C[i][j] = Math.acos(M[i][j]);
                }
                else
                {
                    C[i][j] = Double.NaN;
                }
            }
        }
        return X;
    }

    /**
     * Returns the square-root Matrix mat. If an element is found to negative,
     * then its square root would, be NaN (Not a Number).
     * 
     * @param Matrix
     *            The input mat data set
     * @return Matrix Contains the square-root mat of input Matrix mat "mat".
     */
    public static Matrix sqrt(Matrix mat)
    {
        double[][] M = mat.getArray();
        int row = mat.getRowDimension();
        int col = mat.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                if (M[i][j] < 0.0)
                {
                    // C[i][j] = Double.NaN;
                    throw new ConditionalException("sqrt : Found a negative number.");
                }
                else
                {
                    C[i][j] = Math.sqrt(M[i][j]);
                }
            }
        }
        return X;
    }

    /**
     * Returns the tangent Matrix matrix.
     * 
     * @param Matrix
     *            The input matrix data set
     * @return Matrix Contains the tangent matrix of input Matrix matrix
     *         "matrix".
     */
    public static Matrix tan(Matrix matrix)
    {
        double[][] M = matrix.getArray();
        int row = matrix.getRowDimension(), col = matrix.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                C[i][j] = Math.tan(M[i][j]);
            }
        }
        return X;
    }

    /**
     * Returns the arctangent Matrix matrix.
     * 
     * @param Matrix
     *            The input matrix data set
     * @return Matrix Contains the arctangent matrix of input Matrix matrix
     *         "matrix".
     */
    public static Matrix atan(Matrix matrix)
    {
        double[][] M = matrix.getArray();
        int row = matrix.getRowDimension(), col = matrix.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                C[i][j] = Math.atan(M[i][j]);
            }
        }
        return X;
    }

    public static Matrix atan2(Matrix Y, Matrix X)
    {
        boolean cond = Y == null || Y.isNull();
        if (cond)
        {
            throw new ConditionalException("atan2 : Matrix parameter \"Y\" must be non-null or non-empty.");
        }
        cond = X == null || X.isNull();
        if (cond)
        {
            throw new ConditionalException("atan2 : Matrix parameter \"X\" must be non-null or non-empty.");
        }

        cond = Y.sizeIndices().EQ(X.sizeIndices()).allBoolean();
        if (!cond)
        {
            throw new ConditionalException("atan2 : Sizes of \"Y\" and \"X\" must be the same.");
        }

        int rows = Y.getRowDimension();
        int cols = Y.getColumnDimension();

        Matrix R = new Matrix(rows, cols);
        double valY = 0.0;
        double valX = 0.0;
        double val = 0.0;
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                valY = Y.get(i, j);
                valX = X.get(i, j);
                val = Math.atan2(valY, valX);
                R.set(i, j, val);
            }
        }

        return R;
    }

    /**
     * Returns the sinc-pulse Matrix matrix.
     * 
     * @param matrix
     * @return Matrix Contains the sinc matrix .
     */
    public static Matrix sinc(Matrix matrix)
    {
        double[][] M = matrix.getArray();
        int row = matrix.getRowDimension(), col = matrix.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                if (M[i][j] == 0.0d)
                {
                    C[i][j] = 1.0;
                }
                else
                {
                    C[i][j] = Math.sin(M[i][j]) / M[i][j];
                }
            }
        }
        return X;
    }

    /**
     * Returns the hyperbolic sine Matrix matrix.
     * 
     * @param Matrix
     *            The input matrix data set
     * @return Matrix Contains the hyperbolic sine matrix of input Matrix matrix
     *         "matrix".
     */
    public static Matrix sinh(Matrix matrix)
    {
        double[][] M = matrix.getArray();
        int row = matrix.getRowDimension(), col = matrix.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        double val = 1.0;
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                val = Math.exp(M[i][j]);
                val = 0.5 * (val - 1 / val);
                C[i][j] = val;
            }
        }
        return X;
    }

    /**
     * Returns the hyperbolic cosine Matrix matrix.
     * 
     * @param Matrix
     *            The input matrix data set
     * @return Matrix Contains the hyperbolic cosine matrix of input Matrix
     *         matrix "matrix".
     */
    public static Matrix cosh(Matrix matrix)
    {
        double[][] M = matrix.getArray();
        int row = matrix.getRowDimension(), col = matrix.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double val = 1.0;
        double[][] C = X.getArray();
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                val = Math.exp(M[i][j]);
                val = 0.5 * (val + 1 / val);
                C[i][j] = val;
            }
        }
        return X;
    }

    /**
     * Returns the hyperbolic tangent Matrix matrix.
     * 
     * @param Matrix
     *            The input matrix data set
     * @return Matrix Contains the hyperbolic tangent matrix of input Matrix
     *         matrix "matrix".
     */
    public static Matrix tanh(Matrix matrix)
    {
        double[][] M = matrix.getArray();
        int row = matrix.getRowDimension(), col = matrix.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        double val = 1.0;
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                val = Math.exp(M[i][j]);
                val = (val * val - 1.0) / (val * val + 1.0);
                C[i][j] = val;
            }
        }
        return X;
    }

    public static double log1p(double z)
    {
        double w = z;

        double u = 1.0 + z;
        boolean p = (u <= 0.0) || Double.isInfinite(u);
        boolean m = !(u == 1.0 || p);
        if (m)
        {
            double um = u;
            w = Math.log(um) * (z / (um - 1.0));
            w = Math.log(u);

        }
        return w;
    }

    /**
     * Returns the log base 10 of a Matrix matrix.
     * 
     * @param Matrix
     *            The input matrix data set
     * @return Matrix Contains the log base 10 matrix of input Matrix matrix
     *         "matrix".
     */
    public static Matrix log10(Matrix matrix)
    {
        return logN(10., matrix);
    }

    /**
     * Returns the log base N of a Matrix matrix.
     * 
     * @param base
     *            The log base
     * @param matrix
     *            The input matrix data set
     * @return Matrix Contains the log base N matrix of input Matrix matrix
     *         "matrix".
     */
    public static Matrix logN(double base, Matrix matrix)
    {
        Matrix result = null;
        try
        {
            result = logN(matrix, base);
        }
        catch (Exception ex)
        {
        }
        return result;
    }

    /**
     * Returns the log base N of a Matrix matrix.
     * 
     * @param base
     *            The log base
     * @param matrix
     *            The input matrix data set
     * @return Matrix Contains the log base N matrix of input Matrix matrix
     *         "matrix".
     */
    public static Matrix logN(Matrix matrix, double base)
    {
        double b = 1.0;
        double[][] temp = matrix.getArray();
        int row = matrix.getRowDimension();
        int col = matrix.getColumnDimension();
        double[][] result = new double[row][col];
        if (base <= 0)
        {
            throw new IllegalArgumentException(
                    "logN : Negative or zero base result in a Complex Number or negative Infinity.");
        }

        b = Math.log(base);
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                if (temp[i][j] == 0.0)
                {
                    result[i][j] = Double.NEGATIVE_INFINITY;
                }
                else if (temp[i][j] < 0.0)
                {
                    result[i][j] = Double.NaN;
                }
                else
                {
                    result[i][j] = Math.log(temp[i][j]) / b;
                }
            }// end for
        }// end for
        return new Matrix(result);
    }// end method

    /**
     * Returns the natural log of a Matrix matrix.
     * 
     * @param base
     *            The log base
     * @param matrix
     *            The input matrix data set
     * @return Matrix Contains the natural log matrix of input Matrix .
     */
    public static Matrix log(Matrix matrix)
    {
        double base = Math.exp(1.0);
        return logN(base, matrix);
    }

    /**
     * Round towards zero.
     * 
     * @param matrix
     *            The input matrix data set
     * @return Matrix Contains the Matrix matrix that input "matrix" has been
     *         rounded towards zero.
     */
    public static Matrix fix(Matrix matrix)
    {
        double[][] M = matrix.getArray();
        int row = matrix.getRowDimension();
        int col = matrix.getColumnDimension();
        Matrix X = new Matrix(row, col);
        double[][] C = X.getArray();
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                C[i][j] = MathUtil.fix(M[i][j]);
            }
        }
        return X;
    }// end fix

    public static Matrix floor(Matrix X)
    {
        int m = X.getRowDimension(), n = X.getColumnDimension();
        Matrix val = new Matrix(m, n);
        double[][] C_val = val.getArray();
        double[][] C_X = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C_val[i][j] = Math.floor(C_X[i][j]);
            }
        }
        return val;
    }

    public static Matrix ceil(Matrix X)
    {
        int m = X.getRowDimension(), n = X.getColumnDimension();
        Matrix val = new Matrix(m, n);
        double[][] C_val = val.getArray();
        double[][] C_X = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C_val[i][j] = Math.ceil(C_X[i][j]);
            }
        }
        return val;
    }

    public static Matrix rem(double x, Matrix Y)
    {
        int m = Y.getRowDimension(), n = Y.getColumnDimension();
        Matrix X = new Matrix(m, n, x);
        return rem(X, Y);
    }

    public static Matrix rem(Matrix X, double y)
    {
        int m = X.getRowDimension(), n = X.getColumnDimension();
        Matrix Y = new Matrix(m, n, y);
        return rem(X, Y);
    }

    public static Matrix rem(double x, double y)
    {
        return rem(new Matrix(1, 1, x), new Matrix(1, 1, y));
    }

    public static Matrix rem(Matrix X, Matrix Y)
    {
        int m = X.getRowDimension(), n = X.getColumnDimension();
        if (m != Y.getRowDimension() || n != Y.getColumnDimension())
        {
            throw new IllegalArgumentException("rem : Matrix dimensions of \"X\" and \"Y\" must agree.");
        }
        Matrix result = new Matrix(m, n);
        double[][] C_result = result.getArray();
        double[][] CX = X.getArray();
        double[][] CY = Y.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                // if(CY[i][j]!=0.0d) { C_xoy[i][j] = CX[i][j]/CY[i][j];}
                // else { C_xoy[i][j] = Double.NaN;}
                C_result[i][j] = MathUtil.rem(CX[i][j], CY[i][j]);
            }// end for
        }// end for
         // Matrix result = Y.arrayTimes(fix(X_over_Y)).uminus().plus(X);//x -
         // y.*fix(x./y)
        return result;
    }

    /**
     * MOD Modulus (signed remainder after division). MOD(x,y) is x -
     * y.*floor(x./y) if y ~= 0. By convention, MOD(x,0) is x. The input x and y
     * must be real arrays of the same size, or real scalars.
     * 
     * The statement "x and y are congruent mod m" means mod(x,m) == mod(y,m).
     * 
     * MOD(x,y) has the same posSign as y while REM(x,y) has the same posSign as
     * x. MOD(x,y) and REM(x,y) are equal if x and y have the same posSign, but
     * differ by y if x and y have different signs.
     */
    public static double mod(double x, double y)
    {
        double z = 0.0;
        // Integer denominator.
        // Use the conventional formula.
        boolean m = ((y == MathUtil.fix(y)) && (y != 0.0));
        if (m)
        {
            z = x - Math.floor(x / y) * y;
        }

        // Noninteger denominator.
        // Adjust any quotient within roundoff error of an integer.
        m = (y != MathUtil.fix(y));
        double q = 0.0;
        boolean r = false;
        if (m)
        {
            q = x / y;
            r = Math.abs(q - Math.round(q)) <= (Math.abs(q) * MathUtil.EPS);
            if (r)
            {
                q = Math.round(q);
            }
            z = (q - Math.floor(q)) * y;
        }

        // Zero denominator.
        m = y == 0.0;
        if (m)
        {
            z = x;
        }
        return z;
    }// end methods

    public static Matrix mod(Matrix X, Matrix Y)
    {
        int rowsX = X.getRowDimension();
        int colsX = X.getColumnDimension();
        int rowsY = Y.getRowDimension();
        int colsY = Y.getColumnDimension();

        if (rowsX != rowsY || colsX != colsY)
        {
            throw new IllegalArgumentException("mod : Matrices \"X\" and \"Y\" must be the same size.");
        }

        Matrix R = new Matrix(rowsX, colsX);
        double[][] rs = R.getArray();
        double[][] xs = X.getArray();
        double[][] ys = Y.getArray();
        for (int i = 0; i < rowsX; i++)
        {
            for (int j = 0; j < colsX; j++)
            {
                rs[i][j] = mod(xs[i][j], ys[i][j]);
            }
        }
        return R;
    }// end method

    public static Matrix mod(double x, Matrix Y)
    {
        int r = Y.getRowDimension();
        int c = Y.getColumnDimension();
        Matrix X = new Matrix(r, c, x);
        return mod(X, Y);
    }

    public static Matrix mod(Matrix X, double y)
    {
        int r = X.getRowDimension();
        int c = X.getColumnDimension();
        Matrix Y = new Matrix(r, c, y);
        return mod(X, Y);
    }

    public static Matrix[] meshgrid(Matrix xx, Matrix yy)
    {
        // 2-D array case only
        if (xx.isVector() == false)
        {
            throw new IllegalArgumentException("meshgrid : Parameter \"xx\" , must be a column or row vector.");
        }
        if (yy.isVector() == false)
        {
            throw new IllegalArgumentException("meshgrid : Parameter \"yy\" , must be a column or row vector.");
        }
        int nx = xx.length();
        int ny = yy.length();
        Matrix tmp = null;

        if (xx.isColVector())
        {
            tmp = xx.toRowVector();
        }
        else
        {
            tmp = xx;
        }
        Matrix meshX = tmp.repmat(ny, 1);

        if (yy.isColVector())
        {
            tmp = yy.toRowVector();
        }
        else
        {
            tmp = yy;
        }
        Matrix meshY = tmp.transpose().repmat(1, nx);

        Matrix[] mgrid =
        {
                meshX, meshY
        };
        return mgrid;
    }

    public static Matrix[] meshgrid(Matrix xx)
    {
        return meshgrid(xx, xx.copy());
    }

    public static Matrix sech(Matrix x)
    {
        int r = x.getRowDimension();
        int c = x.getColumnDimension();
        double val = 1.0;
        double[][] xArr = x.getArray();
        Matrix R = new Matrix(r, c);
        double[][] M = R.getArray();
        for (int i = 0; i < r; i++)
        {
            for (int j = 0; j < c; j++)
            {
                val = Math.exp(xArr[i][j]);
                val = 2.0 / (val + 1.0 / val);
                M[i][j] = val;
            }
        }
        return R;
    }

    public static Matrix logBase2(Matrix mat)
    {
        double val = 0.0;
        int m = mat.getRowDimension();
        int n = mat.getColumnDimension();
        Matrix log2 = new Matrix(m, n);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                val = logBase2(mat.get(i, j));
                log2.set(i, j, val);
            }
        }
        return log2;
    }

    public static double logBase2(double n)
    {
        double val = Math.log(n) / Math.log(2.0);
        return val;
    }

    public static double nextpow2(Matrix val)
    {
        double nxp2 = nextpow2((double) val.length());
        return nxp2;
    }

    public static double nextpow2(double n)
    {
        Object[] obj = frexp(Math.abs(n));
        double f = ((Double) obj[0]).doubleValue();
        int p = ((Integer) obj[1]).intValue();

        // Check if n is an exact power of 2.
        if ( /* ~isempty(f) && */f == 0.5)
        {
            p -= 1;
        }// end

        // Check for infinities and NaNs
        // boolean k = ~isfinite(f);
        // p(k) = f(k);
        return (double) p;
    }

    public static Object[] log2(Matrix val)
    {
        int row = val.getRowDimension();
        int col = val.getColumnDimension();
        Matrix F = new Matrix(row, col);
        Indices E = new Indices(row, col);

        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                Object[] obj = log2(val.get(i, j));// frexp(val.get(i, j));
                F.set(i, j, ((Double) obj[0]).doubleValue());
                E.set(i, j, ((Integer) obj[1]).intValue());
            }
        }

        return new Object[]
        {
                F, E
        };
    }

    public static Object[] log2(double val)
    {
        return frexp(val);
    }

    private static Object[] frexp(double value)
    {

        double mantissa = 0.0;
        int exponent = 0;

        if (value == 0.0 /* || value == -0.0 */)
        {
            return new Object[]
            {
                    mantissa, exponent
            };
        }

        if (Double.isNaN(value))
        {
            mantissa = Double.NaN;
            // exponent = -1;
            return new Object[]
            {
                    mantissa, exponent
            };
        }

        if (Double.isInfinite(value))
        {
            mantissa = value;
            // exponent = -1;
            return new Object[]
            {
                    mantissa, exponent
            };
        }

        mantissa = value;
        exponent = 0;
        boolean posSign = true;

        if (mantissa < 0.0)
        {
            posSign = false;
            mantissa = -(mantissa);
        }
        while (mantissa < 0.5)
        {
            mantissa *= 2.0;
            exponent -= 1;
        }
        while (mantissa >= 1.0)
        {
            mantissa *= 0.5;
            exponent++;
        }
        // mantissa = mantissa*((double)posSign);
        if (!posSign)
        {
            mantissa = -mantissa;
        }

        return new Object[]
        {
                mantissa, exponent
        };

    }

    public static void main(String[] args)
    {
        /*
         * Matrix rand = Matrix.random(4,5); Matrix B = new Matrix(4,5,-20);
         * rand.arrayTimesEquals(B);
         * System.out.println("------------random size[4,5]--------------");
         * rand.print(5,6);
         * System.out.println("\n------------fix--------------------");
         * JElfun.fix((Matrix)rand).print(5,6);
         * 
         * Matrix x = Matrix.linspace(-10., 10., 5); x.print(2, 2); Matrix y =
         * pow(x, 2.0); y.print(2, 2);
         */

        Object[] obj = frexp(Double.POSITIVE_INFINITY);
        double F = ((Double) obj[0]).doubleValue();
        int E = ((Integer) obj[1]).intValue();
        System.out.println(" F = " + F);
        System.out.println(" ");
        System.out.println(" E = " + E);

    }
}// ---------------------------End class
// definition------------------------------

