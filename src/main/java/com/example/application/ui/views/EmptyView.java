package com.example.application.ui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route("")
public class EmptyView extends VerticalLayout implements BeforeEnterObserver {

    public EmptyView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(new H1("Redirecting..."));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        UI.getCurrent().navigateToClient("/contacts");
    }
}
