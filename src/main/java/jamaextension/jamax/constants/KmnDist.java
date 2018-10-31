/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.constants;

/**
 * 
 * @author Sione
 */
public enum KmnDist
{

    sqeuclidean, // - Squared Euclidean distance (the default)
    cityblock, // - Sum of absolute differences, a.k.a. L1 distance
    cosine, // - One minus the cosine of the included angle between points
            // (treated as vectors)
    correlation, // - One minus the sample correlation between points (treated
                 // as sequences of values)
    hamming;
}
