package jamaextension.jamax.datafun.imputation;

import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;

public class MeanImputation extends AbstractImputation
{

    public MeanImputation(Matrix data)
    {
        super(data);
        // TODO Auto-generated constructor stub
    }

    public MeanImputation(Matrix data, Dimension rowCol)
    {
        super(data, rowCol);
        this.imputation = ImputationType.MEAN;
    }

    @Override
    public void impute()
    {
        Matrix x = this.rawData.copy();
        if (!this.containMissing)
        {
            this.imputedData = x;
            this.imputatedDone = true;
            return;
        }

        Indices missing = x.isnan();

        Matrix IM = rowMeanImpute(x, missing, this.rowCol);

        this.imputedData = IM;
        this.imputatedDone = true;
    }

    static Matrix rowMeanImpute(Matrix X, Indices missing, Dimension dim)
    {

        boolean flag = false;
        if (dim == Dimension.ROW)
        {
            flag = true;
            X = X.transpose();
            missing = missing.transpose();
        }

        int numGene = X.getRowDimension();
        int numDim = X.getColumnDimension();// size(x);
        Matrix y = X.copy();
        for (int i = 0; i < numGene; i++)
        {
            FindInd find = missing.getRowAt(i).EQ(0).findIJ();
            if (!find.isNull())
            {
                int[] arr = find.getIndex();
                Matrix tmp = X.getMatrix(i, arr);
                double rowMean = JDatafun.mean(tmp).start();
                // rowMean = mean(x(i,missing(i,:)==0));
                find = missing.getRowAt(i).EQ(1).findIJ();
                if (!find.isNull())
                {
                    arr = find.getIndex();
                    int len = arr.length;
                    tmp = new Matrix(1, len, rowMean);
                    y.setMatrix(i, arr, tmp);// y(i,missing(i,:)==1) = rowMean;
                }
            }
        }// end

        if (flag)
        {
            y = y.transpose();
        }

        return y;
    }

    public static void main(String[] args)
    {

        Matrix test = ImputationUtil.testMatrix();// Matrix.rand(m,
                                                  // n).scale(10).round();

        test.printInLabel("test", 0);

        FindInd find = test.LT(2.0).findIJ();
        if (find.isNull())
        {
            return;
        }

        int[] arr = find.getIndex();
        Matrix test2 = test.copy();
        test2.setElements(arr, Double.NaN);

        test2.printInLabel("test2", 0);

        AbstractImputation IM = new MeanImputation(test2, Dimension.ROW);
        IM.addParam("K", 2);
        IM.impute();

        Matrix impute = IM.getImputedData();
        impute.printInLabel("impute");
    }

}
