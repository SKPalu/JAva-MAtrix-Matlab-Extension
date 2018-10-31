package jamaextension.jamax.unsupervised.subspace.features;

import java.util.List;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.PropValPairList;
import jamaextension.jamax.PropertyValuePair;
import jamaextension.jamax.SparseDI;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.Min;
import jamaextension.jamax.datafun.MinMat;
import jamaextension.jamax.datafun.NormalizeFea;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortMat;
import jamaextension.jamax.ops.UniqueSet;
import jamaextension.jamax.stats.RandSample;

public class ConstructAffinity {

	private Matrix fea;
	private PropValPairList options;
	private boolean weightModeBinary; // = "Binary".equals(wgtMode);
	private boolean weightModeHeatKernel; // = "HeatKernel".equals(wgtMode);
	private boolean weightModeCosine; // = "Cosine".equals(wgtMode);

	private boolean bBinary = false;
	private boolean bCosine = false;
	boolean bSpeed = true;
	int BlockSize;
	int optK;

	private boolean neighbourModeKnn;// = "KNN".equals(nbMode);
	private boolean neighbourModeSupervised;// = "Supervised".equals(nbMode);

	private int nSmp;
	private int nLabel;
	private Matrix Label;
	private Matrix W;

	public ConstructAffinity(Matrix fea, PropValPairList options) {

		if (fea == null || fea.isNull()) {
			throw new ConditionalRuleException("Matrix \"fea\" must be non-null or non-empty.");
		}
		this.fea = fea;

		if (options == null || options.isEmpty()) {
			PropertyValuePair PVP = new PropertyValuePair("bNormalized", true);
			this.options = new PropValPairList(PVP);
		}

		// if ~isfield(options,'NeighborMode')
		// options.NeighborMode = 'KNN';
		// end
		if (!this.options.containsProperty("NeighborMode")) {
			this.options.add(new PropertyValuePair("NeighborMode", "KNN"));
		}

		Object nbMode = this.options.getValue("NeighborMode");
		neighbourModeKnn = "KNN".equals(nbMode);
		neighbourModeSupervised = "Supervised".equals(nbMode);

		if (neighbourModeKnn) {
			// this.options.add(new PropertyValuePair("NeighborMode", "KNN"));
			if (!this.options.containsProperty("k")) {
				this.options.add(new PropertyValuePair("k", 5));
			}
		} else if (neighbourModeSupervised) {
			if (!this.options.containsProperty("bLDA")) {
				this.options.add(new PropertyValuePair("bLDA", false));
			}

			if (((Boolean) this.options.getValue("bLDA")).booleanValue()) {
				this.options.add(new PropertyValuePair("bSelfConnected", true));
			}

			if (!this.options.containsProperty("k")) {
				this.options.add(new PropertyValuePair("k", 0));
			}

			if (!this.options.containsProperty("gnd")) {
				// this.options.add(new PropertyValuePair("k", 0));
				throw new ConditionalRuleException("Label(gnd) should be provided under \"Supervised\" NeighborMode!");
			}

			Matrix gndMat = (Matrix) this.options.getValue("gnd");
			if (gndMat.length() != fea.getRowDimension()) {
				throw new ConditionalRuleException("Sizes of \"gnd\" and \"fea\" don't match.");
			}
		} else {

			throw new ConditionalRuleException("Invalid NeighborMode option field : \"" + nbMode + "\" found.");
		}

		if (!this.options.containsProperty("WeightMode")) {
			this.options.add(new PropertyValuePair("WeightMode", "HeatKernel"));
		}

		Matrix D = null;

		Object wgtMode = this.options.getValue("WeightMode");
		weightModeBinary = "Binary".equals(wgtMode);
		weightModeHeatKernel = "HeatKernel".equals(wgtMode);
		weightModeCosine = "Cosine".equals(wgtMode);
		// Cosine

		if (weightModeBinary) {
			// this.options.add(new PropertyValuePair("NeighborMode", "KNN"));
			// if (!this.options.containsProperty("k")) {
			// this.options.add(new PropertyValuePair("k", 5));
			// }
			bBinary = true;
		} else if (weightModeHeatKernel) {
			if (!this.options.containsProperty("t")) {
				// this.options.add(new PropertyValuePair("bLDA", false));
				int nSmp = fea.getRowDimension();
				// nSmp = size(fea,1);
				if (nSmp > 3000) {
					// randsample(nSmp,3000)
					RandSample RS = new RandSample(nSmp, 3000);
					Object nObj = RS.getNumericSample();
					int[] idxarr = ((Indices) nObj).getRowPackedCopy();
					// D = EuDist2(fea(randsample(nSmp,3000),:));
					Matrix rsfea = fea.getRows(idxarr);
					D = new EuDist(rsfea).getD();
				} else {
					D = new EuDist(fea).getD();
				}
				double mean2d = JDatafun.mean(JDatafun.mean(D)).start();
				// options.t = mean(mean(D));
				this.options.add(new PropertyValuePair("t", mean2d));
			}
		} else if (weightModeCosine) {
			bCosine = true;
		} else {
			throw new ConditionalRuleException("Invalid WeightMode option field : \"" + wgtMode + "\" found.");
		}

		if (!this.options.containsProperty("bSelfConnected")) {
			this.options.add(new PropertyValuePair("bSelfConnected", 0));
		}

		// int nSmp = 0;
		// Matrix gndMat = (Matrix) this.options.getValue("gnd");
		if (!this.options.containsProperty("gnd")) {
			Matrix gndMat = (Matrix) this.options.getValue("gnd");
			nSmp = gndMat.length();
		} else {
			nSmp = fea.getRowDimension();
		}
		// if isfield(options,'gnd')
		// nSmp = length(options.gnd);
		// else
		// nSmp = size(fea,1);
		// end
		int maxM = 62500000; // 500M
		double num = maxM / (nSmp * 3.0);
		BlockSize = (int) Math.floor(num);

		///////////////////////////////////////////////////////////
		if (neighbourModeSupervised) {

			boolean res = neighborModeSupervised();
			if (res) {
				return;
			}
			return;
		} // end supervised
			///////////////////////////////////////////////////////////

		Matrix Normfea = fea;
		if (bCosine && !(Boolean) this.options.getValue("bNormalized")) {// &&
																			// ~options.bNormalized
			// Normfea = NormalizeFea(fea);
			Normfea = new NormalizeFea(fea).getFeaNorm();
		}

		boolean knnGtZeros = neighborModeKnnKgreaterThanZero();
		if (knnGtZeros) {
			return;
		}

	}

