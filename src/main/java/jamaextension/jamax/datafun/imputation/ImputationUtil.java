/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun.imputation;


import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.polyfun.Quick1DLinearInterp;
import jamaextension.jamax.stats.RandSample;

/**
 * 
 * @author sionep
 */
public final class ImputationUtil
{

    private ImputationUtil()
    {
    }

    public static Object[] cvImputePrelim(Matrix x, double testFraction)
    {
        int n = x.numel();// nrow(x) * ncol(x)
        Indices missingMatrix = x.isnan();// is.na(x)
        Indices validData = missingMatrix.NOT();// which(!missing.matrix)
        FindInd find = validData.findIJ();
        Indices validDataFind = find.getFindEntries();
        int validDataLen = validDataFind.length();
        int numSam = (int) (validDataLen * testFraction);

        // remove.indices = sample(valid.data, test.fraction*length(valid.data))
        RandSample sample = new RandSample(validDataFind, numSam);
        Indices removeIndices = (Indices) sample.getNumericSample();
        int[] remInd = removeIndices.getRowPackedCopy();

        Matrix xTrain = x.copy();
        // x.train[remove.indices] = NA
        xTrain.setElements(remInd, Double.NaN);

        /*
         * return (list(remove.indices = remove.indices, x.train = x.train))
         */
        return new Object[]
        {
                removeIndices, xTrain
        };
    }

    public static Object[] imputePrelim(Matrix x, boolean byrow)
    {
        return imputePrelim(x, byrow, false);
    }

    public static Object[] imputePrelim(Matrix x, boolean byrow, boolean verbose)
    {
        Indices missingMatrix = x.isnan();// is.na(x)
        int numMissing = JDatafun.sum(missingMatrix).start();
        if (verbose)
        {
            System.out.println("imputing on" + numMissing + "missing values with matrix size" + x.numel());
        }

        Indices missingRowsIndices = null;
        Indices missingColsIndices = null;
        Matrix xMissing = null;

        if (numMissing == 0)
        {
            /*
             * return ( list (missing.matrix = missing.matrix, numMissing =
             * numMissing, missing.rows.indices = NULL, missing.cols.indices =
             * NULL, x.missing = NULL) )
             */
            return new Object[]
            {
                    missingMatrix, numMissing, missingRowsIndices, missingColsIndices, xMissing
            };
        }

        // missing.rows.indices = which(apply(missing.matrix, 1, function(i) {
        // any(i) }))
        // missing.cols.indices = which(apply(missing.matrix, 2, function(i) {
        // any(i) }))
        if (byrow)
        {
            // x.missing = cbind(1:nrow(x),x)[missing.rows.indices,,drop=F]
        }
        else
        {
            // x.missing = rbind(1:ncol(x),x)[,missing.cols.indices,drop=F]
        }
        /*
         * return ( list (missing.matrix = missing.matrix, numMissing =
         * numMissing, missing.rows.indices = missing.rows.indices,
         * missing.cols.indices = missing.cols.indices, x.missing = x.missing) )
         */
        return new Object[]
        {};
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

        /*
         * Matrix slideNonNan = null;
         * 
         * 
         * while (from >= 0) { slideNonNan = X.getEls(from2, to); //linear
         * interpolate here from--; from2--; to--; }
         */
        temp = imputeMissingMiddle(temp);
        X.setElements(start, end, temp);

        return X;
    }

    static Matrix fillFront(Matrix nanMat, int win)
    {
        return fillFront(nanMat, win, true);
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

    static Matrix fillMiddleOld(Matrix nanMat)
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

        temp = imputeMissingMiddle(temp);
        X.setElements(from, to, temp);

        return X;
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

    static boolean testEnd(Matrix nanMat)
    {
        Matrix flip = null;
        if (nanMat.isColVector())
        {
            flip = nanMat.flipUD();
        }
        else
        {
            flip = nanMat.flipLR();
        }
        boolean tf = testFront(flip);
        return tf;
    }

    static boolean testFrontMiddleEnd(Matrix nanMat)
    {
        boolean tf = testFront(nanMat);
        boolean tf2 = testMiddle(nanMat);
        boolean tf3 = testEnd(nanMat);
        boolean tf4 = tf && tf2 && tf3;
        return tf4;
    }

    static boolean testFrontEnd(Matrix nanMat)
    {
        boolean tf = testFront(nanMat);
        boolean tf2 = testMiddle(nanMat);
        boolean tf3 = testEnd(nanMat);
        boolean tf4 = tf && !tf2 && tf3;
        return tf4;
    }

    static boolean testFrontMiddle(Matrix nanMat)
    {
        boolean tf = testFront(nanMat);
        boolean tf2 = testMiddle(nanMat);
        boolean tf3 = testEnd(nanMat);
        boolean tf4 = tf && tf2 && !tf3;
        return tf4;
    }

    static boolean testFront(Matrix nanMat)
    {
        boolean tf = false;

        int len = nanMat.length();

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
            if (!tfEnd)
            {
                // test the middle
                from = indFront + 1;
                to = len - 1;
                temp = nanMat.getEls(from, to);
                tf = temp.isnanBoolean();
                if (tf)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {// test the middle

                return false;

            }
        }

        return tf;
    }

    static boolean testMiddle(Matrix nanMat)
    {
        boolean tf = false;

        int len = nanMat.length();

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
            if (tfEnd)
            {
                from = indFront + 1;
                to = indEnd - 1;
                temp = nanMat.getEls(from, to);
                tf = temp.isnanBoolean();
            }
            else
            {// test the middle
                return false;
            }
        }
        else
        {
            if (tfEnd)
            {
                from = indFront + 1;
                to = indEnd - 1;
                temp = nanMat.getEls(from, to);
                tf = temp.isnanBoolean();
            }
            else
            {// test the middle
                return false;
            }
        }

        return tf;
    }

