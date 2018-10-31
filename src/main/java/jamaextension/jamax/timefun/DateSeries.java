/*
 * DateSeries.java
 *
 * Created on 8 December 2007, 17:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.timefun;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortMat;
import jamaextension.jamax.elfun.JElfun;
import jamaextension.jamax.ops.Unique;

public class DateSeries implements java.io.Serializable, java.lang.Cloneable
{

    private ArrayList<GregorianCalendar> daysList = new ArrayList<GregorianCalendar>();

    /** Creates a new instance of CompoundingList */
    public DateSeries(GregorianCalendar[] series)
    {
        if (series == null)
        {
            throw new IllegalArgumentException("DateSeries : Parameter \"series\" must be non-null.");
        }
        int num = series.length;

        for (int i = 0; i < num; i++)
        {
            daysList.add(series[i]);
        }
    }

    public DateSeries()
    {
        this(new GregorianCalendar[]
        {
            null
        });
    }

    public DateSeries(GregorianCalendar day)
    {
        this(new GregorianCalendar[]
        {
            day
        });
    }

    public DateSeries(GregorianCalendar day, int repeat)
    {
        this(new GregorianCalendar[]
        {
            day
        });
        this.repeat(repeat);
    }

    public DateSeries(ArrayList<GregorianCalendar> daysList)
    {
        if (daysList == null || daysList.size() == 0)
        {
            throw new IllegalArgumentException("DateSeries : Parameter \"daysList\" must be non-null and non-empty.");
        }
        this.daysList = daysList;
    }

    public int size()
    {
        return daysList.size();
    }

    /*
     * public void repeat2(int numTimes){ if(numTimes<1){ throw new
     * IllegalArgumentException
     * ("repeat : Parameter \"numTimes\" must be at least one."); }
     * if(numTimes==1){ return; } for(int i=0; i<numTimes; i++){
     * ArrayList<GregorianCalendar> newDaysList = (ArrayList)daysList.clone();
     * this.add(newDaysList); } }
     */
    public void repeat(int numTimes)
    {
        if (numTimes < 1)
        {
            throw new IllegalArgumentException("repeat : Parameter \"numTimes\" must be at least one.");
        }
        if (numTimes == 1)
        {
            return;
        }

        ArrayList<GregorianCalendar> newDaysList = (ArrayList<GregorianCalendar>) daysList.clone();
        int newLen = newDaysList.size();

        for (int i = 0; i < (numTimes - 1); i++)
        {
            // System.out.println("newLen = "+newLen);
            for (int j = 0; j < newLen; j++)
            {
                GregorianCalendar obj = newDaysList.get(j);
                this.add(obj);
            }
        }
    }

    public DateSeries copy()
    {
        ArrayList<GregorianCalendar> newDaysList = (ArrayList<GregorianCalendar>) daysList.clone();
        DateSeries newDaySeries = new DateSeries(newDaysList);
        return newDaySeries;
    }

    public Object clone()
    {
        return copy();
    }

    public ArrayList<GregorianCalendar> getDaysList()
    {
        return daysList;
    }

    public GregorianCalendar[] toCalendarArray()
    {
        int siz = daysList.size();
        GregorianCalendar[] gc = new GregorianCalendar[siz];
        for (int i = 0; i < siz; i++)
        {
            gc[i] = daysList.get(i);
        }
        return gc;
    }

    public void set(int ind, Double secs)
    {
        GregorianCalendar gc = null;
        if (secs != null)
        {
            if (!Double.isNaN(secs) && !Double.isInfinite(secs))
            {
                gc = Datevec.secsToCalendar(secs);
            }
            // else{}
        }
        // else{}
        this.set(ind, gc);
    }

    public void set(int ind, GregorianCalendar obj)
    {

        if (ind < 0 || ind > (this.size() - 1))
        {
            throw new IllegalArgumentException("set : Parameter \"ind\" is out of bound.");
        }
        daysList.set(ind, obj);
    }

    public void set(int[] ind, GregorianCalendar[] obj)
    {
        if (ind == null)
        {
            throw new IllegalArgumentException("set : Parameter \"ind\"  must be non-null.");
        }
        if (obj == null)
        {
            throw new IllegalArgumentException("set : Parameter \"obj\"  must be non-null.");
        }
        if (ind.length != obj.length)
        {
            throw new IllegalArgumentException("set : Sizes of parameters \"ind\" and \"obj\"  must be equal.");
        }

        int len = ind.length;
        for (int i = 0; i < len; i++)
        {
            set(ind[i], obj[i]);
        }
    }

    public void set(int[] ind, GregorianCalendar obj)
    {
        set(ind, new GregorianCalendar[]
        {
            obj
        });
    }

    public GregorianCalendar getFirst()
    {
        return this.get(0);
    }

    public double getDateNumberAt(int ind)
    {
        GregorianCalendar date = get(ind);
        Datenum dn = new Datenum(date);
        return dn.getFirstValue().doubleValue();
    }

    public GregorianCalendar get(int ind)
    {
        if (ind < 0 || ind > (this.size() - 1))
        {
            throw new IllegalArgumentException("get : Parameter \"ind\" is out of bound.");
        }
        return daysList.get(ind);
    }

    public GregorianCalendar[] get(int[] ind)
    {
        if (ind == null)
        {
            throw new IllegalArgumentException("get : Parameter \"ind\"  must be non-null.");
        }
        int len = ind.length;
        GregorianCalendar[] GC = new GregorianCalendar[len];
        for (int i = 0; i < len; i++)
        {
            GC[i] = get(ind[i]);
        }
        return GC;
    }

    public DateSeries getSubList(int ind)
    {
        return getSubList(new int[]
        {
            ind
        });
    }

    public DateSeries getSubList(int[] ind)
    {
        DateSeries newDateSeries = (DateSeries) this.clone();
        return (new DateSeries(newDateSeries.get(ind)));
    }

    public void add(ArrayList<GregorianCalendar> anotherDaysList)
    {
        if (anotherDaysList == null || anotherDaysList.size() == 0)
        {
            // throw new
            // IllegalArgumentException("repeatDaysList : Parameter \"daysList\" must be non-null and non-empty.");
            return;
        }
        int siz = anotherDaysList.size();
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar cal = anotherDaysList.get(i);
            daysList.add(cal);
        }
        // daysList.
    }

    public void add(GregorianCalendar obj)
    {
        // if(obj==null){
        // throw new
        // IllegalArgumentException("add : Parameter \"obj\" must be non-null.");
        // }
        daysList.add(obj);
    }

    public void add()
    {
        GregorianCalendar obj = null;
        add(obj);
    }

    public boolean afterBool(DateSeries series)
    {
        int siz = this.size();
        int siz2 = series.size();
        if (siz != siz)
        {
            throw new IllegalArgumentException("afterBool : Sizes must be compatible.");
        }
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar dayOne = get(i);
            GregorianCalendar dayTwo = series.get(i);
            if (!dayOne.after(dayTwo))
            {
                return false;
            }
        }
        return true;
    }

    public Indices after(DateSeries series)
    {
        int siz = this.size();
        int siz2 = series.size();
        if (siz != siz)
        {
            throw new IllegalArgumentException("after : Sizes must be compatible.");
        }
        Indices indices = new Indices(1, siz);
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar dayOne = get(i);
            GregorianCalendar dayTwo = series.get(i);
            if (dayOne.after(dayTwo))
            {
                indices.set(0, i, 1);
            }
        }
        indices.setLogical(true);
        return indices;
    }

    public boolean afterBool(GregorianCalendar dayTwo)
    {
        int siz = this.size();

        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar dayOne = get(i);
            if (!dayOne.after(dayTwo))
            {
                return false;
            }
        }
        return true;
    }

    public Indices after(GregorianCalendar dayTwo)
    {
        int siz = this.size();
        Indices indices = new Indices(1, siz);
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar dayOne = get(i);
            if (dayOne.after(dayTwo))
            {
                indices.set(0, i, 1);
            }
        }
        indices.setLogical(true);
        return indices;
    }

    // ------------------------------------------
    public boolean afterBool(Double dayTwoSecs)
    {
        if (dayTwoSecs == null)
        {
            throw new IllegalArgumentException("afterBool : Parameter \"dayTwoSecs\" must be non-null.");
        }
        if (Double.isInfinite(dayTwoSecs) || Double.isNaN(dayTwoSecs))
        {
            throw new IllegalArgumentException(
                    "afterBool : Parameter \"dayTwoSecs\" must be finite (ie, non-infinity or non-NaN).");
        }
        GregorianCalendar dayTwoCalendar = Datevec.secsToCalendar(dayTwoSecs);
        int siz = this.size();
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar dayOne = get(i);
            if (!dayOne.after(dayTwoCalendar))
            {
                return false;
            }
        }
        return true;
    }

    public Indices after(Double dayTwoSecs)
    {
        if (dayTwoSecs == null)
        {
            throw new IllegalArgumentException("after : Parameter \"dayTwoSecs\" must be non-null.");
        }
        if (Double.isInfinite(dayTwoSecs) || Double.isNaN(dayTwoSecs))
        {
            throw new IllegalArgumentException(
                    "after : Parameter \"dayTwoSecs\" must be finite (ie, non-infinity or non-NaN).");
        }
        GregorianCalendar dayTwoCalendar = Datevec.secsToCalendar(dayTwoSecs);
        int siz = this.size();
        Indices indices = new Indices(1, siz);
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar dayOne = get(i);
            if (dayOne.after(dayTwoCalendar))
            {
                indices.set(0, i, 1);
            }
        }
        indices.setLogical(true);
        return indices;
    }

    // ------------------------------------------

    public boolean beforeBool(DateSeries series)
    {
        int siz = this.size();
        int siz2 = series.size();
        if (siz != siz2)
        {
            throw new IllegalArgumentException("beforeBool : Sizes must be compatible.");
        }
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar dayOne = get(i);
            GregorianCalendar dayTwo = series.get(i);
            if (!dayOne.before(dayTwo))
            {
                return false;
            }
        }
        return true;
    }

    public Indices before(DateSeries series)
    {
        int siz = this.size();
        int siz2 = series.size();
        if (siz != siz2)
        {
            throw new IllegalArgumentException("before : Sizes must be compatible.");
        }
        Indices indices = new Indices(1, siz);
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar dayOne = get(i);
            GregorianCalendar dayTwo = series.get(i);
            if (dayOne.before(dayTwo))
            {
                indices.set(0, i, 1);
            }
        }
        indices.setLogical(true);
        return indices;
    }

    public boolean beforeBool(GregorianCalendar dayTwo)
    {
        int siz = this.size();
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar dayOne = get(i);
            if (!dayOne.before(dayTwo))
            {
                return false;
            }
        }
        return true;
    }

    public Indices before(GregorianCalendar dayTwo)
    {
        int siz = this.size();
        Indices indices = new Indices(1, siz);
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar dayOne = get(i);
            if (dayOne.before(dayTwo))
            {
                indices.set(0, i, 1);
            }
        }
        indices.setLogical(true);
        return indices;
    }

    // --------------------------------------------------------
    public boolean beforeBool(Double dayTwoSecs)
    {
        if (dayTwoSecs == null)
        {
            throw new IllegalArgumentException("beforeBool : Parameter \"dayTwoSecs\" must be non-null.");
        }
        if (Double.isInfinite(dayTwoSecs) || Double.isNaN(dayTwoSecs))
        {
            throw new IllegalArgumentException(
                    "beforeBool : Parameter \"dayTwoSecs\" must be finite (ie, non-infinity or non-NaN).");
        }
        GregorianCalendar dayTwoCalendar = Datevec.secsToCalendar(dayTwoSecs);
        int siz = this.size();
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar dayOne = get(i);
            if (!dayOne.before(dayTwoCalendar))
            {
                return false;
            }
        }
        return true;
    }

    public Indices before(Double dayTwoSecs)
    {
        if (dayTwoSecs == null)
        {
            throw new IllegalArgumentException("before : Parameter \"dayTwoSecs\" must be non-null.");
        }
        if (Double.isInfinite(dayTwoSecs) || Double.isNaN(dayTwoSecs))
        {
            throw new IllegalArgumentException(
                    "before : Parameter \"dayTwoSecs\" must be finite (ie, non-infinity or non-NaN).");
        }
        GregorianCalendar dayTwoCalendar = Datevec.secsToCalendar(dayTwoSecs);
        int siz = this.size();
        Indices indices = new Indices(1, siz);
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar dayOne = get(i);
            if (dayOne.before(dayTwoCalendar))
            {
                indices.set(0, i, 1);
            }
        }
        indices.setLogical(true);
        return indices;
    }

    // --------------------------------------------------------

    public Matrix toColMatrix()
    {
        int siz = this.size();
        if (siz == 0)
        {
            return null;
        }
        Matrix mat = new Matrix(siz, 1);
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar feature = daysList.get(i);
            if (feature != null)
            {
                Datenum dn = new Datenum(feature);
                mat.set(i, 0, dn.getFirstValue().doubleValue());
            }
            else
            {
                mat.set(i, 0, Double.NaN);
            }
        }
        // Matrix mat = dn.getValue();
        return mat;
    }

    public Matrix toMatrix()
    {
        int siz = this.size();
        if (siz == 0)
        {
            return null;
        }
        Matrix mat = new Matrix(1, siz);
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar feature = daysList.get(i);
            if (feature != null)
            {
                Datenum dn = new Datenum(feature);
                mat.set(0, i, dn.getFirstValue().doubleValue());
            }
            else
            {
                mat.set(0, i, Double.NaN);
            }
        }
        // Matrix mat = dn.getValue();
        return mat;
    }

    public boolean hasNullDates()
    {
        int siz = this.size();
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar date = get(i);
            // if(dayOne.before(dayTwo)){ indices.set(0,i,1); }
            if (date == null)
            {
                return true;
            }
        }
        return false;
    }

    public boolean isAllNullDates()
    {
        Indices nd = nullDates().find();
        if (nd == null)
        {
            return false;
        }
        int len = nd.getRowDimension();
        return (len == size());
    }

    public Indices nullDates()
    {
        int siz = this.size();
        // ArrayList<Integer> nullDays = new ArrayList<Integer>();
        Indices nullIndices = new Indices(1, siz);
        nullIndices.setLogical(true);
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar date = get(i);
            // if(dayOne.before(dayTwo)){ indices.set(0,i,1); }
            if (date == null)
            {
                nullIndices.set(0, i, 1);
            }
        }

        /*
         * int numNull = nullDays.size(); if(numNull==0){ return null;} Indices
         * nullIndices = new Indices(1,numNull); for(int j=0; j<numNull; j++ ){
         * int val = nullDays.get(j).intValue(); nullIndices.set(0,j,val); }
         */
        return nullIndices;
    }

    public int numNullDates()
    {
        Indices nD = nullDates().find();
        if (nD == null)
        {
            return 0;
        }
        return nD.getRowDimension();
    }

    public void printDates()
    {
        printDates("Dates");
    }

    public void printDates(String str)
    {
        int siz = this.size();
        System.out.println("============ " + str + " ============\n");
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar GC = get(i);
            if (GC == null)
            {
                System.out.println((i + 1) + "). Date : Null");
            }
            else
            {
                int year = GC.get(Calendar.YEAR);
                int month = GC.get(Calendar.MONTH);
                month += 1;
                String mon = month < 10 ? "0" + month : "" + month;
                int days = GC.get(Calendar.DATE);
                String day = days < 10 ? "0" + days : "" + days;
                System.out.println((i + 1) + "). Date : " + day + "/" + mon + "/" + year);
            }
        }
        System.out.println("\n\n");
    }

    public static String calendarToString(GregorianCalendar GC)
    {
        if (GC == null)
        {
            return "Null";
        }
        else
        {
            int year = GC.get(Calendar.YEAR);
            int month = GC.get(Calendar.MONTH);
            month += 1;
            String mon = month < 10 ? "0" + month : "" + month;
            int days = GC.get(Calendar.DATE);
            String day = days < 10 ? "0" + days : "" + days;
            return (day + "/" + mon + "/" + year);
        }

    }

    // public String toDateString2()
    // { return}

    public String toDateString()
    {
        int siz = this.size();
        String matString = "";
        String monthName = "";
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar GC = get(i);
            if (GC == null)
            {
                matString += "[ ]";
            }
            else
            {
                int year = GC.get(Calendar.YEAR);
                int month = GC.get(Calendar.MONTH);
                monthName = intToMonth(month);
                month += 1;
                String mon = month < 10 ? "0" + month : "" + month;
                int days = GC.get(Calendar.DATE);
                String day = days < 10 ? "0" + days : "" + days;
                // System.out.println((i+1)+"). Date : "+day+"/"+mon+"/"+year);
                if (i != (siz - 1))
                {
                    matString += "'" + day + "-" + monthName + "-" + year + "' ; ";
                }
                else
                {
                    matString += "'" + day + "-" + monthName + "-" + year + "' ";
                }
            }
        }
        return matString;
    }

    public static String toDateString(GregorianCalendar date)
    {
        int siz = 1;
        String matString = "";
        String monthName = "";
        // for (int i = 0; i < siz; i++) {
        GregorianCalendar GC = date;
        if (GC == null)
        {
            matString += "[ ]";
        }
        else
        {
            int year = GC.get(Calendar.YEAR);
            int month = GC.get(Calendar.MONTH);
            monthName = intToMonth(month);
            month += 1;
            String mon = month < 10 ? "0" + month : "" + month;
            int days = GC.get(Calendar.DATE);
            String day = days < 10 ? "0" + days : "" + days;
            // System.out.println((i+1)+"). Date : "+day+"/"+mon+"/"+year);
            // if (i != (siz - 1)) {
            // matString += "'" + day + "-" + monthName + "-" + year + "' ; ";
            // } else {
            matString += "'" + day + "-" + monthName + "-" + year + "' ";
            // }
        }
        // }
        return matString;
    }

    public static String intToMonth(int ind)
    {
        String[] str =
        {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        return str[ind];
    }

    public static double eomday(int Y, int M)
    {
        return eomday(new Indices(1, 1, Y), new Indices(1, 1, M)).start();
    }

    public static double eomday(double Y, double M)
    {
        return eomday(new Indices(1, 1, (int) Y), new Indices(1, 1, (int) M)).start();
    }

    /*
     * Returns the last day of the month for the given year, Y, and month, M.
     * Indices vector 'M', must be in the interval [0,11]. The '0' corresponds
     * to Januraly and '11' corresponds to December.
     */
    public static Matrix eomday(Indices Y, Indices M)
    {
        // Number of days in the month.

        if (Y == null)
        {
            throw new IllegalArgumentException("eomday : Parameter \"Y\" must be non-null.");
        }
        if (!Y.isVector())
        {
            throw new IllegalArgumentException("eomday : Parameter \"Y\" must be a vector and not a matrix.");
        }

        if (M == null)
        {
            throw new IllegalArgumentException("eomday : Parameter \"M\" must be non-null.");
        }
        if (!M.isVector())
        {
            throw new IllegalArgumentException("eomday : Parameter \"M\" must be a vector and not a matrix.");
        }

        if (Y.length() != M.length())
        {
            throw new IllegalArgumentException("eomday : Parameter \"Y\" and \"M\" must be have equal sizes.");
        }

        double[] dpm =
        {
                31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        };
        Matrix DPM = new Matrix(dpm).toColVector();

        Matrix yInternal = null;
        if (Y.isColVector())
        {
            yInternal = Matrix.indicesToMatrix(Y);
        }
        else
        {
            yInternal = Matrix.indicesToMatrix(Y.toColVector());
        }

        Indices modifiedM = null;
        Matrix mInternal = null;
        if (M.isColVector())
        {
            modifiedM = M;
            mInternal = Matrix.indicesToMatrix(modifiedM);
        }
        else
        {
            modifiedM = M.toColVector();
            mInternal = Matrix.indicesToMatrix(modifiedM);
        }

        int[] arr = modifiedM.getColumnPackedCopy();

        /*
         * System.out.println("----- modifiedM -----"); modifiedM.print(8,0);
         * 
         * System.out.println("----- DPM -----"); DPM.print(8,0);
         */

        // Make result the right size and orientation.
        Matrix d = yInternal.minus(mInternal);// y - m;

        Matrix temp = DPM.getElements(arr);// .getMatrix(arr,0,0);
        // System.out.println("----- temp -----");
        // temp.print(8,0);
        d = temp; // d(:) = dpm(m);

        // d((m == 2) & ( (rem(y,4) == 0 & rem(y,100) ~= 0) | rem(y,400) == 0) )
        // = 29;
        Indices tempInd = modifiedM.EQ(1);
        tempInd = tempInd.AND(JElfun.rem(yInternal, 4.0).EQ(0.0).AND(JElfun.rem(yInternal, 100.0).NEQ(0.0))
                .OR(JElfun.rem(yInternal, 400.0).EQ(0.0)));
        tempInd = tempInd.find();
        if (tempInd != null)
        {
            d.setFromFind(tempInd, 29.0);
        }

        return d.toRowVector();
    }

    public Unique getUniqueDates()
    {
        Matrix dateNum = this.toMatrix();
        Unique uniqueDates = new Unique(dateNum);
        return uniqueDates;
    }

    public DateSeries flip()
    {
        DateSeries cp = copy();
        int siz = cp.size();
        int[] flipInd = Indices.linspace(0, siz - 1).flipLR().getRowPackedCopy();
        DateSeries newDateSeries = new DateSeries(cp.get(flipInd[0]));
        if (siz > 1)
        {
            for (int i = 1; i < siz; i++)
            {
                newDateSeries.add(cp.get(flipInd[i]));
            }
        }
        return newDateSeries;
    }

    /*
     * This method put the dates's months into a column matrix which starts at 1
     * for January and 12 for December.
     */
    public Matrix month()
    {
        int siz = this.size();
        if (siz == 0)
        {
            return null;
        }

        Matrix monthsMat = new Matrix(siz, 1, Double.NaN);

        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar GC = this.get(i);
            if (GC != null)
            {
                double val = GC.get(GregorianCalendar.MONTH) + 1.0;
                monthsMat.set(i, 0, val);
            }
        }

        return monthsMat;
    }

    public Matrix year()
    {
        int siz = this.size();
        if (siz == 0)
        {
            return null;
        }

        Matrix yearsMat = new Matrix(siz, 1, Double.NaN);

        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar GC = this.get(i);
            if (GC != null)
            {
                double val = GC.get(GregorianCalendar.YEAR);
                yearsMat.set(i, 0, val);
            }
        }

        return yearsMat;
    }

    public Object[] sortDatesAndIndices()
    {
        int siz = size();
        if (siz == 0)
        {
            return new Object[]
            {
                    new DateSeries(), new int[]
                    {
                        -1
                    }
            };
        }
        else if (siz == 1)
        {
            return new Object[]
            {
                    copy(), new int[]
                    {
                        0
                    }
            };
        }

        Matrix dateNum = this.toMatrix();

        // SortMat sort = new SortMat(dateNum); //--> old line
        // ----- Changed on 12/08/2009 from 'SortMat' into 'QuickSortMat'
        QuickSort sort = new QuickSortMat(dateNum, true, false); // --> new line

        int[] ind = sort.getIndices().getRowPackedCopy();
        DateSeries cp = copy();
        DateSeries newDateSeries = new DateSeries(cp.get(ind[0]));
        for (int i = 1; i < siz; i++)
        {
            newDateSeries.add(cp.get(ind[i]));
        }
        return new Object[]
        {
                newDateSeries, ind
        };
    }

    public DateSeries purgedNullDates()
    {
        if (!this.hasNullDates())
        {
            return copy();
        }
        int siz = this.size();
        int count = 0;
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar GC = this.get(i);
            if (GC == null)
            {
                count++;
            }
        }
        if (siz == count)
        {
            return null;
        }

        ArrayList<GregorianCalendar> nonNullDays = new ArrayList<GregorianCalendar>();
        // DateSeries(ArrayList<GregorianCalendar> daysList)
        for (int i = 0; i < siz; i++)
        {
            GregorianCalendar GC = this.get(i);
            if (GC != null)
            {
                nonNullDays.add((GregorianCalendar) GC.clone());
            }
        }

        return new DateSeries(nonNullDays);
    }

    public DateSeries sort()
    {
        Object[] obj = sortDatesAndIndices();
        return (DateSeries) obj[0];

        // The following commented block is the original. Must use the same call
        // from method 'sortDatesAndIndices'
        /*
         * int siz = size(); if (siz == 0) { return new DateSeries(); } else if
         * (siz == 1) { return copy(); }
         * 
         * Matrix dateNum = this.toMatrix(); SortMat sort = new
         * SortMat(dateNum); int[] ind = sort.getIndices().getRowPackedCopy();
         * DateSeries cp = copy(); DateSeries newDateSeries = new
         * DateSeries(cp.get(ind[0])); for (int i = 1; i < siz; i++) {
         * newDateSeries.add(cp.get(ind[i])); } return newDateSeries;
         */
    }

    public void addMatlabDate(String matStrDate, String sep)
    {
        GregorianCalendar GC = parseStringDate(matStrDate, sep);
        this.add(GC);
    }

    public void addStringDate(String matStrDate)
    {
        GregorianCalendar GC = parseStringDate(matStrDate);
        this.add(GC);
    }

    public static GregorianCalendar parseStringDate(String str)
    {
        return parseStringDate(str, "-");
    }

    /*
     *
     */
    public static GregorianCalendar parseStringDate(String str, String sep)
    {
        if (str == null || "".equals(str.trim()))
        {
            throw new IllegalArgumentException(
                    "parseMatlabDate : String parameter \"str\" must be non-null or non-empty.");
        }
        if (sep == null || "".equals(sep.trim()))
        {
            throw new IllegalArgumentException(
                    "parseMatlabDate : String parameter \"sep\" must be non-null or non-empty.");
        }
        /*
         * StringTokenizer strTk = new StringTokenizer(str,"-");
         * ArrayList<String> aList = new ArrayList<String>();
         * strTk.nextToken("-");
         */
        String[] comp = MathUtil.deSpace(str).split(sep);
        if (comp.length != 3)
        {
            throw new IllegalArgumentException("parseMatlabDate : String parameter \"str\" has wrong format.");
        }

        String dateStr = comp[0];
        String monthStr = comp[1];
        String yearStr = comp[2];

        int date = 0;
        try
        {
            date = Integer.parseInt(dateStr);
        }
        catch (NumberFormatException ne)
        {
            throw new IllegalArgumentException("parseMatlabDate : String parameter \"str\" has wrong format for date.");
        }

        int month = 0;
        if (Character.isDigit(monthStr.charAt(0)))
        {
            try
            {
                month = Integer.parseInt(monthStr) - 1;
            }
            catch (NumberFormatException ne)
            {
                throw new IllegalArgumentException(
                        "parseMatlabDate : String parameter \"str\" has wrong format for month.");
            }
        }
        else
        {
            month = monthNumber(monthStr);
        }

        int year = 0;
        try
        {
            year = Integer.parseInt(yearStr);
        }
        catch (NumberFormatException ne)
        {
            throw new IllegalArgumentException("parseMatlabDate : String parameter \"" + yearStr
                    + "\" has wrong format for year.");
        }

        GregorianCalendar GC = new GregorianCalendar(year, month, date);
        return GC;
    }

    public static GregorianCalendar parseStringYearMonthDate(String str)
    {
        return parseStringYearMonthDate(str, "-");
    }

    /*
     * The date format is year-month-day : eg , 2014-06-01
     */
    public static GregorianCalendar parseStringYearMonthDate(String str, String sep)
    {
        String[] parts = str.split(sep);

        // String year = parts[0].trim();
        // String month = parts[1].trim();
        // String day = parts[2].trim();

        // String[] comp = MathUtil.deSpace(str).split(sep);
        if (parts.length != 3)
        {
            throw new IllegalArgumentException("parseStringYearMonthDate : String parameter \"str\" has wrong format.");
        }

        String dateStr = parts[2].trim();// comp[0];
        String monthStr = parts[1].trim();// comp[1];
        String yearStr = parts[0].trim();// comp[2];

        int date = 0;
        try
        {
            date = Integer.parseInt(dateStr);
        }
        catch (NumberFormatException ne)
        {
            throw new IllegalArgumentException(
                    "parseStringYearMonthDate : String parameter \"str\" has wrong format for date.");
        }

        int month = 0;
        if (Character.isDigit(monthStr.charAt(0)))
        {
            try
            {
                month = Integer.parseInt(monthStr) - 1;
            }
            catch (NumberFormatException ne)
            {
                throw new IllegalArgumentException(
                        "parseStringYearMonthDate : String parameter \"str\" has wrong format for month.");
            }
        }
        else
        {
            month = monthNumber(monthStr);
        }

        int year = 0;
        try
        {
            year = Integer.parseInt(yearStr);
        }
        catch (NumberFormatException ne)
        {
            throw new IllegalArgumentException("parseMatlabDate : String parameter \"" + yearStr
                    + "\" has wrong format for year.");
        }

        GregorianCalendar GC = new GregorianCalendar(year, month, date);
        return GC;

    }

    public static int monthNumber(String monName)
    {
        String[] months =
        {
                "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
        };
        if (monName == null)
        {
            throw new IllegalArgumentException("monthNumber : String parameter \"monName\" must be non-null.");
        }
        String str = MathUtil.deSpace(monName).toUpperCase();

        if (str.length() != 3)
        {
            throw new IllegalArgumentException("monthNumber : String parameter \"monName\" must be a 3 character.");
        }

        int ind = 0;
        int len = months.length;
        for (int i = 0; i < len; i++)
        {
            if (str.equals(months[i]))
            {
                ind = i;
                break;
            }
        }

        return ind;
    }

    public static String monthName(int monNum)
    {
        return monthName(monNum, true);
    }

    public static String monthName(int monNum, boolean zeroIndex)
    {
        String[] months =
        {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        if (monNum < 0)
        {
            throw new IllegalArgumentException("monthName : String parameter \"monNum\" must be non-negative.");
        }
        String str = "";// MathUtil.deSpace(monName).toUpperCase();

        int ind = 0;
        int len = months.length;
        /*
         * for (int i = 0; i < len; i++) { if (str.equals(months[i])) { ind = i;
         * break; } }
         */
        if (zeroIndex)
        {
            str = months[monNum];
        }
        else
        {
            str = months[monNum - 1];
        }

        return str;
    }

    /*
     * Parameter 'month' is allowed any positive number that is greater than
     * zero. Month should fall in the interval , [1,12].
     */
    public static GregorianCalendar yearMonthDay(int year, int month, int dayNum)
    {
        if (year < 1900 || year > 2099)
        {
            throw new IllegalArgumentException(
                    "yearMonthDay : Parameter \"year\" must be in the interval : [1900, 2099].");
        }
        if (month < 0)
        {
            throw new IllegalArgumentException("yearMonthDay : Parameter \"month\" must be positive.");
        }
        if (dayNum < 0)
        {
            throw new IllegalArgumentException("yearMonthDay : Parameter \"dayNum\" must be positive.");
        }
        month = month - 1; // subtract 1 here, since the month falls in the
                           // interval [1,inf]
        return new GregorianCalendar(year, month, dayNum);
    }

    /*
     * Parameter 'month' should be in the interval [1,inf)
     */
    public static DateSeries yearMonthDay(Matrix year, Matrix month, Matrix dayNum)
    {
        if (year.isNotFinite())
        {
            throw new IllegalArgumentException("yearMonthDay : All elements of parameter \"year\" must be finite.");
        }
        if (year.LT(1900).OR(year.GT(2099)).anyBoolean())
        {
            throw new IllegalArgumentException("yearMonthDay : Parameter \"year\" must be non-null.");
        }
        if (!year.isVector())
        {
            throw new IllegalArgumentException("yearMonthDay : Parameter \"year\" must be vector and not a matrix.");
        }

        if (month.isNotFinite())
        {
            throw new IllegalArgumentException("yearMonthDay : All elements of parameter \"month\" must be finite.");
        }
        if (month.LT(0.0).anyBoolean())
        {
            throw new IllegalArgumentException("yearMonthDay : Parameter \"month\" must be non-null.");
        }
        if (!month.isVector())
        {
            throw new IllegalArgumentException("yearMonthDay : Parameter \"month\" must be vector and not a matrix.");
        }

        if (dayNum.isNotFinite())
        {
            throw new IllegalArgumentException("yearMonthDay : All elements of parameter \"dayNum\" must be finite.");
        }
        if (dayNum.LT(0.0).anyBoolean())
        {
            throw new IllegalArgumentException("yearMonthDay : Parameter \"dayNum\" must be non-null.");
        }
        if (!dayNum.isVector())
        {
            throw new IllegalArgumentException("yearMonthDay : Parameter \"dayNum\" must be vector and not a matrix.");
        }

        int lenYr = year.length();
        int lenMn = month.length();
        int lenDy = dayNum.length();
        int[] lens =
        {
                lenYr, lenMn, lenDy
        };
        Indices ind = new Indices(lens);
        ind = JDatafun.diff(ind);
        if (ind.NEQ(0).anyBoolean())
        {
            throw new IllegalArgumentException(
                    "yearMonthDay : All vector parameters \"year\", \"month\" and \"dayNum\" must have the same length.");
        }

        DateSeries DS = null;
        for (int i = 0; i < lenYr; i++)
        {
            int Y = (int) year.getElementAt(i);
            int M = (int) month.getElementAt(i);
            int D = (int) dayNum.getElementAt(i);
            GregorianCalendar GC = new GregorianCalendar(Y, M, D);
            if (i == 0)
            {
                DS = new DateSeries(GC);
            }
            else
            {
                DS.add(GC);
            }
        }

        return DS;
    }

    private static void mainEx()
    {
        /*
         * Indices y = Indices.linspace(1980, 1999); Indices m = Indices.ones(1,
         * y.length());//.arrayTimes(2);
         * 
         * Matrix E = DateSeries.eomday(y, m);
         * 
         * Indices find = E.toRowVector().EQ(29.0).find(); if (find != null) {
         * Indices leap = y.getFromFind(find);
         * System.out.println("------------ leap ------------"); leap.print(8);
         * }
         */

        /*
         * DateSeries DS = new DateSeries();
         * System.out.println("1) size = "+DS.size()); DS.repeat(6);
         * System.out.println("2) size = "+DS.size()); //hasNullDates()
         * System.out.println("3) hasNullDates() = "+DS.hasNullDates());
         */

        // StartDate = ['29-Feb-1992' ; '05-Apr-1994' ; '31-Jul-1994';
        // '25-Jul-1994' ; '25-Sep-1994' ; '07-Nov-1994' ]
        // EndDate = ['28-Feb-1996' ; '15-Mar-1995' ; '31-Aug-1995' ;
        // '17-Apr-1995' ; '24-Sep-1996' ; '09-May-1997' ]
        // NumberDays = yearfrac(StartDate, EndDate);

        /*
         * DateSeries startDate = new DateSeries(new GregorianCalendar(1992, 1,
         * 29), 5); //GregorianCalendar(int year, int month, int date)
         * 
         * 
         * startDate.add(new GregorianCalendar(1994, 3, 5)); startDate.add(new
         * GregorianCalendar(1994, 6, 31)); startDate.add(); startDate.add(new
         * GregorianCalendar(1994, 6, 25)); startDate.add(new
         * GregorianCalendar(1994, 8, 25)); startDate.add(new
         * GregorianCalendar(1994, 10, 7));
         * 
         * 
         * startDate.printDates();
         * 
         * System.out.println("==========================");
         * 
         * startDate = startDate.flip(); startDate.printDates();
         */

        DateSeries startDate = new DateSeries(parseStringDate("01-Jan-2000"));
        startDate.add(parseStringDate("01-Jan-2001"));
        startDate.add(parseStringDate("01-Jan-2002"));
        startDate.add(parseStringDate("01-Jan-2003"));

        startDate.printDates();

    }

    public static void main(String[] args)
    {
        int lenMon = 12;
        for (int i = 0; i < lenMon; i++)
        {
            double ds = DateSeries.eomday(2008, i);
            System.out.println(" ds_" + (i + 1) + " = " + ds);
        }
    }
}
