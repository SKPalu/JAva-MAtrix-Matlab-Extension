package jamaextension.jamax.datafun.imputation;

import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.vnijmsl.ARMA;
import jamaextension.jamax.vnijmsl.CubicSpline;
import jamaextension.jamax.vnijmsl.SplineJmsl;

/**
 * 
 * @author Sione
 */
public class SplineImputation extends AbstractImputation
{

    public SplineImputation(Matrix data)
    {
        super(data);
    }

    public SplineImputation(Matrix data, Dimension dim)
    {
        super(data, dim);
        this.imputation = ImputationType.SPLINE;
    }

    @Override
    public void impute()
    {
        // throw new UnsupportedOperationException("Not supported yet."); //To
        // change body of generated methods, choose Tools | Templates.
        Matrix Y = this.rawData;
        if (!this.containMissing)
        {
            this.imputedData = Y;
            this.imputatedDone = true;
            return;
        }

        SplineJmsl spline = null;// new CubicSpline();
        Matrix X = null;
        int len = 0;
        Indices IND = null;
        // Indices IND2 = null;
        FindInd find = null;
        int[] arr = null;
        // int[] arr2 = null;
        Matrix xx = null;
        Matrix yy = null;

        if (Y.isVector())
        {
            boolean rowVec = true;
            if (Y.isColVector())
            {
                rowVec = false;
            }
            len = Y.length();
            IND = Y.isnan();// .findIJ();//.getIndex();
            arr = IND.NOT().findIJ().getIndex();
            X = Matrix.linspace(1.0, len, len);
            xx = X.getEls(arr);
            yy = Y.getEls(arr);
            spline = new CubicSpline(xx, yy);

            find = IND.findIJ();
            arr = find.getIndex();
            Matrix Xi = X.getEls(arr);
            Xi = spline.value(Xi).transpose();

            Y.setElements(arr, Xi);
            if (!rowVec)
            {
                this.imputedData = Y;
            }
            else
            {
                this.imputedData = Y.transpose();
            }
            this.imputatedDone = true;
            return;
        }

        int row = Y.getRowDimension();
        int col = Y.getColumnDimension();
        this.imputedData = new Matrix(row, col);

        if (this.rowCol == Dimension.COL)
        {
            X = Matrix.linspace(1.0, col, col);
            for (int i = 0; i < row; i++)
            {
                Matrix Yi = Y.getRowAt(i);
                if (!Yi.isnanBoolean())
                {
                    this.imputedData.setRowAt(i, Yi);// .setColumnAt(j, Yi);
                    continue;
                }
                IND = Yi.isNonNans();// .findIJ();//.getIndex();
                find = IND.findIJ();
                arr = find.getIndex();
                xx = X.getEls(arr);
                yy = Yi.getEls(arr);
                spline = new CubicSpline(xx, yy);

                find = IND.NOT().findIJ();
                arr = find.getIndex();
                Matrix Xi = X.getEls(arr);
                Xi = spline.value(Xi).transpose();
                Yi.setElements(arr, Xi);

                this.imputedData.setRowAt(i, Yi);// .setColumnAt(j, Yi);
            }
        }
        else
        {
            X = Matrix.linspace(1.0, row, row);// .transpose();
            for (int j = 0; j < col; j++)
            {
                Matrix Yj = Y.getColumnAt(j);
                if (!Yj.isnanBoolean())
                {
                    this.imputedData.setColumnAt(j, Yj);
                    continue;
                }
                IND = Yj.isNonNans();// .findIJ();//.getIndex();
                find = IND.findIJ();
                arr = find.getIndex();
                xx = X.getEls(arr);
                yy = Yj.getEls(arr);
                spline = new CubicSpline(xx, yy);

                find = IND.NOT().findIJ();
                arr = find.getIndex();
                Matrix Xj = X.getEls(arr);
                Xj = spline.value(Xj).transpose();
                Yj.setElements(arr, Xj);
                this.imputedData.setColumnAt(j, Yj);
            }
        }

        this.imputatedDone = true;

    }

    public static Matrix testMatrix()
    {
        double[][] test =
        {
                {
                        10, 6, 5, 5, 9, 1, 4, 2
                },
                {
                        9, 3, 7, 3, 3, 9, 7, 8
                },
                {
                        5, 3, 5, 2, 1, 2, 1, 5
                },
                {
                        4, 3, 10, 0, 4, 2, 6, 3
                },
                {
                        9, 4, 9, 8, 8, 6, 2, 6
                },
                {
                        1, 6, 4, 10, 1, 9, 0, 9
                },
                {
                        9, 2, 7, 3, 7, 7, 0, 2
                },
                {
                        0, 2, 1, 0, 9, 3, 10, 8
                },
                {
                        9, 5, 2, 8, 8, 1, 4, 7
                },
                {
                        9, 3, 6, 1, 1, 7, 5, 8
                }
        };

        return new Matrix(test);
    }

    public static void main(String[] args)
    {
        Matrix data = testMatrix();
        data = data.getRowAt(0);// .getColumnAt(0);
        data.printInLabel("data-1", 0);
        int[] arr = data.LT(5.0).findIJ().getIndex();
        data.setElements(arr, Double.NaN);
        data.printInLabel("data-2", 0);
        data = data.getMatrix(0, 0, 2, 5);
        data.printInLabel("data-25", 0);
        Dimension dim = Dimension.COL;
        SplineImputation spline = new SplineImputation(data, dim);
        spline.impute();
        data = spline.getImputedData();
        data.printInLabel("data-3");
        data = data.transpose();

        Matrix timeSeriesData = data.getMatrix(0, 0, 0, 2);

        ARMA arma = new ARMA(2, 3, timeSeriesData);
        arma.compute();
    }

}
