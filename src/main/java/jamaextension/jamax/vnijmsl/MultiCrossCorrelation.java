/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

/**
 *
 * @author Feynman Perceptrons
 */
import java.io.Serializable;

import jamaextension.jamax.Matrix;

public class MultiCrossCorrelation implements Serializable, Cloneable
{
    /*
     * public static class NonPosVariancesException extends IMSLException {
     * 
     * static final long serialVersionUID = 0xc83900b3c4d4a684L;
     * 
     * public NonPosVariancesException(String s) { super(s); }
     * 
     * public NonPosVariancesException(String s, Object aobj[]) {
     * super("com.imsl.stat", s, aobj); } }
     * 
     * 
     * private static final int check = Messages.check(1);
     */

    static final long serialVersionUID = 0xed6d5ffb73363182L;
    private double dataX[][];
    private double dataY[][];
    private int maxlag;
    private int nobsX;
    private int nobsY;
    private int nchanX;
    private int nchanY;
    private double meanX[];
    private double meanY[];

    public MultiCrossCorrelation(double ad[][], double ad1[][], int i)
    {
        dataX = new Matrix(ad).transpose().getArray();
        dataY = new Matrix(ad1).transpose().getArray();
        nobsX = dataX[0].length;
        nobsY = dataY[0].length;
        nchanX = dataX.length;
        nchanY = dataY.length;
        maxlag = i;
        int k = nobsX;
        int j = nobsY;
        int i1 = nchanX;
        int l = nchanX;
        int j1 = nchanY;
        int k1 = nchanY;
        if (nobsX < 2)
        {
            Object aobj[] =
            {
                    "Number of observations in x", new Integer(nobsX), new Integer(2)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "ArrayLength", aobj);
            throw new IllegalArgumentException("ArrayLength");
        }
        if (nobsY < 2)
        {
            Object aobj1[] =
            {
                    "Number of observations in y", new Integer(nobsY), new Integer(2)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "ArrayLength", aobj1);
            throw new IllegalArgumentException("ArrayLength");
        }
        if (nchanX < 1)
        {
            Object aobj2[] =
            {
                    "x", new Integer(nchanX), new Integer(1)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "ArrayLength", aobj2);
            throw new IllegalArgumentException("ArrayLength");
        }
        if (nchanY < 1)
        {
            Object aobj3[] =
            {
                    "y", new Integer(nchanY), new Integer(1)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "ArrayLength", aobj3);
            throw new IllegalArgumentException("ArrayLength");
        }
        if (maxlag < 1 || maxlag >= Math.min(nobsX, nobsY))
        {
            Object aobj4[] =
            {
                    "maximum_lag", new Integer(maxlag), new Integer(1), new Integer(Math.min(nobsX, nobsY))
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "OutOfRange", aobj4);
            throw new IllegalArgumentException("OutOfRange");
        }
        for (int l1 = 0; l1 < nchanX; l1++)
        {
            int l2 = BLAS.ismin(nobsX, dataX[l1], 0, 1) - 1;
            int j3 = BLAS.ismax(nobsX, dataX[l1], 0, 1) - 1;
            if (dataX[l1][l2] == dataX[l1][j3])
            {
                Object aobj5[] =
                {
                        "x", new Double(dataX[l1][BLAS.ismin(nobsX, dataX[l1], 0, 1) - 1])
                };
                // Messages.throwIllegalArgumentException("com.imsl.stat",
                // "ConstantSeries", aobj5);
                throw new IllegalArgumentException("ConstantSeries");
            }
        }

        for (int i2 = 0; i2 < nchanY; i2++)
        {
            int i3 = BLAS.ismin(nobsY, dataY[i2], 0, 1) - 1;
            int k3 = BLAS.ismax(nobsY, dataY[i2], 0, 1) - 1;
            if (dataY[i2][i3] == dataY[i2][k3])
            {
                Object aobj6[] =
                {
                        "Y", new Double(dataY[i2][BLAS.ismin(nobsY, dataY[i2], 0, 1) - 1])
                };
                // Messages.throwIllegalArgumentException("com.imsl.stat",
                // "ConstantSeries", aobj6);
                throw new IllegalArgumentException("ConstantSeries");
            }
        }

        meanX = new double[nchanX];
        for (int j2 = 0; j2 < nchanX; j2++)
        {
            meanX[j2] = BLAS.sum(nobsX, dataX[j2], 0, 1) / (double) nobsX;
        }

        meanY = new double[nchanY];
        for (int k2 = 0; k2 < nchanY; k2++)
        {
            meanY[k2] = BLAS.sum(nobsY, dataY[k2], 0, 1) / (double) nobsY;
        }

    }

    public void setMeanX(double ad[])
    {
        meanX = ad;
    }

    public double[] getMeanX()
    {
        return meanX;
    }

    public void setMeanY(double ad[])
    {
        meanY = ad;
    }

