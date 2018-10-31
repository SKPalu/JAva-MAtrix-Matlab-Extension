package jamaextension.jamax.datafun.imputation;

 
import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.polyfun.Quick1DLinearInterp;

public class MoveAverageImputation extends AbstractImputation
{

    public MoveAverageImputation(Matrix data)
    {
        super(data);
        this.imputation = ImputationType.MOVAVERAGE;
    }

    public MoveAverageImputation(Matrix data, Dimension dim)
    {
        super(data, dim);
        this.imputation = ImputationType.MOVAVERAGE;
    }

    public void impute()
    {
        Matrix Y = this.rawData;
        if (!this.containMissing)
        {
            this.imputedData = Y;
            this.imputatedDone = true;
            return;
        }

        int win = 0;

        Integer kVal = (Integer) this.params.getKey("window");

        if (kVal != null)
        {
            win = kVal.intValue();
        }
        if (win < 2)
        {
            win = 2;
        }

        int row = Y.getRowDimension();
        int col = Y.getColumnDimension();
        Matrix imputedX = new Matrix(row, col);

        if (this.rowCol == Dimension.COL)
        {
            /*
             * boolean test = true; if (test) { throw new
             * ConditionalException("impute : Not yet implemented."); }
             */

            for (int i = 0; i < row; i++)
            {
                Matrix X = Y.getRowAt(i);
                // X.printInLabel("Xstart_" + i, 0);
                X = fillAll(X, win);
                if (X.isColVector())
                {
                    X = X.toRowVector();
                }
                // X.printInLabel("Xfill_" + i);
                imputedX.setRowAt(i, X);
            }
        }
        else
        {
            for (int i = 0; i < col; i++)
            {
                Matrix X = Y.getColumnAt(i).toRowVector();
                // X.printInLabel("Xstart_" + i, 0);

                /*
                 * if (i == 24) { System.out.println("i = " + i); }
                 */

                X = fillAll(X, win);
                if (X.isRowVector())
                {
                    X = X.toColVector();
                }
                // X.printInLabel("Xfill_" + i, 0);
                imputedX.setColumnAt(i, X);
            }
        }

        this.imputedData = imputedX;
        this.imputatedDone = true;
    }

    static Matrix fillAll(Matrix nanMat, int win)
    {

        LinearSplineType type = LinearSplineUtil.getType(nanMat);
        // NO_NAN, FRONT_NAN, MIDDLE_NAN, END_NAN, FRONT_MIDDLE_NAN,
        // MIDDLE_END_NAN, FRONT_END_NAN , FRONT_MIDDLE_END_NAN
        Matrix X = nanMat;
        switch (type)
        {
        case NO_NAN:
        {// do nothing here as there's no missing data
            break;
        }
        case FRONT_NAN:
        {
            X = fillFront(X, win);
            break;
        }
        case MIDDLE_NAN:
        {
            X = fillMiddle(X);
            break;
        }
        case END_NAN:
        {
            X = fillEnd(X, win);
            break;
        }
        case FRONT_MIDDLE_NAN:
        { // fill-middle should always be completed first because the window of
          // non-missing values
          // to fill the front or end depends on the middle being filled first
            X = fillMiddle(X);
            X = fillFront(X, win);
            break;
        }
        case MIDDLE_END_NAN:
        { // fill-middle should always be completed first because the window of
          // non-missing values
          // to fill the front or end depends on the middle being filled first
            X = fillMiddle(X);
            X = fillEnd(X, win);
            break;
        }
        case FRONT_END_NAN:
        {
            X = fillFront(X, win);
            X = fillEnd(X, win);
            break;
        }
        case FRONT_MIDDLE_END_NAN:
        {// fill-middle should always be completed first because the window of
         // non-missing values
         // to fill the front or end depends on the middle being filled first
            X = fillMiddle(X);

            X = fillFront(X, win);
            X = fillEnd(X, win);
            break;
        }
        default:
        {
            throw new ConditionalException("fillAll : Missing data-type is unknown or not yet implemented.");
        }
        }

        return X;
    }

