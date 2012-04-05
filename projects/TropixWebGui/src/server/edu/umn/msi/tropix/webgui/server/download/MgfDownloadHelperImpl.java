package edu.umn.msi.tropix.webgui.server.download;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.springframework.util.StringUtils;

import com.google.common.base.Function;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.files.PersistentModelStorageDataFactory;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions.MgfStyle;
import edu.umn.msi.tropix.proteomics.conversion.ScanTransformers;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.webgui.server.security.UserSession;

@ManagedBean
public class MgfDownloadHelperImpl implements MgfDownloadHelper {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private final UserSession userSession;
  private final TropixObjectService tropixObjectService;
  private final PersistentModelStorageDataFactory storageDataFactory;
  private final MzXMLToMGFConverter mzxmlToMgfConverter;

  @Inject
  MgfDownloadHelperImpl(final UserSession userSession, final TropixObjectService tropixObjectService,
      final PersistentModelStorageDataFactory storageDataFactory, final MzXMLToMGFConverter mzxmlToMgfConverter) {
    this.userSession = userSession;
    this.tropixObjectService = tropixObjectService;
    this.storageDataFactory = storageDataFactory;
    this.mzxmlToMgfConverter = mzxmlToMgfConverter;
  }

  public void writeMgf(final TropixFile mzxmlTropixFile, final Function<String, String> accessor, final OutputStream outputStream) {
    final String style = accessor.apply("mgfStyle");
    final StorageData storageData = storageDataFactory.getStorageData(mzxmlTropixFile, userSession.getProxy());
    final File mzxmlFile = FILE_UTILS.createTempFile("tpx", "mzxml");
    InputStream mzxmlInputStream = null;
    try {
      storageData.getDownloadContext().get(mzxmlFile);
      mzxmlInputStream = FILE_UTILS.getFileInputStream(mzxmlFile);
      final MgfConversionOptions options = new MgfConversionOptions();
      if(StringUtils.hasText(style)) {
        options.setMgfStyle(MgfStyle.valueOf(style));
      }
      final String filterITraq = accessor.apply("filterITraq");
      if("true".equals(filterITraq)) {
        options.addScanTransformer(ScanTransformers.getITraqFilter());
      }
      mzxmlToMgfConverter.mzxmlToMGF(mzxmlInputStream, outputStream, options);
    } finally {
      IO_UTILS.closeQuietly(mzxmlInputStream);
      FILE_UTILS.deleteQuietly(mzxmlFile);
    }
  }

  public TropixFile getMzXMLTropixFile(final String objectId) {
    final String userId = userSession.getGridId();
    return (TropixFile) tropixObjectService.getAssociation(userId, objectId, "mzxml");
  }

  public void writeMgf(final String objectId, final Function<String, String> accessor, final OutputStream outputStream) {
    final TropixFile file = getMzXMLTropixFile(objectId);
    writeMgf(file, accessor, outputStream);
  }

}
