/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.unsupervised.subspace.pca;

import java.util.ArrayList;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.SvdJLapack;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortInd;
import jamaextension.jamax.vnijmsl.QR;

/**
 * 
 * @author Sione
 */
public class CsspPCA
{

    private Matrix multivariateData;
    private Matrix reducedData;
    private int reducedDimension = 2;
    private boolean built = false;

    public CsspPCA(Matrix A)
    {
        this(A, null);
    }

    public CsspPCA(Matrix A, Integer k)
    {

        if (A == null)
        {
            throw new IllegalArgumentException("CsspPCA : Parameter \"A\" must be non-null.");
        }
        if (A.isVector())
        {
            throw new IllegalArgumentException("CsspPCA : Parameter \"A\" must be a matrix and not a vector.");
        }
        this.multivariateData = A;

        int nVariables = A.getColumnDimension();

        if (k == null)
        {
            return;
        }
        if (k.intValue() < 2)
        {
            throw new IllegalArgumentException("CsspPCA : Value for parameter \"k\" must be at least two.");
        }
        if (k.intValue() > nVariables)
        {
            throw new IllegalArgumentException(
                    "CsspPCA : Value for parameter \"k\" must equal or less than the number of variables available (ie, columns) in the data \"A\".");
        }
        this.reducedDimension = k.intValue();

    }

    public void build()
    {
        Matrix A = this.multivariateData;
        int n = A.getColumnDimension();
        int k = this.reducedDimension;

        // SingularValueDecomposition svd = A.svd(); //[U,S,V]
        SvdJLapack svd = new SvdJLapack(A);
        // [x, z, Vk]
        Matrix x = svd.getU();
        Matrix z = svd.getS();
        Matrix Vk = svd.getV();

        // -- Compute the top-k right singular vectors of A, denoted by Vk
        int[] indArr = Indices.linspace(0, k - 1).getRowPackedCopy();
        x = x.getColumns(indArr);
        z = z.getMatrix(indArr, indArr);
        Vk = Vk.getColumns(indArr);

        // -- Compute the sampling probabilities p_j, for j=1:n, using eqn. (5)
        Matrix p = Matrix.zeros(1, n); // Vector with the probabilities of
                                       // eqn.(5) , initially zero
        Matrix Tmp = A.times(Vk).times(Vk.transpose()); // The matrix A*Vk*Vk'
                                                        // of eqn. (5).

        double normTmp = Tmp.normF(); // norm(Tmp, 'fro')^2; // The Frobenius
                                      // norm square of A*Vk*Vk'.
        normTmp = normTmp * normTmp;

        double normA = A.normF();// norm(A, 'fro')^2; // The Frobenius norm
                                 // square of A.
        normA = normA * normA;

        double val = 0.0;
        double val2 = 0.0;
        double val3 = 0.0;
        double val4 = 2.0 * (normA - normTmp);
        for (int j = 0; j < n; j++)
        {// :size(A,2) // Eqn. (5).
            val = Vk.getRowAt(j).norm1();
            val = val * val / (2.0 * (double) k);
            // p(j) = ((norm(Vk(j,:))^2) / (2*k)) + ((norm(A(:,j))^2 -
            // norm(Tmp(:,j))^2) / ( 2 * ( normA - normTmp )));
            val2 = Math.pow(A.getColumnAt(j).norm1(), 2.0) - Math.pow(Tmp.getColumnAt(j).norm1(), 2.0);// (
                                                                                                       // norm(A(:,j))^2
                                                                                                       // -
                                                                                                       // norm(Tmp(:,j))^2
                                                                                                       // )
            val3 = val + val2 / val4;
            p.set(0, j, val3);
        }

        // -- Let c = O(k log(k))
        double c = Math.ceil(3.0 * k * Math.log((double) k));

        // %1. Randomized phase

        /*-- The matrices S1 and D1 are never formed. Instead, we keep the vector
        %   indexR with the indices of the columns of A kept in this phase.
        %   diagonalD is the vector with the accosiated rescaling factors.*/

        ArrayList<Integer> indexR = new ArrayList<Integer>();
        ArrayList<Double> diagonalD = new ArrayList<Double>();

        for (int j = 0; j < n; j++)
        {
            double min1cpj = Math.min(1.0, c * p.get(0, j)); // sampling
                                                             // probability.
            if ((min1cpj == 1.0) || (min1cpj > Math.random()))
            { // the random
              // experiment.
                indexR.add(new Integer(j)); // keep the j-th index.
                diagonalD.add(new Double(Math.sqrt(min1cpj))); // keep the
                                                               // rescaling
                                                               // factor.
            }
        }

        // 2. Deterministic phase
        // ---------------------------------------------------

        /*-- We don't implement the method of Pan described in Algorithm 1 of the paper.
        %   Instead, we implement the method Pivoted QR described in the subsection 4.1.*/

        // Matrix VkS = Matrix.zeros(indexR.size(), k);
        int[] arrInd = indexRtoIntArray(indexR);
        Matrix VkS = Vk.getRows(arrInd);// Vk(indexR, : );

        // VkS' equals the matrix Vk'*S_1 described in the paper.
        Matrix VkSD = Matrix.zeros(indexR.size(), k);
        int lenDiag = diagonalD.size();
        Matrix temp = null;
        for (int j = 0; j < lenDiag; j++)
        {// :length(diagonalD)
         // VkSD(j, :) = VkS(j, :) ./ diagonalD(j);
            temp = VkS.getRowAt(j).arrayRightDivide(diagonalD.get(j).doubleValue());
            VkSD.setRowAt(j, temp);
        }
        // VkSD' now equals the matrix Vk'*S_1*D_1 described in the paper.
        // [x,z,pd] = qr(VkSD',0) ; % Pivoted QR on Vk'*S_1*D_1.
        QR qrPd = new QR(VkSD.transpose());
        int[] pd = qrPd.getPermute();
        Indices pdInd = new Indices(pd);
        arrInd = Indices.linspace(0, k - 1).getRowPackedCopy();
        pdInd = pdInd.getEls(arrInd).toRowVector();
        arrInd = pdInd.getRowPackedCopy();

        Indices indexRtoIndices = new Indices(indexRtoIntArray(indexR));
        indexRtoIndices = indexRtoIndices.getEls(arrInd);
        QuickSort sort = new QuickSortInd(indexRtoIndices);
        arrInd = ((Indices) sort.getSortedObject()).getRowPackedCopy();// .getIndices().getRowPackedCopy();
        this.reducedData = A.getColumns(indArr);// C = A(:, indexD);

        this.built = true;
    }

