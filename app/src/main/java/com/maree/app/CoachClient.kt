package com.maree.app

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

internal data class CoachMessage(val role: String, val text: String)
internal data class CoachSession(val accessToken: String, val refreshToken: String, val email: String)
internal data class CoachReply(val answer: String, val remaining: Int)
internal data class CoachHttpResult(val status: Int, val body: String)
internal class CoachError(val code: String, message: String) : Exception(message)

internal fun interface CoachTransport {
    fun post(url: String, headers: Map<String, String>, body: String): CoachHttpResult
}

/** HTTPS uniquement ; aucun journal de messages, d'adresses ou de jetons. */
internal object CoachHttpsTransport : CoachTransport {
    override fun post(url: String, headers: Map<String, String>, body: String): CoachHttpResult {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.instanceFollowRedirects = false
            connection.connectTimeout = 15_000
            connection.readTimeout = 30_000
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            headers.forEach { (key, value) -> connection.setRequestProperty(key, value) }
            connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val bytes = stream?.use { input ->
                val output = java.io.ByteArrayOutputStream()
                val buffer = ByteArray(4096)
                while (true) {
                    val count = input.read(buffer)
                    if (count < 0) break
                    if (output.size() + count > 65_536) throw CoachError("INVALID_RESPONSE", tr("La réponse du serveur est trop longue.", "The server response is too long."))
                    output.write(buffer, 0, count)
                }
                output.toByteArray()
            } ?: byteArrayOf()
            return CoachHttpResult(status, bytes.toString(Charsets.UTF_8))
        } finally {
            connection.disconnect()
        }
    }
}

