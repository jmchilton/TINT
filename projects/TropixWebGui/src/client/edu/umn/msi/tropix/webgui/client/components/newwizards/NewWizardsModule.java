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

package edu.umn.msi.tropix.webgui.client.components.newwizards;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.client.Command;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import edu.umn.msi.tropix.webgui.client.components.LocationCommandComponentFactory;

/**
 * Binds all of the concrete new item implementations to named
 * objects available for injection.
 * 
 * @author John Chilton
 * 
 */
public class NewWizardsModule extends AbstractGinModule {

  /**
   * Specifies actual bindings.
   */
  protected void configure() {
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("folder")).to(FolderCommandComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("identificationWorkflow")).to(IdentificationWorkflowCommandComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("identificationAnalysis")).to(IdentificationAnalysisCommandComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("batchIdentificationAnalysis")).to(BatchIdentificationAnalysisCommandComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("bowtieAnalysis")).to(BowtieAnalysisCommandComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("scaffoldAnalysis")).to(ScaffoldAnalysisCommandComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("scaffoldQPlusAnalysis")).to(ScaffoldQPlusAnalysisCommandComponentFactoryImpl.class).in(Singleton.class);    
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("batchScaffoldAnalysis")).to(BatchScaffoldAnalysisCommandComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("idPickerAnalysis")).to(IdPickerAnalysisCommandComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("iTraqQuantificationAnalysis")).to(ITraqQuantitationAnalysisCommandComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("iTraqQuantificationTraining")).to(ITraqQuantitationTrainingCommandComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("bowtieIndex")).to(BowtieIndexUploadCommandComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("arbitraryFile")).to(ArbitraryFileUploadCommandComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("wikiNote")).to(NoteCommandComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("sequenceDatabase")).to(SequenceDatabaseCommandComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("uploadIdentificationAnalysis")).to(IdentificationAnalysisUploadCommandComponentFactoryImpl.class)
        .in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("tissueSample")).to(TissueSampleCommandComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named("proteomicsRun")).to(ProteomicsRunCommandComponentFactoryImpl.class).in(Singleton.class);
  }

}