    private int[] indexRtoIntArray(ArrayList<Integer> indexR)
    {

        if (indexR.size() == 0)
        {
            return null;
        }
        int siz = indexR.size();

        int[] arrInd = new int[siz];
        for (int i = 0; i < siz; i++)
        {
            arrInd[i] = indexR.get(i).intValue();
        }
        return arrInd;
    }

    /**
     * @return the multivariateData
     */
    public Matrix getMultivariateData()
    {

        return multivariateData;
    }

    /**
     * @return the reducedDimension
     */
    public int getReducedDimension()
    {
        return reducedDimension;
    }

    /**
     * @param reducedDimension
     *            the reducedDimension to set
     */
    public void setReducedDimension(int k)
    {
        if (k < 2)
        {
            throw new IllegalArgumentException("setReducedDimension : Value for parameter \"k\" must be at least two.");
        }
        if (k > this.multivariateData.getColumnDimension())
        {
            throw new IllegalArgumentException(
                    "setReducedDimension : Value for parameter \"k\" must equal or less than the number of variables available (ie, columns) in the data \"A\".");
        }
        this.reducedDimension = k;
    }

    /**
     * @return the built
     */
    public boolean isBuilt()
    {
        return built;
    }

    /**
     * @return the reducedData
     */
    public Matrix getReducedData()
    {
        if (!isBuilt())
        {
            throw new IllegalArgumentException("getReducedData : The method \"build\" must be invoked first.");
        }
        return reducedData;
    }
}
