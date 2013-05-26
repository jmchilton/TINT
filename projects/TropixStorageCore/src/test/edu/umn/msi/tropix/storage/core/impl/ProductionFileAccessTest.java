package edu.umn.msi.tropix.storage.core.impl;

import java.io.File;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.storage.core.access.fs.DirectoryTreeFileFunctionImpl;

public class ProductionFileAccessTest {

  @Test
  public void testAccess() {
    DirectoryTreeFileFunctionImpl func = new DirectoryTreeFileFunctionImpl();
    func.setDirectory("/project/sequest/tropix/production_data/");
    func.setTreeDepth(1);
    func.setTreeWidth(3);
    long startTime = System.nanoTime();
    func.apply("03fb268f-c2e7-4844-987a-b16100c64095");
    long endTime = System.nanoTime();

    System.out.println("Calc time is " + (endTime - startTime));

    // File file = new File("/project/sequest/tropix/production_data/6e0/03fb268f-c2e7-4844-987a-b16100c64095");
    final String p1 = "6e0/03fb268f-c2e7-4844-987a-b16100c64095";
    final String p2 = "6ec/0d21137a-6f08-4507-8bde-2fe049d1cd34";
    final String p3 = "341/784955ed-dd41-40d0-a20f-22f3f899fb75";

    startTime = System.nanoTime();
    for(int i = 0; i < 1; i++) {
      final long len = new File("/project/sequest/tropix/production_data/" + p3).length();
    }
    endTime = System.nanoTime();
    long lenTime = endTime - startTime;

    long startTime2 = System.nanoTime();
    for(int i = 0; i < 1; i++) {
      final long len = new File("/project/sequest/tropix/production_data/" + p2).length();
    }
    long endTime2 = System.nanoTime();

    System.out.println(
        String.format("length took %d and last modified took %d ", endTime - startTime, endTime2 - startTime2));

  }

}
