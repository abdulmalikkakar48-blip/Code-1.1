package com.maree.app

import android.content.Context

/** Stockage privé, exclu de la sauvegarde Android. Pas d'historique persistant du chat. */
internal class CoachSessionStore(context: Context) {
    private val prefs = context.getSharedPreferences("selkie_coach_session", Context.MODE_PRIVATE)

    fun load(): CoachSession? {
        val access = prefs.getString("access", null) ?: return null
        val refresh = prefs.getString("refresh", null) ?: return null
        val email = prefs.getString("email", null) ?: return null
        return CoachSession(access, refresh, email)
    }

    fun generation(): Long = synchronized(lock) { prefs.getLong("generation", 0L) }

    fun save(session: CoachSession, expectedGeneration: Long) = synchronized(lock) {
        if (prefs.getLong("generation", 0L) != expectedGeneration)
            throw CoachError("SESSION_CLEARED", tr("La connexion a été effacée. Reconnecte-toi si tu le souhaites.", "The session was cleared. Sign in again if you wish."))
        if (!prefs.edit().putString("access", session.accessToken).putString("refresh", session.refreshToken)
            .putString("email", session.email).commit())
            throw CoachError("STORAGE_ERROR", tr("La connexion n’a pas pu être enregistrée.", "The session could not be saved."))
    }

    fun clear() = synchronized(lock) {
        val next = prefs.getLong("generation", 0L) + 1L
        if (!prefs.edit().clear().putLong("generation", next).commit())
            throw CoachError("STORAGE_ERROR", tr("La connexion n’a pas pu être effacée.", "The session could not be cleared."))
    }

    companion object { private val lock = Any() }
}
