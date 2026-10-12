package com.maree.app

import org.junit.Assert.assertEquals
import org.junit.Test

class AppLanguageTest {
    @Test fun savedChoiceOverridesPhoneLanguage() {
        assertEquals(AppLocale.FRENCH, preferredAppLocale("fr", "en"))
        assertEquals(AppLocale.ENGLISH, preferredAppLocale("en", "fr"))
    }

    @Test fun firstLaunchFollowsSupportedPhoneLanguage() {
        assertEquals(AppLocale.ENGLISH, preferredAppLocale(null, "en"))
        assertEquals(AppLocale.ENGLISH, preferredAppLocale(null, "EN-US"))
        assertEquals(AppLocale.FRENCH, preferredAppLocale(null, "fr"))
        assertEquals(AppLocale.FRENCH, preferredAppLocale(null, "de"))
    }

    @Test fun unknownSavedChoiceFallsBackToPhoneLanguage() {
        assertEquals(AppLocale.ENGLISH, preferredAppLocale("unknown", "en"))
        assertEquals(AppLocale.FRENCH, preferredAppLocale("unknown", "fr"))
    }
}
