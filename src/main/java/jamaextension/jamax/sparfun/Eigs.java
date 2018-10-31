/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.sparfun;

import java.util.ArrayList;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elmat.JElmat;

/**
 * 
 * @author Sione
 */
public class Eigs
{

    private ArrayList<Object> varargout;
    // Internal
    private Object A;
    private boolean Amatrix;
    private boolean isrealprob;
    private boolean issymA;
    private Object n;// can be integer
    private Object B;
    private Object classAB;
    private Object k;
    private Object eigs_sigma;
    private Object whch;
    private Object sigma;
    private Double tol;
    private int maxit;
    private int p;
    private int info;
    private int eigs_display;
    private boolean cholB;
    private Object permB;
    private Object resid;
    private boolean useeig;
    private Object afunNargs;
    // Internal 2
    private int nargin = 0;
    private int mode = 0;
    private Indices ipntr;
    private int ido = 0;
    private int eigs_iter = 0;
    private Object workl;
    private Indices iparam;
    private Indices select;
    private String aupdfun = "";
    private String eupdfun = "";

    public Eigs(int nargout, Object[] varargin)
    {
        // Process inputs and do error-checking
        if (nargout > 3)
        {
            throw new ConditionalException("Eigs : Number of output arguments (= " + nargout + ") must be 3 or less.");
        }// end

        String msg = "";

        Indices cputms = new Indices(5, 1);
        int t0 = (int) System.currentTimeMillis();// cputime; //% start timing
                                                  // pre-processing

        nargin = varargin.length;

        Object[] obj = checkInputs(varargin);

        A = obj[0];
        Amatrix = (Boolean) obj[1];
        isrealprob = (Boolean) obj[2];
        issymA = (Boolean) obj[3];
        n = (Integer) obj[4];
        B = obj[5];
        classAB = obj[6];
        k = (Integer) obj[7];
        eigs_sigma = obj[8];
        whch = obj[9];
        sigma = obj[10];
        tol = (Double) obj[11];
        maxit = (Integer) obj[12];
        p = (Integer) obj[13];
        info = (Integer) obj[14];
        eigs_display = (Integer) obj[15];
        cholB = (Boolean) obj[16];
        permB = (Boolean) obj[17];
        resid = obj[18];
        useeig = (Boolean) obj[19];
        afunNargs = obj[20];

        // ow have enough information to do early return on cases EIGS does not
        // handle. For these cases, use the full EIG code.
        if (useeig)
        {
            fullEig(nargout);
            return;
        }// end

        boolean cond = ("SM".equals((String) eigs_sigma)) || !(eigs_sigma instanceof String);
        boolean cond2 = B == null && ((B instanceof Matrix) && (((Matrix) B).isNull()));

        if (cond)
        {// strcmp(eigs_sigma,'SM') || ~ischar(eigs_sigma)
         // % eigs(A,B,k,scalarSigma) or eigs(A,B,k,'SM'), B may be []
         // % Note: sigma must be real for [s,d]saupd and [s,d]naupd
         // % If sigma is complex, even if A and B are both real, we use
         // [c,z]naupd.
         // % This means that mode=3 in [s,d]naupd, which has
         // % OP = real(inv(A - sigma*M)*M) and B = M
         // % reduces to the same OP as [s,d]saupd and [c,z]naupd.
         // % A*x = lambda*M*x, M symmetric (positive) semi-definite
         // % => OP = inv(A - sigma*M)*M and B = M
         // % => shift-and-invert mode
            mode = 3;
        }
        else if (cond2)
        {// isempty(B)
         // % eigs(A,k,stringSigma) or eigs(A,[],k,stringSigma),
         // stringSigma~='SM'
         // % A*x = lambda*x
         // % => OP = A and B = I
            mode = 1;
        }
        else
        {
            // % eigs(A,B,k,stringSigma), stringSigma~='SM'
            // % A*x = lambda*B*x
            // % Since we can always Cholesky factor B, follow the advice of
            // % Remark 3 in ARPACK Users' Guide, and do not use mode = 2.
            // % Instead, use mode = 1 with OP(x) = R'\(A*(R\x)) and B = I
            // % where R is B's upper triangular Cholesky factor: B = R'*R.
            // % Finally, V = R\V returns the actual generalized eigenvectors of
            // (A,B).
            mode = 1;
        }// end

        cond2 = B != null && ((B instanceof Matrix) && !((Matrix) B).isNull());
        Object RB = null;
        Object RBT = null;
        if (cholB || ((mode == 1) && cond2))
        {// && ~isempty(B))
         // % The reordering permutation permB is [] unless B is sparse
            obj = CHOLfactorB(); // [RB,RBT,permB]
            RB = obj[0];
            RBT = obj[1];
            permB = obj[2];
        }// end

        Object permAsB = null;// = [];
        Object L = null;
        Object U = null;
        Object P = null;
        if ((mode == 3) && Amatrix)
        {// % need lu(A-sigma*B)
         // % The reordering permutation permAsB is [] unless A-sigma*B is
         // sparse
         // [L,U,P,permAsB]
            obj = LUfactorAminusSigmaB();
            L = obj[0];
            U = obj[1];
            P = obj[2];
            permAsB = obj[3];
        }// end // if (mode == 3) && Amatrix

        // % Allocate outputs and ARPACK work variables
        int lworkl = 0;

        Object d = null;
        Object workev = null;
        Object di = null;
        Object v = null;
        Object workd = null;

        if (isrealprob)
        {
            if (issymA)
            {// % real and symmetric
                if (false)
                {// strcmp(classAB,'single')
                    aupdfun = "ssaupd";
                    eupdfun = "sseupd";
                }
                else
                {
                    aupdfun = "dsaupd";
                    eupdfun = "dseupd";
                }// end
                lworkl = p * (p + 8);
                // d = zeros(k,1,classAB);
                if (isMatrix(classAB))
                {
                    d = new Matrix((Integer) k, 1);
                }
                else if (isIndices(classAB))
                {
                    d = new Indices((Integer) k, 1);
                }
                else
                {
                    throw new ConditionalException(
                            "Eigs : Parameter \"classAB\" must be and instance of \"Matrix\" or \"Indices\"");
                }
            }
            else
            {// % real but not symmetric
                if (false)
                {// strcmp(classAB,'single')
                    aupdfun = "snaupd";
                    eupdfun = "sneupd";
                }
                else
                {
                    aupdfun = "dnaupd";
                    eupdfun = "dneupd";
                }// end
                lworkl = 3 * p * (p + 2);

                // workev = zeros(3*p,1,classAB);
                // d = zeros(k+1,1,classAB);
                // di = zeros(k+1,1,classAB);
                if (isMatrix(classAB))
                {
                    workev = new Matrix(3 * p, 1);
                    d = new Matrix((Integer) k + 1, 1);
                    di = new Matrix((Integer) k + 1, 1);
                }
                else if (isIndices(classAB))
                {
                    workev = new Indices(3 * p, 1);
                    d = new Indices((Integer) k + 1, 1);
                    di = new Indices((Integer) k + 1, 1);
                }
                else
                {
                    throw new ConditionalException(
                            "Eigs : Parameter \"classAB\" must be and instance of \"Matrix\" or \"Indices\"");
                }

            }// end
             // v = zeros(n,p,classAB);
             // workd = zeros(n,3,classAB);
             // workl = zeros(lworkl,1,classAB);
            if (isMatrix(classAB))
            {
                v = new Matrix((Integer) n, p);
                workd = new Matrix((Integer) n, 3);
                workl = new Matrix(lworkl, 1);
            }
            else if (isIndices(classAB))
            {
                v = new Indices((Integer) n, p);
                workd = new Indices((Integer) n, 3);
                workl = new Indices(lworkl, 1);
            }
            else
            {
                throw new ConditionalException(
                        "Eigs : Parameter \"classAB\" must be and instance of \"Matrix\" or \"Indices\"");
            }

        }
        else
        { // % complex
            if (true)
            {
                throw new ConditionalException("LUfactorAminusSigmaB : Only real matrices allowed and no complex.");
            }
            if (cond)
            {// strcmp(classAB,'single')
             // aupdfun = 'cnaupd';
             // eupdfun = 'cneupd';
            }
            else
            {
                // aupdfun = 'znaupd';
                // eupdfun = 'zneupd';
            }// end
             // zv = zeros(2*n*p,1,classAB);
             // workd = complex(zeros(n,3,classAB));
             // zworkd = zeros(2*numel(workd),1,classAB);
             // lworkl = int32(3*p^2+5*p);
             // workl = zeros(2*lworkl,1,classAB);
             // workev = zeros(2*2*p,1,classAB);
             // zd = zeros(2*(k+1),1,classAB);
             // rwork = zeros(p,1,classAB);
        }// end

        int ldv = (Integer) n;
        ipntr = new Indices(15, 1);// zeros(15,1,'int32');
        ido = 0;// int32(0); //% reverse communication parameter, initial value
        cond2 = B == null || ((B instanceof Matrix) && ((Matrix) B).isNull());
        String bmat = "G"; // % generalized eigenvalue problem
        if (cond2 || (mode == 1))
        {// isempty(B) || (mode == 1)
            bmat = "I"; // % standard eigenvalue problem
        } // else {
          // bmat = 'G'; //% generalized eigenvalue problem
          // //end

        int nev = (Integer) k; // % number of eigenvalues requested
        int ncv = p; // % number of Lanczos vectors
        iparam = new Indices(11, 1);// zeros(11,1,'int32');
        // % iparam(1) = ishift = 1 ensures we are never asked to handle ido=3
        iparam.setElementAt(0, 1);
        iparam.setElementAt(2, maxit);
        iparam.setElementAt(6, mode); // iparam([1 3 7]) = [1 maxit mode];

        select = new Indices(p, 1);// zeros(p,1,'int32');

        // % To Do: Remove this error when ARPACKC supports singles
        if (false)
        {// strcmp(classAB,'single')
         // error('MATLAB:eigs:single', ...
         // 'EIGS does not support single precision inputs.')
            throw new ConditionalException("Eigs : Does not support single precision inputs.");
        }// end

        // % The ARPACK routines return to EIGS many times per each iteration
        // but we
        // % only want to display the Ritz values once per iteration (if
        // opts.disp>0).
        // % Keep track of whether we've displayed this iteration yet in
        // eigs_iter.
        eigs_iter = 0;

        // cputms(1) = cputime - t0; // end timing pre-processing
        int valL = (int) System.currentTimeMillis() - t0;
        cputms.setElementAt(0, valL);

        // ///////////////////////////// WHILE
        // //////////////////////////////////
        // % Iterate until ARPACK's reverse communication parameter ido says to
        // stop
        while (ido != 99)
        {

            t0 = (int) System.currentTimeMillis();// cputime; //% start timing
                                                  // ARPACK calls **aupd

            if (isrealprob)
            {
                // arpackc( aupdfun, ido, ...
                // bmat, int32(n), whch, nev, tol, resid, ncv, ...
                // v, ldv, iparam, ipntr, workd, workl, lworkl, info );
                if (true)
                {
                    throw new ConditionalException("Eigs : To be implemented.");
                }
            }
            else
            {
                // % The FORTRAN ARPACK routine expects the complex input zworkd
                // to have
                // % real and imaginary parts interleaved, but the OP about to
                // be
                // % applied to workd expects it in MATLAB's complex
                // representation with
                // % separate real and imaginary parts. Thus we need both.
                if (true)
                {
                    throw new ConditionalException("Eigs : Only real matrices allowed and no complex.");
                }
                // zworkd(1:2:end-1) = real(workd);
                // zworkd(2:2:end) = imag(workd);
                // arpackc( aupdfun, ido, ...
                // bmat, int32(n), whch, nev, tol, resid, ncv, ...
                // zv, ldv, iparam, ipntr, zworkd, workl, lworkl, rwork, info );
                // workd =
                // reshape(complex(zworkd(1:2:end-1),zworkd(2:2:end)),[n,3]);
            }// end

            if (info < 0)
            {
                // error('MATLAB:eigs:ARPACKroutineError', ...
                // 'Error with ARPACK routine %s: info = %d', ...
                // aupdfun,full(double(info)))
                throw new ConditionalException("Eigs : Error with ARPACK routine \"" + aupdfun + "\" : info = " + info);
            }// end

            valL = cputms.getElementAt(1) + ((int) System.currentTimeMillis() - t0);//
            cputms.setElementAt(1, valL);// cputms(2) = cputms(2) +
                                         // (cputime-t0); //% end timing ARPACK
                                         // calls **aupd
            t0 = (int) System.currentTimeMillis();// cputime; //% start timing
                                                  // MATLAB OP(X)

            // % Compute which columns of workd ipntr references
            Indices cols = checkIpntr();

            // % The ARPACK reverse communication parameter ido tells EIGS what
            // to do
            switch (ido)
            {
            case -1:// {-1,1} //% abs(ido)==1 => workd(:,col2) =
                    // OP*workd(:,col1)
            case 1:
            {
                switch (mode)
                {

                case 1: // % mode==1 => OP(x) = K*x
                    cond = B == null || ((B instanceof Matrix) && ((Matrix) B).isNull());
                    if (true)
                    {
                        throw new ConditionalException("Eigs : To be implemented.");
                    }
                    if (cond)
                    {// isempty(B) //% standard eigenvalue problem
                     // % OP(x) = A*x
                     // workd(:,cols(2)) = Amtimes(workd(:,cols(1)));
                    }
                    else
                    { // % generalized eigenvalue problem
                      // % OP(x) = R'\(A*(R\x))
                      // workd(:,cols(2)) =
                      // RBTsolve(Amtimes(RBsolve(workd(:,cols(1)))));
                    }// end
                    break;
                case 3: // % mode==3 => OP(x) = inv(A-sigma*B)*B*x
                    cond = B == null || ((B instanceof Matrix) && ((Matrix) B).isNull());
                    if (cond)
                    {// isempty(B) //% standard eigenvalue problem
                     // workd(:,cols(2)) =
                     // AminusSigmaBsolve(workd(:,cols(1)));
                        if (true)
                        {
                            throw new ConditionalException("Eigs : To be implemented.");
                        }
                    }
                    else
                    { // % generalized eigenvalue problem
                        switch (ido)
                        {
                        case -1:
                            // workd(:,cols(2)) = Bmtimes(workd(:,cols(1)));
                            // workd(:,cols(2)) =
                            // AminusSigmaBsolve(workd(:,cols(2)));
                            if (true)
                            {
                                throw new ConditionalException("Eigs : To be implemented.");
                            }
                            break;
                        case 1:
                            // % mode==3 and ido==1:
                            // % workd(:,col2) = inv(A-sigma*B)*B*x
                            // % but B*x is already pre-computed in
                            // workd(:,col3)

                            if (true)
                            {
                                throw new ConditionalException("Eigs : To be implemented.");
                            }
                            // workd(:,cols(2)) =
                            // AminusSigmaBsolve(workd(:,cols(3)));
                            break;
                        default:
                        {// otherwise
                         // error('MATLAB:eigs:UnknownRCP',...
                         // 'Unknown reverse communication parameter.')
                            throw new ConditionalException("Eigs : Unknown reverse communication parameter.");
                        }
                        }// end % switch ido (inner)
                    }// end % if isempty(B)
                    break;
                default:
                {// otherwise % mode is not 1 or 3
                 // error('MATLAB:eigs:UnknownMode','Unknown mode.')
                    throw new ConditionalException("Eigs : Unknown reverse communication parameter.");
                }
                }// end % switch (mode)
                break;
            }
            case 2:
            { // % ido==2 => workd(:,col2) = B*workd(:,col1)

                if (mode == 3)
                {
                    // workd(:,cols(2)) = Bmtimes(workd(:,cols(1)));
                    if (true)
                    {
                        throw new ConditionalException("Eigs : To be implemented.");
                    }
                }
                else
                {
                    throw new ConditionalException("Eigs : Unknown mode.");// error('MATLAB:eigs:UnknownMode','Unknown
                                                                           // mode.')
                }// end
                break;
            }
            case 3:
            { // % ido==3 => EIGS does not know how to compute shifts
              // % setting iparam(1) = ishift = 1 ensures this never happens

                // warning('MATLAB:eigs:WorklShiftsUnsupported', ...
                // ['EIGS does not support computing the shifts in workl.' ...
                // ' Returning immediately.'])
                msg = "This routine does not support computing the shifts in workl.\n";
                msg += "Returning immediately.";
                System.out.println("Eigs : " + msg);
                ido = 99;
                break;
            }
            case 99:
            { // % ido==99 => ARPACK is done
                break;
            }
            default:
            { // otherwise
              // error('MATLAB:eigs:UnknownReverseCommParamFromARPACK',...
              // ['Unknown value of reverse communication parameter' ...
              // ' returned from %s.'],aupdfun)
                msg = "Unknown value of reverse communication parameter\n";
                msg += " returned from \"" + aupdfun + "\".";
                throw new ConditionalException("Eigs : " + msg);
            }
            }// end % switch ido (outer)

            valL = cputms.getElementAt(2) + ((int) System.currentTimeMillis() - t0);
            cputms.setElementAt(2, valL);// cputms(3) = cputms(3) +
                                         // (cputime-t0); //% end timing MATLAB
                                         // OP(X)

            if (eigs_display != 0)
            {
                displayRitzValues();
            }// end

        }// end % while (ido ~= 99)
         // ///////////////////////////// END WHILE
         // //////////////////////////////

        t0 = (int) System.currentTimeMillis();// cputime; //% start timing
                                              // post-processing

        if (info < 0)
        {
            // error('MATLAB:eigs:ARPACKroutineError', ...
            // 'Error with ARPACK routine %s: info = %d',aupdfun,full(info));
            msg = "Unknown value of reverse communication parameter\n";
            msg += " returned from \"" + aupdfun + "\".";
            throw new ConditionalException("Eigs : " + msg);
        }// end % if (info < 0)

        boolean rvec = false;
        if (nargout >= 2)
        {
            rvec = true; // % compute eigenvectors
        }
        else
        {
            rvec = false; // % do not compute eigenvectors
        }// end

        if (isrealprob)
        {
            if (issymA)
            {
                // arpackc( eupdfun, rvec, 'A', select, ...
                // d, v, ldv, sigma, ...
                // bmat, int32(n), whch, nev, tol, resid, ncv, ...
                // v, ldv, iparam, ipntr, workd, workl, lworkl, info );
                if (true)
                {
                    throw new ConditionalException("Eigs : To be implemented.");
                }

                cond = "LM".equals(whch) || "LA".equals(whch);
                if (cond)
                {// strcmp(whch,'LM') || strcmp(whch,'LA')
                 // d = flipud(d);
                    if (rvec == true)
                    {
                        // v(:,1:k) = v(:,k:-1:1);
                    }// end
                }// end
                cond = ("SM".equals(whch) || "SA".equals(whch)) && (rvec == false);
                if (cond)
                {// ((strcmp(whch,'SM') || strcmp(whch,'SA')) && (rvec == 0))
                 // d = flipud(d);
                }// end
            }
            else
            {
                // % If sigma is complex, isrealprob=true and we use [c,z]neupd.
                // % So use sigmar=sigma and sigmai=0 here in dneupd.

                if (true)
                {
                    throw new ConditionalException("Eigs : To be implemented.");
                }
                // arpackc( eupdfun, rvec, 'A', select, ...
                // d, di, v, ldv, sigma, 0, workev, ...
                // bmat, int32(n), whch, nev, tol, resid, ncv, ...
                // v, ldv, iparam, ipntr, workd, workl, lworkl, info );
                // d = complex(d,di);
                if (rvec)
                {
                    // d(k+1) = [];
                }
                else
                {
                    FindInd find = null;// zind = find(d == 0);
                    if (find.isNull())
                    {// isempty(zind)
                     // d = d(k+1:-1:2);
                    }
                    else
                    {
                        // d(max(zind)) = [];
                        // d = flipud(d);
                    }// end
                }// end
            }// end
        }
        else
        {
            if (true)
            {
                throw new ConditionalException("Eigs : Found Complex Numbers.");
            }
            // zsigma = [real(sigma); imag(sigma)];
            // arpackc( eupdfun, rvec, 'A', select, ...
            // zd, zv, ldv, zsigma, workev, ...
            // bmat, int32(n), whch, nev, tol, resid, ncv, zv, ...
            // ldv, iparam, ipntr, zworkd, workl, lworkl, ...
            // rwork, info );
            if (issymA)
            {
                // d = zd(1:2:end-1);
            }
            else
            {
                // d = complex(zd(1:2:end-1),zd(2:2:end));
            }// end
             // v = reshape(complex(zv(1:2:end-1),zv(2:2:end)),[n p]);
        }// end

        boolean flag = processEUPDinfo(nargin < 3);

        if (issymA || (!isrealprob))
        {
            if (true)
            {
                throw new ConditionalException("Eigs : To be implemented.");
            }
            if (nargout <= 1)
            {
                if (isrealprob)
                {
                    varargout.add(d);// varargout{1} = d;
                }
                else
                {
                    // varargout{1} = d(k:-1:1,1);
                }// end
            }
            else
            {
                // varargout{1} = v(:,1:k);
                // varargout{2} = diag(d(1:k,1));
                if (nargout >= 3)
                {
                    // varargout{3} = flag;
                }// end
            }// end
        }
        else
        {
            if (nargout <= 1)
            {
                varargout.add(d);// varargout{1} = d;
            }
            else
            {

                if (true)
                {
                    throw new ConditionalException("Eigs : Found Complex Numbers.");
                }

                // cplxd = find(di ~= 0);

                // % complex conjugate pairs of eigenvalues occur together
                // cplxd = cplxd(1:2:end);
                // v(:,[cplxd cplxd+1]) = [complex(v(:,cplxd),v(:,cplxd+1)) ...
                // complex(v(:,cplxd),-v(:,cplxd+1))];
                // varargout{1} = v(:,1:k);
                // varargout{2} = diag(d);
                if (nargout >= 3)
                {
                    // varargout{3} = flag;
                }// end
            }// end
        }// end

        cond = B != null && ((B instanceof Matrix) && !((Matrix) B).isNull());
        if ((nargout >= 2) && (mode == 1) && cond)
        {// ~isempty(B)
         // varargout{1} = RBsolve(varargout{1});
        }// end

        valL = (int) System.currentTimeMillis() - t0;
        cputms.setElementAt(3, valL);// cputms(4) = cputime-t0; //% end timing
                                     // post-processing

        valL = JDatafun.sum(cputms.getEls(0, 3)).start();
        cputms.setElementAt(4, valL);// cputms(5) = sum(cputms(1:4)); //% total
                                     // time

        if (eigs_display == 2)
        {
            printTimings();
        }// end

    }// end constructor

