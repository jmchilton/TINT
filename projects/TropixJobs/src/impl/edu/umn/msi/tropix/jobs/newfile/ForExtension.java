package edu.umn.msi.tropix.jobs.newfile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Named;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import edu.umn.msi.tropix.persistence.service.impl.StockFileExtensionEnum;

@Scope("prototype")
@Component
@Named
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForExtension {
  
  StockFileExtensionEnum[] value();
  
}
