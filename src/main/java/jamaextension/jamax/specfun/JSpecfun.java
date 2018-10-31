package jamaextension.jamax.specfun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elfun.JElfun;

public final class JSpecfun
{

    // Series for the interval [0,0.01]
    private static final double R9LGMC_COEF[] =
    {
            .166638948045186324720572965082e0, -.138494817606756384073298605914e-4, .981082564692472942615717154749e-8,
            -.180912947557249419426330626672e-10, .622109804189260522712601554342e-13,
            -.339961500541772194430333059967e-15, .268318199848269874895753884667e-17
    };
    // Series on the interval [0,1]
    private static final double GAMMA_COEF[] =
    {
            .8571195590989331421920062399942e-2, .4415381324841006757191315771652e-2,
            .5685043681599363378632664588789e-1, -.4219835396418560501012500186624e-2,
            .1326808181212460220584006796352e-2, -.1893024529798880432523947023886e-3,
            .3606925327441245256578082217225e-4, -.6056761904460864218485548290365e-5,
            .1055829546302283344731823509093e-5, -.1811967365542384048291855891166e-6,
            .3117724964715322277790254593169e-7, -.5354219639019687140874081024347e-8,
            .9193275519859588946887786825940e-9, -.1577941280288339761767423273953e-9,
            .2707980622934954543266540433089e-10, -.4646818653825730144081661058933e-11,
            .7973350192007419656460767175359e-12, -.1368078209830916025799499172309e-12,
            .2347319486563800657233471771688e-13, -.4027432614949066932766570534699e-14,
            .6910051747372100912138336975257e-15, -.1185584500221992907052387126192e-15,
            .2034148542496373955201026051932e-16, -.3490054341717405849274012949108e-17,
            .5987993856485305567135051066026e-18, -.1027378057872228074490069778431e-18
    };
    // limits for switching algorithm in digamma
    /**
     * C limit.
     */
    private static final double C_LIMIT = 49;
    /**
     * S limit.
     */
    private static final double S_LIMIT = 1e-5;

    private JSpecfun()
    {
    }

    /**
     * ERFC Complementary error function. Y = ERFC(X) is the complementary error
     * function for each element of X. X must be real. The complementary error
     * function is defined as:
     * 
     * erfc(x) = 2/sqrt(pi) * integral from x to inf of exp(-t^2) dt. = 1 -
     * erf(x).
     */
    public static double erfc(double x)
    {
        return erfcore(x, 1);
    }// end method

    /**
     * ERFCX Scaled complementary error function. Y = ERFCX(X) is the scaled
     * complementary error function for each element of X. X must be real. The
     * scaled complementary error function is defined as:
     * 
     * erfcx(x) = exp(x^2) * erfc(x)
     * 
     * which is approximately (1/sqrt(pi)) * 1/x for large x.
     */
    public static double erfcx(double x)
    {
        return erfcore(x, 2);
    }// end method

    /**
     * ERFCORE Core algorithm for error functions. erf(x) = erfcore(x,0) erfc(x)
     * = erfcore(x,1) erfcx(x) = exp(x^2)*erfc(x) = erfcore(x,2)
     */
    public static double erfcore(double x, int jint)
    {
        double result = 0.0;
        double y = 0.0;
        double z = 0.0;
        double xnum = 0.0;
        double xden = 0.0;
        double del = 0.0;

        // evaluate erf for |x| <= 0.46875
        double xbreak = 0.46875;
        if (Math.abs(x) <= xbreak)
        {
            Matrix a = new Matrix(new double[][]
            {
                {
                        3.16112374387056560e00, 1.13864154151050156E02, 3.77485237685302021E02, 3.20937758913846947E03,
                        1.85777706184603153E-1
                }
            });
            Matrix b = new Matrix(new double[][]
            {
                {
                        2.36012909523441209E01, 2.44024637934444173E02, 1.28261652607737228E03, 2.84423683343917062E03
                }
            });

            y = Math.abs(x);
            z = y * y;
            xnum = a.get(0, 4) * z;
            xden = z;
            for (int i = 0; i < 3; i++)
            {
                xnum = (xnum + a.get(0, i)) * z;
                xden = (xden + b.get(0, i)) * z;
            }
            result = x * (xnum + a.get(0, 3)) / (xden + b.get(0, 3));

            if (jint != 0)
            {
                result = 1.0 - result;
            }
            if (jint == 2)
            {
                result = Math.exp(z) * result;
            }
            ;
        }

        // evaluate erfc for 0.46875 <= |x| <= 4.0
        if (Math.abs(x) > xbreak && Math.abs(x) <= 4.0)
        {
            Matrix c = new Matrix(new double[][]
            {
                {
                        5.64188496988670089E-1, 8.88314979438837594E00, 6.61191906371416295E01, 2.98635138197400131E02,
                        8.81952221241769090E02, 1.71204761263407058E03, 2.05107837782607147E03, 1.23033935479799725E03,
                        2.15311535474403846E-8
                }
            });
            Matrix d = new Matrix(new double[][]
            {
                {
                        1.57449261107098347E01, 1.17693950891312499E02, 5.37181101862009858E02, 1.62138957456669019E03,
                        3.29079923573345963E03, 4.36261909014324716E03, 3.43936767414372164E03, 1.23033935480374942E03
                }
            });

            y = Math.abs(x);
            xnum = c.get(0, 8) * y;
            xden = y;
            for (int i = 0; i < 7; i++)
            {
                xnum = (xnum + c.get(0, i)) * y;
                xden = (xden + d.get(0, i)) * y;
            }
            result = (xnum + c.get(0, 7)) / (xden + d.get(0, 7));
            if (jint != 2)
            {
                z = MathUtil.fix(y * 16.0) / 16.0;
                del = (y - z) * (y + z);
                result = Math.exp(-z * z) * Math.exp(-del) * result;
            }
        }

        // evaluate erfc for |x| > 4.0
        if (Math.abs(x) > 4.0)
        {
            Matrix p = new Matrix(new double[][]
            {
                {
                        3.05326634961232344e-1, 3.60344899949804439e-1, 1.25781726111229246e-1, 1.60837851487422766e-2,
                        6.58749161529837803e-4, 1.63153871373020978e-2
                }
            });
            Matrix q = new Matrix(new double[][]
            {
                {
                        2.56852019228982242e00, 1.87295284992346047e00, 5.27905102951428412e-1, 6.05183413124413191e-2,
                        2.33520497626869185e-3
                }
            });

            y = Math.abs(x);
            z = 1.0 / (y * y);
            xnum = p.get(0, 5) * z;
            xden = z;
            for (int i = 0; i < 4; i++)
            {
                xnum = (xnum + p.get(0, i)) * z;
                xden = (xden + q.get(0, i)) * z;
            }
            result = z * (xnum + p.get(0, 4)) / (xden + q.get(0, 4));
            result = (1.0 / Math.sqrt(Math.PI) - result) / y;
            if (jint != 2)
            {
                z = MathUtil.fix(y * 16.0) / 16.0;
                del = (y - z) * (y + z);
                result = Math.exp(-z * z) * Math.exp(-del) * result;
                if (Double.isInfinite(result) || Double.isNaN(result))
                {
                    result = 0.0;
                }
            }
        }

        // fix up for negative argument, erf, etc.
        if (jint == 0)
        {
            if (x > xbreak)
            {
                result = 1.0 - result;
            }
            if (x < -xbreak)
            {
                result = -1.0 + result;
            }
        }
        else if (jint == 1)
        {
            if (x < -xbreak)
            {
                result = 2.0 - result;
            }
        }
        else
        { // jint must = 2
            if (x < -xbreak)
            {
                z = MathUtil.fix(x * 16.0) / 16.0;
                del = (x - z) * (x + z);
                y = Math.exp(z * z) * Math.exp(del);
                result = (y + y) - result;
            }
        }

        return result;
    }// end method

