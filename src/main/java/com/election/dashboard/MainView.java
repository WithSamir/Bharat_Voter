package com.election.dashboard;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | Bharat Voter Portal")
@PermitAll
@UIScope
public class MainView extends SplitLayout {

    private final ElectionService electionService;
    private final List<MessageListItem> messages = new ArrayList<>();
    private final MessageList messageList = new MessageList();
    private Paragraph typingIndicator;

    public MainView(
            @Value("${google.maps.api.key:}") String apiKey,
            ElectionService electionService) {

        this.electionService = electionService;
        setSizeFull();
        getStyle().set("background", "var(--lumo-base-color)");

        // ── LEFT: Chat Assistant ──────────────────────────────────────────────
        VerticalLayout chatLayout = buildChatLayout();

        // ── RIGHT: Map + Learning Center ─────────────────────────────────────
        VerticalLayout rightLayout = buildRightLayout(apiKey);

        addToPrimary(chatLayout);
        addToSecondary(rightLayout);
        setSplitterPosition(42);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  CHAT PANEL
    // ─────────────────────────────────────────────────────────────────────────

    private VerticalLayout buildChatLayout() {
        VerticalLayout chatLayout = new VerticalLayout();
        chatLayout.setSizeFull();
        chatLayout.setPadding(false);
        chatLayout.setSpacing(false);
        chatLayout.getStyle()
            .set("background", "linear-gradient(135deg, #0f1b2d 0%, #1a2f4a 100%)")
            .set("border-right", "1px solid rgba(255,255,255,0.08)");

        // Header
        Div chatHeader = buildChatHeader();

        // Quick-action chips
        HorizontalLayout quickActions = buildQuickActions();

        // Message list
        messageList.setSizeFull();
        messageList.getStyle()
            .set("padding", "0 16px")
            .set("flex-grow", "1");

        // Add welcome message
        MessageListItem welcomeMsg = new MessageListItem(
            "Namaste! 🙏 Main hoon Bharat Bot — aapka election guide!\n\n" +
            "Aap mujhse pooch sakte hain voter registration, polling booths, EVM, VVPAT, NOTA, ya election process ke baare mein.\n\n" +
            "Ya phir 'quiz' type karke apna knowledge test karo! 🎯",
            Instant.now(), "🤖 Bharat Bot");
        welcomeMsg.setUserColorIndex(1);
        messages.add(welcomeMsg);
        messageList.setItems(messages);

        // Typing indicator
        typingIndicator = new Paragraph("Bharat Bot is typing...");
        typingIndicator.getStyle()
            .set("color", "rgba(255,255,255,0.5)")
            .set("font-size", "12px")
            .set("padding", "4px 20px")
            .set("font-style", "italic")
            .set("animation", "pulse 1.5s infinite");
        typingIndicator.setVisible(false);

        // Input
        MessageInput messageInput = new MessageInput();
        messageInput.setWidthFull();
        messageInput.getStyle()
            .set("border-top", "1px solid rgba(255,255,255,0.1)")
            .set("background", "rgba(0,0,0,0.3)");
        messageInput.addSubmitListener(this::handleChatSubmit);

        chatLayout.add(chatHeader, quickActions, messageList, typingIndicator, messageInput);
        chatLayout.expand(messageList);

        return chatLayout;
    }

    private Div buildChatHeader() {
        Div header = new Div();
        header.getStyle()
            .set("background", "linear-gradient(90deg, #FF9933 0%, #FF6B00 50%, #138808 100%)")
            .set("padding", "16px 20px")
            .set("display", "flex")
            .set("align-items", "center")
            .set("gap", "12px")
            .set("flex-shrink", "0");

        Div avatar = new Div();
        avatar.setText("🤖");
        avatar.getStyle()
            .set("font-size", "28px")
            .set("background", "rgba(255,255,255,0.2)")
            .set("border-radius", "50%")
            .set("width", "48px")
            .set("height", "48px")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center");

        Div textBlock = new Div();
        H3 name = new H3("Bharat Bot");
        name.getStyle().set("color", "white").set("margin", "0").set("font-size", "18px");
        Paragraph status = new Paragraph("🟢 Online · Indian Elections Expert");
        status.getStyle().set("color", "rgba(255,255,255,0.85)").set("margin", "2px 0 0 0").set("font-size", "12px");
        textBlock.add(name, status);

        header.add(avatar, textBlock);
        return header;
    }

    private HorizontalLayout buildQuickActions() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.getStyle()
            .set("padding", "10px 16px 6px 16px")
            .set("gap", "8px")
            .set("flex-wrap", "wrap")
            .set("background", "rgba(0,0,0,0.2)")
            .set("flex-shrink", "0");

        String[][] chips = {
            {"📋 Register", "How do I register to vote? What is Form 6?"},
            {"🗳️ NOTA", "What is NOTA? How does it work?"},
            {"📍 Find Booth", "How do I find my polling booth?"},
            {"🎯 Quiz", "quiz"},
            {"🇮🇳 Process", "Explain the complete Indian election process step by step"}
        };

        for (String[] chip : chips) {
            Button btn = new Button(chip[0]);
            btn.getStyle()
                .set("background", "rgba(255,153,51,0.15)")
                .set("color", "#FFB74D")
                .set("border", "1px solid rgba(255,153,51,0.4)")
                .set("border-radius", "20px")
                .set("font-size", "12px")
                .set("padding", "4px 12px")
                .set("cursor", "pointer")
                .set("white-space", "nowrap");
            btn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            final String query = chip[1];
            btn.addClickListener(e -> sendChatMessage(query));
            layout.add(btn);
        }

        return layout;
    }

