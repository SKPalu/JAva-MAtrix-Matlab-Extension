/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Feynman Perceptrons
 */
public class Cell implements java.io.Serializable, java.lang.Cloneable
{

    /**
     * Row AND column dimensions.
     * 
     * @serial row dimension.
     * @serial column dimension.
     **/
    private int m, n;
    private Object[][] A;

    public Cell()
    {
        A = null;
        m = 0;
        n = 0;

    }

    public Cell(int[] rc)
    {
        if (rc == null || rc.length == 0)
        {
            A = null;
            m = 0;
            n = 0;
        }
        else
        {
            if (rc.length != 2)
            {
                throw new IllegalArgumentException("Input argument integer array length must be 2 elements.");
            }

            int r = rc[0];
            int c = rc[1];
            if (r < 1 || c < 1)
            {
                throw new IllegalArgumentException("Cell : Parameter \"r\" and \"c\" must be positive numbers.");
            }
            A = new Object[r][c];
            m = r;
            n = c;
        }
    }

    public Cell(int r, int c)
    {
        if (r < 1 || c < 1)
        {
            throw new IllegalArgumentException("Cell : Parameter \"r\" and \"c\" must be positive numbers.");
        }
        A = new Object[r][c];
        m = r;
        n = c;
    }

    public Cell(Object[] x)
    {
        A = new Object[1][];
        A[0] = x;
        m = 1;
        n = x.length;
    }

    public Cell(Object[][] matArray)
    {
        if (matArray == null)
        {
            throw new IllegalArgumentException("Cell : Parameter \"matArray\" must be non-null.");
        }
        m = matArray.length;
        n = matArray[0].length;
        for (int i = 0; i < m; i++)
        {
            if (matArray[i].length != n)
            {
                throw new IllegalArgumentException("Cell : All cell rows must have the same length.");
            }
        }
        this.A = matArray;
    }

