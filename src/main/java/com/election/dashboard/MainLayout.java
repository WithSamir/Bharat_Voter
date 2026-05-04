package com.election.dashboard;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * MainLayout – Premium dark-theme app shell with tricolour accent navbar
 * and a rich sidebar navigation.
 */
public class MainLayout extends AppLayout {

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        createHeader();
        createDrawer();
        getStyle().set("--lumo-base-color", "#0d1929");
    }

    private void createHeader() {
        // Tricolour gradient top bar
        Div flagStripe = new Div();
        flagStripe.getStyle()
            .set("height", "4px")
            .set("width", "100%")
            .set("background", "linear-gradient(90deg, #FF9933 33.3%, #FFFFFF 33.3% 66.6%, #138808 66.6%)")
            .set("flex-shrink", "0");

        DrawerToggle toggle = new DrawerToggle();
        toggle.getStyle().set("color", "white");

        // Logo block
        Div logoBlock = new Div();
        logoBlock.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("gap", "10px");

        Span emblem = new Span("🇮🇳");
        emblem.getStyle().set("font-size", "24px");

        Div titleBlock = new Div();
        H1 title = new H1("Bharat Voter Portal");
        title.getStyle()
            .set("color", "white")
            .set("margin", "0")
            .set("font-size", "18px")
            .set("font-weight", "700")
            .set("letter-spacing", "-0.3px");

        Paragraph subtitle = new Paragraph("Election Commission of India · Digital Portal");
        subtitle.getStyle()
            .set("color", "rgba(255,255,255,0.55)")
            .set("margin", "0")
            .set("font-size", "11px");

        titleBlock.add(title, subtitle);
        logoBlock.add(emblem, titleBlock);

        // Status badge
        Div statusBadge = new Div();
        statusBadge.getStyle()
            .set("background", "rgba(19,136,8,0.2)")
            .set("border", "1px solid rgba(19,136,8,0.5)")
            .set("border-radius", "20px")
            .set("padding", "3px 12px")
            .set("display", "flex")
            .set("align-items", "center")
            .set("gap", "6px");

        Span dot = new Span("●");
        dot.getStyle().set("color", "#4CAF50").set("font-size", "10px");
        Span liveText = new Span("Live");
        liveText.getStyle().set("color", "#4CAF50").set("font-size", "12px").set("font-weight", "600");
        statusBadge.add(dot, liveText);

        HorizontalLayout navbar = new HorizontalLayout(toggle, logoBlock);
        navbar.setWidthFull();
        navbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        navbar.getStyle()
            .set("background", "linear-gradient(135deg, #0f1b2d 0%, #1a2f4a 100%)")
            .set("padding", "10px 20px")
            .set("border-bottom", "1px solid rgba(255,255,255,0.08)");
        navbar.setFlexGrow(1, logoBlock);
        navbar.add(statusBadge);

        Div headerWrapper = new Div(flagStripe, navbar);
        headerWrapper.setWidthFull();
        headerWrapper.getStyle().set("display", "flex").set("flex-direction", "column");

        addToNavbar(headerWrapper);
    }

    private void createDrawer() {
        // Drawer header
        Div drawerHeader = new Div();
        drawerHeader.getStyle()
            .set("background", "linear-gradient(135deg, #FF9933 0%, #FF6B00 50%, #138808 100%)")
            .set("padding", "20px 16px")
            .set("margin-bottom", "8px");

        Paragraph appName = new Paragraph("🗳️ ECI Digital Portal");
        appName.getStyle()
            .set("color", "white")
            .set("font-weight", "700")
            .set("font-size", "16px")
            .set("margin", "0 0 4px 0");

        Paragraph version = new Paragraph("Version 2.0 · Secure");
        version.getStyle()
            .set("color", "rgba(255,255,255,0.8)")
            .set("font-size", "11px")
            .set("margin", "0");

        drawerHeader.add(appName, version);

        // Navigation
        SideNav nav = new SideNav();
        nav.getStyle()
            .set("padding", "0 8px")
            .set("width", "100%");

        SideNavItem homeItem = new SideNavItem("🏠  Home Dashboard", MainView.class, VaadinIcon.DASHBOARD.create());
        SideNavItem registerItem = new SideNavItem("📋  Register to Vote (Form 6)", VoterRegistrationView.class, VaadinIcon.FILE_TEXT_O.create());
        SideNavItem trackItem = new SideNavItem("🔍  Track Application", TrackApplicationView.class, VaadinIcon.SEARCH.create());

        nav.addItem(homeItem, registerItem, trackItem);

        // Footer info
        Div drawerFooter = new Div();
        drawerFooter.getStyle()
            .set("padding", "16px")
            .set("margin-top", "auto")
            .set("border-top", "1px solid rgba(255,255,255,0.1)");

        Paragraph helpLine = new Paragraph("📞 Voter Helpline: 1950");
        helpLine.getStyle().set("color", "rgba(255,255,255,0.6)").set("font-size", "12px").set("margin", "0 0 4px 0");

        Paragraph website = new Paragraph("🌐 eci.gov.in");
        website.getStyle().set("color", "rgba(255,153,51,0.8)").set("font-size", "12px").set("margin", "0");

        drawerFooter.add(helpLine, website);

        VerticalLayout drawerLayout = new VerticalLayout(drawerHeader, nav, drawerFooter);
        drawerLayout.setSizeFull();
        drawerLayout.setPadding(false);
        drawerLayout.setSpacing(false);
        drawerLayout.getStyle()
            .set("background", "linear-gradient(180deg, #0f1b2d 0%, #0a1525 100%)")
            .set("min-height", "100vh");
        drawerLayout.expand(nav);

        addToDrawer(drawerLayout);
    }
}
