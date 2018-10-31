/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Sione
 */
public final class QuickSortMat extends QuickSort
{

    private QsortMatrixWithIndices matrixIndices;

    // //////////////////////////////////////////////////////////////////////////
    public QuickSortMat(Matrix A)
    {
        this(A, null);
    }

    public QuickSortMat(Matrix A, Dimension dim)
    {
        this(A, dim, false);
    }

    public QuickSortMat(Matrix A, Dimension dim, boolean computeSortedIndex)
    {
        this(A, dim, computeSortedIndex, true);
    }

    /*
     * Use this version if the corresponding matlab command is of the following:
     * ' Y = sort(X) '
     */
    public QuickSortMat(Matrix A, boolean computeSortedIndex, boolean sortedCopy)
    {
        this(A, null, computeSortedIndex, sortedCopy);
    }

    /**
     * Creates Mat new instance of QuickSortMat
     */
    public QuickSortMat(Matrix A, Dimension dim, boolean computeSortedIndex, boolean sortedCopy)
    {
        super();
        if (A == null)
        {
            String msg = "Parameter \"A\" must be non-null.";
            throw new ConditionalRuleException("QuickSortMat", msg);
        }

        this.computeSortedIndex = computeSortedIndex;
        this.sortedCopy = sortedCopy;

        if (this.sortedCopy)
        {
            this.sortedObject = A.copy(); // System.out.println("EXECUTE");
        }
        else
        {
            this.sortedObject = A;
        }

        if (dim == null)
        {
            if (A.isVector())
            {
                // System.out.println(" Block #5");
                if (this.computeSortedIndex)
                {
                    if (A.isRowVector())
                    {
                        this.sortedIndices = A.generateIndices(false);
                    }
                    else
                    {
                        this.sortedIndices = A.generateIndices();
                    }
                }
                this.matrixIndices = new QsortMatrixWithIndices((Matrix) this.sortedObject, this.sortedIndices, null,
                        this.computeSortedIndex);
                sortVector(this.matrixIndices);
            }
            else
            {
                // System.out.println(" Block #6");
                if (this.computeSortedIndex)
                {
                    this.sortedIndices = A.generateIndices();
                }
                this.matrixIndices = new QsortMatrixWithIndices((Matrix) this.sortedObject, this.sortedIndices,
                        Dimension.ROW, this.computeSortedIndex);
                sortMatrix(this.matrixIndices);
                System.out.println("EXECUTE sortMatrix");
            }
        }
        else
        {
            // System.out.println(" NON-NULL DIMENSION");
            if (this.computeSortedIndex)
            {
                // System.out.println(" computeSortedIndex");
                if (A.isVector())
                {
                    if (dim == Dimension.ROW)
                    { // Vector Row Dimension
                        if (A.isRowVector())
                        {
                            // System.out.println(" Block #7"); //passed
                            this.sortedIndices = new Indices(1, A.length());
                        }
                        else
                        {
                            // System.out.println(" Block #8"); //passed
                            this.sortedIndices = A.generateIndices();
                        }
                    }
                    else
                    { // Vector Col Dimension
                        if (A.isColVector())
                        {
                            // System.out.println(" Block #9"); //passed
                            this.sortedIndices = new Indices(A.length(), 1);
                        }
                        else
                        {
                            // System.out.println(" Block #10"); //passed
                            this.sortedIndices = A.generateIndices(false);
                        }
                    }
                }
                else
                {
                    if (dim == Dimension.ROW)
                    {
                        // System.out.println(" Block #11");
                        this.sortedIndices = A.generateIndices();
                    }
                    else
                    {
                        // System.out.println(" Block #12");
                        this.sortedIndices = A.generateIndices(false);
                    }
                }
            } /*
               * else { System.out.println(" NON - computeSortedIndex"); }
               */
            this.matrixIndices = new QsortMatrixWithIndices((Matrix) this.sortedObject, this.sortedIndices, dim,
                    this.computeSortedIndex);
            sortMatrix(this.matrixIndices);
        }
        ((Matrix) sortedObject).setSorted(true);
    }

