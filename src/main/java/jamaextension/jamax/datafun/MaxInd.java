/*
 * MaxInd.java
 *
 * Created on 7 November 2007, 15:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Feynman Perceptrons
 */
public class MaxInd extends Max
{

    public MaxInd(Indices A)
    {
        this(A, null);
    }

    /**
     * Creates a new instance of MaxMat
     * 
     * @param A
     * @param dim
     */
    public MaxInd(Indices A, Dimension dim)
    {
        if (dim == null)
        {
            if (A.isVector())
            {
                maxVector(A);
            }
            else
            {
                maxIndices(A, Dimension.ROW);
            }
        }
        else
        {
            maxIndices(A, dim);
        }
    }

    private void maxIndices(Indices A, Dimension dim)
    {
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {// return the same
                    maxObject = A.copy();
                    maxIndices = new Indices(A.getRowDimension(), A.getColumnDimension());
                }
                else
                {
                    maxVector(A);
                }
            }
            else
            {// column vector
                if (dim == Dimension.COL)
                {
                    maxObject = A.copy();
                    maxIndices = new Indices(A.getRowDimension(), A.getColumnDimension());
                }
                else
                {
                    maxVector(A);
                }
            }
        }
        else
        {
            maxIndicesAll(A, dim);
        }
    }
    private void maxIndicesAll(Indices A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
         
        
        QuickSort sort = null;
        Indices IJ = null;
        Indices IND = null;

        switch (dim)
        {
        case ROW:
            maxObject = new Indices(1, cols);
            maxIndices = new Indices(1, cols);
            for (int j = 0; j < cols; j++)
            {
                IJ = A.getColumnAt(j);
                sort = new QuickSortInd(IJ,true,true);
                IJ = (Indices)sort.getSortedObject();
                IND = sort.getIndices();
                ((Indices) maxObject).set(0, j, IJ.end());
                maxIndices.set(0, j, IND.end());
            }
            break;
        case COL:
            maxObject = new Indices(rows, 1);
            maxIndices = new Indices(rows, 1);
            for (int i = 0; i < rows; i++)
            {
                IJ = A.getRowAt(i);
                sort = new QuickSortInd(IJ,true,true);
                IJ = (Indices)sort.getSortedObject();
                IND = sort.getIndices();
                ((Indices) maxObject).set(i, 0, IJ.end());
                maxIndices.set(i, 0, IND.end());
            }
            break;
        default:
            throw new IllegalArgumentException("maxMatrixAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
    }

    private void maxIndicesAllOld(Indices A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
        int t = 0;
        int val = 0;
        int T = 0;
        int Tval = 0;

        switch (dim)
        {
        case ROW:
            maxObject = new Indices(1, cols);
            maxIndices = new Indices(1, cols);
            for (int j = 0; j < cols; j++)
            {
                t = A.get(0, j);
                T = 0;
                for (int i = 1; i < rows; i++)
                {
                    val = A.get(i, j);
                    Tval = i;
                    if (t < val)
                    {
                        /*
                         * t = t; T = T;
                         * //System.out.println("1) t = "+t+"  :  T = "+T); }
                         * else {
                         */
                        t = val;
                        T = Tval;
                        // System.out.println("2) t = "+t+"  :  T = "+T);
                    }
                    // t = t > X.get(i,j) ? t : X.get(i,j) ;
                }
                ((Indices) maxObject).set(0, j, t);
                maxIndices.set(0, j, T);
            }
            break;
        case COL:
            maxObject = new Indices(rows, 1);
            maxIndices = new Indices(rows, 1);
            for (int i = 0; i < rows; i++)
            {
                t = A.get(i, 0);
                T = 0;
                for (int j = 1; j < cols; j++)
                {
                    val = A.get(i, j);
                    Tval = j;
                    if (t < val)
                    {
                        /*
                         * t = t; T = T; } else {
                         */
                        t = val;
                        T = Tval;
                    }
                    // t = t > X.get(i,j)? t : X.get(i,j) ;
                }
                ((Indices) maxObject).set(i, 0, t);
                maxIndices.set(i, 0, T);
            }
            break;
        default:
            throw new IllegalArgumentException("maxIndicesAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
    }

    private void maxVector(Indices A)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(" minVector :  Parameter \"A\" must be a vector and not a matrix.");
        }
        //double t = 0.0;
        //double val = 0.0;
        //int T = 0;
        //int Tval = 0;

        
        maxObject = new Indices(1, 1);
        maxIndices = new Indices(1, 1);
        int c = A.length();
        if (c == 1)
        {// if only one element , then return.
            
            ((Indices) maxObject).set(0, 0, A.get(0, 0));            
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
        maxObject = new Indices(1,1,sortA.end());//sort.sortedObject;
        maxIndices.set(0, 0, sort.getIndices().end());
    }

    
    private void maxVectorOld(Indices A)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(" maxVector :  Parameter \"A\" must be a vector and not a matrix.");
        }

        int t = 0;
        int val = 0;
        int T = 0;
        int Tval = 0;

        maxObject = new Indices(1, 1);

        int c = A.length();
        if (c == 1)
        {// if only one element , then return.
            ((Indices) maxObject).set(0, 0, A.get(0, 0));
            maxIndices = new Indices(1, 1);
            return;
        }

        t = A.get(0, 0);
        T = 0;

        if (A.isRowVector())
        {
            for (int j = 1; j < c; j++)
            {
                val = A.get(0, j);
                Tval = j;
                if (t < val)
                {
                    /*
                     * t = t; T = T; } else{
                     */
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
                if (t < val)
                {
                    /*
                     * t = t; T = T; } else{
                     */
                    t = val;
                    T = Tval;
                }
            }
        }
        ((Indices) maxObject).set(0, 0, t);
        maxIndices = new Indices(1, 1, T);
    }

    public static void main(String[] args)
    {
        Indices A = SampleMatrices.generateIndices();
        Indices row = A.getRowAt(2);
        Indices col = A.getColumnAt(4);
        MaxInd M = new MaxInd(col, Dimension.COL);
        Indices Mr = (Indices) M.getMaxObject();
        System.out.println("----- Mr -----");
        Mr.print(4, 0);
        Indices Ir = M.getIndices();
        System.out.println("----- Ir -----");
        Ir.plus(1).print(4, 0);
    }
}
