package com.doveaccelerator.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for permission-based authorization.
 * Methods annotated with this will be checked for the required permission.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    /**
     * The permission name required to execute the method.
     * @return permission name
     */
    String value();
}