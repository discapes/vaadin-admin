package com.example.application.ui.views;

import com.example.application.actions.CopyAction;
import com.example.application.actions.DeleteAction;
import com.example.application.data.entity.Company;
import com.example.application.data.repository.CompanyRepository;
import com.example.application.frame.CrudForm;
import com.example.application.frame.CrudView;
import com.example.application.frame.NavigationItem;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import jakarta.persistence.EntityManager;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Stream;

@NavigationItem(value = "Companies", icon = VaadinIcon.PHONE_LANDLINE)
@Route(value = "companies", layout = MainLayout.class)
public class CompanyView extends CrudView<Company> {

    TextField searchString = new TextField();
    CompanyRepository repository;
    EntityManager em;

    @Autowired
    public CompanyView(EntityManager em, CompanyRepository repository) {
        super(Company.class, em, List.of(new CopyAction<>(repository), new DeleteAction<>(repository)), repository);
        this.em = em;
        this.repository = repository;
        searchString.setValueChangeMode(ValueChangeMode.EAGER);
        searchString.addValueChangeListener(e -> updateList());
        searchString.setPlaceholder("Search...");
        initialize();
    }

    @Override
    public void withColumns(Grid<Company> grid) {
        grid.addColumns("name", "employeeCount");
    }

    @Override
    protected CrudForm<Company> getCrudForm() {
        return new CompanyForm();
    }

    @RegisterReflectionForBinding(CompanyForm.class)
    public static class CompanyForm extends CrudForm<Company> {
        final TextField name = new TextField(" Name");
        final Binder<Company> binder = new BeanValidationBinder<>(Company.class);

        public CompanyForm() {
            binder.bindInstanceFields(this);
            name.setValueChangeMode(ValueChangeMode.EAGER);
            add(name);
        }

        @Override
        public Binder<Company> getBinder() {
            return binder;
        }
    }

    @Override
    protected Component getFilterBar() {
        return new HorizontalLayout(searchString);
    }

    @Override
    protected Stream<Company> getItems(Pageable pageable) {
        return repository.search(searchString.getValue(), pageable).stream();
    }
}