    public QuickSortMat()
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void sortMatrix(Object sortedObj)
    {
        // Matrix A = (Matrix) sortedObj;
        QsortMatrixWithIndices AB = (QsortMatrixWithIndices) sortedObj;
        if (AB.isVector())
        {
            if (AB.isRowVector())
            {// row vector
                if (AB.getDimension() == Dimension.ROW)
                {// return the same
                 // sortedObject = A;
                    Indices newIndices = new Indices(AB.getNumRows(), AB.getNumCols());
                    AB.setIndices(newIndices);
                }
                else
                {
                    sortVector(AB);
                }
            }
            else
            {// column vector
                if (AB.getDimension() == Dimension.COL)
                {
                    // sortedObject = A;
                    Indices newIndices = new Indices(AB.getNumRows(), AB.getNumCols());
                    AB.setIndices(newIndices);
                }
                else
                {
                    sortVector(AB);
                }
            }
        }
        else
        {
            // System.out.println("EXECUTE sortMatrixAll");
            sortMatrixAll(AB);
        }
    }

    @Override
    protected void sortMatrixAll(Object sortedObj)
    {
        sort((QsortMatrixWithIndices) sortedObj);

    }

    @Override
    protected void sortVector(Object sortedObj)
    {

        sort((QsortMatrixWithIndices) sortedObj);
    }

    // //////////////////////////////////////////////////////////////////////////
    /*
     * Mat and A must be vectors of same size.
     */
    private void sort(QsortMatrixWithIndices AB)
    {
        String msg = "";
        if (AB == null)
        {
            msg = "Array parameter \"a\" must be non-null.";
            throw new ConditionalRuleException("sort", msg);
        }
        // double[] A = copyArray(Mat);
        int iter = -1;
        if (AB.isVector())
        {
            iter = 1;
        }
        else
        {
            if (AB.getDimension() == Dimension.ROW)
            {
                iter = AB.getNumCols();
            }
            else
            {
                iter = AB.getNumRows();
            }
        }

        // System.out.println("iter = " + iter + "\n");

        for (int i = 0; i < iter; i++)
        {
            // System.out.println("iter_" + i + " = " + i);
            quicksort(AB, i);
        }

    }

    /**
     * *********************************************************************
     * Quicksort code from Sedgewick 7.1, 7.2.
     * *********************************************************************
     */
    private void quicksort(QsortMatrixWithIndices AB, int numRowCols)
    {
        // dwIndex = new DoubleWithIndex(Mat);
        shuffle(AB, numRowCols); // to guard against worst-case
        if (AB.isVector())
        {
            quicksort(AB, 0, AB.length() - 1, numRowCols);
        }
        else
        {
            if (AB.getDimension() == Dimension.ROW)
            {
                quicksort(AB, 0, AB.getNumRows() - 1, numRowCols);
            }
            else
            {
                quicksort(AB, 0, AB.getNumCols() - 1, numRowCols);
            }
        }
    }

    // quicksort b[left] to b[right]
    private void quicksort(QsortMatrixWithIndices AB, int left, int right, int numRowCols)
    {
        if (right <= left)
        {
            return;
        }
        int i = partition(AB, left, right, numRowCols);
        quicksort(AB, left, i - 1, numRowCols);
        quicksort(AB, i + 1, right, numRowCols);
    }

