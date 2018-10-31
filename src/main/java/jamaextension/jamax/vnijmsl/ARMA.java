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
import java.util.ArrayList;
import java.util.List;

import jamaextension.jamax.Matrix;

public class ARMA implements Serializable, Cloneable
{

    /*
     * public static class IllConditionedException extends IMSLException {
     * 
     * static final long serialVersionUID = 0x5d30b762cc95af76L;
     * 
     * public IllConditionedException(String s) { super(s); }
     * 
     * public IllConditionedException(String s, Object aobj[]) {
     * super("com.imsl.stat", s, aobj); } }
     * 
     * public static class TooManyJacobianEvalException extends IMSLException {
     * 
     * static final long serialVersionUID = 0xfbdee892c0f05fc3L;
     * 
     * public TooManyJacobianEvalException(String s) { super(s); }
     * 
     * public TooManyJacobianEvalException(String s, Object aobj[]) {
     * super("com.imsl.stat", s, aobj); } }
     * 
     * public static class TooManyFcnEvalException extends IMSLException {
     * 
     * static final long serialVersionUID = 0x53dcae66365ca684L;
     * 
     * public TooManyFcnEvalException(String s) { super(s); }
     * 
     * public TooManyFcnEvalException(String s, Object aobj[]) {
     * super("com.imsl.stat", s, aobj); } }
     * 
     * public static class TooManyITNException extends IMSLException {
     * 
     * static final long serialVersionUID = 0x941c72b3759c1e87L;
     * 
     * public TooManyITNException(String s) { super(s); }
     * 
     * public TooManyITNException(String s, Object aobj[]) {
     * super("com.imsl.stat", s, aobj); } }
     * 
     * public static class MatrixSingularException extends IMSLException {
     * 
     * static final long serialVersionUID = 0x4af4e97c4a941064L;
     * 
     * public MatrixSingularException(String s) { super(s); }
     * 
     * public MatrixSingularException(String s, Object aobj[]) {
     * super("com.imsl.stat", s, aobj); } }
     * 
     * public static class NewInitialGuessException extends IMSLException {
     * 
     * static final long serialVersionUID = 0x6e3b2b525c1b6c3bL;
     * 
     * public NewInitialGuessException(String s) { super(s); }
     * 
     * public NewInitialGuessException(String s, Object aobj[]) {
     * super("com.imsl.stat", s, aobj); } }
     * 
     * public static class IncreaseErrRelException extends IMSLException {
     * 
     * static final long serialVersionUID = 0xfa9e49358a8fe208L;
     * 
     * public IncreaseErrRelException(String s) { super(s); }
     * 
     * public IncreaseErrRelException(String s, Object aobj[]) {
     * super("com.imsl.stat", s, aobj); } }
     * 
     * public static class TooManyCallsException extends IMSLException {
     * 
     * static final long serialVersionUID = 0x4de8633fed5a7e53L;
     * 
     * public TooManyCallsException(String s) { super(s); }
     * 
     * public TooManyCallsException(String s, Object aobj[]) {
     * super("com.imsl.stat", s, aobj); } }
     */
    private class ARMAInfo implements Serializable, Cloneable
    {

        static final long serialVersionUID = 0x5c06b711b3ef3470L;
        protected double a_variance;
        protected double constant;
        protected double z[];
        protected double ar[];
        protected double ma[];
        protected int ar_lags[];
        protected int ma_lags[];
        protected int n_observations;
        protected int p;
        protected int q;
        protected int constant_option;

        public Object clone() throws CloneNotSupportedException
        {
            return super.clone();
        }

        private ARMAInfo()
        {
            z = null;
            ar = null;
            ma = null;
            ar_lags = null;
            ma_lags = null;
        }
    }

    // private static final int check = Messages.check(1);
    static final long serialVersionUID = 0x6e9b6d3e2144eba1L;
    public static final int METHOD_OF_MOMENTS = 0;
    public static final int LEAST_SQUARES = 1;
    private int lv_nmaxs;
    private double lv_fpnrmp;
    private int lv_nsing;
    private double lv_deltap;
    private double lv_gnleng;
    private double lv_phi;
    private double lv_phip;
    private double lv_phipi;
    private double lv_sgnorm;
    private double lv_aftol;
    private double lv_steptl;
    private double lv_rftol;
    private double lv_falstl;
    private double lv_fjactl;
    private int lv_maxfcn;
    private int lv_maxjac;
    private int lv_mxiter;
    private int errorCode;
    private double lv_arma[];
    private int l_ar_lags[];
    private int l_ma_lags[];
    private int l_p;
    private int l_q;
    private int l_nobs;
    private int l_mutual_ls;
    private int l_init_arlag;
    private int l_init_malag;
    private double l_z[];
    private double l_ar_init[];
    private double l_ma_init[];
    private double l_residual[];
    private double l_autocov[];
    private double l_param_est_cov[];
    private double l_dev[];
    private double l_weight[];
    private int l_center;
    private int l_method;
    private int l_mutual_est;
    private int l_init_rel_error;
    private int l_init_max_iter;
    private int l_max_iterations;
    private int l_length;
    private int l_init_back;
    private int l_init_conv_tol;
    private int l_initial_est;
    private int l_backward_origin;
    private double l_relative_error;
    private double l_tolerance;
    private int l_init_mean;
    private double l_convergence_tolerance;
    private double l_rw_mean;
    private double l_ss_residual;
    private double l_confidence;
    private ARMAInfo arma_info;

