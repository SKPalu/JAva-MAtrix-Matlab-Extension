/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import jamaextension.jamax.constants.AHConstraints;
import jamaextension.jamax.constants.BStrategy;
import jamaextension.jamax.constants.FDType;
import jamaextension.jamax.constants.HUpdate;
import jamaextension.jamax.constants.IHessType;
import jamaextension.jamax.constants.LSearchType;
import jamaextension.jamax.constants.MFunction;
import jamaextension.jamax.constants.NonlEqnAlgo;
import jamaextension.jamax.constants.NsStrategy;
import jamaextension.jamax.constants.OptDisplay;
import jamaextension.jamax.constants.SPAlgo;
import jamaextension.jamax.constants.SProblem;
import jamaextension.jamax.constants.UParallel;
import jamaextension.jamax.funfun.FunctionFunctions;

/**
 * 
 * @author Sione
 */
public final class OptimOpt extends Optim
{

    // Display: []
    // MaxFunEvals: []
    // MaxIter: []
    // TolFun: []
    // TolX: []
    // FunValCheck: []
    // OutputFcn: []
    // PlotFcns: []
    private Object ActiveConstrTol;// : []
    private Object Algorithm;// : []
    private AHConstraints AlwaysHonorConstraints;// : []
    private BStrategy BranchStrategy;// : []
    private Boolean DerivativeCheck = false;// : []
    private Boolean Diagnostics = false;// : []
    private Double DiffMaxChange;// : []
    private Double DiffMinChange;// : []
    private FDType FinDiffType;// : []
    private Integer GoalsExactAchieve;// : []
    private Boolean GradConstr;// : []
    private Boolean GradObj = false;// : []
    private FunctionFunctions HessFcn;// : []
    private Boolean Hessian;// : []
    private ArrayList<FunctionFunctions> HessMult;// : []
    private Object HessPattern;// : []//Matrix
    private HUpdate HessUpdate;// : []
    private IHessType InitialHessType;// : []
    private Matrix InitialHessMatrix;// : []
    private Double InitBarrierParam;// : []
    private Double InitTrustRegionRadius;// : []
    private Boolean Jacobian = false;// : []
    private ArrayList<FunctionFunctions> JacobMult;// : []
    private Matrix JacobPattern;// : []
    private Boolean LargeScale = false;// : []
    private Boolean LevenbergMarquardt;// : []
    private LSearchType LineSearchType;// : []
    private Object MaxNodes;// : []
    private Object MaxPCGIter;// : []//Integer
    private Object MaxProjCGIter;// : []
    private Object MaxRLPIter;// : []
    private Integer MaxSQPIter;// : []
    private Integer MaxTime;// : []
    private MFunction MeritFunction;// : []
    private Integer MinAbsMax;// : []
    private Integer NodeDisplayInterval;// : []
    private NsStrategy NodeSearchStrategy;// : []
    private NonlEqnAlgo NonlEqnAlgorithm;// : []
    private Object NoStopIfFlatInfeas;// : []
    private Double ObjectiveLimit;// : []
    private Object PhaseOneTotalScaling;// : []
    private Object Preconditioner;// : []
    private Double PrecondBandWidth;// : []
    private Double RelLineSrchBnd;// : []
    private Integer RelLineSrchBndDuration;// : []
    private SProblem ScaleProblem;// : []
    private Object ShowStatusWindow;// : []
    private Boolean Simplex;// : []
    private SPAlgo SubproblemAlgorithm;// : []
    private Double TolCon;// : []
    private Double TolConSQP;// : []
    private Object TolGradCon;// : []
    private Double TolPCG;// : []
    private Double TolProjCG;// : []
    private Double TolProjCGAbs;// : []
    private Double TolRLPFun;// : []
    private Double TolXInteger;// : []
    private Object TypicalX;// : []//Matrix
    private UParallel UseParallel;// : []

    // private OptimOpt defaultopt;

    public OptimOpt()
    {
        super();
        storeFieldNames();
    }

    private void storeFieldNames()
    {
        Class opt = OptimOpt.class;
        Field[] fields = opt.getDeclaredFields();
        int len = fields.length;
        for (int i = 0; i < len; i++)
        {
            this.varNames.add(fields[i].getName());
        }
    }

    @Override
    public void printVarNames()
    {
        int len = varNames.size();
        for (int i = 0; i < len; i++)
        {
            String var = varNames.get(i);
            System.out.println(var + " :");
        }
    }

    public static Object optimget(Object obj, Object obj2)
    {
        if (obj == null)
        {
            return obj2;
        }
        return obj;
    }

