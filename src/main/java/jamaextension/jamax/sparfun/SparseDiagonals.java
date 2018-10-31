/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.sparfun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortInd;

/**
 * 
 * @author Feynman Perceptrons
 */
public class SparseDiagonals
{

    private Matrix firstOutput;
    private Indices secondOutput;

    public SparseDiagonals(Matrix A)
    {
        this(A, null);
    }

    public SparseDiagonals(Matrix A, Indices d)
    {
        if (A == null)
        {
            throw new IllegalArgumentException("SparseDiagonals : Parameter \"A\" must be non-null.");
        }

        if (A.isVector())
        {
            throw new IllegalArgumentException("SparseDiagonals : Parameter \"A\" must be a matrix and not a vector.");
        }

        int[] arr = null;

        if (d == null)
        {
            Indices find = A.NEQ(0.0).find();
            if (find == null)
            {
                throw new IllegalArgumentException(
                        "SparseDiagonals : Matrix \"A\" is a whole sparse matrix, ie, all elements.");
            }
            Indices I = find.getColumnAt(0);
            Indices J = find.getColumnAt(1);

            // ----- Changed on 12/08/2009 from 'SortInd' into 'QuickSortInd'
            // SortInd SI = new SortInd(J.minus(I)); //--> original line
            QuickSort SI = new QuickSortInd(J.minus(I), false, false); // -->
                                                                       // new
                                                                       // line

            d = (Indices) SI.getSortedObject();
            Matrix dMat = Matrix.indicesToMatrix(d);
            Matrix temp = new Matrix(1, 1, Double.NEGATIVE_INFINITY);
            temp = temp.mergeV(dMat);
            temp = JDatafun.diff(temp);

            // minInf
            // System.out.println("------------- minInf -------------");
            // temp.print(8,0);

            find = temp.NEQ(0.0).find();
            if (find != null)
            {
                // System.out.println("------------- findMinInf  -------------");
                // find.getColumnAt(0).plus(1).print(8,0);
                arr = find.getColIndicesAt(0);
                d = d.getElements(arr);
                // System.out.println("------------- d -------------");
                // d.print(8);
            }
        }
        else
        {
            if (!d.isVector())
            {
                throw new IllegalArgumentException(
                        "SparseDiagonals : Indices parameter \"d\" must be a vector and not a matrix.");
            }
            if (d.isRowVector())
            {
                d = d.toColVector();
            }
        }

        int m = A.getRowDimension();
        int n = A.getColumnDimension();
        int p = d.length();
        Matrix B = Matrix.zeros(Math.min(m, n), p);

        int low = 0;
        int high = 0;
        Indices Ind = null;

        for (int k = 0; k < p; k++)
        {
            arr = null;
            if (m >= n)
            {
                // low = Math.max(1, 1 + d.getElementAt(k)) ;
                // high = Math.min(n, m + d.getElementAt(k)) ;
                low = Math.max(0, d.getElementAt(k));
                high = Math.min(n - 1, m + d.getElementAt(k) - 1);
            }
            else
            {
                // low = Math.max(1, 1 - d.getElementAt(k)) - 1;
                // high = Math.min(m, n - d.getElementAt(k)) - 1;//min(m,n-d(k))
                low = Math.max(0, -d.getElementAt(k));
                high = Math.min(m - 1, n - d.getElementAt(k) - 1);// min(m,n-d(k))
            }
            // System.out.println("low = "+low+" : high = "+high);
            if (low <= high)
            {
                arr = Indices.intLinspaceIncrement(low, high).getRowIndicesAt(0);
            }
            if (arr != null)
            {
                Matrix diagMat = A.diag(d.getElementAt(k));
                // B(i,k) = diag(A,d(k))
                // B.setColumnAt(k, diagMat);// .setElements(arr, diagMat);
                B.setMatrix(arr, k, diagMat);
            }
        }// end for

        // System.out.println("------------- B -------------");
        // B.print(8,0);

        // System.out.println("------------- d -------------");
        // d.print(8,0);

        this.firstOutput = B;
        this.secondOutput = d;
    }

