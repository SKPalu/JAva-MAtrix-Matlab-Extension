/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.constants;

/**
 * 
 * @author Sione
 */
public enum KmnStart
{

    sample, // - Select K observations from X at random (the default)
    uniform, // - Select K points uniformly at random from the range of X. Not
             // valid for Hamming distance.
    cluster;// - Perform preliminary clustering phase on random 10% subsample of
            // X. This preliminary phase is itself initialized using 'sample'.
    // matrix;// - A K-by-P matrix of starting locations. In this case, you can
    // pass in [] for K, and KMEANS infers K from
    // the first dimension of the matrix. You can also
    // supply a 3D array, implying a value for 'Replicates'
    // from the array's third dimension.
}
