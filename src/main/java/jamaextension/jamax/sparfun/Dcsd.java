/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.sparfun;

/**
 * 
 * Output of Dulmage-Mendelsohn decomposition.
 * 
 */
public class Dcsd
{
    /**
     * size m, row permutation
     */
    public int[] p;
    /**
     * size n, column permutation
     */
    public int[] q;
    /**
     * size nb+1, block k is rows r[k] to r[k+1]-1 in A(p,q)
     */
    public int[] r;
    /**
     * size nb+1, block k is cols s[k] to s[k+1]-1 in A(p,q)
     */
    public int[] s;
    /**
     * # of blocks in fine dmperm decomposition
     */
    public int nb;
    /**
     * coarse row decomposition
     */
    public int[] rr;
    /**
     * coarse column decomposition
     */
    public int[] cc;

}
