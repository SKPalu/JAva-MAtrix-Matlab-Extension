package jamaextension.jamax.datafun.imputation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;

public final class LinearSplineUtil
{

    private LinearSplineUtil()
    {
    }

    protected static LinearSplineType getType(Matrix nanData)
    {
        LinearSplineType type = null;

        if (!nanData.isVector())
        {
            throw new ConditionalException("getType : Data must be a vector and not a matrix.");
        }

        if (!nanData.isnanBoolean())
        {
            return LinearSplineType.NO_NAN;
        }

        int len = nanData.length();

        Indices nanInd = nanData.isnan();
        FindInd find = nanInd.findIJ();

        if (find.getIndex().length == len)
        {
            throw new ConditionalException("getType : Data is all missing.");
        }

        if (testFront(nanData))
        {
            type = LinearSplineType.FRONT_NAN;
        }
        else if (testEnd(nanData))
        {
            type = LinearSplineType.END_NAN;
        }
        else if (testMiddle(nanData))
        {
            type = LinearSplineType.MIDDLE_NAN;
        }
        else if (testFrontMiddle(nanData))
        {
            type = LinearSplineType.FRONT_MIDDLE_NAN;
        }
        else if (testMiddleEnd(nanData))
        {
            type = LinearSplineType.MIDDLE_END_NAN;
        }
        else if (testFrontEnd(nanData))
        {
            type = LinearSplineType.FRONT_END_NAN;
        }
        else if (testFrontMiddleEnd(nanData))
        {
            type = LinearSplineType.FRONT_MIDDLE_END_NAN;
        }
        else
        {
            throw new ConditionalException("getType : Missing data-type, not supported yet.");
        }

        return type;
    }

    static boolean testMiddleEnd(Matrix nanMat)
    {

        boolean tf1 = isFrontNan(nanMat);
        boolean tf2 = isMiddleNan(nanMat);
        boolean tf3 = isEndNan(nanMat);

        boolean tf = !tf1 && tf2 && tf3;

        /*
         * Matrix flip = null; if (nanMat.isColVector()) { flip =
         * nanMat.flipUD(); } else { flip = nanMat.flipLR(); } boolean tf =
         * testFrontMiddle(flip);
         */
        return tf;
    }

    static boolean testEnd(Matrix nanMat)
    {

        boolean tf1 = isFrontNan(nanMat);
        boolean tf2 = isMiddleNan(nanMat);
        boolean tf3 = isEndNan(nanMat);

        boolean tf = !tf1 && !tf2 && tf3;

        /*
         * Matrix flip = null; if (nanMat.isColVector()) { flip =
         * nanMat.flipUD(); } else { flip = nanMat.flipLR(); } boolean tf =
         * testFront(flip);
         */

        return tf;
    }

    static boolean testFront(Matrix nanMat)
    {

        boolean tf1 = isFrontNan(nanMat);
        boolean tf2 = isMiddleNan(nanMat);
        boolean tf3 = isEndNan(nanMat);

        boolean tf = tf1 && !tf2 && !tf3;

        /*
         * int len = nanMat.length();
         * 
         * Object[] obj = missingAtFront(nanMat); boolean tfFront = (Boolean)
         * obj[0]; int indFront = (Integer) obj[1];
         * 
         * obj = missingAtEnd(nanMat); boolean tfEnd = (Boolean) obj[0]; int
         * indEnd = (Integer) obj[1];
         * 
         * int from = 0; int to = 0; Matrix temp = null;
         * 
         * if (tfFront) { if (tfEnd) { return false; } else {// test the middle
         * from = indFront + 1; to = len - 1; temp = nanMat.getEls(from, to); tf
         * = temp.isnanBoolean(); if (tf) { return false; } } }
         */

        return tf;
    }

    static boolean testMiddle(Matrix nanMat)
    {

        boolean tf1 = isFrontNan(nanMat);
        boolean tf2 = isMiddleNan(nanMat);
        boolean tf3 = isEndNan(nanMat);

        boolean tf = !tf1 && tf2 && !tf3;

        /*
         * boolean tf = false;
         * 
         * int len = nanMat.length();
         * 
         * Object[] obj = missingAtFront(nanMat); boolean tfFront = (Boolean)
         * obj[0]; int indFront = (Integer) obj[1];
         * 
         * obj = missingAtEnd(nanMat); boolean tfEnd = (Boolean) obj[0]; int
         * indEnd = (Integer) obj[1];
         * 
         * int from = 0; int to = 0; Matrix temp = null;
         * 
         * if (tfFront) { if (tfEnd) { from = indFront + 1; to = indEnd - 1;
         * temp = nanMat.getEls(from, to); tf = temp.isnanBoolean(); } else {//
         * test the middle return false; } }
         */

        return tf;
    }

    static boolean testFrontMiddleEnd(Matrix nanMat)
    {
        boolean tf = isFrontNan(nanMat);
        boolean tf2 = isMiddleNan(nanMat);
        boolean tf3 = isEndNan(nanMat);
        boolean tf4 = tf && tf2 && tf3;
        return tf4;
    }

    static boolean testFrontEnd(Matrix nanMat)
    {
        boolean tf = isFrontNan(nanMat);
        boolean tf2 = isMiddleNan(nanMat);
        boolean tf3 = isEndNan(nanMat);
        boolean tf4 = tf && !tf2 && tf3;
        return tf4;
    }

