/*
 * NdGridInd.java
 *
 * Created on 6 January 2008, 10:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.elmat;

import jamaextension.jamax.Indices;

/**
 * 
 * @author Feynman Perceptrons
 */
public class NdGridInd extends Grid
{

    /**
     * Creates a new instance of NdGridInd
     * 
     * @param A
     * @param B
     */
    public NdGridInd(Indices A, Indices B)
    {
        if (A == null)
        {
            throw new IllegalArgumentException("NdGridInd : Parameter \"A\" must be non-null.");
        }
        if (!A.isVector())
        {
            throw new IllegalArgumentException("NdGridInd : Parameter \"A\" must be a vector and not a matrix.");
        }

        if (B == null)
        {
            throw new IllegalArgumentException("NdGridInd : Parameter \"B\" must be non-null.");
        }
        if (!B.isVector())
        {
            throw new IllegalArgumentException("NdGridInd : Parameter \"b\" must be a vector and not a matrix.");
        }

        computeGrids(A, B);
    }

    public NdGridInd(int[] A, int[] B)
    {
        if (A == null)
        {
            throw new IllegalArgumentException("NdGridInd : Parameter \"A\" must be non-null.");
        }
        if (B == null)
        {
            throw new IllegalArgumentException("NdGridInd : Parameter \"B\" must be non-null.");
        }
        computeGrids(new Indices(A), new Indices(B));
    }

    public NdGridInd(Indices A, int[] B)
    {
        if (A == null)
        {
            throw new IllegalArgumentException("NdGridInd : Parameter \"A\" must be non-null.");
        }
        if (!A.isVector())
        {
            throw new IllegalArgumentException("NdGridInd : Parameter \"A\" must be a vector and not a matrix.");
        }

        if (B == null)
        {
            throw new IllegalArgumentException("NdGridInd : Parameter \"B\" must be non-null.");
        }

        computeGrids(A, new Indices(B));
    }

    public NdGridInd(int[] A, Indices B)
    {
        if (A == null)
        {
            throw new IllegalArgumentException("NdGridInd : Parameter \"A\" must be non-null.");
        }
        // f(!A.isVector()){ throw new
        // IllegalArgumentException("NdGridInd : Parameter \"A\" must be a vector and not a matrix.");
        // }

        if (B == null)
        {
            throw new IllegalArgumentException("NdGridInd : Parameter \"B\" must be non-null.");
        }
        if (!B.isVector())
        {
            throw new IllegalArgumentException("NdGridInd : Parameter \"b\" must be a vector and not a matrix.");
        }

        computeGrids(new Indices(A), B);
    }

    private void computeGrids(Indices A, Indices B)
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

    public Indices getFirst()
    {
        return (Indices) this.getFirstObjectArray();
    }

    public Indices getSecond()
    {
        return (Indices) this.getSecondObjectArray();
    }

    public static void main(String[] args)
    {
        Indices A = new Indices(new int[][]
        {
            {
                    -2, -1, 0, 1, 2, 3, 4
            }
        });
        Indices B = new Indices(new int[][]
        {
            {
                    2, 3, 4
            }
        });

        NdGridInd NG = new NdGridInd(A, B);
        A = NG.getFirst();
        B = NG.getSecond();

        System.out.println("----- A -----");
        A.print(4, 0);

        System.out.println("----- B -----");
        B.print(4, 0);
    }
}
