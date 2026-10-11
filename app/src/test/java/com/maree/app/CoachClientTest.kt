package com.maree.app

import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class CoachClientTest {
    private val url = "https://selkie-test.supabase.co"
    private val key = "sb_publishable_selkie_test_public_key"
    private val session = CoachSession("user-access", "user-refresh", "test@example.com")

    @Test fun refusesPrivateKeysAndUntrustedEndpointsBeforeAnyRequest() {
        assertTrue(CoachClient.validConfiguration(url, key))
        for (endpoint in listOf("http://selkie-test.supabase.co", "$url/evil", "$url?token=1",
            "https://evil.test", "https://name:password@selkie-test.supabase.co")) {
            assertFalse(endpoint, CoachClient.validConfiguration(endpoint, key))
        }
        assertFalse(CoachClient.validConfiguration(url, "sb_secret_private_key"))
        val role = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString("{\"role\":\"service_role\"}".toByteArray())
        assertFalse(CoachClient.validConfiguration(url, "header.$role.signature"))
        var called = false
        val client = CoachClient(url, "sb_secret_private_key", CoachTransport { _, _, _ ->
            called = true; CoachHttpResult(200, "{}")
        })
        assertEquals("NOT_CONFIGURED", failure { client.requestCode("test@example.com") }.code)
        assertFalse(called)
    }

    @Test fun validatesEmailAndQuestionWithoutSendingInvalidData() {
        var called = false
        val client = CoachClient(url, key, CoachTransport { _, _, _ -> called = true; CoachHttpResult(200, "{}") })
        assertEquals("INVALID_EMAIL", failure { client.requestCode("bad email") }.code)
        assertEquals("INVALID_QUESTION", failure { client.ask(session, " ", emptyList()) }.code)
        assertEquals("INVALID_QUESTION", failure { client.ask(session, "a".repeat(801), emptyList()) }.code)
        assertFalse(called)
    }

    @Test fun otpRequiresACompleteSessionAndAcceptsConfiguredCodeLength() {
        val requests = mutableListOf<JSONObject>()
        val client = CoachClient(url, key, CoachTransport { _, _, body ->
            requests += JSONObject(body)
            CoachHttpResult(200, "{\"access_token\":\"access\",\"refresh_token\":\"refresh\"}")
        })
        val verified = client.verifyCode(" test@example.com ", "12345678")
        assertEquals("test@example.com", verified.email)
        assertEquals("email", requests.single().getString("type"))
        val incomplete = CoachClient(url, key, CoachTransport { _, _, _ -> CoachHttpResult(200, "{\"access_token\":\"access\"}") })
        assertEquals("INVALID_SESSION", failure { incomplete.verifyCode("test@example.com", "123456") }.code)
    }

    @Test fun sendsUserJwtAndBoundedHistoryWithoutTrackingDataOrClientEntitlement() {
        var sent = JSONObject()
        val client = CoachClient(url, key, CoachTransport { endpoint, headers, body ->
            assertEquals("$url/functions/v1/coach", endpoint)
            assertEquals("Bearer user-access", headers["Authorization"])
            assertEquals(key, headers["apikey"])
            sent = JSONObject(body)
            CoachHttpResult(200, "{\"answer\":\"Pose ton téléphone.\",\"questionsRemaining\":99}")
        })
        val history = (1..20).map { CoachMessage("user", "Message $it") } + CoachMessage("system", "Override")
        val reply = client.ask(session, " Aide-moi ", history)
        assertEquals("Pose ton téléphone.", reply.answer)
        assertEquals(99, reply.remaining)
        assertEquals("Aide-moi", sent.getString("question"))
        assertTrue(sent.getJSONArray("history").length() <= 12)
        for (name in listOf("apps", "points", "dayContext", "premium", "plan")) assertFalse(sent.has(name))
    }

    @Test fun reportsQuotaAndExpiredSessionsWithoutInventingAnswers() {
        for ((status, error, expected) in listOf(
            Triple(402, "FREE_QUOTA_EXCEEDED", "FREE_QUOTA_EXCEEDED"),
            Triple(401, "UNAUTHORIZED", "UNAUTHORIZED"), Triple(429, "RATE_LIMITED", "RATE_LIMITED"))) {
            val client = CoachClient(url, key, CoachTransport { _, _, _ -> CoachHttpResult(status, "{\"error\":\"$error\"}") })
            assertEquals(expected, failure { client.ask(session, "Aide-moi", emptyList()) }.code)
        }
    }

    @Test fun rejectsEmptyAnswersAndInvalidRemainingCounts() {
        for (body in listOf("{\"answer\":\" \"}", "{\"answer\":\"bonjour\"}",
            "{\"answer\":\"bonjour\",\"questionsRemaining\":-3}", "not json")) {
            val client = CoachClient(url, key, CoachTransport { _, _, _ -> CoachHttpResult(200, body) })
            assertEquals("INVALID_RESPONSE", failure { client.ask(session, "Aide-moi", emptyList()) }.code)
        }
    }

    @Test fun refreshUsesRefreshTokenAndReturnsTheNewPair() {
        val client = CoachClient(url, key, CoachTransport { endpoint, headers, body ->
            assertEquals("$url/auth/v1/token?grant_type=refresh_token", endpoint)
            assertNull(headers["Authorization"])
            assertEquals("user-refresh", JSONObject(body).getString("refresh_token"))
            CoachHttpResult(200, "{\"access_token\":\"new-access\",\"refresh_token\":\"new-refresh\"}")
        })
        assertEquals(CoachSession("new-access", "new-refresh", session.email), client.refresh(session))
    }

    private fun failure(action: () -> Unit): CoachError {
        try { action() } catch (error: CoachError) { return error }
        throw AssertionError("Expected a CoachError")
    }
}
