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

package edu.umn.msi.tropix.proteomics.utils;

public class HexUtils {

  /*
   * public static byte[][] encodeArray(byte[][] binaryArray) { byte[][] hexArray = new byte[binaryArray.length][]; for(int i = 0; i < binaryArray.length; i++) { hexArray[i] = encode(binaryArray[i]); } return hexArray; }
   */

  /*
   * public static byte[][] decodeArray(byte[][] hexArray) { byte[][] binaryArray = new byte[hexArray.length][]; for(int i = 0; i < hexArray.length; i++) { binaryArray[i] = decode(hexArray[i]); } return binaryArray; }
   */

  public static byte[] encode(final byte[] binary) {
    final byte[] hexBinary = new byte[binary.length * 2];
    encode(binary, hexBinary);
    return hexBinary;
  }

  /*
   * public static byte[] decode(byte[] hexBinary) { byte[] binary = new byte[hexBinary.length / 2]; decode(hexBinary, binary); return binary; }
   */

  public static void encode(final byte[] binary, final byte[] hexBinary) {
    for(int i = 0; i < binary.length; i++) {
      hexBinary[2 * i + 1] = encodeByte(binary[i] & 0x0f);
      hexBinary[2 * i] = encodeByte((binary[i] & 0xf0) >>> 4);
    }
  }

  /*
   * public static void decode(byte[] hexBinary, byte[] binary) { int lowerHex, upperHex; for(int i = 0; i < binary.length; i++) { lowerHex = decodeByte(hexBinary[2*i+1]); upperHex = decodeByte(hexBinary[2*i]); binary[i] = (byte) (lowerHex | (upperHex << 4)); } }
   */
  public static byte encodeByte(final int binaryByte) {
    return (byte) ((binaryByte < 10) ? ('0' + binaryByte) : (('a' - 10) + binaryByte));
  }

  /*
   * public static int decodeByte(byte hexByte) { return (('a' <= hexByte) && (hexByte <= 'f')) ? (hexByte - 'a' + 10) : (hexByte - '0'); }
   */

}
