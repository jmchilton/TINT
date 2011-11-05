package edu.umn.msi.tropix.ssh;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.Lists;

class Utils {
  private static final String DEFAULT_PREFIX = "/My Home/";

  public static String cleanAndExpandPath(final String inputPath) {
    String path = inputPath;
    final String prefix = FilenameUtils.getPrefix(path);
    if(prefix != null && !prefix.equals("/")) {
      path = DEFAULT_PREFIX + path.substring(prefix.length());
    }
    String result = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(path));
    if(result == null) { // For instance /.. or /moo/../.., just reset back to /
      result = "/";
    }
    if(!result.equals("/") && result.endsWith("/")) {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }

  public static List<String> pathPieces(final String path) {
    File file = new File(cleanAndExpandPath(path)).getAbsoluteFile();
    final List<String> pathPieces = Lists.newArrayList();
    while(file.getParentFile() != null) {
      pathPieces.add(file.getName());
      file = file.getParentFile();
    }
    Collections.reverse(pathPieces);
    return pathPieces;
  }

  public static String name(final String path) {
    return FilenameUtils.getName(cleanAndExpandPath(path));
  }

  public static String parent(final String path) {
    final List<String> pathPieces = pathPieces(path);
    if(pathPieces.isEmpty()) {
      return "/";
    } else {
      return cleanAndExpandPath(path + "/..");
    }
  }

  public static String join(final String parentPath, final String name) {
    return parentPath + (parentPath.endsWith("/") ? "" : "/") + name;
  }

}
