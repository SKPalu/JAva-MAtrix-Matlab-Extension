/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.ops;

import java.util.ArrayList;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;

/**
 * 
 * @author Feynman Perceptrons
 */
public class IntersectVec
{

    private Matrix intersection;
    private Indices firstIndices;
    private Indices secondIndices;

    public IntersectVec(Matrix matA, Matrix matB)
    {
        Matrix intersect = JOps.intersectVec(matA, matB);
        if (intersect == null)
        {
            return;
        }
        findIntersectIndices(intersect, matA, matB);
        if (intersect.isRowVector())
        {
            this.intersection = intersect.toColVector();
        }
        else
        {
            this.intersection = intersect;
        }
    }

    public IntersectVec(Indices matA, Matrix matB)
    {
        Matrix intersect = JOps.intersectVec(matA, matB);
        if (intersect == null)
        {
            return;
        }
        findIntersectIndices(intersect, Matrix.indicesToMatrix(matA), matB);
        if (intersect.isRowVector())
        {
            this.intersection = intersect.toColVector();
        }
        else
        {
            this.intersection = intersect;
        }
    }

    public IntersectVec(Matrix matA, Indices matB)
    {
        Matrix intersect = JOps.intersectVec(matA, matB);
        if (intersect == null)
        {
            return;
        }
        findIntersectIndices(intersect, matA, Matrix.indicesToMatrix(matB));
        if (intersect.isRowVector())
        {
            this.intersection = intersect.toColVector();
        }
        else
        {
            this.intersection = intersect;
        }
    }

    public Matrix getIntersection()
    {
        return this.intersection;
    }

    public Indices getFirstIndices()
    {
        return this.firstIndices;
    }

    public Indices getSecondIndices()
    {
        return this.secondIndices;
    }

    private void findIntersectIndices(final Matrix intersect, Matrix A, Matrix B)
    {
        // intersect is a row vector
        int lenIntersect = intersect.length();
        double val = 0.0;

        // Index A
        Matrix matA = null;
        if (A.isRowVector())
        {
            matA = A.toColVector();
        }
        else
        {
            matA = A;
        }

        int len = matA.length();
        ArrayList<Integer> arrayListInteger = new ArrayList<Integer>();
        for (int i = 0; i < lenIntersect; i++)
        {
            val = intersect.get(0, i);
            for (int j = 0; j < len; j++)
            {
                if (val == matA.get(j, 0))
                {
                    arrayListInteger.add(new Integer(j));
                    break;
                }
            }
        }
        int num = 0;
        int siz = arrayListInteger.size();
        this.firstIndices = new Indices(siz, 1);
        for (int i = 0; i < siz; i++)
        {
            num = arrayListInteger.get(i).intValue();
            this.firstIndices.set(i, 0, num);
        }

        // Index B
        Matrix matB = B;
        if (B.isRowVector())
        {
            matB = B.toColVector();
        }
        else
        {
            matB = B;
        }

        len = matB.length();
        arrayListInteger.clear();// empty this list first
        for (int i = 0; i < lenIntersect; i++)
        {
            val = intersect.get(0, i);
            for (int j = 0; j < len; j++)
            {
                if (val == matB.get(j, 0))
                {
                    arrayListInteger.add(new Integer(j));
                    break;
                }
            }
        }
        siz = arrayListInteger.size();
        this.secondIndices = new Indices(siz, 1);
        for (int i = 0; i < siz; i++)
        {
            num = arrayListInteger.get(i).intValue();
            this.secondIndices.set(i, 0, num);
        }
    }

    public static void main(String[] args)
    {

        double[][] mat =
        {
            {
                    19, 5, 12, 10, 18, 15, 9, 0, 16
            }
        };
        Matrix matA = new Matrix(mat);
        Matrix matB = new Matrix(new double[][]
        {
            {
                    19, 15, -8, 6
            }
        });

        IntersectVec IV = new IntersectVec(matA, matB);

        System.out.println("\n------- Isec --------");
        IV.getIntersection().print(8, 0);

        System.out.println("\n------- IA --------");
        IV.getFirstIndices().print(8);

        System.out.println("\n------- IB --------");
        IV.getSecondIndices().print(8);

    }

}// --------------------------- End Class Definition
// ----------------------------