    private void printTimings()
    {
        // % Print the time taken for each major stage of the EIGS algorithm
        boolean cond = false;
        if (mode == 1)
        {
            // innerstr = sprintf(['Compute A*X:' ...
            // ' %f\n'],cputms(3));
        }
        else if (mode == 3)
        {

            if (cond)
            {// isempty(B)
             // innerstr = sprintf(['Solve (A-SIGMA*I)*X=Y for X:' ...
             // ' %f\n'],cputms(3));
            }
            else
            {
                // innerstr = sprintf(['Solve (A-SIGMA*B)*X=B*Y for X:' ...
                // ' %f\n'],cputms(3));
            }// end
        }// end
        if ((mode == 3) && (Amatrix))
        {
            if (cond)
            {// isempty(B)
             // prepstr = sprintf(['Pre-processing, including lu(A-sigma*I):'
             // ...
             // ' %f\n'],cputms(1));
            }
            else
            {
                // prepstr = sprintf(['Pre-processing, including lu(A-sigma*B):'
                // ...
                // ' %f\n'],cputms(1));
            }// end
        }
        else
        {
            // prepstr = sprintf(['Pre-processing:' ...
            // ' %f\n'],cputms(1));
        }// end
         // sstr = sprintf('***********CPU Timing Results in
         // seconds***********');
         // ds = sprintf(['\n' sstr '\n' ...
         // prepstr ...
         // 'ARPACK''s %s: %f\n' ...
         // innerstr ...
         // 'Post-processing with ARPACK''s %s: %f\n' ...
         // '***************************************************\n' ...
         // 'Total: %f\n' ...
         // sstr '\n'], ...
         // aupdfun,cputms(2),eupdfun,cputms(4),cputms(5));
         // disp(ds)
    }// end % printTimings

