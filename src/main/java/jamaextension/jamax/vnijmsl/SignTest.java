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

// Referenced classes of package com.imsl.stat:
//            Cdf
public class SignTest implements Serializable, Cloneable
{

    static final long serialVersionUID = 0xc9a7e499b70dce73L;
    private int l_nobs;
    private int l_npos;
    private int l_ntie;
    private int l_percentage_user;
    private int l_percentile_user;
    private double l_x[];
    private double l_percentage;
    private double l_percentile;

    public SignTest(double ad[])
    {
        l_nobs = ad.length;
        if (l_nobs < 2)
        {
            Object aobj[] =
            {
                    "x.length", new Integer(ad.length), new Integer(1)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotLargeEnough", aobj);
            throw new IllegalArgumentException("NotLargeEnough");
        }
        l_x = (double[]) ad.clone();
    }

    public final double compute()
    {
        int i = l_nobs;
        double ad[] = l_x;
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
        double d = l_percentage;
        double d1 = l_percentile;
        double ad1[] =
        {
            0.0D
        };
        int j = l_percentage_user;
        int k = l_percentile_user;
        if (j == 0)
        {
            d = 0.5D;
        }
        if (k == 0)
        {
            d1 = 0.0D;
        }
        l_signt(i, ad, d1, d, ai, ai1, ad1, ai2);
        l_npos = ai[0];
        l_ntie = ai1[0];
        return ad1[0];
    }

    private void l_signt(int i, double ad[], double d, double d1, int ai[], int ai1[], double ad1[], int ai2[])
    {
        ai[0] = 0;
        ai1[0] = 0;
        int l = 0;
        ai2[0] = 0;
        for (int j = 1; j <= i; j++)
        {
            if (!Double.isNaN(ad[j - 1]))
            {
                l++;
                if (ad[j - 1] - d > 0.0D)
                {
                    ai[0]++;
                    continue;
                }
                if (ad[j - 1] - d == 0.0D)
                {
                    ai1[0]++;
                }
            }
            else
            {
                ai2[0]++;
            }
        }

        if (l == 0) // Messages.throwIllegalArgumentException("com.imsl.stat",
                    // "SignTest.AllObsMissing", null);
        {
            throw new IllegalArgumentException("SignTest.AllObsMissing");
        }
        int k = l - ai1[0];
        if (ai[0] > 0)
        {
            ad1[0] = 1.0D - Cdf.binomial(ai[0] - 1, k, d1);
        }
        else
        {
            ad1[0] = 1.0D;
        }
    }

    public int getNumPositiveDev()
    {
        return l_npos;
    }

    public int getNumZeroDev()
    {
        return l_ntie;
    }

    public void setPercentile(double d)
    {
        l_percentile = d;
        l_percentile_user = 1;
    }

    public void setPercentage(double d)
    {
        if (d >= 1.0D || d <= 0.0D)
        {
            Object aobj[] =
            {
                    "percentage", new Double(d), new Double(0.0D), new Double(1.0D)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "OutOfRange", aobj);
            throw new IllegalArgumentException("OutOfRange");
        }
        l_percentage = d;
        l_percentage_user = 1;
    }
}
