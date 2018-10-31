/*
 * SampleMatrices.java
 *
 * Created on 6 November 2007, 23:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;

/**
 * 
 * @author Feynman Perceptrons
 */
public final class SampleMatrices
{

    /**
     * Creates a new instance of SampleMatrices
     */
    private SampleMatrices()
    {
    }

    public static Matrix generateMatrix()
    {
        /*
         * x = [1, 3, 5, 9, 17; 7, 4, 4, 19, 11; 16, 4, 0, 9, 4; 0, 12, 15, 8,
         * 13]
         */
        double[][] A = new double[][]
        {
                {
                        1, 3, 5, 9, 17
                },
                {
                        7, 4, 4, 19, 11
                },
                {
                        16, 4, 0, 9, 4
                },
                {
                        0, 12, 15, 8, 13
                }
        };
        return new Matrix(A);
    }

    public static Indices generateIndices()
    {
        int[][] A = new int[][]
        {
                {
                        1, 3, 5, 9, 17
                },
                {
                        7, 4, 4, 19, 11
                },
                {
                        16, 4, 0, 9, 4
                },
                {
                        0, 12, 15, 8, 13
                }
        };
        return new Indices(A);
    }

    public static Matrix genMinMaxMatrix()
    {
        double[][] x =
        {
                // {-9, 24, -12, -2, -14, -8, 24},
                {
                        Double.NaN, Double.NaN, -12, -2, -14, -8, 24
                },
                {
                        -33, -1, 44, -17, 17, 14, -24
                },
                {
                        3, 7, -3, 6, 25, 16, 0
                },
                {
                        6, 3, 2, -27, -32, 14, -3
                },
                {
                        -23, -4, 21, 14, -29, 26, -32
                },
                {
                        24, 15, 1, 32, 11, 13, 5
                }
        };

        return new Matrix(x);
    }
}
