package jamaextension.jamax.datafun;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.stats.JStats;

public class ZScore
{

    private Matrix mean;
    private Matrix std;
    private Matrix zscore;
    private boolean ignoreNaN = false;

    public ZScore(Matrix A)
    {
        this(A, null);
    }

    public ZScore(Matrix A, Dimension dim)
    {
        this(A, dim, false);
    }

    public ZScore(Matrix A, boolean ignoreNaN)
    {
        this(A, null, ignoreNaN);
    }

    /**
     * Creates a new instance of SumMat
     */
    public ZScore(Matrix A, Dimension dim, boolean ignoreNaN)
    {
        if (ignoreNaN)
        {
            throw new ConditionalException(
                    "ZScore : Only matrix with non-NaNs can be normalized at this stage (To Do).");
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
                zscoreMatrix(A, Dimension.ROW);
            }
        }
        else
        {
            zscoreMatrix(A, dim);
        }
    }

    private void zscoreMatrix(Matrix A, Dimension dim)
    {
        if (A.isVector())
        {
            normalizedVector(A);
        }
        else
        {
            zscoreMatrixAll(A, dim);
        }
    }

    private void zscoreMatrixAll(Matrix A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();

        // Matrix sumMat = null;
        // double max = 1.0;
        // this.zscore = Matrix.zeros(rows, cols);
        // QuickSort sort = null;
        double zc = 0.0;
        double mu = 0.0;
        double sd = 0.0;

        switch (dim)
        {
        case ROW:
            //this.zscore = Matrix.zeros(1, cols);
            this.mean = Matrix.zeros(1, cols);
            this.std = Matrix.zeros(1, cols);
            for (int j = 0; j < cols; j++)
            {
                Matrix colj = A.getColumnAt(j);
                mu = JDatafun.mean(colj).start();
                sd = JDatafun.std(colj).start();
                zc = JStats.zscore(colj).start();
                // sort = new QuickSortMat(colj);
                // max = ((Matrix) sort.getSortedObject()).end();
                // colj = colj.arrayRightDivide(max);
                this.mean.setElementAt(j, mu);
                this.std.setElementAt(j, sd);
                //this.zscore.setElementAt(j, zc);
                // .setColumnAt(j, colj);
            }
            this.zscore = JStats.zscore(A);
            break;
        case COL:
            this.zscore = Matrix.zeros(rows, 1);
            this.mean = Matrix.zeros(rows, 1);
            this.std = Matrix.zeros(rows, 1);
            for (int i = 0; i < rows; i++)
            {
                Matrix rowi = A.getRowAt(i);// .getColumnAt(i);
                // sort = new QuickSortMat(rowi);
                // max = ((Matrix) sort.getSortedObject()).end();
                // rowi = rowi.arrayRightDivide(max);
                mu = JDatafun.mean(rowi).start();
                sd = JDatafun.std(rowi).start();
                zc = JStats.zscore(rowi).start();
                this.mean.setElementAt(i, mu);
                this.std.setElementAt(i, sd);
                 
            }
            this.zscore = JStats.zscore(A.transpose());
            this.zscore = this.zscore.transpose();
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

        /*
         * double max = 0.0;
         * 
         * QuickSort sort = null;
         * 
         * if (A.isRowVector()) { sort = new QuickSortMat(A); } else { sort =
         * new QuickSortMat(A.transpose()); }
         * 
         * max = ((Matrix) sort.getSortedObject()).end();
         */

        // normalize by the biggest value (max-value);
        // zscore = A.arrayRightDivide(max);// new Matrix(1, 1, temp);
        this.mean = JDatafun.mean(A);
        this.std = JDatafun.std(A);
        this.zscore = JStats.zscore(A);
    }

    static Matrix testData()
    {
        double[][] xx =
        {
                {
                        4, 7, 11, 7, 14, 11, 9, 8, 11, 2
                },
                {
                        1, 13, 6, 13, 11, 5, 9, 14, 2, 7
                },
                {
                        10, 6, 3, 10, 13, 8, 9, 1, 5, 9
                },
                {
                        5, 11, 12, 7, 5, 0, 8, 1, 9, 10
                },
                {
                        0, 0, 13, 10, 15, 1, 10, 9, 3, 1
                },
                {
                        3, 3, 2, 8, 8, 14, 9, 2, 9, 5
                },
                {
                        5, 3, 8, 11, 6, 10, 9, 4, 0, 5
                },
                {
                        3, 8, 10, 9, 7, 8, 7, 2, 12, 6
                }
        };

        return new Matrix(xx);
    }

    /**
     * @return the mean
     */
    public Matrix getMean()
    {
        return mean;
    }

    /**
     * @return the std
     */
    public Matrix getStd()
    {
        return std;
    }

    /**
     * @return the zscore
     */
    public Matrix getZscore()
    {
        return zscore;
    }

    public static void main(String[] args)
    {
        Matrix X = testData();
        X.printInLabel("X",0);
        
        ZScore ZC = new ZScore(X, Dimension.COL);

        Matrix zscore = ZC.getZscore();
        Matrix mean = ZC.getMean();
        Matrix std = ZC.getStd();
        zscore.printInLabel("Zscore of X",6);
        mean.printInLabel("mean of X");
        std.printInLabel("Std-Dev of X");
        
        Min min = new MinMat(zscore);
        Matrix minCol = (Matrix)min.getMinObject();
        minCol.printInLabel("minimum of each colum of Zscore");
        Indices minInd = min.getIndices();
        minInd.printInLabel("row index of the minimum of Zscore");
        
        
    }

}
