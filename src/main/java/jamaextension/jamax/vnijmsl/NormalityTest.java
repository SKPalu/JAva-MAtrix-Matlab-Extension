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

public class NormalityTest implements Serializable, Cloneable
{
    /*
     * public static class NoVariationInputException extends IMSLException {
     * 
     * static final long serialVersionUID = 0x634a2a553e737d24L;
     * 
     * public NoVariationInputException(String s) { super(s); }
     * 
     * public NoVariationInputException(String s, Object aobj[]) {
     * super("com.imsl.stat", s, aobj); } }
     */

    // private static final int check = Messages.check(1);
    static final long serialVersionUID = 0x211da6e1b6979bd8L;
    private int l_nobs;
    private int l_n;
    private double l_x[];
    private double l_w;
    private double l_max;
    private double l_df;
    private double l_chi;

    public NormalityTest(double ad[])
    {
        l_w = (0.0D / 0.0D);
        l_max = (0.0D / 0.0D);
        l_df = (0.0D / 0.0D);
        l_chi = (0.0D / 0.0D);
        if (ad.length < 1)
        {
            Object aobj[] =
            {
                    "x.length", new Integer(ad.length), new Integer(0)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotLargeEnough", aobj);
            throw new IllegalArgumentException("NotLargeEnough");
        }
        l_nobs = ad.length;
        l_x = (double[]) ad.clone();
    }

    private double l_normality_test(int i, int j, int k) // throws
                                                         // NoVariationInputException,
                                                         // InverseCdf.DidNotConvergeException
    {
        int l = l_nobs;
        double ad[] = l_x;
        double ad1[] =
        {
            0.0D
        };
        double ad2[] =
        {
            0.0D
        };
        int i1 = 0;
        int j1 = 0;
        int ai[] =
        {
            0
        };
        int ai1[] =
        {
            0
        };
        int ai2[] =
        {
            0
        };
        double ad3[] =
        {
            0.0D
        };
        double ad4[] =
        {
            0.0D
        };
        Object obj = null;
        double ad7[] =
        {
            0.0D
        };
        int l1 = l_n;
        if (j != 0)
        {
            if (l < 5)
            {
                Object aobj[] =
                {
                        "x.length", new Integer(l), new Integer(4)
                };
                // Messages.throwIllegalArgumentException("com.imsl.stat",
                // "NotLargeEnough", aobj);
                throw new IllegalArgumentException("NotLargeEnough");
            }
            i1++;
        }
        if (i != 0)
        {
            if (l < 3 || l > 2000)
            {
                Object aobj1[] =
                {
                        "x.length", new Integer(l), new Integer(3), new Integer(2000)
                };
                // Messages.throwIllegalArgumentException("com.imsl.stat",
                // "OutOfRange", aobj1);
                throw new IllegalArgumentException("OutOfRange");
            }
            i1++;
        }
        if (j != 0)
        {
            double ad5[] = new double[l];
            l_l2llf(l, ad, j1, ad3, ad4, ad2, ad7, ai2, ad5);
            l_max = ad2[0];
        }
        if (i != 0)
        {
            l_c1srt(l, ad, ai1, ai);
            if (ai1[0] == 1 && ai[0] >= 0)
            {
                int k1 = l - ai[0];
                l_s3wlk(k1, ad, ad1, ad7);
            }
            else
            {
                double ad6[] = new double[l];
                l_s2wlk(l, ad, ad1, ad7, ai2, ad6);
            }
            l_w = ad1[0];
        }
        if (k != 0)
        {
            CdfFunction cdffunction = new CdfFunction()
            {

                public double cdf(double d)
                {
                    return Cdf.normal(d);
                }
            };
            ChiSquaredTest chisquaredtest = new ChiSquaredTest(cdffunction, l1 - 1, 0);
            for (int i2 = 0; i2 < ad.length; i2++)
            {
                chisquaredtest.update(ad[i2], 1.0D);
            }

            ad7[0] = chisquaredtest.getP();
            l_df = chisquaredtest.getDegreesOfFreedom();
            l_chi = chisquaredtest.getChiSquared();
        }
        return ad7[0];
    }

    private void l_s2wlk(int i, double ad[], double ad1[], double ad2[], int ai[], double ad3[])
    {
        int ai1[] =
        {
            0
        };
        int ai2[] =
        {
            0
        };
        int i1 = 1;
        i1++;
        l_c1srt(i, ad, ai1, ai2);
        if (ai1[0] == 1 && ai2[0] >= 0)
        {
            int j = i - ai2[0];
            l_s3wlk(j, ad, ad1, ad2);
            ai[0] = ai2[0];
        }
        else
        {
            int l = 0;
            ai[0] = 0;
            for (int k = 1; k <= i; k++)
            {
                if (!Double.isNaN(ad[k - 1]))
                {
                    l++;
                    ad3[l - 1] = ad[k - 1];
                }
                else
                {
                    ai[0]++;
                }
            }

            int j1 = i - ai[0];
            if (j1 <= 2)
            {
                Object aobj[] =
                {
                    new Integer(j1)
                };
                // Messages.throwIllegalArgumentException("com.imsl.stat",
                // "NormalityTest.TooManyMissing", aobj);
                throw new IllegalArgumentException("NormalityTest.TooManyMissing");
            }
            l_svrgn(j1, ad3, ad3);
            l_s3wlk(j1, ad3, ad1, ad2);
        }
    }

    private void l_s3wlk(int i, double ad[], double ad1[], double ad2[])
    {
        int ai[] =
        {
            0
        };
        double ad3[][] =
        {
                {
                        0.118898D, 0.133414D, 0.327907D, 0.0D, 0.0D, 0.0D, 0.0D
                },
                {
                        0.48038500000000001D, 0.318828D, 0.0D, -0.0241665D, 0.0087970099999999992D,
                        0.0029896459999999999D, 0.0D
                }
        };
        double ad4[][] =
        {
                {
                        -0.37541999999999998D, -0.492145D, -1.1243320000000001D, -0.19942199999999999D, 0.0D, 0.0D,
                        0.0D
                },
                {
                        -1.9148700000000001D, -1.3788800000000001D, -0.041832090000000002D, 0.1066339D, -0.03513666D,
                        -0.01504614D, 0.0D
                }
        };
        double ad5[][] =
        {
                {
                        -3.1580499999999998D, 0.72939900000000002D, 3.0185499999999998D, 1.5587759999999999D, 0.0D,
                        0.0D, 0.0D
                },
                {
                        -3.7353800000000001D, -1.0158069999999999D, -0.33188499999999999D, 0.17735380000000001D,
                        -0.016387820000000001D, -0.03215018D, 0.0038526459999999999D
                }
        };
        double ad6[][][] =
        {
                {
                        {
                                -0.28769600000000001D, 1.7895300000000001D, -0.180114D, 0.0D, 0.0D
                        },
                        {
                                -1.6363799999999999D, 5.6092399999999998D, -3.6373799999999998D, 1.08439D, 0.0D
                        },
                        {
                                -5.9919079999999996D, 21.045750000000002D, -24.58061D, 13.78661D, -2.8352949999999999D
                        }
                },
                {
                        {
                                -1.26233D, 1.8796900000000001D, 0.064958299999999997D, -0.047560400000000003D,
                                -0.0139682D
                        },
                        {
                                -2.2813500000000002D, 2.26186D, 0.0D, 0.0D, -0.0086576299999999995D
                        },
                        {
                                -3.3062299999999998D, 2.7628699999999999D, -0.83484000000000003D, 1.2085699999999999D,
                                -0.50758999999999999D
                        }
                }
        };
        double ad7[][] =
        {
                {
                        0.70710678000000005D, 0.0D, 0.0D
                },
                {
                        0.68689999999999996D, 0.1678D, 0.0D
                },
                {
                        0.66469999999999996D, 0.2412D, 0.0D
                },
                {
                        0.6431D, 0.28060000000000002D, 0.087499999999999994D
                }
        };
        ai[0] = 1;
        if (i < 3 || i > 2000)
        {
            Object aobj[] =
            {
                    "x.length", new Integer(i), new Integer(3), new Integer(2000)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "OutOfRange", aobj);
            throw new IllegalArgumentException("OutOfRange");
        }
        ai[0]++;
        double d11 = BLAS.sum(i, ad, 0, 1) / (double) i;
        double d18 = 0.0D;
        double d15 = 0.0D;
        double d14 = 0.0D;
        for (int j = 2; j <= i / 2; j++)
        {
            double d;
            if (i >= 7)
            {
                d = l_s4wlk(j, i);
                d14 += d * d;
            }
            else
            {
                d = ad7[i - 3][j - 1];
            }
            d18 += d * (ad[i - j] - ad[j - 1]);
        }

        for (int k = 1; k <= i; k++)
        {
            d15 += Math.pow(ad[k - 1] - d11, 2D);
        }

        double d1;
        if (i >= 7)
        {
            int j1;
            if (i <= 20)
            {
                j1 = i - 1;
            }
            else
            {
                j1 = i;
            }
            d1 = (6D * (double) j1 + 7D) / (6D * (double) j1 + 13D);
            double d5 = ((double) j1 + 1.0D) / ((double) j1 + 2D);
            d1 *= Math.sqrt((Math.exp(1.0D) / ((double) j1 + 2D)) * Math.pow(d5, j1 - 2));
            d1 = Math.sqrt((d1 * 2D * d14) / (1.0D - 2D * d1));
            d14 += d1 * d1;
        }
        else
        {
            d1 = ad7[i - 3][0];
            d14 = 0.5D;
        }
        d18 += d1 * (ad[i - 1] - ad[0]);
        if (d15 == 0.0D)
        {
            // Warning.print(this, "com.imsl.stat", "NormalityTest.AllObsTied",
            // null);
            System.out.println("NormalityTest.AllObsTied");
            ad1[0] = 0.0D;
            ad2[0] = 1.0D;
            return;
        }
        ad1[0] = Math.pow(d18, 2D) / (2D * d14 * d15);
        if (i == 3)
        {
            ad2[0] = 1.90985932D * (Math.atan(Math.sqrt(ad1[0] / (1.0D - ad1[0]))) - 1.0471975499999999D);
        }
        else if (i <= 6)
        {
            double d6 = (Math.pow(ad7[i - 3][0], 2D) * (double) i) / ((double) i - 1.0D);
            double d10 = Math.log((ad1[0] - d6) / (1.0D - ad1[0]));
            byte byte0;
            if (d10 > 1.3999999999999999D)
            {
                d10 = Math.log(d10);
                byte0 = 1;
            }
            else
            {
                byte0 = 2;
            }
            double d7 = 1.0D;
            double d2 = 0.0D;
            for (int l = 1; l <= 5; l++)
            {
                d2 += ad6[byte0 - 1][i - 4][l - 1] * d7;
                d7 *= d10;
            }

            if (byte0 == 1)
            {
                d2 = Math.exp(d2);
            }
            d2 = Math.exp(d2);
            ad2[0] = 1.90985932D * (Math.asin(Math.sqrt((d2 + 0.75D) / (d2 + 1.0D))) - 1.0471975499999999D);
        }
        else
        {
            double d3 = i;
            double d12;
            if (i <= 20)
            {
                d12 = Math.log(d3) - 3D;
            }
            else
            {
                d12 = Math.log(d3) - 5D;
            }
            double d16 = 0.0D;
            double d17 = 0.0D;
            double d19 = 0.0D;
            double d8 = 1.0D;
            byte byte1;
            byte byte2;
            if (i <= 20)
            {
                byte2 = 4;
                byte1 = 1;
            }
            else
            {
                byte2 = 7;
                byte1 = 2;
            }
            for (int i1 = 1; i1 <= byte2; i1++)
            {
                d16 += ad3[byte1 - 1][i1 - 1] * d8;
                d17 += ad4[byte1 - 1][i1 - 1] * d8;
                d19 += ad5[byte1 - 1][i1 - 1] * d8;
                d8 *= d12;
            }

            double d13 = Math.pow(1.0D - ad1[0], d16);
            double d9 = d19;
            double d4 = d17;
            d9 = Math.exp(d19);
            d4 = Math.exp(d17);
            d13 = (d13 - d4) / d9;
            ad2[0] = 1.0D - Cdf.normal(d13);
        }
    }

    private double l_s4wlk(int i, int j)
    {
        double ad[] =
        {
                0.41988500000000001D, 0.45053599999999999D, 0.45693600000000001D, 0.46848800000000002D
        };
        double ad1[] =
        {
                0.112063D, 0.12177D, 0.23929900000000001D, 0.21515899999999999D
        };
        double ad2[] =
        {
                0.080121999999999999D, 0.111348D, -0.211867D, -0.115049D
        };
        double ad3[] =
        {
                0.474798D, 0.469051D, 0.208597D, 0.25978400000000001D
        };
        double ad4[] =
        {
                0.28276499999999999D, 0.30485600000000002D, 0.40770800000000001D, 0.41409299999999999D
        };
        double d7 = 0.56418959999999996D;
        if (j == 2)
        {
            return d7;
        }
        double d1 = j;
        double d = i;
        if (i <= 3)
        {
            double d2 = (d - ad[i - 1]) / (d1 + ad3[i - 1]);
            double d4 = Math.pow(d2, ad4[i - 1]);
            d7 = (d2 + (d4 * (ad1[i - 1] + d4 * ad2[i - 1])) / d1) - l_s5wlk(i, j);
        }
        else
        {
            double d6 = ad4[3] + -0.283833D / (d + -0.10613599999999999D);
            double d3 = (d - ad[3]) / (d1 + ad3[3]);
            double d5 = Math.pow(d3, d6);
            d7 = (d3 + (d5 * (ad1[3] + d5 * ad2[3])) / d1) - l_s5wlk(i, j);
        }
        d7 = -Cdf.inverseNormal(d7);
        return d7;
    }

    private double l_s5wlk(int i, int j)
    {
        double ad[] =
        {
                9.5D, 28.699999999999999D, 1.8999999999999999D, 0.0D, -7D, -6.2000000000000002D, -1.6000000000000001D
        };
        double ad1[] =
        {
                -6195D, -9569D, -6728D, -17614D, -8278D, -3570D, 1075D
        };
        double ad2[] =
        {
                93380D, 175160D, 410400D, 2157000D, 2376000D, 2065000D, 2065000D
        };
        double d1 = 1.9000000000000001E-005D;
        if (i * j != 4)
        {
            d1 = 0.0D;
            if (i <= 7 && j <= 40)
            {
                if (i != 4 && j > 20)
                {
                    return d1;
                }
                double d = j;
                d = 1.0D / (d * d);
                d1 = (ad[i - 1] + d * (ad1[i - 1] + d * ad2[i - 1])) * 9.9999999999999995E-007D;
            }
        }
        return d1;
    }

    private void l_l2llf(int i, double ad[], int j, double ad1[], double ad2[], double ad3[], double ad4[], int ai[],
            double ad5[]) // throws NoVariationInputException
    {
        int ai1[] =
        {
            0
        };
        l_c1srt(i, ad, ai1, ai);
        if (ai1[0] == 1 && ai[0] >= 0 && ai[0] != i)
        {
            System.arraycopy(ad, 0, ad5, 0, i - ai[0]);
            int k = i - ai[0];
            l_l3llf(k, ad5, ad1, ad2, ad3, ad4);
        }
        else
        {
            ai[0] = 0;
            int i1 = 0;
            for (int l = 1; l <= i; l++)
            {
                if (Double.isNaN(ad[l - 1]))
                {
                    ai[0]++;
                }
                else
                {
                    i1++;
                    ad5[i1 - 1] = ad[l - 1];
                }
            }

            if (i1 < 5)
            {
                Object aobj[] =
                {
                    new Integer(i1)
                };
                // Messages.throwIllegalArgumentException("com.imsl.stat",
                // "NormalityTest.NeedATLeast5", aobj);
                throw new IllegalArgumentException("NormalityTest.NeedATLeast5");
            }
            l_svrgn(i1, ad5, ad5);
            l_l3llf(i1, ad5, ad1, ad2, ad3, ad4);
        }
    }

    private void l_l3llf(int i, double ad[], double ad1[], double ad2[], double ad3[], double ad4[]) // throws
                                                                                                     // NoVariationInputException
    {
        double ad5[] = new double[3];
        if (ad[0] == ad[i - 1]) // throw new
                                // NoVariationInputException("NormalityTest.NoVariationInput",
                                // null);
        {
            throw new IllegalArgumentException("NormalityTest.NoVariationInput");
        }
        ad1[0] = BLAS.sum(i, ad, 0, 1);
        ad1[0] /= i;
        ad2[0] = 0.0D;
        for (int j = 1; j <= i; j++)
        {
            double d1 = ad[j - 1] - ad1[0];
            ad2[0] += d1 * d1;
        }

        ad2[0] = Math.sqrt(ad2[0] / (double) (i - 1));
        for (int k = 1; k <= i; k++)
        {
            ad[k - 1] = (ad[k - 1] - ad1[0]) / ad2[0];
        }

        l_l4llf(i, ad, ad5, 1);
        ad3[0] = ad5[0];
        int l;
        double d;
        if (i > 100)
        {
            d = ad3[0] * Math.pow(i / 100, 0.48999999999999999D);
            l = 100;
        }
        else
        {
            d = ad3[0];
            l = i;
        }
        ad4[0] = ((-7.0125599999999997D * Math.pow(d, 2D) * ((double) l + 2.7801900000000002D) + 2.99587D * d
                * Math.sqrt((double) l + 2.7801900000000002D)) - 0.12211900000000001D)
                + 0.97459799999999996D / Math.sqrt(l) + 1.67997D / (double) l;
        ad4[0] = Math.exp(ad4[0]);
        if (ad4[0] < 0.01D)
        {
            ad4[0] = 0.01D;
        }
        else if (ad4[0] > 0.10000000000000001D)
        {
            ad4[0] = 0.5D;
        }
    }

    private void l_l4llf(int i, double ad[], double ad1[], int j)
    {
        ad1[1] = 0.0D;
        ad1[2] = 0.0D;
        boolean flag = false;
        double d4;
        if (j == 1)
        {
            d4 = Cdf.normal(ad[0]);
        }
        else
        {
            d4 = 1.0D - Math.exp(-ad[0]);
        }
        double d5 = d4;
        double d7 = 0.0D;
        int l = 1;
        double d9 = 0.0D;
        do
        {
            d9++;
            boolean flag1 = false;
            int k = l;
            do
            {
                if (k > i - 1)
                {
                    break;
                }
                if (ad[k - 1] < ad[k])
                {
                    flag = true;
                    flag1 = true;
                    break;
                }
                d9++;
                k++;
            }
            while (true);
            if (!flag1)
            {
                k = i;
            }
            l = k + 1;
            double d8 = d9 / (double) i;
            double d2 = d4 - d8;
            double d3 = d8 - d4;
            double d = d4 - d7;
            double d1 = d7 - d4;
            ad1[1] = Math.max(Math.max(ad1[1], d3), d1);
            ad1[2] = Math.max(Math.max(ad1[2], d2), d);
            if (l > i)
            {
                break;
            }
            if (j == 1)
            {
                d4 = Cdf.normal(ad[l - 1]);
            }
            else
            {
                d4 = 1.0D - Math.exp(-ad[l - 1]);
            }
            double d6 = d4;
            d7 = d8;
        }
        while (true);
        ad1[0] = Math.max(ad1[1], ad1[2]);
        if (flag) // Warning.print(this, "com.imsl.stat",
                  // "NormalityTest.TwoOrMoreTied", null);
        {
            System.out.println("NormalityTest.TwoOrMoreTied");
        }
    }

    private void l_c1srt(int i, double ad[], int ai[], int ai1[])
    {
        ai1[0] = 0;
        byte byte0 = 1;
        ai[0] = 1;
        double d = (-1.0D / 0.0D);
        for (int j = 1; j <= i; j++)
        {
            if (!Double.isNaN(ad[j - 1]))
            {
                if (ad[j - 1] < d)
                {
                    ai[0] = 0;
                    return;
                }
                d = ad[j - 1];
                if (ai1[0] > 0)
                {
                    byte0 = -1;
                }
            }
            else
            {
                ai1[0]++;
            }
        }

        ai1[0] *= byte0;
    }

    private void l_svrgn(int i, double ad[], double ad1[])
    {
        int ai[] = new int[21];
        int ai1[] = new int[21];
        System.arraycopy(ad, 0, ad1, 0, i);
        boolean flag1;
        boolean flag4 = flag1 = false;
        int l1 = 1;
        int j = 1;
        int l = i;
        double d = 0.375D;
        do
        {
            boolean flag2 = false;
            if (j == l)
            {
                flag1 = false;
                flag2 = true;
            }
            if (!flag2)
            {
                if (d <= 0.58984369999999997D)
                {
                    d += 0.0390625D;
                }
                else
                {
                    d -= 0.21875D;
                }
            }
            boolean flag3;
            do
            {
                if (!flag2)
                {
                    int i1 = j;
                    int k = j + (int) ((double) (l - j) * d);
                    double d1 = ad1[k - 1];
                    if (ad1[j - 1] > d1)
                    {
                        ad1[k - 1] = ad1[j - 1];
                        ad1[j - 1] = d1;
                        d1 = ad1[k - 1];
                    }
                    int k1 = l;
                    boolean flag = false;
                    if (ad1[l - 1] >= d1)
                    {
                        flag = true;
                    }
                    if (!flag)
                    {
                        ad1[k - 1] = ad1[l - 1];
                        ad1[l - 1] = d1;
                        d1 = ad1[k - 1];
                        if (ad1[j - 1] > d1)
                        {
                            ad1[k - 1] = ad1[j - 1];
                            ad1[j - 1] = d1;
                            d1 = ad1[k - 1];
                        }
                        flag = true;
                    }
                    do
                    {
                        if (!flag && ad1[k1 - 1] != ad1[i1 - 1])
                        {
                            double d3 = ad1[k1 - 1];
                            ad1[k1 - 1] = ad1[i1 - 1];
                            ad1[i1 - 1] = d3;
                        }
                        do
                        {
                            k1--;
                        }
                        while (ad1[k1 - 1] > d1);
                        do
                        {
                            i1++;
                        }
                        while (ad1[i1 - 1] < d1);
                        if (i1 > k1)
                        {
                            break;
                        }
                        flag = false;
                    }
                    while (true);
                    if (k1 - j <= l - i1)
                    {
                        ai[l1 - 1] = i1;
                        ai1[l1 - 1] = l;
                        l = k1;
                        l1++;
                    }
                    else
                    {
                        ai[l1 - 1] = j;
                        ai1[l1 - 1] = k1;
                        j = i1;
                        l1++;
                    }
                    flag1 = true;
                }
                do
                {
                    if (!flag1)
                    {
                        if (--l1 == 0)
                        {
                            return;
                        }
                        j = ai[l1 - 1];
                        l = ai1[l1 - 1];
                    }
                    flag3 = false;
                    if (l - j >= 11)
                    {
                        flag2 = false;
                        flag3 = true;
                        break;
                    }
                    flag4 = false;
                    if (j == 1)
                    {
                        flag4 = true;
                        break;
                    }
                    j--;
                    do
                    {
                        j++;
                        flag2 = false;
                        if (j == l)
                        {
                            flag1 = false;
                            flag2 = true;
                            break;
                        }
                        double d2 = ad1[j];
                        if (ad1[j - 1] > d2)
                        {
                            int j1 = j;
                            do
                            {
                                ad1[j1] = ad1[j1 - 1];
                                j1--;
                            }
                            while (d2 < ad1[j1 - 1]);
                            ad1[j1] = d2;
                        }
                    }
                    while (true);
                }
                while (flag2);
            }
            while (flag3);
        }
        while (flag4);
    }

    public double getDegreesOfFreedom()
    {
        return l_df;
    }

    public double getChiSquared()
    {
        return l_chi;
    }

    public double getShapiroWilkW()
    {
        return l_w;
    }

    public double getMaxDifference()
    {
        return l_max;
    }

    public final double ShapiroWilkWTest() // throws NoVariationInputException,
                                           // InverseCdf.DidNotConvergeException
    {
        return l_normality_test(1, 0, 0);
    }

    public final double LillieforsTest() // throws NoVariationInputException,
                                         // InverseCdf.DidNotConvergeException
    {
        return l_normality_test(0, 1, 0);
    }

    public final double ChiSquaredTest(int i) // throws
                                              // NoVariationInputException,
                                              // InverseCdf.DidNotConvergeException
    {
        if (i < 2)
        {
            Object aobj[] =
            {
                    "n", new Integer(i), new Integer(1)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotLargeEnough", aobj);
            throw new IllegalArgumentException("NotLargeEnough");
        }
        l_n = i;
        return l_normality_test(0, 0, 1);
    }
}