    public double[] getMeanY()
    {
        return meanY;
    }

    public double[] getVarianceX() // throws NonPosVariancesException
    {
        double ad[] = new double[nchanX];
        for (int i = 0; i < nchanX; i++)
        {
            ad[i] = 0.0D;
            for (int j = 0; j < nobsX; j++)
            {
                ad[i] += ((dataX[i][j] - meanX[i]) * (dataX[i][j] - meanX[i])) / (double) nobsX;
            }

            if (ad[i] < 4.9406564584124654E-324D)
            {
                Object aobj[] =
                {
                        "Variance of X", new Double(ad[i])
                };
                // throw new NonPosVariancesException("NotPositive", aobj);
                throw new IllegalArgumentException("NotPositive");
            }
        }

        return ad;
    }

    public double[] getVarianceY() // throws NonPosVariancesException
    {
        double ad[] = new double[nchanY];
        for (int i = 0; i < nchanY; i++)
        {
            ad[i] = 0.0D;
            for (int j = 0; j < nobsY; j++)
            {
                ad[i] += ((dataY[i][j] - meanY[i]) * (dataY[i][j] - meanY[i])) / (double) nobsY;
            }

            if (ad[i] < 4.9406564584124654E-324D)
            {
                Object aobj[] =
                {
                        "Variance of Y", new Double(ad[i])
                };
                // throw new NonPosVariancesException("NotPositive", aobj);
                throw new IllegalArgumentException("NotPositive");
            }
        }

        return ad;
    }

    public double[][][] getCrossCorrelation() // throws NonPosVariancesException
    {
        double ad[][][] = new double[2 * maxlag + 1][nchanX][nchanY];
        double ad1[][][] = new double[2 * maxlag + 1][nchanX][nchanY];
        double ad2[] = new double[nchanX];
        double ad3[] = new double[nchanY];
        try
        {
            ad2 = getVarianceX();
        }
        catch (Exception exception)
        {
        }
        try
        {
            ad3 = getVarianceY();
        }
        catch (Exception exception1)
        {
        }
        ad = getCrossCovariance();
        for (int j = -maxlag; j <= maxlag; j++)
        {
            for (int k = 0; k < nchanY; k++)
            {
                for (int i = 0; i < nchanX; i++)
                {
                    if (ad2[i] == 0.0D || ad3[k] == 0.0D)
                    {
                        ad1[j + maxlag][i][k] = (0.0D / 0.0D);
                    }
                    else
                    {
                        ad1[j + maxlag][i][k] = ad[j + maxlag][i][k] / Math.sqrt(ad2[i] * ad3[k]);
                    }
                }

            }

        }

        return ad1;
    }

    public double[][][] getCrossCovariance() // throws NonPosVariancesException
    {
        double ad[][][] = new double[2 * maxlag + 1][nchanX][nchanY];
        double ad1[][][] = new double[2 * maxlag + 1][nchanX][nchanY];
        double ad2[] = new double[nchanX];
        double ad3[] = new double[nchanY];
        try
        {
            ad2 = getVarianceX();
        }
        catch (Exception exception)
        {
        }
        try
        {
            ad3 = getVarianceY();
        }
        catch (Exception exception1)
        {
        }
        int k = nobsY - nobsX;
        int i1 = 0;
        int j1 = 0;
        for (int j = -maxlag; j <= maxlag; j++)
        {
            if (k == 0)
            {
                if (j < 0)
                {
                    j1 = 1 - j;
                    i1 = nobsX;
                }
                else if (j >= 0)
                {
                    j1 = 1;
                    i1 = nobsX - j;
                }
            }
            else if (k > 0)
            {
                if (j < 0)
                {
                    j1 = 1 - j;
                    i1 = nobsX;
                }
                else if (j >= 0 && j <= k)
                {
                    j1 = 1;
                    i1 = nobsX;
                }
                else if (j > k)
                {
                    j1 = 1;
                    i1 = nobsY - j;
                }
            }
            else if (k < 0)
            {
                if (j < k)
                {
                    j1 = 1 - j;
                    i1 = nobsX;
                }
                else if (j >= k && j <= 0)
                {
                    j1 = 1 - j;
                    i1 = nobsY - j;
                }
                else if (j > 0)
                {
                    j1 = 1;
                    i1 = nobsY - j;
                }
            }
            int l = (i1 - j1) + 1;
            for (int i2 = 0; i2 < nchanY; i2++)
            {
                for (int i = 0; i < nchanX; i++)
                {
                    double d = 0.0D;
                    for (int k1 = j1; k1 <= i1; k1++)
                    {
                        int l1 = k1 - 1;
                        d += (dataX[i][l1] - meanX[i]) * (dataY[i2][l1 + j] - meanY[i2]);
                    }

                    ad[j + maxlag][i][i2] = d / (double) l;
                }

            }

        }

        return ad;
    }
}