    private boolean processEUPDinfo(boolean warnNonConvergence)
    {
        // % Process the info flag returned by the ARPACK routine **eupd
        boolean flag = false;
        if (info != 0)
        {
            String es = "Error with ARPACK routine " + eupdfun + ":\n";
            switch (info)
            {
            case 2:
                int ss = JDatafun.sum(select).start();
                if (ss < (Integer) k)
                {
                    // error('MATLAB:eigs:ARPACKroutineError02ssLTk', ...
                    // [es 'The logical variable select was only set' ...
                    // ' with %d 1''s instead of nconv=%d (k=%d).\n' ...
                    // 'Please report this to the ARPACK authors at' ...
                    // ' arpack@caam.rice.edu.'], ...
                    // ss,double(iparam(5)),k)
                }
                else
                {
                    // error('MATLAB:eigs:ARPACKroutineError02', ...
                    // [es 'The LAPACK reordering routine %strsen' ...
                    // ' did not return all %d eigenvalues.'], ...
                    // aupdfun(1),k);
                }// end
                 // break;
            case 1:
                // error('MATLAB:eigs:ARPACKroutineError01', ...
                // [es 'The Schur form could not be reordered by the' ...
                // ' LAPACK routine %strsen.\nPlease report this to the' ...
                // ' ARPACK authors at arpack@caam.rice.edu.'], ...
                // aupdfun(1))
            case -14:
                // error('MATLAB:eigs:ARPACKroutineErrorMinus14', ...
                // [es aupdfun ...
                // ' did not find any eigenvalues to sufficient accuracy.']);
            default:
            {// otherwise
             // error('MATLAB:eigs:ARPACKroutineError', ...
             // [es 'info = %d. Please consult the ARPACK Users''' ...
             // ' Guide for more information.'],full(info));
            }
            }// end
        }
        else
        {
            double nconv = iparam.getElementAt(4);
            if (nconv == 0)
            {
                if (warnNonConvergence)
                {
                    // warning('MATLAB:eigs:NoEigsConverged', ...
                    // 'None of the %d requested eigenvalues converged.',k)
                }
                else
                {
                    flag = true;
                }// end
            }
            else if (nconv < (Integer) k)
            {
                if (warnNonConvergence)
                {
                    // warning('MATLAB:eigs:NotAllEigsConverged', ...
                    // 'Only %d of the %d requested eigenvalues converged.', ...
                    // nconv,k)
                }
                else
                {
                    flag = true;
                }// end
            }// end
        }// end

        return flag;
    }// end % processEUPDinfo

