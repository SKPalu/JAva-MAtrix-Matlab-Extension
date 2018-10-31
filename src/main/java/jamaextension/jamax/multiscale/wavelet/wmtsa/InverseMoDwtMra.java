/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.multiscale.wavelet.wmtsa;

import jamaextension.jamax.Matrix;

/**
 * 
 * @author Feynman Perceptrons
 */
public class InverseMoDwtMra
{

    private Matrix[] detailsCoeff;
    private Matrix[] smoothsCoeff;

    public InverseMoDwtMra(MoDwt MD)
    {
        if (MD == null)
        {
            throw new IllegalArgumentException(" InverseMoDwtMra : Parameter 'MD' must be non-null.");
        }
        if (!MD.isTransformed())
        {
            throw new IllegalArgumentException(
                    " InverseMoDwtMra : Method \"transform\" in parameter 'MD' must be called first.");
        }

        WaveletFilter wtf_s = MD.getFilter();
        Matrix ht = wtf_s.getH();
        Matrix gt = wtf_s.getG();
        int L = ht.numel();

        int N = MD.getNX();// = att.NX;
        // int NW = MD.getNW();// = att.NW;
        int J0 = MD.getJ0();// = att.J0;
        int NChan = MD.getNChan();// = att.NChan;
        // Boundary boundary = MD.getBoundary();
        boolean RetainVJ = MD.isRetainVJ();

        Matrix[] WJt = MD.getWJt();
        Matrix[] VJt = MD.getVJt();

        Matrix[] DJt = new Matrix[NChan];
        Matrix[] SJt = new Matrix[NChan];

        for (int i = 0; i < NChan; i++)
        {
            Matrix WJ = WJt[i];// WJt(:,:,i);
            Matrix VJ0 = null;
            if (RetainVJ)
            {
                VJ0 = VJt[i].getColumnAt(J0);// VJt(:,J0,i);
            }
            else
            {
                VJ0 = VJt[i].getColumnAt(0);// VJt(:,1,i);
            }

            // [DJt, DJt_att] = imodwt_details(WJt, wtfname);
            DJt[i] = details(WJ, wtf_s); // ===> original code uses 'WJt',
                                         // however it is changed here because
                                         // of the function 'details' it
                                         // implicated that it should be a
                                         // matrix.

            // [SJt, SJt_att] = imodwt_smooth(VJ0, wtfname, J0);
            SJt[i] = smooths(VJ0, wtf_s, J0);
        }

        detailsCoeff = DJt;
        smoothsCoeff = SJt;
    }// end method

    public Matrix[] getDetailsCoeff()
    {
        return detailsCoeff;
    }

    public Matrix[] getSmoothsCoeff()
    {
        return smoothsCoeff;
    }

    private Matrix details(Matrix WJt, WaveletFilter wtf_s)
    {
        Matrix gt = wtf_s.getG();
        Matrix ht = wtf_s.getH();

        int N = WJt.getRowDimension();
        int J = WJt.getColumnDimension();
        int J0 = J;

        Matrix zeroj = Matrix.zeros(N, 1);
        Matrix DJt = Matrix.zeros(N, J);
        int j = 0;

        for (j = J0; j >= 1; j--)
        {
            Matrix Vin = zeroj;
            Matrix Win = WJt.getColumnAt(j - 1);// WJt(:,j);
            Matrix Vout = null;
            for (int jj = j; jj >= 1; j--)
            {
                // Vout = imodwtj(WJt[i].getColumnAt(j-1), Vin, ht, gt, j, N,
                // L);
                // Vout = InverseMoDwt.imodwtj(Win, Vin, ht, gt, jj, N,
                // ht.length());
                Vout = InverseMoDwt.imodwtj(Win, Vin, ht, gt, jj, Win.numel(), ht.length());
                Win = zeroj;
                Vin = Vout;
            }
            DJt.setColumnAt(j - 1, Vout);// DJt(:,j) = Vout;
        }

        return DJt;
    }

    private Matrix smooths(Matrix VJt, WaveletFilter wtf_s, int J0)
    {
        int L = wtf_s.getL();
        Matrix gt = wtf_s.getG();
        Matrix ht = wtf_s.getH();
        if (!VJt.isVector())
        {
            throw new IllegalArgumentException(" smooths : Parameter 'VJt' must be a vector and not a matrix.");
        }
        int N = VJt.length();

        Matrix zeroj = Matrix.zeros(N, 1);

        Matrix SJt = Matrix.zeros(N, 1);

        Matrix Vin = VJt;
        Matrix Vout = null;

        for (int j = J0; j >= 1; j--)
        {
            Vout = InverseMoDwt.imodwtj(zeroj, Vin, ht, gt, j, zeroj.numel(), L);
            Vin = Vout;
        }

        SJt = Vout;
        SJt = SJt.toColVector();

        return SJt;
    }

}// ----------------------------- End Class Definition
// --------------------------
