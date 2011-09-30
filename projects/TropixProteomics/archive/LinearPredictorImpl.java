package edu.umn.msi.tropix.common.prediction.impl;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.prediction.Predictor;

public class LinearPredictorImpl implements Predictor {
  private double[] coefficents;
  
  public void setCoefficient(double[] coefficents) {
    this.coefficents = coefficents;
  }
  
  public Double predict(double[] x) {
    double y = coefficents[0];
    Preconditions.checkArgument(x.length == coefficents.length - 1);
    for(int i = 1; i <= x.length; i++) {
      y += x[i-1] * coefficents[i];
    }
    return y;
  }  
}