    public SparseDiagonals(Matrix B, Indices d, Matrix A)
    {
        if (B == null)
        {
            throw new IllegalArgumentException("SparseDiagonals : Parameter \"B\" must be non-null.");
        }
        if (B.isVector())
        {
            throw new IllegalArgumentException("SparseDiagonals : Parameter \"B\" must be a matrix and not a vector.");
        }

        if (d == null)
        {
            throw new IllegalArgumentException("SparseDiagonals : Parameter \"d\" must be non-null.");
        }
        else
        {
            if (!d.isVector())
            {
                throw new IllegalArgumentException(
                        "SparseDiagonals : Parameter \"d\" must be a vector and not a matrix.");
            }
            if (d.isRowVector())
            {
                d = d.toColVector();
            }
        }

        if (A == null)
        {
            throw new IllegalArgumentException("SparseDiagonals : Parameter \"A\" must be non-null.");
        }
        if (A.isVector())
        {
            throw new IllegalArgumentException("SparseDiagonals : Parameter \"A\" must be a matrix and not a vector.");
        }

        int p = d.length();
        // [i,j,a] = find(A);
        Indices find = A.NEQ(0.0).find();
        Matrix a = null;
        int[] arr = null;
        Indices I = null;
        Indices J = null;

        if (find != null)
        {
            I = find.getColumnAt(0);
            J = find.getColumnAt(1);
            a = A.getFromFind(find);
            a = Matrix.indicesToMatrix(I).mergeH(J).mergeH(a);
        }

        int m = A.getRowDimension();
        int n = A.getColumnDimension();
        int low = 0;
        int high = 0;
        int mn = m >= n ? 1 : 0;

        if (a != null)
        {
            Matrix temp = null;
            for (int k = 0; k < p; k++)
            {
                // Delete current d(k)-th diagonal
                temp = a.getColumnAt(1).minus(a.getColumnAt(0));
                I = temp.EQ(d.getElementAt(k)).find();
                if (I != null)
                {
                    arr = I.getColIndicesAt(0);
                    a = a.removeRowsAt(arr);
                }
                // Append new d(k)-th diagonal to compact form
                low = Math.max(0, -d.getElementAt(k));
                high = Math.min(m - 1, n - d.getElementAt(k) - 1);
                // i = (max(1,1-d(k)):min(m,n-d(k)))';
                if (low <= high)
                {
                    I = Indices.intLinspaceIncrement(low, high).transpose();
                    // a = [a; i i+d(k) B(i+(m>=n)*d(k),k)];
                    Indices Id = I.plus(d.getElementAt(k));
                    Indices Imndk = I.plus(mn * d.getElementAt(k));
                    arr = Imndk.getColIndicesAt(0);
                    temp = B.getMatrix(arr, k);
                    temp = Matrix.indicesToMatrix(I).mergeH(Id).mergeH(temp); // a
                                                                              // =
                                                                              // [a;
                                                                              // i
                                                                              // i+d(k)
                                                                              // B(i+(m>=n)*d(k),k)];
                    a = a.mergeV(temp);
                }
                // else{ I = null; }
                // a = [a; i i+d(k) B(i+(m>=n)*d(k),k)];

            }// end for
        }// end if

        this.firstOutput = a;

    }

