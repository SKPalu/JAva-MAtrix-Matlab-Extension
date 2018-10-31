/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.MatrixFlt;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Sione
 */
class QsortMatrixFltWithIndices
{

    private MatrixFlt matrix;
    private Indices indices;
    private Dimension dimension;
    private boolean sortingIndices;

    QsortMatrixFltWithIndices(MatrixFlt A, Indices B, Dimension D, boolean allowIndSort)
    {
        String msg = "";
        if (A == null)
        {
            msg = "Matrix parameter \"A\" must be non-null.";
            throw new ConditionalRuleException("QsortMatrixWithIndices", msg);
        }
        /*
         * if (B == null) { msg = "Matrix parameter \"B\" must be non-null.";
         * throw new ConditionalRuleException("QsortMatrixWithIndices", msg); }
         * if (D == null) { msg = "Matrix parameter \"D\" must be non-null.";
         * throw new ConditionalRuleException("QsortMatrixWithIndices", msg); }
         */

        if (B != null)
        {
            if (A.sizeIndices().NEQ(B.size()).anyBoolean())
            {
                msg = "Dimensions/sizes of parametera \"A\" and \"B\" must equal.";
                throw new ConditionalRuleException("QsortMatrixWithIndices", msg);
            }
        }

        this.matrix = A;
        this.indices = B;
        this.dimension = D;
        this.sortingIndices = allowIndSort;

    }

    protected void setM(int i, int j, float doubleVal)
    {
        this.getMatrix().set(i, j, doubleVal);

    }

    protected void setI(int i, int j, int intVal)
    {
        if (this.isSortingIndices())
        {
            this.getIndices().set(i, j, intVal);
        }
    }

    protected void setElementAtM(int i, float doubleVal)
    {

        this.getMatrix().setElementAt(i, doubleVal);

    }

    protected void setElementAtI(int i, int intVal)
    {
        if (this.isSortingIndices())
        {
            this.getIndices().setElementAt(i, intVal);
        }
    }

    protected float getM(int i, int j)
    {
        return this.getMatrix().get(i, j);
    }

    /*
     * Return a negative one if the indices is not required to be sorted.
     */
    protected int getI(int i, int j)
    {
        int valInt = -1;
        if (this.isSortingIndices())
        {
            valInt = this.getIndices().get(i, j);
        }
        return valInt;
    }

    protected float getElementAtM(int i)
    {
        return this.getMatrix().getElementAt(i);
    }

    /*
     * Return a negative one if the indices is not required to be sorted.
     */
    protected int getElementAtI(int i)
    {
        int valInt = -1;
        if (this.isSortingIndices())
        {
            valInt = this.getIndices().getElementAt(i);
        }
        return valInt;
    }

    /**
     * @return the matrix
     */
    protected MatrixFlt getMatrix()
    {
        return matrix;
    }

    /**
     * @return the indices
     */
    protected Indices getIndices()
    {
        return indices;
    }

    /**
     * @return the dimension
     */
    protected Dimension getDimension()
    {
        return dimension;
    }

    /**
     * @return the sortingIndices
     */
    protected boolean isSortingIndices()
    {
        return sortingIndices;
    }

    /**
     * @param matrix
     *            the matrix to setM
     */
    protected void setMatrix(MatrixFlt matrix)
    {
        this.matrix = matrix;
    }

    /**
     * @param indices
     *            the indices to setM
     */
    protected void setIndices(Indices indices)
    {
        this.indices = indices;
    }

    protected int length()
    {
        return this.matrix.length();
    }

    protected int getNumRows()
    {
        return this.matrix.getRowDimension();
    }

    protected int getNumCols()
    {
        return this.matrix.getColumnDimension();
    }

    protected boolean isVector()
    {
        return this.matrix.isVector();
    }

    protected boolean isColVector()
    {
        return this.matrix.isColVector();
    }

    protected boolean isRowVector()
    {
        return this.matrix.isRowVector();
    }
}
