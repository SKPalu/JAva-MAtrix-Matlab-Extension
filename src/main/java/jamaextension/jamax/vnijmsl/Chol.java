/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

//import jamaextension.jamax.Matrix;
//import jamaextension.jamax.SingularMatrixException;
import java.io.Serializable;

/**
 * 
 * @author Feynman Perceptrons
 */
public class Chol implements Serializable, Cloneable
{

    public static class NotSPDException extends Exception
    {

        static final long serialVersionUID = 0x83abecb88c61a63cL;

        public NotSPDException()
        {
            super("NotSPDException.Message");
        }
    }

    static final long serialVersionUID = 0x3647069559cc2c4eL;
    private double factor[][];

    public Chol(jamaextension.jamax.Matrix A) throws SingularMatrixException, NotSPDException
    {
        // Matrix.checkSquareMatrix(ad);
        if (!A.isSquare())
        {
            throw new IllegalArgumentException("Chol : Parameter \"A\" must be a square matrix.");
        }
        double ad[][] = A.getArray();
        int i = ad.length;
        factor = copy(ad);
        for (int j = 0; j < i; j++)
        {
            double ad1[] = factor[j];
            double d = 0.0D;
            for (int i1 = 0; i1 < j; i1++)
            {
                d += ad1[i1] * ad1[i1];
            }

            if (ad1[j] < d)
            {
                throw new NotSPDException();
            }

            ad1[j] = Math.sqrt(ad1[j] - d);
            for (int j1 = j + 1; j1 < i; j1++)
            {
                double ad2[] = factor[j1];
                double d1 = 0.0D;
                for (int k1 = 0; k1 < j; k1++)
                {
                    d1 += factor[j1][k1] * ad1[k1];
                }

                ad2[j] = (ad2[j] - d1) / ad1[j];
            }

        }

        for (int k = 0; k < i; k++)
        {
            if (factor[k][k] == 0.0D)
            {
                throw new SingularMatrixException();
            }
            for (int l = k + 1; l < i; l++)
            {
                factor[k][l] = 0.0D;
            }

        }

    }

    public jamaextension.jamax.Matrix getR()
    {
        return new jamaextension.jamax.Matrix(factor);
    }

    public double[] solve(double ad[])
    {
        int i = factor.length;
        double ad1[] = (double[]) ad.clone();
        for (int j = 0; j < i; j++)
        {
            double ad2[] = factor[j];
            double d = 0.0D;
            for (int i1 = 0; i1 < j; i1++)
            {
                d += ad2[i1] * ad1[i1];
            }

            ad1[j] -= d;
            ad1[j] /= ad2[j];
        }

        for (int k = i - 1; k >= 0; k--)
        {
            double d1 = 0.0D;
            for (int l = k + 1; l < i; l++)
            {
                d1 += factor[l][k] * ad1[l];
            }

            ad1[k] -= d1;
            ad1[k] /= factor[k][k];
        }

        return ad1;
    }

    public jamaextension.jamax.Matrix inverse()
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

        return new jamaextension.jamax.Matrix(ad1);
    }

    public void update(double ad[])
    {
        int i = factor.length;
        double ad1[] = new double[i];
        double ad2[] = new double[i];
        double ad3[] = new double[1];
        double ad4[] = new double[1];
        double ad5[] = new double[1];
        double ad6[] = new double[1];
        for (int j = 0; j < i; j++)
        {
            double ad7[] = factor[j];
            double d = ad[j];
            for (int k = 0; k < j; k++)
            {
                double d1 = ad1[k] * ad7[k] + ad2[k] * d;
                d = ad1[k] * d - ad2[k] * ad7[k];
                ad7[k] = d1;
            }

            ad3[0] = ad7[j];
            ad4[0] = d;
            BLAS.rotg(ad3, ad4, ad5, ad6);
            ad7[j] = ad3[0];
            ad1[j] = ad5[0];
            ad2[j] = ad6[0];
        }

    }

    public void downdate(double ad[]) throws NotSPDException
    {
        int i = factor.length;
        double ad1[] = new double[i];
        double ad2[] = new double[i];
        for (int j = 0; j < i; j++)
        {
            double ad3[] = factor[j];
            ad2[j] = ad[j] - BLAS.dot(j, ad3, 0, ad2, 0);
            ad2[j] /= ad3[j];
        }

        double d = BLAS.nrm2(i, ad2, 0);
        double d2 = Math.sqrt(1.0D - d * d);
        if (Double.isNaN(d2))
        {
            throw new NotSPDException();
        }
        for (int k = i - 1; k >= 0; k--)
        {
            double d3 = d2 + Math.abs(ad2[k]);
            double d5 = d2 / d3;
            double d6 = ad2[k] / d3;
            double d1 = Math.sqrt(d5 * d5 + d6 * d6);
            ad1[k] = d5 / d1;
            ad2[k] = d6 / d1;
            d2 = d3 * d1;
        }

        for (int l = 0; l < i; l++)
        {
            double ad4[] = factor[l];
            double d4 = 0.0D;
            for (int i1 = l; i1 >= 0; i1--)
            {
                double d7 = ad1[i1] * d4 + ad2[i1] * ad4[i1];
                ad4[i1] = ad1[i1] * ad4[i1] - ad2[i1] * d4;
                d4 = d7;
            }

        }

    }

    private static double[][] copy(double ad[][])
    {
        int i = ad.length;
        double ad1[][] = new double[i][ad[0].length];
        for (int j = 0; j < i; j++)
        {
            System.arraycopy(ad[j], 0, ad1[j], 0, ad.length);
        }

        return ad1;
    }

    public static void main(String[] args)
    {
        double[][] d =
        {
                {
                        0.0442, -0.0232
                },
                {
                        -0.0233, 0.0222
                }
        };// {{1, -1},{ -1, 2}};
        jamaextension.jamax.Matrix A = new jamaextension.jamax.Matrix(d);
        try
        {
            Chol chol = new Chol(A);
            jamaextension.jamax.Matrix R = chol.getR();
            System.out.println("--------------- R ---------------");
            R.print(8, 4);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        // Matrix R = chol.getLR();
        // System.out.println(" chol.isSPD = "+chol.isSPD());
    }
}// ------------------------------ End Class Definition
// -------------------------
