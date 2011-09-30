package edu.umn.msi.tropix.common.test;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.prediction.Predictor;
import edu.umn.msi.tropix.common.prediction.impl.MultipleLinearRegression;

public class MultipleLinearRegressionTest {
  
  
  @Test
  public void sanityCheck() {
    double[][] xs = {{1.0,1.0, 1.0, 1.0}, {1.0, 2.0, 3.0, 4.0}, {1.0, 3.0, 5.0, 7.0}, {1.0, 4.0, 7.0, 10.0}};
    double[] y = {1.0 * 1.0 + 2.0 * 1.0 + 3.0 * 1.0, 2.0 * 6.0 + 3.0 * 4.0, 1.0 * 3.0 + 2.0 * 5.0 + 3.0 * 7.0, 1.0 * 4.0 + 2.0 * 7.0 + 3.0 * 10.0};
    MultipleLinearRegression regression = new MultipleLinearRegression();
    Predictor predictor = regression.buildPredictor(xs, y);
    double val = predictor.predict(new double[]{1.0, 2.0, 1.0});
    System.out.println(val);
    assert Math.abs(val - (1.0*1.0 + 2.0*2.0 + 3.0*1.0)) < .0001;
  }
}
