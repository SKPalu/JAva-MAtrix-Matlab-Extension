/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.sparfun;

/**
 * 
 * Output of symbolic Cholesky, LU, or QR analysis.
 * 
 */
public class Dcss
{
    /**
     * inverse row perm. for QR, fill red. perm for Chol
     */
    public int[] pinv;
    /**
     * fill-reducing column permutation for LU and QR
     */
    public int[] q;
    /**
     * elimination tree for Cholesky and QR
     */
    public int[] parent;
    /**
     * column pointers for Cholesky, row counts for QR
     */
    public int[] cp;
    /**
     * leftmost[i] = min(find(A(i,:))), for QR
     */
    public int[] leftmost;
    /**
     * # of rows for QR, after adding fictitious rows
     */
    public int m2;
    /**
     * # entries in L for LU or Cholesky; in V for QR
     */
    public int lnz;
    /**
     * # entries in U for LU; in R for QR
     */
    public int unz;

    public Dcss()
    {
    }

}
