/*
 * Maximum.java
 *
 * Created on 8 November 2007, 13:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Feynman Perceptrons
 */
final class Maximum
{

    private Maximum()
    {
    }

    protected static Matrix max(Matrix A)
    {
        return max(A, null);
    }

    /**
     * Creates a new instance of MaxMat
     * 
     * @param A
     * @param dim
     */
    protected static Matrix max(Matrix A, Dimension dim)
    {
        Matrix maxMat;
        if (dim == null)
        {
            if (A.isVector())
            {
                maxMat = maxMatrixVector(A);
            }
            else
            {
                maxMat = maxMatrix(A, Dimension.ROW);
            }
        }
        else
        {
            maxMat = maxMatrix(A, dim);
        }
        return maxMat;
    }

    private static Matrix maxMatrix(Matrix A, Dimension dim)
    {
        Matrix maxObject = null;
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {
                    maxObject = A.copy();
                } // return the same
                else
                {
                    maxObject = maxMatrixVector(A);
                }
            }
            else
            {// column vector
                if (dim == Dimension.COL)
                {
                    maxObject = A.copy();
                }
                else
                {
                    maxObject = maxMatrixVector(A);
                }
            }
        }
        else
        {
            maxObject = maxMatrixAll(A, dim);
        }
        return maxObject;
    }

    private static Matrix maxMatrixAll(Matrix A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
        double t = 0.0;
        // double val = 0.0;

        Matrix maxObject = null;
        boolean comp = false; // ===> New line

        switch (dim)
        {
        case ROW:
        {
            maxObject = new Matrix(1, cols);
            for (int j = 0; j < cols; j++)
            {
                Matrix colMat = A.getColumnAt(j);
                Matrix nonNan = null;
                if (colMat.isnanBoolean())
                {
                    Indices finite = colMat.isNonNans().find();
                    if (finite != null)
                    {
                        nonNan = colMat.getFromFind(finite);
                        nonNan = maxMatrixVector(nonNan);
                        t = nonNan.get(0, 0);
                    }
                    else
                    {
                        t = Double.NaN;
                    }
                }
                else
                {
                    nonNan = maxMatrixVector(colMat);
                    t = nonNan.get(0, 0);
                }

                maxObject.set(0, j, t);
            }
            break;
        }
        case COL:
        {
            maxObject = new Matrix(rows, 1);
            for (int i = 0; i < rows; i++)
            {
                Matrix rowMat = A.getRowAt(i);
                Matrix nonNan = null;
                if (rowMat.isnanBoolean())
                {
                    Indices finite = rowMat.isNonNans().find();
                    if (finite != null)
                    {
                        nonNan = rowMat.getFromFind(finite);
                        nonNan = maxMatrixVector(nonNan);
                        t = nonNan.get(0, 0);
                    }
                    else
                    {
                        t = Double.NaN;
                    }
                }
                else
                {
                    nonNan = maxMatrixVector(rowMat);
                    t = nonNan.get(0, 0);
                }
                maxObject.set(i, 0, t);
            }
            break;
        }
        default:
            throw new IllegalArgumentException("maxMatrixAll : Dimension  " + dim.toString() + " , not supported.");
        }// switch

        // =================== old codes =====================
        /*
         * switch(dim){ case ROW: maxObject = new Matrix(1,cols); for(int j=0 ;
         * j<cols ; j++){ t = A.get(0,j); for(int i=1 ; i<rows ; i++){
         * //System.out.println("1) t = "+t+"  :  T = "+T); val = A.get(i,j);
         * comp = !MathUtil.compareDoubles(t,val); //===> New line if(comp){
         * //===> New line //if(t>=val) { //t = t;
         * //System.out.println("ROW --> ["
         * +i+"]["+j+"] t = "+t+"  >  val = "+val+"  :  TRUE"); //} //else { t =
         * val;
         * System.out.println("ROW --> ["+i+"]["+j+"] t = "+t+"  >  val = "+
         * val+"  :  FALSE"); } } maxObject.set(0,j,t);
         * 
         * } break; case COL: maxObject = new Matrix(rows,1); for(int i=0 ;
         * i<rows ; i++){ t = A.get(i,0); for(int j=1 ; j<cols; j++){ val =
         * A.get(i,j); comp = !MathUtil.compareDoubles(t,val); //===> New line
         * if(comp){ //===> New line //if(t>=val) { //t = t;
         * //System.out.println
         * ("COL --> ["+i+"]["+j+"] t = "+t+"  >  val = "+val+"  :  TRUE"); //}
         * //else { t = val;
         * System.out.println("COL --> ["+i+"]["+j+"] t = "+t+"  >  val = "
         * +val+"  :  FALSE"); } } maxObject.set(i,0,t); } break; default: throw
         * new
         * IllegalArgumentException("maxMatrixAll : Dimension  "+dim.toString
         * ()+" , not supported."); }//end switch
         */

        return maxObject;
    }

    private static Matrix maxMatrixVector(Matrix A)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(" maxMatrixVector :  Parameter \"A\" must be a vector and not a matrix.");
        }

        double t = 0.0;
        double val = 0.0;
        Matrix maxObject = new Matrix(1, 1);

        int c = A.length();
        if (c == 1)
        {// if only one element , then return.
            maxObject.set(0, 0, A.get(0, 0));
            return maxObject;
        }

        t = A.get(0, 0);

        if (A.isRowVector())
        {
            for (int j = 1; j < c; j++)
            {
                val = A.get(0, j);
                if (!MathUtil.compareDoubles(t, val))
                { // ===> New line
                  // if(t>=val) {
                  // t = t;
                  // }
                  // else{
                    t = val;
                }
            }
        }
        else
        {
            for (int i = 1; i < c; i++)
            {
                val = A.get(i, 0);
                if (!MathUtil.compareDoubles(t, val))
                { // ===> New line
                  // if(t>=val) {
                  // t = t;
                  // }
                  // else{
                    t = val;
                }
            }
        }
        maxObject.set(0, 0, t);
        return maxObject;
    }

    // ##########################################################################
    // ##########################################################################

    protected static Indices max(Indices A)
    {
        return max(A, null);
    }

    /**
     * Creates a new instance of MaxMat
     * 
     * @param A
     * @param dim
     */
    protected static Indices max(Indices A, Dimension dim)
    {
        Indices maxObject = null;
        if (dim == null)
        {
            if (A.isVector())
            {
                maxObject = maxIndicesVector(A);
            }
            else
            {
                maxObject = maxIndices(A, Dimension.ROW);
            }
        }
        else
        {
            maxObject = maxIndices(A, dim);
        }
        return maxObject;
    }

    private static Indices maxIndices(Indices A, Dimension dim)
    {
        Indices maxObject = null;
        if (A.isVector())
        {
            if (A.isRowVector())
            {// row vector
                if (dim == Dimension.ROW)
                {
                    maxObject = A.copy();
                } // return the same
                else
                {
                    maxObject = maxIndicesVector(A);
                }
            }
            else
            {// column vector
                if (dim == Dimension.COL)
                {
                    maxObject = A.copy();
                }
                else
                {
                    maxObject = maxIndicesVector(A);
                }
            }
        }
        else
        {
            maxObject = maxIndicesAll(A, dim);
        }
        return maxObject;
    }

    private static Indices maxIndicesAll(Indices A, Dimension dim)
    {
        int rows = A.getRowDimension();
        int cols = A.getColumnDimension();
        int t = 0;
        int val = 0;

        Indices maxObject = null;

        switch (dim)
        {
        case ROW:
            maxObject = new Indices(1, cols);

            for (int j = 0; j < cols; j++)
            {
                t = A.get(0, j);
                for (int i = 1; i < rows; i++)
                {
                    val = A.get(i, j);
                    if (t >= val)
                    {
                        t = t;
                    }
                    else
                    {
                        t = val;
                    }
                }
                maxObject.set(0, j, t);
            }
            break;
        case COL:
            maxObject = new Indices(rows, 1);

            for (int i = 0; i < rows; i++)
            {
                t = A.get(i, 0);

                for (int j = 1; j < cols; j++)
                {
                    val = A.get(i, j);
                    if (t >= val)
                    {
                        t = t;
                    }
                    else
                    {
                        t = val;
                    }
                }
                maxObject.set(i, 0, t);

            }
            break;
        default:
            throw new IllegalArgumentException("maxIndicesAll : Dimension  " + dim.toString() + " , not supported.");
        }// end switch
        return maxObject;
    }

    private static Indices maxIndicesVector(Indices A)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException(" maxVector :  Parameter \"A\" must be a vector and not a matrix.");
        }

        int t = 0;
        int val = 0;
        Indices maxObject = new Indices(1, 1);

        int c = A.length();
        if (c == 1)
        {// if only one element , then return.
            maxObject.set(0, 0, A.get(0, 0));
            return maxObject;
        }

        t = A.get(0, 0);

        if (A.isRowVector())
        {
            for (int j = 1; j < c; j++)
            {
                val = A.get(0, j);
                if (t >= val)
                {
                    t = t;
                }
                else
                {
                    t = val;
                }
            }
        }
        else
        {
            for (int i = 1; i < c; i++)
            {
                val = A.get(i, 0);
                if (t >= val)
                {
                    t = t;
                }
                else
                {
                    t = val;
                }
            }
        }
        maxObject.set(0, 0, t);

        return maxObject;
    }

    public static void main(String[] args)
    {

        double[][] a =
        {
                {
                        14, 13, 12, Double.NaN, 14
                },
                {
                        3, Double.NaN, 7, 11, 14
                },
                {
                        9, 7, 9, 3, 6
                },
                {
                        7, 0, 12, 6, 13
                }
        };

        Matrix A = new Matrix(a);

        System.out.println("----- A -----");
        A.print(4, 0);

        Matrix max = max(A);

        System.out.println("----- max -----");
        max.print(4, 0);

        /*
         * Indices A = SampleMatrices.generateIndices(); Indices row =
         * A.getRowAt(2); Indices col = A.getColumnAt(4); MaxInd M = new
         * MaxInd(col, Dimension.COL); Indices Mr = (Indices)M.getMaxObject();
         * System.out.println("----- Mr -----"); Mr.print(4,0); Indices Ir =
         * M.getIndices(); System.out.println("----- Ir -----");
         * Ir.plus(1).print(4,0);
         */
    }

}// ////////////////////////// End Class Definition
// /////////////////////////////
