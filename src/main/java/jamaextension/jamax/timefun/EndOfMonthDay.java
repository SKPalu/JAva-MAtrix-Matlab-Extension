/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.timefun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.MatrixUtil;

/**
 * 
 * @author Feynman Perceptrons
 */
public class EndOfMonthDay
{

    private double[] DPM =
    {
            31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
    };
    private Matrix dpm = new Matrix(DPM).toColVector();
    private Matrix endDate;

    public EndOfMonthDay(Indices Y, Indices M)
    {
        if (Y == null)
        {
            throw new IllegalArgumentException("EndOfMonthDay : Parameter \"Y\" must be non-null.");
        }
        if (!Y.isVector())
        {
            throw new IllegalArgumentException("EndOfMonthDay : Parameter \"Y\" must be a vector and not a matrix.");
        }

        if (M == null)
        {
            throw new IllegalArgumentException("EndOfMonthDay : Parameter \"M\" must be non-null.");
        }
        if (!M.isVector())
        {
            throw new IllegalArgumentException("EndOfMonthDay : Parameter \"M\" must be a vector and not a matrix.");
        }

        if (M.LT(0).OR(M.GT(11)).anyBoolean())
        {
            throw new IllegalArgumentException(
                    "EndOfMonthDay : Elements of vector parameter \"M\" must fall in the interval [0 , 11] (ie, Java month index).");
        }

        if (Y.length() != M.length())
        {
            throw new IllegalArgumentException("EndOfMonthDay : Parameter \"Y\" and \"M\" must be have equal sizes.");
        }

        if (Y.length() != 1 && Y.isRowVector())
        {
            Y = Y.toColVector();
        }
        if (M.length() != 1 && M.isRowVector())
        {
            M = M.toColVector();
        }

        // Make result the right size and orientation.
        // Indices d = Y.minus(M);
        int[] arr = M.getColIndicesAt(0);
        Matrix d = dpm.getElements(arr);

        Matrix yDouble = Matrix.indicesToMatrix(Y);

        // ( (rem(y,4) == 0 & rem(y,100) ~= 0 ) | rem(y,400) == 0 )
        Indices Ind = MatrixUtil.rem(yDouble, 4.0).EQ(0.0).AND(MatrixUtil.rem(yDouble, 100.0).NEQ(0.0))
                .OR(MatrixUtil.rem(yDouble, 400.0).EQ(0.0)).AND(M.EQ(1));// (m
                                                                         // ==
                                                                         // 2) &
                                                                         // ((rem(y,4)
                                                                         // == 0
                                                                         // &
                                                                         // rem(y,100)
                                                                         // ~=
                                                                         // 0) |
                                                                         // rem(y,400)
                                                                         // ==
                                                                         // 0);
        Ind = Ind.find();
        if (Ind != null)
        {
            d.setFromFind(Ind, 29.0);
        }

        endDate = d;
    }

    public Matrix getEndDate()
    {
        return endDate;
    }
}