    public Object optimget(String varName, OptimOpt defaultopt) throws NoSuchMethodException, IllegalArgumentException
    {
        // Class optimClass = OptimOpt.this.getClass();
        if (varName == null || "".equals(varName))
        {
            throw new ConditionalException("optimget : Parameter \"varName\" must be non-null nor emptry string.");
        }
        if (defaultopt == null)
        {
            throw new ConditionalException("optimget : Parameter \"defaultopt\" must be non-null.");
        }
        boolean varExist = this.isVarExist(varName.trim().toLowerCase());
        if (!varExist)
        {
            throw new ConditionalException("optimget : Variable name \"" + varName + "\" doesn't exist.");
        }

        Class optClass = OptimOpt.class;
        // Class optimClass = optClass.getSuperclass();
        Method meth = null;
        Method meth2 = null;
        if (this.isOptimVar(varName))
        {
            meth = optClass.getSuperclass().getDeclaredMethod("get" + varName, (Class[]) null);
            meth2 = defaultopt.getClass().getSuperclass().getDeclaredMethod("get" + varName, (Class[]) null);
            System.out.println("OPTIM VAR = TRUE");
        }
        else
        {
            meth = optClass.getDeclaredMethod("get" + varName, (Class[]) null);
            meth2 = defaultopt.getClass().getDeclaredMethod("get" + varName, (Class[]) null);
            System.out.println("OPTIM VAR = FALSE");
        }
        System.out.println("meth = " + meth.getName());
        Object[] objArr = null;
        Object[] objArr2 = null;
        if (meth != null)
        {
            Object Cobj = null;
            Object Cobj2 = null;
            try
            {
                Cobj = meth.invoke(null);
                Cobj2 = meth2.invoke(null);
            }
            catch (IllegalAccessException iae)
            {
            }
            catch (InvocationTargetException ite)
            {
            }
            objArr = (Object[]) Cobj;
            objArr2 = (Object[]) Cobj2;
        }
        else
        {
            throw new ConditionalException("optimget : Method invocation of  \"get" + varName + "\" is NULL.");
        }

        Object retObj = objArr[0];
        if (retObj != null)
        {
            return retObj;
        }

        retObj = objArr2[0];

        return retObj;
    }

    /*
     * public void setDefaultOpt(OptimOpt defaultopt) { if (defaultopt == null)
     * { throw new ConditionalException(
     * "setDefaultOpt : Parameter \"defaultopt\" must be non-null."); }
     * this.defaultopt = defaultopt; }
     */
    /**
     * @return the ActiveConstrTol
     */
    public Object getActiveConstrTol()
    {
        // if (ActiveConstrTol == null) {
        // return defaultopt.getActiveConstrTol();
        // }
        return ActiveConstrTol;
    }

    /**
     * @param ActiveConstrTol
     *            the ActiveConstrTol to set
     */
    public void setActiveConstrTol(Object ActiveConstrTol)
    {
        this.ActiveConstrTol = ActiveConstrTol;
    }

    /**
     * @return the Algorithm
     */
    public Object getAlgorithm()
    {
        // if (Algorithm == null) {
        // return defaultopt.getAlgorithm();
        // }
        return Algorithm;
    }

    /**
     * @param Algorithm
     *            the Algorithm to set
     */
    public void setAlgorithm(Object Algorithm)
    {
        this.Algorithm = Algorithm;
    }

    /**
     * @return the AlwaysHonorConstraints
     */
    public AHConstraints getAlwaysHonorConstraints()
    {
        // if (AlwaysHonorConstraints == null) {
        // return defaultopt.getAlwaysHonorConstraints();
        // }
        return AlwaysHonorConstraints;
    }

    /**
     * @param AlwaysHonorConstraints
     *            the AlwaysHonorConstraints to set
     */
    public void setAlwaysHonorConstraints(AHConstraints AlwaysHonorConstraints)
    {
        this.AlwaysHonorConstraints = AlwaysHonorConstraints;
    }

    /**
     * @return the BranchStrategy
     */
    public BStrategy getBranchStrategy()
    {
        // if (BranchStrategy == null) {
        // return defaultopt.getBranchStrategy();
        // }
        return BranchStrategy;
    }

    /**
     * @param BranchStrategy
     *            the BranchStrategy to set
     */
    public void setBranchStrategy(BStrategy BranchStrategy)
    {
        this.BranchStrategy = BranchStrategy;
    }

    /**
     * @return the DerivativeCheck
     */
    public Boolean getDerivativeCheck()
    {
        // if (DerivativeCheck == null) {
        // return defaultopt.getDerivativeCheck();
        // }
        return DerivativeCheck;
    }

    /**
     * @param DerivativeCheck
     *            the DerivativeCheck to set
     */
    public void setDerivativeCheck(Boolean DerivativeCheck)
    {
        this.DerivativeCheck = DerivativeCheck;
    }

