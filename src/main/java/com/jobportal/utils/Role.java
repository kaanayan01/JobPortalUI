package com.jobportal.utils;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Role {
    String value(); // e.g. ADMIN
}

