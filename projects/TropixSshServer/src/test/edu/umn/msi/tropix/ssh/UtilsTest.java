package edu.umn.msi.tropix.ssh;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class UtilsTest {

  @Test(groups = "unit")
  public void testRootSplit() {
    assert Utils.pathPieces("/").isEmpty();
  }

  @Test(groups = "unit")
  public void testOneSplit() {
    final List<String> pieces = Utils.pathPieces("/foo");
    assert pieces.size() == 1;
    assert pieces.get(0).equals("foo");
  }

  @Test(groups = "unit")
  public void testCleanAndExpandPath() {
    assertCleanedPathIs("/My Home/moo/cow", "C:\\moo\\cow");
    assertCleanedPathIs("/moo/cow", "/moo/cow");
    assertCleanedPathIs("/", "/");
    assertCleanedPathIs("/My Home/a/b/c", "a/b/c");
    assertCleanedPathIs("/My Home", ".");
    assertCleanedPathIs("/", "./..");
    assertCleanedPathIs("/My Home", "./../My Home/");
    assertCleanedPathIs("/", "/..");
  }

  @Test(groups = "unit")
  public void testName() {
    assertNameIs("cow", "C:\\moo\\cow");
    assertNameIs("cow", "/moo/cow");
  }

  private void assertNameIs(final String expected, final String input) {
    final String name = Utils.name(input);
    Assert.assertEquals(expected, name);
  }

  private void assertCleanedPathIs(final String expected, final String input) {
    final String cleanedPath = Utils.cleanAndExpandPath(input);
    Assert.assertEquals(expected, cleanedPath);
  }

}
