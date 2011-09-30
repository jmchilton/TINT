package edu.umn.msi.tropix.jobs.activities.impl.fortest;

import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFor;

@ActivityFor(descriptionClass = CreateTropixFileDescription.class)
public class ActivityForTestBean implements Activity {
  private static int count = 0;

  public ActivityForTestBean() {
    count++;
  }

  public static int getCount() {
    return count;
  }

  public void run() throws ShutdownException {

  }
}