    /**
     * @return the Diagnostics
     */
    public Boolean getDiagnostics()
    {
        // if (Diagnostics == null) {
        // return defaultopt.getDiagnostics();
        // }
        return Diagnostics;
    }

    /**
     * @param Diagnostics
     *            the Diagnostics to set
     */
    public void setDiagnostics(Boolean Diagnostics)
    {
        this.Diagnostics = Diagnostics;
    }

    /**
     * @return the DiffMaxChange
     */
    public Double getDiffMaxChange()
    {
        // if (DiffMaxChange == null) {
        // return defaultopt.getDiffMaxChange();
        // }
        return DiffMaxChange;
    }

    /**
     * @param DiffMaxChange
     *            the DiffMaxChange to set
     */
    public void setDiffMaxChange(Double DiffMaxChange)
    {
        this.DiffMaxChange = DiffMaxChange;
    }

    /**
     * @return the DiffMinChange
     */
    public Double getDiffMinChange()
    {
        // if (DiffMinChange == null) {
        // return defaultopt.getDiffMinChange();
        // }
        return DiffMinChange;
    }

    /**
     * @param DiffMinChange
     *            the DiffMinChange to set
     */
    public void setDiffMinChange(Double DiffMinChange)
    {
        this.DiffMinChange = DiffMinChange;
    }

    /**
     * @return the FinDiffType
     */
    public FDType getFinDiffType()
    {
        // if (FinDiffType == null) {
        // return defaultopt.getFinDiffType();
        // }
        return FinDiffType;
    }

    /**
     * @param FinDiffType
     *            the FinDiffType to set
     */
    public void setFinDiffType(FDType FinDiffType)
    {
        this.FinDiffType = FinDiffType;
    }

    /**
     * @return the GoalsExactAchieve
     */
    public Integer getGoalsExactAchieve()
    {
        // if (GoalsExactAchieve == null) {
        // return defaultopt.getGoalsExactAchieve();
        // }
        return GoalsExactAchieve;
    }

    /**
     * @param GoalsExactAchieve
     *            the GoalsExactAchieve to set
     */
    public void setGoalsExactAchieve(Integer GoalsExactAchieve)
    {
        this.GoalsExactAchieve = GoalsExactAchieve;
    }

    /**
     * @return the GradConstr
     */
    public Boolean getGradConstr()
    {
        // if (GradConstr == null) {
        // return defaultopt.getGradConstr();
        // }
        return GradConstr;
    }

    /**
     * @param GradConstr
     *            the GradConstr to set
     */
    public void setGradConstr(Boolean GradConstr)
    {
        this.GradConstr = GradConstr;
    }

    /**
     * @return the GradObj
     */
    public Boolean getGradObj()
    {
        // if (GradObj == null) {
        // return defaultopt.getGradObj();
        // }
        return GradObj;
    }

    /**
     * @param GradObj
     *            the GradObj to set
     */
    public void setGradObj(Boolean GradObj)
    {
        this.GradObj = GradObj;
    }

    /**
     * @return the HessFcn
     */
    public FunctionFunctions getHessFcn()
    {
        // if (HessFcn == null) {
        // return defaultopt.getHessFcn();
        // }
        return HessFcn;
    }

    /**
     * @param HessFcn
     *            the HessFcn to set
     */
    public void setHessFcn(FunctionFunctions HessFcn)
    {
        this.HessFcn = HessFcn;
    }

    /**
     * @return the Hessian
     */
    public Boolean getHessian()
    {
        // if (Hessian == null) {
        // return defaultopt.getHessian();
        // }
        return Hessian;
    }

    /**
     * @param Hessian
     *            the Hessian to set
     */
    public void setHessian(Boolean Hessian)
    {
        this.Hessian = Hessian;
    }

    /**
     * @return the HessMult
     */
    public ArrayList<FunctionFunctions> getHessMult()
    {
        // if (HessMult == null) {
        // return defaultopt.getHessMult();
        // }
        return HessMult;
    }

    /**
     * @param HessMult
     *            the HessMult to set
     */
    public void setHessMult(ArrayList<FunctionFunctions> HessMult)
    {
        this.HessMult = HessMult;
    }

    /**
     * @return the HessPattern
     */
    public Object getHessPattern()
    {
        // if (HessPattern == null) {
        // return defaultopt.getHessPattern();
        // }
        return HessPattern;
    }

    /**
     * @param HessPattern
     *            the HessPattern to set
     */
    public void setHessPattern(Object HessPattern)
    {
        if (HessPattern == null)
        {
            return;
        }
        String msg = "";
        boolean bool = !(HessPattern instanceof String) && !(HessPattern instanceof Matrix);
        if (bool)
        {
            msg = "Parameter \"HessPattern\" must be an instanceof \"String\" or \"Matrix\".";
            throw new ConditionalRuleException("setHessPattern", msg);
        }
        if (HessPattern instanceof String)
        {
            String str = (String) HessPattern;
            if ("".equals(str.trim()))
            {
                return;
            }
        }
        this.HessPattern = HessPattern;
    }

