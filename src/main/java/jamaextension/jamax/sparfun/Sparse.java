/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.sparfun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortInd;

/**
 * 
 * Matrix in compressed-column or triplet form.
 * 
 */
public class Sparse
{

    /**
     * maximum number of entries
     */
    public int nzmax;
    /**
     * number of rows
     */
    public int m;
    /**
     * number of columns
     */
    public int n;
    /**
     * column pointers (size n+1) or row indices (size nzmax)
     */
    public int[] p;
    /**
     * row indices, size nzmax
     */
    public int[] i;
    /**
     * numerical values, size nzmax
     */
    public double[] x;
    /**
     * # of entries in triplet matrix, -1 for compressed-row
     */
    public int nz;

    public Sparse()
    {
    }

    public Sparse(Object obj)
    {
        if (obj == null)
        {
        }
        boolean cond = !(obj instanceof Matrix) && !(obj instanceof Indices);
        if (cond)
        {
            throw new ConditionalException(
                    "Sparse : Parameter \"obj\" must be an instance of \"Matrix\" or \"Indices\".");
        }

        Matrix mat = null;
        if (obj instanceof Matrix)
        {
            mat = (Matrix) obj;
        }
        else
        {
            mat = Matrix.indicesToMatrix((Indices) obj);
        }

        FindInd find = mat.NEQ(0.0).findIJ();
        i = find.getArrayI();
        p = find.getArrayJ();
        nzmax = p.length;
        nz = p.length;
        m = mat.getRowDimension();
        n = mat.getColumnDimension();
        x = new double[nz];
        for (int u = 0; u < nz; u++)
        {
            x[u] = mat.get(i[u], p[u]);
        }
    }

    public Matrix full()
    {
        if (m == 0 || n == 0)
        {
            return new Matrix();
        }
        Matrix fullMat = new Matrix(m, n);

        if (nz == 0)
        {
            return fullMat;
        }

        new Indices(i).printInLabel("I");
        new Indices(p).printInLabel("J");
        new Matrix(x).printInLabel("X", 0);

        for (int u = 0; u < nz; u++)
        {
            fullMat.set(i[u], p[u], x[u]);
        }
        return fullMat;
    }

    // private static int[]
    public Sparse copy()
    {
        Sparse C = new Sparse();
        // i,p,x
        C.m = m;
        C.n = n;
        C.nz = nz;
        C.nzmax = nzmax;
        int len = i.length;
        int[] newI = new int[len];
        System.arraycopy(i, 0, newI, 0, len);
        C.i = newI;
        int[] newP = new int[len];
        System.arraycopy(p, 0, newP, 0, len);
        C.p = newP;
        double[] newX = new double[len];
        System.arraycopy(x, 0, newX, 0, len);
        C.x = newX;

        return C;
    }

    public Sparse add(Sparse B, double alpha, double beta)
    {
        Sparse A = this;
        Sparse C = Dcs_add.cs_add(A, B, alpha, beta);
        return C;
    }

    public Matrix add(Matrix mat)
    {
        if (mat.isNull())
        {
            return full();
        }
        if (this.m != mat.getRowDimension() || this.n != mat.getColumnDimension())
        {
            throw new ConditionalException("add : Mis-matched dimensions.");
        }
        Matrix mat2 = full().plus(mat);
        return mat2;
    }

    public Matrix add(Indices mat)
    {
        if (mat.isNull())
        {
            return full();
        }
        if (this.m != mat.getRowDimension() || this.n != mat.getColumnDimension())
        {
            throw new ConditionalException("add : Mis-matched dimensions.");
        }
        Matrix mat2 = full().plus(mat);
        return mat2;
    }

    public Sparse add(Number num)
    {
        Sparse copy = copy();
        double val = (Double) num;
        for (int k = 0; k < nz; k++)
        {
            copy.x[k] += val;
        }
        return copy;
    }

