package com.example.application.frame;

import com.example.application.data.entity.AbstractEntity;
import com.example.application.ui.style.StyleUtility;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.Stream;


public abstract class CrudView<T extends AbstractEntity> extends VerticalLayout {
    protected abstract void withColumns(Grid<T> grid);

    protected abstract CrudForm<T> getCrudForm();

    protected abstract Component getFilterBar();

    protected abstract Stream<T> getItems(Pageable pageable);

    List<Action<T>> actions;
    HorizontalLayout localActionBar = new HorizontalLayout();
    HorizontalLayout globalActionBar = new HorizontalLayout();
    VerticalLayout formAndActions = new VerticalLayout();
    Grid<T> grid;
    T editingModel;

    EntityManager em;
    CrudForm<T> form;
    Logger logger = LoggerFactory.getLogger(CrudView.class);
    Class<T> clazz;
    JpaRepository<T, Long> repository;

    public CrudView(Class<T> clazz, EntityManager em, List<Action<T>> actions, JpaRepository<T, Long> repository) {
        grid = new Grid<>(clazz, false);
        this.clazz = clazz;
        this.actions = actions;
        this.em = em;
        this.repository = repository;
    }

    public void initialize() {
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        configureActionBars();
        configureForm();
        Button addButton = new Button("Add");
        addButton.addClickListener(e -> addModel());
        HorizontalLayout toolbar = new HorizontalLayout(getFilterBar(), addButton, globalActionBar);
        toolbar.setAlignItems(Alignment.CENTER);
        HorizontalLayout content = new HorizontalLayout(grid, formAndActions);
        content.setSizeFull();

        add(toolbar, content);
        updateList();
        closeEditor();
    }

    void configureGrid() {
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setSizeFull();
        grid.asMultiSelect().addSelectionListener(e -> updateActionBar(globalActionBar, e.getAllSelectedItems().stream().toList()));
        grid.addClassNames("contact-grid");
        var editCol = grid.addComponentColumn(this::editButtonRenderer);
        withColumns(grid);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        editCol.setAutoWidth(false).setWidth("42px").setFlexGrow(0).setClassNameGenerator(c -> "center-cell-content");
    }

    void updateActionBar(HorizontalLayout actionBar, List<T> models) {
        actionBar.removeAll();
        for (Action<T> action : actions) {
            var button = new Button(action.getName());
            button.setEnabled(action.isEnabled(models));
            button.addClickListener(e -> {
                logger.info("forming action " + action.getName() + " for item " + models);
                action.perform(models);
                updateList();
            });
            actionBar.add(button);
        }
    }

    private Button editButtonRenderer(T model) {
        var button = new Button(new Icon(VaadinIcon.PENCIL));
        button.addClickListener(b -> editModel(model));
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.setWidthFull();
        button.getParent();
        return button;
    }

    private void configureForm() {
        form = getCrudForm();
        var binder = form.getBinder();

        Button saveButton = new Button("Save");
        saveButton.addClickListener(evt -> {
            try {
                binder.writeBean(editingModel);
                saveModel(editingModel);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Button closeButton = new Button("Close");
        closeButton.addClickListener(e -> closeEditor());
        Button resetButton = new Button("Reset");
        resetButton.addClickListener(evt ->
                binder.readBean(editingModel));
        binder.addStatusChangeListener(event -> {
            boolean isValid = event.getBinder().isValid();
            boolean hasChanges = event.getBinder().hasChanges();
            saveButton.setEnabled(hasChanges && isValid);
            resetButton.setEnabled(hasChanges);
        });

        var basicActions = new HorizontalLayout(saveButton, closeButton, resetButton);
        formAndActions.add(localActionBar, form, basicActions);
        formAndActions.addClassName(LumoUtility.Padding.Top.NONE);
        formAndActions.addClassName(StyleUtility.Gap_0);
        basicActions.addClassNames(LumoUtility.Padding.Vertical.MEDIUM);
        formAndActions.setWidth("25em");
    }

    public void saveModel(T model) {
        repository.save(model);
        updateList();
        closeEditor();
    }

    private void configureActionBars() {
        updateActionBar(globalActionBar, List.of());
        globalActionBar.addClassName("toolbar");
        globalActionBar.setAlignItems(Alignment.CENTER);
        globalActionBar.addClassName(LumoUtility.FlexWrap.WRAP);
        localActionBar.addClassName(LumoUtility.FlexWrap.WRAP);
    }

    public void updateList() {
        closeEditor();

        grid.setItems(vaadinQuery -> {
            var sortOrders = vaadinQuery.getSortOrders().stream().map(so ->
                    switch (so.getDirection()) {
                        case ASCENDING -> Sort.Order.asc(so.getSorted());
                        case DESCENDING -> Sort.Order.desc(so.getSorted());
                    }
            ).toList();

            PageRequest pr = PageRequest.of(
                    vaadinQuery.getPage(),
                    vaadinQuery.getPageSize(),
                    Sort.by(sortOrders)
            );
            return getItems(pr);
        });
    }

    public void editModel(T model) {
        editingModel = model;
        updateActionBar(localActionBar, List.of(model));
        form.getBinder().readBean(model);
        formAndActions.setVisible(true);
        addClassName("editing");
    }

    private void closeEditor() {
        editingModel = null;
        form.getBinder().readBean(null);
        formAndActions.setVisible(false);
        removeClassName("editing");
    }

    private void addModel() {
        try {
            editModel(clazz.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