    /**
     * @return the HessUpdate
     */
    public HUpdate getHessUpdate()
    {
        // if (HessUpdate == null) {
        // return defaultopt.getHessUpdate();
        // }
        return HessUpdate;
    }

    /**
     * @param HessUpdate
     *            the HessUpdate to set
     */
    public void setHessUpdate(HUpdate HessUpdate)
    {
        this.HessUpdate = HessUpdate;
    }

    /**
     * @return the InitialHessType
     */
    public IHessType getInitialHessType()
    {
        // if (InitialHessType == null) {
        // return defaultopt.getInitialHessType();
        // }
        return InitialHessType;
    }

    /**
     * @param InitialHessType
     *            the InitialHessType to set
     */
    public void setInitialHessType(IHessType InitialHessType)
    {
        this.InitialHessType = InitialHessType;
    }

    /**
     * @return the InitialHessMatrix
     */
    public Matrix getInitialHessMatrix()
    {
        // if (InitialHessMatrix == null) {
        // return defaultopt.getInitialHessMatrix();
        // }
        return InitialHessMatrix;
    }

    /**
     * @param InitialHessMatrix
     *            the InitialHessMatrix to set
     */
    public void setInitialHessMatrix(Object InitialHessMatrix)
    {
        if (InitialHessMatrix == null)
        {
            return;
        }
        String msg = "";
        boolean bool = !(InitialHessMatrix instanceof Matrix) && !(InitialHessMatrix instanceof Number);
        if (bool)
        {
            msg = "Parameter \"InitialHessMatrix\" must be an instanceof \"Matrix\" or \"Number\".";
            throw new ConditionalRuleException("setTypicalX", msg);
        }
        if (InitialHessMatrix instanceof Matrix)
        {
            this.InitialHessMatrix = (Matrix) InitialHessMatrix;
        }
        else
        {
            Number num = (Number) InitialHessMatrix;
            this.InitialHessMatrix = new Matrix(1, 1, num.doubleValue());
        }
    }

    /**
     * @return the InitBarrierParam
     */
    public Double getInitBarrierParam()
    {
        // if (InitBarrierParam == null) {
        // return defaultopt.getInitBarrierParam();
        // }
        return InitBarrierParam;
    }

    /**
     * @param InitBarrierParam
     *            the InitBarrierParam to set
     */
    public void setInitBarrierParam(Double InitBarrierParam)
    {
        this.InitBarrierParam = InitBarrierParam;
    }

    /**
     * @return the InitTrustRegionRadius
     */
    public Double getInitTrustRegionRadius()
    {
        // if (InitTrustRegionRadius == null) {
        // return defaultopt.getInitTrustRegionRadius();
        // }
        return InitTrustRegionRadius;
    }

    /**
     * @param InitTrustRegionRadius
     *            the InitTrustRegionRadius to set
     */
    public void setInitTrustRegionRadius(Double InitTrustRegionRadius)
    {
        this.InitTrustRegionRadius = InitTrustRegionRadius;
    }

    /**
     * @return the Jacobian
     */
    public Boolean getJacobian()
    {
        // if (Jacobian == null) {
        // return defaultopt.getJacobian();
        // }
        return Jacobian;
    }

    /**
     * @param Jacobian
     *            the Jacobian to set
     */
    public void setJacobian(Boolean Jacobian)
    {
        this.Jacobian = Jacobian;
    }

    /**
     * @return the JacobMult
     */
    public ArrayList<FunctionFunctions> getJacobMult()
    {
        // if (JacobMult == null) {
        // return defaultopt.getJacobMult();
        // }
        return JacobMult;
    }

    /**
     * @param JacobMult
     *            the JacobMult to set
     */
    public void setJacobMult(ArrayList<FunctionFunctions> JacobMult)
    {
        this.JacobMult = JacobMult;
    }

    /**
     * @return the JacobPattern
     */
    public Matrix getJacobPattern()
    {
        // if (JacobPattern == null) {
        // return defaultopt.getJacobPattern();
        // }
        return JacobPattern;
    }

    /**
     * @param JacobPattern
     *            the JacobPattern to set
     */
    public void setJacobPattern(Matrix JacobPattern)
    {
        this.JacobPattern = JacobPattern;
    }

    /**
     * @return the LargeScale
     */
    public Boolean getLargeScale()
    {
        // if (LargeScale == null) {
        // return defaultopt.getLargeScale();
        // }
        return LargeScale;
    }

    /**
     * @param LargeScale
     *            the LargeScale to set
     */
    public void setLargeScale(Boolean LargeScale)
    {
        this.LargeScale = LargeScale;
    }

