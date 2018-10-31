/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.constants;

/**
 * 
 * @author Sione
 */
public enum KmnEmptyAction
{

    error, // - Treat an empty cluster as an error (the default)
    drop, // - Remove any clusters that become empty, and set the corresponding
          // values in C and D to NaN.
    singleton;// - Create a new cluster consisting of the one observation
              // furthest from its centroid.

}
