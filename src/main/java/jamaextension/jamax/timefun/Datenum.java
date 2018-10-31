/*
 * Datenum.java
 *
 * Created on 28 November 2007, 19:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.timefun;

import java.util.Calendar;
import java.util.GregorianCalendar;

import jamaextension.jamax.Matrix;

/**
 * 
 * DATENUM Serial date number. N = DATENUM(S) converts the string S into a
 * serial date number. Date numbers are serial days where 1 corresponds to
 * 1-Jan-0000. The string S must be in one of the date formats 0,1,2,6,13,14,
 * 15,16 (as defined by DATESTR). Date strings with 2 character years are
 * interpreted to be within the 100 years centered around the current year.
 * 
 * N = DATENUM(S,PIVOTYEAR) uses the specified pivot year as the starting year
 * of the 100-year range in which a two-character year resides. The default
 * pivot year is the current year minus 50 years.
 * 
 * N = DATENUM(Y,M,D) returns the serial date number for corresponding elements
 * of the Y,M,D (year,month,day) arrays. Y,M, AND D must be arrays of the same
 * size (or any can be a scalar).
 * 
 * N = DATENUM(Y,M,D,H,MI,S) returns the serial date number for corresponding
 * elements of the Y,M,D,H,MI,S (year,month,hour, minute,second) arrays values.
 * Y,M,D,H,MI,AND S must be arrays of the same size (or any can be a scalar).
 * Values outside the normal range of each array are automatically carried to
 * the next unit (for example month values greater than 12 are carried to
 * years).
 * 
 * Examples: n = datenum('19-May-1995') returns n = 728798. n =
 * datenum(1994,12,19) returns n = 728647. n = datenum(1994,12,19,18,0,0)
 * returns n = 728647.75.
 * 
 * @author Feynman Perceptrons
 */
public class Datenum
{

    private Matrix value;

    /** Creates a new instance of Datenum */
    public Datenum(Object date)
    {
        double val = dateval(date);
        value = new Matrix(1, 1, val);
    }

    public Datenum(GregorianCalendar[] dates)
    {
        value = dateSeriesEval(dates);
    }

    public Datenum(DateSeries datesArray)
    {
        // value = dateSeriesEval(dates);
        this(datesArray.toCalendarArray());
    }

    public Datenum(double[][] datevec)
    {
        Matrix mat = new Matrix(datevec); // check if rectangular
        int numRows = mat.getRowDimension();
        int numCols = mat.getColumnDimension();
        if (numCols != 3 && numCols != 6)
        {
            throw new IllegalArgumentException(
                    "Datenum : Parameter \"datevec\" array , must either have 3 or 6  columns.");
        }
        value = new Matrix(1, numRows);
        for (int i = 0; i < numRows; i++)
        {
            double[] rowVec = datevec[i];
            double val = dateval(rowVec);
            value.set(0, i, val);
        }
    }

    /*
     * For serial date : {year, month, day} , the 'month' should be between
     * [1,12]
     */
    private double dateval(Object date)
    {

        final double[] cdm =
        {
                0.0, 31.0, 59.0, 90.0, 120.0, 151.0, 181.0, 212.0, 243.0, 273.0, 304.0, 334.0
        };

        double t = 0.0;
        double yp = 0.0;
        double mo = 0.0;
        double d = 0.0;
        double h = 0.0;
        double mi = 0.0;
        double s = 0.0;
        double y = 0.0;

        // int hms = 0;
        int m = 0;
        int n = 0;
        int mn = 0;
        int k = 0;
        int iy = 0;
        int mon = 0;

        int len = 0;
        boolean hms = false;

        if (date == null)
        {
            throw new IllegalArgumentException("datenum : Parameter \"date\" Object, must NOT be null.");
        }
        else if (date instanceof GregorianCalendar)
        {
            GregorianCalendar D = (GregorianCalendar) date;
            int year = D.get(Calendar.YEAR);
            int month = D.get(Calendar.MONTH);
            month += 1;
            int day = D.get(Calendar.DATE);
            int hour = D.get(Calendar.HOUR);
            int minute = D.get(Calendar.MINUTE);
            int sec = D.get(Calendar.SECOND);
            double[] dateArray = new double[]
            {
                    year, month, day, hour, minute, sec
            };
            /*
             * double[][] mat = new double[1][]; mat[0] = dateArray; Matrix
             * dateMat = new Matrix(mat);
             * System.out.print("\n=============="+dateMat+"==============");
             * dateMat.print(6,0);
             */
            return dateval(dateArray);
        }
        else if (date instanceof double[])
        {
            double[] dt = (double[]) date;
            len = dt.length;
            if (len != 3 && len != 6)
            {
                throw new IllegalArgumentException(
                        "datenum : Parameter \"date\" array , must have either 3 or 6 number of arrary elements.");
            }

            /* Input is three or six components, or column vectors. */
            m = 1;
            mn = 1; // m*n;
            // System.out.print("\n mn = "+mn+"\n\n");
            hms = (len == 6);

            yp = dt[0];// mxGetPr(prhs[0]);
            mo = dt[1];// mxGetPr(prhs[1]);
            d = dt[2];// mxGetPr(prhs[2]);
            // yinc = false;//mxGetNumberOfElements(prhs[0]) == mn;
            // moinc = false;//mxGetNumberOfElements(prhs[1]) == mn;
            // dinc = false;//mxGetNumberOfElements(prhs[2]) == mn;
            if (hms)
            {
                h = dt[3];// mxGetPr(prhs[3]);
                mi = dt[4];// mxGetPr(prhs[4]);
                s = dt[5];// mxGetPr(prhs[5]);
                // hinc = false;//mxGetNumberOfElements(prhs[3]) == mn;
                // miinc = false;//mxGetNumberOfElements(prhs[4]) == mn;
                // sinc = false;//mxGetNumberOfElements(prhs[5]) == mn;
            }
        }
        else if (date instanceof Matrix)
        {
            if (((Matrix) date).isVector() == false)
            {
                throw new IllegalArgumentException(
                        "datenum : Parameter \"date\" Matrix , must be a vector with 3 or 6 number of elements.");
            }
            len = ((Matrix) date).length();
            if (len != 3 && len != 6)
            {
                throw new IllegalArgumentException(
                        "datenum : Parameter \"date\" Matrix , must have either 3 or 6 number of vector elements.");
            }

            Matrix datMat = (Matrix) date;
            if (datMat.isRowVector() == false)
            {
                datMat = datMat.toRowVector();
            }
            double[] dateMatArray = new double[len];
            for (int i = 0; i < len; i++)
            {
                dateMatArray[i] = datMat.get(0, i);
            }
            return dateval(dateMatArray);
        }
        else
        {
            throw new IllegalArgumentException(
                    "datenum : Parameter \"date\" , must be an \"Object\" of type  \"Calendar\" , \"double[]\" or \"Matrix\".");
        }

        // for (k = 0; k < mn; k++) {
        y = yp;
        mon = (int) mo;
        /* Make sure month is in the range 1 to 12. */
        if (mon < 1)
        {
            mon = 1;
        }
        if (mon > 12)
        {
            y += ((double) (mon - 1)) / 12.0;
            mon = ((mon - 1) % 12) + 1;
        }
        t = 365.0 * y + Math.ceil(y / 4.0) - Math.ceil(y / 100.0) + Math.ceil(y / 400.0) + cdm[mon - 1] + d;
        if (mon > 2)
        {
            iy = (int) y;
            if ((iy % 4 == 0) && (iy % 100 != 0) || (iy % 400 == 0))
            {
                t += 1.0;
            }
        }
        // yp += (yinc?1.0:0.0);
        // mo += (moinc?1.0:0.0);
        // d += (dinc?1.0:0.0);
        if (hms)
        {
            t += (h * 3600.0 + mi * 60.0 + s) / 86400.0;
            // h += (hinc?1.0:0.0);
            // mi += (miinc?1.0:0.0);
            // s += (sinc?1.0:0.0);
        }
        // t;
        // }

        return t;// new Double(t);
    }

