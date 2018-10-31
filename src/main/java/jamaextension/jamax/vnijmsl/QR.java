/*
 * QR.java
 *
 * Created on 26 April 2007, 00:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.vnijmsl;

import jamaextension.jamax.Matrix;

public class QR implements Cloneable
{

    private double qr[][];
    private double qraux[];
    private int jpvt[];
    private static final double EPSILON_LARGE = 2.2204460492503E-016D;
    private static final double thres[] =
    {
            1.0010415475916E-146D, 4.4989137945432004E+161D, 2.2227587494851002E-162D, 1.9979190722022E+146D,
            5.0104209000224002E-293D, 1.9958403095347001E+292D
    };

    public QR(Matrix A)
    {
        buildQR(A);
    }

    public QR(double[][] ad)
    {
        this(new Matrix(ad));
    }

    private void buildQR(Matrix A)
    {
        double ad[][] = A.getArray();

        int i = ad.length;
        int j = ad[0].length;
        int k = i;
        int l = j;
        int i1 = Math.min(k, l);
        qr = A.transpose().getArray();// Matrix.transpose(ad);
        qraux = new double[l];
        jpvt = new int[l];
        double ad1[] = new double[l];
        int j1 = 1;
        int k1 = 0;
        boolean flag = false;
        boolean flag1 = false;
        for (int k2 = 0; k2 < l; k2++)
        {
            boolean flag2 = jpvt[k2] > 0;
            boolean flag3 = jpvt[k2] < 0;
            jpvt[k2] = k2 + 1;
            if (flag3)
                jpvt[k2] = -k2 - 1;
            if (!flag2)
                continue;
            if (k2 + 1 != j1)
                swap(k, qr[j1 - 1], 0, qr[k2 - 1], 0);
            jpvt[k2] = jpvt[j1 - 1];
            jpvt[j1 - 1] = k2 + 1;
            j1++;
        }

        k1 = l;
        for (int l2 = l - 1; l2 >= 0; l2--)
        {
            if (jpvt[l2] >= 0)
                continue;
            jpvt[l2] = -jpvt[l2];
            if (l2 + 1 != k1)
            {
                swap(k, qr[k1 - 1], 0, qr[l2], 0);
                int k3 = jpvt[k1 - 1];
                jpvt[k1 - 1] = jpvt[l2];
                jpvt[l2] = k3;
            }
            k1--;
        }

        if (k1 >= j1)
        {
            for (int i3 = j1 - 1; i3 < k1; i3++)
            {
                qraux[i3] = norm2(k, qr[i3], 0);
                ad1[i3] = qraux[i3];
            }

        }
        i1 = Math.min(k, l);
        for (int j3 = 1; j3 <= i1; j3++)
        {
            int l1 = j3 - 1;
            if (j3 >= j1 && j3 < k1)
            {
                double d = 0.0D;
                int l3 = l1;
                for (int k4 = j3; k4 <= k1; k4++)
                {
                    int i2 = k4 - 1;
                    if (qraux[i2] > d)
                    {
                        d = qraux[i2];
                        l3 = i2;
                    }
                }

                if (l3 != l1)
                {
                    swap(k, qr[l1], 0, qr[l3], 0);
                    qraux[l3] = qraux[l1];
                    ad1[l3] = ad1[l1];
                    int l4 = jpvt[l3];
                    jpvt[l3] = jpvt[l1];
                    jpvt[l1] = l4;
                }
            }
            qraux[l1] = 0.0D;
            if (j3 == k)
                continue;
            double d1 = norm2((k - j3) + 1, qr[l1], l1);
            if (d1 == 0.0D)
                continue;
            if (qr[l1][l1] != 0.0D)
            {
                d1 = Math.abs(d1);
                if (qr[l1][l1] < 0.0D)
                    d1 = -d1;
            }
            for (int i4 = 0; i4 < (k - j3) + 1; i4++)
                qr[l1][l1 + i4] /= d1;

            qr[l1][l1]++;
            if (l >= j3 + 1)
            {
                for (int j4 = j3 + 1; j4 <= l; j4++)
                {
                    int j2 = j4 - 1;
                    double d2 = 0.0D;
                    for (int i5 = l1; i5 < k; i5++)
                        d2 += qr[l1][i5] * qr[j2][i5];

                    d2 /= -qr[l1][l1];
                    for (int j5 = l1; j5 < k; j5++)
                        qr[j2][j5] += d2 * qr[l1][j5];

                    if (j4 < j1 || j4 > k1 || qraux[j2] == 0.0D)
                        continue;
                    double d3 = 1.0D - (Math.abs(qr[j2][l1]) / qraux[j2]) * (Math.abs(qr[j2][l1]) / qraux[j2]);
                    d3 = Math.max(d3, 0.0D);
                    d2 = d3;
                    d3 = 1.0D + 0.050000000000000003D * d3 * (qraux[j2] / ad1[j2]) * (qraux[j2] / ad1[j2]);
                    if (d3 == 1.0D)
                    {
                        qraux[j2] = norm2(k - j3, qr[j2], j3);
                        ad1[j2] = qraux[j2];
                    }
                    else
                    {
                        qraux[j2] = qraux[j2] * Math.sqrt(d2);
                    }
                }

            }
            qraux[l1] = qr[l1][l1];
            qr[l1][l1] = -d1;
        }

    }

    public Matrix getQ()
    {
        int l2 = qr.length;
        int i3 = qr[0].length;
        int k2 = Math.min(l2, i3);
        double ad[][] = new double[i3][i3];
        for (int i = 0; i < k2; i++)
        {
            for (int j1 = i; j1 < i3; j1++)
            {
                ad[i][j1] = qr[i][j1];
            }
        }

        for (int k1 = k2; k1 < i3; k1++)
        {
            ad[k1][k1] = 1.0D;
        }

        double ad1[] = new double[i3];
        double ad2[] = new double[i3];

        label0: for (int j2 = k2 - 1; j2 >= 0; j2--)
        {
            ad1[j2] = qraux[j2];
            for (int j = j2 + 1; j < i3; j++)
            {
                ad1[j] = ad[j2][j];
            }

            ad[j2][j2] = 1.0D;
            for (int k = j2 + 1; k < i3; k++)
            {
                ad[j2][k] = 0.0D;
            }

            if (Math.abs(ad1[j2]) < 4.9406564584124654E-324D)
            {
                continue;
            }
            double d = 1.0D / ad1[j2];
            for (int l = j2; l < i3; l++)
            {
                ad2[l - j2] = 0.0D;
                for (int l1 = j2; l1 < i3; l1++)
                {
                    ad2[l - j2] += d * ad[l][l1] * ad1[l1];
                }
            }

            int i1 = j2;
            do
            {
                if (i1 >= i3)
                {
                    continue label0;
                }
                for (int i2 = j2; i2 < i3; i2++)
                {
                    ad[i1][i2] -= ad2[i1 - j2] * ad1[i2];
                }

                i1++;
            }
            while (true);
        }

        return (new Matrix(ad)).transpose();
    }

    public Matrix getR()
    {
        int i = qr.length;
        int j = qr[0].length;
        double ad[][] = new double[j][i];
        for (int k = 0; k < j; k++)
        {
            for (int l = k; l < i; l++)
                ad[k][l] = qr[l][k];

        }

        return new Matrix(ad);
    }

    public int getRank()
    {
        return rank(2.2204460492503E-016D);
    }

    public int rank(double d)
    {
        int k = 0;
        int j = Math.min(qr.length, qr[0].length);
        double d1 = d * Math.abs(qr[0][0]);
        for (int i = 0; i < j && Math.abs(qr[i][i]) > d1; i++)
            k++;

        return k;
    }

    public int[] getPermute()
    {
        int ai[] = (int[]) jpvt.clone();
        for (int i = 0; i < ai.length; i++)
            ai[i]--;

        return ai;
    }

    public double[] solve(double ad[]) throws SingularMatrixException
    {
        return solve(ad, 2.2204460492503E-016D);
    }

    public double[] solve(double ad[], double d) throws SingularMatrixException
    {
        int i = qr[0].length;
        int j = qr.length;
        int k = rank(d);
        int l = Math.min(k, i - 1);
        double ad1[] = new double[j];
        if (l == 0)
            if (qr[0][0] == 0.0D)
            {
                throw new SingularMatrixException();
            }
            else
            {
                ad1[0] = ad[0] / qr[0][0];
                return ad1;
            }
        double ad2[] = QTb(ad, l);
        System.arraycopy(ad2, 0, ad1, 0, k);
        for (int j1 = k - 1; j1 >= 0; j1--)
        {
            double ad3[] = qr[j1];
            if (ad3[j1] == 0.0D)
            {
                throw new SingularMatrixException();
            }
            ad1[j1] /= ad3[j1];
            double d1 = -ad1[j1];
            for (int i2 = 0; i2 < j1; i2++)
                ad1[i2] += d1 * ad3[i2];

        }

        ad2 = fromIntToDouble((int[]) jpvt.clone());
        for (int k1 = 0; k1 < ad1.length; k1++)
        {
            ad2[k1] = -ad2[k1] + 1;
        }

        for (int l1 = 0; l1 < ad1.length; l1++)
        {
            if (ad2[l1] >= 0.0)
            {
                continue;
            }
            ad2[l1] = -ad2[l1];
            for (int i1 = (int) ad2[l1]; i1 != l1; i1 = (int) ad2[i1])
            {
                double d2 = ad1[l1];
                ad1[l1] = ad1[i1];
                ad1[i1] = d2;
                ad2[i1] = -ad2[i1];
            }

        }

        return ad1;
    }

    private double[] fromIntToDouble(int[] I)
    {
        int len = I.length;
        double[] D = new double[len];
        for (int i = 0; i < len; i++)
        {
            D[i] = I[i];
        }
        return D;
    }

    private double[] QTb(double ad[], int i)
    {
        int j = qr[0].length;
        if (j != ad.length)
        {
            Object aobj[] =
            {
                    new Integer(j), new Integer(ad.length)
            };
            new IllegalArgumentException("QTb : LU.SizeofB = [" + j + " , " + ad.length + "]");
        }
        double ad1[];
        if (i == 0)
        {
            ad1 = new double[1];
            ad1[0] = ad[0];
        }
        else
        {
            ad1 = (double[]) ad.clone();
            for (int k = 0; k < i; k++)
            {
                double ad2[] = qr[k];
                if (qraux[k] == 0.0D)
                    continue;
                double d1 = ad2[k];
                ad2[k] = qraux[k];
                double d = 0.0D;
                for (int l = k; l < j; l++)
                    d += ad2[l] * ad1[l];

                d /= -qr[k][k];
                for (int i1 = k; i1 < j; i1++)
                    ad1[i1] += d * ad2[i1];

                ad2[k] = d1;
            }

        }
        return ad1;
    }

    private static double norm2(int i, double ad[], int j)
    {
        double d4 = 0.0D;
        double d = 0.0D;
        for (int k = 1; k <= i; k++)
        {
            d += Math.abs(ad[(k - 1) + j]);
            if (d > thres[3])
            {
                d = thres[4];
                double d2 = thres[5];
                for (int i1 = 0; i1 < i; i1++)
                {
                    double d5 = d * ad[i1 + j];
                    d4 += d5 * d5;
                }

                return Math.sqrt(d4) * d2;
            }
        }

        if (d < (double) i * thres[0])
        {
            double d1 = thres[1];
            double d3 = thres[2];
            for (int j1 = 0; j1 < i; j1++)
            {
                double d6 = d1 * ad[j1 + j];
                d4 += d6 * d6;
            }

            d4 = Math.sqrt(d4) * d3;
        }
        else
        {
            for (int l = 0; l < i; l++)
            {
                double d7 = ad[l + j];
                d4 += d7 * d7;
            }

            d4 = Math.sqrt(d4);
        }
        return d4;
    }

    public static void swap(int i, double ad[], int j, double ad1[], int k)
    {
        for (int l = 0; l < i; l++)
        {
            double d = ad[l + j];
            ad[l + j] = ad1[l + k];
            ad1[l + k] = d;
        }

    }

    public static void swap(int i, double ad[], int j, int k, double ad1[], int l, int i1)
    {
        if (i > 0)
            if (k != 1 || i1 != 1)
            {
                int l1 = 1;
                int i2 = 1;
                if (k < 0)
                    l1 = (-i + 1) * k + 1;
                if (i1 < 0)
                    i2 = (-i + 1) * i1 + 1;
                for (int j1 = 1; j1 <= i; j1++)
                {
                    double d = ad[(j + l1) - 1];
                    ad[(j + l1) - 1] = ad1[(l + i2) - 1];
                    ad1[(l + i2) - 1] = d;
                    l1 += k;
                    i2 += i1;
                }

            }
            else
            {
                for (int k1 = 1; k1 <= i; k1++)
                {
                    double d1 = ad[k1 - 1];
                    ad[(j + k1) - 1] = ad1[(l + k1) - 1];
                    ad1[(l + k1) - 1] = d1;
                }

            }
    }

    public static void main(String[] args)
    {
        double[][] A =
        {
            {
                    0.0153, 0.7468, 0.4451, 0.9318, 0.4660, 0.4186, 0.8462
            }
        };// {{0.1987, 0.6038, 0.2722, 0.1988}};
        double[] b =
        {
            0.3000
        };
        QR qr = new QR(A);
        try
        {
            double[] sol = qr.solve(b);
            Matrix C = new Matrix(sol).toColVector();
            System.out.println("----- C -----");
            C.print(8, 4);
        }
        catch (SingularMatrixException sm)
        {
            sm.printStackTrace();
        }

    }

}
