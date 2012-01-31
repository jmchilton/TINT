package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.UUID;

import edu.umn.msi.tropix.webgui.client.components.UploadComponentFactory.FileSource;

public class TestFileSources {

  public static FileSource testUploadWithName(final String name) {
    return new FileSource(UUID.randomUUID().toString(), name, true);
  }

}
