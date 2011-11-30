package edu.umn.msi.tropix.galaxy;

import java.io.File;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.PropertiesUtils;
import edu.umn.msi.tropix.common.io.PropertiesUtilsFactory;

/**
 * This class is responsible for mapping galaxy tool ids into a service url to execute
 * the tool via.
 * 
 * @author John Chilton (jmchilton at gmail dot com)
 *
 */
public class GalaxyServiceFunctionImpl implements Function<String, String> {
  private static final Log LOG = LogFactory.getLog(GalaxyServiceFunctionImpl.class);
  private static final PropertiesUtils PROPERTIES_UTILS = PropertiesUtilsFactory.getInstance();
  private final String defaultService;
  private final Function<String, String> serviceMap;
  
  public GalaxyServiceFunctionImpl(final String defaultService, final File serviceMapFile) {
    this.defaultService = defaultService;
    
    if(serviceMapFile != null) {
      if(!serviceMapFile.exists()) {
        LOG.info(String.format("Galaxy service map file %s does not exist, all tool will use default service %s", serviceMapFile.getAbsolutePath(), defaultService));
        serviceMap = null;
      }  else {
        final Properties properties = PROPERTIES_UTILS.load(serviceMapFile);
        serviceMap = Functions.forMap(Maps.fromProperties(properties), null);        
      }
    } else {
      serviceMap = null;
    }
  }
  
  public String apply(final String input) {
    String service = defaultService;
    if(serviceMap != null) {
      final String mappedService = serviceMap.apply(input);
      if(mappedService != null) {
        service = mappedService;
      }
    }
    return service;
  }

}