    /**
     * @return the LevenbergMarquardt
     */
    public Boolean getLevenbergMarquardt()
    {
        // if (LevenbergMarquardt == null) {
        // return defaultopt.getLevenbergMarquardt();
        // }
        return LevenbergMarquardt;
    }

    /**
     * @param LevenbergMarquardt
     *            the LevenbergMarquardt to set
     */
    public void setLevenbergMarquardt(Boolean LevenbergMarquardt)
    {
        this.LevenbergMarquardt = LevenbergMarquardt;
    }

    /**
     * @return the LineSearchType
     */
    public LSearchType getLineSearchType()
    {
        // if (LineSearchType == null) {
        // return defaultopt.getLineSearchType();
        // }
        return LineSearchType;
    }

    /**
     * @param LineSearchType
     *            the LineSearchType to set
     */
    public void setLineSearchType(LSearchType LineSearchType)
    {
        this.LineSearchType = LineSearchType;
    }

    /**
     * @return the MaxNodes
     */
    public Object getMaxNodes()
    {
        // if (MaxNodes == null) {
        // return defaultopt.getMaxNodes();
        // }
        return MaxNodes;
    }

    /**
     * @param MaxNodes
     *            the MaxNodes to set
     */
    public void setMaxNodes(Object MaxNodes)
    {
        this.MaxNodes = MaxNodes;
    }

    /**
     * @return the MaxPCGIter
     */
    public Object getMaxPCGIter()
    {
        // if (MaxPCGIter == null) {
        // return defaultopt.getMaxPCGIter();
        // }
        return MaxPCGIter;
    }

    /**
     * @param MaxPCGIter
     *            the MaxPCGIter to set
     */
    public void setMaxPCGIter(Object MaxPCGIter)
    {
        if (MaxPCGIter == null)
        {
            return;
        }
        String msg = "";
        boolean bool = !(MaxPCGIter instanceof String) && !(MaxPCGIter instanceof Integer);
        if (bool)
        {
            msg = "Parameter \"MaxPCGIter\" must be an instanceof \"String\" or \"Matrix\".";
            throw new ConditionalRuleException("setMaxPCGIter", msg);
        }
        if (MaxPCGIter instanceof String)
        {
            String str = (String) MaxPCGIter;
            if ("".equals(str.trim()))
            {
                return;
            }
        }
        this.MaxPCGIter = MaxPCGIter;
    }

    /**
     * @return the MaxProjCGIter
     */
    public Object getMaxProjCGIter()
    {
        // if (MaxProjCGIter == null) {
        // return defaultopt.getMaxProjCGIter();
        // }
        return MaxProjCGIter;
    }

    /**
     * @param MaxProjCGIter
     *            the MaxProjCGIter to set
     */
    public void setMaxProjCGIter(Object MaxProjCGIter)
    {
        this.MaxProjCGIter = MaxProjCGIter;
    }

    /**
     * @return the MaxRLPIter
     */
    public Object getMaxRLPIter()
    {
        // if (MaxRLPIter == null) {
        // return defaultopt.getMaxRLPIter();
        // }
        return MaxRLPIter;
    }

    /**
     * @param MaxRLPIter
     *            the MaxRLPIter to set
     */
    public void setMaxRLPIter(Object MaxRLPIter)
    {
        this.MaxRLPIter = MaxRLPIter;
    }

    /**
     * @return the MaxSQPIter
     */
    public Integer getMaxSQPIter()
    {
        // if (MaxSQPIter == null) {
        // return defaultopt.getMaxSQPIter();
        // }
        return MaxSQPIter;
    }

    /**
     * @param MaxSQPIter
     *            the MaxSQPIter to set
     */
    public void setMaxSQPIter(Integer MaxSQPIter)
    {
        this.MaxSQPIter = MaxSQPIter;
    }

    /**
     * @return the MaxTime
     */
    public Integer getMaxTime()
    {
        // if (MaxTime == null) {
        // return defaultopt.getMaxTime();
        // }
        return MaxTime;
    }

    /**
     * @param MaxTime
     *            the MaxTime to set
     */
    public void setMaxTime(Integer MaxTime)
    {
        this.MaxTime = MaxTime;
    }

    /**
     * @return the MeritFunction
     */
    public MFunction getMeritFunction()
    {
        // if (MeritFunction == null) {
        // return defaultopt.getMeritFunction();
        // }
        return MeritFunction;
    }

    /**
     * @param MeritFunction
     *            the MeritFunction to set
     */
    public void setMeritFunction(MFunction MeritFunction)
    {
        this.MeritFunction = MeritFunction;
    }

    /**
     * @return the MinAbsMax
     */
    public Integer getMinAbsMax()
    {
        // if (MinAbsMax == null) {
        // return defaultopt.getMinAbsMax();
        // }
        return MinAbsMax;
    }

