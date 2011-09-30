package edu.umn.msi.tropix.jobs.activities.factories;

import org.testng.annotations.Test;

public class UniqueSanitizedFileNamerTest {

  @Test
  public void testProcession() {
    final UniqueSanitizedFileNamer namer = new UniqueSanitizedFileNamer();
    assert "foo".equals(namer.nameFor("foo"));
    assert "foo_1".equals(namer.nameFor("foo"));
    assert "foo_2".equals(namer.nameFor("foo"));
  }

}