    static boolean testFrontMiddle(Matrix nanMat)
    {
        boolean tf = isFrontNan(nanMat);
        boolean tf2 = isMiddleNan(nanMat);
        boolean tf3 = isEndNan(nanMat);
        boolean tf4 = tf && tf2 && !tf3;
        return tf4;
    }

    static Object[] missingAtFront(Matrix X)
    {
        boolean tf = false;

        Indices Xn = X.isnan();
        int len = Xn.length();

        tf = Xn.start() == 1;
        int lastNanInd = -1;

        for (int i = 0; i < len; i++)
        {
            int val = Xn.getElementAt(i);
            if (val == 1)
            {
                lastNanInd = i;
                continue;
            }
            else
            {
                break;
            }
        }

        return new Object[]
        {
                tf, lastNanInd
        };
    }

    static Object[] missingAtEnd(Matrix X)
    {
        boolean tf = false;

        Indices Xn = X.flipLR().isnan();
        int len = Xn.length();

        tf = Xn.start() == 1;
        int lastNanInd = -1;

        for (int i = 0; i < len; i++)
        {
            int val = Xn.getElementAt(i);
            if (val == 1)
            {
                lastNanInd = i;
                continue;
            }
            else
            {
                break;
            }
        }

        if (tf)
        {
            lastNanInd = len - 1 - lastNanInd;
        }
        return new Object[]
        {
                tf, lastNanInd
        };
    }

    static int firstInd(Indices ind)
    {
        int loc = 0;
        int len = ind.length();
        for (int i = 0; i < len; i++)
        {
            if (ind.getElementAt(i) == 1)
            {
                loc = i;
                break;
            }
        }
        if (loc == 0)
        {
            new ConditionalException("firstInd : Its should be missing data in the middle not beginning.");
        }
        return loc;
    }

    static int lastInd(Indices ind)
    {

        int len = ind.length();
        int loc = len - 1;
        Indices indRev = ind.flipLR();
        for (int i = 0; i < len; i++)
        {
            if (indRev.getElementAt(i) == 1)
            {
                loc = i;
                break;
            }
        }
        loc = len - 1 - loc;
        if (loc == (len - 1))
        {
            new ConditionalException("firstInd : Its should be missing data in the middle not ending.");
        }
        return loc;
    }

    static boolean isFrontNan(Matrix nanMat)
    {
        Object[] obj = missingAtFront(nanMat);
        boolean tfFront = (Boolean) obj[0];
        return tfFront;
    }

    static boolean isEndNan(Matrix nanMat)
    {
        Object[] obj = missingAtEnd(nanMat);
        boolean tfEnd = (Boolean) obj[0];
        return tfEnd;
    }

    static boolean isMiddleNan(Matrix nanMat)
    {
        boolean isNanFront = isFrontNan(nanMat);
        boolean isNanEnd = isEndNan(nanMat);

        boolean midNan = false;

        Object[] obj = null;
        boolean tfFront = false;
        int indFront = 0;
        boolean tfEnd = false;
        int indEnd = 0;
        int from = 0;
        int to = 0;
        Matrix middle = null;
        int len = nanMat.length();

        if (isNanFront)
        {

            if (isNanEnd)
            {
                obj = missingAtFront(nanMat);
                tfFront = (Boolean) obj[0];
                indFront = (Integer) obj[1];

                obj = missingAtEnd(nanMat);
                tfEnd = (Boolean) obj[0];
                indEnd = (Integer) obj[1];

                from = indFront + 1;
                to = indEnd - 1;
            }
            else
            {
                obj = missingAtFront(nanMat);
                tfFront = (Boolean) obj[0];
                indFront = (Integer) obj[1];
                from = indFront + 1;
                to = len - 1;
            }
            middle = nanMat.getEls(from, to);
            midNan = middle.isnanBoolean();

        }
        else
        {
            if (isNanEnd)
            {
                obj = missingAtEnd(nanMat);
                tfEnd = (Boolean) obj[0];
                indEnd = (Integer) obj[1];
                from = 0;
                to = indEnd - 1;
                middle = nanMat.getEls(from, to);
                midNan = middle.isnanBoolean();
            }
            else
            {
                midNan = nanMat.isnanBoolean();
            }
        }

        return midNan;

    }

    public static void main(String[] args)
    {
        // String nanFile = "C:\\Parrot\\Data\\TVNZ\\NZ_RawMissingData.txt";

        String resource = null;

        resource = "/NanData/NZ_RawMissingData.txt";// /jamaextension/src/main/resources/NanData

        URL res = LinearSplineUtil.class.getResource(resource);
        String urlStr = res.toString();

        String catalogNames = urlStr.replace("file:/", "");//

        String titleFile = catalogNames;// urlStr.replace("file:/", "");

        Matrix nanMat = null;

        try
        {
            nanMat = Matrix.read(titleFile);
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        nanMat.printInLabel("nanMat", 0);

        // int len = nanMat.length();
        int rows = nanMat.getRowDimension();
        int cols = nanMat.getColumnDimension();

        for (int i = 0; i < rows; i++)
        {
            Matrix col = nanMat.getColumnAt(i).toRowVector();
            LinearSplineType type = getType(col);
            col.printInLabel("col_" + i + "_" + type.toString());
        }

    }

}
