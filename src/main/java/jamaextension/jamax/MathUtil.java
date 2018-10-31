package jamaextension.jamax;

import jamaextension.jamax.constants.SortingMode;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public final class MathUtil
{

    public final static double EPS = Math.pow(2.0, -52.0);
    public final static double TWO_PI = 2.0 * Math.PI;

    private MathUtil()
    {
    }

    public static double fix(double a)
    {
        return a > 0.0 ? Math.floor(a) : Math.ceil(a);
    }

    public static double factorial(int N)
    {
        if (N < 0)
        {
            throw new ArrayIndexOutOfBoundsException("factorial : Parameter 'N' must be non-negative.");
        }
        else if (N == 0 || N == 1)
        {
            return 1.0;
        }

        double val = 1.0;
        for (int i = 1; i <= N; i++)
        {
            val = ((double) i) * val;
        }
        return val;
    }

    public static double hypot(double a, double b)
    {
        double r;
        if (Math.abs(a) > Math.abs(b))
        {
            r = b / a;
            r = Math.abs(a) * Math.sqrt(1 + r * r);
        }
        else if (b != 0)
        {
            r = a / b;
            r = Math.abs(b) * Math.sqrt(1 + r * r);
        }
        else
        {
            r = 0.0;
        }
        return r;
    }

    public static double randn()
    {
        double sum = 0.0;
        for (int i = 0; i < 12; i++)
        {
            sum += Math.random();
        }
        sum = sum - 6.0;
        return sum;
    }

    public static Complex[] flip(Complex[] a)
    {
        return flip(a, false);
    }

    public static Complex[] flip(Complex[] a, boolean copy)
    {
        Complex[] X;
        int len = a.length;
        X = new Complex[len];
        for (int i = 0; i < len; i++)
        {
            if (copy)
            {
                X[i] = a[(len - 1) - i].copy();
            }
            else
            {
                X[i] = a[(len - 1) - i];
            }
        }
        return X;
    }

    public static float[] flip(float[] a)
    {
        float[] X;
        int len = a.length;
        X = new float[len];
        for (int i = 0; i < len; i++)
        {
            X[i] = a[(len - 1) - i];
        }
        return X;
    }

    public static double[] flip(double[] a)
    {
        double[] X;
        int len = a.length;
        X = new double[len];
        for (int i = 0; i < len; i++)
        {
            X[i] = a[(len - 1) - i];
        }
        return X;
    }

    public static int[] flip(int[] a)
    {
        int[] X;
        int len = a.length;
        X = new int[len];
        for (int i = 0; i < len; i++)
        {
            X[i] = a[(len - 1) - i];
        }
        return X;
    }

    public static double sign(double a)
    {
        double val = 0.0;
        if (a < 0.0)
        {
            val = -1.0;
        }
        else if (a > 0.0)
        {
            val = 1.0;
        }
        return val;
    }

    public static int sign(int a)
    {
        int val = 0;
        if (a < 0)
        {
            val = -1;
        }
        else if (a > 0)
        {
            val = 1;
        }
        return val;
    }

    public static int mod(int x, int y)
    {
        if (y == 0)
        {
            return x;
        }
        if (x == 0 || x == y)
        {
            return 0;
        }

        int modVal = (int) rem((double) x, (double) y);
        if (sign(x) != sign(y))
        {
            modVal += y;
        }

        return modVal;
    }

    public static boolean equalsWithTol(double a, double b, double tol)
    {
        if (tol < 0.0)
        {
            throw new IllegalArgumentException("equalsWithTol : Parameter \"tol\" must be a positive number.");
        }

        boolean t = false;
        double signA = sign(a);
        double signB = sign(b);
        double diff = 0.0;
        if (signA * signB > 0.0)
        {
            if (signA > 0.0 && signB > 0.0)
            {
                diff = a - b;
            }
            else if (signA < 0.0 && signB < 0.0)
            {
                diff = Math.abs(a) - Math.abs(b);
            }
            diff = Math.abs(diff);
            if (diff <= Math.abs(tol))
            {
                t = true;
            }
        }
        else if (signA * signB < 0.0)
        {
            if (a < 0.0)
            {
                if (Math.abs(b) <= Math.abs(tol))
                {
                    t = true;
                }
            }
            else if (b < 0.0)
            {
                if (Math.abs(a) <= Math.abs(tol))
                {
                    t = true;
                }
            }
        }
        else
        {
            if (a == 0.0)
            {
                if (Math.abs(b) <= Math.abs(tol))
                {
                    t = true;
                }
            }
            else if (b == 0.0)
            {
                if (Math.abs(a) <= Math.abs(tol))
                {
                    t = true;
                }
            }
        }

        return t;
    }

    public static boolean compareDoubles(double A1, double A2)
    {
        return compareDoubles(A1, A2, null);
    }

    public static boolean compareDoubles(double A1, double A2, SortingMode mode)
    {
        boolean nanA1 = Double.isNaN(A1);
        boolean nanA2 = Double.isNaN(A2);
        boolean tf = false;
        if (mode == null || mode == SortingMode.ASCENDING)
        {
            if (nanA1 && !nanA2)
            {
                // tf = true; //--> original line (11/07/2009)
                tf = false;
            }
            else if (!nanA1 && nanA2)
            {
                // tf = false; //--> original line (11/07/2009)
                tf = true;
            }
            else
            {
                tf = A1 >= A2;
            }
            // System.out.println("\n  ASCENDING" );
        }
        else if (mode == SortingMode.DESCENDING)
        {
            if (nanA1 && !nanA2)
            {
                tf = false; // --> original line (11/07/2009)
                // tf = true;
            }
            else if (!nanA1 && nanA2)
            {
                tf = true; // --> original line (11/07/2009)
                // tf = false;
            }
            else
            {
                tf = A1 <= A2;
            }
            // System.out.println("\n  DESCENDING" );
        }
        else
        {
            throw new IllegalArgumentException(" compare :  Unknown \"SortingMode\" ( = " + mode.toString() + ").");
        }
        return tf;
    }

    public static Indices permuteToIndices(int[] permute)
    {
        int len = permute.length;
        int index = 0;
        Indices permuteIndices = new Indices(len, len);
        for (int i = 0; i < len; i++)
        {
            index = permute[i];
            permuteIndices.set(index, i, 1);
        }
        return permuteIndices;
    }

    public static double rem(double x, double y)
    {
        double rval = 1.0;
        if (y != 0.0d)
        {
            rval = x - y * fix(x / y);
        }
        else
        {
            rval = Double.NaN;
        }
        return rval;
    }

    public static boolean nanOrInf(double[] num)
    {
        int len = num.length;
        double val = 0.0;
        for (int i = 0; i < len; i++)
        {
            if (Double.isNaN(num[i]) || Double.isInfinite(num[i]))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean nanOrInf(double num)
    {
        if (Double.isNaN(num) || Double.isInfinite(num))
        {
            return true;
        }
        return false;
    }

    public static boolean positiveFinite(double num)
    {
        boolean cond = nanOrInf(num);
        if (cond)
        {
            return false;
        }
        return num > 0.0;
    }

    public static boolean nonNegativeFinite(double num)
    {
        boolean cond = nanOrInf(num);
        if (cond)
        {
            return false;
        }
        return num >= 0.0;
    }

    public static boolean isfinite(double num)
    {
        boolean tf = true;
        if (nanOrInf(num))
        {
            tf = false;
        }
        return tf;
    }

    public static boolean nanOrInf(double num, double num2)
    {
        if (Double.isNaN(num) || Double.isInfinite(num) || Double.isNaN(num2) || Double.isInfinite(num2))
        {
            return true;
        }
        return false;
    }

    public static boolean nan(double num, double num2)
    {
        if (Double.isNaN(num) || Double.isNaN(num2))
        {
            return true;
        }
        return false;
    }

    public static boolean nan(double[] num)
    {
        int len = num.length;
        double val = 0.0;
        for (int i = 0; i < len; i++)
        {
            if (Double.isNaN(num[i]))
            {
                return true;
            }
        }
        return false;
    }

    public static double logN(double number, double base)
    {
        double b = 1.0;
        double result = 0.0;
        /*
         * double[][] temp = matrix.getArray(); int row =
         * matrix.getRowDimension(); int col = matrix.getColumnDimension();
         * double[][] result = new double[row][col];
         */
        if (base <= 0)
        {
            throw new IllegalArgumentException(
                    "logN : Negative or zero base result in a Complex Number or negative Infinity.");
        }

        b = Math.log(base);
        // for(int i=0; i < row ; i++){
        // for(int j=0; j < col ; j++){
        if (number == 0.0)
        {
            result = Double.NEGATIVE_INFINITY;
        }
        else if (number < 0.0)
        {
            result = Double.NaN;
        }
        else
        {
            result = Math.log(number) / b;
        }
        // }//end for
        // }//end for
        return result;
    }// end method

    public static double log2(double number)
    {
        return logN(number, 2.0);
    }

    public static String genStringChars(char ch, int num)
    {
        if (num < 1)
        {
            num = 1;
        }
        StringBuilder strbuf = new StringBuilder();

        for (int i = 0; i < num; i++)
        {
            strbuf.append("").append(ch);
        }

        return strbuf.toString();
    }

    public static String replaceChar(String str, char oldChar, char newChar)
    {
        if (str == null)
        {
            return null;
        }
        int len = str.length();
        char[] ch = str.toCharArray();
        StringBuilder strBuf = new StringBuilder();
        for (int i = 0; i < len; i++)
        {
            if (ch[i] != oldChar)
            {
                strBuf.append(ch[i]);
            }
            else
            {
                strBuf.append(newChar);
            }
        }
        return strBuf.toString();
    }

    public static String deSpace(String str)
    {
        if (str == null)
        {
            return null;
        }
        int len = str.length();
        char[] ch = str.toCharArray();
        StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i < len; i++)
        {
            if (((int) ch[i]) != 32)
            {
                strBuf.append(ch[i]);
            }
        }
        return strBuf.toString();
    }

    public static boolean isArray(Object obj)
    {
        boolean tf = (obj instanceof Matrix) || (obj instanceof Matrix3D) || (obj instanceof Indices)
                || (obj instanceof Indices3D);
        return tf;
    }

    public static boolean isNumeric(Object obj)
    {
        boolean tf = false;
        if (obj == null)
        {
            return tf;
        }
        tf = (obj instanceof Matrix) || (obj instanceof Matrix3D)
                || ((obj instanceof Indices) && (((Indices) obj).isLogical())) || (obj instanceof Indices3D)
                || (obj instanceof Number);
        return tf;
    }

    public static int length(Object obj)
    {
        boolean tf = !(obj instanceof Matrix) && !(obj instanceof Matrix3D) && !(obj instanceof Indices)
                && !(obj instanceof Indices3D);
        if (tf)
        {
            throw new ConditionalException(
                    "length : Parameter \"obj\" must be either an instance of \"Matrix\", \"Matrix3D\", \"Indices\" or\"Indices3D\".");
        }
        int len = 0;
        if (obj instanceof Matrix)
        {
            len = ((Matrix) obj).length();
        }
        else if (obj instanceof Matrix3D)
        {
            len = ((Matrix3D) obj).length();
        }
        else if (obj instanceof Indices)
        {
            len = ((Indices) obj).length();
        }
        else if (obj instanceof Indices3D)
        {
            len = ((Indices3D) obj).length();
        }

        return len;
    }

    // public static double[] copy()
    public static void main(String[] args)
    {

        System.out.println("\n p = " + log2(6.0));

    }
}// --------------------------------- End Class Definition
// ----------------------

