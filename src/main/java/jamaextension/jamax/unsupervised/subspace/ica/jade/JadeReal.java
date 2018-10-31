/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.unsupervised.subspace.ica.jade;

import jamaextension.jamax.EigenvalueDecomposition;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.Sort;
import jamaextension.jamax.datafun.SortMat;
import jamaextension.jamax.elfun.JElfun;
import jamaextension.jamax.matfun.JMatfun;

/**
 * 
 * @author Sione
 */
public class JadeReal
{

    private Matrix mixingData;
    private Matrix separatedSources;
    private int numSources;
    private boolean verbose = false;
    private boolean built = false;
    private boolean initCumulantByDiag = false;

    public JadeReal(Matrix dataX)
    {
        this(dataX, null);
    }

    public JadeReal(Matrix dataX, Integer m)
    {
        if (dataX == null)
        {
            throw new IllegalArgumentException("JadeReal : Parameter \"dataX\" must be non-null.");
        }
        if (dataX.isVector())
        {
            throw new IllegalArgumentException("JadeReal : Parameter \"dataX\" must be a matrix and not a vector.");
        }
        this.mixingData = dataX;

        if (m == null)
        {
            this.numSources = dataX.getRowDimension();
        }
        else if (m.intValue() < 2)
        {
            throw new IllegalArgumentException("JadeReal : Value for parameter \"m\" must be at least two.");
        }
        else
        {
            this.numSources = m.intValue();
        }

        if (this.numSources > this.mixingData.getRowDimension())
        {
            throw new IllegalArgumentException(
                    "JadeReal : More sensors than sources, ie \"m\" must equal to or less than \"n\" (number of rows in \"dataX\").");
        }

    }

