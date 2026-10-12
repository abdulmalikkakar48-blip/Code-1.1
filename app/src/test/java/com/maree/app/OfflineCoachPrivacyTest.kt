package com.maree.app

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OfflineCoachPrivacyTest {
    @Test fun englishPrivateOrDistressMessagesAreExcludedFromOptionalHistory() {
        for (message in listOf("My name is Alex", "My address is 12 Example Street",
            "My password is secret", "Here is my credit card number", "I take medication",
            "I want to die", "I am pregnant", "Here is my API key")) {
            assertTrue(message, OfflineCoachHistory.sensitive(message))
        }
    }

    @Test fun frenchPrivateMessagesAndContactDetailsRemainExcluded() {
        for (message in listOf("Mon mot de passe est secret", "Je veux me faire du mal",
            "Contact me at person@example.com", "My number is +33 6 12 34 56 78")) {
            assertTrue(message, OfflineCoachHistory.sensitive(message))
        }
    }

    @Test fun ordinaryScreenTimeQuestionsRemainEligibleWhenUserOptsIn() {
        for (message in listOf("Why do I keep scrolling?", "Can Selkie help me focus?",
            "Pourquoi je regarde toujours mon téléphone ?", "Une idée pour moins scroller ?")) {
            assertFalse(message, OfflineCoachHistory.sensitive(message))
        }
    }
}
