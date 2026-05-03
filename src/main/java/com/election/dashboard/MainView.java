package com.election.dashboard;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Route("")
@UIScope
public class MainView extends AppLayout {

    private final ElectionService electionService;
    private final List<MessageListItem> messages = new ArrayList<>();
    private final MessageList messageList = new MessageList();

    public MainView(@Value("${google.maps.api-key:dummy_key}") String apiKey, ElectionService electionService) {
        this.electionService = electionService;

        // --- Header Setup ---
        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("🗳️ Bharat Voter Dashboard");
        title.addClassNames(LumoUtility.FontSize.XLARGE);
        addToNavbar(toggle, title);
        
        // --- Sidebar ---
        VerticalLayout sidebar = new VerticalLayout();
        sidebar.add(new Span("Services Portal"));
        sidebar.add(VaadinIcon.CHAT.create(), new Span("AI Assistant"));
        sidebar.add(VaadinIcon.MAP_MARKER.create(), new Span("Polling Booths"));
        addToDrawer(sidebar);

        // --- Main Content Area ---
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        // 1. Chat Assistant Layout (Left)
        VerticalLayout chatLayout = new VerticalLayout();
        chatLayout.setSizeFull();
        chatLayout.setPadding(true);
        chatLayout.getStyle().set("background", "#F8FAFC");

        H2 chatHeader = new H2("🤖 Election Assistant");
        chatHeader.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextColor.PRIMARY);
        
        messageList.setSizeFull();
        MessageListItem initialMsg = new MessageListItem(
            "Namaste! Main aapki Election Assistant hu. Aap voter registration ya kisi election process ke baare me kuch bhi pooch sakte hain.",
            Instant.now(), "Assistant");
        initialMsg.setUserColorIndex(1);
        messages.add(initialMsg);
        messageList.setItems(messages);

        MessageInput messageInput = new MessageInput();
        messageInput.setWidthFull();
        messageInput.addSubmitListener(this::handleChatSubmit);

        chatLayout.add(chatHeader, messageList, messageInput);
        
        // 2. Map Layout (Right)
        VerticalLayout mapLayout = new VerticalLayout();
        mapLayout.setSizeFull();
        mapLayout.setPadding(true);
        
        H2 mapHeader = new H2("📍 Polling Station Finder");
        mapHeader.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextColor.SUCCESS);

        PollingStationFinder mapComponent = new PollingStationFinder(apiKey);
        mapLayout.add(mapHeader, mapComponent);

        // Optionally, call renderPollingStations(28.4744, 77.5040) dynamically when needed.
        // For demonstration, render default coordinates after initialization:
        UI.getCurrent().getPage().executeJs("setTimeout(() => $0.$server.renderPollingStations(28.4744, 77.5040), 1500)", mapComponent.getElement());

        // 3. Learning Center (Bottom Right)
        LearningCenter learningCenter = new LearningCenter();
        
        VerticalLayout rightLayout = new VerticalLayout();
        rightLayout.setSizeFull();
        rightLayout.setPadding(false);
        rightLayout.add(mapLayout, learningCenter);
        rightLayout.setFlexGrow(1, mapLayout);
        rightLayout.setFlexGrow(1, learningCenter);

        splitLayout.addToPrimary(chatLayout);
        splitLayout.addToSecondary(rightLayout);
        splitLayout.setSplitterPosition(40);

        setContent(splitLayout);
    }

    private void handleChatSubmit(MessageInput.SubmitEvent event) {
        String text = event.getValue();
        
        // Add user message
        MessageListItem userMsg = new MessageListItem(text, Instant.now(), "You");
        userMsg.setUserColorIndex(2);
        messages.add(userMsg);
        messageList.setItems(messages);
        
        // Get AI Response
        String aiResponse = electionService.processIntent(text);
        
        // Add AI message
        MessageListItem aiMsg = new MessageListItem(aiResponse, Instant.now(), "Assistant");
        aiMsg.setUserColorIndex(1);
        messages.add(aiMsg);
        messageList.setItems(messages);
    }
}
