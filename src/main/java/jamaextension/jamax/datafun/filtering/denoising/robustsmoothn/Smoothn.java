package jamaextension.jamax.datafun.filtering.denoising.robustsmoothn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jamaextension.jamax.Cell;
import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.Parameters;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elfun.JElfun;

public class Smoothn
{
    private Cell data;
    private Parameters option = SmoothnUtil.getSmoothnDefaultOption();
    private boolean robust = false;
    private double smoothness = Double.NaN;
    private Matrix W;

    public Smoothn(Matrix datMat)
    {
        this(datMat, null);
    }

    public Smoothn(Matrix datMat, Matrix W)
    {
        this(datMat, W, Double.NaN);
    }

    public Smoothn(Matrix datMat, Matrix W, double smoothness)
    {
        if (datMat == null || datMat.isNull())
        {
            throw new IllegalArgumentException("Smoothn : Input argumet of type \"Matrix\" must be non null.");
        }
        /*
         * if ( !datMat.isVector()) { throw new IllegalArgumentException(
         * "Smoothn : Input argumet of type \"Matrix\" must be a vector."); }
         */

        Object[] obj =
        {
            datMat
        };
        Cell datCell = new Cell(obj);
        construct(datCell, W, smoothness);
    }

    public Smoothn(Cell datCell)
    {
        this(datCell, null);
    }

    public Smoothn(Cell datCell, Matrix W)
    {
        this(datCell, W, Double.NaN);
    }

    public Smoothn(Cell datCell, Matrix W, double smoothness)
    {
        construct(datCell, W, smoothness);
    }

    private void construct(Cell datCell, Matrix W, double smoothness)
    {
        if (smoothness <= 0 || smoothness >= 1)
        {
            this.smoothness = Double.NaN;
        }
        else
        {
            this.smoothness = smoothness;
        }

        boolean wNull = W == null || W.isNull();
        if (!wNull)
        {
            this.W = W;
        }

        if (datCell == null || datCell.isNull())
        {
            throw new IllegalArgumentException("construct : Input argumet of type \"Cell\" must be non null.");
        }
        if (!datCell.isVector())
        {
            throw new IllegalArgumentException(
                    "construct : Input argumet of type \"Cell\" must be a vector and not  matrix.");
        }

        int len = datCell.length();
        for (int i = 0; i < len; i++)
        {
            Object obj = datCell.getElementAt(i);
            boolean isMatrix = obj instanceof Matrix;
            if (!isMatrix)
            {
                throw new IllegalArgumentException("construct : \"Cell\" element at index = \"" + i
                        + "\" must be an object of type \"Matrix\".");
            }
        }
        // check that sizes must be consistent
        if (len > 1)
        {
            Matrix mat1 = (Matrix) datCell.getElementAt(0);
            Indices siz1 = mat1.sizeIndices();
            for (int i = 1; i < len; i++)
            {
                Matrix matI = (Matrix) datCell.getElementAt(i);
                Indices sizI = matI.sizeIndices();
                boolean eqSize = siz1.EQ(sizI).allBoolean();
                if (!eqSize)
                {
                    throw new IllegalArgumentException(
                            "construct : \"Cell\" elements' \"Matrix\" objects must have the same sizes.");
                }
            }
        }

        this.data = datCell;

        // computeSmooth();
    }

