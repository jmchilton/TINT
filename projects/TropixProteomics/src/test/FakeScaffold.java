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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FakeScaffold {
  public static void main(final String[] args) throws Exception {
    final String path = args[args.length - 1];
    final String contents = new String(readFile(new File(path)));
    int location = 0;
    int nextLocation;
    while((nextLocation = contents.indexOf("<Export", location)) != -1) {
      int start = contents.indexOf("path=\"", nextLocation);
      start = start + "path=\"".length();
      final int end = contents.indexOf("\"", start + 1);
      final String epath = contents.substring(start, end);
      writeFile(epath, "Hello World!".getBytes());
      location = end;
    }
  }

  public static byte[] readFile(final File file) throws IOException {
    FileInputStream fileStream = null;
    fileStream = new FileInputStream(file);
    final int numBytes = fileStream.available();
    final byte[] contents = new byte[numBytes];
    fileStream.read(contents);
    return contents;
  }

  public static void writeFile(final String filePath, final byte[] contents) throws Exception {
    final File file = new File(filePath);
    final File parentDir = file.getAbsoluteFile().getParentFile();
    parentDir.mkdirs();
    FileOutputStream fileStream = null;
    fileStream = new FileOutputStream(file);
    fileStream.write(contents);
  }
}
