/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.sparfun;

import java.util.ArrayList;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;

/**
 * 
 * @author Feynman Perceptrons
 */
public class SparseOld
{

    private int m = 0;
    private int n = 0;
    private ArrayList<Integer> rows;
    private ArrayList<Integer> cols;
    private ArrayList<Double> vals;
    private ArrayList<Integer> runningcol;
    private int length = 0;
    public static boolean printIndexOne = false;
    private boolean transposed = false;

    public SparseOld()
    {
        this(0, 0);
    }

    public SparseOld(Matrix A)
    {
        if (A == null)
        {
            throw new IllegalArgumentException("Sparse : Parameter \"A\" must be non-null.");
        }

        ArrayList<Integer> R = new ArrayList<Integer>();
        ArrayList<Integer> C = new ArrayList<Integer>();
        ArrayList<Double> V = new ArrayList<Double>();

        this.m = A.getRowDimension();
        this.n = A.getColumnDimension();
        double tmp = 0.0;

        for (int j = 0; j < n; j++)
        {
            for (int i = 0; i < m; i++)
            {
                tmp = A.get(i, j);
                if (tmp != 0.0)
                {
                    R.add(i);
                    C.add(j);
                    V.add(tmp);
                }
            }
        }// end outer for
        length = V.size();
        this.rows = R;
        this.cols = C;
        this.vals = V;
    }

    public SparseOld(int m, int n)
    {
        if (m <= 0)
        {
            throw new IllegalArgumentException("Sparse : Parameter \"m\" must be non-negative.");
        }
        if (n <= 0)
        {
            throw new IllegalArgumentException("Sparse : Parameter \"n\" must be non-negative.");
        }

        this.m = m;
        this.n = n;

        rows = new ArrayList<Integer>();
        cols = new ArrayList<Integer>();
        vals = new ArrayList<Double>();
        /*
         * int[] r = new int[length]; int[] c = new int[length];
         * 
         * for(int i=0; i<m; i++){ for(int j=0; j<n; j++){ } }
         */
    }

    public SparseOld transpose()
    {
        // int tmp = n;
        // this.n = m;
        // this.m = tmp;

        if (this.length == 0)
        {
            return new SparseOld(n, m);
        }

        ArrayList<Integer> colsCopy = new ArrayList<Integer>(rows);
        ArrayList<Integer> rowsCopy = new ArrayList<Integer>(cols);
        ArrayList<Double> valsCopy = new ArrayList<Double>(vals);

        /*
         * Matrix matVal = listDbl2Matrix(vals); Indices indRow =
         * listInt2Indices(cols); Indices indCol = listInt2Indices(rows);
         * indRow.plus(1).mergeV(indCol.plus(1)).printInLabel("transpose");
         * QuickSort sort = new QuickSortInd(indCol, true, true); indCol =
         * (Indices)sort.getSortedObject();
         * indCol.plus(1).printInLabel("transpose Col"); Indices indCol2 =
         * sort.getIndices(); int[] arr = indCol2.getRowPackedCopy(); indRow =
         * indRow.getColumns(arr); matVal = matVal.getColumns(arr);
         * 
         * int len = arr.length; ArrayList<Integer> colsCopy = new
         * ArrayList<Integer>();
         * 
         * ArrayList<Integer> rowsCopy = new ArrayList<Integer>();
         * ArrayList<Double> valsCopy = new ArrayList<Double>(); for (int i = 0;
         * i < len; i++) { colsCopy.add(indCol.get(0, i));
         * rowsCopy.add(indRow.get(0, i)); valsCopy.add(matVal.get(0, i)); }
         */

        SparseOld SP = new SparseOld(n, m);
        SP.cols = colsCopy;
        SP.rows = rowsCopy;
        SP.vals = valsCopy;
        SP.length = colsCopy.size();
        SP.transposed = true;

        return SP;
    }

    private static Matrix listDbl2Matrix(ArrayList<Double> list)
    {
        int siz = list.size();
        Matrix ind = new Matrix(1, siz);
        for (int i = 0; i < siz; i++)
        {
            double num = list.get(i);
            ind.set(0, i, num);
        }
        return ind;
    }

    private static Indices listInt2Indices(ArrayList<Integer> list)
    {
        int siz = list.size();
        Indices ind = new Indices(1, siz);
        for (int i = 0; i < siz; i++)
        {
            int num = list.get(i);
            ind.set(0, i, num);
        }
        return ind;
    }

    public Matrix full()
    {
        int len = this.length;
        Matrix mat = new Matrix(m, n);
        if (len == 0)
        {
            return mat;
        }
        int i = 0;
        int j = 0;
        for (int k = 0; k < len; k++)
        {
            i = rows.get(k);
            j = cols.get(k);
            mat.set(i, j, vals.get(k));
        }
        return mat;
    }

