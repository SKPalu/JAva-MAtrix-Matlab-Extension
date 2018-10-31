/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.multiscale.wavelet.wmtsa.filter.dwt;

import jamaextension.jamax.multiscale.wavelet.wmtsa.Filter;
import jamaextension.jamax.multiscale.wavelet.wmtsa.TransformClass;
import jamaextension.jamax.multiscale.wavelet.wmtsa.WaveUtil;
import jamaextension.jamax.multiscale.wavelet.wmtsa.WaveletFilter;

/**
 * 
 * @author Feynman Perceptrons
 */
public class DwtLA16 extends WaveletFilter
{

    public DwtLA16()
    {
        super("LA16", TransformClass.LeastAsymmetric);
        this.g = Filter.wtf_la16();
        this.L = g.length();
        this.h = WaveUtil.qmf(g);
    }

}