    /**
     * ERFINV Inverse error function. X = ERFINV(Y) is the inverse error
     * function for each element of Y. The inverse error function satisfies y =
     * erf(x), for -1 <= y <= 1 and -inf <= x <= inf.
     */
    public static double erfinv(double y)
    {
        // Coefficients in rational approximations.
        double[] a = new double[]
        {
                0.886226899, -1.645349621, 0.914624893, -0.140543331
        };
        double[] b = new double[]
        {
                -2.118377725, 1.442710462, -0.329097515, 0.012229801
        };
        double[] c = new double[]
        {
                -1.970840454, -1.624906493, 3.429567803, 1.641345311
        };
        double[] d = new double[]
        {
                3.543889200, 1.637067800
        };

        double y0 = 0.7;
        double z = 0.0, x = 0.0;

        // Central range.
        if (Math.abs(y) <= y0)
        {
            z = y * y;
            x = y * (((a[3] * z + a[2]) * z + a[1]) * z + a[0]) / ((((b[3] * z + b[2]) * z + b[1]) * z + b[0]) * z + 1);
        }

        // Near end points of range.
        if (y0 < y && y < 1.0)
        {
            z = Math.sqrt(-Math.log((1.0 - y) / 2.0));
            x = (((c[3] * z + c[2]) * z + c[1]) * z + c[0]) / ((d[1] * z + d[0]) * z + 1);
        }
        if ((-y0 > y) && (y > -1.0))
        {
            z = Math.sqrt(-Math.log((1.0 + y) / 2.0));
            x = -(((c[3] * z + c[2]) * z + c[1]) * z + c[0]) / ((d[1] * z + d[0]) * z + 1);
        }
        // System.out.println("0) x = "+x);
        // Two steps of Newton-Raphson correction to full accuracy.
        x = x - (erf(x) - y) / (2.0 / Math.sqrt(Math.PI) * Math.exp(-x * x)); // System.out.println("1) x = "+x);
        x = x - (erf(x) - y) / (2.0 / Math.sqrt(Math.PI) * Math.exp(-x * x)); // System.out.println("2) x = "+x);

        // Exceptional cases
        if (y == -1.0)
        {
            x = Double.NEGATIVE_INFINITY;
        }
        else if (y == 1.0)
        {
            x = Double.POSITIVE_INFINITY;
        }
        else if (Math.abs(y) > 1.0)
        {
            x = Double.NaN;
        }

        return x;
    }// end method

    /**
     * ERF Error function. Y = ERF(X) is the error function for each element of
     * X. X must be real. The error function is defined as:
     * 
     * erf(x) = 2/sqrt(pi) * integral from 0 to x of exp(-t^2) dt.
     */
    public static double erf(double x)
    {
        return erfcore(x, 0);
    }// end method

    /**
     * BETA Beta function. Y = BETA(Z,W) computes the beta function for
     * corresponding elements of Z and W. The beta function is defined as
     * 
     * beta(z,w) = integral from 0 to 1 of t.^(z-1) .* (1-t).^(w-1) dt.
     */
    public static double beta(double z, double w)
    {
        return Math.exp(betaln(z, w));
    }// end method

    /**
     * BETALN Logarithm of beta function. Y = BETALN(Z,W) computes the natural
     * logarithm of the beta function for corresponding elements of Z and W. The
     * arrays Z and W must be the same size (or either can be scalar). BETALN is
     * defined as:
     * 
     * BETALN = LOG(BETA(Z,W))
     * 
     * and is obtained without computing BETA(Z,W). Since the beta function can
     * range over very large or very small values, its logarithm is sometimes
     * more useful.
     */
    public static double betaln(double z, double w)
    {
        return (gammaln(z) + gammaln(w) - gammaln(z + w));
    }

    /**
     * BETAINC Incomplete beta function. Y = BETAINC(X,Z,W) computes the
     * incomplete beta function for corresponding elements of X, Z, and W. The
     * elements of X must be in the closed interval [0,1]. The arguments X, Z
     * and W must all be the same size (or any of them can be scalar).
     * 
     * The incomplete beta function is defined as
     * 
     * I_x(z,b) = 1./BETA(z,w) .* integral from 0 to x of t.^(z-1) .*
     * (1-t).^(w-1) dt
     */
    public static double betaincOld(double x, double a, double b)
    {
        double y = 0.0;
        if (x < 0.0 || x > 1.0)
        {
            throw new IllegalArgumentException("betainc : Input parameter \"x\" = \"" + x
                    + "\" must be in the interval [0 1] :");
        }
        double bt = Math.exp(gammaln(a + b) - gammaln(a) - gammaln(b) + a * Math.log(x + (x == 0.0 ? 1.0 : 0.0)) + b
                * Math.log(1 - x + (x == 1.0 ? 1.0 : 0.0)));
        if (x < ((a + 1.0) / (a + b + 2.0)))
        {
            y = bt * betacore(x, a, b) / a;
        }
        if (x >= ((a + 1.0) / (a + b + 2.0)))
        {
            y = 1.0 - bt * betacore(1.0 - x, b, a) / b;
        }
        if (x == 0.0)
        {
            y = 0.0;
        }
        if (x == 1.0)
        {
            y = 1.0;
        }
        return y;
    }// end method

    public static double betainc(double x, double a, double b)
    {
        return betainc(x, a, b, true);
    }