	boolean neighborModeSupervised() {

		boolean earlyReturn = false;
		Matrix gndMat = (Matrix) this.options.getValue("gnd");
		UniqueSet unqs = new UniqueSet(gndMat);
		Matrix Label = unqs.getUniqueData();// Label = unique(options.gnd);
		nLabel = Label.length();// length(Label);

		if (((Boolean) this.options.getValue("bLDA")).booleanValue()) {// options.bLDA
			Matrix G = Matrix.zeros(nSmp, nSmp);
			for (int idx = 0; idx < nLabel; idx++) {
				FindInd find = gndMat.EQ(Label.getElementAt(idx)).findIJ();// classIdx
																			// =
																			// options.gnd==Label(idx);
				Indices classIdxInd = find.getIndexInd();
				int[] classIdx = classIdxInd.getRowPackedCopy();
				double sumcIdx = JDatafun.sum(classIdxInd).start();
				sumcIdx = 1.0 / sumcIdx;
				// G(classIdx,classIdx) = 1/sum(classIdx);
				G.setMatrix(classIdx, classIdx, sumcIdx);
			}
			this.W = G;// W = sparse(G);
			return true;
		}

		if (this.weightModeBinary) {
			earlyReturn = neighborModeSupervisedWeightModeBinary();
		} else if (this.weightModeHeatKernel) {
			earlyReturn = neighborModeSupervisedWeightModeHeatKernel();
		} else if (this.weightModeCosine) {
			earlyReturn = neighborModeSupervisedWeightModeCosine();
		} else {
		}

		return earlyReturn;
	}

