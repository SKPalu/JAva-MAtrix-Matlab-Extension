package jamaextension.jamax.unsupervised.subspace.pca;

import java.util.ArrayList;
import java.util.List;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.SvdJLapack;
import jamaextension.jamax.SvdJLapack.Economy;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.Max;
import jamaextension.jamax.datafun.MaxMat;
import jamaextension.jamax.elfun.JElfun;
import jamaextension.jamax.specfun.JSpecfun;
import jamaextension.jamax.unsupervised.subspace.FeatureExtractions;

/*
 * The following is a direct port of Matlab PCA in the statistics & machine-learning toolbox into Java.
 * 
 *PCA Principal Component Analysis (PCA) on raw data.
*   COEFF = PCA(X) returns the principal component coefficients for the N
*   by P data matrix X. Rows of X correspond to observations and columns to
*   variables. Each column of COEFF contains coefficients for one principal
*   component. The columns are in descending order in terms of component
*   variance (LATENT). PCA, by default, centers the data and uses the
*   singular value decomposition algorithm. For the non-default options,
*   use the name/value pair arguments.
*   
*   [COEFF, SCORE] = PCA(X) returns the principal component score, which is
*   the representation of X in the principal component space. Rows of SCORE
*   correspond to observations, columns to components. The centered data
*   can be reconstructed by SCORE*COEFF'.
*
*   [COEFF, SCORE, LATENT] = PCA(X) returns the principal component
*   variances, i.e., the eigenvalues of the covariance matrix of X, in
*   LATENT.
*
*   [COEFF, SCORE, LATENT, TSQUARED] = PCA(X) returns Hotelling's T-squared
*   statistic for each observation in X. PCA uses all principal components
*   to compute the TSQUARED (computes in the full space) even when fewer
*   components are requested (see the 'NumComponents' option below). For
*   TSQUARED in the reduced space, use MAHAL(SCORE,SCORE).
*
*   [COEFF, SCORE, LATENT, TSQUARED, EXPLAINED] = PCA(X) returns a vector
*   containing the percentage of the total variance explained by each
*   principal component.
*
*   [COEFF, SCORE, LATENT, TSQUARED, EXPLAINED, MU] = PCA(X) returns the
*   estimated mean.
*
*   [...] = PCA(..., 'PARAM1',val1, 'PARAM2',val2, ...) specifies optional
*   parameter name/value pairs to control the computation and handling of
*   special data types. Parameters are:
*   
*    'Algorithm' - Algorithm that PCA uses to perform the principal
*                  component analysis. Choices are:
*        'svd'   - Singular Value Decomposition of X (the default).
*        'eig'   - Eigenvalue Decomposition of the covariance matrix. It
*                  is faster than SVD when N is greater than P, but less
*                  accurate because the condition number of the covariance
*                  is the square of the condition number of X.
*        'als'   - Alternating Least Squares (ALS) algorithm which finds
*                  the best rank-K approximation by factoring a X into a
*                  N-by-K left factor matrix and a P-by-K right factor
*                  matrix, where K is the number of principal components.
*                  The factorization uses an iterative method starting with
*                  random initial values. ALS algorithm is designed to
*                  better handle missing values. It deals with missing
*                  values without listwise deletion (see {'Rows',
*                  'complete'}).
*
*     'Centered' - Indicator for centering the columns of X. Choices are: 
*         true   - The default. PCA centers X by subtracting off column
*                  means before computing SVD or EIG. If X contains NaN
*                  missing values, NANMEAN is used to find the mean with
*                  any data available.
*         false  - PCA does not center the data. In this case, the original
*                  data X can be reconstructed by X = SCORE*COEFF'. 
*
*     'Economy'  - Indicator for economy size output, when D the degrees of
*                  freedom is smaller than P. D, is equal to M-1, if data
*                  is centered and M otherwise. M is the number of rows
*                  without any NaNs if you use 'Rows', 'complete'; or the
*                  number of rows without any NaNs in the column pair that
*                  has the maximum number of rows without NaNs if you use
*                  'Rows', 'pairwise'. When D < P, SCORE(:,D+1:P) and
*                  LATENT(D+1:P) are necessarily zero, and the columns of
*                  COEFF(:,D+1:P) define directions that are orthogonal to
*                  X. Choices are:
*         true   - This is the default. PCA returns only the first D
*                  elements of LATENT and the corresponding columns of
*                  COEFF and SCORE. This can be significantly faster when P
*                  is much larger than D. NOTE: PCA always returns economy
*                  size outputs if 'als' algorithm is specifed.
*         false  - PCA returns all elements of LATENT. Columns of COEFF and
*                  SCORE corresponding to zero elements in LATENT are
*                  zeros.
*
*     'NumComponents' - The number of components desired, specified as a
*                  scalar integer K satisfying 0 < K <= P. When specified,
*                  PCA returns the first K columns of COEFF and SCORE.
*
*     'Rows'     - Action to take when the data matrix X contains NaN
*                  values. If 'Algorithm' option is set to 'als, this
*                  option is ignored as ALS algorithm deals with missing
*                  values without removing them. Choices are:
*         'complete' - The default action. Observations with NaN values
*                      are removed before calculation. Rows of NaNs are
*                      inserted back into SCORE at the corresponding
*                      location.
*         'pairwise' - If specified, PCA switches 'Algorithm' to 'eig'. 
*                      This option only applies when 'eig' method is used.
*                      The (I,J) element of the covariance matrix is
*                      computed using rows with no NaN values in columns I
*                      or J of X. Please note that the resulting covariance
*                      matrix may not be positive definite. In that case,
*                      PCA terminates with an error message.
*         'all'      - X is expected to have no missing values. All data
*                      are used, and execution will be terminated if NaN is
*                      found.
*                     
*     'Weights'  - Observation weights, a vector of length N containing all
*                  positive elements.
*
*     'VariableWeights' - Variable weights. Choices are:
*          - a vector of length P containing all positive elements.
*          - the string 'variance'. The variable weights are the inverse of
*            sample variance. If 'Centered' is set true at the same time,
*            the data matrix X is centered and standardized. In this case,
*            PCA returns the principal components based on the correlation
*            matrix.
*
*   The following parameter name/value pairs specify additional options
*   when alternating least squares ('als') algorithm is used.
*
*      'Coeff0'  - Initial value for COEFF, a P-by-K matrix. The default is
*                  a random matrix.
*
*      'Score0'  - Initial value for SCORE, a N-by-K matrix. The default is
*                  a matrix of random values.
*
*      'Options' - An options structure as created by the STATSET function.
*                  PCA uses the following fields:
*          'Display' - Level of display output.  Choices are 'off' (the
*                      default), 'final', and 'iter'.
*          'MaxIter' - Maximum number of steps allowed. The default is
*                      100. Unlike in optimization settings, reaching
*                      MaxIter is regarded as convergence.
*           'TolFun' - Positive number giving the termination tolerance for
*                      the cost function.  The default is 1e-6.
*             'TolX' - Positive number giving the convergence threshold
*                      for relative change in the elements of L and R. The
*                      default is 1e-6.
*
*
*   Example:
*       load hald;
*       [coeff, score, latent, tsquared, explained] = pca(ingredients);
*
*   See also PPCA, PCACOV, PCARES, BIPLOT, BARTTEST, CANONCORR, FACTORAN,
*   ROTATEFACTORS.
*
* References:
*   [1] Jolliffe, I.T. Principal Component Analysis, 2nd ed.,Springer,2002. 
*   [2] Krzanowski, W.J., Principles of Multivariate Analysis, Oxford
*       University Press, 1988.
*   [3] Seber, G.A.F., Multivariate Observations, Wiley, 1984. 
*   [4] Jackson, J.E., A User's Guide to Principal Components, Wiley, 1988. 
*   [5] Sam Roweis, EM algorithms for PCA and SPCA, In Proceedings of the
*       1997 conference on Advances in neural information processing
*       systems 10 (NIPS '97), MIT Press, Cambridge, MA, USA, 626-632,1998.
*   [6] Alexander Ilin and Tapani Raiko. Practical Approaches to Principal
*       Component Analysis in the Presence of Missing Values. J. Mach.
*       Learn. Res. 11 (August 2010), 1957-2000, 2010.
*
*/

