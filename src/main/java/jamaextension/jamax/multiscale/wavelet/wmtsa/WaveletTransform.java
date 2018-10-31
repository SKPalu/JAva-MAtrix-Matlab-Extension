/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.multiscale.wavelet.wmtsa;

import jamaextension.jamax.Matrix;

//import com.feynmanperceptron.jnumeric.jamax.*;

/**
 * 
 * @author Feynman Perceptrons
 */
public class WaveletTransform
{

    Matrix g;
    Matrix h;
    int L;
    String name;

    public WaveletTransform(String name, Matrix g, Matrix h, int L)
    {
        if ("".equals(name) && name == null)
        {
            throw new IllegalArgumentException(" WaveletTransform : Parameter 'name' must be non-empty or non-null.");
        }
        this.name = name;
        if (g == null)
        {
            throw new IllegalArgumentException(" WaveletTransform : Parameter 'g' must be non-null.");
        }
        if (!g.isVector())
        {
            throw new IllegalArgumentException(" WaveletTransform : Parameter 'g' must be a vector and not a matrix.");
        }
        this.g = g;

        if (h == null)
        {
            throw new IllegalArgumentException(" WaveletTransform : Parameter 'h' must be non-null.");
        }
        if (!h.isVector())
        {
            throw new IllegalArgumentException(" WaveletTransform : Parameter 'h' must be a vector and not a matrix.");
        }
        this.h = h;

        if (L < 2)
        {
            throw new IllegalArgumentException(" WaveletTransform : Parameter 'L' must be at least 2 ;");
        }
        this.L = L;

    }

}
