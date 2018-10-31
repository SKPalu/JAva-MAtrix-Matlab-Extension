/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.Matrix3D;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Sione
 */
public class SumMat3D extends Sum
{

    public SumMat3D(Matrix3D A)
    {
        this(A, null);
    }

    public SumMat3D(Matrix3D A, Dimension dim)
    {
        if (dim == null)
        {
            // if (A.isVector()) {
            // sumVector(A);
            // } else {
            // sumMatrix(A, Dimension.ROW);
            // }
            dim = Dimension.ROW;
        } // else {
          // sumMatrix(A, dim);
          // }

        if (dim == Dimension.ROW)
        {
            sumRow(A);
        }
        else if (dim == Dimension.COL)
        {
            sumCol(A);
        }
        else if (dim == Dimension.PAGE)
        {
            sumPage(A);
        }

    }

    private void sumRow(Matrix3D A)
    {
        int page = A.getPageDimension();
        // int row = A.getRowDimension();
        int col = A.getColDimension();
        Matrix3D sums = new Matrix3D(1, col, page);
        for (int k = 0; k < page; k++)
        {
            Matrix aPage = A.getPageAt(k);
            aPage = JDatafun.sum(aPage, Dimension.ROW);
            sums.setPageAt(k, aPage);
        }
        this.sumObject = sums;
    }

    private void sumCol(Matrix3D A)
    {
        int page = A.getPageDimension();
        int row = A.getRowDimension();
        // int col = A.getColDimension();
        Matrix3D sums = new Matrix3D(row, 1, page);
        for (int k = 0; k < page; k++)
        {
            Matrix aPage = A.getPageAt(k);
            aPage = JDatafun.sum(aPage, Dimension.COL);
            sums.setPageAt(k, aPage);
        }
        this.sumObject = sums;
    }

    private void sumPage(Matrix3D A)
    {
        int page = A.getPageDimension();
        int row = A.getRowDimension();
        int col = A.getColDimension();
        Matrix sums = new Matrix(row, col);

        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                double val = 0.0;
                for (int k = 0; k < page; k++)
                {
                    Matrix pageMat = A.getPageAt(k);
                    val += pageMat.get(i, j);
                }
                sums.set(i, j, val);
            }
        }

        this.sumObject = sums;
    }
}
