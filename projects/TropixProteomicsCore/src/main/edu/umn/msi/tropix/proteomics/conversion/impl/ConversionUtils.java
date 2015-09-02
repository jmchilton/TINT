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

package edu.umn.msi.tropix.proteomics.conversion.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.annotation.Nonnull;
import javax.annotation.WillClose;

import net.sourceforge.sashimi.mzxml.v3_0.MsRun;
import net.sourceforge.sashimi.mzxml.v3_0.Scan;
import net.sourceforge.sashimi.mzxml.v3_0.Scan.Peaks;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.io.FloatingPointDataInputStream;
import edu.umn.msi.tropix.common.io.FloatingPointDataOutputStream;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.utils.HexUtils;

public class ConversionUtils {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final double SIGNAL_CUTOFF = 0.95d;
  public static final float PROTON_MASS = 1.007276466f;

  public static String getSanitizedName(@Nonnull final String filename, @Nonnull final String extension) {
    Preconditions.checkNotNull(filename, "getSanitizedName sent a null filename");
    Preconditions.checkNotNull(filename, "getSanitizedName sent a null extension");
    final String baseName = FilenameUtils.getBaseName(filename);
    final String cleanedBaseName = baseName.replaceAll("[^\\w]", "_");
    return cleanedBaseName + extension;
  }

  private static MessageDigest getMessageDigestInstance(final String algorithm) {
    try {
      final MessageDigest md = MessageDigest.getInstance(algorithm);
      return md;
    } catch(final NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public static boolean isEmptyPeaks(final double[] peaks) {
    return peaks == null || peaks.length < 2 || (peaks.length == 2 && peaks[0] == 0.0 && peaks[1] == 0.0);
  }

  public static byte[] getDigest(final byte[] message, final String algorithm) {
    return getDigest(new ByteArrayInputStream(message), algorithm);
  }

  public static byte[] getDigest(@WillClose final InputStream inputStream, final String algorithm) {
    try {
      final MessageDigest md = getMessageDigestInstance(algorithm);
      int length = 0;
      final byte[] bytes = new byte[1024];
      while((length = IO_UTILS.read(inputStream, bytes)) != -1) {
        md.update(bytes, 0, length);
      }
      return md.digest();
    } finally {
      IO_UTILS.closeQuietly(inputStream);
    }
  }

  public static short[] getPrecusorCharges(final short recordedChargeState, final double precursorMzValue, final double[] peaks) {
    short[] precursorCharges;
    if(recordedChargeState != 0) {
      precursorCharges = new short[] {recordedChargeState};
    } else if(ConversionUtils.isPlus1ChargeState(peaks, precursorMzValue)) {
      precursorCharges = new short[] {1};
    } else {
      precursorCharges = new short[] {2, 3};
    }
    return precursorCharges;
  }

  /**
   * Code from TPP which Jimmy Eng gave us permission to redistribute with a new license.
   * 
   */
  @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "FE", justification = "sumTotal > 0.0 is checked before division so result will never be NaN")
  public static boolean isPlus1ChargeState(final double[] doubles, final double precursorMz) {
    double sumBelow = 0.0, sumTotal = 0.0;
    boolean isPlus1ChargeState = true;
    for(int i = 0; i < doubles.length / 2; i++) {
      final double mass = doubles[2 * i];
      final double inten = doubles[2 * i + 1];
      sumTotal += inten;
      if(mass < precursorMz) {
        sumBelow += inten;
      }
    }
    if(sumTotal > 0.0 && sumBelow / sumTotal <= SIGNAL_CUTOFF) {
      isPlus1ChargeState = false;
    }
    return isPlus1ChargeState;
  }

  /**
   * Contract: Scan must contain at least one peak list with a specified precision or 32 or 64.
   * 
   * @param scan
   * @return
   */
  public static double[] extractDoubles(final Scan scan) {
    final List<Peaks> peaksList = scan.getPeaks();
    checkArgument(peaksList != null && peaksList.size() > 0, "Scan doesn't appear to contain peaks.");
    final Peaks peaks = peaksList.get(0);
    return extractDoubles(peaks.getValue(), peaks.getPrecision().equals(BigInteger.valueOf(64)));
  }

  public static double[] extractDoublesFromBase64(final String base64, final boolean is64Bit) {
    return extractDoublesFromBase64(base64, is64Bit, false);
  }

  public static double[] extractDoublesFromBase64(final String base64, final boolean is64Bit, final boolean littleEndian) {
    return extractDoublesFromBase64(base64, is64Bit, littleEndian, false);
  }

  public static double[] extractDoublesFromBase64(final String base64, final boolean is64Bit, final boolean littleEndian, final boolean isCompressed) {
    final byte[] decodedBytes = isCompressed ? decompress(Base64.decodeBase64(base64.getBytes())) : Base64.decodeBase64(base64.getBytes());
    final double[] doubles;
    if(littleEndian) {
      doubles = extractDoublesLittleEndian(decodedBytes, is64Bit);
    } else {
      doubles = ConversionUtils.extractDoubles(decodedBytes, is64Bit);
    }
    return doubles;
  }

  public static double[] extractDoublesLittleEndian(final byte[] bytes, final boolean is64Bit) {
    double[] doubles;
    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    if(is64Bit) {
      doubles = new double[bytes.length / 8];
      for(int i = 0; i < doubles.length; i++) {
        doubles[i] = byteBuffer.getDouble();
      }
    } else {
      doubles = new double[bytes.length / 4];
      for(int i = 0; i < doubles.length; i++) {
        doubles[i] = byteBuffer.getFloat();
      }
    }
    return doubles;
  }

  public static double[] extractDoubles(final byte[] bytes, final boolean is64Bit) {
    double[] doubles;
    if(is64Bit) {
      doubles = ConversionUtils.bytes2doubles(bytes);
    } else {
      final float[] floats = ConversionUtils.bytes2floats(bytes);
      doubles = new double[floats.length];
      for(int i = 0; i < floats.length; i++) {
        doubles[i] = floats[i];
      }
    }
    return doubles;
  }

  public static  byte[] decompress(byte[] compressedData) {
    byte[] decompressedData;

    // using a ByteArrayOutputStream to not having to define the result array size beforehand
    Inflater decompressor = new Inflater();

    decompressor.setInput(compressedData);
    // Create an expandable byte array to hold the decompressed data
    ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);
    byte[] buf = new byte[1024];
    while (!decompressor.finished()) {
      try {
        int count = decompressor.inflate(buf);
        if (count == 0 && decompressor.needsInput()) {
          break;
        }
        bos.write(buf, 0, count);
      } catch (DataFormatException e) {
        throw new IllegalStateException("Encountered wrong data format " +
            "while trying to decompress binary data!", e);
      }
    }
    try {
      bos.close();
    } catch (IOException e) {
      // ToDo: add logging
      e.printStackTrace();
    }
    // Get the decompressed data
    decompressedData = bos.toByteArray();

    if (decompressedData == null) {
      throw new IllegalStateException("Decompression of binary data produced no result (null)!");
    }
    return decompressedData;
  }

