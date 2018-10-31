/*
 * MinMat.java
 *
 * Created on 6 November 2007, 21:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.constants.SortingMode;

/**
 * 
 * @author Feynman Perceptrons
 */
public class MinMat extends Min
{

    public MinMat(Matrix A)
    {
        this(A, null);
    }

    /**
     * Creates a new instance of MinMat
     */
    public MinMat(Matrix A, Dimension dim)
    {
        if (dim == null)
        {
            if (A.isVector())
            {
                minVector(A);
            }
            else
            {
                minMatrix(A, Dimension.ROW);
            }
        }
        else
        {
            minMatrix(A, dim);
        }
    }

    private void minMatrix(Matrix A, Dimension dim)
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
            minMatrixAll(A, dim);
        }
    }

    private void minMatrixAll(Matrix A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
         
        
        QuickSort sort = null;
        Matrix IJ = null;
        Indices IND = null;

        switch (dim)
        {
        case ROW:
            minObject = new Matrix(1, cols);
            minIndices = new Indices(1, cols);
            for (int j = 0; j < cols; j++)
            {
                IJ = A.getColumnAt(j);
                sort = new QuickSortMat(IJ,true,true);
                IJ = (Matrix)sort.getSortedObject();
                IND = sort.getIndices();
                ((Matrix) minObject).set(0, j, IJ.start());
                minIndices.set(0, j, IND.start());
            }
            break;
        case COL:
            minObject = new Matrix(rows, 1);
            minIndices = new Indices(rows, 1);
            for (int i = 0; i < rows; i++)
            {
                IJ = A.getRowAt(i);
                sort = new QuickSortMat(IJ,true,true);
                IJ = (Matrix)sort.getSortedObject();
                IND = sort.getIndices();
                ((Matrix) minObject).set(i, 0, IJ.start());
                minIndices.set(i, 0, IND.start());
            }
            break;
        default:
            throw new IllegalArgumentException("minMatrixAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
    }

    
    private void minMatrixAllOld(Matrix A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
        double t = 0.0;
        double val = 0.0;
        int T = 0;
        int Tval = 0;

        switch (dim)
        {
        case ROW:
            minObject = new Matrix(1, cols);
            minIndices = new Indices(1, cols);
            for (int j = 0; j < cols; j++)
            {
                t = A.get(0, j);
                T = 0;
                for (int i = 1; i < rows; i++)
                {
                    val = A.get(i, j);
                    Tval = i;
                    if (MathUtil.compareDoubles(t, val, SortingMode.DESCENDING))
                    {// (t<=val) {// MathUtil.compareDoubles(t, val,
                     // SortingMode.DESCENDING)
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
                ((Matrix) minObject).set(0, j, t);
                minIndices.set(0, j, T);
            }
            break;
        case COL:
            minObject = new Matrix(rows, 1);
            minIndices = new Indices(rows, 1);
            for (int i = 0; i < rows; i++)
            {
                t = A.get(i, 0);
                T = 0;
                for (int j = 1; j < cols; j++)
                {
                    val = A.get(i, j);
                    Tval = j;
                    if (!MathUtil.compareDoubles(t, val, SortingMode.DESCENDING))
                    {// (t<=val) {
                        /*
                         * t = t; T = T; } else {
                         */
                        t = val;
                        T = Tval;
                    }
                    // t = t > X.get(i,j)? t : X.get(i,j) ;
                }
                ((Matrix) minObject).set(i, 0, t);
                minIndices.set(i, 0, T);
            }
            break;
        default:
            throw new IllegalArgumentException("minMatrixAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
    }

    private void minVector(Matrix A)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(" minVector :  Parameter \"A\" must be a vector and not a matrix.");
        }
        //double t = 0.0;
        //double val = 0.0;
        //int T = 0;
        //int Tval = 0;

        
        minObject = new Matrix(1, 1);
        minIndices = new Indices(1, 1);
        int c = A.length();
        if (c == 1)
        {// if only one element , then return.
            
            ((Matrix) minObject).set(0, 0, A.get(0, 0));            
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
        QuickSort sort = new QuickSortMat(A,true,true);
        Matrix sortA = (Matrix)sort.getSortedObject();
        minObject = new Matrix(1,1,sortA.start());//sort.sortedObject;
        minIndices.set(0, 0, sort.getIndices().start());
    }

    private void minVectorOld(Matrix A)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(" minVector :  Parameter \"A\" must be a vector and not a matrix.");
        }
        double t = 0.0;
        double val = 0.0;
        int T = 0;
        int Tval = 0;

        minObject = new Matrix(1, 1);

        int c = A.length();
        if (c == 1)
        {// if only one element , then return.
            ((Matrix) minObject).set(0, 0, A.get(0, 0));
            minIndices = new Indices(1, 1);
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
                if (!MathUtil.compareDoubles(t, val, SortingMode.DESCENDING))
                {// (t<=val) {
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
                if (!MathUtil.compareDoubles(t, val, SortingMode.DESCENDING))
                {// (t<=val) {
                    /*
                     * t = t; T = T; } else{
                     */
                    t = val;
                    T = Tval;
                }
            }
        }
        ((Matrix) minObject).set(0, 0, t);
        minIndices = new Indices(1, 1, T);
    }

    public static void main(String[] args)
    {

        Matrix X = SampleMatrices.genMinMaxMatrix();
        X.printInLabel("X",0);
        MinMat M = new MinMat(X, Dimension.COL);
        Matrix Mmin = (Matrix) M.getMinObject();
        Mmin.printInLabel("Mmin", 0);
        Indices Imin = M.getIndices();
        Imin.plus(1).printInLabel("Imin", 0);

        /*
         * Matrix A = SampleMatrices.generateMatrix(); Matrix row =
         * A.getRowAt(2); Matrix col = A.getColumnAt(4); MinMat M = new
         * MinMat(col, Dimension.COL); Matrix Mr = (Matrix)M.getMinObject();
         * System.out.println("----- Mr -----"); Mr.print(4,0); Indices Ir =
         * M.getIndices(); System.out.println("----- Ir -----");
         * Ir.plus(1).print(4,0);
         */
    }
}