public class Pca extends FeatureExtractions
{

    protected Matrix coeff;
    protected Matrix score;
    protected Matrix latent;
    protected Matrix tsquared;
    protected Matrix explained;
    protected Matrix mu;

    // internal variables
    // AlgorithmNames = {'svd','eig','als'};
    private List<String> AlgorithmNames;
    // private boolean Centered;
    // private boolean Economy;
    private List<String> Rows;
    private List<String> keys;
    private int DOF = 0;
    private int n;
    private int p;
    private String vAlgorithm;
    private boolean vCentered;
    private boolean vEconomy;
    private String vRows;
    private Matrix vWeights;
    private Matrix vVariableWeights;
    private Matrix c0;
    private Matrix s0;

    public Pca(Matrix data)
    {
        super(data);
        init();
    }

    void init()
    {
        Matrix x = this.data;
        n = x.getRowDimension();
        p = x.getColumnDimension();
        this.numDimension = p;

        this.keys = new ArrayList<String>();
        this.keys.add("AlgorithmNames");
        this.keys.add("Centered");
        this.keys.add("Economy");
        this.keys.add("Rows");
        this.keys.add("Weights");
        this.keys.add("VariableWeights");
        this.keys.add("Coeff0");
        this.keys.add("Score0");
        // 'Weights','VariableWeights','Coeff0','Score0'
        Matrix Weights = Matrix.ones(1, n);
        this.addParameter("Weights", Weights);
        Matrix VariableWeights = Matrix.ones(1, p);
        this.addParameter("VariableWeights", VariableWeights);
        Matrix Coeff0 = null;
        this.addParameter("Coeff0", Coeff0);
        Matrix Score0 = null;
        this.addParameter("Score0", Score0);

        this.AlgorithmNames = new ArrayList<String>();
        this.AlgorithmNames.add("svd");
        this.AlgorithmNames.add("eig");
        this.AlgorithmNames.add("als");
        this.addParameter("AlgorithmNames", "svd");

        // default
        // this.Centered = true;
        this.addParameter("Centered", true);
        // this.Economy = true;
        this.addParameter("Economy", true);

        this.Rows = new ArrayList<String>();
        this.Rows.add("complete");
        this.Rows.add("pairwise");
        this.Rows.add("all");
        this.addParameter("Rows", "complete");
    }

