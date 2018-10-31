/*
 * Grid.java
 *
 * Created on 6 January 2008, 09:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.elmat;

/**
 * 
 * @author Feynman Perceptrons
 */
public abstract class Grid
{

    protected Object firstObjectArray;
    protected Object secondObjectArray;

    /** Creates a new instance of Grid */
    public Grid()
    {
    }

    public Object getFirstObjectArray()
    {
        return firstObjectArray;
    }

    public Object getSecondObjectArray()
    {
        return secondObjectArray;
    }
}
