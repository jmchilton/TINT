package edu.umn.msi.tropix.webgui.server;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.files.PersistentModelStorageDataFactory;
import edu.umn.msi.tropix.models.IdentificationParameters;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.service.ParametersService;
import edu.umn.msi.tropix.persistence.service.TropixObjectLoaderService;
import edu.umn.msi.tropix.proteomics.client.IdentificationParameterExpander;
import edu.umn.msi.tropix.proteomics.client.ParameterMapUtils;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.webgui.server.security.UserSession;

@ManagedBean
public class ParameterLoaderImpl implements ParameterLoader {
  private static final Log LOG = LogFactory.getLog(ParameterLoaderImpl.class);
  private TropixObjectLoaderService tropixObjectService;
  private UserSession userSession;
  private ParametersService parametersService;
  private ParameterMapUtils xTandemParameterUtils;
  private IdentificationParameterExpander parameterExpander;
  private PersistentModelStorageDataFactory modelStorageDataFactory;

  public Map<String, String> loadParameterMap(final String id) {
    final IdentificationParameters params = (IdentificationParameters) tropixObjectService.load(userSession.getGridId(), id);
    if(params == null || params.getParametersId() == null) {
      return null;
    }
    Map<String, String> map = new HashMap<String, String>();
    final IdentificationType parameterType = IdentificationType.forParameters(params);
    LOG.trace("Loading parameters of type " + parameterType);
    if(parameterType.equals(IdentificationType.SEQUEST)) {
      ParameterUtils.setMapFromParameters(parametersService.loadSequestParameters(params.getParametersId()), map);
    } else if(parameterType.equals(IdentificationType.XTANDEM)) {
      ParameterUtils.setMapFromParameters(parametersService.loadXTandemParameters(params.getParametersId()), map);
      xTandemParameterUtils.fromRaw(map);
    } else {
      map = loadParameterMap(params, parameterType);
    }
    return map;
  }

  private Map<String, String> loadParameterMap(final IdentificationParameters params, final IdentificationType paramType) {
    final InputContext parameterDownloadContext = getParameterDownloadContext(params);
    return parameterExpander.loadParameterMap(paramType, parameterDownloadContext);
  }

  private InputContext getParameterDownloadContext(final IdentificationParameters params) {
    final TropixFile parameterFile = (TropixFile) tropixObjectService.load(userSession.getGridId(), params.getParametersId(),
        TropixObjectTypeEnum.FILE);
    final ModelStorageData modelStorageData = modelStorageDataFactory.getStorageData(parameterFile, userSession.getProxy());
    InputContext parameterDownloadContext = modelStorageData.getDownloadContext();
    return parameterDownloadContext;
  }

  @Inject
  public void setUserSession(final UserSession userSession) {
    this.userSession = userSession;
  }

  @Inject
  public void setParametersService(final ParametersService parametersService) {
    this.parametersService = parametersService;
  }

  @Inject
  public void setTropixObjectService(final TropixObjectLoaderService tropixObjectService) {
    this.tropixObjectService = tropixObjectService;
  }

  @Inject
  public void setTandemParameterUtils(@Named("xTandemParameterUtils") final ParameterMapUtils xTandemParameterUtils) {
    this.xTandemParameterUtils = xTandemParameterUtils;
  }

  @Inject
  public void setModelStorageDataFactory(final PersistentModelStorageDataFactory modelStorageDataFactory) {
    this.modelStorageDataFactory = modelStorageDataFactory;
  }

  @Inject
  public void setIdentificationParameterExpander(final IdentificationParameterExpander identificationParameterExpander) {
    this.parameterExpander = identificationParameterExpander;
  }

}