    // partition b[left] to b[right], assumes left < right
    private int partition(QsortMatrixWithIndices AB, int left, int right, int numRowCols)
    {
        int i = left - 1;
        int j = right;
        while (true)
        {

            // //////////////////////////////////////////////////////////////////
            // //////////////////////////////////////////////////////////////////
            if (AB.isVector())
            {// ------------- Vector -------------------------
                if (AB.isRowVector())
                {
                    while (less(AB.getM(0, ++i), AB.getM(0, right)))
                    { // find item on left to swapD
                      // this is just empty block
                    } // b[right] acts as sentinel
                    while (less(AB.getM(0, right), AB.getM(0, --j)))
                    { // find item on right to swapD
                        if (j == left)
                        {
                            break; // don't go out-of-bounds
                        }
                    }
                }
                else
                {
                    while (less(AB.getM(++i, 0), AB.getM(right, 0)))
                    { // find item on left to swapD
                      // this is just empty block
                    } // b[right] acts as sentinel
                    while (less(AB.getM(right, 0), AB.getM(--j, 0)))
                    { // find item on right to swapD
                        if (j == left)
                        {
                            break; // don't go out-of-bounds
                        }
                    }
                }
            }
            else
            {// ------------------ Matrix --------------------------------
                if (AB.getDimension() == Dimension.ROW)
                {
                    while (less(AB.getM(++i, numRowCols), AB.getM(right, numRowCols)))
                    { // find item on left to swapD
                      // this is just empty block
                    } // b[right] acts as sentinel
                    while (less(AB.getM(right, numRowCols), AB.getM(--j, numRowCols)))
                    { // find item on right to swapD
                        if (j == left)
                        {
                            break; // don't go out-of-bounds
                        }
                    }
                }
                else
                {// $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                    while (less(AB.getM(numRowCols, ++i), AB.getM(numRowCols, right)))
                    { // find item on left to swapD
                      // this is just empty block
                    } // b[right] acts as sentinel
                    while (less(AB.getM(numRowCols, right), AB.getM(numRowCols, --j)))
                    { // find item on right to swapD
                        if (j == left)
                        {
                            break; // don't go out-of-bounds
                        }
                    }
                }// $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            }// -----------------------------------------------------------------

            /*
             * while (less(A.getElementAt(++i), A.getElementAt(right))) { //
             * find item on left to swapD //this is just empty block } //
             * b[right] acts as sentinel while (less(A.getElementAt(right),
             * A.getElementAt(--j))) { // find item on right to swapD if (j ==
             * left) { break; // don't go out-of-bounds } }
             */
            // //////////////////////////////////////////////////////////////////
            // //////////////////////////////////////////////////////////////////

            if (i >= j)
            {
                break; // check if pointers cross
            }
            // System.out.println(" ( rowI , colJ ) = ( "+rowI+" , "+colJ+ ")");
            exch(AB, i, j, numRowCols); // swapD two elements into place
        }
        exch(AB, i, right, numRowCols); // swapD with partition element

        return i;
    }

    // is x < y ?
    private boolean less(double x, double y)
    {
        comparisons++;

        boolean nanX = Double.isNaN(x);
        boolean nanY = Double.isNaN(y);
        boolean tf = false;

        if (nanX && !nanY)
        {
            tf = false;
        }
        else if (!nanX && nanY)
        {
            tf = true;
        }
        else
        {
            tf = x < y;
        }

        return tf;
    }

    // exchange b[rowI] and b[colJ]
    private void exch(QsortMatrixWithIndices AB, int i, int j, int numRowCols)
    {
        exchanges++;

        double swapD = 0.0;
        int swapI = 0;

        if (AB.isVector())
        {// vector
            if (AB.isRowVector())
            {// row-vector
                swapD = AB.getM(0, i);
                AB.setM(0, i, AB.getM(0, j));
                AB.setM(0, j, swapD);

                if (this.computeSortedIndex)
                {
                    swapI = AB.getI(0, i);
                    AB.setI(0, i, AB.getI(0, j));
                    AB.setI(0, j, swapI);
                }
            }
            else
            {// col-vector
                swapD = AB.getM(i, 0);
                AB.setM(i, 0, AB.getM(j, 0));
                AB.setM(j, 0, swapD);

                if (this.computeSortedIndex)
                {
                    swapI = AB.getI(i, 0);
                    AB.setI(i, 0, AB.getI(j, 0));
                    AB.setI(j, 0, swapI);
                }

            }
        }
        else
        {// matrix
            if (AB.getDimension() == Dimension.ROW)
            {// row-wise
             // System.out.println("AB[" + i + " , " + numRowCols + "]");
                swapD = AB.getM(i, numRowCols);
                // double abget = AB.getM(j,numRowCols);
                // System.out.println("AB["+numRowCols+" , "+j+"] = "+abget);
                AB.setM(i, numRowCols, AB.getM(j, numRowCols));
                AB.setM(j, numRowCols, swapD);

                if (this.computeSortedIndex)
                {
                    swapI = AB.getI(i, numRowCols);
                    AB.setI(i, numRowCols, AB.getI(j, numRowCols));
                    AB.setI(j, numRowCols, swapI);
                }
            }
            else
            {// column-wise
                swapD = AB.getM(numRowCols, i);
                AB.setM(numRowCols, i, AB.getM(numRowCols, j));
                AB.setM(numRowCols, j, swapD);

                if (this.computeSortedIndex)
                {
                    swapI = AB.getI(numRowCols, i);
                    AB.setI(numRowCols, i, AB.getI(numRowCols, j));
                    AB.setI(numRowCols, j, swapI);
                }
            }
        }
    }

    // shuffle the array b[]
    private void shuffle(QsortMatrixWithIndices AB, int numRowCols)
    {
        int N = 0;

        if (AB.isVector())
        {
            N = AB.length();
        }
        else
        {
            if (AB.getDimension() == Dimension.ROW)
            {
                N = AB.getNumRows();
            }
            else
            {
                N = AB.getNumCols();
            }
        }

        // System.out.println("N = " + N);
        for (int i = 0; i < N; i++)
        {
            int r = i + (int) (Math.random() * (N - i)); // between rowI and N-1
            exch(AB, i, r, numRowCols);
        }
    }

    // test client
    private static void mainExample()
    {
        int M = 1;
        int N = 1500000;// Integer.parseInt(args[0]);

        boolean print = false;

        Matrix Mat = genTestMatrix();// .getRowAt(0).transpose();//genRandMatrix(M,
                                     // N).round();
        /*
         * Indices MatInd = Mat.generateIndices();
         * MatInd.printInLabel("MatInd1", 0); MatInd =
         * Mat.generateIndices(false); MatInd.printInLabel("MatInd2", 0);
         */

        if (true)
        {
            // return;
        }

        Dimension dimMat = null;// Dimension.COL;
        if (dimMat == Dimension.ROW)
        {
            System.out.println(" numRows = " + Mat.getRowDimension());
        }
        else if (dimMat == null)
        {
            System.out.println(" numRows = " + Mat.getRowDimension());
        }
        else
        {
            System.out.println(" numCols = " + Mat.getColumnDimension());
        }

        if (print)
        {
            Mat.printInLabel("Mat1");
            Mat.generateIndices().printInLabel("Mat1_Indices", 0);
        }

        // double[] b = Mat.getRowPackedCopy();
        // Mat.printInLabel("Mat1", 0);

        // sort them
        System.out.println("===BEGIN===");
        long start = System.currentTimeMillis();
        QuickSort sort = new QuickSortMat(Mat, dimMat, true, true);
        long stop = System.currentTimeMillis();
        System.out.println("===END===");

        Matrix A = (Matrix) sort.getSortedObject();// new
                                                   // Matrix(sort.getDwIndex().getA());
        Indices B = sort.getIndices();
        double elapsed = (stop - start) / 1000.0;
        System.out.println("Quicksort:   " + elapsed + " seconds");

        if (true)
        {
            // return;
        }

        Mat.printInLabel("Mat2", 0);
        A.printInLabel("A", 0);
        if (B != null)
        {
            B.plus(1).printInLabel("B", 0);
        }

        if (print)
        {
            A.printInLabel("B");
            Indices ind = sort.getIndices();
            ind.printInLabel("Quicksort Index", 0);
        }

        if (true)
        {
            // return;
        }

        start = System.currentTimeMillis();
        // Matrix C = JDatafun.sort(Mat);
        Sort sortMat = new SortMat(Mat);
        Matrix C = (Matrix) sortMat.getSortedObject();
        stop = System.currentTimeMillis();
        elapsed = (stop - start) / 1000.0;
        System.out.println("Bubblesort:   " + elapsed + " seconds");

        if (print)
        {
            // C.printInLabel("C");
            // sort.dwIndex().getIndex().printInLabel("Bubblesort Index", 0);
        }
        // if (print) {
        // A.printInLabel("[Mat ; A]", 0);
        // }

        // boolean EQUALITY = A.minus(C).EQ(0.0).allBoolean();
        // System.out.println("EQUALITY = " + EQUALITY);
    }

    static Matrix genRandMatrix(int M, int N)
    {
        return genRandMatrix(M, N, 100.0);
    }

    static Matrix genRandMatrix(int M, int N, double scale)
    {
        Matrix Mat = Matrix.random(M, N);
        Mat = Mat.arrayTimes(scale);
        return Mat;
    }

    private static Matrix genTestMatrix()
    {
        double[][] a =
        {
                {
                        88, 79, 65, 31, 42, 78, 53
                },
                {
                        4, 52, 86, 82, 44, 2, 90
                },
                {
                        17, 79, 3, 59, 10, 40, 32
                },
                {
                        86, 95, 85, 90, 83, 12, 72
                },
                {
                        24, 2, 43, 62, 41, 75, 81
                },
                {
                        23, 97, 88, 17, 53, 28, 6
                },
                {
                        27, 31, 95, 42, 98, 28, 29
                },
                {
                        93, 87, 81, 83, 71, 5, 28
                }, /*
                    * {73, 11, 60, 87, 1, 100, 77}, {96, 2, 6, 42, 19, 66, 58},
                    * {52, 46, 83, 97, 26, 59, 10}, {9, 62, 80, 44, 97, 68, 85},
                    * {18, 1, 30, 50, 36, 11, 57}, {19, 97, 68, 42, 32, 40, 57},
                    * {23, 91, 69, 93, 20, 45, 48}
                    */
        };
        return new Matrix(a);
    }

    private static void example2()
    {
        Matrix Mat = genTestMatrix();
        Mat.printInLabel("Mat1", 0);
        QuickSort sort = new QuickSortMat(Mat, Dimension.COL);// ,
                                                              // Dimension.ROW,
                                                              // true);
        long stop = System.currentTimeMillis();
        // new Matrix(b).printInLabel("b", 0);
        Matrix A = (Matrix) sort.getSortedObject();// new
                                                   // Matrix(sort.getDwIndex().getA());
        Indices B = sort.getIndices();

        // Mat.printInLabel("Mat2", 0);
        A.printInLabel("A", 0);
        if (B != null)
        {
            B.printInLabel("B", 0);
        }
    }

    static void example3()
    {
        int n = 2000000;
        int c = 100;
        Matrix randmat = Matrix.rand(1, n).scale(100);
        randmat.getMatrix(0, 0, 0, c).printInLabel("randmat : size[1," + randmat.length() + "]");

        long start = System.currentTimeMillis();
        QuickSort sort = new QuickSortMat(randmat);// , Dimension.ROW, true);
        long stop = System.currentTimeMillis();

        long lapsed = (stop - start);
        System.out.println("lapsed = " + lapsed);

        // new Matrix(b).printInLabel("b", 0);
        Matrix A = (Matrix) sort.getSortedObject();// new
                                                   // Matrix(sort.getDwIndex().getA());
        int lenA = A.length();
        A.getMatrix(0, 0, lenA - c, lenA - 1).printInLabel("A : size[1," + lenA + "]");
        // Indices B = sort.getIndices()
    }

    public static void main(String[] args)
    {
        // mainExample();
        // example2();
        example3();
    }
}
