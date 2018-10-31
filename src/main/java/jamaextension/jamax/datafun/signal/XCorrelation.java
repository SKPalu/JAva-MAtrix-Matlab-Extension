/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun.signal;

import jamaextension.jamax.Complex;
import jamaextension.jamax.ComplexMatrix;
import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.Matrix3D;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elfun.JElfun;

/**
 * 
 * @author Sione
 */
public class XCorrelation
{

    public XCorrelation(Object x, Object... varargin)
    {

        // error(nargchk(1,4,nargin,'struct'));
        if (x == null)
        {
            throw new ConditionalException("XCorrelation : Object parameter \"x\"  must be non-null.");
        }
        if (!(x instanceof Matrix) && !(x instanceof Matrix3D))
        {
            throw new ConditionalException(
                    "XCorrelation : Object parameter \"x\"  must be an instanceof either a \"Matrix\" or \"Matrix3D\".");
        }

        if (!(x instanceof Matrix))
        {
            throw new ConditionalException(
                    "XCorrelation : At this stage parameter \"x\"  must be an instanceof either a \"Matrix\". Future to do list will include \"Matrix3D\".");
        }

        Matrix X = (Matrix) x;
        // [x,nshift] = shiftdim(x);
        if (X.isVector())
        {
            if (X.isRowVector())
            {
                X = X.toColVector();
            }
        }

        // [xIsMatrix,autoFlag,maxlag,scaleType,msg] =
        // parseinput(x,varargin{:});
        Object[] parseOut = parseinput(x, varargin);
        boolean xIsMatrix = ((Boolean) parseOut[0]).booleanValue();
        boolean autoFlag = ((Boolean) parseOut[1]).booleanValue();
        int maxlag = ((Integer) parseOut[2]).intValue();
        String scaleType = (String) parseOut[3];
        String msg = (String) parseOut[4];

        if (msg != null || !"".equals(msg))
        {// ~isempty(msg),
         // error(generatemsgid('SigErr'),msg);
            throw new ConditionalException("XCorrelation : " + msg + ".");
        }// end
        Object[] matC = null;
        Object c = null;
        int M = 0;
        int N = 0;
        if (xIsMatrix)
        {
            // [c,M,N] = matrixCorr(x);
            matC = matrixCorr(x);
        }
        else
        {
            // [c,M,N] = vectorXcorr(x,autoFlag,varargin{:});
            matC = vectorXcorr(x, autoFlag, varargin);
        }// end

        c = matC[0];
        M = ((Integer) matC[1]).intValue();
        N = ((Integer) matC[2]).intValue();

        /*----------------------------------------------------------------------*
         * The following method call is not needed since the data is real, ie,
         * Matrix and Matrix3D
         *----------------------------------------------------------------------*/
        // Force correlation to be real when inputs are real
        // c = forceRealCorr(c,x,autoFlag,varargin{:});

        Indices lags = Indices.linspace(-maxlag, maxlag);

        // Keep only the lags we want and move negative lags before positive
        // lags
        if (maxlag >= M)
        {
            // c =
            // [zeros(maxlag-M+1,N^2);c(end-M+2:end,:);c(1:M,:);zeros(maxlag-M+1,N^2)];
        }
        else
        {
            // c = [c(end-maxlag+1:end,:);c(1:maxlag+1,:)];
        }// end

        // Scale as specified
        matC = scaleXcorr(c, xIsMatrix, scaleType, autoFlag, M, maxlag, lags, x, varargin);
        c = matC[0];
        msg = (String) matC[1];
        if (msg != null || !"".equals(msg))
        {
            // error(generatemsgid('SigErr'),msg);
            throw new ConditionalException("XCorrelation : " + msg + ".");
        }// end

        // If first vector is a row, return a row

        // c = shiftdim(c,-nshift);

    }

