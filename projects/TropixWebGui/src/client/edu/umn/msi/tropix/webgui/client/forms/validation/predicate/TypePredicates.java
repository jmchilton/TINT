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

package edu.umn.msi.tropix.webgui.client.forms.validation.predicate;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;

public class TypePredicates {
  private static final Predicate<String> IS_INT_PREDICATE = new IsIntPredicate();
  private static final Predicate<String> IS_LONG_PREDICATE = new IsLongPredicate();
  private static final Predicate<String> IS_DOUBLE_PREDICATE = new IsDoublePredicate();
  public static final Predicate<String> TRUE = new TruePredicate();
  public static final Predicate<String> FALSE = new FalsePredicate();

  public static Predicate<String> isInt() {
    return TypePredicates.IS_INT_PREDICATE;
  }

  public static Predicate<String> isDouble() {
    return TypePredicates.IS_DOUBLE_PREDICATE;
  }

  public static Predicate<String> isLong() {
    return TypePredicates.IS_LONG_PREDICATE;
  }

  public static Predicate<String> minLength(final int length) {
    final MinLengthPredicate predicate = new MinLengthPredicate();
    predicate.setLength(length);
    return predicate;
  }

  public static Predicate<String> maxLength(final int length) {
    final MaxLengthPredicate predicate = new MaxLengthPredicate();
    predicate.setLength(length);
    return predicate;
  }

  public static Predicate<String> matches(final String pattern) {
    final MatchesPredicate predicate = new MatchesPredicate();
    predicate.setPattern(pattern);
    return predicate;
  }

  public static Predicate<String> isGreaterThan(final double lowerBound) {
    final IsGreaterThanPredicate predicate = new IsGreaterThanPredicate();
    predicate.setValue(lowerBound);
    return predicate;
  }

  public static Predicate<String> isGreaterThanOrEqual(final double lowerBound) {
    final IsGreaterThanOrEqualPredicate predicate = new IsGreaterThanOrEqualPredicate();
    predicate.setValue(lowerBound);
    return predicate;
  }

  public static Predicate<String> isAbsLessThanOrEqual(final double upperBound) {
    final IsAbsLessThanOrEqualPredicate predicate = new IsAbsLessThanOrEqualPredicate();
    predicate.setValue(upperBound);
    return predicate;
  }

  public static Predicate<String> isAbsLessThan(final double upperBound) {
    final IsAbsLessThanPredicate predicate = new IsAbsLessThanPredicate();
    predicate.setValue(upperBound);
    return predicate;
  }

  public static Predicate<String> isLessThan(final double upperBound) {
    final IsLessThanPredicate predicate = new IsLessThanPredicate();
    predicate.setValue(upperBound);
    return predicate;
  }

  public static Predicate<String> isLessThanOrEqual(final double upperBound) {
    final IsLessThanOrEqualPredicate predicate = new IsLessThanOrEqualPredicate();
    predicate.setValue(upperBound);
    return predicate;
  }

  public static Predicate<String> isIn(final Collection<String> enumeratedValues) {
    final IsInPredicate predicate = new IsInPredicate();
    predicate.setEnumeratedValues(enumeratedValues);
    return predicate;
  }

  public static Predicate<String> listOf(final Predicate<String> elementPredicate) {
    final ListOfPredicate predicate = new ListOfPredicate();
    predicate.setElementPredicate(elementPredicate);
    return predicate;
  }

  public static Predicate<String> or(final List<Predicate<String>> validationPredicateList) {
    final int size = validationPredicateList.size();
    if(size == 0) {
      return TypePredicates.FALSE;
    } else if(size == 1) {
      return validationPredicateList.get(0);
    } else {
      final OrPredicate predicate = new OrPredicate();
      predicate.setFirstPredicate(validationPredicateList.get(0));
      OrPredicate tempPredicate = predicate;
      for(int i = 1; i < size - 1; i++) {
        final Predicate<String> ithPredicate = validationPredicateList.get(i);
        final OrPredicate newPredicate = new OrPredicate();
        newPredicate.setFirstPredicate(ithPredicate);
        tempPredicate.setSecondPredicate(newPredicate);
        tempPredicate = newPredicate;
      }
      tempPredicate.setSecondPredicate(validationPredicateList.get(size - 1));

      return predicate;
    }
  }

  public static Predicate<String> and(final List<Predicate<String>> validationPredicateList) {
    final int size = validationPredicateList.size();
    if(size == 0) {
      return TypePredicates.TRUE;
    } else if(size == 1) {
      return validationPredicateList.get(0);
    } else {
      final AndPredicate predicate = new AndPredicate();
      predicate.setFirstPredicate(validationPredicateList.get(0));
      AndPredicate tempPredicate = predicate;
      for(int i = 1; i < size - 1; i++) {
        final Predicate<String> ithPredicate = validationPredicateList.get(i);
        final AndPredicate newPredicate = new AndPredicate();
        newPredicate.setFirstPredicate(ithPredicate);
        tempPredicate.setSecondPredicate(newPredicate);
        tempPredicate = newPredicate;
      }
      tempPredicate.setSecondPredicate(validationPredicateList.get(size - 1));
      return predicate;
    }
  }

}