    public static double betainc(double x, double a, double b, boolean lower)
    {
        double y = 0.0;

        boolean cond = x < 0.0 || x > 1.0 || Double.isNaN(x);
        if (cond)
        {
            throw new IllegalArgumentException("betainc : Input parameter \"x\" = \"" + x
                    + "\" must be in the interval [0 1] :");
        }
        cond = a < 0.0 || Double.isNaN(a);
        if (cond)
        {
            throw new IllegalArgumentException("betainc : Input parameter \"a\" = \"" + a + "\" must be non-negative :");
        }
        cond = b < 0.0 || Double.isNaN(b);
        if (cond)
        {
            throw new IllegalArgumentException("betainc : Input parameter \"b\" = \"" + b + "\" must be non-negative :");
        }

        y = x + a + b;

        if (lower)
        {
            y = (x == 1.0 ? 1.0 : 0.0);
        }
        else
        {
            y = (x == 0.0 ? 1.0 : 0.0);
        }// end

        // ----------------------------------------------------------------------
        // Use the continued fraction unless either parameter is very large.
        boolean approx = (a + b) > 1e7;
        // if isscalar(approx) && ~isscalar(y)
        // k = repmat(~approx,size(y));
        // else
        boolean k = !approx;
        // end
        double temp = 0.0;
        double omx = 1.0 - x; // one minus x
        if (k && !lower)
        {
            // in the rows indexed by k, swap a for b, x for 1-x
            // if isscalar(a), a = repmat(a,size(y)); end
            // if isscalar(b), b = repmat(b,size(y)); end
            // if isscalar(x)
            omx = x;
            x = 1 - x;
            // else
            // omx(k) = x(k);
            // x(k) = 1-x(k);
            // end
            temp = a;
            a = b;
            b = temp;
            // temp = []; //save space, no longer used
        }// end

        k = (0 < x & x < (a + 1) / (a + b + 2) && !approx);
        if (k)
        {
            // if isscalar(x), xk = x; else xk = x(k); end
            // if isscalar(a), ak = a; else ak = a(k); end
            // if isscalar(b), bk = b; else bk = b(k); end
            // % This is x^a * (1-x)^b / (a*beta(a,b)), computed so that a==0
            // works.
            double btk = Math.exp(gammaln(a + b) - gammaln(a + 1) - gammaln(b) + a * Math.log(x) + b * Math.log1p(-x));
            y = btk * betacoreInc(x, a, b);
        }// end

        k = ((a + 1) / (a + b + 2) <= x && omx > 0 && !approx);
        if (k)
        {
            // if isscalar(x), xk = x; omxk=omx; else xk = x(k);omxk=omx(k); end
            // if isscalar(a), ak = a; else ak = a(k); end
            // if isscalar(b), bk = b; else bk = b(k); end
            // This is x^a * (1-x)^b / (b*beta(a,b)), computed so that b==0
            // works.
            double ab = gammaln(a + b);
            double btk = Math.exp(ab - gammaln(a) - gammaln(b + 1) + a * Math.log(x) + b * Math.log1p(-x));
            y = 1.0 - btk * betacoreInc(omx, b, a);

        }// end

        // % NaNs may have come from a=b=0, leave those alone. Otherwise if the
        // % continued fraction in betacore failed to converge, or if we didn't
        // use
        // % it, use approximations.
        k = (Double.isNaN(y) && (a + b > 0)) || approx;
        if (k)
        {
            // if isscalar(x), xk = x; else xk = x(k); end
            // if isscalar(a), ak = a; else ak = a(k); end
            // if isscalar(b), bk = b; else bk = b(k); end
            double w1 = Math.pow((b * x), (1.0 / 3.0));
            double w2 = Math.pow((a * (1 - x)), (1.0 / 3.0));
            double sgn = -1;

            if (lower)
            {
                sgn = +1;
            }// else
             // sgn=-1;
             // end
            y = 0.5 * erfc(-sgn * 3.0 / Math.sqrt(2.0) * ((1.0 - 1.0 / (9.0 * b)) * w1 - (1.0 - 1.0 / (9.0 * a)) * w2)
                    / Math.sqrt(Math.pow(w1, 2.0) / b + Math.pow(w2, 2.0) / a));

            boolean k1 = ((a + b - 1.0) * (1.0 - x) <= 0.8);
            if (k1)
            {
                // if isscalar(x), xk = x; else xk = xk(k1); end
                // if isscalar(a), ak = a; else ak = ak(k1); end
                // if isscalar(b), bk = b; else bk = bk(k1); end

                double s = 0.5 * ((a + b - 1.0) * (3.0 - x) - (b - 1.0)) * (1.0 - x);
                if (lower)
                {
                    y = 1.0 - gammainc(s, b);// ,'upper');
                }
                else
                {
                    y = gammainc(s, b);// ,'lower'); ===>>> default
                }// end
            }// end
        }// end
         // ----------------------------------------------------------------------

        return y;
    }

    private static double betacoreInc(double x, double a, double b)
    {
        double y = 0.0;
        double aplusb = a + b;
        double aplus1 = a + 1.0;
        double aminus1 = a - 1.0;
        double C = 1.0;
        // When called from BETAINC, Dinv can never be zero unless (a+b) or
        // (a+1)
        // round to a.
        double Dinv = 1.0 - aplusb * x / aplus1;
        y = C / Dinv;
        int maxiter = 1000;
        int m = 0;
        for (m = 1; m <= maxiter; m++)
        {
            double yold = y;
            double twom = 2.0 * m;
            double d = m * (b - m) * x / ((aminus1 + twom) * (a + twom));
            C = 1.0 + d / C;
            // Using Dinv, not D, ensures that C = 1/D will be a stable fixed
            // point
            Dinv = 1.0 + d / Dinv;
            y = y * (C / Dinv);
            d = -(a + m) * (aplusb + m) * x / ((a + twom) * (aplus1 + twom));
            C = 1.0 + d / C;
            Dinv = 1.0 + d / Dinv;
            y = y * (C / Dinv);
            // System.out.println("betacore : EPS in the following line must have an argument.");
            boolean k = Math.abs(y - yold) > 1000.0 * EPS(y);// eps(y(:));

            if (!k)
            {
                break;
            }// end
        }// end
        if (m > maxiter)
        {
            y = Double.NaN;
        }// end
        return y;
    }

    /**
     * BETACORE Core algorithm for the incomplete beta function. BETACORE(x,a,b)
     * is used twice by BETAINC(X,A,B). Returns NaN if continued fraction does
     * not converge.
     * 
     * @deprecated Use method <B>betainc</B> instead.
     */
    public static double betacore(double x, double a, double b)
    {
        double y = x;
        double qab = a + b;
        double qap = a + 1.0;
        double qam = a - 1.0;
        double am = 1.0;
        double bm = am;

        y = am;

        double bz = 1.0 - qab * x / qap;
        double d = 0.0;
        double app = d;
        double ap = d;
        double bpp = d;
        double bp = d;
        double yold = d;
        double m = 1.0;
        double tem = 0.0;

        while (Math.abs(y - yold) > (4.0 * MathUtil.EPS * Math.abs(y)))
        {
            tem = 2 * m;
            d = m * (b - m) * x / ((qam + tem) * (a + tem));
            ap = y + d * am;
            bp = bz + d * bm;
            d = -(a + m) * (qab + m) * x / ((a + tem) * (qap + tem));
            app = ap + d * y;
            bpp = bp + d * bz;
            yold = y;
            am = ap / bpp;
            bm = bp / bpp;
            y = app / bpp;

            if (m == 1.0)
            {
                bz = 1.0; // only need to do this first time through
            }
            m = m + 1.0;
        }
        return y;
    }// end method

