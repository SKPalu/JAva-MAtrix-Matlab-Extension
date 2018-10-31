/**
 * 
 */
package jamaextension.jamax.datafun.imputation;

import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortMat;
import jamaextension.jamax.elfun.JElfun;

/**
 * 
 * @author Sanjeev Sharma
 * 
 */
public class KnnImputation extends AbstractImputation
{

    /**
     * 
     */
    public KnnImputation(Matrix X, Dimension dim)
    {
        // TODO Auto-generated constructor stub
        super(X, dim);
        this.imputation = ImputationType.KNN;
    }

    /**
     * 
     */
    public KnnImputation(Matrix X)
    {
        // TODO Auto-generated constructor stub
        super(X);
    }

    @Override
    public void impute()
    {
        Integer kVal = (Integer) this.params.getKey("K");
        int k = 2;
        if (kVal != null)
        {
            k = kVal.intValue();
        }
        if (k < 2)
        {
            k = 2;
        }

        Matrix x = this.rawData.copy();
        if (!this.containMissing)
        {
            this.imputedData = x;
            this.imputatedDone = true;
            return;
        }

        System.out.println(k + " NN imputation\n");
        int n = x.getRowDimension();// size(x,1);

        Indices missing = x.isnan();

        Indices missingCount = JDatafun.sum(missing, Dimension.COL);

        // missingCount.printInLabel("missingCount");

        FindInd find = missingCount.EQ(0).findIJ();

        if (find.isNull())
        {
            this.imputedData = x;
            this.imputatedDone = true;
            return;
        }

        int[] ind = find.getIndex();
        Matrix noMissing = x.getRows(ind);// x(missingCount == 0,:);

        for (int i = 0; i < n; i++)
        {
            if (missingCount.getElementAt(i) > 0)
            {
                // x(i,:) = knnAux(noMissing,x(i,:),missing(i,:),K);
                Matrix xRow = x.getRowAt(i);
                Indices missingRow = missing.getRowAt(i);
                Matrix temp = knnAux(noMissing, xRow, missingRow, k);
                x.setRowAt(i, temp);
            }// end
        }// end

        this.imputedData = x;
        this.imputatedDone = true;

    }

    /**
     * 
     * @param A
     * @param XX
     * @param m
     * @param K
     * @return
     */
    static Matrix knnAux(Matrix A, Matrix XX, Indices m, int K)
    {
        // Matrix knn = null;
        Matrix x = XX;

        FindInd find = m.EQ(0).findIJ();
        int[] m0 = find.getIndex();
        Matrix Am0 = A.getColumns(m0);
        int rowA = A.getRowDimension();
        Matrix ones = Matrix.ones(rowA, 1);
        Matrix xmo = x.getEls(m0);
        if (xmo.isColVector())
        {
            xmo = xmo.toRowVector();
        }

        // summing = (A(:,m==0)-ones(size(A,1),1)*x(m==0)).^2 ;

        Matrix summing = ones.times(xmo);
        summing = Am0.minus(summing);
        summing = JElfun.pow(summing, 2.0);

        Matrix inner = JDatafun.sum(summing, Dimension.COL);
        Matrix D = JElfun.sqrt(inner);

        QuickSort sort = new QuickSortMat(D, true, true);
        Matrix SD = (Matrix) sort.getSortedObject();
        Indices I = sort.getIndices();

        Indices meq1 = m.EQ(1);
        find = meq1.findIJ();
        m0 = find.getIndex();
        int I1 = I.start();
        Matrix temp = null;

        double val = Math.pow(10.0, -10.0);
        if (SD.start() < val)
        {
            temp = A.getMatrix(I1, m0);
        }
        else
        {
            Matrix tmp2 = null;
            int[] k1 = Indices.linspace(0, K - 1).getRowPackedCopy();
            tmp2 = SD.getEls(k1);
            tmp2 = tmp2.reciprocate();
            double sumSD = JDatafun.sum(tmp2).start();
            tmp2 = tmp2.transpose();
            k1 = I.getEls(k1).getRowPackedCopy();
            temp = A.getMatrix(k1, m0);
            // (1./SD(1:K))' * A(I(1:K),m==1) / sum(1./SD(1:K));
            temp = tmp2.times(temp).arrayRightDivide(sumSD);
        }

        x.setElements(m0, temp);

        return x;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        int m = 10;
        int n = 8;
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

        AbstractImputation IM = new KnnImputation(test2);
        IM.addParam("K", 2);
        IM.impute();

        Matrix impute = IM.getImputedData();
        impute.printInLabel("impute");

    }

}
