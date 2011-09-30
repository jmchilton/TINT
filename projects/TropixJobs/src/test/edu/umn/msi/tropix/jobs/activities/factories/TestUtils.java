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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;

import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.TropixObjectDescription;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;

class TestUtils {
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();
  private static final Random RANDOM = new Random();

  static <T extends ActivityDescription> T init(final T activityDescription) {
    final JobDescription jobDescription = new JobDescription();
    jobDescription.setName("Job Description");
    jobDescription.setId(UUID.randomUUID().toString());
    activityDescription.setJobDescription(jobDescription);
    activityDescription.setId(UUID.randomUUID().toString());
    return activityDescription;
  }

  static <T extends TropixObjectDescription> T init(final T activityDescription) {
    init((ActivityDescription) activityDescription);
    activityDescription.setName(UUID.randomUUID().toString());
    activityDescription.setDescription(UUID.randomUUID().toString());
    activityDescription.setCommitted(RANDOM.nextBoolean());
    try {
      @SuppressWarnings("unchecked")
      final Map<String, Method> properties = BeanUtils.describe(activityDescription);
      for(final Map.Entry<String, Method> propertyEntry : properties.entrySet()) {
        final String property = propertyEntry.getKey();
        if(property.endsWith("Id")) {
          final Object object = REFLECTION_HELPER.getBeanProperty(activityDescription, property);
          if(object == null) {
            REFLECTION_HELPER.setBeanProperty(activityDescription, property, UUID.randomUUID().toString());
          }
        }
      }
    } catch(final Exception e) {
      throw new RuntimeException(e);
    }
    return activityDescription;
  }

  static ActivityContext getContext() {
    final ActivityContext context = new ActivityContext();
    context.setId(UUID.randomUUID().toString());
    context.setCredentialStr(Credentials.getMock().toString());
    return context;
  }

  static void verifyCommonMetadata(final TropixObjectDescription tropixObjectDescription, final TropixObject tropixObject) {
    assert tropixObjectDescription.getName().equals(tropixObject.getName());
    assert tropixObjectDescription.getDescription().equals(tropixObject.getDescription());
    assert tropixObjectDescription.getCommitted() == tropixObject.getCommitted() : "Excepted committed of " + tropixObjectDescription.getCommitted() + " obtained " + tropixObject.getCommitted();
  }

  static TropixFile getNewTropixFile() {
    return getNewTropixFile("http://storage");
  }

  static TropixFile getNewTropixFile(final String storageServiceUrl) {
    final TropixFile tropixFile = new TropixFile();
    tropixFile.setId(UUID.randomUUID().toString());
    tropixFile.setFileId(UUID.randomUUID().toString());
    tropixFile.setStorageServiceUrl(storageServiceUrl);
    return tropixFile;
  }

}
