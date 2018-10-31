/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.timefun;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;

/**
 * 
 * @author Sione
 */
public class WeekDay
{

    private final static String[] days =
    {
            "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
    };
    private Indices dayNumber;
    private ArrayList<String> dayName = new ArrayList<String>();
    private DateSeries dates;
    private boolean daysNameIncluded = false;

    public WeekDay(GregorianCalendar gcDate)
    {
        this(new DateSeries(gcDate));
    }

    public WeekDay(DateSeries dates)
    {
        this(dates, false);
    }

    public WeekDay(DateSeries dates, boolean daysNameIncluded)
    {
        String msg = "";
        if (dates == null)
        {
            msg = "Parameter \"dates\"  must be non-null.";
            throw new ConditionalRuleException("WeekDay", msg);
        }
        if (dates.hasNullDates())
        {
            msg = "Parameter \"dates\"  contains a null date element.";
            throw new ConditionalRuleException("WeekDay", msg);
        }
        this.dates = dates;
        this.daysNameIncluded = daysNameIncluded;
        computeDayNum();
    }

    public WeekDay(Double dateNum)
    {
        this(new Matrix(1, 1, dateNum == null ? Double.NaN : dateNum.doubleValue()), false);
    }

    public WeekDay(Matrix dateNumMat)
    {
        this(dateNumMat, false);
    }

    public WeekDay(Matrix datesMat, boolean daysNameIncluded)
    {
        String msg = "";
        if (datesMat == null)
        {
            msg = "Parameter \"dateNumMat\"  must be non-null.";
            throw new ConditionalRuleException("WeekDay", msg);
        }
        if (!datesMat.isVector())
        {
            msg = "Parameter \"dateNumMat\"  must be a column or row vector and not a matrix.";
            throw new ConditionalRuleException("WeekDay", msg);
        }
        if (datesMat.isnanBoolean())
        {
            msg = "Parameter \"dateNumMat\"  contains a NaN element (ie, not-a-number).";
            throw new ConditionalRuleException("WeekDay", msg);
        }

        this.dates = Datevec.secsToDateSeries(datesMat.getRowPackedCopy());
        this.daysNameIncluded = daysNameIncluded;
        computeDayNum();
    }

    private void computeDayNum()
    {
        int len = dates.size();
        this.dayNumber = new Indices(1, len);

        for (int i = 0; i < len; i++)
        {
            GregorianCalendar GC = dates.get(i);
            int weekDayNum = GC.get(GregorianCalendar.DAY_OF_WEEK);
            this.dayNumber.set(0, i, weekDayNum);
            if (daysNameIncluded)
            {
                String dayNameStr = days[weekDayNum - 1];
                dayName.add(dayNameStr);
            }
        }
    }

    /**
     * @return the dayNumber
     */
    public Indices getDayNumber()
    {
        return dayNumber;
    }

    /**
     * @return the dayNumber
     */
    public Matrix getDayNumberMatrix()
    {
        return Matrix.indicesToMatrix(dayNumber);
    }

    /**
     * @return the dayName
     */
    public ArrayList<String> getDayName()
    {
        return dayName;
    }

    /*
     * private Object[] weekday(Object calendarOrSecs) { String msg = ""; if
     * (calendarOrSecs == null) { msg =
     * "Parameter \"calendarOrSecs\"  value must be non-null."; throw new
     * ConditionalRuleException("weekday", msg); }
     * 
     * boolean tf = !(calendarOrSecs instanceof Double) && !(calendarOrSecs
     * instanceof GregorianCalendar); if (tf) { msg =
     * "Parameter \"calendarOrSecs\"  must be an instance of either \"Double\" or \"GregorianCalendar\"."
     * ; throw new ConditionalRuleException("weekday", msg); }
     * 
     * GregorianCalendar GC = null; if (calendarOrSecs instanceof Double) { GC =
     * Datevec.secsToCalendar(((Double) calendarOrSecs).doubleValue()); } else {
     * GC = (GregorianCalendar) calendarOrSecs; }
     * 
     * int weekDayNum = GC.get(GregorianCalendar.DAY_OF_WEEK); String dayNameStr
     * = days[weekDayNum - 1];
     * 
     * return new Object[]{weekDayNum, dayName}; }
     */
}
