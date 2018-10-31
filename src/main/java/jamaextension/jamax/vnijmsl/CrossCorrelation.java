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

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.Matrix;

public class CrossCorrelation implements Serializable, Cloneable
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

    static final long serialVersionUID = 0xc2409d3a4857f6edL;
    private double dataX[];
    private double dataY[];
    private double meanX;
    private double meanY;
    private int maxlag;
    private int nobs;

    public CrossCorrelation(Matrix adMat, Matrix adMat2, int i)
    {
        String msg = "";
        double[] ad = null;
        if (adMat == null)
        {
            msg = "The matrix parameter \"adMat\" must be non-null.";
            throw new ConditionalRuleException("CrossCorrelation", msg);
        }
        if (!adMat.isVector())
        {
            msg = "The matrix parameter \"adMat\" must be a vector and not a matrix.";
            throw new ConditionalRuleException("CrossCorrelation", msg);
        }
        if (adMat.isRowVector())
        {
            ad = adMat.getArray()[0];
        }
        else
        {
            ad = adMat.getColumnPackedCopy();
        }

        double[] ad2 = null;
        if (adMat2 == null)
        {
            msg = "The matrix parameter \"adMat2\" must be non-null.";
            throw new ConditionalRuleException("CrossCorrelation", msg);
        }
        if (!adMat2.isVector())
        {
            msg = "The matrix parameter \"adMat2\" must be a vector and not a matrix.";
            throw new ConditionalRuleException("CrossCorrelation", msg);
        }
        if (adMat2.isRowVector())
        {
            ad2 = adMat2.getArray()[0];
        }
        else
        {
            ad2 = adMat2.getColumnPackedCopy();
        }

        assembleParams(ad, ad2, i);
    }

    public CrossCorrelation(double ad[], double ad1[], int i)
    {
        assembleParams(ad, ad1, i);
    }

    private void assembleParams(double ad[], double ad1[], int i)
    {

        dataX = (double[]) ad.clone();
        dataY = (double[]) ad1.clone();
        int l = ad.length;
        int i1 = ad1.length;
        maxlag = i;
        if (l < 2)
        {
            Object aobj[] =
            {
                    "x", new Integer(l), new Integer(2)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "ArrayLength", aobj);
            throw new IllegalArgumentException("ArrayLength");
        }
        if (i1 < 2)
        {
            Object aobj1[] =
            {
                    "y", new Integer(i1), new Integer(2)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "ArrayLength", aobj1);
            throw new IllegalArgumentException("ArrayLength");
        }
        if (l <= i1)
        {
            nobs = l;
        }
        if (i1 <= l)
        {
            nobs = i1;
        }
        if (l == i1)
            ;
        if (maxlag < 1 || maxlag >= nobs)
        {
            Object aobj2[] =
            {
                    "maximum_lag", new Integer(maxlag), new Integer(1), new Integer(nobs)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "OutOfRange", aobj2);
            throw new IllegalArgumentException("OutOfRange");
        }
        for (int j = 0; j < nobs; j++)
        {
            if (Double.isNaN(dataX[j]))
            {
                Object aobj3[] =
                {
                        "x", new Integer(j)
                };
                // Messages.throwIllegalArgumentException("com.imsl.stat",
                // "ContainsNaN", aobj3);
                throw new IllegalArgumentException("ContainsNaN");
            }
        }

        for (int k = 0; k < nobs; k++)
        {
            if (Double.isNaN(dataY[k]))
            {
                Object aobj4[] =
                {
                        "y", new Integer(k)
                };
                // Messages.throwIllegalArgumentException("com.imsl.stat",
                // "ContainsNaN", aobj4);
                throw new IllegalArgumentException("ContainsNaN");
            }
        }

        if (nobs > 1)
        {
            if (dataX[BLAS.ismin(nobs, dataX, 0, 1) - 1] == dataX[BLAS.ismax(nobs, dataX, 0, 1) - 1])
            {
                Object aobj5[] =
                {
                        "x", new Double(dataX[BLAS.ismin(nobs, dataX, 0, 1) - 1])
                };
                // Messages.throwIllegalArgumentException("com.imsl.stat",
                // "ConstantSeries", aobj5);
                throw new IllegalArgumentException("ConstantSeries");
            }
            if (dataY[BLAS.ismin(nobs, dataY, 0, 1) - 1] == dataY[BLAS.ismax(nobs, dataY, 0, 1) - 1])
            {
                Object aobj6[] =
                {
                        "y", new Double(dataY[BLAS.ismin(nobs, dataY, 0, 1) - 1])
                };
                // Messages.throwIllegalArgumentException("com.imsl.stat",
                // "ConstantSeries", aobj6);
                throw new IllegalArgumentException("ConstantSeries");
            }
        }
        meanX = BLAS.sum(nobs, ad, 0, 1) / (double) nobs;
        meanY = BLAS.sum(nobs, ad1, 0, 1) / (double) nobs;
    }

    public void setMeanX(double d)
    {
        meanX = d;
    }

    public double getMeanX()
    {
        return meanX;
    }

    public void setMeanY(double d)
    {
        meanY = d;
    }

    public double getMeanY()
    {
        return meanY;
    }

    public double getVarianceX() // throws NonPosVariancesException
    {
        double d = 0.0D;
        for (int i = 0; i < nobs; i++)
        {
            d += ((dataX[i] - meanX) * (dataX[i] - meanX)) / (double) nobs;
        }

        if (d < 4.9406564584124654E-324D)
        {
            Object aobj[] =
            {
                    "Variance of X", new Double(d)
            };
            // throw new NonPosVariancesException("NotPositive", aobj);
            throw new IllegalArgumentException("NotPositive");
        }
        else
        {
            return d;
        }
    }

    public double getVarianceY() // throws NonPosVariancesException
    {
        double d = 0.0D;
        for (int i = 0; i < nobs; i++)
        {
            d += ((dataY[i] - meanY) * (dataY[i] - meanY)) / (double) nobs;
        }

        if (d < 4.9406564584124654E-324D)
        {
            Object aobj[] =
            {
                    "Variance of Y", new Double(d)
            };
            // throw new NonPosVariancesException("NotPositive", aobj);
            throw new IllegalArgumentException("NotPositive");
        }
        else
        {
            return d;
        }
    }

    public Matrix getCrossCorrelation() // throws NonPosVariancesException
    {
        double d = 0.0D;
        double d1 = 0.0D;
        double ad[] = new double[2 * maxlag + 1];
        double ad1[] = new double[2 * maxlag + 1];
        BLAS.set(2 * maxlag + 1, 0.0D, ad1, 0);
        ad = getCrossCovariance().getRowPackedCopy();
        try
        {
            d = getVarianceX();
        }
        catch (Exception exception)
        {
        }
        try
        {
            d1 = getVarianceY();
        }
        catch (Exception exception1)
        {
        }
        BLAS.dvcal(2 * maxlag + 1, 1.0D / Math.sqrt(d * d1), ad, 0, 1, ad1, 0, 1);
        return new Matrix(ad1);
    }

    public Matrix getCrossCovariance()
    {
        double ad[] = new double[2 * maxlag + 1];
        BLAS.set(2 * maxlag + 1, 0.0D, ad, 0);
        for (int k = -maxlag; k <= maxlag; k++)
        {
            int l;
            int i1;
            if (k >= 0)
            {
                i1 = 1;
                l = nobs - k;
            }
            else
            {
                i1 = 1 - k;
                l = nobs;
            }
            for (int i = i1; i <= l; i++)
            {
                int j = i - 1;
                ad[k + maxlag] += (dataX[j] - meanX) * (dataY[j + k] - meanY);
            }

            ad[k + maxlag] /= nobs;
        }

        return new Matrix(ad);
    }

    public Matrix getAutoCorrelationX() // throws NonPosVariancesException
    {
        // double ad[] = new double[maxlag + 1];
        Matrix ad = null;
        AutoCorrelation autocorrelation = new AutoCorrelation(dataX, maxlag);
        try
        {
            ad = autocorrelation.getAutoCorrelations();

        }
        catch (Exception exception)
        {
        }
        return ad;
    }

    public Matrix getAutoCorrelationY() // throws NonPosVariancesException
    {
        // double ad[] = new double[maxlag + 1];
        Matrix ad = null;
        AutoCorrelation autocorrelation = new AutoCorrelation(dataY, maxlag);
        try
        {
            ad = autocorrelation.getAutoCorrelations();
        }
        catch (Exception exception)
        {
        }
        return ad;
    }

    public Matrix getAutoCovarianceX() // throws NonPosVariancesException
    {
        // double ad[] = new double[maxlag + 1];
        Matrix ad = null;
        AutoCorrelation autocorrelation = new AutoCorrelation(dataX, maxlag);
        try
        {
            ad = autocorrelation.getAutoCovariances();
        }
        catch (Exception exception)
        {
        }
        return ad;
    }

    public Matrix getAutoCovarianceY() // throws NonPosVariancesException
    {
        // double ad[] = new double[maxlag + 1];
        Matrix ad = null;
        AutoCorrelation autocorrelation = new AutoCorrelation(dataY, maxlag);
        try
        {
            ad = autocorrelation.getAutoCovariances();
        }
        catch (Exception exception)
        {
        }
        return ad;
    }

    public Matrix getStandardErrors(CrossCorrelStdErrMethod method) // throws
                                                                    // NonPosVariancesException
    {
        /*
         * if (method < 1 || method > 2) { Object aobj[] = { "stderrMethod", new
         * Integer(method), new Integer(0), new Integer(3) };
         * //Messages.throwIllegalArgumentException("com.imsl.stat",
         * "OutOfRange", aobj); throw new
         * IllegalArgumentException("OutOfRange"); }
         */

        if (method == null)
        {
            throw new IllegalArgumentException("getStandardErrors : Parameter \"method\" must be non-null.");
        }

        double ad2[] = new double[2 * maxlag + 1];
        double ad[] = new double[maxlag + 1];
        double ad1[] = new double[maxlag + 1];
        double ad3[] = new double[2 * maxlag + 1];
        try
        {
            ad3 = getCrossCorrelation().getRowPackedCopy();
        }
        catch (Exception exception)
        {
        }
        AutoCorrelation autocorrelation = new AutoCorrelation(dataX, maxlag);
        ad = autocorrelation.getAutoCorrelations().getRowPackedCopy();
        autocorrelation = new AutoCorrelation(dataY, maxlag);
        ad1 = autocorrelation.getAutoCorrelations().getRowPackedCopy();
        if (method == CrossCorrelStdErrMethod.BARTLETTS_FORMULA)
        {
            BLAS.set(2 * maxlag + 1, 0.0D, ad2, 0);
            for (int j = 0; j <= maxlag; j++)
            {
                for (int i1 = -maxlag; i1 <= maxlag; i1++)
                {
                    ad2[j + maxlag] += ad[Math.abs(i1)] * ad1[Math.abs(i1)];
                }

                for (int j1 = -(maxlag - j); j1 <= maxlag - j; j1++)
                {
                    ad2[j + maxlag] += ad3[j + j1 + maxlag] * ad3[(j - j1) + maxlag];
                }

                for (int k1 = -maxlag; k1 <= maxlag; k1++)
                {
                    ad2[j + maxlag] += ad3[j + maxlag]
                            * ad3[j + maxlag]
                            * (ad3[k1 + maxlag] * ad3[k1 + maxlag] + 0.5D * ad[Math.abs(k1)] * ad[Math.abs(k1)] + 0.5D
                                    * ad1[Math.abs(k1)] * ad1[Math.abs(k1)]);
                }

                for (int l1 = -maxlag; l1 <= maxlag - j; l1++)
                {
                    double d = ad1[Math.abs(l1 + j)];
                    ad2[j + maxlag] += -2D * ad3[j + maxlag]
                            * (ad[Math.abs(l1)] * ad3[l1 + j + maxlag] + ad3[-l1 + maxlag] * d);
                }

                ad2[j + maxlag] = Math.sqrt(Math.max(ad2[j + maxlag] / (double) (nobs - j), 0.0D));
            }

            for (int k = -maxlag; k <= -1; k++)
            {
                for (int i2 = -maxlag; i2 <= maxlag; i2++)
                {
                    ad2[k + maxlag] += ad[Math.abs(i2)] * ad1[Math.abs(i2)];
                }

                for (int j2 = -(maxlag + k); j2 <= maxlag + k; j2++)
                {
                    ad2[k + maxlag] += ad3[k + j2 + maxlag] * ad3[(k - j2) + maxlag];
                }

                for (int k2 = -maxlag; k2 <= maxlag; k2++)
                {
                    ad2[k + maxlag] += ad3[k + maxlag]
                            * ad3[k + maxlag]
                            * (ad3[-k2 + maxlag] * ad3[-k2 + maxlag] + 0.5D * ad[Math.abs(k2)] * ad[Math.abs(k2)] + 0.5D
                                    * ad1[Math.abs(k2)] * ad1[Math.abs(k2)]);
                }

                for (int l2 = -maxlag; l2 <= maxlag + k; l2++)
                {
                    double d1 = ad1[Math.abs(l2)];
                    ad2[k + maxlag] += -2D * ad3[k + maxlag]
                            * (ad[Math.abs(l2 - k)] * ad3[l2 + maxlag] + ad3[(k - l2) + maxlag] * d1);
                }

                ad2[k + maxlag] = Math.sqrt(Math.max(ad2[k + maxlag] / (double) (nobs + k), 0.0D));
            }

        }
        else if (method == CrossCorrelStdErrMethod.BARTLETTS_FORMULA_NOCC)
        {
            double d2 = 1.0D + 2D * BLAS.dot(maxlag, ad, 1, ad1, 1);
            for (int l = -maxlag; l <= maxlag; l++)
            {
                ad2[l + maxlag] = Math.sqrt(Math.max(d2 / (double) (nobs - Math.abs(l)), 0.0D));
            }

        }
        return new Matrix(ad2);
    }
}
