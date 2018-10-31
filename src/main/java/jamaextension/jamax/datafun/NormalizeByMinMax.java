package jamaextension.jamax.datafun;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

public class NormalizeByMinMax
{

    private Matrix normalized;
    private Matrix minumum;
    private Matrix maximum;
    private boolean ignoreNaN = false;

    public NormalizeByMinMax(Matrix A)
    {
        this(A, null);
    }

    public NormalizeByMinMax(Matrix A, Dimension dim)
    {
        this(A, dim, false);
    }

    public NormalizeByMinMax(Matrix A, boolean ignoreNaN)
    {
        this(A, null, ignoreNaN);
    }

    /**
     * Creates a new instance of SumMat
     */
    public NormalizeByMinMax(Matrix A, Dimension dim, boolean ignoreNaN)
    {
        if (ignoreNaN)
        {
            throw new ConditionalException(
                    "NormalizeByMinMax : Only matrix with non-NaNs can be normalized at this stage (To Do).");
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

        // Sum sum = null;
        // Matrix sumMat = null;

        Max max = null;
        // this.maximum = (Matrix) max.getMaxObject();
        Min min = null;// new MinMat(A);
        // this.minumum = (Matrix) min.getMinObject();
        // double minNum = this.minumum.start();
        Matrix spread = null;// this.maximum.start() - minNum;

        // normalize by the biggest value (max-value);
        // normalized = A.minus(minNum).arrayRightDivide(spread);

        switch (dim)
        {
        case ROW:
            // normalized = new Matrix(1, cols);
            max = new MaxMat(A, Dimension.ROW);
            this.maximum = (Matrix) max.getMaxObject();// .getSumObject();
            min = new MinMat(A, Dimension.ROW);
            this.minumum = (Matrix) min.getMinObject();
            spread = this.maximum.minus(this.minumum);
            spread = spread.repmat(rows, 1);
            this.normalized = A.minus(this.minumum.repmat(rows, 1)).arrayRightDivide(spread);
            break;
        case COL:
            // normalized = new Matrix(rows, 1);

            max = new MaxMat(A, Dimension.COL);
            this.maximum = (Matrix) max.getMaxObject();// .getSumObject();
            min = new MinMat(A, Dimension.COL);
            this.minumum = (Matrix) min.getMinObject();
            spread = this.maximum.minus(this.minumum);
            spread = spread.repmat(1, cols);
            this.normalized = A.minus(this.minumum.repmat(1, cols)).arrayRightDivide(spread);

            // sum = new SumMat(A, Dimension.COL);
            // sumMat = (Matrix) sum.getSumObject();
            // sumMat = sumMat.repmat(1, cols);
            // this.normalized = A.arrayRightDivide(sumMat);
            break;
        default:
            throw new IllegalArgumentException("normalizedMatrixAll : Dimension  " + dim.toString()
                    + " , not supported.");
        }// end switch
    }

    private void normalizedVector(Matrix A)
    {
        if (!A.isVector())
        {
            throw new IllegalArgumentException(
                    " normalizedVector :  Parameter \"A\" must be a vector and not a matrix.");
        }

        Max max = new MaxMat(A);
        this.maximum = (Matrix) max.getMaxObject();
        Min min = new MinMat(A);
        this.minumum = (Matrix) min.getMinObject();
        double minNum = this.minumum.start();
        double spread = this.maximum.start() - minNum;

        // normalize by the biggest value (max-value);
        normalized = A.minus(minNum).arrayRightDivide(spread);// new Matrix(1,
                                                              // 1, temp);
    }

    public Matrix getNormalized()
    {
        return this.normalized;
    }

    public Matrix getMinumum()
    {
        return minumum;
    }

    public Matrix getMaximum()
    {
        return maximum;
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
        Matrix X1 = X;//.getRowAt(0).transpose();
        X.printInLabel("X", 0);
        X1.printInLabel("X1", 0);
        NormalizeByMinMax norm = new NormalizeByMinMax(X1);// new
        // Normalize(X,Dimension.COL);
        X = norm.getNormalized();
        X.printInLabel("X-norm-Col");

        Matrix max = norm.getMaximum();
        max.printInLabel("max-norm-Col");
        Matrix min = norm.getMinumum();
        min.printInLabel("Min-norm-Col");

    }

}
