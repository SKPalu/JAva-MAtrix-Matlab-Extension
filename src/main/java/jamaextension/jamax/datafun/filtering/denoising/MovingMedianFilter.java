package jamaextension.jamax.datafun.filtering.denoising;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.TestData;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.Median;

public class MovingMedianFilter extends DataSmoothingFilter
{

    public MovingMedianFilter(Matrix data, Dimension dim)
    {
        super(data, dim);
    }

    public void filter()
    {

        Matrix sigIn = this.dataIn;

        boolean colDim = this.dim == Dimension.COL ? true : false;
        if (!colDim)
        {
            sigIn = sigIn.transpose();
            System.out.println("EXECUTE");
        }

        /*
         * if (sigIn.isVector() && sigIn.isRowVector()) { sigIn =
         * sigIn.toColVector(); }
         */

        int p = 4;

        if (!this.isEmptyParameters() && this.parameters.containsKey("filterlength"))
        {
            p = (Integer) parameters.getKey("filterlength");
        }

        // [N,D] = size(sigIn);
        int N = sigIn.getRowDimension();
        int D = sigIn.getColumnDimension();
        if (p < 1)
        {
            throw new ConditionalException("MedianFilter : Parameter \"p\" must be positive.");
        }

        int p2 = (p - 1) / 2; // half filter size
        Matrix sigOut = Matrix.zeros(N, D); // output is same size as input

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

        if (!colDim)
        {
            sigOut = sigOut.transpose();
        }

        this.dataOut = sigOut;

        filtered = true;
    }

    public static void main(String[] args)
    {
        Matrix sigIn = TestData.testMat1();// .getRowAt(0);
        sigIn.printInLabel("sigIn");

        int flen = 2;

        DataSmoothingFilter MF = new MovingMedianFilter(sigIn, Dimension.COL);// ,
                                                                              // 3);
        MF.getParameters().add("filterlength", flen);
        MF.filter();

        Matrix sigOut = MF.getDataOut();
        sigOut.printInLabel("sigOut1");

        MF = new MovingMedianFilter(sigIn, Dimension.ROW);// ,
        // 3);
        MF.getParameters().add("filterlength", flen);
        MF.filter();

        sigOut = MF.getDataOut();
        sigOut.printInLabel("sigOut2");
    }

}
