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
import jamaextension.jamax.MatrixUtil;

// Referenced classes of package com.imsl.math:
//            SingularMatrixException, Matrix

public class LU implements Serializable, Cloneable
{

    protected double factor[][];
    protected int ipvt[];

    public LU(double ad[][]) throws SingularMatrixException
    {
        int j1 = 0;
        // Matrix.checkSquareMatrix(ad);
        MatrixUtil.checkMatrix(ad);
        if (ad.length != ad[0].length)
        {
            throw new IllegalArgumentException("LU : Matrix \"ad\" must be square.");
        }

        int k1 = ad.length;
        factor = copy(ad);
        ipvt = new int[k1];
        for (int i1 = 0; i1 < k1 - 1; i1++)
        {
            double ad1[] = factor[i1];
            double d3 = -1D;
            for (int j = i1; j < k1; j++)
            {
                double d4 = Math.abs(ad1[j]);
                if (d4 > d3)
                {
                    j1 = j;
                    d3 = d4;
                }
            }

            ipvt[i1] = j1;
            if (ad1[j1] == 0.0D)
                throw new SingularMatrixException();
            if (j1 != i1)
            {
                double d = ad1[j1];
                ad1[j1] = ad1[i1];
                ad1[i1] = d;
            }
            double d1 = -1D / ad1[i1];
            for (int k = i1 + 1; k < k1; k++)
                ad1[k] *= d1;

            for (int i = i1 + 1; i < k1; i++)
            {
                double ad2[] = factor[i];
                double d2 = ad2[j1];
                if (j1 != i1)
                {
                    ad2[j1] = ad2[i1];
                    ad2[i1] = d2;
                }
                for (int l = i1 + 1; l < k1; l++)
                    ad2[l] += d2 * ad1[l];

            }

        }

        ipvt[k1 - 1] = k1 - 1;
        if (factor[k1 - 1][k1 - 1] == 0.0D)
            throw new SingularMatrixException();
        else
            return;
    }

    public double[] solve(double ad[])
    {
        int i = factor.length;
        if (i != ad.length)
        {
            throw new IllegalArgumentException("solve : Inconsistent sizes.");
        }
        double ad1[] = (double[]) ad.clone();
        for (int j = 0; j < i; j++)
        {
            double d = 0.0D;
            double ad3[] = factor[j];
            for (int j1 = 0; j1 < j; j1++)
                d += ad3[j1] * ad1[j1];

            ad1[j] = (ad1[j] - d) / ad3[j];
        }

        for (int k = i - 2; k >= 0; k--)
        {
            double ad2[] = factor[k];
            for (int l = k + 1; l < i; l++)
                ad1[k] += ad2[l] * ad1[l];

            int i1 = ipvt[k];
            if (i1 != k)
            {
                double d1 = ad1[i1];
                ad1[i1] = ad1[k];
                ad1[k] = d1;
            }
        }

        return ad1;
    }

    public double[] solveTranspose(double ad[])
    {
        int i = factor.length;
        if (i != ad.length)
        {
            throw new IllegalArgumentException("solve : Inconsistent sizes.");
        }
        double ad1[] = (double[]) ad.clone();
        for (int j = 0; j < i - 1; j++)
        {
            int l = ipvt[j];
            double d = ad1[l];
            if (l != j)
            {
                ad1[l] = ad1[j];
                ad1[j] = d;
            }
            double ad3[] = factor[j];
            for (int j1 = j + 1; j1 < i; j1++)
                ad1[j1] += d * ad3[j1];

        }

        for (int k = i - 1; k >= 0; k--)
        {
            double ad2[] = factor[k];
            ad1[k] /= ad2[k];
            double d1 = -ad1[k];
            for (int i1 = 0; i1 < k; i1++)
                ad1[i1] += d1 * ad2[i1];

        }

        return ad1;
    }

    public double determinant()
    {
        double d = 1.0D;
        double d1 = 1.0D;
        int i = factor.length;
        for (int j = 0; j < i; j++)
            d *= factor[j][j];

        for (int k = 0; k < i; k++)
            if (ipvt[k] > k)
                d1 = -d1;

        return d1 * d;
    }

    public static double[] solve(double ad[][], double ad1[]) throws SingularMatrixException
    {
        MatrixUtil.checkMatrix(ad);
        int i = ad.length;
        int j = ad[0].length;
        if (i != ad1.length)
        {
            throw new IllegalArgumentException("solve : Inconsistent sizes.");
        }
        LU lu = new LU(ad);
        return lu.solve(ad1);
    }

    public double[][] inverse()
    {
        int i = factor.length;
        double ad[] = new double[i];
        double ad1[][] = new double[i][i];
        for (int j = 0; j < i; j++)
        {
            ad[j] = 1.0D;
            ad1[j] = solve(ad);
            ad[j] = 0.0D;
        }

        return (new Matrix(ad1)).transpose().getArray();
    }

    public double condition(double ad[][])
    {

        int i = ad.length;
        double d = new Matrix(ad).norm1();// Matrix.oneNorm(ad);
        double ad1[] = new double[i];
        double ad2[] = new double[i];
        for (int j = i - 1; j >= 0; j--)
        {
            double ad3[] = factor[j];
            double d2 = (1.0D - ad2[j]) / ad3[j];
            double d4 = (-1D - ad2[j]) / ad3[j];
            double d5 = d2;
            double d6 = d4;
            for (int j1 = 0; j1 < j; j1++)
            {
                d5 += (ad2[j] + ad3[j1] * d2) / factor[j1][j1];
                d6 += (ad2[j] + ad3[j1] * d4) / factor[j1][j1];
            }

            ad1[j] = d5 < d6 ? d4 : d2;
            for (int k1 = 0; k1 < j; k1++)
                ad2[k1] += ad3[k1] * ad1[j];

        }

        ad2 = null;
        double d1 = 0.0D;
        for (int k = 0; k < i; k++)
            d1 += Math.abs(ad1[k]);

        for (int l = 0; l < i; l++)
            ad1[l] /= d1;

        double ad4[] = solve(ad1);
        double d3 = 0.0D;
        for (int i1 = 0; i1 < i; i1++)
            d3 += Math.abs(ad4[i1]);

        return 1.0D / (d * d3);
    }

    private static double[][] copy(double ad[][])
    {
        int i = ad.length;
        double ad1[][] = new double[i][ad[0].length];
        for (int j = 0; j < i; j++)
            System.arraycopy(ad[j], 0, ad1[j], 0, ad.length);

        return ad1;
    }

}// -------------------- End Class Definition
// -----------------------------------