    static Matrix fillFront(Matrix nanMat, int win, boolean front)
    {
        int len = nanMat.length();

        Object[] obj = missingAtFront(nanMat);
        boolean tfFront = (Boolean) obj[0];
        int indFront = (Integer) obj[1];

        obj = missingAtEnd(nanMat);
        boolean tfEnd = (Boolean) obj[0];
        int indEnd = (Integer) obj[1];

        if (!tfFront)
        {
            new ConditionalException((front ? "fillFront" : "fillEnd") + " : There should be missing data at "
                    + (front ? "front" : "end") + ".");
        }

        Matrix X = nanMat.copy();
        Matrix X2 = nanMat.copy(); // for moving average

        int start = 0;
        int end = 0;

        if (tfEnd)
        {
            end = indEnd - 1;
        }
        else
        {
            end = len - 1;
        }

        Matrix temp = X.getEls(start, end);

        int from = 0;
        int from2 = 0;
        int to = 0;

        from = indFront;
        from2 = from + 1;
        to = from + win;

        Matrix winMat = temp.getEls(from2, to);
        double val = JDatafun.mean(winMat).start();
        temp.set(0, 0, val);

        if (from == 0)
        {
            X.setElements(start, end, temp);
            return X;
        }

        temp = imputeMissingMiddle(temp);
        X.setElements(start, end, temp);

        return X;
    }

    static Matrix fillMiddle(Matrix nanMat)
    {
        Matrix X = nanMat;
        int len = X.length();

        Object[] obj = missingAtFront(nanMat);
        boolean tfFront = (Boolean) obj[0];
        int indFront = (Integer) obj[1];

        obj = missingAtEnd(nanMat);
        boolean tfEnd = (Boolean) obj[0];
        int indEnd = (Integer) obj[1];

        int from = 0;
        int to = 0;
        Matrix temp = null;

        if (tfFront)
        {
            from = indFront + 1;
            if (tfEnd)
            {
                to = indEnd - 1;
            }
            else
            {
                to = len - 1;
            }
            temp = X.getEls(from, to);
        }
        else
        {
            from = 0;
            if (tfEnd)
            {
                to = indEnd - 1;
                temp = X.getEls(from, to);
            }
            else
            {
                temp = X;
                to = len - 1;
            }
        }

        if (!temp.isnanBoolean())
        {
            return X;
        }

        temp = interpolateMissingMiddle(temp);// imputeMissingMiddle(temp);
        X.setElements(from, to, temp);

        return X;
    }

    static Matrix fillFront(Matrix nanMat, int win)
    {
        return fillFront(nanMat, win, true);
    }

