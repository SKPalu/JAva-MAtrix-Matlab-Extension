/*
 * Datevec.java
 *
 * Created on 28 November 2007, 20:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.timefun;

import java.util.Calendar;
import java.util.GregorianCalendar;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.elfun.JElfun;

/**
 * 
 * @author Feynman Perceptrons
 */
public class Datevec
{

    private Matrix datevec;

    /**
     * Creates a new instance of Datevec
     */
    public Datevec(GregorianCalendar date)
    {
        datevec = evalDate(date);
    }

    public Datevec(Double seconds)
    {
        datevec = evalDate(seconds);
    }

    public Datevec(GregorianCalendar date, Double pivotyear)
    {
        datevec = evalDate(date, pivotyear);
    }

    public Datevec(DateSeries dateSeries)
    {
        if (dateSeries == null)
        {
            throw new IllegalArgumentException("Datevec : Parameter \"dateSeries\" , must be non-null.");
        }
        int siz = dateSeries.size();
        this.datevec = new Matrix(siz, 6);
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar date = dateSeries.get(i);
            Matrix temp = evalDate(date);
            datevec.setRowAt(i, temp);
        }
    }

    public Datevec(GregorianCalendar[] date)
    {
        this(new DateSeries(date));
    }

    public Matrix getDatevec()
    {
        return this.datevec;
    }

    public Matrix getFirstDatevec()
    {
        return this.datevec.getRowAt(0);
    }

    private static Matrix evalDate(GregorianCalendar date)
    {
        return evalDate(date, null);
    }

    /**
   *
   */
    private static Matrix evalDate(Double seconds)
    {
        if (seconds == null)
        {
            throw new IllegalArgumentException("datevec : Parameter \"seconds\" , must be NOT be null.");
        }
        Double D = new Double(86400.0 * seconds.doubleValue());
        Dvcore dvcore = new Dvcore(D);

        return dvcore.getCore();// dvcore(new
                                // Double(86400.0*seconds.doubleValue()));
    }// end method

    private static Matrix evalDate(GregorianCalendar date, Double pivotyear)
    {
        String msg = "";
        if (date == null)
        {
            msg = "Parameter \"date\" must be non-null.";
            // throw new ConditionalRuleException("evalDate",msg);
            throw new IllegalArgumentException("datevec : " + msg);
        }

        // if(pivotyear==null){
        // msg = "Parameter \"pivotyear\" must be non-null.";
        // throw new ConditionalRuleException("evalDate",msg);
        // }

        double y = (double) date.get(Calendar.YEAR);

        double pvyr = 2007.0;
        if (pivotyear == null)
        {
            pvyr = (double) ((new GregorianCalendar()).get(Calendar.YEAR) - 50.0);
        }
        else
        {
            pvyr = pivotyear.doubleValue();
        }

        Matrix pvy = new Matrix(1, 1, pvyr);
        Matrix Y = new Matrix(1, 1);
        //
        Y = pvy.plus(JElfun.rem(JElfun.rem(pvy, 100.0).uminus().plus(y).plus(100.0), 100.0)); // +
                                                                                              // rem(f
                                                                                              // +
                                                                                              // 100
                                                                                              // -
                                                                                              // rem(pivotyear,100),100)

        y = Y.get(0, 0);
        // -(double)rightNow.get(Calendar.YEAR);
        double mo = (double) date.get(Calendar.MONTH) + 1.0;
        double d = (double) date.get(Calendar.DATE);
        double h = (double) date.get(Calendar.HOUR);
        double mi = (double) date.get(Calendar.MINUTE);
        double s = (double) date.get(Calendar.SECOND);
        double[][] dvec = new double[][]
        {
            {
                    y, mo, d, h, mi, s
            }
        };
        return new Matrix(dvec);
    }// end method

    public static GregorianCalendar[] secsToCalendar(Double[] seconds)
    {
        if (seconds == null)
        {
            throw new IllegalArgumentException("secsToCalendar : Parameter \"seconds\" , must be non-null.");
        }
        int len = seconds.length;
        GregorianCalendar[] GC = new GregorianCalendar[len];
        for (int i = 0; i < len; i++)
        {
            GC[i] = secsToCalendar(seconds[i]);
        }
        return GC;
    }

    public static GregorianCalendar[] secsToCalendar(double[] seconds)
    {
        if (seconds == null)
        {
            throw new IllegalArgumentException("secsToCalendar : Parameter \"seconds\" , must be non-null.");
        }
        int len = seconds.length;
        GregorianCalendar[] GC = new GregorianCalendar[len];
        for (int i = 0; i < len; i++)
        {
            GC[i] = secsToCalendar(new Double(seconds[i]));
        }
        return GC;
    }

    public static GregorianCalendar secsToCalendar(Double seconds)
    {
        if (seconds == null)
        {
            throw new IllegalArgumentException("secsToCalendar : Parameter \"seconds\" , must be non-null.");
        }
        double val = seconds.doubleValue();
        if (Double.isNaN(val) || Double.isInfinite(val))
        {
            return null;
        }
        Matrix dmonthMat = (new Datevec(seconds)).getDatevec();
        GregorianCalendar gc = new GregorianCalendar((int) dmonthMat.get(0, 0), (int) dmonthMat.get(0, 1) - 1,
                (int) dmonthMat.get(0, 2));
        return gc;
    }

    public static DateSeries secsToDateSeries(double[] seconds)
    {
        GregorianCalendar[] GC = secsToCalendar(seconds);
        return new DateSeries(GC);
    }

    public static DateSeries secsToDateSeries(Double[] seconds)
    {
        GregorianCalendar[] GC = secsToCalendar(seconds);
        return new DateSeries(GC);
    }

    public static DateSeries secsToDateSeries(Double seconds)
    {
        GregorianCalendar GC = secsToCalendar(seconds);
        return new DateSeries(GC);
    }

    public static Matrix evalDateSeries(GregorianCalendar[] dateSeries)
    {
        int len = dateSeries.length;
        Matrix DS = new Matrix(len, 6);
        for (int i = 0; i < len; i++)
        {
            Matrix temp = evalDate(dateSeries[i]);
            DS.setRowAt(i, temp);
        }
        return DS;
    }

    public static Matrix evalDateSeries(DateSeries series)
    {
        GregorianCalendar[] dateSeries = series.toCalendarArray();
        return evalDateSeries(dateSeries);
    }

}
