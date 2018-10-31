/*
 * OrdinaryLeastSquares.java
 *
 * Created on 26 January 2008, 00:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax;

import jamaextension.jamax.datafun.JDatafun;

/**
 * The original code from 'ols.m' from the book :
 * "An_Introduction_to_Classical_Econometric_Theory"
 * 
 * @author Feynman Perceptrons
 */
public class OrdinaryLeastSquares
{

    private Matrix variance;
    private Matrix coefficients;
    private double sumOfSquareErrors = 0.0;
    private double totalSumOfSquares = 0.0;

    /**
     * Creates a new instance of OrdinaryLeastSquares
     */
    public OrdinaryLeastSquares(Matrix X, Matrix yDependent)
    {
        if (X == null)
        {
            throw new IllegalArgumentException(
                    "MultiCollinearityBasis : Parameter \"X\" (predictors or independent variables) must be non-null.");
        }
        if (X.isVector())
        {
            throw new IllegalArgumentException(
                    "MultiCollinearityBasis : Parameter \"X\" (predictors or independent variables) must be a matrix and not a vector.");
        }
        if (yDependent == null)
        {
            throw new IllegalArgumentException(
                    "MultiCollinearityBasis : Parameter \"yDependent\" (dependent variable) must be non-null.");
        }
        if (!yDependent.isVector())
        {
            throw new IllegalArgumentException(
                    "MultiCollinearityBasis : Parameter \"yDependent\" (dependent variable) must be a vector and not a matrix.");
        }

        int n = X.getRowDimension();
        int k = X.getColumnDimension();
        if (k == n)
        {
            throw new IllegalArgumentException(
                    "MultiCollinearityBasis : Parameter \"X\" (predictors or independent variables) can't be a square matrix. It must be rectangular.");
        }

        int lenY = yDependent.length();

        if (n != lenY)
        {
            throw new IllegalArgumentException(
                    "MultiCollinearityBasis : The number of observations in \"X\" (rows) must equal the length of \"yDependent\".");
        }

        Indices basis = MatrixUtil.findMultiCollinear(X);

        Matrix y = null;
        if (yDependent.isRowVector())
        {
            y = yDependent.toColVector();
        }
        else
        {
            y = yDependent;
        }

        // System.out.println("--------------- y ---------------");
        // y.toRowVector().print(6,0);

        int[] arr = basis.find().getColumnAt(0).getColumnPackedCopy();

        // System.out.println("--------------- X ---------------");
        // X.print(6,0);

        // fit coefficients to basis vectors for Col(X); see Section 2.4,
        // Orthogonal Projection
        Matrix Z = X.getColumns(arr);// X(:,basis); //final basis for Col(X)

        // System.out.println("--------------- Z ---------------");
        // Z.print(6,0);

        Matrix ZttZInv = Z.transpose().times(Z).inverse();
        Matrix c = ZttZInv.times(Z.transpose()).times(y);// inv(Z'*Z)*Z'*y; //
                                                         // OLS fitted
                                                         // coefficients (this
                                                         // could also be c =
                                                         // Z\y)

        // System.out.println("--------------- c ---------------");
        // c.print(6,4);

        Matrix b = Matrix.zeros(k, 1); // expand coefficient vector to include
                                       // zeros for collinear RHS variables
        b.setElements(arr, c);

        coefficients = b;

        Matrix mu = X.times(b); // fitted values
        Matrix temp = y.minus(mu);
        double ess = temp.transpose().times(temp).get(0, 0);// (y-mu)'*(y-mu);
                                                            // //error sum of
                                                            // squares
        double avgy = JDatafun.mean(y).get(0, 0); // sample mean of y
        temp = y.minus(avgy);
        double tss = temp.transpose().times(temp).get(0, 0); // total sum of
                                                             // squares

        // compute estimator of the conditional variance of each element of y
        // conditional on X;
        // see Section 8.2, Second-Moment Properties, Section 8.3, Variance and
        // Covariance Matrices,
        // and Section 8.4, Estimation of the Variance Parameter
        double s2 = ess / (double) (n - k); // (sum of squared
                                            // residuals)/(degrees of freedom)
        Matrix Vb = Matrix.zeros(k, k);
        temp = Z.transpose().times(Z).inverse().arrayTimes(s2);// s2*inv(Z'*Z);
        Vb.setMatrix(arr, arr, temp);

        variance = Vb;
        sumOfSquareErrors = ess;
        totalSumOfSquares = tss;
        // System.out.println("-------------- Vb --------------");
        // Vb.print(8,12);

        // System.out.println("-------------- b --------------");
        // b.print(8,4);

    }

    public Matrix getVariance()
    {
        return this.variance;
    }

    public Matrix getCoefficients()
    {
        return this.coefficients;
    }

    public double getSumOfSquareErrors()
    {
        return this.sumOfSquareErrors;
    }

    public double getTotalSumOfSquares()
    {
        return this.totalSumOfSquares;
    }

    public static void main(String[] args)
    {
        double[][] xx =
        {
                {
                        13, 14, 5, 0, 7, 14, 4
                },
                {
                        5, 12, 11, 2, 8, 8, 12
                },
                {
                        14, 9, 10, 12, 0, 15, 3
                },
                {
                        8, 12, 5, 12, 3, 12, 6
                },
                {
                        4, 14, 10, 15, 3, 7, 5
                },
                {
                        3, 4, 0, 9, 4, 3, 11
                },
        };

        Matrix X = new Matrix(xx);

        Matrix y = new Matrix(new double[][]
        {
            {
                    4, 10, 7, 15, 6, 8
            }
        });

        OrdinaryLeastSquares OLS = new OrdinaryLeastSquares(X, y);

    }

}// ------------------------------ End Class Definition
// -------------------------
