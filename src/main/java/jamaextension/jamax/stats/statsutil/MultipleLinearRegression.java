/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.stats.statsutil;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.QRDecomposition;

/**
 * ***********************************************************************
 * Compilation: javac -classpath jama.jar:. MultipleLinearRegression.java
 * Execution: java -classpath jama.jar:. MultipleLinearRegression Dependencies:
 * jama.jar
 * 
 * Compute least squares solution to X beta = y using Jama library. Assumes X
 * has full column rank.
 * 
 * http://math.nist.gov/javanumerics/jama/
 * http://math.nist.gov/javanumerics/jama/Jama-1.0.1.jar
 * 
 * This class was cut & pasted from the following link:
 * http://introcs.cs.princeton
 * .edu/java/97data/MultipleLinearRegression.java.html
 * 
 ************************************************************************ 
 */
public class MultipleLinearRegression
{

    private final int N; // number of
    private final int p; // number of dependent variables
    private final Matrix beta; // reg coefficients
    private double SSE; // sum of squared
    private double SST; // sum of squared

    public MultipleLinearRegression(Matrix x, Matrix y)
    {
        this(x.getArray(), y.getRowPackedCopy());
    }

    public MultipleLinearRegression(double[][] x, double[] y)
    {
        if (x.length != y.length)
        {
            throw new RuntimeException("dimensions don't agree");
        }
        N = y.length;
        p = x[0].length;

        Matrix X = new Matrix(x);

        // create matrix from vector
        Matrix Y = new Matrix(y, N);

        // find least squares solution
        QRDecomposition qr = new QRDecomposition(X);
        beta = qr.solve(Y);

        // mean of y[] values
        double sum = 0.0;
        for (int i = 0; i < N; i++)
        {
            sum += y[i];
        }
        double mean = sum / N;

        // total variation to be accounted for
        for (int i = 0; i < N; i++)
        {
            double dev = y[i] - mean;
            SST += dev * dev;
        }

        // variation not accounted for
        Matrix residuals = X.times(beta).minus(Y);
        SSE = residuals.norm2() * residuals.norm2();

    }

    public double beta(int j)
    {
        return beta.get(j, 0);
    }

    public double R2()
    {
        return 1.0 - SSE / SST;
    }

    public static void main(String[] args)
    {
        double[][] x =
        {
                {
                        1, 10, 20
                },
                {
                        1, 20, 40
                },
                {
                        1, 40, 15
                },
                {
                        1, 80, 100
                },
                {
                        1, 160, 23
                },
                {
                        1, 200, 18
                }
        };
        double[] y =
        {
                243, 483, 508, 1503, 1764, 2129
        };
        MultipleLinearRegression reg = new MultipleLinearRegression(x, y);

        System.out.printf("%.2f + %.2f beta1 + %.2f beta2  (R^2 = %.2f)\n", reg.beta(0), reg.beta(1), reg.beta(2),
                reg.R2());
    }
}
