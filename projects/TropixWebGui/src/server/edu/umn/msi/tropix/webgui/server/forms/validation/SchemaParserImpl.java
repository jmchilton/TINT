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

package edu.umn.msi.tropix.webgui.server.forms.validation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.webgui.client.forms.validation.predicate.TypePredicates;

// TODO: Would be nice to use AbstractFactory to remove dependency on JDOM so this can be executed on the client side
// if need be.
@ManagedBean
class SchemaParserImpl implements SchemaParser {
  private static final Object XSD_SCHEMA = "http://www.w3.org/2001/XMLSchema";

  public Map<String, Predicate<String>> parse(final InputStream inputStream) throws Exception {
    final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    builderFactory.setNamespaceAware(true);
    final DocumentBuilder builder = builderFactory.newDocumentBuilder();
    final Document document = builder.parse(inputStream);
    final Map<String, Predicate<String>> predicateMap = new HashMap<String, Predicate<String>>();
    this.populatePredicates(predicateMap, document.getDocumentElement());
    return predicateMap;
  }

  @SuppressWarnings("unchecked")
  protected Map<String, Predicate<String>> getInitialTypePredicateMap() {
    final Map<String, Predicate<String>> predicateMap = new HashMap<String, Predicate<String>>();
    // Rough approximations of baseTypes, there is some question here as to
    // efficiency of checking versus
    // accurately describing the types...
    final ArrayList<String> booleanDomain = new ArrayList<String>(4);
    booleanDomain.addAll(Arrays.asList("true", "false", "0", "1"));
    predicateMap.put("boolean", TypePredicates.isIn(booleanDomain));
    predicateMap.put("string", TypePredicates.TRUE);
    predicateMap.put("integer", TypePredicates.isLong());
    predicateMap.put("long", TypePredicates.isLong());
    predicateMap.put("int", TypePredicates.isInt());
    predicateMap.put("double", TypePredicates.isDouble());
    predicateMap.put("float", TypePredicates.and(Arrays.asList(TypePredicates.isDouble(), TypePredicates.isAbsLessThanOrEqual(Float.MAX_VALUE))));
    predicateMap.put("nonNegativeInteger", TypePredicates.and(Arrays.asList(TypePredicates.isLong(), TypePredicates.isGreaterThanOrEqual(0.0))));
    predicateMap.put("nonPositiveInteger", TypePredicates.and(Arrays.asList(TypePredicates.isLong(), TypePredicates.isLessThanOrEqual(0.0))));
    predicateMap.put("negativeInteger", TypePredicates.and(Arrays.asList(TypePredicates.isLong(), TypePredicates.isLessThan(0.0))));
    predicateMap.put("positiveInteger", TypePredicates.and(Arrays.asList(TypePredicates.isLong(), TypePredicates.isGreaterThan(0.0))));
    predicateMap.put("short", TypePredicates.and(Arrays.asList(TypePredicates.isDouble(), TypePredicates.isAbsLessThanOrEqual(32767))));
    predicateMap.put("byte", TypePredicates.and(Arrays.asList(TypePredicates.isDouble(), TypePredicates.isAbsLessThanOrEqual(128))));
    predicateMap.put("unsignedShort", TypePredicates.and(Arrays.asList(predicateMap.get("nonNegativeInteger"), TypePredicates.isLessThanOrEqual(65535))));
    predicateMap.put("unsignedByte", TypePredicates.and(Arrays.asList(predicateMap.get("nonNegativeInteger"), TypePredicates.isLessThanOrEqual(255))));
    predicateMap.put("unsignedLong", predicateMap.get("positiveInteger"));
    predicateMap.put("unsignedInt", TypePredicates.and(Arrays.asList(predicateMap.get("int"), TypePredicates.isGreaterThanOrEqual(0.0))));
    return predicateMap;
  }

  protected static List<Element> getNonAnnotationChildElements(final Node node) {
    final List<Element> childNodes = new LinkedList<Element>();
    final NodeList childNodeList = node.getChildNodes();
    for(int i = 0; i < childNodeList.getLength(); i++) {
      final Node childNode = childNodeList.item(i);
      if(childNode.getNodeType() == Node.ELEMENT_NODE && !(node.getNamespaceURI().equals(SchemaParserImpl.XSD_SCHEMA) && node.getLocalName().equals("annotation"))) {
        childNodes.add((Element) childNode);
      }
    }
    return childNodes;
  }

