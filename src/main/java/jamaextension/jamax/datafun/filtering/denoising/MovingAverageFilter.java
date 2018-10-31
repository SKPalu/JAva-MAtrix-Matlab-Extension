package jamaextension.jamax.datafun.filtering.denoising;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;

public class MovingAverageFilter extends DataSmoothingFilter
{

    public MovingAverageFilter(Matrix mat, Dimension dim)
    {

        super(mat, dim);

        // len = length(Y);
        // X = 1:len;

        // len2 = 6;

        // coeff = ones(1, len2)/len2;
        // Yfil = filter(coeff, 1,Y);
    }

    public void filter()
    {

        int row = dataIn.getRowDimension();
        int col = dataIn.getColumnDimension();
        int len = dataIn.length();
        Matrix coeff = null;
        double[] one =
        {
            1
        };
        double[] vecB = null;

        // default window length
        int win = 4;
        if (!this.isEmptyParameters() && this.parameters.containsKey("window"))
        {
            win = (Integer) parameters.getKey("window");
        }

        if (!this.isDataInMatrix())
        {
            coeff = Matrix.ones(1, win).arrayRightDivide(win);// /len2;
            vecB = coeff.getRowPackedCopy();
            boolean tf = this.dataIn.isRowVector();
            double[] arr = this.dataIn.getRowPackedCopy();
            arr = JDatafun.filter(vecB, one, arr);
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
                coeff = Matrix.ones(1, win).arrayRightDivide(win);
                vecB = coeff.getRowPackedCopy();
                for (int i = 0; i < row; i++)
                {
                    Matrix temp = this.dataIn.getRowAt(i);
                    double[] arr1 = temp.getColumnPackedCopy();
                    arr1 = JDatafun.filter(vecB, one, arr1);
                    temp = new Matrix(arr1);
                    this.dataOut.setRowAt(i, temp);
                }
            }
            else
            {
                coeff = Matrix.ones(1, win).arrayRightDivide(win);
                vecB = coeff.getRowPackedCopy();
                for (int j = 0; j < col; j++)
                {
                    Matrix temp2 = this.dataIn.getColumnAt(j);
                    double[] arr2 = temp2.getColumnPackedCopy();
                    arr2 = JDatafun.filter(vecB, one, arr2);
                    temp2 = new Matrix(arr2).toColVector();
                    this.dataOut.setColumnAt(j, temp2);
                }
            }
        }

        filtered = true;
    }

    public static void main(String[] args)
    {
        Matrix data = Matrix.rand(1, 8).scale(10.0).round();
        data.printInLabel("data",0);
        
        //"window"
        MovingAverageFilter  MA = new MovingAverageFilter(data, Dimension.ROW);
        MA.getParameters().add("window", 3);
        MA.filter();
        
        Matrix datafil = MA.getDataOut();
        datafil.printInLabel("datafil",0);
        
        
        //MA.

    }

}
