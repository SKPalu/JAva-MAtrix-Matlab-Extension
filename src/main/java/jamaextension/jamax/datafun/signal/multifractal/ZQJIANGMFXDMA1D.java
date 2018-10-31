/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun.signal.multifractal;

import java.util.ArrayList;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elfun.JElfun;

/**
 * From the paper
 * "Multifractal detrending moving average cross-correlation analysis" Zhi-Qiang
 * Jiang and Wei-Xing Zhou1
 * 
 * @author Sione
 */
public class ZQJIANGMFXDMA1D
{

    private Matrix Fxxq;// = Matrix.zeros(q.length(), s.length());
    private Matrix Fxyq;// = Fxxq.copy();
    private Matrix Fyyq;
    private Indices S;

    public ZQJIANGMFXDMA1D(Matrix x, Matrix y)
    {
        this(x, y, Double.NaN);
    }

    public ZQJIANGMFXDMA1D(Matrix x, Matrix y, double theta)
    {
        this(x, y, theta, null);
    }

    public ZQJIANGMFXDMA1D(Matrix x, Matrix y, double theta, Indices q)
    {
        this(x, y, theta, q, null);
    }

    public ZQJIANGMFXDMA1D(Matrix x, Matrix y, double theta, Indices q, Indices s)
    {
        if (x == null || x.isNull())
        {
            throw new ConditionalException("ZQJIANGMFXDMA1D : Parameter \"x\" must be non-null or non-empty.");
        }
        if (!x.isVector())
        {
            throw new ConditionalException("ZQJIANGMFXDMA1D : Parameter \"x\" must be a vector and not a matrix.");
        }
        if (y == null || y.isNull())
        {
            throw new ConditionalException("ZQJIANGMFXDMA1D : Parameter \"y\" must be non-null or non-empty.");
        }
        if (!y.isVector())
        {
            throw new ConditionalException("ZQJIANGMFXDMA1D : Parameter \"y\" must be a vector and not a matrix.");
        }

        if (x.isColVector())
        {
            x = x.toRowVector();
        }
        if (y.isColVector())
        {
            y = y.toRowVector();
        }

        if (q == null || q.isNull())
        {
            q = new Indices(1, 1, 2);
        }

        if (theta <= 0)
        {
            throw new ConditionalException("ZQJIANGMFXDMA1D : Parameter \"theta\" must be a positive number.");
        }
        if (Double.isNaN(theta))
        {
            theta = 0.5;
        }

        int L = x.length();
        if (s == null || s.isNull())
        {
            ArrayList<Integer> sList = new ArrayList<Integer>();
            double i = 1.3;
            double Lov4 = (double) L / 4.0;
            int val = 0;
            while (Math.round(Math.pow(10.0, i)) <= Lov4)
            {
                // s = [ s round(10ˆ i ) ] ;
                val = (int) Math.round(Math.pow(10.0, i));
                sList.add(val);
                i = i + 0.1;
            }
            int siz = sList.size();
            if (!sList.isEmpty())
            {
                this.S = new Indices(1, siz);
                for (int j = 0; j < siz; j++)
                {
                    val = sList.get(j);
                    this.S.set(0, j, val);
                }
            }
        }
        else
        {
            if (!s.isVector())
            {
                throw new ConditionalException(
                        "ZQJIANGMFXDMA1D : Indices parameter \"s\" must be a vector and not a matrix.");
            }
            if (s.isColVector())
            {
                this.S = s.toRowVector();
            }
            else
            {
                this.S = s.copy();
            }
        }

        if (this.S == null)
        {
            throw new ConditionalException("ZQJIANGMFXDMA1D : Parameter \"s\" is null, therefore it has zero length.");
        }
        // Check if 'S' contains and integer element which is less than one
        boolean nonPositive = this.S.LT(1).anyBoolean();
        if (nonPositive)
        {
            throw new ConditionalException("ZQJIANGMFXDMA1D : All elements of parameter \"s\" must be positive.");
        }

        // Computing
        Matrix Fxxq2 = Matrix.zeros(q.length(), this.S.length());
        Matrix Fxyq2 = Fxxq2.copy();
        Matrix Fyyq2 = Fxxq2.copy();

        for (int i = 0; i < s.length(); i++)
        {
            Matrix x_re = myfunMA(x, this.S.getElementAt(i), theta);
            Matrix y_re = myfunMA(y, this.S.getElementAt(i), theta);
            // [ Fxxq2 ( : , i ) , Fxyq2 ( : , i ) , Fyyq2 ( : , i ) ] = myfunFq
            // ( x r e , y r e , s ( i ) , q ) ;
            Matrix[] FQ = myfunFq(x_re, y_re, this.S.getElementAt(i), q);
            Fxxq2.setColumnAt(i, FQ[0]);
            Fxyq2.setColumnAt(i, FQ[1]);
            Fyyq2.setColumnAt(i, FQ[2]);
        }

        this.Fxxq = Fxxq2;
        this.Fxyq = Fxyq2;
        this.Fyyq = Fyyq2;

    }

