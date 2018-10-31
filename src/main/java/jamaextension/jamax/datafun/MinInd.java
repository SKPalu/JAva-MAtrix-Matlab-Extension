/*
 * MinInd.java
 *
 * Created on 7 November 2007, 17:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.constants.Dimension;

/**
 * TEST Passed.
 * 
 * @author Feynman Perceptrons
 */
public class MinInd extends Min
{

    public MinInd(Indices A)
    {
        this(A, null);
    }

    /**
     * Creates a new instance of MinMat
     */
    public MinInd(Indices A, Dimension dim)
    {
        if (dim == null)
        {
            if (A.isVector())
            {
                minVector(A);
            }
            else
            {
                minIndices(A, Dimension.ROW);
            }
        }
        else
        {
            minIndices(A, dim);
        }
    }

    private void minIndices(Indices A, Dimension dim)
    {
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {// return the same
                    minObject = A.copy();
                    minIndices = new Indices(A.getRowDimension(), A.getColumnDimension());
                }
                else
                {
                    minVector(A);
                }
            }
            else
            {// column vector
                if (dim == Dimension.COL)
                {
                    minObject = A.copy();
                    minIndices = new Indices(A.getRowDimension(), A.getColumnDimension());
                }
                else
                {
                    minVector(A);
                }
            }
        }
        else
        {
            minIndicesAll(A, dim);
        }
    }

    private void minIndicesAll(Indices A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
         
        
        QuickSort sort = null;
        Indices IJ = null;
        Indices IND = null;

        switch (dim)
        {
        case ROW:
            minObject = new Indices(1, cols);
            minIndices = new Indices(1, cols);
            for (int j = 0; j < cols; j++)
            {
                IJ = A.getColumnAt(j);
                sort = new QuickSortInd(IJ,true,true);
                IJ = (Indices)sort.getSortedObject();
                IND = sort.getIndices();
                ((Indices) minObject).set(0, j, IJ.start());
                minIndices.set(0, j, IND.start());
            }
            break;
        case COL:
            minObject = new Indices(rows, 1);
            minIndices = new Indices(rows, 1);
            for (int i = 0; i < rows; i++)
            {
                IJ = A.getRowAt(i);
                sort = new QuickSortInd(IJ,true,true);
                IJ = (Indices)sort.getSortedObject();
                IND = sort.getIndices();
                ((Indices) minObject).set(i, 0, IJ.start());
                minIndices.set(i, 0, IND.start());
            }
            break;
        default:
            throw new IllegalArgumentException("minMatrixAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
    }

    
    
    private void minIndicesAllOld(Indices A, Dimension dim)
    {
        System.out.println("Pure Matrix");
        /*
         * QuickSort sort = new QuickSortInd(A, dim, true, true); if (dim ==
         * Dimension.ROW) { System.out.println("Dim-ROW"); this.minObject =
         * ((Indices) sort.getSortedObject()).getRowAt(0); this.minIndices =
         * sort.getIndices().getRowAt(0); } else {
         * System.out.println("Dim-COL"); this.minObject = ((Indices)
         * sort.getSortedObject()).getColumnAt(0); this.minIndices =
         * sort.getIndices().getColumnAt(0); }
         */

        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
        int t = 0;
        int val = 0;
        int T = 0;
        int Tval = 0;

        switch (dim)
        {
        case ROW:
            minObject = new Indices(1, cols);
            minIndices = new Indices(1, cols);
            for (int j = 0; j < cols; j++)
            {
                t = A.get(0, j);
                T = 0;
                for (int i = 1; i < rows; i++)
                {
                    val = A.get(i, j);
                    Tval = i;
                    if (t <= val)
                    {
                        t = t;
                        T = T;
                        // System.out.println("1) t = "+t+"  :  T = "+T);
                    }
                    else
                    {
                        t = val;
                        T = Tval;
                        // System.out.println("2) t = "+t+"  :  T = "+T);
                    }
                    // t = t > X.get(i,j) ? t : X.get(i,j) ;
                }
                ((Indices) minObject).set(0, j, t);
                minIndices.set(0, j, T);
            }
            break;
        case COL:
            minObject = new Indices(rows, 1);
            minIndices = new Indices(rows, 1);
            for (int i = 0; i < rows; i++)
            {
                t = A.get(i, 0);
                T = 0;
                for (int j = 1; j < cols; j++)
                {
                    val = A.get(i, j);
                    Tval = j;
                    if (t <= val)
                    {
                        t = t;
                        T = T;
                    }
                    else
                    {
                        t = val;
                        T = Tval;
                    }
                    // t = t > X.get(i,j)? t : X.get(i,j) ;
                }
                ((Indices) minObject).set(i, 0, t);
                minIndices.set(i, 0, T);
            }
            break;
        default:
            throw new IllegalArgumentException("minIndicesAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch

    }

    private void minVector(Indices A)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(" minVector :  Parameter \"A\" must be a vector and not a matrix.");
        }
        //double t = 0.0;
        //double val = 0.0;
        //int T = 0;
        //int Tval = 0;

        
        minObject = new Indices(1, 1);
        minIndices = new Indices(1, 1);
        int c = A.length();
        if (c == 1)
        {// if only one element , then return.
            
            ((Indices) minObject).set(0, 0, A.get(0, 0));            
            return;
        }

        /*
         * t = A.get(0, 0); T = 0;
         * 
         * if (A.isRowVector()) { for (int j = 1; j < c; j++) { val = A.get(0,
         * j); Tval = j; if (!MathUtil.compareDoubles(t, val,
         * SortingMode.DESCENDING)) {// (t<=val) {
         * 
         * //t = t; T = T; } else{
         * 
         * t = val; T = Tval; } } } else { for (int i = 1; i < c; i++) { val =
         * A.get(i, 0); Tval = i; if (!MathUtil.compareDoubles(t, val,
         * SortingMode.DESCENDING)) {// (t<=val) {
         * 
         * t = t; T = T; } else{
         * 
         * t = val; T = Tval; } } }
         */
        //((Matrix) minObject).set(0, 0, t);
        //minIndices = new Indices(1, 1, T);
        QuickSort sort = new QuickSortInd(A,true,true);
        Indices sortA = (Indices)sort.getSortedObject();
        minObject = new Indices(1,1,sortA.start());//sort.sortedObject;
        minIndices.set(0, 0, sort.getIndices().start());
    }

    
    private void minVectorOld(Indices A)
    {
        System.out.println("Pure Vector");

        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(" minVector :  Parameter \"A\" must be a vector and not a matrix.");
        }

        minObject = new Indices(1, 1);
        int c = A.length();
        if (c == 1)
        {// if only one element , then return.
            ((Indices) minObject).set(1, 1, A.get(0, 0));
            minIndices = new Indices(1, 1);
            return;
        }

        // QuickSort sort = new QuickSortInd(A, null, true, true);
        // this.minObject = ((Indices) sort.getSortedObject()).getAsIndices(0,
        // 0);
        // this.minIndices = sort.getIndices().getAsIndices(0, 0);

        int t = A.get(0, 0);
        int T = 0;
        int val = 0;
        int Tval = 0;

        if (A.isRowVector())
        {
            for (int j = 1; j < c; j++)
            {
                val = A.get(0, j);
                Tval = j;
                if (t <= val)
                {
                    t = t;
                    T = T;
                }
                else
                {
                    t = val;
                    T = Tval;
                }
            }
        }
        else
        {
            for (int i = 1; i < c; i++)
            {
                val = A.get(i, 0);
                Tval = i;
                if (t <= val)
                {
                    t = t;
                    T = T;
                }
                else
                {
                    t = val;
                    T = Tval;
                }
            }
        }
        ((Indices) minObject).set(0, 0, t);
        minIndices = new Indices(1, 1, T);

    }

    public static void main(String[] args)
    {
        // /1500000
        Indices A = Indices.random(1, 2000000, 200);// SampleMatrices.generateIndices();
        // Indices row = A.getRowAt(2); //row.printInLabel("row");
        // Indices col = A.getColumnAt(4); //col.printInLabel("col");

        long start = System.currentTimeMillis();
        MinInd M = new MinInd(A);
        long stop = System.currentTimeMillis();
        double elapsed = (stop - start) / 1000.0;
        System.out.println("MinInd:   " + elapsed + " seconds");

        Indices Mr = (Indices) M.getMinObject();
        System.out.println("----- Mr -----");
        Mr.print(4, 0);
        Indices Ir = M.getIndices();
        System.out.println("----- Ir -----");
        Ir.plus(1).print(4, 0);
    }
}
