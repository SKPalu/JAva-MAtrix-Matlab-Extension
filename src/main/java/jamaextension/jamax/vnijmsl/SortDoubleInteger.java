/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

/**
 * 
 * @author Feynman Perceptrons
 */
// public class SortDoubleInteger {

// }

public final class SortDoubleInteger
{

    private SortDoubleInteger()
    {
    }

    public static void descending(double ad[], int ai[])
    {
        ascending(ad, ai);
        int i = ad.length;
        for (int j = 0; j < i / 2; j++)
        {
            double d = ad[j];
            ad[j] = ad[i - j - 1];
            ad[i - j - 1] = d;
            int k = ai[j];
            ai[j] = ai[i - j - 1];
            ai[i - j - 1] = k;
        }

    }

    public static void ascending(double ad[], int ai[])
    {
        if (ad.length == 0)
        {
            return;
        }
        int ai1[] = new int[21];
        int ai2[] = new int[21];
        int i = ad.length;
        ai1[0] = 0;
        ai2[0] = i - 1;
        double d = 0.375D;
        int k = 1;
        label0: do
        {
            if (k == -1)
            {
                break;
            }
            if (--k == -1)
            {
                continue;
            }
            int l = ai1[k];
            int i1 = ai2[k];
            do
            {
                if (l != 0)
                {
                    break;
                }
                if (l == i1)
                {
                    continue label0;
                }
                if (d <= 0.58984369999999997D)
                {
                    d += 0.0390625D;
                }
                else
                {
                    d -= 0.21875D;
                }
                do
                {
                    int j1 = l;
                    int k1 = (int) ((double) l + (double) (i1 - l) * d);
                    double d2 = ad[k1];
                    int j2 = ai[k1];
                    if (ad[l] > d2)
                    {
                        ad[k1] = ad[l];
                        ad[l] = d2;
                        d2 = ad[k1];
                        ai[k1] = ai[l];
                        ai[l] = j2;
                        j2 = ai[k1];
                    }
                    int k2 = i1;
                    if (ad[i1] < d2)
                    {
                        ad[k1] = ad[i1];
                        ad[i1] = d2;
                        d2 = ad[k1];
                        ai[k1] = ai[i1];
                        ai[i1] = j2;
                        j2 = ai[k1];
                        if (ad[l] > d2)
                        {
                            ad[k1] = ad[l];
                            ad[l] = d2;
                            d2 = ad[k1];
                            ai[k1] = ai[l];
                            ai[l] = j2;
                            j2 = ai[k1];
                        }
                    }
                    for (k2--; ad[k2] > d2; k2--)
                        ;
                    for (j1++; ad[j1] < d2; j1++)
                        ;
                    while (j1 <= k2)
                    {
                        if (ad[k2] != ad[j1])
                        {
                            double d3 = ad[k2];
                            ad[k2] = ad[j1];
                            ad[j1] = d3;
                            int l2 = ai[k2];
                            ai[k2] = ai[j1];
                            ai[j1] = l2;
                        }
                        for (k2--; ad[k2] > d2; k2--)
                            ;
                        j1++;
                        while (ad[j1] < d2)
                        {
                            j1++;
                        }
                    }
                    if (k2 - l <= i1 - j1)
                    {
                        ai1[k] = j1;
                        ai2[k] = i1;
                        i1 = k2;
                        k++;
                    }
                    else
                    {
                        ai1[k] = l;
                        ai2[k] = k2;
                        l = j1;
                        k++;
                    }
                }
                while (i1 - l >= 11);
            }
            while (l == 0);
            l--;
            while (++l != i1)
            {
                double d1 = ad[l + 1];
                int l1 = ai[l + 1];
                if (ad[l] > d1)
                {
                    int i2 = l;
                    ad[i2 + 1] = ad[i2];
                    ai[i2 + 1] = ai[i2];
                    int j = i2;
                    for (i2--; d1 < ad[i2]; i2--)
                    {
                        ad[j] = ad[i2];
                        ai[j] = ai[i2];
                        j = i2;
                    }

                    ad[i2 + 1] = d1;
                    ai[i2 + 1] = l1;
                }
            }
        }
        while (true);
    }