    public Sparse add(Sparse B)
    {
        if (m != B.m || n != B.n)
        {
            throw new ConditionalException("add : Dimension of input parameter \"B\" must agree.");
        }

        if (B.nz == 0)
        {
            return copy();
        }
        if (nz == 0)
        {
            return B.copy();
        }

        Sparse C = new Sparse();

        int[] pos1 = rowIndex(i, p, m, n);// {1, 4, 5, 8, 9, 10, 12, 13, 15, 18,
                                          // 19, 20, 21, 23, 24, 25, 26, 27,
                                          // 29};
        new Indices(pos1).printInLabel("pos1");
        // Set<Integer> pos1SetIntersect = toSet(pos1);
        Set<Integer> pos1SetUnion = toSet(pos1);
        int len = pos1.length;
        int[] pos1Ind = Indices.linspace(0, len - 1).getRowPackedCopy();
        HashMap<Integer, Integer> hm1 = new HashMap<Integer, Integer>();
        for (int v = 0; v < len; v++)
        {
            hm1.put(pos1[v], pos1Ind[v]);
        }

        int[] pos2 = rowIndex(B.i, B.p, B.m, B.n);// {1, 3, 4, 5, 6, 7, 11, 13,
                                                  // 15, 19, 22, 23, 24, 25, 27,
                                                  // 28, 30};
        new Indices(pos2).printInLabel("pos2");
        // Set<Integer> pos2SetIntersect = toSet(pos2);
        Set<Integer> pos2SetUnion = toSet(pos2);
        int len2 = pos2.length;
        int[] pos2Ind = Indices.linspace(0, len2 - 1).getRowPackedCopy();
        HashMap<Integer, Integer> hm2 = new HashMap<Integer, Integer>();
        for (int v = 0; v < len2; v++)
        {
            hm2.put(pos2[v], pos2Ind[v]);
        }
        // HashMap hm1 = null;

        boolean un = pos1SetUnion.addAll(pos2SetUnion);
        // boolean intersect = pos1SetIntersect.retainAll(pos2SetIntersect);
        Indices union = toIndices(pos1SetUnion);
        union.printInLabel("union");

        Object[] setObj = pos1SetUnion.toArray();
        len = setObj.length;
        ArrayList<double[]> result = new ArrayList<double[]>();

        double newI = 0;
        double newP = 0;
        double newX = 0.0;

        for (int u = 0; u < len; u++)
        {
            Integer key = (Integer) setObj[u];
            Integer posInd1 = hm1.get(key);
            Integer posInd2 = hm2.get(key);

            if (posInd1 == null && posInd2 != null)
            {
                newI = B.i[posInd2];
                newP = B.p[posInd2];
                newX = B.x[posInd2];
            }
            else if (posInd1 != null && posInd2 == null)
            {
                newI = i[posInd1];
                newP = p[posInd1];
                newX = x[posInd1];
            }
            else
            {
                newI = B.i[posInd2];
                newP = B.p[posInd2];
                newX = x[posInd1] + B.x[posInd2];
            }

            if (newX != 0.0)
            {
                result.add(new double[]
                {
                        newI, newP, newX
                });
            }
        }

        int siz = result.size();
        int[] II = new int[siz];
        int[] PP = new int[siz];
        double[] XX = new double[siz];

        for (int k = 0; k < siz; k++)
        {
            double[] res = result.get(k);
            II[k] = (int) res[0];
            PP[k] = (int) res[1];
            XX[k] = (double) res[2];
        }

        C.i = II;
        C.p = PP;
        C.x = XX;
        C.m = m;
        C.n = n;
        C.nz = siz;
        C.nzmax = siz;

        return C;
    }

    public Matrix addOld(Object obj)
    {
        if (obj == null)
        {
            return full();
        }
        Matrix mat = null;
        if (obj instanceof Matrix)
        {
            mat = (Matrix) obj;
            if (mat.isNull())
            {
                return full();
            }
            if (this.m != mat.getRowDimension() || this.n != mat.getColumnDimension())
            {
                throw new ConditionalException("add : Mis-matched dimensions.");
            }
            mat = full().plus(mat);
        }
        else if (obj instanceof Indices)
        {
            Indices matInd = (Indices) obj;
            if (matInd.isNull())
            {
                return full();
            }
            if (this.m != matInd.getRowDimension() || this.n != matInd.getColumnDimension())
            {
                throw new ConditionalException("add : Mis-matched dimensions.");
            }
            mat = full().plus(matInd);
        }
        else if (obj instanceof Number)
        {
            double num = (Double) obj;
            mat = full().plus(num);
        }
        else
        {
            throw new ConditionalException(
                    "add : Parameter \"obj\" must be an instance of \"Matrix\", \"Indices\" or \"Number\".");
        }
        return mat;
    }

    public Sparse transpose()
    {
        Sparse C = new Sparse();
        C.n = m;
        C.m = n;
        C.nz = nz;
        C.nzmax = nzmax;

        int[] pos = rowIndex(p, i, n, m);
        Indices indPos = new Indices(pos);

        // Indices I = new Indices(S.i);
        // Indices P = new Indices(S.p);
        // Matrix X = new Matrix(S.x);

        QuickSort sort = new QuickSortInd(indPos, true, false);
        // indPos = (Indices) sort.getSortedObject();
        Indices Ind = sort.getIndices();
        int[] reorder = Ind.getRowPackedCopy();

        Indices I = new Indices(p);
        I = I.getEls(reorder);
        Indices P = new Indices(i);
        P = P.getEls(reorder);
        Matrix X = new Matrix(x);
        X = X.getEls(reorder);

        C.i = I.getRowPackedCopy();
        C.p = P.getRowPackedCopy();
        C.x = X.getRowPackedCopy();

        return C;
    }

