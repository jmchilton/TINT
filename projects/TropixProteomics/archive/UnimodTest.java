package edu.umn.msi.tropix.proteomics.test;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.umn.msi.tropix.proteomics.Unimod;
import edu.umn.msi.tropix.proteomics.UnimodDisplay;
import edu.umn.msi.tropix.proteomics.UnimodId;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.unimod.*;


public class UnimodTest {
	Unimod unimod;
	
	@Test(groups={"unit","unimod"})
	public void unimodDisplayBeanTest() {
	  UnimodId id1 = new UnimodId("1;2");
	  UnimodId id2 = new UnimodId("3;4");
	  UnimodDisplay display = new UnimodDisplay("moo",id1);
	  assert display.getDisplayString().equals("moo");
	  assert display.getUnimodId() == id1;
	  display.setDisplayString("new moo");
	  assert display.getDisplayString().equals("new moo");
	  display.setUnimodId(id2);
	  assert display.getUnimodId() == id2;
	}
	
  @Test(groups={"unit","unimod"})
	public void unimodIdToFromString() {
    UnimodId id = UnimodId.fromString("1;23");
    assert id.getModId() == 1l;
    assert id.getSpecificityId() == 23l;
    assert id.toString().equals("1;23");
	}
  
  @Test(groups="unit")
  public void unimodSpecificity() {
    UnimodId id = new UnimodId(1,2);
    assert id.getSpecificityId() == 2;
    id.setSpecificityId("4");
    assert id.getSpecificityId() == 4;
  }
  

	
	@BeforeClass(groups={"unimod", "unit"}) 
	public void preprocess() {
		unimod = new Unimod(getClass().getResourceAsStream("unimod.xml"));
	}
	
	@Test(groups={"unimod", "unit"})
	public void testUnimodId() throws Exception {
		String[] validIds = new String[] {"101;2232"," 101;2232 ", "101 ;   2232","    123459 ;   678     "};
		String[] invalidIds = new String[] {"12,23", "1.2;3", "301921", "a;b", "a;3", "3:a"};
		boolean exceptionThrown;
		for(String validId : validIds) {
			exceptionThrown = false;
			try {
				new UnimodId(validId);
			} catch(IllegalArgumentException e) {
				exceptionThrown = true;
			}
			assert !exceptionThrown : "Failed to parse id " + validIds; 
		}
		
		for(String invalidId : invalidIds) {
			exceptionThrown = false;
			try {
				new UnimodId(invalidId);
			} catch(IllegalArgumentException e) {
				exceptionThrown = true;
			}
			assert exceptionThrown : "Didn't fail to parse id " + validIds; 
		}

		UnimodId unimodId = new UnimodId(" 123 ;456   ");
		assert unimodId.getModId() == 123L : "getModId returned incorrect value.";
		assert unimodId.getSpecificityId() == 456L : "getSpecificityId returned incorrect value.";

		unimodId = new UnimodId(123, 456);
		assert unimodId.getModId() == 123L : "getModId returned incorrect value.";
		assert unimodId.getSpecificityId() == 456L : "getSpecificityId returned incorrect value.";

		unimodId.setModId(567);
		assert unimodId.getModId() == 567L : "setModId failed.";

		unimodId.setModId("567");
		assert unimodId.getModId() == 567L : "setModId from String failed.";

		unimodId.setSpecificityId(567);
		assert unimodId.getSpecificityId() == 567L : "setSpecificityId failed.";

		unimodId.setModId("567");
		assert unimodId.getSpecificityId() == 567L : "setSpecificityId from String failed.";	
	}
	
	@Test(groups = {"unimod","unit"})
	public void testGetModificationByTitle() {
		String[] namesToCheck = {"Delta:H(6)C(6)O(1)", "iTRAQ4plex", "NEM:2H(5)"};
		for(String nameToCheck : namesToCheck) {
			ModT modT = unimod.getModificationByTitle(nameToCheck);
			assert modT != null : "No modification found for given title.";
			assert modT.getTitle().equals(nameToCheck) : "Found title does not match. ";
		}

		assert unimod.getModificationByTitle("atitlethatwillnotappear") == null : "getModififcationByTitle didn't return null.";
	}

