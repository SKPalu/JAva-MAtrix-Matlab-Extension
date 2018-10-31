package jamaextension.jamax.datafun.filtering.denoising;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;

public class ExponentialWeightedMovingAverageFilter extends DataSmoothingFilter
{

    public ExponentialWeightedMovingAverageFilter(Matrix data, Dimension dim)
    {
        super(data, dim);
    }

    public void filter()
    {

        int row = dataIn.getRowDimension();
        int col = dataIn.getColumnDimension();

        Matrix vecB = null;// genBinomialCoeff();

        double[] one =
        {
            1
        };
        double[] binomialCoeff = vecB.getRowPackedCopy();

        // default alpha
        double alpha = 4;
        if (!this.isEmptyParameters() && this.parameters.containsKey("alpha"))
        {
            alpha = (Double) parameters.getKey("alpha");
        }

        if (!this.isDataInMatrix())
        {

            boolean tf = this.dataIn.isRowVector();
            double[] arr = this.dataIn.getRowPackedCopy();
            arr = JDatafun.filter(binomialCoeff, one, arr);
            if (tf)
            {
                this.dataOut = new Matrix(arr);
            }
            else
            {
                this.dataOut = new Matrix(arr).transpose();
            }
        }
        else
        {// matrix
            if (dim == Dimension.COL)
            {
                for (int i = 0; i < row; i++)
                {
                    Matrix temp = this.dataIn.getRowAt(i);
                    double[] arr1 = temp.getColumnPackedCopy();
                    arr1 = JDatafun.filter(binomialCoeff, one, arr1);
                    temp = new Matrix(arr1);
                    this.dataOut.setRowAt(i, temp);
                }
            }
            else
            {
                for (int j = 0; j < col; j++)
                {
                    Matrix temp2 = this.dataIn.getColumnAt(j);
                    double[] arr2 = temp2.getColumnPackedCopy();
                    arr2 = JDatafun.filter(binomialCoeff, one, arr2);
                    temp2 = new Matrix(arr2);
                    this.dataOut.setColumnAt(j, temp2);
                }
            }
        }

        filtered = true;
    }

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
