package com.election.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * TrackApplicationView – Premium application status tracker.
 * Shows a detailed animated timeline with mock status data.
 */
@Route(value = "track", layout = MainLayout.class)
@PageTitle("Track Application | Bharat Voter Portal")
@PermitAll
public class TrackApplicationView extends VerticalLayout {

    public TrackApplicationView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "linear-gradient(135deg, #0a0f1e 0%, #0f1b2d 100%)")
            .set("min-height", "100vh");

        add(buildHeader(), buildTracker());
    }

    private Div buildHeader() {
        Div header = new Div();
        header.getStyle()
            .set("background", "linear-gradient(135deg, #1a2a4a 0%, #0d1929 100%)")
            .set("padding", "32px 40px")
            .set("border-bottom", "1px solid rgba(255,255,255,0.07)");

        H1 title = new H1("🔍 Track Application Status");
        title.getStyle()
            .set("color", "white")
            .set("margin", "0 0 8px 0")
            .set("font-size", "24px")
            .set("font-weight", "800");

        Paragraph subtitle = new Paragraph(
            "Enter your Reference ID (received after submitting Form 6/7/8) to check your application status."
        );
        subtitle.getStyle()
            .set("color", "rgba(255,255,255,0.55)")
            .set("margin", "0")
            .set("font-size", "14px");

        header.add(title, subtitle);
        return header;
    }

    private VerticalLayout buildTracker() {
        VerticalLayout tracker = new VerticalLayout();
        tracker.getStyle()
            .set("padding", "32px 40px")
            .set("max-width", "750px")
            .set("width", "100%");
        tracker.setAlignSelf(Alignment.CENTER);

        // Search card
        Div searchCard = new Div();
        searchCard.getStyle()
            .set("background", "rgba(255,255,255,0.04)")
            .set("border", "1px solid rgba(255,255,255,0.1)")
            .set("border-radius", "16px")
            .set("padding", "28px 32px");

        H3 searchTitle = new H3("Enter Reference ID");
        searchTitle.getStyle().set("color", "white").set("margin", "0 0 8px 0");

        Paragraph searchHint = new Paragraph("Format: OXX followed by 8 alphanumeric characters (e.g. OXX1A2B3C4D)");
        searchHint.getStyle().set("color", "rgba(255,255,255,0.45)").set("font-size", "12px").set("margin", "0 0 20px 0");

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidthFull();
        searchLayout.setAlignItems(Alignment.BASELINE);

        TextField refField = new TextField();
        refField.setPlaceholder("e.g. OXX1A2B3C4D");
        refField.setWidthFull();
        refField.setClearButtonVisible(true);
        refField.getStyle()
            .set("--vaadin-input-field-background", "rgba(255,255,255,0.08)")
            .set("--vaadin-input-field-border-color", "rgba(255,255,255,0.15)");

        Button trackBtn = new Button("Track Status", VaadinIcon.SEARCH.create());
        trackBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        trackBtn.getStyle()
            .set("background", "linear-gradient(135deg, #FF9933, #FF6B00)")
            .set("color", "white")
            .set("border", "none")
            .set("border-radius", "8px")
            .set("white-space", "nowrap")
            .set("font-weight", "700");

        searchLayout.add(refField, trackBtn);
        searchLayout.setFlexGrow(1, refField);

        searchCard.add(searchTitle, searchHint, searchLayout);

        // Result panel (hidden initially)
        Div resultPanel = new Div();
        resultPanel.setVisible(false);
        resultPanel.getStyle()
            .set("margin-top", "24px");

        trackBtn.addClickListener(e -> {
            if (refField.isEmpty()) {
                refField.setInvalid(true);
                refField.setErrorMessage("Please enter a Reference ID");
                return;
            }
            refField.setInvalid(false);
            String refId = refField.getValue().trim().toUpperCase();
            resultPanel.removeAll();
            resultPanel.add(buildResultCard(refId));
            resultPanel.setVisible(true);
        });

        refField.addKeyPressListener(com.vaadin.flow.component.Key.ENTER,
            e -> trackBtn.click());

        tracker.add(searchCard, resultPanel);
        return tracker;
    }

    private Div buildResultCard(String refId) {
        // Simulate deterministic status based on reference ID hash
        int hash = Math.abs(refId.hashCode());
        int daysSinceSubmission = (hash % 30) + 1;
        boolean bLOVerified = daysSinceSubmission > 5;
        boolean fieldVerified = daysSinceSubmission > 15;
        boolean finalDone = daysSinceSubmission > 25;

        String overallStatus = finalDone ? "Approved" : fieldVerified ? "Under Review" : bLOVerified ? "BLO Verification" : "Submitted";
        String statusColor = finalDone ? "#4CAF50" : fieldVerified ? "#FF9933" : "#2196F3";
        String statusBg = finalDone ? "rgba(76,175,80,0.1)" : fieldVerified ? "rgba(255,153,51,0.1)" : "rgba(33,150,243,0.1)";
        String statusBorder = finalDone ? "rgba(76,175,80,0.3)" : fieldVerified ? "rgba(255,153,51,0.3)" : "rgba(33,150,243,0.3)";

        LocalDate submittedDate = LocalDate.now().minusDays(daysSinceSubmission);

        Div card = new Div();
        card.getStyle()
            .set("background", "rgba(255,255,255,0.04)")
            .set("border", "1px solid rgba(255,255,255,0.1)")
            .set("border-radius", "16px")
            .set("overflow", "hidden");

        // Card header
        Div cardHeader = new Div();
        cardHeader.getStyle()
            .set("background", "rgba(255,255,255,0.04)")
            .set("border-bottom", "1px solid rgba(255,255,255,0.07)")
            .set("padding", "20px 24px")
            .set("display", "flex")
            .set("justify-content", "space-between")
            .set("align-items", "center");

        Div refBlock = new Div();
        Paragraph refLabel = new Paragraph("Reference ID");
        refLabel.getStyle().set("color", "rgba(255,255,255,0.45)").set("font-size", "11px").set("margin", "0");
        Paragraph refValue = new Paragraph(refId);
        refValue.getStyle().set("color", "white").set("font-weight", "700").set("font-size", "18px").set("margin", "2px 0 0 0")
            .set("font-family", "monospace").set("letter-spacing", "1px");
        refBlock.add(refLabel, refValue);

        Div statusBadge = new Div();
        statusBadge.getStyle()
            .set("background", statusBg)
            .set("border", "1px solid " + statusBorder)
            .set("border-radius", "20px")
            .set("padding", "6px 16px")
            .set("color", statusColor)
            .set("font-weight", "700")
            .set("font-size", "14px");
        statusBadge.setText(overallStatus);

        cardHeader.add(refBlock, statusBadge);

        // Card body — timeline
        Div cardBody = new Div();
        cardBody.getStyle().set("padding", "24px");

        Div submittedInfo = new Div();
        submittedInfo.getStyle().set("margin-bottom", "24px");
        Paragraph submittedLabel = new Paragraph("Application submitted on " + submittedDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy")));
        submittedLabel.getStyle().set("color", "rgba(255,255,255,0.5)").set("font-size", "13px").set("margin", "0");
        submittedInfo.add(submittedLabel);

        H3 timelineTitle = new H3("Application Timeline");
        timelineTitle.getStyle().set("color", "rgba(255,255,255,0.8)").set("margin", "0 0 20px 0").set("font-size", "15px");

        Div timeline = new Div();
        timeline.getStyle().set("position", "relative");

        timeline.add(buildStep("Application Submitted", "Form 6 received by the Electoral Registration Officer", true,
            submittedDate.format(DateTimeFormatter.ofPattern("d MMM yyyy"))));
        timeline.add(buildStep("BLO Verification", "Booth Level Officer verification of documents", bLOVerified,
            bLOVerified ? submittedDate.plusDays(5).format(DateTimeFormatter.ofPattern("d MMM yyyy")) : "Pending"));
        timeline.add(buildStep("Field Verification", "Physical verification at your registered address", fieldVerified,
            fieldVerified ? submittedDate.plusDays(15).format(DateTimeFormatter.ofPattern("d MMM yyyy")) : "Pending"));
        timeline.add(buildStep("Final Decision", finalDone ? "✅ Voter ID Approved & Dispatched" : "Awaiting review by ERO", finalDone,
            finalDone ? submittedDate.plusDays(25).format(DateTimeFormatter.ofPattern("d MMM yyyy")) : "Pending"));

        cardBody.add(submittedInfo, timelineTitle, timeline);

        if (finalDone) {
            Div approvedBanner = new Div();
            approvedBanner.getStyle()
                .set("background", "rgba(76,175,80,0.12)")
                .set("border", "1px solid rgba(76,175,80,0.35)")
                .set("border-radius", "10px")
                .set("padding", "16px 20px")
                .set("margin-top", "20px");
            Paragraph approvedText = new Paragraph(
                "🎉 Congratulations! Your Voter ID (EPIC) has been approved and dispatched. " +
                "Collect it from your nearest ERO office or it will be delivered to your registered address."
            );
            approvedText.getStyle().set("color", "#A5D6A7").set("margin", "0").set("font-size", "13px");
            approvedBanner.add(approvedText);
            cardBody.add(approvedBanner);
        }

        card.add(cardHeader, cardBody);
        return card;
    }

    private Div buildStep(String title, String description, boolean completed, String date) {
        Div step = new Div();
        step.getStyle()
            .set("display", "flex")
            .set("gap", "16px")
            .set("margin-bottom", "20px")
            .set("position", "relative");

        // Step icon
        Div icon = new Div();
        icon.getStyle()
            .set("width", "36px")
            .set("height", "36px")
            .set("border-radius", "50%")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("font-size", "16px")
            .set("flex-shrink", "0")
            .set("z-index", "1")
            .set("background", completed ? "linear-gradient(135deg, #138808, #1CAA0D)" : "rgba(255,255,255,0.1)")
            .set("border", completed ? "none" : "2px solid rgba(255,255,255,0.2)");
        icon.setText(completed ? "✓" : "○");
        icon.getStyle().set("color", completed ? "white" : "rgba(255,255,255,0.4)");

        // Step text
        Div textBlock = new Div();
        H4 stepTitle = new H4(title);
        stepTitle.getStyle()
            .set("color", completed ? "white" : "rgba(255,255,255,0.4)")
            .set("margin", "0 0 3px 0")
            .set("font-size", "14px")
            .set("font-weight", "600");

        Paragraph stepDesc = new Paragraph(description);
        stepDesc.getStyle()
            .set("color", "rgba(255,255,255,0.4)")
            .set("font-size", "12px")
            .set("margin", "0 0 3px 0");

        Paragraph stepDate = new Paragraph(date);
        stepDate.getStyle()
            .set("color", completed ? "rgba(76,175,80,0.8)" : "rgba(255,255,255,0.25)")
            .set("font-size", "11px")
            .set("font-weight", "600")
            .set("margin", "0");

        textBlock.add(stepTitle, stepDesc, stepDate);
        step.add(icon, textBlock);
        return step;
    }
}
