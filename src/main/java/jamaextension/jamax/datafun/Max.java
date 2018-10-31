/*
 * Max.java
 *
 * Created on 7 November 2007, 15:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;

/**
 * 
 * @author Feynman Perceptrons
 */
public abstract class Max
{

    protected Indices maxIndices;
    protected Object maxObject;

    /** Creates a new instance of Max */
    public Max()
    {
    }

    public Indices getIndices()
    {
        return maxIndices;
    }

    public Object getMaxObject()
    {
        return maxObject;
    }

}
