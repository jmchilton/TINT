package edu.umn.msi.tropix.common.io;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.annotation.WillNotClose;

public class FileContext implements FileCoercible, HasStreamOutputContext, HasStreamInputContext {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private File file;
  
  public FileContext(final File file) {
    this.file = file;
  }

  public File asFile() {
    return file;
  }
  
  public void put(@WillNotClose final InputStream inputStream) {
    final OutputStream outputStream = asOutputStream();
    try {
      IO_UTILS.copyLarge(inputStream, outputStream);
    } finally {
      IO_UTILS.closeQuietly(outputStream);
    }
  }

  public void put(final File file) {
    StreamOutputContextImpl.put(file, this);
  }

  public void put(final URL url) {
    StreamOutputContextImpl.put(url, this);
  }

  public void put(final byte[] bytes) {
    StreamOutputContextImpl.put(bytes, this);
  }

  public OutputStream asOutputStream() {
    final OutputStream outputStream = FILE_UTILS.getFileOutputStream(file);
    return outputStream;
  }

  public InputStream asInputStream() {
    return FILE_UTILS.getFileInputStream(file);
  }

  public void get(final OutputStream outputStream) {
    InputContexts.get(outputStream, this);
  }
  
  public void get(final OutputContext outputContext) {
    outputContext.put(asFile());
  }

  public void get(final File file) {
    StreamInputContextImpl.get(file, this);
  }

}
