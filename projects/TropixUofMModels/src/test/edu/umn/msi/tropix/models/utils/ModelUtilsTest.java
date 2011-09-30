/*******************************************************************************
 * Copyright 2009 Regents of the University of Minnesota. All rights
 * reserved.
 * Copyright 2009 Mayo Foundation for Medical Education and Research.
 * All rights reserved.
 *
 * This program is made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS
 * OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A
 * PARTICULAR PURPOSE.  See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 ******************************************************************************/

package edu.umn.msi.tropix.models.utils;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.BowtieAnalysis;
import edu.umn.msi.tropix.models.BowtieIndex;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.ITraqQuantitationAnalysis;
import edu.umn.msi.tropix.models.ITraqQuantitationTraining;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.Run;
import edu.umn.msi.tropix.models.Sample;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.models.TissueSample;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;

public class ModelUtilsTest {
  private static <T extends TropixObject> T getInstance(final Class<T> clazz) {
    try {
      final T instance = clazz.newInstance();
      instance.setId(UUID.randomUUID().toString());
      return instance;
    } catch(final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test(groups = "unit")
  public void testGetChildren() {
    final TropixFile tropixFile = getInstance(TropixFile.class);
    assert Iterables.isEmpty(ModelUtils.getChildren(tropixFile));
    final Folder folder = getInstance(Folder.class);

    folder.setContents(Lists.<TropixObject>newArrayList(tropixFile));
    assert ModelUtils.getChildren(folder).contains(tropixFile);
    assert ModelUtils.hasChildren(folder);
    
    final VirtualFolder vFolder = getInstance(VirtualFolder.class);
    vFolder.setContents(Lists.<TropixObject>newArrayList(tropixFile));
    assert ModelUtils.getChildren(vFolder).contains(tropixFile);
    assert ModelUtils.hasChildren(vFolder);

    final Request request = getInstance(Request.class);
    request.setContents(Lists.<TropixObject>newArrayList(tropixFile));
    assert ModelUtils.getChildren(request).contains(tropixFile);
    assert ModelUtils.hasChildren(request);

    final Run run = getInstance(Run.class);
    assert ModelUtils.getChildren(run).isEmpty();
    assert !ModelUtils.hasChildren(run);

    final ProteomicsRun pRun = getInstance(ProteomicsRun.class);
    pRun.setMzxml(tropixFile);
    assert ModelUtils.getChildren(pRun).contains(tropixFile);
    assert ModelUtils.hasChildren(pRun);

    final TissueSample sample = getInstance(TissueSample.class);
    assert ModelUtils.getChildren(sample).isEmpty();
    assert !ModelUtils.hasChildren(sample);

    final TropixFile tropixFile2 = getInstance(TropixFile.class);
    
    final ITraqQuantitationAnalysis qAnalysis = new ITraqQuantitationAnalysis();
    qAnalysis.setOutput(tropixFile);
    qAnalysis.setReport(tropixFile2);
    assert ModelUtils.getChildren(qAnalysis).containsAll(Lists.newArrayList(tropixFile, tropixFile2));
    assert ModelUtils.hasChildren(qAnalysis);
    
    final ITraqQuantitationTraining qTraining = new ITraqQuantitationTraining();
    qTraining.setTrainingFile(tropixFile);
    qTraining.setReport(tropixFile2);
    assert ModelUtils.getChildren(qTraining).containsAll(Lists.newArrayList(tropixFile, tropixFile2));
    assert ModelUtils.hasChildren(qTraining);
    
    final BowtieIndex bowtieIndex = new BowtieIndex();
    bowtieIndex.setIndexesFile(tropixFile);
    assert ModelUtils.getChildren(bowtieIndex).contains(tropixFile);
    assert ModelUtils.hasChildren(bowtieIndex);

    final BowtieAnalysis bowtieAnalysis = new BowtieAnalysis();
    bowtieAnalysis.setOutput(tropixFile);
    assert ModelUtils.getChildren(bowtieAnalysis).contains(tropixFile);
    assert ModelUtils.hasChildren(bowtieAnalysis);

    
    final Database database = new Database();
    database.setDatabaseFile(tropixFile);
    assert ModelUtils.getChildren(database).contains(tropixFile);
    assert ModelUtils.hasChildren(database);
    
    final IdentificationAnalysis iAnalysis = new IdentificationAnalysis();
    iAnalysis.setOutput(tropixFile);
    assert ModelUtils.getChildren(iAnalysis).contains(tropixFile);
    assert ModelUtils.hasChildren(iAnalysis);
    
    final ScaffoldAnalysis sAnalysis = new ScaffoldAnalysis();
    sAnalysis.setOutputs(tropixFile);
    sAnalysis.setInput(tropixFile2);
    assert ModelUtils.getChildren(sAnalysis).containsAll(Lists.newArrayList(tropixFile, tropixFile2));
    assert ModelUtils.hasChildren(sAnalysis);
    
  }

  @Test(groups = "unit")
  public void testConstructor() {
    new ModelUtils();
  }
  
  @Test(groups = "unit")
  public void getExtension() {
    final TropixFile fileNoExtension = getInstance(TropixFile.class);
    fileNoExtension.setName("moo");
    assert ModelUtils.getExtension(fileNoExtension).equals("");
    
    final TropixFile fileWithExtension = getInstance(TropixFile.class);
    final FileType fileType = new FileType();
    fileType.setExtension(".cow");
    fileWithExtension.setFileType(fileType);
    assert ModelUtils.getExtension(fileWithExtension).equals(".cow");
  }

  @Test(groups = "unit")
  public void testGetIds() {
    final List<TropixObject> list = Lists.newArrayList();
    list.add(getInstance(Run.class));
    list.add(getInstance(Sample.class));
    assert Iterables.elementsEqual(ModelUtils.getIds(list), Lists.newArrayList(list.get(0).getId(), list.get(1).getId()));
  }
  
}
