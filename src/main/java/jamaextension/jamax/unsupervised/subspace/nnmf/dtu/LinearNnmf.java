/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.unsupervised.subspace.nnmf.dtu;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.TestData;

/**
 * 
 * @author sionep
 */
public class LinearNnmf
{

    private Matrix data;
    private double tolerance = 1.0e-9;
    private int maximumIteration = 50;
    private boolean nonNegativeOption = true;
    private double regularizationParameter = 0.01;
    private int rank = 2; // dimension that needs to be reduced to , in the
                          // reduction
    private boolean verbose = false;
    private boolean svdInitialized = false;
    private boolean svdInitializedOptimized = false;
    private Matrix W;
    private Matrix H;
    private Matrix Winit;
    private Matrix Hinit;

    /**
     * Creates a new instance of LinearNnmf
     */
    public LinearNnmf()
    {
    }

    public LinearNnmf(Matrix data)
    {
        setData(data);
    }

    public boolean isSvdInitialized()
    {
        return svdInitialized;
    }

    public void setSvdInitialized(boolean svdInitialized)
    {
        this.svdInitialized = svdInitialized;
    }

    public Matrix getWinit()
    {
        return Winit;
    }

    public Matrix getHinit()
    {
        return Hinit;
    }

    public boolean isVerbose()
    {
        return verbose;
    }

    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    public void setData(Matrix data)
    {
        if (data == null)
        {
            throw new IllegalArgumentException("setData : Data must be non-empty.");
        }
        if (data.isVector())
        {
            throw new IllegalArgumentException("setData : Data must be a matrix and not a vector.");
        }
        if (isAnyElementNegative(data))
        {
            throw new IllegalArgumentException("setData : Found a negative element in the data.");
        }
        this.data = data;
    }

    public Matrix getData()
    {
        return data.copy();
    }

    public void setTolerance(double tol)
    {
        if (Math.abs(tol) < 1.0e-12 || Math.abs(tol) > 0.01)
        {
            throw new IllegalArgumentException(
                    "setTolerance : Tolerance must be a positive number between [ 1.0e-12  and 0.01 ].");
        }
        tolerance = tol;
    }

    public double getTolerance()
    {
        return tolerance;
    }

    public void setMaximumIteration(int iter)
    {
        if (iter < 10)
        {
            throw new IllegalArgumentException("setMaximumIteration : Number of interations must be at least 20.");
        }
        maximumIteration = iter;
    }

    public int getMaximumIteration()
    {
        return this.maximumIteration;
    }

    public void setNonNegativeOption(boolean tf)
    {
        this.nonNegativeOption = tf;
    }

    public boolean isNonNegativeOption()
    {
        return this.nonNegativeOption;
    }

    public void setRegularizationParameter(double regParam)
    {
        if (regParam < 1.0e-7 || regParam > 0.9)
        {
            throw new IllegalArgumentException(
                    "setRegularizationParameter : The regularization parameter must fall in the interval [ 1.0e-7 and 0.9]");
        }
        this.regularizationParameter = regParam;
    }

    public double getRegularizationParameter()
    {
        return this.regularizationParameter;
    }

    public void setRank(int r)
    {
        if (r < 2)
        {
            throw new IllegalArgumentException("setRank : The rank must be at least two.");
        }
        rank = r;
    }

    public int getRank()
    {
        return rank;
    }

    public Matrix getW()
    {
        return W;
    }

    public Matrix getH()
    {
        return H;
    }

    /**
     * Check the data if any element is negative. All entries must be
     * non-negative.
     */
    private boolean isAnyElementNegative(Matrix data)
    {
        int m = data.getRowDimension();
        int n = data.getColumnDimension();
        double[][] A = data.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] < 0.0)
                {
                    System.out.println("A[" + i + "][" + j + "] = " + A[i][j] + " <<== negative element");
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSvdInitializedOptimized()
    {
        return svdInitializedOptimized;
    }

    public void setSvdInitializedOptimized(boolean svdInitializedOptimized)
    {
        this.svdInitializedOptimized = svdInitializedOptimized;
    }

    /*
     * Main routine
     */
    public void computeNNMF()
    {
        if (data == null)
        {
            throw new IllegalArgumentException("computeNNMF : Data must be non-empty.");
        }
        int m = data.getRowDimension();
        int n = data.getColumnDimension();

        if (svdInitialized)
        {
            Object[] obj = null;
            if (svdInitializedOptimized)
            {
                obj = DtuNnmfUtil.nmfSvdInitialization(data);
            }
            else
            {
                obj = DtuNnmfUtil.nmfSvdInitialization(data, rank);
            }
            
            W = (Matrix)obj[0];
            H = (Matrix)obj[1];
            rank = (Integer)obj[2];
        }
        else
        {
            W = Matrix.random(m, rank);
            H = Matrix.random(rank, n);
        }

        for (int j = 0; j < maximumIteration; j++)
        {
            Matrix A = W.transpose().times(W).plus(Matrix.identity(rank).arrayTimes(regularizationParameter));

            for (int i = 0; i < n; i++)
            {
                if (this.verbose)
                {
                    int colCount = (i + 1);
                    if (colCount % 50 == 0)
                    {
                        System.out.println("NMF iteration #" + (j + 1) + " : Column #" + colCount);
                    }
                }

                Matrix b = W.transpose().times(data.getColumnAt(i));
                Matrix temp = A.inverse().times(b);
                H.setColumnAt(i, temp);
            }// end inner for-loop

            if (this.nonNegativeOption)
            {
                // Matrix temp2 = Matrix.indicesToMatrix(H.GT(0.0));
                // H = H.arrayTimes(temp2);
                H = H.arrayTimes(H.GT(0.0));
            }

            Matrix numerator = W.arrayTimes(data.times(H.transpose()));
            Matrix denominator = W.times(H.times(H.transpose())).plus(tolerance);
            W = numerator.arrayRightDivide(denominator);
        }// end outer for-loop
    }

    /*
     * This is just automated data for testing.
     */
    public static Matrix generateTestData()
    {
        double[][] A = new double[][]
        {
                {
                        19, 18, 16, 18, 19
                },
                {
                        5, 15, 9, 15, 18
                },
                {
                        12, 9, 12, 4, 8
                },
                {
                        10, 0, 16, 8, 18
                }
        };

        /*
         * A = [19, 18, 16, 18, 19;... 5, 15, 9, 15, 18;... 12, 9, 12, 4, 8;...
         * 10, 0, 16, 8, 18];
         * 
         * [W, H] = gdcls(A, 3, 50, 0.01, 'nonneg');
         */

        return new Matrix(A);
    }

    public static void main(String[] args)
    {
        Matrix A = TestData.testMat1().abs();//generateTestData();

        System.out.print("\n-------- A --------\n");
        A.print(4, 0);// print to a precision of 0 decimal places

        LinearNnmf NNMF = new LinearNnmf();
        NNMF.setData(A);
        NNMF.setRank(3);// reduce the original data dimension from 5 (columns)
                        // to 3;
        NNMF.setSvdInitialized(true);
        NNMF.setSvdInitializedOptimized(true);
        
        NNMF.computeNNMF();
        Matrix W = NNMF.getW();
        Matrix H = NNMF.getH();

        Matrix A_reconstructed = W.times(H);
        System.out.print("\n-------- A_reconstructed --------\n");
        A_reconstructed.print(4, 4);// print to a precision of 4 decimal pl

        Matrix difference = A.minus(A_reconstructed);
        System.out.print("\n-------- difference --------\n");
        difference.print(4, 4);// print to a precision of 4 decimal pl

        System.out.print("\n\n");
    }
}
