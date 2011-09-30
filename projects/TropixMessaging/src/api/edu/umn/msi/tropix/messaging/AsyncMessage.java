package edu.umn.msi.tropix.messaging;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

import org.apache.camel.Pattern;
import org.apache.camel.ExchangePattern;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Pattern(ExchangePattern.InOnly)
public @interface AsyncMessage {
}
