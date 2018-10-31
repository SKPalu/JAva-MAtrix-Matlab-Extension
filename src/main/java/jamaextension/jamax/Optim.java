/*
 * Optim.java
 *
 * Created on 19 April 2007, 03:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax;

import java.lang.reflect.Field;
import java.util.ArrayList;

import jamaextension.jamax.constants.OptDisplay;
import jamaextension.jamax.funfun.FunctionFunctions;

/**
 * 
 * @author Feynman Perceptrons
 */
public class Optim
{

    protected OptDisplay Display;// = OptDisplay.Off;// [ off | on | iter |
                                 // notify | final ]\n');
    protected Object MaxFunEvals;// = new Integer(50);//: [ positive scalar
                                 // ]\n');
    protected Object MaxIter;// = new Integer(50);//: [ positive scalar ]\n');
    protected Double TolFun;// = 0.01;//: [ positive scalar ]\n');
    protected Double TolX;// = 0.01;//: [ positive scalar ]\n');
    protected Boolean FunValCheck;// = false;//: [ {off} | on ]\n');
    protected ArrayList<FunctionFunctions> OutputFcn;// : [ function | {[]}
                                                     // ]\n');
    protected ArrayList<FunctionFunctions> PlotFcns;
    protected ArrayList<String> varNames = new ArrayList<String>();

    /**
     * Creates a new instance of Optim
     */
    public Optim()
    {
        // storeVarNames();
        storeFieldNames();
    }

    public boolean isOptimVar(String varName)
    {
        boolean exist = isVarExist(varName);
        if (!exist)
        {
            return false;
        }
        String[] str =
        {
                "Display", "MaxFunEvals", "MaxIter", "TolFun", "TolX", "FunValCheck", "OutputFcn", "PlotFcns"
        };
        int len = str.length;
        for (int i = 0; i < len; i++)
        {
            if (str[i].equals(varName))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isVarExist(String varName)
    {
        int siz = varNames.size();
        // int count = 0;
        // boolean tf = false;
        for (int i = 0; i < siz; i++)
        {
            String str = this.varNames.get(i).toLowerCase();
            if (str.equals(varName.toLowerCase()))
            {
                return true;
                // break;
            }
        }

        return false;
    }

    public void setDisplay(OptDisplay d)
    {
        if (d == null)
        {
            return;
        }
        this.Display = d;
    }

    public OptDisplay getDisplay()
    {
        return this.Display;
    }

    public void setMaxFunEvals(Object mfe)
    {
        this.MaxFunEvals = mfe;
    }

    public Object getMaxFunEvals()
    {
        return this.MaxFunEvals;
    }

    public void setMaxIter(Integer mi)
    {
        this.MaxIter = mi;
    }

    public Object getMaxIter()
    {
        return this.MaxIter;
    }

    public void setTolFun(Double tf)
    {
        this.TolFun = tf;
    }

    public Double getTolFun()
    {
        return this.TolFun;
    }

    public void setTolX(Double tx)
    {
        this.TolX = tx;
    }

    public Double getTolX()
    {
        return this.TolX;
    }

    public void setFunValCheck(Boolean d)
    {
        this.FunValCheck = d;
    }

    public Boolean isFunValCheck()
    {
        return this.FunValCheck;
    }

    public void setOutputFcn(Object obj)
    {
        if (obj == null)
        {
            return;
        }
        boolean cond = obj instanceof FunctionFunctions;
        if (cond)
        {
            this.OutputFcn = new ArrayList<FunctionFunctions>();
            this.OutputFcn.add((FunctionFunctions) obj);
        }
        else if (obj instanceof ArrayList)
        {
            ArrayList list = (ArrayList) obj;
            int siz = list.size();
            if (siz == 0)
            {
                return;
            }
            // if(obj instanceof )
            // Object objList = list.get(0);
            this.OutputFcn = (ArrayList<FunctionFunctions>) obj;
        }
        else
        {
            throw new ConditionalException(
                    "setOutputFcn : Parameter \"obj\" must either be instance of \"FunctionFunctions\" or \"ArrayList<FunctionFunctions>()\".");
        }
    }

    public ArrayList<FunctionFunctions> getOutputFcn()
    {
        return this.OutputFcn;
    }

    /**
     * @return the PlotFcns
     */
    public ArrayList<FunctionFunctions> getPlotFcns()
    {
        return PlotFcns;
    }

    /**
     * @param PlotFcns
     *            the PlotFcns to set
     */
    public void setPlotFcns(Object obj)
    {
        // this.PlotFcns = PlotFcns;
        if (obj == null)
        {
            return;
        }
        boolean cond = obj instanceof FunctionFunctions;
        if (cond)
        {
            this.PlotFcns = new ArrayList<FunctionFunctions>();
            this.PlotFcns.add((FunctionFunctions) obj);
        }
        else if (obj instanceof ArrayList)
        {
            ArrayList list = (ArrayList) obj;
            int siz = list.size();
            if (siz == 0)
            {
                return;
            }
            // if(obj instanceof )
            // Object objList = list.get(0);
            this.PlotFcns = (ArrayList<FunctionFunctions>) obj;
        }
        else
        {
            throw new ConditionalException(
                    "setPlotFcns : Parameter \"obj\" must either be instance of \"FunctionFunctions\" or \"ArrayList<FunctionFunctions>()\".");
        }
    }

    public void printVarNames()
    {
        int len = varNames.size();
        for (int i = 0; i < len; i++)
        {
            String var = varNames.get(i);
            System.out.println(var + " :");
        }
    }

    private void storeFieldNames()
    {
        Class opt = Optim.class;
        Field[] fields = opt.getDeclaredFields();
        int len = fields.length;
        for (int i = 0; i < len; i++)
        {
            this.varNames.add(fields[i].getName());
        }
    }

    private void storeVarNames()
    {
        String[] names =
        {
                "Display", "MaxFunEvals", "MaxIter", "TolFun", "TolX", "FunValCheck", "OutputFcn", "PlotFcns",
                "ActiveConstrTol", "Algorithm", "AlwaysHonorConstraints", "BranchStrategy", "DerivativeCheck",
                "Diagnostics", "DiffMaxChange", "DiffMinChange", "FinDiffType", "GoalsExactAchieve", "GradConstr",
                "GradObj", "HessFcn", "Hessian", "HessMult", "HessPattern", "HessUpdate", "InitialHessType",
                "InitialHessMatrix", "InitBarrierParam", "InitTrustRegionRadius", "Jacobian", "JacobMult",
                "JacobPattern", "LargeScale", "LevenbergMarquardt", "LineSearchType", "MaxNodes", "MaxPCGIter",
                "MaxProjCGIter", "MaxRLPIter", "MaxSQPIter", "MaxTime", "MeritFunction", "MinAbsMax",
                "NodeDisplayInterval", "NodeSearchStrategy", "NonlEqnAlgorithm", "NoStopIfFlatInfeas",
                "ObjectiveLimit", "PhaseOneTotalScaling", "Preconditioner", "PrecondBandWidth", "RelLineSrchBnd",
                "RelLineSrchBndDuration", "ScaleProblem", "ShowStatusWindow", "Simplex", "SubproblemAlgorithm",
                "TolCon", "TolConSQP", "TolGradCon", "TolPCG", "TolProjCG", "TolProjCGAbs", "TolRLPFun", "TolXInteger",
                "TypicalX", "UseParallel"
        };

        int len = names.length;
        for (int i = 0; i < len; i++)
        {
            varNames.add(names[i].trim());
        }

    }
}
