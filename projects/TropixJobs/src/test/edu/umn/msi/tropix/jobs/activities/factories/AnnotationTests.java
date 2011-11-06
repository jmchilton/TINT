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

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javassist.Modifier;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.Entity;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;

/**
 * This class does some static analysis of various configuration annotations. Its not a unit test per se.
 * 
 * @author John Chilton
 * 
 */
public class AnnotationTests {

  @SuppressWarnings("unchecked")
  private static final Collection<Class<? extends ActivityFactory<?>>> FACTORIES = Lists.<Class<? extends ActivityFactory<?>>>newArrayList(
      CreateBowtieAnalysisActivityFactoryImpl.class, CreateBowtieIndexActivityFactoryImpl.class, CreateDatabaseActivityFactoryImpl.class,
      CreateFolderActivityFactoryImpl.class, CreateProteomicsRunActivityFactoryImpl.class, CreateIdentificationAnalysisActivityFactoryImpl.class,
      CreateIdentificationParametersActivityFactoryImpl.class, CreateITraqQuantitationAnalysisActivityFactoryImpl.class,
      CreateITraqQuantitationTrainingActivityFactoryImpl.class, CreateScaffoldDriverActivityFactoryImpl.class,
      CreateScaffoldAnalysisActivityFactoryImpl.class, CreateTropixFileActivityFactoryImpl.class,
      CreateIdPickerParametersActivityFactoryImpl.class, SubmitIdPickerAnalysisActivityFactoryImpl.class,
      MergeScaffoldSamplesActivityFactoryImpl.class, PollJobActivityFactoryImpl.class, SubmitBowtieAnalysisActivityFactoryImpl.class,
      SubmitGalaxyActivityFactoryImpl.class, SubmitIdentificationAnalysisJobActivityFactoryImpl.class,
      SubmitITraqQuantitationAnalysisJobActivityFactoryImpl.class, SubmitITraqQuantitationTrainingJobActivityFactoryImpl.class,
      SubmitProteomicsConvertActivityFactoryImpl.class, SubmitScaffoldAnalysisActivityFactoryImpl.class,
      SubmitThermofinniganRunJobActivityFactoryImpl.class, UploadFileActivityFactoryImpl.class, CreateTissueSampleActivityFactoryImpl.class,
      CommitObjectActivityFactoryImpl.class);

  @Test(groups = "unit", timeOut = 1000)
  public void test() throws ClassNotFoundException {
    final Collection<Class<?>> descriptionClasses = Lists.newArrayList();
    final URL url = AnnotationTests.class.getClassLoader().getResource("META-INF/jobs/persistence.xml");
    final String jpaConfigContents = InputContexts.toString(InputContexts.forUrl(url));
    final List<Class<? extends ActivityDescription>> activityDescriptionsMapped = Lists.newArrayList();
    for(final Class<? extends ActivityFactory<?>> factoryClass : FACTORIES) {
      // Make sure the class is make as a ManageBean to DI context picks it up.
      assert factoryClass.getAnnotation(ManagedBean.class) != null : "Factory class " + factoryClass + " is not annotated with @ManagedBean";

      // assert factoryClass.getDeclaredConstructors().length == 1;
      boolean foundInjectedConstructor = false;
      for(Constructor<?> constructor : factoryClass.getDeclaredConstructors()) {
        if(constructor.getParameterTypes().length == 0 || constructor.getAnnotation(Inject.class) != null) {
          foundInjectedConstructor = true;
        }
      }
      assert foundInjectedConstructor;

      final ActivityFactoryFor factoryAnnotation = factoryClass.getAnnotation(ActivityFactoryFor.class);
      // Make sure this class specifies which ActivityDescription it acts an ActivityFactory for.
      assert factoryAnnotation != null;
      final Class<? extends ActivityDescription> descriptionClass = factoryAnnotation.value();
      activityDescriptionsMapped.add(descriptionClass);
      assert ActivityDescription.class.isAssignableFrom(descriptionClass);
      // Make sure there are not multiple descriptions per factory
      assert !descriptionClasses.contains(descriptionClass);

      // Make sure the description can be persisted
      assert descriptionClass.getAnnotation(Entity.class) != null : descriptionClass + " is not annotated with entity.";
      assert jpaConfigContents.contains(descriptionClass.getCanonicalName()) : "Class " + descriptionClass.getCanonicalName()
          + " not found in JPA configuration.";
    }

    final Pattern pattern = Pattern.compile("\\<class\\>.*\\<");
    final Matcher matcher = pattern.matcher(jpaConfigContents);
    int start = 0;
    while(matcher.find(start)) {
      final int startIdx = matcher.start() + "<class>".length();
      final int endIdx = jpaConfigContents.indexOf("<", startIdx);
      final String classStr = jpaConfigContents.substring(startIdx, endIdx);
      final Class<?> clazz = Class.forName(classStr);
      if(ActivityDescription.class.isAssignableFrom(clazz)) {
        if(!Modifier.isAbstract(clazz.getModifiers())) {
          assert activityDescriptionsMapped.contains(clazz) : "No activity factory found for description class " + clazz;
        }
      }
      start = endIdx;
    }
  }

}