    static Matrix beginEndFit(Matrix XX, int win)
    {

        Matrix X = XX.copy();

        Object[] obj = missingAtFront(X);
        boolean tf = (Boolean) obj[0];
        int indEnd = (Integer) obj[1];

        Object[] obj2 = missingAtEnd(X);
        boolean tf2 = (Boolean) obj2[0];
        int indEnd2 = (Integer) obj2[1];

        int len = X.length();

        boolean tf3 = tf && tf2;
        if (tf3)
        {
            int a = indEnd + 1;
            int b = indEnd2 - 1;
            Matrix tmp = X.getEls(a, b);

            if (tmp.isnanBoolean())
            {
                tmp = imputeMissingMiddle(tmp);
                X.setElements(a, b, tmp);
            }
        }

        // X.printInLabel("X-step1");

        if (tf && !tf2)
        {
            int a = 0;
            int b = indEnd - 1;
            Matrix tmpNan = X.copy();// .getEls(a, b);
            int end2 = tmpNan.length() - 1;
            int c = indEnd + 1;
            int d = c + win - 1;
            if (d > end2)
            {
                throw new ConditionalException("beginEndFit : Window length leads to out-of-bounds.");
            }
            Matrix tmpNonNan = tmpNan.getEls(c, d);

            double meanVal = JDatafun.mean(tmpNonNan).start();
            tmpNan.set(0, 0, meanVal);

            if (indEnd != 0)
            {
                tmpNan = imputeMissingMiddle(tmpNan);
            }
            X = tmpNan;// .setElements(a, b, tmpNan);
        }

        // X.printInLabel("X-step2");

        if (!tf && tf2)
        {
            int a = indEnd + 1;
            int b = len - 1;
            Matrix tmpNan = X.getEls(a, b);
            int end2 = indEnd2 - 1;
            int c = end2 - win - 1;
            if (c < a)
            {
                throw new ConditionalException("beginEndFit : Window length leads to out-of-bounds.");
            }
            Matrix tmpNonNan = X.getEls(c, end2);

            double meanVal = JDatafun.mean(tmpNonNan).start();
            tmpNan.setEnd(meanVal);

            if (indEnd2 != b)
            {
                tmpNan = imputeMissingMiddle(tmpNan);
            }
            X.setElements(a, b, tmpNan);
        }

        // X.printInLabel("X-step3");

        return X;
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

    static Matrix imputeMissingMiddleOld(Matrix X)
    {

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

    public static Matrix splineAlong(Matrix nanData, int win)
    {
        boolean containNan = nanData.isnanBoolean();
        if (!containNan)
        {

            return nanData;
        }

        Matrix X = nanData.copy();

        Object[] obj = missingAtFront(X);
        boolean tf = (Boolean) obj[0];

        Object[] obj2 = missingAtEnd(X);
        boolean tf2 = (Boolean) obj2[0];

        // X.toColVector().printInLabel("X1");

        X = missingAtMiddle(X);
        X = beginEndFit(X, win);

        /*
         * boolean tf3 = !tf && !tf2; if (tf3) {// definitely missing in the
         * middle ONLY X = imputeMissingMiddle(X); } else {// can be missing at
         * the front, middle & end X = beginEndFit(X, win); }
         */

        return X;
    }

    public static Matrix testMatrix()
    {
        double[][] test =
        {
                {
                        10, 6, 5, 5, 9, 1, 4, 2
                },
                {
                        9, 3, 7, 3, 3, 9, 7, 8
                },
                {
                        5, 3, 5, 2, 1, 2, 1, 5
                },
                {
                        4, 3, 10, 0, 4, 2, 6, 3
                },
                {
                        9, 4, 9, 8, 8, 6, 2, 6
                },
                {
                        1, 6, 4, 10, 1, 9, 0, 9
                },
                {
                        9, 2, 7, 3, 7, 7, 0, 2
                },
                {
                        0, 2, 1, 0, 9, 3, 10, 8
                },
                {
                        9, 5, 2, 8, 8, 1, 4, 7
                },
                {
                        9, 3, 6, 1, 1, 7, 5, 8
                }
        };

        return new Matrix(test);
    }

}