    private void displayRitzValues()
    {
        // % Display a few Ritz values at the current iteration
        int iter = ipntr.getElementAt(14);// double(ipntr(15));
        boolean cond = (iter > eigs_iter) && (ido != 99);

        if (cond)
        {// (iter > eigs_iter) && (ido ~= 99)
            eigs_iter = iter;
            // ds = sprintf(['Iteration %d: a few Ritz values of the %d-by-%d
            // matrix:'],iter,p,p);
            // disp(ds)
            System.out.println("Iteration " + iter + ": a few Ritz values of the " + p + "-by-" + p + " matrix:");
            if (isrealprob)
            {
                Matrix dispvec = null;
                int[] arr = null;
                int from = 0;
                int to = 0;
                int end = 0;

                if (issymA)
                {
                    Indices ind = Indices.linspace(1, p).plus(ipntr.getElementAt(5));
                    arr = ind.getRowPackedCopy();
                    dispvec = ((Matrix) workl).getEls(arr);// dispvec =
                                                           // workl(double(ipntr(6))+(0:p-1));
                    end = dispvec.length();

                    if ("BE".equals(whch))
                    {// strcmp(whch,'BE')
                     // % roughly k Large eigenvalues and k Small eigenvalues
                        from = Math.max(end - 1 - 2 * (Integer) k + 1, 1 - 1);
                        to = end - 1;
                        dispvec.getEls(from, to).print(10, 4);// disp(dispvec(max(end-2*k+1,1):end))
                    }
                    else
                    {
                        // % k eigenvalues
                        from = Math.max(end - 1 - (Integer) k + 1, 1 - 1);
                        to = end - 1;
                        dispvec.getEls(from, to).print(10, 4);// disp(dispvec(max(end-k+1,1):end))
                    }// end
                }
                else
                {
                    // dispvec = complex(workl(double(ipntr(6))+(0:p-1)), ...
                    // workl(double(ipntr(7))+(0:p-1)));
                    // % k+1 eigenvalues (keep complex conjugate pairs together)
                    // disp(dispvec(max(end-k,1):end))
                    throw new ConditionalException(
                            "displayRitzValues : Lead to Complex Numbers as a result of non-symmetric.");
                }// end
            }
            else
            {
                // dispvec = complex(workl(2*double(ipntr(6))-1+(0:2:2*(p-1))),
                // ...
                // workl(2*double(ipntr(6))+(0:2:2*(p-1))));
                // disp(dispvec(max(end-k+1,1):end))
                throw new ConditionalException("displayRitzValues : Complex Numbers is not allowed.");
            }// end
        }// end
    }// end

