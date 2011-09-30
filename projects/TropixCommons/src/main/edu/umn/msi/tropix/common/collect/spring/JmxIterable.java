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

package edu.umn.msi.tropix.common.collect.spring;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.util.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Collections;

/**
 * Acts as an {@link Iterable} that can be used for configuration 
 * purpose. The contents of this class can be changes at runtime via
 * JMX. 
 * 
 * To use this class a parseFunction and separator pattern should be 
 * specified. If the separator pattern is "\\w+" and the parse functions
 * body is {@code return Double.parseString(x);} then when the software
 * deployer specifies contents of "12.3  5.6 18" from Spring or JMX, the
 * contents this Iterable will operate over will then be 12.3, 5.6, and 18.0.
 * 
 * Individual elements can be added and removed with {@code addElement} and 
 * {@code removeElement} respectively.
 *  
 * @author John Chilton
 *
 */
@ManagedResource
public class JmxIterable<T> implements Iterable<T> {
  private static final String DEFAULT_SEPARATOR_PATTERN = "\\s*,\\s*";
  private final Collection<T> contents = new LinkedList<T>();
  private Function<String, T> parseFunction;

  /**
   * @param parseFunction This function describes how to transform simple
   * strings into objects that this {@Iterable} will iterate over.
   */
  public void setParseFunction(final Function<String, T> parseFunction) {
    this.parseFunction = parseFunction;
  }

  /**
   * @param separatorPattern This regular expression breaks up a human
   * supplied string into individual {@link String}s that will be passed
   * to {@code parseFunction} for translation. 
   */
  public void setSeparatorPattern(final String separatorPattern) {
    this.separatorPattern = separatorPattern;
  }

  private String separatorPattern = DEFAULT_SEPARATOR_PATTERN;

  public Iterator<T> iterator() {
    final List<T> copiedContents = new LinkedList<T>();
    synchronized(this.contents) {
      copiedContents.addAll(this.contents);
    }
    return copiedContents.iterator();
  }

  public void setContents(final Collection<T> initialContents) {
    synchronized(this.contents) {
      this.contents.clear();
      Iterables.addAll(this.contents, initialContents);
    }
  }

  @ManagedAttribute
  public void setContents(final String contentsStr) {
    Preconditions.checkNotNull(contentsStr);
    if(StringUtils.hasLength(contentsStr)) {
      this.setContents(Collections.transform(Arrays.asList(contentsStr.split(this.separatorPattern)), this.parseFunction));
    } else {
      this.setContents(Lists.<T>newLinkedList());
    }
  }

  @ManagedAttribute
  public String getContents() {
    synchronized(this.contents) {
      return Joiner.on(", ").join(contents);
    }
  }

  @ManagedOperation
  public boolean addElement(final String elStr) {
    final T el = this.parseFunction.apply(elStr);
    synchronized(this.contents) {
      return this.contents.add(el);
    }
  }

  @ManagedOperation
  public boolean removeElement(final String elStr) {
    final T el = this.parseFunction.apply(elStr);
    synchronized(this.contents) {
      return this.contents.remove(el);
    }
  }

}