    @Override
    public void buildFeatures()
    {
        Matrix x = this.data;
        int vNumComponents = this.numDimension;
        vAlgorithm = (String) this.get("AlgorithmNames");
        vCentered = (Boolean) this.get("Centered");
        vEconomy = (Boolean) this.get("Economy");
        vRows = (String) this.get("Rows");
        vWeights = (Matrix) this.get("Weights");
        vVariableWeights = (Matrix) this.get("VariableWeights");
        c0 = (Matrix) this.get("Coeff0");
        s0 = (Matrix) this.get("Score0");

        if (vAlgorithm.equals("svd"))
        {
            if ("pairwise".equals(vRows))
            {
                vAlgorithm = "eig";
            }
            if ((c0 != null && !c0.isNull()) || (s0 != null && !s0.isNull()))
            {
                vAlgorithm = "als";
            }
        }
        else if (vAlgorithm.equals("als"))
        {
        }

        if (vWeights.isVector() && vWeights.numel() == n)
        {
            if (vWeights.isColVector())
            {
                vWeights = vWeights.toRowVector();
            }
        }
        else
        {
            throw new ConditionalRuleException("buildFeatures : Weight vector must have a length of " + n
                    + " elements, which is the number of observations.");
        }

        if (vVariableWeights.isVector() && vVariableWeights.numel() == p)
        {
            if (vVariableWeights.isColVector())
            {
                vVariableWeights = vVariableWeights.toRowVector();
            }
        }
        else
        {
            throw new ConditionalRuleException("buildFeatures : VariableWeights vector must have a length of " + p
                    + " elements, which is the number of features.");
        }

        if ((vWeights.LTEQ(0.0).anyBoolean()) || (vVariableWeights.LTEQ(0.0).anyBoolean()))
        {
            throw new ConditionalRuleException(
                    "buildFeatures : Found non-positive elements in Weights & VariableWeights vectors. Elements must be all positives.");
        }

        Indices nanIdx = x.isnan();
        Indices numNaN = JDatafun.sum(nanIdx, Dimension.COL); // number of NaNs
                                                              // in each row
        Indices wasNaN = numNaN.ANY(Dimension.COL);// any(numNaN,2); // Rows
                                                   // that contain NaN

        if (nanIdx.allBoolean())
        {
            throw new ConditionalRuleException("buildFeatures : All data elements are NaN.");
        }

        if ("all".equals(vRows) && !"als".equals(vAlgorithm))
        {
            if (wasNaN.anyBoolean())
            {// any(wasNaN)
             // error(message('stats:pca:RowsAll'));
                throw new ConditionalRuleException("buildFeatures : Found rows that are all NaN.");
            }
            else
            {
                vRows = "complete";
            }
        }

        int intVal = (vCentered ? 1 : 0);
        if ("complete".equals(vRows))
        {// Degrees of freedom (DOF) is n-1 if centered and n if not centered,
         // where n is the numer of rows without any NaN element.
            intVal = intVal + JDatafun.sum(wasNaN).start();
            DOF = Math.max(0, n - intVal);
        }
        else if ("pairwise".equals(vRows))
        {
            // DOF is the maximum number of element pairs without NaNs
            Indices notNaN = nanIdx.NOT();// double(~nanIdx);
            Indices nanC = notNaN.transpose().times(notNaN);
            nanC = nanC.arrayTimes(Indices.eye(p).NOT());// .*(~eye(p));
            DOF = JDatafun.max(nanC.toColVector()).start();
            DOF = DOF - intVal;
        }
        else
        {
            DOF = Math.max(0, n - intVal);
        }

        System.out.println("DOF = " + DOF);

        this.mu = JDatafun.wnanmean(x, vWeights);

        if ("eig".equals(vAlgorithm))
        {
            EIG(x);
        }
        else if ("svd".equals(vAlgorithm))
        {
            SVD(x);
        }
        else if ("als".equals(vAlgorithm))
        {
            ALS();
        }
        else
        {
            throw new ConditionalRuleException("buildFeatures : Solver algorithm \"" + vAlgorithm
                    + "\" is not unsupported.");
        }

        double val = 100 / JDatafun.sum(this.latent).start();
        this.explained = this.latent.arrayTimes(val);

        int[] ind = null;
        if (vNumComponents < DOF)
        {
            int numCol = coeff.getColumnDimension();
            ind = Indices.linspace(vNumComponents, numCol - 1).getRowPackedCopy();
            // coeff(:, vNumComponents+1:end) = [];
            this.coeff = this.coeff.removeColsAt(ind);
            // if nargout > 1
            // score(:, vNumComponents+1:end) = [];
            // end
            this.score = this.score.removeColsAt(ind);
        }

        // Enforce a sign convention on the coefficients -- the largest element
        // in each column will have a positive sign.
        Matrix absCoeff = this.coeff.abs();
        Max max = new MaxMat(absCoeff, Dimension.ROW);
        // [~,maxind] = max(abs(coeff), [], 1);
        Indices maxind = max.getIndices();
        // [d1, d2] = size(coeff);
        int d1 = this.coeff.getRowDimension();
        int d2 = this.coeff.getColumnDimension();
        int to = (d2 - 1) * d1;
        Indices fromTo = Indices.linspace(0, d1, to);
        Indices maxindFromTo = maxind.plus(fromTo);
        maxindFromTo = maxindFromTo.minus(1);
        ind = maxindFromTo.getRowPackedCopy();
        Matrix coeffInd = this.coeff.getEls(ind);
        // colsign = sign(coeff(maxind + (0:d1:(d2-1)*d1)));
        Matrix colsign = coeffInd.sign();
        // coeff = bsxfun(@times, coeff, colsign);
        this.coeff = this.coeff.times(colsign.diag());

        // score = bsxfun(@times, score, colsign); // scores = score
        this.score = this.score.times(colsign.diag());

        this.coeff.printInLabel("coeff");
        this.score.printInLabel("score");
        this.latent.printInLabel("latent");
        this.tsquared.printInLabel("tsquared");
        this.explained.printInLabel("explained");
        this.mu.printInLabel("mu");

        this.built = true;

    }

