package com.election.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.stereotype.Service;

@Service
public class MultilingualRouterService {

    private final Translate translate = TranslateOptions.getDefaultInstance().getService();

    public String translateToEnglish(String text) {
        Translation translation = translate.translate(
            text,
            Translate.TranslateOption.targetLanguage("en")
        );
        return translation.getTranslatedText();
    }
    
    public String translateResponse(String text, String targetLanguage) {
        Translation translation = translate.translate(
            text,
            Translate.TranslateOption.targetLanguage(targetLanguage)
        );
        return translation.getTranslatedText();
    }
}
