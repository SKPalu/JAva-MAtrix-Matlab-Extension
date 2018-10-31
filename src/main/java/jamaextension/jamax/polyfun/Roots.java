/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.polyfun;

import jamaextension.jamax.ComplexMatrix;
import jamaextension.jamax.EigenvalueDecomposition;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;

/**
 * 
 * @author Feynman Perceptrons
 */
public class Roots
{

    private ComplexMatrix roots;

    public Roots(Matrix polynom)
    {
        if (polynom == null)
        {
            throw new IllegalArgumentException("PolyRoots : Parameter \"polynom\" must be non-null.");
        }
        if (!polynom.isVector())
        {
            throw new IllegalArgumentException(
                    "PolyRoots : Parameter \"polynom\" must be either a column or row vector.");
        }
        Matrix c = null; // change to row
        if (polynom.isColVector())
        {
            c = polynom.toRowVector();
        }
        else
        {
            c = polynom;
        }

        Indices INZ = c.NEQ(0.0).find();
        // Matrix inz = c.find();

        // All elements are zero
        if (INZ == null)
        {
            return;
        }

        int lenC = c.length();
        if (lenC > 1)
        {
            while (c.get(0, 0) == 0.0)
            {// Strip leading zeros
                lenC = c.length();
                c = c.getMatrix(0, 0, 1, lenC - 1);
            }// end while
            lenC = c.length();
        }// end if

        if (lenC == 1)
        {
            return;
        }// a constant value like 'y=4, y=-7' , has no root OR infinite roots as
         // in y=0.

        Matrix C_copy = c.flipLR();
        int count = 0;
        if (lenC > 1)
        {
            while (C_copy.get(0, 0) == 0.0)
            { // Strip trailing zeros, but flag as root at zeros.
                int N = C_copy.length();
                C_copy = C_copy.getMatrix(0, 0, 1, N - 1);
                count++;
            }// end while
            lenC = c.length();
        }// end if

        if (lenC == 1 && count != 0)
        { // a function like 'y=x, y=-7*x' , has a root at zero
            this.roots = new ComplexMatrix(1, 1);// Matrix(1,1,0.0);
            return;
        }

        if (count != 0)
        {
            c = C_copy.flipLR();
            lenC = c.length();
        }

        double[][] rB = c.getArray();
        // System.out.println("***** Matrix c *****");
        // c.print(4,0);

        ComplexMatrix RE = null;// Matrix.toMatrix(real);
        // System.out.println(" lenC = "+lenC);
        if (lenC > 1)
        {
            Matrix ones = new Matrix(1, lenC - 2, 1.0d);
            Matrix a = ones.diag(-1);
            double[][] C = a.getArray();
            int cols = a.getColumnDimension();

            for (int i = 0; i < cols; i++)
            {
                if (i > (cols - 1))
                {
                    break;
                }
                else
                {
                    C[0][i] = -rB[0][i + 1] / rB[0][0];
                }
            }// end for

            EigenvalueDecomposition ed = a.eig();
            double[] real = ed.getRealEigenvalues();
            double[] imag = ed.getImagEigenvalues();

            Matrix Re = new Matrix(real).toColVector();
            Matrix Im = new Matrix(imag).toColVector();

            /*
             * ArrayList<Double> realRootVector = new ArrayList<Double>();
             * 
             * for(int i=0; i<imag.length; i++){
             * if(MathUtil.equalsWithTol(imag[i],0.0d,tol)){
             * realRootVector.add(new Double(real[i])); } }
             * 
             * if(count!=0){ //include ALL multiplicity roots at zeros. for(int
             * k=0; k<count; k++){ realRootVector.add(new Double(0.0d)); } }
             * 
             * int sizVec = realRootVector.size(); if(sizVec==0) { return null;
             * } //ALL roots are complex numbers.
             * 
             * double[][] realR = new double[sizVec][1]; for(int i=0; i<sizVec;
             * i++) { realR[i][0] = realRootVector.get(i).doubleValue(); }
             */

            RE = new ComplexMatrix(Re, Im);
        }// end if

        this.roots = RE;

    }// -----------------------

    public ComplexMatrix getRoots()
    {
        return this.roots;
    }
}
