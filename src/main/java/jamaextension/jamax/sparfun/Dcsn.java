/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.sparfun;

/**
 * 
 * Output of numeric Cholesky, LU, or QR factorization
 * 
 */
public class Dcsn
{
    /**
     * L for LU and Cholesky, V for QR
     */
    public Sparse L;
    /**
     * U for LU, R for QR, not used for Cholesky
     */
    public Sparse U;
    /**
     * partial pivoting for LU
     */
    public int[] pinv;
    /**
     * beta [0..n-1] for QR
     */
    public double[] B;

    public Dcsn()
    {
    }

}
