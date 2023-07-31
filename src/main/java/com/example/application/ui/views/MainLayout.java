package com.example.application.ui.views;

import com.example.application.Application;
import com.example.application.frame.NavigationItem;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteData;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MainLayout extends AppLayout implements HasDynamicTitle {

    H1 pageTitle = new H1(Application.APP_NAME);

    Logger logger = LoggerFactory.getLogger(MainLayout.class);

    public MainLayout() {
        setPrimarySection(Section.DRAWER);

        addToNavbar(createHeader());

        H1 logo = new H1(Application.APP_NAME);
        logo.addClassNames(LumoUtility.FontSize.LARGE);
        logo.addClassNames(LumoUtility.Padding.LARGE);

        addToDrawer(logo, createSideNav());
    }

    private HorizontalLayout createHeader() {
        pageTitle.addClassNames(LumoUtility.FontSize.LARGE);
        var header = new HorizontalLayout(new DrawerToggle(), pageTitle);
        header.setPadding(true);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(LumoUtility.Padding.SMALL);
        return header;
    }

    private SideNav createSideNav() {
        List<RouteData> routes = RouteConfiguration.forSessionScope().getAvailableRoutes();
        List<SideNavItem> navItems = routes.stream()
                .filter(routeData -> MainLayout.class.equals((routeData.getParentLayout())))
                .map(this::getNavItem)
                .toList();

        SideNav nav = new SideNav();
        nav.addItem(navItems.toArray(new SideNavItem[0]));
        return nav;
    }

    @Override
    public void showRouterLayoutContent(HasElement hasElement) {
        pageTitle.setText(getViewTitle(hasElement.getClass()));
        super.showRouterLayoutContent(hasElement);
    }

    private SideNavItem getNavItem(RouteData routeData) {
        Class<? extends Component> clazz = routeData.getNavigationTarget();
        String title = getViewTitle(clazz);
        VaadinIcon icon = getViewIcon(clazz);
        return new SideNavItem(title, clazz, icon.create());
    }

    private String getViewTitle(Class<?> clazz) {
        try {
            return clazz.getAnnotationsByType(NavigationItem.class)[0].value();
        } catch (Exception e) {
            logger.warn("Unable to set page title: " + e);
            return "Untitled";
        }
    }

    private VaadinIcon getViewIcon(Class<?> clazz) {
        try {
            return clazz.getAnnotationsByType(NavigationItem.class)[0].icon();
        } catch (Exception e) {
            logger.warn("Unable to set page icon: " + e);
            return VaadinIcon.CROSSHAIRS;
        }
    }

    @Override
    public String getPageTitle() {
        return pageTitle.getText() + " | " + Application.APP_NAME;
    }
}