    public SparseDiagonals(Matrix B, Indices d, int m, int n)
    {
        if (B == null)
        {
            throw new IllegalArgumentException("SparseDiagonals : Parameter \"B\" must be non-null.");
        }
        if (B.isVector())
        {
            throw new IllegalArgumentException("SparseDiagonals : Parameter \"B\" must be a matrix and not a vector.");
        }

        if (d == null)
        {
            throw new IllegalArgumentException("SparseDiagonals : Parameter \"d\" must be non-null.");
        }
        else
        {
            if (!d.isVector())
            {
                throw new IllegalArgumentException(
                        "SparseDiagonals : Parameter \"d\" must be a vector and not a matrix.");
            }
            if (d.isRowVector())
            {
                d = d.toColVector();
            }
        }

        if (m < 1)
        {
            throw new IllegalArgumentException("SparseDiagonals : Parameter \"m\" must be a positive number.");
        }

        if (n < 1)
        {
            throw new IllegalArgumentException("SparseDiagonals : Parameter \"n\" must be a positive number.");
        }

        int p = d.length();
        int low = 0;
        int high = 0;
        int val = 0;
        int mn = m >= n ? 1 : 0;

        Indices len = new Indices(p + 1, 1, 0);
        for (int k = 0; k < p; k++)
        {
            low = Math.max(0, -d.get(k, 0));
            high = Math.min(m - 1, n - d.get(k, 0) - 1);
            // len(k+1) = len(k)+length(max(1,1-d(k)):min(m,n-d(k)));
            if (low <= high)
            {
                val = len.get(k, 0) + Indices.intLinspaceIncrement(low, high).length();
            }
            else
            {
                val = len.get(k, 0);
            }
            len.set(k + 1, 0, val);
        }// end for

        // System.out.println("------------- len -------------");
        // len.print(8,0);

        int[] arr = null;
        int[] arr2 = null;

        Matrix a = Matrix.zeros(len.get(p, 0), 3);

        for (int k = 0; k < p; k++)
        {
            // Append new d(k)-th diagonal to compact form
            low = Math.max(0, -d.get(k, 0));
            high = Math.min(m - 1, n - d.get(k, 0) - 1);
            if (low <= high)
            {
                Indices I = Indices.intLinspaceIncrement(low, high).transpose();

                // System.out.println("------------- I -------------");
                // I.plus(1).print(8,0);

                Indices Id = I.plus(d.get(k, 0));
                Indices Imndk = I.plus(mn * d.get(k, 0));
                arr = Imndk.getColIndicesAt(0);
                Matrix temp = B.getMatrix(arr, k);
                // a((len(k)+1):len(k+1),:) = [i i+d(k) B(i+(m>=n)*d(k),k)];
                temp = Matrix.indicesToMatrix(I).mergeH(Id).mergeH(temp); // a =
                                                                          // [i
                                                                          // i+d(k)
                                                                          // B(i+(m>=n)*d(k),k)];

                // System.out.println("------------- temp -------------");
                // temp.print(8,0);

                int low2 = len.get(k, 0);// (len(k)+1):
                int high2 = len.get(k + 1, 0) - 1;// len(k+1)

                if (low2 <= high2)
                {
                    I = Indices.intLinspaceIncrement(low2, high2);
                    // System.out.println("------------- I -------------");
                    // I.print(8,0);
                    arr2 = I.getRowIndicesAt(0);
                    a.setRows(arr2, temp);
                }
            }
        }

        /*
         * Indices I = a.getColumnAt(0).toIndices(); Indices J =
         * a.getColumnAt(1).toIndices(); Matrix V = a.getColumnAt(2);
         */

        this.firstOutput = new Matrix(m, n);
        int lenI = a.getRowDimension();
        for (int i = 0; i < lenI; i++)
        {
            this.firstOutput.set((int) a.get(i, 0), (int) a.get(i, 1), a.get(i, 2));
        }

    }

    public Matrix getFirstOutput()
    {
        return this.firstOutput;
    }

    public Indices getSecondOutput()
    {
        return this.secondOutput;
    }

    public static void main(String[] args)
    {
        /*
         * double[][] b = {{4, 11, 13, 10}, {4, 2, 3, 4}, {13, 0, 4, 7}}; Matrix
         * x = new Matrix(b); SparseDiagonals SP = new SparseDiagonals(x);
         */
        int n = 8;
        Matrix e = Matrix.ones(n, 1);
        Matrix B = e.mergeH(e.arrayTimes(-2.0)).mergeH(e);
        Indices d = Indices.intLinspaceIncrement(-1, 1);
        // spdiags([e -2*e e], -1:1, n, n)
        SparseDiagonals SP = new SparseDiagonals(B, d, n, n);
        Matrix A = SP.firstOutput;

        System.out.println("------------- A -------------");
        A.print(8, 0);

        // System.out.println("------------- d -------------");
        // d.print(8,0);
    }
}// ----------------------------- End Class Definition
// --------------------------