    /**
     * @param MinAbsMax
     *            the MinAbsMax to set
     */
    public void setMinAbsMax(Integer MinAbsMax)
    {
        this.MinAbsMax = MinAbsMax;
    }

    /**
     * @return the NodeDisplayInterval
     */
    public Integer getNodeDisplayInterval()
    {
        // if (NodeDisplayInterval == null) {
        // return defaultopt.getNodeDisplayInterval();
        // }
        return NodeDisplayInterval;
    }

    /**
     * @param NodeDisplayInterval
     *            the NodeDisplayInterval to set
     */
    public void setNodeDisplayInterval(Integer NodeDisplayInterval)
    {
        this.NodeDisplayInterval = NodeDisplayInterval;
    }

    /**
     * @return the NodeSearchStrategy
     */
    public NsStrategy getNodeSearchStrategy()
    {
        // if (NodeSearchStrategy == null) {
        // return defaultopt.getNodeSearchStrategy();
        // }
        return NodeSearchStrategy;
    }

    /**
     * @param NodeSearchStrategy
     *            the NodeSearchStrategy to set
     */
    public void setNodeSearchStrategy(NsStrategy NodeSearchStrategy)
    {
        this.NodeSearchStrategy = NodeSearchStrategy;
    }

    /**
     * @return the NonlEqnAlgorithm
     */
    public NonlEqnAlgo getNonlEqnAlgorithm()
    {
        // if (NonlEqnAlgorithm == null) {
        // return defaultopt.getNonlEqnAlgorithm();
        // }
        return NonlEqnAlgorithm;
    }

    /**
     * @param NonlEqnAlgorithm
     *            the NonlEqnAlgorithm to set
     */
    public void setNonlEqnAlgorithm(NonlEqnAlgo NonlEqnAlgorithm)
    {
        this.NonlEqnAlgorithm = NonlEqnAlgorithm;
    }

    /**
     * @return the NoStopIfFlatInfeas
     */
    public Object getNoStopIfFlatInfeas()
    {
        // if (NoStopIfFlatInfeas == null) {
        // return defaultopt.getNoStopIfFlatInfeas();
        // }
        return NoStopIfFlatInfeas;
    }

    /**
     * @param NoStopIfFlatInfeas
     *            the NoStopIfFlatInfeas to set
     */
    public void setNoStopIfFlatInfeas(Object NoStopIfFlatInfeas)
    {
        this.NoStopIfFlatInfeas = NoStopIfFlatInfeas;
    }

    /**
     * @return the ObjectiveLimit
     */
    public Double getObjectiveLimit()
    {
        // if (ObjectiveLimit == null) {
        // return defaultopt.getObjectiveLimit();
        // }
        return ObjectiveLimit;
    }

    /**
     * @param ObjectiveLimit
     *            the ObjectiveLimit to set
     */
    public void setObjectiveLimit(Double ObjectiveLimit)
    {
        this.ObjectiveLimit = ObjectiveLimit;
    }

    /**
     * @return the PhaseOneTotalScaling
     */
    public Object getPhaseOneTotalScaling()
    {
        // if (PhaseOneTotalScaling == null) {
        // return defaultopt.getPhaseOneTotalScaling();
        // }
        return PhaseOneTotalScaling;
    }

    /**
     * @param PhaseOneTotalScaling
     *            the PhaseOneTotalScaling to set
     */
    public void setPhaseOneTotalScaling(Object PhaseOneTotalScaling)
    {
        this.PhaseOneTotalScaling = PhaseOneTotalScaling;
    }

    /**
     * @return the Preconditioner
     */
    public Object getPreconditioner()
    {
        // if (Preconditioner == null) {
        // return defaultopt.getPreconditioner();
        // }
        return Preconditioner;
    }

    /**
     * @param Preconditioner
     *            the Preconditioner to set
     */
    public void setPreconditioner(Object Preconditioner)
    {
        this.Preconditioner = Preconditioner;
    }

    /**
     * @return the PrecondBandWidth
     */
    public Double getPrecondBandWidth()
    {
        // if (PrecondBandWidth == null) {
        // return defaultopt.getPrecondBandWidth();
        // }
        return PrecondBandWidth;
    }

    /**
     * @param PrecondBandWidth
     *            the PrecondBandWidth to set
     */
    public void setPrecondBandWidth(Double PrecondBandWidth)
    {
        this.PrecondBandWidth = PrecondBandWidth;
    }

    /**
     * @return the RelLineSrchBnd
     */
    public Double getRelLineSrchBnd()
    {
        // if (RelLineSrchBnd == null) {
        // return defaultopt.getRelLineSrchBnd();
        // }
        return RelLineSrchBnd;
    }

