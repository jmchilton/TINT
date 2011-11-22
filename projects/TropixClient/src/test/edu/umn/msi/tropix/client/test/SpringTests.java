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

package edu.umn.msi.tropix.client.test;

import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import info.minnesotapartnership.tropix.directory.models.Person;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.xml.namespace.QName;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.client.directory.GridUser;
import edu.umn.msi.tropix.client.metadata.MetadataResolver;
import edu.umn.msi.tropix.client.search.TropixSearchClient;
import edu.umn.msi.tropix.client.services.GridService;
import edu.umn.msi.tropix.client.services.IdentificationGridService;
import edu.umn.msi.tropix.client.services.QueueGridService;
import edu.umn.msi.tropix.common.jobqueue.queuestatus.QueueStatus;
import edu.umn.msi.tropix.common.test.FreshConfigTest;
import edu.umn.msi.tropix.proteomics.scaffold.metadata.ScaffoldMetadata;
import edu.umn.msi.tropix.proteomics.scaffold.metadata.ScaffoldVersion;

@ContextConfiguration(locations = "classpath:edu/umn/msi/tropix/client/test/testApplicationContext.xml")
public class SpringTests extends FreshConfigTest {

  @Resource
  private Supplier<Multimap<String, Person>> directoryServicePersonSupplier;

  @SuppressWarnings({"unchecked", "unused"})
  @Test
  public void load() throws RemoteException {

    // edu.umn.msi.tropix.labs.catalog.impl.LocalCatalogInstanceImpl catalog = new edu.umn.msi.tropix.labs.catalog.impl.LocalCatalogInstanceImpl();
    // catalog.setHost("http://appdev1.msi.umn.edu:8080/");
    // catalog.getCatalogOntAPI().addCategory("Laboratory Service", "A laboratory service", new CategoryFieldAssociation[0]);

    Multimap<String, Person> persons = directoryServicePersonSupplier.get();
    System.out.println("Persons");
    for(Person person : persons.values()) {
      System.out.println(person.getCagridIdentity());
    }
    System.out.println("/Persons");
    if(true)
      throw new RuntimeException();

    Supplier<Iterable<QueueGridService>> bowtieServiceSupplier = (Supplier<Iterable<QueueGridService>>) applicationContext
        .getBean("rawExtractGridServiceSupplier");
    for(QueueGridService service : bowtieServiceSupplier.get()) {
      System.out.println(service.getServiceAddress());
    }

    final MetadataResolver resolver = (MetadataResolver) applicationContext.getBean("metadataResolver");
    final QueueStatus queueStatus = resolver.getMetadata("local://Scaffold",
        QName.valueOf("{http://msi.umn.edu/tropix/common/jobqueue/queueStatus}QueueStatus"), QueueStatus.class);
    System.out.println("Queue Size: " + queueStatus.getSize());
    /*
     * final Map<String, String> services = (Map<String, String>) applicationContext.getBean("authenticationServices");
     * assert services.containsKey("local");
     * assert services.containsValue("local");
     */
    final ScaffoldMetadata scaffoldMetadata = resolver.getMetadata("local://Scaffold", ScaffoldMetadata.getTypeDesc().getXmlType(),
        ScaffoldMetadata.class);
    assert scaffoldMetadata.getScaffoldVersion() == ScaffoldVersion.V3;

    ServiceMetadata metadata = resolver.getMetadata("local://Scaffold", new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata",
        "ServiceMetadata"), ServiceMetadata.class);
    assert metadata.getServiceDescription().getService().getName().equals("Local Scaffold");

    metadata = resolver.getMetadata("local://XTandem", new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata"),
        ServiceMetadata.class);
    assert metadata.getServiceDescription().getService().getName().equals("Local X! Tandem");

    metadata = resolver.getMetadata("local://Bowtie", new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata"),
        ServiceMetadata.class);
    assert metadata.getServiceDescription().getService().getName().equals("Local Bowtie");

    metadata = resolver.getMetadata("local://Inspect", new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata"),
        ServiceMetadata.class);
    assert metadata.getServiceDescription().getService().getName().equals("Local InsPecT");

    metadata = resolver.getMetadata("local://TagRecon", new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata"),
        ServiceMetadata.class);
    assert metadata.getServiceDescription().getService().getName().equals("Local TagRecon");

