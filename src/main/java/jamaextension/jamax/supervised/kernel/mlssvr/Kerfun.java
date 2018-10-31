package jamaextension.jamax.supervised.kernel.mlssvr;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.elfun.JElfun;

public class Kerfun {
	
	private KernelType kernel;
	private Matrix X;
	private Matrix Z;
	private Matrix kernelFunction;
	private double p1;
	private double p2;
	
	public Kerfun(Matrix X, Matrix Z, double p1, double p2){
		this(null,X,Z,p1,p2);
	}
	
	public Kerfun(KernelType kernel, Matrix X, Matrix Z, double p1, double p2){
		if(X==null || X.isNull() || Z==null || Z.isNull()) {
			return;
		}
		if(X.getColumnDimension() != Z.getColumnDimension()){
			System.out.println("The column numbers for input arguments \"X\" and \"Z\" must be the same.");
			return;
		}
		if(kernel==null){
			this.kernel = KernelType.rbf;
		}
		else{
			this.kernel = kernel;
		}
		
	Matrix temp = null;	
	Matrix temp2 = null;
	Matrix temp3 = null;
	 double val = 0.0;
	
		
	   switch(this.kernel){
	   case linear: { 
		   this.kernelFunction = X.times(Z.transpose());
		   break;
		   }
	   case poly: { 
		   this.kernelFunction = X.times(Z.transpose()).plus(p1);//K = (X*Z' + p1).^p2;
		   this.kernelFunction = JElfun.pow(this.kernelFunction, p2);
		   break;
		   }
	   case rbf: { 
		   temp = X.dot(X,Dimension.COL);
		   temp = temp.repmat(1, Z.getRowDimension());
		   temp2 = Z.dot(Z,Dimension.COL).transpose();
		   temp2 = temp2.repmat( X.getRowDimension(),1);
		   temp3 = X.times(Z.transpose()).arrayTimes(-2);
		   
		   temp = temp.plus(temp2).plus(temp3).arrayTimes(-p1);
		   this.kernelFunction = JElfun.exp(temp);
		   //K = exp(-p1*(repmat(dot(X, X, 2), 1, size(Z, 1)) + ...
		   //         repmat(dot(Z, Z, 2)', size(X, 1), 1) - 2*X*Z'));
		   break;
		   }
	   case erbf: {
		   temp = X.dot(X,Dimension.COL);
		   temp = temp.repmat(1, Z.getRowDimension());
		   temp2 = Z.dot(Z,Dimension.COL).transpose();
		   temp2 = temp2.repmat(X.getRowDimension(),1);
		   temp3 = X.times(Z.transpose()).arrayTimes(-2);
		   
		   val = 2*Math.pow(p1,2.0);
		   
		   temp = temp.plus(temp2).plus(temp3).arrayTimes(-p1);
		   temp = JElfun.sqrt(temp).arrayRightDivide(-val);
		   this.kernelFunction = JElfun.exp(temp).plus(p2);	   
		   //K = exp(-sqrt(repmat(dot(X, X, 2), 1, size(Z, 1)) + ...
           //repmat(dot(Z, Z, 2)', size(X, 1), 1) - 2*X*Z') / (2*p1^2)) + p2;
		   break;
		   }
	   case sigmoid: { 
		   //K = tanh(p1*X*Z'/size(X, 2)  + p2);
		   temp = X.times(Z.transpose()).arrayTimes(p1/X.getColumnDimension()).plus(p2);
		   this.kernelFunction = JElfun.tanh(temp);
		   break;
		   }
	   default: {
		   this.kernelFunction = X.times(Z.transpose()).plus(p1+p2);
	   }
	    
	   }
	}

	public double getP1() {
		return p1;
	}

	public KernelType getKernel() {
		return kernel;
	}

	public Matrix getX() {
		return X;
	}

	public Matrix getZ() {
		return Z;
	}

	public Matrix getKernelFunction() {
		return kernelFunction;
	}

	public double getP2() {
		return p2;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
