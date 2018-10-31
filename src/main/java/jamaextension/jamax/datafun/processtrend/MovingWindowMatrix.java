package jamaextension.jamax.datafun.processtrend;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.Tuple;

public class MovingWindowMatrix
{

    private Matrix rawData;
    private int windowLength;
    private int windowOverlap;
    private Matrix windowedData;
    private Indices windowedIndices;
    private boolean includeAll;

    public MovingWindowMatrix(double[] data, int winLen, int overlap)
    {
        this(data, winLen, overlap, true);
    }

    public MovingWindowMatrix(double[] data, int winLen, int overlap, boolean incAll)
    {
        this(new Matrix(data), winLen, overlap, incAll);
    }

    public MovingWindowMatrix(Matrix data, int winLen, int overLap)
    {
        this(data, winLen, overLap, true);
    }

    public MovingWindowMatrix(Matrix data, int winLen, int overLap, boolean incAll)
    {
        if (data == null || data.isNull())
        {
            throw new IllegalArgumentException("Null \"data\" found.");
        }
        // this.vector = data.isVector();
        // this.data = data;
        if (!data.isVector())
        {
            throw new IllegalArgumentException("Input argument \"data\" must be a vector and not a matrix.");
        }
        if (data.isColVector())
        {
            this.rawData = data.toRowVector();
        }
        else
        {
            this.rawData = data;
        }

        int len = this.rawData.length();
        if (overLap >= winLen)
        {
            throw new IllegalArgumentException("Input argument \"winLen\" (= " + winLen
                    + ") must be greater than \"overlap\" (= " + overLap + ").");
        }
        if (winLen > len)
        {
            throw new IllegalArgumentException("Input argument \"winLen\" (= " + winLen
                    + ") must be less than number of elements in \"data\" (= " + len + ").");
        }
        if (overLap < 1)
        {
            //throw new IllegalArgumentException("Input argument \"overlap\" (= " + overLap
            //        + ") must be greater than zero.");
        }

        this.includeAll = incAll;
        this.windowLength = winLen;
        this.windowOverlap = overLap;

        Tuple<Matrix, Indices> obj = window(data, this.windowLength, this.windowOverlap, incAll);

        this.windowedData = obj.x;
        this.windowedIndices = obj.y;
    }

    public boolean isIncludeAll()
    {
        return includeAll;
    }

    public Matrix getRawData()
    {
        return rawData;
    }

    public Matrix getWindowedData()
    {
        return windowedData;
    }

    public Indices getWindowedIndices()
    {
        return windowedIndices;
    }

    public static Tuple<Matrix, Indices> window(Matrix data, int win, int ovr)
    {
        return window(data, win, ovr, true);
    }

    public static Tuple<Matrix, Indices> window(Matrix data, int win, int ovr, boolean incAll)
    {
        if (data == null || data.isNull())
        {
            throw new IllegalArgumentException("Null \"data\" found.");
        }

        int N = win;
        int L = data.length();

        Matrix X = data;
        if (X.isColVector())
        {
            X = X.toRowVector();
        }

        Indices XIr = Indices.linspace(0, L - 1).flipLR();// length(X):-1:1;

        int from = 0;
        int to = 0;
        int M = 0;

        int OV = win - ovr;

        boolean first = true;

        while (to < L)
        {
            if (first)
            {
                from = 0;
                first = false;
            }
            else
            {
                from = from + OV;
            }
            to = from + win - 1;
            if (to >= L)
            {
                break;
            }

            M = M + 1;

        }

        Indices vecsArr = new Indices(M, N);
        int count = 0;
        first = true;
        from = 0;
        to = 0;
        int[] fromTo = null;

        while (to < L)
        {
            if (first)
            {
                from = 0;
                first = false;
            }
            else
            {
                from = from + OV;
            }
            to = from + win - 1;
            if (to >= L)
            {
                break;
            }

            fromTo = Indices.linspace(from, to).getRowPackedCopy();
            Indices rowI = XIr.getEls(fromTo).toRowVector();// (from:to);

            vecsArr.setRowAt(count, rowI);
            // vecsArr(count,:) = rowI;
            count++;// = count + 1;
        }

        // vecsArr.printInLabel("vecsArr");

        vecsArr = vecsArr.rot90(2);// rot90(vecsArr,2);
        int startI = vecsArr.start();// (1);
        Matrix Xv = new Matrix(M, N);// X(vecsArr);
        int[] colIndArr = null;// vecsArr.toColVector().getRowPackedCopy();
        for (int i = 0; i < M; i++)
        {
            colIndArr = vecsArr.getRowAt(i).getRowPackedCopy();
            Matrix colXv = X.getEls(colIndArr).toRowVector();
            Xv.setRowAt(i, colXv);// .setElements(colIndArr, colXv);
        }

        if (incAll)
        {
            if (startI > 0)
            {
                startI = 0;
                Indices vecs1Win = Indices.linspace(0, win - 1);
                int[] toWin = vecs1Win.getRowPackedCopy();
                Matrix x1Win = X.getEls(toWin).toRowVector();
                Xv = x1Win.mergeV(Xv);// Xv = [X(1:win); Xv ];
                vecsArr = vecs1Win.mergeV(vecsArr); // vecsArr =
                                                    // [(1:win);vecsArr];
            }
        }

        Tuple<Matrix, Indices> obj = new Tuple<Matrix, Indices>(Xv, vecsArr);

        return obj;
    }

    public static void main(String[] args)
    {

        double[] x =
        {
                9, 7, 3, 7, 13, 13, 16, 11, 4, 11, 2, 13, 10, 11, 7
        };
        Matrix X = new Matrix(x);// Matrix.rand(1,
                                 // 15).scale(20).plus(1).round();
        X.printInLabel("X", 0);

        MovingWindowMatrix MWM = new MovingWindowMatrix(X, 7, 4);
        Matrix MW = MWM.getWindowedData();
        MW.printInLabel("MW", 0);
        Indices MI = MWM.getWindowedIndices();
        MI.printInLabel("MI");

        System.out.println("End");
    }

}