    /*
     * final CatalogClient catalogClient = (CatalogClient) applicationContext.getBean("catalogClient");
     * catalogClient.entrySearch("moo");
     * assert catalogClient.getCatalogIds().iterator().hasNext();
     * for(final String catalogId : catalogClient.getCatalogIds()) {
     * System.out.println("CatalogId is " + catalogId);
     * catalogClient.getEntryAPI(catalogId);
     * catalogClient.getOntologAPI(catalogId).getProviders(null);
     * catalogClient.getSearchAPI(catalogId).entrySearch("moo");
     * }
     * 
     * final String mayoCatalogUrl = "https://tropix.mayo.edu:8443/wsrf/services/cagrid/TropixCatalogService";
     * final Field[] fields = catalogClient.getOntologAPI(mayoCatalogUrl).getFields(null);
     * final HashMap<String, String> fieldNames = new HashMap<String, String>();
     * for(final Field field : fields) {
     * fieldNames.put(field.getId(), field.getName());
     * }
     * 
     * final FullEntry[] fullEntries = catalogClient.getEntryAPI(mayoCatalogUrl).getFullDetails(null);
     * System.out.println("Full Entries");
     * for(final FullEntry fullEntry : fullEntries) {
     * System.out.println("Service " + fullEntry.getName() + "<" + fullEntry.getId() + ">");
     * final Attribute[] attributes = catalogClient.getEntryAPI(mayoCatalogUrl).getAttributes(fullEntry.getId());
     * for(final Attribute attribute : attributes) {
     * System.out.println("\tAttribute " + fieldNames.get(attribute.getFieldID()) + "<" + attribute.getId() + ">" + " of type " +
     * attribute.getType());
     * final AttributeType type = attribute.getType();
     * if(type.equals(AttributeType.FREETEXT)) {
     * System.out.println("\t\tFREETEXT {" + attribute.getValues()[0] + "}");
     * } else if(type.equals(AttributeType.SIMPLE)) {
     * System.out.println("\t\tSIMPLE " + Arrays.toString(attribute.getValues()));
     * } else if(attribute.getValues() != null) {
     * final FieldValue[] attributeFieldValues = catalogClient.getOntologAPI(mayoCatalogUrl).getFieldValues(attribute.getValues());
     * System.out.println("\t\tVALUE for Field " + fieldNames.get(attribute.getFieldID()) + "<" + attribute.getFieldID() + " >");
     * for(final FieldValue attributeFieldValue : attributeFieldValues) {
     * System.out.println("\t\t\tField value " + attributeFieldValue.getValue() + "<" + attributeFieldValue.getId() + ">");
     * }
     * }
     * }
     * }
     */
    /*
     * final DelegatedCredentialResolverGridImpl resolver = (DelegatedCredentialResolverGridImpl)
     * applicationContext.getBean("delegatedCredentialResolver");
     * 
     * final DelegatedCredentialFactory dcFactory = (DelegatedCredentialFactory) applicationContext.getBean("delegatedCredentialFactory");
     * 
     * final Supplier<Credential> proxySupplier = (Supplier<Credential>) applicationContext.getBean("testProxySupplier");
     * final Credential proxy = proxySupplier.get();
     * final DelegatedCredentialReference dcRef = dcFactory.createDelegatedCredential(proxy);
     * 
     * resolver.getDelgatedCredential(dcRef);
     */
    final Supplier<Iterable<IdentificationGridService>> iterableSupplier = (Supplier<Iterable<IdentificationGridService>>) applicationContext
        .getBean("identificationGridServiceSupplier");
    for(final IdentificationGridService service : iterableSupplier.get()) {
      System.out.println("gridService --- " + service);
    }

    final Supplier<Iterable<QueueGridService>> qIterableSupplier = (Supplier<Iterable<QueueGridService>>) applicationContext
        .getBean("scaffoldGridServiceSupplier");
    for(final QueueGridService service : qIterableSupplier.get()) {
      System.out.println("scaffold gridService --- " + service);
    }

    final Supplier<Iterable<QueueGridService>> itraqIterableSupplier = (Supplier<Iterable<QueueGridService>>) applicationContext
        .getBean("iTraqQuantitationGridServiceSupplier");
    for(final QueueGridService service : itraqIterableSupplier.get()) {
      System.out.println("gridService --- " + service);
    }

    for(final QueueGridService service : qIterableSupplier.get()) {
      System.out.println("gridService --- " + service);
    }

    final Supplier<Iterable<GridService>> searchIterableSupplier = (Supplier<Iterable<GridService>>) applicationContext
        .getBean("searchGridServiceSupplier");
    for(final GridService service : searchIterableSupplier.get()) {
      System.out.println("gridService --- " + service);
    }

    final Iterable<String> urlSupplier = (Iterable<String>) applicationContext.getBean("indexServiceUrlSupplier");
    for(final String url : urlSupplier) {
      System.out.println("Indexed URL : " + url);
    }

    final Iterable<GridUser> gridUserIterable = (Iterable<GridUser>) applicationContext.getBean("userIterable");
    for(final GridUser gridUser : gridUserIterable) {
      System.out.println(gridUser);
    }
    final Map<String, String> catalogMap = (Map<String, String>) applicationContext.getBean("catalogToRequestMap");
    for(final Entry<String, String> entry : catalogMap.entrySet()) {
      System.out.println("Catalog service found " + entry.getKey() + " maps to request service " + entry.getValue());
    }

    /*
     * final CatalogInstance catalogInstance = (CatalogInstance) applicationContext.getBean("localCatalogInstance");
     * catalogInstance.getCatalogOntAPI().getProviders(null);
     * catalogInstance.getCatalogSearchAPI().entrySearch("moo");
     * 
     * final String fieldId = catalogInstance.getCatalogOntAPI().addField("Number of Samples", null, FieldType.UNSTRUCTURED);
     * // catalogInstance.getCatalogOntAPI().addCategory("Laboratory Service", "A laboratory service", new CategoryFieldAssociation[0]);
     * // catalogInstance.getCatalogAdminAPI().rebuildTextIndex(0);
     * 
     * catalogInstance.getCatalogEntryAPI().addFreeTextAttribute("catalog:43", fieldId, "Hello World!");
     */
    final TropixSearchClient client = (TropixSearchClient) applicationContext.getBean("tropixSearchClient");

  }

}
