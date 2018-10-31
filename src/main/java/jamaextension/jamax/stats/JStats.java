package jamaextension.jamax.stats;

import jamaextension.jamax.CholeskyDecomposition;
import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.EigenvalueDecomposition;
import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortMat;
import jamaextension.jamax.elfun.JElfun;
import jamaextension.jamax.specfun.JSpecfun;

//import com.feynmanperceptron.derived.numeric.Cholesky;
/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public final class JStats
{

    private JStats()
    {
    }

    /**
     * Normal cumulative distribution function (cdf). P = NORMCDF(X,MU,SIGMA)
     * computes the normal cdf with mean MU and standard deviation SIGMA at the
     * values in X.
     * 
     * The size of P is the common size of X, MU and SIGMA. A scalar input
     * functions as a constant matrix of the same size as the other inputs.
     * 
     * Default values for MU and SIGMA are 0 and 1 respectively.
     */
    public static double normcdf(double x, double mu, double sigma)
    {
        double y = 0.0;
        if (sigma <= 0.0)
        {
            y = Double.NaN;
        }
        else
        {
            y = 0.5 * JSpecfun.erfc(-(x - mu) / (sigma * Math.sqrt(2.0)));
        }
        if (y > 1.0)
        {
            y = 1.0;
        }
        return y;
    }// end method

    public static double normcdf(double x, double mu)
    {
        return normcdf(x, mu, 1.0);
    }

    public static double normcdf(double x)
    {
        return normcdf(x, 0.0, 1.0);
    }

    /**
     * NORMINV Inverse of the normal cumulative distribution function (cdf). X =
     * NORMINV(P,MU,SIGMA) finds the inverse of the normal cdf with mean, MU,
     * and standard deviation, SIGMA.
     * 
     * The size of X is the common size of the input arguments. A scalar input
     * functions as a constant matrix of the same size as the other inputs.
     * 
     * Default values for MU and SIGMA are 0 and 1 respectively.
     * 
     * References: [1] M. Abramowitz and I. A. Stegun, "Handbook of Mathematical
     * Functions", Government Printing Office, 1964, 7.1.1 and 26.2.2
     */
    public static double norminv(double p, double mu, double sigma)
    {
        // Allocate space for x.
        double x = 0.0, tmp = 0.0;

        // Return NaN if the arguments are outside their respective limits.
        boolean k = (sigma <= 0.0) || (p < 0.0) || (p > 1.0);
        if (k)
        {
            tmp = Double.NaN;
            x = tmp;
        }

        // Put in the correct values when P is either 0 or 1.
        k = (p == 0.0);
        if (k)
        {
            tmp = Double.POSITIVE_INFINITY;
            x = Double.NEGATIVE_INFINITY;
        }

        k = (p == 1.0);
        if (k)
        {
            tmp = Double.POSITIVE_INFINITY;
            x = tmp;
        }

        // Compute the inverse function for the intermediate values.
        k = ((p > 0.0) && (p < 1.0) && (sigma > 0.0));
        if (k)
        { // System.out.println("EXE");
            x = Math.sqrt(2.0) * sigma * JSpecfun.erfinv(2.0 * p - 1.0) + mu;
        }

        return x;
    }// end method

    public static double norminv(double p, double mu)
    {
        return norminv(p, mu, 1.0);
    }// end method

    public static double norminv(double p)
    {
        return norminv(p, 0.0, 1.0);
    }// end method

    public static Matrix norminv(Matrix p)
    {
        int m = p.getRowDimension();
        int n = p.getColumnDimension();
        Matrix R = new Matrix(m, n);
        double val = 0.0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                val = norminv(p.get(i, j));
                R.set(i, j, val);
            }
        }
        return R;
    }

    /**
     * NORMRND Random matrices from normal distribution. R = NORMRND(MU,SIGMA)
     * returns a matrix of random numbers chosen from the normal distribution
     * with parameters MU and SIGMA.
     * 
     * The size of R is the common size of MU and SIGMA if both are matrices. If
     * either parameter is a scalar, the size of R is the size of the other
     * parameter. Alternatively, R = NORMRND(MU,SIGMA,M,N) returns an M by N
     * matrix.
     */
    public static double normrnd(double mu, double sigma)
    {
        if (sigma <= 0.0)
        {
            throw new IllegalArgumentException("normrnd : Parameter 'sigma' must be a positive number.");
        }
        double val = MathUtil.randn() * sigma + mu;
        return val;
    }// end method

    /**
     * LOGNINV Inverse of the lognormal cumulative distribution function (cdf).
     * X = LOGNINV(P,MU,SIGMA) finds the inverse of the lognormal cdf with mean,
     * MU, and standard deviation, SIGMA.
     * 
     * The size of X is the common size of the input arguments. A scalar input
     * functions as a constant matrix of the same size as the other inputs.
     * 
     * Default values for MU and SIGMA are 0 and 1 respectively.
     * 
     * Reference: [1] Evans, Merran, Hastings, Nicholas and Peacock, Brian,
     * "Statistical Distributions, Second Edition", Wiley 1993 p. 102-105.
     */
    public static double logninv(double p, double mu, double sigma)
    {
        double x = 0.0;

        if ((sigma <= 0.0) || (p < 0.0) || (p > 1.0))
        {
            return Double.NaN;
        }
        if (p == 1.0)
        {
            return Double.POSITIVE_INFINITY;
        }
        if ((p > 0.0) && (p < 1.0) && (sigma > 0.0))
        {
            x = Math.exp(Math.sqrt(2.0) * sigma * JSpecfun.erfinv(2.0 * p - 1.0) + mu);
        }

        return x;
    }// end method

    public static double logninv(double p, double mu)
    {
        return logninv(p, mu, 1.0);
    }// end method

    public static double logninv(double p)
    {
        return logninv(p, 0.0);
    }// end method

    public static Object[] ecdf(Matrix y, Indices censoring, Indices frequency, double alpha, String function)
    {// Object[] varargin){
     // [F,x,Flo,Fup,D] =
        if (y == null)
        {
            throw new IllegalArgumentException(" ecdf : Parameter 'y' must be non-null.");
        }
        if (!y.isVector())
        {
            throw new IllegalArgumentException(" ecdf : Parameter 'y' must be a vector and not a matrix.");
        }

        Matrix x = null;
        if (y.isRowVector())
        {
            x = y.toColVector();
        }
        else
        {
            x = y;
        }

        int xr = x.getRowDimension();
        // int xc = x.getColumnDimension();
        Indices cens = null;

        if (censoring == null)
        {
            cens = new Indices(xr, 1, new Boolean(false));
        }
        else
        {
            if (!censoring.isVector())
            {
                throw new IllegalArgumentException(
                        " ecdf : Parameter 'censoring' must be a vector same size as 'y' but not a matrix.");
            }
            if (!censoring.isLogical())
            {
                throw new IllegalArgumentException(" ecdf : Parameter 'censoring' must be a boolean vector.");
            }
            if (censoring.isRowVector())
            {
                cens = censoring.toColVector();
            }
            else
            {
                cens = censoring;
            }
            if (xr != cens.length())
            {
                throw new IllegalArgumentException(
                        " ecdf : Length of parameter 'censoring' must equal the length of 'x'.");
            }
        }

        Indices freq = null;

        if (frequency == null)
        {
            freq = new Indices(xr, 1, 1);
        }
        else
        {
            if (!frequency.isVector())
            {
                throw new IllegalArgumentException(
                        " ecdf : Parameter 'frequency' must be a vector same size as 'x' but not a matrix.");
            }
            if (frequency.isRowVector())
            {
                freq = frequency.toColVector();
            }
            else
            {
                freq = frequency;
            }
            if (xr != freq.length())
            {
                throw new IllegalArgumentException(
                        " ecdf : Length of parameter 'frequency' must equal the length of 'x'.");
            }
        }

        if (alpha <= 0.0 || alpha >= 1.0)
        {
            throw new IllegalArgumentException(" ecdf : Parameter 'alpha' must fall in the exclusive interval (0 , 1).");
        }

        if (function == null || "".equals(function))
        {
            throw new IllegalArgumentException(" ecdf : Parameter 'function' must not be null or empty.");
        }

        // Remove NaNs, so they will be treated as missing
        // [ignore1,ignore2,x,cens,freq] = statremovenan(x,cens,freq);
        // Check for valid function names. Go out of the way to accept three
        // possible ways to name the cumulative hazard function.
        String[] okvals =
        {
                "cdf", "survivor", "chf", "cumulative hazard", "cumhazard"
        };
        int count = 0;
        for (int i = 0; i < okvals.length; i++)
        {
            if (function.equals(okvals[i]))
            {
                break;
            }
            else
            {
                count++;
            }
        }

        if (count == okvals.length)
        {
            throw new IllegalArgumentException(
                    " ecdf : Parameter 'function' must be one of {\"cdf\", \"survivor\", \"chf\", \"cumulative hazard\", \"cumhazard\"}.");
        }

        boolean cdf_sf = (okvals.equals(okvals[0]) || okvals.equals(okvals[1]));

        String fn = function;

        /*
         * //Remove missing observations indicated by NaN's. t = ~isnan(x) &
         * ~isnan(freq) & ~isnan(cens) & freq>0; x = x(t); n = length(x); if n
         * == 0 error('stats:ecdf:NotEnoughData', 'Input sample has no valid
         * data (all missing values).'); end cens = cens(t); freq = freq(t);
         */
        // Remove missing observations indicated by NaN's.
        // t = ~isnan(x) & ~isnan(freq) & ~isnan(cens) & freq>0;
        // Sort observation data in ascending order.
        // [x,t] = sort(x);
        // Object[] objSort = JDatafun.sortInd(x);
        // --- Changed on 12/08/09 from 'SortMat' into 'QuickSortMat' -----
        // SortMat objSort = new SortMat(x); //--> old line
        QuickSort objSort = new QuickSortMat(x, true, true); // --> new line

        x = (Matrix) objSort.getSortedObject();
        Indices tt = objSort.getIndices();
        int[] t = tt.getRowPackedCopy();
        // cens = cens(t);
        cens = cens.getIndices(t, 0, 0);
        // freq = freq(t);
        freq = freq.getIndices(t, 0, 0);

        // if isa(x,'single')
        // freq = single(freq);
        // end
        // Compute cumulative sum of frequencies
        Indices totcumfreq = JDatafun.cumsum(freq);
        Indices obscumfreq = JDatafun.cumsum(freq.arrayTimes(cens));// cumsum(freq
                                                                    // .*
                                                                    // ~cens);

        Indices T = JDatafun.diff(x).EQ(0.0);
        if (T.anyBoolean())
        {
            t = T.getColumnAt(1).find().getRowPackedCopy();
            x = x.removeRowsAt(t);
            totcumfreq = totcumfreq.removeRows(t);
            obscumfreq = obscumfreq.removeRows(t);
        }

        int totalcount = totcumfreq.end();

        // Get number of deaths and number at risk at each unique X
        // D = [obscumfreq(1); diff(obscumfreq)];
        Indices D = (new Indices(1, 1, obscumfreq.get(0, 0))).mergeV(JDatafun.diff(obscumfreq));
        // N = totalcount - [0; totcumfreq(1:end-1)];
        int len = totcumfreq.length();
        T = Indices.intLinspaceIncrement(0, len - 2);
        Indices N = (new Indices(1, 1, 0)).mergeV(totcumfreq.getRows(T.getRowPackedCopy()).uminus());

        // No change in function except at a death, so remove other points
        T = D.GT(0).find();// (D>0);
        if (T != null)
        {
            t = T.getColumnAt(1).find().getRowPackedCopy();
            x = x.getMatrix(t, 0);// x(t);
            D = D.getIndices(t, 0, 0);// D(t);
            N = N.getIndices(t, 0, 0);// N(t);
        }

        Matrix Func = null;
        double F0 = 0.0;
        Matrix S = null;
        Matrix temp = Matrix.indicesToMatrix(D).arrayRightDivide(Matrix.indicesToMatrix(N));
        Matrix temp2 = null;

        if (cdf_sf)
        { // 'cdf' or 'survivor'
          // Use the product-limit (Kaplan-Meier) estimate of the survivor
          // function, transform to the CDF.
            temp = temp.uminus().plus(1.0);
            S = JDatafun.cumprod(temp);// (1 - D./N);

            if ("cdf".equals(fn))
            {// strcmp(fn, 'cdf')
                Func = S.uminus().plus(1.0);// 1 - S;
                F0 = 0.0; // starting value of this function (at x=-Inf)
            }
            else
            { // 'survivor'
                Func = S;
                F0 = 1.0;

            }
        }
        else
        {// 'cumhazard'
         // Use the Nelson-Aalen estimate of the cumulative hazard function.
            Func = JDatafun.cumsum(temp);// cumsum(D./N);
            F0 = 0.0;
        }

        // Include a starting value; required for accurate staircase plot
        x = (new Matrix(1, 1, JDatafun.min(y).get(0, 0))).mergeV(x);// [min(y);
                                                                    // x];
        Matrix F = (new Matrix(1, 1, F0)).mergeV(Func);// [F0; Func];

        Matrix se = null;

        // Get standard error of requested function
        if (cdf_sf)
        { // 'cdf' or 'survivor'
          // se = repmat(NaN,size(D));
            if (N.end() == D.end())
            {// N(end)==D(end)
                t = Indices.intLinspaceIncrement(0, N.length() - 2).getRowPackedCopy();// 1:length(N)-1;
            }
            else
            {
                t = Indices.intLinspaceIncrement(0, N.length() - 1).getRowPackedCopy();// 1:length(N);
            }

            // se(t) = S(t) .* sqrt(cumsum(D(t) ./ (N(t) .* (N(t)-D(t)))));
            Indices denom = N.getRows(t).arrayTimes(N.getRows(t).minus(D.getRows(t)));// (N(t)
                                                                                      // .*
                                                                                      // (N(t)-D(t))
            temp = Matrix.indicesToMatrix(D.getRows(t)).arrayRightDivide(denom);
            temp2 = JElfun.sqrt(JDatafun.cumsum(temp));
            se = S.getRows(t).arrayTimes(temp2);
        }
        else
        { // 'cumhazard'
            temp = Matrix.indicesToMatrix(N).arrayTimes(Matrix.indicesToMatrix(N));
            temp = Matrix.indicesToMatrix(D).arrayRightDivide(temp);
            se = JElfun.sqrt(JDatafun.cumsum(temp));// sqrt(cumsum(D ./ (N .*
                                                    // N)));
        }

        // Get confidence limits
        double zalpha = -norminv(alpha / 2.0);
        Matrix halfwidth = se.arrayTimes(zalpha);// zalpha*se;

        Matrix Flo = JDatafun.max(Func.minus(halfwidth), 0.0);// max(0,
                                                              // Func-halfwidth);
        Indices isnanHF = halfwidth.isnan();
        // Flo(isnan(halfwidth)) = NaN; //max drops NaNs, put them back
        if (isnanHF.find() != null)
        {
            Flo.setEntriesAtIndicesTo(isnanHF, Double.NaN);
        }

        Matrix Fup = null;
        if (cdf_sf)
        { // 'cdf' or 'survivor'
            Fup = JDatafun.min(Func.plus(halfwidth), 1.0);// min(1,
                                                          // Func+halfwidth);
            // Fup(isnan(halfwidth)) = NaN; % max drops NaNs
            Fup.setEntriesAtIndicesTo(isnanHF, Double.NaN);
        }
        else
        {
            Fup = Func.plus(halfwidth);// Func+halfwidth; //no restriction on
                                       // upper limit
        }

        Matrix oneNan = new Matrix(1, 1, Double.NaN);

        Flo = oneNan.mergeV(Flo);// [NaN; Flo];
        Fup = oneNan.mergeV(Fup);// [NaN; Fup];

        return new Object[]
        {
                F, x, Flo, Fup, D
        };
    }

    public static Object[] normfit(Matrix x, double alpha)
    {
        return null;
    }

    /*
     * public static Object[] normfit(Matrix x, double alpha){
     * 
     * //[m, n] = size(x); int m = x.getRowDimension(); int n =
     * x.getColumnDimension(); Matrix xx = null; if (Math.min(m,n) == 1){ xx =
     * x.toColVector(); m = Math.max(m,n); n = 1; } else { xx = x; }
     * 
     * Matrix muhat = JDatafun.mean(xx); Matrix sigmahat = JDatafun.std(xx);
     * Matrix muci = null; Matrix sigmaci = null; /* muci = zeros(2,n); sigmaci
     * = zeros(2,n);
     * 
     * tcrit = tinv([alpha/2 1-alpha/2],m-1); muci = [(muhat +
     * tcrit(1)*sigmahat/sqrt(m)); (muhat + tcrit(2)*sigmahat/sqrt(m))];
     * 
     * chi2crit = chi2inv([alpha/2 1-alpha/2],m-1); sigmaci =
     * [(sigmahat*sqrt((m-1)./chi2crit(2)));
     * (sigmahat*sqrt((m-1)./chi2crit(1)))];
     */
    // Object[] obj = new Object[]{muhat, sigmahat, muci, sigmaci};
    // return obj;
    // }
    // public static Object[] normfit(Matrix x) {
    // return normfit( x, 0.05);
    // }
    /*
     * public static Matrix mvnrnd(double mu, double sigma, int cases) {
     * if(cases == 0) {return null; } if(cases < 0){ throw new
     * IllegalArgumentException("mvnrnd : Negative index found."); }
     * CholeskyDecomposition chol = new CholeskyDecomposition(
     * 
     * new Matrix(new double[][] { new double[] { sigma } })); Matrix T =
     * chol.getL(); double p = !chol.isSPD() ? 1.0D : 0.0D; if(p != 0.0D) {
     * throw new
     * IllegalArgumentException("mvnrnd : Sigma must be a positive definite.");
     * } else { int c = 1; T = JDatafun.randn(cases, c).arrayTimes(T.get(0,
     * 0)).plus(mu); return T; } }
     */
    public static Matrix[] mvnrnd(Matrix mu, Matrix sigma)
    {
        return mvnrnd(mu, sigma, null);
    }

    public static Matrix[] mvnrnd(Matrix mu, Matrix sigma, Integer cases)
    {
        return mvnrnd(mu, sigma, cases, null);
    }

    public static Matrix[] mvnrnd(Matrix mu, Matrix sigma, Integer cases, Matrix T)
    {
        Object[] obj = mvnrnd(mu, new Matrix[]
        {
            sigma
        }, cases, T);
        Matrix r = (Matrix) obj[0];
        Matrix[] TT = (Matrix[]) obj[1];
        return new Matrix[]
        {
                r, TT[0]
        };
    }

    public static Object[] mvnrnd(Matrix mu, Matrix[] sigma)
    {
        return mvnrnd(mu, sigma, null);
    }

    public static Object[] mvnrnd(Matrix mu, Matrix[] sigma, Integer cases)
    {
        return mvnrnd(mu, sigma, cases, null);
    }

    public static Object[] mvnrnd(Matrix mu, Matrix[] sigma, Integer cases, Matrix T)
    {
        Matrix[] TT = null;
        Matrix r = null;

        if (mu == null)
        {
            throw new IllegalArgumentException("mvnrnd : Parameter 'mu' must not be null.");
        }

        if (sigma == null)
        {
            throw new IllegalArgumentException("mvnrnd : Parameter 'sigma' must not be null.");
        }

        int lenSig = sigma.length;
        int n = mu.getRowDimension();
        int d = mu.getColumnDimension();

        for (int i = 0; i < lenSig; i++)
        {
            if (!sigma[i].isSquare())
            {
                throw new IllegalArgumentException("mvnrnd : All matrices in array 'sigma' must be square.");
            }
        }

        Matrix MU = null;
        // Special case: if mu is a column vector, then use sigma to try
        // to interpret it as a row vector.
        if ((d == 1) && (sigma[0].length() == n))
        {
            MU = mu.transpose();// mu = mu';
            // [n,d] = size(mu);
            n = MU.getRowDimension();
            d = MU.getColumnDimension();
        }
        else
        {
            MU = mu;
        }

        boolean nocases = false;

        // Get size of data.
        if (cases == null)
        {
            nocases = true; // cases not supplied
        }
        else
        {
            if (cases.intValue() < 1)
            {
                throw new IllegalArgumentException("mvnrnd : CASES must be a positive integer.");
            }
            nocases = false; // cases was supplied
            if (n == cases.intValue())
            {
                // mu is ok
            }
            else if (n == 1)
            { // mu is a single row, make cases copies
                n = cases.intValue();
                MU = MU.repmat(n, 1);// mu = repmat(mu,n,1);
            }
            else
            {
                // error('stats:mvnrnd:InputSizeMismatch', 'MU must be a row
                // vector, or must have CASES rows.');
                throw new IllegalArgumentException("mvnrnd : MU must be a row vector, or must have CASES rows.");
            }
        }

        // Single covariance matrix
        if (lenSig == 1)
        {// ########################################################
         // Make sure sigma is the right size
            if (sigma[0].length() != d)
            {
                // error('stats:mvnrnd:InputSizeMismatch', 'SIGMA must be a
                // square matrix with size equal to the number of columns in
                // MU.');
                throw new IllegalArgumentException(
                        "mvnrnd : SIGMA must be a square matrix with size equal to the number of columns in MU.");
            }

            // Factor sigma unless that has already been done, using a function
            // that will perform a Cholesky-like factorization as long as the
            // sigma matrix is positive semi-definite (can have perfect
            // correlation).
            // Cholesky requires a positive definite matrix. sigma == T'*T
            if (T == null)
            {
                // [T p] = statchol(sigma);
                Object[] obj = statchol(sigma[0]);
                TT = new Matrix[]
                {
                    (Matrix) obj[0]
                };
                Boolean p = (Boolean) obj[1];
                if (!p.booleanValue())
                {// (p > 0){
                 // error('stats:mvnrnd:BadSigma', 'SIGMA must be a positive
                 // semi-definite matrix.');
                    throw new IllegalArgumentException("mvnrnd : #1) SIGMA must be a positive semi-definite matrix.");
                }
            }
            else
            {
                TT = new Matrix[]
                {
                    T
                };
            }
            // r = randn(n,size(T,1)) * T + mu;
            r = JDatafun.randn(n, TT[0].getRowDimension()).times(TT[0]).plus(MU);
        }
        else
        {// ################# Multiple covariance matrices ####################
         // mu is a single row and cases not given, rep mu out to match sigma
            if ((n == 1) && nocases)
            { // already know size(sigma,3) > 1
                n = sigma.length;// size(sigma,3);
                MU = MU.repmat(n, 1);// repmat(mu,n,1);
            }

            // Make sure sigma is the right size
            // if size(sigma,1) ~= d | size(sigma,2) ~= d
            // error('stats:mvnrnd:InputSizeMismatch', 'Each page of SIGMA must
            // be a square matrix with size equal to the number of columns in
            // MU.');
            for (int i = 0; i < lenSig; i++)
            {
                if (sigma[i].length() != d)
                {
                    throw new IllegalArgumentException(
                            "mvnrnd : Each page of SIGMA must be a square matrix with size equal to the number of columns in MU.");
                }
            }

            if (sigma.length != n)
            {// size(sigma,3) ~= n
             // error('stats:mvnrnd:InputSizeMismatch','SIGMA must have CASES
             // pages.');
                throw new IllegalArgumentException("mvnrnd : SIGMA must have CASES pages.");
            }

            r = Matrix.zeros(n, d);
            if (T == null)
            {// $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                TT = new Matrix[sigma.length];// zeros(size(sigma));
                for (int j = 0; j < sigma.length; j++)
                {
                    TT[j] = Matrix.zeros(sigma[j].length());
                }

                for (int i = 0; i < n; i++)
                {
                    Object[] Rp = statchol(sigma[i]);// statchol(sigma(:,:,i));
                    Matrix R = (Matrix) Rp[0];
                    Boolean p = (Boolean) Rp[1];
                    if (!p.booleanValue())
                    {
                        // error('stats:mvnrnd:BadSigma', 'SIGMA must be a
                        // positive semi-definite matrix.');
                        throw new IllegalArgumentException(
                                "mvnrnd : #2) SIGMA must be a positive semi-definite matrix.");
                    }
                    int Rrows = R.getRowDimension();// size(R,1);
                    // r(i,:) = randn(1,Rrows) * R + mu(i,:);
                    Matrix temp = JDatafun.randn(1, Rrows).times(R).plus(MU.getRowAt(i));
                    r.setRowAt(i, temp);

                    // T(1:Rrows,:,i) = R;
                    temp = TT[i];
                    int[] ind = Indices.intLinspaceIncrement(0, Rrows - 1).getRowPackedCopy();
                    temp.setMatrix(ind, i, R);
                }// end for

            }
            else
            {// $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                for (int i = 0; i < n; i++)
                {
                    // r(i,:) = randn(1,d) * T(:,:,i) + mu(i,:);
                    Matrix rtm = JDatafun.randn(1, d).times(TT[i]).plus(MU.getRowAt(i));
                    r.setRowAt(i, rtm);
                }
            }// $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

        }// ######################################################################

        return new Object[]
        {
                r, TT
        };
    }

    public static double unifrnd(double a, double b)
    {
        // if (true) {
        // String msg = "To Do.";
        // throw new ConditionalRuleException("unifrnd", msg);
        // }

        // Avoid a+(b-a)*rand in case a-b > realmax
        double a2 = a / 2.0;
        double b2 = b / 2.0;
        double mu = a2 + b2;
        double sig = b2 - a2;

        double r = mu + sig * (2.0 * Math.random() - 1.0);

        // Fill in elements corresponding to illegal parameter values
        if (a > b)
        {// ~isscalar(a) || ~isscalar(b)
         // r(a > b) = NaN;
            r = Double.NaN;
            // elseif a > b
            // r(:) = NaN;
        }// end

        return r;
    }

    public static Matrix unifrnd(Matrix A, Matrix B)
    {
        String msg = "";
        if (A == null || A.isNull())
        {
            msg = "Parameter \"A\" must be non-null.";
            throw new ConditionalRuleException("unifrnd", msg);
        }
        if (B == null || B.isNull())
        {
            msg = "Parameter \"B\" must be non-null.";
            throw new ConditionalRuleException("unifrnd", msg);
        }

        Indices sizA = A.sizeIndices();
        Indices sizB = B.sizeIndices();
        boolean cond = sizA.EQ(sizB).allBoolean();
        if (!cond)
        {
            msg = "Parameters \"A\" and \"B\" must have the same size.";
            throw new ConditionalRuleException("unifrnd", msg);
        }

        int row = A.getRowDimension();
        int col = A.getColumnDimension();
        double val = 0.0;

        Matrix UF = new Matrix(row, col);
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                val = unifrnd(A.get(i, j), B.get(i, j));
                UF.set(i, j, val);
            }
        }

        return UF;
    }

    private static Object[] statchol(Matrix sigma)
    {
        /*-------------------------------------------------------------------------*
         double[][] ec = {
         {101,   101,   119,    56,    37},
         {101,   202,   198,   111,   104},
         {119,   198,   202,   109,    96},
         {56,   111,   109,    61,    57},
         {37,   104,    96,    57,    58} };

         Matrix eCov = new Matrix(ec);

         Object[] obj = statchol(eCov);
         Matrix T = (Matrix)obj[0];

         Boolean p = (Boolean)obj[1];

         System.out.println("\n------- T --------");
         T.print(4,4);
         *
         *-------------------------------------------------------------------------*/
        if (!sigma.isSquare())
        {
            throw new IllegalArgumentException("statchol : SIGMA must be a square matrix.");
        }

        CholeskyDecomposition chol = new CholeskyDecomposition(sigma, true);// sigma.chol();
        Matrix T = chol.getL();

        /*
         * System.out.println("\n------- T1 --------"); T.print(4,4);
         * System.out.
         * println(" Positive Definite = "+JMatfun.isPositiveDefinite(sigma));
         */
        Boolean p = new Boolean(chol.isSPD());

        if (!chol.isSPD())
        {
            // Can get factors of the form sigma==T'*T using the eigenvalue
            // decomposition of a symmetric matrix, so long as the matrix
            // is positive semi-definite.

            // System.out.println("\n EXECUTE ");
            // [U,D] = eig((sigma+sigma')/2);
            Matrix temp = sigma.plus(sigma.transpose()).arrayRightDivide(2.0);
            EigenvalueDecomposition eig = temp.eig();
            Matrix U = eig.getV();
            Matrix D = eig.getD();
            // D = diag(D);
            D = D.diag();

            double tol = MathUtil.EPS * JDatafun.max(D).get(0, 0) * ((double) D.length());

            Indices t = D.abs().GT(tol).find();// (abs(D) > tol);
            // System.out.println("\n------- t --------");
            // t.print(4);

            if (t != null)
            {
                D = D.getFromFind(t);
            }

            // System.out.println("\n------- D --------");
            // D.print(4,4);
            int pp = JDatafun.sum(D.LT(0.0)).get(0, 0);// sum(D<0);
            // System.out.println("\n pp = "+pp);
            if (pp == 0)
            {
                // T = diag(sqrt(D)) * U(:,t)';
                int[] ind = t.getColumnAt(0).getRowPackedCopy();
                temp = U.getColumns(ind).transpose();

                // System.out.println("\n------- Utt --------");
                // temp.print(4,4);
                T = JElfun.sqrt(D).diag().times(temp);
                p = new Boolean(true);
            }
            else
            {
                T = null;
                p = new Boolean(false);
            }

        }

        return new Object[]
        {
                T, p
        };
    }

    // //////////////////////////////////////////////////////////////////////////
    /**
     * <p>
     * Calculate the the mean of a vector (column or row) that possibly contain
     * NaNs. Eg, the dataset {3,2, NaN, 6,1, 8} has a mean of 4 :
     * </p>
     * 
     * @param dataVector
     *            A matrix vector (row or column) , that can contain NaN
     *            elements.
     * @return The mean of the non-NaN elements array (matrix) or vector
     *         <B>dataVector</B>.
     */
    public static double vectorNanMean(Matrix dataVector)
    {
        if (dataVector == null)
        {
            throw new IllegalArgumentException(" vectorNanMean : Parameter 'dataVector' must be non-null.");
        }
        if (!dataVector.isVector())
        {
            throw new IllegalArgumentException(
                    " vectorNanMean : Parameter 'dataVector' must be a vector and not a matrix.");
        }

        int indX = 0;
        int indY = 0;
        double sum = 0.0;
        double val = 0.0;
        int count = 0;
        int len = dataVector.length();

        if (dataVector.isRowVector())
        {
            for (int i = 0; i < len; i++)
            {
                val = dataVector.get(0, i);
                if (isfinite(val))
                {
                    sum += val;
                    count++;
                }
            }
        }
        else
        {
            for (int i = 0; i < len; i++)
            {
                val = dataVector.get(i, 0);
                if (isfinite(val))
                {
                    sum += val;
                    count++;
                }
            }
        }
        /*
         * Indices indices = dataVector.isnan().findIndices(); if (indices ==
         * null) { return JDatafun.mean(dataVector).get(0,0); }
         * 
         * 
         * 
         * double finiteNum = (double)(dataVector.numel() -
         * indices.getNumberOfRows());
         * 
         * for (int i = 0; i < indices.getNumberOfRows(); i++) { indX =
         * indices.getValueAt(i,0); indY = indices.getValueAt(i,1); sum +=
         * dataVector.get(indX, indY); }
         */
        return (sum / ((double) count));

    }

    /**
     * Check if a number is NaN (non-a-number) or Inf (infinity).
     * 
     * @param num
     *            Number to be determined if it is a NaN or Inf.
     * @return True if <B>num</B> is a NaN or Inf and False otherwise.
     */
    public static boolean nanOrInf(double num)
    {
        if (Double.isNaN(num) || Double.isInfinite(num))
        {
            return true;
        }
        return false;
    }

    /**
     * Check to see if a number is finite or not.
     * 
     * @param num
     *            Number to be determined if it is a NaN or Inf.
     * @return True if num is finite else false.
     */
    public static boolean isfinite(double num)
    {
        boolean tf = true;
        if (nanOrInf(num))
        {
            tf = false;
        }
        return tf;
    }

    /**
     * <p>
     * The sample kurtosis of the values in X. For a vector input, K is the
     * fourth central moment of X, divided by fourth power of its standard
     * deviation. For a matrix input, K is a row vector containing the sample
     * kurtosis of each column of X.
     * </p>
     * 
     * @param x
     *            A matrix to find its kurtosis.
     * @return A matrix kurtosis
     */
    public static Matrix kurtosis(Matrix x)
    {
        int row = x.getRowDimension();
        int col = x.getColumnDimension();
        if (row == 1 && col == 1)
        {
            return new Matrix(1, 1);
        }
        else
        {
            Matrix m4 = moment(x, 4);
            Matrix sm2 = JElfun.sqrt(moment(x, 2));
            Matrix k = m4.arrayRightDivide(JElfun.pow(sm2, 4D));
            return k;
        }
    }

    /**
     * <p>
     * The sample skewness of the values in X. For a vector input, S is the
     * third central moment of X, divided by the cube of its standard deviation.
     * For a matrix input, S is a row vector containing the sample skewness of
     * each column of X.
     * </p>
     * 
     * @param x
     *            A matrix to calculate its skewness.
     * @return Skewed matrix
     */
    public static Matrix skewness(Matrix x)
    {
        int row = x.getRowDimension();
        int col = x.getColumnDimension();
        if (row == 1 && col == 1)
        {
            return new Matrix(1, 1);
        }
        else
        {
            Matrix m3 = moment(x, 3);
            Matrix sm2 = JElfun.sqrt(moment(x, 2));
            Matrix sk = m3.arrayRightDivide(JElfun.pow(sm2, 3D));
            return sk;
        }
    }

    /**
     * <p>
     * Central moments of all orders
     * </p>
     * 
     * @param data
     *            Dataset fo moment computation
     * @param order
     *            Moment order
     * @return The ORDER-th central sample moment of the values in X
     */
    public static Matrix moment(Matrix data, int order)
    {
        Matrix Data = null;
        Matrix sigma = null;
        Matrix mu = null;
        Matrix temp = null;
        double val = 0.0D;
        int rows = data.getRowDimension();
        int cols = data.getColumnDimension();
        int len = 0;
        if (order < 1)
        {
            throw new IllegalArgumentException("moment : Requires a positive integer second argument (order).");
        }
        if (data.isVector())
        {
            if (data.isRowVector())
            {
                Data = data.toColVector();
            }
            else
            {
                Data = data;
            }
            rows = data.getRowDimension();
            cols = data.getColumnDimension();
        }
        else
        {
            Data = data;
        }

        if (order == 1)
        {
            sigma = new Matrix(1, cols, 0.0D);
        }
        else
        {
            mu = JDatafun.mean(Data);
            temp = mu.copy();
            len = mu.length();
            if (len > 1)
            {
                mu = new Matrix(rows, cols);
                for (int i = 0; i < rows; i++)
                {
                    for (int j = 0; j < cols; j++)
                    {
                        mu.set(i, j, temp.get(0, j));
                    }
                }
                temp = Data.minus(mu);
                temp = JElfun.pow(temp, order);
            }
            else
            {
                temp = JElfun.pow(Data.minus(mu.get(0, 0)), (double) order);
            }
            sigma = JDatafun.mean(temp);
        }
        return sigma;
    }

    /**
     * Compute the z-score of a matrix dataset.
     * 
     * @param Dataset
     *            to be computed for zscore.
     * @return Zscore standardized value (removed mean then divided by
     *         std.deviation)
     */
    public static Matrix zscore(Matrix data)
    {
        int m = data.getRowDimension();
        int n = data.getColumnDimension();
        boolean transposed = false;
        Matrix d = null;
        if (m == 1)
        {
            m = n;
            d = data.transpose();
            transposed = true;
        }
        else
        {
            d = data;
        }

        Matrix md = JDatafun.mean(d).repmat(m, 1);
        Matrix sd = JDatafun.std(d).repmat(m, 1);
        m = sd.getRowDimension();
        n = sd.getColumnDimension();
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (sd.get(i, j) == 0.0)
                {
                    sd.set(i, j, 1.0);
                }
            }
        }

        Matrix z = d.minus(md).arrayRightDivide(sd);

        if (transposed)
        {
            z = z.transpose();
        }

        return z;
    }

    /**
     * <p>
     * Student's T cumulative distribution function (cdf). P = TCDF(X,V)
     * computes the cdf for Student's T distribution with V degrees of freedom,
     * at the values in X.
     * </p>
     * 
     * <p>
     * The size of P is the common size of X and V. A scalar input functions as
     * a constant matrix of the same size as the other input.
     * </p>
     * 
     * @param x
     *            Value at which to evaluate for t-CDF.
     * @param v
     *            Degree of freedom
     * @return A value for the student's t-CDF.
     */
    public static double tcdf(double x, int v)
    {
        double p = 0.0;
        double temp = 1.0;
        double xx = 0.0;

        // use special cases for some specific values of v
        // See Devroye pages 29 and 450.
        // (This is also the Cauchy distribution)
        if (v == 1)
        {
            p = 0.5 + Math.atan(x) / Math.PI;
        }

        // See Abramowitz and Stegun, formulas 26.5.27 and 26.7.1
        // separate into positive and negative components.
        if (x > 0.0 && v != 1 && v > 0)
        {
            xx = (double) v / ((double) v + x * x);
            temp = 1.0 - JSpecfun.betainc(xx, (double) v / 2.0, 0.5);
            // Now convert from P(|T| < t) = temp to what we want,
            // i.e. P(T < t)
            p = 1.0 - (1.0 - temp) / 2.0;
        }

        if (x < 0.0 && v != 1 && v > 0)
        {
            xx = (double) v / ((double) v + x * x);
            temp = 1.0 - JSpecfun.betainc(xx, (double) v / 2.0, 0.5);
            // Now convert from P(|T| < t)=temp to what we want,
            // i.e. P(T < t)
            p = (1.0 - temp) / 2.0;
        }

        if (x == 0.0 && v != 1 && v > 0)
        {
            p = 0.5;
        }

        // Return NaN if the degrees of freedom is not a positive integer.
        if (v <= 0)
        {
            p = Double.NaN;
        }

        return p;
    }// end method

    /**
     * <p>
     * F cumulative distribution function. P = FCDF(X,V1,V2) returns the F
     * cumulative distribution function with V1 and V2 degrees of freedom at the
     * values in X.
     * </p>
     * 
     * <p>
     * The size of P is the common size of the input arguments. A scalar input
     * functions as a constant matrix of the same size as the other inputs.
     * </p>
     * 
     * @param x
     *            Value at which to evaluate for f-CDF
     * @param v1
     *            Degree of freedom
     * @param v2
     *            Degree of freedom
     * @return A value for the student's f-CDF.
     */
    public static double fcdf(double x, int v1, int v2)
    {
        // System.out.println(" x = "+x+"  :   v1 = "+v1+"  :  v2 = "+v2);
        double p = 0.0;
        if (v1 <= 0 || v2 <= 0)
        {
            return Double.NaN;
        }
        if (x > 0.0)
        {
            double xx = (double) v2 / ((double) v2 + (double) v1 * x);
            p = 1.0 - JSpecfun.betainc(xx, (double) v2 / 2.0, (double) v1 / 2.0);
        }
        return p;
    }// end method

    // //////////////////////////////////////////////////////////////////////////

    public static double pearsonCorrelation(Matrix xVect, Matrix yVect)
    {
        if (!xVect.isVector())
        {
            throw new ConditionalRuleException(
                    "pearsonCorrelation : First input argument must be a vector and not a marix.");
        }
        if (!yVect.isVector())
        {
            throw new ConditionalRuleException(
                    "pearsonCorrelation : Second input argument must be a vector and not a marix.");
        }

        if (xVect.length() != yVect.length())
        {
            throw new ConditionalRuleException(
                    "pearsonCorrelation : The length of the 2 input argument vectors must be the same.");
        }

        double meanX = JDatafun.mean(xVect).start();
        double meanY = JDatafun.mean(yVect).start();

        double sumXY = 0.0, sumX2 = 0.0, sumY2 = 0.0, xE = 0.0, yE = 0.0;
        for (int i = 0; i < xVect.length(); i++)
        {
            xE = xVect.getElementAt(i);
            yE = yVect.getElementAt(i);
            sumXY += ((xE - meanX) * (yE - meanY));
            sumX2 += Math.pow(xE - meanX, 2.0);
            sumY2 += Math.pow(yE - meanY, 2.0);
        }

        double val = (sumXY / (Math.sqrt(sumX2) * Math.sqrt(sumY2)));

        return val;
    }// end: GetCorrelation(X,Y)

    public static void main(String[] args)
    {
        double[][] m =
        {
            {
                    2, 3
            }
        };
        Matrix mu = new Matrix(m);
        double[][] s =
        {
                {
                        1, 1.5
                },
                {
                        1.5, 3
                }
        };
        Matrix sigma = new Matrix(s);
        Integer cases = new Integer(5);

        Matrix[] rT = mvnrnd(mu, sigma, cases);
        Matrix r = rT[0];
        Matrix T = rT[1];

        System.out.println("\n------- r --------");
        r.print(4, 4);

        System.out.println("\n------- T --------");
        T.print(4, 4);

    }
}// -------------------------------- End Class Definition
// -----------------------
