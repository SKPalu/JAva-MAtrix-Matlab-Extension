/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.strfun;

import java.util.ArrayList;

import jamaextension.jamax.Indices;

/**
 * 
 * @author Sione
 */
public final class StrUtil
{

    private StrUtil()
    {
    }

    public static Indices isletter(String str)
    {
        if (str == null)
        {
            return null;
        }
        int len = str.length();
        Indices lett = new Indices(1, len);
        lett.setLogical(true);

        for (int i = 0; i < len; i++)
        {
            if (Character.isLetter(str.charAt(i)))
            {
                lett.set(0, i, Boolean.TRUE);
            }
        }

        return lett;
    }

    public static Indices stringEQ(String str, int charNum)
    {
        if (str == null || "".equals(str))
        {
            return null;
        }
        int len = str.length();
        Indices strEq = new Indices(1, len);
        strEq.setLogical(true);
        for (int i = 0; i < len; i++)
        {
            if ((int) str.charAt(i) == charNum)
            {
                strEq.set(0, i, Boolean.TRUE);
            }
        }
        return strEq;
    }

    public static Indices stringEQ(String str, char charNum)
    {
        return stringEQ(str, (int) charNum);
    }

    public static Indices stringLT(String str, int charNum)
    {
        if (str == null || "".equals(str))
        {
            return null;
        }
        int len = str.length();
        Indices strEq = new Indices(1, len);
        strEq.setLogical(true);
        for (int i = 0; i < len; i++)
        {
            if ((int) str.charAt(i) < charNum)
            {
                strEq.set(0, i, Boolean.TRUE);
            }
        }
        return strEq;
    }

    public static Indices stringLT(String str, char charNum)
    {
        return stringLT(str, (int) charNum);
    }

    public static Indices stringLTEQ(String str, int charNum)
    {
        if (str == null || "".equals(str))
        {
            return null;
        }
        int len = str.length();
        Indices strEq = new Indices(1, len);
        strEq.setLogical(true);
        for (int i = 0; i < len; i++)
        {
            if ((int) str.charAt(i) <= charNum)
            {
                strEq.set(0, i, Boolean.TRUE);
            }
        }
        return strEq;
    }

    public static Indices stringLTEQ(String str, char charNum)
    {
        return stringLTEQ(str, (int) charNum);
    }

    public static Indices stringGT(String str, int charNum)
    {
        if (str == null || "".equals(str))
        {
            return null;
        }
        int len = str.length();
        Indices strEq = new Indices(1, len);
        strEq.setLogical(true);
        for (int i = 0; i < len; i++)
        {
            if ((int) str.charAt(i) > charNum)
            {
                strEq.set(0, i, Boolean.TRUE);
            }
        }
        return strEq;
    }

    public static Indices stringGT(String str, char charNum)
    {
        return stringGT(str, (int) charNum);
    }

    public static Indices stringGTEQ(String str, int charNum)
    {
        if (str == null || "".equals(str))
        {
            return null;
        }
        int len = str.length();
        Indices strEq = new Indices(1, len);
        strEq.setLogical(true);
        for (int i = 0; i < len; i++)
        {
            if ((int) str.charAt(i) >= charNum)
            {
                strEq.set(0, i, Boolean.TRUE);
            }
        }
        return strEq;
    }

    public static Indices stringGTEQ(String str, char charNum)
    {
        return stringGTEQ(str, (int) charNum);
    }

    public static String repmat(String str, int row, int col)
    {
        StringBuilder rmat = new StringBuilder();

        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {

                if (j == (col - 1))
                {
                    rmat.append(str).append("\n");
                }
                else
                {
                    rmat.append(str).append("");
                }
            }
        }
        return rmat.toString();
    }

    public static String padwidth(int num)
    {
        StringBuilder sb = new StringBuilder();
        String space = " ";
        char chSp = space.charAt(0);
        for (int i = 0; i < num; i++)
        {
            sb.append(chSp);
        }
        return sb.toString();
    }

    public static String strvcat(ArrayList<String> list)
    {
        int siz = list.size();
        if (siz == 0)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < siz; i++)
        {
            String str = list.get(i);
            boolean cond = str == null || "".equals(str.trim());
            if (!cond)
            {
                sb.append(str).append("\n");
            }
        }

        return sb.toString();
    }
}
