/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datatypes;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Indices3D;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.Matrix3D;

/**
 * 
 * @author Sione
 */
public class Uint8
{

    private Indices data;

    public Uint8(Object X)
    {
        String msg = "";
        if (X == null)
        {
            msg = "Parameter \"X\" must be non-null.";
            throw new ConditionalRuleException("Uint8", msg);
        }
        boolean cond = false;
        cond = !(X instanceof Number) && !(X instanceof Matrix) && !(X instanceof Matrix3D) && !(X instanceof Indices)
                && !(X instanceof Indices3D);

        if (cond)
        {
            msg = "Parameter \"X\" must be an instance of \"Number\", \"Matrix\", \"Matrix3D\", \"Indices\" or \"Indices3D\".";
            throw new ConditionalRuleException("Uint8", msg);
        }

        int rows = 0;
        int cols = 0;
        int val = 0;

        if (X instanceof Number)
        {
            val = uint8((Number) X);
            data = new Indices(1, 1, val);
        }
        else if (X instanceof Matrix)
        {
            Matrix mat = (Matrix) X;
            if (mat.isNull())
            {
                msg = "Matrix parameter \"X\" must be non-empty.";
                throw new ConditionalRuleException("Uint8", msg);
            }
            rows = mat.getRowDimension();
            cols = mat.getColumnDimension();
            data = new Indices(rows, cols);
            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    val = uint8(mat.get(i, j));
                    data.set(i, j, val);
                }
            }
        }
        else if (X instanceof Matrix3D)
        {
            msg = "To be implemented. TO DO.";
            throw new ConditionalRuleException("Uint8", msg);
        }
        else if (X instanceof Indices)
        {
            Indices ind = (Indices) X;
            if (ind.isNull())
            {
                msg = "Indices parameter \"X\" must be non-empty.";
                throw new ConditionalRuleException("Uint8", msg);
            }
            rows = ind.getRowDimension();
            cols = ind.getColumnDimension();
            data = new Indices(rows, cols);
            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    val = uint8(ind.get(i, j));
                    data.set(i, j, val);
                }
            }
        }
        else if (X instanceof Indices3D)
        {
            msg = "To be implemented. TO DO.";
            throw new ConditionalRuleException("Uint8", msg);
        }
        else
        {
            msg = "Parameter \"X\" must be an instance of \"Number\", \"Matrix\", \"Matrix3D\", \"Indices\" or \"Indices3D\".";
            throw new ConditionalRuleException("Uint8", msg);
        }
    }

    public static int uint8(Number X)
    {
        int val = 0;
        String msg = "";
        if (X == null)
        {
            msg = "Number \"X\" must be non-null.";
            throw new ConditionalRuleException("uint8", msg);
        }
        if (X instanceof Double)
        {
            if (Double.isNaN((Double) X))
            {
                val = 0;
            }
            else if (((Double) X) < 0.0)
            {
                val = 0;
            }
            else if (((Double) X) > 255.0)
            {
                val = 255;
            }
            else
            {
                val = ((Number) X).intValue();
            }
        }
        else if (X instanceof Float)
        {
            if (Float.isNaN((Float) X))
            {
                val = 0;
            }
            else if (((Float) X) < 0.0f)
            {
                val = 0;
            }
            else if (((Float) X) > 255.0f)
            {
                val = 255;
            }
            else
            {
                val = ((Number) X).intValue();
            }
        }
        else if (X instanceof Long)
        {
            if (((Long) X) < 0L)
            {
                val = 0;
            }
            else if (((Long) X) > 255L)
            {
                val = 255;
            }
            else
            {
                val = ((Number) X).intValue();
            }
        }
        else if (X instanceof Integer)
        {
            if (((Integer) X) < 0)
            {
                val = 0;
            }
            else if (((Integer) X) > 255)
            {
                val = 255;
            }
            else
            {
                val = ((Number) X).intValue();
            }
        }
        else
        {
            msg = "Currently supported number \"X\" must be an instance of \"Double\", \"Float\", \"Long\" or \"Integer\".";
            throw new ConditionalRuleException("uint8", msg);
        }

        return val;
    }

    /**
     * @return the data
     */
    public Indices getData()
    {
        return data;
    }
}
