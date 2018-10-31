/*
 * Difference.java
 *
 * Created on 27 November 2007, 08:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.datafun;

/**
 * 
 * @author Feynman Perceptrons
 */
public abstract class Difference
{

    protected int order;
    protected Object differenceObject;

    /** Creates a new instance of Difference */
    public Difference()
    {
    }

    public Object getDifferenceObject()
    {
        return differenceObject;
    }

    // proptected void copyFromTo()

}
