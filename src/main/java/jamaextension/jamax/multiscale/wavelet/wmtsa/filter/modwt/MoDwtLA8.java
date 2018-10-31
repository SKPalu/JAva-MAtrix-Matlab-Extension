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
public class MoDwtLA8 extends WaveletFilter
{

    public MoDwtLA8()
    {
        super("LA8", TransformClass.LeastAsymmetric, Transform.MODWT);
        Matrix f = Filter.wtf_la8();
        this.g = f.arrayRightDivide(Math.sqrt(2.0));
        this.L = g.length();
        // WaveUtil.qmf(g).printInLabel("h");
        this.h = WaveUtil.qmf(f).arrayRightDivide(Math.sqrt(2.0));
    }

}
