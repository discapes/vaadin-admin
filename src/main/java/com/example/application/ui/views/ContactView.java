package com.example.application.ui.views;

import com.example.application.actions.CopyAction;
import com.example.application.actions.DeleteAction;
import com.example.application.data.entity.Company;
import com.example.application.data.entity.Contact;
import com.example.application.data.entity.Status;
import com.example.application.data.repository.CompanyRepository;
import com.example.application.data.repository.ContactRepository;
import com.example.application.data.repository.StatusRepository;
import com.example.application.frame.CrudForm;
import com.example.application.frame.CrudView;
import com.example.application.frame.NavigationItem;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
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

@NavigationItem(value = "Contacts", icon = VaadinIcon.PHONE)
@Route(value = "contacts", layout = MainLayout.class)
public class ContactView extends CrudView<Contact> {

    TextField searchString = new TextField();
    ComboBox<Status> statusSelector = new ComboBox<>();
    ContactRepository repository;
    EntityManager em;
    StatusRepository statusRepository;
    CompanyRepository companyRepository;

    @Autowired
    public ContactView(EntityManager em, ContactRepository repository, CompanyRepository companyRepository, StatusRepository statusRepository) {
        super(Contact.class, em, List.of(new CopyAction<>(repository), new DeleteAction<>(repository)), repository);
        this.companyRepository = companyRepository;
        this.statusRepository = statusRepository;
        this.em = em;
        this.repository = repository;
        searchString.setValueChangeMode(ValueChangeMode.EAGER);
        searchString.addValueChangeListener(e -> updateList());
        searchString.setPlaceholder("Search...");
        statusSelector.setItems(statusRepository.findAll());
        statusSelector.setPlaceholder("Status");
        statusSelector.addValueChangeListener(e -> updateList());
        statusSelector.setClearButtonVisible(true);
        statusSelector.setItemLabelGenerator(Status::getName);
        initialize();
    }

    @Override
    public void withColumns(Grid<Contact> grid) {
        grid.addColumns("firstName", "lastName", "email");
        grid.addColumn(contact -> contact.getStatus().getName()).setHeader("Status");
        grid.addColumn(contact -> contact.getCompany().getName()).setHeader("Company");
    }

    @Override
    protected CrudForm<Contact> getCrudForm() {
        return new ContactForm(companyRepository, statusRepository);
    }

    @RegisterReflectionForBinding(ContactForm.class)
    public static class ContactForm extends CrudForm<Contact> {
        final TextField firstName = new TextField("First name");
        final TextField lastName = new TextField("Last name");
        final EmailField email = new EmailField("Email");
        final ComboBox<Status> status = new ComboBox<>("Status");
        final ComboBox<Company> company = new ComboBox<>("Company");
        final Binder<Contact> binder = new BeanValidationBinder<>(Contact.class);

        public ContactForm(CompanyRepository companyRepository, StatusRepository statusRepository) {
            binder.bindInstanceFields(this);
            company.setItems(companyRepository.findAll());
            company.setItemLabelGenerator(Company::getName);
            status.setItems(statusRepository.findAll());
            status.setItemLabelGenerator(Status::getName);
            firstName.setValueChangeMode(ValueChangeMode.EAGER);
            lastName.setValueChangeMode(ValueChangeMode.EAGER);
            email.setValueChangeMode(ValueChangeMode.EAGER);
            add(firstName, lastName, email, company, status);
        }

        @Override
        public Binder<Contact> getBinder() {
            return binder;
        }
    }

    @Override
    protected Component getFilterBar() {
        return new HorizontalLayout(searchString, statusSelector);
    }

    @Override
    protected Stream<Contact> getItems(Pageable pageable) {
        return repository.search(searchString.getValue(), statusSelector.getValue(), pageable).stream();
    }
}
