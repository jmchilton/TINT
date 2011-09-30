package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.UUID;

import edu.umn.msi.tropix.jobs.activities.descriptions.CommonMetadataProvider;

public class TestCommonMetadataProvider implements CommonMetadataProvider {
  private final String destinationId;

  public TestCommonMetadataProvider() {
    this(UUID.randomUUID().toString());
  }

  public TestCommonMetadataProvider(final String destinationId) {
    this.destinationId = destinationId;
  }

  public String getName() {
    return "test name";
  }

  public String getDescription() {
    return "test description";
  }

  public String getDestinationId() {
    return destinationId;
  }

  public void assertIsDestinationId(final String actualId) {
    assert destinationId.equals(actualId);
  }

}