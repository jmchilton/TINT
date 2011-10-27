/*******************************************************************************
 * Copyright 2009 Regents of the University of Minnesota. All rights
 * reserved.
 * Copyright 2009 Mayo Foundation for Medical Education and Research.
 * All rights reserved.
 *
 * This program is made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS
 * OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A
 * PARTICULAR PURPOSE.  See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 ******************************************************************************/

package edu.umn.msi.tropix.models.utils;

import javax.annotation.Nullable;

import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.TropixFile;

public enum StockFileExtensionEnum implements StockFileExtensionI {
  ZIP(".zip"), XML(".xml"), OMSSA_OUTPUT(".omx.zip"),
  FASTA(".fasta"), FASTQ(".fastq"), TEXT(".txt"),
  MZXML(".mzXML"), SCAFFOLD_REPORT(".sfd"), SCAFFOLD3_REPORT(".sf3"),
  TABULAR_XLS(".xls"), BOWTIE_INDEX(".ebwt.zip"), THERMO_RAW(".RAW"),
  MASCOT_OUTPUT(".dat"), MASCOT_GENERIC_FORMAT(".mgf"), UNKNOWN(""), PEPXML(".pepXML");

  private String extension;

  private StockFileExtensionEnum(final String extension) {
    this.extension = extension;
  }

  public String getExtension() {
    return extension;
  }
  
  public String stripExtension(final String fileName) {
    final boolean hasExtension = fileName.toUpperCase().endsWith(getExtension().toUpperCase());
    final int length = fileName.length();
    final String databaseName = hasExtension ? fileName.substring(0, length - getExtension().length()) : fileName;
    return databaseName;
  }
  
  public StockFileExtensionEnum loadForExtension(final String extension) {
    StockFileExtensionEnum fileExtension = null;
    for(StockFileExtensionEnum stockFileExtension : values()) {
      if(stockFileExtension.getExtension().equals(extension)) {
        fileExtension = stockFileExtension;
        break;
      }
    }
    return fileExtension;
  }
  
  @Nullable
  public static StockFileExtensionEnum loadForFile(final TropixFile tropixFile) {
    StockFileExtensionEnum fileExtension = null;
    final FileType fileType = tropixFile.getFileType();
    if(fileType != null) {
      final String extensionString = fileType.getExtension();
      if(extensionString != null) {
        for(StockFileExtensionEnum value : values()) {
          if(value.extension.equals(extensionString)) {
            fileExtension = value;
          }
        }
      }
    }
    return fileExtension;
  }

}
