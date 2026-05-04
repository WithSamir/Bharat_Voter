package com.election.dashboard;

import org.springframework.stereotype.Service;

/**
 * ElectionService – primary chatbot service.
 * Priority: GeminiApiService (REST API with key) → Mock responses.
 *
 * FIX: Removed broken VertexAiGeminiChatModel with wrong model name "gemini-3-flash".
 * Now uses GeminiApiService which calls the Gemini 1.5 Flash REST API with GEMINI_API_KEY.
 */
@Service
public class ElectionService {

    private final GeminiApiService geminiApiService;

    public ElectionService(GeminiApiService geminiApiService) {
        this.geminiApiService = geminiApiService;
    }

    public String processIntent(String userInput) {
        // Try live Gemini API first
        if (geminiApiService.isConfigured()) {
            try {
                String response = geminiApiService.generate(userInput);
                if (response != null && !response.isBlank()) {
                    return response;
                }
            } catch (Exception e) {
                System.err.println("ElectionService: Gemini API call failed, using mock. " + e.getMessage());
            }
        }

        // Fallback to rich mock responses
        return getMockResponse(userInput);
    }

    private String getMockResponse(String userInput) {
        String input = userInput.toLowerCase();

        // Process / steps / how
        if (input.contains("process") || input.contains("step") || input.contains("how does") || input.contains("explain")) {
            return "Here is the complete process of the Indian Election System:\n\n" +
                   "1️⃣ *Voter Registration* — Fill Form 6 online at voters.eci.gov.in to get your Voter ID (EPIC).\n" +
                   "2️⃣ *Election Notification* — The ECI announces election schedule, dates, and model code of conduct (MCC).\n" +
                   "3️⃣ *Candidate Nomination* — Candidates file nominations, pay deposits, and get scrutinized.\n" +
                   "4️⃣ *Campaign Period* — Parties campaign (the MCC prohibits freebies after dates announced). Campaign ends 48 hrs before voting.\n" +
                   "5️⃣ *Voting Day* — Vote at your assigned polling booth using the EVM. VVPAT gives paper confirmation.\n" +
                   "6️⃣ *Counting & Results* — Votes counted, majority wins, and the largest-party/coalition leader forms government!\n\n" +
                   "💡 Bilkul sahi samjha? Type 'quiz' to test your knowledge!";
        }

        // Quiz
        if (input.contains("quiz") || input.contains("test")) {
            return "Awesome! Let's play! 🎯\n\nQ: What is the minimum age to vote in India?\n\n" +
                   "(a) 16 years\n(b) 18 years\n(c) 21 years\n\nReply with a, b, or c.";
        }

        // Quiz answer — b / 18
        if (input.equals("b") || input.equals("(b)") || (input.contains("18") && input.length() < 5)) {
            return "✅ Bilkul Sahi! 18 years is the correct minimum voting age in India.\n\n" +
                   "Next Q: What does VVPAT stand for?\n(a) Voter Verified Paper Audit Trail\n(b) Voting Verification Paper Audit Trail\n(c) Voter Verifiable Paper Audit Trail\nReply a, b, or c!";
        }

        if (input.equals("a") && input.length() < 3) {
            return "❌ Not quite! The answer was B — 18 years. Keep going, you've got this! Bhai, ek baar aur padhna 😄";
        }

        if (input.equals("c") && input.length() < 3) {
            return "✅ Correct! VVPAT stands for Voter Verifiable Paper Audit Trail — it's the paper slip machine attached to the EVM that verifies your vote for 7 seconds!";
        }

        // Form 6
        if (input.contains("form 6") || input.contains("form6") || input.contains("register") || input.contains("registration")) {
            return "📋 *Form 6 — New Voter Registration*\n\n" +
                   "Form 6 is the official form to register as a new voter in India.\n\n" +
                   "📌 *Eligibility:* 18+ years, Indian citizen.\n" +
                   "📌 *Documents needed:* Proof of Age (Aadhaar/Birth Certificate) + Proof of Address.\n" +
                   "📌 *Apply online at:* voters.eci.gov.in → 'New Registration (Form-6)'\n" +
                   "📌 *Offline:* Visit your nearest Electoral Registration Officer (ERO).\n\n" +
                   "Bhai, form bhar de time se — registration deadlines hoti hain! ⏰";
        }

        // Age
        if (input.contains("age") || input.contains("minimum") || input.contains("how old")) {
            if (input.contains("max")) {
                return "There is absolutely NO maximum age limit to vote in India! 🇮🇳\n\n" +
                       "As long as you are 18 or above, an Indian citizen, and your name is on the electoral roll — your right to vote remains intact for life!";
            }
            return "The minimum age to vote in India is **18 years**. 🎂\n\n" +
                   "You must also be:\n• An Indian citizen\n• Registered as a voter in the electoral roll\n• Mentally sound (not disqualified by law)\n\n" +
                   "Age 18 ho gayi? Voter ID banwa lo! 🪪";
        }

        // NRI
        if (input.contains("nri") || input.contains("abroad") || input.contains("overseas")) {
            return "🌍 *NRI Voting Rights*\n\n" +
                   "Yes! Non-Resident Indians (NRIs) CAN vote. Here's how:\n\n" +
                   "1. Fill **Form 6A** to register as an Overseas Elector.\n" +
                   "2. You must be physically present in India on election day to vote at your constituency polling booth.\n" +
                   "3. Proxy voting is NOT currently allowed (debates are ongoing).\n\n" +
                   "Global Indian, desh ke liye vote zaroor karo! 🙌";
        }

        // Documents / ID
        if (input.contains("id") || input.contains("document") || input.contains("proof") || input.contains("aadhaar")) {
            return "🪪 *Valid Documents for Voting*\n\n" +
                   "While a Voter ID (EPIC) is preferred, the ECI also accepts:\n" +
                   "• Aadhaar Card\n• PAN Card\n• Passport\n• Driving License\n• MGNREGA Job Card\n• Bank / Post Office Passbook with photo\n• Service Photo Identity Card (Govt employees)\n\n" +
                   "❗ Condition: Your name MUST be on the electoral roll (voter list). Document only proves identity, not enrollment.";
        }

        // NOTA win
        if (input.contains("nota") && input.contains("win")) {
            return "🗳️ *If NOTA wins...*\n\n" +
                   "If NOTA receives the highest votes, it does NOT trigger a re-election. The candidate with the next-highest vote count is declared the winner.\n\n" +
                   "NOTA is purely symbolic — it lets voters officially express dissatisfaction without losing their right to vote. Think of it as the democratic equivalent of 'none of you!'\n\n" +
                   "The Supreme Court ruled on this clearly in 2023. Judicial system, ekdum solid! ⚖️";
        }

        // NOTA
        if (input.contains("nota")) {
            return "🗳️ *NOTA — None of the Above*\n\n" +
                   "NOTA (नोटा) is an option on the Electronic Voting Machine (EVM) introduced in 2013 by the Supreme Court of India.\n\n" +
                   "It allows voters to officially *reject all* candidates in their constituency — a powerful democratic right!\n\n" +
                   "📌 Introduced: 2013 (after PUCL vs. Union of India case)\n" +
                   "📌 Symbol: A ballot paper with a cross\n" +
                   "📌 Effect: Purely symbolic; does NOT cancel the election.\n\n" +
                   "Yaar, NOTA ekdum powerful statement hai — use it wisely! 💪";
        }

        // VVPAT
        if (input.contains("vvpat")) {
            return "🖨️ *VVPAT — Voter Verifiable Paper Audit Trail*\n\n" +
                   "VVPAT is a printer machine attached to the EVM. After you vote:\n\n" +
                   "1. A paper slip is generated showing the candidate's name and symbol.\n" +
                   "2. The slip is visible for 7 seconds through a glass window.\n" +
                   "3. It then automatically falls into a sealed compartment.\n\n" +
                   "Its purpose is to allow YOU to verify your vote was correctly recorded! It's a transparency mechanism.\n\n" +
                   "The ECI cross-checks a sample of VVPAT slips against EVM counts after election. Transparent, bilkul!";
        }

        // EVM
        if (input.contains("evm")) {
            return "🗳️ *EVM — Electronic Voting Machine*\n\n" +
                   "EVMs are standalone, battery-powered electronic devices used to record votes in Indian elections.\n\n" +
                   "📌 *Units:* Two units — Ballot Unit (voter side) and Control Unit (polling officer side).\n" +
                   "📌 *Security:* EVMs are not connected to the internet. Each unit has a unique serial number.\n" +
                   "📌 *Introduced:* 1982 in Kerala (pilot), nationwide from 1999.\n" +
                   "📌 *Made by:* BEL and ECIL under ECI supervision.\n\n" +
                   "Yaar, EVMs are incredibly secure — stand-alone devices, airtight! 🔒";
        }

        // ECI Headquarters
        if (input.contains("headquarter") || input.contains("office") || input.contains("address of eci") || input.contains("location of eci")) {
            return "🏛️ *Election Commission of India (ECI)*\n\n" +
                   "📍 **Headquarters:** Nirvachan Sadan, Ashoka Road, New Delhi – 110001\n" +
                   "📞 Phone: +91-11-23052205\n" +
                   "🌐 Website: eci.gov.in\n\n" +
                   "The ECI was established on January 25, 1950 — one day before India became a Republic!";
        }

        // Chief Election Commissioner
        if (input.contains("chief election") || input.contains("cec") || input.contains("head of eci") || input.contains("commissioner")) {
            return "👤 *Chief Election Commissioner (CEC)*\n\n" +
                   "The CEC is the head of the Election Commission of India, supported by two Election Commissioners.\n\n" +
                   "📌 Appointed by: The *President of India*\n" +
                   "📌 Term: 6 years or till age 65 (whichever is earlier)\n" +
                   "📌 Removal: Same process as removal of a Supreme Court judge (ensures independence!)\n\n" +
                   "The ECI is a Constitutional body — fully independent from the government. Ekdum neutral! ⚖️";
        }

        // Model Code of Conduct
        if (input.contains("model code") || input.contains("mcc") || input.contains("code of conduct")) {
            return "📜 *Model Code of Conduct (MCC)*\n\n" +
                   "The MCC is a set of guidelines issued by the ECI to regulate political parties and candidates *after* election dates are announced.\n\n" +
                   "Key rules:\n" +
                   "• No use of government resources for campaigning\n" +
                   "• No divisive speeches or hate speech\n" +
                   "• No cash/freebies distribution ('bribing voters')\n" +
                   "• No new policy announcements by the ruling government\n\n" +
                   "Violation can lead to disqualification! MCC hai toh fair election hai! 🤝";
        }

        // Hello / namaste
        if (input.contains("hello") || input.contains("hi") || input.contains("namaste") || input.contains("helo")) {
            return "Namaste! 🙏 Main hoon aapka **Bharat Bot** — Indian Elections ka expert!\n\n" +
                   "Main aapki madad kar sakta hoon:\n" +
                   "🗳️ Voter Registration (Form 6)\n" +
                   "📍 Polling Station dhundhne mein\n" +
                   "📚 EVM, VVPAT, NOTA ke baare mein\n" +
                   "🏛️ ECI aur election process samjhne mein\n" +
                   "🎯 Quiz khelne mein!\n\n" +
                   "Kya poochhna hai aapko? Bolo bhai! 😊";
        }

        // Polling booth
        if (input.contains("polling booth") || input.contains("polling station") || input.contains("where to vote") || input.contains("booth")) {
            return "📍 *Finding Your Polling Booth*\n\n" +
                   "Use the **Polling Station Finder** panel on the right! 👉\n\n" +
                   "Or, to find your booth online:\n" +
                   "1. Go to: **voters.eci.gov.in**\n" +
                   "2. Click 'Know Your Polling Station'\n" +
                   "3. Enter your EPIC number or name\n\n" +
                   "You can also call the ECI Voter Helpline: **1950** (Toll Free)!\n\n" +
                   "Booth pe jaana mat bhoolo on election day! 🗳️";
        }

        // Default response
        return "Namaste! 🙏 Main hoon **Bharat Bot** — your election guide!\n\n" +
               "Aap mujhse pooch sakte hain:\n" +
               "• '*election process*' — poora step by step\n" +
               "• '*voter registration*' or '*Form 6*' — kaise register karein\n" +
               "• '*NOTA*', '*EVM*', '*VVPAT*' — kya hain ye?\n" +
               "• '*NRI voting*' — NRI kaise vote karte hain?\n" +
               "• '*quiz*' — apna knowledge test karo!\n\n" +
               "Kuch bhi poochho — main hoon na! 💪";
    }
}
