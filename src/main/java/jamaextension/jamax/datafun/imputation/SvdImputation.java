/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun.imputation;

import java.util.HashMap;
import java.util.Map;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.SvdJLapack;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Sione
 */
public class SvdImputation extends AbstractImputation
{

    public SvdImputation(Matrix data, Dimension rowCol)
    {
        super(data, rowCol);
        this.imputation = ImputationType.SVD;
    }

    public SvdImputation(Matrix data)
    {
        super(data);
    }

    /*
     * SVD Imputation
     * 
     * Imputation using the SVD First fill missing values using the mean of the
     * column Then, compute a low, rank-k approximation of x. Fill the missing
     * values again from the rank-k approximation. Recompute the rank-k
     * approximation with the imputed values and fill again, repeating num.iters
     * times
     * 
     * @param x a data frame or matrix where each row represents a different
     * record
     * 
     * @param k the rank-k approximation to use for x
     * 
     * @param num.iters the number of times to compute the rank-k approximation
     * and impute the missing data
     * 
     * @param verbose if TRUE print status updates
     * 
     * @examples x = matrix(rnorm(100),10,10) x.missing = x > 1 x[x.missing] =
     * NA SVDImpute(x, 3)
     * 
     * @export
     */
    public void impute()
    {
        Matrix X = rawData;
        boolean hasParams = this.isEmptyParams();
        int k = 2;
        int iters = 10;
        if (hasParams)
        {
            HashMap<String, Object> mapVal = this.params.getKeyValue();
            // int siz = mapVal.size();
            for (Map.Entry<String, Object> entry : mapVal.entrySet())
            {
                if ("k".equals(entry.getKey()))
                {
                    k = (Integer) entry.getValue();
                }
                else if ("iters".equals(entry.getKey()))
                {
                    iters = (Integer) entry.getValue();
                }
                else
                {
                    throw new ConditionalRuleException("impute", "Undefined parameter \"" + entry.getValue()
                            + "\".\nOnly \"k\" and \"iters\" are valie.");
                }
            }
        }// end if

        int numCols = X.getColumnDimension();
        if (k > numCols)
        {
            throw new ConditionalRuleException("impute",
                    "Rank-k approximation cannot exceed the number of columns of x");
        }

        /*
         * 
         * prelim = impute.prelim(x, byrow=F) if (prelim$numMissing == 0) return
         * (x) missing.matrix = prelim$missing.matrix x.missing =
         * prelim$x.missing missing.cols.indices = prelim$missing.cols.indices
         * 
         * #First initialize missing values with mean x.missing.imputed =
         * apply(x.missing, 2, function(j) { colIndex = j[1] j.original = j[-1]
         * missing.rows = which(missing.matrix[,colIndex])
         * if(length(missing.rows) == nrow(x)) warning(
         * paste("Column",colIndex,"is completely missing",sep=" ") )
         * j.original[missing.rows] = mean(j.original[-missing.rows]) j.original
         * }) #replace columns with missing values with x.missing.imputed
         * x[,missing.cols.indices] = x.missing.imputed #Fill anything that is
         * still NA with 0 missing.matrix2 = is.na(x) x[missing.matrix2] = 0
         * for(i in 1:num.iters) { if(verbose) print(paste("Running iteration",
         * i, sep=" ")) x.svd = .rankKapprox(x, k) x[missing.matrix] =
         * x.svd[missing.matrix] } return ( list ( x=x,
         * missing.matrix=missing.matrix ))
         */
    }

    Object imputePrelim(Object x, boolean byrow)
    {
        return new Object();
    }

    Matrix rankKapprox(Matrix X, int k)
    {
        SvdJLapack svd = new SvdJLapack(X);
        int k1 = k - 1;
        Matrix U = svd.getU();
        U = U.getColumns(0, k1);

        Matrix S = svd.getS();
        S = S.getMatrix(0, k1, 0, k1);

        Matrix V = svd.getV();
        V = V.getRows(0, k1);

        Matrix recon = U.times(S).times(V);
        return recon;
    }
}
