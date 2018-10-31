package jamaextension.jamax.datafun;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.TestData;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.processtrend.MovingWindowMatrix;

public class MovingMedian
{

    private Matrix data;
    private Matrix medianSeries;
    private Matrix medianSeriesOriginal;
    private int window = 3;
    private Dimension dim;
    static final int minWin = 3;

    public MovingMedian(Matrix data)
    {
        this(data, null);
    }

    public MovingMedian(Matrix data, Dimension dim)
    {
        this(data, dim, minWin);
    }

    public Matrix getMedianSeries()
    {
        return medianSeries;
    }

    public Matrix getMedianSeriesOriginal()
    {
        return medianSeriesOriginal;
    }

    public int getWindow()
    {
        return window;
    }

    public Dimension getDim()
    {
        return dim;
    }

    public MovingMedian(Matrix data, Dimension dim, int win)
    {
        if (data == null || data.isNull())
        {
            throw new IllegalArgumentException("First input argument \"data\" must be non-null.");
        }
        this.data = data;

        if (dim == null)
        {
            this.dim = Dimension.ROW;
        }
        else
        {
            this.dim = dim;
        }
        if (win < minWin)
        {
            this.window = minWin;
        }
        else
        {
            this.window = win;
        }

        computeMovMedian();
    }

    private void movMedVector()
    {
        int len = data.length();
        if (len < this.window)
        {
            throw new IllegalArgumentException("Length of first input vector argument \"data\" (=" + len
                    + ") must equal window (=" + this.window + ").");
        }

        boolean isRow = data.isRowVector();

        Matrix dt = null;
        if (isRow)
        {
            dt = data;
        }
        else
        {
            dt = data.transpose();
        }

        // import
        // jamaextension.jamax.datafun.processtrend.MovingWindowMatrix;
        MovingWindowMatrix MWM = new MovingWindowMatrix(dt, this.window, this.window - 1);
        Matrix wmat = MWM.getWindowedData();
        int nr = wmat.getRowDimension();

        this.medianSeries = Matrix.zeros(1, nr);
        this.medianSeriesOriginal = new Matrix(1, dt.length() , Double.NaN);
        for (int i = 0; i < nr; i++)
        {
            Matrix rowI = wmat.getRowAt(i);
            Median med = new Median(rowI, dim);
            double medVal = med.getMedian().start();
            this.medianSeries.setElementAt(i, medVal);
        }

        int from = this.window - 1;
        int to = this.medianSeriesOriginal.length() - 1;
        this.medianSeriesOriginal.setElements(from, to, this.medianSeries);
        if (!isRow)
        {
            this.medianSeries = this.medianSeries.transpose();
            this.medianSeriesOriginal = this.medianSeriesOriginal.transpose();
        }

    }

    private void moveMedMatrix()
    {
        int nr = data.getRowDimension();
        int nc = data.getColumnDimension();
        int npoints = -1;
        MovingWindowMatrix MWM = null;
        Matrix wmat = null;
        int from = this.window - 1;
        int to = -1;
        int[] indArr = null;

        if (dim == Dimension.ROW)
        {
            if (nr < this.window)
            {
                throw new IllegalArgumentException("Number of rows for argument \"data\" (=" + nr
                        + ") must equal window (=" + this.window + ").");
            }

            Matrix colJ = data.getColumnAt(0);
            MWM = new MovingWindowMatrix(colJ, this.window, this.window - 1);
            wmat = MWM.getWindowedData();
            npoints = wmat.getRowDimension();

            this.medianSeries = Matrix.zeros(npoints, nc);
            this.medianSeriesOriginal = new Matrix(nr , nc, Double.NaN);

            for (int j = 0; j < nc; j++)
            {
                colJ = data.getColumnAt(j);
                MWM = new MovingWindowMatrix(colJ, this.window, this.window - 1);
                wmat = MWM.getWindowedData();

                for (int u = 0; u < npoints; u++)
                {
                    Matrix rowVec = wmat.getRowAt(u);
                    Median med = new Median(rowVec, dim);
                    double medVal = med.getMedian().start();
                    this.medianSeries.set(u, j, medVal);
                }

            }

            to = nr - 1;
            indArr = Indices.linspace(from, to, 1).getRowPackedCopy();
            this.medianSeriesOriginal.setRows(indArr, this.medianSeries);
            // .setElements(from, to, this.medianSeries);
        }
        else
        {
            if (nc < this.window)
            {
                throw new IllegalArgumentException("Number of columns for argument \"data\" (=" + nc
                        + ") must equal window (=" + this.window + ").");
            }

            Matrix rowI = data.getRowAt(0);
            MWM = new MovingWindowMatrix(rowI, this.window, this.window - 1);
            wmat = MWM.getWindowedData();
            npoints = wmat.getRowDimension();

            this.medianSeries = Matrix.zeros(nr, npoints);
            this.medianSeriesOriginal = new Matrix(nr, nc , Double.NaN);

            for (int i = 0; i < nr; i++)
            {
                rowI = data.getRowAt(i);
                MWM = new MovingWindowMatrix(rowI, this.window, this.window - 1);
                wmat = MWM.getWindowedData();

                for (int u = 0; u < npoints; u++)
                {
                    Matrix rowVec = wmat.getRowAt(u);
                    Median med = new Median(rowVec, dim);
                    double medVal = med.getMedian().start();
                    this.medianSeries.set(i, u, medVal);
                }
            }

            to = nc - 1;
            indArr = Indices.linspace(from, to, 1).getRowPackedCopy();
            this.medianSeriesOriginal.setColumns(indArr, this.medianSeries);
        }

    }

    private void computeMovMedian()
    {
        Matrix x = this.data;
        if (x.isVector())
        {
            // If input is a vector, calculate single value of output.
            movMedVector();
        }
        else
        {// --------------------------------------------------------------

            moveMedMatrix();

            // this.median = yMat;
        }// ---------------------------------------------------------------------
    }

    static void testExam()
    {
        Matrix vMat = TestData.testMat1();

        vMat.printInLabel("vMat", 0);
         vMat = vMat.getRowAt(0)
;
         
         vMat.printInLabel("vMat2", 0);

        Dimension dim = Dimension.COL;
        MovingMedian med = new MovingMedian(vMat, dim);
        Matrix medMat = med.getMedianSeries();
        medMat.printInLabel("MedianSeries");
        Matrix medMatO = med.getMedianSeriesOriginal();
        medMatO.printInLabel("MedianSeriesO");
    }

    public static void main(String[] args)
    {
        testExam();

    }

}
