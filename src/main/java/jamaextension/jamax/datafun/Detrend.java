/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.DeTrendMethod;
import jamaextension.jamax.ops.Unique;

/**
 * 
 * @author Sione
 */
public class Detrend
{ // DeTrendMethod

    private Matrix detrendData;
    private Matrix data;
    private DeTrendMethod trendMethod = DeTrendMethod.constant;
    private Indices breakPoints = null;
    private boolean buildTrend = false;

    public Detrend(Matrix data)
    {
        if (data == null)
        {
            throw new ConditionalException("Detrend : Parameter \"data\" must be non-null.");
        }
        this.data = data;
    }

    public void build()
    {
        Matrix x = this.data;
        int n = x.getRowDimension();// size(x,1);
        if (n == 1)
        {
            x = x.toColVector();// x(:); // If a row, turn into column vector
        }
        int N = x.getRowDimension();// size(x,1);

        if (this.trendMethod == DeTrendMethod.constant)
        {
            this.detrendData = constantTrend(x, N);
        }
        else if (this.trendMethod == DeTrendMethod.linear)
        {
            this.detrendData = linearTrend(x, N);
        }
        else
        {
            throw new ConditionalException("build : Unrecognized detrending method \"" + this.trendMethod.toString()
                    + "\".");
        }

        if (n == 1)
        {
            this.detrendData = this.detrendData.transpose();
        }// end

        buildTrend = true;
    }

    private Matrix linearTrend(Matrix x, int N)
    {
        Matrix y = null;
        Matrix merg = Matrix.zeros(1).mergeVerti(breakPoints, new Indices(1, 1, N - 1));
        // bp = unique([0;double(bp(:));N-1]); // Include both endpoints
        Unique UQ = new Unique(merg);
        Indices bp = UQ.getUniqueData().toIndices();
        int lb = bp.length() - 1;
        Matrix tmp = null;

        // Build regressor with linear pieces + DC
        Matrix a = Matrix.zeros(N, lb).mergeH(Matrix.ones(N, 1));
        for (int kb = 0; kb < lb; kb++)
        {
            int M = N - bp.getElementAt(kb);
            tmp = Matrix.linspace(1.0, (double) M, M).transpose().arrayRightDivide((double) M);
            int[] arrInd = Indices.linspace(1, M).plus(bp.getElementAt(kb) - 1).getRowPackedCopy();// minus
                                                                                                   // 1,
                                                                                                   // since
                                                                                                   // Java
                                                                                                   // index
                                                                                                   // starts
                                                                                                   // at
                                                                                                   // zero.
            // a((1:M)+bp(kb),kb) = (1:M)'/M;
            a.setMatrix(arrInd, kb, tmp);
        }// end

        // Remove best fit
        tmp = a.solve(x);
        tmp = a.times(tmp);
        y = x.minus(tmp);

        return y;
    }

    private Matrix constantTrend(Matrix x, int N)
    {
        Matrix y = null;
        // Remove just mean from each column
        // y = x - ones(N,1)*mean(x);
        Matrix meanX = JDatafun.mean(x);
        if (x.isVector())
        {
            y = x.minus(new Matrix(N, 1, meanX.start()));
        }
        else
        {
            y = x.minus(Matrix.ones(N, 1).times(meanX));
        }
        return y;
    }

    /**
     * @return the detrendData
     */
    public Matrix getDetrendData()
    {
        if (!isBuildTrend())
        {
            throw new ConditionalException("getDetrendData : Method \"build\" must be called first.");
        }
        return detrendData;
    }

    /**
     * @param trendMethod
     *            the trendMethod to set
     */
    public void setTrendMethod(DeTrendMethod trendMethod)
    {
        if (trendMethod == null)
        {
            return;
        }
        this.trendMethod = trendMethod;
    }

    /**
     * @param breakPoints
     *            the breakPoints to set
     */
    public void setBreakPoints(Indices breakPoints)
    {
        if (breakPoints == null)
        {
            return;
        }
        if (!breakPoints.isVector())
        {
            throw new ConditionalException(
                    "setBreakPoints : Parameter \"breakPoints\" must be a vector and not a matrix.");
        }
        boolean cond = breakPoints.LT(3).anyBoolean();
        if (cond)
        {
            throw new ConditionalException("setBreakPoints : Elements of parameter \"breakPoints\" must be at least 3;");
        }
        if (breakPoints.isRowVector())
        {
            this.breakPoints = breakPoints.toColVector();
        }
        else
        {
            this.breakPoints = breakPoints;
        }
    }

    public void setBreakPoints(int bPoint)
    {
        setBreakPoints(new Indices(1, 1, bPoint));
    }

    /**
     * @return the buildTrend
     */
    public boolean isBuildTrend()
    {
        return buildTrend;
    }

    static void example1()
    {
        double[][] arr =
        {
                {
                        0.111111111111111, 0, 1.000000000000000
                },
                {
                        0.222222222222222, 0, 1.000000000000000
                },
                {
                        0.333333333333333, 0, 1.000000000000000
                },
                {
                        0.444444444444444, 0, 1.000000000000000
                },
                {
                        0.555555555555556, 0, 1.000000000000000
                },
                {
                        0.666666666666667, 0.250000000000000, 1.000000000000000
                },
                {
                        0.777777777777778, 0.500000000000000, 1.000000000000000
                },
                {
                        0.888888888888889, 0.750000000000000, 1.000000000000000
                },
                {
                        1.000000000000000, 1.000000000000000, 1.000000000000000
                }
        };

        Matrix a = new Matrix(arr);

        double[] xar =
        {
                0, 2, 0, 4, 4, 4, 0, 2, 0
        };

        Matrix x = new Matrix(xar).transpose();

        Matrix at = a.transpose();

        Matrix Z = a.solve(x);
        Z.printInLabel("Z");
    }

    static void test1()
    {
        double[] sigArr =
        {
                0, 1, -2, 1, 0, 1, -2, 1, 0
        };
        Matrix sig = new Matrix(sigArr);
        double[] trendArr =
        {
                0, 1, 2, 3, 4, 3, 2, 1, 0
        };
        Matrix trend = new Matrix(trendArr);
        Matrix x = sig.plus(trend);
        Detrend DT = new Detrend(x);
        DT.setTrendMethod(DeTrendMethod.linear);
        DT.setBreakPoints(5);
        DT.build();

        Matrix y = DT.getDetrendData();
        y.printInLabel("y");
    }

    public static void main(String[] args)
    {
        test1();
    }
}
