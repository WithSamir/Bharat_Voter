package com.election.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.util.UUID;

/**
 * VoterRegistrationView – Premium Form 6 voter registration page.
 * Includes improved validation, state dropdown, and UX enhancements.
 */
@Route(value = "register", layout = MainLayout.class)
@PageTitle("Form 6 — New Voter Registration | Bharat Voter Portal")
@PermitAll
public class VoterRegistrationView extends VerticalLayout {

    private static final String[] INDIAN_STATES = {
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
        "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka",
        "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram",
        "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu",
        "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal",
        "Delhi (NCT)", "Jammu & Kashmir", "Ladakh", "Puducherry", "Chandigarh"
    };

    public VoterRegistrationView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "linear-gradient(135deg, #0a0f1e 0%, #0f1b2d 100%)")
            .set("min-height", "100vh")
            .set("overflow-y", "auto");

        add(buildHeader(), buildForm());
    }

    private Div buildHeader() {
        Div header = new Div();
        header.getStyle()
            .set("background", "linear-gradient(135deg, #FF9933 0%, #FF6B00 60%, #138808 100%)")
            .set("padding", "32px 40px")
            .set("position", "relative")
            .set("overflow", "hidden");

        // Decorative circle
        Div circle = new Div();
        circle.getStyle()
            .set("position", "absolute")
            .set("right", "-60px")
            .set("top", "-60px")
            .set("width", "220px")
            .set("height", "220px")
            .set("border-radius", "50%")
            .set("background", "rgba(255,255,255,0.08)");

        H1 title = new H1("📋 Form 6 — Voter Registration");
        title.getStyle()
            .set("color", "white")
            .set("margin", "0 0 8px 0")
            .set("font-size", "26px")
            .set("font-weight", "800")
            .set("position", "relative");

        Paragraph subtitle = new Paragraph(
            "Register as a new voter in the electoral roll of your constituency. " +
            "This is the official Form 6 as prescribed by the Election Commission of India."
        );
        subtitle.getStyle()
            .set("color", "rgba(255,255,255,0.85)")
            .set("margin", "0")
            .set("font-size", "14px")
            .set("max-width", "600px")
            .set("position", "relative");

        // Steps indicator
        HorizontalLayout steps = new HorizontalLayout();
        steps.getStyle().set("margin-top", "20px").set("gap", "8px");
        String[] stepLabels = {"1. Personal Info", "2. Address", "3. Documents", "4. Submit"};
        for (int i = 0; i < stepLabels.length; i++) {
            Div step = new Div();
            step.setText(stepLabels[i]);
            step.getStyle()
                .set("background", i == 0 ? "rgba(255,255,255,0.9)" : "rgba(255,255,255,0.25)")
                .set("color", i == 0 ? "#FF6B00" : "rgba(255,255,255,0.8)")
                .set("border-radius", "20px")
                .set("padding", "4px 14px")
                .set("font-size", "12px")
                .set("font-weight", i == 0 ? "700" : "400");
            steps.add(step);
        }

        header.add(circle, title, subtitle, steps);
        return header;
    }

    private Div buildForm() {
        Div wrapper = new Div();
        wrapper.getStyle()
            .set("padding", "32px 40px")
            .set("max-width", "800px")
            .set("width", "100%")
            .set("margin", "0 auto");

        // Info alert
        Div infoBox = new Div();
        infoBox.getStyle()
            .set("background", "rgba(66,133,244,0.1)")
            .set("border", "1px solid rgba(66,133,244,0.3)")
            .set("border-radius", "10px")
            .set("padding", "14px 18px")
            .set("margin-bottom", "28px");
        Paragraph infoText = new Paragraph(
            "ℹ️ Eligibility: Indian citizen, 18+ years of age, not disqualified by law. " +
            "Documents required: Proof of Age + Proof of Address. " +
            "Voter Helpline: 1950 (Toll Free)."
        );
        infoText.getStyle().set("color", "#90CAF9").set("margin", "0").set("font-size", "13px");
        infoBox.add(infoText);

        FormLayout form = new FormLayout();
        form.getStyle().set("gap", "4px");

        // Fields
        TextField fullName = new TextField("Full Name *");
        fullName.setPlaceholder("As per Aadhaar Card");
        fullName.setRequiredIndicatorVisible(true);
        styleFormField(fullName);

        TextField fatherName = new TextField("Father's / Husband's Name");
        fatherName.setPlaceholder("Full name");
        styleFormField(fatherName);

        DatePicker dob = new DatePicker("Date of Birth *");
        dob.setRequiredIndicatorVisible(true);
        dob.setMax(LocalDate.now().minusYears(18));
        dob.setPlaceholder("DD/MM/YYYY");
        styleFormField(dob);

        TextField mobile = new TextField("Mobile Number");
        mobile.setPlaceholder("10-digit number");
        mobile.setPattern("\\d{10}");
        mobile.setMaxLength(10);
        styleFormField(mobile);

        TextField email = new TextField("Email Address");
        email.setPlaceholder("example@email.com");
        styleFormField(email);

        TextField addressLine1 = new TextField("Address Line 1 *");
        addressLine1.setPlaceholder("House/Flat No., Street Name");
        addressLine1.setRequiredIndicatorVisible(true);
        styleFormField(addressLine1);

        TextField addressLine2 = new TextField("Address Line 2");
        addressLine2.setPlaceholder("Area / Village / Town");
        styleFormField(addressLine2);

        ComboBox<String> state = new ComboBox<>("State / UT *");
        state.setItems(INDIAN_STATES);
        state.setPlaceholder("Select your state");
        state.setRequiredIndicatorVisible(true);
        styleFormField(state);

        TextField district = new TextField("District");
        district.setPlaceholder("e.g. Gautam Buddh Nagar");
        styleFormField(district);

        TextField constituency = new TextField("Assembly Constituency");
        constituency.setPlaceholder("e.g. Noida, Varanasi");
        styleFormField(constituency);

        TextField pinCode = new TextField("PIN Code *");
        pinCode.setPlaceholder("6-digit PIN code");
        pinCode.setPattern("\\d{6}");
        pinCode.setMaxLength(6);
        pinCode.setRequiredIndicatorVisible(true);
        styleFormField(pinCode);

        // Upload
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setDropLabel(new Paragraph("📎 Drop Aadhaar Card / Proof of Identity here"));
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "application/pdf");
        upload.setMaxFiles(1);
        upload.getStyle()
            .set("background", "rgba(255,255,255,0.04)")
            .set("border", "2px dashed rgba(255,153,51,0.4)")
            .set("border-radius", "10px");

        form.add(fullName, fatherName, dob, mobile, email, addressLine1, addressLine2, state, district, constituency, pinCode, upload);
        form.setColspan(addressLine1, 2);
        form.setColspan(addressLine2, 2);
        form.setColspan(upload, 2);

        // Declaration
        Div declaration = new Div();
        declaration.getStyle()
            .set("background", "rgba(19,136,8,0.08)")
            .set("border", "1px solid rgba(19,136,8,0.3)")
            .set("border-radius", "10px")
            .set("padding", "14px 18px")
            .set("margin-top", "20px");
        Paragraph declarationText = new Paragraph(
            "✅ Declaration: I hereby declare that the information provided above is true to the best of my knowledge " +
            "and belief. I am an Indian citizen and have completed 18 years of age on the qualifying date. " +
            "I am not disqualified from being a voter under any law."
        );
        declarationText.getStyle().set("color", "#A5D6A7").set("margin", "0").set("font-size", "13px");
        declaration.add(declarationText);

        // Submit button
        Button submitBtn = new Button("🗳️ Submit Form 6 Application");
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        submitBtn.getStyle()
            .set("background", "linear-gradient(135deg, #138808, #1CAA0D)")
            .set("color", "white")
            .set("border", "none")
            .set("border-radius", "10px")
            .set("width", "100%")
            .set("margin-top", "20px")
            .set("height", "50px")
            .set("font-size", "16px")
            .set("font-weight", "700");

        submitBtn.addClickListener(e -> {
            if (fullName.isEmpty() || dob.isEmpty() || state.isEmpty() || addressLine1.isEmpty() || pinCode.isEmpty()) {
                Notification error = Notification.show("⚠️ Please fill all required (*) fields before submitting.");
                error.addThemeVariants(NotificationVariant.LUMO_ERROR);
                error.setDuration(4000);
                return;
            }

            String referenceId = "OXX" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            Div successContent = new Div();
            successContent.getStyle().set("padding", "8px");
            Paragraph successMsg = new Paragraph(
                "✅ Application Submitted Successfully!\n" +
                "Reference ID: " + referenceId + "\n" +
                "Please note this ID to track your application status."
            );
            successMsg.getStyle().set("margin", "0").set("line-height", "1.6");

            Notification success = Notification.show(
                "✅ Submitted! Reference ID: " + referenceId + " (Valid for 6 months)"
            );
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            success.setDuration(12000);

            // Clear form
            fullName.clear(); fatherName.clear(); dob.clear();
            mobile.clear(); email.clear(); addressLine1.clear();
            addressLine2.clear(); state.clear(); district.clear();
            constituency.clear(); pinCode.clear(); upload.clearFileList();
        });

        wrapper.add(infoBox, form, declaration, submitBtn);
        return wrapper;
    }

    private void styleFormField(com.vaadin.flow.component.Component field) {
        field.getElement().getStyle()
            .set("--vaadin-input-field-background", "rgba(255,255,255,0.06)")
            .set("--vaadin-input-field-border-color", "rgba(255,255,255,0.15)")
            .set("color", "white");
    }
}
