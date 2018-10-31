package jamaextension.jamax.datafun.filtering.denoising;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

public class FastOneDimMedianFilter extends DataSmoothingFilter
{

    public FastOneDimMedianFilter(Matrix mat, Dimension dim)
    {
        super(mat, dim);
        if (!mat.isVector())
        {
            throw new ConditionalRuleException(
                    "FastOneDimMedianFilter : Input argument \"mat\" must be a vector and not a matrix.");
        }
    }

    public FastOneDimMedianFilter(Matrix mat)
    {
        this(mat, Dimension.COL);
    }

    @Override
    public void filter()
    {
        Matrix sigIn = this.dataIn;

        /*
         * boolean colDim = this.dim == Dimension.COL ? true : false; if
         * (!colDim) { sigIn = sigIn.transpose(); System.out.println("EXECUTE");
         * }
         */

        /*
         * if (sigIn.isVector() && sigIn.isRowVector()) { sigIn =
         * sigIn.toColVector(); }
         */

        int W = 3;

        if (!this.isEmptyParameters() && this.parameters.containsKey("window"))
        {
            W = (Integer) parameters.getKey("window");
        }

        // [N,D] = size(sigIn);
        int N = sigIn.length();
        int D = sigIn.getColumnDimension();
        if (W < 1)
        {
            throw new ConditionalException("MedianFilter : Parameter \"p\" must be positive.");
        }

    }

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
