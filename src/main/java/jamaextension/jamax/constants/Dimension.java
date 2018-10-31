/*
 * Dimension.java
 *
 * Created on 6 November 2007, 12:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.constants;

/**
 * 
 * @author Feynman Perceptrons
 */
public enum Dimension
{

    ROW(1), COL(2), PAGE(3);
    private int num;

    private Dimension(int num)
    {
        this.num = num;
    }

    public int getNum()
    {
        return num;
    }

    public int getNumInd0()
    {
        return (getNum() - 1);
    }
}
