package jamaextension.jamax.datafun;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

public class NormalizeByMax
{

    private Matrix normalized;
    private boolean ignoreNaN = false;

    public NormalizeByMax(Matrix A)
    {
        this(A, null);
    }

    public NormalizeByMax(Matrix A, Dimension dim)
    {
        this(A, dim, false);
    }

    public NormalizeByMax(Matrix A, boolean ignoreNaN)
    {
        this(A, null, ignoreNaN);
    }

    /**
     * Creates a new instance of SumMat
     */
    public NormalizeByMax(Matrix A, Dimension dim, boolean ignoreNaN)
    {
        if (ignoreNaN)
        {
            throw new ConditionalException(
                    "NormalizeByMax : Only matrix with non-NaNs can be normalized at this stage (To Do).");
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

        Matrix sumMat = null;
        double max = 1.0;
        this.normalized = Matrix.zeros(rows, cols);
        QuickSort sort = null;

        switch (dim)
        {
        case ROW:
            for (int j = 0; j < cols; j++)
            {
                Matrix colj = A.getColumnAt(j);
                sort = new QuickSortMat(colj);
                max = ((Matrix) sort.getSortedObject()).end();
                colj = colj.arrayRightDivide(max);
                this.normalized.setColumnAt(j, colj);
            }
            break;
        case COL:
            for (int i = 0; i < rows; i++)
            {
                Matrix rowi = A.getRowAt(i);// .getColumnAt(i);
                sort = new QuickSortMat(rowi);
                max = ((Matrix) sort.getSortedObject()).end();
                rowi = rowi.arrayRightDivide(max);
                this.normalized.setRowAt(i, rowi);// .setColumnAt(i, coli);
            }
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

        double max = 0.0;

        QuickSort sort = null;

        if (A.isRowVector())
        {
            sort = new QuickSortMat(A);
        }
        else
        {
            sort = new QuickSortMat(A.transpose());
        }

        max = ((Matrix) sort.getSortedObject()).end();
        // normalize by the biggest value (max-value);
        normalized = A.arrayRightDivide(max);// new Matrix(1, 1, temp);
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
        Matrix X1 = X.getRowAt(0);// .transpose();
        X.printInLabel("X", 0);
        X1.printInLabel("X1", 0);
        NormalizeByMax norm = null;// new
        // norm = new NormalizeByMax(X,Dimension.ROW);
        norm = new NormalizeByMax(X1, Dimension.COL);
        X = norm.getNormalized();
        X.printInLabel("X");
        // TODO Auto-generated method stub

    }

}
