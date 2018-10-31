/*
 * Dvcore.java
 *
 * Created on 28 November 2007, 20:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.timefun;

import java.util.List;

import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;

/**
 * 
 * @author Feynman Perceptrons
 */
public class Dvcore
{

    private Matrix core;
    private final double[][] dpm =
    {
        {
                31.0, 28.0, 31.0, 30.0, 31.0, 30.0, 31.0, 31.0, 30.0, 31.0, 30.0, 31
        }
    };
    private Matrix DPM = new Matrix(dpm);

    /** Creates a new instance of Dvcore */
    public Dvcore(Double SEC)
    {
        core = dvcoreEval(SEC);
    }

    public Dvcore(Double[] SECS)
    {
        int len = SECS.length;
        core = new Matrix(len, 6);
        for (int i = 0; i < len; i++)
        {
            Matrix temp = dvcoreEval(SECS[i]);
            core.setRowAt(i, temp);
        }
    }

    public Dvcore(List<Double> SECS)
    {
        int len = SECS.size();
        core = new Matrix(len, 6);
        for (int i = 0; i < len; i++)
        {
            Matrix temp = dvcoreEval(SECS.get(i));
            core.setRowAt(i, temp);
        }
    }

    public Matrix getCore()
    {
        return core;
    }

    /**
   *
   */
    private Matrix dvcoreEval(Double SEC)
    {
        if (SEC == null)
        {
            throw new IllegalArgumentException("dvcore : Parameter \"SEC\" , must be NOT be null.");
        }
        double seconds = SEC.doubleValue();
        double t = Math.abs(seconds);
        double p = (400.0 * 365.0 + 97.0) * 86400.0; // System.out.println("pp = "+p);
        double shift = Math.max(0.0, Math.ceil(-t / p)); // System.out.println("shift = "+shift);
        t = t + shift * p; // System.out.println(" t1 = "+t);

        double sec = MathUtil.rem(t, 60.0);
        // Truncate to the nearest second
        t = Math.floor(t); // System.out.println(" t2 = "+t);
        t = Math.floor(t / 60.0); // System.out.println(" t3 = "+t);
        double min = MathUtil.rem(t, 60.0);
        t = Math.floor(t / 60.0);
        double hrs = MathUtil.rem(t, 24.0);
        t = Math.floor(t / 24.0);
        double a = 365.0 + 97.0 / 400.0;
        double y = Math.floor(t / a);

        double temp = 365.0 * y + Math.ceil(0.25 * y) - Math.ceil(0.01 * y) + Math.ceil(0.0025 * y);
        if (t <= temp)
        {
            y = y - 1.0;
        }// System.out.println(" y = "+y);}

        double year = y - shift * 400.0;

        t = t - (365.0 * y + Math.ceil(0.25 * y) - Math.ceil(0.01 * y) + Math.ceil(0.0025 * y)); // System.out.println(" t = "+t);

        boolean iVal = ((MathUtil.rem(y, 4.0) == 0.0) && (MathUtil.rem(y, 100.0) != 0.0))
                || MathUtil.rem(y, 400.0) == 0.0;

        if (iVal)
        {
            DPM.set(0, 1, 29.0);
        }

        DPM = JDatafun.cumsum(DPM);

        // System.out.println("********DPM Cumsum*********");
        // DPM.print(4,0);

        Matrix DPMcopy = DPM.copy();
        // Matrix DPMcopy2 = DPM.copy();
        DPMcopy.set(0, 0, 0.0);
        int lengthDPM = DPM.length();
        for (int i = 1; i < lengthDPM; i++)
        {
            DPMcopy.set(0, i, DPM.get(0, i - 1));
        }

        // System.out.println("********DPMcopy Cumsum*********");
        // DPMcopy.print(4,0);

        Matrix T = new Matrix(1, lengthDPM, t);
        Matrix H = T.plus(DPMcopy.uminus());
        // System.out.println("********* H **********");
        // H.print(4,0);

        Matrix tempInd = Matrix.indicesToMatrix(H.GT(0.0));

        double month = JDatafun.sum(tempInd).get(0, 0);// sum((t(:,ones(1,12)) -
                                                       // cdm > 0)')';

        double days = t - DPMcopy.get(0, (int) month - 1);// + 1.0;

        // System.out.println(" year: "+(int)year+", month: "+(int)month+", day: "+(int)days);

        return new Matrix(new double[][]
        {
            {
                    year, month, days, hrs, min, sec
            }
        });
    }// end method

}
