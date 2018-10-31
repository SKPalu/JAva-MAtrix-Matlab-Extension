/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

/**
 * 
 * @author Sione
 */
public enum VarianceMethods
{

    VARIANCE_COVARIANCE_MATRIX(0), // = 0;
    CORRELATION_MATRIX(1);// = 1;
    private int num;

    VarianceMethods(int n)
    {
        this.num = n;
    }

    public int getNum()
    {
        return this.num;
    }
}