    private void EIG(Matrix x)
    {
        // Center the data if 'Centered' is true.
        if (vCentered)
        {
            // x = bsxfun(@minus,x,mu);
        }

        // Use EIG to compute.
        Object[] obj = localEIG(x, vCentered, vRows, vWeights, vVariableWeights);
        this.coeff = (Matrix) obj[0];
        Matrix eigValues = (Matrix) obj[1];
        int[] ind = null;
        // When 'Economy' value is true, nothing corresponding to zero
        // eigenvalues should be returned.
        if (DOF < p)
        {
            ind = Indices.linspace(DOF, p - 1).getRowPackedCopy();
            if (vEconomy)
            {
                // coeff(:, DOF+1:p) = [];
                coeff = coeff.removeColsAt(ind);
                // eigValues(DOF+1:p, :) = [];
                eigValues = eigValues.removeRowsAt(ind);
            }
            else
            { // make sure they are zeros.
              // eigValues(DOF+1:p, :) = 0;
                eigValues.setRows(ind, 0.0);// = eigValues.removeRowsAt(ind);
            }
        }

        // Check if eigvalues are all postive
        if (eigValues.LT(0.0).anyBoolean())
        {// any(eigValues<0)
         // error(message('stats:pca:CovNotPositiveSemiDefinite'));
            throw new ConditionalRuleException("EIG : Covariance matrix not positive definite.");
        }

        // if nargout > 1
        // score = x/coeff';
        this.latent = eigValues; // Output Eigenvalues
        // if nargout > 3
        // tsquared = localTSquared(score, latent, n, p);
        // end
        // end
    }

