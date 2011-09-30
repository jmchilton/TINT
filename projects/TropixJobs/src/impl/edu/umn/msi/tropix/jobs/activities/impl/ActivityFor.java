package edu.umn.msi.tropix.jobs.activities.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Named;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;

@Scope("prototype")
@Component()
@Named
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityFor {
  Class<? extends ActivityDescription> descriptionClass();
}
