package jamaextension.jamax.timefun;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public final class DateTimeUtil
{

    private DateTimeUtil()
    {
        // TODO Auto-generated constructor stub
    }

    public static String getCurrentDateTime()
    {
        GregorianCalendar GC = new GregorianCalendar();
        // int year = GC.getTime()
        // System.out.println(" GC = "+GC.getTime());
        String currentTime = GC.getTime().toString();
        currentTime = currentTime.replace(":", "-");
        currentTime = currentTime.replace(" ", "_");

        return currentTime;
    }

    public static List<GregorianCalendar> dateStringToCalendar(List<String> dateInString, SimpleDateFormat formatter)
    {
        List<GregorianCalendar> list = new ArrayList<GregorianCalendar>();
        if (dateInString == null || dateInString.isEmpty())
        {
            return list;
        }

        int siz = dateInString.size();
        for (int i = 0; i < siz; i++)
        {
            String dStr = dateInString.get(i);
            GregorianCalendar GC = dateStringToCalendar(dStr, formatter);
            list.add(GC);
        }

        return list;
    }

    public static GregorianCalendar dateStringToCalendar(String dateInString, SimpleDateFormat formatter)
    {
        Date date = null;
        try
        {
            date = formatter.parse(dateInString);
            // System.out.println(date);
            // System.out.println(formatter.format(date));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        return calendar;
    }

    public static List<Double> dateStringToDateNum(List<String> dateInString, SimpleDateFormat formatter)
    {

        List<Double> list = new ArrayList<Double>();
        if (dateInString == null || dateInString.isEmpty())
        {
            return list;
        }

        int siz = dateInString.size();
        GregorianCalendar calendar = null;

        for (int i = 0; i < siz; i++)
        {
            String dStr = dateInString.get(i);
            calendar = dateStringToCalendar(dStr, formatter);
            double dateNum = Datenum.toDatenum(calendar);
            list.add(dateNum);
        }

        return list;
    }

    public static double dateStringToDateNum(String dateInString, SimpleDateFormat formatter)
    {
        GregorianCalendar calendar = dateStringToCalendar(dateInString, formatter);

        double dateNum = Datenum.toDatenum(calendar);

        return dateNum;
    }

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
