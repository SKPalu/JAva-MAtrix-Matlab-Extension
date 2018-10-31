/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.unsupervised.subspace.lasso;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.MaxMat;
import jamaextension.jamax.elfun.JElfun;

/**
 * 
 * @author Sione
 */
public class LeastAngleRegression
{

    // X, y, method, stop, useGram, Gram, trace
    private Matrix predictor;
    private Matrix response;
    private Matrix gramMatrix;
    private boolean trace = false;
    private LassoMethod method = LassoMethod.LARS;
    private boolean useGram = true;
    private int numberOfIterations;
    private int stop = 0;
    private boolean built = false;
    private Matrix beta;

    public LeastAngleRegression()
    {
    }

    public LeastAngleRegression(Matrix predictor, Matrix response)
    {
    }

    /**
     * @param predictor
     *            the predictor to set
     */
    public void setPredictor(Matrix predictor)
    {
        this.predictor = predictor;
    }

    /**
     * @param response
     *            the response to set
     */
    public void setResponse(Matrix response)
    {
        this.response = response;
    }

    /**
     * @param gramMatrix
     *            the gramMatrix to set
     */
    public void setGramMatrix(Matrix gramMatrix)
    {
        this.gramMatrix = gramMatrix;
    }

    /**
     * @param trace
     *            the trace to set
     */
    public void setTrace(boolean trace)
    {
        this.trace = trace;
    }

    /**
     * @param method
     *            the method to set
     */
    public void setMethod(LassoMethod method)
    {
        this.method = method;
    }

    /**
     * @param useGram
     *            the useGram to set
     */
    public void setUseGram(boolean useGram)
    {
        this.useGram = useGram;
    }

    /**
     * @return the built
     */
    public boolean isBuilt()
    {
        return built;
    }

