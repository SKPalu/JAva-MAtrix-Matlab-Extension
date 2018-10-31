/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun.signal;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elfun.JElfun;
import jamaextension.jamax.polyfun.PolyFit;
import jamaextension.jamax.polyfun.PolyVal;

/**
 * Detrended Fluctuation Analysis
 * 
 * @author Sione
 */
public class DFA
{

    private double output;

    public DFA(Matrix DATA, int win_length, int order)
    {
        if (DATA == null)
        {
            throw new ConditionalException("DFA : Parameter\"DATA\" must be non-null.");
        }
        if (!DATA.isVector())
        {
            throw new ConditionalException("DFA : Matrix parameter\"DATA\" must be a vector and not a matrix.");
        }

        Matrix dat = null;
        if (DATA.isRowVector())
        {
            dat = DATA.toColVector();
        }
        else
        {
            dat = DATA;
        }

        int N = dat.length();
        int n = (int) Math.floor((double) N / (double) win_length);
        int N1 = n * win_length;
        Matrix y = Matrix.zeros(N1, 1);
        Matrix Yn = Matrix.zeros(N1, 1);

        Matrix fitcoef = Matrix.zeros(n, order + 1);

        double mean1 = JDatafun.mean(dat.getEls(0, N1 - 1)).start();
        double val = 0.0;

        for (int i = 0; i < N1; i++)
        {
            // y(i)=sum(DATA(1:i)-mean1);
            Matrix seg = dat.getEls(0, i);
            seg = seg.minus(mean1);
            val = JDatafun.sum(seg).start();
            y.setElementAt(i, val);
        }

        y = y.transpose();

        Matrix pxx = Matrix.linspace(1.0, (double) win_length, win_length);
        for (int j = 0; j < n; j++)
        {
            int J = j + 1;
            int from = ((J - 1) * win_length + 1) - 1;
            int to = J * win_length - 1;
            Matrix pyy = y.getEls(from, to);
            // fitcoef(j,:)=polyfit(1:win_length,y(((j-1)*win_length+1):j*win_length),order);
            PolyFit poly = new PolyFit(pxx, pyy, order);
            poly.setSorted(true);
            poly.build();
            Matrix coeff = poly.getCoeffs();
            fitcoef.setRowAt(j, coeff);
        }

        for (int j = 0; j < n; j++)
        {
            Matrix coeff = fitcoef.getRowAt(j);
            PolyVal polyval = new PolyVal(coeff, pxx);
            Matrix evalY = polyval.getY();
            // Yn(((j-1)*win_length+1):j*win_length)=polyval(fitcoef(j,:),1:win_length);
            int J = j + 1;
            int from = ((J - 1) * win_length + 1) - 1;// ((J - 1) * win_length +
                                                      // 1) - 1;
            int to = J * win_length - 1;// J * win_length - 1;
            int[] intArr = Indices.linspace(from, to).getRowPackedCopy();
            Yn.setElements(intArr, evalY);
        }

        Matrix tmp = y.transpose().minus(Yn);
        tmp = JElfun.pow(tmp, 2.0);
        double sum1 = JDatafun.sum(tmp).start() / (double) N1;// sum((y'-Yn).^2)/N1;
        sum1 = Math.sqrt(sum1);

        this.output = sum1;
    }

    /**
     * @return the output
     */
    public double getOutput()
    {
        return output;
    }

    public static void main(String[] args)
    {
        double[] xx =
        {
                26, 21, 33, 31, 15, 22, 1, 49, 8, 5, 19, 10, 24, 17, 48, 46, 3, 37, 13, 21, 27, 47, 21, 49, 15, 35, 33,
                27, 35, 33
        };

        Matrix x = new Matrix(xx);
        int win_length = 5;
        int order = 4;
        DFA da = new DFA(x, win_length, order);

        double out = da.getOutput();
        System.out.println("out = " + out);
    }
}
