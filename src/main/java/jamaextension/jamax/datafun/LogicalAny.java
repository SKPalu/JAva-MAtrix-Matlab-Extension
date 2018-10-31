/*
 * LogicalAny.java
 *
 * Created on 28 November 2007, 06:09
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
public abstract class LogicalAny
{

    protected Indices logicalAnyObject;

    /** Creates a new instance of LogicalAny */
    public LogicalAny()
    {
    }

    public Indices getIndices()
    {
        return logicalAnyObject;
    }
}