    private Indices checkIpntr()
    {
        // % Check that ipntr returned from ARPACK refers to the start of a
        // % column of workd.

        Indices cols = null;

        boolean cond = (B != null) && (B instanceof Matrix) && ((Matrix) B).isNull();// ((Matrix)
                                                                                     // B).isNull()
                                                                                     // ;
        Indices inds = null;
        if (cond && (mode == 3) && (ido == 1))
        {// ~isempty(B) && (mode == 3) && (ido == 1)
            inds = ipntr.getEls(0, 2);// inds = double(ipntr(1:3));
        }
        else
        {
            inds = ipntr.getEls(0, 1);// //inds = double(ipntr(1:2));
        }// end
        int[] n3 =
        {
                (Integer) n, 3
        };
        Indices ID[] = JElmat.ind2sub(n3, inds);// [rows,cols] =
                                                // ind2sub([n,3],inds);
        cols = ID[1];

        inds = ID[0].NEQ(0); // nonOneRows = find(rows~=1);
        cond = !inds.findIJ().isNull();
        if (cond)
        {// ~isempty(nonOneRows)
         // error('MATLAB:eigs:ipntrMismatchWorkdColumn', ...
         // ['One of ipntr(1:3) does not refer to the start' ...
         // ' of a column of the %d-by-3 array workd.'],n)
            String msg = "checkIpntr : One of ipntr(1:3) does not refer to the start";
            msg = " of a column of the [" + n + "-by-3] array \"workd\".";
            throw new ConditionalException(msg);
        }// end

        return cols;
    }// end % checkIpntr

    private boolean isMatrix(Object obj)
    {
        boolean cond = obj instanceof Matrix;
        return cond;
    }

    private boolean isIndices(Object obj)
    {
        boolean cond = obj instanceof Indices;
        return cond;
    }

