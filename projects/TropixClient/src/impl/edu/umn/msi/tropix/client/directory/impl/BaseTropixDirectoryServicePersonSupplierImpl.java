package edu.umn.msi.tropix.client.directory.impl;

import info.minnesotapartnership.tropix.directory.TropixDirectoryService;
import info.minnesotapartnership.tropix.directory.models.Person;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Supplier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;

public abstract class BaseTropixDirectoryServicePersonSupplierImpl implements Supplier<Multimap<String, Person>> {
  private static final Log LOG = LogFactory.getLog(CaGridTropixDirectoryServicePersonSupplierImpl.class);

  protected abstract TropixDirectoryService getService(final String address);

  private Map<String, String> institutionToServiceAddressMap;

  public Multimap<String, Person> get() {
    // LOG.trace("Host proxy obtained " + proxy);
    Multimap<String, Person> personMap = HashMultimap.create();
    for(final Entry<String, String> institutionAddressEntry : institutionToServiceAddressMap.entrySet()) {
      final String institution = institutionAddressEntry.getKey();
      final String address = institutionAddressEntry.getValue();
      try {
        LOG.trace("Obtaining person array from directory service " + address);
        final TropixDirectoryService directoryService = getService(address);
        final Person[] personArray = directoryService.getUsers();
        if(personArray != null) {
          personMap.putAll(institution, Arrays.asList(personArray));
        } else {
          LOG.info("Directory service " + address + " returned a null Person array.");
        }
      } catch(final RuntimeException e) {
        ExceptionUtils.logQuietly(LOG, e);
        continue;
      }
    }
    LOG.trace("Returning person multimap");
    return personMap;
  }

  public void setInstitutionToServiceAddressMap(final Map<String, String> institutionToServiceAddressMap) {
    this.institutionToServiceAddressMap = institutionToServiceAddressMap;
  }

}