internal class CoachClient(
    private val baseUrl: String,
    private val publicKey: String,
    private val transport: CoachTransport = CoachHttpsTransport,
    private val provider: String = "openai"
) {
    val configured: Boolean get() = validConfiguration(baseUrl, publicKey, provider)

    fun requestCode(email: String) {
        val address = checkedEmail(email)
        call("/auth/v1/otp", null, JSONObject().put("email", address).put("create_user", true))
    }

    fun verifyCode(email: String, code: String): CoachSession {
        val address = checkedEmail(email)
        if (!code.matches(Regex("[0-9]{4,10}"))) throw CoachError("INVALID_CODE", tr("Recopie le code reçu par e-mail.", "Enter the code you received by email."))
        return session(call("/auth/v1/verify", null, JSONObject()
            .put("email", address).put("token", code).put("type", "email")), address)
    }

    fun refresh(session: CoachSession): CoachSession = session(
        call("/auth/v1/token?grant_type=refresh_token", null,
            JSONObject().put("refresh_token", session.refreshToken)), session.email)

    fun ask(session: CoachSession, question: String, history: List<CoachMessage>): CoachReply {
        val text = question.trim()
        if (text.isEmpty() || text.length > 800) throw CoachError("INVALID_QUESTION", tr("Écris une question de 1 à 800 caractères.", "Write a question between 1 and 800 characters long."))
        val previous = JSONArray()
        history.takeLast(12).forEach {
            if (it.role in listOf("user", "assistant") && it.text.isNotBlank() && it.text.length <= 2000)
                previous.put(JSONObject().put("role", it.role).put("text", it.text))
        }
        val result = call("/functions/v1/coach", session.accessToken,
            JSONObject().put("question", text).put("history", previous))
        val answer = result.optString("answer").trim()
        if (answer.isEmpty() || answer.length > 4000 || !result.has("questionsRemaining"))
            throw CoachError("INVALID_RESPONSE", tr("Le coach n’a pas renvoyé de réponse valide. Réessaie plus tard.", "The coach did not return a valid answer. Try again later."))
        val remaining = result.optInt("questionsRemaining", Int.MIN_VALUE)
        if (remaining < -1) throw CoachError("INVALID_RESPONSE", tr("La réponse du serveur est invalide.", "The server response is invalid."))
        return CoachReply(answer, remaining)
    }

    private fun call(path: String, token: String?, input: JSONObject): JSONObject {
        if (!configured) throw CoachError("NOT_CONFIGURED", tr("Le service IA doit être activé par l’éditeur.", "The publisher needs to activate the AI service."))
        val headers = mutableMapOf("apikey" to publicKey)
        if (token != null) headers["Authorization"] = "Bearer $token"
        // Public provider name only. Provider credentials stay behind the server function.
        if (path == "/functions/v1/coach") headers["X-Selkie-Coach-Provider"] = provider
        val response = transport.post(baseUrl.trimEnd('/') + path, headers, input.toString())
        val body = try { JSONObject(response.body) } catch (_: Exception) {
            throw CoachError("INVALID_RESPONSE", tr("Le service ne répond pas correctement. Réessaie plus tard.", "The service is not responding correctly. Try again later."))
        }
        if (response.status !in 200..299) {
            val code = when {
                response.status == 401 -> "UNAUTHORIZED"
                response.status == 429 -> "RATE_LIMITED"
                else -> body.optString("error", "SERVER_ERROR")
            }
            throw CoachError(code, when (code) {
                "UNAUTHORIZED" -> tr("Ta connexion a expiré. Reconnecte-toi avec un code e-mail.", "Your session has expired. Sign in again with an email code.")
                "FREE_QUOTA_EXCEEDED" -> tr("Tes 100 questions gratuites ont été utilisées.", "You have used all 100 free questions.")
                "RATE_LIMITED" -> tr("Patiente un peu avant de réessayer.", "Please wait a little before trying again.")
                "NOT_CONFIGURED" -> tr("L’éditeur doit terminer l’activation du service IA.", "The publisher needs to finish activating the AI service.")
                "BETA_ACCESS_REQUIRED" -> tr("Ce compte n’a pas encore accès au test du coach.", "This account does not yet have access to the coach beta.")
                "PROVIDER_MISMATCH" -> tr("Le fournisseur IA de cette application ne correspond pas à la configuration du serveur. L’éditeur doit corriger la configuration avant un nouvel échange.",
                    "This app’s AI provider does not match the server configuration. The publisher must correct the configuration before a new conversation.")
                else -> tr("Le service est momentanément indisponible. Réessaie plus tard.", "The service is temporarily unavailable. Try again later.")
            })
        }
        return body
    }

    private fun session(body: JSONObject, email: String): CoachSession {
        val access = body.optString("access_token")
        val refresh = body.optString("refresh_token")
        if (access.isBlank() || refresh.isBlank())
            throw CoachError("INVALID_SESSION", tr("Le code n’a pas permis de te connecter. Demande un nouveau code.", "This code did not sign you in. Request a new code."))
        return CoachSession(access, refresh, email)
    }

    private fun checkedEmail(email: String): String {
        val address = email.trim()
        if (address.length > 254 || !address.matches(Regex("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")))
            throw CoachError("INVALID_EMAIL", tr("Entre une adresse e-mail valide.", "Enter a valid email address."))
        return address
    }

    companion object {
        fun validConfiguration(url: String, key: String, provider: String = "openai"): Boolean = try {
            val parsed = URI(url)
            val safeUrl = parsed.scheme == "https" && parsed.host?.endsWith(".supabase.co") == true &&
                parsed.userInfo == null && parsed.port == -1 && parsed.query == null && parsed.fragment == null &&
                parsed.path in listOf("", "/")
            val safeKey = if (key.startsWith("sb_publishable_")) key.length > 20 &&
                key.matches(Regex("sb_publishable_[A-Za-z0-9_-]+")) else {
                val parts = key.split('.')
                parts.size == 3 && JSONObject(String(java.util.Base64.getUrlDecoder().decode(parts[1]), Charsets.UTF_8))
                    .optString("role") == "anon"
            }
            safeUrl && safeKey && provider in setOf("openai", "groq")
        } catch (_: Exception) { false }
    }
}
