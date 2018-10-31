/*
 * Princomp.java
 *
 * Created on 1 July 2007, 01:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.stats;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.SingularValueDecomposition;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;

/**
 * 
 * @author Feynman Perceptrons
 */
public class Princomp
{

    private Matrix x;
    private boolean econFlag = false;

    private Matrix coeff;
    private Matrix score;
    private Matrix latent;
    private Matrix tsquare;

    /** Creates a new instance of Princomp */
    public Princomp(Matrix x)
    {
        this(x, false);
    }

    /** Creates a new instance of Princomp */
    public Princomp(Matrix x, boolean econFlag)
    {
        if (x == null)
        {
            throw new IllegalArgumentException("Princomp : Parameter 'x' must be non-null.");
        }
        int m = x.getRowDimension();
        int n = x.getColumnDimension();

        if (n < 5)
        {
            throw new IllegalArgumentException("Princomp : Parameter 'x' must have at least 5 variables.");
        }
        if (m < 5 * n)
        {
            throw new IllegalArgumentException("Princomp : Parameter 'x' must have at least " + (5 * n)
                    + " observations.");
        }

        this.x = x;
        this.econFlag = econFlag;
    }

    public void build()
    {
        // Center X by subtracting off column means
        int n = x.getRowDimension();
        int p = x.getColumnDimension();
        Matrix x0 = x.minus(JDatafun.mean(x, Dimension.ROW).repmat(n, 1));// x -
                                                                          // repmat(mean(x,1),n,1);
        int r = Math.min(n - 1, p); // max possible rank of X0

        SingularValueDecomposition svd = x0.svd();
        Matrix U = svd.getU();
        Matrix sigma = svd.getS();
        coeff = svd.getV();

        if (!econFlag)
        {
            // When econFlag is 'econ', only (n-1) components should be
            // returned.
            // See comment below.
            if (n <= p)
            {// && isequal(econFlag, 'econ')
             // coeff(:,n) = [];
                coeff = coeff.removeColAt(n - 1);
            }
        }
        else
        {// /////////////////////////////////
         // Project X0 onto the principal component axes to get the scores.
            if (n == 1)
            { // sigma might have only 1 row
                sigma = sigma.getAsMatrix(0, 0);
            }
            else
            {
                sigma = sigma.diag();
            }
            score = U.arrayTimes(sigma.transpose().repmat(n, 1)); // == x0*coeff
            sigma = sigma.arrayRightDivide(Math.sqrt((double) n - 1.0));

            /*
             * 
             * // When X has at least as many variables as observations,
             * eigenvalues // n:p of S are exactly zero. if (n <= p){ // When
             * econFlag is 'econ', nothing corresponding to the zero //
             * eigenvalues should be returned. svd(,'econ') won't have //
             * returned anything corresponding to components (n+1):p, so we //
             * just have to cut off the n-th component. if (!econFlag){ sigma(n)
             * = []; coeff = coeff.removeColAt(n-1);//coeff(:,n) = []; score =
             * score.removeColAt(n-1);//score(:,n) = []; // Otherwise, set those
             * eigenvalues and the corresponding scores to // exactly zero.
             * svd(,0) won't have returned columns of U //corresponding to
             * components (n+1):p, need to fill those out. } else{ sigma(n:p,1)
             * = 0; // make sure this extends as a column score(:,n:p) = 0; } }
             */

        }// ////////////////////////////////////
    }// end method

}// -------------------------- End Class Definition
// -----------------------------
