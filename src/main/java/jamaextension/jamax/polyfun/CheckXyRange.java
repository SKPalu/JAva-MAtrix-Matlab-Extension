/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.polyfun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortMat;

/**
 * 
 * @author Feynman Perceptrons
 */
class CheckXyRange
{

    private Matrix endslopes;
    private Matrix X;
    private Matrix Y;

    public CheckXyRange(Matrix XX, Matrix YY)
    {
        if (XX == null)
        {
            throw new IllegalArgumentException("CheckXyRange : Parameter \"XX\" must be non-null.");
        }
        if (!XX.isVector())
        {
            throw new IllegalArgumentException("CheckXyRange : Parameter \"XX\" must be a vector and not a matrix.");
        }
        boolean sortX = XX.isSorted();
        if (XX.isColVector())
        {
            XX = XX.toRowVector();
            XX.setSorted(sortX);
        }

        if (YY == null)
        {
            throw new IllegalArgumentException("CheckXyRange : Parameter \"YY\" must be non-null.");
        }
        if (!YY.isVector())
        {
            throw new IllegalArgumentException("CheckXyRange : Parameter \"YY\" must be a vector and not a matrix.");
        }
        if (YY.isColVector())
        {
            YY = YY.toRowVector();
        }

        /*
         * if(XX.length()!=YY.length()){ throw new IllegalArgumentException(
         * "CheckXyRange : Vectors \"XX\" and \"YY\" must have the same length."
         * ); }
         */

        // System.out.println("--------------------- X1 ---------------------");
        // XX.print(8,0);

        int yn = YY.length();
        int n = XX.length();
        int nstart = n; // original number of elements before NaNs is purged.

        // deal with NaN's among the sites:
        Indices nanx = XX.isnan().find();// find(isnan(x));
        int[] arr = null;
        if (nanx != null)
        {
            // System.out.println("--------------------- nanx ---------------------");
            // nanx.print(8,0);

            arr = nanx.getColIndicesAt(1);
            if (arr.length == n)
            {
                throw new IllegalArgumentException("CheckXyRange : All elements of vector \"XX\" are found to be NaNs.");
            }
            else
            {
                System.out.println("CheckXyRange : All data points with NaN as their site will be ignored.");
            }
            XX = XX.removeColsAt(arr);// XX.getElements(arr).toRowVector();
            if (XX != null)
            {
                XX.setSorted(sortX);
            }
        }

        // System.out.println("--------------------- X2 ---------------------");
        // XX.print(8,0);

        n = XX.length(); // re-assign since NaNs could have been removed.

        if (n < 2)
        {
            throw new IllegalArgumentException(
                    "CheckXyRange : There must be at least 2 data points in vector \"XX\" that are non NaNs.");
        }

        Matrix dx = JDatafun.diff(XX);

        // if(!XX.isUnique()){
        // throw new
        // IllegalArgumentException("CheckXyRange : All elements in vector \"XX\" must be unique.");
        // }

        Indices ind = null;
        if (dx.LT(0.0).anyBoolean())
        {// the 2 conditions must be included since 'sortX' might be true, but
         // was forgotten to manually set in method call
         // System.out.println("CheckXyRange : BLOCK #1 ");
            QuickSort sortMat = new QuickSortMat(XX, true, false);
            XX = (Matrix) sortMat.getSortedObject();
            ind = sortMat.getIndices();
            dx = JDatafun.diff(XX);
        }
        else
        {
            // System.out.println("CheckXyRange : BLOCK #2 ");
            ind = Indices.intLinspaceIncrement(0, n - 1);
        }

        if (dx.EQ(0.0).find() != null)
        {
            throw new IllegalArgumentException("CheckXyRange : All elements in vector \"XX\" must be unique.");
        }

        if (yn == nstart)
        {
            endslopes = null; // System.out.println("CheckXyRange : BLOCK #3 ");
        }
        else if (yn == (nstart + 2))
        {
            arr = new int[]
            {
                    0, n + 1
            };// [1 n+2]
            endslopes = YY.getElements(arr).toRowVector();// y(:,[1 n+2]);
            // System.out.println("CheckXyRange : BLOCK #4 ");
            YY = YY.removeColsAt(arr);// y(:,[1 n+2])=[];
            if (endslopes.isnan().anyBoolean())
            {
                throw new IllegalArgumentException("CheckXyRange : The endslopes cannot be NaN.");
            }
            if (endslopes.isinf().anyBoolean())
            {
                throw new IllegalArgumentException("CheckXyRange : The endslopes cannot be Inf.");
            }
        }
        else
        {
            throw new IllegalArgumentException("CheckXyRange : The number of sites = " + nstart
                    + " is incompatible with the number of values = " + yn + " :");
        }

        if (nanx != null)
        { // System.out.println("CheckXyRange : BLOCK #5 ");
            arr = nanx.getColIndicesAt(1);
            YY = YY.removeColsAt(arr);
        }

        arr = ind.getRowPackedCopy();
        YY = YY.getElements(arr).toRowVector();

        // nanx = find(isnan(x));
        Indices nany = YY.isnan().find();
        if (nany != null)
        { // System.out.println("CheckXyRange : BLOCK #6 ");
            arr = nany.getColIndicesAt(1);
            YY = YY.removeColsAt(arr);
            XX = XX.removeColsAt(arr);
            System.out.println("CheckXyRange : All data points with NaN in their value will be removed.");
            n = XX.length();
            if (YY == null || n < 2)
            {
                throw new IllegalArgumentException(
                        "CheckXyRange : There must be at least 2 points which don't that are non NaNs.");
            }
        }

        this.X = XX;
        this.Y = YY;
    }

    public Matrix getX()
    {
        return X;
    }

    public Matrix getY()
    {
        return Y;
    }

    public Matrix getEndslopes()
    {
        return endslopes;
    }

    public int getSizeY()
    {
        return 1;
    }

    public static void main(String[] args)
    {
        Matrix XX = new Matrix(new double[]
        {
                7, 14, Double.NaN, 6, 13, 8, 3, 10
        });
        Matrix YY = new Matrix(new double[]
        {
                3.2849, 4.9530, 3.2849, -1.3971, Double.NaN, 4.9468, 0.7056, -2.7201
        });
        Matrix XXYY = XX.mergeV(YY).transpose();
        System.out.println("--------------------- XXYY 1 ---------------------");
        XXYY.print(8, 4);

        /*
         * PointList pointList = new PointList(XX,YY);
         * pointList.uniquePoints(XX.isSorted()); pointList.print(8, 4,
         * "XXYY 2");
         * 
         * 
         * XX = pointList.getX(); YY = pointList.getY();
         * 
         * 
         * XX = pointList.getX(); YY = pointList.getY(); XXYY =
         * XX.mergeV(YY).transpose();
         * System.out.println("--------------------- XXYY 2 ---------------------"
         * ); XXYY.print(8,4);
         */

        CheckXyRange CR = new CheckXyRange(XX, YY);
        Matrix X = CR.getX();
        Matrix Y = CR.getY();
        Matrix XY = X.mergeV(Y).transpose();

        System.out.println("--------------------- XY ---------------------");
        XY.print(8, 4);

    }

}// -------------------------- End Class Definition
// -----------------------------

