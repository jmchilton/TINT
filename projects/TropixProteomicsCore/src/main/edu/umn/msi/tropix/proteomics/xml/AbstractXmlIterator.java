package edu.umn.msi.tropix.proteomics.xml;

import java.io.InputStream;

import javax.xml.stream.XMLStreamReader;

import com.google.common.collect.UnmodifiableIterator;

import edu.umn.msi.tropix.proteomics.conversion.impl.XMLStreamReaderUtils;

public abstract class AbstractXmlIterator<T> extends UnmodifiableIterator<T> {
  private final XMLStreamReader reader;
  private boolean foundStartOfNext = false;
  protected String delimeterTag;

  protected AbstractXmlIterator(final InputStream inputStream, final String delimeterTag) {
    this(XMLStreamReaderUtils.get(inputStream), delimeterTag);
  }

  protected AbstractXmlIterator(final XMLStreamReader reader, final String delimeterTag) {
    this.reader = reader;
    this.delimeterTag = delimeterTag;
  }

  protected XMLStreamReader reader() {
    return reader;
  }

  protected boolean findStartOfNext() {
    while(!foundStartOfNext && XMLStreamReaderUtils.hasNext(reader)) {
      handleNext();
      advance();
      if(isStartOf(delimeterTag)) {
        foundStartOfNext = true;
        break;
      }
    }
    return foundStartOfNext;
  }

  protected void advance() {
    XMLStreamReaderUtils.next(reader);
  }

  protected void handleNext() {
  }

  protected boolean isStartOf(final String tag) {
    return XMLStreamReaderUtils.isStartOfElement(reader, tag);
  }

  protected boolean isEndOf(final String tag) {
    return XMLStreamReaderUtils.isEndOfElement(reader, tag);
  }

  protected void resetAtNextStart() {
    if(!findStartOfNext()) {
      throw new IllegalStateException("Failed to find another instance of tag - " + delimeterTag);
    }
    foundStartOfNext = false;
  }

  protected abstract T parseNext();

  public boolean hasNext() {
    return findStartOfNext();
  }

  public T next() {
    resetAtNextStart();
    return parseNext();
  }

  protected String getAttributeValue(final String attributeName) {
    return XMLStreamReaderUtils.getAttributeValue(reader(), attributeName);
  }

}