    public ARMA(int i, int j, Matrix timeSeriesData)
    {
        // double ad[]
        if (timeSeriesData == null)
        {
            throw new IllegalArgumentException("AutoCorrelation : Parameter \"timeSeriesData\" must be non-null.");
        }
        if (!timeSeriesData.isVector())
        {
            throw new IllegalArgumentException(
                    "AutoCorrelation : Parameter \"timeSeriesData\" must be a vector and not a matrix.");
        }

        if (timeSeriesData.isColVector())
        {
            timeSeriesData = timeSeriesData.toRowVector();
        }

        double[] ad = timeSeriesData.getArray()[0];

        lv_nsing = 0;
        errorCode = 0;
        lv_arma = null;
        l_confidence = 95D;
        arma_info = new ARMAInfo();
        if (i < 0)
        {
            Object aobj[] =
            {
                    "p", new Integer(i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "Negative", aobj);
            throw new IllegalArgumentException("Negative");
        }
        l_p = i;
        if (j < 0)
        {
            Object aobj1[] =
            {
                    "q", new Integer(j)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "Negative", aobj1);
            throw new IllegalArgumentException("Negative");
        }
        l_q = j;
        if (i + j < 1)
        {
            Object aobj2[] =
            {
                    "p+q", new Integer(i + j), new Integer(0)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotLargeEnough", aobj2);
            throw new IllegalArgumentException("NotLargeEnough");
        }
        if (ad.length < 2)
        {
            Object aobj3[] =
            {
                    "z.length", new Integer(ad.length), new Integer(2)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotLargeEnough", aobj3);
            throw new IllegalArgumentException("NotLargeEnough");
        }
        else if (l_isanan(ad.length, ad, 1) != 0)
        {
            Object aobj4[] =
            {
                    "z[" + (l_isanan(ad.length, ad, 1) - 1) + "]", "NaN"
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "CannotBe", aobj4);
            throw new IllegalArgumentException("CannotBe");
        }
        if (i + j + 1 >= ad.length)
        {
            Object aobj5[] =
            {
                    "z.length", new Integer(ad.length), "p+q+1"
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotLargeEnough", aobj5);
            throw new IllegalArgumentException("NotLargeEnough");
        }
        l_z = (double[]) ad.clone();
        l_nobs = ad.length;
        l_init_arlag = 1;
        l_init_malag = 1;
    }

    public ARMA(int i, int j, double ad[])
    {
        this(i, j, new Matrix(ad));

    }

    public final void compute()
    { // throws MatrixSingularException, TooManyCallsException,
      // IncreaseErrRelException, NewInitialGuessException,
      // IllConditionedException, TooManyITNException, TooManyFcnEvalException,
      // TooManyJacobianEvalException

        int i = l_nobs;
        double ad[] = l_z;
        int j = l_p;
        int k = l_q;
        int l = 0;
        int i1 = l_center;
        int j1 = l_method;
        int k1 = 0;
        int l1 = l_initial_est;
        int i2 = l_init_malag;
        int j2 = l_init_arlag;
        int k2 = l_init_conv_tol;
        int l2 = l_init_back;
        int i3 = l_init_mean;
        int j3 = l_init_max_iter;
        int k3 = l_init_rel_error;
        boolean flag = true;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        int l3 = 0;
        boolean flag4 = true;
        int i4 = l_mutual_ls;
        int j4 = l_mutual_est;
        int ai[] = l_ma_lags;
        int ai1[] = l_ar_lags;
        double ad1[] = null;
        double ad2[] = null;
        double ad3[] = null;
        double ad4[] = null;
        double ad5[] = null;
        double ad6[] = l_ar_init;
        double ad7[] = l_ma_init;
        double ad8[] =
        {
            0.0D
        };
        double ad9[] =
        {
            l_rw_mean
        };
        double ad10[] =
        {
            0.0D
        };
        Object obj = null;
        boolean flag5 = true;
        int l5 = l_max_iterations;
        double d = l_relative_error;
        Object obj1 = null;
        Object obj2 = null;
        Object obj3 = null;
        Object obj4 = null;
        Object obj5 = null;
        Object obj6 = null;
        Object obj7 = null;
        Object obj8 = null;
        Object obj9 = null;
        Object obj10 = null;
        Object obj11 = null;
        Object obj12 = null;
        Object obj13 = null;
        Object obj14 = null;
        int ai3[] =
        {
            0
        };
        int i6 = l_length;
        double d1 = l_tolerance;
        double d2 = l_convergence_tolerance;
        Object obj15 = null;
        Object obj16 = null;
        Object obj17 = null;
        Object obj18 = null;
        Object obj19 = null;
        Object obj20 = null;
        double ad31[] = null;
        int k6 = 0;
        int l6 = 0;
        Object obj21 = null;
        Object obj22 = null;
        Object obj23 = null;
        Object obj24 = null;
        if (i4 >= 1 && j1 == 0) // Messages.throwIllegalArgumentException("com.imsl.stat",
                                // "ARMA.MethodsInconsistent", null);
        {
            throw new IllegalArgumentException("MethodsInconsistent");
        }
        int k5 = j + k + 1;
        if (i3 != 0)
        {
            k1 = 0;
        }
        if (i3 == 0)
        {
            k1 = 1;
        }
        if (l3 != -1)
        {
            int l7 = 0;
            if (l3 != 1)
            {
                ad5 = new double[j + k + 2];
            }
            double ad35[] = new double[k5 + 1];
            double ad36[] = new double[k5];
            if (l3 == 0)
            {
                l_autocov = ad5;
            }
            l_acf(i, ad, l, l7, k1, ad9, k5, ad5, ad35, ad36);
            ad35 = null;
            ad36 = null;
        }
        if (!flag1)
        {
            if (j != 0)
            {
                ad4 = new double[j];
            }
            if (j == 0)
            {
                ad4 = new double[1];
            }
            if (k != 0)
            {
                ad3 = new double[k];
            }
            if (k == 0)
            {
                ad3 = new double[1];
            }
        }
        if (l1 != 1)
        {
            if (j3 == 0)
            {
                l5 = 200;
            }
            if (k3 == 0)
            {
                d = 2.2204460492503131E-014D;
            }
            double ad12[] = new double[j + k + 1];
            double ad13[] = new double[j + 1];
            double ad14[] = new double[k + 1];
            double ad15[] = new double[k + 1];
            double ad16[] = new double[k + 1];
            double ad17[] = new double[k + 1];
            double ad18[] = new double[(k + 1) * (k + 1)];
            double ad19[] = new double[(int) ((double) ((k + 1) * (k + 2)) / 2D)];
            double ad20[] = new double[k + 1];
            double ad21[] = new double[5 * (k + 1)];
            double ad22[];
            double ad23[];
            double ad24[];
            int ai2[];
            if (j != 0)
            {
                ad22 = new double[j * 2 * (j * 2)];
                ad23 = new double[j * 2 * (j * 2)];
                ai2 = new int[j];
                ad24 = new double[j];
            }
            else
            {
                ad22 = new double[1];
                ad23 = new double[1];
                ai2 = new int[1];
                ad24 = new double[1];
            }
            l_n2pe(i, ad, k1, ad9, j, k, d, l5, ad8, ad4, ad3, ad10, ad12, ad13, ad14, ad15, ad16, ad17, ad18, ad19,
                    ad20, ad21, ad22, ad23, ai2, ad24);
            if (j == 0)
            {
                ad4[0] = 0.0D;
            }
            if (k == 0)
            {
                ad3[0] = 0.0D;
            }
            ad12 = null;
            ad13 = null;
            ad14 = null;
            ad15 = null;
            ad16 = null;
            ad17 = null;
            ad18 = null;
            ad19 = null;
            ad20 = null;
            ad21 = null;
            ad22 = null;
            ad23 = null;
            ai2 = null;
            ad24 = null;
        }
        if (j1 == 1)
        {
            if (l1 == 1)
            {
                System.arraycopy(ad6, 0, ad4, 0, j);
                System.arraycopy(ad7, 0, ad3, 0, k);
            }
            if (i1 == 1)
            {
                k1 = 0;
            }
            if (i1 == 0)
            {
                k1 = 1;
            }
            if (k1 == 1 && i3 == 0 && l1 == 1)
            {
                ad9[0] = BLAS.sum(i, ad, 0, 1) / (double) i;
            }
            if (i2 == 1)
            {
                if (k != 0)
                {
                    ai = new int[k];
                }
                if (k == 0)
                {
                    ai = new int[1];
                }
                if (k != 0)
                {
                    for (int k4 = 1; k4 <= k; k4++)
                    {
                        ai[k4 - 1] = k4;
                    }

                }
                if (k == 0)
                {
                    ai[0] = 1;
                }
            }
            if (j2 == 1)
            {
                if (j != 0)
                {
                    ai1 = new int[j];
                }
                if (j == 0)
                {
                    ai1 = new int[1];
                }
                if (j != 0)
                {
                    for (int i5 = 1; i5 <= j; i5++)
                    {
                        ai1[i5 - 1] = i5;
                    }

                }
                if (j == 0)
                {
                    ai1[0] = 1;
                }
            }
            if (k2 == 0)
            {
                d2 = Math.max(1.0000000000000001E-009D, Math.pow(2.2204460492503131E-016D, 0.66666666666666663D));
            }
            if (l2 == 0)
            {
                i6 = 10;
                d1 = 0.0D;
            }
            if (j != 0)
            {
                k6 = ai1[BLAS.iimax(j, ai1, 0, 1) - 1];
            }
            if (j == 0)
            {
                k6 = 0;
            }
            if (k != 0)
            {
                l6 = ai[BLAS.iimax(k, ai, 0, 1) - 1];
            }
            if (k == 0)
            {
                l6 = 0;
            }
            if (!flag2)
            {
                ad1 = new double[(i - k6) + i6];
            }
            if (!flag2)
            {
                l_residual = ad1;
            }
            int j6 = k1 + j + k;
            if (!flag3)
            {
                ad2 = new double[j6 * j6];
            }
            if (!flag3)
            {
                l_param_est_cov = ad2;
            }
            int i7 = j + k + k1;
            int j7 = (i - k6) + i6;
            int k7 = j7;
            double ad25[] = new double[i7];
            double ad26[] = new double[i7];
            double ad27[] = new double[j7];
            double ad28[] = new double[i7];
            double ad29[] = new double[j7];
            double ad30[] = new double[k7 * i7];
            double ad32[] = new double[(10 * i7 + 2 * j7) - 1];
            int ai4[] = new int[i7];
            double ad33[] = new double[i + i6];
            if (l6 != 0)
            {
                ad31 = new double[l6];
            }
            if (l6 == 0)
            {
                ad31 = new double[1];
            }
            double ad34[] = new double[i6];
            l_n2lse(i, ad, l, k1, ad9, j, ad4, ai1, k, ad3, ai, i6, d1, d2, ad8, ad2, j6, ai3, ad1, ad10, ad25, ad26,
                    ad27, ad28, ad29, ad30, k7, ad32, ai4, ad33, ad31, ad34);
            if (flag5)
            {
                l_rw_mean = ad9[0];
            }
            if (j == 0)
            {
                ad4[0] = 0.0D;
            }
            if (k == 0)
            {
                ad3[0] = 0.0D;
            }
            ad25 = null;
            ad26 = null;
            ad27 = null;
            ad28 = null;
            ad29 = null;
            ad30 = null;
            ad32 = null;
            ai4 = null;
            ad33 = null;
            ad31 = null;
            ad34 = null;
        }
        if (flag4)
        {
            byte byte0 = 5;
            int i8 = 3 + j + k;
            int j8 = 2 + i + j + k;
            boolean flag6 = true;
            int k8 = 10;
            k8 = k8 + j + k;
            if (j1 == 0)
            {
                if (i2 == 1)
                {
                    if (k != 0)
                    {
                        ai = new int[k];
                    }
                    if (k == 0)
                    {
                        ai = new int[1];
                    }
                    if (k != 0)
                    {
                        for (int l4 = 1; l4 <= k; l4++)
                        {
                            ai[l4 - 1] = l4;
                        }

                    }
                    if (k == 0)
                    {
                        ai[0] = 1;
                    }
                }
                if (j2 == 1)
                {
                    if (j != 0)
                    {
                        ai1 = new int[j];
                    }
                    if (j == 0)
                    {
                        ai1 = new int[1];
                    }
                    if (j != 0)
                    {
                        for (int j5 = 1; j5 <= j; j5++)
                        {
                            ai1[j5 - 1] = j5;
                        }

                    }
                    if (j == 0)
                    {
                        ai1[0] = 1;
                    }
                }
            }
            arma_info.a_variance = ad10[0];
            arma_info.constant = ad8[0];
            arma_info.n_observations = i;
            arma_info.p = j;
            arma_info.q = k;
            arma_info.z = new double[i];
            arma_info.ar = new double[j];
            arma_info.ma = new double[k];
            arma_info.ar_lags = new int[j];
            arma_info.ma_lags = new int[k];
            if (i1 == 1)
            {
                arma_info.constant_option = 2;
            }
            if (i1 == 0)
            {
                arma_info.constant_option = 1;
            }
            System.arraycopy(ad, 0, arma_info.z, 0, i);
            if (j != 0)
            {
                System.arraycopy(ad4, 0, arma_info.ar, 0, j);
            }
            if (k != 0)
            {
                System.arraycopy(ad3, 0, arma_info.ma, 0, k);
            }
            if (j != 0)
            {
                System.arraycopy(ai1, 0, arma_info.ar_lags, 0, j);
            }
            if (k != 0)
            {
                System.arraycopy(ai, 0, arma_info.ma_lags, 0, k);
            }
        }
        if (flag)
        {
            l_ss_residual = ad10[0] * (double) (i - k1 - j - k);
        }
        if (!flag1)
        {
            double ad11[] = new double[j + k + 1];
            ad11[0] = ad8[0];
            if (j > 0)
            {
                System.arraycopy(ad4, 0, ad11, 1, j);
            }
            if (k > 0)
            {
                System.arraycopy(ad3, 0, ad11, j + 1, k);
            }
            lv_arma = ad11;
        }
    }

    public final Matrix forecast(int i)
    {
        ARMAInfo armainfo = null;
        int j = 0;
        int k = l_backward_origin;
        double d = l_confidence;
        boolean flag = false;
        int l = 0;
        double ad[] = null;
        double ad1[] = null;
        double ad2[] = null;
        double ad3[] = null;
        double ad4[] = null;
        int ai[] = null;
        if (arma_info == null)
        {
            Matrix nullMat = null;
            return nullMat;
            // return (double[][]) null;
        }
        if (i <= 0)
        {
            Object aobj[] =
            {
                    "nPredict", new Integer(i), new Integer(0)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotLargeEnough", aobj);
            throw new IllegalArgumentException("NotLargeEnough");
        }
        try
        {
            armainfo = (ARMAInfo) arma_info.clone();
        }
        catch (Exception exception)
        {
        }
        double d1 = (100D - d) * 0.01D;
        j++;
        int i1;
        if (armainfo.constant_option == 2)
        {
            i1 = 0;
        }
        else
        {
            i1 = 1;
        }
        if (!flag)
        {
            ad = new double[i * (k + 3)];
        }
        int j1;
        if (armainfo.p > 0)
        {
            j1 = armainfo.ar_lags[BLAS.iimax(armainfo.p, armainfo.ar_lags, 0, 1) - 1];
        }
        else
        {
            j1 = 0;
        }
        int k1;
        if (armainfo.q > 0)
        {
            k1 = armainfo.ma_lags[BLAS.iimax(armainfo.q, armainfo.ma_lags, 0, 1) - 1];
        }
        else
        {
            k1 = 0;
        }
        ad1 = new double[j1 + 1];
        ad2 = new double[k1 + 1];
        ad3 = new double[i + 1];
        ad4 = new double[i + 1];
        ai = new int[i + 1];
        l_n2bjf(armainfo.n_observations, armainfo.z, l, armainfo.p, armainfo.ar, armainfo.ar_lags, armainfo.q,
                armainfo.ma, armainfo.ma_lags, i1, armainfo.constant, armainfo.a_variance, d1, k, i, ad, i, ad1, ad2,
                ad3, ad4, ai);
        l_m1ran(k + 3, i, ad, ad);
        if (ad1 != null)
        {
            ad1 = null;
        }
        if (ad2 != null)
        {
            ad2 = null;
        }
        if (ad3 != null)
        {
            ad3 = null;
        }
        if (ad4 != null)
        {
            ad4 = null;
        }
        if (ai != null)
        {
            ai = null;
        }
        double ad5[][] = new double[i][k + 1];
        l_dev = new double[i];
        l_weight = new double[i];
        for (int l1 = 0; l1 < i; l1++)
        {
            System.arraycopy(ad, l1 * (k + 3), ad5[l1], 0, k + 1);
            l_dev[l1] = ad[l1 * (k + 3) + k + 1];
            l_weight[l1] = ad[l1 * (k + 3) + k + 2];
        }

        return new Matrix((double[][]) ad5.clone());
    }

    private void l_n2pe(int i, double ad[], int j, double ad1[], int k, int l, double d, int i1, double ad2[],
            double ad3[], double ad4[], double ad5[], double ad6[], double ad7[], double ad8[], double ad9[],
            double ad10[], double ad11[], double ad12[], double ad13[], double ad14[], double ad15[], double ad16[],
            double ad17[], int ai[], double ad18[]) // throws
                                                    // MatrixSingularException,
                                                    // TooManyCallsException,
                                                    // IncreaseErrRelException,
                                                    // NewInitialGuessException,
                                                    // IllConditionedException
    {
        if (i > 0 && ad[BLAS.ismin(i, ad, 0, 1) - 1] == ad[BLAS.ismax(i, ad, 0, 1) - 1])
        {
            Object aobj[] =
            {
                    new Integer(BLAS.ismin(i, ad, 0, 1)), new Double(ad[BLAS.ismin(i, ad, 0, 1) - 1])
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "ARMA.ConstantSeries", aobj);
            throw new IllegalArgumentException("ConstantSeries");
        }
        int l1 = k + l;
        if (j == 1)
        {
            ad1[0] = BLAS.sum(i, ad, 0, 1) / (double) i;
        }
        BLAS.set(l1 + 1, 0.0D, ad6, 0, 1);
        for (int k1 = 0; k1 <= l1; k1++)
        {
            for (int j1 = 1; j1 <= i - k1; j1++)
            {
                ad6[k1] += ((ad[j1 - 1] - ad1[0]) * (ad[(j1 + k1) - 1] - ad1[0])) / (double) i;
            }

        }

        if (ad6[0] <= 2.2250738585072009E-308D)
        {
            Object aobj1[] =
            {
                new Double(ad6[0])
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "ARMA.VariancePositive", aobj1);
            throw new IllegalArgumentException("VariancePositive");
        }
        if (k > 0)
        {
            l_a2mme(l1, ad6, 0, l, k, ad3, ad16, ad17, ai, ad18);
        }
        if (l > 0)
        {
            l_m2mme(l1, ad6, 0, k, ad3, d, i1, l, ad4, ad7, ad8, ad9, ad10, ad11, ad12, ad13, ad14, ad15);
        }
        if (k == 0)
        {
            ad2[0] = ad1[0];
        }
        else
        {
            ad2[0] = ad1[0] * (1.0D - BLAS.sum(k, ad3, 0, 1));
        }
        if (l == 0)
        {
            ad5[0] = ad6[0] - BLAS.dot(k, ad3, 0, 1, ad6, 1, 1);
        }
        else
        {
            ad5[0] = Math.pow(ad10[0], 2D);
        }
    }

    private void l_n2lse(int i, double ad[], int j, int k, double ad1[], int l, double ad2[], int ai[], int i1,
            double ad3[], int ai1[], int j1, double d, double d1, double ad4[], double ad5[], int k1, int ai2[],
            double ad6[], double ad7[], double ad8[], double ad9[], double ad10[], double ad11[], double ad12[],
            double ad13[], int l1, double ad14[], int ai3[], double ad15[], double ad16[], double ad17[]) // throws
                                                                                                          // TooManyITNException,
                                                                                                          // TooManyFcnEvalException,
                                                                                                          // TooManyJacobianEvalException
    {
        int ai4[] =
        {
            0
        };
        int ai5[] =
        {
            0
        };
        int ai6[] =
        {
            0
        };
        int k3 = 1;
        int l3 = 1;
        int k2;
        if (l == 0)
        {
            k2 = 0;
            boolean flag = false;
        }
        else
        {
            k2 = ai[BLAS.iimax(l, ai, 0, 1) - 1] * k3;
            int l2 = ai[BLAS.iimin(l, ai, 0, 1) - 1];
        }
        int i3;
        if (i1 == 0)
        {
            i3 = 0;
            boolean flag1 = false;
        }
        else
        {
            i3 = ai1[BLAS.iimax(i1, ai1, 0, 1) - 1] * l3;
            int j3 = ai1[BLAS.iimin(i1, ai1, 0, 1) - 1];
        }
        int j4 = k + l + i1;
        int i4 = (i - k2) + j1;
        System.arraycopy(ad2, 0, ad8, 0, l);
        System.arraycopy(ad3, 0, ad8, l, i1);
        if (k == 1)
        {
            ad8[l + i1] = ad1[0];
        }
        double d3;
        if (d == 0.0D)
        {
            d3 = 0.0D;
            double d4 = BLAS.sum(i, ad, 0, 1) / (double) i;
            for (int i2 = 1; i2 <= i; i2++)
            {
                d3 += Math.pow(ad[i2 - 1] - d4, 2D) / (double) (i - 1);
            }

            d3 = 0.01D * Math.sqrt(d3);
        }
        else
        {
            d3 = d;
        }
        errorCode = 0;
        l_n3lse(i, ad, k, l, ai, i1, ai1, j1, d3, d1, ai2, k2, i3, ad8, ad9, ad10, ad11, ad12, ad14,
                (10 * j4 + i4) - 1, 0, j4, 2 * j4, 3 * j4, ad13, l1, ai3, 4 * j4, 5 * j4, 6 * j4, j4 * 8 - 1,
                j4 * 9 - 1, j4 * 10 - 1, ad15, ad16, ai4, ad17, ai5);
        if (errorCode != 0)
        {
            errorCode = 11256;
            // Warning.print(this, "com.imsl.stat", "ARMA.LeastSquaresFailed",
            // null);
            System.out.println("LeastSquaresFailed");

        }
        System.arraycopy(ad12, 0, ad6, 0, ai2[0]);
        if (i4 - ai2[0] > 0)
        {
            BLAS.set(i4 - ai2[0], (0.0D / 0.0D), ad6, ai2[0], 1);
        }
        System.arraycopy(ad11, 0, ad2, 0, l);
        System.arraycopy(ad11, l, ad3, 0, i1);
        if (k == 0)
        {
            ad4[0] = 0.0D;
        }
        else
        {
            ad1[0] = ad11[j4 - 1];
            ad4[0] = ad1[0] * (1.0D - BLAS.sum(l, ad2, 0, 1));
        }
        ad7[0] = BLAS.dot(ai2[0], ad6, 0, 1, ad6, 0, 1) / (double) (i - k - l - i1);
        if (k == 1)
        {
            System.arraycopy(ad13, (j4 - 1) * l1, ad14, 0, i4);
            for (int j2 = j4 - 1; j2 >= 1; j2--)
            {
                System.arraycopy(ad13, (j2 - 1) * l1, ad13, j2 * l1, i4);
            }

            System.arraycopy(ad14, 0, ad13, 0, i4);
        }
        l_mxtxf(i4, j4, ad13, l1, j4, ad5, k1);
        double d2 = 2.2204460492503131E-014D;
        l_chfac(j4, ad5, k1, d2, ai6, ad5, k1);
        l_rcovb(j4, ad5, k1, ad7[0], ad5, k1);
    }

    private void l_n3lse(int i, double ad[], int j, int k, int ai[], int l, int ai1[], int i1, double d, double d1,
            int ai2[], int j1, int k1, double ad1[], double ad2[], double ad3[], double ad4[], double ad5[],
            double ad6[], int l1, int i2, int j2, int k2, int l2, double ad7[], int i3, int ai3[], int j3, int k3,
            int l3, int i4, int j4, int k4, double ad8[], double ad9[], int ai4[], double ad10[], int ai5[]) // throws
                                                                                                             // TooManyITNException,
                                                                                                             // TooManyFcnEvalException,
                                                                                                             // TooManyJacobianEvalException
    {
        int ai6[] =
        {
            0
        };
        int ai7[] =
        {
            0
        };
        int ai8[] =
        {
            0
        };
        int ai9[] =
        {
            0
        };
        int ai10[] =
        {
            0
        };
        int ai11[] = new int[6];
        double ad11[] = new double[7];
        double ad12[] =
        {
            0.0D
        };
        double ad13[] =
        {
            0.0D
        };
        double ad14[] =
        {
            0.0D
        };
        ai11[0] = 0;
        if (ai11[0] == 0)
        {
            imsls_u4lsf(ai11, ad11);
        }
        int j6 = (i - j1) + i1;
        int l6 = k + l + j;
        BLAS.set(l6, 1.0D, ad2, 0, 1);
        BLAS.set(j6, 1.0D, ad3, 0, 1);
        System.arraycopy(ad1, 0, ad4, 0, l6);
        l_u5lsf(j6, l6, ad4, ad2, ad3, 0, ai11, ad11);
        double d6 = ai11[1];
        lv_mxiter = ai11[2];
        lv_maxfcn = ai11[3];
        lv_maxjac = ai11[4];
        int k6 = ai11[5];
        lv_fjactl = ad11[0];
        lv_steptl = ad11[1];
        if (d1 == 0.0D)
        {
            lv_rftol = ad11[2];
        }
        else
        {
            lv_rftol = Math.abs(1.0D - Math.sqrt(1.0D - d1));
        }
        lv_aftol = ad11[3];
        lv_falstl = ad11[4];
        double d7 = ad11[5];
        ad13[0] = ad11[6];
        double d2 = 2.2204460492503131E-016D;
        double d3 = Math.max(d2, Math.pow(10D, -d6));
        ai5[0] = 0;
        ai7[0] = 0;
        int i7 = 0;
        ai9[0] = 0;
        l_n6lse(ad4, 0, ad5, 0, i, ad, j, k, ai, l, ai1, i1, d, ai2, j1, k1, ad8, ad9, ai4, ad10);
        double d4 = 0.5D * Math.pow(BLAS.nrm2(j6, ad5, 0, 1), 2D);
        ai7[0]++;
        l_n4lse(j6, l6, ad4, 0, ad2, ad5, 0, d3, ad7, i3, ad6, k4, i, ad, j, k, ai, l, ai1, i1, d, ai2, j1, k1, ad8,
                ad9, ai4, ad10);
        i7++;
        ai6[0] = 0;
        BLAS.set(l6, 0.0D, ad6, l2, 1);
        for (int l5 = 1; l5 <= l6; l5++)
        {
            for (int l4 = 1; l4 <= j6; l4++)
            {
                ad6[(l2 + l5) - 1] += ad7[(l5 - 1) * i3 + (l4 - 1)] * ad3[l4 - 1] * ad3[l4 - 1] * ad5[l4 - 1];
            }

        }

        errorCode = 0;
        l_u6lsf(l6, ad4, 0, ad6, j2, d4, l2, ad2, ai9, ai5[0], ai7[0], i7, 0, ai6[0]);
        if (errorCode != 0 || ai9[0] == -999)
        {
            return;
        }
        do
        {
            ai5[0]++;
            for (int i5 = 1; i5 <= j6; i5++)
            {
                BLAS.scal(l6, ad3[i5 - 1], ad7, i5 - 1, i3);
            }

            for (int j5 = 0; j5 < l6; j5++)
            {
                ai3[j5] = 0;
            }

            l_l2rrr(j6, l6, ad7, i3, 1, ai3, ad7, i3, ad6, j3, k3, l3);
            if (k6 == 1)
            {
                l_u8lsf(ai5[0], l6, ad6, k3, ad2);
            }
            l_u11nf(j6, ad3, 1, ad5, 0, ad6, k4);
            l_u10sf(j6, l6, ad7, i3, ad6, j3, k4, k4);
            ai9[0] = 6;
            ai10[0] = 1;
            do
            {
                l_u7lsf(l6, ad6, l2, ad7, i3, ai3, ad2, k4, d7, ad13, ad12, ai10, j2, k2, ai8, j3, k3, l3);
                l_n5lse(j6, l6, ad4, d4, ad6, l2, ad7, i3, ai3, j2, ad2, ai8[0], d7, ad13, ai9, i4, j4, i2, ad5, l1,
                        ad14, ai6, ai7, i, ad, j, k, ai, l, ai1, i1, d, ai2, j1, k1, ad8, ad9, ai4, ad10);
            }
            while (ai9[0] >= 4);
            l_n4lse(j6, l6, ad6, i2, ad2, ad6, l1, d3, ad7, i3, ad6, k4, i, ad, j, k, ai, l, ai1, i1, d, ai2, j1, k1,
                    ad8, ad9, ai4, ad10);
            i7++;
            BLAS.set(l6, 0.0D, ad6, l2, 1);
            for (int i6 = 1; i6 <= l6; i6++)
            {
                for (int k5 = 1; k5 <= j6; k5++)
                {
                    ad6[(l2 + i6) - 1] += ad7[(i6 - 1) * i3 + (k5 - 1)] * ad3[k5 - 1] * ad3[k5 - 1]
                            * ad6[(l1 + k5) - 1];
                }

            }

            errorCode = 0;
            l_u6lsf(l6, ad6, i2, ad6, j2, ad14[0], l2, ad2, ai9, ai5[0], ai7[0], i7, 0, ai6[0]);
            if (errorCode == 0 && ai9[0] != -999)
            {
                System.arraycopy(ad6, i2, ad4, 0, l6);
                System.arraycopy(ad6, l1, ad5, 0, j6);
                d4 = ad14[0];
            }
            else
            {
                System.arraycopy(ad6, i2, ad4, 0, l6);
                System.arraycopy(ad6, l1, ad5, 0, j6);
                double d5 = ad14[0];
                ai11[2] = ai5[0];
                ai11[3] = ai7[0];
                ai11[4] = i7;
                return;
            }
        }
        while (true);
    }

    private void l_n4lse(int i, int j, double ad[], int k, double ad1[], double ad2[], int l, double d, double ad3[],
            int i1, double ad4[], int j1, int k1, double ad5[], int l1, int i2, int ai[], int j2, int ai1[], int k2,
            double d1, int ai2[], int l2, int i3, double ad6[], double ad7[], int ai3[], double ad8[])
    {
        double d2 = Math.sqrt(Math.max(d, 2.2204460492503131E-016D));
        for (int k3 = 1; k3 <= j; k3++)
        {
            double d3 = d2 * Math.max(Math.abs(ad[(k + k3) - 1]), 1.0D / ad1[k3 - 1]);
            if (ad[(k + k3) - 1] < 0.0D)
            {
                d3 = -d3;
            }
            double d4 = ad[(k + k3) - 1];
            ad[(k + k3) - 1] = d4 + d3;
            l_n6lse(ad, k, ad4, j1, k1, ad5, l1, i2, ai, j2, ai1, k2, d1, ai2, l2, i3, ad6, ad7, ai3, ad8);
            ad[(k + k3) - 1] = d4;
            for (int j3 = 1; j3 <= i; j3++)
            {
                ad3[(k3 - 1) * i1 + (j3 - 1)] = (ad4[(j1 + j3) - 1] - ad2[(l + j3) - 1]) / d3;
            }

        }

    }

    private void l_n5lse(int i, int j, double ad[], double d, double ad1[], int k, double ad2[], int l, int ai[],
            int i1, double ad3[], int j1, double d1, double ad4[], int ai1[], int k1, int l1, int i2, double ad5[],
            int j2, double ad6[], int ai2[], int ai3[], int k2, double ad7[], int l2, int i3, int ai4[], int j3,
            int ai5[], int k3, double d2, int ai6[], int l3, int i4, double ad8[], double ad9[], int ai7[],
            double ad10[])
    {
        ai2[0] = 0;
        l_u11nf(j, ad3, 1, ad1, i1, ad1, i2);
        double d10 = BLAS.nrm2(j, ad1, i2, 1);
        for (int j4 = 1; j4 <= j; j4++)
        {
            ad1[(i2 + j4) - 1] = ad[j4 - 1] + ad1[(i1 + j4) - 1];
        }

        l_n6lse(ad1, i2, ad1, j2, k2, ad7, l2, i3, ai4, j3, ai5, k3, d2, ai6, l3, i4, ad8, ad9, ai7, ad10);
        ai3[0]++;
        ad6[0] = 0.5D * Math.pow(BLAS.nrm2(i, ad1, j2, 1), 2D);
        double d3 = ad6[0] - d;
        double d8 = BLAS.dot(j, ad1, k, 1, ad1, i1, 1);
        if (ai1[0] != 5)
        {
            lv_fpnrmp = 0.0D;
        }
        if (ai1[0] == 5 && (ad6[0] >= lv_fpnrmp || d3 > 0.0001D * d8))
        {
            ai1[0] = 0;
            System.arraycopy(ad1, k1, ad1, i2, j);
            System.arraycopy(ad1, l1, ad1, j2, i);
            ad6[0] = lv_fpnrmp;
            ad4[0] *= 0.5D;
        }
        else if (d3 >= 0.0001D * d8)
        {
            double d6 = 0.0D;
            for (int k4 = 1; k4 <= j; k4++)
            {
                double d11 = Math.abs(ad1[(i1 + k4) - 1]) / Math.max(Math.abs(ad1[(i2 + k4) - 1]), 1.0D / ad3[k4 - 1]);
                d6 = Math.max(d6, d11);
            }

            if (d6 < lv_steptl)
            {
                ai1[0] = 1;
                System.arraycopy(ad, 0, ad1, i2, j);
                System.arraycopy(ad5, 0, ad1, j2, i);
            }
            else
            {
                ai1[0] = 4;
                double d9 = 2.2250738585072009E-308D;
                double d4 = 1.7976931348623157E+308D;
                if (d4 * d9 < 1.0D)
                {
                    d9 = 1.0D / d4;
                }
                double d12;
                if (Math.abs(d3 - d8) > d9)
                {
                    d12 = (-d8 * d10) / (2D * (d3 - d8));
                }
                else
                {
                    d12 = (-d8 * d10) / 2D;
                }
                if (d12 < 0.10000000000000001D * ad4[0])
                {
                    ad4[0] *= 0.10000000000000001D;
                }
                else if (d12 > 0.5D * ad4[0])
                {
                    ad4[0] *= 0.5D;
                }
                else
                {
                    ad4[0] = d12;
                }
            }
        }
        else
        {
            double d5 = d8;
            for (int l4 = 1; l4 <= j; l4++)
            {
                int j5 = ai[l4 - 1];
                double d13 = BLAS.dot((j - l4) + 1, ad2, (l4 - 1) * l + (l4 - 1), l, ad1, (i1 + j5) - 1, 0);
                d5 += 0.5D * d13 * d13;
            }

            boolean flag = Math.abs(d5 - d3) <= 0.10000000000000001D * Math.abs(d3);
            if (ai1[0] != 4 && (flag || d3 <= d8) && j1 == 0 && ad4[0] <= 0.98999999999999999D * d1)
            {
                ai1[0] = 5;
                System.arraycopy(ad1, i2, ad1, k1, j);
                System.arraycopy(ad1, j2, ad1, l1, i);
                lv_fpnrmp = ad6[0];
                ad4[0] = Math.min(2D * ad4[0], d1);
            }
            else
            {
                ai1[0] = 0;
                if (d10 > 0.98999999999999999D * d1)
                {
                    ai2[0] = 1;
                }
                if (d3 >= 0.10000000000000001D * d5)
                {
                    ad4[0] *= 0.5D;
                }
                else if (d3 <= 0.75D * d5)
                {
                    ad4[0] = Math.min(2D * ad4[0], d1);
                }
            }
            if (d3 <= 2D * d5)
            {
                if (Math.abs(d3) <= lv_rftol * Math.abs(d) && Math.abs(d5) <= lv_rftol * Math.abs(d))
                {
                    ai1[0] = 2;
                }
            }
            else
            {
                double d7 = 0.0D;
                for (int i5 = 1; i5 <= j; i5++)
                {
                    double d14 = Math.abs(ad1[(i1 + i5) - 1])
                            / Math.max(Math.abs(ad1[(i2 + i5) - 1]), 1.0D / ad3[i5 - 1]);
                    d7 = Math.max(d7, d14);
                }

                if (d7 < lv_falstl)
                {
                    ai1[0] = 3;
                }
            }
        }
    }

    private void l_n6lse(double ad[], int i, double ad1[], int j, int k, double ad2[], int l, int i1, int ai[], int j1,
            int ai1[], int k1, double d, int ai2[], int l1, int i2, double ad3[], double ad4[], int ai3[], double ad5[])
    {
        int ai4[] =
        {
            0
        };
        int k2 = 0;
        double d1;
        if (l == 0)
        {
            d1 = 0.0D;
        }
        else
        {
            d1 = ad[i + i1 + j1];
        }
        int l2 = 1;
        int i3 = 1;
        int i4 = 1;
        int l3 = k - l1;
        int j2 = -1;
        BLAS.set(i2, 0.0D, ad4, 0, 1);
        l_nsre(k, 0, ad2, k2, d1, i1, ad, i, ai, l2, j1, i + i1, ai1, i3, j2, ad4, i4, l3, ad1, j);
        if (k1 > 0)
        {
            int j3 = 1;
            int k3 = k1;
            j2 = -1;
            l_nsfbc(k, ad2, k2, d1, i1, ad, i, ai, l2, j1, i + i1, ai1, i3, -1, ad1, (j + j3) - 1, 1, k3, d, ai4, ai3,
                    ad5);
            System.arraycopy(ad5, 0, ad3, 0, ai3[0]);
            BLAS.add(ai3[0], d1, ad3, 0, 1);
        }
        else
        {
            ai3[0] = 0;
        }
        System.arraycopy(ad2, 0, ad3, ai3[0], k);
        ai2[0] = (k - l1) + ai3[0];
        j2 = 1;
        i4 = -ai3[0] + l1 + 1;
        l3 = k;
        BLAS.set((k - l1) + k1, 0.0D, ad1, j, 1);
        BLAS.set(i2, 0.0D, ad4, 0, 1);
        l_nsre(k, ai3[0], ad3, k2, d1, i1, ad, i, ai, l2, j1, i + i1, ai1, i3, j2, ad4, i4, l3, ad1, j);
    }

    private void imsls_u4lsf(int ai[], double ad[])
    {
        double d1 = 2.2204460492503131E-016D;
        double d2 = 0.33333333333333331D;
        double d3 = 0.66666666666666663D;
        ai[0] = 1;
        ai[1] = (int) (-Sfun.log10(d1) + 0.10000000000000001D);
        ai[2] = 100;
        ai[3] = 400;
        ai[4] = 100;
        ai[5] = 1;
        double d = 1E-010D;
        ad[0] = Math.sqrt(d1);
        ad[1] = Math.pow(d1, d3);
        ad[2] = Math.max(d, Math.pow(d1, d3));
        ad[3] = Math.max(d * d, d1 * d1);
        ad[4] = 100D * d1;
        ad[5] = -999D;
        ad[6] = -999D;
    }

    private void l_u5lsf(int i, int j, double ad[], double ad1[], double ad2[], int k, int ai[], double ad3[])
    {
        double d1 = 2.2204460492503131E-016D;
        int l = 1;
        do
        {
            if (l > i)
            {
                break;
            }
            if (ad2[l - 1] <= 0.0D)
            {
                errorCode = 20122;
                // Warning.print(this, "com.imsl.stat",
                // "ARMA.NeedPositiveFScaleElem", null);
                System.out.println("NeedPositiveFScaleElem");
                BLAS.set(i, 1.0D, ad2, 0, 1);
                break;
            }
            l++;
        }
        while (true);
        double d3 = Math.pow(d1, 0.33333333333333331D);
        double d5 = Math.pow(d1, 0.66666666666666663D);
        double d = 1E-010D;
        if (ad3[5] <= 0.0D)
        {
            double d4 = 0.0D;
            for (int i1 = 1; i1 <= j; i1++)
            {
                d4 += Math.pow(ad1[i1 - 1] * ad[i1 - 1], 2D);
            }

            d4 = Math.sqrt(d4);
            double d6 = BLAS.nrm2(j, ad1, 0, 1);
            double d2 = 1000D * Math.max(d4, d6);
            if (ai[0] != 0 && ad3[5] != -999D)
            {
                errorCode = 20117;
                Object aobj[] =
                {
                        new Double(ad3[5]), new Double(d2)
                };
                // Warning.print(this, "com.imsl.stat",
                // "ARMA.NeedNonnegativeStepmx", aobj);
                System.out.println("NeedNonnegativeStepmx");
            }
            ad3[5] = d2;
        }
        if (ad3[6] <= 0.0D)
        {
            ad3[6] = -999D;
        }
    }

    private void l_u6lsf(int i, double ad[], int j, double ad1[], int k, double d, int l, double ad2[], int ai[],
            int i1, int j1, int k1, int l1, int i2) // throws
                                                    // TooManyITNException,
                                                    // TooManyFcnEvalException,
                                                    // TooManyJacobianEvalException
    {
        if (d <= lv_aftol)
        {
            ai[0] = -999;
            return;
        }
        double d5 = 0.0D;
        double d4 = 2.2250738585072009E-308D;
        double d1 = 1.7976931348623157E+308D;
        if (d1 * d4 < 1.0D)
        {
            d4 = 1.0D / d1;
        }
        for (int j2 = 1; j2 <= i; j2++)
        {
            double d2;
            if (d <= d4)
            {
                d2 = Math.abs(ad1[(l + j2) - 1]) * Math.max(Math.abs(ad[(j + j2) - 1]), 1.0D / ad2[j2 - 1]);
            }
            else
            {
                d2 = (Math.abs(ad1[(l + j2) - 1]) * Math.max(Math.abs(ad[(j + j2) - 1]), 1.0D / ad2[j2 - 1])) / d;
            }
            d5 = Math.max(d2, d5);
        }

        if (d5 <= lv_fjactl)
        {
            ai[0] = -999;
            return;
        }
        if (i1 == 0)
        {
            lv_nmaxs = 0;
            return;
        }
        d5 = 0.0D;
        for (int k2 = 1; k2 <= i; k2++)
        {
            double d3 = Math.abs(ad1[(k + k2) - 1]) / Math.max(Math.abs(ad[(j + k2) - 1]), 1.0D / ad2[k2 - 1]);
            d5 = Math.max(d3, d5);
        }

        if (d5 <= lv_steptl)
        {
            ai[0] = -999;
            errorCode = 20128;
            // Warning.print(this, "com.imsl.stat", "ARMA.StepTolerance", null);
            System.out.println("StepTolerance");
            return;
        }
        if (ai[0] == 2)
        {
            errorCode = 20119;
            Object aobj[] =
            {
                new Double(lv_rftol)
            };
            // Warning.print(this, "com.imsl.stat", "ARMA.LittleFcnChange",
            // aobj);
            System.out.println("LittleFcnChange");
            return;
        }
        if (ai[0] == 3)
        {
            errorCode = 20130;
            // Warning.print(this, "com.imsl.stat", "ARMA.FalseConvergence",
            // null);
            System.out.println("NeedNonnegativeStepmx");
            return;
        }
        if (i1 >= lv_mxiter) // throw new TooManyITNException("ARMA.TooManyITN",
                             // null);
        {
            throw new IllegalArgumentException("VariancePositive");
        }
        if (j1 >= lv_maxfcn) // throw new
                             // TooManyFcnEvalException("ARMA.TooManyFcnEval",
                             // null);
        {
            throw new IllegalArgumentException("TooManyFcnEval");
        }
        if (l1 != 0 && k1 >= lv_maxjac) // throw new
                                        // TooManyJacobianEvalException("ARMA.TooManyJacobianEval",
                                        // null);
        {
            throw new IllegalArgumentException("TooManyJacobianEval");
        }
        if (i2 != 0)
        {
            lv_nmaxs++;
            if (lv_nmaxs == 5)
            {
                errorCode = 20129;
                // Warning.print(this, "com.imsl.stat", "ARMA.UnBounded", null);
                System.out.println("UnBounded");
            }
        }
    }

    private void l_u7lsf(int i, double ad[], int j, double ad1[], int k, int ai[], double ad2[], int l, double d,
            double ad3[], double ad4[], int ai1[], int i1, int j1, int ai2[], int k1, int l1, int i2)
    {
        double d9 = 2.2250738585072009E-308D;
        double d6 = 1.7976931348623157E+308D;
        if (d6 * d9 < 1.0D)
        {
            d9 = 1.0D / d6;
        }
        double d7 = 2.2204460492503131E-016D;
        if (ad3[0] == -999D)
        {
            ad4[0] = 0.0D;
            l_u11nf(i, ad2, -1, ad, j, ad, l1);
            double d2 = BLAS.nrm2(i, ad, l1, 1);
            double d5 = 0.0D;
            for (int j2 = 1; j2 <= i; j2++)
            {
                double d11 = 0.0D;
                for (int k2 = j2; k2 <= i; k2++)
                {
                    int i4 = ai[k2 - 1];
                    d11 += (ad1[(k2 - 1) * k + (j2 - 1)] * ad[(j + i4) - 1]) / (ad2[i4 - 1] * ad2[i4 - 1]);
                }

                ad[(l1 + j2) - 1] = d11;
            }

            d5 = BLAS.nrm2(i, ad, l1, 1);
            if (d5 <= d9)
            {
                ad3[0] = d2 * Math.sqrt(d2);
            }
            else
            {
                ad3[0] = d2 / d5;
                ad3[0] *= d2;
                ad3[0] /= d5;
                ad3[0] *= d2;
            }
            ad3[0] = Math.min(ad3[0], d);
        }
        if (ai1[0] != 0)
        {
            lv_nsing = i;
            for (int l2 = 1; l2 <= i; l2++)
            {
                if (ad1[(l2 - 1) * k + (l2 - 1)] == 0.0D && lv_nsing == i)
                {
                    lv_nsing = l2 - 1;
                }
                if (lv_nsing < i)
                {
                    ad[(l1 + l2) - 1] = 0.0D;
                }
            }

            l_u12sf(lv_nsing, ad1, k, ad, l, l1);
            for (int i3 = 1; i3 <= i; i3++)
            {
                ad[(j1 + ai[i3 - 1]) - 1] = -ad[(l1 + i3) - 1];
            }

            l_u11nf(i, ad2, 1, ad, j1, ad, l1);
            lv_gnleng = BLAS.nrm2(i, ad, l1, 1);
            l_u11nf(i, ad2, -1, ad, j, ad, l1);
            lv_sgnorm = BLAS.nrm2(i, ad, l1, 1);
        }
        double d8 = 1.5D;
        double d1 = 0.75D;
        if (lv_gnleng <= d8 * ad3[0])
        {
            ai2[0] = 1;
            System.arraycopy(ad, j1, ad, i1, i);
            ad4[0] = 0.0D;
            ad3[0] = Math.min(ad3[0], lv_gnleng);
        }
        else
        {
            ai2[0] = 0;
            if (ad4[0] > 0.0D)
            {
                ad4[0] += -((lv_phi + lv_deltap) / ad3[0]) * (((lv_deltap - ad3[0]) + lv_phi) / lv_phip);
            }
            lv_phi = lv_gnleng - ad3[0];
            double d3;
            if (lv_nsing == i)
            {
                if (ai1[0] != 0)
                {
                    ai1[0] = 0;
                    for (int j3 = 1; j3 <= i; j3++)
                    {
                        int j4 = ai[j3 - 1];
                        ad[(l1 + j3) - 1] = ad2[j4 - 1] * ad2[j4 - 1] * ad[(j1 + j4) - 1];
                    }

                    l_u12sf(i, ad1, k, ad, l1, l1);
                    lv_phipi = -Math.pow(BLAS.nrm2(i, ad, l1, 1), 2D) / lv_gnleng;
                }
                d3 = -lv_phi / lv_phipi;
            }
            else
            {
                ai1[0] = 0;
                d3 = 0.0D;
            }
            double d4 = lv_sgnorm / ad3[0];
            boolean flag = false;
            do
            {
                if (ad4[0] < d3 || ad4[0] > d4)
                {
                    ad4[0] = Math.max(Math.sqrt(d3 * d4), 0.001D * d4);
                }
                double d12 = Math.sqrt(ad4[0]);
                BLAS.dvcal(i, d12, ad2, 0, 1, ad, l1, 1);
                l_u11sf(i, ad1, k, ai, ad, l1, l, i1, k1, i2);
                BLAS.scal(i, -1D, ad, i1, 1);
                l_u11nf(i, ad2, 1, ad, i1, ad, i2);
                double d10 = BLAS.nrm2(i, ad, i2, 1);
                lv_phi = d10 - ad3[0];
                for (int k3 = 1; k3 <= i; k3++)
                {
                    int k4 = ai[k3 - 1];
                    ad[(l1 + k3) - 1] = ad2[k4 - 1] * ad[(i2 + k4) - 1];
                }

                for (int l3 = 1; l3 <= i; l3++)
                {
                    if (Math.abs(ad[(k1 + l3) - 1]) >= d9)
                    {
                        ad[(l1 + l3) - 1] /= ad[(k1 + l3) - 1];
                    }
                    if (l3 < i)
                    {
                        BLAS.axpy(i - l3, -ad[(l1 + l3) - 1], ad1, (l3 - 1) * k + l3, 1, ad, l1 + l3, 1);
                    }
                }

                lv_phip = -Math.pow(BLAS.nrm2(i, ad, l1, 1), 2D) / d10;
                if (d10 >= d1 * ad3[0] && d10 <= d8 * ad3[0] || d4 - d3 <= 10D * d7)
                {
                    flag = true;
                }
                else
                {
                    d3 = Math.max(d3, ad4[0] - lv_phi / lv_phip);
                    if (lv_phi < 0.0D)
                    {
                        d4 = ad4[0];
                    }
                    ad4[0] += -(d10 / ad3[0]) * (lv_phi / lv_phip);
                }
            }
            while (!flag);
        }
        lv_deltap = ad3[0];
    }

    private void l_u8lsf(int i, int j, double ad[], int k, double ad1[])
    {
        if (i == 1)
        {
            System.arraycopy(ad, k, ad1, 0, j);
        }
        else
        {
            for (int l = 1; l <= j; l++)
            {
                ad1[l - 1] = Math.max(ad1[l - 1], ad[(k + l) - 1]);
            }

        }
        for (int i1 = 1; i1 <= j; i1++)
        {
            if (ad1[i1 - 1] <= 9.9999999999999995E-007D)
            {
                ad1[i1 - 1] = 1.0D;
            }
        }

    }

    private void l_acf(int i, double ad[], int j, int k, int l, double ad1[], int i1, double ad2[], double ad3[],
            double ad4[])
    {
        if (l == 1)
        {
            ad1[0] = BLAS.sum(i, ad, 0, 1) / (double) i;
        }
        BLAS.set(i1 + 1, 0.0D, ad2, 0, 1);
        for (int k2 = 0; k2 <= i1; k2++)
        {
            for (int k1 = 1; k1 <= i - k2; k1++)
            {
                ad2[k2] += ((ad[k1 - 1] - ad1[0]) * (ad[(k1 + k2) - 1] - ad1[0])) / (double) i;
            }

        }

        if (ad2[0] < 2.2250738585072009E-308D)
        {
            Object aobj[] =
            {
                new Double(ad2[0])
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "ARMA.VariancePositive", aobj);
            throw new IllegalArgumentException("VariancePositive");
        }
        int j1 = i1 + 1;
        double d = 1.0D / ad2[0];
        BLAS.dvcal(j1, d, ad2, 0, 1, ad3, 0, 1);
        if (k == 1)
        {
            BLAS.set(i1, 0.0D, ad4, 0, 1);
            for (int l2 = 1; l2 <= i1; l2++)
            {
                for (int l1 = -i1; l1 <= i1; l1++)
                {
                    ad4[l2 - 1] += ad3[Math.abs(l1)] * ad3[Math.abs(l1)] * (1.0D + 2D * ad3[l2] * ad3[l2]);
                }

                for (int i2 = -(i1 - l2); i2 <= i1 - l2; i2++)
                {
                    ad4[l2 - 1] += ad3[Math.abs(i2 + l2)] * ad3[Math.abs(i2 - l2)];
                }

                for (int j2 = -(i1 - l2); j2 <= i1; j2++)
                {
                    ad4[l2 - 1] += -4D * ad3[l2] * ad3[Math.abs(j2)] * ad3[Math.abs(j2 - l2)];
                }

                ad4[l2 - 1] = Math.sqrt((1.0D / (double) i) * ad4[l2 - 1]);
            }

        }
        else if (k == 2)
        {
            for (int i3 = 1; i3 <= i1; i3++)
            {
                ad4[i3 - 1] = Math.sqrt((double) (i - i3) / (double) (i * (i + 2)));
            }

        }
    }

    private int l_isanan(int i, double ad[], int j)
    {
        int l = 0;
        int i1 = 1;
        int k = 1;
        do
        {
            if (i1 > i)
            {
                break;
            }
            if (Double.isNaN(ad[k - 1]))
            {
                l = i1;
                break;
            }
            k += j;
            i1++;
        }
        while (true);
        return l;
    }

    private void l_mxtxf(int i, int j, double ad[], int k, int l, double ad1[], int i1)
    {
        BLAS.gemm('T', 'N', j, j, i, 1.0D, ad, 0, k, ad, 0, k, 0.0D, ad1, 0, i1);
    }

    private void l_chfac(int i, double ad[], int j, double d, int ai[], double ad1[], int k)
    {
        int ai1[] =
        {
            0
        };
        ai1[0] = 1;
        l_c1dim(1, i, "N", j, "LDA", ai1);
        l_c1dim(1, i, "*N", k, "LDR", ai1);
        int l = 0;
        for (int i1 = 1; i1 <= i; i1++)
        {
            System.arraycopy(ad, j * (i1 - 1), ad1, k * (i1 - 1), i1);
        }

        ai[0] = 0;
        for (int j1 = 1; j1 <= i; j1++)
        {
            double d1 = 0.0D;
            double d3 = d * Math.sqrt(Math.abs(ad1[(j1 + k * (j1 - 1)) - 1]));
            for (int k1 = 1; k1 <= j1 - 1; k1++)
            {
                double d2 = ad1[(k1 + k * (j1 - 1)) - 1] - BLAS.dot(k1 - 1, ad1, k * (k1 - 1), 1, ad1, k * (j1 - 1), 1);
                if (ad1[(k1 + k * (k1 - 1)) - 1] != 0.0D)
                {
                    d2 /= ad1[(k1 + k * (k1 - 1)) - 1];
                    ad1[(k1 + k * (j1 - 1)) - 1] = d2;
                    d1 += d2 * d2;
                    continue;
                }
                if (l == 0 && Math.abs(d2) > d3 * BLAS.nrm2(k1 - 1, ad1, k * (k1 - 1), 1))
                {
                    l = j1;
                }
                ad1[(k1 + k * (j1 - 1)) - 1] = 0.0D;
            }

            d1 = ad1[(j1 + k * (j1 - 1)) - 1] - d1;
            if (Math.abs(d1) <= d * Math.abs(ad1[(j1 + k * (j1 - 1)) - 1]))
            {
                d1 = 0.0D;
            }
            else if (d1 < 0.0D)
            {
                d1 = 0.0D;
                if (l == 0)
                {
                    l = j1;
                }
            }
            else
            {
                ai[0]++;
            }
            ad1[(j1 + k * (j1 - 1)) - 1] = Math.sqrt(d1);
        }

        if (l != 0)
        {
            errorCode = 20406;
            Object aobj[] =
            {
                    new Integer(l), new Double(d)
            };
            // Warning.print(this, "com.imsl.stat", "KalmanFilter.SubMatrix",
            // aobj);
            System.out.println("KalmanFilter.SubMatrix");
        }
        l_c1trg(i, ad1, k);
    }

    private void l_c1trg(int i, double ad[], int j)
    {
        for (int k = 1; k <= i - 1; k++)
        {
            BLAS.set(i - k, 0.0D, ad, (k + 1 + j * (k - 1)) - 1, 1);
        }

    }

    private void l_rcovb(int i, double ad[], int j, double d, double ad1[], int k)
    {
        int ai[] =
        {
            0
        };
        int ai1[] =
        {
            0
        };
        double ad2[] =
        {
            0.0D
        };
        ai[0] = 1;
        l_c1dim(1, i, "m", j, "ldr", ai);
        l_c1dim(1, i, "*m", k, "cov_col_dim", ai);
        ai[0]++;
        l_c1r(i, ad, j, ai);
        errorCode = 0;
        l_girts(i, ad, j, 0, ad2, 1, 3, ai1, ad2, 1, ad1, k);
        if (errorCode != 0)
        {
            return;
        }
        for (int l = 1; l <= i; l++)
        {
            if (ad1[(l - 1) * k + (l - 1)] > 0.0D)
            {
                for (int k1 = 1; k1 <= l - 1; k1++)
                {
                    double d1 = ad1[(l - 1) * k + (k1 - 1)];
                    BLAS.axpy(k1, d1, ad1, (l - 1) * k, 1, ad1, (k1 - 1) * k, 1);
                }

                double d2 = ad1[(l - 1) * k + (l - 1)];
                BLAS.scal(l, d2, ad1, (l - 1) * k, 1);
            }
            else
            {
                BLAS.set(l, 0.0D, ad1, (l - 1) * k, 1);
            }
        }

        for (int i1 = 1; i1 <= i; i1++)
        {
            BLAS.scal(i1, d, ad1, (i1 - 1) * k, 1);
        }

        for (int j1 = 1; j1 < i; j1++)
        {
            BLAS.copy(i - j1, ad1, j1 * k + (j1 - 1), k, ad1, (j1 - 1) * k + j1, 1);
        }

    }

    private void l_l2rrr(int i, int j, double ad[], int k, int l, int ai[], double ad1[], int i1, double ad2[], int j1,
            int k1, int l1)
    {
        double d8 = 0.0D;
        double d3 = 1.0D;
        double d4 = 2.2250738585072009E-308D;
        double d2 = 1.7976931348623157E+308D;
        if (d4 * d2 < d3)
        {
            d4 = d3 / d2;
        }
        for (int k3 = 1; k3 <= j; k3++)
        {
            System.arraycopy(ad, (k3 - 1) * k, ad1, (k3 - 1) * i1, i);
        }

        int i3 = 1;
        int j3 = 0;
        if (l != 0)
        {
            for (int l3 = 1; l3 <= j; l3++)
            {
                boolean flag1 = ai[l3 - 1] > 0;
                boolean flag = ai[l3 - 1] < 0;
                ai[l3 - 1] = l3;
                if (flag)
                {
                    ai[l3 - 1] = -l3;
                }
                if (!flag1)
                {
                    continue;
                }
                if (l3 != i3)
                {
                    BLAS.swap(i, ad1, (i3 - 1) * i1, ad1, (l3 - 1) * i1);
                }
                ai[l3 - 1] = ai[i3 - 1];
                ai[i3 - 1] = l3;
                i3++;
            }

            j3 = j;
            for (int i4 = j; i4 >= 1; i4--)
            {
                if (ai[i4 - 1] >= 0)
                {
                    continue;
                }
                ai[i4 - 1] = -ai[i4 - 1];
                if (i4 != j3)
                {
                    BLAS.swap(i, ad1, (j3 - 1) * i1, ad1, (i4 - 1) * i1);
                    int l4 = ai[j3 - 1];
                    ai[j3 - 1] = ai[i4 - 1];
                    ai[i4 - 1] = l4;
                }
                j3--;
            }

        }
        for (int j4 = 1; j4 <= j; j4++)
        {
            ad2[(k1 + j4) - 1] = BLAS.nrm2(i, ad1, (j4 - 1) * i1, 1);
        }

        System.arraycopy(ad2, (k1 + i3) - 1, ad2, (j1 + i3) - 1, (j3 - i3) + 1);
        if (l != 0)
        {
            System.arraycopy(ad2, (j1 + i3) - 1, ad2, (l1 + i3 + j) - 2, (j3 - i3) + 1);
        }
        int k5 = Math.min(i, j);
        for (int j5 = 1; j5 <= k5; j5++)
        {
            if (j5 >= i3 && j5 < j3)
            {
                int i2 = (j3 - j5) + 1;
                int k2 = 1;
                int l5 = (BLAS.ismax(i2, ad2, (j1 + j5) - 1, k2) + j5) - 1;
                double d1 = ad2[(j1 + l5) - 1];
                if (l5 != j5)
                {
                    BLAS.swap(i, ad1, (j5 - 1) * i1, ad1, (l5 - 1) * i1);
                    ad2[(j1 + l5) - 1] = ad2[(j1 + j5) - 1];
                    if (l != 0)
                    {
                        ad2[(l1 + l5 + j) - 2] = ad2[(l1 + j5 + j) - 2];
                    }
                    int i5 = ai[l5 - 1];
                    ai[l5 - 1] = ai[j5 - 1];
                    ai[j5 - 1] = i5;
                }
            }
            ad2[(j1 + j5) - 1] = d8;
            if (j5 == i)
            {
                continue;
            }
            double d7 = BLAS.nrm2((i - j5) + 1, ad1, (j5 - 1) * i1 + (j5 - 1), 1);
            if (d7 < d4)
            {
                continue;
            }
            if (ad1[(j5 - 1) * i1 + (j5 - 1)] < d8)
            {
                d7 = -d7;
            }
            BLAS.scal((i - j5) + 1, d3 / d7, ad1, (j5 - 1) * i1 + (j5 - 1), 1);
            ad1[(j5 - 1) * i1 + (j5 - 1)] += d3;
            if (j5 < j)
            {
                int j2 = (i - j5) + 1;
                int l2 = j - j5;
                double d = -d3 / ad1[(j5 - 1) * i1 + (j5 - 1)];
                boolean flag2 = true;
                boolean flag3 = true;
                BLAS.gemv('T', j2, l2, d, ad1, j5 * i1 + (j5 - 1), i1, ad1, (j5 - 1) * i1 + (j5 - 1), 1, 0.0D, ad2, l1,
                        1);
                BLAS.ger((i - j5) + 1, j - j5, d3, ad1, (j5 - 1) * i1 + (j5 - 1), 1, ad2, l1, 1, ad1, j5 * i1
                        + (j5 - 1), i1);
            }
            for (int k4 = j5 + 1; k4 <= j; k4++)
            {
                if (k4 < i3 || k4 > j3 || ad2[(j1 + k4) - 1] == d8)
                {
                    continue;
                }
                double d6 = d3 - Math.pow(Math.abs(ad1[(k4 - 1) * i1 + (j5 - 1)]) / ad2[(j1 + k4) - 1], 2D);
                d6 = Math.max(d6, d8);
                double d5 = d6;
                if (l != 0)
                {
                    d6 = d3 + 0.050000000000000003D * d6 * Math.pow(ad2[(j1 + k4) - 1] / ad2[(l1 + k4 + j) - 2], 2D);
                }
                else
                {
                    d6 = d3 + 0.050000000000000003D * d6;
                }
                if (d6 != d3)
                {
                    ad2[(j1 + k4) - 1] *= Math.sqrt(d5);
                    continue;
                }
                ad2[(j1 + k4) - 1] = BLAS.nrm2(i - j5, ad1, (k4 - 1) * i1 + j5, 1);
                if (l != 0)
                {
                    ad2[(l1 + k4 + j) - 2] = ad2[(j1 + k4) - 1];
                }
            }

            ad2[(j1 + j5) - 1] = ad1[(j5 - 1) * i1 + (j5 - 1)];
            ad1[(j5 - 1) * i1 + (j5 - 1)] = -d7;
        }

    }

    private void l_u11nf(int i, double ad[], int j, double ad1[], int k, double ad2[], int l)
    {
        if (j < 0)
        {
            if (j == -1)
            {
                for (int i1 = 1; i1 <= i; i1++)
                {
                    ad2[(l + i1) - 1] = ad1[(k + i1) - 1] / ad[i1 - 1];
                }

            }
            else
            {
                for (int j1 = 1; j1 <= i; j1++)
                {
                    ad2[(l + j1) - 1] = ad1[(k + j1) - 1] / Math.pow(ad[j1 - 1], -j);
                }

            }
        }
        else if (j == 1)
        {
            for (int k1 = 1; k1 <= i; k1++)
            {
                ad2[(l + k1) - 1] = ad1[(k + k1) - 1] * ad[k1 - 1];
            }

        }
        else
        {
            for (int l1 = 1; l1 <= i; l1++)
            {
                ad2[(l + l1) - 1] = Math.pow(ad[l1 - 1], j) * ad1[(k + l1) - 1];
            }

        }
    }

    private void l_u10sf(int i, int j, double ad[], int k, double ad1[], int l, int i1, int j1)
    {
        for (int k1 = 1; k1 <= j; k1++)
        {
            if (ad1[(l + k1) - 1] != 0.0D)
            {
                double d = ad1[(l + k1) - 1] * ad1[(i1 + k1) - 1];
                if (k1 < i)
                {
                    d += BLAS.dot(i - k1, ad, (k1 - 1) * k + k1, 1, ad1, i1 + k1, 1);
                }
                double d1 = -d / ad1[(l + k1) - 1];
                ad1[(i1 + k1) - 1] += ad1[(l + k1) - 1] * d1;
                if (k1 < i)
                {
                    BLAS.axpy(i - k1, d1, ad, (k1 - 1) * k + k1, 1, ad1, i1 + k1, 1);
                }
            }
            ad1[(j1 + k1) - 1] = ad1[(i1 + k1) - 1];
        }

    }

    private void l_nsre(int i, int j, double ad[], int k, double d, int l, double ad1[], int i1, int ai[], int j1,
            int k1, int l1, int ai1[], int i2, int j2, double ad2[], int k2, int l2, double ad3[], int i3)
    {
        int j4;
        if (l == 0)
        {
            j4 = 0;
            boolean flag = false;
        }
        else
        {
            j4 = ai[BLAS.iimax(l, ai, 0, 1) - 1] * j1;
            int k4 = ai[BLAS.iimin(l, ai, 0, 1) - 1];
        }
        int l4;
        if (k1 == 0)
        {
            l4 = 0;
            boolean flag1 = false;
        }
        else
        {
            l4 = ai1[BLAS.iimax(k1, ai1, 0, 1) - 1] * i2;
            int i5 = ai1[BLAS.iimin(k1, ai1, 0, 1) - 1];
        }
        if (j2 == 1)
        {
            if ((1 + j4) - j > k2 || k2 > l2 || l2 > i) // Messages.throwIllegalArgumentException("com.imsl.stat",
                                                        // "ARMA.OutOfRange3",
                                                        // null);
            {
                throw new IllegalArgumentException("ARMA.OutOfRange3");
            }
        }
        else if (j2 == -1 && (1 > k2 || k2 > l2 || l2 > (i - j4) + j)) // Messages.throwIllegalArgumentException("com.imsl.stat",
                                                                       // "ARMA.OutOfRange3",
                                                                       // null);
        {
            throw new IllegalArgumentException("ARMA.OutOfRange3");
        }

        int j8 = (l2 - k2) + 1;
        if (j2 == 1)
        {
            int l6 = 0;
            int j5 = k2;
            int k3 = j2;
            for (int j3 = ((l2 - k2) + k3) / k3 <= 0 ? 0 : ((l2 - k2) + k3) / k3; j3 > 0; j3--)
            {
                l6 += j2;
                double d1;
                if (j5 <= 0)
                {
                    d1 = ad[(j + j5) - 1];
                }
                else
                {
                    d1 = ad[j5 - 1];
                }
                ad3[(i3 + l6) - 1] = d1 - d;
                for (int j7 = 1; j7 <= l; j7++)
                {
                    int l5 = j5 - j2 * ai[j7 - 1] * j1;
                    double d2;
                    if (l5 <= 0)
                    {
                        d2 = ad[(j + l5) - 1];
                    }
                    else
                    {
                        d2 = ad[l5 - 1];
                    }
                    ad3[(i3 + l6) - 1] += -ad1[(i1 + j7) - 1] * (d2 - d);
                }

                for (int k7 = 1; k7 <= k1; k7++)
                {
                    int i6 = j5 - j2 * ai1[k7 - 1] * i2;
                    double d3;
                    if (i6 >= k2)
                    {
                        d3 = ad3[(i3 + i6) - k2];
                    }
                    else
                    {
                        d3 = ad2[(l4 + i6) - k2];
                    }
                    ad3[(i3 + l6) - 1] += ad1[(l1 + k7) - 1] * d3;
                }

                j5 += k3;
            }

        }
        else if (j2 == -1)
        {
            int i7 = j8 + 1;
            int k5 = l2;
            int i4 = j2;
            for (int l3 = ((k2 - l2) + i4) / i4 <= 0 ? 0 : ((k2 - l2) + i4) / i4; l3 > 0; l3--)
            {
                i7 += j2;
                ad3[(i3 + i7) - 1] = ad[k5 - 1] - d;
                for (int l7 = 1; l7 <= l; l7++)
                {
                    int j6 = k5 - j2 * ai[l7 - 1] * j1;
                    ad3[(i3 + i7) - 1] += -ad1[(i1 + l7) - 1] * (ad[j6 - 1] - d);
                }

                for (int i8 = 1; i8 <= k1; i8++)
                {
                    int k6 = k5 - j2 * ai1[i8 - 1] * i2;
                    double d4;
                    if (k6 <= l2)
                    {
                        d4 = ad3[(i3 + j8) - (l2 - k6) - 1];
                    }
                    else
                    {
                        d4 = ad2[k6 - l2 - 1];
                    }
                    ad3[(i3 + i7) - 1] += ad1[(l1 + i8) - 1] * d4;
                }

                k5 += i4;
            }

        }
    }

    private void l_nsfbc(int i, double ad[], int j, double d, int k, double ad1[], int l, int ai[], int i1, int j1,
            int k1, int ai1[], int l1, int i2, double ad2[], int j2, int k2, int l2, double d1, int ai2[], int ai3[],
            double ad3[])
    {
        int j4;
        if (k == 0)
        {
            j4 = 0;
            boolean flag = false;
        }
        else
        {
            j4 = ai[BLAS.iimax(k, ai, 0, 1) - 1] * i1;
            int k4 = ai[BLAS.iimin(k, ai, 0, 1) - 1];
        }
        int l4;
        if (j1 == 0)
        {
            l4 = 0;
            boolean flag1 = false;
        }
        else
        {
            l4 = ai1[BLAS.iimax(j1, ai1, 0, 1) - 1] * l1;
            int i5 = ai1[BLAS.iimin(j1, ai1, 0, 1) - 1];
        }
        if (i2 == -1 && (k2 < 1 || k2 > (i - j4) + 1)) // Messages.throwIllegalArgumentException("com.imsl.stat",
                                                       // "ARMA.OutOfRange2",
                                                       // null);
        {
            throw new IllegalArgumentException("ARMA.OutOfRange2");
        }

        double d7;
        if (d1 == 0.0D)
        {
            d7 = 0.0D;
            double d8 = BLAS.sum(i, ad, 0, 1) / (double) i;
            for (int i3 = 1; i3 <= i; i3++)
            {
                d7 += Math.pow(ad[i3 - 1] - d8, 2D) / (double) (i - 1);
            }

            d7 = 0.01D * Math.sqrt(d7);
        }
        else
        {
            d7 = d1;
        }
        ai2[0] = 0;
        ai3[0] = l2;
        if (i2 == 1)
        {
            for (int j3 = 1; j3 <= l4 / 2; j3++)
            {
                double d2 = ad2[(j2 + l4) - j3];
                ad2[(j2 + l4) - j3] = ad2[(j2 + j3) - 1];
                ad2[(j2 + j3) - 1] = d2;
            }

        }
        BLAS.set(l2, (0.0D / 0.0D), ad3, 0, 1);
        int i6 = 1;
        do
        {
            if (i6 > l2)
            {
                break;
            }
            ad3[i6 - 1] = 0.0D;
            if (k > 0)
            {
                for (int k3 = 1; k3 <= k; k3++)
                {
                    int k5 = i6 - i1 * ai[k3 - 1];
                    double d3;
                    if (k5 <= 0)
                    {
                        d3 = ad[(k2 + i2 * k5) - 1] - d;
                    }
                    else
                    {
                        d3 = ad3[Math.abs(k5) - 1];
                    }
                    ad3[i6 - 1] += ad1[(l + k3) - 1] * d3;
                }

            }
            if (j1 > 0)
            {
                for (int j5 = 1; j5 <= j1; j5++)
                {
                    int l5 = i6 - l1 * ai1[j5 - 1];
                    double d4;
                    if (l5 <= 0)
                    {
                        d4 = ad2[j2 + Math.abs(l5)];
                    }
                    else
                    {
                        d4 = 0.0D;
                    }
                    ad3[i6 - 1] += -ad1[(k1 + j5) - 1] * d4;
                }

            }
            if (Math.abs(ad3[i6 - 1]) < d7)
            {
                ai2[0] = 1;
                ai3[0] = i6;
                break;
            }
            i6++;
        }
        while (true);
        if (i2 == 1)
        {
            for (int l3 = 1; l3 <= l4 / 2; l3++)
            {
                double d5 = ad2[(j2 + l4) - l3];
                ad2[(j2 + l4) - l3] = ad2[(j2 + l3) - 1];
                ad2[(j2 + l3) - 1] = d5;
            }

        }
        if (i2 == -1)
        {
            for (int i4 = 1; i4 <= ai3[0] / 2; i4++)
            {
                double d6 = ad3[ai3[0] - i4];
                ad3[ai3[0] - i4] = ad3[i4 - 1];
                ad3[i4 - 1] = d6;
            }

        }
    }

    private void l_u12sf(int i, double ad[], int j, double ad1[], int k, int l)
    {
        ad1[(l + i) - 1] = ad1[(k + i) - 1] / ad[(i - 1) * j + (i - 1)];
        for (int i1 = i - 1; i1 >= 1; i1--)
        {
            double d = BLAS.dot(i - i1, ad, i1 * j + (i1 - 1), j, ad1, l + i1, 1);
            ad1[(l + i1) - 1] = (ad1[(k + i1) - 1] - d) / ad[(i1 - 1) * j + (i1 - 1)];
        }

    }

    private void l_u11sf(int i, double ad[], int j, int ai[], double ad1[], int k, int l, int i1, int j1, int k1)
    {
        l_csfrg(i, ad, j);
        BLAS.copy(i, ad, 0, j + 1, ad1, i1, 1);
        System.arraycopy(ad1, l, ad1, k1, i);
        for (int i2 = 1; i2 <= i; i2++)
        {
            int i4 = ai[i2 - 1];
            if (ad1[(k + i4) - 1] != 0.0D)
            {
                BLAS.set((i - i2) + 1, 0.0D, ad1, (j1 + i2) - 1, 1);
                ad1[(j1 + i2) - 1] = ad1[(k + i4) - 1];
                double d2 = 0.0D;
                for (int j3 = i2; j3 <= i; j3++)
                {
                    if (ad1[(j1 + j3) - 1] == 0.0D)
                    {
                        continue;
                    }
                    double d;
                    double d3;
                    if (Math.abs(ad[(j3 - 1) * j + (j3 - 1)]) < Math.abs(ad1[(j1 + j3) - 1]))
                    {
                        double d1 = ad[(j3 - 1) * j + (j3 - 1)] / ad1[(j1 + j3) - 1];
                        d3 = 0.5D / Math.sqrt(0.25D + 0.25D * d1 * d1);
                        d = d3 * d1;
                    }
                    else
                    {
                        double d5 = ad1[(j1 + j3) - 1] / ad[(j3 - 1) * j + (j3 - 1)];
                        d = 0.5D / Math.sqrt(0.25D + 0.25D * d5 * d5);
                        d3 = d * d5;
                    }
                    ad[(j3 - 1) * j + (j3 - 1)] = d * ad[(j3 - 1) * j + (j3 - 1)] + d3 * ad1[(j1 + j3) - 1];
                    double d6 = d * ad1[(k1 + j3) - 1] + d3 * d2;
                    d2 = -d3 * ad1[(k1 + j3) - 1] + d * d2;
                    ad1[(k1 + j3) - 1] = d6;
                    int l3 = j3 + 1;
                    if (i < l3)
                    {
                        continue;
                    }
                    for (int l1 = l3; l1 <= i; l1++)
                    {
                        double d7 = d * ad[(j3 - 1) * j + (l1 - 1)] + d3 * ad1[(j1 + l1) - 1];
                        ad1[(j1 + l1) - 1] = -d3 * ad[(j3 - 1) * j + (l1 - 1)] + d * ad1[(j1 + l1) - 1];
                        ad[(j3 - 1) * j + (l1 - 1)] = d7;
                    }

                }

            }
            ad1[(j1 + i2) - 1] = ad[(i2 - 1) * j + (i2 - 1)];
            ad[(i2 - 1) * j + (i2 - 1)] = ad1[(i1 + i2) - 1];
        }

        int k4 = i;
        for (int j2 = 1; j2 <= i; j2++)
        {
            if (ad1[(j1 + j2) - 1] == 0.0D && k4 == i)
            {
                k4 = j2 - 1;
            }
            if (k4 < i)
            {
                ad1[(k1 + j2) - 1] = 0.0D;
            }
        }

        if (k4 >= 1)
        {
            for (int k3 = 1; k3 <= k4; k3++)
            {
                int k2 = (k4 - k3) + 1;
                double d4 = 0.0D;
                int i3 = k2 + 1;
                if (k4 >= i3)
                {
                    d4 = BLAS.dot(k4 - k2, ad, (k2 - 1) * j + (i3 - 1), 1, ad1, (k1 + i3) - 1, 1);
                }
                ad1[(k1 + k2) - 1] = (ad1[(k1 + k2) - 1] - d4) / ad1[(j1 + k2) - 1];
            }

        }
        for (int l2 = 1; l2 <= i; l2++)
        {
            int j4 = ai[l2 - 1];
            ad1[(i1 + j4) - 1] = ad1[(k1 + l2) - 1];
        }

    }

    private void l_a2mme(int i, double ad[], int j, int k, int l, double ad1[], double ad2[], double ad3[], int ai[],
            double ad4[]) // throws MatrixSingularException,
                          // IllConditionedException
    {
        int k1 = 3;
        k1++;
        if (ad[0] <= 0.0D)
        {
            Object aobj[] =
            {
                new Double(ad[0])
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "ARMA.VariancePositive", aobj);

            throw new IllegalArgumentException("ARMA.VariancePositive");
        }
        if (l == 1)
        {
            l_c1div(ad[k + 1], ad[k], ad1, 0);
        }
        else if (l == 2)
        {
            double d = Math.pow(ad[k], 2D) - ad[Math.abs(k - 1)] * ad[k + 1];
            ad1[0] = ad[k + 1] * ad[k] - ad[k + 2] * ad[Math.abs(k - 1)];
            l_c1div(ad1[0], d, ad1, 0);
            ad1[1] = ad[k] * ad[k + 2] - Math.pow(ad[k + 1], 2D);
            l_c1div(ad1[1], d, ad1, 1);
        }
        else
        {
            for (int i1 = 1; i1 <= l; i1++)
            {
                ad1[i1 - 1] = ad[k + i1];
                for (int j1 = 1; j1 <= l; j1++)
                {
                    ad2[(j1 - 1) * l + (i1 - 1)] = ad[Math.abs((k + i1) - j1)];
                }

            }

            errorCode = 0;
            l_l2lrg(l, ad2, l, ad1, 1, ad1, ad3, ai, ad4);
            if (errorCode == 11240) // throw new
                                    // IllConditionedException("ARMA.IllConditioned",
                                    // null);
            {
                throw new IllegalArgumentException("ARMA.IllConditioned");
            }
        }
    }

    private void l_m2mme(int i, double ad[], int j, int k, double ad1[], double d, int l, int i1, double ad2[],
            double ad3[], double ad4[], double ad5[], double ad6[], double ad7[], double ad8[], double ad9[],
            double ad10[], double ad11[]) // throws TooManyCallsException,
                                          // IncreaseErrRelException,
                                          // NewInitialGuessException
    {
        double ad12[] =
        {
            0.0D
        };
        if (ad[0] <= 0.0D)
        {
            Object aobj[] =
            {
                new Double(ad[0])
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "ARMA.VariancePositive", aobj);

            throw new IllegalArgumentException("ARMA.VariancePositive");
        }
        double d2;
        if (d == 0.0D)
        {
            d2 = 2.2204460492503131E-014D;
        }
        else
        {
            d2 = d;
        }
        int k2;
        if (l == 0)
        {
            k2 = 200;
        }
        else
        {
            k2 = l;
        }
        ad3[0] = -1D;
        System.arraycopy(ad1, 0, ad3, 1, k);
        if (i1 > 0)
        {
            if (k == 0)
            {
                System.arraycopy(ad, 0, ad4, 0, i1 + 1);
            }
            else if (k > 0)
            {
                BLAS.set(i1 + 1, 0.0D, ad4, 0, 1);
                for (int l1 = 0; l1 <= i1; l1++)
                {
                    for (int k1 = 0; k1 <= k; k1++)
                    {
                        for (int j2 = 0; j2 <= k; j2++)
                        {
                            ad4[l1] += ad3[k1] * ad3[j2] * ad[Math.abs((l1 + k1) - j2)];
                        }

                    }

                }

            }
            if (ad4[0] <= 0.0D)
            {
                Object aobj1[] =
                {
                    new Double(ad4[0])
                };
                // Messages.throwIllegalArgumentException("com.imsl.stat",
                // "ARMA.DerivedVarGTZero", aobj1);

                throw new IllegalArgumentException("ARMA.DerivedVarGTZero");
            }
            else
            {
                ad5[0] = Math.sqrt(ad4[0]);
                for (int i2 = 1; i2 <= i1; i2++)
                {
                    ad5[i2] = 0.0D;
                }

                int j1 = i1 + 1;
                l_m4mme(d2, j1, k2, ad4, ad5, ad6, ad12, ad7, ad8, ad9, ad10, ad11);
                if (ad6[0] <= 0.0D)
                {
                    Object aobj2[] =
                    {
                        new Double(ad6[0])
                    };
                    // Messages.throwIllegalArgumentException("com.imsl.stat",
                    // "ARMA.WrongRandShockVar", aobj2);

                    throw new IllegalArgumentException("ARMA.WrongRandShockVar");
                }
                else
                {
                    double d1 = -1D / ad6[0];
                    BLAS.dvcal(i1, d1, ad6, 1, 1, ad2, 0, 1);
                }
            }
        }
    }

    private void l_c1div(double d, double d1, double ad[], int i)
    {
        double d3 = 3.4021989709935937E+038D;
        if (Double.isNaN(d) || Double.isNaN(d1))
        {
            ad[i] = d3;
            return;
        }
        double d2 = Math.abs(d1);
        if (d2 <= 1.0D)
        {
            double d4 = 1.7976931348623157E+308D;
            if (Math.abs(d) < d4 * d2)
            {
                ad[i] = d / d1;
                return;
            }
            if (d == 0.0D)
            {
                ad[i] = d3;
            }
            else if (d1 >= 0.0D)
            {
                if (d >= 0.0D)
                {
                    ad[i] = (1.0D / 0.0D);
                }
                else
                {
                    ad[i] = (-1.0D / 0.0D);
                }
            }
            else if (d >= 0.0D)
            {
                ad[i] = (-1.0D / 0.0D);
            }
            else
            {
                ad[i] = (1.0D / 0.0D);
            }
            return;
        }
        double d5 = 2.2250738585072009E-308D;
        if (Math.abs(d) >= d5 * d2)
        {
            ad[i] = d / d1;
            return;
        }
        else
        {
            ad[i] = 0.0D;
            return;
        }
    }

    private void l_l2lrg(int i, double ad[], int j, double ad1[], int k, double ad2[], double ad3[], int ai[],
            double ad4[]) // throws MatrixSingularException
    {
        int ai1[] =
        {
            0
        };
        int ai2[] =
        {
                0, 0, 0, 0
        };
        double ad5[] =
        {
            0.0D
        };
        ai1[0] = 16;
        ai2[0] = 1;
        ai2[1] = 16;
        ai2[2] = 0;
        ai2[3] = 1;
        int l = 0;
        if (i % ai2[3] == 0)
        {
            l = ai2[2];
        }
        l_l2crg(i, ad, j, ad3, i + l, ai, ad5, ad4);
        l_lfsrg(i, ad3, i + l, ai, ad1, k, ad2);
        if (ad5[0] <= 2.2204460492503131E-016D)
        {
            errorCode = 11240;
            Object aobj[] =
            {
                new Double(ad5[0])
            };
            // Warning.print(this, "com.imsl.stat", "ARMA.MatrixIllConditioned",
            // aobj);
            System.out.println("ARMA.MatrixIllConditioned");
        }
    }

    private void l_l2crg(int i, double ad[], int j, double ad1[], int k, int ai[], double ad2[], double ad3[]) // throws
                                                                                                               // MatrixSingularException
    {
        int ai1[] =
        {
            0
        };
        int ai2[] =
        {
                0, 0
        };
        double ad4[] =
        {
            0.0D
        };
        ad2[0] = 1.0D;
        ai1[0] = 17;
        ai2[0] = 1;
        ai2[1] = 2;
        if (ai2[1] == 2)
        {
            l_nr1rr(i, i, ad, j, ad4);
        }
        l_l2trg(i, ad, j, ad1, k, ai, ad3);
        boolean flag = false;
        if (ai2[1] != 2)
        {
            flag = true;
        }
        if (!flag)
        {
            double d = 1.0D;
            BLAS.set(i, 0.0D, ad3, 0, 1);
            for (int i1 = 1; i1 <= i; i1++)
            {
                if (ad3[i1 - 1] != 0.0D)
                {
                    d = -ad3[i1 - 1] >= 0.0D ? Math.abs(d) : -Math.abs(d);
                }
                if (Math.abs(d - ad3[i1 - 1]) > Math.abs(ad1[(i1 - 1) * k + (i1 - 1)]))
                {
                    double d1 = Math.abs(ad1[(i1 - 1) * k + (i1 - 1)]) / Math.abs(d - ad3[i1 - 1]);
                    BLAS.scal(i, d1, ad3, 0, 1);
                    d *= d1;
                }
                double d9 = d - ad3[i1 - 1];
                double d10 = -d - ad3[i1 - 1];
                double d2 = Math.abs(d9);
                double d4 = Math.abs(d10);
                if (ad1[(i1 - 1) * k + (i1 - 1)] != 0.0D)
                {
                    d9 /= ad1[(i1 - 1) * k + (i1 - 1)];
                    d10 /= ad1[(i1 - 1) * k + (i1 - 1)];
                }
                else
                {
                    d9 = 1.0D;
                    d10 = 1.0D;
                }
                int i2 = i1 + 1;
                if (i2 <= i)
                {
                    for (int l = i2; l <= i; l++)
                    {
                        d4 += Math.abs(ad3[l - 1] + d10 * ad1[(l - 1) * k + (i1 - 1)]);
                        ad3[l - 1] += d9 * ad1[(l - 1) * k + (i1 - 1)];
                        d2 += Math.abs(ad3[l - 1]);
                    }

                    if (d2 < d4)
                    {
                        double d5 = d10 - d9;
                        d9 = d10;
                        BLAS.axpy(i - i1, d5, ad1, (i2 - 1) * k + (i1 - 1), k, ad3, i2 - 1, 1);
                    }
                }
                ad3[i1 - 1] = d9;
            }

            double d3 = 1.0D / BLAS.asum(i, ad3, 0, 1);
            BLAS.scal(i, d3, ad3, 0, 1);
            for (int j1 = i; j1 >= 1; j1--)
            {
                if (j1 < i)
                {
                    ad3[j1 - 1] += BLAS.dot(i - j1, ad1, (j1 - 1) * k + j1, 1, ad3, j1, 1);
                }
                if (Math.abs(ad3[j1 - 1]) > 1.0D)
                {
                    d3 = 1.0D / Math.abs(ad3[j1 - 1]);
                    BLAS.scal(i, d3, ad3, 0, 1);
                }
                int j2 = ai[j1 - 1];
                double d6 = ad3[j2 - 1];
                ad3[j2 - 1] = ad3[j1 - 1];
                ad3[j1 - 1] = d6;
            }

            d3 = 1.0D / BLAS.asum(i, ad3, 0, 1);
            BLAS.scal(i, d3, ad3, 0, 1);
            double d11 = 1.0D;
            for (int k1 = 1; k1 <= i; k1++)
            {
                int k2 = ai[k1 - 1];
                double d7 = ad3[k2 - 1];
                ad3[k2 - 1] = ad3[k1 - 1];
                ad3[k1 - 1] = d7;
                if (k1 < i)
                {
                    BLAS.axpy(i - k1, d7, ad1, (k1 - 1) * k + k1, 1, ad3, k1, 1);
                }
                if (Math.abs(ad3[k1 - 1]) > 1.0D)
                {
                    d3 = 1.0D / Math.abs(ad3[k1 - 1]);
                    BLAS.scal(i, d3, ad3, 0, 1);
                    d11 *= d3;
                }
            }

            d3 = 1.0D / BLAS.asum(i, ad3, 0, 1);
            BLAS.scal(i, d3, ad3, 0, 1);
            d11 *= d3;
            for (int l1 = i; l1 >= 1; l1--)
            {
                if (Math.abs(ad3[l1 - 1]) > Math.abs(ad1[(l1 - 1) * k + (l1 - 1)]))
                {
                    d3 = Math.abs(ad1[(l1 - 1) * k + (l1 - 1)]) / Math.abs(ad3[l1 - 1]);
                    BLAS.scal(i, d3, ad3, 0, 1);
                    d11 *= d3;
                }
                if (ad1[(l1 - 1) * k + (l1 - 1)] != 0.0D)
                {
                    ad3[l1 - 1] /= ad1[(l1 - 1) * k + (l1 - 1)];
                }
                else
                {
                    ad3[l1 - 1] = 1.0D;
                }
                double d8 = -ad3[l1 - 1];
                BLAS.axpy(l1 - 1, d8, ad1, (l1 - 1) * k, 1, ad3, 0, 1);
            }

            d3 = 1.0D / BLAS.asum(i, ad3, 0, 1);
            BLAS.scal(i, d3, ad3, 0, 1);
            d11 *= d3;
            if (ad4[0] != 0.0D)
            {
                ad2[0] = d11 / ad4[0];
            }
        }
        if (ad2[0] <= 2.2204460492503131E-016D)
        {
            errorCode = 11236;
            Object aobj[] =
            {
                new Double(ad2[0])
            };
            // Warning.print(this, "com.imsl.stat", "ARMA.MatrixAlgSingular",
            // aobj);
            System.out.println("ARMA.MatrixAlgSingular");
        }
    }

    private void l_lfsrg(int i, double ad[], int j, int ai[], double ad1[], int k, double ad2[])
    {
        System.arraycopy(ad1, 0, ad2, 0, i);
        double d1 = 2.2250738585072009E-308D;
        double d = 1.7976931348623157E+308D;
        if (d1 * d < 1.0D)
        {
            d1 = 1.0D / d;
        }
        if (k == 1)
        {
            for (int l = 1; l <= i - 1; l++)
            {
                int l1 = ai[l - 1];
                double d2 = ad2[l1 - 1];
                if (l1 != l)
                {
                    ad2[l1 - 1] = ad2[l - 1];
                    ad2[l - 1] = d2;
                }
                BLAS.axpy(i - l, d2, ad, (l - 1) * j + l, 1, ad2, l, 1);
            }

            for (int i1 = i; i1 >= 1; i1--)
            {
                if (Math.abs(ad[(i1 - 1) * j + (i1 - 1)]) <= d1) // Messages.throwIllegalArgumentException("com.imsl.stat",
                                                                 // "ARMA.MatrixSingular",
                                                                 // null);
                {
                    throw new IllegalArgumentException("ARMA.MatrixSingular");
                }
            }

            BLAS.trsv('U', 'N', 'N', i, ad, 0, j, ad2, 0, 1);
        }
        else if (k == 2)
        {
            for (int j1 = 1; j1 <= i; j1++)
            {
                if (Math.abs(ad[(j1 - 1) * j + (j1 - 1)]) <= d1) // Messages.throwIllegalArgumentException("com.imsl.stat",
                                                                 // "ARMA.MatrixSingular",
                                                                 // null);
                {
                    throw new IllegalArgumentException("ARMA.MatrixSingular");
                }
            }

            BLAS.trsv('U', 'T', 'N', i, ad, 0, j, ad2, 0, 1);
            for (int k1 = i - 1; k1 >= 1; k1--)
            {
                ad2[k1 - 1] += BLAS.dot(i - k1, ad, (k1 - 1) * j + k1, 1, ad2, k1, 1);
                int i2 = ai[k1 - 1];
                if (i2 != k1)
                {
                    double d3 = ad2[i2 - 1];
                    ad2[i2 - 1] = ad2[k1 - 1];
                    ad2[k1 - 1] = d3;
                }
            }

        }
    }

    private void l_nr1rr(int i, int j, double ad[], int k, double ad1[])
    {
        ad1[0] = 0.0D;
        for (int l = 1; l <= j; l++)
        {
            double d = BLAS.asum(i, ad, (l - 1) * k, 1);
            ad1[0] = Math.max(d, ad1[0]);
        }

    }

    private void l_c1r(int i, double ad[], int j, int ai[])
    {
        for (int k = 1; k <= i; k++)
        {
            if (ad[(k + j * (k - 1)) - 1] != 0.0D)
            {
                continue;
            }
            for (int l = k + 1; l <= i; l++)
            {
                if (ad[(k + j * (l - 1)) - 1] != 0.0D)
                {
                    Object aobj[] =
                    {
                            new Integer(k - 1), new Integer(l - 1), new Double(ad[(k + j * (k - 1)) - 1]),
                            new Double(ad[(k + j * (l - 1)) - 1])
                    };
                    // Messages.throwIllegalArgumentException("com.imsl.stat",
                    // "KalmanFilter.RmnElmntsNotZero", aobj);
                    throw new IllegalArgumentException("KalmanFilter.RmnElmntsNotZero");
                }
            }

        }

        ai[0]++;
    }

    private void l_c1dim(int i, int j, String s, int k, String s1, int ai[])
    {
        if (s.charAt(0) == '*')
        {
            ai[0]++;
            ai[0]++;
            if (k >= 1)
            {
                ai[0]++;
            }
            else
            {
                ai[0]++;
            }
        }
        else
        {
            ai[0]++;
            ai[0]++;
            if (k >= 1)
            {
                ai[0]++;
            }
            else
            {
                ai[0]++;
            }
        }
    }

    private void l_crgrg(int i, double ad[], int j, double ad1[], int k)
    {
        boolean flag = false;
        for (int i1 = 1; i1 <= i; i1++)
        {
            for (int l = 1; l <= i; l++)
            {
                ad1[(i1 - 1) * k + (l - 1)] = ad[(i1 - 1) * j + (l - 1)];
            }

        }

    }

    private void l_csfrg(int i, double ad[], int j)
    {
        for (int k = 1; k <= i - 1; k++)
        {
            BLAS.copy(i - k, ad, k * j + (k - 1), j, ad, (k - 1) * j + k, 1);
        }

    }

    private void l_l2trg(int i, double ad[], int j, double ad1[], int k, int ai[], double ad2[]) // throws
                                                                                                 // MatrixSingularException
    {
        int ai1[] =
        {
            0
        };
        int ai2[] =
        {
            0
        };
        int ai3[] =
        {
            0
        };
        int ai4[] =
        {
            0
        };
        int ai5[] =
        {
            0
        };
        int ai6[] =
        {
            0
        };
        int ai7[] =
        {
            0
        };
        int ai8[] =
        {
            0
        };
        l_crgrg(i, ad, j, ad1, k);
        int j5 = 0;
        int k5 = 1;
        int i6 = 1;
        double d1 = 2.2250738585072009E-308D;
        double d = 1.7976931348623157E+308D;
        if (d1 * d < 1.0D)
        {
            d1 = 1.0D / d;
        }
        k5 = 1;
        i6 = 1;
        for (int k2 = 1; k2 <= i; k2++)
        {
            int i5 = BLAS.iamax(i, ad1, k2 - 1, k);
            ad2[k2 - 1] = Math.abs(ad1[(i5 - 1) * k + (k2 - 1)]);
            if (ad2[k2 - 1] < d1)
            {
                ad2[k2 - 1] = 1.0D;
            }
            else
            {
                ad2[k2 - 1] = 1.0D / ad2[k2 - 1];
            }
        }

        for (; k5 < i; k5 += 8)
        {
            int l = (i - k5) + 1;
            l_l4trg(l, ad1, (k5 - 1) * k + (k5 - 1), ad2, k5 - 1, ai1);
            ai1[0] += k5 - 1;
            ai[k5 - 1] = ai1[0];
            if (Math.abs(ad1[(k5 - 1) * k + (ai1[0] - 1)]) > d1)
            {
                if (ai1[0] != k5)
                {
                    i6 = -i6;
                }
                double d2 = ad2[k5 - 1];
                ad2[k5 - 1] = ad2[ai1[0] - 1];
                ad2[ai1[0] - 1] = d2;
                d2 = ad1[(k5 - 1) * k + (ai1[0] - 1)];
                ad1[(k5 - 1) * k + (ai1[0] - 1)] = ad1[(k5 - 1) * k + (k5 - 1)];
                ad1[(k5 - 1) * k + (k5 - 1)] = d2;
                d2 = -1D / d2;
                double d8 = ad1[k5 * k + (ai1[0] - 1)];
                ad1[k5 * k + (ai1[0] - 1)] = ad1[k5 * k + (k5 - 1)];
                ad1[k5 * k + (k5 - 1)] = d8;
                for (int l2 = k5 + 1; l2 <= i; l2++)
                {
                    ad1[(k5 - 1) * k + (l2 - 1)] *= d2;
                    ad1[k5 * k + (l2 - 1)] += d8 * ad1[(k5 - 1) * k + (l2 - 1)];
                }

            }
            else
            {
                i6 = 0;
                j5 = ai1[0];
            }
            if (k5 + 1 >= i)
            {
                break;
            }
            l = i - k5;
            l_l4trg(l, ad1, k5 * k + k5, ad2, k5, ai2);
            ai2[0] += k5;
            ai[k5] = ai2[0];
            double d3 = ad2[k5];
            ad2[k5] = ad2[ai2[0] - 1];
            ad2[ai2[0] - 1] = d3;
            d3 = ad1[k5 * k + (ai2[0] - 1)];
            ad1[k5 * k + (ai2[0] - 1)] = ad1[k5 * k + k5];
            ad1[k5 * k + k5] = d3;
            if (Math.abs(d3) > d1)
            {
                d3 = -1D / d3;
                if (ai2[0] != k5 + 1)
                {
                    i6 = -i6;
                }
            }
            else
            {
                i6 = 0;
                j5 = ai2[0];
            }
            double d9 = ad1[(k5 - 1) * k + (ai2[0] - 1)];
            ad1[(k5 - 1) * k + (ai2[0] - 1)] = ad1[(k5 - 1) * k + k5];
            ad1[(k5 - 1) * k + k5] = d9;
            d9 = ad1[(k5 + 1) * k + (ai1[0] - 1)];
            ad1[(k5 + 1) * k + (ai1[0] - 1)] = ad1[(k5 + 1) * k + (k5 - 1)];
            ad1[(k5 + 1) * k + (k5 - 1)] = d9;
            double d12 = ad1[(k5 + 1) * k + (ai2[0] - 1)] + d9 * ad1[(k5 - 1) * k + k5];
            ad1[(k5 + 1) * k + (ai2[0] - 1)] = ad1[(k5 + 1) * k + k5];
            ad1[(k5 + 1) * k + k5] = d12;
            for (int i3 = k5 + 2; i3 <= i; i3++)
            {
                ad1[k5 * k + (i3 - 1)] *= d3;
                ad1[(k5 + 1) * k + (i3 - 1)] += d9 * ad1[(k5 - 1) * k + (i3 - 1)] + d12 * ad1[k5 * k + (i3 - 1)];
            }

            boolean flag5 = false;
            if (k5 + 2 >= i)
            {
                flag5 = true;
            }
            if (!flag5)
            {
                int i1 = i - k5 - 1;
                l_l4trg(i1, ad1, (k5 + 1) * k + (k5 + 1), ad2, k5 + 1, ai3);
                ai3[0] += k5 + 1;
                ai[k5 + 1] = ai3[0];
                double d4 = ad2[k5 + 1];
                ad2[k5 + 1] = ad2[ai3[0] - 1];
                ad2[ai3[0] - 1] = d4;
                d4 = ad1[(k5 + 1) * k + (ai3[0] - 1)];
                ad1[(k5 + 1) * k + (ai3[0] - 1)] = ad1[(k5 + 1) * k + (k5 + 1)];
                ad1[(k5 + 1) * k + (k5 + 1)] = d4;
                if (Math.abs(d4) > d1)
                {
                    d4 = -1D / d4;
                    if (ai3[0] != k5 + 2)
                    {
                        i6 = -i6;
                    }
                }
                else
                {
                    i6 = 0;
                    j5 = ai3[0];
                }
                double d10 = ad1[(k5 - 1) * k + (ai3[0] - 1)];
                ad1[(k5 - 1) * k + (ai3[0] - 1)] = ad1[(k5 - 1) * k + (k5 + 1)];
                ad1[(k5 - 1) * k + (k5 + 1)] = d10;
                d10 = ad1[k5 * k + (ai3[0] - 1)];
                ad1[k5 * k + (ai3[0] - 1)] = ad1[k5 * k + (k5 + 1)];
                ad1[k5 * k + (k5 + 1)] = d10;
                d10 = ad1[(k5 + 2) * k + (ai1[0] - 1)];
                ad1[(k5 + 2) * k + (ai1[0] - 1)] = ad1[(k5 + 2) * k + (k5 - 1)];
                ad1[(k5 + 2) * k + (k5 - 1)] = d10;
                double d13 = ad1[(k5 + 2) * k + (ai2[0] - 1)] + d10 * ad1[(k5 - 1) * k + k5];
                ad1[(k5 + 2) * k + (ai2[0] - 1)] = ad1[(k5 + 2) * k + k5];
                ad1[(k5 + 2) * k + k5] = d13;
                double d14 = ad1[(k5 + 2) * k + (ai3[0] - 1)] + d10 * ad1[(k5 - 1) * k + (k5 + 1)] + d13
                        * ad1[k5 * k + (k5 + 1)];
                ad1[(k5 + 2) * k + (ai3[0] - 1)] = ad1[(k5 + 2) * k + (k5 + 1)];
                ad1[(k5 + 2) * k + (k5 + 1)] = d14;
                for (int j3 = k5 + 3; j3 <= i; j3++)
                {
                    ad1[(k5 + 1) * k + (j3 - 1)] *= d4;
                    ad1[(k5 + 2) * k + (j3 - 1)] += d10 * ad1[(k5 - 1) * k + (j3 - 1)] + d13 * ad1[k5 * k + (j3 - 1)]
                            + d14 * ad1[(k5 + 1) * k + (j3 - 1)];
                }

                boolean flag4 = false;
                if (k5 + 3 >= i)
                {
                    flag4 = true;
                }
                if (!flag4)
                {
                    int j1 = i - k5 - 2;
                    l_l4trg(j1, ad1, (k5 + 2) * k + (k5 + 2), ad2, k5 + 2, ai4);
                    ai4[0] += k5 + 2;
                    ai[k5 + 2] = ai4[0];
                    double d5 = ad2[k5 + 2];
                    ad2[k5 + 2] = ad2[ai4[0] - 1];
                    ad2[ai4[0] - 1] = d5;
                    d5 = ad1[(k5 + 2) * k + (ai4[0] - 1)];
                    ad1[(k5 + 2) * k + (ai4[0] - 1)] = ad1[(k5 + 2) * k + (k5 + 2)];
                    ad1[(k5 + 2) * k + (k5 + 2)] = d5;
                    if (Math.abs(d5) > d1)
                    {
                        d5 = -1D / d5;
                        if (ai4[0] != k5 + 3)
                        {
                            i6 = -i6;
                        }
                    }
                    else
                    {
                        i6 = 0;
                        j5 = ai4[0];
                    }
                    double d11 = ad1[(k5 - 1) * k + (ai4[0] - 1)];
                    ad1[(k5 - 1) * k + (ai4[0] - 1)] = ad1[(k5 - 1) * k + (k5 + 2)];
                    ad1[(k5 - 1) * k + (k5 + 2)] = d11;
                    d11 = ad1[k5 * k + (ai4[0] - 1)];
                    ad1[k5 * k + (ai4[0] - 1)] = ad1[k5 * k + (k5 + 2)];
                    ad1[k5 * k + (k5 + 2)] = d11;
                    d11 = ad1[(k5 + 1) * k + (ai4[0] - 1)];
                    ad1[(k5 + 1) * k + (ai4[0] - 1)] = ad1[(k5 + 1) * k + (k5 + 2)];
                    ad1[(k5 + 1) * k + (k5 + 2)] = d11;
                    d11 = ad1[(k5 + 3) * k + (ai1[0] - 1)];
                    ad1[(k5 + 3) * k + (ai1[0] - 1)] = ad1[(k5 + 3) * k + (k5 - 1)];
                    ad1[(k5 + 3) * k + (k5 - 1)] = d11;
                    d13 = ad1[(k5 + 3) * k + (ai2[0] - 1)] + d11 * ad1[(k5 - 1) * k + k5];
                    ad1[(k5 + 3) * k + (ai2[0] - 1)] = ad1[(k5 + 3) * k + k5];
                    ad1[(k5 + 3) * k + k5] = d13;
                    double d15 = ad1[(k5 + 3) * k + (ai3[0] - 1)] + d11 * ad1[(k5 - 1) * k + (k5 + 1)] + d13
                            * ad1[k5 * k + (k5 + 1)];
                    ad1[(k5 + 3) * k + (ai3[0] - 1)] = ad1[(k5 + 3) * k + (k5 + 1)];
                    ad1[(k5 + 3) * k + (k5 + 1)] = d15;
                    double d21 = ad1[(k5 + 3) * k + (ai4[0] - 1)] + d11 * ad1[(k5 - 1) * k + (k5 + 2)] + d13
                            * ad1[k5 * k + (k5 + 2)] + d15 * ad1[(k5 + 1) * k + (k5 + 2)];
                    ad1[(k5 + 3) * k + (ai4[0] - 1)] = ad1[(k5 + 3) * k + (k5 + 2)];
                    ad1[(k5 + 3) * k + (k5 + 2)] = d21;
                    for (int k3 = k5 + 4; k3 <= i; k3++)
                    {
                        ad1[(k5 + 2) * k + (k3 - 1)] *= d5;
                        ad1[(k5 + 3) * k + (k3 - 1)] += d11 * ad1[(k5 - 1) * k + (k3 - 1)] + d13
                                * ad1[k5 * k + (k3 - 1)] + d15 * ad1[(k5 + 1) * k + (k3 - 1)] + d21
                                * ad1[(k5 + 2) * k + (k3 - 1)];
                    }

                    boolean flag3 = false;
                    if (k5 + 4 >= i)
                    {
                        flag3 = true;
                    }
                    if (!flag3)
                    {
                        int k1 = i - k5 - 3;
                        l_l4trg(k1, ad1, (k5 + 3) * k + (k5 + 3), ad2, k5 + 3, ai5);
                        ai5[0] += k5 + 3;
                        ai[k5 + 3] = ai5[0];
                        double d6 = ad2[k5 + 3];
                        ad2[k5 + 3] = ad2[ai5[0] - 1];
                        ad2[ai5[0] - 1] = d6;
                        d6 = ad1[(k5 + 3) * k + (ai5[0] - 1)];
                        ad1[(k5 + 3) * k + (ai5[0] - 1)] = ad1[(k5 + 3) * k + (k5 + 3)];
                        ad1[(k5 + 3) * k + (k5 + 3)] = d6;
                        if (Math.abs(d6) > d1)
                        {
                            d6 = -1D / d6;
                            if (ai5[0] != k5 + 4)
                            {
                                i6 = -i6;
                            }
                        }
                        else
                        {
                            i6 = 0;
                            j5 = ai5[0];
                        }
                        d11 = ad1[(k5 - 1) * k + (ai5[0] - 1)];
                        ad1[(k5 - 1) * k + (ai5[0] - 1)] = ad1[(k5 - 1) * k + (k5 + 3)];
                        ad1[(k5 - 1) * k + (k5 + 3)] = d11;
                        d11 = ad1[k5 * k + (ai5[0] - 1)];
                        ad1[k5 * k + (ai5[0] - 1)] = ad1[k5 * k + (k5 + 3)];
                        ad1[k5 * k + (k5 + 3)] = d11;
                        d11 = ad1[(k5 + 1) * k + (ai5[0] - 1)];
                        ad1[(k5 + 1) * k + (ai5[0] - 1)] = ad1[(k5 + 1) * k + (k5 + 3)];
                        ad1[(k5 + 1) * k + (k5 + 3)] = d11;
                        d11 = ad1[(k5 + 2) * k + (ai5[0] - 1)];
                        ad1[(k5 + 2) * k + (ai5[0] - 1)] = ad1[(k5 + 2) * k + (k5 + 3)];
                        ad1[(k5 + 2) * k + (k5 + 3)] = d11;
                        d11 = ad1[(k5 + 4) * k + (ai1[0] - 1)];
                        ad1[(k5 + 4) * k + (ai1[0] - 1)] = ad1[(k5 + 4) * k + (k5 - 1)];
                        ad1[(k5 + 4) * k + (k5 - 1)] = d11;
                        d13 = ad1[(k5 + 4) * k + (ai2[0] - 1)] + d11 * ad1[(k5 - 1) * k + k5];
                        ad1[(k5 + 4) * k + (ai2[0] - 1)] = ad1[(k5 + 4) * k + k5];
                        ad1[(k5 + 4) * k + k5] = d13;
                        double d16 = ad1[(k5 + 4) * k + (ai3[0] - 1)] + d11 * ad1[(k5 - 1) * k + (k5 + 1)] + d13
                                * ad1[k5 * k + (k5 + 1)];
                        ad1[(k5 + 4) * k + (ai3[0] - 1)] = ad1[(k5 + 4) * k + (k5 + 1)];
                        ad1[(k5 + 4) * k + (k5 + 1)] = d16;
                        double d22 = ad1[(k5 + 4) * k + (ai4[0] - 1)] + d11 * ad1[(k5 - 1) * k + (k5 + 2)] + d13
                                * ad1[k5 * k + (k5 + 2)] + d16 * ad1[(k5 + 1) * k + (k5 + 2)];
                        ad1[(k5 + 4) * k + (ai4[0] - 1)] = ad1[(k5 + 4) * k + (k5 + 2)];
                        ad1[(k5 + 4) * k + (k5 + 2)] = d22;
                        double d26 = ad1[(k5 + 4) * k + (ai5[0] - 1)] + d11 * ad1[(k5 - 1) * k + (k5 + 3)] + d13
                                * ad1[k5 * k + (k5 + 3)] + d16 * ad1[(k5 + 1) * k + (k5 + 3)] + d22
                                * ad1[(k5 + 2) * k + (k5 + 3)];
                        ad1[(k5 + 4) * k + (ai5[0] - 1)] = ad1[(k5 + 4) * k + (k5 + 3)];
                        ad1[(k5 + 4) * k + (k5 + 3)] = d26;
                        for (int l3 = k5 + 5; l3 <= i; l3++)
                        {
                            ad1[(k5 + 3) * k + (l3 - 1)] *= d6;
                            ad1[(k5 + 4) * k + (l3 - 1)] += d11 * ad1[(k5 - 1) * k + (l3 - 1)] + d13
                                    * ad1[k5 * k + (l3 - 1)] + d16 * ad1[(k5 + 1) * k + (l3 - 1)] + d22
                                    * ad1[(k5 + 2) * k + (l3 - 1)] + d26 * ad1[(k5 + 3) * k + (l3 - 1)];
                        }

                        boolean flag2 = false;
                        if (k5 + 5 >= i)
                        {
                            flag2 = true;
                        }
                        if (!flag2)
                        {
                            int l1 = i - k5 - 4;
                            l_l4trg(l1, ad1, (k5 + 4) * k + (k5 + 4), ad2, k5 + 4, ai6);
                            ai6[0] += k5 + 4;
                            ai[k5 + 4] = ai6[0];
                            d6 = ad2[k5 + 4];
                            ad2[k5 + 4] = ad2[ai6[0] - 1];
                            ad2[ai6[0] - 1] = d6;
                            d6 = ad1[(k5 + 4) * k + (ai6[0] - 1)];
                            ad1[(k5 + 4) * k + (ai6[0] - 1)] = ad1[(k5 + 4) * k + (k5 + 4)];
                            ad1[(k5 + 4) * k + (k5 + 4)] = d6;
                            if (Math.abs(d6) > d1)
                            {
                                d6 = -1D / d6;
                                if (ai6[0] != k5 + 5)
                                {
                                    i6 = -i6;
                                }
                            }
                            else
                            {
                                i6 = 0;
                                j5 = ai6[0];
                            }
                            d11 = ad1[(k5 - 1) * k + (ai6[0] - 1)];
                            ad1[(k5 - 1) * k + (ai6[0] - 1)] = ad1[(k5 - 1) * k + (k5 + 4)];
                            ad1[(k5 - 1) * k + (k5 + 4)] = d11;
                            d11 = ad1[k5 * k + (ai6[0] - 1)];
                            ad1[k5 * k + (ai6[0] - 1)] = ad1[k5 * k + (k5 + 4)];
                            ad1[k5 * k + (k5 + 4)] = d11;
                            d11 = ad1[(k5 + 1) * k + (ai6[0] - 1)];
                            ad1[(k5 + 1) * k + (ai6[0] - 1)] = ad1[(k5 + 1) * k + (k5 + 4)];
                            ad1[(k5 + 1) * k + (k5 + 4)] = d11;
                            d11 = ad1[(k5 + 2) * k + (ai6[0] - 1)];
                            ad1[(k5 + 2) * k + (ai6[0] - 1)] = ad1[(k5 + 2) * k + (k5 + 4)];
                            ad1[(k5 + 2) * k + (k5 + 4)] = d11;
                            d11 = ad1[(k5 + 3) * k + (ai6[0] - 1)];
                            ad1[(k5 + 3) * k + (ai6[0] - 1)] = ad1[(k5 + 3) * k + (k5 + 4)];
                            ad1[(k5 + 3) * k + (k5 + 4)] = d11;
                            d11 = ad1[(k5 + 5) * k + (ai1[0] - 1)];
                            ad1[k5 + 5 + k + (ai1[0] - 1)] = ad1[(k5 + 5) * k + (k5 - 1)];
                            ad1[(k5 + 5) * k + (k5 - 1)] = d11;
                            d13 = ad1[(k5 + 5) * k + (ai2[0] - 1)] + d11 * ad1[(k5 - 1) * k + k5];
                            ad1[(k5 + 5) * k + (ai2[0] - 1)] = ad1[(k5 + 5) * k + k5];
                            ad1[(k5 + 5) * k + k5] = d13;
                            double d17 = ad1[(k5 + 5) * k + (ai3[0] - 1)] + d11 * ad1[(k5 - 1) * k + (k5 + 1)] + d13
                                    * ad1[k5 * k + (k5 + 1)];
                            ad1[(k5 + 5) * k + (ai3[0] - 1)] = ad1[(k5 + 5) * k + (k5 + 1)];
                            ad1[(k5 + 5) * k + (k5 + 1)] = d17;
                            double d23 = ad1[(k5 + 5) * k + (ai4[0] - 1)] + d11 * ad1[(k5 - 1) * k + (k5 + 2)] + d13
                                    * ad1[k5 * k + (k5 + 2)] + d17 * ad1[(k5 + 1) * k + (k5 + 2)];
                            ad1[(k5 + 5) * k + (ai4[0] - 1)] = ad1[(k5 + 5) * k + (k5 + 2)];
                            ad1[(k5 + 5) * k + (k5 + 2)] = d23;
                            double d27 = ad1[(k5 + 5) * k + (ai5[0] - 1)] + d11 * ad1[(k5 - 1) * k + (k5 + 3)] + d13
                                    * ad1[k5 * k + (k5 + 3)] + d17 * ad1[(k5 + 1) * k + (k5 + 3)] + d23
                                    * ad1[(k5 + 2) * k + (k5 + 3)];
                            ad1[(k5 + 5) * k + (ai5[0] - 1)] = ad1[(k5 + 5) * k + (k5 + 3)];
                            ad1[(k5 + 5) * k + (k5 + 3)] = d27;
                            double d30 = ad1[(k5 + 5) * k + (ai6[0] - 1)] + d11 * ad1[(k5 - 1) * k + (k5 + 4)] + d13
                                    * ad1[k5 * k + (k5 + 4)] + d17 * ad1[(k5 + 1) * k + (k5 + 4)] + d23
                                    * ad1[(k5 + 2) * k + (k5 + 4)] + d27 * ad1[(k5 + 3) * k + (k5 + 4)];
                            ad1[(k5 + 5) * k + (ai6[0] - 1)] = ad1[(k5 + 5) * k + (k5 + 4)];
                            ad1[(k5 + 5) * k + (k5 + 4)] = d30;
                            for (int i4 = k5 + 6; i4 <= i; i4++)
                            {
                                ad1[(k5 + 4) * k + (i4 - 1)] *= d6;
                                ad1[(k5 + 5) * k + (i4 - 1)] += d11 * ad1[(k5 - 1) * k + (i4 - 1)] + d13
                                        * ad1[k5 * k + (i4 - 1)] + d17 * ad1[(k5 + 1) * k + (i4 - 1)] + d23
                                        * ad1[(k5 + 2) * k + (i4 - 1)] + d27 * ad1[(k5 + 3) * k + (i4 - 1)] + d30
                                        * ad1[(k5 + 4) * k + (i4 - 1)];
                            }

                            boolean flag1 = false;
                            if (k5 + 6 >= i)
                            {
                                flag1 = true;
                            }
                            if (!flag1)
                            {
                                int i2 = i - k5 - 5;
                                l_l4trg(i2, ad1, (k5 + 5) * k + (k5 + 5), ad2, k5 + 5, ai7);
                                ai7[0] += k5 + 5;
                                ai[k5 + 5] = ai7[0];
                                d6 = ad2[k5 + 5];
                                ad2[k5 + 5] = ad2[ai7[0] - 1];
                                ad2[ai7[0] - 1] = d6;
                                d6 = ad1[(k5 + 5) * k + (ai7[0] - 1)];
                                ad1[(k5 + 5) * k + (ai7[0] - 1)] = ad1[(k5 + 5) * k + (k5 + 5)];
                                ad1[(k5 + 5) * k + (k5 + 5)] = d6;
                                if (Math.abs(d6) > d1)
                                {
                                    d6 = -1D / d6;
                                    if (ai7[0] != k5 + 6)
                                    {
                                        i6 = -i6;
                                    }
                                }
                                else
                                {
                                    i6 = 0;
                                    j5 = ai7[0];
                                }
                                d11 = ad1[(k5 - 1) * k + (ai7[0] - 1)];
                                ad1[(k5 - 1) * k + (ai7[0] - 1)] = ad1[(k5 - 1) * k + (k5 + 5)];
                                ad1[(k5 - 1) * k + (k5 + 5)] = d11;
                                d11 = ad1[k5 * k + (ai7[0] - 1)];
                                ad1[k5 * k + (ai7[0] - 1)] = ad1[k5 * k + (k5 + 5)];
                                ad1[k5 * k + (k5 + 5)] = d11;
                                d11 = ad1[(k5 + 1) * k + (ai7[0] - 1)];
                                ad1[(k5 + 1) * k + (ai7[0] - 1)] = ad1[(k5 + 1) * k + (k5 + 5)];
                                ad1[(k5 + 1) * k + (k5 + 5)] = d11;
                                d11 = ad1[(k5 + 2) * k + (ai7[0] - 1)];
                                ad1[(k5 + 2) * k + (ai7[0] - 1)] = ad1[(k5 + 2) * k + (k5 + 5)];
                                ad1[(k5 + 2) * k + (k5 + 5)] = d11;
                                d11 = ad1[(k5 + 3) * k + (ai7[0] - 1)];
                                ad1[(k5 + 3) * k + (ai7[0] - 1)] = ad1[(k5 + 3) * k + (k5 + 5)];
                                ad1[(k5 + 3) * k + (k5 + 5)] = d11;
                                d11 = ad1[(k5 + 4) * k + (ai7[0] - 1)];
                                ad1[(k5 + 4) * k + (ai7[0] - 1)] = ad1[(k5 + 4) * k + (k5 + 5)];
                                ad1[(k5 + 4) * k + (k5 + 5)] = d11;
                                d11 = ad1[(k5 + 6) * k + (ai1[0] - 1)];
                                ad1[(k5 + 6) * k + (ai1[0] - 1)] = ad1[(k5 + 6) * k + (k5 - 1)];
                                ad1[(k5 + 6) * k + (k5 - 1)] = d11;
                                d13 = ad1[(k5 + 6) * k + (ai2[0] - 1)] + d11 * ad1[(k5 - 1) * k + k5];
                                ad1[(k5 + 6) * k + (ai2[0] - 1)] = ad1[(k5 + 6) * k + k5];
                                ad1[(k5 + 6) * k + k5] = d13;
                                double d18 = ad1[(k5 + 6) * k + (ai3[0] - 1)] + d11 * ad1[(k5 - 1) * k + (k5 + 1)]
                                        + d13 * ad1[k5 * k + (k5 + 1)];
                                ad1[(k5 + 6) * k + (ai3[0] - 1)] = ad1[(k5 + 6) * k + (k5 + 1)];
                                ad1[(k5 + 6) * k + (k5 + 1)] = d18;
                                double d24 = ad1[(k5 + 6) * k + (ai4[0] - 1)] + d11 * ad1[(k5 - 1) * k + (k5 + 2)]
                                        + d13 * ad1[k5 * k + (k5 + 2)] + d18 * ad1[(k5 + 1) * k + (k5 + 2)];
                                ad1[(k5 + 6) * k + (ai4[0] - 1)] = ad1[(k5 + 6) * k + (k5 + 2)];
                                ad1[(k5 + 6) * k + (k5 + 2)] = d24;
                                double d28 = ad1[(k5 + 6) * k + (ai5[0] - 1)] + d11 * ad1[(k5 - 1) * k + (k5 + 3)]
                                        + d13 * ad1[k5 * k + (k5 + 3)] + d18 * ad1[(k5 + 1) * k + (k5 + 3)] + d24
                                        * ad1[(k5 + 2) * k + (k5 + 3)];
                                ad1[(k5 + 6) * k + (ai5[0] - 1)] = ad1[(k5 + 6) * k + (k5 + 3)];
                                ad1[(k5 + 6) * k + (k5 + 3)] = d28;
                                double d31 = ad1[(k5 + 6) * k + (ai6[0] - 1)] + d11 * ad1[(k5 - 1) * k + (k5 + 4)]
                                        + d13 * ad1[k5 * k + (k5 + 4)] + d18 * ad1[(k5 + 1) * k + (k5 + 4)] + d24
                                        * ad1[(k5 + 2) * k + (k5 + 4)] + d28 * ad1[(k5 + 3) * k + (k5 + 4)];
                                ad1[(k5 + 6) * k + (ai6[0] - 1)] = ad1[(k5 + 6) * k + (k5 + 4)];
                                ad1[(k5 + 6) * k + (k5 + 4)] = d31;
                                double d33 = ad1[(k5 + 6) * k + (ai7[0] - 1)] + d11 * ad1[(k5 - 1) * k + (k5 + 5)]
                                        + d13 * ad1[k5 * k + (k5 + 5)] + d18 * ad1[(k5 + 1) * k + (k5 + 5)] + d24
                                        * ad1[(k5 + 2) * k + (k5 + 5)] + d28 * ad1[(k5 + 3) * k + (k5 + 5)] + d31
                                        * ad1[(k5 + 4) * k + (k5 + 5)];
                                ad1[(k5 + 6) * k + (ai7[0] - 1)] = ad1[(k5 + 6) * k + (k5 + 5)];
                                ad1[(k5 + 6) * k + (k5 + 5)] = d33;
                                for (int j4 = k5 + 7; j4 <= i; j4++)
                                {
                                    ad1[(k5 + 5) * k + (j4 - 1)] *= d6;
                                    ad1[(k5 + 6) * k + (j4 - 1)] += d11 * ad1[(k5 - 1) * k + (j4 - 1)] + d13
                                            * ad1[k5 * k + (j4 - 1)] + d18 * ad1[(k5 + 1) * k + (j4 - 1)] + d24
                                            * ad1[(k5 + 2) * k + (j4 - 1)] + d28 * ad1[(k5 + 3) * k + (j4 - 1)] + d31
                                            * ad1[(k5 + 4) * k + (j4 - 1)] + d33 * ad1[(k5 + 5) * k + (j4 - 1)];
                                }

                                boolean flag = false;
                                if (k5 + 7 >= i)
                                {
                                    flag = true;
                                }
                                if (!flag)
                                {
                                    int j2 = i - k5 - 6;
                                    l_l4trg(j2, ad1, (k5 + 6) * k + (k5 + 6), ad2, k5 + 6, ai8);
                                    ai8[0] += k5 + 6;
                                    ai[k5 + 6] = ai8[0];
                                    d6 = ad2[k5 + 6];
                                    ad2[k5 + 6] = ad2[ai8[0] - 1];
                                    ad2[ai8[0] - 1] = d6;
                                    d6 = ad1[(k5 - 1) * k + (ai8[0] - 1)];
                                    ad1[(k5 - 1) * k + (ai8[0] - 1)] = ad1[(k5 - 1) * k + (k5 + 6)];
                                    ad1[(k5 - 1) * k + (k5 + 6)] = d6;
                                    d6 = ad1[k5 * k + (ai8[0] - 1)];
                                    ad1[k5 * k + (ai8[0] - 1)] = ad1[k5 * k + (k5 + 6)];
                                    ad1[k5 * k + (k5 + 6)] = d6;
                                    d6 = ad1[(k5 + 1) * k + (ai8[0] - 1)];
                                    ad1[(k5 + 1) * k + (ai8[0] - 1)] = ad1[(k5 + 1) * k + (k5 + 6)];
                                    ad1[(k5 + 1) * k + (k5 + 6)] = d6;
                                    d6 = ad1[(k5 + 2) * k + (ai8[0] - 1)];
                                    ad1[(k5 + 2) * k + (ai8[0] - 1)] = ad1[(k5 + 2) * k + (k5 + 6)];
                                    ad1[(k5 + 2) * k + (k5 + 6)] = d6;
                                    d6 = ad1[(k5 + 3) * k + (ai8[0] - 1)];
                                    ad1[(k5 + 3) * k + (ai8[0] - 1)] = ad1[(k5 + 3) * k + (k5 + 6)];
                                    ad1[(k5 + 3) * k + (k5 + 6)] = d6;
                                    d6 = ad1[(k5 + 4) * k + (ai8[0] - 1)];
                                    ad1[(k5 + 4) * k + (ai8[0] - 1)] = ad1[(k5 + 4) * k + (k5 + 6)];
                                    ad1[(k5 + 4) * k + (k5 + 6)] = d6;
                                    d6 = ad1[(k5 + 5) * k + (ai8[0] - 1)];
                                    ad1[(k5 + 5) * k + (ai8[0] - 1)] = ad1[(k5 + 5) * k + (k5 + 6)];
                                    ad1[(k5 + 5) * k + (k5 + 6)] = d6;
                                    d6 = ad1[(k5 + 6) * k + (ai8[0] - 1)];
                                    ad1[(k5 + 6) * k + (ai8[0] - 1)] = ad1[(k5 + 6) * k + (k5 + 6)];
                                    ad1[(k5 + 6) * k + (k5 + 6)] = d6;
                                    if (Math.abs(d6) > d1)
                                    {
                                        d6 = -1D / d6;
                                        for (int k4 = k5 + 8; k4 <= i; k4++)
                                        {
                                            ad1[(k5 + 6) * k + (k4 - 1)] *= d6;
                                        }

                                        if (ai8[0] != k5 + 7)
                                        {
                                            i6 = -i6;
                                        }
                                    }
                                    else
                                    {
                                        i6 = 0;
                                        j5 = ai8[0];
                                    }
                                    for (int l5 = i; l5 >= k5 + 8; l5--)
                                    {
                                        d11 = ad1[(l5 - 1) * k + (ai1[0] - 1)];
                                        ad1[(l5 - 1) * k + (ai1[0] - 1)] = ad1[(l5 - 1) * k + (k5 - 1)];
                                        ad1[(l5 - 1) * k + (k5 - 1)] = d11;
                                        d13 = ad1[(l5 - 1) * k + (ai2[0] - 1)] + d11 * ad1[(k5 - 1) * k + k5];
                                        ad1[(l5 - 1) * k + (ai2[0] - 1)] = ad1[(l5 - 1) * k + k5];
                                        ad1[(l5 - 1) * k + k5] = d13;
                                        double d19 = ad1[(l5 - 1) * k + (ai3[0] - 1)] + d11
                                                * ad1[(k5 - 1) * k + (k5 + 1)] + d13 * ad1[k5 * k + (k5 + 1)];
                                        ad1[(l5 - 1) * k + (ai3[0] - 1)] = ad1[(l5 - 1) * k + (k5 + 1)];
                                        ad1[(l5 - 1) * k + (k5 + 1)] = d19;
                                        double d25 = ad1[(l5 - 1) * k + (ai4[0] - 1)] + d11
                                                * ad1[(k5 - 1) * k + (k5 + 2)] + d13 * ad1[k5 * k + (k5 + 2)] + d19
                                                * ad1[(k5 + 1) * k + (k5 + 2)];
                                        ad1[(l5 - 1) * k + (ai4[0] - 1)] = ad1[(l5 - 1) * k + (k5 + 2)];
                                        ad1[(l5 - 1) * k + (k5 + 2)] = d25;
                                        double d29 = ad1[(l5 - 1) * k + (ai5[0] - 1)] + d11
                                                * ad1[(k5 - 1) * k + (k5 + 3)] + d13 * ad1[k5 * k + (k5 + 3)] + d19
                                                * ad1[(k5 + 1) * k + (k5 + 3)] + d25 * ad1[(k5 + 2) * k + (k5 + 3)];
                                        ad1[(l5 - 1) * k + (ai5[0] - 1)] = ad1[(l5 - 1) * k + (k5 + 3)];
                                        ad1[(l5 - 1) * k + (k5 + 3)] = d29;
                                        double d32 = ad1[(l5 - 1) * k + (ai6[0] - 1)] + d11
                                                * ad1[(k5 - 1) * k + (k5 + 4)] + d13 * ad1[k5 * k + (k5 + 4)] + d19
                                                * ad1[(k5 + 1) * k + (k5 + 4)] + d25 * ad1[(k5 + 2) * k + (k5 + 4)]
                                                + d29 * ad1[(k5 + 3) * k + (k5 + 4)];
                                        ad1[(l5 - 1) * k + (ai6[0] - 1)] = ad1[(l5 - 1) * k + (k5 + 4)];
                                        ad1[(l5 - 1) * k + (k5 + 4)] = d32;
                                        double d34 = ad1[(l5 - 1) * k + (ai7[0] - 1)] + d11
                                                * ad1[(k5 - 1) * k + (k5 + 5)] + d13 * ad1[k5 * k + (k5 + 5)] + d19
                                                * ad1[(k5 + 1) * k + (k5 + 5)] + d25 * ad1[(k5 + 2) * k + (k5 + 5)]
                                                + d29 * ad1[(k5 + 3) * k + (k5 + 5)] + d32
                                                * ad1[(k5 + 4) * k + (k5 + 5)];
                                        ad1[(l5 - 1) * k + (ai7[0] - 1)] = ad1[(l5 - 1) * k + (k5 + 5)];
                                        ad1[(l5 - 1) * k + (k5 + 5)] = d34;
                                        double d35 = ad1[(l5 - 1) * k + (ai8[0] - 1)] + d11
                                                * ad1[(k5 - 1) * k + (k5 + 6)] + d13 * ad1[k5 * k + (k5 + 6)] + d19
                                                * ad1[(k5 + 1) * k + (k5 + 6)] + d25 * ad1[(k5 + 2) * k + (k5 + 6)]
                                                + d29 * ad1[(k5 + 3) * k + (k5 + 6)] + d32
                                                * ad1[(k5 + 4) * k + (k5 + 6)] + d34 * ad1[(k5 + 5) * k + (k5 + 6)];
                                        ad1[(l5 - 1) * k + (ai8[0] - 1)] = ad1[(l5 - 1) * k + (k5 + 6)];
                                        ad1[(l5 - 1) * k + (k5 + 6)] = d35;
                                        for (int l4 = k5 + 8; l4 <= i; l4++)
                                        {
                                            ad1[(l5 - 1) * k + (l4 - 1)] += d11 * ad1[(k5 - 1) * k + (l4 - 1)] + d13
                                                    * ad1[k5 * k + (l4 - 1)] + d19 * ad1[(k5 + 1) * k + (l4 - 1)] + d25
                                                    * ad1[(k5 + 2) * k + (l4 - 1)] + d29 * ad1[(k5 + 3) * k + (l4 - 1)]
                                                    + d32 * ad1[(k5 + 4) * k + (l4 - 1)] + d34
                                                    * ad1[(k5 + 5) * k + (l4 - 1)] + d35 * ad1[(k5 + 6) * k + (l4 - 1)];
                                        }

                                    }

                                    double d36 = ad1[(k5 - 1) * k + (ai8[0] - 1)];
                                    ad1[(k5 - 1) * k + (ai8[0] - 1)] = ad1[(k5 - 1) * k + (k5 + 6)];
                                    ad1[(k5 - 1) * k + (k5 + 6)] = d36;
                                    d36 = ad1[k5 * k + (ai8[0] - 1)];
                                    ad1[k5 * k + (ai8[0] - 1)] = ad1[k5 * k + (k5 + 6)];
                                    ad1[k5 * k + (k5 + 6)] = d36;
                                    d36 = ad1[(k5 + 1) * k + (ai8[0] - 1)];
                                    ad1[(k5 + 1) * k + (ai8[0] - 1)] = ad1[(k5 + 1) * k + (k5 + 6)];
                                    ad1[(k5 + 1) * k + (k5 + 6)] = d36;
                                    d36 = ad1[(k5 + 2) * k + (ai8[0] - 1)];
                                    ad1[(k5 + 2) * k + (ai8[0] - 1)] = ad1[(k5 + 2) * k + (k5 + 6)];
                                    ad1[(k5 + 2) * k + (k5 + 6)] = d36;
                                    d36 = ad1[(k5 + 3) * k + (ai8[0] - 1)];
                                    ad1[(k5 + 3) * k + (ai8[0] - 1)] = ad1[(k5 + 3) * k + (k5 + 6)];
                                    ad1[(k5 + 3) * k + (k5 + 6)] = d36;
                                    d36 = ad1[(k5 + 4) * k + (ai8[0] - 1)];
                                    ad1[(k5 + 4) * k + (ai8[0] - 1)] = ad1[(k5 + 4) * k + (k5 + 6)];
                                    ad1[(k5 + 4) * k + (k5 + 6)] = d36;
                                    d36 = ad1[(k5 + 5) * k + (ai8[0] - 1)];
                                    ad1[(k5 + 5) * k + (ai8[0] - 1)] = ad1[(k5 + 5) * k + (k5 + 6)];
                                    ad1[(k5 + 5) * k + (k5 + 6)] = d36;
                                }
                                double d37 = ad1[(k5 - 1) * k + (ai7[0] - 1)];
                                ad1[(k5 - 1) * k + (ai7[0] - 1)] = ad1[(k5 - 1) * k + (k5 + 5)];
                                ad1[(k5 - 1) * k + (k5 + 5)] = d37;
                                d37 = ad1[k5 * k + (ai7[0] - 1)];
                                ad1[k5 * k + (ai7[0] - 1)] = ad1[k5 * k + (k5 + 5)];
                                ad1[k5 * k + (k5 + 5)] = d37;
                                d37 = ad1[(k5 + 1) * k + (ai7[0] - 1)];
                                ad1[(k5 + 1) * k + (ai7[0] - 1)] = ad1[(k5 + 1) * k + (k5 + 5)];
                                ad1[(k5 + 1) * k + (k5 + 5)] = d37;
                                d37 = ad1[(k5 + 2) * k + (ai7[0] - 1)];
                                ad1[(k5 + 2) * k + (ai7[0] - 1)] = ad1[(k5 + 2) * k + (k5 + 5)];
                                ad1[(k5 + 2) * k + (k5 + 5)] = d37;
                                d37 = ad1[(k5 + 3) * k + (ai7[0] - 1)];
                                ad1[(k5 + 3) * k + (ai7[0] - 1)] = ad1[(k5 + 3) * k + (k5 + 5)];
                                ad1[(k5 + 3) * k + (k5 + 5)] = d37;
                                d37 = ad1[(k5 + 4) * k + (ai7[0] - 1)];
                                ad1[(k5 + 4) * k + (ai7[0] - 1)] = ad1[(k5 + 4) * k + (k5 + 5)];
                                ad1[(k5 + 4) * k + (k5 + 5)] = d37;
                            }
                            double d7 = ad1[(k5 - 1) * k + (ai6[0] - 1)];
                            ad1[(k5 - 1) * k + (ai6[0] - 1)] = ad1[(k5 - 1) * k + (k5 + 4)];
                            ad1[(k5 - 1) * k + (k5 + 4)] = d7;
                            d7 = ad1[k5 * k + (ai6[0] - 1)];
                            ad1[k5 * k + (ai6[0] - 1)] = ad1[k5 * k + (k5 + 4)];
                            ad1[k5 * k + (k5 + 4)] = d7;
                            d7 = ad1[(k5 + 1) * k + (ai6[0] - 1)];
                            ad1[(k5 + 1) * k + (ai6[0] - 1)] = ad1[(k5 + 1) * k + (k5 + 4)];
                            ad1[(k5 + 1) * k + (k5 + 4)] = d7;
                            d7 = ad1[(k5 + 2) * k + (ai6[0] - 1)];
                            ad1[(k5 + 2) * k + (ai6[0] - 1)] = ad1[(k5 + 2) * k + (k5 + 4)];
                            ad1[(k5 + 2) * k + (k5 + 4)] = d7;
                            d7 = ad1[(k5 + 3) * k + (ai6[0] - 1)];
                            ad1[(k5 + 3) * k + (ai6[0] - 1)] = ad1[(k5 + 3) * k + (k5 + 4)];
                            ad1[(k5 + 3) * k + (k5 + 4)] = d7;
                        }
                        d6 = ad1[(k5 - 1) * k + (ai5[0] - 1)];
                        ad1[(k5 - 1) * k + (ai5[0] - 1)] = ad1[(k5 - 1) * k + (k5 + 3)];
                        ad1[(k5 - 1) * k + (k5 + 3)] = d6;
                        d6 = ad1[k5 * k + (ai5[0] - 1)];
                        ad1[k5 * k + (ai5[0] - 1)] = ad1[k5 * k + (k5 + 3)];
                        ad1[k5 * k + (k5 + 3)] = d6;
                        d6 = ad1[(k5 + 1) * k + (ai5[0] - 1)];
                        ad1[(k5 + 1) * k + (ai5[0] - 1)] = ad1[(k5 + 1) * k + (k5 + 3)];
                        ad1[(k5 + 1) * k + (k5 + 3)] = d6;
                        d6 = ad1[(k5 + 2) * k + (ai5[0] - 1)];
                        ad1[(k5 + 2) * k + (ai5[0] - 1)] = ad1[(k5 + 2) * k + (k5 + 3)];
                        ad1[(k5 + 2) * k + (k5 + 3)] = d6;
                    }
                    d11 = ad1[(k5 - 1) * k + (ai4[0] - 1)];
                    ad1[(k5 - 1) * k + (ai4[0] - 1)] = ad1[(k5 - 1) * k + (k5 + 2)];
                    ad1[(k5 - 1) * k + (k5 + 2)] = d11;
                    d11 = ad1[k5 * k + (ai4[0] - 1)];
                    ad1[k5 * k + (ai4[0] - 1)] = ad1[k5 * k + (k5 + 2)];
                    ad1[k5 * k + (k5 + 2)] = d11;
                    d11 = ad1[(k5 + 1) * k + (ai4[0] - 1)];
                    ad1[(k5 + 1) * k + (ai4[0] - 1)] = ad1[(k5 + 1) * k + (k5 + 2)];
                    ad1[(k5 + 1) * k + (k5 + 2)] = d11;
                }
                d13 = ad1[(k5 - 1) * k + (ai3[0] - 1)];
                ad1[(k5 - 1) * k + (ai3[0] - 1)] = ad1[(k5 - 1) * k + (k5 + 1)];
                ad1[(k5 - 1) * k + (k5 + 1)] = d13;
                d13 = ad1[k5 * k + (ai3[0] - 1)];
                ad1[k5 * k + (ai3[0] - 1)] = ad1[k5 * k + (k5 + 1)];
                ad1[k5 * k + (k5 + 1)] = d13;
            }
            double d20 = ad1[(k5 - 1) * k + (ai2[0] - 1)];
            ad1[(k5 - 1) * k + (ai2[0] - 1)] = ad1[(k5 - 1) * k + k5];
            ad1[(k5 - 1) * k + k5] = d20;
        }

        ai[i - 1] = i;
        if (Math.abs(ad1[(i - 1) * k + (i - 1)]) <= d1)
        {
            j5 = i;
        }
        if (j5 != 0) // throw new MatrixSingularException("ARMA.MatrixSingular",
                     // null);
        {
            throw new IllegalArgumentException("ARMA.MatrixSingular");
        }
        else
        {
            return;
        }
    }

    private void l_l4trg(int i, double ad[], int j, double ad1[], int k, int ai[])
    {
        double ad2[] = new double[400];
        ai[0] = 1;
        double d = 0.0D;
        for (int i1 = 1; i1 <= i; i1 += 400)
        {
            int j1 = Math.min((i1 + 400) - 1, i);
            for (int l = i1; l <= j1; l++)
            {
                ad2[l - i1] = ad[(j + l) - 1] * ad1[(k + l) - 1];
            }

            int k1 = BLAS.iamax((j1 - i1) + 1, ad2, 0, 1);
            double d1 = Math.abs(ad2[k1 - 1]);
            if (d1 > d)
            {
                d = d1;
                ai[0] = (k1 + i1) - 1;
            }
        }

    }

    private void l_girts(int i, double ad[], int j, int k, double ad1[], int l, int i1, int ai[], double ad2[], int j1,
            double ad3[], int k1)
    {
        int ai1[] =
        {
            0
        };
        ai1[0] = 1;
        l_c1dim(1, i, "n", j, "ldr", ai1);
        ai1[0]++;
        ai1[0]++;
        l_c1r(i, ad, j, ai1);
        ai[0] = 0;
        for (int l1 = 1; l1 <= i; l1++)
        {
            if (ad[(l1 + j * (l1 - 1)) - 1] != 0.0D)
            {
                ai[0]++;
            }
        }

        for (int j3 = 1; j3 <= k; j3++)
        {
            System.arraycopy(ad1, l * (j3 - 1), ad2, j1 * (j3 - 1), i);
        }

        if (i1 == 1 || i1 == 3)
        {
            if (ai[0] < i)
            {
                for (int i2 = 1; i2 <= k; i2++)
                {
                    for (int k3 = i; k3 >= 1; k3--)
                    {
                        if (ad[(k3 + j * (k3 - 1)) - 1] == 0.0D)
                        {
                            if (ad2[(k3 + j1 * (i2 - 1)) - 1] != 0.0D)
                            {
                                errorCode = 11058;
                                Object aobj[] =
                                {
                                        new Integer(k3), new Integer(i2), new Double(ad2[(k3 + j1 * (i2 - 1)) - 1])
                                };
                                // Warning.print(this, "com.imsl.stat",
                                // "KalmanFilter.ToleranceInconsistent", aobj);
                                System.out.println("KalmanFilter.ToleranceInconsistent");
                            }
                            ad2[(k3 + j1 * (i2 - 1)) - 1] = 0.0D;
                        }
                        else
                        {
                            ad2[(k3 + j1 * (i2 - 1)) - 1] /= ad[(k3 + j * (k3 - 1)) - 1];
                            double d = -ad2[(k3 + j1 * (i2 - 1)) - 1];
                            BLAS.axpy(k3 - 1, d, ad, j * (k3 - 1), 1, ad2, j1 * (i2 - 1), 1);
                        }
                    }

                }

            }
            else
            {
                for (int j2 = 1; j2 <= k; j2++)
                {
                    BLAS.trsv('U', 'N', 'N', i, ad, 0, j, ad2, j1 * (j2 - 1), 1);
                }

            }
        }
        else if (i1 == 2 || i1 == 4)
        {
            if (ai[0] < i)
            {
                for (int k2 = 1; k2 <= k; k2++)
                {
                    for (int l3 = 1; l3 <= i; l3++)
                    {
                        double d1 = ad2[(l3 + j1 * (k2 - 1)) - 1]
                                - BLAS.dot(l3 - 1, ad, j * (l3 - 1), 1, ad2, j1 * (k2 - 1), 1);
                        if (ad[(l3 + j * (l3 - 1)) - 1] == 0.0D)
                        {
                            double d3 = Math.abs(ad2[(l3 + j1 * (k2 - 1)) - 1])
                                    + l_a1ot(l3 - 1, ad, j * (l3 - 1), 1, ad2, j1 * (k2 - 1), 1);
                            d3 *= 4.4408920985006262E-014D;
                            if (Math.abs(d1) > d3)
                            {
                                errorCode = 11059;
                                Object aobj1[] =
                                {
                                        new Integer(l3), new Integer(k2)
                                };
                                // Warning.print(this, "com.imsl.stat",
                                // "KalmanFilter.ToleranceInconsistent", aobj1);
                                System.out.println("KalmanFilter.ToleranceInconsistent");
                            }
                            ad2[(l3 + j1 * (k2 - 1)) - 1] = 0.0D;
                        }
                        else
                        {
                            ad2[(l3 + j1 * (k2 - 1)) - 1] = d1 / ad[(l3 + j * (l3 - 1)) - 1];
                        }
                    }

                }

            }
            else
            {
                for (int l2 = 1; l2 <= k; l2++)
                {
                    BLAS.trsv('U', 'T', 'N', i, ad, 0, j, ad2, j1 * (l2 - 1), 1);
                }

            }
        }
        if (i1 == 3 || i1 == 4)
        {
            for (int i4 = 1; i4 <= i; i4++)
            {
                System.arraycopy(ad, j * (i4 - 1), ad3, k1 * (i4 - 1), i4);
            }

            for (int j4 = 1; j4 <= i; j4++)
            {
                if (ad3[(j4 + k1 * (j4 - 1)) - 1] == 0.0D)
                {
                    BLAS.set(j4, 0.0D, ad3, k1 * (j4 - 1), 1);
                    BLAS.set(i - j4, 0.0D, ad3, (j4 + k1 * j4) - 1, k1);
                    continue;
                }
                ad3[(j4 + k1 * (j4 - 1)) - 1] = 1.0D / ad3[(j4 + k1 * (j4 - 1)) - 1];
                double d2 = -ad3[(j4 + k1 * (j4 - 1)) - 1];
                BLAS.scal(j4 - 1, d2, ad3, k1 * (j4 - 1), 1);
                if (j4 < i)
                {
                    BLAS.ger(j4 - 1, i - j4, 1.0D, ad3, k1 * (j4 - 1), 1, ad3, (j4 + k1 * j4) - 1, k1, ad3, k1 * j4, k1);
                    BLAS.scal(i - j4, ad3[(j4 + k1 * (j4 - 1)) - 1], ad3, (j4 + k1 * j4) - 1, k1);
                }
            }

            for (int i3 = 1; i3 <= i - 1; i3++)
            {
                BLAS.set(i - i3, 0.0D, ad3, (i3 + 1 + k1 * (i3 - 1)) - 1, 1);
            }

        }
    }

    private void l_m4mme(double d, int i, int j, double ad[], double ad1[], double ad2[], double ad3[], double ad4[],
            double ad5[], double ad6[], double ad7[], double ad8[]) // throws
                                                                    // TooManyCallsException,
                                                                    // IncreaseErrRelException,
                                                                    // NewInitialGuessException
    {
        int ai[] =
        {
            0
        };
        int ai1[] =
        {
            0
        };
        double d2 = 100D;
        ai[0] = 0;
        int i1 = j * (i + 1);
        int j1 = i - 1;
        int k1 = i - 1;
        double d1 = 0.0D;
        byte byte0 = 2;
        BLAS.set(i, 1.0D, ad8, 0, 1);
        int l1 = 0;
        int l = (i * (i + 1)) / 2;
        System.arraycopy(ad1, 0, ad2, 0, i);
        l_m5mme(d, i, ad, ad2, ad4, ad5, ad6, ad7, i1, j1, k1, d1, byte0, d2, l1, ai, ai1, l, ad8, 0, i, i * 2, i * 3,
                i * 4);
        if (ai[0] == 5)
        {
            ai[0] = 4;
        }
        ad3[0] = BLAS.dot(i, ad4, 0, 1, ad4, 0, 1);
        if (ai[0] == 2)
        {
            int k = j * (i + 1);
            // throw new TooManyCallsException("ARMA.TooManyCalls", null);
            throw new IllegalArgumentException("ARMA.TooManyCalls");
        }
        if (ai[0] == 3)
        {
            Object aobj[] =
            {
                new Double(d)
            };
            // throw new IncreaseErrRelException("ARMA.IncreaseErrrel", aobj);
            throw new IllegalArgumentException("ARMA.IncreaseErrrel");
        }
        if (ai[0] == 4) // throw new
                        // NewInitialGuessException("ARMA.NewInitialGuess",
                        // null);
        {
            throw new IllegalArgumentException("ARMA.NewInitialGuess");
        }
        else
        {
            return;
        }
    }

    private void l_m5mme(double d, int i, double ad[], double ad1[], double ad2[], double ad3[], double ad4[],
            double ad5[], int j, int k, int l, double d1, int i1, double d2, int j1, int ai[], int ai1[], int k1,
            double ad6[], int l1, int i2, int j2, int k2, int l2)
    {
        int ai2[] =
        {
            0
        };
        int ai3[] =
        {
            0
        };
        double d5 = 2.2204460492503131E-016D;
        double d4 = 0.0D;
        double d16 = 0.0D;
        ai[0] = 0;
        int k3 = 0;
        ai1[0] = 0;
        boolean flag2 = false;
        if (i1 == 2)
        {
            for (int i4 = 1; i4 <= i; i4++)
            {
                if (ad6[(l1 + i4) - 1] <= 0.0D)
                {
                    flag2 = true;
                }
            }

        }
        if (!flag2)
        {
            k3 = 1;
            l_m3mme(ad1, 0, ad2, 0, i, ad);
            ai1[0] = 1;
            boolean flag3 = false;
            if (k3 < 0)
            {
                flag3 = true;
            }
            if (!flag3)
            {
                double d6 = BLAS.nrm2(i, ad2, 0, 1);
                int i7 = Math.min(k + l + 1, i);
                int l3 = 1;
                int k7 = 0;
                int j7 = 0;
                int l7 = 0;
                int i8 = 0;
                boolean flag4;
                do
                {
                    flag4 = false;
                    boolean flag = true;
                    k3 = 2;
                    l_m6mme(i, ad1, ad2, ad3, 2, k, l, d1, ad6, i2, j2, ad);
                    ai1[0] += i7;
                    if (k3 < 0)
                    {
                        break;
                    }
                    l_n5qnf(i, i, ad3, 0, ai2, 1, ad6, i2, j2, k2);
                    if (l3 == 1)
                    {
                        if (i1 != 2)
                        {
                            System.arraycopy(ad6, j2, ad6, l1, i);
                            for (int j4 = 1; j4 <= i; j4++)
                            {
                                if (ad6[(j2 + j4) - 1] == 0.0D)
                                {
                                    ad6[(l1 + j4) - 1] = 1.0D;
                                }
                            }

                        }
                        for (int k4 = 1; k4 <= i; k4++)
                        {
                            ad6[(k2 + k4) - 1] = ad6[(l1 + k4) - 1] * ad1[k4 - 1];
                        }

                        d16 = BLAS.nrm2(i, ad6, k2, 1);
                        d4 = d2 * d16;
                        if (d4 == 0.0D)
                        {
                            d4 = d2;
                        }
                    }
                    System.arraycopy(ad2, 0, ad5, 0, i);
                    for (int l4 = 1; l4 <= i; l4++)
                    {
                        if (ad3[(l4 - 1) * i + (l4 - 1)] != 0.0D)
                        {
                            double d11 = BLAS.dot((i - l4) + 1, ad3, (l4 - 1) * i + (l4 - 1), 1, ad5, l4 - 1, 1);
                            double d14 = -d11 / ad3[(l4 - 1) * i + (l4 - 1)];
                            BLAS.axpy((i - l4) + 1, d14, ad3, (l4 - 1) * i + (l4 - 1), 1, ad5, l4 - 1, 1);
                        }
                    }

                    ai3[0] = 0;
                    for (int i5 = 1; i5 <= i; i5++)
                    {
                        int k6 = i5;
                        int j6 = i5 - 1;
                        if (j6 >= 1)
                        {
                            for (int i3 = 1; i3 <= j6; i3++)
                            {
                                ad4[k6 - 1] = ad3[(i5 - 1) * i + (i3 - 1)];
                                k6 += i - i3;
                            }

                        }
                        ad4[k6 - 1] = ad6[(i2 + i5) - 1];
                        if (ad6[(i2 + i5) - 1] == 0.0D)
                        {
                            ai3[0] = 1;
                        }
                    }

                    l_n6qnf(i, i, ad3, ad6, i2);
                    if (i1 != 2)
                    {
                        for (int j5 = 1; j5 <= i; j5++)
                        {
                            ad6[(l1 + j5) - 1] = Math.max(ad6[(l1 + j5) - 1], ad6[(j2 + j5) - 1]);
                        }

                    }
                    do
                    {
                        boolean flag1 = false;
                        if (j1 <= 0)
                        {
                            flag1 = true;
                        }
                        if (!flag1 && k3 < 0)
                        {
                            flag4 = true;
                            break;
                        }
                        l_n7qnf(i, ad4, k1, ad6, l1, ad5, d4, i2, j2, k2);
                        BLAS.scal(i, -1D, ad6, i2, 1);
                        for (int k5 = 1; k5 <= i; k5++)
                        {
                            ad6[(j2 + k5) - 1] = ad1[k5 - 1] + ad6[(i2 + k5) - 1];
                            ad6[(k2 + k5) - 1] = ad6[(l1 + k5) - 1] * ad6[(i2 + k5) - 1];
                        }

                        double d8 = BLAS.nrm2(i, ad6, k2, 1);
                        if (l3 == 1)
                        {
                            d4 = Math.min(d4, d8);
                        }
                        l_m3mme(ad6, j2, ad6, l2, i, ad);
                        ai1[0]++;
                        double d7 = BLAS.nrm2(i, ad6, l2, 1);
                        double d3 = -1D;
                        if (d7 < d6)
                        {
                            d3 = 1.0D - Math.pow(d7 / d6, 2D);
                        }
                        int l6 = 1;
                        for (int j3 = 1; j3 <= i; j3++)
                        {
                            double d12 = BLAS.dot((i - j3) + 1, ad4, l6 - 1, 1, ad6, (i2 + j3) - 1, 1);
                            l6 += (i - j3) + 1;
                            ad6[(k2 + j3) - 1] = ad5[j3 - 1] + d12;
                        }

                        double d15 = BLAS.nrm2(i, ad6, k2, 1);
                        double d9 = 1.0D;
                        if (d15 < d6)
                        {
                            d9 = 1.0D - Math.pow(d15 / d6, 2D);
                        }
                        double d10 = 0.0D;
                        if (d9 > 0.0D)
                        {
                            d10 = d3 / d9;
                        }
                        if (d10 >= 0.10000000000000001D)
                        {
                            j7 = 0;
                            k7++;
                            if (d10 >= 0.5D || k7 > 1)
                            {
                                d4 = Math.max(d4, d8 / 0.5D);
                            }
                            if (Math.abs(d10 - 1.0D) <= 0.10000000000000001D)
                            {
                                d4 = d8 / 0.5D;
                            }
                        }
                        else
                        {
                            k7 = 0;
                            j7++;
                            d4 *= 0.5D;
                        }
                        if (d10 >= 0.0001D)
                        {
                            System.arraycopy(ad6, j2, ad1, 0, i);
                            System.arraycopy(ad6, l2, ad2, 0, i);
                            for (int l5 = 1; l5 <= i; l5++)
                            {
                                ad6[(j2 + l5) - 1] = ad6[(l1 + l5) - 1] * ad1[l5 - 1];
                            }

                            d16 = BLAS.nrm2(i, ad6, j2, 1);
                            d6 = d7;
                            l3++;
                        }
                        l7++;
                        if (d3 >= 0.001D)
                        {
                            l7 = 0;
                        }
                        if (flag)
                        {
                            i8++;
                        }
                        if (d3 >= 0.10000000000000001D)
                        {
                            i8 = 0;
                        }
                        if (d4 <= d * d16 || d6 == 0.0D)
                        {
                            ai[0] = 1;
                        }
                        if (ai[0] != 0)
                        {
                            flag4 = true;
                            break;
                        }
                        if (ai1[0] >= j)
                        {
                            ai[0] = 2;
                        }
                        if (0.10000000000000001D * Math.max(0.10000000000000001D * d4, d8) <= d5 * d16)
                        {
                            ai[0] = 3;
                        }
                        if (i8 == 5)
                        {
                            ai[0] = 4;
                        }
                        if (l7 == 10)
                        {
                            ai[0] = 5;
                        }
                        if (ai[0] != 0)
                        {
                            flag4 = true;
                            break;
                        }
                        if (j7 == 2)
                        {
                            break;
                        }
                        for (int i6 = 1; i6 <= i; i6++)
                        {
                            double d13 = BLAS.dot(i, ad3, (i6 - 1) * i, 1, ad6, l2, 1);
                            ad6[(j2 + i6) - 1] = (d13 - ad6[(k2 + i6) - 1]) / d8;
                            ad6[(i2 + i6) - 1] = ad6[(l1 + i6) - 1] * ((ad6[(l1 + i6) - 1] * ad6[(i2 + i6) - 1]) / d8);
                            if (d10 >= 0.0001D)
                            {
                                ad5[i6 - 1] = d13;
                            }
                        }

                        l_n8qnf(i, i, ad4, ad6, i2, j2, k2, ai3);
                        l_n9qnf(i, i, ad3, i, ad6, j2, k2);
                        l_n9qnf(1, i, ad5, 1, ad6, j2, k2);
                        flag = false;
                    }
                    while (true);
                }
                while (!flag4);
            }
        }
        if (k3 < 0)
        {
            ai[0] = k3;
        }
        k3 = 0;
    }

    private void l_m6mme(int i, double ad[], double ad1[], double ad2[], int j, int k, int l, double d, double ad3[],
            int i1, int j1, double ad4[])
    {
        double d2 = 2.2204460492503131E-016D;
        double d1 = Math.sqrt(Math.max(d, d2));
        int l3 = k + l + 1;
        if (l3 >= i)
        {
            for (int i3 = 1; i3 <= i; i3++)
            {
                double d6 = ad[i3 - 1];
                double d3 = d1 * Math.abs(d6);
                if (d3 == 0.0D)
                {
                    d3 = d1;
                }
                ad[i3 - 1] = d6 + d3;
                l_m3mme(ad, 0, ad3, i1, i, ad4);
                if (j < 0)
                {
                    return;
                }
                ad[i3 - 1] = d6;
                for (int k2 = 1; k2 <= i; k2++)
                {
                    ad2[(i3 - 1) * i + (k2 - 1)] = (ad3[(i1 + k2) - 1] - ad1[k2 - 1]) / d3;
                }

            }

            return;
        }
        for (int k3 = 1; k3 <= l3; k3++)
        {
            int j3 = k3;
            int l1 = l3;
            for (int k1 = ((i - k3) + l1) / l1 <= 0 ? 0 : ((i - k3) + l1) / l1; k1 > 0; k1--)
            {
                ad3[(j1 + j3) - 1] = ad[j3 - 1];
                double d4 = d1 * Math.abs(ad3[(j1 + j3) - 1]);
                if (d4 == 0.0D)
                {
                    d4 = d1;
                }
                ad[j3 - 1] = ad3[(j1 + j3) - 1] + d4;
                j3 += l1;
            }

            l_m3mme(ad, 0, ad3, i1, i, ad4);
            if (j < 0)
            {
                return;
            }
            j3 = k3;
            int j2 = l3;
            for (int i2 = ((i - k3) + j2) / j2 <= 0 ? 0 : ((i - k3) + j2) / j2; i2 > 0; i2--)
            {
                ad[j3 - 1] = ad3[(j1 + j3) - 1];
                double d5 = d1 * Math.abs(ad3[(j1 + j3) - 1]);
                if (d5 == 0.0D)
                {
                    d5 = d1;
                }
                for (int l2 = 1; l2 <= i; l2++)
                {
                    ad2[(j3 - 1) * i + (l2 - 1)] = 0.0D;
                    if (l2 >= j3 - l && l2 <= j3 + k)
                    {
                        ad2[(j3 - 1) * i + (l2 - 1)] = (ad3[(i1 + l2) - 1] - ad1[l2 - 1]) / d5;
                    }
                }

                j3 += j2;
            }

        }

    }

    private void l_m3mme(double ad[], int i, double ad1[], int j, int k, double ad2[])
    {
        int j1 = k - 1;
        BLAS.dvcal(k, -1D, ad2, 0, 1, ad1, j + 0, 1);
        for (int i1 = 0; i1 <= j1; i1++)
        {
            for (int l = 0; l <= j1 - i1; l++)
            {
                ad1[j + i1] += ad[i + l] * ad[i + l + i1];
            }

        }

    }

    private void l_n5qnf(int i, int j, double ad[], int k, int ai[], int l, double ad1[], int i1, int j1, int k1)
    {
        double d2 = 2.2204460492503131E-016D;
        for (int l1 = 1; l1 <= j; l1++)
        {
            ad1[(j1 + l1) - 1] = BLAS.nrm2(i, ad, (l1 - 1) * j, 1);
            if (k != 0)
            {
                ai[l1 - 1] = l1;
            }
        }

        System.arraycopy(ad1, j1, ad1, i1, j);
        System.arraycopy(ad1, i1, ad1, k1, j);
        int k3 = Math.min(i, j);
        for (int i2 = 1; i2 <= k3; i2++)
        {
            if (k != 0)
            {
                int j3 = i2;
                for (int k2 = i2; k2 <= j; k2++)
                {
                    if (ad1[(i1 + k2) - 1] > ad1[(i1 + j3) - 1])
                    {
                        j3 = k2;
                    }
                }

                if (j3 != i2)
                {
                    BLAS.swap(i, ad, (i2 - 1) * j, ad, (j3 - 1) * j);
                    ad1[(i1 + j3) - 1] = ad1[(i1 + i2) - 1];
                    ad1[(k1 + j3) - 1] = ad1[(k1 + i2) - 1];
                    int l2 = ai[i2 - 1];
                    ai[i2 - 1] = ai[j3 - 1];
                    ai[j3 - 1] = l2;
                }
            }
            double d = BLAS.nrm2((i - i2) + 1, ad, (i2 - 1) * j + (i2 - 1), 1);
            double d3 = 2.2250738585072009E-308D;
            double d1 = 1.7976931348623157E+308D;
            if (d3 * d1 < 1.0D)
            {
                d3 = 1.0D / d1;
            }
            if (d != 0.0D)
            {
                if (ad[(i2 - 1) * j + (i2 - 1)] < 0.0D)
                {
                    d = -d;
                }
                BLAS.scal((i - i2) + 1, 1.0D / d, ad, (i2 - 1) * j + (i2 - 1), 1);
                ad[(i2 - 1) * j + (i2 - 1)]++;
                int j2 = i2 + 1;
                if (j >= j2)
                {
                    for (int i3 = j2; i3 <= j; i3++)
                    {
                        double d4 = BLAS.dot((i - i2) + 1, ad, (i2 - 1) * j + (i2 - 1), 1, ad, (i3 - 1) * j + (i2 - 1),
                                1);
                        double d5 = d4 / ad[(i2 - 1) * j + (i2 - 1)];
                        BLAS.axpy((i - i2) + 1, -d5, ad, (i2 - 1) * j + (i2 - 1), 1, ad, (i3 - 1) * j + (i2 - 1), 1);
                        if (k == 0 || ad1[(i1 + i3) - 1] == 0.0D)
                        {
                            continue;
                        }
                        d5 = ad[(i3 - 1) * j + (i2 - 1)] / ad1[(i1 + i3) - 1];
                        ad1[(i1 + i3) - 1] *= Math.sqrt(Math.max(0.0D, 1.0D - Math.pow(d5, 2D)));
                        if (0.050000000000000003D * Math.pow(ad1[(i1 + i3) - 1] / ad1[(k1 + i3) - 1], 2D) <= d2)
                        {
                            ad1[(i1 + i3) - 1] = BLAS.nrm2(i - i2, ad, (i3 - 1) * j + (j2 - 1), 1);
                            ad1[(k1 + i3) - 1] = ad1[(i1 + i3) - 1];
                        }
                    }

                }
            }
            ad1[(i1 + i2) - 1] = -d;
        }

    }

    private void l_n6qnf(int i, int j, double ad[], double ad1[], int k)
    {
        int j2 = Math.min(i, j);
        if (j2 >= 2)
        {
            for (int l = 2; l <= j2; l++)
            {
                int k1 = l - 1;
                BLAS.set(k1, 0.0D, ad, (l - 1) * j, 1);
            }

        }
        int k2 = j + 1;
        if (i >= k2)
        {
            for (int i1 = k2; i1 <= i; i1++)
            {
                BLAS.set(i, 0.0D, ad, (i1 - 1) * j, 1);
                ad[(i1 - 1) * j + (i1 - 1)] = 1.0D;
            }

        }
        for (int i2 = 1; i2 <= j2; i2++)
        {
            int l1 = (j2 - i2) + 1;
            System.arraycopy(ad, (l1 - 1) * j + (l1 - 1), ad1, (k + l1) - 1, (i - l1) + 1);
            BLAS.set((i - l1) + 1, 0.0D, ad, (l1 - 1) * j + (l1 - 1), 1);
            ad[(l1 - 1) * j + (l1 - 1)] = 1.0D;
            double d1 = 2.2250738585072009E-308D;
            double d = 1.7976931348623157E+308D;
            if (d * d1 < 1.0D)
            {
                d1 = 1.0D / d;
            }
            if (ad1[(k + l1) - 1] == 0.0D)
            {
                continue;
            }
            for (int j1 = l1; j1 <= i; j1++)
            {
                double d2 = BLAS.dot((i - l1) + 1, ad, (j1 - 1) * j + (l1 - 1), 1, ad1, (k + l1) - 1, 1);
                double d3 = d2 / ad1[(k + l1) - 1];
                BLAS.axpy((i - l1) + 1, -d3, ad1, (k + l1) - 1, 1, ad, (j1 - 1) * j + (l1 - 1), 1);
            }

        }

    }

    private void l_n7qnf(int i, double ad[], int j, double ad1[], int k, double ad2[], double d, int l, int i1, int j1)
    {
        double d4 = 2.2204460492503131E-016D;
        int j3 = (i * (i + 1)) / 2 + 1;
        for (int l3 = 1; l3 <= i; l3++)
        {
            int l1 = (i - l3) + 1;
            int k3 = l1 + 1;
            j3 -= l3;
            int i4 = j3 + 1;
            double d9 = BLAS.dot((i - k3) + 1, ad, i4 - 1, 1, ad1, (l + k3) - 1, 1);
            double d11 = ad[j3 - 1];
            if (d11 == 0.0D)
            {
                int j4 = l1;
                for (int k1 = 1; k1 <= l1; k1++)
                {
                    d11 = Math.max(d11, Math.abs(ad[j4 - 1]));
                    j4 += i - k1;
                }

                d11 *= d4;
                if (d11 == 0.0D)
                {
                    d11 = d4;
                }
            }
            ad1[(l + l1) - 1] = (ad2[l1 - 1] - d9) / d11;
        }

        BLAS.set(i, 0.0D, ad1, i1, 1);
        for (int i2 = 1; i2 <= i; i2++)
        {
            ad1[(j1 + i2) - 1] = ad1[(k + i2) - 1] * ad1[(l + i2) - 1];
        }

        double d6 = BLAS.nrm2(i, ad1, j1, 1);
        if (d6 > d)
        {
            int k4 = 1;
            for (int j2 = 1; j2 <= i; j2++)
            {
                double d12 = ad2[j2 - 1];
                BLAS.axpy((i - j2) + 1, d12, ad, k4 - 1, 1, ad1, (i1 + j2) - 1, 1);
                k4 += (i - j2) + 1;
                ad1[(i1 + j2) - 1] /= ad1[(k + j2) - 1];
            }

            double d5 = BLAS.nrm2(i, ad1, i1, 1);
            double d7 = 0.0D;
            double d1 = d / d6;
            double d8 = 2.2250738585072009E-308D;
            double d2 = 1.7976931348623157E+308D;
            if (d2 * d8 < 1.0D)
            {
                d8 = 1.0D / d2;
            }
            if (d5 != 0.0D)
            {
                for (int k2 = 1; k2 <= i; k2++)
                {
                    ad1[(i1 + k2) - 1] = ad1[(i1 + k2) - 1] / d5 / ad1[(k + k2) - 1];
                }

                int l4 = 1;
                for (int l2 = 1; l2 <= i; l2++)
                {
                    double d10 = BLAS.dot((i - l2) + 1, ad, l4 - 1, 1, ad1, (i1 + l2) - 1, 1);
                    l4 += (i - l2) + 1;
                    ad1[(j1 + l2) - 1] = d10;
                }

                double d13 = BLAS.nrm2(i, ad1, j1, 1);
                d7 = d5 / d13 / d13;
                d1 = 0.0D;
                if (d7 < d)
                {
                    double d3 = BLAS.nrm2(i, ad2, 0, 1);
                    double d14 = (d3 / d5) * (d3 / d6) * (d7 / d);
                    d14 += -(d / d6)
                            * Math.pow(d7 / d, 2D)
                            + Math.sqrt(Math.pow(d14 - d / d6, 2D) + (1.0D - Math.pow(d / d6, 2D))
                                    * (1.0D - Math.pow(d7 / d, 2D)));
                    d1 = ((d / d6) * (1.0D - Math.pow(d7 / d, 2D))) / d14;
                }
            }
            double d15 = (1.0D - d1) * Math.min(d7, d);
            for (int i3 = 1; i3 <= i; i3++)
            {
                ad1[(l + i3) - 1] = d15 * ad1[(i1 + i3) - 1] + d1 * ad1[(l + i3) - 1];
            }

        }
    }

    private void l_n8qnf(int i, int j, double ad[], double ad1[], int k, int l, int i1, int ai[])
    {
        double d = 1.7976931348623157E+308D;
        int k2 = (j * ((2 * i - j) + 1)) / 2 - (i - j);
        System.arraycopy(ad, k2 - 1, ad1, (i1 + j) - 1, (i - j) + 1);
        int j3 = j - 1;
        if (j3 >= 1)
        {
            for (int k3 = 1; k3 <= j3; k3++)
            {
                int i2 = j - k3;
                k2 -= (i - i2) + 1;
                ad1[(i1 + i2) - 1] = 0.0D;
                if (ad1[(l + i2) - 1] == 0.0D)
                {
                    continue;
                }
                double d1;
                double d5;
                double d9;
                if (Math.abs(ad1[(l + j) - 1]) < Math.abs(ad1[(l + i2) - 1]))
                {
                    double d7 = ad1[(l + j) - 1] / ad1[(l + i2) - 1];
                    d9 = 0.5D / Math.sqrt(0.25D + 0.25D * Math.pow(d7, 2D));
                    d5 = d9 * d7;
                    d1 = 1.0D;
                    if (Math.abs(d5) * d > 1.0D)
                    {
                        d1 = 1.0D / d5;
                    }
                }
                else
                {
                    double d11 = ad1[(l + i2) - 1] / ad1[(l + j) - 1];
                    d5 = 0.5D / Math.sqrt(0.25D + 0.25D * Math.pow(d11, 2D));
                    d9 = d5 * d11;
                    d1 = d9;
                }
                ad1[(l + j) - 1] = d9 * ad1[(l + i2) - 1] + d5 * ad1[(l + j) - 1];
                ad1[(l + i2) - 1] = d1;
                int l2 = k2;
                for (int j1 = i2; j1 <= i; j1++)
                {
                    double d3 = d5 * ad[l2 - 1] - d9 * ad1[(i1 + j1) - 1];
                    ad1[(i1 + j1) - 1] = d9 * ad[l2 - 1] + d5 * ad1[(i1 + j1) - 1];
                    ad[l2 - 1] = d3;
                    l2++;
                }

            }

        }
        for (int k1 = 1; k1 <= i; k1++)
        {
            ad1[(i1 + k1) - 1] += ad1[(l + j) - 1] * ad1[(k + k1) - 1];
        }

        ai[0] = 0;
        if (j3 >= 1)
        {
            for (int j2 = 1; j2 <= j3; j2++)
            {
                if (ad1[(i1 + j2) - 1] != 0.0D)
                {
                    double d2;
                    double d6;
                    double d10;
                    if (Math.abs(ad[k2 - 1]) < Math.abs(ad1[(i1 + j2) - 1]))
                    {
                        double d8 = ad[k2 - 1] / ad1[(i1 + j2) - 1];
                        d10 = 0.5D / Math.sqrt(0.25D + 0.25D * Math.pow(d8, 2D));
                        d6 = d10 * d8;
                        d2 = 1.0D;
                        if (Math.abs(d6) * d > 1.0D)
                        {
                            d2 = 1.0D / d6;
                        }
                    }
                    else
                    {
                        double d12 = ad1[(i1 + j2) - 1] / ad[k2 - 1];
                        d6 = 0.5D / Math.sqrt(0.25D + 0.25D * Math.pow(d12, 2D));
                        d10 = d6 * d12;
                        d2 = d10;
                    }
                    int i3 = k2;
                    for (int l1 = j2; l1 <= i; l1++)
                    {
                        double d4 = d6 * ad[i3 - 1] + d10 * ad1[(i1 + l1) - 1];
                        ad1[(i1 + l1) - 1] = -d10 * ad[i3 - 1] + d6 * ad1[(i1 + l1) - 1];
                        ad[i3 - 1] = d4;
                        i3++;
                    }

                    ad1[(i1 + j2) - 1] = d2;
                }
                if (ad[k2 - 1] == 0.0D)
                {
                    ai[0] = 1;
                }
                k2 += (i - j2) + 1;
            }

        }
        System.arraycopy(ad1, (i1 + j) - 1, ad, k2 - 1, (i - j) + 1);
        if (ad[k2 - 1] == 0.0D)
        {
            ai[0] = 1;
        }
    }

    private void l_n9qnf(int i, int j, double ad[], int k, double ad1[], int l, int i1)
    {
        int j2 = j - 1;
        if (j2 >= 1)
        {
            for (int k2 = 1; k2 <= j2; k2++)
            {
                int l1 = j - k2;
                double d2;
                double d4;
                if (Math.abs(ad1[(l + l1) - 1]) > 1.0D)
                {
                    d2 = 1.0D / ad1[(l + l1) - 1];
                    d4 = Math.sqrt(1.0D - Math.pow(d2, 2D));
                }
                else
                {
                    d4 = ad1[(l + l1) - 1];
                    d2 = Math.sqrt(1.0D - Math.pow(d4, 2D));
                }
                for (int j1 = 1; j1 <= i; j1++)
                {
                    double d = d2 * ad[(l1 - 1) * k + (j1 - 1)] - d4 * ad[(j - 1) * k + (j1 - 1)];
                    ad[(j - 1) * k + (j1 - 1)] = d4 * ad[(l1 - 1) * k + (j1 - 1)] + d2 * ad[(j - 1) * k + (j1 - 1)];
                    ad[(l1 - 1) * k + (j1 - 1)] = d;
                }

            }

            for (int i2 = 1; i2 <= j2; i2++)
            {
                double d3;
                double d5;
                if (Math.abs(ad1[(i1 + i2) - 1]) > 1.0D)
                {
                    d3 = 1.0D / ad1[(i1 + i2) - 1];
                    d5 = Math.sqrt(1.0D - Math.pow(d3, 2D));
                }
                else
                {
                    d5 = ad1[(i1 + i2) - 1];
                    d3 = Math.sqrt(1.0D - Math.pow(d5, 2D));
                }
                for (int k1 = 1; k1 <= i; k1++)
                {
                    double d1 = d3 * ad[(i2 - 1) * k + (k1 - 1)] + d5 * ad[(j - 1) * k + (k1 - 1)];
                    ad[(j - 1) * k + (k1 - 1)] = -d5 * ad[(i2 - 1) * k + (k1 - 1)] + d3 * ad[(j - 1) * k + (k1 - 1)];
                    ad[(i2 - 1) * k + (k1 - 1)] = d1;
                }

            }

        }
    }

    private double l_a1ot(int i, double ad[], int j, int k, double ad1[], int l, int i1)
    {
        double d = 0.0D;
        if (i > 0)
        {
            if (k != 1 || i1 != 1)
            {
                int l1 = 1;
                int i2 = 1;
                if (k < 0)
                {
                    l1 = (-i + 1) * k + 1;
                }
                if (i1 < 0)
                {
                    i2 = (-i + 1) * i1 + 1;
                }
                for (int j1 = 1; j1 <= i; j1++)
                {
                    d += Math.abs(ad[(j + l1) - 1] * ad1[(l + i2) - 1]);
                    l1 += k;
                    i2 += i1;
                }

            }
            else
            {
                for (int k1 = 1; k1 <= i; k1++)
                {
                    d += Math.abs(ad[(j + k1) - 1] * ad1[(l + k1) - 1]);
                }

            }
        }
        return d;
    }

    private void l_m1ran(int i, int j, double ad[], double ad1[])
    {
        boolean flag = false;
        int k1 = i * j - 1;
        if (i != j)
        {
            flag = true;
        }
        if (!flag)
        {
            if (ad1 != ad)
            {
                BLAS.copy(i * j, ad, ad1);
            }
            int l1 = 2;
            int i2 = i + 1;
            int j2 = i - 1;
            for (int k = i; k <= k1; k += i)
            {
                int k2 = l1 + j2;
                for (int i1 = l1; i1 <= k; i1++)
                {
                    double d = ad1[i1 - 1];
                    ad1[i1 - 1] = ad1[k2 - 1];
                    ad1[k2 - 1] = d;
                    k2 += i;
                }

                l1 += i2;
            }

            return;
        }
        double ad2[];
        if (ad == ad1)
        {
            ad2 = new double[j * i];
        }
        else
        {
            ad2 = ad1;
        }
        for (int l = 0; l < i; l++)
        {
            for (int j1 = 0; j1 < j; j1++)
            {
                ad2[l + i * j1] = ad[j1 + j * l];
            }

        }

        if (ad == ad1)
        {
            BLAS.copy(i * j, ad2, ad1);
        }
    }

    private void l_n2bjf(int i, double ad[], int j, int k, double ad1[], int ai[], int l, double ad2[], int ai1[],
            int i1, double d, double d1, double d2, int j1, int k1, double ad3[], int l1, double ad4[], double ad5[],
            double ad6[], double ad7[], int ai2[])
    {
        int i4 = 0;
        int ai3[] =
        {
            0
        };
        i4++;
        i4++;
        for (int j2 = j1; j2 >= 0; j2--)
        {
            int j3 = (j1 - j2) + 1;
            int l2 = i - j2;
            for (int k3 = 1; k3 <= k1; k3++)
            {
                if (i1 == 0)
                {
                    ad3[(j3 - 1) * l1 + (k3 - 1)] = 0.0D;
                }
                else
                {
                    ad3[(j3 - 1) * l1 + (k3 - 1)] = d;
                }
                for (int i2 = 1; i2 <= k; i2++)
                {
                    double d5;
                    if (k3 > ai[i2 - 1])
                    {
                        d5 = ad3[(j3 - 1) * l1 + (k3 - ai[i2 - 1] - 1)];
                    }
                    else
                    {
                        d5 = ad[((l2 - ai[i2 - 1]) + k3) - 1];
                    }
                    ad3[(j3 - 1) * l1 + (k3 - 1)] += ad1[i2 - 1] * d5;
                }

                for (int i3 = 1; i3 <= l; i3++)
                {
                    int k2 = (l2 - ai1[i3 - 1]) + k3;
                    double d6;
                    if (k3 > ai1[i3 - 1] || k2 <= i - j1)
                    {
                        d6 = 0.0D;
                    }
                    else
                    {
                        d6 = ad[k2 - 1] - ad3[(j3 - 2) * l1];
                    }
                    ad3[(j3 - 1) * l1 + (k3 - 1)] += ad2[i3 - 1] * d6;
                }

            }

        }

        if (k == 0 && l == 0)
        {
            BLAS.set(k1, 0.0D, ad3, (j1 + 2) * l1, 1);
        }
        else if (k == 0)
        {
            double d3 = -1D;
            BLAS.set(k1, 0.0D, ad3, (j1 + 2) * l1, 1);
            BLAS.dvcal(l, d3, ad2, 0, 1, ad3, (j1 + 2) * l1, 1);
        }
        else
        {
            l_n2psw(k, ad1, ai, l, ad2, ai1, k1, ai3, ad7, ai2, ad4, ad5, ad6);
            BLAS.copy(k1, ad6, 1, 1, ad3, (j1 + 2) * l1, 1);
        }
        double d7 = Cdf.inverseNormal(1.0D - d2 / 2D);
        double d4 = 0.0D;
        ad3[(j1 + 1) * l1] = d7 * Math.sqrt(d1);
        for (int l3 = 2; l3 <= k1; l3++)
        {
            d4 += Math.pow(ad3[(j1 + 2) * l1 + (l3 - 2)], 2D);
            ad3[(j1 + 1) * l1 + (l3 - 1)] = d7 * Math.sqrt((1.0D + d4) * d1);
        }

    }

    private void l_n2psw(int i, double ad[], int ai[], int j, double ad1[], int ai1[], int k, int ai2[], double ad2[],
            int ai3[], double ad3[], double ad4[], double ad5[])
    {
        int l = 0;
        l = 1;
        l++;
        l++;
        l++;
        if (i == 0 && j == 0)
        {
            ad5[0] = 1.0D;
            BLAS.set(k, 0.0D, ad5, 1, 1);
            ai2[0] = 0;
        }
        else
        {
            l_g2old(-1, i, ad, ai, j, ad1, ai1, k, ai2, ad2, ai3, ad3, ad4, ad5);
        }
    }

    private void l_g2old(int i, int j, double ad[], int ai[], int k, double ad1[], int ai1[], int l, int ai2[],
            double ad2[], int ai3[], double ad3[], double ad4[], double ad5[])
    {
        int j2;
        if (j >= 1)
        {
            j2 = ai[BLAS.iimax(j, ai, 0, 1) - 1];
        }
        else
        {
            j2 = 0;
        }
        int k2;
        if (k >= 1)
        {
            k2 = ai1[BLAS.iimax(k, ai1, 0, 1) - 1];
        }
        else
        {
            k2 = 0;
        }
        BLAS.set(j2 + 1, 0.0D, ad3, 0, 1);
        BLAS.set(k2 + 1, 0.0D, ad4, 0, 1);
        if (i != 0)
        {
            ad3[0] = 1.0D;
            ad4[0] = 1.0D;
        }
        for (int i1 = 1; i1 <= j; i1++)
        {
            int l1 = ai[i1 - 1];
            ad3[l1] += ad[i1 - 1] * ((double) i >= 0.0D ? Math.abs(1.0D) : -Math.abs(1.0D));
        }

        for (int j1 = 1; j1 <= k; j1++)
        {
            int i2 = ai1[j1 - 1];
            ad4[i2] += ad1[j1 - 1] * ((double) i >= 0.0D ? Math.abs(1.0D) : -Math.abs(1.0D));
        }

        l_hpold(j2, ad3, k2, ad4, l, ad5);
        ai2[0] = 0;
        for (int k1 = 0; k1 <= l; k1++)
        {
            if (ad5[k1] != 0.0D)
            {
                ai2[0]++;
                ad2[ai2[0] - 1] = ad5[k1];
                ai3[ai2[0] - 1] = k1;
            }
        }

    }

    private void l_hpold(int i, double ad[], int j, double ad1[], int k, double ad2[])
    {
        int k1;
        int j1 = k1 = 0;
        boolean flag = false;
        if (i == j)
        {
            flag = true;
            for (int l = 0; l <= i; l++)
            {
                if (ad[l] != ad1[l])
                {
                    flag = false;
                }
            }

        }
        if (flag)
        {
            ad2[0] = 1.0D;
            if (k >= 1)
            {
                for (int i1 = 1; i1 <= k; i1++)
                {
                    ad2[i1] = 0.0D;
                }

            }
        }
        else
        {
            ad2[0] = ad1[0];
            for (int l1 = 1; l1 <= k; l1++)
            {
                if (i >= k)
                {
                    j1 = 0;
                    k1 = l1 - 1;
                }
                else if (i < k)
                {
                    if (l1 <= i)
                    {
                        j1 = 0;
                        k1 = l1 - 1;
                    }
                    else if (l1 > i)
                    {
                        j1 = l1 - i;
                        k1 = l1 - 1;
                    }
                }
                ad2[l1] = -BLAS.dot((k1 - j1) + 1, ad2, j1, 1, ad, l1 - k1, -1);
                if (l1 <= j)
                {
                    ad2[l1] += ad1[l1];
                }
            }

            if (ad[0] != 0.0D)
            {
                BLAS.scal(k + 1, ad[0], ad2, 0, 1);
            }
        }
    }

    public Matrix getMA()
    {
        if (lv_arma == null)
        {
            return null;
        }
        else
        {
            double ad[] = new double[l_q];
            System.arraycopy(lv_arma, l_p + 1, ad, 0, l_q);
            return new Matrix(ad);
        }
    }

    public Matrix getAR()
    {
        if (lv_arma == null)
        {
            return null;
        }
        else
        {
            double ad[] = new double[l_p];
            System.arraycopy(lv_arma, 1, ad, 0, l_p);
            return new Matrix(ad);
        }
    }

    public double getConstant()
    {
        if (lv_arma == null)
        {
            return (0.0D / 0.0D);
        }
        else
        {
            return lv_arma[0];
        }
    }

    public double getVariance()
    {
        if (l_autocov == null)
        {
            return (0.0D / 0.0D);
        }
        else
        {
            return l_autocov[0];
        }
    }

    public Matrix getAutoCovariance()
    {
        if (l_autocov == null)
        {
            return null;
        }
        else
        {
            double ad[] = new double[l_autocov.length - 1];
            System.arraycopy(l_autocov, 1, ad, 0, l_autocov.length - 1);
            return new Matrix(ad);
        }
    }

    public double getSSResidual()
    {
        l_mutual_ls++;
        if (l_mutual_ls >= 1 && l_method == 0) // Messages.throwIllegalArgumentException("com.imsl.stat",
                                               // "ARMA.MethodsInconsistent",
                                               // null);
        {
            throw new IllegalArgumentException("ARMA.MethodsInconsistent");
        }
        return l_ss_residual;
    }

    public Matrix getResidual()
    {
        if (l_residual == null)
        {
            return null;
        }
        l_mutual_ls++;
        if (l_mutual_ls >= 1 && l_method == 0) // Messages.throwIllegalArgumentException("com.imsl.stat",
                                               // "ARMA.MethodsInconsistent",
                                               // null);
        {
            throw new IllegalArgumentException("ARMA.MethodsInconsistent");
        }
        return new Matrix((double[]) l_residual.clone());
    }

    public void setInitialEstimates(double ad[], double ad1[])
    {
        if (ad.length != l_p)
        {
            Object aobj[] =
            {
                    "ar", new Integer(ad.length), new Integer(l_p)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj);
            throw new IllegalArgumentException("NotEqual");
        }
        if (ad1.length != l_q)
        {
            Object aobj1[] =
            {
                    "ma", new Integer(ad1.length), new Integer(l_q)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj1);
            throw new IllegalArgumentException("NotEqual");
        }
        l_ar_init = (double[]) ad.clone();
        l_ma_init = (double[]) ad1.clone();
        l_initial_est = 1;
        l_mutual_est++;
        if (l_mutual_est > 1) // Messages.throwIllegalArgumentException("com.imsl.stat",
                              // "ARMA.MethodsInconsistent", null);
        {
            throw new IllegalArgumentException("ARMA.MethodsInconsistent");
        }
    }

    public double getMeanEstimate()
    {
        return l_rw_mean;
    }

    public void setMeanEstimate(double d)
    {
        l_rw_mean = d;
        l_init_mean = 1;
    }

    public void setRelativeError(double d)
    {
        if (d < 0.0D)
        {
            Object aobj[] =
            {
                    "relativeError", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotPositive", aobj);
            throw new IllegalArgumentException("NotPositive");
        }
        l_relative_error = d;
        l_init_rel_error = 1;
    }

    public void setConvergenceTolerance(double d)
    {
        if (d < 0.0D || d >= 1.0D)
        {
            Object aobj[] =
            {
                    "convergenceTolerance", new Double(d), new Double(0.0D), new Double(1.0D)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "OutOfRange", aobj);
            throw new IllegalArgumentException("OutOfRange");
        }
        l_convergence_tolerance = d;
        l_init_conv_tol = 1;
        l_mutual_ls++;
    }

    public void setBackcasting(int i, double d)
    {
        if (i < 0)
        {
            Object aobj[] =
            {
                    "length", new Integer(i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "Negative", aobj);
            throw new IllegalArgumentException("Negative");
        }
        if (d < 0.0D)
        {
            Object aobj1[] =
            {
                    "tolerance", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "Negative", aobj1);
            throw new IllegalArgumentException("Negative");
        }
        l_length = i;
        l_tolerance = d;
        l_init_back = 1;
        l_mutual_ls++;
    }

    public void setMALags(int ai[])
    {
        if (ai.length != l_q)
        {
            Object aobj[] =
            {
                    "maLags", new Integer(ai.length), new Integer(l_q)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj);
            throw new IllegalArgumentException("NotEqual");
        }
        if (ai[BLAS.iimin(ai.length, ai, 0, 1) - 1] <= 0)
        {
            Object aobj1[] =
            {
                    "maLags[" + (BLAS.iimin(ai.length, ai, 0, 1) - 1) + "]",
                    new Integer(ai[BLAS.iimin(ai.length, ai, 0, 1) - 1]), new Integer(0)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotLargeEnough", aobj1);
            throw new IllegalArgumentException("NotLargeEnough");
        }
        l_ma_lags = (int[]) ai.clone();
        l_init_malag = 0;
        l_mutual_ls++;
    }

    public void setARLags(int ai[])
    {
        if (ai.length != l_p)
        {
            Object aobj[] =
            {
                    "arLags", new Integer(ai.length), new Integer(l_p)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj);
            throw new IllegalArgumentException("NotEqual");
        }
        if (ai[BLAS.iimin(ai.length, ai, 0, 1) - 1] <= 0)
        {
            Object aobj1[] =
            {
                    "arLags[" + (BLAS.iimin(ai.length, ai, 0, 1) - 1) + "]",
                    new Integer(ai[BLAS.iimin(ai.length, ai, 0, 1) - 1]), new Integer(0)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotLargeEnough", aobj1);
            throw new IllegalArgumentException("NotLargeEnough");
        }
        l_ar_lags = (int[]) ai.clone();
        l_init_arlag = 0;
        l_mutual_ls++;
    }

    public void setMaxIterations(int i)
    {
        if (i < 0)
        {
            Object aobj[] =
            {
                    "iterations", new Integer(i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotPositive", aobj);
            throw new IllegalArgumentException("NotPositive");
        }
        l_max_iterations = i;
        l_init_max_iter = 1;
    }

    public void setCenter(boolean flag)
    {
        if (!flag)
        {
            l_center = 0;
        }
        else
        {
            l_center = 1;
        }
        l_mutual_ls++;
    }

    public void setMethod(int i)
    {
        if (i < 0 || i > 1)
        {
            Object aobj[] =
            {
                    "method", new Integer(i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "CannotBe", aobj);
            throw new IllegalArgumentException("CannotBe");
        }
        if (i == 0)
        {
            l_method = i;
            l_mutual_est++;
        }
        else
        {
            l_method = i;
        }
        if (l_mutual_est > 1) // Messages.throwIllegalArgumentException("com.imsl.stat",
                              // "ARMA.MethodsInconsistent", null);
        {
            throw new IllegalArgumentException("ARMA.MethodsInconsistent");
        }
    }

    public Matrix getParamEstimatesCovariance()
    {
        int i = 0;
        l_mutual_ls++;
        if (l_mutual_ls >= 1 && l_method == 0) // Messages.throwIllegalArgumentException("com.imsl.stat",
                                               // "ARMA.MethodsInconsistent",
                                               // null);
        {
            throw new IllegalArgumentException("ARMA.MethodsInconsistent");
        }
        if (l_center == 1)
        {
            i = 0;
        }
        if (l_center == 0)
        {
            i = 1;
        }
        int j = i + l_p + l_q;
        if (l_param_est_cov == null)
        {
            // return (double[][]) null;
            Matrix numMat = null;
            return numMat;
        }
        double ad[][] = new double[j][j];
        for (int k = 0; k < j; k++)
        {
            System.arraycopy(l_param_est_cov, k * j, ad[k], 0, j);
        }

        return new Matrix(ad);
    }

    public void setConfidence(double d)
    {
        if (d <= 0.0D || d >= 1.0D)
        {
            Object aobj[] =
            {
                    "confidence", new Double(d), new Double(0.0D), new Double(1.0D)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "GreaterLess", aobj);
            throw new IllegalArgumentException("GreaterLess");
        }
        d *= 100D;
        l_confidence = d;
    }

    public void setBackwardOrigin(int i)
    {
        int j = 0;
        if (i < 0)
        {
            Object aobj[] =
            {
                    "backwardOrigin", new Integer(i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotPositive", aobj);
            throw new IllegalArgumentException("NotPositive");
        }
        if (l_p >= 1)
        {
            j = Math.max(j, arma_info.ar_lags[BLAS.iimax(arma_info.p, arma_info.ar_lags, 0, 1) - 1]);
        }
        if (l_q >= 1)
        {
            j = Math.max(j, arma_info.ma_lags[BLAS.iimax(arma_info.q, arma_info.ma_lags, 0, 1) - 1]);
        }
        if (i > arma_info.n_observations - j)
        {
            Object aobj1[] =
            {
                    "backwardOrigin", new Integer(i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "CannotBe", aobj1);
            throw new IllegalArgumentException("CannotBe");
        }
        l_backward_origin = i;
    }

    public Matrix getDeviations()
    {
        if (l_dev == null)
        {
            return null;
        }
        else
        {
            return new Matrix((double[]) l_dev.clone());
        }
    }

    public Matrix getPsiWeights()
    {
        if (l_weight == null)
        {
            return null;
        }
        else
        {
            return new Matrix((double[]) l_weight.clone());
        }
    }

    private static void ArmaEx1()
    {
        double[] z =
        {
                100.8, 81.6, 66.5, 34.8, 30.6, 7, 19.8, 92.5, 154.4, 125.9, 84.8, 68.1, 38.5, 22.8, 10.2, 24.1, 82.9,
                132, 130.9, 118.1, 89.9, 66.6, 60, 46.9, 41, 21.3, 16, 6.4, 4.1, 6.8, 14.5, 34, 45, 43.1, 47.5, 42.2,
                28.1, 10.1, 8.1, 2.5, 0, 1.4, 5, 12.2, 13.9, 35.4, 45.8, 41.1, 30.4, 23.9, 15.7, 6.6, 4, 1.8, 8.5,
                16.6, 36.3, 49.7, 62.5, 67, 71, 47.8, 27.5, 8.5, 13.2, 56.9, 121.5, 138.3, 103.2, 85.8, 63.2, 36.8,
                24.2, 10.7, 15, 40.1, 61.5, 98.5, 124.3, 95.9, 66.5, 64.5, 54.2, 39, 20.6, 6.7, 4.3, 22.8, 54.8, 93.8,
                95.7, 77.2, 59.1, 44, 47, 30.5, 16.3, 7.3, 37.3, 73.9
        };

        ARMA arma = new ARMA(2, 1, z);
        arma.setRelativeError(0.0);
        arma.setMaxIterations(0);
        arma.compute();

        Matrix Ar = arma.getAR();
        Ar.printInLabel("AR");
        Matrix Ma = arma.getMA();
        Ma.printInLabel("MA");
    }

    private static void ArmaEx2()
    {
        /* sunspots from 1770 to 1869 */
        double[] z =
        {
                100.8, 81.6, 66.5, 34.8, 30.6, 7, 19.8, 92.5, 154.4, 125.9, 84.8, 68.1, 38.5, 22.8, 10.2, 24.1, 82.9,
                132, 130.9, 118.1, 89.9, 66.6, 60, 46.9, 41, 21.3, 16, 6.4, 4.1, 6.8, 14.5, 34, 45, 43.1, 47.5, 42.2,
                28.1, 10.1, 8.1, 2.5, 0, 1.4, 5, 12.2, 13.9, 35.4, 45.8, 41.1, 30.4, 23.9, 15.7, 6.6, 4, 1.8, 8.5,
                16.6, 36.3, 49.7, 62.5, 67, 71, 47.8, 27.5, 8.5, 13.2, 56.9, 121.5, 138.3, 103.2, 85.8, 63.2, 36.8,
                24.2, 10.7, 15, 40.1, 61.5, 98.5, 124.3, 95.9, 66.5, 64.5, 54.2, 39, 20.6, 6.7, 4.3, 22.8, 54.8, 93.8,
                95.7, 77.2, 59.1, 44, 47, 30.5, 16.3, 7.3, 37.3, 73.9
        };
        int backwardOrigin = 3;
        /*
         * double[][] printTable = new double[15][4]; double[][] printEstimates
         * = new double[1][4]; double[] forecasts; double[] deviations;
         */
        /*
         * PrintMatrixFormat pmf = new PrintMatrixFormat(); PrintMatrix pm = new
         * PrintMatrix(); NumberFormat nf = NumberFormat.getNumberInstance();
         * pm.setColumnSpacing(3);
         */

        ARMA arma = new ARMA(2, 1, z);
        arma.setRelativeError(0.0);
        arma.setMaxIterations(0);
        arma.compute();

        System.out.println("ARMA ESTIMATES");
        Matrix ar = arma.getAR();
        ar.printInLabel("ar");
        Matrix ma = arma.getMA();
        ma.printInLabel("ma");

        // printEstimates[0][0] = arma.getConstant();
        // printEstimates[0][1] = ar.get(0, 0);// [0];
        // printEstimates[0][2] = ar.get(0, 1);// [1];
        // printEstimates[0][3] = ma.get(0, 0);// [0];

        /*
         * String[] estimateLabels = { "Constant", "AR(1)", "AR(2)", "MA(1)" };
         */

        /*
         * pmf.setColumnLabels(estimateLabels); nf.setMinimumFractionDigits(5);
         * nf.setMaximumFractionDigits(5); pmf.setNumberFormat(nf);
         * pm.setTitle("ARMA ESTIMATES"); pm.print(pmf, printEstimates);
         */

        // arma.setBackwardOrigin(backwardOrigin);

        /*
         * String[] labels = { "From 1866", "From 1867", "From 1868",
         * "From 1869" };
         */

        /*
         * pmf.setColumnLabels(labels); pmf.setFirstRowNumber(1);
         * nf.setMinimumFractionDigits(1); nf.setMaximumFractionDigits(1);
         * pmf.setNumberFormat(nf); pm.setTitle("FORECASTS"); pm.print(pmf,
         * arma.forecast(5));
         */
        Matrix armaFore = arma.forecast(5);
        armaFore.printInLabel("forecast (5)");

        /*
         * FORECASTING - An example of forecasting using the ARMA estimates In
         * this case, forecasts are returned for the last 10 values in the
         * series followed by the forecasts for the next 5 values.
         */
        /*
         * String[] forecastLabels={"Observed", "Forecast", "Residual",
         * "UCL(90%)"}; pmf.setColumnLabels(forecastLabels); backwardOrigin =
         * 10; arma.setBackwardOrigin(backwardOrigin); int n_forecast = 5;
         * arma.setConfidence(0.9); forecasts = arma.getForecast(n_forecast);
         * deviations = arma.getDeviations(); for(int i=0; i<backwardOrigin;
         * i++){ printTable[i][0] = z[z.length-backwardOrigin+i];
         * printTable[i][1] = forecasts[i]; printTable[i][2] =
         * z[z.length-backwardOrigin+i]-forecasts[i]; printTable[i][3] =
         * forecasts[i] + deviations[0]; } for(int i=backwardOrigin;
         * i<n_forecast+backwardOrigin; i++){ printTable[i][0] = Double.NaN;
         * printTable[i][1] = forecasts[i]; printTable[i][2] = Double.NaN;
         * printTable[i][3] = forecasts[i] + deviations[i-backwardOrigin]; }
         */

        // pmf.setFirstRowNumber(1869-backwardOrigin+1);
        // pm.setTitle("ARMA ONE-STEP AHEAD FORECASTS");
        // pm.print(pmf, printTable);

    }

    static Matrix getSunspotData()
    {
        double[] z =
        {
                100.8, 81.6, 66.5, 34.8, 30.6, 7, 19.8, 92.5, 154.4, 125.9, 84.8, 68.1, 38.5, 22.8, 10.2, 24.1, 82.9,
                132, 130.9, 118.1, 89.9, 66.6, 60, 46.9, 41, 21.3, 16, 6.4, 4.1, 6.8, 14.5, 34, 45, 43.1, 47.5, 42.2,
                28.1, 10.1, 8.1, 2.5, 0, 1.4, 5, 12.2, 13.9, 35.4, 45.8, 41.1, 30.4, 23.9, 15.7, 6.6, 4, 1.8, 8.5,
                16.6, 36.3, 49.7, 62.5, 67, 71, 47.8, 27.5, 8.5, 13.2, 56.9, 121.5, 138.3, 103.2, 85.8, 63.2, 36.8,
                24.2, 10.7, 15, 40.1, 61.5, 98.5, 124.3, 95.9, 66.5, 64.5, 54.2, 39, 20.6, 6.7, 4.3, 22.8, 54.8, 93.8,
                95.7, 77.2, 59.1, 44, 47, 30.5, 16.3, 7.3, 37.3, 73.9
        };
        return new Matrix(z);
    }

    static void ArmaEx3ParamSearch()
    {
        Matrix data = getSunspotData();

        int maxAr = 6;
        int maxMa = 6;

        List<Matrix> resList = new ArrayList<Matrix>();
        Matrix matRes = null;

        for (int i = 1; i < maxAr; i++)
        {
            for (int j = 1; j < maxMa; j++)
            {
                ARMA arma = new ARMA(i, j, data);
                // arma.setRelativeError(0.0);
                // arma.setMaxIterations(0);
                // arma.setMethod(ARMA.LEAST_SQUARES);
                arma.compute();

                System.out.println("ARMA ESTIMATES");
                Matrix ar = arma.getAR();
                Matrix ma = arma.getMA();

                double res = arma.getResidual().sumAll();
                double[] orderRes =
                {
                        i, j, res
                };

                Matrix matOrderRes = new Matrix(orderRes);

                if (matRes == null)
                {
                    matRes = matOrderRes;
                }
                else
                {
                    matRes = matRes.mergeV(matOrderRes);
                }

                /*
                 * printEstimates[0][0] = arma.getConstant();
                 * printEstimates[0][1] = ar.get(0, 0);// [0];
                 * printEstimates[0][2] = ar.get(0, 1);// [1];
                 * printEstimates[0][3] = ma.get(0, 0);
                 */

                System.out.println("AR = " + i + " ,  MA = " + j);
            }
        }

        matRes.printInLabel("matRes");

    }

    public static void main(String args[]) throws Exception
    {
        // ArmaEx3ParamSearch();
        ArmaEx2();
    }
}// --------------------------- End Class Definition
// ----------------------------