	@Test(groups = {"unimod", "unit"})	
	public void testGetModificationFromDisplayString() {
		String displayString = 	"Carboxymethyl (C / Anywhere)";
		assert unimod.getModificationByTitle("Carboxymethyl").equals(unimod.getModificationFromDisplayString(displayString)); 
	}
	
	@Test(groups = {"unimod", "unit"})	
	public void testGetSpecificitySiteFromDisplayString() {
		assert unimod.getSpecificitySiteFromDisplayString("Carboxymethyl (C / Anywhere)").equals("C");
		assert unimod.getSpecificitySiteFromDisplayString("Cation:Na (C-term / Any C-term)").equals("C-term");
	}
	
	@Test(groups = {"unimod", "unit"})
	public void testGetDeltaMethods() {
		assert unimod.getDeltaMonoFromDisplayString("Cation:Na (C-term / Any C-term)") == 21.981943;
		assert unimod.getDeltaAvgeFromDisplayString("Cation:Na (C-term / Any C-term)") == 21.9818;
		double[] pair = unimod.getDeltaPairFromDisplayString("Cation:Na (C-term / Protein C-term)");
		assert pair.length == 2;
		assert pair[0] == 21.981943;
		assert pair[1] == 21.9818;
	}
	
	
	private String shorten(PositionT position) {
		switch(position) {
		case ANY_C_TERM :
			return "CTerm";
		case ANY_N_TERM :
			return "NTerm";
		case PROTEIN_C_TERM:
			return "ProCTerm";
		case PROTEIN_N_TERM:
			return "ProNTerm";
		}
		return "";
	}
	