    private Object[] LUfactorAminusSigmaB()
    {// [L,U,P,perm]

        if (true)
        {
            throw new ConditionalException("LUfactorAminusSigmaB : To be fully implemented.");
        }

        // % LU factor A-sigma*B, including a reordering perm if it is sparse
        boolean cond = B == null || ((B instanceof Matrix) && ((Matrix) B).isNull());
        Object AsB = null;
        Matrix U = null;
        if (cond)
        {// isempty(B)
            if (issparse(A))
            {
                // AsB = A - sigma * speye(n);
            }
            else
            {
                // AsB = A - sigma * eye(n);
            }// end
        }
        else
        {
            if (cholB)
            {
                if (issparse(B))
                {
                    // AsB = A - sigma * Bmtimes(speye(n));
                }
                else
                {
                    // AsB = A - sigma * Bmtimes(eye(n));
                }// end
            }
            else
            {
                // AsB = A - sigma * B;
            }// end
        }// end
        if (issparse(AsB))
        {
            // [L,U,P,Q] = lu(AsB);
            // [perm,ignore] = find(Q);
        }
        else
        {
            // [L,U,P] = lu(AsB);
            // perm = [];
        }// end

        // % Warn if lu(A-sigma*B) is ill-conditioned
        // % => sigma is close to an exact eigenvalue of (A,B)
        Matrix dU = U.diag();// diag(U);
        Matrix absDU = dU.abs();
        double minAbs = JDatafun.min(absDU).start();
        double maxAbs = JDatafun.max(absDU).start();

        double rcondestU = minAbs / maxAbs;// full(min(abs(dU)) / max(abs(dU)));
        if (rcondestU < MathUtil.EPS)
        {
            String ds = "";
            if (cond)
            {// isempty(B)
                ds = "(A-sigma*I)";
            }
            else
            {
                ds = "(A-sigma*B)";
            }// end
             // warning('MATLAB:eigs:SigmaNearExactEig',...
             // [ds ' has small reciprocal condition' ...
             // ' estimate: %f\n' ...
             // ' indicating that sigma is near an exact' ...
             // ' eigenvalue.\n The algorithm may not converge unless' ...
             // ' you try a new value for sigma.\n'], ...
             // rcondestU);
            System.out.println(ds + " has small reciprocal condition estimate: " + rcondestU);
            System.out.println("         indicating that sigma is near an exact eigenvalue.");
            System.out.println("         The algorithm may not converge unless you try a new value for sigma.");
        }// end

        return null;

    }// end % LUfactorAminusSigmaB

    private Object[] CHOLfactorB()
    {// [RB,RBT,perm]
     // % permB may be [] (from checkInputs) if the problem is not sparse
     // % or if it was not passed in as opts.permB

        if (true)
        {
            throw new ConditionalException("CHOLfactorB : To be fully implemented.");
        }

        Object perm = permB;
        boolean cond = false;
        int pB = 0;
        if (cholB)
        {
            // % CHOL(B) was passed in as B
            // RB = B;
            // RBT = B';
        }
        else
        {
            cond = B != null && ((B instanceof Matrix) && !((Matrix) B).isNull());
            // % CHOL(B) was not passed into EIGS
            if ((mode == 1) && cond)
            {// ~isempty(B)
             // % Algorithm requires CHOL(B) to be computed
                if (issparse(B))
                {
                    // perm = symamd(B);
                    // [RB,pB] = chol(B(perm,perm));
                }
                else
                {
                    // [RB,pB] = chol(B);
                }// end
                if (pB == 0)
                {
                    // RBT = RB';
                }
                else
                {
                    // error('MATLAB:eigs:BNotSPD', ...
                    // 'B is not symmetric positive definite.')
                }// end
            }// end
        }// end

        return (Object[]) null;
    }// end % CHOLfactorB

    private static boolean issparse(Object obj)
    {
        boolean cond = obj instanceof SparseOld;
        return cond;
    }

