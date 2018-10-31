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
public class Indices3D
{

    private Indices[] array;
    private boolean printJavaIndex = false;
    private boolean logical = false;
    private boolean findIndex = false;
    private boolean sorted = false;

    public Indices3D(Indices mat)
    {
        if (mat == null)
        {
            throw new ConditionalException("Indices3D : Indices parameter \"mat\" must be non-null.");
        }
        this.array = new Indices[]
        {
            mat
        };
    }

    public Indices3D(int dim1, int dim2, int dim3)
    {
        this(dim1, dim2, dim3, 0);
    }

    public Indices3D(int dim1, int dim2, int dim3, int inVal)
    {
        Indices ind = new Indices(new int[]
        {
                dim1, dim2, dim3
        });
        ind = ind.LT(1).find();
        if (ind != null)
        {
            throw new ConditionalException(
                    "Indices3D : All integer parameters \"dim1\", \"dim2\" and \"dim3\" must be positive.");
        }
        this.array = new Indices[dim3];
        for (int k = 0; k < dim3; k++)
        {
            this.array[k] = new Indices(dim1, dim2, inVal);
        }
    }

    public Indices[] getArray()
    {
        return this.array;
    }

    public Indices[] getArrayCopy()
    {
        return this.copy().getArray();
    }

    public void setPage(int pageInd, Indices mat)
    {
        int dim3 = this.array.length;
        if (pageInd >= dim3)
        {
            throw new ConditionalException("setPage : Integer parameter \"pageInd\" (= " + pageInd
                    + ") is out of bound.");
        }
        Indices first = this.array[0];
        Indices ind = first.sizeIndices().NEQ(mat.sizeIndices()).find();
        if (ind != null)
        {
            throw new ConditionalException("setPage : Matrix parameter \"mat\" must conform to size: ["
                    + mat.getRowDimension() + " x " + mat.getColumnDimension() + "].");
        }
        this.array[pageInd] = mat;
    }

    public Indices getPageCopy(int pageInd)
    {
        return getPage(pageInd).copy();
    }

    public Indices getPage(int pageInd)
    {
        int dim3 = this.array.length;
        if (pageInd >= dim3)
        {
            throw new ConditionalException("getPage : Integer parameter \"pageInd\" (= " + pageInd
                    + ") is out of bound.");
        }
        return this.array[pageInd];
    }

    public void setAllTo(int val)
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

    public void set(int i, int j, int k, int val)
    {
        Indices mat = this.array[0];
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

    /**
     * 
     * @param ind
     * @return
     */
    public Indices getElements(int[] ind)
    {
        int len = ind.length;
        Indices R = new Indices(len, 1);
        int val = 0;
        for (int i = 0; i < len; i++)
        {
            val = this.getElementAt(ind[i]);
            R.set(i, 0, val);
        }
        return R;
    }

    public int getElementAt(int ind)
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

        int val = this.array[page - 1].getElementAt(rem - 1);
        return val;
    }

