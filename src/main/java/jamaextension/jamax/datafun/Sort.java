/*
 * Sort.java
 *
 * Created on 7 November 2007, 15:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.constants.SortingMode;

/**
 * 
 * @author Feynman Perceptrons
 * 
 * @deprecated Use the Class <B>QuickSort</B>, instead.
 */
public abstract class Sort
{

    protected Indices sortedIndices;
    protected Object sortedObject;
    protected SortingMode mode = SortingMode.ASCENDING;

    /** Creates a new instance of Sort */
    public Sort()
    {
    }

    public Indices getIndices()
    {
        return sortedIndices;
    }

    public Object getSortedObject()
    {
        return sortedObject;
    }

}
