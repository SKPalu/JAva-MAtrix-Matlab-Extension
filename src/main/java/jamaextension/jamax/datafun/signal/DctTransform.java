package jamaextension.jamax.datafun.signal;

import org.apache.commons.math3.transform.DctNormalization;
import org.apache.commons.math3.transform.TransformType;

import jamaextension.jamax.Cell;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

import org.apache.commons.math3.transform.FastCosineTransformer;

public class DctTransform
{

    // private Matrix dctData;
    // private Dimension dim;
    // private TransformType transform;
    private static FastCosineTransformer DCT = new FastCosineTransformer(DctNormalization.STANDARD_DCT_I);// FastCosineTransformer();

    private DctTransform()
    {
        // this(Dimension.COL);
    }

    /*
     * public DctTransform(Dimension dim) { this(dim, TransformType.FORWARD); }
     * 
     * public DctTransform(Dimension dim, TransformType ttype) { }
     */

    public static Matrix dct(Matrix data, Dimension dim)
    {
        // validateArg(data, dim);

        Matrix trans = transform(data, TransformType.FORWARD, dim);

        return trans;
    }

    public static Matrix idct(Matrix data, Dimension dim)
    {
        // validateArg(data, ttype, dim);

        Matrix trans = transform(data, TransformType.INVERSE, dim);

        return trans;
    }

    static Matrix transformVector(Matrix data, TransformType ttype)
    {
        boolean isRow = data.isRowVector();

        double[] datArr = data.getRowPackedCopy();

        datArr = DCT.transform(datArr, ttype);

        Matrix transData = new Matrix(datArr);
        if (!isRow)
        {
            transData = transData.transpose();
        }

        return transData;
    }

    static Matrix transformMatrix(Matrix data, TransformType ttype, Dimension dim)
    {
        int M = data.getRowDimension();
        int N = data.getColumnDimension();

        Matrix transData = new Matrix(M, N);
        // double[] datArr = null;

        if (dim == Dimension.ROW)
        {
            for (int j = 0; j < N; j++)
            {
                Matrix colJ = data.getColumnAt(j);
                double[] datArr = colJ.getRowPackedCopy();
                datArr = DCT.transform(datArr, ttype);
                colJ = new Matrix(datArr).toColVector();
                transData.setColumnAt(j, colJ);
            }
        }
        else
        {
            for (int i = 0; i < M; i++)
            {
                Matrix rowI = data.getRowAt(i);
                double[] datArr = rowI.getRowPackedCopy();
                datArr = DCT.transform(datArr, ttype);
                rowI = new Matrix(datArr);
                transData.setRowAt(i, rowI);
            }
        }

        return transData;
    }

    static Matrix transform(Matrix data, TransformType ttype, Dimension dim)
    {
        Matrix trans = null;

        validateArg(data, ttype, dim);

        boolean isVector = data.isVector();
        if (isVector)
        {
            trans = transformVector(data, ttype);
        }
        else
        {
            trans = transformMatrix(data, ttype, dim);
        }

        return trans;
    }

    static void validateArg(Matrix data, TransformType ttype, Dimension dim)
    {
        boolean isNull = data == null || data.isNull();
        if (isNull)
        {
            throw new IllegalArgumentException("Matrix input argument must be non-null.");
        }
        if (ttype == null)
        {
            throw new IllegalArgumentException("TransformType input argument must be non-null.");
        }
        if (dim == null)
        {
            throw new IllegalArgumentException("Dimension input argument must be non-null.");
        }
    }

    static Matrix weighting(Matrix data, Dimension dim)
    {
        Matrix weights = null;
        boolean isvector = data.isVector();
        if (isvector)
        {
        }
        else
        {
        }
        return weights;
    }

    static double[] weighting(double[] dat)
    {
        return null;
    }

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
