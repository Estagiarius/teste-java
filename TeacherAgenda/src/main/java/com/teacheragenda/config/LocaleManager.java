package com.teacheragenda.config;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.ArrayList; // For getSupportedLocales

public class LocaleManager {

    private static final String BUNDLE_BASE_NAME = "com.teacheragenda.i18n.messages";
    private static final String PREF_LANGUAGE_KEY = "user_language";
    private static final String PREF_COUNTRY_KEY = "user_country"; // Optional, but good to have

    private ResourceBundle currentBundle;
    private Locale currentLocale;
    private final Preferences prefs;

    private static LocaleManager instance;

    private LocaleManager() {
        this.prefs = Preferences.userNodeForPackage(LocaleManager.class);
        // Initialization logic will be in init() to be called after instance creation
    }

    public static synchronized LocaleManager getInstance() {
        if (instance == null) {
            instance = new LocaleManager();
            instance.init(); // Initialize after instance creation
        }
        return instance;
    }

    private void init() {
        this.currentLocale = loadPreferredLocale();
        loadBundle(this.currentLocale);
    }

    public void loadBundle(Locale locale) {
        try {
            this.currentBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
            this.currentLocale = locale;
            Locale.setDefault(locale); // Set default locale for JVM, affects some standard Java components
            System.out.println("ResourceBundle loaded for locale: " + locale + ". Bundle found: " + (this.currentBundle != null));
        } catch (MissingResourceException e) {
            System.err.println("ResourceBundle not found for locale: " + locale + ". Falling back to default (English).");
            // Fallback to English if the specified locale's bundle is missing
            Locale fallbackLocale = Locale.ENGLISH;
            if (!locale.equals(fallbackLocale)) { // Avoid infinite loop if English is also missing
                loadBundle(fallbackLocale);
            } else {
                // Critical error: Default bundle is missing. Application might not be usable.
                // For now, set bundle to null and getString will handle it.
                this.currentBundle = null;
                this.currentLocale = fallbackLocale; // Or some indication of error state
                System.err.println("CRITICAL: Default English ResourceBundle is also missing!");
            }
        }
    }

    public String getString(String key) {
        if (currentBundle == null) {
            // This case indicates a critical failure during init (e.g., default bundle missing)
            return "[!NO_BUNDLE_FOR_" + key + "!]";
        }
        try {
            return currentBundle.getString(key);
        } catch (MissingResourceException e) {
            return "[!" + key + "!]"; // Key not found in the current bundle
        } catch (NullPointerException e) {
            // This might happen if currentBundle is null, though loadBundle tries to prevent this.
            return "[!BUNDLE_ERROR_" + key + "!]";
        }
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public List<Locale> getSupportedLocales() {
        // Define the locales your application explicitly supports
        List<Locale> locales = new ArrayList<>();
        locales.add(Locale.ENGLISH); // en
        locales.add(new Locale("pt", "BR")); // pt_BR
        // Add more locales here as you create more messages_xx.properties files
        return locales;
    }

    // Helper to get display names for supported locales
    public String getLocaleDisplayName(Locale locale) {
        return locale.getDisplayName(getCurrentLocale()); // Display name in current UI language
    }

    public String getLocaleDisplayNameInItsOwnLanguage(Locale locale) {
        return locale.getDisplayName(locale); // Display name in its own language
    }


    public void savePreferredLocale(Locale locale) {
        if (locale != null) {
            prefs.put(PREF_LANGUAGE_KEY, locale.getLanguage());
            if (locale.getCountry() != null && !locale.getCountry().isEmpty()) {
                prefs.put(PREF_COUNTRY_KEY, locale.getCountry());
            } else {
                prefs.remove(PREF_COUNTRY_KEY); // Remove if country is not specified
            }
            // Optional: prefs.sync(); // Not usually needed immediately
        }
    }

    public Locale loadPreferredLocale() {
        String lang = prefs.get(PREF_LANGUAGE_KEY, Locale.ENGLISH.getLanguage());
        String country = prefs.get(PREF_COUNTRY_KEY, ""); // Default to empty string for country

        if (country != null && !country.isEmpty()) {
            return new Locale(lang, country);
        } else {
            return new Locale(lang);
        }
    }

    /**
     * Changes the application's current locale and resource bundle.
     * Also saves this preference for future sessions.
     * @param newLocale The new Locale to set.
     */
    public void changeLocale(Locale newLocale) {
        if (newLocale != null && !newLocale.equals(this.currentLocale)) {
            loadBundle(newLocale);
            savePreferredLocale(newLocale);
            // Note: UI refresh needs to be handled separately by notifying listeners or restarting UI parts.
            System.out.println("Locale changed to: " + newLocale);
        }
    }
}