	@Test(groups = {"unimodlist", "unit"})
	public void listModifications() throws Exception {
		UnimodT unimodDB = unimod.getUnimodT();
		FileWriter writer = new FileWriter(new File("test/unimod.csv"));
		List<ModT> modifications = unimodDB.getModifications().getMod();
		for(ModT mod : modifications) {
			String title = mod.getTitle();
			String fullName = mod.getFullName();
			Double modificationMass = mod.getDelta().getMonoMass();
			String composition = mod.getDelta().getComposition();
			String description = mod.getMiscNotes();
			StringBuffer sites = new StringBuffer();
			boolean first = true;
			for(SpecificityT spec : mod.getSpecificity()) {
				if(first) {
					first = false;
				} else {
					sites.append(", ");
				}
				String site = spec.getSite();
				PositionT position = spec.getPosition();
				if(position.equals(PositionT.PROTEIN_C_TERM) || position.equals(PositionT.PROTEIN_N_TERM) &&
					site.equals("N-term") || site.equals("C-term")) {
					sites.append("Pro " + site);
				} else if(site.equals("N-term") || site.equals("C-term")) {
					sites.append(site);
				} else if(position.equals(PositionT.ANYWHERE)) {
					sites.append(site);
				} else {
					sites.append(site + "-" + shorten(position));
				}	
			}
			writer.write(title + "\t" + fullName + "\t" + modificationMass + "\t" + sites + "\t " + composition);
			if(description != null) {
				writer.write("\t" + description);
			} 
			writer.write("\n");
		}
	}
	
	
	private boolean doubleEquals(double d1, double d2) {
		return Math.abs(d1 - d2) < .00000001;
	}
	
	
	@Test(groups= {"unimod", "unit"})
	public void testAggregateModifications() {
		List<UnimodId> unimodIds = new LinkedList<UnimodId>();
		Map<String, Double> map;
		
		map = unimod.aggregateModifications(unimodIds, true);
		assert map.isEmpty();
		map = unimod.aggregateModifications(unimodIds, false);
		assert map.isEmpty();
		
		unimodIds.add(new UnimodId("1;5"));
		map = unimod.aggregateModifications(unimodIds, true);
		assert map.containsKey("Protein N-term");
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.010565);
		assert !map.containsKey("N-term");
		assert !map.containsKey("Protein C-term");
		assert !map.containsKey("C-term");
		assert !map.containsKey("K");
		assert !map.containsKey("C");
		
		
		map = unimod.aggregateModifications(unimodIds, false);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.0367);
		assert !map.containsKey("N-term");
		assert !map.containsKey("Protein C-term");
		assert !map.containsKey("C-term");
		assert !map.containsKey("K");
		assert !map.containsKey("C");
		
		unimodIds.add(new UnimodId("3;2"));
		
		map = unimod.aggregateModifications(unimodIds, true);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.010565);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.077598);
		assert !map.containsKey("Protein C-term");
		assert !map.containsKey("C-term");
		assert !map.containsKey("K");
		assert !map.containsKey("C");

		
		map = unimod.aggregateModifications(unimodIds, false);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.0367);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.2954);
		assert !map.containsKey("Protein C-term");
		assert !map.containsKey("C-term");
		assert !map.containsKey("K");
		assert !map.containsKey("C");
		
		unimodIds.add(new UnimodId("34;10"));
		
		map = unimod.aggregateModifications(unimodIds, true);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.010565+14.015650);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.077598);
		assert !map.containsKey("Protein C-term");
		assert !map.containsKey("C-term");
		assert !map.containsKey("K");
		assert !map.containsKey("C");		
				
		map = unimod.aggregateModifications(unimodIds, false);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.0367+14.0266);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.2954);
		assert !map.containsKey("Protein C-term");
		assert !map.containsKey("C-term");
		assert !map.containsKey("K");
		assert !map.containsKey("C");

		
		
		
		unimodIds.add(new UnimodId("34;5"));
		
		map = unimod.aggregateModifications(unimodIds, true);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.010565+14.015650);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.077598+14.015650);
		assert !map.containsKey("Protein C-term");
		assert !map.containsKey("C-term");
		assert !map.containsKey("K");
		assert !map.containsKey("C");

		map = unimod.aggregateModifications(unimodIds, false);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.0367+14.0266);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.2954+14.0266);
		assert !map.containsKey("Protein C-term");
		assert !map.containsKey("C-term");
		assert !map.containsKey("K");
		assert !map.containsKey("C");

		
		unimodIds.add(new UnimodId("289;1")); // Protein C-Term 356.188212  356.4835
		
		map = unimod.aggregateModifications(unimodIds, true);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.010565+14.015650);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.077598+14.015650);
		assert map.containsKey("Protein C-term") && doubleEquals(map.get("Protein C-term"), 356.188212);
		assert !map.containsKey("C-term");
		assert !map.containsKey("K");
		assert !map.containsKey("C");		
		
		map = unimod.aggregateModifications(unimodIds, false);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.0367+14.0266);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.2954+14.0266);
		assert map.containsKey("Protein C-term") && doubleEquals(map.get("Protein C-term"), 356.4835);
		assert !map.containsKey("C-term");
		assert !map.containsKey("K");
		assert !map.containsKey("C");

		
		unimodIds.add(new UnimodId("394;1")); // Protein C-Term 123.008530 123.0477
		
		map = unimod.aggregateModifications(unimodIds, true);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.010565+14.015650);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.077598+14.015650);
		assert map.containsKey("Protein C-term") && doubleEquals(map.get("Protein C-term"), 356.188212 + 123.008530);
		assert !map.containsKey("C-term");
		assert !map.containsKey("K");
		assert !map.containsKey("C");		
		
		map = unimod.aggregateModifications(unimodIds, false);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.0367 + 14.0266);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.2954+14.0266);
		assert map.containsKey("Protein C-term") && doubleEquals(map.get("Protein C-term"), 356.4835 + 123.0477);
		assert !map.containsKey("C-term");
		assert !map.containsKey("K");
		assert !map.containsKey("C");
				
		unimodIds.add(new UnimodId("734;1")); // Any C-Term 43.042199, 43.0678
		
		map = unimod.aggregateModifications(unimodIds, true);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.010565+14.015650);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.077598+14.015650);
		assert map.containsKey("Protein C-term") && doubleEquals(map.get("Protein C-term"), 356.188212 + 123.008530);
		assert map.containsKey("C-term") && doubleEquals(map.get("C-term"), 43.042199);
		assert !map.containsKey("K");
		assert !map.containsKey("C");		
		
		map = unimod.aggregateModifications(unimodIds, false);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.0367 + 14.0266);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.2954+14.0266);
		assert map.containsKey("Protein C-term") && doubleEquals(map.get("Protein C-term"), 356.4835 + 123.0477);
		assert map.containsKey("C-term") && doubleEquals(map.get("C-term"), 43.0678);
		assert !map.containsKey("K");
		assert !map.containsKey("C");

		unimodIds.add(new UnimodId("2;2")); // Any C-Term -0.984016 , -0.9848

		map = unimod.aggregateModifications(unimodIds, true);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.010565+14.015650);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.077598+14.015650);
		assert map.containsKey("Protein C-term") && doubleEquals(map.get("Protein C-term"), 356.188212 + 123.008530);
		assert map.containsKey("C-term") && doubleEquals(map.get("C-term"), 43.042199 -  0.984016);
		assert !map.containsKey("K");
		assert !map.containsKey("C");		
		
		map = unimod.aggregateModifications(unimodIds, false);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.0367 + 14.0266);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.2954+14.0266);
		assert map.containsKey("Protein C-term") && doubleEquals(map.get("Protein C-term"), 356.4835 + 123.0477);
		assert map.containsKey("C-term") && doubleEquals(map.get("C-term"), 43.0678 - 0.9848);
		assert !map.containsKey("K");
		assert !map.containsKey("C");
		
		unimodIds.add(new UnimodId("3;1")); // K 226.077598,  226.2954

		map = unimod.aggregateModifications(unimodIds, true);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.010565+14.015650);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.077598+14.015650);
		assert map.containsKey("Protein C-term") && doubleEquals(map.get("Protein C-term"), 356.188212 + 123.008530);
		assert map.containsKey("C-term") && doubleEquals(map.get("C-term"), 43.042199 -  0.984016);
		assert map.containsKey("K") && doubleEquals(map.get("K"), 226.077598);
		assert !map.containsKey("C");		
		
		map = unimod.aggregateModifications(unimodIds, false);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.0367 + 14.0266);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.2954+14.0266);
		assert map.containsKey("Protein C-term") && doubleEquals(map.get("Protein C-term"), 356.4835 + 123.0477);
		assert map.containsKey("C-term") && doubleEquals(map.get("C-term"), 43.0678 - 0.9848);
		assert map.containsKey("K") && doubleEquals(map.get("K"), 226.2954);
		assert !map.containsKey("C");
		
		unimodIds.add(new UnimodId("4;2")); // K 57.021464, 57.0513

		map = unimod.aggregateModifications(unimodIds, true);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.010565+14.015650);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.077598+14.015650);
		assert map.containsKey("Protein C-term") && doubleEquals(map.get("Protein C-term"), 356.188212 + 123.008530);
		assert map.containsKey("C-term") && doubleEquals(map.get("C-term"), 43.042199 -  0.984016);
		assert map.containsKey("K") && doubleEquals(map.get("K"), 226.077598 + 57.021464);
		assert !map.containsKey("C");		
		
		map = unimod.aggregateModifications(unimodIds, false);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.0367 + 14.0266);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.2954+14.0266);
		assert map.containsKey("Protein C-term") && doubleEquals(map.get("Protein C-term"), 356.4835 + 123.0477);
		assert map.containsKey("C-term") && doubleEquals(map.get("C-term"), 43.0678 - 0.9848);
		assert map.containsKey("K") && doubleEquals(map.get("K"), 226.2954 + 57.0513);
		assert !map.containsKey("C");

		
		unimodIds.add(new UnimodId("4;1")); // Ditto with C.
		
		map = unimod.aggregateModifications(unimodIds, true);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.010565+14.015650);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.077598+14.015650);
		assert map.containsKey("Protein C-term") && doubleEquals(map.get("Protein C-term"), 356.188212 + 123.008530);
		assert map.containsKey("C-term") && doubleEquals(map.get("C-term"), 43.042199 -  0.984016);
		assert map.containsKey("K") && doubleEquals(map.get("K"), 226.077598 + 57.021464);
		assert map.containsKey("C") && doubleEquals(map.get("C"), 57.021464);		
		
		map = unimod.aggregateModifications(unimodIds, false);
		assert map.containsKey("Protein N-term") && doubleEquals(map.get("Protein N-term"), 42.0367 + 14.0266);
		assert map.containsKey("N-term") && doubleEquals(map.get("N-term"), 226.2954+14.0266);
		assert map.containsKey("Protein C-term") && doubleEquals(map.get("Protein C-term"), 356.4835 + 123.0477);
		assert map.containsKey("C-term") && doubleEquals(map.get("C-term"), 43.0678 - 0.9848);
		assert map.containsKey("K") && doubleEquals(map.get("K"), 226.2954 + 57.0513);
		assert map.containsKey("C") && doubleEquals(map.get("C"), 57.0513);		
		
	}
	
}
