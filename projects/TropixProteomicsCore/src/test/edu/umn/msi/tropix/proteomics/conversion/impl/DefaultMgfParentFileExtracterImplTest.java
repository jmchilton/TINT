package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.util.Iterator;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.TempDirectoryTest;
import edu.umn.msi.tropix.proteomics.conversion.Scan;
import edu.umn.msi.tropix.proteomics.conversion.impl.MgfToMzXMLConverterStreamingImpl.MgfParentFileExtracter;
import edu.umn.msi.tropix.proteomics.conversion.impl.MzXmlStreamWriterUtils.MzXmlParentFileType;

public class DefaultMgfParentFileExtracterImplTest extends TempDirectoryTest {

  @Test(groups = "unit")
  public void testSha1AlwaysNull() {
    final MgfParentFileExtracter strategy = new DefaultMgfParentFileExtracterImpl();
    assert null == strategy.getParentFileSha1ForScan(new Scan(0, 0, 0, null));
  }

  @Test(groups = "unit")
  public void testSha1() {
    writeToFile("test.mgf", "moo");

    final MgfParentFileExtracter strategy = new DefaultMgfParentFileExtracterImpl();
    final Iterator<MzxmlParentFile> files = strategy.getAllParentFiles(getFile("test.mgf"));
    assert files.hasNext();
    final MzxmlParentFile parentFile = files.next();
    assert !files.hasNext();
    assert parentFile.getFileType() == MzXmlParentFileType.PROCESSED;
    assert parentFile.getSha1().equals(ConversionUtilsTest.MOO_SHA1) : parentFile.getSha1();
    assert parentFile.getSourceFileName().equals("test.mgf");
  }

}
