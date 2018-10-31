package jamaextension.jamax.unsupervised.subspace;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.KeyValue;
import jamaextension.jamax.Matrix;

public abstract class FeatureExtractions
{
    /*
     * protected Matrix coeff; protected Matrix score; protected Matrix latent;
     * protected Matrix tsquared; protected Matrix explained; protected Matrix
     * mu;
     */

    protected Matrix data;
    protected boolean built;
    protected KeyValue parameters;
    protected int numDimension;

    public FeatureExtractions(Matrix data)
    {
        if (data == null || data.isNull())
        {
            throw new ConditionalRuleException("FeatureExtractions : Input data must be non-null.");
        }
        if (data.isVector())
        {
            throw new ConditionalRuleException("FeatureExtractions : Input data must be a matrix and not a vector.");
        }
        this.data = data;
        this.parameters = new KeyValue();
        // default is 2
        this.numDimension = 2;
    }

    public Matrix getData()
    {
        return data;
    }

    public int getNumDimension()
    {
        return numDimension;
    }

    public void setNumDimension(int numDimension)
    {
        if (numDimension < 2)
        {
            this.numDimension = 2;
            System.out
                    .println("setNumDimension : Number of dimensions specified is less than 2, so setting dimension = 2.");
        }
        else
        {
            this.numDimension = numDimension;
        }
    }

    public boolean isBuilt()
    {
        return built;
    }

    public void addParameter(String key, Object value)
    {
        isKeyValid(key);
        this.parameters.add(key, value);
    }

    public Object get(String key)
    {
        return this.parameters.getKey(key);
    }

    public boolean containsParameterKey(String key)
    {
        return this.parameters.containsKey(key);
    }

    public KeyValue getParameters()
    {
        return parameters;
    }

    public static boolean isNull(Object obj)
    {
        boolean tf = obj == null;
        return tf;
    }

    public abstract void buildFeatures();

    protected abstract void isKeyValid(String key);

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