    /*
     * % checkInputs error checks the inputs to EIGS and also derives some
     * variables from them: A may be a matrix or a function applying OP. Amatrix
     * is true if A is a matrix, false if A is a function. isrealprob is true if
     * all of A, B and sigma are real, false otherwise. issymA is true if A is
     * symmetric, false otherwise. n is the size of (square) A and B. B is []
     * for the standard problem. Otherwise it may be one of B, CHOL(B) or
     * CHOL(B(permB,permB)). classAB is single if either A or B is single,
     * otherwise double. k is the number of eigenvalues to be computed.
     * eigs_sigma is the value for sigma passed in by the user, 'LM' if it was
     * unspecified. eigs_sigma may be either a string or a scalar value. whch is
     * the ARPACK string corresponding to eigs_sigma and mode. sigma is the
     * ARPACK scalar corresponding to eigs_sigma and mode. tol is the
     * convergence tolerance. maxit is the maximum number of iterations. p is
     * the number of Lanczos vectors. info is the start value, initialized to 1
     * or 0 to indicate whether to use resid as the start vector or not.
     * eigs_display is true if Ritz values should be displayed, false otherwise.
     * cholB is true if CHOL(B) was passed in instead of B, false otherwise.
     * permB may be [], otherwise it is the permutation in CHOL(B(permB,permB)).
     * resid is the start vector if specified and info=1, otherwise all zero.
     * useeig is true if we need to use EIG instead of ARPACK, otherwise false.
     * afunNargs is the range of EIGS' varargin that are to be passed as
     * trailing parameters to the function as in afun(X,P1,P2,...).
     */
    private Object[] checkInputs(Object[] varargin)
    {
        if (true)
        {
            throw new ConditionalException("checkInputs : To be implemented.");
        }

        boolean cond = false;

        // % Process inputs and do error-checking

        // % Process the input A or the inputs AFUN and N
        // % Start to derive some qualities (real, symmetric) about the problem
        if (true)
        {// isfloat(varargin{1})
            A = (Matrix) varargin[0];
            Amatrix = true;
        }
        else
        {
            // % By checking the function A with fcnchk, we can now use direct
            // % function evaluation on the result, without resorting to feval

            // A = fcnchk(varargin{1});
            Amatrix = false;
            throw new ConditionalException("checkInputs : Must not reach here.");
        }// end

        // % isrealprob = isreal(A) && isreal(B) && isreal(sigma)
        isrealprob = true;
        issymA = false;
        if (Amatrix)
        {
            isrealprob = true;// isreal(A);
            issymA = ishermitian((Matrix) A);
            int m = ((Matrix) A).getRowDimension();// [m,n] = size(A);
            n = ((Matrix) A).getColumnDimension();
            if (new Integer(m) != (Integer) n)
            {
                // error('MATLAB:eigs:NonSquareMatrixOrFunction',...
                // 'A must be a square matrix or a function.')
            }// end
        }
        else
        {
            n = varargin[1];// {2};
            String nstr = "Size of problem, \"n\", must be a positive integer.";
            if (false)
            {// ~isscalar(n) || ~isreal(n)
             // error('MATLAB:eigs:NonPosIntSize', nstr)
            }// end
            if (issparse(n))
            {
                // n = full(n);
                throw new ConditionalException("checkInputs : To Do.");
            }// end

            cond = n instanceof Matrix || n instanceof Double;
            if (cond)
            {// (round(n) ~= n)
             // warning('MATLAB:eigs:NonPosIntSize',['%s\n ' ...
             // 'Rounding input size.'],nstr)
                System.out.println("checkInputs : Rounding input size");

                if (n instanceof Matrix)
                {// n = round(n);
                    n = ((Matrix) n).round().toIndices();
                }
                else
                {
                    n = (Integer) n;
                }
            }// end
        }// end

        // % Process the input B and derive the class of the problem.
        // % Is B present in the eigs call or not?
        boolean Bpresent = true;
        String Bstr = "Generalized matrix B must be the same size as A and"
                + " either a symmetric positive (semi-)definite matrix or" + " its Cholesky factor.";

        int Amat = Amatrix == true ? 1 : 0;

        if (nargin < (3 - Amat))
        {
            B = null;
            Bpresent = false;
        }
        else
        {
            // % Is the next input B or K?
            B = varargin[3 - Amat - 1];
            cond = B != null && ((B instanceof Matrix) && (!((Matrix) B).isNull()));
            if (cond)
            {// ~isempty(B) //% allow eigs(A,[],k,sigma,opts);
                cond = ((Matrix) B).isScalar();
                if (cond)
                {// isscalar(B)
                    cond = (B instanceof Indices) && ((Indices) n).length() == 1 && ((Indices) n).start() != 1;
                    if (cond)
                    {// n ~= 1
                     // % this input is really K and B is not specified
                        B = null;
                        Bpresent = false;
                    }
                    else
                    {
                        // % This input could be B or K.
                        // % If A is scalar, then the only valid value for k is
                        // 1.
                        // % So if this input is scalar, let it be B, namely
                        // % eigs(4,2,...) assumes A=4, B=2, NOT A=4, k=2
                        if (false)
                        {// ~isnumeric(B)
                         // error('MATLAB:eigs:BsizeMismatchAorNotSPDorNotChol',
                         // Bstr);
                            throw new ConditionalException("checkInputs :  " + Bstr);
                        }// end
                         // % Unless, of course, the scalar is 1, in which case
                         // % assume the that it is meant to be K.
                        if (cond)
                        {// (B == 1) && ((Amatrix && nargin <= 3) || (~Amatrix
                         // && nargin <= 4))
                            B = null;
                            Bpresent = false;
                        }
                        else if (false)
                        {// ~isfloat(B)
                         // error('MATLAB:eigs:BsizeMismatchAorNotSPDorNotChol',
                         // Bstr);
                            throw new ConditionalException("checkInputs :  " + Bstr);
                        }// end
                    }// end
                }
                else
                {
                    // % B is a not a scalar.
                    Indices sizNN = new Indices(new int[]
                    {
                            (Integer) n, (Integer) n
                    });
                    if (B instanceof Indices)
                    {
                        cond = !((Indices) B).size().EQ(sizNN).allBoolean();
                    }
                    else if (B instanceof Matrix)
                    {
                        cond = !((Matrix) B).sizeIndices().EQ(sizNN).allBoolean();
                    }
                    else
                    {
                    }
                    if (cond)
                    {// ~isfloat(B) || ~isequal(size(B),[n,n])
                     // error('MATLAB:eigs:BsizeMismatchAorNotSPDorNotChol',
                     // Bstr);
                        throw new ConditionalException("checkInputs :  " + Bstr);
                    }// end
                     // isrealprob = isrealprob && isreal(B);
                }// end
            }// end
        }// end

        // % ARPACK can only handle homogeneous inputs
        if (Amatrix)
        {
            // classAB = superiorfloat(A,B);
            // A = cast(A,classAB);
            // B = cast(B,classAB);
            if (A instanceof Indices)
            {
                A = Matrix.indicesToMatrix((Indices) A);
            }
            if (B instanceof Indices)
            {
                B = Matrix.indicesToMatrix((Indices) B);
            }
        }
        else
        {
            cond = B != null && ((B instanceof Matrix) && (!((Matrix) B).isNull()));
            if (cond)
            {// ~isempty(B)
             // classAB = class(B);
            }
            else
            {
                // classAB = 'double';
            }// end
        }// end

        // % argOffset tells us where to get the eigs inputs K, SIGMA and OPTS.
        // % If A is really the function afun, then it also helps us find the
        // % trailing parameters in eigs(afun,n,[B],k,sigma,opts,P1,P2,...)
        // % Values of argOffset:
        // % 0: Amatrix is false and Bpresent is true:
        // % eigs(afun,n,B,k,sigma,opts,P1,P2,...)
        // % 1: Amatrix and Bpresent are both true, or both false
        // % eigs(A,B,k,sigma,opts)
        // % eigs(afun,n,k,sigma,opts,P1,P2,...)
        // % 2: Amatrix is true and Bpresent is false:
        // % eigs(A,k,sigma,opts)
        int argOffset = (Amatrix == true ? 1 : 0) + (!Bpresent ? 1 : 0);

        if (Amatrix && (nargin - (Bpresent ? 1 : 0)) > 4)
        {
            // error('MATLAB:eigs:TooManyInputs', 'Too many inputs.')
            throw new ConditionalException("checkInputs :  Too many inputs.");
        }// end

        // % Process the input K.
        if (nargin < (4 - argOffset))
        {
            // k = min(n,6);
            throw new ConditionalException("checkInputs :  To Do.");
        }
        else
        {
            k = varargin[4 - argOffset - 1];
            String kstr = "Number of eigenvalues requested, k, must be a" + " positive integer <= n.";
            cond = true;
            if (cond)
            {// ~isnumeric(k) || ~isscalar(k) || ~isreal(k) || (k>n)
             // error('MATLAB:eigs:NonIntegerEigQty', kstr)
                throw new ConditionalException("checkInputs :  " + kstr);
            }// end
            if (issparse(k))
            {
                // k = full(k);
                throw new ConditionalException("checkInputs :  To Do.");
            }// end
            cond = true;
            if (cond)
            {// (round(k) ~= k)
                throw new ConditionalException("checkInputs :  To Do.");
                // warning('MATLAB:eigs:NonIntegerEigQty',['%s\n ' ...
                // 'Rounding number of eigenvalues.'],kstr)
                // k = round(k);
            }// end
        }// end

        return null;
    }