    public int get(int i, int j, int k)
    {
        Indices mat = this.array[0];
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

    public boolean isNull()
    {
        return (this.array == null || this.array.length == 0);
    }

    public int start()
    {
        return array[0].start();
    }

    /**
     * 
     * @return
     */
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
                    if (this.array[u].get(i, j) != 0)
                    {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public static boolean isIntMaxMin(int num)
    {
        boolean b1 = num == Integer.MAX_VALUE;
        boolean b2 = num == Integer.MIN_VALUE;
        return (b1 || b2);
    }

    public FindInd3 findIJK()
    {
        ArrayList<int[]> objFind = findArrayListIJK();
        // {entryIJK, entryColInd, new Integer(m), new Integer(n)}
        // ArrayList<int[]> entryIJK = (ArrayList<int[]>) objFind[0];
        // ArrayList<Integer> entryColInd = (ArrayList<Integer>) objFind[1];
        FindInd3 find = FindInd3.create();
        if (objFind.isEmpty())
        {
            return find;
        }

        int siz = objFind.size();
        int[] index = new int[siz];
        Indices indices = new Indices(siz, 3);
        int[][] X = indices.getArray();
        int[] indVal = null;
        for (int i = 0; i < siz; i++)
        {
            indVal = objFind.get(i);
            X[i][0] = indVal[0];
            X[i][1] = indVal[1];
            X[i][2] = indVal[2];
            index[i] = indVal[3];
        }
        indices.setFindIndex(true);// .findIndex = true;

        int k = this.array.length;
        int n = this.array[0].getColumnDimension();
        int m = this.array[1].getRowDimension();

        find.setFindEntries(indices);
        find.setIndex(index);
        find.setFindSize(new int[]
        {
                m, n, k
        });

        return find;
    }

    private ArrayList<int[]> findArrayListIJK()
    {
        ArrayList<int[]> entryIJK = new ArrayList<int[]>();
        // ArrayList<Integer> entryColInd = new ArrayList<Integer>();
        int p = this.array.length;
        int n = this.array[0].getColumnDimension();
        int m = this.array[0].getRowDimension();

        int count = 0;
        int A = 0;
        for (int k = 0; k < p; k++)
        {
            for (int j = 0; j < n; j++)
            {
                for (int i = 0; i < m; i++)
                {
                    A = this.get(i, j, k);
                    if (!isIntMaxMin(A) && A != 0)
                    {
                        entryIJK.add(new int[]
                        {
                                i, j, k, count
                        });
                        // System.out.println("["+i+" , "+j+" , "+count+"]");
                        // entryColInd.add(new Integer(count++));
                    }
                    count++;
                }
            }
        }
        // findIndex = true;
        return entryIJK;
    }

    /**
     * 
     * @param logical
     */
    public void setLogical(boolean logical)
    {
        this.logical = logical;
    }

    public void makeLogical()
    {
        setLogical(true);

        int p = this.array.length;
        int n = this.array[0].getColumnDimension();
        int m = this.array[0].getRowDimension();
        int A = 0;

        for (int k = 0; k < p; k++)
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    A = this.get(i, j, k);
                    if (A != 0)
                    {
                        A = 1;
                    }
                    else
                    {
                        A = 0;
                    }
                    this.array[k].set(i, j, A);
                }
            }
        }
    }

    /**
     * 
     * @return
     */
    public Indices3D NOT()
    {
        if (this.logical == false)
        {
            this.makeLogical();
        }

        int p = this.array.length;
        int n = this.array[0].getColumnDimension();
        int m = this.array[0].getRowDimension();
        int A = 0;

        Indices3D X = new Indices3D(m, n, p);
        X.setLogical(true);
        Indices[] C = X.array;
        for (int k = 0; k < p; k++)
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (A == 0)
                    {
                        C[k].set(i, j, 1);// = 1;
                    }
                }// end for

            }
        }// end for

        return X;
    }// end method

    public int numel()
    {
        int k = this.array.length;
        Indices mat = this.array[0];
        int i = mat.getRowDimension();
        int j = mat.getColumnDimension();
        return (i * j * k);
    }

    public Indices toColVector()
    {
        int k = this.array.length;
        if (k == 1)
        {
            return this.array[0].toColVector();
        }
        Indices firstCol = this.array[0].toColVector();
        Indices[] list = new Indices[k];
        for (int i = 1; i < k; i++)
        {
            list[i] = this.array[i].toColVector();
        }
        Indices colVec = firstCol.mergeVerti(list);
        return colVec;
    }

    public Indices toRowVector()
    {
        int k = this.array.length;
        if (k == 1)
        {
            return this.array[0].toColVector();
        }
        Indices firstCol = this.array[0].toRowVector();
        Indices[] list = new Indices[k];
        for (int i = 1; i < k; i++)
        {
            list[i] = this.array[i].toRowVector();
        }
        Indices rowVec = firstCol.mergeHoriz(list);
        return rowVec;
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

    public Indices3D ANY()
    {
        return ANY(Dimension.ROW);
    }

    public Indices ANY3()
    {
        Indices3D pageInd = ANY(Dimension.PAGE);
        return pageInd.getPage(0);
    }

    public Indices3D ANY(Dimension Dim)
    {
        Indices mat = this.array[0];
        int i = mat.getRowDimension();
        int j = mat.getColumnDimension();
        int k = this.array.length;
        Indices3D ind3 = null;

        if (Dim == null)
        {
            Dim = Dimension.ROW;
        }

        if (Dim == Dimension.ROW)
        {
            ind3 = new Indices3D(1, j, k);
            for (int v = 0; v < k; v++)
            {
                Indices mat2 = this.array[v];
                mat2 = mat2.ANY(Dim);
                ind3.setPage(v, mat2);
            }
        }
        else if (Dim == Dimension.COL)
        {
            ind3 = new Indices3D(i, 1, k);
            for (int v = 0; v < k; v++)
            {
                Indices mat2 = this.array[v];
                mat2 = mat2.ANY(Dim);
                ind3.setPage(v, mat2);
            }
        }
        else
        {
            ind3 = new Indices3D(i, j, 1);
            int val = 0;
            for (int w = 0; w < i; w++)
            {
                for (int z = 0; z < j; z++)
                {
                    label: for (int v = 0; v < k; v++)
                    {
                        Indices mat2 = this.array[v];
                        val = mat2.get(w, z);
                        if (val != 0)
                        {
                            ind3.set(w, z, 0, 1);
                            break label;
                        }
                    }
                }
            }
        }

        return ind3;
    }

    /**
     * 
     * @return
     */
    public Indices3D ALL()
    {
        return ALL(Dimension.ROW);
    }

    /**
     * 
     * @param Dim
     * @return
     */
    public Indices3D ALL(Dimension Dim)
    {
        Indices mat = this.array[0];
        int i = mat.getRowDimension();
        int j = mat.getColumnDimension();
        int k = this.array.length;
        Indices3D ind3 = null;

        if (Dim == null)
        {
            Dim = Dimension.ROW;
        }

        if (Dim == Dimension.ROW)
        {
            ind3 = new Indices3D(1, j, k);
            for (int v = 0; v < k; v++)
            {
                Indices mat2 = this.array[v];
                mat2 = mat2.ALL(Dim);
                ind3.setPage(v, mat2);
            }
        }
        else if (Dim == Dimension.COL)
        {
            ind3 = new Indices3D(i, 1, k);
            for (int v = 0; v < k; v++)
            {
                Indices mat2 = this.array[v];
                mat2 = mat2.ALL(Dim);
                ind3.setPage(v, mat2);
            }
        }
        else
        {
            ind3 = new Indices3D(i, j, 1);
            int count = 0;
            int val = 0;

            for (int w = 0; w < i; w++)
            {
                for (int z = 0; z < j; z++)
                {
                    for (int v = 0; v < k; v++)
                    {
                        Indices mat2 = this.array[v];
                        // mat2 = mat2.ALL(Dim);
                        val = mat2.get(w, z);
                        if (val != 0)
                        {
                            count++;
                        }
                        else
                        {
                            continue;
                        }
                    }
                    if (count == k)
                    {
                        ind3.set(w, z, 0, 1);// allInd.set(0, j, 1);
                    }
                    count = 0;
                }
            }
        }

        return ind3;
    }

    /**
     * 
     * @return
     */
    public boolean anyBoolean()
    {

        Indices mat = this.array[0];
        int m = mat.getRowDimension();
        int n = mat.getColumnDimension();
        int k = this.array.length;

        for (int u = 0; u < k; u++)
        {
            Indices A = this.array[u];
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (A.get(i, j) != 0)
                    {
                        return true;
                    }
                    // else { countInd++; }
                }
            }
        }
        return false;

    }

    /**
     * Element-by-element multiplication, C = A.*B
     * 
     * @param B
     *            another matrix
     * @return A.*B
     */
    public Indices3D arrayTimes(Number B)
    {

        int m = this.getRowDimension();
        int n = this.getColDimension();
        int p = this.getPageDimension();
        Indices3D X = new Indices3D(m, n, p);
        for (int i = 0; i < p; i++)
        {
            Indices Amat = this.array[i];
            Indices Cmat = Amat.arrayTimes(B);// B.array[i];
            // Matrix Cmat = Amat.arrayTimes(Bmat);
            X.setPage(i, Cmat);
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
    public Indices3D arrayTimes(Indices3D B)
    {

        int m = this.getRowDimension();
        int n = this.getColDimension();
        int p = this.getPageDimension();

        Indices siz1 = new Indices(this.size());
        Indices siz2 = new Indices(B.size());
        boolean cond = siz1.EQ(siz2).trueAll();
        if (!cond)
        {
            throw new ConditionalException("arrayTimes : Array sizes of \"this\" and \"B\" must be the same.");
        }

        Indices3D X = new Indices3D(m, n, p);
        for (int i = 0; i < p; i++)
        {
            Indices Amat = this.array[i];
            Indices Bmat = B.getPage(i);
            Indices Cmat = Amat.arrayTimes(Bmat);// B.array[i];
            // Matrix Cmat = Amat.arrayTimes(Bmat);
            X.setPage(i, Cmat);
        }
        return X;
    }

    /**
     * 
     * @return
     */
    public boolean allBoolean()
    {

        Indices mat = this.array[0];
        int m = mat.getRowDimension();
        int n = mat.getColumnDimension();
        int k = this.array.length;

        for (int u = 0; u < k; u++)
        {
            Indices A = this.array[u];
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (A.get(i, j) == 0)
                    {
                        return false;
                    }
                    // else { countInd++; }
                }
            }
        }
        return true;

    }

    public int[] size()
    {
        Indices mat = this.array[0];
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
        Indices mat = this.array[0];
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

    public Indices3D copy()
    {

        Indices mat = this.array[0];
        int r = mat.getRowDimension();
        int c = mat.getColumnDimension();
        int k = this.array.length;
        Indices3D copy = new Indices3D(r, c, k);
        for (int i = 0; i < k; i++)
        {
            Indices deepCopy = this.array[i].copy();
            copy.setPage(i, deepCopy);
        }
        return copy;
    }

    public Indices3D permute(Dimension dim1, Dimension dim2, Dimension dim3)
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

        Indices mat = this.array[0];
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
        permDim = permDim.getElements(ind.getRowPackedCopy()).toRowVector();

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

        Indices3D perm = new Indices3D(iNew, jNew, kNew);
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

    private void perm132(Indices3D perm)
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
                    int val = this.get(i, j, k);
                    perm.set(i, k, j, val);
                }
            }
        }

    }

    private void perm213(Indices3D perm)
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
                    int val = this.get(i, j, k);
                    perm.set(j, i, k, val);
                }
            }
        }

    }

    private void perm231(Indices3D perm)
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
                    int val = this.get(i, j, k);
                    perm.set(j, k, i, val);
                }
            }
        }

    }

    private void perm321(Indices3D perm)
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
                    int val = this.get(i, j, k);
                    perm.set(k, j, i, val);
                }
            }
        }

    }

    private void perm312(Indices3D perm)
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
                    int val = this.get(i, j, k);
                    perm.set(k, i, j, val);
                }
            }
        }

    }

    /**
     * @param printJavaIndex
     *            the printJavaIndex to set
     */
    public void setPrintJavaIndex(boolean printJavaIndex)
    {
        this.printJavaIndex = printJavaIndex;
    }

    public void print3dArray(String varName, int w)
    {
        print3dArray(varName, w, 0);
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
            Indices Amat = this.array[i3];

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
    public static void printMatrix(Indices Amat, int w, int d, String nameSubArray)
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
    public static void printMatrix(Indices Amat, PrintWriter output, int w, int d, String nameSubArray)
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
    public static void printMatrix(Indices Amat, NumberFormat format, int width, String nameSubArray)
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
    public static void printMatrix(Indices Amat, PrintWriter output, NumberFormat format, int width, String nameSubArray)
    {
        int[][] A = Amat.getArray();
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

    public static Indices3D zeros(int dim1, int dim2, int dim3)
    {
        Indices3D Zr = new Indices3D(dim1, dim2, dim3);
        return Zr;
    }

    public static Indices3D ones(int dim1, int dim2, int dim3)
    {
        Indices3D Zr = new Indices3D(dim1, dim2, dim3, 1);
        return Zr;
    }

    static void exampleALL()
    {
        int[][] kk1 =
        {
                {
                        12, 9, 14
                },
                {
                        14, 1, 14
                },
                {
                        2, 4, 2
                },
                {
                        14, 8, 15
                }
        };
        Indices KK1 = new Indices(kk1);

        int[][] kk2 =
        {
                {
                        14, 6, 10
                },
                {
                        7, 14, 1
                },
                {
                        12, 12, 13
                },
                {
                        2, 14, 14
                }
        };
        Indices KK2 = new Indices(kk2);
        int i = KK1.getRowDimension();
        int j = KK1.getColumnDimension();

        Indices3D K = new Indices3D(i, j, 2);

        K.setPage(0, KK1);
        K.setPage(1, KK2);
        K.print3dArray("K", 6);

        K.ALL().print3dArray("K_ALL", 6);

        System.out.println("=========================================\n");

        Indices Page1 = null;
        Indices Page2 = null;
        /*
         * Indices3D K2 = K.copy(); Page1 = K2.getPage(0); Page1.setColumnAt(0,
         * 0); K2.print3dArray("K2", 6);
         * 
         * K2.ALL(Dimension.ROW).print3dArray("K2_ALL(ROW)", 6);
         * 
         * System.out.println("=========================================\n");
         */

        Indices3D K3 = K.copy();
        Page1 = K3.getPage(0);
        Page1.setRowAt(0, 0);
        K3.print3dArray("K3", 6);

        K3.ALL(Dimension.COL).print3dArray("K3_ALL(COL)", 6); // Dimension.COL

        System.out.println("=========================================\n");

        Indices3D K4 = K.copy();
        Page1 = K4.getPage(0);
        Page1.set(0, 0, 0);
        Page2 = K4.getPage(1);
        Page2.set(0, 0, 0);

        K4.print3dArray("K4", 6);

        K4.ALL(Dimension.PAGE).print3dArray("K4_ALL(PAGE)", 6);

    }

    static void exampleANY()
    {
        int[][] kk1 =
        {
                {
                        12, 9, 14
                },
                {
                        14, 1, 14
                },
                {
                        2, 4, 2
                },
                {
                        14, 8, 15
                }
        };
        Indices KK1 = new Indices(kk1);

        int[][] kk2 =
        {
                {
                        14, 6, 10
                },
                {
                        7, 14, 1
                },
                {
                        12, 12, 13
                },
                {
                        2, 14, 14
                }
        };
        Indices KK2 = new Indices(kk2);
        int i = KK1.getRowDimension();
        int j = KK1.getColumnDimension();

        Indices3D K = new Indices3D(i, j, 2);

        K.setPage(0, KK1);
        K.setPage(1, KK2);
        K.print3dArray("K", 6);

        K.ANY().print3dArray("K_ANY", 6);

        System.out.println("=========================================\n");

        Indices Page1 = null;
        Indices Page2 = null;
        /*
         * Indices3D K2 = K.copy(); Page1 = K2.getPage(0); Page1.setColumnAt(0,
         * 0); K2.print3dArray("K2", 6);
         * 
         * K2.ALL(Dimension.ROW).print3dArray("K2_ALL(ROW)", 6);
         * 
         * System.out.println("=========================================\n");
         */

        Indices3D K3 = K.copy();
        Page1 = K3.getPage(0);
        Page1.setRowAt(0, 0);
        K3.print3dArray("K3", 6);

        K3.ANY(Dimension.COL).print3dArray("K3_ANY(COL)", 6); // Dimension.COL

        System.out.println("=========================================\n");

        Indices3D K4 = K.copy();
        Page1 = K4.getPage(0);
        Page1.set(0, 0, 0);
        Page2 = K4.getPage(1);
        Page2.set(0, 0, 0);

        K4.print3dArray("K4", 6);

        K4.ANY(Dimension.PAGE).print3dArray("K4_ANY(PAGE)", 6);

    }

    public static void main(String[] arg)
    {

        exampleANY();

        /*
         * int[][] a1 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
         * 
         * int[][] a2 = {{0, 5, 4}, {2, 7, 6}, {9, 3, 1}};
         * 
         * Indices[] multiArr = new Indices[]{new Indices(a1), new Indices(a2)};
         * 
         * Indices3D MA = new Indices3D(3, 3, 2); MA.setPage(0, multiArr[0]);
         * MA.setPage(1, multiArr[1]); MA.print3dArray("MA", 6, 0);
         * 
         * System.out.println(" ");
         * 
         * Indices3D MA2 = MA.permute(Dimension.ROW, Dimension.COL,
         * Dimension.PAGE); MA2.print3dArray("MA2", 6, 0);
         */
    }
}
