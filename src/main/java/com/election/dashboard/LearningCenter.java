package com.election.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class LearningCenter extends VerticalLayout {

    public LearningCenter() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "#F8FAFC");

        H2 header = new H2("📚 Indian Election System - Learning Center");
        header.addClassNames(LumoUtility.FontSize.XXLARGE, LumoUtility.TextColor.PRIMARY);

        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setSizeFull();
        contentLayout.setSpacing(true);

        VerticalLayout flashcards = createFlashcardsSection();
        VerticalLayout quiz = createQuizSection();

        contentLayout.add(flashcards, quiz);
        contentLayout.setFlexGrow(1, flashcards);
        contentLayout.setFlexGrow(1, quiz);

        add(header, contentLayout);
    }

    private VerticalLayout createFlashcardsSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)")
            .set("padding", "24px");

        H3 title = new H3("Flashcards");
        title.addClassNames(LumoUtility.Margin.Top.NONE);

        VerticalLayout card = new VerticalLayout();
        card.getStyle()
            .set("background", "#EFF6FF")
            .set("border-radius", "8px")
            .set("padding", "32px")
            .set("cursor", "pointer")
            .set("min-height", "200px")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("text-align", "center")
            .set("transition", "transform 0.3s");

        H3 cardText = new H3("What is the minimum voting age in India?");
        Paragraph cardAnswer = new Paragraph("18 Years");
        cardAnswer.setVisible(false);
        cardAnswer.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.SUCCESS);

        card.add(cardText, cardAnswer);
        
        card.addClickListener(e -> {
            boolean isShowingAnswer = cardAnswer.isVisible();
            cardText.setVisible(isShowingAnswer);
            cardAnswer.setVisible(!isShowingAnswer);
            if (!isShowingAnswer) {
                card.getStyle().set("background", "#F0FDF4");
            } else {
                card.getStyle().set("background", "#EFF6FF");
            }
        });

        HorizontalLayout controls = new HorizontalLayout();
        Button prev = new Button("Previous", VaadinIcon.ARROW_LEFT.create());
        Button next = new Button("Next", VaadinIcon.ARROW_RIGHT.create());
        next.setIconAfterText(true);
        controls.add(prev, next);
        controls.setWidthFull();
        controls.setJustifyContentMode(JustifyContentMode.BETWEEN);

        // Simple mock behavior for next button
        next.addClickListener(e -> {
            cardText.setText("What does EVM stand for?");
            cardAnswer.setText("Electronic Voting Machine");
            cardText.setVisible(true);
            cardAnswer.setVisible(false);
            card.getStyle().set("background", "#EFF6FF");
        });
        
        prev.addClickListener(e -> {
            cardText.setText("What is the minimum voting age in India?");
            cardAnswer.setText("18 Years");
            cardText.setVisible(true);
            cardAnswer.setVisible(false);
            card.getStyle().set("background", "#EFF6FF");
        });

        layout.add(title, new Paragraph("Click the card to flip!"), card, controls);
        return layout;
    }

    private int currentQuizIndex = 0;
    private final String[] questions = {
        "Who conducts the Lok Sabha elections in India?",
        "What is the maximum number of members in the Lok Sabha?",
        "EVM stands for?",
        "Who appoints the Chief Election Commissioner of India?"
    };
    private final String[][] options = {
        {"State Election Commission", "Election Commission of India", "President of India", "Parliament"},
        {"545", "550", "552", "500"},
        {"Electronic Voting Machine", "Election Voting Machine", "Electric Voter Machine", "Electronic Verification Machine"},
        {"Prime Minister", "Chief Justice of India", "President of India", "Parliament"}
    };
    private final int[] correctAnswers = {1, 2, 0, 2}; // Index of the correct option (0-based)

    private VerticalLayout createQuizSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)")
            .set("padding", "24px");

        H3 title = new H3("Quick Quiz");
        title.addClassNames(LumoUtility.Margin.Top.NONE);

        Paragraph question = new Paragraph();
        question.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD);

        VerticalLayout optionsLayout = new VerticalLayout();
        Button opt1 = new Button();
        Button opt2 = new Button();
        Button opt3 = new Button();
        Button opt4 = new Button();

        optionsLayout.add(opt1, opt2, opt3, opt4);
        Button[] optButtons = {opt1, opt2, opt3, opt4};
        
        for (Button btn : optButtons) {
            btn.setWidthFull();
            btn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        }

        Paragraph feedback = new Paragraph();
        feedback.setVisible(false);

        Button nextQuestionBtn = new Button("Next Question", VaadinIcon.ARROW_RIGHT.create());
        nextQuestionBtn.setIconAfterText(true);
        nextQuestionBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        nextQuestionBtn.setVisible(false);
        nextQuestionBtn.setWidthFull();

        Runnable loadQuestion = () -> {
            question.setText(questions[currentQuizIndex]);
            for (int i = 0; i < 4; i++) {
                optButtons[i].setText(options[currentQuizIndex][i]);
                optButtons[i].removeThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_ERROR);
                optButtons[i].addThemeVariants(ButtonVariant.LUMO_CONTRAST);
                optButtons[i].setEnabled(true);
            }
            feedback.setVisible(false);
            nextQuestionBtn.setVisible(false);
        };

        for (int i = 0; i < 4; i++) {
            int finalI = i;
            optButtons[i].addClickListener(e -> {
                for (Button b : optButtons) b.setEnabled(false); // disable all
                if (finalI == correctAnswers[currentQuizIndex]) {
                    feedback.setText("Correct! Brilliant job.");
                    feedback.setClassName(LumoUtility.TextColor.SUCCESS);
                    optButtons[finalI].addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                } else {
                    feedback.setText("Incorrect. The correct answer was: " + options[currentQuizIndex][correctAnswers[currentQuizIndex]]);
                    feedback.setClassName(LumoUtility.TextColor.ERROR);
                    optButtons[finalI].addThemeVariants(ButtonVariant.LUMO_ERROR);
                    optButtons[correctAnswers[currentQuizIndex]].addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                }
                feedback.setVisible(true);
                
                if (currentQuizIndex < questions.length - 1) {
                    nextQuestionBtn.setVisible(true);
                } else {
                    feedback.setText(feedback.getText() + " You've completed all the quizzes!");
                }
            });
        }

        nextQuestionBtn.addClickListener(e -> {
            currentQuizIndex++;
            loadQuestion.run();
        });

        loadQuestion.run(); // Load the first question

        layout.add(title, question, optionsLayout, feedback, nextQuestionBtn);
        return layout;
    }
}
