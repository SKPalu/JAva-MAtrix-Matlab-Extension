/*
 * NdGridMat.java
 *
 * Created on 6 January 2008, 09:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.elmat;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;

/**
 * 
 * @author Feynman Perceptrons
 */
public class NdGridMat extends Grid
{

    public NdGridMat(Indices A, Indices B)
    {
        if (A == null)
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"A\" must be non-null.");
        }
        if (!A.isVector())
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"A\" must be a vector and not a matrix.");
        }

        if (B == null)
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"B\" must be non-null.");
        }
        if (!B.isVector())
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"b\" must be a vector and not a matrix.");
        }

        computeGrids(Matrix.indicesToMatrix(A), Matrix.indicesToMatrix(B));
    }

    public NdGridMat(Indices A, Matrix B)
    {
        if (A == null)
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"A\" must be non-null.");
        }
        if (!A.isVector())
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"A\" must be a vector and not a matrix.");
        }

        if (B == null)
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"B\" must be non-null.");
        }
        if (!B.isVector())
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"b\" must be a vector and not a matrix.");
        }

        computeGrids(Matrix.indicesToMatrix(A), B);
    }

    public NdGridMat(Matrix A, Indices B)
    {
        if (A == null)
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"A\" must be non-null.");
        }
        if (!A.isVector())
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"A\" must be a vector and not a matrix.");
        }

        if (B == null)
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"B\" must be non-null.");
        }
        if (!B.isVector())
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"b\" must be a vector and not a matrix.");
        }

        computeGrids(A, Matrix.indicesToMatrix(B));
    }

    /**
     * Creates a new instance of NdGridMat
     * 
     * @param A
     * @param B
     */
    public NdGridMat(Matrix A, Matrix B)
    {
        if (A == null)
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"A\" must be non-null.");
        }
        if (!A.isVector())
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"A\" must be a vector and not a matrix.");
        }

        if (B == null)
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"B\" must be non-null.");
        }
        if (!B.isVector())
        {
            throw new IllegalArgumentException("NdGridMat : Parameter \"b\" must be a vector and not a matrix.");
        }

        computeGrids(A, B);
    }

    private void computeGrids(Matrix A, Matrix B)
    {
        int rows = A.length();
        int cols = B.length();

        if (A.isColVector())
        {
            this.firstObjectArray = A.repmat(1, cols);
        }
        else
        {
            this.firstObjectArray = A.toColVector().repmat(1, cols);
        }

        if (B.isRowVector())
        {
            this.secondObjectArray = B.repmat(rows, 1);
        }
        else
        {
            this.secondObjectArray = B.toRowVector().repmat(rows, 1);
        }
    }

    public Matrix getFirst()
    {
        return (Matrix) this.getFirstObjectArray();
    }

    public Matrix getSecond()
    {
        return (Matrix) this.getSecondObjectArray();
    }

    public static void main(String[] args)
    {
        Matrix A = new Matrix(new double[][]
        {
            {
                    -2, -1, 0, 1, 2, 3, 4
            }
        });
        Matrix B = new Matrix(new double[][]
        {
            {
                    2, 3, 4
            }
        });

        NdGridMat NG = new NdGridMat(A, B);
        A = NG.getFirst();
        B = NG.getSecond();

        System.out.println("----- A -----");
        A.print(4, 0);

        System.out.println("----- B -----");
        B.print(4, 0);
    }
}