    private boolean ishermitian(Matrix A)
    {
        // %ISHERMITIAN
        Matrix At = A.transpose();
        boolean tf = A.equalAll(At).trueAll();// isequal(A,A');
        return tf;
    }

    private void fullEig(int nOutputs)
    {

        if (true)
        {
            throw new ConditionalException("fullEig : To be fully implemented.");
        }

        // % Use EIG(FULL(A)) or EIG(FULL(A),FULL(B)) instead of ARPACK
        boolean cond = B != null && ((B instanceof Matrix) && !((Matrix) B).isNull());
        if (cond)
        {// ~isempty(B)
         // B = Bmtimes(eye(n));
        }// end

        cond = A != null && ((A instanceof Matrix) && !((Matrix) A).isNull());
        if (cond)
        {// isfloat(A)
            cond = (A instanceof SparseOld);
            if (cond)
            {// issparse(A);
             // A = full(A);
            }// end
        }
        else
        {
            // % A is specified by a function.
            // % Form the matrix A by applying the function.
            if (cond)
            {// ischar(eigs_sigma) && ~strcmp(eigs_sigma,'SM')
             // % A is a function multiplying A*x
             // AA = eye(n);
                for (int i = 0; i < (Integer) n; i++)
                {// 1:n
                 // AA(:,i) = A(AA(:,i),varargin{afunNargs});
                }// end
                 // A = AA;
            }
            else
            {
                cond = true;
                if (cond)
                {// (isfloat(eigs_sigma) && eigs_sigma == 0) ||
                 // strcmp(eigs_sigma,'SM')
                 // % A is a function solving A\x
                 // invA = eye(n);
                    for (int i = 0; i < (Integer) n; i++)
                    {// 1:n
                     // invA(:,i) = A(invA(:,i),varargin{afunNargs});
                    }// end
                     // A = eye(n) / invA;
                }
                else
                {
                    // % A is a function solving (A-sigma*B)\x
                    // % B may be [], indicating the identity matrix
                    // % U = (A-sigma*B)\sigma*B
                    // % => (A-sigma*B)*U = sigma*B
                    // % => A*U = sigma*B(U + eye(n))
                    // % => A = sigma*B(U + eye(n)) / U
                    cond = B == null || ((B instanceof Matrix) && ((Matrix) B).isNull());
                    if (cond)
                    {// isempty(B)
                     // sB = eigs_sigma*eye(n);
                    }
                    else
                    {
                        // sB = eigs_sigma*B;
                    }// end
                     // U = zeros(n,n);
                    for (int i = 0; i < (Integer) n; i++)
                    {// 1:n
                     // U(:,i) = A(sB(:,i),varargin{afunNargs});
                    }// end
                     // A = sB*(U+eye(n)) / U;
                }// end
            }// end
        }// end

        cond = B == null || ((B instanceof Matrix) && ((Matrix) B).isNull());
        if (cond)
        {// isempty(B)
         // eigInputs = {A};
        }
        else
        {
            // eigInputs = {A,B};
        }// end

        // % Now with full floating point matrices A and B, use EIG:
        if (nOutputs <= 1)
        {
            // d = eig(eigInputs{:});
        }
        else
        {
            // [V,D] = eig(eigInputs{:});
            // d = diag(D);
        }// end

        // % Grab the eigenvalues we want, based on sigma
        // firstKindices = 1:k;
        // lastKindices = n:-1:n-k+1;

        if (eigs_sigma instanceof String)
        {
            String upperC = ((String) eigs_sigma).toUpperCase();
            if (upperC.length() != 2)
            {
                throw new ConditionalException("fullEig : String parameter (= \"" + upperC
                        + "\") must be 2 characters.");
            }
            // switch eigs_sigma
            if ("LM".equals(upperC))
            {
                // [ignore,ind] = sort(abs(d));
                // range = lastKindices;
            }
            else if ("SM".equals(upperC))
            {
                // [ignore,ind] = sort(abs(d));
                // range = firstKindices;
            }
            else if ("LA".equals(upperC))
            {
                // [ignore,ind] = sort(d);
                // range = lastKindices;
            }
            else if ("SA".equals(upperC))
            {
                // [ignore,ind] = sort(d);
                // range = firstKindices;
            }
            else if ("LR".equals(upperC))
            {
                // [ignore,ind] = sort(abs(real(d)));
                // range = lastKindices;
            }
            else if ("SR".equals(upperC))
            {
                // [ignore,ind] = sort(abs(real(d)));
                // range = firstKindices;
            }
            else if ("LI".equals(upperC))
            {
                // [ignore,ind] = sort(abs(imag(d)));
                // range = lastKindices;
            }
            else if ("SI".equals(upperC))
            {
                // [ignore,ind] = sort(abs(imag(d)));
                // range = firstKindices;
            }
            else if ("BE".equals(upperC))
            {
                // [ignore,ind] = sort(abs(d));
                // range = [1:floor(k/2), n-ceil(k/2)+1:n];
            }
            else
            {
                // error('MATLAB:eigs:fullEigSigma','Unknown value of sigma');
                throw new ConditionalException("fullEig : Unknown value of sigma (= \"" + upperC
                        + "\"). Not supported.");
            }

        }
        else
        {
            // % sigma is a scalar
            // [ignore,ind] = sort(abs(d-eigs_sigma));
            // range = 1:k;
        }// end

        if (nOutputs <= 1)
        {
            // varargout{1} = d(ind(range)); //===================>>>>>>>
        }
        else
        {
            // varargout{1} = V(:,ind(range)); //===================>>>>>>>
            // varargout{2} = D(ind(range),ind(range));
            // //===================>>>>>>>
            if (nOutputs == 3)
            {
                // % flag indicates "convergence"
                // varargout{3} = 0; //===================>>>>>>>
            }// end
        }// end

    }// end % FULLEIG
}