    /**
     * GAMMAINC Incomplete gamma function. Y = GAMMAINC(X,A) evaluates the
     * incomplete gamma function for corresponding elements of X and A. X and A
     * must be real and the same size (or either can be a scalar). A must also
     * be non-negative. The incomplete gamma function is defined as:
     * 
     * gammainc(x,a) = 1 ./ gamma(a) .* integral from 0 to x of t^(a-1) exp(-t)
     * dt
     * 
     * For any a>=0, as x approaches infinity, gammainc(x,a) approaches 1. For
     * small x and a, gammainc(x,a) ~= x^a, so gammainc(0,0) = 1.
     */
    public static double gammainc(double x, double a)
    {
        double b = 0.0;
        double gam = gammaln(a + Double.MIN_VALUE);
        if (x == 0.0)
        {
            b = 0.0;
            return b;
        }

        if (a == 0.0)
        {
            b = 1.0;
            return b;
        }

        double sum = 0.0, ap = 0.0, del = 0.0;

        // Series expansion for x < a+1
        if (x < (a + 1.0))
        { // System.out.println("CASE 1");
            ap = a;
            sum = 1.0 / ap;
            del = sum;
            while (del >= (10.0 * MathUtil.EPS * sum))
            {
                ap = ap + 1.0;
                del = x * del / ap;
                sum = sum + del;
            }// end while
            b = sum * Math.exp(-x + a * Math.log(x) - gam);
            return b;
        }

        // Continued fraction for x >= a+1
        if (x >= (a + 1.0))
        { // System.out.println("CASE 2");
            double a0 = 1.0;
            double a1 = x;
            double b0 = 0.0;
            double b1 = a0;
            double fac = 1.0;
            double n = 1.0;
            double g = b1;
            double gold = b0;

            double ana = 0.0, anf = 0.0;

            while (Math.abs(g - gold) >= (10.0 * MathUtil.EPS * g))
            {
                gold = g;
                ana = n - a;
                a0 = (a1 + a0 * ana) * fac;
                b0 = (b1 + b0 * ana) * fac;
                anf = n * fac;
                a1 = x * a0 + anf * a1;
                b1 = x * b0 + anf * b1;
                fac = 1.0 / a1;
                g = b1 * fac;
                n = n + 1.0;
            }
            // System.out.println(" n = "+(int)n+" :   g = "+g);
            b = 1.0 - Math.exp(-x + a * Math.log(x) - gam) * g;
        }// end if
        return b;
    }// end method

    /**
     * Returns the Gamma function of a double.
     * 
     * @param x
     *            A double value.
     * @return The Gamma function of x. If x is a negative integer, the result
     *         is NaN.
     * 
     *         This method is a direct cut and paste from IMSL 'JNL' package.
     */
    public static double gamma(double x)
    {

        double ans;
        double y = Math.abs(x);

        if (y <= 10.0)
        {
            /*
             * Compute gamma(x) for |x|<=10. First reduce the interval and find
             * gamma(1+y) for 0 <= y < 1.
             */
            int n = (int) x;
            if (x < 0.0)
            {
                n--;
            }
            y = x - n;
            n--;
            ans = 0.9375 + csevl(2.0 * y - 1.0, GAMMA_COEF);
            if (n == 0)
            {
            }
            else if (n < 0)
            {
                // Compute gamma(x) for x < 1
                n = -n;
                if (x == 0.0)
                {
                    ans = Double.NaN;
                }
                else if (y < 1.0 / Double.MAX_VALUE)
                {
                    ans = Double.POSITIVE_INFINITY;
                }
                else
                {
                    double xn = n - 2;
                    if (x < 0.0 && x + xn == 0.0)
                    {
                        ans = Double.NaN;
                    }
                    else
                    {
                        for (int i = 0; i < n; i++)
                        {
                            ans /= x + i;
                        }
                    }
                }
            }
            else
            { // gamma(x) for x >= 2.0
                for (int i = 1; i <= n; i++)
                {
                    ans *= y + i;
                }
            }
        }
        else
        { // gamma(x) for |x| > 10
            if (x > 171.614)
            {
                ans = Double.POSITIVE_INFINITY;
            }
            else if (x < -170.56)
            {
                ans = 0.0; // underflows
            }
            else
            {
                // 0.9189385332046727 = 0.5*log(2*PI)
                ans = Math.exp((y - 0.5) * Math.log(y) - y + 0.9189385332046727 + r9lgmc(y));
                if (x < 0.0)
                {
                    double sinpiy = Math.sin(Math.PI * y);
                    if (sinpiy == 0 || Math.round(y) == y)
                    {
                        ans = Double.NaN;
                    }
                    else
                    {
                        ans = -Math.PI / (y * sinpiy * ans);
                    }
                }
            }
        }
        return ans;
    }// end method

    /*
     * Evaluate a Chebyschev series.
     * 
     * This method is a direct cut and paste from IMSL 'JNL' package.
     */
    private static double csevl(double x, double coef[])
    {
        double b0, b1, b2, twox;
        int i;
        b1 = 0.0;
        b0 = 0.0;
        b2 = 0.0;
        twox = 2.0 * x;
        for (i = coef.length - 1; i >= 0; i--)
        {
            b2 = b1;
            b1 = b0;
            b0 = twox * b1 - b2 + coef[i];
        }
        return 0.5 * (b0 - b2);
    }// end method

    /*
     * Returns the log gamma correction term for argument values greater than or
     * equal to 10.0.
     */
    private static double r9lgmc(double x)
    {
        double ans;

        if (x < 10.0)
        {
            ans = Double.NaN;
        }
        else if (x < 9.490626562e+07)
        {
            // 9.490626562e+07 = 1/Math.sqrt(EPSILON_SMALL)
            double y = 10.0 / x;
            ans = csevl(2.0 * y * y - 1.0, R9LGMC_COEF) / x;
        }
        else if (x < 1.39118e+11)
        {
            // 1.39118e+11 = exp(min(log(amach(2) / 12.0), -log(12.0 *
            // amach(1))));
            // See A&S 6.1.41
            ans = 1.0 / (12.0 * x);
        }
        else
        {
            ans = 0.0; // underflows
        }
        return ans;
    }