    private Object[] parseinput(Object x, Object... varargin)
    {
        // [xIsMatrix,autoFlag,maxlag,scaleType,msg]

        // Set some defaults
        String scaleType = "";
        String msg = "";
        boolean autoFlag = true; // Assume autocorrelation until proven
                                 // otherwise
        boolean xIsMatrix = false;
        Object maxlag = null;// [];
        Object y = null;
        Object[] rObj = null;

        String errMsg = "Input argument is not recognized.";
        int nargin = 1;
        if (varargin != null && varargin.length != 0)
        {
            nargin += varargin.length;
        }

        switch (nargin)
        {
        case 2:
        {
            // Can be (x,y), (x,maxlag), or (x,scaleType)
            if (varargin[0] instanceof String)
            {
                // Second arg is scaleType
                scaleType = (String) varargin[0];

            }
            else if (varargin[0] instanceof Matrix || varargin[0] instanceof Matrix3D)
            {
                // Can be y or maxlag
                int var1Len = 0;
                if (varargin[0] instanceof Matrix)
                {
                    var1Len = ((Matrix) varargin[0]).length();
                }
                else if (varargin[0] instanceof Matrix3D)
                {
                    var1Len = ((Matrix3D) varargin[0]).length();
                }

                if (var1Len == 1)
                {// length(varargin{1}) == 1,
                    maxlag = varargin[0];
                }
                else
                {
                    autoFlag = false;
                    y = varargin[0];
                }// end
            }
            else
            {
                // Not recognized
                msg = errMsg + " (case 2:)";
                rObj = new Object[]
                {
                        xIsMatrix, autoFlag, maxlag, scaleType, msg
                };
                return rObj;
            }// end
            break;
        }
        case 3:
        {
            // Can be (x,y,maxlag), (x,maxlag,scaleType) or (x,y,scaleType)
            boolean maxlagflag = false; // By default, assume 3rd arg is not
                                        // maxlag
            if (varargin[1] instanceof String)
            {
                // Must be scaletype
                scaleType = (String) varargin[1];

            }
            else if (varargin[1] instanceof Matrix || varargin[1] instanceof Matrix3D)
            {
                // Must be maxlag
                maxlagflag = true;
                maxlag = varargin[1];

            }
            else
            {
                // Not recognized
                msg = errMsg;
                rObj = new Object[]
                {
                        xIsMatrix, autoFlag, maxlag, scaleType, msg
                };
                return rObj;
            }// end

            if (varargin[1] instanceof Matrix || varargin[1] instanceof Matrix3D)
            {
                if (maxlagflag)
                {
                    autoFlag = false;
                    y = varargin[0];
                }
                else
                {
                    // Can be y or maxlag
                    int var1Len = 0;
                    if (varargin[0] instanceof Matrix)
                    {
                        var1Len = ((Matrix) varargin[0]).length();
                    }
                    else if (varargin[0] instanceof Matrix3D)
                    {
                        var1Len = ((Matrix3D) varargin[0]).length();
                    }
                    if (var1Len == 1)
                    {// length(varargin{1}) == 1,
                        maxlag = varargin[0];
                    }
                    else
                    {
                        autoFlag = false;
                        y = varargin[0];
                    }// end
                }// end
            }
            else
            {
                // Not recognized
                msg = errMsg + " (case 3:)";
                rObj = new Object[]
                {
                        xIsMatrix, autoFlag, maxlag, scaleType, msg
                };
                return rObj;
            }// end
            break;
        }
        case 4:
        {
            // Must be (x,y,maxlag,scaleType)
            autoFlag = false;
            y = varargin[0];

            maxlag = varargin[1];

            scaleType = (String) varargin[2];
            break;
        }
        }// end switch

        // Determine if x is a matrix or a vector
        // [xIsMatrix,m] = parse_x(x);
        rObj = parse_x(x);
        xIsMatrix = ((Boolean) rObj[0]).booleanValue();
        int m = ((Integer) rObj[1]).intValue();

        if (!autoFlag)
        {
            // Test y for correctness
            // [maxlag,msg] = parse_y(y,m,xIsMatrix,maxlag);
            rObj = parse_y(y, m, xIsMatrix, maxlag);
            maxlag = rObj[0];
            msg = (String) rObj[1];
            if (msg != null || !"".equals(msg))
            {
                rObj = new Object[]
                {
                        xIsMatrix, autoFlag, maxlag, scaleType, msg
                };
                return rObj;
            }// end
        }// end

        // [maxlag,msg] = parse_maxlag(maxlag,m);
        rObj = parse_maxlag(maxlag, m);
        maxlag = rObj[0];
        msg = (String) rObj[1];
        if (msg != null || !"".equals(msg))
        {
            rObj = new Object[]
            {
                    xIsMatrix, autoFlag, maxlag, scaleType, msg
            };
            return rObj;
        }// end

        // Test the scaleType validity
        // [scaleType,msg] =
        // parse_scaleType(scaleType,errMsg,autoFlag,m,varargin{:});
        rObj = parse_scaleType(scaleType, errMsg, autoFlag, m, varargin);
        scaleType = (String) rObj[0];
        msg = (String) rObj[1];

        if (msg != null || !"".equals(msg))
        {
            rObj = new Object[]
            {
                    xIsMatrix, autoFlag, maxlag, scaleType, msg
            };
            return rObj;
        }// end

        rObj = new Object[]
        {
                xIsMatrix, autoFlag, maxlag, scaleType, msg
        };
        return rObj;
    }

