package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

class MgfParseUtils {
  private static final Pattern CHARGE_PATTERN = Pattern.compile("[cC][hH][aA][rR][gG][eE]=.*");
  private static final Pattern COM_PATTERN = Pattern.compile("[cC][oO][mM]=.*");
  private static final Pattern SPOT_SET_PATTERN = Pattern.compile(".*Spot Set: \\w+\\\\(\\w+) .*");

  static Optional<String> parseDefaultParentName(final String line) {
    Optional<String> parentName = Optional.absent();
    if(COM_PATTERN.matcher(line).matches()) {
      final Matcher spotSetMatcher = SPOT_SET_PATTERN.matcher(line);
      if(spotSetMatcher.matches()) {
        parentName = Optional.of(spotSetMatcher.group(1));
      }
    }
    return parentName;
  }

  static List<Short> parseCharges(final String line) {
    List<Short> charges = null;
    if(CHARGE_PATTERN.matcher(line).matches()) {
      final String chargeStr = line.substring("CHARGE=".length());
      final Scanner scanner = new Scanner(chargeStr).useDelimiter("[^\\d]+");
      if(scanner.hasNextShort()) {
        charges = Lists.newArrayList();
        while(scanner.hasNextShort()) {
          charges.add(scanner.nextShort());
        }
      }
    }
    return charges;
  }

  static int parseScanStart(final String line) {
    return extractScansPart(line, 0);
  }

  static int parseScanEnd(final String line) {
    return extractScansPart(line, 1);
  }

  private static int extractScansPart(final String line, final int index) {
    final String scansPart = line.split("=")[1].trim();
    Preconditions.checkState(StringUtils.hasText(scansPart));
    if(scansPart.contains("-")) {
      return Integer.parseInt(scansPart.split("-")[index].trim());
    } else {
      return Integer.parseInt(scansPart);
    }
  }

}
