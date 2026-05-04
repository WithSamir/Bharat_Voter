package com.election.dashboard;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * LoginView – Premium dark-themed secure login page.
 * Displays the tricolour branding and ECI identity.
 */
@Route("login")
@PageTitle("Secure Login | Bharat Voter Portal")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle()
            .set("background", "linear-gradient(135deg, #0a0f1e 0%, #0f1b2d 50%, #1a1f3a 100%)")
            .set("min-height", "100vh");

        // Tricolour accent bar
        Div accentBar = new Div();
        accentBar.getStyle()
            .set("width", "260px")
            .set("height", "5px")
            .set("background", "linear-gradient(90deg, #FF9933 33.3%, #FFFFFF 33.3% 66.6%, #138808 66.6%)")
            .set("border-radius", "3px")
            .set("margin-bottom", "24px");

        // Logo / emblem
        Div emblem = new Div();
        emblem.setText("🇮🇳");
        emblem.getStyle()
            .set("font-size", "60px")
            .set("text-align", "center")
            .set("margin-bottom", "8px");

        H1 title = new H1("Bharat Voter Portal");
        title.getStyle()
            .set("color", "white")
            .set("margin", "0 0 4px 0")
            .set("font-size", "28px")
            .set("font-weight", "800")
            .set("text-align", "center")
            .set("letter-spacing", "-0.5px");

        Paragraph subtitle = new Paragraph("Election Commission of India · Digital Services");
        subtitle.getStyle()
            .set("color", "rgba(255,255,255,0.55)")
            .set("margin", "0 0 32px 0")
            .set("font-size", "14px")
            .set("text-align", "center");

        // Login form card
        Div card = new Div();
        card.getStyle()
            .set("background", "rgba(255,255,255,0.05)")
            .set("border", "1px solid rgba(255,255,255,0.1)")
            .set("border-radius", "16px")
            .set("padding", "32px 36px")
            .set("backdrop-filter", "blur(10px)")
            .set("width", "min(400px, 90vw)")
            .set("box-shadow", "0 25px 50px rgba(0,0,0,0.5)");

        H2 loginTitle = new H2("Secure Sign In");
        loginTitle.getStyle()
            .set("color", "white")
            .set("margin", "0 0 4px 0")
            .set("font-size", "20px")
            .set("font-weight", "700");

        Paragraph loginSubtitle = new Paragraph("Access your election dashboard securely");
        loginSubtitle.getStyle()
            .set("color", "rgba(255,255,255,0.5)")
            .set("margin", "0 0 20px 0")
            .set("font-size", "13px");

        // Demo credentials hint
        Div hint = new Div();
        hint.getStyle()
            .set("background", "rgba(255,153,51,0.12)")
            .set("border", "1px solid rgba(255,153,51,0.3)")
            .set("border-radius", "8px")
            .set("padding", "10px 14px")
            .set("margin-bottom", "20px");

        Paragraph hintText = new Paragraph("💡 Demo Login — username: voter · password: voter123");
        hintText.getStyle()
            .set("color", "#FFB74D")
            .set("font-size", "12px")
            .set("margin", "0");

        hint.add(hintText);

        login.setAction("login");
        login.getStyle().set("width", "100%");

        card.add(loginTitle, loginSubtitle, hint, login);

        // Footer
        Paragraph footer = new Paragraph("🔒 Secure · Encrypted · Verified by ECI");
        footer.getStyle()
            .set("color", "rgba(255,255,255,0.3)")
            .set("font-size", "12px")
            .set("text-align", "center")
            .set("margin-top", "24px");

        add(accentBar, emblem, title, subtitle, card, footer);
        setAlignSelf(Alignment.CENTER, accentBar, emblem, title, subtitle, card, footer);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
}
