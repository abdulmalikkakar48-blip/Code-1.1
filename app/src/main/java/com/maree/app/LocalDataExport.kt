package com.maree.app

import android.content.ContentResolver
import android.net.Uri
import org.json.JSONObject
import java.io.IOException
import java.io.OutputStream
import java.time.Instant
import java.time.ZoneId

/** Un export choisi par la personne, sans préférence brute, session ou contenu du coach. */
internal object LocalDataExport {
    fun json(
        store: Store,
        language: AppLocale = AppLanguage.current,
        appearance: ThemeMode = AppAppearance.current,
        effectiveNight: Boolean = AppAppearance.isNight,
        now: Long = System.currentTimeMillis()
    ): String = JSONObject().apply {
        put("format", "selkie-local-data")
        put("schemaVersion", 5)
        put("exportedAt", Instant.ofEpochMilli(now).toString())
        put("localTimeZone", ZoneId.systemDefault().id)
        put("application", JSONObject().apply {
            put("name", "Selkie")
            put("version", BuildConfig.VERSION_NAME)
        })
        put("preferences", JSONObject().apply {
            put("language", language.tag)
            put("appearance", appearance.tag)
            put("effectiveAppearance", if (effectiveNight) "dark" else "light")
        })
        put("storedData", store.exportLocalState(now))
    }.toString(2)

    fun write(resolver: ContentResolver, destination: Uri, json: String) {
        val stream = resolver.openOutputStream(destination, "wt")
            ?: throw IOException("Unable to open the chosen document")
        write(stream, json)
    }

    /** UTF-8 et fermeture garantie, même lorsque le fournisseur échoue pendant l'écriture. */
    internal fun write(stream: OutputStream, json: String) {
        stream.bufferedWriter(Charsets.UTF_8).use { writer -> writer.write(json) }
    }
}