    public static void ascending(int ai[], int ai1[])
    {
        if (ai.length == 0)
        {
            return;
        }
        int ai2[] = new int[21];
        int ai3[] = new int[21];
        int i = ai.length;
        ai2[0] = 0;
        ai3[0] = i - 1;
        double d = 0.375D;
        int j = 1;
        label0: do
        {
            if (j == -1)
            {
                break;
            }
            if (--j == -1)
            {
                continue;
            }
            int k = ai2[j];
            int l = ai3[j];
            do
            {
                if (k != 0)
                {
                    break;
                }
                if (k == l)
                {
                    continue label0;
                }
                if (d <= 0.58984369999999997D)
                {
                    d += 0.0390625D;
                }
                else
                {
                    d -= 0.21875D;
                }
                do
                {
                    int i1 = k;
                    int k1 = (int) ((double) k + (double) (l - k) * d);
                    int i2 = ai[k1];
                    int k2 = ai1[k1];
                    if (ai[k] > i2)
                    {
                        ai[k1] = ai[k];
                        ai[k] = i2;
                        i2 = ai[k1];
                        ai1[k1] = ai1[k];
                        ai1[k] = k2;
                        k2 = ai1[k1];
                    }
                    int l2 = l;
                    if (ai[l] < i2)
                    {
                        ai[k1] = ai[l];
                        ai[l] = i2;
                        i2 = ai[k1];
                        ai1[k1] = ai1[l];
                        ai1[l] = k2;
                        k2 = ai1[k1];
                        if (ai[k] > i2)
                        {
                            ai[k1] = ai[k];
                            ai[k] = i2;
                            i2 = ai[k1];
                            ai1[k1] = ai1[k];
                            ai1[k] = k2;
                            k2 = ai1[k1];
                        }
                    }
                    for (l2--; ai[l2] > i2; l2--)
                        ;
                    for (i1++; ai[i1] < i2; i1++)
                        ;
                    while (i1 <= l2)
                    {
                        if (ai[l2] != ai[i1])
                        {
                            int i3 = ai[l2];
                            ai[l2] = ai[i1];
                            ai[i1] = i3;
                            int j3 = ai1[l2];
                            ai1[l2] = ai1[i1];
                            ai1[i1] = j3;
                        }
                        for (l2--; ai[l2] > i2; l2--)
                            ;
                        i1++;
                        while (ai[i1] < i2)
                        {
                            i1++;
                        }
                    }
                    if (l2 - k <= l - i1)
                    {
                        ai2[j] = i1;
                        ai3[j] = l;
                        l = l2;
                        j++;
                    }
                    else
                    {
                        ai2[j] = k;
                        ai3[j] = l2;
                        k = i1;
                        j++;
                    }
                }
                while (l - k >= 11);
            }
            while (k == 0);
            k--;
            while (++k != l)
            {
                int j1 = ai[k + 1];
                int l1 = ai1[k + 1];
                if (ai[k] > j1)
                {
                    int j2 = k;
                    ai[j2 + 1] = ai[j2];
                    ai1[j2 + 1] = ai1[j2];
                    for (j2--; j1 < ai[j2]; j2--)
                    {
                        ai[j2 + 1] = ai[j2];
                        ai1[j2 + 1] = ai1[j2];
                    }

                    ai[j2 + 1] = j1;
                    ai1[j2 + 1] = l1;
                }
            }
        }
        while (true);
    }

    public static void descending(double ad[])
    {
        ascending(ad);
        int i = ad.length;
        for (int j = 0; j < i / 2; j++)
        {
            double d = ad[j];
            ad[j] = ad[i - j - 1];
            ad[i - j - 1] = d;
        }

    }

