package jamaextension.jamax.datafun.imputation;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

public class LinearSplineImputation extends AbstractImputation
{

    public LinearSplineImputation(Matrix data)
    {
        super(data);
        this.imputation = ImputationType.LINEARSPLINE;
    }

    public LinearSplineImputation(Matrix data, Dimension dim)
    {
        super(data, dim);
        this.imputation = ImputationType.LINEARSPLINE;
    }

    public void impute()
    {
        Matrix Y = this.rawData;
        if (!this.containMissing)
        {
            this.imputedData = Y;
            this.imputatedDone = true;
            return;
        }

        int win = 0;

        Integer kVal = (Integer) this.params.getKey("window");

        if (kVal != null)
        {
            win = kVal.intValue();
        }
        if (win < 2)
        {
            win = 4;
        }

        int row = Y.getRowDimension();
        int col = Y.getColumnDimension();
        Matrix imputedX = new Matrix(row, col);

        if (this.rowCol == Dimension.COL)
        {
            /*
             * boolean test = true; if (test) { throw new
             * ConditionalException("impute : Not yet implemented."); }
             */

            for (int i = 0; i < row; i++)
            {
                Matrix X = Y.getRowAt(i);
                // X.printInLabel("Xstart_" + i, 0);
                X = ImputationUtil.fillAll(X, win);
                if (X.isColVector())
                {
                    X = X.toRowVector();
                }
                // X.printInLabel("Xfill_" + i);
                imputedX.setRowAt(i, X);
            }
        }
        else
        {
            for (int i = 0; i < col; i++)
            {
                Matrix X = Y.getColumnAt(i).toRowVector();
                // X.printInLabel("Xstart_" + i, 0);

                if (i == 74)
                    System.out.println("linear-impute = " + i);

                X = ImputationUtil.fillAll(X, win);
                if (X.isRowVector())
                {
                    X = X.toColVector();
                }
                // X.printInLabel("Xfill_" + i, 0);
                imputedX.setColumnAt(i, X);
            }
        }

        this.imputedData = imputedX;
        this.imputatedDone = true;
    }

    public void imputeOld()
    {
        Matrix Y = this.rawData;
        if (!this.containMissing)
        {
            this.imputedData = Y;
            this.imputatedDone = true;
            return;
        }

        int win = 0;

        Integer kVal = (Integer) this.params.getKey("window");

        if (kVal != null)
        {
            win = kVal.intValue();
        }
        if (win < 2)
        {
            win = 4;
        }

        int row = Y.getRowDimension();
        int col = Y.getColumnDimension();
        Matrix imputedX = new Matrix(row, col);

        if (this.rowCol == Dimension.COL)
        {

            for (int i = 0; i < row; i++)
            {

                Matrix X = Y.getRowAt(i);

                if (!X.isnanBoolean())
                {

                    imputedX.setRowAt(i, X);
                    // X.printInLabel("X_Imputed-col_" + i);
                    continue;
                }
                /*
                 * Object[] obj = missingAtFront(X); boolean tf = (Boolean)
                 * obj[0];
                 * 
                 * Object[] obj2 = missingAtEnd(X); boolean tf2 = (Boolean)
                 * obj2[0];
                 * 
                 * // X.toColVector().printInLabel("X1");
                 * 
                 * boolean tf3 = !tf && !tf2; if (tf3) {// definitely missing in
                 * the middle ONLY X = imputeMissingMiddle(X); } else {// can be
                 * missing at the front, middle & end X = beginEndFit(X, win); }
                 */

                X = ImputationUtil.splineAlong(X, win);

                imputedX.setRowAt(i, X);
            }
        }
        else
        {
            System.out.println("\n");
            for (int i = 0; i < col; i++)
            {

                System.out.println(" Index = " + i);

                if (i == 2)
                {
                    System.out.println(" IIIII = " + i);
                }

                Matrix X = Y.getColumnAt(i).toRowVector();

                if (!X.isnanBoolean())
                {
                    if (X.isRowVector())
                    {
                        X = X.toColVector();
                    }
                    imputedX.setColumnAt(i, X);
                    X.printInLabel("X_Imputed-col_" + i);
                    continue;
                }

                X = ImputationUtil.splineAlong(X, win);
                if (X.isRowVector())
                {
                    X = X.toColVector();
                }

                imputedX.setColumnAt(i, X);
            }
        }

        this.imputedData = imputedX;
        this.imputatedDone = true;

    }

    static Matrix testMiddleNanData()
    {
        double[] xx =
        {
                1.0000, 1.0000, Double.NaN, 3.0000, 2.0000, Double.NaN, 1.0000, 2.0000, 3.0000
        };

        Matrix test = new Matrix(xx);
        return test;
    }

    public static void main(String[] args)
    {

        Matrix nanMat = testMiddleNanData();
        nanMat.printInLabel("nanMat", 0);
        Matrix test = ImputationUtil.interpolateMissingMiddle(nanMat);

        test.printInLabel("imputed");
    }

}
