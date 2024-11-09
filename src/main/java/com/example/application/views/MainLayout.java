package com.example.application.views;

import com.example.application.views.lobby.LobbyView;
import com.example.application.views.lobby.LobbyView2;
import com.example.application.views.lobby.LobbyView3;
import com.example.application.views.testpkg.TesterPage1;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {
    private final AuthenticationContext authenticationContext;

    private H2 viewTitle;

    public MainLayout(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
        setPrimarySection(Section.DRAWER); 
        addNavbarContent();
        addDrawerContent();
    }

    private void addNavbarContent() {
// tag::snippet[]
    var toggle = new DrawerToggle(); 
    toggle.setAriaLabel("Menu toggle"); 
    toggle.setTooltipText("Menu toggle");

    viewTitle = new H2();
    viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE,
        LumoUtility.Flex.GROW);
    
    var logout = new Button("Logout " + authenticationContext.getPrincipalName().orElse(""), 
        event -> authenticationContext.logout());
    logout.addClassName("btn1");

    var header = new Header(toggle, viewTitle, logout); 
    header.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX,
        LumoUtility.Padding.End.MEDIUM, LumoUtility.Width.FULL);

    addToNavbar(false, header); 
// end::snippet[]
}

    private void addDrawerContent() {
// tag::snippet[]
    var appName = new Span("Vaadin Chat"); 
    appName.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX,
            LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD,
            LumoUtility.Height.XLARGE, LumoUtility.Padding.Horizontal.MEDIUM);

    addToDrawer(appName, new Scroller(createSideNav())); 
// end::snippet[]
}

// tag::snippet[]
private SideNav createSideNav() {
    SideNav nav = new SideNav(); 

    nav.addItem(new SideNavItem("Lobby", LobbyView.class,
        VaadinIcon.BUILDING.create())); 
    
        nav.addItem(new SideNavItem("Lobby2", LobbyView2.class,
        VaadinIcon.BUILDING.create())); 

        nav.addItem(new SideNavItem("Lobby3", LobbyView3.class,
        VaadinIcon.BUILDING.create())); 

        nav.addItem(new SideNavItem("Final Countdown", TesterPage1.class,
        VaadinIcon.BUILDING.create()));

    return nav;
}
// end::snippet[]

private String getCurrentPageTitle() {
    if (getContent() == null) {
        return "";
    } else if (getContent() instanceof HasDynamicTitle titleHolder) {
        return titleHolder.getPageTitle();
    } else {
        var title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}

@Override
protected void afterNavigation() {
    super.afterNavigation();
    viewTitle.setText(getCurrentPageTitle());

}


}