  protected boolean isElementWithName(final Node node, final String name) {
    return node.getNodeType() == Node.ELEMENT_NODE && node.getNamespaceURI().equals(SchemaParserImpl.XSD_SCHEMA) && node.getLocalName().equals(name);
  }

  protected boolean isElementNode(final Node node) {
    return this.isElementWithName(node, "element");
  }

  protected boolean isSimpleTypeNode(final Node node) {
    return this.isElementWithName(node, "simpleType");
  }

  protected Node getSimpleTypeChild(final Node node) {
    for(final Node childNode : SchemaParserImpl.getNonAnnotationChildElements(node)) {
      if(this.isElementWithName(childNode, "restriction")) {
        return childNode;
      } else if(this.isElementWithName(childNode, "union")) {
        return childNode;
      } else if(this.isElementWithName(childNode, "list")) {
        return childNode;
      }
    }
    throw new IllegalStateException("SimpleType must contain exactly one of restriction, union, or list");
  }

  protected Element getSimpleTypeNode(final Node node) {
    for(final Element childNode : SchemaParserImpl.getNonAnnotationChildElements(node)) {
      if(this.isSimpleTypeNode(childNode)) {
        return childNode;
      }
    }
    throw new IllegalStateException("Expecting simpleType node but failed to find one");
  }

  protected Predicate<String> buildPredicate(final Node node, final Map<String, Predicate<String>> typePredicateMap) {
    final Node childNode = this.getSimpleTypeChild(node);
    if(this.isElementWithName(childNode, "restriction")) {
      return this.buildRestrictionPredicate(childNode, typePredicateMap);
    } else if(this.isElementWithName(childNode, "union")) {
      return this.buildUnionPredicate(childNode, typePredicateMap);
    } else if(this.isElementWithName(childNode, "list")) {
      return this.buildListPredicate(childNode, typePredicateMap);
    } else {
      throw new IllegalStateException("Unknown child element under simpleType element");
    }
  }

  private Predicate<String> buildListPredicate(final Node node, final Map<String, Predicate<String>> typePredicateMap) {
    final NamedNodeMap attributes = node.getAttributes();
    final Node listTypeAttribute = attributes.getNamedItem("itemType");
    Predicate<String> validationPredicate;
    if(listTypeAttribute != null) {
      validationPredicate = typePredicateMap.get(listTypeAttribute.getNodeValue());
      if(validationPredicate == null) {
        throw new IllegalStateException("Encountered unknown type " + listTypeAttribute.getNodeValue() + " in listType attribute of list element");
      }
    } else {
      validationPredicate = this.buildPredicate(this.getSimpleTypeNode(node), typePredicateMap);
    }
    return TypePredicates.listOf(validationPredicate);
  }

  private Predicate<String> buildUnionPredicate(final Node node, final Map<String, Predicate<String>> typePredicateMap) {
    final LinkedList<Predicate<String>> predicates = new LinkedList<Predicate<String>>();
    final NamedNodeMap attributes = node.getAttributes();
    final Node memberTypesAttribute = attributes.getNamedItem("memberTypes");
    if(memberTypesAttribute != null) {
      final String[] memberTypes = SchemaParserImpl.whitespaceSplit(memberTypesAttribute.getNodeValue());
      for(final String memberType : memberTypes) {
        final Predicate<String> predicate = typePredicateMap.get(memberType);
        if(predicate == null) {
          throw new IllegalStateException("Unknown memberType " + memberType + " found on union element");
        }
        predicates.add(typePredicateMap.get(memberType));
      }
    }
    for(final Element childNode : SchemaParserImpl.getNonAnnotationChildElements(node)) {
      if(this.isSimpleTypeNode(childNode)) {
        predicates.add(this.buildPredicate(childNode, typePredicateMap));
      }
    }
    return TypePredicates.or(predicates);
  }

  protected static String[] whitespaceSplit(final String str) {
    final String[] parts = str.split("\\s+");
    return parts;
  }

