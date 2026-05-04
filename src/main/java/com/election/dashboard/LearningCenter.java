package com.election.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * LearningCenter – Interactive flashcards and quiz section.
 * Expanded to 10 flashcards and 6 quiz questions with full state management.
 */
public class LearningCenter extends VerticalLayout {

    // ── Flashcard Data ────────────────────────────────────────────────────────
    private static final String[][] FLASHCARDS = {
        {"What is the minimum voting age in India?", "18 Years"},
        {"What does EVM stand for?", "Electronic Voting Machine"},
        {"What does VVPAT stand for?", "Voter Verifiable Paper Audit Trail"},
        {"What does NOTA stand for?", "None of the Above"},
        {"Where is the ECI headquartered?", "Nirvachan Sadan, Ashoka Road, New Delhi"},
        {"Who appoints the Chief Election Commissioner?", "The President of India"},
        {"What is Form 6 used for?", "New Voter Registration (Electoral Roll)"},
        {"Which year was EVM first used nationwide?", "1999 General Elections"},
        {"What is the toll-free voter helpline number?", "1950"},
        {"What is the Model Code of Conduct (MCC)?", "Guidelines issued by ECI after election dates are announced to ensure fair elections"}
    };

    // ── Quiz Data ─────────────────────────────────────────────────────────────
    private static final String[] QUESTIONS = {
        "Who conducts Lok Sabha elections in India?",
        "What is the maximum number of elected members in the Lok Sabha?",
        "EVM stands for?",
        "Who appoints the Chief Election Commissioner?",
        "What is the minimum age to vote in India?",
        "NOTA was introduced in India in which year?"
    };
    private static final String[][] OPTIONS = {
        {"State Election Commission", "Election Commission of India", "President of India", "Parliament"},
        {"543", "545", "552", "500"},
        {"Electronic Voting Machine", "Election Voting Machine", "Electric Voter Machine", "Electronic Verification Machine"},
        {"Prime Minister", "Chief Justice of India", "President of India", "Parliament"},
        {"16 years", "18 years", "21 years", "25 years"},
        {"2009", "2013", "2015", "2019"}
    };
    private static final int[] CORRECT = {1, 0, 0, 2, 1, 1};

    // ── State ─────────────────────────────────────────────────────────────────
    private int flashcardIndex = 0;
    private boolean showingAnswer = false;
    private int quizIndex = 0;
    private int score = 0;
    private boolean quizCompleted = false;

