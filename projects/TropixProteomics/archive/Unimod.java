package edu.umn.msi.tropix.proteomics;

import java.io.*;
import java.util.*;

import javax.xml.bind.*;

import org.apache.log4j.Logger;
import org.unimod.*;


import java.util.Map;
import java.util.HashMap;


//TODOMAYBE: Cache results
//TODO: Add the abaility to reload the database
//TODO: Clean out class, it has a lot of old unused code
public class Unimod {
  UnimodT unimodDB;
  private static Logger logger = Logger.getLogger(Unimod.class);

  public Unimod(String filePath) {
    this(new File(filePath));
  }

  public  UnimodT getUnimodT() {
    return unimodDB;
  }

  public Unimod(File file) throws IllegalArgumentException {
    try { 
      FileReader reader = new FileReader(file);
      initUnimodDB(reader);
    } catch(FileNotFoundException e) {
      throw new IllegalArgumentException("Could not find unimod file.", e);
    }
  } 

  public Unimod(Reader reader) {
    initUnimodDB(reader);
  }

  public Unimod(InputStream stream) {
    this(new InputStreamReader(stream));
  }

  private void initUnimodDB(Reader reader) throws IllegalArgumentException {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance( "org.unimod" );
      Unmarshaller unmarshal = jaxbContext.createUnmarshaller();
      Object unmarshalled = unmarshal.unmarshal(reader);
      JAXBElement<UnimodT> unimodElement = (JAXBElement<UnimodT>) unmarshalled;
      unimodDB = (UnimodT) unimodElement.getValue();
    } catch(JAXBException e) {
      throw new IllegalArgumentException("Unimod XML file or configuration error.",e);
    }
  }	

  private List<ModT> getModifications() {
    return unimodDB.getModifications().getMod();
  }

  public ModT getModificationFromRecordId(long id) {
    List<ModT> modifications = getModifications();
    for(ModT mod : modifications) {
      if(mod.getRecordId() == id) {
        return mod;
      }
    }
    return null;
  }

  public ModT getModificationFromRecordId(String id) {
    return getModificationFromRecordId(Long.parseLong(id));
  }

  public ModT getModification(UnimodId id) {
    return getModificationFromRecordId(id.getModId());
  }

  public SpecificityT getSpecificity(UnimodId id) {
    return getSpecificity(getModification(id), id);
  }

  public SpecificityT getSpecificity(ModT mod, UnimodId id) {
    return getSpecificityFromGroupId(mod, id.getSpecificityId());
  }

  public ModT getModificationByTitle(String title) {
    List<ModT> modifications = getModifications();
    for(ModT mod : modifications) {
      if(mod.getTitle().equals(title)) return mod;
    }
    return null;
  }

  public ModT getModificationByFullName(String fullName) {
    List<ModT> modifications = getModifications();
    for(ModT mod : modifications) {
      if(mod.getFullName().equals(fullName)) return mod;
    }
    return null;	
  }

  public List<String> getDisplayString() {
    List<ModT> modifications = getModifications();
    List<String> strings = new LinkedList<String>();
    for(ModT mod : modifications) {
      for(SpecificityT specificity : mod.getSpecificity()) {
        strings.add(mod.getTitle() + " (" + specificity.getSite() + " / " + specificity.getPosition().toString() + ")");
      }
    }
    return strings;
  }

  public List<UnimodDisplay> getUnimodDisplayList() {
    List<ModT> modifications = getModifications();
    List<UnimodDisplay> displayList = new LinkedList<UnimodDisplay>();
    for(ModT mod : modifications) {
      for(SpecificityT specificity : mod.getSpecificity()) {
        if(!specificity.isHidden()) {
          String displayString = mod.getTitle() + " (" + specificity.getSite() + " / " + specificity.getPosition().toString() + ")";
          UnimodId unimodId = new UnimodId(mod.getRecordId(), Long.parseLong(specificity.getSpecGroup().toString()));
          displayList.add(new UnimodDisplay(displayString, unimodId));
        }
      }
    }
    return displayList;
  }




  public ModT getModificationFromDisplayString(String displayString) {
    int index = displayString.lastIndexOf('(') - 1;
    String title = displayString.substring(0,index);
    return getModificationByTitle(title);
  }

  public String getSpecificitySiteFromDisplayString(String displayString) {
    int beginIndex = displayString.lastIndexOf('(') + 1;
    int endIndex = displayString.lastIndexOf('/') - 1;
    return displayString.substring(beginIndex, endIndex);
  }

  public String getSpecificityPositionFromDisplayString(String displayString) {
    int beginIndex = displayString.lastIndexOf('/') + 1;
    int endIndex   = displayString.lastIndexOf(')');
    return displayString.substring(beginIndex, endIndex);
  }

  public SpecificityT getSpecificityFromDisplayString(String displayString) {
    ModT modification = getModificationFromDisplayString(displayString);
    String site     = getSpecificitySiteFromDisplayString(displayString);
    String position = getSpecificityPositionFromDisplayString(displayString); 
    SpecificityT specificity = null;
    for(SpecificityT spec : modification.getSpecificity()) {
      if(spec.getSite().equals(site) && spec.getPosition().toString().equals(position)) {
        specificity = spec;
        break;
      }
    }
    return specificity;
  }

  public SpecificityT getSpecificityFromIds(long recordId, long groupId) {
    ModT mod = getModificationFromRecordId(recordId);
    if(mod == null) return null;
    return getSpecificityFromGroupId(mod, groupId);
  }

  public SpecificityT getSpecificityFromIds(String recordId, String groupId) {
    return getSpecificityFromIds(Long.parseLong(recordId), Long.parseLong(groupId));
  }

  public SpecificityT getSpecificityFromGroupId(ModT mod, long groupId) {
    for(SpecificityT specificity : mod.getSpecificity()) {
      if(specificity.getSpecGroup().equals(new java.math.BigInteger(""+groupId))) {
        return specificity;
      }
    }
    return null;
  }

  public SpecificityT getSpecificityFromGroupId(ModT mod, String groupId) {
    return getSpecificityFromGroupId(mod, Long.parseLong(groupId));
  }

  public double[] getDeltaPairFromDisplayString(String displayString) {
    ModT modification = getModificationFromDisplayString(displayString);
    double[] deltaPair = new double[2];
    deltaPair[0] = modification.getDelta().getMonoMass();
    deltaPair[1] = modification.getDelta().getAvgeMass();
    return deltaPair;
  }

  public double getDeltaMonoFromDisplayString(String displayString) {
    ModT modification = getModificationFromDisplayString(displayString);
    return modification.getDelta().getMonoMass();
  }

  public double getDeltaAvgeFromDisplayString(String displayString) { 
    ModT modification = getModificationFromDisplayString(displayString);
    return modification.getDelta().getAvgeMass();
  }

  private static void addToAggregateMap(Map<String, Double> map, String key, double value) {
    if(map.containsKey(key)) {
      map.put(key, map.get(key) + value);
    } else {
      map.put(key, value);
    }
  }




  public Map<String, Double> aggregateModifications(UnimodId[] ids, boolean monoisotopicMasses) {
    return aggregateModifications(Arrays.asList(ids), monoisotopicMasses);
  }

  public Map<String, Double> aggregateModifications(Collection<UnimodId> ids, boolean monoisotopicMasses) {
    Map<String, Double> aggregateMap = new HashMap<String, Double>();
    double value;
    ModT mod;
    SpecificityT specificity; 

    for(UnimodId id : ids) {

      mod = getModification(id);
      if(mod == null) {
        logger.warn("Failed to aggregate modification (not found) " + id);
        continue;
      }

      specificity = getSpecificity(mod, id);
      if(specificity == null) {
        logger.warn("Failed to aggregate modification (not found) " + id);
        continue;
      }

      if(monoisotopicMasses) {
        value = mod.getDelta().getMonoMass();
      } else {
        value = mod.getDelta().getAvgeMass();
      } 

      String site     = specificity.getSite();
      PositionT position = specificity.getPosition();
      if(!site.equals("N-term") && !site.equals("C-term") && position.equals(PositionT.ANYWHERE)) {
        // Assume its a residue!
        addToAggregateMap(aggregateMap, site, value);
      } else if(site.equals("N-term") && position.equals(PositionT.PROTEIN_N_TERM)) {
        addToAggregateMap(aggregateMap, "Protein N-term", value);
      } else if(site.equals("N-term") && (position.equals(PositionT.ANYWHERE) || position.equals(PositionT.ANY_N_TERM))){
        addToAggregateMap(aggregateMap, "N-term", value);
      } else if(site.equals("C-term") && position.equals(PositionT.PROTEIN_C_TERM)) {
        addToAggregateMap(aggregateMap, "Protein C-term", value);
      } else if(site.equals("C-term") && (position.equals(PositionT.ANYWHERE) || position.equals(PositionT.ANY_C_TERM) )) {
        addToAggregateMap(aggregateMap, "C-term", value);
      } else {
        logger.warn("Failed to aggregate modification (do not know how to) " + id);
      }
    }

    return aggregateMap;
  }
}