    public Sparse transpose(boolean values)
    {
        Sparse A = this;
        Sparse Atrans = Dcs_transpose.cs_transpose(A, values);
        return Atrans;
    }

    public Sparse uminus()
    {
        Sparse copy = copy();
        int len = copy.x.length;
        for (int k = 0; k < len; k++)
        {
            copy.x[k] = -copy.x[k];
        }
        return copy;
    }

    public Sparse scale(Double num)
    {
        Sparse copy = copy();
        if (num == 0.0)
        {
            return copy;
        }
        int len = copy.x.length;
        for (int k = 0; k < len; k++)
        {
            copy.x[k] = num * copy.x[k];
        }
        return copy;
    }

    public Sparse times(Sparse B)
    {
        Sparse A = this;
        Sparse C = Dcs_multiply.cs_multiply(A, B);
        return C;
    }

    public Matrix times(Matrix B)
    {
        Matrix A = this.full();
        Matrix C = A.times(B);
        return C;
    }

    static Set<Integer> toSet(int[] arr)
    {
        Set<Integer> set = new TreeSet<Integer>();
        int len = arr.length;
        for (int v = 0; v < len; v++)
        {
            int d = arr[v];
            set.add(d);
        }
        return set;
    }

    static Indices toIndices(Set<Integer> set)
    {
        int len = set.size();
        Object[] indObj = set.toArray();
        Indices ind = new Indices(1, len);
        for (int i = 0; i < len; i++)
        {
            ind.set(0, i, (Integer) indObj[i]);
        }
        return ind;
    }

    public Sparse arrayTimes(Object B)
    {
        if (B == null)
        {
            throw new ConditionalException("arrayTimes : Input parameter \"B\" must be non-null.");
        }

        boolean isMatrix = (B instanceof Matrix);
        boolean isSparse = (B instanceof Sparse);
        boolean cond = !isMatrix && !(B instanceof Sparse) && !(B instanceof Indices) && !(B instanceof Number);
        if (cond)
        {
            throw new ConditionalException(
                    "arrayTimes : Input parameter \"B\" must be a \"Matrix\", \"Sparse\", \"Number\" or \"Indices\".");
        }

        Matrix Bmat = null;
        Sparse Bspa = null;
        Double num = null;
        if (isMatrix)
        {
            Bmat = (Matrix) B;
            if (m != Bmat.getRowDimension() || n != Bmat.getColumnDimension())
            {
                throw new ConditionalException("arrayTimes : Dimension of input parameter Matrix \"B\" must agree.");
            }
        }
        else if (isSparse)
        {
            Bspa = (Sparse) B;
            if (m != Bspa.m || n != Bspa.n)
            {
                throw new ConditionalException("arrayTimes : Dimension of input parameter \"B\" must agree.");
            }
        }
        else if (B instanceof Number)
        {
            num = (Double) B;
            if (num != 0.0)
            {
                return scale(num);
            }
            else
            {
                Bspa = new Sparse();
                Bspa.m = m;
                Bspa.n = n;
                return Bspa;
            }
        }
        else
        {
            // throw new
            // ConditionalException("arrayTimes : \"Indices\" not implemented yet.");
            Bmat = Matrix.indicesToMatrix((Indices) B);
            if (m != Bmat.getRowDimension() || n != Bmat.getColumnDimension())
            {
                throw new ConditionalException("arrayTimes : Dimension of input parameter Indices \"B\" must agree.");
            }
        }

        Sparse C = new Sparse();
        if (nz == 0 || (isSparse && Bspa.nz == 0))
        {
            C.m = m;
            C.n = n;
            return C;
        }

        int[] pos1 = rowIndex(i, p, m, n);// {1, 4, 5, 8, 9, 10, 12, 13, 15, 18,
                                          // 19, 20, 21, 23, 24, 25, 26, 27,
                                          // 29};
        // new Indices(pos1).printInLabel("pos1");
        Set<Integer> pos1Set = toSet(pos1);
        int len = pos1.length;
        int[] pos1Ind = Indices.linspace(0, len - 1).getRowPackedCopy();
        HashMap<Integer, Integer> hm1 = new HashMap<Integer, Integer>();
        for (int v = 0; v < len; v++)
        {
            hm1.put(pos1[v], pos1Ind[v]);
        }

        int[] pos2 = rowIndex(Bspa.i, Bspa.p, Bspa.m, Bspa.n);// {1, 3, 4, 5, 6,
                                                              // 7, 11, 13, 15,
                                                              // 19, 22, 23, 24,
                                                              // 25, 27, 28,
                                                              // 30};
        // new Indices(pos2).printInLabel("pos2");
        Set<Integer> pos2Set = toSet(pos2);
        int len2 = pos2.length;
        int[] pos2Ind = Indices.linspace(0, len2 - 1).getRowPackedCopy();
        HashMap<Integer, Integer> hm2 = new HashMap<Integer, Integer>();
        for (int v = 0; v < len2; v++)
        {
            hm2.put(pos2[v], pos2Ind[v]);
        }
        // HashMap hm1 = null;

        boolean intersect = pos1Set.retainAll(pos2Set);
        // Indices inter = toIndices(pos1Set);
        // inter.printInLabel("intersection");

        Object[] setObj = pos1Set.toArray();
        len = setObj.length;

        ArrayList<double[]> result = new ArrayList<double[]>();

        for (int v = 0; v < len; v++)
        {
            Integer key = (Integer) setObj[v];

            // Map1
            Integer val = hm1.get(key);
            Integer val2 = hm2.get(key);
            double newX = 0.0;
            if (isMatrix)
            {
                newX = x[val] * Bmat.getElementAt(key);
            }
            else if (isSparse)
            {
                newX = x[val] * Bspa.x[val2];
            }
            else
            {
                throw new ConditionalException(
                        "arrayTimes : Only objects of type \"Number\", \"Matrix\" and \"Sparse\" are allowed.");
            }
            double newI = i[val];
            double newP = p[val];

            if (newX != 0.0)
            {
                result.add(new double[]
                {
                        newI, newP, newX
                });
            }
            // Map2
        }

        int siz = result.size();
        int[] II = new int[siz];
        int[] PP = new int[siz];
        double[] XX = new double[siz];

        for (int k = 0; k < siz; k++)
        {
            double[] res = result.get(k);
            II[k] = (int) res[0];
            PP[k] = (int) res[1];
            XX[k] = (double) res[2];
        }

        C.i = II;
        C.p = PP;
        C.x = XX;
        C.m = m;
        C.n = n;
        C.nz = siz;
        C.nzmax = siz;

        return C;
    }

