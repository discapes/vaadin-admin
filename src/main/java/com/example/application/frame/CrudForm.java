package com.example.application.frame;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;

public abstract class CrudForm<T> extends FormLayout {
    public abstract Binder<T> getBinder();
}
