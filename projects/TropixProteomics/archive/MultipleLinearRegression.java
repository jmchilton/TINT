package edu.umn.msi.tropix.common.prediction.impl;

import java.util.Arrays;

import com.google.common.base.Preconditions;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import edu.umn.msi.tropix.common.prediction.Predictor;

public class MultipleLinearRegression {
  public double MINIMUM_SINGULAR_VALUE = .00001;
  
  public Predictor buildPredictor(double[][] xs, double[] ys) {
    Preconditions.checkArgument(xs.length > 0 && xs[0].length > 0 && xs.length == ys.length);
    int n = xs.length, k = xs[0].length;

    Matrix A = new Matrix(xs);
    System.out.println("Rank is " + A.rank());
    SingularValueDecomposition svd = A.svd();
    double[] singularValues = svd.getSingularValues();
    System.out.println(Arrays.toString(singularValues));
    int numSingularValues = singularValues.length;
    while(numSingularValues >= 0) {
      if(singularValues[numSingularValues-1] >= MINIMUM_SINGULAR_VALUE) {
        break;
      }
      numSingularValues--;
    }
    Matrix V,UT;
    V = svd.getV();
    UT = svd.getU().transpose();
    if(numSingularValues == singularValues.length) {
      V = V.getMatrix(0, V.getRowDimension(), 0, numSingularValues);
      UT = UT.getMatrix(0, numSingularValues, 0, UT.getColumnDimension());
    }
    Matrix VSinv = V;
    for(int i = 0; i < numSingularValues; i++) {
      double siinv = 1.0 / singularValues[i];
      for(int j = 0; j < V.getRowDimension(); j++) {
        double vij = V.get(i, j);
        VSinv.set(i, j, vij * siinv);
      }
    }
    Matrix VSinvUT = VSinv.times(UT);
    double[] nu = MathUtils.times(VSinvUT,ys);
    double[] yhat = MathUtils.times(A, nu);
    double rss = MathUtils.computeSumOfSquaresOfDifference(ys, yhat);
    double syy = MathUtils.computeSYY(ys);
    double rSquared = -1 * ((rss / syy) - 1);
    double sigmaHatSquared = rss / (n - k);
    System.out.println(Arrays.toString(nu));
    LinearPredictorImpl linearPredictorImpl = new LinearPredictorImpl();
    linearPredictorImpl.setCoefficient(nu);
    return linearPredictorImpl;
  }
}
