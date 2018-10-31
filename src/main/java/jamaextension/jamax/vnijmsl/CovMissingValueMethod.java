/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

/**
 * 
 * @author Sione
 */
public enum CovMissingValueMethod
{

    VARIANCE_COVARIANCE_MATRIX(0), // = 0;
    CORRECTED_SSCP_MATRIX(1), // = 1;
    CORRELATION_MATRIX(2), // = 2;
    STDEV_CORRELATION_MATRIX(3);// = 3;
    private int num;

    CovMissingValueMethod(int n)
    {
        this.num = n;
    }

    public int getNum()
    {
        return this.num;
    }
}
