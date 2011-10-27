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

package edu.umn.msi.tropix.jobs.test;

import java.lang.reflect.Field;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.message.MessageSource;
import edu.umn.msi.tropix.files.NewFileMessageQueue;
import edu.umn.msi.tropix.files.PersistentModelStorageDataFactory;
import edu.umn.msi.tropix.jobs.activities.ActivityDirector;
import edu.umn.msi.tropix.jobs.activities.descriptions.MessageCodeConstants;

/**
 * Just loads the spring beans to make sure they all fit together properly.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 * 
 */
@ContextConfiguration(locations = "testApplicationContext.xml")
public class IntegrationTest extends AbstractTestNGSpringContextTests {

  @Test(groups = "integration")
  public void loadBeans() {
    assert applicationContext.getBean("persistentStorageDataFactory") != null;
    assert applicationContext.getBean(ActivityDirector.class) != null;
    assert applicationContext.getBean(PersistentModelStorageDataFactory.class) != null;
    assert applicationContext.getBean(NewFileMessageQueue.class) != null;
  }

  @Test(groups = "integration")
  public void testMessages() throws IllegalArgumentException, IllegalAccessException {
    final MessageSource messageSource = (MessageSource) applicationContext.getBean("jobsMessageSource");
    for(final Field field : MessageCodeConstants.class.getFields()) {
      if(field.getType().equals(String.class)) {
        assert null != messageSource.getMessage((String) field.get(new MessageCodeConstants()));
      }
    }

  }

}