    public void build()
    {
        Matrix X = this.mixingData;
        int m = this.numSources;

        int n = X.getRowDimension();
        int T = X.getColumnDimension();

        if (verbose)
        {
            System.out.println("JadeReal -> Looking for " + m + " sources.\n");
        }

        // Mean removal
        if (verbose)
        {
            System.out.println("JadeReal -> Removing the mean value.\n");
        }
        Matrix temp = JDatafun.mean(X.transpose()).transpose().times(Matrix.ones(1, T));
        X = X.minus(temp);// - mean(X')' * ones(1,T);

        /*
         * %% whitening & projection onto signal subspace %
         * ===========================================
         */
        if (verbose)
        {
            System.out.println("JadeReal -> Whitening the data.\n");
        }
        // [U,D] = eig((X*X')/T) ; %% An eigen basis for the sample covariance
        // matrix
        temp = X.times(X.transpose()).arrayRightDivide((double) T);
        EigenvalueDecomposition eig = temp.eig();
        Matrix U = eig.getV();
        Matrix D = eig.getD();
        // [Ds,k] = sort(diag(D)) ; %% Sort by increasing variances
        Sort sort = new SortMat(D.diag());
        Matrix Ds = (Matrix) sort.getSortedObject();
        Indices k = sort.getIndices();
        // PCs = n:-1:n-m+1 ; %% The m most significant princip. comp. by
        // decreasing variance
        Indices PCs = Indices.linspace(n - m, n - 1).flipLR();

        // --- PCA ----------------------------------------------------------
        int[] arrInd = PCs.getRowPackedCopy();
        arrInd = k.getElements(arrInd).getRowPackedCopy();
        Matrix B = U.getColumns(arrInd).transpose();// U(:,k(PCs))' ; // At this
                                                    // stage, B does the PCA on
                                                    // m components

        // --- Scaling ------------------------------------------------------
        Matrix scales = JElfun.sqrt(Ds.getElements(PCs.getRowPackedCopy())); // The
                                                                             // scales
                                                                             // of
                                                                             // the
                                                                             // principal
                                                                             // components
                                                                             // .
        B = scales.reciprocate().diag().times(B);// diag(1./scales)*B ; // Now,
                                                 // B does PCA followed by a
                                                 // rescaling = sphering

        // --- Sphering ------------------------------------------------------
        X = B.times(X); // B is a whitening matrix and now X is white.

        /*
         * %% Estimation of the cumulant matrices. %
         * ====================================
         */
        if (verbose)
        {
            System.out.println("JadeReal -> Estimating cumulant matrices.\n");
        }
        // Reshaping of the data, to speed up things a little...
        X = X.transpose();

        int dimsymm = (m * (m + 1)) / 2; // Dim. of the space of real symm
                                         // matrices
        int nbcm = dimsymm; // number of cumulant matrices
        Matrix CM = Matrix.zeros(m, m * nbcm); // Storage for cumulant matrices
        Matrix R = Matrix.eye(m);
        Matrix Qij = Matrix.zeros(m); // Temp for a cum. matrix
        Matrix Xim = Matrix.zeros(m, 1); // Temp
        Matrix Xijm = Matrix.zeros(m, 1); // Temp
        // Matrix Uns = Matrix.ones(1, m); // for convenience

        Indices Range = Indices.linspace(0, m - 1); // will index the columns of
                                                    // CM where to store the
                                                    // cum. mats.

        for (int im = 0; im < m; im++)
        {
            Xim = X.getColumnAt(im);// X(:,im) ;
            Xijm = Xim.arrayTimes(Xim);// Xim.*Xim ;

            // Note : the -R on next line can be removed: it does not affect
            // the joint diagonalization criterion
            temp = R.getColumnAt(im).times(R.getColumnAt(im).transpose()).arrayTimes(2.0).plus(R);
            Qij = Xijm.repmat(1, m).arrayTimes(X).transpose().times(X).arrayRightDivide((double) T).minus(temp);// ((Xijm(:,Uns).*X)'
                                                                                                                // *
                                                                                                                // X)/T
                                                                                                                // -
                                                                                                                // R
                                                                                                                // -
                                                                                                                // 2
                                                                                                                // *
                                                                                                                // R(:,im)*R(:,im)'
                                                                                                                // ;
            CM.setColumns(Range.getRowPackedCopy(), Qij);// CM(:,Range) = Qij ;
            Range = Range.plus(m);

            for (int jm = 0; jm < (im - 1); jm++)
            {
                Xijm = Xim.arrayTimes(X.getColumnAt(jm));// Xim.*X(:,jm) ;
                temp = R.getColumnAt(jm).times(R.getColumnAt(im).transpose());
                Matrix temp2 = R.getColumnAt(im).times(R.getColumnAt(jm).transpose());
                temp = temp.plus(temp2);
                // Qij = sqrt(2) *( ((Xijm(:,Uns).*X)' * X)/T - R(:,im)*R(:,jm)'
                // - R(:,jm)*R(:,im)' ) ;
                Qij = Xijm.repmat(1, m).arrayTimes(X).transpose().times(X).arrayRightDivide((double) T).minus(temp)
                        .arrayTimes(Math.sqrt(2.0));
                CM.setColumns(Range.getRowPackedCopy(), Qij);// CM(:,Range) =
                                                             // Qij ;
                Range = Range.plus(m);// Range = Range + m ;
            }
        }

        // joint diagonalization of the cumulant matrices
        // Initialize
        Matrix V = null;
        if (this.initCumulantByDiag)
        { // Init by diagonalizing a *single* cumulant matrix. It seems to save
          // some computation time `sometimes'. Not clear if initialization is
          // really worth
          // it since Jacobi rotations are very efficient. On the other hand,
          // it does not
          // cost much...

            // if verbose, fprintf('jade -> Initialization of the
            // diagonalization\n'); end
            if (verbose)
            {
                System.out.println("JadeReal -> Initialization of the diagonalization.\n");
            }
            arrInd = Indices.linspace(0, m - 1).getRowPackedCopy();
            // [V,D] = eig(CM(:,1:m)); // Selectng a particular cumulant matrix.
            eig = CM.getColumns(arrInd).eig();
            V = eig.getV();
            // D = eig.getD();
            for (int u = 0; u < m * nbcm; u = u + m)
            { // Accordingly updating the cumulant set given the init
              // CM(:,u:u+m-1) = CM(:,u:u+m-1)*V ;
                arrInd = Indices.linspace(u, u + m - 1).getRowPackedCopy();
                temp = CM.getColumns(arrInd).times(V);
                CM.setColumns(arrInd, temp);
            }
            CM = V.transpose().times(CM);// V'*CM;
        }
        else
        { // non-smart init
            V = Matrix.eye(m); // la rotation initiale
        }

        // Computing the initial value of the contrast
        Matrix Diag = Matrix.zeros(m, 1);
        double On = 0.0;
        Range = Indices.linspace(0, m - 1); // Range = 1:m ;
        for (int im = 0; im < nbcm; im++)
        {
            Diag = CM.getColumns(Range.getRowPackedCopy()).diag();// diag(CM(:,Range))
                                                                  // ;
            On = On + JDatafun.sum(Diag.arrayTimes(Diag)).start();
            Range = Range.plus(m);
        }
        double Off = JDatafun.sum(JDatafun.sum(CM.arrayTimes(CM))).start() - On;

        double seuil = 1.0e-6 / Math.sqrt((double) T); // A statistically scaled
                                                       // threshold on `small'
                                                       // angles
        boolean encore = true;
        int sweep = 0; // sweep number
        int updates = 0; // Total number of rotations
        int upds = 0; // Number of rotations in a given seep
        Matrix g = Matrix.zeros(2, nbcm);
        Matrix gg = Matrix.zeros(2, 2);
        Matrix G = Matrix.zeros(2, 2);
        double c = 0;
        double s = 0;
        double ton = 0;
        double toff = 0;
        double theta = 0;
        double Gain = 0;

        // %% Joint diagonalization proper
        if (verbose)
        {
            System.out.println("JadeReal -> Contrast optimization by joint diagonalization.\n");
        }

        // //////////////////////////////////////////////////////////////////////
        while (encore)
        {

            encore = false;

            // if verbose, fprintf('jade -> Sweep #%3d',sweep); end
            if (verbose)
            {
                System.out.println("JadeReal -> Sweep #" + sweep);
            }

            sweep = sweep + 1;
            upds = 0;
            Matrix Vkeep = V;

            for (int p = 0; p < m - 1; p++)
            {
                for (int q = p; q < m; q++)
                {

                    int[] Ip = Indices.linspace(p, m * nbcm - 1, m).getRowPackedCopy();// p:m:m*nbcm
                                                                                       // ;
                    int[] Iq = Indices.linspace(q, m * nbcm - 1, m).getRowPackedCopy();// q:m:m*nbcm
                                                                                       // ;
                    int[] IpIq = Indices.linspace(p, m * nbcm - 1, m).mergeH(Indices.linspace(q, m * nbcm - 1, m))
                            .getRowPackedCopy();

                    // computation of Givens angle
                    // g = [ CM(p,Ip)-CM(q,Iq) ; CM(p,Iq)+CM(q,Ip) ];
                    temp = CM.getMatrix(p, Ip).minus(CM.getMatrix(q, Iq));
                    Matrix temp2 = CM.getMatrix(p, Iq).plus(CM.getMatrix(q, Ip));
                    temp = temp.mergeV(temp2);
                    gg = g.times(g.transpose());// g*g';
                    ton = gg.get(0, 0) - gg.get(1, 1);
                    toff = gg.get(0, 1) + gg.get(1, 0);
                    theta = 0.5 * Math.atan2(toff, ton + Math.sqrt(ton * ton + toff * toff));
                    Gain = (Math.sqrt(ton * ton + toff * toff) - ton) / 4.0;

                    // Givens update
                    if (Math.abs(theta) > seuil)
                    {
                        // if Gain > 1.0e-3*On/m/m ,
                        encore = true;
                        upds = upds + 1;
                        c = Math.cos(theta);
                        s = Math.sin(theta);
                        // G = [ c -s ; s c ] ;
                        double[][] Garr =
                        {
                                {
                                        c, -s
                                },
                                {
                                        s, c
                                }
                        };
                        G = new Matrix(Garr);

                        int[] pair =
                        {
                                p, q
                        };
                        // V(:,pair) = V(:,pair)*G ;
                        temp = V.getColumns(pair).times(G);
                        V.setColumns(pair, temp);
                        // CM(pair,:) = G' * CM(pair,:) ;
                        temp = G.transpose().times(CM.getRows(pair));
                        CM.setRows(pair, temp);
                        // CM(:,[Ip Iq]) = [ c*CM(:,Ip)+s*CM(:,Iq)
                        // -s*CM(:,Ip)+c*CM(:,Iq) ] ;
                        temp = CM.getColumns(Ip).arrayTimes(c).plus(CM.getColumns(Iq).arrayTimes(s));
                        temp2 = CM.getColumns(Ip).arrayTimes(-s).plus(CM.getColumns(Iq).arrayTimes(c));
                        temp = temp.mergeH(temp2);
                        CM.setColumns(IpIq, temp);

                        On = On + Gain;
                        Off = Off - Gain;

                        // fprintf('jade -> %3d %3d %12.8f\n',p,q,Off/On);
                    } // of the if
                } // of the loop on q
            } // of the loop on p
              // if verbose, fprintf(' completed in %d rotations\n',upds); end
            if (verbose)
            {
                System.out.println(" completed in " + upds + " rotations\n");
            }

            updates = updates + upds;

        } // of the while loop
          // //////////////////////////////////////////////////////////////////////

        if (verbose)
        {
            System.out.println("JadeReal -> Total of " + updates + " Givens rotations.\n");
        }

        /*
         * %% A separating matrix % ===================
         */
        B = V.transpose().times(B);// V'*B ;

        /*
         * %% Permut the rows of the separating matrix B to get the most
         * energetic components first. %%% Here the **signals** are normalized
         * to unit variance. Therefore, the sort is %%% according to the norm of
         * the columns of A = pinv(B)
         */
        if (verbose)
        {
            System.out.println("JadeReal -> Sorting the components.\n");
        }

        Matrix A = JMatfun.pinv(B);
        // [Ds,keys] = sort(sum(A.*A)) ;
        temp = JDatafun.sum(A.arrayTimes(A));
        sort = new SortMat(temp);
        int[] keys = sort.getIndices().getRowPackedCopy();
        B = B.getRows(keys);// B(keys,:) ;
        // B = B(m:-1:1,:) ;
        arrInd = Indices.linspace(0, m - 1).flipLR().getRowPackedCopy();
        B = B.getRows(arrInd);

        // Signs are fixed by forcing the first column of B to have non-negative
        // entries.

        // if verbose, fprintf('jade -> Fixing the signs\n',updates); end
        if (verbose)
        {
            System.out.println("JadeReal -> Fixing the signs.\n");
        }

        Matrix b = B.getColumnAt(0);// B(:,1) ;
        Matrix signs = JDatafun.sign(JDatafun.sign(b).plus(0.1)); // just a
                                                                  // trick to
                                                                  // deal with
                                                                  // sign=0
        B = signs.diag().times(B);// diag(signs)*B ;

        this.separatedSources = B;

        built = true;
    }

    /**
     * @return the mixingData
     */
    public Matrix getMixingData()
    {

        return mixingData;
    }

    /**
     * @return the separatedSources
     */
    public Matrix getSeparatedSources()
    {
        if (!isBuilt())
        {
            throw new IllegalArgumentException("getSeparatedSources : The method \"build\" must be called first.");
        }
        return separatedSources;
    }

    /**
     * @return the numSources
     */
    public int getNumSources()
    {

        return numSources;
    }

    /**
     * @return the verbose
     */
    public boolean isVerbose()
    {
        return verbose;
    }

    /**
     * @param verbose
     *            the verbose to set
     */
    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    /**
     * @return the built
     */
    public boolean isBuilt()
    {
        return built;
    }

    /**
     * @return the initCumulantByDiag
     */
    public boolean isInitCumulantByDiag()
    {
        return initCumulantByDiag;
    }

    /**
     * @param initCumulantByDiag
     *            the initCumulantByDiag to set
     */
    public void setInitCumulantByDiag(boolean initCumulantByDiag)
    {
        this.initCumulantByDiag = initCumulantByDiag;
    }
}
