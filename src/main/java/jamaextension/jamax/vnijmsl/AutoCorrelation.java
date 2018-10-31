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

public class AutoCorrelation implements Serializable, Cloneable
{

    static final long serialVersionUID = 0x84d13433a6ca8c83L;
    private double data[];
    private double xmean;
    private int maxlag;
    private int nobs;

    // public static final int BARTLETTS_FORMULA = 1;
    // public static final int MORANS_FORMULA = 2;

    public AutoCorrelation(Matrix adMat, int i)
    {

        if (adMat == null)
        {
            throw new IllegalArgumentException("AutoCorrelation : Parameter \"adMat\" must be non-null.");
        }
        if (!adMat.isVector())
        {
            throw new IllegalArgumentException(
                    "AutoCorrelation : Parameter \"adMat\" must be a vector and not a matrix.");
        }

        if (adMat.isColVector())
        {
            adMat = adMat.toRowVector();
        }

        double[] ad = adMat.getArray()[0];
        data = (double[]) ad.clone();
        maxlag = i;
        nobs = data.length;
        if (nobs < 2)
        {
            Object aobj[] =
            {
                    "x", new Integer(data.length), new Integer(2)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "ArrayLength", aobj);
            throw new IllegalArgumentException("ArrayLength");
        }
        if (maxlag < 1 || maxlag >= nobs)
        {
            Object aobj1[] =
            {
                    "maximum_lag", new Integer(maxlag), new Integer(1), new Integer(nobs)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "OutOfRange", aobj1);
            throw new IllegalArgumentException("OutOfRange");
        }
        for (int j = 0; j < nobs; j++)
        {
            if (Double.isNaN(data[j]))
            {
                Object aobj2[] =
                {
                        "x", new Integer(j)
                };
                // Messages.throwIllegalArgumentException("com.imsl.stat",
                // "ContainsNaN", aobj2);
                throw new IllegalArgumentException("ContainsNaN");
            }
        }

        if (nobs > 1 && ad[BLAS.ismin(nobs, ad, 0, 1) - 1] == ad[BLAS.ismax(nobs, ad, 0, 1) - 1])
        {
            Object aobj3[] =
            {
                    "x", new Double(ad[BLAS.ismin(nobs, ad, 0, 1) - 1])
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "ConstantSeries", aobj3);
            throw new IllegalArgumentException("ConstantSeries");
        }
        xmean = BLAS.sum(nobs, ad, 0, 1) / (double) nobs;
    }

    public AutoCorrelation(double ad[], int i)
    {
        this(new Matrix(ad), i);
    }

    public void setMean(double d)
    {
        xmean = d;
    }

    public double getMean()
    {
        return xmean;
    }

    public Matrix getStandardErrors(AutoCorrelStdMethod method)
    {
        if (method == null)
        {
            throw new IllegalArgumentException("getStandardErrors : Parameter \"method\" must be non-null.");
        }
        /*
         * if (method < 1 && method > 2) { Object aobj[] = { "stderrMethod", new
         * Integer(method), new Integer(0), new Integer(3) };
         * //Messages.throwIllegalArgumentException("com.imsl.stat",
         * "OutOfRange", aobj); throw new
         * IllegalArgumentException("OutOfRange"); }
         */
        double ad1[] = new double[maxlag + 1];
        double[][] matArr = getAutoCorrelations().getArray();
        // ad1 = getAutoCorrelations();
        ad1 = matArr[0];
        double ad[] = new double[maxlag];
        if (method == AutoCorrelStdMethod.BARTLETTS_FORMULA)
        {
            BLAS.set(maxlag, 0.0D, ad, 0);
            for (int j = 1; j <= maxlag; j++)
            {
                for (int l = -maxlag; l <= maxlag; l++)
                {
                    ad[j - 1] += ad1[Math.abs(l)] * ad1[Math.abs(l)] * (1.0D + 2D * ad1[j] * ad1[j]);
                }

                for (int i1 = -(maxlag - j); i1 <= maxlag - j; i1++)
                {
                    ad[j - 1] += ad1[Math.abs(i1 + j)] * ad1[Math.abs(i1 - j)];
                }

                for (int j1 = -(maxlag - j); j1 <= maxlag; j1++)
                {
                    ad[j - 1] += -4D * ad1[j] * ad1[Math.abs(j1)] * ad1[Math.abs(j1 - j)];
                }

                ad[j - 1] = Math.sqrt((1.0D / (double) nobs) * ad[j - 1]);
            }

        }
        else if (method == AutoCorrelStdMethod.MORANS_FORMULA)
        {
            for (int k = 1; k <= maxlag; k++)
            {
                ad[k - 1] = Math.sqrt((double) (nobs - k) / (double) (nobs * (nobs + 2)));
            }

        }
        return new Matrix(ad);
    }