    public static Matrix gammaln(Matrix X)
    {
        int m = X.getRowDimension();
        int n = X.getColumnDimension();
        Matrix MPSI = new Matrix(m, n);
        double[][] A = MPSI.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = gammaln(X.get(i, j));
            }
        }
        return MPSI;
    }

    public static double gammaln(double x)
    {
        double res = 1.0;

        double d1 = -5.772156649015328605195174e-1;

        double[] p1 =
        {
                4.945235359296727046734888e0, 2.018112620856775083915565e2, 2.290838373831346393026739e3,
                1.131967205903380828685045e4, 2.855724635671635335736389e4, 3.848496228443793359990269e4,
                2.637748787624195437963534e4, 7.225813979700288197698961e3
        };

        double[] q1 =
        {
                6.748212550303777196073036e1, 1.113332393857199323513008e3, 7.738757056935398733233834e3,
                2.763987074403340708898585e4, 5.499310206226157329794414e4, 6.161122180066002127833352e4,
                3.635127591501940507276287e4, 8.785536302431013170870835e3
        };

        double d2 = 4.227843350984671393993777e-1;

        double[] p2 =
        {
                4.974607845568932035012064e0, 5.424138599891070494101986e2, 1.550693864978364947665077e4,
                1.847932904445632425417223e5, 1.088204769468828767498470e6, 3.338152967987029735917223e6,
                5.106661678927352456275255e6, 3.074109054850539556250927e6
        };

        double[] q2 =
        {
                1.830328399370592604055942e2, 7.765049321445005871323047e3, 1.331903827966074194402448e5,
                1.136705821321969608938755e6, 5.267964117437946917577538e6, 1.346701454311101692290052e7,
                1.782736530353274213975932e7, 9.533095591844353613395747e6
        };

        double d4 = 1.791759469228055000094023e0;

        double[] p4 =
        {
                1.474502166059939948905062e4, 2.426813369486704502836312e6, 1.214755574045093227939592e8,
                2.663432449630976949898078e9, 2.940378956634553899906876e10, 1.702665737765398868392998e11,
                4.926125793377430887588120e11, 5.606251856223951465078242e11
        };

        double[] q4 =
        {
                2.690530175870899333379843e3, 6.393885654300092398984238e5, 4.135599930241388052042842e7,
                1.120872109616147941376570e9, 1.488613728678813811542398e10, 1.016803586272438228077304e11,
                3.417476345507377132798597e11, 4.463158187419713286462081e11
        };

        double[] c =
        {
                -1.910444077728e-03, 8.4171387781295e-04, -5.952379913043012e-04, 7.93650793500350248e-04,
                -2.777777777777681622553e-03, 8.333333333333333331554247e-02, 5.7083835261e-03
        };

        // if ~isempty(x) && (any((imag(x) ~= 0) | (x < 0)))
        // error('MATLAB:gammaln:ComplexOrNegInputs',...
        // 'Input arguments must be real and nonnegative.')
        // end

        if (x < 0)
        {
            throw new IllegalArgumentException("gammaln : Input parameter \"x\" = \"" + x
                    + "\" must be greater than or equal to zero.");
        }

        res = x;
        // %
        // % 0 <= x <= eps
        // %
        boolean k = x <= EPS(1.0);
        if (k)
        {
            res = -Math.log(x);
        }// end
         // %
         // % eps < x <= 0.5
         // %
        k = (x > EPS(1.0)) && (x <= 0.5);
        if (k)
        {
            double y = x;
            double xden = 1.0;// ones(size(k));
            double xnum = 0;
            for (int i = 0; i < 8; i++)
            {
                xnum = xnum * y + p1[i];
                xden = xden * y + q1[i];
            }// end
            res = -Math.log(y) + (y * (d1 + y * (xnum / xden)));
        }// end
         // %
         // % 0.5 < x <= 0.6796875
         // %
        k = (x > 0.5) && (x <= 0.6796875);
        if (k)
        {
            double xm1 = (x - 0.5) - 0.5;
            double xden = 1.0;// ones(size(k));
            double xnum = 0;
            for (int i = 0; i < 8; i++)
            {
                xnum = xnum * xm1 + p2[i];
                xden = xden * xm1 + q2[i];
            }// end
            res = -Math.log(x) + xm1 * (d2 + xm1 * (xnum / xden));
        }// end
         // %
         // % 0.6796875 < x <= 1.5
         // %
        k = (x > 0.6796875) && (x <= 1.5);
        if (k)
        {
            double xm1 = (x - 0.5) - 0.5;
            double xden = 1.0;// ones(size(k));
            double xnum = 0;
            for (int i = 0; i < 8; i++)
            {
                xnum = xnum * xm1 + p1[i];
                xden = xden * xm1 + q1[i];
            }// end
            res = xm1 * (d1 + xm1 * (xnum / xden));
        }// end
         // %
         // % 1.5 < x <= 4
         // %
        k = (x > 1.5) && (x <= 4);
        if (k)
        {
            double xm2 = x - 2;
            double xden = 1.0;// ones(size(k));
            double xnum = 0;
            for (int i = 0; i < 8; i++)
            {
                xnum = xnum * xm2 + p2[i];
                xden = xden * xm2 + q2[i];
            }// end
            res = xm2 * (d2 + xm2 * (xnum / xden));
        }// end
         // %
         // % 4 < x <= 12
         // %
        k = (x > 4) && (x <= 12);
        if (k)
        {
            double xm4 = x - 4;
            double xden = -1.0;// -ones(size(k));
            double xnum = 0;
            for (int i = 0; i < 8; i++)
            {
                xnum = xnum * xm4 + p4[i];
                xden = xden * xm4 + q4[i];
            }// end
            res = d4 + xm4 * (xnum / xden);
        }// end
         // %
         // % x > 12
         // %
        k = (x > 12);
        if (k)
        {
            double y = x;
            double r = c[6];
            double ysq = y * y;
            for (int i = 0; i < 6; i++)
            {
                r = r / ysq + c[i];
            }// end
            r = r / y;
            double corr = Math.log(y);
            double spi = 0.9189385332046727417803297;
            res = r + spi - 0.5 * corr + y * (corr - 1.0);
        }// end

        return res;
    }

    /**
     * GAMMALN Logarithm of gamma function. Y = GAMMALN(X) computes the natural
     * logarithm of the gamma function for each element of X. GAMMALN is defined
     * as
     * 
     * LOG(GAMMA(X))
     * 
     * and is obtained without computing GAMMA(X). Since the gamma function can
     * range over very large or very small values, its logarithm is sometimes
     * more useful.
     */
    public static double gammalnOld(double x)
    {
        if (x < 0)
        {
            throw new IllegalArgumentException("gammaln : Input parameter \"x\" = \"" + x
                    + "\" must be greater than or equal to zero.");
        }
        double y = 0.0;
        double result = 1.0;
        double xden = 0.0, xnum = 0.0, xm1 = 0.0, xm2 = 0.0, xm4 = 0.0;
        int len = 0;

        double d1 = -5.772156649015328605195174E-1;
        double[] f =
        {
                1.0, 2.0
        };
        double[] p1 =
        {
                4.945235359296727046734888E0, 2.018112620856775083915565E2, 2.290838373831346393026739E3,
                1.131967205903380828685045E4, 2.855724635671635335736389E4, 3.848496228443793359990269E4,
                2.637748787624195437963534E4, 7.225813979700288197698961E3
        };
        double[] q1 =
        {
                6.748212550303777196073036e1, 1.113332393857199323513008e3, 7.738757056935398733233834E3,
                2.763987074403340708898585E4, 5.499310206226157329794414E4, 6.161122180066002127833352E4,
                3.635127591501940507276287E4, 8.785536302431013170870835E3
        };
        double d2 = 4.227843350984671393993777E-1;
        double[] p2 =
        {
                4.974607845568932035012064E0, 5.424138599891070494101986E2, 1.550693864978364947665077E4,
                1.847932904445632425417223E5, 1.088204769468828767498470E6, 3.338152967987029735917223E6,
                5.106661678927352456275255E6, 3.074109054850539556250927E6
        };
        double[] q2 =
        {
                1.830328399370592604055942E2, 7.765049321445005871323047E3, 1.331903827966074194402448E5,
                1.136705821321969608938755E6, 5.267964117437946917577538E6, 1.346701454311101692290052E7,
                1.782736530353274213975932E7, 9.533095591844353613395747E6
        };
        double d4 = 1.791759469228055000094023E0;
        double[] p4 =
        {
                1.474502166059939948905062E4, 2.426813369486704502836312E6, 1.214755574045093227939592E8,
                2.663432449630976949898078E9, 2.940378956634553899906876E10, 1.702665737765398868392998E11,
                4.926125793377430887588120E11, 5.606251856223951465078242E11
        };
        double[] q4 =
        {
                2.690530175870899333379843E3, 6.393885654300092398984238E5, 4.135599930241388052042842E7,
                1.120872109616147941376570E9, 1.488613728678813811542398E10, 1.016803586272438228077304E11,
                3.417476345507377132798597E11, 4.463158187419713286462081E11
        };
        double[] c =
        {
                -1.910444077728E-03, 8.4171387781295E-04, -5.952379913043012E-04, 7.93650793500350248E-04,
                -2.777777777777681622553E-03, 8.333333333333333331554247E-02, 5.7083835261E-03
        };

        if (x <= MathUtil.EPS)
        { // 0 <= x <= eps
            result = -Math.log(x); // System.out.println("x = "+x);
        }
        else if ((x > MathUtil.EPS) && (x <= 0.5))
        { // eps < x <= 0.5
            y = x;
            xden = 1.0; // System.out.println("2");
            xnum = 0.0;
            len = p1.length;
            for (int i = 0; i < len; i++)
            {
                xnum = xnum * y + p1[i];
                xden = xden * y + q1[i];
            }
            result = -Math.log(y) + (y * (d1 + y * (xnum / xden)));
        }
        else if ((x > 0.5) && (x <= 0.6796875))
        { // 0.5 < x <= 0.6796875
            xm1 = x - 1.0;
            xden = 1.0;
            xnum = 0.0; // System.out.println("3");
            len = p2.length;
            for (int i = 0; i < len; i++)
            {
                xnum = xnum * xm1 + p2[i];
                xden = xden * xm1 + q2[i];
            }
            result = -Math.log(x) + xm1 * (d2 + xm1 * (xnum / xden));
        }
        else if ((x > 0.6796875) && (x <= 1.5))
        { // 0.6796875 < x <= 1.5
            xm1 = x - 1.0;
            xden = 1.0;
            xnum = 0.0;
            len = p1.length; // System.out.println("4");
            for (int i = 0; i < len; i++)
            {
                xnum = xnum * xm1 + p1[i];
                xden = xden * xm1 + q1[i];
            }
            result = xm1 * (d1 + xm1 * (xnum / xden));
        }
        else if ((x > 1.5) && (x <= 4.0))
        { // 1.5 < x <= 4
            xm2 = x - 2.0;
            xden = 1.0;
            xnum = 0.0;
            len = p2.length; // System.out.println("5");
            for (int i = 0; i < len; i++)
            {
                xnum = xnum * xm2 + p2[i];
                xden = xden * xm2 + q2[i];
            }
            result = xm2 * (d2 + xm2 * (xnum / xden));
        }
        else if ((x > 4.0) && (x <= 12.0))
        { // 4 < x <= 12
            xm4 = x - 4.0;
            xden = -1.0;
            xnum = 0.0;
            len = p4.length; // System.out.println("6");
            for (int i = 0; i < len; i++)
            {
                xnum = xnum * xm4 + p4[i];
                xden = xden * xm4 + q4[i];
            }
            result = d4 + xm4 * (xnum / xden);
        }
        else if (x > 12.0)
        { // x > 12
            y = x;
            len = c.length;
            double r = c[len - 1]; // System.out.println("7");
            double ysq = y * y;
            for (int i = 0; i < (len - 2); i++)
            {
                r = r / ysq + c[i];
            }
            r = r / y;
            double corr = Math.log(y);
            double sp = 0.9189385332046727417803297;
            result = r + sp - 0.5 * corr + y * (corr - 1.0);
        }
        return result;
    }// end method

    /**
     * PRIMES Generate list of prime numbers. PRIMES(N) is a row vector of the
     * prime numbers less than or equal to N. A prime number is one that has no
     * factors other than 1 and itself.
     */
    public static Matrix primes(int n)
    {
        if (n <= 1)
        {
            return null;
        }
        else if (n == 2)
        {
            return new Matrix(1, 1, 2.0);
        }
        else if (n == 3 || n == 4)
        {
            return new Matrix(new double[][]
            {
                {
                        2.0, 3.0
                }
            });
        }
        else if (n == 5)
        {
            return new Matrix(new double[][]
            {
                {
                        2.0, 3.0, 5.0
                }
            });
        }
        Matrix p = Matrix.linspace(1.0, (double) n, n);
        p.set(0, 0, 0.0); // System.out.println("*** p ***"); p.print(4,0);

        Matrix knn = null;
        Matrix tmp = null;
        int upperLim = (int) Math.sqrt((double) n); // System.out.println("upperLim = "+upperLim);
        Matrix K = Matrix.linspace(2.0, (double) upperLim, upperLim - 1); // System.out.println("*** kfor ***");
                                                                          // K.print(4,0);
        int len = K.length();
        int k = 0;
        int vv = 0;

        for (int i = 0; i < len; i++)
        {
            k = (int) K.get(0, i); // System.out.println(" k = "+k);
            if (p.get(0, k - 1) != 0.0)
            {
                knn = Matrix.linIncrement((double) (k * k), (double) n, (double) k).minus(1.0);
                for (int j = 0; j < knn.length(); j++)
                {
                    vv = (int) knn.get(0, j);
                    p.set(0, vv, 0.0);
                }
                // System.out.println("*** knn ***"); knn.print(4,0);
                // p.setValueAtEntries(0.0,knn);
                // //System.out.println("*** p ***"); p.print(4,0);
            }
        }

        Indices indices = p.GT(0.0).find();
        if (indices != null)
        {
            k = indices.getRowDimension();
            tmp = new Matrix(1, k);
            double dVal = 0.0;
            int[][] arrInd = indices.getArray();
            for (int i = 0; i < k; i++)
            {
                dVal = p.get(arrInd[i][0], arrInd[i][1]);
                tmp.set(0, i, dVal);
            }
            p = tmp;
        }

        return p;
    }// end method

    /**
     * ISPRIME True for prime numbers. ISPRIME(X) is 'true' if X which is prime,
     * 'false' otherwise.
     */
    public static boolean isPrime(int n)
    {
        boolean rVal = false;
        if (n <= 1)
        {
            return false;
        }
        Matrix prm = primes(n);
        int len = prm.length();
        for (int i = 0; i < len; i++)
        {
            if (prm.get(0, i) == (double) n)
            {
                rVal = true;
                break;
            }
        }
        return rVal;
    }// end method

    public static Indices isPrime(Matrix mat)
    {
        int m = mat.getRowDimension();
        int n = mat.getColumnDimension();
        Indices indices = new Indices(m, n);
        indices.setLogical(true);
        int[][] X = indices.getArray();
        int ind = 0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                ind = (int) mat.get(i, j);
                if (isPrime(ind))
                {
                    X[i][j] = 1;
                }
            }
        }
        return indices;
    }

    /**
     * GCD Greatest common divisor. G = GCD(A,B) is the greatest common divisor
     * of corresponding elements of A and B. The arrays A and B must contain
     * non-negative integers and must be the same size (or either can be
     * scalar). GCD(0,0) is 0 by convention; all other GCDs are positive
     * integers.
     * 
     * [G,C,D] = GCD(A,B) also returns C and D so that G = A.*C + B.*D. These
     * are useful for solving Diophantine equations and computing Hermite
     * transformations.
     * 
     * See also LCM.
     */
    public static Matrix gcd(int a, int b)
    {
        Matrix gcd = new Matrix(1, 3);
        Matrix u = new Matrix(new double[][]
        {
            {
                    1.0, 0.0, Math.abs((double) a)
            }
        });
        Matrix v = new Matrix(new double[][]
        {
            {
                    0.0, 1.0, Math.abs((double) b)
            }
        });
        Matrix t = null;
        double q = 0.0;
        while (v.get(0, 2) != 0.0)
        {
            q = Math.floor(u.get(0, 2) / v.get(0, 2));
            t = u.minus(v.arrayTimes(q));
            u = v.copy();
            v = t;
        }// end while

        gcd.set(0, 0, u.get(0, 2)); // g
        gcd.set(0, 1, u.get(0, 0) * JElfun.sign(a)); // c
        gcd.set(0, 2, u.get(0, 1) * JElfun.sign(b)); // c

        return gcd;
    }// end method

    public static Matrix mpsi(Matrix X)
    {
        int m = X.getRowDimension();
        int n = X.getColumnDimension();
        Matrix MPSI = new Matrix(m, n);
        double[][] A = MPSI.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = mpsi(0, X.get(i, j));
            }
        }
        return MPSI;
    }

    public static Matrix mpsi(int k, Matrix X)
    {
        int m = X.getRowDimension();
        int n = X.getColumnDimension();
        Matrix MPSI = new Matrix(m, n);
        double[][] A = MPSI.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = mpsi(k, X.get(i, j));
            }
        }
        return MPSI;
    }

    /**
     * Computes the trigamma function of x. This function is derived by taking
     * the derivative of the implementation of digamma.
     * 
     * @param x
     *            Argument.
     * @return trigamma(x) to within 10-8 relative or absolute error whichever
     *         is smaller
     * @see <a
     *      href="http://en.wikipedia.org/wiki/Trigamma_function">Trigamma</a>
     * @see Gamma#digamma(double)
     * @since 2.0
     */
    public static double trigamma(double x)
    {
        if (x > 0 && x <= S_LIMIT)
        {
            return 1 / (x * x);
        }

        if (x >= C_LIMIT)
        {
            double inv = 1 / (x * x);
            // 1 1 1 1 1
            // - + ---- + ---- - ----- + -----
            // x 2 3 5 7
            // 2 x 6 x 30 x 42 x
            return 1 / x + inv / 2 + inv / x * (1.0 / 6 - inv * (1.0 / 30 + inv / 42));
        }

        return trigamma(x + 1) + 1 / (x * x);
    }

    public static double mpsi(int i, double x)
    {
        double ps = 0.0;
        if (i == 1)
        {
            ps = trigamma(x);
        }
        else
        {
            ps = mpsi(x);
        }
        return ps;
    }

    public static double mpsi(double x)
    {
        double xa = Math.abs(x);
        double pi = Math.PI;// 3.141592653589793d0;
        double el = 0.5772156649015329;
        double s = 0.0;
        double ps = 0.0;
        double n = 0.0;

        if (x == MathUtil.fix(x) && x <= 0.0)
        { // System.out.println("Cond 1");
            ps = 1.0d + 300;
            return ps;
        }

        if (xa == MathUtil.fix(xa))
        { // System.out.println("Cond 2");
            n = xa;
            for (int k = 1; k <= ((int) n - 1); k++)
            {
                s += 1.0 / ((double) k);
            } // System.out.println("s = "+s);
            ps = -el + s;
        }
        else if ((xa + 0.5) == MathUtil.fix(xa + 0.5))
        { // System.out.println("Cond 3");
            n = xa - 0.5;
            for (int k = 1; k <= (int) n; k++)
            {
                s += 1.0 / (2.0 * (double) k - 1.0);
            }
            ps = -el + 2.0 * s - 1.386294361119891;
        }
        else
        { // System.out.println("Cond 4");
            if (xa < 10.0)
            {
                n = 10.0 - MathUtil.fix(xa);
                for (int k = 0; k <= ((int) n - 1); k++)
                {
                    s += 1.0 / (xa + (double) k);
                }
                xa += n;
            }
            double x2 = 1.0 / (xa * xa);
            double a1 = -0.8333333333333e-01;
            double a2 = 0.83333333333333333e-02;
            double a3 = -0.39682539682539683e-02;
            double a4 = 0.41666666666666667e-02;
            double a5 = -0.75757575757575758e-02;
            double a6 = 0.21092796092796093e-01;
            double a7 = -0.83333333333333333e-01;
            double a8 = 0.4432598039215686;
            ps = Math.log(xa) - 0.5 / xa + x2
                    * (((((((a8 * x2 + a7) * x2 + a6) * x2 + a5) * x2 + a4) * x2 + a3) * x2 + a2) * x2 + a1);
            ps -= s;
        }

        if (x < 0.0)
        {
            ps = ps - pi * Math.cos(pi * x) / Math.sin(pi * x) - 1.0 / x;
        }

        /*
         * function [x,ps]=mpsi(x,ps);
         * 
         * % Input : x --- Argument of psi(x) % Output: PS --- psi(x) %
         * ======================================
         * 
         * 
         * 
         * % % % xa=abs(x); pi=3.141592653589793d0; el=.5772156649015329d0;
         * s=0.0d0;--------- if (x == fix(x)&x <= 0.0) ; ps=1.0d+300;
         * return;----------
         * 
         * elseif (xa == fix(xa)); n=xa; for k=1 :n-1; s=s+1.0d0./k; end;
         * ps=-el+s; elseif (xa+.5 == fix(xa+.5)); n=xa-.5; for k=1:n;
         * s=s+1.0./(2.0d0.*k-1.0d0); end; ps=-el+2.0d0.*s-1.386294361119891d0;
         * else; if (xa < 10.0) ; n=10-fix(xa); for k=0:n-1; s=s+1.0d0./(xa+k);
         * end; xa=xa+n; end; x2=1.0d0./(xa.*xa); a1=-.8333333333333d-01;
         * a2=.83333333333333333d-02; a3=-.39682539682539683d-02;
         * a4=.41666666666666667d-02; a5=-.75757575757575758d-02;
         * a6=.21092796092796093d-01; a7=-.83333333333333333d-01;
         * a8=.4432598039215686d0;
         * ps=log(xa)-.5d0./xa+x2.*(((((((a8.*x2+a7).*x2+
         * a6).*x2+a5).*x2+a4).*x2+a3).*x2+a2).*x2+a1); ps=ps-s;
         * end;-------------------
         * 
         * if (x < 0.0) ps=ps-pi.*cos(pi.*x)./sin(pi.*x)-1.0d0./x; end; return;
         */
        return ps;
    }

    /**
     * DOT(A,B,DIM) returns the scalar product of A and B in the row or column
     * dimension DIM.
     * 
     * @param A
     *            Matrix
     * @param B
     *            Matrix
     * @param Dim
     *            int
     * @return Matrix
     */
    public static Matrix dot(Matrix A, Matrix B, Dimension Dim)
    {

        Matrix AA = A;
        if (A.isVector())
        {
            if (A.isRowVector())
            {
                AA = A.toColVector();
            }
        }
        Matrix BB = B;
        if (B.isVector())
        {
            if (B.isRowVector())
            {
                BB = B.toColVector();
            }
        }

        if ((AA.getRowDimension() != BB.getRowDimension()) || (AA.getColumnDimension() != BB.getColumnDimension()))
        {
            throw new IllegalArgumentException("dot : Sizes of 'A' and 'B' must be the same.");
        }

        Matrix c = JDatafun.sum(AA.arrayTimes(BB), Dim);
        return c;
    }

    /**
     * DOT Vector dot product. C = DOT(A,B) returns the scalar product of the
     * vectors A and B. A and B must be vectors of the same length. When A and B
     * are both column vectors, DOT(A,B) is the same as A'*B.
     * 
     * @param A
     *            Matrix
     * @param B
     *            Matrix
     * @return Matrix
     */
    public static Matrix dot(Matrix A, Matrix B)
    {
        Matrix a, b;
        if (A.isVector())
        {
            a = A.toColVector();
        }
        else
        {
            a = A;
        }
        if (B.isVector())
        {
            b = B.toColVector();
        }
        else
        {
            b = B;
        }
        if ((a.getRowDimension() != b.getRowDimension()) || (a.getColumnDimension() != b.getColumnDimension()))
        {
            throw new IllegalArgumentException("dot : Lengths of 'A' and 'B' must be the same.");
        }
        Matrix c = JDatafun.sum(a.arrayTimes(b));
        return c;
    }

    public static double EPS(double xin)
    {
        double eps = Math.pow(2.0, -52.0);
        double lo = 0.0;
        double up = 0.0;
        double n = 1.0;
        boolean cond = false;
        double x = Math.abs(xin);

        if (Double.isInfinite(x) || Double.isNaN(x))
        {
            return Double.NaN;
        }
        else if (x == 0.0)
        {
            return Math.pow(2.0, -1074.0);
        }
        else if (x == Double.MAX_VALUE)
        {
            return Math.pow(2.0, 971.0);
        }
        else if (x == (Double.MIN_VALUE / 2.0))
        {
            return Math.pow(2.0, -1074.0);
        }
        else if (x == (Double.MIN_VALUE / 16.0))
        {
            return Math.pow(2.0, -1074.0);
        }

        cond = true; // Set to true, since this is how Matlab actually does it.
        boolean cond2 = false;
        if (cond)
        { // x < 1.0
            double machEps = 1.0;
            do
            {
                machEps /= 2.0;
                // n = n + 1.0;
            }
            while ((x + (machEps)) != x);
            // System.out.println(" n = " + n);
            // System.out.println("$$$$$$$$$$$$$  x < 1  (X IS LESS THAN 1)  $$$$$$$$$$$$$");
            return (2.0 * machEps);
        }
        else if (cond2)
        {// set to false, since, this is not the algorithm used in matlab (see
         // block above which was cut & pasted from wikipedia).
            do
            {
                lo = Math.pow(2.0, n - 1.0);
                up = Math.pow(2.0, n);
                cond = ((lo <= x) && (x < up));
                // System.out.println("lo = " + lo + " ;  up = " + up +
                // " ;  cond = " + cond);
                n += 1.0;
            }
            while (!cond);
        }

        // System.out.println("lo = " + lo + " ;  up = " + up + " ;  n = " + n);
        // System.out.println("Calculated Machine epsilon: " + eps);
        double mult = (n - 1.0);
        double indPow = mult - 1.0;
        double val = Math.pow(2.0, indPow) * eps;
        return val;
    }

    public static void main(String[] args)
    {

        double x = 13;
        double psi = mpsi(1, x);
        System.out.println("psi = " + psi);
        // System.out.println("realmax = " + Double.MAX_VALUE);

        /*
         * x = 16.0;
         * 
         * do { lo = Math.pow(2.0, n - 1.0); up = Math.pow(2.0, n); cond = ((lo
         * <= x) && (x < up)); //System.out.println("lo = " + lo + " ; up = " +
         * up + " ; cond = " + cond); n = n + 1.0; } while (!cond);
         */

        /*
         * if (x < 1) { } else if (1.0 <= x && x < 2.0) { } else { n = 1.0;
         * double lo = Math.pow(2.0, n - 1.0); double up = Math.pow(2.0, n);
         * boolean cond = !(lo <= x && x < up); while (cond) { //s =
         * Math.pow(2.0, n); n++; lo = Math.pow(2.0, n - 1.0); up =
         * Math.pow(2.0, n); cond = lo <= x && x < up;
         * 
         * } }
         */
        // double eps = (n - 1.0) * EPS;
        // System.out.println("n = " + n);
        // System.out.println("n = " + n);
        // System.out.println("eps = " + eps);
    }
}// -------------------------- End Class Definition
// -----------------------------

