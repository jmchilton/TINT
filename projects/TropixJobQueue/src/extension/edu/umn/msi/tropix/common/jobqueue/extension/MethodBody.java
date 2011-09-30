/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.common.jobqueue.extension;

import gov.nih.nci.cagrid.introduce.codegen.services.methods.SyncHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copied from the Interfaces Extension.
 * 
 * @author John Chilton
 * 
 */
public class MethodBody {
  private final String signature;
  private final StringBuffer source;

  protected class MethodBoundary {
    private int start; // First character after newline following open bracket, or first nonwhitespace after open bracket
    private int end; // First character on line containing close bracket or first nonwhitespace on line of close bracket

    public int getStart() {
      return start;
    }

    public int getEnd() {
      return end;
    }

    public MethodBoundary() {
      // int methodStart = SyncHelper.startOfSignature(source, signature);
      final int methodStart = source.indexOf(signature);
      final int firstBracket = source.indexOf("{", methodStart);
      start = firstBracket + 1;
      while(true) {
        final char curChar = source.charAt(start);
        if(!Character.isWhitespace(curChar)) {
          break;
        } else if(curChar == '\n' || curChar == '\r') {
          start++;
          break;
        }
        start++;
      }
      end = SyncHelper.bracketMatch(source, firstBracket);
      while(true) {
        final char curChar = source.charAt(end);
        if(curChar == '}') {
          break;
        }
        end--;
      }
      while(true) {
        end--;
        final char curChar = source.charAt(end);
        if(curChar == '\n' || curChar == '\r' || !Character.isWhitespace(curChar)) {
          end++;
          break;
        }
      }
    }
  }

  public MethodBody(final StringBuffer source, final String signature) {
    this.signature = signature;
    this.source = source;
  }

  public void setContents(final String body) {
    final MethodBoundary boundary = new MethodBoundary();
    source.delete(boundary.getStart(), boundary.getEnd());
    source.insert(boundary.getStart(), body);
  }

  public String guessIndentation() {
    final MethodBoundary boundary = new MethodBoundary();
    final int endIndex = boundary.getEnd();
    final int bracketIndex = source.indexOf("}", endIndex);
    final CharSequence endToBracket = source.substring(endIndex, bracketIndex);
    return endToBracket.toString();
  }

  public String indent(final String inputCode) {
    String code = inputCode;
    final String indentation = guessIndentation();
    final char lastChar = code.charAt(code.length() - 1);
    code = code.replaceAll("\n", "\n" + indentation + indentation);
    code = code.replaceAll("\r", "\r" + indentation + indentation);
    if(lastChar == '\n' || lastChar == '\r') {
      code = code.substring(0, code.length() - indentation.length() * 2);
    }
    return indentation + indentation + code;
  }

  public void append(final String code) {
    append(code, false);
  }

  public void prepend(final String code) {
    prepend(code, false);
  }

  public void append(final String inputCode, final boolean indent) {
    final MethodBoundary boundary = new MethodBoundary();
    final String code = indent ? indent(inputCode) : inputCode;
    source.insert(boundary.getEnd(), code);
  }

  public void prepend(final String inputCode, final boolean indent) {
    final MethodBoundary boundary = new MethodBoundary();
    final String code = indent ? indent(inputCode) : inputCode;
    source.insert(boundary.getStart(), code);
  }

  public CharSequence getContents() {
    final MethodBoundary boundary = new MethodBoundary();
    return source.subSequence(boundary.getStart(), boundary.getEnd());
  }

  public String toString() {
    return getContents().toString();
  }

  /* Pattern matching against the method body. */
  public boolean contains(final String regex) {
    return contains(Pattern.compile(regex));
  }

  public boolean contains(final Pattern pattern) {
    final Matcher matcher = getMatcher(pattern);
    return matcher.find();
  }

  public Matcher getMatcher(final Pattern pattern) {
    final Matcher matcher = pattern.matcher(getContents());
    return matcher;
  }

  public Matcher getMatcher(final String regex) {
    return getMatcher(Pattern.compile(regex));
  }
}
