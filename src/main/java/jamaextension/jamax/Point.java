/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

/**
 * 
 * @author Feynman Perceptrons
 */
public class Point
{

    private double x = 0.0;
    private double y = 0.0;

    public Point()
    {
    }

    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public Point copy()
    {
        return new Point(x, y);
    }

    public boolean equal(Point P, double tol)
    {
        if (P == null)
        {
            return false;
        }

        boolean tf = MathUtil.equalsWithTol(x, P.x, tol) && MathUtil.equalsWithTol(y, P.y, tol);
        return tf;
    }

    public boolean equal(Point P)
    {
        double tol = MathUtil.EPS;
        return equal(P, tol);
    }
}