    private void handleChatSubmit(MessageInput.SubmitEvent event) {
        sendChatMessage(event.getValue());
    }

    private void sendChatMessage(String text) {
        if (text == null || text.isBlank()) return;

        // Add user message
        MessageListItem userMsg = new MessageListItem(text, Instant.now(), "You");
        userMsg.setUserColorIndex(2);
        messages.add(userMsg);
        messageList.setItems(messages);

        // Show typing indicator
        typingIndicator.setVisible(true);

        // Run AI response asynchronously
        UI ui = UI.getCurrent();
        CompletableFuture.supplyAsync(() -> electionService.processIntent(text))
            .thenAcceptAsync(aiResponse -> {
                ui.access(() -> {
                    typingIndicator.setVisible(false);
                    MessageListItem aiMsg = new MessageListItem(aiResponse, Instant.now(), "🤖 Bharat Bot");
                    aiMsg.setUserColorIndex(1);
                    messages.add(aiMsg);
                    messageList.setItems(messages);
                });
            });
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  RIGHT PANEL: MAP + LEARNING CENTER
    // ─────────────────────────────────────────────────────────────────────────

    private VerticalLayout buildRightLayout(String apiKey) {
        VerticalLayout rightLayout = new VerticalLayout();
        rightLayout.setSizeFull();
        rightLayout.setPadding(false);
        rightLayout.setSpacing(false);
        rightLayout.getStyle().set("background", "#0d1929");

        // Map section
        VerticalLayout mapSection = buildMapSection(apiKey);

        // Learning center
        LearningCenter learningCenter = new LearningCenter();

        rightLayout.add(mapSection, learningCenter);
        rightLayout.setFlexGrow(1, mapSection);
        rightLayout.setFlexGrow(1, learningCenter);

        return rightLayout;
    }

    private VerticalLayout buildMapSection(String apiKey) {
        VerticalLayout mapLayout = new VerticalLayout();
        mapLayout.setPadding(false);
        mapLayout.setSpacing(false);
        mapLayout.getStyle()
            .set("background", "#0d1929")
            .set("border-bottom", "1px solid rgba(255,255,255,0.08)");

        // Map header
        Div mapHeader = new Div();
        mapHeader.getStyle()
            .set("padding", "14px 20px 10px 20px")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "space-between")
            .set("background", "rgba(255,255,255,0.04)")
            .set("border-bottom", "1px solid rgba(255,255,255,0.06)");

        H3 mapTitle = new H3("📍 Polling Station Finder");
        mapTitle.getStyle()
            .set("color", "#4DB6AC")
            .set("margin", "0")
            .set("font-size", "16px");

        Span mapBadge = new Span(apiKey != null && !apiKey.isBlank() ? "Google Maps" : "OpenStreetMap");
        mapBadge.getStyle()
            .set("background", apiKey != null && !apiKey.isBlank() ? "rgba(66,133,244,0.2)" : "rgba(77,182,172,0.2)")
            .set("color", apiKey != null && !apiKey.isBlank() ? "#4285F4" : "#4DB6AC")
            .set("border", "1px solid " + (apiKey != null && !apiKey.isBlank() ? "rgba(66,133,244,0.4)" : "rgba(77,182,172,0.4)"))
            .set("border-radius", "12px")
            .set("font-size", "11px")
            .set("padding", "2px 10px");

        mapHeader.add(mapTitle, mapBadge);

        // Search bar
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidthFull();
        searchLayout.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.BASELINE);
        searchLayout.getStyle().set("padding", "10px 16px");

