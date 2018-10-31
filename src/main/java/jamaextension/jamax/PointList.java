/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax;

import java.util.ArrayList;

import jamaextension.jamax.ops.JOps;

/**
 * 
 * @author Feynman Perceptrons
 */
public class PointList
{

    private ArrayList<Point> points = new ArrayList<Point>();
    private boolean sorted = false;
    private boolean unique = false;

    public PointList()
    {
    }

    public PointList(Matrix X, Matrix Y)
    {
        if (X == null)
        {
            throw new IllegalArgumentException("PointList : Parameter \"X\" must be non-null.");
        }
        if (Y == null)
        {
            throw new IllegalArgumentException("PointList : Parameter \"Y\" must be non-null.");
        }

        if (!X.isVector())
        {
            throw new IllegalArgumentException("PointList : Parameter \"X\" must be a vector and not a matrix.");
        }
        if (!Y.isVector())
        {
            throw new IllegalArgumentException("PointList : Parameter \"Y\" must be a vector and not a matrix.");
        }

        if (X.length() != Y.length())
        {
            throw new IllegalArgumentException("PointList : Length of vector \"X\" and \"Y\" must be the same.");
        }

        if (X.isColVector())
        {
            X = X.toRowVector();
        }
        if (Y.isColVector())
        {
            Y = Y.toRowVector();
        }

        int len = X.length();
        for (int i = 0; i < len; i++)
        {
            Point P = new Point(X.get(0, i), Y.get(0, i));
            points.add(P);
        }
    }

    public PointList(Point point)
    {
        points.add(point);
    }

    public PointList(PointList pointList)
    {
        // this = pointList;
        if (pointList == null || pointList.size() == 0)
        {
            return;
        }
        int siz = pointList.size();
        for (int i = 0; i < siz; i++)
        {
            Point P = pointList.get(i).copy();
            this.addPoint(P);
        }
    }

    public void addPoint(Point point)
    {
        points.add(point);
    }

    public Point get(int ind)
    {
        return points.get(ind);
    }

    public PointList getPoints(int[] index)
    {
        if (index == null)
        {
            return null;
        }
        int siz = size();
        if (siz == 0)
        {
            return null;
        }

        int len = index.length;
        for (int i = 0; i < len; i++)
        {
            if (index[i] < 0 || index[i] >= siz)
            {
                throw new IllegalArgumentException("getPoints : Out-of-bound index reference.");
            }
        }

        PointList newPointList = new PointList();
        for (int i = 0; i < len; i++)
        {
            Point P = points.get(index[i]);
            newPointList.addPoint(P);
        }

        return newPointList;
    }

    public void removePoint(int ind)
    {
        this.removePoints(new int[]
        {
            ind
        });
    }

    public void removePoints(int[] index)
    {
        if (index == null)
        {
            return;
        }
        int len = index.length;
        int siz = size();
        if (siz == 0)
        {
            return;
        }
        for (int i = 0; i < len; i++)
        {
            if (index[i] < 0 || index[i] >= siz)
            {
                throw new IllegalArgumentException("removePoints : Out-of-bound index reference.");
            }
        }

        for (int j = 0; j < siz; j++)
        {
            points.set(index[j], null);
        }
        ArrayList<Point> nonNullPoints = new ArrayList<Point>();
        for (int j = 0; j < siz; j++)
        {
            Point P = points.get(j);
            if (P != null)
            {
                nonNullPoints.add(P);
            }
        }
        points = nonNullPoints;
    }

    public void print(int i, int d)
    {
        print(i, d, "PointList");
    }

    public void print(int i, int d, String name)
    {
        Matrix XY = this.getX().mergeV(this.getY()).transpose();
        System.out.println("\n------------- " + name + " -------------");
        XY.print(i, d);
        System.out.println("\n");
    }

    public int size()
    {
        return this.points.size();
    }

    public Matrix getX()
    {
        int siz = size();
        if (siz == 0)
        {
            return null;
        }
        Matrix X = new Matrix(1, siz);

        for (int i = 0; i < siz; i++)
        {
            Point P = points.get(i);
            X.set(0, i, P.getX());
        }

        return X;
    }

    public Matrix getY()
    {
        int siz = size();
        if (siz == 0)
        {
            return null;
        }
        Matrix Y = new Matrix(1, siz);

        for (int i = 0; i < siz; i++)
        {
            Point P = points.get(i);
            Y.set(0, i, P.getY());
        }

        return Y;
    }

    public void uniquePoints()
    {
        uniquePoints(false);
    }

    public boolean hasNaNsInX()
    {
        Matrix X = this.getX();
        // if(X==null){ return true;}
        return X.isnanBoolean();
    }

    public void purgedNaNsInX()
    {
        if (hasNaNsInX() == false)
        {
            return;
        }
        Matrix X = this.getX();
        Matrix Y = this.getY();
        int len = X.length();
        ArrayList<Point> nonNaN_X_Points = new ArrayList<Point>();
        for (int i = 0; i < len; i++)
        {
            double val = X.get(0, i);
            if (!Double.isNaN(val))
            {
                Point P = new Point(val, Y.get(0, i));
                nonNaN_X_Points.add(P);
            }
        }
        this.points = nonNaN_X_Points;
    }

    public void uniquePoints(boolean sortPoints)
    {

        Matrix X = this.getX();
        if (X == null)
        {
            return;
        }

        purgedNaNsInX();
        X = this.getX();
        X.setSorted(sortPoints);

        Object[] obj = JOps.uniqueMat(X, sortPoints);
        X = (Matrix) obj[0]; // x is a rowvector
        Indices ind = (Indices) obj[1];
        int[] arr = ind.getRowPackedCopy();

        // System.out.println("\n------------- X unique -------------");
        // X.print(8,4);

        Matrix Y = this.getY();
        Y = Y.getElements(arr).toRowVector();

        int len = Y.length();
        ArrayList<Point> uniquePoints = new ArrayList<Point>();
        for (int i = 0; i < len; i++)
        {
            Point P = new Point(X.get(0, i), Y.get(0, i));
            uniquePoints.add(P);
        }

        points = uniquePoints;
        unique = true;
        sorted = sortPoints;
    }

    public boolean isUnique()
    {
        return this.unique;
    }

    public boolean isSorted()
    {
        return this.sorted;
    }

    public static void main(String[] args)
    {
        Matrix X = new Matrix(new double[]
        {
                7, 14, 7, 6, 13, 8, 3, 10
        });
        Matrix Y = new Matrix(new double[]
        {
                3.2849, 4.9530, 3.2849, -1.3971, 2.1008, 4.9468, 0.7056, -2.7201
        });

        PointList PL = new PointList(X, Y);
        PL.print(8, 4, "Non-unique/Unsorted");

        PL.uniquePoints(X.isSorted());
        PL.print(8, 4, "Unique/Sorted");
    }

}
