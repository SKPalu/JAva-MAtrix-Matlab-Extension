/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax;

/**
 * 
 * @author Feynman Perceptrons
 */
public class MatVector
{

    private Matrix vector;

    public MatVector(Matrix A)
    {
        if (A == null)
        {
            throw new IllegalArgumentException("MatVector : Parameter \"A\" must be non-null.");
        }
        if (!A.isVector())
        {
            throw new IllegalArgumentException("MatVector : Parameter \"A\" must be a vector.");
        }

        double val = 0.0;
        int len = A.length();
        vector = new Matrix(1, len);
        if (A.isRowVector())
        {
            for (int i = 0; i < len; i++)
            {
                val = A.get(0, i);
                vector.set(0, i, val);
            }
        }
        else
        {
            for (int i = 0; i < len; i++)
            {
                val = A.get(i, 0);
                vector.set(0, i, val);
            }
        }

    }

    public MatVector(double[] a)
    {
        if (a == null)
        {
            throw new IllegalArgumentException("MatVector : Parameter \"a\" must be non-null.");
        }
        vector = new Matrix(a);
    }

    public double get(int ind)
    {
        int len = vector.length();
        if (ind < 0 || ind >= len)
        {
            throw new IllegalArgumentException("get : Parameter \"ind\" is out-of-bound.");
        }
        return vector.get(0, ind);
    }

    public void set(int ind, double val)
    {
        int len = vector.length();
        if (ind < 0 || ind >= len)
        {
            throw new IllegalArgumentException("set : Parameter \"ind\" is out-of-bound.");
        }
        vector.set(0, ind, val);
    }

    public Matrix getVectorCopy()
    {
        return vector.copy();
    }

    public Matrix getVectorReference()
    {
        return vector;
    }

}
