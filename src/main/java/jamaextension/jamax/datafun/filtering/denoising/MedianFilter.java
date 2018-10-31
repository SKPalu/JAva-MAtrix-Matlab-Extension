/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun.filtering.denoising;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.TestData;
import jamaextension.jamax.datafun.Median;

/**
 * 
 * @author Sione
 */
public class MedianFilter
{

    private Matrix sigOut;

    public MedianFilter(Matrix sigIn, int p)
    {
        if (sigIn == null)
        {
            throw new ConditionalException("MedianFilter : Parameter \"sigIn\" must be non-null.");
        }
        if (sigIn.isVector() && sigIn.isRowVector())
        {
            sigIn = sigIn.toColVector();
        }
        // [N,D] = size(sigIn);
        int N = sigIn.getRowDimension();
        int D = sigIn.getColumnDimension();
        if (p < 1)
        {
            throw new ConditionalException("MedianFilter : Parameter \"p\" must be positive.");
        }

        int p2 = (p - 1) / 2; // half filter size
        sigOut = Matrix.zeros(N, D); // output is same size as input

        for (int n = 0; n < (N - p); n++)
        {
            // x = sigIn(n:n+p-1,:);
            int[] ind = Indices.linspace(n, n + p - 1).getRowPackedCopy();
            Matrix x = sigIn.getRows(ind);
            // sig_out(n+p2,:) = median(x);
            Median med = new Median(x);
            x = med.getMedian();
            sigOut.setRowAt(n + p2, x);
        }// end; %
    }

    /**
     * @return the sigOut
     */
    public Matrix getSigOut()
    {
        return sigOut;
    }

    public static void main(String[] args)
    {
        Matrix sigIn = TestData.testMat1().getRowAt(0);
        sigIn = sigIn.abs();
        sigIn.printInLabel("sigIn");
        MedianFilter MF = new MedianFilter(sigIn, 3);
        Matrix sigOut = MF.getSigOut();
        sigOut.printInLabel("sigOut", 0);
    }
}