    private void SVD(Matrix x)
    {
        // Center the data if 'Centered' is true.
        int rows = x.getRowDimension();
        if (vCentered)
        {
            // x = bsxfun(@minus,x,mu);
            x = x.minus(this.mu.repmat(rows, 1));
        }

        Object[] obj = localSVD(x, n, vEconomy, vWeights, vVariableWeights);
        // [U,sigma, coeff, wasNaN]
        Matrix U = (Matrix) obj[0];
        Matrix sigma = (Matrix) obj[1];
        // sigma = sigma.diag();
        this.coeff = (Matrix) obj[2];

        Indices wasNaN = (Indices) obj[3];
        // if nargout > 1
        if (sigma.isVector())
        {
            this.score = U.times(sigma.diag());// bsxfun(@times,U,sigma');
        }
        else
        {
            this.score = U.times(sigma);
        }

        this.latent = JElfun.pow(sigma, 2.0).arrayRightDivide(DOF);

        // if nargout > 3
        this.tsquared = localTSquared(score, latent, DOF, p);

        // end
        // Insert NaNs back
        if (wasNaN.anyBoolean())
        {// any(wasNaN)
         // score = internal.stats.insertnan(wasNaN, score);
         // if nargout >3
         // tsquared = internal.stats.insertnan(wasNaN,tsquared);
         // end
            System.out.println("NaN data found.");
        }
        // end

        if (DOF < p)
        {
            // When 'Economy' value is true, nothing corresponding to zero
            // eigenvalues should be returned.
            int numCol = 0;
            int numRow = 0;
            int[] dofEnd = null;

            if (vEconomy)
            {
                numCol = coeff.getColumnDimension();
                dofEnd = Indices.linspace(DOF, numCol - 1).getRowPackedCopy();
                // coeff(:, DOF+1:end) = [];
                this.coeff = this.coeff.removeColsAt(dofEnd);
                // score(:, DOF+1:end)=[];
                this.score = this.score.removeColsAt(dofEnd);
                numRow = latent.getRowDimension();
                // latent(DOF+1:end, :)=[];
                dofEnd = Indices.linspace(DOF, numRow - 1).getRowPackedCopy();
                this.latent = this.latent.removeRowsAt(dofEnd);
            }
            else
            {
                // otherwise, eigenvalues and corresponding outputs need to pad
                // zeros because svd(x,0) does not return columns of U
                // corresponding to components of (DOF+1):p.
                numCol = this.score.getColumnDimension();
                dofEnd = Indices.linspace(DOF, numCol - 1).getRowPackedCopy();
                // score(:, DOF+1:p) = 0;
                this.score.setColumns(dofEnd, 0.0);
                numRow = this.latent.getRowDimension();
                dofEnd = Indices.linspace(DOF, numRow - 1).getRowPackedCopy();
                // latent(DOF+1:p, 1) = 0;
                this.latent.setRows(dofEnd, 0.0); // .setRows(dofEnd, 0.0);
            }
        }

    }

