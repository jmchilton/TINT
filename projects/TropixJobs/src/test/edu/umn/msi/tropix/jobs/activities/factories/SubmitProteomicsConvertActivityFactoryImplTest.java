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

package edu.umn.msi.tropix.jobs.activities.factories;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitProteomicsConvertDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.proteomics.convert.input.ConvertParameters;
import edu.umn.msi.tropix.proteomics.convert.input.Format;
import edu.umn.msi.tropix.proteomics.service.ProteomicsConvertJobQueueContext;

public class SubmitProteomicsConvertActivityFactoryImplTest extends BaseSubmitActivityFactoryImplTest {

    @Test(groups = "unit")
    public void submit() {
      init();
      final SubmitProteomicsConvertActivityFactoryImpl factory = new SubmitProteomicsConvertActivityFactoryImpl(getFactorySupport(), getSubmitSupport());
      final SubmitProteomicsConvertDescription description = TestUtils.init(new SubmitProteomicsConvertDescription());

      final String serviceUrl = "http://ProteomicsConvert";
      description.setServiceUrl(serviceUrl);


      final TropixFile f1 = TestUtils.getNewTropixFile();
      getFactorySupport().saveObjects(f1);
      description.setInputFileId(f1.getId());

      description.setInputFormat(Format.MGF.toString());
      description.setOutputFormat(Format.MzXML.toString());
      description.setInputName("moocow");
      
      final ProteomicsConvertJobQueueContext context = expectCreateJob(serviceUrl, ProteomicsConvertJobQueueContext.class);
      
      final Capture<ConvertParameters> parametersCapture = EasyMockUtils.newCapture();
      
      context.submitJob(expectDownload(f1), expectCredentialResource(), EasyMock.capture(parametersCapture));

      preRun(context);
      final Activity activity = factory.getActivity(description, getContext());
      activity.run();
      postRun(description);
      
      final ConvertParameters parameters = parametersCapture.getValue();
      assert parameters.getInputFormat().equals(Format.MGF);
      assert parameters.getOutputFormat().equals(Format.MzXML);
      assert parameters.getInputName().equals("moocow");
      
    }

}