    public void computeSmooth()
    {
        Cell datCell = this.data;
        Cell y = this.data;
        int ny = datCell.length();
        Matrix mat1 = (Matrix) datCell.getElementAt(0);
        Indices sizy = mat1.sizeIndices();
        int noe = mat1.numel();

        boolean exitflag = false;
        Cell z = null;

        if (noe < 2)
        {
            z = y;
            // s = [];
            exitflag = true;
            return;
        }
        // Indices siz1 = mat1.sizeIndices();

        // Smoothness parameter and weights
        Matrix WL = null;

        // Matrix Wgt = (Matrix) this.option.get("Weights");
        if (this.W == null)
        {
            this.W = Matrix.ones(sizy.getRowPackedCopy());
        }
        else
        {
            this.W = (Matrix) this.option.get("Weights");
        }

        WL = this.W.copy();

        boolean eqSiz = mat1.sizeIndices().EQ(WL.sizeIndices()).allBoolean();
        if (!eqSiz)
        {
            throw new IllegalArgumentException(
                    "computeSmooth : Size of \"Weight\" matrix must have the same size as the \"data\" size.");
        }

        int MaxIter = 100;
        Integer mIter = (Integer) this.option.get("MaxIter");
        if (mIter != null)
        {
            MaxIter = mIter;
        }

        double TolZ = 1e-3;
        Double tol = (Double) this.option.get("TolZ");
        if (tol != null)
        {
            TolZ = tol;
        }

        // "Weights", "bisquare"
        String weightstr = "bisquare";
        String wstr = (String) this.option.get("Weights");
        if (wstr != null)
        {
            weightstr = wstr;
        }

        boolean isinitial = false;
        Cell z0 = (Cell) this.option.get("Initial");
        if (z0 != null && !z0.isNull())
        {
            if (!z0.isVector())
            {
                throw new IllegalArgumentException(
                        "computeSmooth : Initial \"Cell\" object parameter must be a vector and not  matrix.");
            }
            isinitial = true;

            int nz0 = z0.numel();// numel(z0);
            if (nz0 != ny)
            {
                throw new IllegalArgumentException("computeSmooth : Z0 must be a valid initial guess for Z");
            }

            for (int ii = 0; ii < nz0; ii++)
            {
                Matrix matInit = (Matrix) z0.getElementAt(ii);
                Indices matInitSiz = matInit.sizeIndices();
                boolean eqSiz2 = sizy.EQ(matInitSiz).allBoolean();
                if (!eqSiz2)
                {
                    throw new IllegalArgumentException(
                            "computeSmooth : Initial guess size for Z is inconsistent with the data.");
                }
                // z0{i} = double(z0{i});
            }
        }

        int m = 2;
        Integer mOrder = (Integer) this.option.get("Order");
        if (mOrder != null)
        {
            m = mOrder;
            if (m < 0 || m > 2)
            {
                throw new IllegalArgumentException("computeSmooth : Invalid order number (= " + m
                        + "). Valid orders are [0, 1, 2]");
            }
        }

        // Weights. Zero weights are assigned to not finite values (Inf or NaN),
        // (Inf/NaN values = missing data).
        Indices IsFinite = ((Matrix) this.data.start()).isfinite();// isfinite(y{1});
        for (int ii = 1; ii < ny; ii++)
        {
            Matrix yi = (Matrix) this.data.getElementAt(ii);
            IsFinite = IsFinite.AND(yi.isfinite());// isfinite(y{i});
        }
        int nof = IsFinite.nnz();// nnz(IsFinite); // number of finite elements
        WL = WL.arrayTimes(IsFinite);// W.*IsFinite;

        if (WL.LT(0.0).anyBoolean())
        {// any(W<0)
         // error('MATLAB:smoothn:NegativeWeights',...
         // 'Weights must all be >=0')
            throw new IllegalArgumentException("computeSmooth : All elements in weights must be non-negative.");
        }
        else
        {
            // W = W/max(W(:));
            WL = WL.arrayRightDivide(JDatafun.max(WL.toColVector()).start());
        }

        // ---
        // Weighted or missing data?
        boolean isweighted = WL.toColVector().LT(1.0).anyBoolean();// any(W(:)<1);
        // ---
        // Robust smoothing?
        boolean isrobust = this.robust;// any(strcmpi(varargin,'robust'));
        // ---
        // Automatic smoothing?
        double s = smoothness;
        boolean isauto = Double.isNaN(smoothness);// isempty(s);

        // Create the Lambda tensor
        // ---
        // Lambda contains the eingenvalues of the difference matrix used in
        // this
        // penalized least squares process (see CSDA paper for details)
        int d = 2;// ndims(y{1});
        Matrix Lambda = Matrix.zeros(sizy.getRowPackedCopy());
        for (int i = 0; i < d; i++)
        {
            Indices siz0 = Indices.ones(1, d);
            // siz0(i) = sizy(i);
            int sizII = sizy.getRowPackedCopy()[i];
            siz0.setElementAt(i, sizII);
            // siz0.reshape(1, 3);
            Matrix toReshape = Matrix.linspace(1, sizy.getElementAt(i), sizy.getElementAt(i));
            toReshape = toReshape.reshape(siz0.getRowPackedCopy()).minus(1);
            toReshape = toReshape.arrayRightDivide(sizy.getElementAt(i));
            toReshape = toReshape.arrayTimes(Math.PI);
            // Lambda = bsxfun(@plus,Lambda,...
            // cos(pi*(reshape(1:sizy(i),siz0)-1)/sizy(i)));
            toReshape = JElfun.cos(toReshape);
            if (toReshape.length() == 1)
            {
                Lambda = Lambda.plus(toReshape.start());
            }
            else
            {
                Lambda = Lambda.plus(toReshape);
            }
        }// end

        // Lambda = ;//2*(d-Lambda);
        Lambda = Lambda.uminus().plus(d).arrayTimes(2);
        Lambda.printInLabel("Lambda");

        Matrix Gamma = null;
        if (!isauto)
        {
            // Gamma = 1./(1+s*Lambda.^m);
            Matrix LambdaPow = JElfun.pow(Lambda, m);
            LambdaPow = LambdaPow.arrayTimes(s).plus(1);
            Gamma = LambdaPow.reciprocate();
        }// end

        // Upper and lower bound for the smoothness parameter
        // The average leverage (h) is by definition in [0 1]. Weak smoothing
        // occurs
        // if h is close to 1, while over-smoothing appears when h is near 0.
        // Upper
        // and lower bounds for h are given to avoid under- or over-smoothing.
        // See
        // equation relating h to the smoothness parameter for m = 2 (Equation
        // #12
        // in the referenced CSDA paper).
        int N = JDatafun.sum(sizy.NEQ(1)).start();// sum(sizy~=1); // tensor
                                                  // rank of the y-array
        double hMin = 1e-6;
        double hMax = 0.99;
        double sMinBnd = Double.NaN;
        double sMaxBnd = Double.NaN;
        double nPow = Double.NaN;
        double basePow = Double.NaN;

        if (m == 0)
        { // Not recommended. For mathematical purpose only.
            nPow = 1.0 / (double) N;
            // sMinBnd = 1/hMax^(1/N)-1;
            sMinBnd = 1.0 / Math.pow(hMax, nPow) - 1.0;
            // sMaxBnd = 1/hMin^(1/N)-1;
            sMaxBnd = 1.0 / Math.pow(hMin, nPow) - 1.0;
        }
        else if (m == 1)
        {
            nPow = 2.0 / (double) N;
            // sMinBnd = (1/hMax^(2/N)-1)/4;
            sMinBnd = 1.0 / Math.pow(hMax, nPow) - 1.0;
            sMinBnd /= 4.0;
            // sMaxBnd = (1/hMin^(2/N)-1)/4;
            sMaxBnd = 1.0 / Math.pow(hMin, nPow) - 1.0;
            sMaxBnd /= 4.0;
        }
        else if (m == 2)
        {
            // sMinBnd = (((1+sqrt(1+8*hMax^(2/N)))/4/hMax^(2/N))^2-1)/16;
            // ( numer ) /16

            nPow = 2 / (double) N;
            double numer2 = 1 + Math.sqrt(1 + 8 * Math.pow(hMax, nPow));
            double inner = numer2 / 4 / Math.pow(hMax, nPow);
            double numer = Math.pow(inner, 2) - 1.0;
            sMinBnd = numer / 16.0;

            // sMaxBnd = (((1+sqrt(1+8*hMin^(2/N)))/4/hMin^(2/N))^2-1)/16;
            numer2 = 1 + Math.sqrt(1 + 8 * Math.pow(hMin, nPow));
            inner = numer2 / 4 / Math.pow(hMin, nPow);
            numer = Math.pow(inner, 2) - 1.0;
            sMaxBnd = numer / 16.0;
        }
        else
        {
            new IllegalArgumentException("Value for parameter \"m\" (= " + m
                    + ") must be in the closed interval [0,2].");
        }

        System.out.println("\n\nsMinBnd = " + sMinBnd);
        System.out.println("\n\nsMaxBnd = " + sMaxBnd);

        // Initialize before iterating
        // ---
        Matrix Wtot = WL;

        // Cell z = null;
        // --- Initial conditions for z
        if (isweighted)
        {
            // --- With weighted/missing data
            // An initial guess is provided to ensure faster convergence. For
            // that
            // purpose, a nearest neighbor interpolation followed by a coarse
            // smoothing are performed.
            // ---
            if (isinitial)
            { // an initial guess (z0) has been already given
                z = z0;
            }
            else
            {
                z = InitialGuess(y, IsFinite);
            }
        }
        else
        {
            z = new Cell(y.sizeIntArr());// cell(size(y));
            for (int i = 0; i < ny; i++)
            {
                // z{i} = zeros(sizy);
                Matrix zeroMat = Matrix.zeros(sizy.getRowPackedCopy());
                z.setElementAt(i, zeroMat);
            }
        }

        // %---
        z0 = z;
        Indices notIsFinite = IsFinite.NOT();
        FindInd find = notIsFinite.findIJ();
        if (!find.isNull())
        {
            int[] nif = find.getIndex();
            for (int i = 0; i < ny; i++)
            {
                // y{i}(~IsFinite) = 0; //% arbitrary values for missing y-data
                Matrix yi = (Matrix) y.getElementAt(i);
                yi.setElements(nif, 0);
            }
        }
        // %---
        // double tol = 1;
        boolean RobustIterativeProcess = true;
        int RobustStep = 1;
        int nit = 0;
        Cell DCTy = new Cell(1, ny);
        // vec = @(x) x(:);
        // %--- Error on p. Smoothness parameter s = 10^p
        double nerrp = 0.1;
        // opt = optimset('TolX',errp);
        // %--- Relaxation factor RF: to speedup convergence
        double RF = 1 + 0.75 * (isweighted ? 1 : 0);

        // %% Main iterative process
        // %---
        while (RobustIterativeProcess)
        {
            // %--- "amount" of weights (see the function GCVscore)
            double aow = JDatafun.sum(Wtot.toColVector()).start() / noe;// sum(Wtot(:))/noe;
                                                                        // //% 0
                                                                        // < aow
                                                                        // <= 1
            // %---
            while (tol > TolZ && nit < MaxIter)
            {
                nit = nit + 1;
                for (int i = 0; i < ny; i++)
                {
                    // DCTy{i} = dctn(Wtot.*(y{i}-z{i})+z{i});
                }// end
                double log2 = (Double) JElfun.log2(nit)[0];

                boolean notremlog = JElfun.rem(log2, 1.0).start() != 0.0;// rem(log2(nit),1);
                if (isauto && !notremlog)
                {
                    /*
                     * %--- % The generalized cross-validation (GCV) method is
                     * used. % We seek the smoothing parameter S that minimizes
                     * the GCV % score i.e. S = Argmin(GCVscore). % Because this
                     * process is time-consuming, it is performed from % time to
                     * time (when the step number - nit - is a power of 2) %---
                     */

                    // fminbnd(@gcv,log10(sMinBnd),log10(sMaxBnd),opt);
                }// end
                for (int i = 0; i < ny; i++)
                {
                    // z{i} = RF*idctn(Gamma.*DCTy{i}) + (1-RF)*z{i};
                }// end

                // % if no weighted/missing data => tol=0 (no iteration)
                Matrix z0Mat = vec(z0.toColVector());
                Matrix zMat = vec(z.toColVector());

                double normNum = z0Mat.minus(zMat).norm();
                double normDen = zMat.norm();
                tol = (isweighted ? 01 : 0) * normNum / normDen;// norm(vec([z0{:}]-[z{:}]))/norm(vec([z{:}]));

                z0 = z; // % re-initialization
            }// end inner while
            exitflag = nit < MaxIter;

            if (isrobust)
            { // %-- Robust Smoothing: iteratively re-weighted process
                // %--- average leverage
                double h = 0.0;
                if (m == 0)
                { // % not recommended
                    h = 1 / (1 + s);
                }
                else if (m == 1)
                {
                    h = 1 / Math.sqrt(1 + 4 * s);
                }
                else if (m == 2)
                {
                    h = Math.sqrt(1 + 16 * s);
                    h = Math.sqrt(1 + h) / Math.sqrt(2) / h;
                }
                else
                {
                    // error('m must be 0, 1 or 2.')
                    throw new ConditionalRuleException("m must be 0, 1 or 2.");
                }// end
                h = Math.pow(h, N);// h^N;
                // %--- take robust weights into account
                Matrix RW = RobustWeights(y, z, IsFinite, h, weightstr);
                Wtot = W.arrayTimes(RW);// W.*RobustWeights(y,z,IsFinite,h,weightstr);
                // %--- re-initialize for another iterative weighted process
                isweighted = true;
                tol = 1.0;
                nit = 0;
                // %---
                RobustStep = RobustStep + 1;
                RobustIterativeProcess = RobustStep < 4; // % 3 robust steps are
                                                         // enough.
            }
            else
            {
                RobustIterativeProcess = false; // % stop the whole process
            }// end
        }// end main while

        System.out.println("\n\nDbStop");

    }