    /**
     * @param RelLineSrchBnd
     *            the RelLineSrchBnd to set
     */
    public void setRelLineSrchBnd(Double RelLineSrchBnd)
    {
        this.RelLineSrchBnd = RelLineSrchBnd;
    }

    /**
     * @return the RelLineSrchBndDuration
     */
    public Integer getRelLineSrchBndDuration()
    {
        // if (RelLineSrchBndDuration == null) {
        // return defaultopt.getRelLineSrchBndDuration();
        // }
        return RelLineSrchBndDuration;
    }

    /**
     * @param RelLineSrchBndDuration
     *            the RelLineSrchBndDuration to set
     */
    public void setRelLineSrchBndDuration(Integer RelLineSrchBndDuration)
    {
        this.RelLineSrchBndDuration = RelLineSrchBndDuration;
    }

    /**
     * @return the ScaleProblem
     */
    public SProblem getScaleProblem()
    {
        // if (ScaleProblem == null) {
        // return defaultopt.getScaleProblem();
        // }
        return ScaleProblem;
    }

    /**
     * @param ScaleProblem
     *            the ScaleProblem to set
     */
    public void setScaleProblem(SProblem ScaleProblem)
    {
        this.ScaleProblem = ScaleProblem;
    }

    /**
     * @return the ShowStatusWindow
     */
    public Object getShowStatusWindow()
    {
        // if (ShowStatusWindow == null) {
        // return defaultopt.getShowStatusWindow();
        // }
        return ShowStatusWindow;
    }

    /**
     * @param ShowStatusWindow
     *            the ShowStatusWindow to set
     */
    public void setShowStatusWindow(Object ShowStatusWindow)
    {
        this.ShowStatusWindow = ShowStatusWindow;
    }

    /**
     * @return the Simplex
     */
    public Boolean getSimplex()
    {
        // if (Simplex == null) {
        // return defaultopt.getSimplex();
        // }
        return Simplex;
    }

    /**
     * @param Simplex
     *            the Simplex to set
     */
    public void setSimplex(Boolean Simplex)
    {
        this.Simplex = Simplex;
    }

    /**
     * @return the SubproblemAlgorithm
     */
    public SPAlgo getSubproblemAlgorithm()
    {
        // if (SubproblemAlgorithm == null) {
        // return defaultopt.getSubproblemAlgorithm();
        // }
        return SubproblemAlgorithm;
    }

    /**
     * @param SubproblemAlgorithm
     *            the SubproblemAlgorithm to set
     */
    public void setSubproblemAlgorithm(SPAlgo SubproblemAlgorithm)
    {
        this.SubproblemAlgorithm = SubproblemAlgorithm;
    }

    /**
     * @return the TolCon
     */
    public Double getTolCon()
    {
        // if (TolCon == null) {
        // return defaultopt.getTolCon();
        // }
        return TolCon;
    }

    /**
     * @param TolCon
     *            the TolCon to set
     */
    public void setTolCon(Double TolCon)
    {
        this.TolCon = TolCon;
    }

    /**
     * @return the TolConSQP
     */
    public Double getTolConSQP()
    {
        // if (TolConSQP == null) {
        // return defaultopt.getTolConSQP();
        // }
        return TolConSQP;
    }

    /**
     * @param TolConSQP
     *            the TolConSQP to set
     */
    public void setTolConSQP(Double TolConSQP)
    {
        this.TolConSQP = TolConSQP;
    }

    /**
     * @return the TolGradCon
     */
    public Object getTolGradCon()
    {
        // if (TolGradCon == null) {
        // return defaultopt.getTolGradCon();
        // }
        return TolGradCon;
    }

    /**
     * @param TolGradCon
     *            the TolGradCon to set
     */
    public void setTolGradCon(Object TolGradCon)
    {
        this.TolGradCon = TolGradCon;
    }

    /**
     * @return the TolPCG
     */
    public Double getTolPCG()
    {
        // if (TolPCG == null) {
        // return defaultopt.getTolPCG();
        // }
        return TolPCG;
    }

    /**
     * @param TolPCG
     *            the TolPCG to set
     */
    public void setTolPCG(Double TolPCG)
    {
        this.TolPCG = TolPCG;
    }

    /**
     * @return the TolProjCG
     */
    public Double getTolProjCG()
    {
        // if (TolProjCG == null) {
        // return defaultopt.getTolProjCG();
        // }
        return TolProjCG;
    }

    /**
     * @param TolProjCG
     *            the TolProjCG to set
     */
    public void setTolProjCG(Double TolProjCG)
    {
        this.TolProjCG = TolProjCG;
    }

    /**
     * @return the TolProjCGAbs
     */
    public Double getTolProjCGAbs()
    {
        // if (TolProjCGAbs == null) {
        // return defaultopt.getTolProjCGAbs();
        // }
        return TolProjCGAbs;
    }

