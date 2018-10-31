/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun.signal;

import jamaextension.jamax.ComplexMatrix;
import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.Min;
import jamaextension.jamax.datafun.MinMat;
import jamaextension.jamax.elfun.JElfun;

/**
 * 
 * @author Sione
 */
public class FftFilter
{

    private Object filteredSig;

    public FftFilter(Object b, Object x)
    {
        fftfilt(b, x, Integer.MAX_VALUE);
    }

    public FftFilter(Object b, Object x, int nfft)
    {
        this.filteredSig = fftfilt(b, x, nfft);
    }

    private Object fftfilt(Object bb, Object xx, int nfft)
    {

        if (bb == null)
        {
            throw new ConditionalException("fftfilt : Object parameter \"bb\" must be non-null.");
        }
        boolean cond = !(bb instanceof Matrix) && !(bb instanceof ComplexMatrix);
        if (cond)
        {
            throw new ConditionalException(
                    "fftfilt : Object parameter \"bb\" must be an instanceof \"Matrix\" or \"ComplexMatrix\".");
        }
        if (xx == null)
        {
            throw new ConditionalException("fftfilt : Object parameter \"xx\" must be non-null.");
        }
        cond = !(xx instanceof Matrix) && !(xx instanceof ComplexMatrix);
        if (cond)
        {
            throw new ConditionalException(
                    "fftfilt : Object parameter \"xx\" must be an instanceof \"Matrix\" or \"ComplexMatrix\".");
        }

        ComplexMatrix b = null;
        if (bb instanceof Matrix)
        {
            b = new ComplexMatrix((Matrix) bb);
        }
        else
        {
            b = (ComplexMatrix) bb;
        }
        ComplexMatrix x = null;
        if (bb instanceof Matrix)
        {
            x = new ComplexMatrix((Matrix) xx);
        }
        else
        {
            x = (ComplexMatrix) xx;
        }

        int m = x.getRowDimension();// size(x, 1);
        if (m == 1)
        {
            x = x.toColVector(); // turn row into a column
        }// end

        int nx = x.getRowDimension();// size(x,1);

        if (JDatafun.min(b.sizeIndices()).start() > 1)
        {// min(size(b))>1
            int b2 = b.getColumnDimension();
            int x2 = x.getColumnDimension();
            if (b2 != x2 && x2 > 1)
            {// (size(b,2)~=size(x,2))&&(size(x,2)>1)
             // error(generatemsgid('InvalidDimensions'),'Filter matrix B
             // must have same number of columns as X.')
                throw new ConditionalException(
                        "fftfilt : Filter matrix \"B\" must have same number of columns as \"X\".");
            }// end
        }
        else
        {
            b = b.toColVector();// b(:); // make input a column
        }// end
        int nb = b.getRowDimension();// size(b,1);

        if ((double) nb > Math.pow(2.0, 20.0))
        {// nb > 2^20,
         // error(generatemsgid('filterTooLong'), ...
         // 'Filters of length greater than 2^20 are not supported. Use
         // dfilt.fftfir instead.');
            throw new ConditionalException(
                    "fftfilt : Filters of length greater than 2^20 are not supported. Use dfilt.fftfir instead.");
        }// end

        int L = 0;
        if (nfft == Integer.MAX_VALUE)
        {// nargin < 3
         // figure out which nfft and L to use
            if (nb >= nx)
            { // take a single FFT in this case
                double pow = JElfun.nextpow2((double) (nb + nx - 1));
                nfft = (int) Math.pow(2.0, pow);// 2^nextpow2(nb+nx-1);
                L = nx;
            }
            else
            {
                double[] fftlp =
                {
                        18, 59, 138, 303, 660, 1441, 3150, 6875, 14952, 32373, 69762, 149647, 319644, 680105, 1441974,
                        3047619, 6422736, 13500637, 28311786, 59244791
                };
                Matrix fftflops = new Matrix(fftlp);
                Matrix L1to20 = Matrix.linspace(1.0, 2.0, 20);
                Matrix n = JElfun.pow(2.0, L1to20);// 2.^(1:20);
                Indices validset = n.GT((double) (nb - 1)).find();// find(n>(nb-1));
                                                                  // // must
                                                                  // have nfft >
                                                                  // (nb-1)
                if (validset != null)
                {
                    n = n.getFromFind(validset);
                    fftflops = fftflops.getFromFind(validset);
                }
                // minimize (number of blocks) * (number of flops per fft)
                Matrix Lind = n.minus((double) (nb - 1));
                Matrix tmp = JElfun.ceil(Lind.reciprocate(nx)).arrayTimes(fftflops);
                // [dum,ind] = min( ceil(nx./Lind) .* fftflops ); //#ok
                Min mn = new MinMat(tmp);
                int ind = mn.getIndices().start();
                nfft = (int) n.getElementAt(ind);
                L = (int) Lind.getElementAt(ind);
            }// end

        }
        else
        { // nfft is given
            if (nfft < nb)
            {
                nfft = nb;
            }// end
            double pow = (Math.ceil(Math.log((double) nfft) / Math.log(2.0)));
            nfft = (int) Math.pow(2.0, pow);// 2.^(ceil(log(nfft)/log(2))); //
                                            // force this to a power of 2 for
                                            // speed
            L = nfft - nb + 1;
        }// end

        ComplexMatrix B = SigProc.fft(b, nfft);
        if (b.length() == 1)
        {
            // B = B(:); // make sure fft of B is a column (might be a row if b
            // is scalar)
            B = B.toColVector();
        }// end
        int[] arrInd = null;
        if (b.getColumnDimension() == 1)
        {// size(b,2)==1
         // B = B(:,ones(1,size(x,2))); // replicate the column B
            arrInd = new Indices(1, x.getColumnDimension()).getRowPackedCopy();
            B = B.getColumns(arrInd);
        }// end
        if (x.getColumnDimension() == 1)
        {// size(x,2)==1
         // x = x(:,ones(1,size(b,2))); // replicate the column x
            arrInd = new Indices(1, b.getColumnDimension()).getRowPackedCopy();
            x = x.getColumns(arrInd);
        }// end

        ComplexMatrix y = new ComplexMatrix(x.sizeIntArr());// zeros(size(x));

        int istart = 1;
        while (istart <= nx)
        {
            int iend = Math.min(istart + L - 1, nx);
            ComplexMatrix X = null;
            if ((iend - istart) == 0)
            {
                // X = x(istart(ones(nfft,1)),:); // need to fft a scalar
                arrInd = new Indices(1, nfft, istart - 1).getRowPackedCopy();
                X = x.getRows(arrInd);
            }
            else
            {
                // X = fft(x(istart:iend,:),nfft);
                arrInd = Indices.linspace(istart, iend).minus(1).getRowPackedCopy();
                X = SigProc.fft(x.getRows(arrInd), nfft);
            }// end
            ComplexMatrix XB = X.arrayTimes(B);
            ComplexMatrix Y = SigProc.ifft(XB);
            int yend = Math.min(nx, istart + nfft - 1);

            arrInd = Indices.linspace(istart, iend).minus(1).getRowPackedCopy();
            int[] arrInd2 = Indices.linspace(1, (yend - istart + 1)).minus(1).getRowPackedCopy();
            // y(istart:yend,:) = y(istart:yend,:) + Y(1:(yend-istart+1),:);
            ComplexMatrix yTmp = y.getRows(arrInd);
            yTmp = yTmp.plus(Y.getRows(arrInd2));
            y.setRows(arrInd, yTmp);
            istart = istart + L;
        }// end

        cond = !(b.toColVector().im().anyBoolean() || x.toColVector().im().anyBoolean());// ~(any(imag(b(:)))
                                                                                         // ||
                                                                                         // any(imag(x(:))));

        Object yRet = y;
        if (cond)
        {// ~(any(imag(b(:))) || any(imag(x(:))))
         // y = real(y);
            yRet = y.re();
        }// end

        if ((m == 1) && (y.getColumnDimension() == 1))
        {
            // y = y(:).'; // turn column back into a row
            yRet = y.toColVector().transpose();
        }// end

        return yRet;
    }

    public boolean isReal()
    {
        return (this.getFilteredSig() instanceof Matrix);
    }

    /**
     * @return the filteredSig
     */
    public Object getFilteredSig()
    {
        return filteredSig;
    }

    public ComplexMatrix getFilteredSigCP()
    {
        ComplexMatrix CP = null;
        if (isReal())
        {
            CP = new ComplexMatrix((Matrix) this.filteredSig);
        }
        else
        {
            CP = (ComplexMatrix) this.filteredSig;
        }
        return CP;
    }
}