	boolean neighborModeSupervisedWeightModeBinary() {
		int k = optK;// ((Integer) this.options.getValue("k")).intValue();
		Matrix G = null;
		Matrix gndMat = (Matrix) this.options.getValue("gnd");
		if (k > 0) {// options.k > 0
			G = Matrix.zeros(nSmp * (k + 1), 3);
			int idNow = 0;
			for (int i = 0; i < nLabel; i++) {
				// classIdx = find(options.gnd==Label(i));
				FindInd find = gndMat.EQ(Label.getElementAt(i)).findIJ();
				Indices classIdxInd = find.getIndexInd();
				int[] classIdx = classIdxInd.getRowPackedCopy();
				// D = EuDist2(fea(classIdx,:),[],0);
				Matrix featmp = fea.getRows(classIdx);
				Matrix D = new EuDist(false, featmp).getD();
				// [dump idx] = sort(D,2); // sort each row
				QuickSort sort = new QuickSortMat(D, Dimension.COL);
				Indices idx = sort.getIndices();

				// clear D dump;
				idx = idx.getColumns(0, k);// idx = idx(:,1:options.k+1);

				int nSmpClass = classIdx.length * (k + 1);// length(classIdx)*(options.k+1);
				Indices cIdx2 = classIdxInd.repmat(k + 1, 1);
				int[] rowInds = Indices.linspace(idNow + 1, nSmpClass + idNow).getRowPackedCopy();
				G.setMatrix(rowInds, 0, Matrix.indicesToMatrix(cIdx2));// G(idNow+1:nSmpClass+idNow,1)
																		// =
																		// repmat(classIdx,[options.k+1,1]);

				// G(idNow+1:nSmpClass+idNow,2) = classIdx(idx(:));
				Indices idNowrows = Indices.linspace(idNow + 1, nSmpClass + idNow);
				Indices classIdxidx = classIdxInd.getEls(idx.toColVector().getRowPackedCopy());
				G.setMatrix(idNowrows.getRowPackedCopy(), 1, classIdxidx);

				// G(idNow+1:nSmpClass+idNow,3) = 1;
				G.setMatrix(idNowrows.getRowPackedCopy(), 2, 1);
				idNow = idNow + nSmpClass;

				// clear idx
			}
			// G = sparse(G(:,1),G(:,2),G(:,3),nSmp,nSmp);
			int[] II = G.getColumnAt(0).toIndices().getRowPackedCopy();
			int[] JJ = G.getColumnAt(1).toIndices().getRowPackedCopy();
			List<Double> entry = G.getColumnAt(1).toArrayList();
			SparseDI sparse = new SparseDI(II, JJ, entry, nSmp, nSmp);
			G = (Matrix) sparse.full();
			G = JDatafun.max(G, G.transpose());
		} else {
			G = Matrix.zeros(nSmp, nSmp);

			for (int i = 0; i < nLabel; i++) {
				// classIdx = find(options.gnd==Label(i));
				Indices gndLabel = gndMat.EQ(Label.getElementAt(i));
				FindInd find = gndLabel.findIJ();
				if (!find.isNull()) {
					gndLabel = find.getIndexInd();
					int[] cIdxArr = gndLabel.getRowPackedCopy();
					// G(classIdx,classIdx) = 1;
					G.setMatrix(cIdxArr, cIdxArr, 1);
				}
			}
		}

		if (!((Boolean) this.options.getValue("bSelfConnected")).booleanValue()) {// ~options.bSelfConnected
			for (int i = 0; i < G.getRowDimension(); i++) {// size(G,1)
				G.set(i, i, 0);
			}
		}

		W = G;// sparse(G);
		return true;
	}

	boolean neighborModeSupervisedWeightModeHeatKernel() {
		return true;
	}

	boolean neighborModeSupervisedWeightModeCosine() {
		return true;
	}

