/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

import jamaextension.jamax.Matrix;

/**
 * 
 * @author Feynman Perceptrons
 */
public class CubicSpline extends SplineJmsl
{

    static final long serialVersionUID = 0x5a5692572cdd49a5L;

    public CubicSpline(Matrix ad, Matrix ad1)
    {
        this(ad, ad1, InterpConstant.NOT_A_KNOT, 0.0D, InterpConstant.NOT_A_KNOT, 0.0D);
    }

    public CubicSpline(Matrix X, Matrix Y, InterpConstant i, double d, InterpConstant j, double d1)
    {
        if (X == null)
        {
            throw new IllegalArgumentException("CubicSpline : Parameter \"X\" must be non-null.");
        }
        if (!X.isVector())
        {
            throw new IllegalArgumentException("CubicSpline : Parameter \"X\" must be a vector and not a matrix.");
        }

        if (Y == null)
        {
            throw new IllegalArgumentException("CubicSpline : Parameter \"Y\" must be non-null.");
        }
        if (!Y.isVector())
        {
            throw new IllegalArgumentException("CubicSpline : Parameter \"Y\" must be a vector and not a matrix.");
        }

        double ad[] = X.getRowPackedCopy();
        double ad1[] = Y.getRowPackedCopy();
        int l1 = ad1.length;
        double d5 = 0.0D;
        if (l1 < 2)
        {
            // Object aobj[] = { "xData", new Integer(l1), new Integer(2) };
            // throw new IllegalArgumentException("TooFewPoints");
            throw new IllegalArgumentException("CubicSpline : There are too few points and at least 2 are needed.");
        }
        copyAndSortData(ad, ad1);
        int i1 = l1 - 1;
        for (int j1 = 1; j1 < l1; j1++)
        {
            coef[j1][2] = breakPoint[j1] - breakPoint[j1 - 1];
            coef[j1][3] = (coef[j1][0] - coef[j1 - 1][0]) / coef[j1][2];
        }

        switch (i)
        {
        case NOT_A_KNOT: // '\0'
            if (l1 == 2)
            {
                coef[0][3] = 1.0D;
                coef[0][2] = 1.0D;
                coef[0][1] = 2D * coef[1][3];
            }
            else
            {
                coef[0][3] = coef[2][2];
                coef[0][2] = coef[1][2] + coef[2][2];
                coef[0][1] = ((coef[1][2] + 2D * coef[0][2]) * coef[1][3] * coef[2][2] + Math.pow(coef[1][2], 2D)
                        * coef[2][3])
                        / coef[0][2];
            }
            break;

        case FIRST_DERIVATIVE: // '\001'
            coef[0][3] = 1.0D;
            coef[0][2] = 0.0D;
            coef[0][1] = d;
            break;

        case SECOND_DERIVATIVE: // '\002'
            coef[0][3] = 2D;
            coef[0][2] = 1.0D;
            coef[0][1] = 3D * coef[1][3] - (coef[1][2] / 2D) * d;
            break;
        }

        if (l1 > 2)
        {
            for (int k1 = 1; k1 < i1; k1++)
            {
                d5 = -coef[k1 + 1][2] / coef[k1 - 1][3];
                coef[k1][1] = d5 * coef[k1 - 1][1] + 3D
                        * (coef[k1][2] * coef[k1 + 1][3] + coef[k1 + 1][2] * coef[k1][3]);
                coef[k1][3] = d5 * coef[k1 - 1][2] + 2D * (coef[k1][2] + coef[k1 + 1][2]);
            }

            switch (j)
            {
            case NOT_A_KNOT: // '\0'
                if (l1 == 3 && i == InterpConstant.NOT_A_KNOT)
                {
                    coef[l1 - 1][1] = 2D * coef[l1 - 1][3];
                    coef[l1 - 1][3] = 1.0D;
                    d5 = -1D / coef[l1 - 2][3];
                }
                break;

            case FIRST_DERIVATIVE: // '\001'
                coef[l1 - 1][1] = d1;
                break;

            case SECOND_DERIVATIVE: // '\002'
                coef[l1 - 1][1] = 3D * coef[l1 - 1][3] + (coef[l1 - 1][2] / 2D) * d1;
                coef[l1 - 1][3] = 2D;
                d5 = -1D / coef[l1 - 2][3];
                break;
            }

            if (j == InterpConstant.NOT_A_KNOT && (l1 != 3 || i != InterpConstant.NOT_A_KNOT))
            {
                d5 = coef[l1 - 2][2] + coef[l1 - 1][2];
                coef[l1 - 1][1] = ((coef[l1 - 1][2] + 2D * d5) * coef[l1 - 1][3] * coef[l1 - 2][2] + (Math.pow(
                        coef[l1 - 1][2], 2D) * (coef[l1 - 2][0] - coef[l1 - 3][0])) / coef[l1 - 2][2])
                        / d5;
                d5 = -d5 / coef[l1 - 2][3];
                coef[l1 - 1][3] = coef[l1 - 2][2];
            }
        }
        else
        {
            switch (j)
            {

            case NOT_A_KNOT: // '\0'
                if (i != InterpConstant.NOT_A_KNOT)
                {
                    coef[l1 - 1][1] = 2D * coef[l1 - 1][3];
                    coef[l1 - 1][3] = 1.0D;
                }
                else
                {
                    coef[l1 - 1][1] = coef[l1 - 1][3];
                }
                break;

            case FIRST_DERIVATIVE: // '\001'
                coef[l1 - 1][1] = d1;
                break;

            case SECOND_DERIVATIVE: // '\002'
                coef[l1 - 1][1] = 3D * coef[l1 - 1][3] + (coef[l1 - 1][2] / 2D) * d1;
                coef[l1 - 1][3] = 2D;
                break;

            default:
                break;
            }

            d5 = -1D / coef[l1 - 2][3];
        }
        if (j != InterpConstant.FIRST_DERIVATIVE
                && (l1 > 2 || j != InterpConstant.NOT_A_KNOT || i != InterpConstant.NOT_A_KNOT))
        {
            coef[l1 - 1][3] += d5 * coef[l1 - 2][2];
            coef[l1 - 1][1] = (d5 * coef[l1 - 2][1] + coef[l1 - 1][1]) / coef[l1 - 1][3];
        }
        for (int l = i1 - 1; l >= 0; l--)
        {
            coef[l][1] = (coef[l][1] - coef[l][2] * coef[l + 1][1]) / coef[l][3];
        }

        for (int k = 1; k < l1; k++)
        {
            double ad2[] = coef[k];
            double ad3[] = coef[k - 1];
            double d4 = ad2[2];
            double d2 = (ad2[0] - ad3[0]) / d4;
            double d3 = (ad3[1] + ad2[1]) - 2D * d2;
            ad3[2] = (2D * (d2 - ad3[1] - d3)) / d4;
            ad3[3] = (d3 / d4) * (6D / d4);
        }

    }
}
