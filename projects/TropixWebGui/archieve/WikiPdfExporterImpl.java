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

package edu.umn.msi.tropix.webgui.server.wiki;

import info.bliki.pdf.PDFGenerator;
import info.bliki.wiki.model.WikiModel;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.ManagedBean;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IORuntimeException;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

@ManagedBean
class WikiPdfExporterImpl implements WikiPdfExporter {
  private static final WikiModel MODEL = new IslandWikiModel();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final String CSS_STYLE = "<style type=\"text/css\">\n" + "h1 { color: maroon; }\n" + "h2 { color: orange; }\n" + "b  { color: green; }\n" + "@page { \n" + "margin: 0.25in; \n" + "-fs-flow-top: header; \n" + "-fs-flow-bottom: footer; \n"
      + "-fs-flow-left: left; \n" + "-fs-flow-right: right; \n" + "padding: 1em; \n" + "} \n" + "</style>\n";

  public void exportAsPdf(final String rawWikiText, final OutputContext outputContext) {
    final File tempPdfFile = FILE_UTILS.createTempFile("tpxwiki", "pdf");
    try {
      final String pdfXhtml = MODEL.renderPDF(rawWikiText);
      URL url;
      try {
        url = tempPdfFile.getParentFile().toURI().toURL();
      } catch(final MalformedURLException e) {
        throw new IORuntimeException(e);
      }
      final PDFGenerator gen = new PDFGenerator(url);
      try {
        gen.create(tempPdfFile.getAbsolutePath(), pdfXhtml, PDFGenerator.HEADER_TEMPLATE, PDFGenerator.FOOTER, "Big Test", CSS_STYLE);
      } catch(final Exception e) {
        throw ExceptionUtils.convertException(e, "Failed to generated pdf file");
      }
      outputContext.put(tempPdfFile);
    } finally {
      FILE_UTILS.deleteQuietly(tempPdfFile);
    }
  }
}