	boolean neighborModeKnnKgreaterThanZero() {
		boolean cosineNormalized = true;
		if (cosineNormalized) {// ~(bCosine && options.bNormalized)
			Matrix G = Matrix.zeros(nSmp * (optK + 1), 3);
			for (int i = 0; i < (int) Math.ceil(nSmp / BlockSize); i++) {
				int I = i + 1;
				if (I == (int) Math.ceil(nSmp / BlockSize)) {
					int[] smpIdx = Indices.linspace((I - 1) * BlockSize, nSmp - 1).getRowPackedCopy();//// smpIdx
																										//// =
																										//// (i-1)*BlockSize+1:nSmp;
					Matrix feaSmpIdx = fea.getRows(smpIdx);
					Matrix dist = new EuDist(false, feaSmpIdx, fea).getD();// dist
																			// =
																			// EuDist2(fea(smpIdx,:),fea,0);

					if (bSpeed) {
						int nSmpNow = smpIdx.length;// length(smpIdx);
						Matrix dump = Matrix.zeros(nSmpNow, optK + 1);
						Indices idx = dump.toIndices();
						for (int j = 0; j < optK + 1; j++) {
							Min min = new MinMat(dist, Dimension.COL);
							Matrix dj = (Matrix) min.getMinObject();
							Indices idxj = min.getIndices();
							// [dump(:,j),idx(:,j)] = min(dist,[],2);
							dump.setColumnAt(j, dj);
							idx.setColumnAt(j, idxj);
							// temp = (idx(:,j)-1)*nSmpNow+[1:nSmpNow]';
							// dist(temp) = 1e100;
						}
					} else {
						// [dump idx] = sort(dist,2); // sort each row
						// idx = idx(:,1:options.k+1);
						// dump = dump(:,1:options.k+1);
					}

					if (!bBinary) {
						if (bCosine) {
							// dist = Normfea(smpIdx,:)*Normfea';
							// dist = full(dist);
							// linidx = [1:size(idx,1)]';
							// dump =
							// dist(sub2ind(size(dist),linidx(:,ones(1,size(idx,2))),idx));
						} else {
							// dump = exp(-dump/(2*options.t^2));
						}
					}

					// G((i-1)*BlockSize*(options.k+1)+1:nSmp*(options.k+1),1) =
					// repmat(smpIdx',[options.k+1,1]);
					// G((i-1)*BlockSize*(options.k+1)+1:nSmp*(options.k+1),2) =
					// idx(:);
					if (!bBinary) {
						// G((i-1)*BlockSize*(options.k+1)+1:nSmp*(options.k+1),3)
						// = dump(:);
					} else {
						// G((i-1)*BlockSize*(options.k+1)+1:nSmp*(options.k+1),3)
						// = 1;
					}
				} else {
					// smpIdx = (i-1)*BlockSize+1:i*BlockSize;

					// dist = EuDist2(fea(smpIdx,:),fea,0);

					if (bSpeed) {
						// nSmpNow = length(smpIdx);
						// dump = zeros(nSmpNow,options.k+1);
						// idx = dump;
						for (int j = 0; j < optK + 1; j++) {
							// [dump(:,j),idx(:,j)] = min(dist,[],2);
							// temp = (idx(:,j)-1)*nSmpNow+[1:nSmpNow]';
							// dist(temp) = 1e100;
						}
					} else {
						// [dump idx] = sort(dist,2); // sort each row
						// idx = idx(:,1:options.k+1);
						// dump = dump(:,1:options.k+1);
					}

					if (!bBinary) {
						if (bCosine) {
							// dist = Normfea(smpIdx,:)*Normfea';
							// dist = full(dist);
							// linidx = [1:size(idx,1)]';
							// dump =
							// dist(sub2ind(size(dist),linidx(:,ones(1,size(idx,2))),idx));
						} else {
							// dump = exp(-dump/(2*options.t^2));
						}
					}

					// G((i-1)*BlockSize*(options.k+1)+1:i*BlockSize*(options.k+1),1)
					// =
					// repmat(smpIdx',[options.k+1,1]);
					// G((i-1)*BlockSize*(options.k+1)+1:i*BlockSize*(options.k+1),2)
					// = idx(:);
					if (!bBinary) {
						// G((i-1)*BlockSize*(options.k+1)+1:i*BlockSize*(options.k+1),3)
						// = dump(:);
					} else {
						// G((i-1)*BlockSize*(options.k+1)+1:i*BlockSize*(options.k+1),3)
						// = 1;
					}
				}
			}

			// W = sparse(G(:,1),G(:,2),G(:,3),nSmp,nSmp);
		} else {
			Matrix G = Matrix.zeros(nSmp * (optK + 1), 3);// G =
															// zeros(nSmp*(options.k+1),3);
			for (int i = 0; i < Math.ceil(nSmp / BlockSize); i++) {
				int I = i + 1;
				if (I == Math.ceil(nSmp / BlockSize)) {
					int[] smpIdx = Indices.linspace((I - 1) * BlockSize, nSmp - 1).getRowPackedCopy();// smpIdx
																										// =
																										// (i-1)*BlockSize+1:nSmp;
					Matrix feaSmpIdx = fea.getRows(smpIdx);
					Matrix dist = feaSmpIdx.times(fea.transpose());// dist =
																	// fea(smpIdx,:)*fea';
					// dist = full(dist);

					if (bSpeed) {
						int nSmpNow = smpIdx.length;// length(smpIdx);
						Matrix dump = Matrix.zeros(nSmpNow, optK + 1);
						Indices idx = dump.toIndices();// idx = dump;
						for (int j = 0; j < optK + 1; j++) {
							// [dump(:,j),idx(:,j)] = max(dist,[],2);
							// temp = (idx(:,j)-1)*nSmpNow+[1:nSmpNow]';
							// dist(temp) = 0;
						}
					} else {
						// [dump idx] = sort(-dist,2); % sort each row
						// idx = idx(:,1:options.k+1);
						// dump = -dump(:,1:options.k+1);
					}

					// G((i-1)*BlockSize*(options.k+1)+1:nSmp*(options.k+1),1) =
					// repmat(smpIdx',[options.k+1,1]);
					// G((i-1)*BlockSize*(options.k+1)+1:nSmp*(options.k+1),2) =
					// idx(:);
					// G((i-1)*BlockSize*(options.k+1)+1:nSmp*(options.k+1),3) =
					// dump(:);
				} else {
					// smpIdx = (i-1)*BlockSize+1:i*BlockSize;
					// dist = fea(smpIdx,:)*fea';
					// dist = full(dist);

					if (bSpeed) {
						// nSmpNow = length(smpIdx);
						// dump = zeros(nSmpNow,options.k+1);
						// idx = dump;
						for (int j = 0; j < optK + 1; j++) {
							// [dump(:,j),idx(:,j)] = max(dist,[],2);
							// temp = (idx(:,j)-1)*nSmpNow+[1:nSmpNow]';
							// dist(temp) = 0;
						}
					} else {
						// [dump idx] = sort(-dist,2); % sort each row
						// idx = idx(:,1:options.k+1);
						// dump = -dump(:,1:options.k+1);
					}

					// G((i-1)*BlockSize*(options.k+1)+1:i*BlockSize*(options.k+1),1)
					// =
					// repmat(smpIdx',[options.k+1,1]);
					// G((i-1)*BlockSize*(options.k+1)+1:i*BlockSize*(options.k+1),2)
					// = idx(:);
					// G((i-1)*BlockSize*(options.k+1)+1:i*BlockSize*(options.k+1),3)
					// = dump(:);
				}
			}

			// W = sparse(G(:,1),G(:,2),G(:,3),nSmp,nSmp);
		}

		if (bBinary) {
			// W(logical(W)) = 1;
		}

		boolean bSemSuper = true;

		if (bSemSuper) {// isfield(options,'bSemiSupervised') &&
						// options.bSemiSupervised
			// tmpgnd = options.gnd(options.semiSplit);

			// Label = unique(tmpgnd);
			// nLabel = length(Label);
			// G = zeros(sum(options.semiSplit),sum(options.semiSplit));
			for (int idx = 0; idx < nLabel; idx++) {
				// classIdx = tmpgnd==Label(idx);
				// G(classIdx,classIdx) = 1;
			}
			// Wsup = sparse(G);
			boolean notSameCategoryWeight = false;
			if (notSameCategoryWeight) {// ~isfield(options,'SameCategoryWeight')
				// options.SameCategoryWeight = 1;
			}
			// W(options.semiSplit,options.semiSplit) =
			// (Wsup>0)*options.SameCategoryWeight;
		}

		boolean notbSelfConnected = false;
		if (notbSelfConnected) {// ~options.bSelfConnected
			// W = W - diag(diag(W));
		}

		boolean hasbTrueKNN = false;

		if (hasbTrueKNN) {// isfield(options,'bTrueKNN') && options.bTrueKNN

		} else {
			// W = max(W,W');
		}

		return true;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
