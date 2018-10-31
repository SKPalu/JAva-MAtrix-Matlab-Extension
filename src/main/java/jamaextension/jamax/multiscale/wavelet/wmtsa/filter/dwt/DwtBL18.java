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
public class DwtBL18 extends WaveletFilter
{

    public DwtBL18()
    {
        super("BL18", TransformClass.BestLocalized);
        this.g = Filter.wtf_bl18();
        this.L = g.length();
        this.h = WaveUtil.qmf(g);
    }

}
