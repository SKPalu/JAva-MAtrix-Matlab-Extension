/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;

/**
 * 
 * @author Sione
 */
public class Matrix3D
{

    private Matrix[] array;
    private boolean printJavaIndex = false;

    public Matrix3D(Matrix mat)
    {
        if (mat == null)
        {
            throw new ConditionalException("Matrix3D : Matrix parameter \"mat\" must be non-null.");
        }
        this.array = new Matrix[]
        {
            mat.copy()
        };
    }

    public Matrix3D(int... dim)
    {
        if (dim == null)
        {
            throw new ConditionalException("Matrix3D : Integer array parameter \"dim\" must be non-null.");
        }
        if (dim.length != 3)
        {
            throw new ConditionalException("Matrix3D : Length of integer array parameter \"dim\" must be 3;");
        }
        int dim1 = dim[0];
        int dim2 = dim[1];
        int dim3 = dim[2];
        construct(dim1, dim2, dim3, 0.0);
    }

    public Matrix3D(int dim1, int dim2, int dim3)
    {
        this(dim1, dim2, dim3, 0.0);
    }

    public Matrix3D(int[] mnk, double s)
    {
        if (mnk == null)
        {
            throw new ConditionalException("Matrix3D : Parameter \"mnk\" must be non-null.");
        }
        if (mnk.length != 3)
        {
            throw new ConditionalException("Matrix3D : Length of integer array parameter \"mnk\" must be 3;");
        }
        Indices ind = new Indices(mnk);
        if (ind.LTEQ(0).anyBoolean())
        {
            throw new ConditionalException("Matrix3D : Elements of integer array parameter \"mnk\" must be positive.");
        }

        int dim1 = mnk[0];
        int dim2 = mnk[1];
        int dim3 = mnk[2];

        construct(dim1, dim2, dim3, s);
    }

    public Matrix3D(int dim1, int dim2, int dim3, double inVal)
    {
        construct(dim1, dim2, dim3, inVal);
    }

    private void construct(int dim1, int dim2, int dim3, double inVal)
    {
        Indices ind = new Indices(new int[]
        {
                dim1, dim2, dim3
        });
        ind = ind.LT(1).find();
        if (ind != null)
        {
            throw new ConditionalException(
                    "construct : All integer parameters \"dim1\", \"dim2\" and \"dim3\" must be positive.");
        }
        this.array = new Matrix[dim3];
        for (int k = 0; k < dim3; k++)
        {
            this.array[k] = new Matrix(dim1, dim2, inVal);
        }
    }

    public void setColAt(int col, double colDb)
    {
        int m = this.getRowDimension();
        int n = this.getColDimension();
        int p = this.getPageDimension();
        Matrix mp = new Matrix(m, p, colDb);
        setColAt(col, mp);
    }

    public void setColAt(int col, Matrix colMat)
    {
        int m = this.getRowDimension();
        int n = this.getColDimension();
        int p = this.getPageDimension();

        if (col < 0 || col >= n)
        {
            throw new ConditionalException("setColAt : Integer parameter \"col\" must fall in the interval [0, "
                    + (n - 1) + "].");
        }

        if (colMat == null || colMat.isNull())
        {
            throw new ConditionalException("setColAt : Parameter \"colMat\" must be non-null or non-empty.");
        }

        int[] dimArr =
        {
                m, p
        };
        Indices dimInd = new Indices(dimArr);
        Indices sizColMat = colMat.sizeIndices();
        boolean notEq = dimInd.NEQ(sizColMat).anyBoolean();
        int[] arr = colMat.sizeIntArr();

        if (notEq)
        {
            throw new ConditionalException("setColAt : Size of \"colMat\" [" + arr[0] + " x " + arr[1]
                    + "] must equal this numRows by numPages : [" + m + " x " + p + "].");
        }

        for (int i = 0; i < p; i++)
        {
            Matrix colPage = colMat.getColumnAt(i);
            this.array[i].setColumnAt(col, colPage);
        }

    }

    public void setRowAt(int row, double rowDb)
    {
        int m = this.getRowDimension();
        int n = this.getColDimension();
        int p = this.getPageDimension();
        Matrix np = new Matrix(n, p, rowDb);
        setColAt(row, np);
    }

    public void setRowAt(int row, Matrix rowMat)
    {
        int m = this.getRowDimension();
        int n = this.getColDimension();
        int p = this.getPageDimension();

        if (row < 0 || row >= m)
        {
            throw new ConditionalException("setRowAt : Integer parameter \"row\" must fall in the interval [0, "
                    + (m - 1) + "].");
        }

        if (rowMat == null || rowMat.isNull())
        {
            throw new ConditionalException("setRowAt : Parameter \"rowMat\" must be non-null or non-empty.");
        }

        int[] dimArr =
        {
                n, p
        };
        Indices dimInd = new Indices(dimArr);
        Indices sizRowMat = rowMat.sizeIndices();
        boolean notEq = dimInd.NEQ(sizRowMat).anyBoolean();
        int[] arr = rowMat.sizeIntArr();

        if (notEq)
        {
            throw new ConditionalException("setRowAt : Size of \"rowMat\" [" + arr[0] + " x " + arr[1]
                    + "] must equal this numRows by numPages : [" + n + " x " + p + "].");
        }

        for (int i = 0; i < p; i++)
        {
            Matrix rowPage = rowMat.getColumnAt(i).transpose();
            this.array[i].setRowAt(row, rowPage);
        }

    }

    public void setPageMatrix(int[] rows, int[] cols, int page, Matrix mat)
    {
        Matrix pageMat = this.getPageAt(page);
        pageMat.setMatrix(rows, cols, mat);
    }

    public void setPageAt(int pageInd, double val)
    {
        // Matrix first = this.array[0];
        // int m = first.getRowDimension();
        // int n = first.getColumnDimension();
        // Matrix valMat = new Matrix(m, n, val);
        Matrix page = this.getPageAt(pageInd);
        page.setAllTo(val);
        // this.setPageAt(pageInd, valMat);
    }

    public void setPageAt(int pageInd, Matrix mat)
    {
        int dim3 = this.array.length;
        if (pageInd >= dim3)
        {
            throw new ConditionalException("setPage : Integer parameter \"pageInd\" (= " + pageInd
                    + ") is out of bound.");
        }
        Matrix first = this.array[0];
        Indices ind = first.sizeIndices().NEQ(mat.sizeIndices()).find();
        if (ind != null)
        {
            throw new ConditionalException("setPage : Matrix parameter \"mat\" must conform to size: ["
                    + first.getRowDimension() + " x " + first.getColumnDimension() + "].");
        }
        this.array[pageInd] = mat;
    }

    public void setPages(int[] pageInds, Matrix[] pages)
    {
        int len = pageInds.length;
        int pgLen = pages.length;
        if (len != pgLen)
        {
            throw new ConditionalException("setPages : Lengths of arrays \"pageInds\" and \"pages\" must be the same.");
        }
        int numPages = this.size()[2];
        // check indices in pageInds
        for (int i = 0; i < len; i++)
        {
            int ind = pageInds[i];
            if (ind < 0 || ind >= numPages)
            {
                throw new ConditionalException("setPages : Integer element " + i
                        + " of array \"pageInds\" is out of bound.");
            }
        }
        // check sizes of pages if they conform
        int row = this.size()[0];
        int col = this.size()[1];
        Indices rowCol = new Indices(new int[]
        {
                row, col
        });
        for (int i = 0; i < pgLen; i++)
        {
            Indices pgSize = pages[i].sizeIndices();
            boolean bn = rowCol.NEQ(pgSize).anyBoolean();
            if (bn)
            {
                throw new ConditionalException("setPages : Size of matrix element " + i + " [" + row + " x " + col
                        + "] is incompatible with this size [" + pgSize.getRowDimension() + " x "
                        + pgSize.getColumnDimension() + "].");
            }
        }

        // set the pages
        for (int i = 0; i < len; i++)
        {
            int ind = pageInds[i];
            this.setPageAt(ind, pages[i].copy());
        }

    }

    public void setPages(int[] pageInds, Matrix page)
    {
        int len = pageInds.length;
        // set the pages
        for (int i = 0; i < len; i++)
        {
            int ind = pageInds[i];
            this.setPageAt(ind, page.copy());
        }
    }

    public void setPages(int[] pageInds, double pageVal)
    {
        int len = pageInds.length;
        // set the pages
        for (int i = 0; i < len; i++)
        {
            int ind = pageInds[i];
            this.setPageAt(ind, pageVal);
        }
    }

    public void setEls(int[] els, Matrix val)
    {
        int len = els.length;
        int numel = this.numel();
        // check each index element
        for (int i = 0; i < len; i++)
        {
            int ind = els[i];
            if (ind < 0 || ind >= numel)
            {
                throw new ConditionalException("setEls : Integer element " + i + " in array \"els\" is out of bound.");
            }
        }

        if (!val.isVector())
        {
            throw new ConditionalException("setEls : Matrix parameter \"val\" must be a vector and not a matrix.");
        }
        if (len != val.numel())
        {
            throw new ConditionalException("setEls : Number of elements in \"els\" and \"val\" must equal.");
        }

        // -----------------------------------------------------
        int dim3 = this.array.length;
        if (dim3 == 1)
        {// i
         // this.array[0].getElementAt(indLT);
            this.array[0].setElements(els, val);
            return;
        }

        int numEl1 = this.array[0].numel();
        int page = 0;
        int pageMul = 0;
        int rem = 0;

        for (int i = 0; i < len; i++)
        {
            int ind = els[i];
            int ind2 = ind + 1;

            if (ind2 <= numEl1)
            {
                rem = numEl1 % ind2;
                if (rem == 0 && ind2 == numEl1)
                {
                    rem = numEl1;
                }
                page = 0;
            }
            else
            {
                rem = ind2 % numEl1;
                page = ind / numEl1;
                pageMul = (page + 1) * numEl1;
                if ((rem == 0) && (ind2 == pageMul))
                {
                    rem = numEl1;
                }
            }
            // System.out.println(" page[" + i + "] = " + page);
            Matrix thisPage = this.array[page];
            thisPage.setElementAt(rem - 1, val.getElementAt(i));
        }

        /*------------- old code block ------------------*
         * int numEl1 = this.array[0].numel();
        for (int i = 0; i < len; i++) {
        int ind = els[i];
        int ind2 = ind + 1;
        int page = ind2 / numEl1;
        int rem = ind2 % numEl1;
        this.array[page - 1].setElementAt(rem - 1, val.getElementAt(i));
        }
         *-----------------------------------------------*/

    }

