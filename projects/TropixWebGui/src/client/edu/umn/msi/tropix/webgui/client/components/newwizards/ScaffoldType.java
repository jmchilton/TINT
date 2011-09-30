package edu.umn.msi.tropix.webgui.client.components.newwizards;

@Deprecated
public enum ScaffoldType {
  MANY_ANALYSIS("One scaffold analysis per run"),
  MANY_SAMPLE("Runs as independent replicates"), // ("One scaffold sample per run"),
  ONE_SAMPLE("All runs as one scaffold sample");
  final String description;

  private ScaffoldType(final String description) {
    this.description = description;
  }
}