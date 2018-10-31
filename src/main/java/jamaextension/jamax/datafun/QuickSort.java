/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;

/**
 * 
 * @author Sione
 */
public abstract class QuickSort
{

    protected long comparisons = 0;
    protected long exchanges = 0;
    // protected DoubleWithIndex dwIndex;
    protected Indices sortedIndices;
    protected Object sortedObject;
    // protected SortingMode mode = SortingMode.ASCENDING;
    protected boolean computeSortedIndex = false;
    protected boolean sortedCopy = false;

    /** Creates a new instance of Sort */
    public QuickSort()
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

    protected abstract void sortMatrix(Object sortedObj);

    protected abstract void sortMatrixAll(Object sortedObj);

    protected abstract void sortVector(Object sortedObj);
}