  private Predicate<String> buildRestrictionPredicate(final Node node, final Map<String, Predicate<String>> typePredicateMap) {
    final LinkedList<Predicate<String>> predicates = new LinkedList<Predicate<String>>();
    final NamedNodeMap attributes = node.getAttributes();
    final Node baseAttribute = attributes.getNamedItem("base");
    boolean hasBase = false;
    if(baseAttribute != null) {
      hasBase = true;
      final String baseTypeName = this.getTypeName(baseAttribute.getNodeValue());
      if(!typePredicateMap.containsKey(baseTypeName)) {
        throw new IllegalStateException("Unknown restriction base type encountered - " + baseTypeName);
      }
      predicates.add(typePredicateMap.get(baseTypeName));

    }
    final LinkedList<String> enumeratedValues = new LinkedList<String>();
    for(final Element childNode : SchemaParserImpl.getNonAnnotationChildElements(node)) {

      // Handle potential nodes that wouldn't require a value attribute
      if(this.isSimpleTypeNode(childNode)) {
        hasBase = true;
        predicates.add(this.buildPredicate(childNode, typePredicateMap));
        continue;
      }

      // Handle nodes that do require a value attribute
      final Node valueNode = childNode.getAttributes().getNamedItem("value");
      if(valueNode == null) {
        throw new IllegalStateException("Encountered an element without a value attribute when one is required");
      }
      final String value = valueNode.getNodeValue();

      if(this.isElementWithName(childNode, "enumeration")) {
        enumeratedValues.add(value);
        continue;
      } else if(this.isElementWithName(childNode, "minInclusive")) {
        predicates.add(TypePredicates.isGreaterThanOrEqual(Double.parseDouble(value)));
        continue;
      } else if(this.isElementWithName(childNode, "maxInclusive")) {
        predicates.add(TypePredicates.isLessThanOrEqual(Double.parseDouble(value)));
        continue;
      } else if(this.isElementWithName(childNode, "minExclusive")) {
        predicates.add(TypePredicates.isGreaterThan(Double.parseDouble(value)));
        continue;
      } else if(this.isElementWithName(childNode, "maxExclusive")) {
        predicates.add(TypePredicates.isLessThan(Double.parseDouble(value)));
        continue;
      } else if(this.isElementWithName(childNode, "pattern")) {
        predicates.add(TypePredicates.matches(value));
        continue;
      } else if(this.isElementWithName(childNode, "minLength")) {
        predicates.add(TypePredicates.minLength(Integer.parseInt(value)));
      } else if(this.isElementWithName(childNode, "maxLength")) {
        predicates.add(TypePredicates.maxLength(Integer.parseInt(value)));
      }
    }

    if(!hasBase) {
      throw new IllegalStateException("restriction element found without a base and a simpleType");
    }

    if(enumeratedValues.size() > 0) {
      predicates.add(TypePredicates.isIn(enumeratedValues));
    }
    // TODO : Add remaining restrictions to predicates
    return TypePredicates.and(predicates);
  }

  private String getTypeName(final String qualifiedTypeName) {
    return qualifiedTypeName.substring(qualifiedTypeName.indexOf(':') + 1);
  }

  protected void populateTypePredicateMap(final Map<String, Predicate<String>> typePredicateMap, final Element rootElement) {
    final Map<String, Element> nodes = new HashMap<String, Element>();
    for(final Element node : SchemaParserImpl.getNonAnnotationChildElements(rootElement)) {
      if(!this.isSimpleTypeNode(node)) {
        continue;
      }
      final NamedNodeMap attributes = node.getAttributes();
      final Node nameNode = attributes.getNamedItem("name");
      if(nameNode == null) {
        throw new IllegalStateException("Top-level simpleType element without name encountered");
      }
      final String name = nameNode.getNodeValue();
      nodes.put(name, node);
    }

    // Create DAG of type dependencies
    final Multimap<String, String> dependencyGraph = HashMultimap.create();
    for(final String typeName : nodes.keySet()) {
      dependencyGraph.putAll(typeName, this.getSimpleTypeDependencies(nodes.get(typeName)));
    }

    final Collection<String> sortedTypeNames = this.topologicalSort(dependencyGraph);
    for(final String typeName : sortedTypeNames) {
      final Element node = nodes.get(typeName);
      final Predicate<String> predicate = this.buildPredicate(node, typePredicateMap);
      typePredicateMap.put(typeName, predicate);
    }
  }

  // Topological sort inspired by DFS base algorithm in Introduction to
  // Algorithms (Cormen et. al.)
  private Collection<String> topologicalSort(final Multimap<String, String> dag) {
    // Perform topological sort
    final Map<String, Color> nodeColor = new HashMap<String, Color>(dag.keySet().size());
    for(final String node : dag.keySet()) {
      nodeColor.put(node, Color.WHITE);
    }
    final List<String> sortedNodes = new ArrayList<String>(dag.keySet().size());

    for(final String node : dag.keySet()) {
      if(nodeColor.get(node).equals(Color.WHITE)) {
        this.topologicalSortVisit(node, dag, sortedNodes, nodeColor);
      }
    }

    return sortedNodes;
  }