    static int[] rowIndex(int[] rows, int[] cols, int m, int n)
    {
        int len = rows.length;
        if (len != cols.length)
        {
            throw new ConditionalException("rowIndex : Length of 2 arrays \"rows\" and \"cols\" must equal.");
        }

        int[] rowInd = new int[len];
        int val = 0;

        for (int u = 0; u < len; u++)
        {
            val = rows[u] + cols[u] * m;
            rowInd[u] = val;
        }

        return rowInd;
    }

    static Set<Integer> sum(int[] a, int[] b, int nz)
    {
        // int[] c = null;
        int len = a.length;
        if (len != b.length)
        {
            throw new ConditionalException("sum : Length of 2 arrays \"A\" and \"B\" must equal.");
        }

        // int[] c = new int[nz];
        Set<Integer> c = new HashSet<Integer>();
        for (int v = 0; v < nz; v++)
        {
            int d = a[v] + b[v];
            c.add(d);
        }

        return c;
    }

    private static Sparse sumCols(Sparse S)
    {
        Sparse C = new Sparse();
        if (S.nz == 0)
        {
            C.m = S.m;
            C.n = S.n;
            return C;
        }

        if (S.nz == 1)
        {
            C.nz = 1;
            C.nzmax = 1;
            C.m = S.m;
            C.n = S.n;
            C.i = S.i;
            C.p = S.p;
            C.x = S.x;
            return C;
        }

        Indices I = new Indices(S.i);
        Indices P = new Indices(S.p);
        Matrix X = new Matrix(S.x);

        QuickSort sort = new QuickSortInd(I, true, false);
        I = (Indices) sort.getSortedObject();
        Indices Ind = sort.getIndices();
        int[] reorder = Ind.getRowPackedCopy();

        // P = P.getEls(reorder);
        X = X.getEls(reorder);

        ArrayList<Integer> row = new ArrayList<Integer>();
        ArrayList<double[]> collect = new ArrayList<double[]>();
        row.add(I.start());// (S.p[0]);
        double val = X.start();// S.x[0];
        for (int u = 1; u < S.nz; u++)
        {
            // if (row.contains(S.p[u])) {
            if (row.contains(I.getElementAt(u)))
            {
                val += X.getElementAt(u);// S.x[u];
            }
            else
            {
                collect.add(new double[]
                {
                        row.get(0), 0, val
                });
                row.clear();
                row.add(I.getElementAt(u));// (S.p[u]);
                val = X.getElementAt(u);// S.x[u];
            }
        }
        collect.add(new double[]
        {
                row.get(0), 0, val
        });

        int siz = collect.size();
        int[] newI = new int[siz];
        int[] newP = new int[siz];
        double[] newX = new double[siz];

        for (int u = 0; u < siz; u++)
        {
            double[] CC = collect.get(u);
            newI[u] = (int) CC[0];
            newP[u] = (int) CC[1];
            newX[u] = CC[2];
        }

        C.nz = siz;
        C.nzmax = siz;
        C.m = S.m;
        C.n = 1;
        C.i = newI;
        C.p = newP;
        C.x = newX;

        return C;
    }

