package jamaextension.jamax.datafun.filtering.denoising;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;

public class MovingMeanFilter extends DataSmoothingFilter
{

    public MovingMeanFilter(Matrix dataIn, Dimension dim)
    {
        super(dataIn, dim);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void filter()
    {
        int row = dataIn.getRowDimension();
        int col = dataIn.getColumnDimension();
        int len = dataIn.length();
        Matrix X = dataIn;
        Indices ndim = X.sizeIndices();

        // default window length
        int win = 3;
        if (!this.isEmptyParameters() && this.parameters.containsKey("window"))
        {
            win = (Integer) parameters.getKey("window");
        }

        if ((ndim.getElementAt(0) < win) && (this.dim == Dimension.ROW))
        {
            throw new ConditionalRuleException(
                    "filter : Number of row-wise elements is less than the window. It must be larger.");
        }
        if ((ndim.getElementAt(1) < win) && (this.dim == Dimension.COL))
        {
            throw new ConditionalRuleException(
                    "filter : Number of column-wise elements is less than the window. It must be larger.");
        }

        int halfspace = ((win - 1) / 2);

        /*
         * if (!this.isDataInMatrix()) {// vector boolean isrow =
         * X.isRowVector();
         * 
         * } else {
         */
        // matrix
        /*
         * %solution in dim=1 if dim==2 data=data'; end
         */
        int ndimval = 0;

        // start=[ones(1,halfspace+1) 2:(n(dim)-halfspace)];
        Indices start = Indices.ones(1, halfspace + 1);
        Indices start2 = null;
        if (dim == Dimension.COL)
        {
            ndimval = ndim.getElementAt(1);
        }
        else
        {
            ndimval = ndim.getElementAt(0);
        }
        start2 = Indices.linspace(2, ndimval - halfspace);
        start = start.mergeH(start2);
        start.printInLabel("start");

        /*
         * Computes the beginning and ending column for each moving average
         * compuation. Divide is the number of elements that are incorporated in
         * each moving average.
         */

        // stop=[(1+halfspace):n(dim) ones(1,halfspace)*n(dim)];
        Indices stop = Indices.linspace(1 + halfspace, ndimval);
        Indices stop2 = Indices.ones(1, halfspace).arrayTimes(ndimval);
        stop = stop.mergeH(stop2);
        stop.printInLabel("stop");
        
        // divide=stop-start+1;
        Indices divide = stop.minus(start).plus(1);
        divide.printInLabel("divide");

        if (dim == Dimension.COL)
        {
            X = X.transpose();
        }

        Matrix data1 = X.getRowAt(0);

        /*
         * Calculates the moving average by calculating the sum of elements from
         * the start row to the stop row for each central element, and then
         * dividing by the number of elements used in that sum to get the
         * average for that central element. Implemented by calculating the
         * moving sum of the full data set. Cumulative sum for each central
         * element is calculated by subtracting the cumulative sum for the row
         * before the start row from the cumulative sum for the stop row. Row
         * references are adusted to take into account the fact that you can now
         * reference a row<1. Divides the series of cumulative sums for by the
         * number of elements in each sum to get the moving average.
         */

        // change index into java-based zero-index
        start2 = start.minus(1);
        int[] ind1 = start2.getRowPackedCopy();
        stop2 = stop.minus(1);
        int[] ind2 = stop2.getRowPackedCopy();

        Matrix CumulativeSum = JDatafun.cumsum(X);
        //CumulativeSum.printInLabel("CumulativeSum",0);
        
        Matrix cumsumStop = CumulativeSum.getRows(ind2);// CumulativeSum(stop,:);
        //cumsumStop.printInLabel("cumsumStop",0);
        
        // subtract one so the index is zero-based
        Indices maxStart = JDatafun.max(start.minus(1), 1).minus(1);
        int[] ind3 = maxStart.getRowPackedCopy();
        Matrix cumsumStart = CumulativeSum.getRows(ind3);// CumulativeSum(max(start-1,1),:);
        Matrix temp_sum = cumsumStop.minus(cumsumStart); // CumulativeSum(stop,:)-CumulativeSum(max(start-1,1),:);
        //temp_sum.printInLabel("temp_sum",0);
        Indices startEq1 = start.EQ(1);
        FindInd find = startEq1.findIJ();
        if (!find.isNull())
        {
            int[] ind4 = find.getIndex();
            Matrix temp_sum_ind = temp_sum.getRows(ind4);
            int nrowsTempSum = temp_sum_ind.getRowDimension();
            Matrix data1repmat = data1.repmat(nrowsTempSum, 1);
            temp_sum_ind = temp_sum_ind.plus(data1repmat);
            // temp_sum((start==1),:)=bsxfun(@plus,temp_sum((start==1),:),data(1,:));
            temp_sum.setRows(ind4, temp_sum_ind);;//.setElements(ind4, temp_sum_ind);
        }

        // result=bsxfun(@rdivide,temp_sum,divide');
        Indices divtrans = divide.transpose();
        int temp_sum_ncols = temp_sum.getColumnDimension();
        divtrans = divtrans.repmat(1, temp_sum_ncols);
        Matrix result = temp_sum.arrayRightDivide(divtrans);
        if (dim == Dimension.COL)
        {
            result = result.transpose();
        }
        this.dataOut = result;
        // }

        filtered = true;

    }

    static Matrix testData()
    {
        double[][] X =
        {
                {
                        12, 5, 6, 20, 7, 1, 17, 17, 18, 16
                },
                {
                        17, 13, 1, 11, 5, 17, 13, 11, 0, 5
                },
                {
                        17, 2, 1, 8, 9, 15, 15, 16, 18, 16
                },
                {
                        14, 4, 15, 5, 8, 3, 2, 14, 11, 4
                },
                {
                        0, 2, 19, 3, 0, 16, 11, 18, 4, 11
                },
                {
                        7, 12, 19, 7, 6, 3, 20, 7, 3, 5
                },
                {
                        11, 18, 18, 1, 6, 7, 3, 18, 14, 17
                },
                {
                        20, 19, 17, 15, 15, 9, 4, 7, 2, 1
                },
                {
                        11, 20, 5, 18, 10, 16, 1, 4, 15, 14
                },
                {
                        17, 5, 14, 13, 3, 7, 3, 2, 16, 20
                },
                {
                        9, 4, 19, 18, 2, 3, 13, 13, 11, 9
                },
                {
                        8, 11, 4, 16, 9, 9, 19, 5, 2, 16
                },
                {
                        17, 9, 4, 7, 20, 11, 12, 20, 20, 2
                },
                {
                        19, 14, 8, 20, 20, 9, 1, 16, 4, 7
                },
                {
                        11, 2, 14, 6, 14, 13, 16, 10, 14, 4
                }
        };

        return new Matrix(X);
    }

    public static void main(String[] args)
    {
        Matrix X = testData();
        X.printInLabel("X",0);
        Dimension dim = Dimension.ROW;
        DataSmoothingFilter DSF = new MovingMeanFilter(X.getColumnAt(0), dim);
        DSF.addParameter("window", 3);
        DSF.filter();
        Matrix Xr = DSF.getDataOut();
        Xr.printInLabel("Xr");

    }

}
