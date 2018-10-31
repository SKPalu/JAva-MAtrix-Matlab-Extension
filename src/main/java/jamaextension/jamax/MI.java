/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

import jamaextension.jamax.sparfun.SparseOld;

/**
 * 
 * @author Sione
 */
public final class MI
{

    private MI()
    {
    }

    public static boolean isSparse(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        boolean cond = obj instanceof SparseOld;
        return cond;
    }

    public static boolean isReal(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        boolean cond = !(obj instanceof ComplexMatrix);
        return cond;
    }

    public static boolean isCplx(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        boolean cond = (obj instanceof ComplexMatrix);
        return cond;
    }

    public static boolean isScalar(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        boolean cond = (obj instanceof Number);
        if (cond)
        {
            return true;
        }

        if (obj instanceof Matrix)
        {
            cond = ((Matrix) obj).isScalar();
        }
        else if (obj instanceof Matrix3D)
        {
            cond = ((Matrix3D) obj).numel() == 1;
        }
        else if (obj instanceof Indices)
        {
            cond = ((Indices) obj).isScalar();
        }
        else if (obj instanceof Indices3D)
        {
            cond = ((Indices3D) obj).numel() == 1;
        }

        return cond;
    }

    public static boolean isNumeric(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        boolean cond = false;
        if (obj instanceof Number)
        {
            cond = true;
        }
        else if (obj instanceof ComplexMatrix)
        {
            cond = true;
        }
        else if (obj instanceof Matrix)
        {
            cond = true;
        }
        else if (obj instanceof Matrix3D)
        {
            cond = true;
        }
        else if (obj instanceof Indices)
        {
            cond = true;
        }
        else if (obj instanceof Indices3D)
        {
            cond = true;
        }

        return cond;
    }

    public static boolean isChar(Object obj)
    {
        return false;
    }
}