    private Object[] matrixCorr(Object xObj)
    {
        // [c,M,N] =
        if (xObj == null)
        {
            throw new ConditionalException("matrixCorr : Object parameter \"xObj\"  must be non-null.");
        }
        if (!(xObj instanceof Matrix) && !(xObj instanceof Matrix3D))
        {
            throw new ConditionalException(
                    "matrixCorr : Object parameter \"xObj\"  must be an instanceof either a \"Matrix\" or \"Matrix3D\".");
        }

        // Compute all possible auto- and cross-correlations for a matrix input
        Matrix x = null;
        if (xObj instanceof Matrix)
        {
            x = (Matrix) xObj;
        }
        else
        {
            x = ((Matrix3D) xObj).getPageAt(0);
        }

        // [M,N] = size(xObj);
        int M = x.getRowDimension();
        int N = x.getColumnDimension();

        int pow = (int) Math.pow(2.0, JElfun.nextpow2(2.0 * (double) M - 1.0));
        ComplexMatrix X = SigProc.fft(new ComplexMatrix(x), pow);

        ComplexMatrix Xc = X.conjugate();// conj(X);

        int MX = X.getRowDimension();
        int NX = X.getColumnDimension();

        // [MX,NX] = size(X);
        ComplexMatrix C = new ComplexMatrix(MX, NX * NX);
        for (int n = 0; n < N; n++)
        {
            // C(:,(((n-1)*N)+1):(n*N)) = repmat(X(:,n),1,N).*Xc;
            ComplexMatrix cmRepmat = X.getColumnAt(n).repmat(1, N);
            cmRepmat = cmRepmat.arrayTimes(Xc);
            // Subtract 1 from the following variables since Java idx is one
            // less than matlab.
            int stt = n * N;
            int end = (n + 1) * N - 1;
            int[] arr = Indices.linspace(stt, end).getRowPackedCopy();
            C.setColumns(arr, cmRepmat);
        }// end

        ComplexMatrix cIfft = SigProc.ifft(C);
        Matrix c = cIfft.re();

        return new Object[]
        {
                c, new Integer(M), new Integer(N)
        };
    }