    private void ALS()
    {
    }

    @Override
    protected void isKeyValid(String key)
    {
        boolean tf = this.keys.contains(key);
        if (!tf)
        {
            throw new ConditionalRuleException("isKeyValid : Parameter key name \"" + key + "\" is invalid.");
        }
    }

    private static Matrix localTSquared(Matrix score, Matrix latent, int DOF, int p)
    {
        // Subfunction to calulate the Hotelling's T-squared statistic. It is
        // the
        // sum of squares of the standardized scores, i.e., Mahalanobis
        // distances.
        // When X appears to have column rank < r, ignore components that are
        // orthogonal to the data.

        if (score == null || score.isNull())
        {// isempty(score)
         // tsquared = score;
         // return;
            throw new ConditionalRuleException("localTSquared : Score matrix is null or empty. Null score not allowed.");
        }

        int r = Math.min(DOF, p); // Max possible rank of x;
        int q = 0;
        if (DOF > 1)
        {
            int maxDofP = Math.max(DOF, p);
            double latentEPS = JSpecfun.EPS(latent.start()) * maxDofP;
            Indices latentInd = latent.GT(latentEPS);
            // q = sum(latent > max(DOF,p)*eps(latent(1)));
            q = JDatafun.sum(latentInd).start();
            if (q < r)
            {
                // warning(message('stats:pca:ColRankDefX', q));
                System.out.println("localTSquared : WARNING -> Column rank deficiency found. Rank is " + q + " ;");
            }
        }

        if (q == 0)
        {
            throw new ConditionalRuleException("localTSquared : Rank deficiency found in the data.");
        }

        // score(:,1:q)*diag(1./sqrt(latent(1:q,:))')
        int[] qrank = Indices.linspace(0, q - 1).getRowPackedCopy();
        Matrix scoreRank = score.getColumns(qrank);
        Matrix latentRank = latent.getEls(qrank);
        latentRank = JElfun.sqrt(latentRank).reciprocate();
        // standScores = bsxfun(@times, score(:,1:q), 1./sqrt(latent(1:q,:))');
        Matrix standScores = scoreRank.times(latentRank.diag());
        standScores = JElfun.pow(standScores, 2.0);
        // tsquared = sum(standScores.^2, 2);
        Matrix tsquared = JDatafun.sum(standScores, Dimension.COL);

        return tsquared;
    }