    public Matrix getAutoCorrelations()
    {
        double ad[] = new double[maxlag + 1];
        double ad1[] = new double[maxlag + 1];
        try
        {
            double[][] adMat = getAutoCovariances().getArray();
            ad = adMat[0];
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        int i = maxlag + 1;
        double d = 1.0D / ad[0];
        int j = 1;
        int k = 1;
        BLAS.dvcal(i, d, ad, 0, j, ad1, 0, k);
        return new Matrix(ad1);
    }

    public Matrix getAutoCovariances()
    { // throws NonPosVariancesException

        double ad[] = new double[maxlag + 1];
        BLAS.set(maxlag + 1, 0.0D, ad, 0);
        for (int j = 0; j <= maxlag; j++)
        {
            for (int i = 1; i <= nobs - j; i++)
            {
                ad[j] += ((data[i - 1] - xmean) * (data[(i + j) - 1] - xmean)) / (double) nobs;
            }

        }

        if (ad[0] < 4.9406564584124654E-324D)
        {
            Object aobj[] =
            {
                    "Variance", new Double(ad[0])
            };
            // throw new NonPosVariancesException("NotPositive", aobj);
            throw new IllegalArgumentException("Variance NotPositive");
        }
        else
        {
            return new Matrix(ad);
        }
    }

    public double getVariance()
    {
        double ad[] = new double[maxlag + 1];
        try
        {
            double[][] adMat = getAutoCovariances().getArray();
            // ad = getAutoCovariances() ;
            ad = adMat[0];
        }
        catch (Exception exception)
        {
        }
        return ad[0];
    }

    public Matrix getPartialAutoCorrelations()
    {
        double ad[] = new double[maxlag];
        double ad1[] = new double[2 * maxlag];
        double ad2[] = new double[maxlag + 1];
        try
        {
            double[][] matAd = getAutoCorrelations().getArray();
            ad2 = matAd[0];// getAutoCorrelations();
        }
        catch (Exception exception)
        {
        }
        ad1[0] = ad2[1];
        ad[0] = ad2[1];
        for (int k = 2; k <= maxlag; k++)
        {
            int j1 = k - 1;
            ad1[j1] = (ad2[k] - BLAS.dot(k - 1, ad1, 0, 1, ad2, 1, -1))
                    / (1.0D - BLAS.dot(k - 1, ad1, 0, 1, ad2, 1, 1));
            ad[j1] = ad1[j1];
            for (int i = 1; i <= k - 1; i++)
            {
                int l = i - 1;
                ad1[maxlag + l] = ad1[l] - ad[j1] * ad1[j1 - i];
            }

            for (int j = 1; j <= k - 1; j++)
            {
                int i1 = j - 1;
                ad1[i1] = ad1[maxlag + i1];
            }

        }

        return new Matrix(ad);
    }

    public static void main(String args[]) throws Exception
    {
        double[] x =
        {
                100.8, 81.6, 66.5, 34.8, 30.6, 7, 19.8, 92.5, 154.4, 125.9, 84.8, 68.1, 38.5, 22.8, 10.2, 24.1, 82.9,
                132, 130.9, 118.1, 89.9, 66.6, 60, 46.9, 41, 21.3, 16, 6.4, 4.1, 6.8, 14.5, 34, 45, 43.1, 47.5, 42.2,
                28.1, 10.1, 8.1, 2.5, 0, 1.4, 5, 12.2, 13.9, 35.4, 45.8, 41.1, 30.4, 23.9, 15.7, 6.6, 4, 1.8, 8.5,
                16.6, 36.3, 49.7, 62.5, 67, 71, 47.8, 27.5, 8.5, 13.2, 56.9, 121.5, 138.3, 103.2, 85.8, 63.2, 36.8,
                24.2, 10.7, 15, 40.1, 61.5, 98.5, 124.3, 95.9, 66.5, 64.5, 54.2, 39, 20.6, 6.7, 4.3, 22.8, 54.8, 93.8,
                95.7, 77.2, 59.1, 44, 47, 30.5, 16.3, 7.3, 37.3, 73.9
        };

        AutoCorrelation ac = new AutoCorrelation(x, 20);

        // new PrintMatrix("AutoCovariances are:  ").print
        // (ac.getAutoCovariances());
        ac.getAutoCovariances().toColVector().printInLabel("AutoCovariances");

        System.out.println();
        // new PrintMatrix("AutoCorrelations are:  ").print
        // (ac.getAutoCorrelations());
        ac.getAutoCorrelations().toColVector().printInLabel("AutoCorrelations");

        System.out.println("Mean = " + ac.getMean());
        System.out.println();
        // new PrintMatrix("Standard Error using Bartlett are:  ").print
        // (ac.getStandardErrors(AutoCorrelStdMethod.BARTLETTS_FORMULA));
        ac.getStandardErrors(AutoCorrelStdMethod.BARTLETTS_FORMULA).toColVector()
                .printInLabel("Standard Error using Bartlett");

        System.out.println();
        // new PrintMatrix("Standard Error using Moran are:  ").print
        // (ac.getStandardErrors(AutoCorrelStdMethod.MORANS_FORMULA));
        ac.getStandardErrors(AutoCorrelStdMethod.MORANS_FORMULA).toColVector()
                .printInLabel("Standard Error using Moran");

        System.out.println();
        // new PrintMatrix("Partial AutoCovariances:  ").print
        // (ac.getPartialAutoCorrelations());
        ac.getPartialAutoCorrelations().toColVector().printInLabel("Partial AutoCovariances");

        ac.setMean(50);
        // new PrintMatrix("AutoCovariances are:  ").print
        // (ac.getAutoCovariances());
        ac.getAutoCovariances().toColVector().printInLabel("AutoCovariances");

        System.out.println();
        // new PrintMatrix("AutoCorrelations are:  ").print
        // (ac.getAutoCorrelations());
        ac.getAutoCorrelations().toColVector().printInLabel("AutoCorrelations");

        System.out.println();
        // new PrintMatrix("Standard Error using Bartlett are:  ").print
        // (ac.getStandardErrors(AutoCorrelStdMethod.BARTLETTS_FORMULA));
        ac.getStandardErrors(AutoCorrelStdMethod.BARTLETTS_FORMULA).toColVector()
                .printInLabel("Standard Error using Bartlett");

    }

}