        TextField searchBox = new TextField();
        searchBox.setPlaceholder("Enter PIN code, city or locality (e.g. 201310, Noida)...");
        searchBox.setWidthFull();
        searchBox.getStyle()
            .set("--vaadin-input-field-background", "rgba(255,255,255,0.08)")
            .set("--vaadin-input-field-border-color", "rgba(255,255,255,0.15)")
            .set("color", "white");

        Button searchBtn = new Button("Find Booth");
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchBtn.getStyle()
            .set("background", "linear-gradient(135deg, #FF9933, #FF6B00)")
            .set("color", "white")
            .set("border", "none")
            .set("border-radius", "8px")
            .set("white-space", "nowrap");

        searchLayout.add(searchBox, searchBtn);
        searchLayout.setFlexGrow(1, searchBox);

        // Map component
        PollingStationFinder mapComponent = new PollingStationFinder(apiKey);

        // Wire up search
        searchBtn.addClickListener(e -> {
            if (!searchBox.isEmpty()) {
                String input = searchBox.getValue().trim();

                double baseLat, baseLng;
                if (input.matches("\\d{6}")) {
                    // Indian PIN geocoding using postal zone prefix (first 2 digits = state zone)
                    int pin = Integer.parseInt(input);
                    int zone = pin / 10000; // First 2 digits
                    switch (zone) {
                        case 11: baseLat = 28.65; baseLng = 77.23; break; // Delhi
                        case 12: case 13: baseLat = 30.70; baseLng = 76.72; break; // Punjab/Haryana
                        case 14: case 15: baseLat = 31.10; baseLng = 77.17; break; // Himachal
                        case 16: baseLat = 30.73; baseLng = 76.78; break; // Chandigarh/Punjab
                        case 17: baseLat = 32.72; baseLng = 74.85; break; // J&K
                        case 20: case 21: baseLat = 26.85; baseLng = 80.91; break; // UP
                        case 22: case 23: baseLat = 25.45; baseLng = 81.85; break; // UP East
                        case 24: case 25: baseLat = 27.18; baseLng = 78.01; break; // UP West
                        case 26: case 27: baseLat = 29.95; baseLng = 77.56; break; // Uttarakhand
                        case 30: case 31: baseLat = 26.91; baseLng = 75.79; break; // Rajasthan
                        case 36: case 38: baseLat = 23.03; baseLng = 72.58; break; // Gujarat
                        case 40: case 41: case 42: case 43: baseLat = 19.07; baseLng = 72.87; break; // Maharashtra
                        case 44: case 45: baseLat = 21.14; baseLng = 79.09; break; // Nagpur/Vidarbha
                        case 46: case 47: case 48: baseLat = 21.25; baseLng = 81.62; break; // Chhattisgarh
                        case 49: baseLat = 22.71; baseLng = 82.15; break; // Chhattisgarh E
                        case 50: case 51: baseLat = 17.38; baseLng = 78.48; break; // Telangana
                        case 52: case 53: baseLat = 16.51; baseLng = 80.62; break; // AP
                        case 56: case 57: case 58: case 59: baseLat = 12.97; baseLng = 77.59; break; // Karnataka
                        case 60: case 61: case 62: case 63: case 64: baseLat = 13.08; baseLng = 80.27; break; // Tamil Nadu
                        case 67: case 68: case 69: baseLat = 10.52; baseLng = 76.21; break; // Kerala
                        case 70: case 71: case 72: case 74: baseLat = 22.57; baseLng = 88.36; break; // West Bengal
                        case 75: case 76: baseLat = 20.46; baseLng = 85.87; break; // Odisha
                        case 80: case 81: case 82: case 84: case 85: baseLat = 25.59; baseLng = 85.13; break; // Bihar
                        case 83: baseLat = 23.35; baseLng = 85.33; break; // Jharkhand
                        default:
                            // Generic: use last 4 digits as offset from India centre
                            baseLat = 20.59 + ((pin % 1000) - 500) * 0.008;
                            baseLng = 78.96 + ((pin % 2000) - 1000) * 0.005;
                    }
                    // Add small offset from last 3 digits so different PINs in same zone differ
                    baseLat += ((pin % 100) - 50) * 0.005;
                    baseLng += ((pin % 200) - 100) * 0.004;
                } else {
                    // City name — simple lookup for common cities
                    String city = input.toLowerCase();
                    if (city.contains("delhi") || city.contains("new delhi")) { baseLat = 28.65; baseLng = 77.23; }
                    else if (city.contains("mumbai") || city.contains("bombay")) { baseLat = 19.07; baseLng = 72.87; }
                    else if (city.contains("bengaluru") || city.contains("bangalore")) { baseLat = 12.97; baseLng = 77.59; }
                    else if (city.contains("hyderabad")) { baseLat = 17.38; baseLng = 78.48; }
                    else if (city.contains("chennai") || city.contains("madras")) { baseLat = 13.08; baseLng = 80.27; }
                    else if (city.contains("kolkata") || city.contains("calcutta")) { baseLat = 22.57; baseLng = 88.36; }
                    else if (city.contains("pune")) { baseLat = 18.52; baseLng = 73.85; }
                    else if (city.contains("ahmedabad")) { baseLat = 23.03; baseLng = 72.58; }
                    else if (city.contains("jaipur")) { baseLat = 26.91; baseLng = 75.79; }
                    else if (city.contains("lucknow")) { baseLat = 26.85; baseLng = 80.91; }
                    else if (city.contains("noida")) { baseLat = 28.54; baseLng = 77.39; }
                    else if (city.contains("gurgaon") || city.contains("gurugram")) { baseLat = 28.46; baseLng = 77.03; }
                    else if (city.contains("varanasi")) { baseLat = 25.32; baseLng = 82.97; }
                    else if (city.contains("agra")) { baseLat = 27.18; baseLng = 78.01; }
                    else if (city.contains("surat")) { baseLat = 21.17; baseLng = 72.83; }
                    else if (city.contains("kanpur")) { baseLat = 26.44; baseLng = 80.33; }
                    else {
                        // Generic hash for unknown city names
                        baseLat = 20.59 + (input.hashCode() % 1000) * 0.005;
                        baseLng = 78.96 + (new StringBuilder(input).reverse().toString().hashCode() % 800) * 0.005;
                    }
                }

                Notification notif = Notification.show("🔍 Finding polling booths near \"" + input + "\"...");
                notif.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                mapComponent.renderPollingStations(baseLat, baseLng);
            } else {
                searchBox.setInvalid(true);
                searchBox.setErrorMessage("Please enter a PIN code or city name");
            }
        });

        // Allow pressing Enter in the search box
        searchBox.addKeyPressListener(com.vaadin.flow.component.Key.ENTER,
            e -> searchBtn.click());

        mapLayout.add(mapHeader, searchLayout, mapComponent);
        mapLayout.expand(mapComponent);

        // Initialize with Delhi NCR
        mapComponent.renderPollingStations(28.4744, 77.5040);

        return mapLayout;
    }
}