    public Cell(int m, int n, Object s)
    {
        if (m < 1 || n < 1)
        {
            throw new IllegalArgumentException("Cell : Parameters \"m\" and \"n\" must be at least one.");
        }
        this.m = m;
        this.n = n;
        A = new Object[m][n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                A[i][j] = s;
            }
        }
    }

    public Cell(Object[][] A, int m, int n)
    {
        this.A = A;
        this.m = m;
        this.n = n;
    }

    public Object get(int i, int j)
    {
        return A[i][j];
    }

    public void set(int i, int j, Object s)
    {
        A[i][j] = s;
    }

    public void set(Object[][] objArr)
    {
        if (objArr == null)
        {
            this.m = 0;
            this.n = 0;
        }
        else
        {
            this.m = objArr.length;
            this.n = objArr[0].length;
        }
        this.A = objArr;
    }

    public Cell interleaveCol(Cell B)
    {
        if (B.getRowDimension() != m)
        {
            throw new ConditionalRuleException("Inconsistent Row Numbers.");
        }
        if (B.getColumnDimension() != n)
        {
            throw new ConditionalRuleException("Inconsistent Column Numbers.");
        }

        // int bn = B.getColumnDimension();
        Cell iL = new Cell(m, 2 * n);

        for (int j = 0; j < n; j++)
        {
            int ff = 2 * j;
            iL.setColumnAt(ff, this.getColumnAt(j));
            int ff2 = ff + 1;
            iL.setColumnAt(ff2, B.getColumnAt(j));
        }

        return iL;
    }

    public Cell interleaveRow(Cell B)
    {
        if (B.getRowDimension() != m)
        {
            throw new ConditionalRuleException("Inconsistent Row Numbers.");
        }
        if (B.getColumnDimension() != n)
        {
            throw new ConditionalRuleException("Inconsistent Column Numbers.");
        }

        // int bn = B.getColumnDimension();
        Cell iL = new Cell(2 * m, n);

        for (int i = 0; i < m; i++)
        {
            int ff = 2 * i;
            iL.setRowAt(ff, this.getRowAt(i));
            int ff2 = ff + 1;
            iL.setRowAt(ff2, B.getRowAt(i));
        }

        return iL;
    }

    public Object start()
    {
        return A[0][0];
    }

    public Object end()
    {
        return A[m - 1][n - 1];
    }

    /**
     * 
     * @param index
     *            int
     * @param matColumns
     *            Matrix
     */
    public void setColumnAt(int index, Cell matColumns)
    {
        if (matColumns.numel() == 1)
        {
            setColumns(new int[]
            {
                    index
            }, matColumns.start());
        }
        else
        {
            setColumns(new int[]
            {
                    index
            }, matColumns);
        }
    }

    /**
     * 
     * @param index
     * @param valColumns
     */
    public void setColumnAt(int index, Object valColumns)
    {
        setColumns(new int[]
        {
                index
        }, valColumns);
    }

    /**
     * 
     * @param indices
     * @param valColumns
     */
    public void setColumns(int[] indices, Object valColumns)
    {
        int len = indices.length;
        Cell matColumns = new Cell(m, len, valColumns);
        setColumns(indices, matColumns);
    }

    /**
     * 
     * @param indices
     *            int[]
     * @param matColumns
     *            Matrix
     */
    public void setColumns(int[] indices, Cell matColumns)
    {
        if (matColumns.getRowDimension() != m)
        {
            throw new IllegalArgumentException(" setColumns: Inconsistent size of columns.");
        }
        int len = indices.length;
        if (matColumns.getColumnDimension() != len)
        {
            throw new IllegalArgumentException(" setColumns: Inconsistent size of 'indices' and 'Columns'.");
        }
        for (int i = 0; i < len; i++)
        {
            if (indices[i] > (n - 1) || indices[i] < 0)
            {
                throw new IllegalArgumentException(" setColumns: Array out-of-bounds.");
            }
            Cell temp = matColumns.getColumnAt(i);
            setMatrix(0, m - 1, indices[i], indices[i], temp);
        }
    }

    /**
     * 
     * @param indices
     *            int[]
     * @param matCols
     *            Matrix
     */
    public void setRows(int[] indices, Cell matRows)
    {
        if (matRows.getColumnDimension() != n)
        {
            throw new IllegalArgumentException(" setRows: Inconsistent size of rows.");
        }
        int len = indices.length;
        if (matRows.getRowDimension() != len)
        {
            throw new IllegalArgumentException(" setRows: Inconsistent size of 'indices' and 'matRows'.");
        }
        for (int i = 0; i < len; i++)
        {
            if (indices[i] > (m - 1) || indices[i] < 0)
            {
                throw new IllegalArgumentException(" setRows: Array out-of-bounds.");
            }
            Cell temp = matRows.getRowAt(i);
            setMatrix(indices[i], indices[i], 0, n - 1, temp);
        }
    }

    /**
     * 
     * @param indices
     * @param valRows
     */
    public void setRows(int[] indices, Object valRows)
    {
        int len = indices.length;
        if (valRows == null)
        {
            return;
        }
        Cell matRows = null;

        if (valRows instanceof Matrix)
        {
            matRows = ((Matrix) valRows).toCellString();
        }
        else if (valRows instanceof Indices)
        {
            matRows = ((Indices) valRows).toCellString();
        }
        else
        {
            matRows = new Cell(len, n, valRows);
        }

        // Cell matRows = new Cell(len, n, valRows);
        setRows(indices, matRows);
    }

    /**
     * 
     * @param index
     *            int
     * @param matRows
     */
    public void setRowAt(int index, Cell matRows)
    {
        if (matRows.numel() == 1)
        {
            setRows(new int[]
            {
                    index
            }, matRows.start());
        }
        else
        {
            setRows(new int[]
            {
                    index
            }, matRows);
        }
    }

    /**
     * 
     * @param index
     * @param valRows
     */
    public void setRowAt(int index, Object valRows)
    {
        setRows(new int[]
        {
                index
        }, valRows);
    }

    public Cell flipLR()
    {
        Cell X = new Cell(m, n);
        Object[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            // C[i] = MathUtil.flip(A[i]);
            Object[] Ar = A[i];
            Object[] CC = new Object[n];
            // System.arraycopy(, 0, CC, 0, n);
            for (int j = 0; j < n; j++)
            {
                CC[j] = Ar[n - 1 - j];
            }
            Object[] CC2 = new Object[n];

            System.arraycopy(CC, 0, CC2, 0, n);

            C[i] = CC2;
        }
        return X;
    }

    /**
     * Flip this matrix from Top to Bottom OR Upside-Down, eg: ( 5, 10, 15 ) If
     * A = | 4, 9, 14 | , ( 3, 8, 13 )
     * 
     * flipUD(); ( 3, 8, 13 ) A = | 4, 9, 14 | , matrix A has been flipped
     * upside down. ( 5, 10, 15 )
     * 
     * 
     * @return flipped Matrix
     */
    public Cell flipUD()
    {
        Cell X = new Cell(m, n);
        Object[][] C = X.getArray();
        Object[][] temp = this.getArrayCopy();
        Object[] D = null;
        for (int i = 0; i < m; i++)
        {
            D = temp[(m - 1) - i];
            for (int j = 0; j < n; j++)
            {
                C[i][j] = D[j];
            } // end for

        } // end for

        return X;
    }

    public int getRowDimension()
    {
        return m;
    }

    /**
     * Get column dimension.
     * 
     * @return lenC, the number of columns.
     */
    public int getColumnDimension()
    {
        return n;
    }

    /**
     * Tile this matrix into an m-rows by lenC-columns pattern
     * 
     * @param r
     *            number of i patterns
     * @param c
     *            number of column patterns. return Matrix from tiling A[][]
     *            into r-rows AND c-columns tile pattern.
     * @return
     */
    public Cell repmat(int r, int c)
    {
        if (r == 1 && c == 1)
        {
            return copy();
        }
        Object[][] X = A;// getArrayCopy();
        int countRow = 0, countColumn = 0;
        // double[] tempHolder;
        if (r < 1 || c < 1)
        {
            throw new ArrayIndexOutOfBoundsException("repmat : Array index is out-of-bound.");
        }
        int newRowDim = m * r;
        int newColDim = n * c;
        Object[][] result = new Object[newRowDim][];
        Object[] tempHolder = new Object[newColDim];

        for (int i = 0; i < newRowDim; i++)
        {
            for (int j = 0; j < newColDim; j++)
            {
                tempHolder[j] = X[countRow][countColumn++];
                // reset the column-index to zero to avoid reference to
                // out-of-bound index in a[][]
                if (countColumn == n)
                {
                    countColumn = 0;
                }
            } // end for

            countRow++;
            // reset the i-index to zero to avoid reference to out-of-bound
            // index in a[][]
            if (countRow == m)
            {
                countRow = 0;
            }

            result[i] = tempHolder;
            // reassign the tempHolder to a new array
            tempHolder = new Object[newColDim];
        } // end for

        return new Cell(result);
    }// end method

    public List<Object[]> toObjArray()
    {
        Cell trainAnTestMat = this;
        int siz = trainAnTestMat.getRowDimension();
        Object[][] objArr = trainAnTestMat.getArray();
        List<Object[]> objList = new ArrayList<Object[]>();
        for (int i = 0; i < siz; i++)
        {
            Object[] AA = objArr[i];
            objList.add(AA);
        }
        return objList;
    }

    public Cell convertNullToEmptyString()
    {
        Cell cobj = this;
        // int m = cobj.getRowDimension();
        // int n = cobj.getColumnDimension();
        Cell done = new Cell(m, n);

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Object obj = cobj.get(i, j);
                if (obj == null)
                {
                    done.set(i, j, "");
                }
                else
                {
                    done.set(i, j, obj);
                }
            }
        }

        return done;
    }

    /**
     * 
     * @return
     */
    public Cell copy()
    {
        if (A == null)
        {
            return new Cell(null, m, n);
        }
        Cell ind = new Cell(m, n);

        Object[][] X = ind.getArray();
        for (int i = 0; i < m; i++)
        {
            // for (int i = 0; i < n; i++) {
            // X[i][i] = A[i][i];
            // }
            Object[] Xi = X[i];
            Object[] Ai = A[i];
            System.arraycopy(Ai, 0, Xi, 0, n);
        }
        return ind;
    }

    public Cell blkdiag(Cell... varargin)
    {
        if (varargin == null || varargin.length == 0)
        {
            return this.copy();
        }

        int len = varargin.length;

        int row = 0;
        int col = 0;
        for (int i = 0; i < len; i++)
        {
            if (varargin[i] != null)
            {

                Cell ind = varargin[i];
                if (!ind.isNull())
                {
                    row += ind.getRowDimension();
                    col += ind.getColumnDimension();
                }

            }
        }

        if ((row + m) == 0 || (col + n) == 0)
        {
            return new Cell(null, row + m, col + n);
        }

        // collect all matrices to be blockwise concatenated
        ArrayList<Cell> list = new ArrayList<Cell>();
        if (!this.isNull())
        {
            list.add(this.copy());
        }

        for (int i = 0; i < len; i++)
        {
            if (varargin[i] != null)
            {
                Cell ind = varargin[i];
                if (!ind.isNull())
                {
                    list.add(ind);
                }
            }
        }

        Cell concat = new Cell(row + m, col + n);

        int rowStart = 0;
        int rowEnd = 0;

        int colStart = 0;
        int colEnd = 0;

        int siz = list.size();
        for (int i = 0; i < siz; i++)
        {

            Cell mat = list.get(i);
            if (i == 0)
            {
                rowStart = 0;
                rowEnd = mat.getRowDimension() - 1;
                colStart = 0;
                colEnd = mat.getColumnDimension() - 1;
            }
            else
            {
                rowStart = rowEnd + 1;
                rowEnd += mat.getRowDimension();
                colStart = colEnd + 1;
                colEnd += mat.getColumnDimension();
            }
            concat.setMatrix(rowStart, rowEnd, colStart, colEnd, mat);
        } // end for

        return concat;
    }

    public Cell horzcat(Cell... B)
    {
        if (B == (Cell[]) null || B.length == 0)
        {
            return copy();
        }
        int lenB = B.length;
        int numR = 0;
        int numC = 0;
        ArrayList<Cell> objList = new ArrayList<Cell>();
        if (!this.isNull())
        {
            objList.add(this.copy());
            // numR = m;
            // numC = n;
        }

        for (int i = 0; i < lenB; i++)
        {
            if (B[i] != null)
            {

                boolean cond = B[i].isNull();
                // numR += B[i].getRowDimension();
                // numC += B[i].getColumnDimension();

                if (!cond)
                {
                    objList.add(B[i]);
                }
            }
        }

        // if (objList.isEmpty()) {
        // return new Cell(null, numR, numC);
        // }

        if (objList.isEmpty())
        {
            return new Cell();
        }
        else if (objList.size() == 1)
        {
            Cell OB = objList.get(0);
            return OB.copy();
        }

        // Check all rows are the same
        numR = 0;
        int currentNumR = 0;
        for (int i = 0; i < objList.size(); i++)
        {
            if (i == 0)
            {
                numR = objList.get(0).getRowDimension();
            }
            else
            {
                currentNumR = objList.get(i).getRowDimension();
                if (numR != currentNumR)
                {
                    throw new ConditionalRuleException("horzcat", "All cells must have the same rows.");
                }
            }
        }

        numR = 0;
        numC = 0;

        Cell[] list = new Cell[objList.size()];
        for (int i = 0; i < objList.size(); i++)
        {
            list[i] = objList.get(i);
            numR += list[i].getRowDimension();
            numC += list[i].getColumnDimension();
        }

        Cell cat = new Cell(currentNumR, numC);
        int colSt = 0;
        int colEn = 0;
        int totRow = 0;
        for (int i = 0; i < list.length; i++)
        {
            Cell TC = objList.get(i);
            Object[][] Atc = TC.getArray();
            if (i == 0)
            {
                totRow = TC.getRowDimension();
                colEn = TC.getColumnDimension() - 1;
            }
            else
            {
                colSt += colEn + 1;
                colEn += TC.getColumnDimension() - 1;
            }
            cat.setCell(0, totRow - 1, colSt, colEn, Atc);
        }

        return cat;
    }

    public Cell vertcat(Cell... B)
    {
        if (B == (Cell[]) null || B.length == 0)
        {
            return copy();
        }
        int lenB = B.length;
        int numR = 0;
        int numC = 0;
        ArrayList<Cell> objList = new ArrayList<Cell>();
        if (!this.isNull())
        {
            objList.add(this.copy());
            // numR = m;
            // numC = n;
        }

        for (int i = 0; i < lenB; i++)
        {
            if (B[i] != null)
            {

                boolean cond = B[i].isNull();
                // numR += B[i].getRowDimension();
                // numC += B[i].getColumnDimension();

                if (!cond)
                {
                    objList.add(B[i]);
                }
            }
        }

        // if (objList.isEmpty()) {
        // return new Cell(null, numR, numC);
        // }

        if (objList.isEmpty())
        {
            return new Cell();
        }
        else if (objList.size() == 1)
        {
            Cell OB = objList.get(0);
            return OB.copy();
        }

        // Check all columns are the same
        numR = 0;
        int currentNumC = 0;
        for (int i = 0; i < objList.size(); i++)
        {
            if (i == 0)
            {
                numR = objList.get(0).getColumnDimension();
            }
            else
            {
                currentNumC = objList.get(i).getColumnDimension();
                if (numR != currentNumC)
                {
                    throw new ConditionalRuleException("vertcat", "All cells must have the same columns.");
                }
            }
        }

        numR = 0;
        numC = 0;

        Cell[] list = new Cell[objList.size()];
        for (int i = 0; i < objList.size(); i++)
        {
            list[i] = objList.get(i);
            numR += list[i].getRowDimension();
        }

        Cell cat = new Cell(numR, currentNumC);
        int rowSt = 0;
        int rowEn = 0;
        int totCol = 0;
        for (int i = 0; i < list.length; i++)
        {
            Cell TC = objList.get(i);
            Object[][] Atc = TC.getArray();
            if (i == 0)
            {
                totCol = TC.getColumnDimension();
                rowEn = TC.getRowDimension() - 1;
            }
            else
            {
                rowSt += rowEn + 1;
                rowEn += TC.getRowDimension() - 1;
            }
            cat.setCell(rowSt, rowEn, 0, totCol - 1, Atc);
        }

        return cat;
    }

    /**
     * 
     * @return
     */
    public Cell toRowVector()
    {
        Object[] val = getRowPackedCopy();
        Object[][] R = new Object[1][];
        R[0] = val;
        Cell temp = new Cell(R);
        return temp;
    }

    public boolean isNull()
    {
        return this.A == null;
    }

    /**
     * 
     * @return
     */
    public Cell toColVector()
    {
        Object[] val = getColumnPackedCopy();
        Object[][] R = new Object[val.length][1];
        for (int j = 0; j < val.length; j++)
        {
            R[j][0] = val[j];
        }
        Cell temp = new Cell(R);

        return temp;
    }

    /**
     * Make a one-dimensional column packed copy of the internal array.
     * 
     * @return Matrix elements packed in a one-dimensional array by columns.
     */
    public Object[] getColumnPackedCopy()
    {
        Object[] vals = new Object[m * n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] instanceof Matrix)
                {
                    vals[i + j * m] = ((Matrix) A[i][j]).copy();
                }
                else
                {
                    vals[i + j * m] = A[i][j];
                }
            }
        }
        return vals;
    }

    /**
     * Make a one-dimensional row packed copy of the internal array.
     * 
     * @return Matrix elements packed in a one-dimensional array by rows.
     */
    public Object[] getRowPackedCopy()
    {
        Object[] vals = new Object[m * n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] instanceof Matrix)
                {
                    vals[i * n + j] = ((Matrix) A[i][j]).copy();
                }
                else
                {
                    vals[i * n + j] = A[i][j];
                }
            }
        }
        return vals;
    }

    public Object[][] getArray()
    {
        return A;
    }

    public Object[][] getArrayCopy()
    {
        Object[][] arCopy = new Object[m][n];
        // System.arraycopy(CC, 0, CC2, 0, n);
        for (int i = 0; i < m; i++)
        {
            Object[] AC = new Object[n];
            System.arraycopy(A[i], 0, AC, 0, n);
            arCopy[i] = AC;
        }
        return arCopy;
    }

    public int length()
    {
        if (A == null)
        {
            return 0;
        }
        return m > n ? m : n;
    }

    public int numel()
    {
        return m * n;
    }

    public Cell getCell(int i, int j)
    {
        Cell R = new Cell(1, 1);
        if (A[i][j] != null)
        {
            R.set(0, 0, A[i][j]);
        }
        return R;
    }

    public Cell getCellAt(int ind)
    {
        Object val = null;
        int count = 0;
        int i = ind;
        if (ind >= m * n)
        {
            throw new IllegalArgumentException("getCellAt : ArrayIndex is out-of-bound.");
        }
        if (i < m)
        {
            val = A[ind][0];
        }
        else
        {
            while (m <= i)
            {
                i = i - m;
                count++;
            }
            val = A[i][count];
        }

        Cell cellAt = new Cell(1, 1, val);

        return cellAt;
    }// end method.

    public Cell getElements(int[] ind)
    {
        int len = ind.length;
        Cell R = new Cell(len, 1);
        // double val = 0.0;
        for (int i = 0; i < len; i++)
        {
            Object val = this.getElementAt(ind[i]);
            R.set(i, 0, val);
        }
        return R;
    }

    public Cell getElements(int from, int to)
    {

        int[] ind = Indices.linspace(from, to).getRowPackedCopy();
        int len = ind.length;
        Cell R = new Cell(len, 1);
        // double val = 0.0;
        for (int i = 0; i < len; i++)
        {
            Object val = this.getElementAt(ind[i]);
            R.set(i, 0, val);
        }
        return R;
    }

    public Object getElementAt(int ind)
    {
        Object val = null;
        int count = 0;
        int i = ind;
        if (ind >= m * n)
        {
            throw new IllegalArgumentException("getElementAt : ArrayIndex is out-of-bound.");
        }
        if (i < m)
        {
            val = A[ind][0];
        }
        else
        {
            while (m <= i)
            {
                i = i - m;
                count++;
            }
            val = A[i][count];
        }

        return val;
    }// end method.

    public void setOldToNew(Object oldObj, Object newObj)
    {
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                // this.A[i][j] = val;
                if (this.A[i][j].equals(oldObj))
                {
                    this.A[i][j] = newObj;
                }
            }
        }
    }

    public List<String> toStringListByRow()
    {
        List<String> strList = new ArrayList<String>();
        for (int i = 0; i < m; i++)
        {
            Cell rowI = this.getRowAt(i);
            List<String> rowStr = rowI.toStringList();
            String rowStrConcat = fromListToString(rowStr, false);
            strList.add(rowStrConcat);
        }
        return strList;
    }

    public List<String> toStringListByColumn()
    {
        List<String> strList = new ArrayList<String>();
        for (int j = 0; j < n; j++)
        {
            Cell colI = this.getColumnAt(j);
            List<String> colStr = colI.toStringList();
            String colStrConcat = fromListToString(colStr, false);
            strList.add(colStrConcat);
        }
        return strList;
    }

    public static String fromListToString(List<String> listStr)
    {
        return fromListToString(listStr, true);

    }

    public static String fromListToString(List<String> listStr, boolean newLine)
    {
        StringBuilder builder = new StringBuilder();
        int num = listStr.size();
        String delim = " ";
        if (newLine)
        {
            delim = "\r\n";
        }

        if (listStr == null || listStr.isEmpty())
        {
            return "";
        }

        if (num == 1)
        {
            return listStr.get(0);
        }

        for (int i = 0; i < num; i++)
        {
            String str = listStr.get(i);
            // System.out.println((i + 1) + ") \"" + str + "\"");
            if (str == null)
            {
                continue;
            }
            str = str.trim();
            if ("".equals(str))
            {
                continue;
            }

            if (i != (num - 1))
            {
                builder = builder.append(str + delim);
            }
            else
            {
                builder = builder.append(str);
            }
        }

        return builder.toString();
    }

    public static String fromListToString(List<String> listStr, String sep)
    {
        StringBuilder builder = new StringBuilder();
        int num = listStr.size();
        String delim = null;
        if (sep == null)
        {
            delim = "\r\n";
        }
        else
        {
            delim = sep;
        }

        if (listStr == null || listStr.isEmpty())
        {
            return "";
        }

        if (num == 1)
        {
            return listStr.get(0);
        }

        for (int i = 0; i < num; i++)
        {
            String str = listStr.get(i);
            // System.out.println((i + 1) + ") \"" + str + "\"");
            if (str == null)
            {
                continue;
            }
            str = str.trim();
            if ("".equals(str))
            {
                continue;
            }

            if (i != (num - 1))
            {
                builder = builder.append(str + delim);
            }
            else
            {
                builder = builder.append(str);
            }
        }

        return builder.toString();
    }

    public int numNull()
    {
        int count = 0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] == null)
                {
                    count++;
                }
            }
        }
        return count;
    }

    public Indices isnull()
    {
        Indices nullInd = new Indices(m, n, 0);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] == null)
                {
                    nullInd.set(i, j, 1);

                }
            }
        }
        return nullInd;
    }

    public boolean hasNull()
    {
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] == null)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasMatrix()
    {
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if ((A[i][j] != null) && (A[i][j] instanceof Matrix))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public Indices ismatrix()
    {
        Indices nullInd = new Indices(m, n, 0);
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if ((A[i][j] != null) && (A[i][j] instanceof Matrix))
                {
                    nullInd.set(i, j, 1);
                }
            }
        }
        return nullInd;
    }

    public boolean isVector()
    {
        return (isRowVector() || isColVector());
    }

    /**
     * Test if this matrix is a row vector, eg: If A = (6,8,9,-1) , a 1 x 4
     * matix, A.isRowVector() returns true;
     * 
     * @return True if this matrix is a row vector OR otherwise false.
     */
    public boolean isRowVector()
    {
        boolean r = false;
        if (m == 1)
        {
            r = true;
        }
        return r;
    }

    /**
     * Test if this matrix is a column vector , eg: ( 3 ) If matrix A = | 4 | ,
     * a 3 x 1 matrix is returned. ( 5 ) Matrix A is a column vector matrix,
     * which A.isColumnVector() returns true.
     * 
     * @return True if this matrix is a column vector OR otherwise false.
     */
    public boolean isColVector()
    {
        boolean r = false;
        if (n == 1)
        {
            r = true;
        }
        return r;
    }

    public boolean isAllString()
    {
        boolean allString = true;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Object obj = this.get(i, j);
                if (!(obj instanceof String))
                {
                    return false;
                }
            }
        }
        return allString;
    }

    /**
     * 
     * @param ind
     * @param val
     */
    public void setElementAt(int ind, Object val)
    {
        int count = 0;
        int i = ind;
        if (ind >= m * n)
        {
            throw new IllegalArgumentException("setElementAt: ArrayIndex is out-of-bound.");
        }
        if (i < m)
        {
            A[ind][0] = val;
        }
        else
        {
            while (m <= i)
            {
                i -= m;
                count++;
            }
            A[i][count] = val;
        }
    }// end method

    public static Object[] deal(int nargout, Object... varargin)
    {
        if (varargin == null || varargin.length == 0)
        {
            throw new ConditionalException(
                    "deal : Array Object parameter \"varargin\" must not be non-null or zero-length.");
        }
        if (nargout < 1)
        {
            throw new ConditionalException(
                    "deal : The output array length parameter \"nargout\" (= " + nargout + ") must be at least one.");
        }

        int nargin = varargin.length;
        Object[] varargout = new Object[nargout];

        if (nargin == 1)
        {
            Object obj = varargin[0];
            for (int i = 0; i < nargout; i++)
            {
                Object retObj = null;
                if (obj instanceof Matrix)
                {
                    retObj = ((Matrix) obj).copy();
                }
                else if (obj instanceof Matrix3D)
                {
                    retObj = ((Matrix3D) obj).copy();
                }
                else if (obj instanceof Indices)
                {
                    retObj = ((Indices) obj).copy();
                }
                else if (obj instanceof Indices3D)
                {
                    retObj = ((Indices3D) obj).copy();
                }
                else if (obj instanceof String)
                {
                    retObj = ((String) obj) + "";
                }
                else
                {
                    // throw new ConditionalException("deal : Instance");
                    retObj = obj;
                    System.out.println("deal : Instanceof \"" + obj.getClass().getSimpleName() + "\"\n.");
                }
                varargout[i] = retObj;
            }
        }
        else
        {
            if (nargout != nargin)
            {
                throw new ConditionalException("deal : The number of outputs should match the number of inputs.");
            }
            varargout = varargin;
        }

        return varargout;
    }

    public static Cell arrayToCell(Object[] objArr)
    {
        if (objArr == null || objArr.length == 0)
        {
            throw new ConditionalException("objectArrayToCell : Array \"objArr\" must be non-null or non-empty.");
        }
        int len = objArr.length;
        Cell AC = new Cell(1, len);
        for (int i = 0; i < len; i++)
        {
            AC.setElementAt(i, objArr[i]);
        }
        return AC;
    }

    public Map<Integer, List<String>> toStringListMap()
    {
        Map<Integer, List<String>> map = new LinkedHashMap<Integer, List<String>>();

        boolean isVec = this.isVector();
        if (isVec)
        {
            boolean isRow = this.isRowVector();
            List<String> listStr = new ArrayList<String>();

            if (isRow)
            {
                for (int i = 0; i < m; i++)
                {
                    Object obj = A[i][0];
                    if (obj == null)
                    {
                        listStr.add("");
                    }
                    else
                    {
                        listStr.add(obj.toString());
                    }
                }
            }
            else
            {
                for (int j = 0; j < n; j++)
                {
                    Object obj = A[0][j];
                    if (obj == null)
                    {
                        listStr.add("");
                    }
                    else
                    {
                        listStr.add(obj.toString());
                    }
                }
            }
            map.put(0, listStr);
            return map;
        }

        for (int i = 0; i < m; i++)
        {
            List<String> listStr = new ArrayList<String>();
            for (int j = 0; j < n; j++)
            {
                Object obj = A[i][j];
                if (obj == null)
                {
                    listStr.add("");
                }
                else
                {
                    listStr.add(obj.toString());
                }
            }
            map.put(i, listStr);
        }
        return map;
    }

    public List<Object> toObjectList()
    {
        List<Object> list = new ArrayList<Object>();

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Object obj = A[i][j];
                if (obj == null)
                {
                    list.add(null);
                }
                else
                {
                    list.add(obj);
                }
            }
        }

        return list;
    }

    public List<String> toStringList()
    {
        List<String> list = new ArrayList<String>();

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Object obj = A[i][j];
                if (obj == null)
                {
                    list.add("");
                }
                else
                {
                    list.add(obj.toString());
                }
            }
        }

        return list;
    }

    public List<String> toUniqueStringList()
    {
        return toUniqueStringList(false);
    }

    public List<String> toUniqueStringList(boolean removeNullOrEmpty)
    {

        Set<String> set = new TreeSet<String>();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Object obj = A[i][j];
                if (obj == null)
                {
                    if (removeNullOrEmpty)
                    {
                        continue;
                    }
                    // obj = "";// list.add("");
                }
                String objStr = obj.toString();
                if ("".equals(objStr.trim()))
                {
                    if (removeNullOrEmpty)
                    {
                        continue;
                    }
                }
                // else
                // {
                // list.add(obj.toString());
                // }
                set.add(objStr);
            }
        }

        List<String> list = new ArrayList<String>(set);
        return list;
    }

    public static List<Cell> listToCellByRowOrColumn(List<Cell> listC, Dimension dim)
    {
        List<Cell> split = new ArrayList<Cell>();
        int siz = listC.size();

        for (int i = 0; i < siz; i++)
        {
            Cell ele = listC.get(i);

            if (dim == Dimension.COL)
            {
                int row = ele.getRowDimension();
                for (int j = 0; j < row; j++)
                {
                    Cell eleCell = ele.getRowAt(j);
                    split.add(eleCell);
                }
            }
            else
            {
                int col = ele.getColumnDimension();
                for (int j = 0; j < col; j++)
                {
                    Cell eleCell = ele.getColumnAt(j);
                    split.add(eleCell);
                }
            }
        }
        return split;
    }

    public static Cell mapToCell(Map<String, Object> map)
    {
        int siz = map.size();
        Cell cellMap = new Cell(siz, 2);
        int count = 0;
        for (Map.Entry<String, Object> entry : map.entrySet())
        {
            String key = entry.getKey();
            cellMap.set(count, 0, key);
            Object value = entry.getValue();
            if (value == null)
            {
                cellMap.set(count, 1, "");
            }
            else
            {
                boolean numOrStr = value instanceof String || value instanceof Number;
                if (!numOrStr)
                {
                    throw new ConditionalRuleException(
                            "Each value of the map object must be either a \"String\" or \"Number\"");
                }
                if (value instanceof String)
                {
                    cellMap.set(count, 1, (String) value);
                }
                else
                {
                    cellMap.set(count, 1, (Number) value);
                }
            }
            count++;
        }
        return cellMap;
    }

    public static Cell listToCell(List<Cell> listC, Dimension dim)
    {
        // check sizes
        int siz = listC.size();
        int sameLen = 0;
        int row = 0;
        int col = 0;

        Cell aggregate = null;

        if (dim == Dimension.COL)
        {
            for (int V = 0; V < siz; V++)
            {
                Cell cc = listC.get(V);
                if (V == 0)
                {
                    col = cc.getColumnDimension();
                    row = cc.getRowDimension();
                    continue;
                }
                if (col != cc.getColumnDimension())
                {
                    throw new ConditionalRuleException("All cells must have the same number of columns.");
                }
                row += cc.getRowDimension();
            }
        }
        else
        {
            for (int V = 0; V < siz; V++)
            {
                Cell cc = listC.get(V);
                if (V == 0)
                {
                    col = cc.getColumnDimension();
                    row = cc.getRowDimension();

                    continue;
                }
                if (row != cc.getRowDimension())
                {
                    throw new ConditionalRuleException("All cells must have the same number of rows.");
                }
                col += cc.getColumnDimension();
            }
        }

        aggregate = new Cell(row, col);

        int from = 0;
        int to = 0;

        if (dim == Dimension.COL)
        {
            for (int V = 0; V < siz; V++)
            {
                Cell cc = listC.get(V);
                int rowV = cc.getRowDimension();
                from = V * rowV;
                to = from + rowV - 1;
                aggregate.setMatrix(from, to, 0, col - 1, cc);
            }
        }
        else
        {
            for (int V = 0; V < siz; V++)
            {
                Cell cc = listC.get(V);
                int colV = cc.getColumnDimension();
                from = V * colV;
                to = from + colV - 1;
                aggregate.setMatrix(0, row - 1, from, to, cc);
            }
        }

        return aggregate;
    }

    public Map<String, Cell> toMap(boolean incFirstRow, int keyInd)
    {
        return toMap(incFirstRow, keyInd, null);
    }

    public Map<String, Cell> toMap(boolean incFirstRow, int keyInd, int[] valueInd)
    {
        if (n < 2)
        {
            throw new ConditionalRuleException("Cell data must have more than 2 columns.");
        }

        Map<String, Cell> keyValueMap = new LinkedHashMap<String, Cell>();
        Indices IND = Indices.linspace(0, n - 1);

        if (!IND.EQ(keyInd).anyBoolean())
        {
            throw new ConditionalRuleException("The key-index (= " + keyInd + ") is out of bound.");
        }

        Cell copy = null;
        if (incFirstRow)
        {
            copy = this;
        }
        else
        {
            copy = this.getRows(1, m - 1);
        }

        int M = copy.getRowDimension();
        int N = copy.getColumnDimension();

        // Cell.;//
        // IND.toCell().to
        Cell keyCell = null;
        Cell valueCell = null;
        int[] keyIndArr = null;

        if (valueInd == null || valueInd.length == 0)
        {
            FindInd find = IND.NEQ(keyInd).findIJ();
            keyIndArr = find.getIndex();
            keyCell = copy.getColumnAt(keyInd);
            valueCell = copy.getColumns(keyIndArr);
        }
        else
        {
            keyCell = copy.getColumnAt(keyInd);
            valueCell = copy.getColumns(valueInd);
        }

        int numR = keyCell.length();

        for (int i = 0; i < numR; i++)
        {
            Object obj = keyCell.getElementAt(i);
            Cell rowI = valueCell.getRowAt(i);
            keyValueMap.put((String) obj, rowI);
        }

        return keyValueMap;
    }

    public Matrix toMatrix()
    {
        Matrix cellMat = new Matrix(this.m, this.n, Double.NaN);
        for (int i = 0; i < m; i++)
        {
            // List<String> listStr = new ArrayList<String>();
            for (int j = 0; j < n; j++)
            {
                Object obj = A[i][j];
                if (obj == null)
                {
                    continue;
                }
                if (obj instanceof String)
                {// try to parse to see if its a number.
                    double val = 0.0;
                    try
                    {
                        val = Double.parseDouble((String) obj);
                    }
                    catch (NumberFormatException nfe)
                    {
                        continue;
                    }
                    cellMat.set(i, j, val);
                }
                else if (obj instanceof Number)
                {
                    cellMat.set(i, j, ((Number) obj).doubleValue());
                }
            }
        }
        return cellMat;
    }

    public void printSize()
    {
        System.out.println("Size = [" + m + ", " + n + "]\n");
    }

    public void print()
    {
        print(4);
    }

    public void print(int dp)
    {
        print("", dp);
    }

    /*
     * This method needs to check if instances of each other.
     */
    public boolean cellOfObjects(Class obj)
    {
        if (this.A == null)
        {
            return false;
        }

        int count = 0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] == null)
                {
                    return false;
                }
                boolean eqCl = obj.equals(A[i][j].getClass());
                if (!eqCl)
                {
                    return false;
                }
                else
                {
                    count++;
                }
            }
        }

        boolean tf = (this.numel() == count);

        return tf;
    }

    /**
     * 
     * @param i
     * @return
     */
    public Cell removeRowAt(int row)
    {
        return this.removeRowsAt(new int[]
        {
                row
        });
    }

    /**
     * 
     * @param rows
     * @return
     */
    public Cell removeRowsAt(int[] rows)
    {
        int lenRows = rows.length;
        ArrayList<Integer> arrayListRowCount = new ArrayList<Integer>();

        int count = 0;

        for (int i = 0; i < lenRows; i++)
        {
            if (rows[i] < 0 || rows[i] >= m)
            {
                throw new ArrayIndexOutOfBoundsException("removeRowsAt : Row indices is out-of-bound.");
            }
            // arrayListRowCount.add(new Integer(count++));
        }

        for (int i = 0; i < m; i++)
        {
            arrayListRowCount.add(new Integer(count++));
        }

        for (int i = 0; i < lenRows; i++)
        {
            Integer countInt = new Integer(rows[i]);
            if (arrayListRowCount.contains(countInt))
            {
                arrayListRowCount.set(rows[i], null);
            }
        }

        count = 0;
        ArrayList<Integer> nonNullRowsList = new ArrayList<Integer>();

        for (int i = 0; i < m; i++)
        {
            Integer obj = arrayListRowCount.get(i);
            if (obj != null)
            {
                nonNullRowsList.add(obj);
            }
            else
            {
                count++;
            }
        }

        if (count == m)
        {
            return (Cell) null;
        }

        int numRows = nonNullRowsList.size();
        int[] newRows = new int[numRows];
        for (int i = 0; i < numRows; i++)
        {
            newRows[i] = nonNullRowsList.get(i).intValue();
        }

        Cell R = this.getRows(newRows);
        return R;
    }

    /**
     * 
     * @param i
     * @return
     */
    public Cell removeColAt(int col)
    {
        return this.removeColsAt(new int[]
        {
                col
        });
    }

    /**
     * 
     * @param cols
     * @return
     */
    public Cell removeColsAt(int[] cols)
    {
        int lenCols = cols.length;
        ArrayList<Integer> arrayListColCount = new ArrayList<Integer>();
        int count = 0;
        for (int i = 0; i < lenCols; i++)
        {
            if (cols[i] < 0 || cols[i] >= n)
            {
                throw new ArrayIndexOutOfBoundsException("removeColsAt : Column indices is out-of-bound.");
            }
            // arrayListColCount.add(new Integer(count++));
        }

        for (int i = 0; i < n; i++)
        {
            arrayListColCount.add(new Integer(count++));
        }

        for (int i = 0; i < lenCols; i++)
        {
            Integer countInt = new Integer(cols[i]);
            if (arrayListColCount.contains(countInt))
            {
                arrayListColCount.set(cols[i], null);
            }
        }

        count = 0;
        ArrayList<Integer> nonNullColsList = new ArrayList<Integer>();

        for (int i = 0; i < n; i++)
        {
            Integer obj = arrayListColCount.get(i);
            if (obj != null)
            {
                nonNullColsList.add(obj);
            }
            else
            {
                count++;
            }
        }

        if (count == n)
        {
            return null;
        }

        int numCols = nonNullColsList.size();
        int[] newCols = new int[numCols];
        for (int i = 0; i < numCols; i++)
        {
            newCols[i] = nonNullColsList.get(i).intValue();
        }

        Cell R = this.getColumns(newCols);
        return R;
    }

    public Cell removeElAt(int row, int col)
    {
        if (row >= this.m)
        {
            throw new ConditionalRuleException("removeElAt : Index parameter \"row\" (= " + row + ") is out of bound.");
        }
        if (col >= this.n)
        {
            throw new ConditionalRuleException("removeElAt : Index parameter \"col\" (= " + col + ") is out of bound.");
        }
        Cell result = null;
        int count = 0;
        label: for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                count++;
                if (i == row && j == col)
                {
                    break label;
                }
            }
        }
        result = removeElAt(count);
        return result;
    }

    public Cell removeEls(int[] numEls)
    {
        Cell result = null;
        if (numEls == null || numEls.length == 0)
        {
            return this;// copy();
        }
        int totEl = this.numel();

        if (numEls.length > totEl)
        {
            throw new ConditionalException(
                    "removeEls : The total number of elements to be removed exceeds total matrix elements.");
        }

        // int[] index = Indices.linspace(0, totEl - 1).getRowPackedCopy();

        // check if valid entries
        int lenRemov = numEls.length;
        for (int i = 0; i < lenRemov; i++)
        {
            if (numEls[i] < 0 || numEls[i] >= totEl)
            {
                throw new ConditionalException(
                        "removeEls : Element \"numEls[" + i + "] = " + numEls[i] + "\" to be removed is out-of-bound.");
            }
        }

        ArrayList<Object> R = new ArrayList<Object>();

        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                R.add(A[i][j]);
            }
        }

        for (int i = 0; i < lenRemov; i++)
        {// mark the position to be removed.
            int indRem = numEls[i];
            Object DB = null;
            R.set(indRem, DB);
        }

        int siz = R.size();
        ArrayList<Object> RnonNull = new ArrayList<Object>();

        for (int k = 0; k < siz; k++)
        {
            Object DB = R.get(k);
            if (DB != null)
            {
                RnonNull.add(DB);
            }
        }

        siz = RnonNull.size();
        if (siz == 0)
        {// everything has been removed.
            return (Cell) null;
        }

        if (!this.isVector())
        {// matrix
            result = new Cell(1, siz);
            for (int i = 0; i < siz; i++)
            {
                result.set(0, i, RnonNull.get(i));
            }
        }
        else
        {// vector
            if (this.isRowVector())
            {
                result = new Cell(1, siz);
                for (int i = 0; i < siz; i++)
                {
                    result.set(0, i, RnonNull.get(i));
                }
            }
            else
            {
                result = new Cell(siz, 1);
                for (int i = 0; i < siz; i++)
                {
                    result.set(i, 0, RnonNull.get(i));
                }
            }
        }
        return result;
    }

    public Cell removeElAt(int ind)
    {
        Cell result = null;
        ArrayList<Object> R = new ArrayList<Object>();
        int len = 0;
        int newLen = 0;
        if (this.isVector())
        {
            len = this.length();
            if (ind >= len)
            {
                throw new ConditionalRuleException(
                        "removeElAt : Index parameter \"ind\" (= " + ind + ") is out of bound.");
            }
            if (this.isRowVector())
            {
                for (int i = 0; i < len; i++)
                {
                    if (i != ind)
                    {
                        R.add(A[0][i]);
                    }
                }
                newLen = len - 1;
                result = new Cell(1, newLen);
                for (int i = 0; i < newLen; i++)
                {
                    result.set(0, i, R.get(i));
                }
            }
            else
            {
                for (int i = 0; i < len; i++)
                {
                    if (i != ind)
                    {
                        R.add(A[i][0]);
                    }
                }
                newLen = len - 1;
                result = new Cell(newLen, 1);
                for (int i = 0; i < newLen; i++)
                {
                    result.set(i, 0, R.get(i));
                }
            }
        }
        else
        {
            len = this.numel();
            if (ind >= len)
            {
                throw new ConditionalRuleException("removeElAt : Index parameter \"ind\" (= " + ind
                        + ") is out of bound colum-wise ( = " + len + ").");
            }
            Cell newMat = this.toColVector();
            for (int i = 0; i < len; i++)
            {
                if (i != ind)
                {
                    R.add(newMat.get(i, 0));
                }
            }
            newLen = len - 1;
            result = new Cell(1, newLen);
            for (int i = 0; i < newLen; i++)
            {
                result.set(0, i, R.get(i));
            }
        }
        return result;
    }

    /**
     * 
     * @param indices
     *            int[]
     * @return Matrix
     */
    public Cell getRows(int[] indices)
    {
        int len = indices.length;
        Cell val = new Cell(len, n);
        for (int i = 0; i < len; i++)
        {
            if (indices[i] > (m - 1) || indices[i] < 0)
            {
                throw new IllegalArgumentException(" getRows: Array out-of-bounds.");
            }
            Cell temp = this.getMatrix(indices[i], indices[i], 0, n - 1);
            val.setMatrix(i, i, 0, n - 1, temp);
        }
        return val;
    }// end method

    public Cell getRows(int from, int to)
    {
        if (from > to)
        {
            throw new IllegalArgumentException(" getRows: Value for parameter \"from\" (= " + from
                    + ") must be equal to or less than that of \"to\" (= " + to + ").");
        }
        int[] ind = Indices.linspace(from, to).getRowPackedCopy();
        return getRows(ind);
    }

    public Cell getRowAt(int ind)
    {
        int[] indices =
        {
                ind
        };
        return getRows(indices);
    }

    /**
     * 
     * 
     * @param B
     *            Matrix
     * @return Matrix
     *         <p>
     *         Merge this matrix AND Object array 'B horizontally where each
     *         type are either 'Matrix' or 'Indices' to form a larger matrix.
     *         Row size must agree.
     *         </p>
     */
    public Cell mergeHoriz(Object... B)
    {
        if (B == (Object[]) null || B.length == 0)
        {
            return copy();
        }

        int lenB = B.length;
        ArrayList<Object> objList = new ArrayList<Object>();
        if (!this.isNull())
        {
            objList.add(this.copy());
        }

        for (int i = 0; i < lenB; i++)
        {
            if (B[i] != null)
            {
                if (!(B[i] instanceof Cell))
                {
                    throw new IllegalArgumentException(
                            " mergeHoriz : Input argument " + (i + 1) + " must be either a \"Cell\".");
                }
                objList.add(B[i]);
            }
        }

        // if all input arguments is null, then just return a copy of this
        // matrix.
        if (objList.size() == 1)
        {
            return copy();
        }

        for (int i = 0; i < objList.size(); i++)
        {
            Object matIndObj = objList.get(i);
            if (matIndObj instanceof Cell)
            {
                if (m != ((Cell) matIndObj).getRowDimension())
                {
                    throw new IllegalArgumentException(" mergeHoriz : Cell row dimensions must agree.");
                }
            }
            else
            {
                throw new IllegalArgumentException(
                        " mergeHoriz : Input argument " + (i + 1) + " must be either a \"Cell\".");

            }
        }

        int newCol = 0;
        for (int i = 0; i < objList.size(); i++)
        {
            Object matIndObj = objList.get(i);
            if (matIndObj instanceof Cell)
            {
                newCol += ((Cell) matIndObj).getColumnDimension();
            }
            else
            {
                throw new IllegalArgumentException(
                        " mergeHoriz : Input argument " + (i + 1) + " must be either a \"Cell\".");
            }
        }

        Cell R = new Cell(m, newCol);

        int begInd = 0;
        int endInd = 0;
        int numCol = 0;

        for (int i = 0; i < objList.size(); i++)
        {
            Object matIndObj = objList.get(i);
            if (i == 0)
            {// first object is guranteed to be a matrix, because 'this' matrix
             // is the first one to be added.
                numCol = ((Cell) matIndObj).getColumnDimension();
                endInd = numCol - 1;
                // System.out.println("BLOCK #1 : [begin,end] = [" + begInd +
                // "," + endInd + "]");
                R.setMatrix(0, m - 1, begInd, endInd, (Cell) matIndObj);
            }
            else
            {
                if (matIndObj instanceof Cell)
                {
                    begInd += numCol;
                    numCol = ((Cell) matIndObj).getColumnDimension();
                    endInd = begInd + numCol - 1;
                    // System.out.println("BLOCK #2 : [begin,end] = [" + begInd
                    // + "," + endInd + "]");
                    R.setMatrix(0, m - 1, begInd, endInd, (Cell) matIndObj);
                }
                else
                {
                    throw new IllegalArgumentException(
                            " mergeHoriz : Input argument " + (i + 1) + " must be either a \"Cell\".");
                }
            }
        }

        return R;
    }

    /**
     * 
     * 
     * @param B
     *            Matrix
     * @return Matrix
     *         <p>
     *         Merge this matrix AND matrix 'B' horizontally to form a larger
     *         matrix. Row size must agree.
     *         </p>
     */
    public Cell mergeH(Cell B)
    {
        return mergeHoriz(B);
        /*
         * if (m != B.m) { throw new IllegalArgumentException(" mergeH : Matrix
         * i dimensions must agree."); }
         * 
         * int row = n + B.n; Matrix R = new Matrix(m, row); R.setMatrix(0, m -
         * 1, 0, n - 1, this); R.setMatrix(0, m - 1, n, n + B.n - 1, B); return
         * R;
         */
    }

    /**
     * 
     * 
     * @param B
     *            Matrix
     * @return Matrix
     *         <p>
     *         Merge this matrix AND matrix 'B' vertically to form a larger
     *         matrix. Column size must agree.
     *         </p>
     */
    public Cell mergeV(Cell B)
    {
        return mergeVerti(B);
        /*
         * if (n != B.n) { throw new IllegalArgumentException(" mergeV : Matrix
         * column dimensions must agree."); } int row = m + B.m; Matrix R = new
         * Matrix(row, n); R.setMatrix(0, m - 1, 0, n - 1, this); R.setMatrix(m,
         * m + B.m - 1, 0, n - 1, B); return R;
         */
    }

    public Cell mergeVerti(Object... B)
    {
        if (B == (Object[]) null || B.length == 0)
        {
            return copy();
        }

        int lenB = B.length;
        ArrayList<Object> objList = new ArrayList<Object>();
        objList.add(this);

        for (int i = 0; i < lenB; i++)
        {
            if (B[i] != null)
            {
                if (!(B[i] instanceof Cell))
                {
                    throw new IllegalArgumentException(
                            " mergeVerti : Input argument " + (i + 1) + " must be a \"Cell\".");
                }
                objList.add(B[i]);
            }
        }

        // if all input arguments is null, then just return a copy of this
        // matrix.
        if (objList.size() == 1)
        {
            return copy();
        }

        for (int i = 0; i < objList.size(); i++)
        {
            Object matIndObj = objList.get(i);
            if (matIndObj instanceof Cell)
            {
                if (n != ((Cell) matIndObj).getColumnDimension())
                {
                    throw new IllegalArgumentException(" mergeVerti : Cell column dimensions must agree.");
                }
            }
            else
            {

                throw new IllegalArgumentException(" mergeVerti : Input argument " + (i + 1) + " must be a \"Cell\".");

            }
        }

        int newRow = 0;
        for (int i = 0; i < objList.size(); i++)
        {
            Object matIndObj = objList.get(i);
            if (matIndObj instanceof Cell)
            {
                newRow += ((Cell) matIndObj).getRowDimension();
            }
            else
            {

            }
        }

        Cell R = new Cell(newRow, n);

        int begInd = 0;
        int endInd = 0;
        int numRow = 0;

        for (int i = 0; i < objList.size(); i++)
        {
            Object matIndObj = objList.get(i);
            if (i == 0)
            {// first object is guranteed to be a matrix, because 'this' matrix
             // is the first one to be added.
                numRow = ((Cell) matIndObj).getRowDimension();
                endInd = numRow - 1;
                // System.out.println("vert BLOCK #1 : [begin,end] = [" + begInd
                // + "," + endInd + "]");
                R.setMatrix(begInd, endInd, 0, n - 1, (Cell) matIndObj);
            }
            else
            {
                if (matIndObj instanceof Cell)
                {
                    begInd += numRow;
                    numRow = ((Cell) matIndObj).getRowDimension();
                    endInd = begInd + numRow - 1;
                    // System.out.println("vert BLOCK #2 : [begin,end] = [" +
                    // begInd + "," + endInd + "]");
                    R.setMatrix(begInd, endInd, 0, n - 1, (Cell) matIndObj);
                }
                else
                {
                    throw new IllegalArgumentException(
                            " mergeVerti : Input argument " + (i + 1) + " must be a \"Cell\".");
                }
            }
        }

        return R;
    }

    /**
     * Get a submatrix.
     * 
     * @param i0
     *            Initial i index
     * @param i1
     *            Final i index
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @return A(i0:i1,j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public Cell getMatrix(int i0, int i1, int j0, int j1)
    {
        Cell X = new Cell(i1 - i0 + 1, j1 - j0 + 1);
        Object[][] B = X.getArray();
        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    B[i - i0][j - j0] = A[i][j];
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
     * Get a submatrix.
     * 
     * @param r
     *            Array of i indices.
     * @param c
     *            Array of column indices.
     * @return A(r(:),c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public Cell getMatrix(int[] r, int[] c)
    {
        Cell X = new Cell(r.length, c.length);
        Object[][] B = X.getArray();
        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    B[i][j] = A[r[i]][c[j]];
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
     * 
     * @param arrayList
     * @return
     */
    public Cell getMatrix(ArrayList<int[]> arrayList)
    {
        int siz = arrayList.size();
        if (siz == 0)
        {
            return null;
        }
        Cell matrix = new Cell(siz, 1);
        Object[][] X = matrix.getArray();
        int[] indices = null;
        for (int i = 0; i < siz; i++)
        {
            indices = (int[]) arrayList.get(i);
            X[i][0] = A[indices[0]][indices[1]];
        }
        return matrix;
    }

    /**
     * Get a submatrix.
     * 
     * @param i0
     *            Initial i index
     * @param i1
     *            Final i index
     * @param c
     *            Array of column indices.
     * @return A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public Cell getMatrix(int i0, int i1, int[] c)
    {
        Cell X = new Cell(i1 - i0 + 1, c.length);
        Object[][] B = X.getArray();
        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    B[i - i0][j] = A[i][c[j]];
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
     * Get a submatrix.
     * 
     * @param i0
     *            Initial i index
     * @param i1
     *            Final i index
     * @param c
     *            Array of column indices.
     * @return A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public Cell getMatrix(int i0, int[] c)
    {
        return getMatrix(i0, i0, c);
    }

    /**
     * Get a submatrix.
     * 
     * @param r
     *            Array of i indices.
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @return A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public Cell getMatrix(int[] r, int j0, int j1)
    {
        Cell X = new Cell(r.length, j1 - j0 + 1);
        Object[][] B = X.getArray();
        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    B[i][j - j0] = A[r[i]][j];
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
     * Get a submatrix.
     * 
     * @param r
     *            Array of i indices.
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @return A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public Cell getMatrix(int[] r, int j0)
    {
        return getMatrix(r, j0, j0);
    }

    /**
     * Set a submatrix.
     * 
     * @param i0
     *            Initial i index
     * @param i1
     *            Final i index
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @param X
     *            A(i0:i1,j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int i0, int i1, int j0, int j1, Object X)
    {
        if (!(X instanceof Matrix) && !(X instanceof Indices) && !(X instanceof String) && !(X instanceof Number)
                && !(X instanceof Cell))
        {
            throw new ArrayIndexOutOfBoundsException(
                    "setMatrix : Object \"X\" must either be of type \"Matrix\", \"Indices\", \"Numbers\", \"Cell\" or \"String\".");
        }
        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    if (X instanceof Matrix)
                    {
                        A[i][j] = ((Matrix) X).get(i - i0, j - j0);
                    }
                    else if (X instanceof Matrix)
                    {
                        A[i][j] = ((Indices) X).get(i - i0, j - j0);
                    }
                    else if (X instanceof Cell)
                    {
                        A[i][j] = ((Cell) X).get(i - i0, j - j0);
                    }
                    else if (X instanceof Number)
                    {
                        A[i][j] = ((Number) X).doubleValue();
                    }
                    else
                    {
                        A[i][j] = (String) X;// ((Indices) X).get(i - i0, j -
                                             // j0);
                    }

                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("setMatrix : Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param r
     *            Array of i indices.
     * @param c
     *            Array of column indices.
     * @param X
     *            A(r(:),c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int[] r, int[] c, Object X)
    {
        if (!(X instanceof Matrix) && !(X instanceof Indices))
        {
            throw new ArrayIndexOutOfBoundsException(
                    "setMatrix : Object \"X\" must either be of type \"Matrix\" or \"Indices\".");
        }
        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    if (X instanceof Matrix)
                    {
                        A[r[i]][c[j]] = ((Matrix) X).get(i, j);
                    }
                    else
                    {
                        A[r[i]][c[j]] = ((Indices) X).get(i, j);
                    }

                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("setMatrix : Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param r
     *            Array of i indices.
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @param X
     *            A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int[] r, int j0, int j1, Object X)
    {
        if (!(X instanceof Matrix) && !(X instanceof Indices))
        {
            throw new ArrayIndexOutOfBoundsException(
                    "setMatrix : Object \"X\" must either be of type \"Matrix\" or \"Indices\".");
        }
        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    if (X instanceof Matrix)
                    {
                        A[r[i]][j] = ((Matrix) X).get(i, j - j0);
                    }
                    else
                    {
                        A[r[i]][j] = ((Indices) X).get(i, j - j0);
                    }

                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("setMatrix : Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param r
     *            Array of i indices.
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @param X
     *            A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int[] r, int j0, Object X)
    {
        if (!(X instanceof Matrix) && !(X instanceof Indices))
        {
            throw new ArrayIndexOutOfBoundsException(
                    "setMatrix : Object \"X\" must either be of type \"Matrix\" or \"Indices\".");
        }
        setMatrix(r, j0, j0, X);
    }

    /**
     * Set a submatrix.
     * 
     * @param i0
     *            Initial i index
     * @param i1
     *            Final i index
     * @param c
     *            Array of column indices.
     * @param X
     *            A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int i0, int i1, int[] c, Object X)
    {
        if (!(X instanceof Matrix) && !(X instanceof Indices))
        {
            throw new ArrayIndexOutOfBoundsException(
                    "setMatrix : Object \"X\" must either be of type \"Matrix\" or \"Indices\".");
        }
        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    if (X instanceof Matrix)
                    {
                        A[i][c[j]] = (Matrix) X;// ((Matrix) X).get(i - i0, i);
                    }
                    else
                    {
                        A[i][c[j]] = (Indices) X;
                    }

                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("setMatrix : Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param i0
     *            Initial i index
     * @param i1
     *            Final i index
     * @param c
     *            Array of column indices.
     * @param X
     *            A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setMatrix(int i0, int[] c, Object X)
    {
        if (!(X instanceof Matrix) && !(X instanceof Indices))
        {
            throw new ArrayIndexOutOfBoundsException(
                    "setMatrix : Object \"X\" must either be of type \"Matrix\" or \"Indices\".");
        }
        setMatrix(i0, i0, c, X);
    }

    public int[] sizeIntArr()
    {
        return new int[]
        {
                m, n
        };
    }

    /**
     * 
     * @return
     */
    public Indices sizeIndices()
    {
        return new Indices(sizeIntArr());
    }

    public Cell getColumns(int from, int to)
    {
        if (from > to)
        {
            throw new IllegalArgumentException(" getColumns: Value for parameter \"from\" (= " + from
                    + ") must be equal to or less than that of \"to\" (= " + to + ").");
        }
        int[] arr = Indices.linspace(from, to).getRowPackedCopy();
        return this.getColumns(arr);
    }

    /**
     * 
     * @param indices
     *            int[]
     * @return Matrix
     */
    public Cell getColumns(int[] indices)
    {
        int len = indices.length;
        Cell val = new Cell(m, len);
        for (int i = 0; i < len; i++)
        {
            if (indices[i] > (n - 1) || indices[i] < 0)
            {
                throw new IllegalArgumentException(" getColumns: Array out-of-bounds.");
            }
            Cell temp = this.getMatrix(0, m - 1, indices[i], indices[i]);
            val.setMatrix(0, m - 1, i, i, temp);
        }
        return val;
    }// end method

    /**
     * 
     * @param colIndex
     *            int
     * @return Matrix
     */
    public Cell getColumnAt(int colIndex)
    {
        return getColumns(new int[]
        {
                colIndex
        });
    }

    public boolean isAllClass(Class classType)
    {
        if (this.A == null)
        {
            return false;
        }
        boolean tf = false;
        String classTypeName = classType.getCanonicalName();
        int count = 0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Object obIJ = A[i][j];
                String classAname = obIJ.getClass().getCanonicalName();
                // if(this.A[i][i] instanceof classType.getClass()){}
                if (classTypeName.equals(classAname))
                {
                    count++;
                }
            }
        }

        if (this.numel() == count)
        {
            return true;
        }

        return tf;
    }

    public boolean containsClass(Class classType)
    {
        if (this.A == null)
        {
            return false;
        }
        boolean tf = false;
        String classTypeName = classType.getCanonicalName();

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Object obIJ = A[i][j];
                String classAname = obIJ.getClass().getCanonicalName();
                // if(this.A[i][i] instanceof classType.getClass()){}
                if (classTypeName.equals(classAname))
                {
                    return true;
                }
            }
        }

        return tf;
    }

    public void print(String name, int dp)
    {
        int len = 0;
        if (name == null || "".equals(name))
        {
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$ Cell Object $$$$$$$$$$$$$$$$$$$$$$\n");
        }
        else
        {
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$ " + name + " $$$$$$$$$$$$$$$$$$$$$$\n");
        }

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] == null)
                {
                    System.out.println("########## A[" + i + "][" + j + "] => NULL ##########");
                }
                else if (A[i][j] instanceof Matrix[])
                {
                    Matrix[] matArr = (Matrix[]) A[i][j];
                    len = matArr.length;
                    System.out.println("########## A[" + i + "][" + j + "] => Matrix[" + len + "] ##########");
                    for (int k = 0; k < len; k++)
                    {
                        matArr[k].printInLabel("matArr[" + k + "]", dp);
                    }
                }
                else if (A[i][j] instanceof Matrix)
                {
                    Matrix mat = (Matrix) A[i][j];
                    System.out.println("########## A[" + i + "][" + j + "] => Matrix ##########");
                    mat.printInLabel("mat", dp);
                }
                else if (A[i][j] instanceof Indices[])
                {
                    Indices[] indArr = (Indices[]) A[i][j];
                    len = indArr.length;
                    System.out.println("########## A[" + i + "][" + j + "] => Indices[" + len + "] ##########");
                    for (int k = 0; k < len; k++)
                    {
                        indArr[k].printInLabel("indArr[" + k + "]", 0);
                    }
                }
                else if (A[i][j] instanceof Indices)
                {
                    Indices ind = (Indices) A[i][j];
                    System.out.println("########## A[" + i + "][" + j + "] => Indices ##########");
                    ind.printInLabel("ind");
                }
                else if (A[i][j] instanceof String)
                {
                    System.out.println(
                            "########## A[" + i + "][" + j + "] : (" + ((String) A[i][j]) + ") => String ##########");
                }
                else
                {
                    System.out.println("########## A[" + i + "][" + j + "] : => class name : "
                            + A[i][j].getClass().getName() + " ##########");
                }
            }
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param i0
     *            Initial i index
     * @param i1
     *            Final i index
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @param X
     *            A(i0:i1,j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setCell(int i0, int i1, int j0, int j1, Object[][] X)
    {
        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    A[i][j] = X[i - i0][j - j0];
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("setCell : Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param r
     *            Array of i indices.
     * @param c
     *            Array of column indices.
     * @param X
     *            A(r(:),c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setCell(int[] r, int[] c, Object[][] X)
    {

        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    A[r[i]][c[j]] = X[i][j];
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("setCell : Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param r
     *            Array of i indices.
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @param X
     *            A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setCell(int[] r, int j0, int j1, Object[][] X)
    {

        try
        {
            for (int i = 0; i < r.length; i++)
            {
                for (int j = j0; j <= j1; j++)
                {
                    A[r[i]][j] = X[i][j - j0];
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("setCell : Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param r
     *            Array of i indices.
     * @param j0
     *            Initial column index
     * @param j1
     *            Final column index
     * @param X
     *            A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setCell(int[] r, int j0, Object[][] X)
    {
        setMatrix(r, j0, j0, X);
    }

    /**
     * Set a submatrix.
     * 
     * @param i0
     *            Initial i index
     * @param i1
     *            Final i index
     * @param c
     *            Array of column indices.
     * @param X
     *            A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setCell(int i0, int i1, int[] c, Object[][] X)
    {

        try
        {
            for (int i = i0; i <= i1; i++)
            {
                for (int j = 0; j < c.length; j++)
                {
                    A[i][c[j]] = X[i - i0][j];
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("setCell : Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     * 
     * @param i0
     *            Initial i index
     * @param i1
     *            Final i index
     * @param c
     *            Array of column indices.
     * @param X
     *            A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException
     *                Submatrix indices
     */
    public void setCell(int i0, int[] c, Object[][] X)
    {
        setMatrix(i0, i0, c, X);
    }

    public Object cat(Dimension dim)
    {
        boolean cond = cellOfObjects(Matrix.class);
        if (!cond)
        {
            throw new ConditionalException("cat : All entries must be objects of type \"Matrix\".");
        }

        Object catObj = null;

        Cell cellCol = this.toColVector();
        int numEl = cellCol.length();
        int first = 0;
        int second = 0;
        int row = 0;
        int col = 0;

        if (dim == Dimension.ROW)
        {// must be same colum
            if (numEl == 1)
            {
                Matrix mat = (Matrix) cellCol.start();
                catObj = mat;
            }
            else
            {
                for (int i = 0; i < numEl; i++)
                {
                    Matrix mat = (Matrix) cellCol.getElementAt(i);
                    // row += mat.getRowDimension();
                    if (i == 0)
                    {
                        first = mat.getColumnDimension();
                    }
                    else
                    {
                        second = mat.getColumnDimension();
                        if (first != second)
                        {
                            throw new ConditionalException("cat : All matrix entries must have the same columns.");
                        }
                    }
                }
                Matrix rowMat = null;// new Matrix(row, firstRowElement);
                Object[] rest = new Object[numEl - 1];
                for (int i = 0; i < numEl; i++)
                {
                    Matrix mat = (Matrix) cellCol.getElementAt(i);
                    if (i == 0)
                    {
                        rowMat = mat;
                    }
                    else
                    {
                        rest[i - 1] = mat;
                    }
                }
                catObj = rowMat.vertcat(rest);
            }
        }
        else if (dim == Dimension.COL)
        {// must be the same row
            if (numEl == 1)
            {
                Matrix mat = (Matrix) cellCol.start();
                catObj = mat;
            }
            else
            {
                for (int i = 0; i < numEl; i++)
                {
                    Matrix mat = (Matrix) cellCol.getElementAt(i);
                    if (i == 0)
                    {
                        first = mat.getRowDimension();
                    }
                    else
                    {
                        second = mat.getRowDimension();
                        if (first != second)
                        {
                            throw new ConditionalException("cat : All matrix entries must have the same rows.");
                        }
                    }
                }
                Matrix colMat = null;// new Matrix(row, firstRowElement);
                Object[] rest = new Object[numEl - 1];
                for (int i = 0; i < numEl; i++)
                {
                    Matrix mat = (Matrix) cellCol.getElementAt(i);
                    if (i == 0)
                    {
                        colMat = mat;
                    }
                    else
                    {
                        rest[i - 1] = mat;
                    }
                }
                catObj = colMat.horzcat(rest);
            }
        }
        else
        {// must be the same size
            Indices firstSiz = null;
            Indices secondSiz = null;
            for (int i = 0; i < numEl; i++)
            {
                Matrix mat = (Matrix) cellCol.getElementAt(i);
                if (i == 0)
                {
                    firstSiz = mat.sizeIndices();
                }
                else
                {
                    secondSiz = mat.sizeIndices();
                    if (firstSiz.NEQ(secondSiz).anyBoolean())
                    {
                        throw new ConditionalException(
                                "cat : All matrix entries must have the same size (rows by colums).");
                    }
                }
            }
            row = firstSiz.start();
            col = firstSiz.getElementAt(1);
            Matrix3D mat3d = new Matrix3D(row, col, numEl);
            for (int i = 0; i < numEl; i++)
            {
                Matrix mat = (Matrix) cellCol.getElementAt(i);
                mat3d.setPageAt(i, mat);
            }
            catObj = mat3d;
        }

        return catObj;
    }

    public Matrix horzcat()
    {

        // --------------------------------------------
        int numel = this.numel();
        int numNull = this.numNull();

        if (numel == numNull)
        {
            return new Matrix();
        }

        Cell cellCol = this.toColVector();

        Indices nullCell = cellCol.isnull().NOT();
        // nullCell.printInLabel("nullCell");
        FindInd find = nullCell.findIJ();

        int[] arr = null;

        if (!find.isNull())
        {
            arr = find.getIndex();
            cellCol = cellCol.getElements(arr);
        }
        // --------------------------------------------

        boolean cond = cellCol.cellOfObjects(Matrix.class);
        if (!cond)
        {
            throw new ConditionalException("horzcat : All entries must be objects of type \"Matrix\".");
        }

        find = cellCol.nullMatrices().NOT().findIJ();
        if (!find.isNull())
        {
            arr = find.getIndex();
            cellCol = cellCol.getElements(arr);
        }
        else
        {
            return new Matrix();
        }

        Matrix catObj = null;

        int numEl = cellCol.length();
        int first = 0;
        int second = 0;
        int row = 0;
        int col = 0;

        // must be the same row
        if (numEl == 1)
        {
            Matrix mat = (Matrix) cellCol.start();
            catObj = mat;
        }
        else
        {
            for (int i = 0; i < numEl; i++)
            {
                Matrix mat = (Matrix) cellCol.getElementAt(i);
                if (i == 0)
                {
                    first = mat.getRowDimension();
                }
                else
                {
                    second = mat.getRowDimension();
                    if (first != second)
                    {
                        throw new ConditionalException(
                                "horzcat : All matrix entries must have the same number of rows.");
                    }
                }
            }
            Matrix colMat = null;// new Matrix(row, firstRowElement);
            Object[] rest = new Object[numEl - 1];
            for (int i = 0; i < numEl; i++)
            {
                Matrix mat = (Matrix) cellCol.getElementAt(i);
                if (i == 0)
                {
                    colMat = mat;
                }
                else
                {
                    rest[i - 1] = mat;
                }
            }
            catObj = colMat.horzcat(rest);
        }

        return catObj;
    }

    public Matrix vertcat()
    {
        int numel = this.numel();
        int numNull = this.numNull();

        if (numel == numNull)
        {
            return new Matrix();
        }

        Cell cellCol = this.toColVector();
        Indices nullCell = cellCol.isnull().NOT();
        // nullCell.printInLabel("nullCell");
        FindInd find = nullCell.findIJ();
        int[] arr = null;

        if (!find.isNull())
        {
            arr = find.getIndex();
            cellCol = cellCol.getElements(arr);
        }

        boolean cond = cellCol.cellOfObjects(Matrix.class);
        if (!cond)
        {
            throw new ConditionalException("vertcat : All entries must be objects of type \"Matrix\".");
        }

        find = cellCol.nullMatrices().NOT().findIJ();
        if (!find.isNull())
        {
            arr = find.getIndex();
            cellCol = cellCol.getElements(arr);
        }
        else
        {
            return new Matrix();
        }

        Matrix catObj = null;
        // Dimension dim = Dimension.ROW;

        int numEl = cellCol.length();
        int first = 0;
        int second = 0;
        // int row = 0;
        // int col = 0;

        // must be same colum
        if (numEl == 1)
        {
            Matrix mat = (Matrix) cellCol.start();
            catObj = mat;
        }
        else
        {
            for (int i = 0; i < numEl; i++)
            {
                Matrix mat = (Matrix) cellCol.getElementAt(i);
                // row += mat.getRowDimension();
                if (i == 0)
                {
                    first = mat.getColumnDimension();
                }
                else
                {
                    second = mat.getColumnDimension();
                    if (first != second)
                    {
                        throw new ConditionalException(
                                "vertcat : All matrix entries must have the same number of columns.");
                    }
                }
            }
            Matrix rowMat = null;// new Matrix(row, firstRowElement);
            Object[] rest = new Object[numEl - 1];
            for (int i = 0; i < numEl; i++)
            {
                Matrix mat = (Matrix) cellCol.getElementAt(i);
                if (i == 0)
                {
                    rowMat = mat;
                }
                else
                {
                    rest[i - 1] = mat;
                }
            }
            catObj = rowMat.vertcat(rest);
        }

        return catObj;
    }

    /*
     * This method is to be used only with 'vertcat' and 'horzcat'
     */
    private Indices nullMatrices()
    {
        boolean cond = cellOfObjects(Matrix.class);
        if (!cond)
        {
            throw new ConditionalException("nullMatrices : All entries must be objects of type \"Matrix\".");
        }

        Indices indNull = new Indices(m, n);
        indNull.makeLogical();

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Matrix mat = (Matrix) this.get(i, j);
                if (mat == null || mat.isNull())
                {
                    indNull.set(i, j, true);
                }
            }
        }

        return indNull;
    }

    public Matrix cell2Mat()
    {
        boolean cond = cellOfObjects(Matrix.class);
        if (!cond)
        {
            throw new ConditionalException("cell2Mat : All entries must be objects of type \"Matrix\".");
        }

        if (this.hasNull())
        {
            throw new ConditionalException("cell2Mat : Null entry elements are not allowed.");
        }

        if (this.numel() == 1)
        {
            return ((Matrix) this.start()).copy();
        }

        // Check number of rows are consistent
        int rows1 = 0;
        int rows = 0;
        int totCols = 0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Matrix matAt = (Matrix) this.A[i][j];
                if (j == 0)
                {
                    rows1 = matAt.getRowDimension();
                    totCols = matAt.getColumnDimension();
                }
                else
                {
                    rows = matAt.getRowDimension();
                    if (rows1 != rows)
                    {
                        throw new ConditionalException("cell2Mat : The number of rows at entry [" + i + " , " + j
                                + "] is " + rows + " which is not equal to " + rows1 + ".");
                    }
                    totCols += matAt.getColumnDimension();
                }
            }
        }

        // Check number of columns are consistent
        int cols1 = 0;
        int cols = 0;
        int totRows = 0;
        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                Matrix matAt = (Matrix) this.A[i][j];
                if (i == 0)
                {
                    cols1 = matAt.getColumnDimension();
                    totRows = matAt.getRowDimension();
                }
                else
                {
                    cols = matAt.getColumnDimension();
                    if (cols1 != cols)
                    {
                        throw new ConditionalException("cell2Mat : The number of columns at entry [" + i + " , " + j
                                + "] is " + cols + " which is not equal to " + cols1 + ".");
                    }
                    totRows += matAt.getRowDimension();
                }
            }
        }

        Matrix cellmat = null;// new Matrix(totRows, totCols);
        if (this.isRowVector())
        {
            Matrix first = (Matrix) this.A[0][0];
            Object[] rest = new Object[n - 1];
            for (int j = 1; j < n; j++)
            {
                rest[j - 1] = (Matrix) this.A[0][j];
            }
            cellmat = first.horzcat(rest);
        }
        else if (this.isColVector())
        {
            Matrix first = (Matrix) this.A[0][0];
            Object[] rest = new Object[m - 1];
            for (int i = 1; i < m; i++)
            {
                rest[i - 1] = (Matrix) this.A[i][0];
            }
            cellmat = first.vertcat(rest);
        }
        else
        {
            Matrix firstRow = null;
            Object[] restRows = new Object[m - 1];
            for (int i = 0; i < m; i++)
            {
                Matrix firstRowElement = (Matrix) this.A[i][0];
                Object[] restRowElement = new Object[n - 1];
                for (int j = 1; j < n; j++)
                {
                    restRowElement[j - 1] = (Matrix) this.A[i][j];
                }
                if (i == 0)
                {
                    firstRow = firstRowElement.horzcat(restRowElement);
                }
                else
                {
                    Matrix tmp = firstRowElement.horzcat(restRowElement);
                    restRows[i - 1] = tmp;
                }
            }
            cellmat = firstRow.vertcat(restRows);
        }

        return cellmat;
    }

    public void printCell()
    {
        printCell(null);
    }

    public void printCell(String name)
    {
        // final Object[][][] tab = new Object[this.dateNums.size()][][];

        int II = m;
        int JJ = n;

        String nameStr = "";
        if (name != null && !"".equals(name.trim()))
        {
            nameStr = " : " + name;
        }

        if ("".equals(nameStr))
        {
            System.out.println("Printing Table.\n\n");
        }
        else
        {
            System.out.println("Printing Table \"" + nameStr + "\".\n\n");
        }

        Object[][] table = genTable(II, JJ);// new
                                            // String[II][JJ];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Object obj = A[i][j];
                if (obj == null)
                {
                    continue;
                }
                table[i][j] = obj.toString();
            }
        }

        /*
         * table[0][0] = this.dateString.get(k) + nameStr; // for (int j = 0; j
         * < JJ; j++) // { table[1][0] = "\t"; for (int i = 2; i < II; i++) {
         * table[i][0] = this.itemCatalog.get(i - 2); } for (int j = 1; j < JJ;
         * j++) { table[1][j] = this.sourceVarNames.get(j - 1);//
         * this.varNames.get(j // - 1); }
         */

        /*
         * Matrix dayMat = this.values.get(k); for (int i = 2; i < II; i++) {
         * for (int j = 1; j < JJ; j++) { double val = dayMat.get(i - 2, j - 1);
         * if (displayIntegerVal) { table[i][j] = (int) val + ""; } else {
         * table[i][j] = val + ""; } } }
         */

        // }

        /*
         * table[0] = new String[] { "foo", "bar", "baz" }; table[1] = new
         * String[] { "bar2", "foo2", "baz2" }; table[2] = new String[] {
         * "baz3", "bar3", "foo3" }; table[3] = new String[] { "foo4", "bar4",
         * "baz4" };
         */

        StringBuilder builder = new StringBuilder();
        for (int v = 0; v < n; v++)
        {
            builder = builder.append("%35s");
        }
        String sep = builder.toString();

        for (final Object[] row : table)
        {
            System.out.format(sep + "\n", row);

        }
        System.out.println("\n");

    }

    static Object[][] genTable(int II, int JJ)
    {

        Object[][] table = new String[II][JJ];
        for (int i = 0; i < II; i++)
        {
            for (int j = 0; j < JJ; j++)
            {
                table[i][j] = "";
            }
        }
        return table;
    }

    /**
     * Matrix transpose.
     * 
     * @return A'
     */
    public Cell transpose()
    {
        Cell X = new Cell(n, m);
        Object[][] C = X.getArray();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                C[j][i] = A[i][j];
            }
        }
        return X;
    }

    public Cell toRowiseStringConcatenation()
    {
        return toRowiseStringConcatenation(null);
    }

    public Cell toRowiseStringConcatenation(String sep)
    {

        String sep2 = sep;
        if (sep2 == null)
        {
            sep2 = "";
        }
        int sep2Len = sep2.length();
        boolean sepNzLen = sep2Len > 0;

        Cell rowise = new Cell(m, 1);
        for (int i = 0; i < m; i++)
        {
            Cell cellred = this.getRowAt(i);
            // List<String> listStr = new ArrayList<String>();
            int countC = 0;
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < n; j++)
            {
                Object obj = cellred.getElementAt(j);
                if (obj == null)
                {
                    countC++;
                    continue;
                }
                builder.append(obj.toString() + sep2);
            }
            if (countC == n)
            {
                rowise.setElementAt(i, "");
            }
            else
            {
                String btos = builder.toString();
                int tlen = btos.length();
                if (sepNzLen)
                {
                    btos = btos.substring(0, tlen - sep2Len);
                }

                rowise.setElementAt(i, btos);
            }
            // map.put(i, listStr);
        }
        return rowise;
    }

    public Cell toColumwiseStringConcatenation()
    {
        return toColumwiseStringConcatenation(null);
    }

    public Cell toColumwiseStringConcatenation(String sep)
    {
        String sep2 = sep;
        if (sep2 == null)
        {
            sep2 = "";
        }
        int sep2Len = sep2.length();
        boolean sepNzLen = sep2Len > 0;

        Cell colwise = new Cell(1, n);
        for (int J = 0; J < n; J++)
        {
            Cell cellred = this.getColumnAt(J);
            // List<String> listStr = new ArrayList<String>();
            int countC = 0;
            StringBuilder builder = new StringBuilder();
            for (int I = 0; I < m; I++)
            {
                Object obj = cellred.getElementAt(I);
                if (obj == null)
                {
                    countC++;
                    continue;
                }
                builder.append(obj.toString() + sep2);
            }
            if (countC == m)
            {
                colwise.setElementAt(J, "");
            }
            else
            {
                String btos = builder.toString();
                int tlen = btos.length();
                if (sepNzLen)
                {
                    btos = btos.substring(0, tlen - sep2Len);
                }
                colwise.setElementAt(J, btos);
            }
            // map.put(i, listStr);
        }
        return colwise;
    }

    public Map<Integer, List<String>> toRowiseStringList()
    {
        Map<Integer, List<String>> map = new LinkedHashMap<Integer, List<String>>();
        for (int i = 0; i < m; i++)
        {
            List<String> listStr = new ArrayList<String>();
            for (int j = 0; j < n; j++)
            {
                Object obj = A[i][j];
                if (obj == null)
                {
                    continue;
                }
                listStr.add(obj.toString());
            }
            map.put(i, listStr);
        }
        return map;
    }

    public static List<String> prependWith(List<String> list, String preStr)
    {
        int num = list.size();
        List<String> replaced = new ArrayList<String>();
        for (int i = 0; i < num; i++)
        {
            String str = list.get(i);
            str = preStr + str;// .replace(oldStr, newStr);
            replaced.add(str);
        }
        return replaced;
    }

    public static List<String> postpendWith(List<String> list, String postStr)
    {
        int num = list.size();
        List<String> replaced = new ArrayList<String>();
        for (int i = 0; i < num; i++)
        {
            String str = list.get(i);
            str = str + postStr;// .replace(oldStr, newStr);
            replaced.add(str);
        }
        return replaced;
    }

    public static List<String> replaceWith(List<String> list, String oldStr)
    {
        return replaceWith(list, oldStr, "");
    }

    public static List<String> replaceWith(List<String> list, String oldStr, String newStr)
    {
        int num = list.size();
        List<String> replaced = new ArrayList<String>();
        for (int i = 0; i < num; i++)
        {
            String str = list.get(i);
            str = str.replace(oldStr, newStr);
            replaced.add(str);
        }
        return replaced;
    }

    public static Cell listToCell(List<?> list)
    {
        if (list == null || list.isEmpty())
        {
            return null;
        }
        int siz = list.size();
        Cell cL = new Cell(1, siz);

        for (int i = 0; i < siz; i++)
        {
            Object obj = list.get(i);
            cL.set(0, i, obj);
        }

        return cL;
    }

    public static Cell genCellSequence(String str, int num)
    {
        Cell cc = new Cell(1, num);
        for (int i = 1; i <= num; i++)
        {
            cc.set(0, i - 1, str + i);
        }
        return cc;
    }

    public static Map<String, Tuple<String, String>> permuteTwoListsToMap(List<String> strList,
            Tuple<List<String>, List<String>> tupleStrList)
    {
        Map<String, Tuple<String, String>> keyTupleMap = permuteTwoListsToMap(strList, tupleStrList, null);
        return keyTupleMap;
    }

    public static Map<String, Tuple<String, String>> permuteTwoListsToMap(String str,
            Tuple<List<String>, List<String>> tupleStrList)
    {
        Map<String, Tuple<String, String>> keyTupleMap = permuteTwoListsToMap(str, tupleStrList, null);
        return keyTupleMap;
    }

    public static Map<String, Tuple<String, String>> permuteTwoListsToMap(String str,
            Tuple<List<String>, List<String>> tupleStrList, String sep)
    {
        List<String> strList = new ArrayList<String>();
        strList.add(str);
        Map<String, Tuple<String, String>> keyTupleMap = permuteTwoListsToMap(strList, tupleStrList, sep);
        return keyTupleMap;
    }

    public static Map<String, Tuple<String, String>> permuteTwoListsToMap(List<String> strList,
            Tuple<List<String>, List<String>> tupleStrList, String sep)
    {

        if (strList == null || strList.isEmpty())
        {
            throw new IllegalArgumentException("First List input argument must be non-null or non-empty.");
        }

        // check tuple size first
        List<String> idList = tupleStrList.x;
        List<String> titleList = tupleStrList.y;
        if (idList.size() != titleList.size())
        {
            throw new IllegalArgumentException("The 2 \"List\" in the \"Tuple\" object must have the same size.");
        }

        if (sep == null)
        {
            sep = "_";
        }

        int nr = strList.size();
        int nc = idList.size();

        Map<String, Tuple<String, String>> keyTupleMap = new LinkedHashMap<String, Tuple<String, String>>();

        for (int i = 0; i < nr; i++)
        {
            String str1 = strList.get(i);
            for (int j = 0; j < nc; j++)
            {
                String str2 = idList.get(j);
                String key = str1 + sep + str2;
                String str3 = titleList.get(j);
                Tuple<String, String> value = new Tuple<String, String>(str2, str3);
                keyTupleMap.put(key, value);
            }
        }

        return keyTupleMap;
    }

    public static List<String> permuteTwoLists(List<String> strList1, List<String> strList, String sep)
    {
        if (strList1 == null || strList1.isEmpty())
        {
            throw new IllegalArgumentException("First List input argument must be non-null or non-empty.");
        }

        if (strList == null || strList.isEmpty())
        {
            // throw new
            // IllegalArgumentException("First List input argument must be
            // non-null or non-empty.");
            return strList1;// Cell.listToCell(strList1).toColVector();
        }
        if (sep == null)
        {
            sep = "_";
        }
        int nr = strList1.size();
        int nc = strList.size();

        List<String> stListAll = new ArrayList<String>();

        for (int i = 0; i < nr; i++)
        {
            String str1 = strList1.get(i);
            for (int j = 0; j < nc; j++)
            {
                String str2 = strList.get(j);
                stListAll.add(str1 + sep + str2);
            }
        }

        return stListAll;
    }

    public static List<String> permuteTwoLists(String str, List<String> strList)
    {
        return permuteTwoLists(str, strList, null);
    }

    public static List<String> permuteTwoLists(String str, List<String> strList, String sep)
    {
        int num = strList.size();
        // Cell cc = new Cell(1, num);
        if (str == null)
        {
            throw new IllegalArgumentException("First input argument must be non-null.");
        }
        if (sep == null)
        {
            sep = "_";
        }
        List<String> strAllList = new ArrayList<String>();
        for (int i = 0; i < num; i++)
        {
            // cc.set(0, i - 1, str + sep + strList.get(i));
            strAllList.add(str + sep + strList.get(i));
        }
        return strAllList;
    }

    public static Cell dateListFromTo(String day1, String day2)
    {
        return dateListFromTo(day1, day2, null);
    }

    public static Cell dateListFromTo(String day1, String day2, String DATE_FORMAT)
    {
        if (day1 == null || "".equals(day1.trim()))
        {
            throw new ConditionalRuleException("First \"String\" input argument must be non-null or non-empty.");
        }

        if (day2 == null || "".equals(day2.trim()))
        {
            throw new ConditionalRuleException("Second \"String\" input argument must be non-null or non-empty.");
        }

        if (DATE_FORMAT == null || "".equals(DATE_FORMAT.trim()))
        {
            DATE_FORMAT = "yyyy-MM-dd";
        }
        DateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");//
        // ("dd-M-yyyy");
        // String dateInString = "31-08-1982 10:20:56";
        // Date date = sdf.parse(dateInString);

        // String day1 = "2017-01-05";
        // String day2 = "2017-03-05";
        Date startDate = null;
        Date endDate = null;

        try
        {
            startDate = sdf.parse(day1);
            endDate = sdf.parse(day2);// new Date(day2);

        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (startDate.compareTo(endDate) == 0)
        {
            System.out.println("Same");
            return new Cell(1, 1, day1);
        }
        /*
         * else { System.out.println("Different"); }
         */

        // convert date to localdatetime
        LocalDateTime localDateTimeStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime localDateTimeEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        int cm = localDateTimeStartDate.compareTo(localDateTimeEndDate);
        System.out.println("Different");

        if (localDateTimeStartDate.isAfter(localDateTimeEndDate))
        {
            LocalDateTime tmp = localDateTimeStartDate;
            localDateTimeStartDate = localDateTimeEndDate;
            localDateTimeEndDate = tmp;
        }

        List<String> listDate = new ArrayList<String>();
        Date dateOne = Date.from(localDateTimeStartDate.atZone(ZoneId.systemDefault()).toInstant());
        listDate.add(sdf.format(dateOne));

        LocalDateTime dayIter = localDateTimeStartDate;

        while (dayIter.isBefore(localDateTimeEndDate))
        {
            dayIter = dayIter.plusDays(1);
            Date dateI = Date.from(dayIter.atZone(ZoneId.systemDefault()).toInstant());
            listDate.add(sdf.format(dateI));
        }

        return Cell.listToCell(listDate).toRowVector();
    }

    static void test1()
    {
        Matrix a00 = Matrix.ones(1);
        double[][] A12 =
        {
                {
                        2, 3, 4
                }
        };
        Matrix a01 = new Matrix(A12);
        double[][] A21 =
        {
                {
                        5
                },
                {
                        9
                }
        };
        Matrix a10 = new Matrix(A21);
        double[][] A22 =
        {
                {
                        6, 7, 8
                },
                {
                        10, 11, 12
                }
        };
        Matrix a11 = new Matrix(A22);

        Cell matCell = new Cell(2, 2);
        matCell.set(0, 0, a00);
        matCell.set(0, 1, a01);
        matCell.set(1, 0, a10);
        matCell.set(1, 1, a11);

        Matrix mat = matCell.cell2Mat();
        mat.printInLabel("mat", 0);
    }

    static void test2()
    {
        String day2 = "2017-01-05";
        String day1 = "2017-01-08";
        dateListFromTo(day1, day2);
    }

    public static void main(String[] args)
    {
        // test1();
        test2();

    }
}// ---------------------------- End Class Definition
 // ---------------------------