    /**
     * 
     * @return
     */
    public Matrix getValue()
    {
        return this.value;
    }

    public Double getFirstValue()
    {
        return new Double(value.get(0, 0));
    }

    public static Double toDatenum(GregorianCalendar day)
    {
        Datenum DN = new Datenum(day);
        return DN.getFirstValue();
    }

    // public DateSeries toDateSeries(){}
    /**
     * 
     * @param dates
     * @return
     */
    private Matrix dateSeriesEval(GregorianCalendar[] dates)
    {
        if (dates == null)
        {
            return null;
        }
        int len = dates.length;
        Matrix dateMat = new Matrix(1, len);

        for (int i = 0; i < len; i++)
        {
            double D = dateval(dates[i]);
            dateMat.set(0, i, D);
        }
        return dateMat;
    }

    public static String getCurrentDateTime()
    {
        GregorianCalendar GC = new GregorianCalendar();
        String str = GC.getTime().toString();
        str = str.replace(" ", "_");
        str = str.replace(":", "-");
        return str;
    }

    private static void mainEx()
    {

        // settle = ['29-Feb-1992' ; '05-Apr-1994' ; '31-Jul-1994' ;
        // '25-Jul-1994' ; '25-Sep-1994' ; '07-Nov-1994' ]
        // maturity = ['28-Feb-1996' ; '15-Mar-1995' ; '31-Aug-1995' ;
        // '17-Apr-1995' ; '24-Sep-1996' ; '09-May-1997' ]

        boolean multiDate = true;

        DateSeries settle = new DateSeries(new GregorianCalendar(1992, 1, 29)); // GregorianCalendar(int
                                                                                // year,
                                                                                // int
                                                                                // month,
                                                                                // int
                                                                                // date)
        if (multiDate)
        {
            settle.add(new GregorianCalendar(1994, 3, 5));
            settle.add(new GregorianCalendar(1994, 6, 31));
            settle.add(new GregorianCalendar(1994, 6, 25));
            settle.add(new GregorianCalendar(1994, 8, 25));
            settle.add(new GregorianCalendar(1994, 10, 7));
        }

        /*
         * DateSeries maturity = new DateSeries(new
         * GregorianCalendar(1996,1,28)); if(multiDate) { maturity.add(new
         * GregorianCalendar(1995,2,15) ); maturity.add(new
         * GregorianCalendar(1995,7,31) ); maturity.add(new
         * GregorianCalendar(1995,3,17) ); maturity.add(new
         * GregorianCalendar(1996,8,24) ); maturity.add(new
         * GregorianCalendar(1997,4,9) ); }
         */

        Datenum DN = new Datenum(settle);

        System.out.println("----- settle -----");
        DN.getValue().toColVector().print(8, 0);

        System.out.println("settle = [ " + settle.toDateString() + "]");
        System.out.println("\n");

    }

    public static void main(String[] args)
    {

        mainEx();
        /*
         * Datenum dn = new Datenum(new double[] { 2008, 2, 31 });
         * System.out.println(" dn = " + dn.getFirstValue());
         */

        // String str = Datenum.getCurrentDateTime();
        // System.out.println("str = " + str);

    }
}// ------------------------------- End Class Definition
// ------------------------

