package jamaextension.jamax.misc;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;

public class AlgoImplChallenge
{

    static int solution(int K, int M, int A[])
    {
        // check if there are 'N' elements in 'A'
        /*
         * if (A.length != N) { throw new
         * IllegalArgumentException("Length of integer array \"A\" (= " +
         * A.length + ") must be " + N); }
         */
        int N = A.length;

        // check if any element/s in 'A' is greater than 'M'
        Indices indA = new Indices(A);
        FindInd find = indA.GT(M).findIJ();
        if (!find.isNull())
        {
            int[] arr = find.getIndex();
            indA.getEls(arr).printInLabel("Elements that are greater than " + M);
            throw new IllegalArgumentException("Found " + arr.length + " elements in 'A' greater than " + M);
        }

        // check if 'K' is greater than 'N'
        if (K > N)
        {
            throw new IllegalArgumentException("Block parameter K (=" + K + ") must equal or less than " + M);
        }

        // create an integer vector from '0' to 'N-1'
        Indices vec = Indices.linspace(1, N);
        vec = vec.repmat(N, 1);
        vec.printInLabel("vec");

        int maxCount = 10000;
        int count = 1;

        // Random random = new Random();

        int maxMin = 0;
        return maxMin;
    }

    static void runAlgo()
    {
        int N = 20;
        int M = 15;
        int K = 4;
        Matrix matA = Matrix.rand(1, N).scale(M).round();
        Indices indA = matA.toIndices();
        indA.printInLabel("indA");
        int[] A = indA.getRowPackedCopy();

        int ans = solution(K, M, A);
    }

    public static void main(String[] args)
    {
        runAlgo();

    }

}
