package jamaextension.jamax.datafun;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

public class NormalizeBySum
{

    private Matrix normalized;
    private boolean ignoreNaN = false;

    public NormalizeBySum(Matrix A)
    {
        this(A, null);
    }

    public NormalizeBySum(Matrix A, Dimension dim)
    {
        this(A, dim, false);
    }

    public NormalizeBySum(Matrix A, boolean ignoreNaN)
    {
        this(A, null, ignoreNaN);
    }

    /**
     * Creates a new instance of SumMat
     */
    public NormalizeBySum(Matrix A, Dimension dim, boolean ignoreNaN)
    {
        if (ignoreNaN)
        {
            throw new ConditionalException(
                    "Normalize : Only matrix with non-NaNs can be normalized at this stage (To Do).");
        }
        this.ignoreNaN = ignoreNaN;
        if (dim == null)
        {
            if (A.isVector())
            {
                normalizedVector(A);
            }
            else
            {
                normalizedMatrix(A, Dimension.ROW);
            }
        }
        else
        {
            normalizedMatrix(A, dim);
        }
    }

    private void normalizedMatrix(Matrix A, Dimension dim)
    {
        if (A.isVector())
        {
            normalizedVector(A);
        }
        else
        {
            normalizedMatrixAll(A, dim);
        }
    }

    private void normalizedMatrixAll(Matrix A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();

        Sum sum = null;
        Matrix sumMat = null;

        switch (dim)
        {
        case ROW:
            // normalized = new Matrix(1, cols);
            sum = new SumMat(A, Dimension.ROW);
            sumMat = (Matrix) sum.getSumObject();
            sumMat = sumMat.repmat(rows, 1);
            this.normalized = A.arrayRightDivide(sumMat);
            break;
        case COL:
            // normalized = new Matrix(rows, 1);
            sum = new SumMat(A, Dimension.COL);
            sumMat = (Matrix) sum.getSumObject();
            sumMat = sumMat.repmat(1, cols);
            this.normalized = A.arrayRightDivide(sumMat);
            break;
        default:
            throw new IllegalArgumentException("meanMatrixAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
    }

    private void normalizedVector(Matrix A)
    {
        if (!A.isVector())
        {
            throw new IllegalArgumentException(" meanVector :  Parameter \"A\" must be a vector and not a matrix.");
        }

        double total = 0.0;

        int len = A.length();

        if (A.isRowVector())
        {
            for (int j = 0; j < len; j++)
            {
                total += A.get(0, j);
            }
        }
        else
        {
            for (int i = 0; i < len; i++)
            {
                total += A.get(i, 0);
            }
        }

        // normalize by the biggest value (max-value);
        normalized = A.arrayRightDivide(total);// new Matrix(1, 1, temp);
    }

    public Matrix getNormalized()
    {
        return this.normalized;
    }

    static Matrix testMat()
    {
        double[][] x =
        {
                {
                        8, 9, 3, 10
                },
                {
                        9, 6, 5, 2
                },
                {
                        1, 1, 10, 10
                }
        };
        return new Matrix(x);
    }

    public static void main(String[] args)
    {

        Matrix X = testMat();
        Matrix X1 = X.getRowAt(0).transpose();
        X.printInLabel("X", 0);
        X1.printInLabel("X1", 0);
        NormalizeBySum norm = new NormalizeBySum(X1, Dimension.COL);// new
                                                                    // Normalize(X,Dimension.COL);
        X = norm.getNormalized();
        X.printInLabel("X");
        // TODO Auto-generated method stub

    }

}
