/*
 * Min.java
 *
 * Created on 7 November 2007, 16:03
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
public abstract class Min
{

    protected Indices minIndices;
    protected Object minObject;

    /** Creates a new instance of Min */
    public Min()
    {
    }

    public Indices getIndices()
    {
        return minIndices;
    }

    public Object getMinObject()
    {
        return minObject;
    }

}
