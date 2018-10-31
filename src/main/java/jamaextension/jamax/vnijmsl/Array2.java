/*
 * Array2.java
 *
 * Created on 31 March 2007, 17:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.vnijmsl;

final class Array2
{

    private double a[];
    private int dim;

    Array2(double ad[], int i)
    {
        a = ad;
        dim = i;
    }

    final double get(int i, int j)
    {
        return a[i * dim + j];
    }

    final void set(int i, int j, double d)
    {
        a[i * dim + j] = d;
    }

    final void plusEqual(int i, int j, double d)
    {
        a[i * dim + j] += d;
    }

    final void overEqual(int i, int j, double d)
    {
        a[i * dim + j] /= d;
    }

}