    static Matrix RobustWeights(Cell y, Cell z, Indices I, double h, String wstr)
    {
        return null;
    }

    static Matrix vec(Object X)
    {
        Matrix obj = null;
        if (X == null)
        {
            return obj;
        }
        if (X instanceof Cell)
        {
            Cell xCol = ((Cell) X).toColVector();
            int numEls = xCol.length();
            List<Matrix> matList = new ArrayList<Matrix>();
            for (int i = 0; i < numEls; i++)
            {
                matList.add((Matrix) xCol.getElementAt(i));
            }
            obj = Matrix.mergeV(matList);
        }
        else if (X instanceof Matrix)
        {
            obj = ((Matrix) X).toColVector();
        }
        else
        {
            throw new IllegalArgumentException("Input argument \"X\" must be an instance of \"Cell\" or \"Matrix\"");
        }
        return obj;
    }

    public void setOptionKeyValue(Object key, Object value)
    {
        Set<Object> optionSet = this.option.getKeySet();
        boolean hasIt = optionSet.contains(key);
        if (!hasIt)
        {
            System.out.println("Parameter Key name \"" + key + "\" is invalid.");
            Cell optionCell = Cell.listToCell(new ArrayList<Object>(optionSet));
            optionCell.printCell("Valid Parameter Names");
            return;
        }
        this.option.add(key, value);
    }

