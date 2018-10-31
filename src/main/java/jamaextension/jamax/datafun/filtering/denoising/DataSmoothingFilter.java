package jamaextension.jamax.datafun.filtering.denoising;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.KeyValue;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

public abstract class DataSmoothingFilter
{
    protected Matrix dataIn;
    protected Matrix dataOut;
    protected Dimension dim;
    protected KeyValue parameters;
    protected boolean filtered;

    public DataSmoothingFilter(Matrix dataIn, Dimension dim)
    {
        if (dataIn == null || dataIn.isNull())
        {
            throw new ConditionalException(
                    "DataSmoothingFilter : Input argument matrix \"dataIn\" must be non-null or non-empty.");
        }

        this.dataIn = dataIn;

        if (dim == null)
        {
            throw new ConditionalException("DataSmoothingFilter : Input argument \"dim\" must be non-null.");
        }

        int row = dataIn.getRowDimension();
        int col = dataIn.getColumnDimension();

        this.dataOut = new Matrix(row, col);

        this.dim = dim;
        this.parameters = new KeyValue();
    }

    public abstract void filter();

    /**
     * @return the dataOut
     */
    public Matrix getDataOut()
    {
        if (!filtered)
        {
            throw new ConditionalException("getDataOut : Method \"filter\" must be called first.");
        }
        return dataOut;
    }

    /**
     * @return the parameters
     */
    public KeyValue getParameters()
    {
        return parameters;
    }

    public boolean isEmptyParameters()
    {
        // parameters.add(key, value);
        return parameters.isempty();
    }

    public void addParameter(String key, Object value)
    {
        parameters.add(key, value);
        // this.parameters.add
    }

    public boolean isDataInMatrix()
    {
        return !this.dataIn.isVector();
    }

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
