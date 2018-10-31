/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

/**
 * 
 * @author Sione
 */
public enum CrossCorrelStdErrMethod
{

    BARTLETTS_FORMULA(1), // = 1;
    BARTLETTS_FORMULA_NOCC(2);// = 2;
    private int num;

    CrossCorrelStdErrMethod(int n)
    {
        this.num = n;
    }

    public int getNum()
    {
        return this.num;
    }
}