    public LearningCenter() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "rgba(0,0,0,0.2)")
            .set("border-top", "1px solid rgba(255,255,255,0.07)");

        HorizontalLayout content = new HorizontalLayout();
        content.setSizeFull();
        content.setSpacing(false);

        VerticalLayout flashcardsPanel = createFlashcardsPanel();
        VerticalLayout quizPanel = createQuizPanel();

        flashcardsPanel.getStyle().set("border-right", "1px solid rgba(255,255,255,0.07)");

        content.add(flashcardsPanel, quizPanel);
        content.setFlexGrow(1, flashcardsPanel);
        content.setFlexGrow(1, quizPanel);

        // Section header
        Div header = new Div();
        header.getStyle()
            .set("background", "rgba(255,255,255,0.03)")
            .set("border-bottom", "1px solid rgba(255,255,255,0.07)")
            .set("padding", "12px 20px")
            .set("display", "flex")
            .set("align-items", "center")
            .set("gap", "12px");

        Span headerIcon = new Span("📚");
        headerIcon.getStyle().set("font-size", "18px");
        H3 headerTitle = new H3("Learning Center");
        headerTitle.getStyle()
            .set("color", "rgba(255,255,255,0.8)")
            .set("margin", "0")
            .set("font-size", "14px")
            .set("font-weight", "600");
        Span badge = new Span(FLASHCARDS.length + " Cards · " + QUESTIONS.length + " Questions");
        badge.getStyle()
            .set("background", "rgba(255,153,51,0.15)")
            .set("color", "#FFB74D")
            .set("border", "1px solid rgba(255,153,51,0.3)")
            .set("border-radius", "12px")
            .set("font-size", "10px")
            .set("padding", "2px 10px");

        header.add(headerIcon, headerTitle, badge);

        add(header, content);
        expand(content);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  FLASHCARDS
    // ─────────────────────────────────────────────────────────────────────────

    private VerticalLayout createFlashcardsPanel() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.getStyle().set("padding", "16px");

        H4 title = new H4("⚡ Flashcards");
        title.getStyle().set("color", "#FFB74D").set("margin", "0 0 8px 0").set("font-size", "13px");

        // Progress indicator
        Paragraph progress = new Paragraph();
        progress.getStyle().set("color", "rgba(255,255,255,0.4)").set("font-size", "11px").set("margin", "0 0 8px 0");

        // Card
        Div card = new Div();
        card.getStyle()
            .set("background", "linear-gradient(135deg, rgba(255,153,51,0.12), rgba(19,136,8,0.08))")
            .set("border", "1px solid rgba(255,153,51,0.25)")
            .set("border-radius", "12px")
            .set("padding", "20px")
            .set("cursor", "pointer")
            .set("min-height", "120px")
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("text-align", "center")
            .set("transition", "all 0.3s ease")
            .set("flex-grow", "1");

        Paragraph cardText = new Paragraph(FLASHCARDS[0][0]);
        cardText.getStyle()
            .set("color", "rgba(255,255,255,0.9)")
            .set("font-size", "14px")
            .set("margin", "0")
            .set("font-weight", "600");

        Paragraph cardAnswer = new Paragraph(FLASHCARDS[0][1]);
        cardAnswer.getStyle()
            .set("color", "#4CAF50")
            .set("font-size", "18px")
            .set("font-weight", "800")
            .set("margin", "0");
        cardAnswer.setVisible(false);

        Paragraph tapHint = new Paragraph("👆 Tap to reveal answer");
        tapHint.getStyle()
            .set("color", "rgba(255,153,51,0.6)")
            .set("font-size", "11px")
            .set("margin", "8px 0 0 0");

        card.add(cardText, cardAnswer, tapHint);

        // Update progress display
        Runnable updateProgress = () -> {
            progress.setText("Card " + (flashcardIndex + 1) + " of " + FLASHCARDS.length);
        };
        updateProgress.run();

        card.addClickListener(e -> {
            showingAnswer = !showingAnswer;
            cardText.setVisible(!showingAnswer);
            cardAnswer.setVisible(showingAnswer);
            tapHint.setVisible(!showingAnswer);
            if (showingAnswer) {
                card.getStyle()
                    .set("background", "linear-gradient(135deg, rgba(76,175,80,0.15), rgba(19,136,8,0.12))")
                    .set("border-color", "rgba(76,175,80,0.35)");
            } else {
                card.getStyle()
                    .set("background", "linear-gradient(135deg, rgba(255,153,51,0.12), rgba(19,136,8,0.08))")
                    .set("border-color", "rgba(255,153,51,0.25)");
            }
        });

        // Controls
        HorizontalLayout controls = new HorizontalLayout();
        controls.setWidthFull();
        controls.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Button prev = new Button(VaadinIcon.ARROW_LEFT.create());
        prev.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        prev.getStyle().set("color", "rgba(255,255,255,0.6)");

        Button next = new Button(VaadinIcon.ARROW_RIGHT.create());
        next.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        next.setIconAfterText(true);
        next.getStyle().set("color", "rgba(255,255,255,0.6)");

        prev.addClickListener(e -> {
            if (flashcardIndex > 0) {
                flashcardIndex--;
                showingAnswer = false;
                cardText.setText(FLASHCARDS[flashcardIndex][0]);
                cardAnswer.setText(FLASHCARDS[flashcardIndex][1]);
                cardText.setVisible(true);
                cardAnswer.setVisible(false);
                tapHint.setVisible(true);
                card.getStyle()
                    .set("background", "linear-gradient(135deg, rgba(255,153,51,0.12), rgba(19,136,8,0.08))")
                    .set("border-color", "rgba(255,153,51,0.25)");
                updateProgress.run();
            }
        });

        next.addClickListener(e -> {
            if (flashcardIndex < FLASHCARDS.length - 1) {
                flashcardIndex++;
                showingAnswer = false;
                cardText.setText(FLASHCARDS[flashcardIndex][0]);
                cardAnswer.setText(FLASHCARDS[flashcardIndex][1]);
                cardText.setVisible(true);
                cardAnswer.setVisible(false);
                tapHint.setVisible(true);
                card.getStyle()
                    .set("background", "linear-gradient(135deg, rgba(255,153,51,0.12), rgba(19,136,8,0.08))")
                    .set("border-color", "rgba(255,153,51,0.25)");
                updateProgress.run();
            }
        });

        controls.add(prev, next);

        layout.add(title, progress, card, controls);
        layout.expand(card);

        return layout;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  QUIZ
    // ─────────────────────────────────────────────────────────────────────────

    private VerticalLayout createQuizPanel() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(true);
        layout.setSpacing(false);
        layout.getStyle().set("padding", "16px");

        H4 title = new H4("🎯 Quick Quiz");
        title.getStyle().set("color", "#4DB6AC").set("margin", "0 0 8px 0").set("font-size", "13px");

        Paragraph questionCounter = new Paragraph();
        questionCounter.getStyle().set("color", "rgba(255,255,255,0.4)").set("font-size", "11px").set("margin", "0 0 8px 0");

        Paragraph questionText = new Paragraph();
        questionText.getStyle()
            .set("color", "rgba(255,255,255,0.9)")
            .set("font-size", "13px")
            .set("font-weight", "600")
            .set("margin", "0 0 12px 0")
            .set("line-height", "1.5");

        VerticalLayout optionsLayout = new VerticalLayout();
        optionsLayout.setPadding(false);
        optionsLayout.setSpacing(false);
        optionsLayout.getStyle().set("gap", "6px");

        Button[] optButtons = new Button[4];
        for (int i = 0; i < 4; i++) {
            optButtons[i] = new Button();
            optButtons[i].setWidthFull();
            optButtons[i].addThemeVariants(ButtonVariant.LUMO_SMALL);
            optButtons[i].getStyle()
                .set("background", "rgba(255,255,255,0.05)")
                .set("border", "1px solid rgba(255,255,255,0.12)")
                .set("color", "rgba(255,255,255,0.8)")
                .set("border-radius", "8px")
                .set("text-align", "left")
                .set("justify-content", "flex-start")
                .set("padding", "8px 12px")
                .set("font-size", "12px");
            optionsLayout.add(optButtons[i]);
        }

        Paragraph feedback = new Paragraph();
        feedback.getStyle()
            .set("font-size", "12px")
            .set("font-weight", "600")
            .set("margin", "8px 0 0 0")
            .set("min-height", "18px");

        Paragraph scoreDisplay = new Paragraph();
        scoreDisplay.getStyle()
            .set("color", "#FFB74D")
            .set("font-size", "12px")
            .set("margin", "4px 0 0 0");

        Button nextBtn = new Button("Next Question →");
        nextBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        nextBtn.getStyle()
            .set("background", "linear-gradient(135deg, #4DB6AC, #26A69A)")
            .set("color", "white")
            .set("border", "none")
            .set("margin-top", "8px")
            .set("width", "100%");
        nextBtn.setVisible(false);

        Button restartBtn = new Button("🔄 Restart Quiz");
        restartBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        restartBtn.getStyle().set("color", "rgba(255,255,255,0.5)").set("margin-top", "4px").set("width", "100%");
        restartBtn.setVisible(false);

        // Load question logic
        Runnable[] loadQ = new Runnable[1];
        loadQ[0] = () -> {
            if (quizIndex >= QUESTIONS.length) {
                // Quiz done
                questionText.setText("🎉 Quiz Complete! You scored " + score + "/" + QUESTIONS.length + "!");
                questionText.getStyle().set("color", "#4CAF50");
                questionCounter.setText("Final Score");
                optionsLayout.setVisible(false);
                feedback.setVisible(false);
                nextBtn.setVisible(false);
                restartBtn.setVisible(true);
                scoreDisplay.setText("Score: " + score + " / " + QUESTIONS.length +
                    (score == QUESTIONS.length ? " 🏆 Perfect!" : score >= 4 ? " 🌟 Great job!" : " 📚 Keep learning!"));
                return;
            }

            questionCounter.setText("Q" + (quizIndex + 1) + " of " + QUESTIONS.length + " · Score: " + score);
            questionText.setText(QUESTIONS[quizIndex]);
            questionText.getStyle().set("color", "rgba(255,255,255,0.9)");
            optionsLayout.setVisible(true);
            feedback.setText("");
            nextBtn.setVisible(false);

            for (int i = 0; i < 4; i++) {
                optButtons[i].setText(OPTIONS[quizIndex][i]);
                optButtons[i].setEnabled(true);
                optButtons[i].getStyle()
                    .set("background", "rgba(255,255,255,0.05)")
                    .set("border", "1px solid rgba(255,255,255,0.12)")
                    .set("color", "rgba(255,255,255,0.8)");
            }
        };

        for (int i = 0; i < 4; i++) {
            int idx = i;
            optButtons[i].addClickListener(e -> {
                for (Button b : optButtons) b.setEnabled(false);
                if (idx == CORRECT[quizIndex]) {
                    score++;
                    optButtons[idx].getStyle()
                        .set("background", "rgba(76,175,80,0.2)")
                        .set("border-color", "rgba(76,175,80,0.6)")
                        .set("color", "#4CAF50");
                    feedback.setText("✅ Correct! Bilkul sahi jawab!");
                    feedback.getStyle().set("color", "#4CAF50");
                } else {
                    optButtons[idx].getStyle()
                        .set("background", "rgba(244,67,54,0.2)")
                        .set("border-color", "rgba(244,67,54,0.6)")
                        .set("color", "#EF5350");
                    optButtons[CORRECT[quizIndex]].getStyle()
                        .set("background", "rgba(76,175,80,0.2)")
                        .set("border-color", "rgba(76,175,80,0.6)")
                        .set("color", "#4CAF50");
                    feedback.setText("❌ Incorrect. Correct: " + OPTIONS[quizIndex][CORRECT[quizIndex]]);
                    feedback.getStyle().set("color", "#EF5350");
                }
                questionCounter.setText("Q" + (quizIndex + 1) + " of " + QUESTIONS.length + " · Score: " + score);
                nextBtn.setVisible(true);
            });
        }

        nextBtn.addClickListener(e -> {
            quizIndex++;
            loadQ[0].run();
        });

        restartBtn.addClickListener(e -> {
            quizIndex = 0;
            score = 0;
            restartBtn.setVisible(false);
            optionsLayout.setVisible(true);
            feedback.setVisible(true);
            scoreDisplay.setText("");
            loadQ[0].run();
        });

        loadQ[0].run();

        layout.add(title, questionCounter, questionText, optionsLayout, feedback, scoreDisplay, nextBtn, restartBtn);
        return layout;
    }
}