    public SparseOld add(SparseOld B)
    {
        return null;
    }

    public void print(String str)
    {

        if (this.transposed)
        {
            Matrix full = this.full();
            SparseOld SP = new SparseOld(full);
            StringBuilder sb = new StringBuilder();
            System.out.println("---------- " + str + " ----------");
            if (SP.length == 0)
            {
                System.out.println("\tEmpty");
            }
            else
            {
                if (printIndexOne)
                {
                    for (int i = 0; i < SP.length; i++)
                    {
                        int II = SP.rows.get(i) + 1;
                        int JJ = SP.cols.get(i) + 1;
                        double VV = SP.vals.get(i);
                        sb = sb.append("(").append(II).append(",").append(JJ).append(")\t\t").append(VV).append("\n");
                    }
                }
                else
                {
                    for (int i = 0; i < SP.length; i++)
                    {
                        int II = SP.rows.get(i);
                        int JJ = SP.cols.get(i);
                        double VV = SP.vals.get(i);
                        sb = sb.append("(").append(II).append(",").append(JJ).append(")\t\t").append(VV).append("\n");
                    }
                }
                System.out.println(sb.toString() + "\n");
            }

            return;
        }

        StringBuilder sb = new StringBuilder();
        System.out.println("---------- " + str + " ----------");
        if (this.length == 0)
        {
            System.out.println("\tEmpty");
        }
        else
        {
            if (printIndexOne)
            {
                for (int i = 0; i < length; i++)
                {
                    int II = this.rows.get(i) + 1;
                    int JJ = this.cols.get(i) + 1;
                    double VV = this.vals.get(i);
                    sb = sb.append("(").append(II).append(",").append(JJ).append(")\t\t").append(VV).append("\n");
                }
            }
            else
            {
                for (int i = 0; i < length; i++)
                {
                    int II = this.rows.get(i);
                    int JJ = this.cols.get(i);
                    double VV = this.vals.get(i);
                    sb = sb.append("(").append(II).append(",").append(JJ).append(")\t\t").append(VV).append("\n");
                }
            }
            System.out.println(sb.toString() + "\n");
        }
    }

    public void set(int i, int j, double v)
    {
        boolean cond = (i >= 0) && (i < m);
        if (!cond)
        {
            throw new ConditionalException("set : Index parameter \"i\" (= " + i
                    + ") is out of bound, should be less than \"m\" (= " + m + ")");
        }
        cond = (j >= 0) && (j < n);
        if (!cond)
        {
            throw new ConditionalException("set : Index parameter \"j\" (= " + j
                    + ") is out of bound, should be less than \"n\" (= " + n + ")");
        }
        if (v == 0.0)
        {
            return;
        }
    }

    public double get(int i, int j)
    {
        boolean cond = (i >= 0) && (i < m);
        if (!cond)
        {
            throw new ConditionalException("get : Index parameter \"i\" (= " + i
                    + ") is out of bound, should be less than \"m\" (= " + m + ")");
        }
        cond = (j >= 0) && (j < n);
        if (!cond)
        {
            throw new ConditionalException("get : Index parameter \"j\" (= " + j
                    + ") is out of bound, should be less than \"n\" (= " + n + ")");
        }
        if (length == 0.0)
        {
            return 0.0;
        }

        double val2 = 0.0;
        int k = 0;
        for (k = 0; k < length; k++)
        {
            int II = rows.get(k);
            int JJ = cols.get(k);
            cond = (II == i) && (JJ == j);
            if (cond)
            {
                break;
            }
        }

        return vals.get(k);
    }

    public static void main(String[] args)
    {

        double[][] cc =
        {
                {
                        4.5, 0.0, 3.2, 0.0
                },
                {
                        3.1, 2.9, 0.0, 0.9
                },
                {
                        0.0, 1.7, 3.0, 0.0
                },
                {
                        3.5, 0.4, 0.0, 1.0
                }
        };

        Matrix C = new Matrix(cc);
        C.printInLabel("Full C", 1);

        SparseOld SP = new SparseOld(C);
        SP.setPrintIndexOne(true);
        SP.print("SP");

        SparseOld SPtrans = SP.transpose();
        // SPtrans.setPrintIndexOne(true);
        SPtrans.print("SPtrans");
        // Matrix fullMat = SPtrans.full();
        // fullMat.printInLabel("SPtrans_Full", 1);
    }

    /**
     * @return the printIndexOne
     */
    public boolean isPrintIndexOne()
    {
        return printIndexOne;
    }

    /**
     * @param printIndexOne
     *            the printIndexOne to set
     */
    public void setPrintIndexOne(boolean printIndexOne)
    {
        this.printIndexOne = printIndexOne;
    }
}
