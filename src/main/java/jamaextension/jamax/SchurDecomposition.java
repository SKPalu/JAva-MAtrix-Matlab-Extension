/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax;

import jamaextension.jampackx.JampackException;
import jamaextension.jampackx.Schur;
import jamaextension.jampackx.Zmat;
import jamaextension.jampackx.Zutmat;

/**
 * 
 * @author Feynman Perceptrons
 */
public class SchurDecomposition
{

    private Zutmat T;
    private Zmat U;

    public SchurDecomposition(Matrix A)
    {
        if (!A.isSquare())
        {
            throw new IllegalArgumentException(" SchurDecomposition: Matrix \"A\" must be square.");
        }
        Schur schur = null;// Schur(Zmat A);
        Zmat zmat = new Zmat(A.getArray());
        try
        {
            schur = new Schur(zmat);
        }
        catch (JampackException jpe)
        {
            jpe.printStackTrace();
        }

        U = schur.U;
        T = schur.T;
    }

    public Matrix getU()
    {
        return new Matrix(U.getRe());
    }

    public Matrix getT()
    {
        return new Matrix(T.getRe());
    }

    public Matrix getImagU()
    {
        return new Matrix(U.getIm());
    }

    public Matrix getImagT()
    {
        return new Matrix(T.getIm());
    }

    public static void main(String[] args)
    {
        double[][] a =
        {
                {
                        1, 4, 9, 4, 17, 4
                },
                {
                        7, 12, 19, 13, 10, 14
                },
                {
                        16, 5, 9, 17, 14, 6
                },
                {
                        0, 4, 8, 0, 9, 11
                },
                {
                        3, 0, 17, 14, 6, 3
                },
                {
                        4, 15, 11, 8, 4, 14
                }
        };

        Matrix A = new Matrix(a);
        SchurDecomposition schur = new SchurDecomposition(A);
        Matrix U = schur.getU();
        Matrix T = schur.getT();

        Matrix ImU = schur.getImagU();
        Matrix ImT = schur.getImagT();

        /*
         * System.out.println("----- U -----"); U.print(8,4);
         * 
         * System.out.println("----- T -----"); T.print(8,4);
         * 
         * System.out.println("----- ImU -----"); ImU.print(8,4);
         * 
         * System.out.println("----- ImT -----"); ImT.print(8,4);
         */

        ComplexMatrix u = new ComplexMatrix(U, ImU);
        ComplexMatrix t = new ComplexMatrix(T, ImT);

        ComplexMatrix UTU = u.times(t).times(u.transpose());

        Matrix Re = UTU.re();
        Matrix Im = UTU.im();

        System.out.println("----- A -----");
        A.print(8, 0);

        System.out.println("----- Re -----");
        Re.print(8, 4);

        System.out.println("----- Im -----");
        Im.print(8, 4);

    }

}// --------------------------- End Class Definition
// ----------------------------