    public boolean isRobust()
    {
        return robust;
    }

    public void setRobust(boolean robust)
    {
        this.robust = robust;
    }

    // Initial Guess with weighted/missing data
    static Cell InitialGuess(Cell y, Indices I)
    {
        int ny = y.numel();// numel(y);
        // - nearest neighbor interpolation (in case of missing values)
        boolean anyI = true;//
        boolean imgLicense = false;
        Cell z = null;

        boolean scircuit = true;
        if (scircuit)
        {
            throw new IllegalArgumentException("To be implemented.");
        }

        if (anyI)
        {// any(~I(:))
            z = new Cell(y.sizeIntArr());
            if (imgLicense)
            {// license('test','image_toolbox') //(exist('bwdist','file'))
             // for i = 1:ny
             // [z{i},L] = bwdist(I);
             // z{i} = y{i};
             // z{i}(~I) = y{i}(L(~I));
             // end
            }
            else
            {
                // If BWDIST does not exist, NaN values are all replaced with
                // the
                // same scalar. The initial guess is not optimal and a warning
                // message thus appears.
                z = y;
                for (int i = 0; i < ny; i++)
                {
                    // z{i}(~I) = mean(y{i}(I));
                }
                throw new IllegalArgumentException("To be implemented.");

                // warning('MATLAB:smoothn:InitialGuess',...
                // ['BWDIST (Image Processing Toolbox) does not exist. ',...
                // 'The initial guess may not be optimal; additional',...
                // ' iterations can thus be required to ensure complete',...
                // ' convergence. Increase ''MaxIter'' criterion if
                // necessary.'])
            }
        }
        else
        {
            z = y;
        }
        // -- coarse fast smoothing using one-tenth of the DCT coefficients
        Indices siz = ((Matrix) z.start()).sizeIndices();// size(z{1});
        // z = cellfun(@(x) dctn(x),z,'UniformOutput',0);
        int ndims = 2;// ndims(z{1})
        for (int k = 0; k < ndims; k++)
        {// 1:ndims(z{1})
            for (int i = 0; i < ny; i++)
            {// i = 1:ny
             // z{i}(ceil(siz(k)/10)+1:end,:) = 0;
             // z{i} = reshape(z{i},circshift(siz,[0 1-k]));
             // z{i} = shiftdim(z{i},1);
            }
        }
        // z = cellfun(@(x) idctn(x),z,'UniformOutput',0);
        return z;
    }

    static Matrix testMat()
    {
        double[] arr =
        {
                0.18070075, 0.1983774, 0.1858855, 0.20250514, 0.2636717, 0.28463835, 0.18927014, 0.21697817,
                0.23876101, 0.18720646, 0.21953171, 0.21541255, 0.23842597, 0.21227763, 0.25527886, 0.36459568,
                0.25095313, 0.32952499, 0.44032701, 0.59919104, 0.53352143, 0.49424367, 0.4722145, 0.62989495,
                0.76366125, 0.51699277, 0.46491929, 0.47939953, 0.52504567, 0.521413, 0.55660825, 0.46417675,
                0.40080193, 0.55175675, 0.98395132, 0.44153825, 0.40033016, 0.51512282, 0.3305724, 0.49988245,
                0.48619224, 0.52695489, 0.2772919, 0.42077821, 0.37752675, 0.43022949, 0.5034834, 0.32052971,
                0.29636091

        };
        return new Matrix(arr);
    }

    public static void main(String[] args)
    {
        Matrix datMat = testMat();
        Smoothn SM = new Smoothn(datMat);
        SM.setRobust(true);
        SM.computeSmooth();

    }

}
