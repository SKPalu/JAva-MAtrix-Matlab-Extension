/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

/**
 * 
 * @author Sione
 */
public enum AutoCorrelStdMethod
{

    BARTLETTS_FORMULA(1), // = 1;
    MORANS_FORMULA(2);// = 2;
    private int num;

    AutoCorrelStdMethod(int n)
    {
        this.num = n;
    }

    public int getNum()
    {
        return this.num;
    }
}