    private Object[] vectorXcorr(Object xx, boolean autoFlag, Object... varargin)
    {
        // [c,M,N]
        if (xx == null)
        {
            throw new ConditionalException("vectorXcorr : Parameter \"xx\" must be non-null.");
        }
        if (!(xx instanceof Matrix))
        {
            throw new ConditionalException("vectorXcorr : Parameter \"xx\" must be an instance of \"Matrix\".");
        }
        // x = x(:);
        Matrix x = (Matrix) xx;
        if (x.isRowVector())
        {
            x = x.toColVector();
        }

        // [M,N] = size(x);
        int M = x.getRowDimension();
        int N = x.getColumnDimension();
        ComplexMatrix X = null;
        ComplexMatrix Y = null;
        ComplexMatrix c = null;
        Matrix y = null;

        if (autoFlag)
        {
            // Autocorrelation
            // Compute correlation via FFT
            int pow = (int) Math.pow(2.0, JElfun.nextpow2(2.0 * M - 1.0));
            X = SigProc.fft(x, pow);
            // c = ifft(abs(X).^2);
            Matrix absx2 = JElfun.pow(X.abs(), 2.0);
            c = SigProc.ifft(new ComplexMatrix(absx2));
        }
        else
        {
            // xcorrelation
            y = (Matrix) varargin[0];
            // y = y(:);
            if (y.isRowVector())
            {
                y = y.toColVector();
            }
            // L = length(y);
            int L = y.length();
            // Cache the length(x)
            int Mcached = M;

            // Recompute length(x) in case length(y) > length(x)
            M = Math.max(Mcached, L);

            Indices LM = new Indices(new int[]
            {
                    L / Mcached, Mcached / L
            });

            if (L != Mcached && LM.GT(10).anyBoolean())
            {// (L ~= Mcached) && any([L./Mcached, Mcached./L] > 10),
             // Vector sizes differ by a factor greater than 10,
             // fftfilt is faster
             // neg_c = conj(fftfilt(conj(x),flipud(y))); // negative lags
                FftFilter FF = new FftFilter(new ComplexMatrix(x).conjugate(), y.flipUD());
                Matrix neg_c = null;
                if (!FF.isReal())
                {
                    throw new ConditionalException("vectorXcorr : Signal \"neg_c\" is a complex array.");
                }
                else
                {
                    neg_c = (Matrix) FF.getFilteredSig();
                }

                // pos_c = flipud(fftfilt(conj(y),flipud(x))); // positive lags
                FF = new FftFilter(new ComplexMatrix(y).conjugate(), x.flipUD());
                Matrix pos_c = null;
                if (!FF.isReal())
                {
                    throw new ConditionalException("vectorXcorr : Signal \"pos_c\" is a complex array.");
                }
                else
                {
                    pos_c = (Matrix) FF.getFilteredSig();
                    pos_c = pos_c.flipUD();
                }

                // Make them of almost equal length (remove zero-th lag from
                // neg)
                int lneg = neg_c.length();
                int lpos = pos_c.length();
                int[] arrInd = Indices.linspace(0, lneg - 2).getRowPackedCopy();
                // neg_c = [zeros(lpos-lneg,1);neg_c(1:end-1)];
                neg_c = Matrix.zeros(lpos - lneg, 1).mergeV(neg_c.getElements(arrInd));
                // pos_c = [pos_c;zeros(lneg-lpos,1)];
                pos_c = pos_c.mergeV(Matrix.zeros(lneg - lpos, 1));
                // c = [pos_c;neg_c];
                Matrix tmp = pos_c.mergeV(neg_c);
                c = new ComplexMatrix(tmp);
            }
            else
            {
                if (L != Mcached)
                {// L ~= Mcached,
                 // Force equal lengths
                    if (L > Mcached)
                    {// L > Mcached
                     // x = [x;zeros(L-Mcached,1)];
                        x = x.mergeV(Matrix.zeros(L - Mcached, 1));
                    }
                    else
                    {
                        // y = [y;zeros(Mcached-L,1)];
                        y = y.mergeV(Matrix.zeros(Mcached - L, 1));
                    }// end
                }// end

                // Transform both vectors
                double nxt = JElfun.nextpow2((double) (2 * M - 1));
                int pow = (int) Math.pow(2.0, nxt);
                // X = fft(x,2^nextpow2(2*M-1));
                X = SigProc.fft(x, pow);
                // Y = fft(y,2^nextpow2(2*M-1));
                Y = SigProc.fft(y, pow);

                // Compute cross-correlation
                c = SigProc.ifft(X.arrayTimes(Y.conjugate()));
            }// end
        }// end
        return new Object[]
        {
                c.re(), M, N
        };
    }