    private static Sparse sumRows(Sparse S)
    {
        Sparse C = new Sparse();
        if (S.nz == 0)
        {
            C.m = S.m;
            C.n = S.n;
            return C;
        }

        if (S.nz == 1)
        {
            C.nz = 1;
            C.nzmax = 1;
            C.m = S.m;
            C.n = S.n;
            C.i = S.i;
            C.p = S.p;
            C.x = S.x;
            return C;
        }

        ArrayList<Integer> col = new ArrayList<Integer>();
        ArrayList<double[]> collect = new ArrayList<double[]>();
        col.add(S.p[0]);
        double val = S.x[0];
        for (int u = 1; u < S.nz; u++)
        {
            if (col.contains(S.p[u]))
            {
                val += S.x[u];
            }
            else
            {
                collect.add(new double[]
                {
                        0, col.get(0), val
                });
                col.clear();
                col.add(S.p[u]);
                val = S.x[u];
            }
        }
        collect.add(new double[]
        {
                0, col.get(0), val
        });

        int siz = collect.size();
        int[] newI = new int[siz];
        int[] newP = new int[siz];
        double[] newX = new double[siz];

        for (int u = 0; u < siz; u++)
        {
            double[] CC = collect.get(u);
            newI[u] = (int) CC[0];
            newP[u] = (int) CC[1];
            newX[u] = CC[2];
        }

        C.nz = siz;
        C.nzmax = siz;
        C.m = 1;
        C.n = S.n;
        C.i = newI;
        C.p = newP;
        C.x = newX;

        return C;
    }

    public Sparse sum()
    {
        return sum(null);
    }

    public Sparse sum(Dimension dim)
    {
        Sparse C = null;
        Dimension D = dim;
        if (D == null)
        {
            D = Dimension.ROW;
        }

        if (D == Dimension.ROW)
        {
            C = sumRows(this);
        }
        else
        {
            C = sumCols(this);
        }
        return C;
    }

    public double sumAll()
    {
        double sumAll = 0.0;
        if (nz == 0)
        {
            return 0.0;
        }
        Matrix mat = new Matrix(x);
        sumAll = mat.sumAll();
        return sumAll;
    }

    public static Sparse matrixSparseFormat(Matrix mat)
    {
        int rows = mat.getRowDimension();
        int cols = mat.getColumnDimension();
        if (cols != 3)
        {
            throw new ConditionalException("matrixSparseFormat : Must be 3 columns for sparse data format.");
        }
        Sparse C = new Sparse();
        C.m = (int) mat.get(0, 0);
        C.n = (int) mat.get(0, 1);
        C.nz = (int) mat.get(0, 2);
        C.nzmax = C.nz;

        int len = C.nz;

        int[] newI = new int[len];
        int[] newP = new int[len];
        double[] newX = new double[len];

        for (int u = 1; u < rows; u++)
        {
            newI[u] = (int) mat.get(u, 0);
            newP[u] = (int) mat.get(u, 1);
            newX[u] = mat.get(u, 2);
        }

        C.i = newI;
        C.p = newP;
        C.x = newX;

        return C;
    }

    public Sparse multiply(Sparse B)
    {
        Sparse A = this;
        Sparse C = Dcs_multiply.cs_multiply(A, B);
        return C;
    }

    public void print()
    {
        print(false);
    }

    public void print(boolean tf)
    {
        Dcs_print.cs_print(this, tf);
    }

    public static Sparse load(String fullFilePath)
    {
        Sparse A = Dcs_load.cs_load(fullFilePath);
        return A;
    }
}