    static Matrix fillEnd(Matrix nanMat, int win)
    {
        Matrix X = null;
        boolean tfRow = false;
        if (nanMat.isColVector())
        {
            X = nanMat.flipUD();
        }
        else
        {
            X = nanMat.flipLR();
            tfRow = true;
        }

        X = fillFront(X, win, false);

        // flip it back
        if (tfRow)
        {
            X = X.flipLR();
        }
        else
        {
            X.flipUD();
        }

        return X;
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

    static Matrix missingAtMiddle(Matrix X)
    {
        Matrix xc = X.copy();
        if (!xc.isnanBoolean())
        {
            return xc;
        }
        int len = xc.length();

        Object[] obj = missingAtFront(xc);
        boolean tfFront = (Boolean) obj[0];
        int indFront = (Integer) obj[1];

        obj = missingAtEnd(xc);
        boolean tfEnd = (Boolean) obj[0];
        int indEnd = (Integer) obj[1];

        boolean missMid = false;
        Matrix middle = null;
        int from = 0;
        int to = 0;

        if (tfFront && tfEnd)
        {
            from = indFront + 1;
            to = indEnd - 1;
            middle = xc.getEls(from, to);
            missMid = middle.isnanBoolean();
            if (missMid)
            {
                middle = imputeMissingMiddle(middle);
                xc.setElements(from, to, middle);
            }
        }
        else if (tfFront && !tfEnd)
        {
            from = indFront + 1;
            to = len - 1;
            middle = xc.getEls(from, to);
            missMid = middle.isnanBoolean();
            if (missMid)
            {
                middle = imputeMissingMiddle(middle);
                xc.setElements(from, to, middle);
            }
        }
        else if (!tfFront && tfEnd)
        {
            from = 0;
            to = indEnd - 1;
            middle = xc.getEls(from, to);
            missMid = middle.isnanBoolean();
            if (missMid)
            {
                middle = imputeMissingMiddle(middle);
                xc.setElements(from, to, middle);
            }
        }
        else
        {
            middle = xc;// .getEls(from, to);
            missMid = middle.isnanBoolean();
            if (missMid)
            {
                middle = imputeMissingMiddle(middle);
                xc = middle;// .setElements(from, to, middle);
            }
        }

        return xc;
    }

    static Matrix imputeMissingMiddle(Matrix X)
    {

        // X.printInLabel("X");
        int[] firstLast = firstAndLastMiddleNonNan(X);
        int start = firstLast[0] - 1;
        int end = firstLast[1] + 1;
        Matrix middle = X.getEls(start, end);
        int count = 1;
        // middle.printInLabel("middle_0");

        while (middle.isnanBoolean())
        {
            int[] arr = middleFirstAndNextInstance(middle);
            int st = arr[0] - 1;
            int en = arr[1] + 1;
            double A = middle.getElementAt(st);
            double B = middle.getElementAt(en);
            Indices indind = Indices.linspace(st, en);
            Matrix XI = null;
            double[] xintp = null;
            Matrix XXX = null;
            double[] yintp = null;

            yintp = new double[]
            {
                    A, B
            };
            Matrix YYY = new Matrix(yintp);
            Matrix interp = null;

             
                xintp = new double[]
                {
                        1, indind.length()
                };
                XI = Matrix.linspace(2.0, indind.length() - 1, indind.length() - 2);
                XXX = new Matrix(xintp);
                Quick1DLinearInterp QL = new Quick1DLinearInterp(XXX, YYY, XI);
                interp = QL.getInterpValues();
            

            middle.setElements(st + 1, en - 1, interp);
            // middle.printInLabel("middle_" + count);
            count++;
        }

        // test again if NaN still exist, which shouldn't be
        boolean test = middle.isnanBoolean();
        if (test)
        {
            throw new ConditionalException("imputeMissingMiddle : NaN still exists.");
        }

        X.setElements(start, end, middle);

        return X;
    }

    static Matrix interpolateMissingMiddle(Matrix nanMat)
    {
        Object[] obj = missingAtFront(nanMat);
        boolean tfFront = (Boolean) obj[0];
        // int indFront = (Integer) obj[1];

        obj = missingAtEnd(nanMat);
        boolean tfEnd = (Boolean) obj[0];
        // int indEnd = (Integer) obj[1];

        if (tfFront || tfEnd)
        {
            throw new ConditionalException("interpolateMissingMiddle :  This doesn't apply to missing at front & end");
        }

        int count = 0;
        Matrix middle = nanMat.copy();

        while (middle.isnanBoolean())
        {
            int[] arr = middleFirstAndNextInstance(middle);
            int st = arr[0] - 1;
            int en = arr[1] + 1;
            double A = middle.getElementAt(st);
            double B = middle.getElementAt(en);
            Indices indind = Indices.linspace(st, en);
            Matrix XI = null;
            double[] xintp = null;
            Matrix XXX = null;
            double[] yintp = null;

            yintp = new double[]
            {
                    A, B
            };
            Matrix YYY = new Matrix(yintp);
            Matrix interp = null;

         
                xintp = new double[]
                {
                        1, indind.length()
                };
                XI = Matrix.linspace(2.0, indind.length() - 1, indind.length() - 2);
                XXX = new Matrix(xintp);
                Quick1DLinearInterp QL = new Quick1DLinearInterp(XXX, YYY, XI);
                interp = QL.getInterpValues();
            

            middle.setElements(st + 1, en - 1, interp);
            // middle.printInLabel("middle_" + count);
            count++;
        }

        // test again if NaN still exist, which shouldn't be
        boolean test = middle.isnanBoolean();
        if (test)
        {
            throw new ConditionalException("imputeMissingMiddle : NaN still exists.");
        }

        return middle;
    }

    static Matrix movAverageMissingMiddle(Matrix nanMat, int win)
    {
        Object[] obj = missingAtFront(nanMat);
        boolean tfFront = (Boolean) obj[0];
        // int indFront = (Integer) obj[1];

        obj = missingAtEnd(nanMat);
        boolean tfEnd = (Boolean) obj[0];
        // int indEnd = (Integer) obj[1];

        if (tfFront || tfEnd)
        {
            throw new ConditionalException("interpolateMissingMiddle :  This doesn't apply to missing at front & end");
        }

        int count = 0;
        Matrix middle = nanMat.copy();

        int winFrontInd = win - 1;
        int winEndStartInd = 0;
        int winEndInd = 0;

        while (middle.isnanBoolean())
        {
            int[] arr = middleFirstAndNextInstance(middle);
            int st = arr[0] - 1;
            int en = arr[1] + 1;
            winEndInd = arr[1] + win;
            winEndStartInd = en;

            int[] endArr = Indices.linspace(winEndStartInd, winEndInd).getRowPackedCopy();
            int lenEndArr = endArr.length;
            int lastEndInd = endArr[lenEndArr - 1];

            // boolean hasWinEnd = lastEndInd == middle
            // Matrix movWinEnd = middle.getEls(endArr);

            double A = middle.getElementAt(st);
            double B = middle.getElementAt(en);
            Indices indind = Indices.linspace(st, en);
            Matrix XI = null;
            double[] xintp = null;
            Matrix XXX = null;
            double[] yintp = null;

            /*
             * yintp = new double[] { A, B }; Matrix YYY = new Matrix(yintp);
             */

            Matrix aver = null;

            if (arr[0] == arr[1])
            {
                /*
                 * xintp = new double[] { 1, 3 }; LinearInterpolator LI = new
                 * LinearInterpolator(); PolynomialSplineFunction PSF =
                 * LI.interpolate(xintp, yintp);
                 */

                double val = (A + B) / 2;// PSF.value(2);
                aver = new Matrix(1, 1, val);
            }
            else
            {
                /*
                 * xintp = new double[] { 1, indind.length() }; XI =
                 * Matrix.linspace(2.0, indind.length() - 1, indind.length() -
                 * 2); XXX = new Matrix(xintp); Quick1DLinearInterp QL = new
                 * Quick1DLinearInterp(XXX, YYY, XI); aver =
                 * QL.getInterpValues();
                 */
            }

            middle.setElements(st + 1, en - 1, aver);
            // middle.printInLabel("middle_" + count);
            count++;
        }

        // test again if NaN still exist, which shouldn't be
        boolean test = middle.isnanBoolean();
        if (test)
        {
            throw new ConditionalException("imputeMissingMiddle : NaN still exists.");
        }

        return middle;
    }

    static int[] firstAndLastMiddleNonNan(Matrix X)
    {
        Indices nan = X.isnan();
        int first = firstInd(nan);
        int last = lastInd(nan);

        int[] ind = new int[]
        {
                first, last
        };

        return ind;
    }

    static int[] middleFirstAndNextInstance(Matrix nanMat)
    {
        Indices nan = nanMat.isnan();
        int first = firstInd(nan);
        int count = first;
        while (nan.getElementAt(count) == 1)
        {
            count++;
        }
        count--;
        int[] indd = new int[]
        {
                first, count
        };
        return indd;
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

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
