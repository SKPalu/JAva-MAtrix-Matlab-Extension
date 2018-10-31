/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class MoDwtLA16 extends WaveletFilter
{

    public MoDwtLA16()
    {
        super("LA16", TransformClass.LeastAsymmetric, Transform.MODWT);
        Matrix f = Filter.wtf_la16();
        this.g = f.arrayRightDivide(Math.sqrt(2.0));
        this.L = g.length();
        this.h = WaveUtil.qmf(f).arrayRightDivide(Math.sqrt(2.0));
    }

}
