package com.makewithmoto.apidoc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented  
@Target(ElementType.FIELD) 
@Retention(RetentionPolicy.RUNTIME) 
@Inherited 
public @interface APIAnnotationField { 
	
	public String description() default ""; 
	public String example() default ""; 	
	
} 
