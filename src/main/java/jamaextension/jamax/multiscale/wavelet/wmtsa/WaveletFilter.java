/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.multiscale.wavelet.wmtsa;

import jamaextension.jamax.Matrix;

//import com.feynmanperceptron.jnumeric.wavelet.wmtsa.*;
//import com.feynmanperceptron.jnumeric.jamax.*; 

/**
 * 
 * @author Feynman Perceptrons
 */
public abstract class WaveletFilter
{
    protected Matrix g;
    protected Matrix h;
    protected int L;
    protected String name;
    protected Transform transform;
    protected TransformClass transType;

    public WaveletFilter(String name, TransformClass transType)
    {
        this(name, transType, null);
    }

    public WaveletFilter(String name, TransformClass transType, Transform transform)
    {
        if (name == null || "".equals(name))
        {
            throw new IllegalArgumentException(" WaveletFilter : Parameter 'wtf' must be non-null or non-empty.");
        }
        this.name = name;

        if (transType == null)
        {
            throw new IllegalArgumentException(" WaveletFilter : Parameter 'transType' must be non-null.");
        }
        this.transType = transType;

        if (transform == null)
        {
            this.transform = Transform.DWT;
        }
        else
        {
            this.transform = transform;
        }
    }

    public Matrix getH()
    {
        return h;
    }

    public Matrix getG()
    {
        return g;
    }

    public String getName()
    {
        return name;
    }

    public Transform getTransform()
    {
        return transform;
    }

    public TransformClass getTransType()
    {
        return transType;
    }

    public int getL()
    {
        return L;
    }

}
