package jamaextension.jamax;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

//import jamaextension.jamax.Cell;

public final class TextProcessing
{

    public static Calendar stringToDate(String startDateString, DateFormat df)
    {
        // String startDateString = "2013-03-26";
        // DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        /*
         * Date startDate = null; String newDateString = null; try { startDate =
         * df.parse(startDateString); newDateString = df.format(startDate);
         * System.out.println(startDate); } catch (ParseException e) {
         * e.printStackTrace(); }
         */

        Calendar cal = Calendar.getInstance();
        // SimpleDateFormat sdf = new
        // SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.EN_US);
        // cal.setTime(df.parse("Mon Mar 14 16:02:37 GMT 2011"));
        try
        {
            Date dd = df.parse(startDateString);
            cal.setTime(dd);
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // System.out.println(cal.toString());
        // cal.get(Calendar.DAY_OF_WEEK);

        return cal;
    }

    public static Collection<String> removeTermsInList(Collection<String> coll, List<String> termsToRemove)
    {
        int len = termsToRemove.size();

        for (int i = 0; i < len; i++)
        {
            String termR = termsToRemove.get(i);
            while (coll.contains(termR)) // if (coll.contains(termR))
            {
                coll.remove(termR);
            }
        }
        return coll;
    }

    public static List<String> removeTermsInList(List<String> termList, List<String> termsToRemove)
    {
        int len = termsToRemove.size();

        for (int i = 0; i < len; i++)
        {
            String termR = termsToRemove.get(i);
            termR = termR.trim();
            String termRL = termR.toLowerCase();
            while (termList.contains(termR))
            {
                termList.remove(termR);
            }
            while (termList.contains(termRL))
            {
                termList.remove(termRL);
            }
        }

        List<String> aList = new ArrayList<String>(termList);

        return aList;
    }

    public static String replaceStringInListFromString(String origStr, List<String> listOfRemovedStr)
    {
        return replaceStringInListFromString(origStr, listOfRemovedStr, "");
    }

    public static String replaceStringInListFromString(String origStr, List<String> listOfRemovedStr,
            String replacedWith)
    {
        String str = origStr.trim();
        if ("".equals(str))
        {
            return "";
        }

        int nrep = listOfRemovedStr.size();

        for (int i = 0; i < nrep; i++)
        {
            str = str.replace(listOfRemovedStr.get(i), replacedWith);
        }

        return str;
    }

    public static List<String> splitOn(String sent, String regSplit)
    {
        return splitOn(sent, regSplit, false);
    }

    public static List<String> splitOn(String sent, String regSplit, boolean toLower)
    {
        return splitOn(sent, regSplit, toLower, false);
    }

    public static String concatList(List<String> list, String sep, String[] remove)
    {
        // List<String> strList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if (sep == null || "".equals(sep))
        {
            sep = ",";
        }
        int siz = list.size();

        for (int i = 0; i < siz; i++)
        {
            String item = list.get(i).trim();
            if (item == null || "".equals(item))
            {
                continue;
            }

            if (remove != null)
            {
                item = TextProcessing.removeStrings(item, remove);
            }
            if (i != siz - 1)
            {
                sb = sb.append(item + sep);
            }
            else
            {
                sb = sb.append(item);
            }

        }

        String sbStr = sb.toString();

        return sbStr;
    }

    public static String concatStringListToString(List<String> list)
    {
        return concatStringListToString(list, " ");
    }

    public static String concatStringListToString(List<String> list, String sep)
    {
        // List<String> strList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if (sep == null || "".equals(sep))
        {
            sep = ",";
        }
        int siz = list.size();

        for (int i = 0; i < siz; i++)
        {
            String item = list.get(i).trim();
            if (item == null || "".equals(item))
            {
                continue;
            }

            if (i != siz - 1)
            {
                sb = sb.append(item + sep);
            }
            else
            {
                sb = sb.append(item);
            }

        }

        String sbStr = sb.toString();

        return sbStr;
    }

    public static List<String> splitOn(String sent, String regSplit, boolean toLower, boolean whiteSpaceToDash)
    {
        Set<String> set = new TreeSet<String>();
        if (regSplit == null || "".equals(regSplit))
        {
            regSplit = " ";
        }

        String[] sp = sent.split(regSplit);
        int len = sp.length;

        for (int i = 0; i < len; i++)
        {
            String spI = sp[i].trim();
            if ("".equals(spI))
            {
                continue;
            }
            if (toLower)
            {
                spI = spI.toLowerCase();
            }
            if (whiteSpaceToDash)
            {
                spI = spI.replace(" ", "-");
            }
            set.add(spI);
        }

        List<String> list = new ArrayList<String>(set);

        return list;
    }

    public static List<String> toUniqueStringList(String[] strArr)
    {
        List<String> list = toStringList(strArr);
        Set<String> set = new TreeSet<String>(list);
        list = new ArrayList<String>(set);
        return list;
    }

    public static List<String> toUniqueStringList(List<String> list)
    {
        // List<String> list = null;//toStringList(strArr);
        List<String> uniqueList = new ArrayList<String>();
        Set<String> set = new TreeSet<String>();
        if (list == null || list.isEmpty())
        {
            return uniqueList;
        }
        int len = list.size();
        for (int i = 0; i < len; i++)
        {
            String str = list.get(i);
            str = str.trim();
            set.add(str);
        }

        uniqueList = new ArrayList<String>(set);
        return uniqueList;
    }

    public static List<String> removeWordLength(List<String> list, int wordLen)
    {
        List<String> nList = new ArrayList<String>();
        int len = list.size();
        for (int i = 0; i < len; i++)
        {
            String word = list.get(i).trim();
            if (word.length() <= wordLen)
            {
                continue;
            }
            nList.add(word);
        }
        return nList;
    }

    public static boolean isNumeric(String str)
    {
        if (str == null || "".equals(str.trim()))
        {
            return false;
        }
        boolean tf = true;
        try
        {
            str = str.trim();
            Double.parseDouble(str);
        }
        catch (NumberFormatException nfe)
        {
            tf = false;
        }

        return tf;
    }

    public static List<String> toStringList(String[] strArr)
    {
        List<String> strList = new ArrayList<String>();
        if (strArr == null || strArr.length == 0)
        {
            strList.add("");
            return strList;
        }

        int len = strArr.length;
        for (int i = 0; i < len; i++)
        {
            String str = getAsciiCharOnly(strArr[i]).trim();
            if ("".equals(str))
            {
                strList.add("");
            }
            else
            {
                strList.add(str);
            }
        }

        return strList;
    }

    public static String getAsciiCharOnly(String str)
    {

        int stop = 0;
        String strAscii = str.replaceAll("[^\\p{ASCII}]", "");
        if (!str.equals(strAscii))
        {
            stop = 1;
        }
        /*
         * if (str == null) { return ""; } char[] charSet = str.toCharArray();
         * int len = charSet.length; StringBuilder builder = new
         * StringBuilder(); for (int i = 0; i < len; i++) { String str2 = "" +
         * charSet[i]; boolean bl = isPureAscii(str2); if (bl) { builder =
         * builder.append(charSet[i]); } }
         * 
         * strAscii = builder.toString();
         */
        return strAscii;
    }

    public static String fromListToString(List<String> listStr)
    {
        return fromListToString(listStr, true);

    }

    public static String[] fromStringListToStringArray(List<String> list)
    {
        String[] strArr = new String[list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            strArr[i] = list.get(i);
        }
        return strArr;
    }

    public static String fromListToString(List<String> listStr, boolean newLine)
    {
        StringBuilder builder = new StringBuilder();
        int num = listStr.size();
        String delim = " ";
        if (newLine)
        {
            delim = "\r\n";
        }

        if (listStr == null || listStr.isEmpty())
        {
            return "";
        }

        if (num == 1)
        {
            return listStr.get(0);
        }

        for (int i = 0; i < num; i++)
        {
            String str = listStr.get(i);
            // System.out.println((i + 1) + ") \"" + str + "\"");
            if (str == null)
            {
                continue;
            }
            str = str.trim();
            if ("".equals(str))
            {
                continue;
            }

            if (i != (num - 1))
            {
                builder = builder.append(str + delim);
            }
            else
            {
                builder = builder.append(str);
            }
        }

        String toStr = builder.toString();
        toStr = toStr.trim();

        return toStr;
    }

    public static String removeStrings(String str, String... strList)
    {
        int len = strList.length;
        String str2 = str + "";
        for (int i = 0; i < len; i++)
        {
            str2 = str2.replace(strList[i], "");
        }
        return str2;
    }

    public static String replaceWithWhiteSpace(String str, String... strList)
    {
        int len = strList.length;
        String str2 = str + "";
        for (int i = 0; i < len; i++)
        {
            String sc = strList[i];
            int stop = 0;
            boolean tf = false;
            if (sc.equals("'"))
            {
                stop = 0;
                tf = str2.contains("'");
            }

            str2 = str2.replace(strList[i], " ");
        }
        return str2;
    }

    public static List<String> cleanUpText(String str)
    {

        String ST = str.replace("[", "");
        ST = ST.replace("]", "");
        ST = ST.replace("'", " ");
        ST = ST.replace("\"", " ");
        ST = ST.replace(".", " ");

        String[] split = ST.split(",");

        int len = split.length;
        Set<String> set = new TreeSet<String>();

        for (int i = 0; i < len; i++)
        {
            String sp = split[i].trim();
            if (sp.length() == 1)
            {
                continue;
            }
            set.add(sp);
        }

        List<String> list = new ArrayList<String>(set);
        int siz = list.size();

        set = new TreeSet<String>();

        for (int v = 0; v < siz; v++)
        {

            String listStr = list.get(v);
            split = listStr.split(" ");

            len = split.length;

            for (int i = 0; i < len; i++)
            {
                String sp = split[i].trim();
                if (sp.length() == 1)
                {
                    continue;
                }
                set.add(sp);
            }
        }

        list = new ArrayList<String>(set);

        return list;
    }

    public static List<String> fromStringToUniqueWordList(String str)
    {
        return fromStringToUniqueWordList(str, true);
    }

    public static List<String> fromStringToUniqueWordList(String str, boolean uniqueWords)
    {
        String SS = str.trim();
        // Set<String> set = new TreeSet<>();
        List<String> accumuList = new ArrayList<String>();
        String[] split = SS.split(" ");
        int len = split.length;
        for (int i = 0; i < len; i++)
        {
            String SI = split[i];
            SI = SI.trim();
            if ("".equals(SI))
            {
                continue;
            }
            accumuList.add(SI);
        }
        List<String> list = null;
        if (uniqueWords)
        {
            Set<String> set = new TreeSet<String>(accumuList);
            list = new ArrayList<String>(set);
        }
        else
        {
            list = accumuList;
        }

        // List<String> list = uniqueList;

        return list;
    }

    public static boolean isAllCharNonAlphaNumeric(String str)
    {
        boolean tf = false;
        if (str == null || "".equals(str.trim()))
        {
            return tf;
        }

        str = str.trim();
        int len = str.length();
        int count = 0;

        for (int i = 0; i < len; i++)
        {
            char ch = str.charAt(i);
            boolean tf2 = !Character.isDigit(ch) && !Character.isLetter(ch);
            if (tf2)
            {
                count++;
            }
        }

        if (count == len)
        {
            tf = true;
        }

        return tf;
    }

    public static List<Object[]> toObjArray(Cell trainAnTestMat)
    {
        int siz = trainAnTestMat.getRowDimension();
        Object[][] objArr = trainAnTestMat.getArray();
        List<Object[]> objList = new ArrayList<Object[]>();
        for (int i = 0; i < siz; i++)
        {
            Object[] AA = objArr[i];
            objList.add(AA);
        }
        return objList;
    }

    public static Map<String, String> listToLowerCaseMap(List<String> list, boolean trim)
    {
        int siz = list.size();
        Map<String, String> map = new LinkedHashMap<String, String>();

        for (int i = 0; i < siz; i++)
        {
            String item = list.get(i);
            if (trim)
            {
                item = item.trim();
            }
            map.put(item.toLowerCase(), item);
        }

        return map;
    }

    public static Map<String, String> listToLowerCaseMap(List<String> list)
    {
        return listToLowerCaseMap(list, false);
    }

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