    public void build()
    {
        dataValidation();

        boolean lasso = false;
        if (method == LassoMethod.LASSO)
        {
            lasso = true;
        }

        Matrix X = predictor;
        Matrix y = response;

        // LARS variable setup
        int n = X.getRowDimension();
        int p = X.getColumnDimension();
        int nvars = Math.min(n - 1, p);
        int maxk = numberOfIterations;
        if (maxk < (8 * nvars))
        {
            maxk = 8 * nvars;
        } // Maximum number of iterations

        if (stop == 0)
        {
            beta = Matrix.zeros(2 * nvars, p);
        }
        else if (stop < 0)
        {
            beta = Matrix.zeros(2 * (-stop), p);
        }
        else
        {
            beta = Matrix.zeros(100, p);
        }

        Matrix mu = Matrix.zeros(n, 1); // current "position" as LARS travels
                                        // towards lsq solution
        Indices I = Indices.linspace(0, p - 1);// 1:p; // inactive set
        Indices A = null; // active set

        // Calculate Gram matrix if necessary
        if (gramMatrix == null && useGram)
        {
            gramMatrix = X.transpose().times(X); // Precomputation of the Gram
                                                 // matrix. Fast but memory
                                                 // consuming.
        }

        Matrix R = null; // Cholesky factorization R'R = X'X where R is upper
                         // triangular

        boolean lassocond = false; // LASSO condition boolean
        boolean stopcond = false; // Early stopping condition boolean
        int k = 0; // Iteration count
        int vars = 0; // Current number of variables

        if (trace)
        {
            // disp(sprintf('Step\tAdded\tDropped\t\tActive set size'));
        }

        // ////////////////////////////////////////////////////////////////////////
        // LARS main loop
        while (vars < nvars && !stopcond && k < maxk)
        {
            k = k + 1;
            Matrix c = X.transpose().times(y.minus(mu));// X'*(y - mu);
            int[] arr = I.getRowPackedCopy();
            Matrix temp = c.getEls(arr).abs();
            // [C j] = max(abs(c(I)));
            MaxMat max = new MaxMat(temp);
            Matrix C = (Matrix) max.getMaxObject();
            Indices j = max.getIndices();
            // j = I(j);
            arr = j.getColumnPackedCopy();
            j = I.getEls(arr);
            int[] arr2 = null;
            Indices tmpInd = null;

            if (!lassocond)
            { // if a variable has been dropped, do one iteration with this
              // configuration (don't add new one right away)
                if (!useGram)
                {
                    arr = j.getColumnPackedCopy();
                    arr2 = A.getColumnPackedCopy();
                    R = cholinsert(R, X.getColumns(arr), X.getColumns(arr2));// (R,X(:,j),X(:,A));
                }
                // A = [A j];
                if (A != null)
                {
                    A = A.mergeH(j);
                }
                // I(I == j) = [];
                tmpInd = I.EQ(j).find();
                if (tmpInd != null)
                {
                    arr = tmpInd.getRowPackedCopy();
                    I = I.removeCols(arr);
                }
                vars = vars + 1;
                if (trace)
                {
                    // disp(sprintf('%d\t\t%d\t\t\t\t\t%d', k, j, vars));
                }
            }

            // s = sign(c(A)); // get the signs of the correlations
            arr = A.getRowPackedCopy();
            Matrix s = JDatafun.sign(c.getElements(arr));

            Matrix GA1 = null;
            Matrix AA = null;
            Matrix w = null;

            if (useGram)
            {
                Matrix S = s.times(Matrix.ones(1, vars));
                arr = A.getRowPackedCopy();
                // GA1 = inv(Gram(A,A).*S'.*S)* Matrix.ones(vars,1);
                GA1 = gramMatrix.getMatrix(arr, arr).arrayTimes(S.transpose()).arrayTimes(S).inverse();
                GA1 = GA1.times(Matrix.ones(vars, 1));
                // AA = 1/sqrt(sum(GA1));
                AA = JElfun.sqrt(JDatafun.sum(GA1)).reciprocate();
                w = AA.times(GA1).arrayTimes(s); // weights applied to each
                                                 // active variable to get
                                                 // equiangular direction
            }
            else
            {
                GA1 = R.inverse().times(R.transpose().inverse().times(s));// R\(R'\s);
                // AA = 1/sqrt(sum(GA1.*s));
                AA = JElfun.sqrt(JDatafun.sum(GA1.arrayTimes(s))).reciprocate();
                // w = AA*GA1;
                w = AA.times(GA1);
            }

            arr = A.getRowPackedCopy();
            Matrix u = X.getColumns(arr).times(w);// X(:,A)*w; // equiangular
                                                  // direction (unit vector)

            Matrix tempMat = null;
            Matrix tempMat2 = null;
            Matrix gamma = null;
            if (vars == nvars)
            { // if all variables active, go all the way to the lsq solution
                gamma = C.inverse().times(AA);// C/AA;
            }
            else
            {
                Matrix a = X.transpose().times(u);// X'*u; // correlation
                                                  // between each variable and
                                                  // eqiangular vector
                Matrix cI = c.getElements(I.getRowPackedCopy());
                Matrix aI = a.getElements(I.getRowPackedCopy());
                tempMat = C.minus(cI).arrayRightDivide(AA.minus(aI));
                tempMat2 = C.plus(cI).arrayRightDivide(AA.plus(aI));
                temp = tempMat.mergeV(tempMat2);// [(C - c(I))./(AA - a(I)); (C
                                                // + c(I))./(AA + a(I))];
                // gamma = min([temp(temp > 0); C/AA]);
                tmpInd = temp.GT(0.0).find();
                if (tmpInd != null)
                {
                    temp = temp.getElements(tmpInd.getRowPackedCopy());
                    gamma = JDatafun.min(temp.mergeV(C.inverse().times(AA)));
                }
                else
                {
                    gamma = JDatafun.min(C.inverse().times(AA));
                }
            }

            // LASSO modification
            if (lasso)
            {
                lassocond = false;
                // temp = -beta(k,A)./w';
                temp = beta.getMatrix(k, k, A.getRowPackedCopy()).uminus().arrayRightDivide(w.transpose());
                // [gamma_tilde] = min([temp(temp > 0) gamma]);
                Matrix gamma_tilde = null;
                tmpInd = temp.GT(0.0).find();
                if (tmpInd != null)
                {
                    temp = temp.getElements(tmpInd.getRowPackedCopy());
                    gamma_tilde = JDatafun.min(temp.mergeH(gamma));
                }
                else
                {
                    gamma_tilde = JDatafun.min(gamma);
                }

                // j = find(temp == gamma_tilde);
                j = temp.EQ(gamma_tilde).find();
                if (gamma_tilde.LT(gamma).trueAll())
                {
                    gamma = gamma_tilde;
                    lassocond = true;
                }
            }

            mu = mu.plus(gamma.times(u));// mu + gamma*u;
            if (beta.getRowDimension() < (k + 1))
            {
                beta = beta.mergeV(Matrix.zeros(beta.getRowDimension(), p));// [beta;
                                                                            // zeros(size(beta,1),
                                                                            // p)];
            }

            arr = A.getRowPackedCopy();
            // beta(k+1,A) = beta(k,A) + gamma*w';
            tempMat = beta.getMatrix(k, arr).plus(gamma.times(w.transpose()));
            beta.setMatrix(k + 1, arr, tempMat);

            // Early stopping at specified bound on L1 norm of beta
            if (stop > 0)
            {
                double t2 = JDatafun.sum(beta.getRowAt(k + 1).abs()).start();
                ;// sum(abs(beta(k+1,:)));
                if (t2 >= (double) stop)
                {
                    double t1 = JDatafun.sum(beta.getRowAt(k).abs()).start();// sum(abs(beta(k,:)));
                    double ss = ((double) stop - t1) / (t2 - t1); // interpolation
                                                                  // factor 0 <
                                                                  // s < 1
                    // beta(k+1,:) = beta(k,:) + ss*(beta(k+1,:) - beta(k,:));
                    tempMat = beta.getRowAt(k + 1).minus(beta.getRowAt(k)).arrayTimes(ss);
                    tempMat = beta.getRowAt(k).plus(tempMat);
                    beta.setRowAt(k + 1, tempMat);
                    stopcond = true;
                }
            }

            // If LASSO condition satisfied, drop variable from active set
            if (lassocond == true)
            {
                if (!useGram)
                {
                    R = choldelete(R, j);
                }
                arr = j.getRowPackedCopy();
                // I = [I A(j)];
                I = I.mergeH(A.getColumns(arr));
                // A(j) = [];
                A = A.removeCols(arr);
                vars = vars - 1;
                if (trace)
                {
                    // disp(sprintf('%d\t\t\t\t%d\t\t\t%d', k, j, vars));
                }
            }

            // Early stopping at specified number of variables
            if (stop < 0)
            {
                stopcond = (vars >= -stop);
            }
        } // end while
          // ////////////////////////////////////////////////////////////////////////

        // trim beta
        if (beta.getRowDimension() > (k + 1))
        {
            // beta(k+2:end, :) = [];
            int rows = beta.getRowDimension();
            int[] arr = Indices.linspace(k + 1, rows - 1).getRowPackedCopy();
            beta = beta.removeRowsAt(arr);
        }

        if (k == maxk)
        {
            // disp('LARS warning: Forced exit. Maximum number of iteration
            // reached.');
        }

        built = true;
    }// end method