    private Object[] scaleXcorr(Object c, boolean xIsMatrix, String scaleType, boolean autoFlag, int M, int maxlag,
            Indices lags, Object x, Object... varargin)
    {
        // Scale correlation as specified

        String msg = "";

        // switch scaleType,
        if ("none".equals(scaleType))
        {
            return null;
        }
        else if ("biased".equals(scaleType))
        {
            // Scales the raw cross-correlation by 1/M.
            // c = c./M;
            if (c instanceof Matrix)
            {
                c = ((Matrix) c).arrayRightDivide((double) M);
            }
            else if (c instanceof ComplexMatrix)
            {
                c = ((ComplexMatrix) c).arrayDivide((double) M);
            }
            else
            {
                throw new ConditionalException(
                        "scaleXcorr : Parameter \"c\" must be an instance of \"Matrix\" or \"ComplexMatrix\".");
            }
        }
        else if ("unbiased".equals(scaleType))
        {
            // Scales the raw correlation by 1/(M-abs(lags)).
            Indices scale = lags.abs().uminus().plus(M).transpose();// (M-abs(lags)).';

            // scale(scale<=0)=1; // avoid divide by zero, when correlation is
            // zero
            Indices find = scale.LTEQ(0).find();
            if (find != null)
            {
                scale.setFromFind(find, 1);
            }

            if (xIsMatrix)
            {
                // scale = repmat(scale,1,size(c,2));
                int c2len = 0;
                if (c instanceof Matrix)
                {
                    c2len = ((Matrix) c).getColumnDimension();
                }
                else if (c instanceof ComplexMatrix)
                {
                    c2len = ((ComplexMatrix) c).getColumnDimension();
                }
                else
                {
                    throw new ConditionalException(
                            "scaleXcorr : Parameter \"c\" must be an instance of \"Matrix\" or \"ComplexMatrix\".");
                }
                scale = scale.repmat(1, c2len);
            }// end
             // c = c./scale;
            if (c instanceof Matrix)
            {
                c = ((Matrix) c).arrayRightDivide(scale);
            }
            else if (c instanceof ComplexMatrix)
            {
                c = ((ComplexMatrix) c).arrayDivide(scale);
            }
            else
            {
                throw new ConditionalException(
                        "scaleXcorr : Parameter \"c\" must be an instance of \"Matrix\" or \"ComplexMatrix\".");
            }

        }
        else if ("coeff".equals(scaleType))
        {
            // Normalizes the sequence so that the auto-correlations
            // at zero lag are identically 1.0.
            if (!autoFlag)
            {
                // xcorr(x,y)
                // Compute autocorrelations at zero lag
                // cxx0 = sum(abs(x).^2);
                Matrix absV = null;
                if (x instanceof Matrix)
                {
                    absV = ((Matrix) x).abs();
                }
                else if (x instanceof ComplexMatrix)
                {
                    absV = ((ComplexMatrix) x).abs();
                }
                else
                {
                    throw new ConditionalException(
                            "scaleXcorr : Parameter \"x\" must be an instance of \"Matrix\" or \"ComplexMatrix\".");
                }
                Matrix cxx0 = JDatafun.sum(JElfun.pow(absV, 2.0));

                // cyy0 = sum(abs(varargin{1}).^2);
                if (varargin[0] instanceof Matrix)
                {
                    absV = ((Matrix) varargin[0]).abs();
                }
                else if (varargin[0] instanceof ComplexMatrix)
                {
                    absV = ((ComplexMatrix) varargin[0]).abs();
                }
                else
                {
                    throw new ConditionalException("scaleXcorr : Parameter \"" + ((varargin[0]).toString())
                            + "\" must be an instance of \"Matrix\" or \"ComplexMatrix\".");
                }
                Matrix cyy0 = JDatafun.sum(JElfun.pow(absV, 2.0));

                Matrix scaleMat = JElfun.sqrt(cxx0.times(cyy0));
                // c = c./scale;
                if (c instanceof Matrix)
                {
                    c = ((Matrix) c).arrayRightDivide(scaleMat);
                }
                else if (c instanceof ComplexMatrix)
                {
                    c = ((ComplexMatrix) c).arrayDivide(scaleMat);
                }
                else
                {
                    throw new ConditionalException(
                            "scaleXcorr : Parameter \"c\" must be an instance of \"Matrix\" or \"ComplexMatrix\".");
                }
            }
            else
            {
                if (!xIsMatrix)
                {
                    // Autocorrelation case, simply normalize by c[0]
                    // c = c./c(maxlag+1);
                    Object denom = null;
                    if (c instanceof Matrix)
                    {
                        denom = ((Matrix) c).getElementAt(maxlag + 1);
                        c = ((Matrix) c).arrayRightDivide((Double) denom);
                    }
                    else if (c instanceof ComplexMatrix)
                    {
                        denom = ((ComplexMatrix) c).getElementAt(maxlag + 1);
                        c = ((ComplexMatrix) c).arrayDivide((Complex) denom);
                    }
                    else
                    {
                        throw new ConditionalException(
                                "scaleXcorr : Parameter \"c\" must be an instance of \"Matrix\" or \"ComplexMatrix\".");
                    }
                }
                else
                {
                    // Compute the indices corresponding to the columns for
                    // which
                    // we have autocorrelations (e.g. if c = n by 9, the
                    // autocorrelations
                    // are at columns [1,5,9] the other columns are
                    // cross-correlations).
                    // [mc,nc] = size(c);
                    int mc = 0;
                    int nc = 0;
                    if (c instanceof Matrix)
                    {
                        mc = ((Matrix) c).getRowDimension();
                        nc = ((Matrix) c).getColumnDimension();
                    }
                    else if (c instanceof ComplexMatrix)
                    {
                        mc = ((ComplexMatrix) c).getRowDimension();
                        nc = ((ComplexMatrix) c).getColumnDimension();
                    }
                    else
                    {
                        throw new ConditionalException(
                                "scaleXcorr : Parameter \"c\" must be an instance of \"Matrix\" or \"ComplexMatrix\".");
                    }

                    if (true)
                    {
                        throw new ConditionalException(
                                "scaleXcorr : The following code blocks are needed to be implemented.");
                        // jkl = reshape(1:nc,sqrt(nc),sqrt(nc))';
                        // tmp = sqrt(c(maxlag+1,diag(jkl)));
                        // tmp = tmp(:)*tmp;
                        // cdiv = repmat(tmp(:).',mc,1);
                        // c = c ./ cdiv; // The autocorrelations at zero-lag
                        // are normalized to one
                    }
                }// end
            }// end
        }// end
        return null;
    }

    private Object[] parse_x(Object x)
    {
        // [xIsMatrix,m]
        return null;
    }

    private Object[] parse_y(Object y, int m, boolean xIsMatrix, Object maxlag)
    {
        return null;
    }

    private Object[] parse_maxlag(Object maxlag, int m)
    {
        // [maxlag,msg]
        return null;
    }

    public Object[] parse_scaleType(String scaleType, String errMsg, boolean autoFlag, int m, Object... varargin)
    {
        // [scaleType,msg]
        return null;
    }
}
