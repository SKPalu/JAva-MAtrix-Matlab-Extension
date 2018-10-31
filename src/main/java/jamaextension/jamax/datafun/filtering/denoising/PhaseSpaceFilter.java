/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun.filtering.denoising;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.SvdJLapack;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elfun.JElfun;

/**
 * 
 * @author Sione
 */
public class PhaseSpaceFilter
{

    private Matrix U;
    private Matrix S;
    private Matrix V;

    public PhaseSpaceFilter(Matrix X, int embed, int lag)
    {
        if (lag < 1)
        {
            throw new ConditionalException("PhaseSpaceFilter : Parameter \"lag\" must be positive.");
        }
        if (embed < 1)
        {
            throw new ConditionalException("PhaseSpaceFilter : Parameter \"embed\" must be positive.");
        }
        if (!X.isVector())
        {
            throw new ConditionalException("PhaseSpaceFilter : Parameter \"X\" must be a vector and not a matrix.");
        }
        int N = X.length();

        boolean cond = (N - (embed - 1) * lag) < 1;
        if (cond)
        {
            throw new ConditionalException(
                    "PhaseSpaceFilter : Length of \"X\" must be greater or equal to roughly product of  \"embed\" and \"lag\".");
        }
        // embedding_dim = 20;
        // lag = 1;
        // make embedding matrix, X
        Matrix EMB = embed(X, embed, lag);
        // Now do a singular value decompostion
        // [UU,S,V] = svd(X,0);
        SvdJLapack svd = new SvdJLapack(EMB, SvdJLapack.Economy.ZERO);
        this.U = svd.getU();
        this.S = svd.getS();
        this.V = svd.getV();
    }

    private Matrix embed(Matrix x, int embd, int lag)
    {
        // Matrix UU = null;
        if (!x.isVector())
        {
            throw new ConditionalException("embedding : Parameter \"x\" must be a vector and not a matrix.");
        }
        // need column vector;
        Matrix X = null;
        // x=x(:);
        if (!x.isColVector())
        {
            X = x.toColVector();
        }
        else
        {
            X = x;
        }
        int N = X.length();// size(x,1);

        // embed data sequence for embedding dimension <embd> and lag <lag>

        Indices hindex = Indices.linspace(1, embd * lag, lag);// .linspace(1:lag:embd*lag);
                                                              // // horizontal
                                                              // vector
        hindex = hindex.minus(1);// hindex-1;
        Indices vindex = Indices.linspace(1, N - (embd - 1) * lag);
        vindex = vindex.transpose(); // vertical vector
        int Nv = JDatafun.max(vindex.size()).start();

        // UU=x(:,ones(1,embd));
        // UU = X.repmat(1, embd);
        hindex = hindex.repmat(Nv, 1);
        vindex = vindex.repmat(1, embd);
        Indices hv = hindex.plus(vindex).minus(1); // minus 1 since java index
                                                   // starts at 0
        // UU=UU(hindex(ones(Nv,1),:)+vindex(:,ones(embd,1)));
        int subR = hv.getRowDimension();
        int subC = hv.getColumnDimension();
        Matrix subU = new Matrix(subR, subC);
        for (int i = 0; i < subR; i++)
        {
            for (int j = 0; j < subC; j++)
            {
                int el = hv.get(i, j);
                double val = X.getElementAt(el);
                subU.set(i, j, val);
            }
        }

        return subU;
    }

    public Matrix reconstruct(int sp)
    {
        Matrix sig_out = Matrix.zeros(this.U.getRowDimension(), 1);

        Matrix R = this.U.times(JElfun.sqrt(S));
        Matrix fill = Matrix.zeros(this.U.getColumnDimension() - 1, 1);

        for (int n = 0; n < sp; n++)
        {
            sig_out = sig_out.plus(R.getColumnAt(n));
        }

        sig_out = sig_out.mergeV(fill);// [sig_out; fill];
        return sig_out;

    }

    public static void main(String[] args)
    {
        double[] kk =
        {
                81, 91, 13, 91, 63, 10, 28, 55, 96, 96, 16, 97, 96, 49, 80, 14
        };
        Matrix SI = new Matrix(kk);
        SI.printInLabel("SI");
        PhaseSpaceFilter FFF = new PhaseSpaceFilter(SI, 8, 1);
        Matrix SO = FFF.reconstruct(5);
        SO.toRowVector().printInLabel("SO");
    }
}
