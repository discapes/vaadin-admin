package com.example.application.frame;

import com.vaadin.flow.component.icon.VaadinIcon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NavigationItem {
    String value();

    VaadinIcon icon();
}