    private void dataValidation()
    {
        if (response == null)
        {
            throw new IllegalArgumentException(" dataValidation : The response parameter 'response' must be non-null.");
        }
        if (!response.isColVector())
        {
            throw new IllegalArgumentException(
                    " dataValidation : The response parameter response' must be a column vector.");
        }

        if (predictor == null)
        {
            throw new IllegalArgumentException(" dataValidation : The predictor parameter 'x' must be non-null.");
        }
        if (predictor.getRowDimension() != response.length())
        {
            throw new IllegalArgumentException(
                    " dataValidation : The length of \"response\" and the number of rows in \"predictor\" must be the same.");
        }
    }

    /**
     * @return the beta
     */
    public Matrix getBeta()
    {
        if (built == false)
        {
            throw new IllegalArgumentException(" getBeta : The method 'build' must be called first.");
        }
        return beta;
    }

    /**
     * @param numberOfIterations
     *            the numberOfIterations to set
     */
    public void setNumberOfIterations(int numberOfIterations)
    {
        this.numberOfIterations = numberOfIterations;
    }

    /**
     * @param stop
     *            the stop to set
     */
    public void setStop(int stop)
    {
        this.stop = stop;
    }

    private Matrix cholinsert(Matrix R, Matrix x, Matrix X)
    {
        Matrix diag_k = x.transpose().times(x); // diagonal element k in X'X
                                                // matrix
        Matrix retR = null;
        if (R == null)
        {
            retR = JElfun.sqrt(diag_k);
        }
        else
        {
            Matrix col_k = x.transpose().times(X);
            ; // elements of column k in X'X matrix
            Matrix R_k = R.transpose().inverse().times(col_k.transpose());// R'\col_k';
                                                                          // //
                                                                          // R'R_k
                                                                          // =
                                                                          // (X'X)_k,
                                                                          // solve
                                                                          // for
                                                                          // R_k
            Matrix R_kk = JElfun.sqrt(diag_k.minus(R_k.transpose().times(R_k))); // norm(x'x)
                                                                                 // =
                                                                                 // norm(R'*R),
                                                                                 // find
                                                                                 // last
                                                                                 // element
                                                                                 // by
                                                                                 // exclusion
            Matrix tmp = Matrix.zeros(1, R.getColumnDimension()).mergeH(R_kk);
            retR = R.mergeH(R_k).mergeV(tmp);// [R R_k; [zeros(1,size(R,2))
                                             // R_kk]]; // update R
        }

        return retR;
    }

    private Matrix choldelete(Matrix R, Indices j)
    {
        int[] arr = j.getRowPackedCopy();
        Matrix retR = R.removeColsAt(arr);// R(:,j) = []; // remove column j
        /*
         * int n = retR.getColumnDimension();//size(R,2); for(int k = j; k<=n;
         * k++){ p = k:k+1; [G,R(p,k)] = planerot(R(p,k)); // remove extra
         * element in column if (k < n){ R(p,k+1:n) = G*R(p,k+1:n); // adjust
         * rest of row } } R(end,:) = []; // remove zero'ed out row
         */
        return retR;
    }
}// //////////////////////////End
// Definition/////////////////////////////////////
