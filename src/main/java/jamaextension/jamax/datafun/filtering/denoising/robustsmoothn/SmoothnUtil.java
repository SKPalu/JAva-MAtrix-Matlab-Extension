package jamaextension.jamax.datafun.filtering.denoising.robustsmoothn;

import jamaextension.jamax.Parameters;

public final class SmoothnUtil
{

    private SmoothnUtil()
    {
    }

    // Parameters(Object key, Object value)
    public static Parameters getSmoothnDefaultOption()
    {
        Parameters PM = new Parameters();
        
        PM.add("TolZ", 1e-3);
        PM.add("MaxIter", 100);
        PM.add("Initial", null);
        PM.add("Weights", "bisquare");
        PM.add("Order", 2);
        
        return PM;
    }

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