    private Matrix myfunMA(Matrix ts, int s, double theta)
    {
        // e s t imat e moveing av e rag e
        Matrix ts2 = JDatafun.cumsum(ts);
        int N = ts2.length();
        Matrix A = Matrix.zeros(s, N - s + 1);
        for (int k = 0; k < s; k++)
        {
            // A(k , : ) = t s ( k :N−s+k ) ;
            int to = N - s + k;
            if (k <= to)
            {
                Matrix tmp = ts2.getEls(k, to);
                A.setRowAt(k, tmp);
            }
        }
        Matrix MA = JDatafun.mean(A);

        // moving average
        int from = 1 + (int) Math.floor((s - 1) * theta);
        int to = ts2.length() - (int) Math.ceil((s - 1) * (1 - theta));
        // residual of time-series
        Matrix ts_re = ts2.getEls(from, to);// ts2(1+floor ( ( s−1)∗ the ta ) :
                                            // length ( ts2 )−ceil ( (
                                            // s−1)∗(1−theta ) ) ) − MA;
        ts_re = ts_re.minus(MA);

        // If the residuals can not be completely covered by the series , we can
        // cover the
        // series from both sides .
        N = ts_re.length();
        int n = (int) MathUtil.fix((double) N / (double) s);
        int ls = N - n * s;
        if (ls != 0)
        {
            Matrix ts_re1 = ts_re.getEls(1, n * s);// ts_re ( 1 : n∗ s ) ;
            Matrix ts_re2 = ts_re.getEls(ls + 1, N);// ts_re ( ls +1:N) ;
            ts_re = ts_re1.mergeH(ts_re2);// ts_re = [ ts_re1 ts_re2 ] ;
        }// end

        return ts_re;
    }

    /*
     * %% Estimating the fluctuations scaling function
     */
    private Matrix[] myfunFq(Matrix x_re, Matrix y_re, int s, Indices q)
    {
        int n = x_re.length() / s;
        Matrix X = x_re.reshape(s, n);
        Matrix Y = y_re.reshape(s, n);

        Matrix tmp = JElfun.pow(X, 2.0);
        Matrix Fxx = JDatafun.mean(tmp);// mean( abs (X) . ∗ abs (X) ) ;

        tmp = X.abs().arrayTimes(Y.abs());
        Matrix Fxy = JDatafun.mean(tmp);// mean( abs (X) . ∗ abs (Y) ) ;

        tmp = JElfun.pow(Y, 2.0);
        Matrix Fyy = JDatafun.mean(tmp);// mean( abs (Y) . ∗ abs (Y) ) ;

        Matrix Fxxq2 = Matrix.zeros(q.length(), 1);
        Matrix Fxyq2 = Matrix.zeros(q.length(), 1);
        Matrix Fyyq2 = Matrix.zeros(q.length(), 1);

        for (int k = 0; k < q.length(); k++)
        {
            double val = 0.0;
            if (q.getElementAt(k) != 0)
            {
                double pow1 = (double) q.getElementAt(k) / 2.0;
                double pow2 = 1.0 / (double) q.getElementAt(k);
                // Fxxq (k , 1 ) = (mean(Fxx . ˆ ( q ( k ) /2) ) ) . ˆ ( 1 / q (
                // k ) ) ;
                tmp = JElfun.pow(Fxx, pow1);
                tmp = JDatafun.mean(tmp);
                val = tmp.start();
                val = Math.pow(val, pow2);
                Fxxq2.set(k, 0, val);
                // Fxyq (k , 1 ) = (mean(Fxy . ˆ ( q ( k ) /2) ) ) . ˆ ( 1 / q (
                // k ) ) ;
                tmp = JElfun.pow(Fxy, pow1);
                tmp = JDatafun.mean(tmp);
                val = tmp.start();
                val = Math.pow(val, pow2);
                Fxyq2.set(k, 0, val);
                // Fyyq (k , 1 ) = (mean(Fyy . ˆ ( q ( k ) /2) ) ) . ˆ ( 1 / q (
                // k ) ) ;
                tmp = JElfun.pow(Fyy, pow1);
                tmp = JDatafun.mean(tmp);
                val = tmp.start();
                val = Math.pow(val, pow2);
                Fyyq2.set(k, 0, val);
            }
            else if (q.getElementAt(k) == 0)
            {
                // Fxxq (k , 1 ) = exp( 0 . 5 ∗mean( log (Fxx) ) ) ;
                tmp = JElfun.log(Fxx);
                tmp = JDatafun.mean(tmp).arrayTimes(0.5);
                tmp = JElfun.exp(tmp);
                val = tmp.start();
                Fxxq2.set(k, 0, val);
                // Fxyq (k , 1 ) = exp( 0 . 5 ∗mean( log (Fxy) ) ) ;
                tmp = JElfun.log(Fxy);
                tmp = JDatafun.mean(tmp).arrayTimes(0.5);
                tmp = JElfun.exp(tmp);
                val = tmp.start();
                Fxyq2.set(k, 0, val);
                // Fyyq (k , 1 ) = exp( 0 . 5 ∗mean( log (Fyy) ) ) ;
                tmp = JElfun.log(Fyy);
                tmp = JDatafun.mean(tmp).arrayTimes(0.5);
                tmp = JElfun.exp(tmp);
                val = tmp.start();
                Fyyq2.set(k, 0, val);
            }// end
        }// end

        return new Matrix[]
        {
                Fxxq2, Fxyq2, Fyyq2
        };
    }

    /**
     * @return the Fxxq
     */
    public Matrix getFxxq()
    {
        return Fxxq;
    }

    /**
     * @return the Fxyq
     */
    public Matrix getFxyq()
    {
        return Fxyq;
    }

    /**
     * @return the Fyyq
     */
    public Matrix getFyyq()
    {
        return Fyyq;
    }

    /**
     * @return the S
     */
    public Indices getS()
    {
        return S;
    }

    public static void main(String[] args)
    {
    }
}
