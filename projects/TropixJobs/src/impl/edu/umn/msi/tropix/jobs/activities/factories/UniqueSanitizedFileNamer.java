package edu.umn.msi.tropix.jobs.activities.factories;

import java.util.Set;

import com.google.common.collect.Sets;

public class UniqueSanitizedFileNamer {
  private final Set<String> names = Sets.newHashSet();

  public String nameFor(final String startingPoint) {
    String nextGuess = startingPoint;
    int i = 1;
    while(names.contains(nextGuess)) {
      nextGuess = startingPoint + "_" + i++;
    }
    names.add(nextGuess);
    return nextGuess;
  }

}