    private static Object[] localSVD(Matrix x, int n, boolean vEconomy, Matrix vWeights, Matrix vVariableWeights)
    {
        // Compute by SVD. Weights are supplied by vWeights and
        // vVariableWeights.

        // Remove NaNs missing data and record location
        // [~,wasNaN,x] = internal.stats.removenan(x);
        Object[] obj = JDatafun.removenan(x);
        Indices wasNaN = (Indices) obj[1];
        // wasNaN.printInLabel("wasNaN");
        x = (Matrix) obj[2];
        // x.printInLabel("x_localSVD");
        if (n == 1) // special case because internal.stats.removenan treats all
                    // vectors as columns
        {
            wasNaN = wasNaN.transpose();
            x = x.transpose();
        }

        // Apply observation and variable weights
        FindInd find = wasNaN.findIJ();
        if (!find.isNull())
        {
            // vWeights(wasNaN) = [];
            vWeights = vWeights.removeColsAt(find.getIndex());
        }

        Matrix OmegaSqrt = JElfun.sqrt(vWeights);
        Matrix PhiSqrt = JElfun.sqrt(vVariableWeights);

        int numcol = x.getColumnDimension();
        x = x.arrayTimes(OmegaSqrt.transpose().repmat(1, numcol));// bsxfun(@times,
                                                                  // x,
                                                                  // OmegaSqrt');
        x = x.times(PhiSqrt.diag());// bsxfun(@times, x, PhiSqrt);

        // x.printInLabel("x");

        Matrix U = null;
        Matrix sigma = null;
        Matrix coeff2 = null;
        SvdJLapack svd = null;
        if (vEconomy)
        {
            // [U,sigma,coeff] = svd(x,'econ');
            svd = new SvdJLapack(x, Economy.ECON);
        }
        else
        {
            // [U,sigma, coeff] = svd(x, 0);
            svd = new SvdJLapack(x, Economy.ZERO);
        }

        U = svd.getU();
        sigma = svd.getS();
        coeff2 = svd.getV();

        numcol = U.getColumnDimension();

        Matrix OmegaRecip = OmegaSqrt.transpose().reciprocate();
        U = U.arrayTimes(OmegaRecip.repmat(1, numcol));// bsxfun(@times, U,
                                                       // 1./OmegaSqrt');
        Matrix PhiRecip = PhiSqrt.transpose().reciprocate();
        coeff2 = coeff2.arrayTimes(PhiRecip.repmat(1, numcol));// bsxfun(@times,
                                                               // coeff,1./PhiSqrt');

        if (n == 1) // sigma might have only 1 row
        {
            sigma = new Matrix(1, 1, sigma.start());// sigma(1);
        }
        else
        {
            sigma = sigma.diag();
        }

        boolean out = false;
        if (out)
        {
            U.printInLabel("U");
            sigma.printInLabel("sigma");
            coeff2.printInLabel("coeff2");
            wasNaN.printInLabel("wasNaN");
        }

        Object[] obj2 =
        {
                U, sigma, coeff2, wasNaN
        };
        return obj2;
    }

