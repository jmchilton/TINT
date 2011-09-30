package edu.umn.msi.tropix.common.prediction.impl;

import org.apache.commons.math.stat.StatUtils;

import com.google.common.base.Preconditions;

import Jama.Matrix;

public class MathUtils {
  
  public static double computeSYY(double[] ys) {
    double ybar = StatUtils.mean(ys);
    double syy = 0.0;
    for(double y : ys) {
      double ydelta = y - ybar;
      syy += ydelta * ydelta;
    }
    return syy;
  }

  /**
   * 
   * @param x
   * @param y
   * @return (x - y)^T(x-y)
   */
  public static double computeSumOfSquaresOfDifference(double[] x, double[] y) {
    Preconditions.checkArgument(x.length == y.length);
    double sum = 0.0;
    for(int i = 0; i < x.length; i++) {
      double diff = x[i] - y[i];
      sum += diff * diff;
    }
    return sum;
  }
  
  /**
   * 
   * @param A
   * @param a
   * @return A*x
   */
  public static double[] times(final Matrix A, final double[] x) {
    Preconditions.checkArgument(A.getColumnDimension() == x.length);
    double[] y = new double[A.getRowDimension()];
    for(int i = 0; i < y.length; i++) {
      double yi = 0.0;
      for(int j = 0; j < x.length; j++) {
        yi += A.get(i, j) * x[j];
      }
      y[i] = yi;
    }
    return y;
  }
  
}