    /**
     * @param TolProjCGAbs
     *            the TolProjCGAbs to set
     */
    public void setTolProjCGAbs(Double TolProjCGAbs)
    {
        this.TolProjCGAbs = TolProjCGAbs;
    }

    /**
     * @return the TolRLPFun
     */
    public Double getTolRLPFun()
    {
        // if (TolRLPFun == null) {
        // return defaultopt.getTolRLPFun();
        // }
        return TolRLPFun;
    }

    /**
     * @param TolRLPFun
     *            the TolRLPFun to set
     */
    public void setTolRLPFun(Double TolRLPFun)
    {
        this.TolRLPFun = TolRLPFun;
    }

    /**
     * @return the TolXInteger
     */
    public Double getTolXInteger()
    {
        // if (TolXInteger == null) {
        // return defaultopt.getTolXInteger();
        // }
        return TolXInteger;
    }

    /**
     * @param TolXInteger
     *            the TolXInteger to set
     */
    public void setTolXInteger(Double TolXInteger)
    {
        this.TolXInteger = TolXInteger;
    }

    /**
     * @return the TypicalX
     */
    public Object getTypicalX()
    {
        // if (TypicalX == null) {
        // return defaultopt.getTypicalX();
        // }
        return TypicalX;
    }

    /**
     * @param TypicalX
     *            the TypicalX to set.
     */
    public void setTypicalX(Object TypicalX)
    {
        if (TypicalX == null)
        {
            return;
        }
        String msg = "";
        boolean bool = !(TypicalX instanceof String) && !(TypicalX instanceof Matrix) && !(TypicalX instanceof Number);
        if (bool)
        {
            msg = "Parameter \"TypicalX\" must be an instanceof \"String\", \"Matrix\" or \"Number\".";
            throw new ConditionalRuleException("setTypicalX", msg);
        }
        if (TypicalX instanceof String)
        {
            String str = (String) TypicalX;
            if ("".equals(str.trim()))
            {
                return;
            }
        }
        this.TypicalX = TypicalX;
    }

    /**
     * @return the UseParallel
     */
    public UParallel getUseParallel()
    {
        // if (UseParallel == null) {
        // return defaultopt.getUseParallel();
        // }
        return UseParallel;
    }

    /**
     * @param UseParallel
     *            the UseParallel to set
     */
    public void setUseParallel(UParallel UseParallel)
    {
        this.UseParallel = UseParallel;
    }

    // //////////////////////////////////////////////////////////////////////////
    @Override
    public OptDisplay getDisplay()
    {
        // if (Display == null) {
        // return defaultopt.getDisplay();
        // }
        return super.getDisplay();
    }

    @Override
    public Object getMaxFunEvals()
    {
        // if (MaxFunEvals == null) {
        // return defaultopt.getMaxFunEvals();
        // }
        return super.getMaxFunEvals();// this.MaxFunEvals;
    }

    @Override
    public Object getMaxIter()
    {
        // if (MaxIter == null) {
        // return defaultopt.getMaxIter();
        // }
        return super.getMaxIter();// this.MaxIter;
    }

    @Override
    public Double getTolFun()
    {
        // if (TolFun == null) {
        // return defaultopt.getTolFun();
        // }
        return super.getTolFun();// this.TolFun;
    }

    @Override
    public Double getTolX()
    {
        // if (TolX == null) {
        // return defaultopt.getTolX();
        // }
        return super.getTolX();
    }

    @Override
    public Boolean isFunValCheck()
    {
        // if (FunValCheck == null) {
        // return defaultopt.isFunValCheck();
        // }
        return super.isFunValCheck();
    }

    @Override
    public ArrayList<FunctionFunctions> getOutputFcn()
    {
        /*
         * if (OutputFcn == null) { boolean isNull = defaultopt == null; if
         * (isNull) { System.out.println("defaultopt = NULL"); } else {
         * System.out.println("defaultopt = NOT_NULL"); }
         * 
         * return defaultopt.getOutputFcn(); }
         */
        return super.getOutputFcn();// this.OutputFcn;
    }

    /**
     * @return the PlotFcns
     */
    @Override
    public ArrayList<FunctionFunctions> getPlotFcns()
    {
        // if (PlotFcns == null) {
        // return defaultopt.getPlotFcns();
        // }
        return super.getPlotFcns();// PlotFcns;
    }

    // //////////////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        OptimOpt opt = new OptimOpt();
        Class optimClass = opt.getClass();
        Field[] fields = optimClass.getFields();
        int len = fields.length;
        for (int i = 0; i < len; i++)
        {
            System.out.println("field[" + i + "] = " + fields[i].getName());
        }
    }
}