    public void setEls(int[] els, double val)
    {
        int len = els.length;
        int numel = this.numel();
        // check each index element
        for (int i = 0; i < len; i++)
        {
            int ind = els[i];
            if (ind < 0 || ind >= numel)
            {
                throw new ConditionalException("setEls : Integer element " + i + " in array \"els\" is out of bound.");
            }
        }

        // -----------------------------------------------------
        int dim3 = this.array.length;
        if (dim3 == 1)
        {// i
         // this.array[0].getElementAt(indLT);
            this.array[0].setElements(els, val);
            return;
        }

        // System.out.println("Size : ");
        // new Indices(new int[]{m,n,p}).printInLabel("Size of this");

        int numEl1 = this.array[0].numel();
        // System.out.println("numEl1 = " + numEl1);

        int page = 0;
        int pageMul = 0;
        int rem = 0;

        for (int i = 0; i < len; i++)
        {
            int ind = els[i];
            int ind2 = ind + 1;

            rem = ind2 % numEl1;
            page = ind / numEl1;
            pageMul = (page + 1) * numEl1;
            if ((rem == 0) && (ind2 == pageMul))
            {
                rem = numEl1;
            }

            /*
             * if (ind2 <= numEl1) { rem = numEl1 % ind2; if (rem == 0 && ind2
             * == numEl1) { rem = numEl1; } page = 0; } else { rem = ind2 %
             * numEl1; page = ind / numEl1; pageMul = (page + 1) * numEl1; if
             * ((rem == 0) && (ind2 == pageMul)) { rem = numEl1; } }
             */
            // System.out.println(" page[" + i + "] = " + page);
            Matrix thisPage = this.array[page];
            rem = rem - 1;
            thisPage.setElementAt(rem, val);
        }

    }