    private static Object[] localEIG(Matrix x, boolean vCentered, String vRows, Matrix vWeights, Matrix vVariableWeights)
    {
        // Compute by EIG. vRows are the options of handing NaN when compute
        // covariance matrix

        // Apply observation and variable weights
        Matrix OmegaSqrt = JElfun.sqrt(vWeights);
        Matrix PhiSqrt = JElfun.sqrt(vVariableWeights);
        x = x.times(OmegaSqrt.transpose());// bsxfun(@times, x, OmegaSqrt');
        x = x.times(PhiSqrt.transpose());// bsxfun(@times, x, PhiSqrt);

        // xCov = ncnancov(x, vRows, vCentered);

        // [coeff, eigValueDiag] = eig(xCov);
        // [eigValues, idx] = sort(diag(eigValueDiag), 'descend');
        // coeff = coeff(:, idx);

        // coeff = bsxfun(@times, coeff,1./PhiSqrt');
        return null;
    }

    public Matrix getCoeff()
    {
        // boolean tf = FeatureExtractions.isNull(this.coeff);
        if (!this.built)
        {
            throw new ConditionalRuleException("getCoeff : You must call the method \"buildFeatures\" first.");
        }
        return coeff;
    }

    public Matrix getScore()
    {
        if (!this.built)
        {
            throw new ConditionalRuleException("getScore : You must call the method \"buildFeatures\" first.");
        }
        return score;
    }

    public Matrix getLatent()
    {
        if (!this.built)
        {
            throw new ConditionalRuleException("getLatent : You must call the method \"buildFeatures\" first.");
        }
        return latent;
    }

    public Matrix getTsquared()
    {
        if (!this.built)
        {
            throw new ConditionalRuleException("getTsquared : You must call the method \"buildFeatures\" first.");
        }
        return tsquared;
    }

    public Matrix getExplained()
    {
        if (!this.built)
        {
            throw new ConditionalRuleException("getExplained : You must call the method \"buildFeatures\" first.");
        }
        return explained;
    }

    public Matrix getMu()
    {
        if (!this.built)
        {
            throw new ConditionalRuleException("getMu : You must call the method \"buildFeatures\" first.");
        }
        return mu;
    }

    static Matrix testData()
    {
        double[][] X =
        {
                {
                        12, 5, 6, 20, 7, 1, 17, 17, 18, 16
                },
                {
                        17, 13, 1, 11, 5, 17, 13, 11, 0, 5
                },
                {
                        17, 2, 1, 8, 9, 15, 15, 16, 18, 16
                },
                {
                        14, 4, 15, 5, 8, 3, 2, 14, 11, 4
                },
                {
                        0, 2, 19, 3, 0, 16, 11, 18, 4, 11
                },
                {
                        7, 12, 19, 7, 6, 3, 20, 7, 3, 5
                },
                {
                        11, 18, 18, 1, 6, 7, 3, 18, 14, 17
                },
                {
                        20, 19, 17, 15, 15, 9, 4, 7, 2, 1
                },
                {
                        11, 20, 5, 18, 10, 16, 1, 4, 15, 14
                },
                {
                        17, 5, 14, 13, 3, 7, 3, 2, 16, 20
                },
                {
                        9, 4, 19, 18, 2, 3, 13, 13, 11, 9
                },
                {
                        8, 11, 4, 16, 9, 9, 19, 5, 2, 16
                },
                {
                        17, 9, 4, 7, 20, 11, 12, 20, 20, 2
                },
                {
                        19, 14, 8, 20, 20, 9, 1, 16, 4, 7
                },
                {
                        11, 2, 14, 6, 14, 13, 16, 10, 14, 4
                }
        };

        return new Matrix(X);
    }

    public static void main(String[] args)
    {
        Matrix x = testData();
        x.printInLabel("x1", 0);
        Indices xlteq5 = x.LTEQ(5);
        FindInd find = xlteq5.findIJ();
        if (!find.isNull())
        {
            int[] ind = find.getIndex();
            // x.setElements(ind, Double.NaN);
        }
        x.printInLabel("x2", 0);

        FeatureExtractions pca = new Pca(x);
        pca.setNumDimension(3);
        pca.buildFeatures();
        System.out.println("\nDone");
    }

}