  public static Float getPrecursorMz(final Scan scan) {
    float precursorMzValue = 0.0f;
    final List<Scan.PrecursorMz> precursorMzList = scan.getPrecursorMz();
    if(precursorMzList != null && precursorMzList.size() > 0) {
      precursorMzValue = scan.getPrecursorMz().get(0).getValue();
    }
    return precursorMzValue;
  }

  public static Float getPrecusorIntensity(final Scan scan) {
    Float intensity = 0.0f;
    final List<Scan.PrecursorMz> precursorMzList = scan.getPrecursorMz();
    if(precursorMzList != null && precursorMzList.size() > 0) {
      intensity = scan.getPrecursorMz().get(0).getPrecursorIntensity();
    }
    return intensity;
  }

  public static Integer getChargeState(final Scan scan) {
    Integer chargeState = null;
    final List<Scan.PrecursorMz> precursorMzList = scan.getPrecursorMz();
    if(precursorMzList != null && precursorMzList.size() > 0) {
      final BigInteger precursorCharge = scan.getPrecursorMz().get(0).getPrecursorCharge();
      if(precursorCharge != null) {
        chargeState = Integer.parseInt(precursorCharge.toString());
      }
    }
    return chargeState;
  }

  public static MsRun.ParentFile findParentFile(final List<MsRun.ParentFile> parents, final String sha1) {
    for(final MsRun.ParentFile parent : parents) {
      if(parent.getFileSha1().equals(sha1)) {
        return parent;
      }
    }
    throw new IllegalArgumentException("Unknown parent id encountered in MzXML object.");
  }

  /**
   * @param message
   *          Byte array to SHA1 encode
   * @return SHA1 encoding of input
   */
  public static String getSHA1(final byte[] message) {
    final byte[] digest = getSHA1Bytes(message);
    return digestToString(digest);
  }

  private static String digestToString(final byte[] digest) {
    return new String(HexUtils.encode(digest));
  }

  public static String getSHA1(@WillClose final InputStream inputStream) {
    final byte[] digest = getDigest(inputStream, "SHA");
    return digestToString(digest);
  }

  /**
   * @param message
   *          String to SHA1 encode
   * @return SHA1 encoding of input
   */
  public static String getSHA1(final String message) {
    return getSHA1(message.getBytes());
  }

  // Base on code found at:
  // http://mindprod.com/jgloss/sha1.html
  private static byte[] getSHA1Bytes(final byte[] message) {
    return getDigest(message, "SHA");
  }

  public static byte[] doubles2bytes(final double[] doubles) {
    final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(doubles.length * 8);
    final FloatingPointDataOutputStream stream = new FloatingPointDataOutputStream(byteOutputStream);
    for(final double doubleToWrite : doubles) {
      stream.writeDouble(doubleToWrite);
    }
    return byteOutputStream.toByteArray();
  }

  public static byte[] floats2bytes(final float[] floats) {
    final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(floats.length * 8);
    final FloatingPointDataOutputStream stream = new FloatingPointDataOutputStream(byteOutputStream);
    for(final float floatToWrite : floats) {
      stream.writeFloat(floatToWrite);
    }
    return byteOutputStream.toByteArray();
  }

  public static double[] bytes2doubles(final byte[] bytes) {
    final ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
    final FloatingPointDataInputStream stream = new FloatingPointDataInputStream(byteInputStream);
    final double[] doubles = new double[bytes.length / 8];
    for(int i = 0; i < doubles.length; i++) {
      doubles[i] = stream.readDouble();
    }
    return doubles;
  }

  public static float[] bytes2floats(final byte[] bytes) {
    final ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
    final FloatingPointDataInputStream stream = new FloatingPointDataInputStream(byteInputStream);
    final float[] floats = new float[bytes.length / 4];
    for(int i = 0; i < floats.length; i++) {
      floats[i] = stream.readFloat();
    }
    return floats;
  }
}