    public void setAllTo(double val)
    {
        int p = this.array.length;
        int m = this.array[0].getRowDimension();
        int n = this.array[0].getColumnDimension();
        for (int k = 0; k < p; k++)
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    // this.A[i][j] = val;
                    set(i, j, k, val);
                }
            }
        }
    }

    public void set(int i, int j, int k, double val)
    {
        Matrix mat = this.array[0];
        int m = mat.getRowDimension();
        int n = mat.getColumnDimension();
        int ndim = this.array.length;
        if (i >= m || i < 0)
        {
            throw new ConditionalException("set : Integer parameter \"i\" (= " + i + ") is out of bound.");
        }
        if (j >= n || j < 0)
        {
            throw new ConditionalException("set : Integer parameter \"j\" (= " + j + ") is out of bound.");
        }
        if (k >= ndim || k < 0)
        {
            throw new ConditionalException("set : Integer parameter \"k\" (= " + k + ") is out of bound.");
        }
        this.array[k].set(i, j, val);
    }

    public double get(int[] ijk)
    {
        if (ijk.length != 3)
        {
            throw new ConditionalException("get : Length of integer array parameter \"ijk\" (= " + ijk.length
                    + ") must be 3;");
        }
        return get(ijk[0], ijk[1], ijk[2]);
    }

    public double get(int i, int j, int k)
    {
        Matrix mat = this.array[0];
        int m = mat.getRowDimension();
        int n = mat.getColumnDimension();
        int ndim = this.array.length;
        if (i >= m || i < 0)
        {
            throw new ConditionalException("get : Integer parameter \"i\" (= " + i + ") is out of bound.");
        }
        if (j >= n || j < 0)
        {
            throw new ConditionalException("get : Integer parameter \"j\" (= " + j + ") is out of bound.");
        }
        if (k >= ndim || k < 0)
        {
            throw new ConditionalException("get : Integer parameter \"k\" (= " + k + ") is out of bound.");
        }
        return this.array[k].get(i, j);
    }

    /**
     * 
     * @param indLT
     * @return
     */
    public Matrix getElements(int[] ind)
    {
        int len = ind.length;
        Matrix R = new Matrix(len, 1);
        double val = 0.0;
        for (int i = 0; i < len; i++)
        {
            val = this.getElementAt(ind[i]);
            R.set(i, 0, val);
        }
        return R;
    }

    public double getElementAt(int ind)
    {
        int dim3 = this.array.length;
        if (dim3 == 1)
        {
            return this.array[0].getElementAt(ind);
        }
        int ind2 = ind + 1;
        int numEl1 = this.array[0].numel();

        int page = ind2 / numEl1;
        int rem = ind2 % numEl1;

        double val = this.array[page - 1].getElementAt(rem - 1);
        return val;
    }

    public int nnz()
    {

        int m = this.array[0].getRowDimension();
        int n = this.array[0].getColumnDimension();
        int k = this.array.length;

        int count = 0;
        for (int u = 0; u < k; u++)
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (this.array[u].get(i, j) != 0.0)
                    {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public int numel()
    {
        int k = this.array.length;
        Matrix mat = this.array[0];
        int i = mat.getRowDimension();
        int j = mat.getColumnDimension();
        return (i * j * k);
    }

    public Indices3D ANY(Dimension dim)
    {
        Matrix mat = this.array[0];
        int r = mat.getRowDimension();
        int c = mat.getColumnDimension();
        int k = this.array.length;

        if (dim == null)
        {
            throw new ConditionalException("ANY : Parameter \"dim\" must be non-null.");
        }
        Indices3D anyInd = null;
        switch (dim)
        {
        case ROW:
        {
            anyInd = new Indices3D(1, c, k);
            for (int i = 0; i < k; i++)
            {
                Indices Ind = this.array[i].ANY(dim);
                anyInd.setPage(i, Ind);
            }
            break;
        }
        case COL:
        {
            anyInd = new Indices3D(r, 1, k);
            for (int i = 0; i < k; i++)
            {
                Indices Ind = this.array[i].ANY(dim);
                anyInd.setPage(i, Ind);
            }
            break;
        }
        case PAGE:
        {

            ArrayList<Indices> list = new ArrayList<Indices>();
            for (int j = 0; j < c; j++)
            {
                Matrix first = null;
                for (int i = 0; i < k; i++)
                {
                    if (first != null)
                    {
                        first = first.mergeH(this.array[i].getColumnAt(j));
                    }
                    else
                    {
                        first = this.array[i].getColumnAt(j);
                    }
                }
                Indices Ind = first.ANY(Dimension.COL);
                // Ind.printInLabel("Ind");
                list.add(Ind);
            }

            int siz = list.size();
            // System.out.println(" siz = "+siz);
            Indices Ind = null;
            for (int v = 0; v < siz; v++)
            {
                if (Ind != null)
                {
                    Ind = Ind.mergeH(list.get(v));
                }
                else
                {
                    Ind = list.get(v);
                }
            }
            anyInd = new Indices3D(Ind);
            break;
        }
        default:
        {
            throw new ConditionalException("ANY : Parameter \"dim\" must be non-null.");
        }
        }
        return anyInd;
    }

    public Matrix toColVector()
    {
        int k = this.array.length;
        if (k == 1)
        {
            return this.array[0].toColVector();
        }
        Matrix firstCol = this.array[0].toColVector();
        Object[] list = new Object[k];
        for (int i = 1; i < k; i++)
        {
            list[i] = this.array[i].toColVector();
        }
        Matrix colVec = firstCol.mergeVerti(list);
        return colVec;
    }

    public Matrix toRowVector()
    {
        int k = this.array.length;

        if (k == 1)
        {
            return this.array[0].toColVector();
        }
        Matrix firstCol = this.array[0].toRowVector();
        Object[] list = new Object[k];
        for (int i = 1; i < k; i++)
        {
            list[i] = this.array[i].toRowVector();
        }
        Matrix rowVec = firstCol.mergeHoriz(list);
        return rowVec;
    }

    public boolean isNull()
    {
        return (this.array == null || this.array.length == 0);
    }

    public Indices3D isnan()
    {
        Matrix mat = this.array[0];
        int r = mat.getRowDimension();
        int c = mat.getColumnDimension();
        int k = this.array.length;
        Indices3D nanInd = new Indices3D(r, c, k);
        for (int i = 0; i < k; i++)
        {
            Indices NAN = this.array[i].isnan();
            nanInd.setPage(i, NAN);
        }
        return nanInd;
    }

    public Indices3D isinf()
    {
        Matrix mat = this.array[0];
        int r = mat.getRowDimension();
        int c = mat.getColumnDimension();
        int k = this.array.length;
        Indices3D infInd = new Indices3D(r, c, k);
        for (int i = 0; i < k; i++)
        {
            Indices INF = this.array[i].isinf();
            infInd.setPage(i, INF);
        }
        return infInd;
    }

    public int getRowDimension()
    {
        return this.array[0].getRowDimension();
    }

    public int getColDimension()
    {
        return this.array[0].getColumnDimension();
    }

    public int getPageDimension()
    {
        return this.array.length;
    }

    private int dimToInt(Dimension dim)
    {
        if (dim == null)
        {
            throw new ConditionalException("dimToInt : Dimension parameter \"dim\" must be non-null.");
        }
        int order = 0;
        switch (dim)
        {
        case ROW:
        {
            order = 1;
            break;
        }
        case COL:
        {
            order = 2;
            break;
        }
        case PAGE:
        {
            order = 3;
            break;
        }
        default:
        {
            throw new ConditionalException("dimToInt : Dimension parameter \"dim\" is not applicable here.");
        }
        }
        return order;
    }

    /**
     * 
     * @param B
     * @param tolerance
     * @return
     */
    public Indices3D EQ(Matrix3D B, double tolerance)
    {

        /***********************
         * Old Block **************************** checkMatrixDimensions(B);
         * Indices X = new Indices(m, n); X.setLogical(true); int[][] C =
         * X.getArray();
         * 
         * if (tolerance != 0.0d) { //Matrix addTolerance = B.plusCP(tolerance);
         * //double[][] upperT = addTolerance.getArray(); //Matrix subTolerance
         * = B.plusCP(-tolerance); //double[][] lowerT =
         * subTolerance.getArray(); double[][] C_B = B.getArray();
         * 
         * for (int i = 0; i < m; i++) { for (int j = 0; j < n; j++) { if
         * (!MathUtil.nan(A[i][j], C_B[i][j])) { if
         * (MathUtil.equalsWithTol(A[i][j], C_B[i][j], tolerance)) { C[i][j] =
         * 1; } //else {C[i][j] = 0.0d;} } }//end for
         * 
         * }//end for
         * 
         * }//end if else { double[][] M = B.getArray(); for (int i = 0; i < m;
         * i++) { for (int j = 0; j < n; j++) { if (!MathUtil.nan(A[i][j],
         * M[i][j])) { if (A[i][j] == M[i][j]) { C[i][j] = 1; } //else {C[i][j]
         * = 0.0d;} } }//end for
         * 
         * }//end for
         * 
         * }//end else
         *******************************************************/
        // ---------- The following line replaced the old block shown above
        // (7/7/10) ------------
        Indices3D X = equalAll(B, tolerance);
        // --------------------------------------------------------------------------------------

        return X;
    }// end method

    public double start()
    {
        return this.getPageAt(0).start();
    }

    public boolean isOnePage()
    {
        return this.array.length == 1;
    }

    public Matrix[] getArray()
    {
        return this.array;
    }

    public Matrix[] getArrayCopy()
    {
        return this.copy().getArray();
    }

    /**
     * C = A + B
     * 
     * @param B
     *            another matrix
     * @return A + B
     */
    public Matrix3D plus(Matrix3D B)
    {
        // ---------- modified/added on 16/01/11 ---------
        if (B.numel() == 1)
        {
            return this.plus(B.start());
        }
        if (this.numel() == 1)
        {
            return B.plus(this.start());
        }

        int[] sizDim = this.size();
        int m = sizDim[0];
        int n = sizDim[1];
        int p = sizDim[2];
        double val = 0.0;

        // -----------------------------------------------
        checkMatrixDimensions(B);

        Matrix3D X = new Matrix3D(m, n, p);
        // Matrix[] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                for (int k = 0; k < p; k++)
                {
                    // C[i][j] = A[i][j][k] + B.A[i][j][k];
                    val = this.get(i, j, k) + B.get(i, j, k);
                    X.set(i, j, k, val);
                }
            }
        }
        return X;
    }

    /**
     * C = A + B
     * 
     * @param B
     *            another matrix
     * @return A + B
     */
    public Matrix3D plus(double b)
    {
        int[] sizDim = this.size();
        int m = sizDim[0];
        int n = sizDim[1];
        int p = sizDim[2];
        double val = 0.0;
        Matrix3D X = new Matrix3D(m, n, p);
        // double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                for (int k = 0; k < p; k++)
                {
                    // C[i][j] = A[i][j] + b;
                    val = this.get(i, j, k) + b;
                    X.set(i, j, k, val);
                }
            }
        }
        return X;
    }

    // ///////////////////////////////////////////////////////
    /**
     * C = A - B
     * 
     * @param B
     *            another matrix
     * @return A - B
     */
    public Matrix3D minus(Matrix3D B)
    {
        // ---------- modified/added on 16/01/11 ---------
        if (B.numel() == 1)
        {
            return this.minus(B.start());
        }
        if (this.numel() == 1)
        {
            return B.minus(this.start());
        }

        int[] sizDim = this.size();
        int m = sizDim[0];
        int n = sizDim[1];
        int p = sizDim[2];
        double val = 0.0;

        // -----------------------------------------------
        checkMatrixDimensions(B);

        Matrix3D X = new Matrix3D(m, n, p);
        // Matrix[] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                for (int k = 0; k < p; k++)
                {
                    // C[i][j] = A[i][j][k] + B.A[i][j][k];
                    val = this.get(i, j, k) - B.get(i, j, k);
                    X.set(i, j, k, val);
                }
            }
        }
        return X;
    }

    /**
     * C = A - B
     * 
     * @param B
     *            another matrix
     * @return A - B
     */
    public Matrix3D minus(double b)
    {
        int[] sizDim = this.size();
        int m = sizDim[0];
        int n = sizDim[1];
        int p = sizDim[2];
        double val = 0.0;
        Matrix3D X = new Matrix3D(m, n, p);
        // double[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                for (int k = 0; k < p; k++)
                {
                    // C[i][j] = A[i][j] + b;
                    val = this.get(i, j, k) - b;
                    X.set(i, j, k, val);
                }
            }
        }
        return X;
    }

    // ///////////////////////////////////////////////////////
    /**
     * 
     * @param B
     * @return
     */
    public Indices3D EQ(Matrix3D B)
    {
        return EQ(B, 0.0d);
    }

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public Indices3D EQ(double b, double tolerance)
    {
        int[] siz = this.size();
        Matrix3D X = new Matrix3D(siz[0], siz[1], siz[2], b);
        return EQ(X, tolerance);
    }// end method

    /**
     * 
     * @param d
     * @return
     */
    public Indices3D EQ(double d)
    {
        // Matrix X = new Matrix(m,lenC,d);
        return EQ(d, 0.0d);
    }// end method

    /**
     * 
     * @param d
     * @return
     */
    public Indices3D EQ(Double d)
    {
        // Matrix X = new Matrix(m,lenC,d);
        if (d == null)
        {
            throw new IllegalArgumentException(" EQ: Parameter \"d\" must be non-null.");
        }
        double val = d.doubleValue();
        return EQ(val, 0.0d);
    }// end method

    /**
     * 
     * @param B
     * @param tolerance
     * @return
     */
    public Indices3D NEQ(Matrix3D B, double tolerance)
    {

        // ---- added 3/3/11 ----
        if (this.numel() == 1)
        {
            // return B.NEQ(A[0][0], tolerance);
        }
        if (B.numel() == 1)
        {
            // return this.NEQ(B.start(), tolerance);
        }
        // ----------------------

        checkMatrixDimensions(B);

        int[] sizDim = this.size();
        int m = sizDim[0];
        int n = sizDim[1];
        int p = sizDim[2];

        Indices3D X = new Indices3D(m, n, p);
        X.setLogical(true);
        // Indices[] C = X.getArray();

        if (tolerance != 0.0d)
        {
            Matrix3D upperT = B.plus(tolerance);
            // double[][] upperT = addTolerance.getArray();
            Matrix3D lowerT = B.plus(-tolerance);
            // double[][] lowerT = subTolerance.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    for (int k = 0; k < p; k++)
                    {
                        if (!MathUtil.nan(new double[]
                        {
                                this.get(i, j, k), lowerT.get(i, j, k), upperT.get(i, j, k)
                        }))
                        {
                            if (!(this.get(i, j, k) >= lowerT.get(i, j, k) && this.get(i, j, k) <= upperT.get(i, j, k)))
                            {
                                X.set(i, j, k, 1);// C[i][j] = 1;
                            }
                            // else {C[i][j] = 1.0d;}
                        }
                    }
                }// end for

            }// end for

        }// end if
        else
        {
            // double[][] M = B.getArray();
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    for (int k = 0; k < p; k++)
                    {
                        if (!MathUtil.nan(this.get(i, j, k), B.get(i, j, k)))
                        {
                            if (!(this.get(i, j, k) == B.get(i, j, k)))
                            {
                                X.set(i, j, k, 1);// C[i][j] = 1;
                            }
                            // else {C[i][j] = 1.0d;}
                        }
                    }
                }// end for

            }// end for

        }// end else

        return X;
    }// end method

    /**
     * 
     * @param B
     * @return
     */
    public Indices3D NEQ(Matrix3D B)
    {
        return NEQ(B, 0.0d);
    }// end method

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public Indices3D NEQ(double b, double tolerance)
    {
        int[] sizDim = this.size();
        int m = sizDim[0];
        int n = sizDim[1];
        int p = sizDim[2];
        Matrix3D X = new Matrix3D(m, n, p, b);
        return NEQ(X, tolerance);
    }// end method

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public Indices3D NEQ(Double b, double tolerance)
    {
        return NEQ(b.doubleValue(), tolerance);
    }

    /**
     * 
     * @param b
     * @return
     */
    public Indices3D NEQ(double b)
    {
        int[] sizDim = this.size();
        int m = sizDim[0];
        int n = sizDim[1];
        int p = sizDim[2];
        Matrix3D X = new Matrix3D(m, n, p, b);
        return NEQ(X, 0.0d);
    }// end method

    /** Check if size(A) == size(B) **/
    private void checkMatrixDimensions(Matrix3D B)
    {
        Indices thisSize = new Indices(this.size());
        Indices bSize = new Indices(B.size());
        boolean cond = thisSize.EQ(bSize).allBoolean();
        if (!cond)
        {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
    }

    // ------------------------------------------------------------
    /**
     * 
     * @param B
     * @param tolerance
     * @return
     */
    public Indices3D equalAll(Matrix3D B, double tolerance)
    {
        checkMatrixDimensions(B);

        Matrix mat = this.array[0];
        int m = mat.getRowDimension();
        int n = mat.getColumnDimension();
        int k = this.array.length;

        Indices3D X = new Indices3D(m, n, k);
        // X.setLogical(true);

        for (int u = 0; u < k; u++)
        {
            // int[][] C = X.getArray();
            Matrix A = this.getPageAt(u);
            Matrix C_B = B.getPageAt(u);
            int[][] C = X.getPage(u).getArray();
            // System.out.println("=====Execute====");
            if (tolerance != 0.0d)
            {
                // Matrix addTolerance = B.plusCP(tolerance);
                // double[][] upperT = addTolerance.getArray();
                // Matrix subTolerance = B.plusCP(-tolerance);
                // double[][] lowerT = subTolerance.getArray();

                for (int i = 0; i < m; i++)
                {
                    for (int j = 0; j < n; j++)
                    {
                        if (!MathUtil.nan(A.get(i, j), C_B.get(i, j)))
                        {
                            if (A.get(i, j) == Double.NEGATIVE_INFINITY && C_B.get(i, j) == Double.NEGATIVE_INFINITY)
                            {
                                C[i][j] = 1;
                            }
                            else if (A.get(i, j) == Double.NEGATIVE_INFINITY
                                    && C_B.get(i, j) != Double.NEGATIVE_INFINITY)
                            {
                                ;
                            }
                            else if (A.get(i, j) != Double.NEGATIVE_INFINITY
                                    && C_B.get(i, j) == Double.NEGATIVE_INFINITY)
                            {
                                ;
                            }
                            else if (A.get(i, j) == Double.POSITIVE_INFINITY
                                    && C_B.get(i, j) == Double.POSITIVE_INFINITY)
                            {
                                C[i][j] = 1;
                            }
                            else if (A.get(i, j) == Double.POSITIVE_INFINITY
                                    && C_B.get(i, j) != Double.POSITIVE_INFINITY)
                            {
                                ;
                            }
                            else if (A.get(i, j) != Double.POSITIVE_INFINITY
                                    && C_B.get(i, j) == Double.POSITIVE_INFINITY)
                            {
                                ;
                            }
                            else if (MathUtil.equalsWithTol(A.get(i, j), C_B.get(i, j), tolerance))
                            {
                                C[i][j] = 1;
                            }
                            // else {C[i][j] = 0.0d;}
                        }
                        else if (Double.isNaN(A.get(i, j)) && (Double.isNaN(C_B.get(i, j))))
                        {
                            C[i][j] = 1;
                        }
                    }// end for

                }// end for

            }// end if
            else
            {
                // double[][] M = B.getArray();
                for (int i = 0; i < m; i++)
                {
                    for (int j = 0; j < n; j++)
                    {
                        if (!MathUtil.nan(A.get(i, j), C_B.get(i, j)))
                        {
                            if (A.get(i, j) == Double.NEGATIVE_INFINITY && C_B.get(i, j) == Double.NEGATIVE_INFINITY)
                            {
                                C[i][j] = 1;
                            }
                            else if (A.get(i, j) == Double.NEGATIVE_INFINITY
                                    && C_B.get(i, j) != Double.NEGATIVE_INFINITY)
                            {
                                ;
                            }
                            else if (A.get(i, j) != Double.NEGATIVE_INFINITY
                                    && C_B.get(i, j) == Double.NEGATIVE_INFINITY)
                            {
                                ;
                            }
                            else if (A.get(i, j) == Double.POSITIVE_INFINITY
                                    && C_B.get(i, j) == Double.POSITIVE_INFINITY)
                            {
                                C[i][j] = 1;
                            }
                            else if (A.get(i, j) == Double.POSITIVE_INFINITY
                                    && C_B.get(i, j) != Double.POSITIVE_INFINITY)
                            {
                                ;
                            }
                            else if (A.get(i, j) != Double.POSITIVE_INFINITY
                                    && C_B.get(i, j) == Double.POSITIVE_INFINITY)
                            {
                                ;
                            }
                            else if (A.get(i, j) == C_B.get(i, j))
                            {
                                C[i][j] = 1;
                            }
                            // System.out.println("=====Execute 1====");
                        }
                        else if (Double.isNaN(A.get(i, j)) && Double.isNaN(C_B.get(i, j)))
                        {
                            C[i][j] = 1;
                            // System.out.println("=====Execute 2====");
                        }
                        // else{ System.out.println("=====Execute 3===="); }
                    }// end for

                }// end for

            }// end else
        }

        return X;
    }// end method

    /**
     * 
     * @param B
     * @return
     */
    public Indices3D equalAll(Matrix3D B)
    {
        return equalAll(B, 0.0d);
    }

    /**
     * 
     * @param b
     * @param tolerance
     * @return
     */
    public Indices3D equalAll(double b, double tolerance)
    {
        Matrix mat = this.array[0];
        int m = mat.getRowDimension();
        int n = mat.getColumnDimension();
        int k = this.array.length;
        Matrix3D X = new Matrix3D(m, n, k, b);
        return equalAll(X, tolerance);
    }// end method

    /**
     * 
     * @param d
     * @return
     */
    public Indices3D equalAll(double d)
    {
        // Matrix X = new Matrix(m,lenC,d);
        return equalAll(d, 0.0d);
    }// end method

    /**
     * 
     * @param d
     * @return
     */
    public Indices3D equalAll(Double d)
    {
        // Matrix X = new Matrix(m,lenC,d);
        if (d == null)
        {
            throw new IllegalArgumentException(" equalAll: Parameter \"b\" must be non-null.");
        }
        double val = d.doubleValue();
        return equalAll(val, 0.0d);
    }// end method

    public Indices sizeI()
    {
        return new Indices(size());
    }

    // ------------------------------------------------------------
    public int[] size()
    {
        Matrix mat = this.array[0];
        int i = mat.getRowDimension();
        int j = mat.getColumnDimension();
        int k = this.array.length;
        return new int[]
        {
                i, j, k
        };
    }

    public int size(Dimension dim)
    {
        if (dim == null)
        {
            throw new IllegalArgumentException(" size: Parameter \"dim\" must be non-null.");
        }
        Matrix mat = this.array[0];
        int i = mat.getRowDimension();
        int j = mat.getColumnDimension();
        int k = this.array.length;

        int val = 0;
        if (dim == Dimension.ROW)
        {
            val = i;
        }
        else if (dim == Dimension.COL)
        {
            val = j;
        }
        else
        {
            val = k;
        }
        return val;
    }

    public Object squeeze()
    {
        Indices sizInd = new Indices(this.size());

        // sizInd.printInLabel("sizInd - 1");
        FindInd find = sizInd.EQ(1).findIJ();
        Indices sizInd2 = find.getIndexInd();
        // sizInd2.printInLabel("sizInd2");
        if (sizInd2 == null)
        {
            return this.copy();
        }
        // sizInd = sizInd.getFindIj(find);
        // sizInd.printInLabel("sizInd - 2");

        int[] intArr = sizInd2.getRowPackedCopy();
        sizInd = sizInd.removeCols(intArr);
        int len = 0;
        if (sizInd != null)
        {
            len = sizInd.length();
        }

        Matrix sqzMat = null;

        if (len == 0)
        {
            intArr = new int[]
            {
                    1, 1
            };
        }
        else if (len == 1)
        {
            intArr = new int[]
            {
                    sizInd.start(), 1
            };
        }
        else
        {// len == 2
            intArr = sizInd.getRowPackedCopy();
        }

        int newR = intArr[0];
        int newC = intArr[1];
        sqzMat = reshape(newR, newC);

        return sqzMat;
    }

    public Matrix3D copy()
    {
        // Matrix3D(int dim1, int dim2, int dim3)
        Matrix mat = this.array[0];
        int r = mat.getRowDimension();
        int c = mat.getColumnDimension();
        int k = this.array.length;
        Matrix3D copy = new Matrix3D(r, c, k);
        for (int i = 0; i < k; i++)
        {
            Matrix deepCopy = this.array[i].copy();
            copy.setPageAt(i, deepCopy);
        }
        return copy;
    }

    public Matrix3D permute(Dimension dim1, Dimension dim2, Dimension dim3)
    {// int order, int order2, int order3) {
        int order1 = dimToInt(dim1);
        int order2 = dimToInt(dim2);
        int order3 = dimToInt(dim3);

        Indices ind = new Indices(new int[]
        {
                order1, order2, order3
        });
        Indices ind2 = ind.LT(1).OR(ind.GT(3)).find();
        if (ind2 != null)
        {
            throw new ConditionalException(
                    "permute : Integer parameters \"order1\", \"order2\" and \"order3\" must be in the interval [1,3].");
        }
        ind = JDatafun.sort(ind);
        ind = JDatafun.diff(ind).EQ(0).find();
        if (ind != null)
        {
            throw new ConditionalException(
                    "permute : Dimension parameters \"dim1\", \"dim2\" and \"dim3\" must be unique.");
        }

        Matrix mat = this.array[0];
        int iOld = mat.getRowDimension();
        int jOld = mat.getColumnDimension();
        int kOld = this.array.length;

        ind = new Indices(new int[]
        {
                order1, order2, order3
        }).minus(1); // subtract 1, since java index starts @ 0

        Indices permDim = new Indices(new int[]
        {
                iOld, jOld, kOld
        });
        permDim = permDim.getEls(ind.getRowPackedCopy()).toRowVector();

        int iNew = permDim.get(0, 0);
        int jNew = permDim.get(0, 1);
        int kNew = permDim.get(0, 2);

        // test to see if it is the original order
        ind = new Indices(new int[]
        {
                order1, order2, order3
        });
        Indices original = new Indices(new int[]
        {
                1, 2, 3
        });
        boolean same = original.EQ(ind).allBoolean();
        if (same)
        {
            return this.copy();
        }

        Matrix3D perm = new Matrix3D(iNew, jNew, kNew);
        String strOrder = order1 + "" + order2 + "" + order3;

        if ("132".equals(strOrder))
        {
            perm132(perm);
        }
        else if ("213".equals(strOrder))
        {
            perm213(perm);
        }
        else if ("231".equals(strOrder))
        {
            perm231(perm);
        }
        else if ("321".equals(strOrder))
        {
            perm321(perm);
        }
        else if ("312".equals(strOrder))
        {
            perm312(perm);
        }
        else
        {
            throw new ConditionalException("permute : It shouldn't go thru this block.");
        }

        return perm;
    }

    private void perm132(Matrix3D perm)
    {
        int R = this.getRowDimension();
        int C = this.getColDimension();
        int P = this.getPageDimension();

        for (int i = 0; i < R; i++)
        {
            for (int j = 0; j < C; j++)
            {
                for (int k = 0; k < P; k++)
                {
                    double val = this.get(i, j, k);
                    perm.set(i, k, j, val);
                }
            }
        }

    }

    private void perm213(Matrix3D perm)
    {
        int R = this.getRowDimension();
        int C = this.getColDimension();
        int P = this.getPageDimension();

        for (int i = 0; i < R; i++)
        {
            for (int j = 0; j < C; j++)
            {
                for (int k = 0; k < P; k++)
                {
                    double val = this.get(i, j, k);
                    perm.set(j, i, k, val);
                }
            }
        }

    }

    private void perm231(Matrix3D perm)
    {
        int R = this.getRowDimension();
        int C = this.getColDimension();
        int P = this.getPageDimension();

        for (int i = 0; i < R; i++)
        {
            for (int j = 0; j < C; j++)
            {
                for (int k = 0; k < P; k++)
                {
                    double val = this.get(i, j, k);
                    perm.set(j, k, i, val);
                }
            }
        }

    }

    private void perm321(Matrix3D perm)
    {
        int R = this.getRowDimension();
        int C = this.getColDimension();
        int P = this.getPageDimension();

        for (int i = 0; i < R; i++)
        {
            for (int j = 0; j < C; j++)
            {
                for (int k = 0; k < P; k++)
                {
                    double val = this.get(i, j, k);
                    perm.set(k, j, i, val);
                }
            }
        }

    }

    private void perm312(Matrix3D perm)
    {
        int R = this.getRowDimension();
        int C = this.getColDimension();
        int P = this.getPageDimension();

        for (int i = 0; i < R; i++)
        {
            for (int j = 0; j < C; j++)
            {
                for (int k = 0; k < P; k++)
                {
                    double val = this.get(i, j, k);
                    perm.set(k, i, j, val);
                }
            }
        }

    }

    public int length()
    {
        int len = 0;
        int i = this.array[0].getRowDimension();
        int j = this.array[0].getColumnDimension();
        int k = this.array.length;
        Indices ind = new Indices(new int[]
        {
                i, j, k
        });
        ind = JDatafun.max(ind);
        len = ind.start();
        return len;
    }

    public Matrix3D reshape(int newR, int newC, int newP)
    {
        int tEl = this.numel();
        if (tEl != (newR * newC * newP))
        {
            throw new ConditionalException("reshape : The new dimension of [" + newR + " x " + newC + " x " + newP
                    + "] must equal the number of elements (= " + tEl + ").");
        }
        Matrix3D rs = new Matrix3D(newR, newC, newP);
        Matrix colVec = this.toColVector();
        int count = 0;
        double val = 0.0;
        for (int k = 0; k < newP; k++)
        {
            Matrix newPage = new Matrix(newR, newC);
            for (int j = 0; j < newC; j++)
            {
                for (int i = 0; i < newR; i++)
                {
                    val = colVec.get(count++, 0);
                    newPage.set(i, j, val);
                }
            }
            rs.setPageAt(k, newPage);
        }
        return rs;

    }

    public Matrix reshape(int newR, int newC)
    {
        int tEl = this.numel();
        if (tEl != (newR * newC))
        {
            throw new ConditionalException("reshape : The new dimension of [" + newR + " x " + newC
                    + "] must equal the number of elements (= " + tEl + ").");
        }
        Matrix rs = new Matrix(newR, newC);
        Matrix colVec = this.toColVector();
        int count = 0;
        double val = 0.0;

        for (int j = 0; j < newC; j++)
        {
            for (int i = 0; i < newR; i++)
            {
                val = colVec.get(count++, 0);
                rs.set(i, j, val);
            }
        }
        return rs;
    }

    public void print3dArray(String varName, int w, int d)
    {
        // arrayObj = new double[dims.get(0)][dims.get(1)][dims.get(2)];
        // int m1 = this.getRowDimension();
        // int m2 = this.getColDimension();
        int k = this.getPageDimension();
        // double[][][] A = (double[][][]) array;
        // Idx = new Index(new int[]{m1, m2, k});
        // double val = 0.0;

        for (int i3 = 0; i3 < k; i3++)
        {
            Matrix Amat = this.array[i3];

            String localName = "";
            if (printJavaIndex)
            {
                localName = varName + "(:,:," + i3 + ") = ";
            }
            else
            {
                localName = varName + "(:,:," + (i3 + 1) + ") = ";
            }
            printMatrix(Amat, w, d, localName);
            // collect2D.add(Amat);
        }
    }

    /**
     * Print the matrix to stdout. Line the elements up in columns with a
     * Fortran-like 'Fw.d' style format.
     * 
     * @param w
     *            Column width.
     * @param d
     *            Number of digits after the decimal.
     */
    public static void printMatrix(Matrix Amat, int w, int d, String nameSubArray)
    {
        printMatrix(Amat, new PrintWriter(System.out, true), w, d, nameSubArray);
    }

    /**
     * Print the matrix to the output stream. Line the elements up in columns
     * with a Fortran-like 'Fw.d' style format.
     * 
     * @param output
     *            Output stream.
     * @param w
     *            Column width.
     * @param d
     *            Number of digits after the decimal.
     */
    public static void printMatrix(Matrix Amat, PrintWriter output, int w, int d, String nameSubArray)
    {
        DecimalFormat format = new DecimalFormat();
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        printMatrix(Amat, output, format, w + 2, nameSubArray);
    }

    /**
     * Print the matrix to stdout. Line the elements up in columns. Use the
     * format object, AND right justify within columns of width characters.
     * 
     * @param format
     *            A Formatting object for individual elements.
     * @param width
     *            Field width for each column.
     */
    public static void printMatrix(Matrix Amat, NumberFormat format, int width, String nameSubArray)
    {
        printMatrix(Amat, new PrintWriter(System.out, true), format, width, nameSubArray);
    }

    // DecimalFormat is a little disappointing coming from Fortran OR C's
    // printf.
    // Since it doesn't pad on the left, the elements will come out different
    // widths. Consequently, we'll pass the desired column width in as an
    // argument AND do the extra padding ourselves.
    /**
     * Print the matrix to the output stream. Line the elements up in columns.
     * Use the format object, AND right justify within columns of width
     * characters.
     * 
     * @param output
     *            the output stream.
     * @param format
     *            A formatting object to format the matrix elements
     * @param width
     *            Column width.
     */
    public static void printMatrix(Matrix Amat, PrintWriter output, NumberFormat format, int width, String nameSubArray)
    {
        double[][] A = Amat.getArray();
        int m = Amat.getRowDimension();
        int n = Amat.getColumnDimension();
        // nameSubArray
        output.println("" + nameSubArray);
        output.println(); // start on new line.
        if (A != null)
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    String s = format.format(A[i][j]); // format the number

                    int padding = Math.max(1, width - s.length()); // At _least_
                                                                   // 1 space

                    for (int k = 0; k < padding; k++)
                    {
                        output.print(' ');
                    }
                    output.print(s);
                }
                output.println();
            }
        }
        else
        {
            String empty = "Empty matrix: " + m + "-by-" + n;
            output.print(empty);
        }
        output.println(); // end with blank line.

    }

    public static Matrix3D zeros(int... dim)
    {
        if (dim == null)
        {
            throw new ConditionalException("zeros: Integer array parameter \"dim\" must be non-null.");
        }
        if (dim.length != 3)
        {
            throw new ConditionalException("zeros : Length of integer array parameter \"dim\" must be 3;");
        }

        int dim1 = dim[0];
        int dim2 = dim[1];
        int dim3 = dim[2];

        Matrix3D Zr = new Matrix3D(dim1, dim2, dim3);
        return Zr;
    }

    public static Matrix3D zeros(int dim1, int dim2, int dim3)
    {
        Matrix3D Zr = new Matrix3D(dim1, dim2, dim3);
        return Zr;
    }

    public static Matrix3D ones(int... dim)
    {

        if (dim == null)
        {
            throw new ConditionalException("ones: Integer array parameter \"dim\" must be non-null.");
        }
        if (dim.length != 3)
        {
            throw new ConditionalException("ones : Length of integer array parameter \"dim\" must be 3;");
        }

        int dim1 = dim[0];
        int dim2 = dim[1];
        int dim3 = dim[2];

        Matrix3D Zr = new Matrix3D(dim1, dim2, dim3, 1.0);
        return Zr;
    }

    public static Matrix3D ones(int dim1, int dim2, int dim3)
    {
        Matrix3D Zr = new Matrix3D(dim1, dim2, dim3, 1.0);
        return Zr;
    }

    /**
     * @param printJavaIndex
     *            the printJavaIndex to set
     */
    public void setPrintJavaIndex(boolean printJavaIndex)
    {
        this.printJavaIndex = printJavaIndex;
    }

    /**
     * 
     * @param indices
     * @return
     */
    public static Matrix3D indicesToMatrix3D(Indices3D indices)
    {
        if (indices == null)
        {
            return null;
        }

        Indices ind = indices.getPage(0);
        int m = ind.getRowDimension();
        int n = ind.getColumnDimension();
        int k = indices.getPageDimension();

        double val = 0.0;
        Matrix3D mat = new Matrix3D(m, n, k);
        for (int u = 0; u < k; u++)
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    val = (double) indices.get(i, j, u);
                    if (indices.get(i, j, u) != Integer.MAX_VALUE)
                    {
                        mat.set(i, j, u, val);
                    }
                    else
                    {
                        mat.set(i, j, u, Double.NaN);
                    }
                }
            }
        }
        return mat;
    }

    public Matrix3D repmat(int... dim123)
    {
        // int dim2 = dim23[1];
        // int dim3 = dim23[2];
        if (dim123 == null)
        {
            throw new ConditionalException("repmat : Parameter \"dim123\" must be non-null.");
        }
        if (dim123.length == 0)
        {
            throw new ConditionalException("repmat : Parameter \"dim123\" must be non-empty.");
        }
        if (dim123.length > 3)
        {
            throw new ConditionalException("repmat : Length of parameter \"dim123\" must be 3 or less.");
        }

        int[] idim = dim123;// {dim1, dim2, dim3};
        Indices siz = null;

        if (idim.length == 1)
        {
            int[] mg =
            {
                    dim123[0], dim123[0]
            };
            siz = new Indices(mg);
        }
        else
        {
            siz = new Indices(idim);
        }

        // siz.printInLabel("siz1");
        Indices indLT = siz.LT(1);
        if (indLT.anyBoolean())
        {
            throw new ConditionalException("repmat : All input integer parameters must be at least one.");
        }

        Indices Asiz = this.sizeI();// size(A);
        int lenSiz = siz.length();
        int lenAsiz = Asiz.length();
        int diffLen = lenSiz - lenAsiz;
        if (diffLen > 0)
        {// Asiz = [Asiz ones(1,length(siz)-length(Asiz))];
            Asiz = Asiz.mergeH(Indices.ones(1, diffLen));
        }
        lenAsiz = Asiz.length();
        diffLen = lenAsiz - lenSiz;
        // siz.printInLabel("siz2");
        if (diffLen > 0)
        {// siz = [siz ones(1,length(Asiz)-length(siz))];
            siz = siz.mergeH(Indices.ones(1, diffLen));
        }

        // Asiz.printInLabel("Asiz");
        // siz.printInLabel("siz3");

        Cell subs = new Cell(1, lenAsiz);
        Cell subsRev = new Cell(1, lenAsiz);
        for (int i = (lenAsiz - 1); i >= 0; i--)
        {// length(Asiz):-1:1
         // indLT = (1:Asiz(i))';
            int asizg = Asiz.get(0, i);
            // System.out.println(i+") asizg = "+asizg);
            Indices ind = Indices.linspace(1, asizg).transpose();
            // ind.printInLabel("ind");
            int[] arr = new Indices(1, siz.get(0, i)).getRowPackedCopy();// Indices.linspace(0,
                                                                         // siz.get(0,
                                                                         // i) -
                                                                         // 1).getRowPackedCopy();
            // subs{i} = indLT(:,ones(1,siz(i)));
            // new Indices(arr).printInLabel("arr");
            // ind = ind.getColumns(arr);
            ind = ind.getColumns(arr);
            subs.set(0, i, ind);
            subsRev.set(0, (lenAsiz - 1) - i, ind.toColVector().minus(1));
        }// end

        boolean print = false;
        // The following is for debugging only
        if (print)
        {
            for (int i = 0; i < subsRev.length(); i++)
            {
                ((Indices) subs.getElementAt(i)).printInLabel("ind" + (i + 1));
            }
        }

        Indices page = (Indices) subsRev.get(0, 0);
        Indices coln = (Indices) subsRev.get(0, 1);
        Indices rowm = (Indices) subsRev.get(0, 2);
        int colLen = rowm.numel() * coln.numel();
        int newRows = rowm.numel();// rowm.numel(), coln.numel(), page.numel());

        Matrix3D tile = new Matrix3D(rowm.numel(), coln.numel(), page.numel());

        for (int k = 0; k < page.length(); k++)
        {
            // System.out.println("========== Page " + k + " ==========");
            double[] arrRow = new double[colLen];
            int count = 0;
            for (int j = 0; j < coln.length(); j++)
            {
                for (int i = 0; i < rowm.length(); i++)
                {
                    int pgk = page.get(k, 0);
                    int cnj = coln.get(j, 0);
                    int rmi = rowm.get(i, 0);
                    int[] arr =
                    {
                            rmi, cnj, pgk
                    };
                    double val = this.get(arr);
                    arrRow[count++] = val;
                    // new Indices(arr)
                    // System.out.println("" + this.get(arr));
                }
            }
            Matrix newPage = new Matrix(arrRow, newRows);
            tile.setPageAt(k, newPage);
        }
        // }
        // }
        return tile;
    }

    static void permuteEx()
    {
        double[][] a1 =
        {
                {
                        1, 2, 3
                },
                {
                        4, 5, 6
                },
                {
                        7, 8, 9
                }
        };

        double[][] a2 =
        {
                {
                        0, 5, 4
                },
                {
                        2, 7, 6
                },
                {
                        9, 3, 1
                }
        };

        Matrix[] multiArr = new Matrix[]
        {
                new Matrix(a1), new Matrix(a2)
        };

        Matrix3D MA = new Matrix3D(3, 3, 2);
        MA.setPageAt(0, multiArr[0]);
        MA.setPageAt(1, multiArr[1]);
        MA.print3dArray("MA", 6, 0);

        System.out.println(" ");

        Matrix3D MA2 = MA.permute(Dimension.ROW, Dimension.COL, Dimension.PAGE);
        MA2.print3dArray("MA2", 6, 0);
    }

    static void reshapeEx3D()
    {
        double[][] a1 =
        {
                {
                        -9, 3, -23, 24
                },
                {
                        -33, 6, 24, -1
                }
        };

        double[][] a2 =
        {
                {
                        7, -4, -12, -3
                },
                {
                        3, 15, 44, 2
                }
        };

        double[][] a3 =
        {
                {
                        21, -2, 6, 14
                },
                {
                        1, -17, -27, 32
                }
        };

        Matrix[] multiArr = new Matrix[]
        {
                new Matrix(a1), new Matrix(a2), new Matrix(a3)
        };

        Matrix3D MA = new Matrix3D(2, 4, 3);
        MA.setPageAt(0, multiArr[0]);
        MA.setPageAt(1, multiArr[1]);
        MA.setPageAt(2, multiArr[2]);
        MA.print3dArray("MA", 6, 0);

        System.out.println(" ");

        // Matrix3D MA2 = MA.permute(Dimension.ROW, Dimension.COL,
        // Dimension.PAGE);
        // MA2.print3dArray("MA2", 6, 0);
        Matrix3D RES = MA.reshape(3, 2, 4);
        RES.print3dArray("RES3x2x4", 6, 0);

        System.out.println(" ");

        RES = MA.reshape(2, 4, 3);
        RES.print3dArray("RES2x4x3", 6, 0);

        System.out.println(" ");

        RES = MA.reshape(6, 2, 2);
        RES.print3dArray("RES6x2x2", 6, 0);
    }

    static Matrix3D getMat()
    {
        double[][] a1 =
        {
                {
                        -9, 3, -23, 24
                },
                {
                        -33, 6, 24, -1
                }
        };

        double[][] a2 =
        {
                {
                        7, -4, -12, -3
                },
                {
                        3, 15, 44, 2
                }
        };

        double[][] a3 =
        {
                {
                        21, -2, 6, 14
                },
                {
                        1, -17, -27, 32
                }
        };

        Matrix[] multiArr = new Matrix[]
        {
                new Matrix(a1), new Matrix(a2), new Matrix(a3)
        };

        Matrix3D MA = new Matrix3D(2, 4, 3);
        MA.setPageAt(0, multiArr[0]);
        MA.setPageAt(1, multiArr[1]);
        MA.setPageAt(2, multiArr[2]);

        return MA;
    }

    static void reshapeEx2D()
    {
        double[][] a1 =
        {
                {
                        -9, 3, -23, 24
                },
                {
                        -33, 6, 24, -1
                }
        };

        double[][] a2 =
        {
                {
                        7, -4, -12, -3
                },
                {
                        3, 15, 44, 2
                }
        };

        double[][] a3 =
        {
                {
                        21, -2, 6, 14
                },
                {
                        1, -17, -27, 32
                }
        };

        Matrix[] multiArr = new Matrix[]
        {
                new Matrix(a1), new Matrix(a2), new Matrix(a3)
        };

        Matrix3D MA = new Matrix3D(2, 4, 3);
        MA.setPageAt(0, multiArr[0]);
        MA.setPageAt(1, multiArr[1]);
        MA.setPageAt(2, multiArr[2]);
        MA.print3dArray("MA", 6, 0);

        System.out.println(" ");

        // Matrix3D MA2 = MA.permute(Dimension.ROW, Dimension.COL,
        // Dimension.PAGE);
        // MA2.print3dArray("MA2", 6, 0);
        Matrix RES = MA.reshape(8, 3);
        RES.printInLabel("RES8x3", 6, 0);

        System.out.println(" ");

        RES = MA.reshape(6, 4);
        RES.printInLabel("RES6x4", 6, 0);

        System.out.println(" ");

        RES = MA.reshape(2, 12);
        RES.printInLabel("RES2x12", 6, 0);
    }

    static void anyEx()
    {
        double[][] a1 =
        {
                {
                        13, 2
                },
                {
                        14, 4
                },
                {
                        8, 0
                },
                {
                        2, 4
                }
        };

        double[][] a2 =
        {
                {
                        12, 3
                },
                {
                        4, 4
                },
                {
                        14, 0
                },
                {
                        5, 7
                }
        };

        double[][] a3 =
        {
                {
                        5, 14
                },
                {
                        12, 4
                },
                {
                        9, 0
                },
                {
                        8, 11
                }
        };

        Matrix[] multiArr = new Matrix[]
        {
                new Matrix(a1), new Matrix(a2), new Matrix(a3)
        };

        Matrix3D MA = new Matrix3D(4, 2, 3);
        MA.setPageAt(0, multiArr[0]);
        MA.setPageAt(1, multiArr[1]);
        MA.setPageAt(2, multiArr[2]);
        MA.print3dArray("MA", 6, 0);

        System.out.println(" ");

        // Matrix3D MA2 = MA.permute(Dimension.ROW, Dimension.COL,
        // Dimension.PAGE);
        // MA2.print3dArray("MA2", 6, 0);
        Indices3D ANY = MA.ANY(Dimension.ROW);
        ANY.print3dArray("ANY1", 6);

        System.out.println(" ");

        ANY = MA.ANY(Dimension.COL);
        ANY.print3dArray("ANY2", 6);

        System.out.println(" ");

        ANY = MA.ANY(Dimension.PAGE);
        ANY.print3dArray("ANY3", 6);
    }

    static void squeezeEx()
    {
        Matrix3D X = new Matrix3D(4, 1, 1);
        double[] x1 =
        {
                12, 14, 2, 14
        };
        X.setPageAt(0, new Matrix(x1).transpose());
        /*
         * double[] x2 = {9, 1, 4, 8}; X.setPageAt(1, new
         * Matrix(x2).transpose()); double[] x3 = {14, 14, 2, 15};
         * X.setPageAt(2, new Matrix(x3).transpose());
         */
        X.print3dArray("X", 6, 0);

        System.out.println(" ================================== ");

        Object obj = X.squeeze();
        if (obj instanceof Matrix)
        {
            ((Matrix) obj).printInLabel("X-squeeze", 6, 0);
        }
        else
        {
            ((Matrix3D) obj).print3dArray("X-squeeze", 6, 0);
        }

        if (true)
        {
            return;
        }

        System.out.println(" ================================== ");

        Matrix3D X2 = new Matrix3D(1, 1, 1, -8);
        X2.print3dArray("X2", 6, 0);
        obj = X2.squeeze();
        if (obj instanceof Matrix)
        {
            ((Matrix) obj).printInLabel("X2-squeeze", 6, 0);
        }
        else
        {
            ((Matrix3D) obj).print3dArray("X2-squeeze", 6, 0);
        }

        System.out.println(" ================================== ");

        Matrix3D X3 = Matrix3D.getMat();
        X3.print3dArray("X3", 6, 0);
        obj = X3.squeeze();
        if (obj instanceof Matrix)
        {
            ((Matrix) obj).printInLabel("X3-squeeze", 6, 0);
        }
        else
        {
            ((Matrix3D) obj).print3dArray("X3-squeeze", 6, 0);
        }

    }

    static void repmatEx()
    {
        Matrix mat1 = Matrix.linspace(1.0, 6.0, 6);
        mat1 = mat1.reshape(2, 3);
        // mat1.printInLabel("mat1", 0);

        Matrix mat2 = Matrix.linspace(7.0, 12.0, 6);
        mat2 = mat2.reshape(2, 3);
        // mat2.printInLabel("mat2", 0);

        Matrix mat3 = Matrix.linspace(13.0, 18.0, 6);
        mat3 = mat3.reshape(2, 3);
        // mat3.printInLabel("mat3", 0);

        Matrix3D mat3d = new Matrix3D(2, 3, 3);
        mat3d.setPageAt(0, mat1);
        mat3d.setPageAt(1, mat2);
        mat3d.setPageAt(2, mat3);

        mat3d.print3dArray("mat3d", 6, 0);

        Matrix3D tile = mat3d.repmat(2, 3);// (2, 3, 2);
        tile.print3dArray("tile", 6, 0);
    }

    private Matrix3D concatVert(Dimension dim, Matrix3D mat)
    {// rowwise concatenation so, the number of columns and pages must be the
     // same
        if (dim == null)
        {
            throw new ConditionalException("concatVert : Parameter \"dim\" must be \"Dimension.ROW\" and non-null.");
        }
        else if (dim != Dimension.ROW)
        {
            throw new ConditionalException("concatVert : Parameter \"dim\" must be \"Dimension.ROW\".");
        }

        int m = this.array[0].getRowDimension();
        int n = this.array[0].getColumnDimension();
        int p = this.array.length;
        int[] np =
        {
                n, p
        };
        Indices npInd = new Indices(np);

        int m2 = mat.array[0].getRowDimension();
        int n2 = mat.array[0].getColumnDimension();
        int p2 = mat.array.length;
        int[] np2 =
        {
                n2, p2
        };
        Indices npInd2 = new Indices(np2);

        boolean equal = npInd.EQ(npInd2).allBoolean();
        if (!equal)
        {
            String strThis = "{ this : [" + n + " x " + p + "], mat : [" + n2 + " x " + p2 + "] }";
            throw new ConditionalException("concatVert : The number of columns and pages must be the same.\n" + strThis);
        }

        int totRows = m + m2;
        Matrix3D vert = new Matrix3D(totRows, n, p);
        for (int v = 0; v < p; v++)
        {
            Matrix vertPage = this.getPageAt(v).mergeV(mat.getPageAt(v));
            vert.setPageAt(v, vertPage);
        }

        return vert;
    }

    private Matrix3D concatHorz(Dimension dim, Matrix3D mat)
    {// col-wise concatenation so, the number of pages and pages must be the
     // same
        if (dim == null)
        {
            throw new ConditionalException("concatVert : Parameter \"dim\" must be \"Dimension.COL\" and non-null.");
        }
        else if (dim != Dimension.COL)
        {
            throw new ConditionalException("concatVert : Parameter \"dim\" must be \"Dimension.COL\".");
        }

        int m = this.array[0].getRowDimension();
        int n = this.array[0].getColumnDimension();
        int p = this.array.length;
        int[] mp =
        {
                m, p
        };
        Indices mpInd = new Indices(mp);

        int m2 = mat.array[0].getRowDimension();
        int n2 = mat.array[0].getColumnDimension();
        int p2 = mat.array.length;
        int[] mp2 =
        {
                m2, p2
        };
        Indices mpInd2 = new Indices(mp2);

        boolean equal = mpInd.EQ(mpInd2).allBoolean();
        if (!equal)
        {
            String strThis = "{ this : [" + m + " x " + p + "], mat : [" + m2 + " x " + p2 + "] }";
            throw new ConditionalException("concatVert : The number of rows and pages must be the same.\n" + strThis);
        }

        int totCols = n + n2;
        Matrix3D horiz = new Matrix3D(m, totCols, p);
        for (int v = 0; v < p; v++)
        {
            Matrix horizPage = this.getPageAt(v).mergeH(mat.getPageAt(v));
            horiz.setPageAt(v, horizPage);
        }

        return horiz;
    }

    private Matrix3D concatDepth(Dimension dim, Matrix3D mat)
    {// page-wise concatenation so, the number of pages and cols must be the
     // same
        if (dim == null)
        {
            throw new ConditionalException("concatVert : Parameter \"dim\" must be \"Dimension.PAGE\" and non-null.");
        }
        else if (dim != Dimension.PAGE)
        {
            throw new ConditionalException("concatVert : Parameter \"dim\" must be \"Dimension.PAGE\".");
        }

        int m = this.array[0].getRowDimension();
        int n = this.array[0].getColumnDimension();
        int p = this.array.length;
        int[] mn =
        {
                m, n
        };
        Indices mnInd = new Indices(mn);

        int m2 = mat.array[0].getRowDimension();
        int n2 = mat.array[0].getColumnDimension();
        int p2 = mat.array.length;
        int[] mn2 =
        {
                m2, n2
        };
        Indices mnInd2 = new Indices(mn2);

        boolean equal = mnInd.EQ(mnInd2).allBoolean();
        if (!equal)
        {
            String strThis = "{ this : [" + m + " x " + n + "], mat : [" + m2 + " x " + n2 + "] }";
            throw new ConditionalException("concatVert : The number of rows and cols must be the same.\n" + strThis);
        }

        int totPages = p + p2;
        Matrix3D depth = new Matrix3D(m, n, totPages);

        Matrix3D thisPage = this.copy();
        Matrix3D matPage = mat.copy();
        int count = 0;

        for (int v = 0; v < totPages; v++)
        {
            Matrix depthPage = null;// this.getPageAt(v).mergeH(mat.getPageAt(v));
            if (v < p)
            {
                depthPage = thisPage.getPageAt(v);
            }
            else
            {
                depthPage = matPage.getPageAt(count++);
            }
            depth.setPageAt(v, depthPage);
        }

        return depth;
    }

    /**
     * Element-by-element right division, C = A./B
     * 
     * @param B
     *            another matrix
     * @return A./B
     */
    public Matrix3D arrayRightDivide(Matrix3D B)
    {
        checkMatrixDimensions(B);
        int m = this.getRowDimension();
        int n = this.getColDimension();
        int p = this.getPageDimension();
        Matrix3D X = new Matrix3D(m, n, p);
        // double[][] C = X.getArray();
        // for (int i = 0; i < m; i++) {
        // for (int j = 0; j < n; j++) {
        // C[i][j] = A[i][j] / B.A[i][j];
        // }
        // }
        for (int i = 0; i < p; i++)
        {
            Matrix Amat = this.array[i];
            Matrix Bmat = B.array[i];
            Matrix Cmat = Amat.arrayRightDivide(Bmat);
            X.setPageAt(i, Cmat);
        }
        return X;
    }

    /**
     * Element-by-element multiplication, C = A.*B
     * 
     * @param B
     *            another matrix
     * @return A.*B
     */
    public Matrix3D arrayTimes(Matrix3D B)
    {
        checkMatrixDimensions(B);
        int m = this.getRowDimension();
        int n = this.getColDimension();
        int p = this.getPageDimension();
        Matrix3D X = new Matrix3D(m, n, p);
        for (int i = 0; i < p; i++)
        {
            Matrix Amat = this.array[i];
            Matrix Bmat = B.array[i];
            Matrix Cmat = Amat.arrayTimes(Bmat);
            X.setPageAt(i, Cmat);
        }
        return X;
    }

    /**
     * Element-by-element multiplication, C = A.*B
     * 
     * @param B
     *            another matrix
     * @return A.*B
     */
    public Matrix3D arrayTimes(Number B)
    {

        int m = this.getRowDimension();
        int n = this.getColDimension();
        int p = this.getPageDimension();
        Matrix3D X = new Matrix3D(m, n, p);
        for (int i = 0; i < p; i++)
        {
            Matrix Amat = this.array[i];
            Matrix Cmat = Amat.arrayTimes(B);// B.array[i];
            // Matrix Cmat = Amat.arrayTimes(Bmat);
            X.setPageAt(i, Cmat);
        }
        return X;
    }

    public Matrix3D cat(Dimension dim, Matrix3D mat)
    {
        if (dim == null)
        {
            throw new ConditionalException("cat : Parameter \"dim\" must be non-null.");
        }

        Matrix3D cat = null;
        if (dim == Dimension.ROW)
        {
            cat = this.concatVert(dim, mat);
        }
        else if (dim == Dimension.COL)
        {
            cat = this.concatHorz(dim, mat);
        }
        else if (dim == Dimension.PAGE)
        {
            cat = this.concatDepth(dim, mat);
        }
        else
        {
            throw new ConditionalException("cat : It shouldn't get to here.");
        }

        return cat;
    }

    public Matrix3D getRowAt(int row)
    {
        return getRows(new int[]
        {
            row
        });
    }

    public Matrix3D getRows(int[] rows)
    {

        if (rows == null || rows.length == 0)
        {
            throw new ConditionalException("getRows : Array integer parameter \"rows\" must be non-null or nom-empty.");
        }

        int numrows = this.array[0].getRowDimension();
        Indices indRows = new Indices(rows);
        indRows = indRows.LT(0).OR(indRows.GTEQ(numrows));
        if (indRows.anyBoolean())
        {
            throw new ConditionalException("getRows : All integer elements in \"rows\" must be in the interval : [0, "
                    + (numrows - 1) + "].");
        }
        int numPages = this.array.length;
        // Matrix page1 = this.array[0];
        int colNums = this.array[0].getColumnDimension();
        Matrix3D rows3D = new Matrix3D(rows.length, colNums, numPages);

        for (int i = 0; i < numPages; i++)
        {
            Matrix pageMat = this.getPageAt(i);
            Matrix rowsMat = pageMat.getRows(rows);
            rows3D.setPageAt(i, rowsMat);
        }

        // for(int i=0)
        return rows3D;
    }

    public Matrix3D getColAt(int col)
    {
        return getCols(new int[]
        {
            col
        });
    }

    public Matrix3D getCols(int[] cols)
    {
        if (cols == null || cols.length == 0)
        {
            throw new ConditionalException("getCols : Array integer parameter \"cols\" must be non-null or nom-empty.");
        }

        int numcols = this.array[0].getColumnDimension();
        Indices indCols = new Indices(cols);
        indCols = indCols.LT(0).OR(indCols.GTEQ(numcols));
        if (indCols.anyBoolean())
        {
            throw new ConditionalException("getCols : All integer elements in \"cols\" must be in the interval : [0, "
                    + (numcols - 1) + "].");
        }
        int numPages = this.array.length;
        // Matrix page1 = this.array[0];
        int rowNums = this.array[0].getRowDimension();
        Matrix3D cols3D = new Matrix3D(rowNums, cols.length, numPages);

        for (int i = 0; i < numPages; i++)
        {
            Matrix pageMat = this.getPageAt(i);
            Matrix colsMat = pageMat.getColumns(cols);
            cols3D.setPageAt(i, colsMat);
        }

        return cols3D;
    }

    public Matrix getPageAt(int pageInd)
    {
        int dim3 = this.array.length;
        if (pageInd >= dim3 || pageInd < 0)
        {
            throw new ConditionalException("getPage : Integer parameter \"pageInd\" (= " + pageInd
                    + ") is out of bound.");
        }
        return this.array[pageInd];
    }

    public Matrix3D getPages(int[] pages)
    {
        if (pages == null || pages.length == 0)
        {
            throw new ConditionalException(
                    "getPages : Array integer parameter \"pages\" must be non-null or nom-empty.");
        }

        int m = this.array[0].getRowDimension();
        int n = this.array[0].getColumnDimension();

        int numPages = this.array.length;

        Indices indPages = new Indices(pages);
        indPages = indPages.LT(0).OR(indPages.GTEQ(numPages));
        if (indPages.anyBoolean())
        {
            throw new ConditionalException(
                    "getPages : All integer elements in \"pages\" must be in the interval : [0, " + (numPages - 1)
                            + "].");
        }

        Matrix3D pages3D = new Matrix3D(m, n, pages.length);
        for (int i = 0; i < pages.length; i++)
        {
            Matrix pageMat = this.array[pages[i]].copy();
            // pages3D.setPageAt(i, pageMat);
            pages3D.array[i] = pageMat;
        }

        return pages3D;
    }

    static void cat1Ex()
    {
        double[][] a =
        {
                {
                        -9, 6
                },
                {
                        -33, -23
                },
                {
                        3, 24
                }
        };
        Matrix a1 = new Matrix(a);

        double[][] b =
        {
                {
                        24, 3
                },
                {
                        -1, -4
                },
                {
                        7, 15
                }
        };
        Matrix b1 = new Matrix(b);

        double[][] c =
        {
                {
                        -12, 2
                },
                {
                        44, 21
                },
                {
                        -3, 1
                }
        };
        Matrix c1 = new Matrix(c);

        Matrix3D abc = new Matrix3D(3, 2, 3);
        abc.setPageAt(0, a1);
        abc.setPageAt(1, b1);
        abc.setPageAt(2, c1);

        abc.print3dArray("abc", 6, 0);

        double[][] d =
        {
                {
                        -2, 6
                },
                {
                        -17, -27
                }
        };
        Matrix d1 = new Matrix(d);

        double[][] e =
        {
                {
                        14, -14
                },
                {
                        32, 17
                }
        };
        Matrix e1 = new Matrix(e);

        double[][] f =
        {
                {
                        25, -29
                },
                {
                        -32, 11
                }
        };
        Matrix f1 = new Matrix(f);

        Matrix3D def = new Matrix3D(2, 2, 3);
        def.setPageAt(0, d1);
        def.setPageAt(1, e1);
        def.setPageAt(2, f1);

        def.print3dArray("def", 6, 0);

        Matrix3D vert = abc.cat(Dimension.ROW, def);
        vert.print3dArray("vert", 6, 0);
    }

    static void cat2Ex()
    {
        double[][] a =
        {
                {
                        -9, 6
                },
                {
                        -33, -23
                },
                {
                        3, 24
                }
        };
        Matrix a1 = new Matrix(a).transpose();

        double[][] b =
        {
                {
                        24, 3
                },
                {
                        -1, -4
                },
                {
                        7, 15
                }
        };
        Matrix b1 = new Matrix(b).transpose();

        double[][] c =
        {
                {
                        -12, 2
                },
                {
                        44, 21
                },
                {
                        -3, 1
                }
        };
        Matrix c1 = new Matrix(c).transpose();

        Matrix3D abc = new Matrix3D(2, 3, 3);
        abc.setPageAt(0, a1);
        abc.setPageAt(1, b1);
        abc.setPageAt(2, c1);

        abc.print3dArray("abc", 6, 0);

        double[][] d =
        {
                {
                        -2, 6
                },
                {
                        -17, -27
                }
        };
        Matrix d1 = new Matrix(d);

        double[][] e =
        {
                {
                        14, -14
                },
                {
                        32, 17
                }
        };
        Matrix e1 = new Matrix(e);

        double[][] f =
        {
                {
                        25, -29
                },
                {
                        -32, 11
                }
        };
        Matrix f1 = new Matrix(f);

        Matrix3D def = new Matrix3D(2, 2, 3);
        def.setPageAt(0, d1);
        def.setPageAt(1, e1);
        def.setPageAt(2, f1);

        def.print3dArray("def", 6, 0);

        Matrix3D horiz = abc.cat(Dimension.COL, def);
        horiz.print3dArray("horiz", 6, 0);
    }

    static void cat3Ex()
    {
        double[][] a =
        {
                {
                        -9, 6
                },
                {
                        -33, -23
                },
                {
                        3, 24
                }
        };
        Matrix a1 = new Matrix(a);

        double[][] b =
        {
                {
                        24, 3
                },
                {
                        -1, -4
                },
                {
                        7, 15
                }
        };
        Matrix b1 = new Matrix(b);

        double[][] c =
        {
                {
                        -12, 2
                },
                {
                        44, 21
                },
                {
                        -3, 1
                }
        };
        Matrix c1 = new Matrix(c);

        Matrix3D abc = new Matrix3D(3, 2, 3);
        abc.setPageAt(0, a1);
        abc.setPageAt(1, b1);
        abc.setPageAt(2, c1);

        abc.print3dArray("abc", 6, 0);

        double[][] d =
        {
                {
                        -8, 14
                },
                {
                        14, 26
                },
                {
                        16, 13
                }
        };
        Matrix d1 = new Matrix(d);

        double[][] e =
        {
                {
                        24, -3
                },
                {
                        -24, -32
                },
                {
                        0, 5
                }
        };
        Matrix e1 = new Matrix(e);

        // double[][] f = {{25, -29},
        // {-32, 11}};
        // Matrix f1 = new Matrix(f);

        Matrix3D de = new Matrix3D(3, 2, 2);
        de.setPageAt(0, d1);
        de.setPageAt(1, e1);
        // def.setPageAt(2, f1);

        de.print3dArray("de", 6, 0);

        Matrix3D depth = abc.cat(Dimension.PAGE, de);
        depth.print3dArray("depth", 6, 0);
    }

    static void getColsEx()
    {
        double[][] a =
        {
                {
                        -9, 6
                },
                {
                        -33, -23
                },
                {
                        3, 24
                }
        };
        Matrix a1 = new Matrix(a);

        double[][] b =
        {
                {
                        24, 3
                },
                {
                        -1, -4
                },
                {
                        7, 15
                }
        };
        Matrix b1 = new Matrix(b);

        double[][] c =
        {
                {
                        -12, 2
                },
                {
                        44, 21
                },
                {
                        -3, 1
                }
        };
        Matrix c1 = new Matrix(c);

        Matrix3D abc = new Matrix3D(3, 2, 3);
        abc.setPageAt(0, a1);
        abc.setPageAt(1, b1);
        abc.setPageAt(2, c1);

        // abc.print3dArray("abc", 6, 0);

        double[][] d =
        {
                {
                        -8, 14
                },
                {
                        14, 26
                },
                {
                        16, 13
                }
        };
        Matrix d1 = new Matrix(d);

        double[][] e =
        {
                {
                        24, -3
                },
                {
                        -24, -32
                },
                {
                        0, 5
                }
        };
        Matrix e1 = new Matrix(e);

        // double[][] f = {{25, -29},
        // {-32, 11}};
        // Matrix f1 = new Matrix(f);

        Matrix3D de = new Matrix3D(3, 2, 2);
        de.setPageAt(0, d1);
        de.setPageAt(1, e1);
        // def.setPageAt(2, f1);

        // de.print3dArray("de", 6, 0);

        Matrix3D depth = abc.cat(Dimension.PAGE, de);
        depth.print3dArray("depth", 6, 0);

        System.out.println("###############################################");

        Matrix3D cols = depth.getCols(new int[]
        {
                0, 1, 0, 0
        });
        cols.print3dArray("cols", 6, 0);
    }

    static void getRowsEx()
    {
        double[][] a =
        {
                {
                        -9, 6
                },
                {
                        -33, -23
                },
                {
                        3, 24
                }
        };
        Matrix a1 = new Matrix(a);

        double[][] b =
        {
                {
                        24, 3
                },
                {
                        -1, -4
                },
                {
                        7, 15
                }
        };
        Matrix b1 = new Matrix(b);

        double[][] c =
        {
                {
                        -12, 2
                },
                {
                        44, 21
                },
                {
                        -3, 1
                }
        };
        Matrix c1 = new Matrix(c);

        Matrix3D abc = new Matrix3D(3, 2, 3);
        abc.setPageAt(0, a1);
        abc.setPageAt(1, b1);
        abc.setPageAt(2, c1);

        // abc.print3dArray("abc", 6, 0);

        double[][] d =
        {
                {
                        -8, 14
                },
                {
                        14, 26
                },
                {
                        16, 13
                }
        };
        Matrix d1 = new Matrix(d);

        double[][] e =
        {
                {
                        24, -3
                },
                {
                        -24, -32
                },
                {
                        0, 5
                }
        };
        Matrix e1 = new Matrix(e);

        // double[][] f = {{25, -29},
        // {-32, 11}};
        // Matrix f1 = new Matrix(f);

        Matrix3D de = new Matrix3D(3, 2, 2);
        de.setPageAt(0, d1);
        de.setPageAt(1, e1);
        // def.setPageAt(2, f1);

        // de.print3dArray("de", 6, 0);

        Matrix3D depth = abc.cat(Dimension.PAGE, de);
        depth.print3dArray("depth", 6, 0);

        System.out.println("###############################################");

        // Matrix3D cols = depth.getCols(new int[]{0, 1, 0, 0});
        // cols.print3dArray("cols", 6, 0);
        Matrix3D rows = depth.getRows(new int[]
        {
                0, 1, 0, 1
        });
        rows.print3dArray("rows", 6, 0);
    }

    static void getPagesEx()
    {
        double[][] a =
        {
                {
                        -9, 6
                },
                {
                        -33, -23
                },
                {
                        3, 24
                }
        };
        Matrix a1 = new Matrix(a);

        double[][] b =
        {
                {
                        24, 3
                },
                {
                        -1, -4
                },
                {
                        7, 15
                }
        };
        Matrix b1 = new Matrix(b);

        double[][] c =
        {
                {
                        -12, 2
                },
                {
                        44, 21
                },
                {
                        -3, 1
                }
        };
        Matrix c1 = new Matrix(c);

        Matrix3D abc = new Matrix3D(3, 2, 3);
        abc.setPageAt(0, a1);
        abc.setPageAt(1, b1);
        abc.setPageAt(2, c1);

        // abc.print3dArray("abc", 6, 0);

        double[][] d =
        {
                {
                        -8, 14
                },
                {
                        14, 26
                },
                {
                        16, 13
                }
        };
        Matrix d1 = new Matrix(d);

        double[][] e =
        {
                {
                        24, -3
                },
                {
                        -24, -32
                },
                {
                        0, 5
                }
        };
        Matrix e1 = new Matrix(e);

        // double[][] f = {{25, -29},
        // {-32, 11}};
        // Matrix f1 = new Matrix(f);

        Matrix3D de = new Matrix3D(3, 2, 2);
        de.setPageAt(0, d1);
        de.setPageAt(1, e1);
        // def.setPageAt(2, f1);

        // de.print3dArray("de", 6, 0);

        Matrix3D depth = abc.cat(Dimension.PAGE, de);
        depth.print3dArray("depth", 6, 0);

        System.out.println("###############################################");

        // Matrix3D cols = depth.getCols(new int[]{0, 1, 0, 0});
        // cols.print3dArray("cols", 6, 0);
        Matrix3D pages = depth.getPages(new int[]
        {
                2, 1, 0, 1
        });
        pages.print3dArray("pages", 6, 0);
    }

    static void setColEx()
    {
        double[][] a =
        {
                {
                        -9, 6
                },
                {
                        -33, -23
                },
                {
                        3, 24
                }
        };
        Matrix a1 = new Matrix(a);

        double[][] b =
        {
                {
                        24, 3
                },
                {
                        -1, -4
                },
                {
                        7, 15
                }
        };
        Matrix b1 = new Matrix(b);

        double[][] c =
        {
                {
                        -12, 2
                },
                {
                        44, 21
                },
                {
                        -3, 1
                }
        };
        Matrix c1 = new Matrix(c);

        Matrix3D abc = new Matrix3D(3, 2, 3);
        abc.setPageAt(0, a1);
        abc.setPageAt(1, b1);
        abc.setPageAt(2, c1);

        // abc.print3dArray("abc", 6, 0);

        double[][] d =
        {
                {
                        -8, 14
                },
                {
                        14, 26
                },
                {
                        16, 13
                }
        };
        Matrix d1 = new Matrix(d);

        double[][] e =
        {
                {
                        24, -3
                },
                {
                        -24, -32
                },
                {
                        0, 5
                }
        };
        Matrix e1 = new Matrix(e);

        // double[][] f = {{25, -29},
        // {-32, 11}};
        // Matrix f1 = new Matrix(f);

        Matrix3D de = new Matrix3D(3, 2, 2);
        de.setPageAt(0, d1);
        de.setPageAt(1, e1);
        // def.setPageAt(2, f1);

        // de.print3dArray("de", 6, 0);

        Matrix3D depth = abc.cat(Dimension.PAGE, de);
        depth.print3dArray("depth 1", 6, 0);

        System.out.println("###############################################");

        // Matrix3D cols = depth.getCols(new int[]{0, 1, 0, 0});
        // cols.print3dArray("cols", 6, 0);
        // Matrix3D pages = depth.getPages(new int[]{2, 1, 0, 1});
        // pages.print3dArray("pages", 6, 0);
        double[][] vArr =
        {
                {
                        326, 365, 111, 386, 383
                },
                {
                        362, 253, 219, 63, 194
                },
                {
                        51, 39, 383, 388, 320
                }
        };

        Matrix V = new Matrix(vArr);
        V.printInLabel("V", 0);

        System.out.println("###############################################");

        depth.setColAt(1, V);
        depth.print3dArray("depth 2", 6, 0);
    }

    static void setRowEx()
    {
        double[][] a =
        {
                {
                        -9, 6
                },
                {
                        -33, -23
                },
                {
                        3, 24
                }
        };
        Matrix a1 = new Matrix(a);

        double[][] b =
        {
                {
                        24, 3
                },
                {
                        -1, -4
                },
                {
                        7, 15
                }
        };
        Matrix b1 = new Matrix(b);

        double[][] c =
        {
                {
                        -12, 2
                },
                {
                        44, 21
                },
                {
                        -3, 1
                }
        };
        Matrix c1 = new Matrix(c);

        Matrix3D abc = new Matrix3D(3, 2, 3);
        abc.setPageAt(0, a1);
        abc.setPageAt(1, b1);
        abc.setPageAt(2, c1);

        // abc.print3dArray("abc", 6, 0);

        double[][] d =
        {
                {
                        -8, 14
                },
                {
                        14, 26
                },
                {
                        16, 13
                }
        };
        Matrix d1 = new Matrix(d);

        double[][] e =
        {
                {
                        24, -3
                },
                {
                        -24, -32
                },
                {
                        0, 5
                }
        };
        Matrix e1 = new Matrix(e);

        // double[][] f = {{25, -29},
        // {-32, 11}};
        // Matrix f1 = new Matrix(f);

        Matrix3D de = new Matrix3D(3, 2, 2);
        de.setPageAt(0, d1);
        de.setPageAt(1, e1);
        // def.setPageAt(2, f1);

        // de.print3dArray("de", 6, 0);

        Matrix3D depth = abc.cat(Dimension.PAGE, de);
        depth.print3dArray("depth 1", 6, 0);

        System.out.println("###############################################");

        // Matrix3D cols = depth.getCols(new int[]{0, 1, 0, 0});
        // cols.print3dArray("cols", 6, 0);
        // Matrix3D pages = depth.getPages(new int[]{2, 1, 0, 1});
        // pages.print3dArray("pages", 6, 0);
        double[][] wArr =
        {
                {
                        71, 458, 480, 18, 467
                },
                {
                        211, 396, 328, 425, 339
                }
        };

        Matrix W = new Matrix(wArr);
        W.printInLabel("W", 0);

        System.out.println("###############################################");

        depth.setRowAt(1, W);
        depth.print3dArray("depth 2", 6, 0);
    }

    static void modulusEx()
    {
        double[][] a =
        {
                {
                        9, 6
                },
                {
                        33, 23
                },
                {
                        3, Double.NaN
                }
        };
        Matrix a1 = new Matrix(a);

        double[][] b =
        {
                {
                        24, Double.NaN
                },
                {
                        1, 4
                },
                {
                        Double.NaN, 15
                }
        };
        Matrix b1 = new Matrix(b);

        double[][] c =
        {
                {
                        12, 2
                },
                {
                        Double.NaN, 21
                },
                {
                        3, Double.NaN
                }
        };
        Matrix c1 = new Matrix(c);

        Matrix3D abc = new Matrix3D(3, 2, 3);
        abc.setPageAt(0, a1);
        abc.setPageAt(1, b1);
        abc.setPageAt(2, c1);

        abc.print3dArray("abc 1", 8, 0);

        Indices3D ind3 = abc.isnan();
        FindInd3 find3 = ind3.findIJK();
        int[] arr = find3.getIndex();
        find3.getIndexInd().printInLabel("find3Ind");

        abc.setEls(arr, -5.0);
        abc.print3dArray("abc 2", 8, 0);

    }

    public static void main(String[] arg)
    {
        modulusEx();
        // setRowEx();
        // setColEx();
        // getPagesEx();
        // getRowsEx();
        // getColsEx();
        // cat3Ex();
        // repmatEx();
        // squeezeEx();
        // reshapeEx2D();
    }
}
