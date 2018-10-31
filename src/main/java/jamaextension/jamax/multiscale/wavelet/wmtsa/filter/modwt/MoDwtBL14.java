/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//package com.feynmanperceptron.jnumeric.wavelet.wmtsa.filter.modwt;

//import com.feynmanperceptron.jnumeric.jamax.*;

//import com.feynmanperceptron.jnumeric.wavelet.wmtsa.*; 

package jamaextension.jamax.multiscale.wavelet.wmtsa.filter.modwt;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.multiscale.wavelet.wmtsa.Filter;
import jamaextension.jamax.multiscale.wavelet.wmtsa.Transform;
import jamaextension.jamax.multiscale.wavelet.wmtsa.TransformClass;
import jamaextension.jamax.multiscale.wavelet.wmtsa.WaveUtil;
import jamaextension.jamax.multiscale.wavelet.wmtsa.WaveletFilter;

/**
 * 
 * @author Feynman Perceptrons
 */
public class MoDwtBL14 extends WaveletFilter
{

    public MoDwtBL14()
    {
        super("BL14", TransformClass.BestLocalized, Transform.MODWT);
        Matrix f = Filter.wtf_bl14();
        this.g = f.arrayRightDivide(Math.sqrt(2.0));
        this.L = g.length();
        this.h = WaveUtil.qmf(f).arrayRightDivide(Math.sqrt(2.0));
    }

}