    public static void ascending(double ad[])
    {
        if (ad.length == 0)
        {
            return;
        }
        int ai[] = new int[21];
        int ai1[] = new int[21];
        int i = ad.length;
        ai[0] = 0;
        ai1[0] = i - 1;
        double d = 0.375D;
        int j = 1;
        label0: do
        {
            if (j == -1)
            {
                break;
            }
            if (--j == -1)
            {
                continue;
            }
            int k = ai[j];
            int l = ai1[j];
            do
            {
                if (k != 0)
                {
                    break;
                }
                if (k == l)
                {
                    continue label0;
                }
                if (d <= 0.58984369999999997D)
                {
                    d += 0.0390625D;
                }
                else
                {
                    d -= 0.21875D;
                }
                do
                {
                    int i1 = k;
                    int j1 = (int) ((double) k + (double) (l - k) * d);
                    double d2 = ad[j1];
                    if (ad[k] > d2)
                    {
                        ad[j1] = ad[k];
                        ad[k] = d2;
                        d2 = ad[j1];
                    }
                    int l1 = l;
                    if (ad[l] < d2)
                    {
                        ad[j1] = ad[l];
                        ad[l] = d2;
                        d2 = ad[j1];
                        if (ad[k] > d2)
                        {
                            ad[j1] = ad[k];
                            ad[k] = d2;
                            d2 = ad[j1];
                        }
                    }
                    for (l1--; ad[l1] > d2; l1--)
                        ;
                    for (i1++; ad[i1] < d2; i1++)
                        ;
                    while (i1 <= l1)
                    {
                        if (ad[l1] != ad[i1])
                        {
                            double d3 = ad[l1];
                            ad[l1] = ad[i1];
                            ad[i1] = d3;
                        }
                        for (l1--; ad[l1] > d2; l1--)
                            ;
                        i1++;
                        while (ad[i1] < d2)
                        {
                            i1++;
                        }
                    }
                    if (l1 - k <= l - i1)
                    {
                        ai[j] = i1;
                        ai1[j] = l;
                        l = l1;
                        j++;
                    }
                    else
                    {
                        ai[j] = k;
                        ai1[j] = l1;
                        k = i1;
                        j++;
                    }
                }
                while (l - k >= 11);
            }
            while (k == 0);
            k--;
            while (++k != l)
            {
                double d1 = ad[k + 1];
                if (ad[k] > d1)
                {
                    int k1 = k;
                    ad[k1 + 1] = ad[k1];
                    for (k1--; d1 < ad[k1]; k1--)
                    {
                        ad[k1 + 1] = ad[k1];
                    }

                    ad[k1 + 1] = d1;
                }
            }
        }
        while (true);
    }

    public static void ascending(int ai[])
    {
        if (ai.length == 0)
        {
            return;
        }
        int ai1[] = new int[21];
        int ai2[] = new int[21];
        int i = ai.length;
        ai1[0] = 0;
        ai2[0] = i - 1;
        double d = 0.375D;
        int j = 1;
        label0: do
        {
            if (j == -1)
            {
                break;
            }
            if (--j == -1)
            {
                continue;
            }
            int k = ai1[j];
            int l = ai2[j];
            do
            {
                if (k != 0)
                {
                    break;
                }
                if (k == l)
                {
                    continue label0;
                }
                if (d <= 0.58984369999999997D)
                {
                    d += 0.0390625D;
                }
                else
                {
                    d -= 0.21875D;
                }
                do
                {
                    int i1 = k;
                    int k1 = (int) ((double) k + (double) (l - k) * d);
                    int i2 = ai[k1];
                    if (ai[k] > i2)
                    {
                        ai[k1] = ai[k];
                        ai[k] = i2;
                        i2 = ai[k1];
                    }
                    int j2 = l;
                    if (ai[l] < i2)
                    {
                        ai[k1] = ai[l];
                        ai[l] = i2;
                        i2 = ai[k1];
                        if (ai[k] > i2)
                        {
                            ai[k1] = ai[k];
                            ai[k] = i2;
                            i2 = ai[k1];
                        }
                    }
                    for (j2--; ai[j2] > i2; j2--)
                        ;
                    for (i1++; ai[i1] < i2; i1++)
                        ;
                    while (i1 <= j2)
                    {
                        if (ai[j2] != ai[i1])
                        {
                            int k2 = ai[j2];
                            ai[j2] = ai[i1];
                            ai[i1] = k2;
                        }
                        for (j2--; ai[j2] > i2; j2--)
                            ;
                        i1++;
                        while (ai[i1] < i2)
                        {
                            i1++;
                        }
                    }
                    if (j2 - k <= l - i1)
                    {
                        ai1[j] = i1;
                        ai2[j] = l;
                        l = j2;
                        j++;
                    }
                    else
                    {
                        ai1[j] = k;
                        ai2[j] = j2;
                        k = i1;
                        j++;
                    }
                }
                while (l - k >= 11);
            }
            while (k == 0);
            k--;
            while (++k != l)
            {
                int j1 = ai[k + 1];
                if (ai[k] > j1)
                {
                    int l1 = k;
                    ai[l1 + 1] = ai[l1];
                    for (l1--; j1 < ai[l1]; l1--)
                    {
                        ai[l1 + 1] = ai[l1];
                    }

                    ai[l1 + 1] = j1;
                }
            }
        }
        while (true);
    }
}
