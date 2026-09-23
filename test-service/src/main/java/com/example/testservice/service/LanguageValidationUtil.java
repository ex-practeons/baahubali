package com.example.testservice.service;

import com.example.testservice.exception.ValidationException;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LanguageValidationUtil {

    public static void validateTranslationsMap(Map<String, String> translations, List<String> requiredLanguages) {
        if (requiredLanguages == null || requiredLanguages.isEmpty()) {
            return;
        }
        if (translations == null) {
            throw new ValidationException("Translations map cannot be null. Required languages: " + requiredLanguages);
        }
        for (String reqLang : requiredLanguages) {
            if (!translations.containsKey(reqLang) || translations.get(reqLang).trim().isEmpty()) {
                throw new ValidationException("Missing required language translation: " + reqLang);
            }
        }
    }

    public static void validateTranslationsSet(Set<String> providedLanguages, List<String> requiredLanguages) {
        if (requiredLanguages == null || requiredLanguages.isEmpty()) {
            return;
        }
        for (String reqLang : requiredLanguages) {
            if (!providedLanguages.contains(reqLang)) {
                throw new ValidationException("Missing required language translation: " + reqLang);
            }
        }
    }

    public static String resolveLanguage(String requestedLang, List<String> requiredLanguages) {
        if (requestedLang != null && !requestedLang.trim().isEmpty()) {
            return requestedLang;
        }
        if (requiredLanguages != null && !requiredLanguages.isEmpty()) {
            return requiredLanguages.get(0);
        }
        return "EN";
    }
}