  private void topologicalSortVisit(final String node, final Multimap<String, String> dag, final List<String> sortedNodes, final Map<String, Color> nodeColors) {
    nodeColors.put(node, Color.GRAY);
    for(final String adjacentNode : dag.get(node)) {
      if(dag.containsKey(adjacentNode) && nodeColors.get(adjacentNode).equals(Color.WHITE)) {
        this.topologicalSortVisit(adjacentNode, dag, sortedNodes, nodeColors);
      }
    }
    nodeColors.put(node, Color.BLACK);
    sortedNodes.add(node);
  }

  private enum Color {
    WHITE, GRAY, BLACK;
  }

  protected Collection<String> getSimpleTypeDependencies(final Element node) {
    final LinkedList<String> dependencies = new LinkedList<String>();
    for(final Element childNode : SchemaParserImpl.getNonAnnotationChildElements(node)) {
      Collection<String> childDependencies;
      if(this.isElementWithName(childNode, "restriction")) {
        childDependencies = this.getRestrictionDependencies(childNode);
      } else if(this.isElementWithName(childNode, "union")) {
        childDependencies = this.getUnionDependencies(childNode);
      } else if(this.isElementWithName(childNode, "list")) {
        childDependencies = this.getListDependencies(childNode);
      } else {
        childDependencies = Arrays.asList(new String[] {});
      }
      dependencies.addAll(childDependencies);
    }
    return dependencies;
  }

  private Collection<String> getRestrictionDependencies(final Element node) {
    final LinkedList<String> dependencies = new LinkedList<String>();

    final NamedNodeMap attributes = node.getAttributes();
    final Node baseAttribute = attributes.getNamedItem("base");
    if(baseAttribute != null) {
      dependencies.add(baseAttribute.toString());
    }
    for(final Element childNode : SchemaParserImpl.getNonAnnotationChildElements(node)) {
      if(this.isSimpleTypeNode(childNode)) {
        dependencies.addAll(this.getSimpleTypeDependencies(childNode));
      }
    }
    return dependencies;
  }

  private Collection<String> getUnionDependencies(final Element node) {
    final LinkedList<String> dependencies = new LinkedList<String>();

    final NamedNodeMap attributes = node.getAttributes();
    final Node memberTypesAttribute = attributes.getNamedItem("memberTypes");
    if(memberTypesAttribute != null) {
      final String[] memberTypes = SchemaParserImpl.whitespaceSplit(memberTypesAttribute.getNodeValue());
      dependencies.addAll(Arrays.asList(memberTypes));
    }
    for(final Element childNode : SchemaParserImpl.getNonAnnotationChildElements(node)) {
      if(this.isSimpleTypeNode(childNode)) {
        dependencies.addAll(this.getSimpleTypeDependencies(childNode));
      }
    }
    return dependencies;
  }

  private Collection<String> getListDependencies(final Element node) {
    final NamedNodeMap attributes = node.getAttributes();
    final Node listTypeAttribute = attributes.getNamedItem("itemType");
    if(listTypeAttribute != null) {
      return Arrays.asList(new String[] {listTypeAttribute.getNodeValue()});
    } else {
      return this.getSimpleTypeDependencies(this.getSimpleTypeNode(node));
    }
  }

  protected void populatePredicates(final Map<String, Predicate<String>> predicateMap, final Element rootElement) {
    final Map<String, Predicate<String>> typePredicateMap = this.getInitialTypePredicateMap();
    this.populateTypePredicateMap(typePredicateMap, rootElement);

    for(final Element node : SchemaParserImpl.getNonAnnotationChildElements(rootElement)) {
      final NamedNodeMap attributes = node.getAttributes();
      final Node nameNode = attributes.getNamedItem("name");
      if(nameNode == null) {
        throw new IllegalStateException("element without name encountered");
      }
      final String name = nameNode.getNodeValue();

      if(this.isElementNode(node)) {
        final Node type = attributes.getNamedItem("type");
        if(type == null) {
          final Node simpleTypeNode = this.getSimpleTypeNode(node);
          final Predicate<String> predicate = this.buildPredicate(simpleTypeNode, typePredicateMap);
          predicateMap.put(name, predicate);
        } else {
          final String typeName = this.getTypeName(type.getNodeValue());
          predicateMap.put(name, typePredicateMap.get(typeName));
        }
      }
    }
  }
}
