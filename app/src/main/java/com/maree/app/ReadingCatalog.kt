package com.maree.app

/** Reviewed short selections, independent of account, plan or tracking. */
internal data class ReadingResource(
    val id: String,
    val titleFr: String,
    val titleEn: String,
    val author: String,
    val descriptionFr: String,
    val descriptionEn: String,
    val sourceUrl: String,
    val assetName: String,
    val sourceLabel: String,
    val editionNoteFr: String,
    val editionNoteEn: String
)

internal val readingResources = listOf(
    ReadingResource(
        id = "descartes-method",
        titleFr = "Discours de la méthode · quatre règles",
        titleEn = "Discourse on Method · four rules",
        author = "René Descartes (1596–1650)",
        descriptionFr = "Apprendre à vérifier, décomposer et ordonner un problème avant de conclure.",
        descriptionEn = "Learn to check, break down and order a problem before reaching a conclusion.",
        sourceUrl = "https://raw.githubusercontent.com/GITenberg/Discours-de-la-m-thode_13846/master/13846-0.txt",
        assetName = "descartes-method",
        sourceLabel = "Discours de la méthode (1637), deuxième partie · GITenberg",
        editionNoteFr = "Passage français ancien du domaine public. Orthographe modernisée pour Selkie ; « taire » corrigé en « faire ». Traduction anglaise préparée pour Selkie.",
        editionNoteEn = "Public-domain historical French passage. Spelling modernised for Selkie; “taire” corrected to “faire”. English translation prepared for Selkie."
    ),
    ReadingResource(
        id = "bacon-studies",
        titleFr = "De l’étude · lire et réfléchir",
        titleEn = "Of Studies · reading and reflection",
        author = "Francis Bacon (1561–1626)",
        descriptionFr = "Ne pas seulement accumuler des pages : peser les idées, discuter et écrire.",
        descriptionEn = "Go beyond counting pages: weigh ideas, discuss and write.",
        sourceUrl = "https://raw.githubusercontent.com/GITenberg/The-Essays-or-Counsels-Civil-and-Moral_575/master/575.txt",
        assetName = "bacon-studies",
        sourceLabel = "Essays, « Of Studies » (1625) · GITenberg",
        editionNoteFr = "Deux passages séparés de l’original anglais du domaine public. Traduction française préparée pour Selkie. Le reste de l’essai est exclu.",
        editionNoteEn = "Two separate passages from the public-domain English original. French translation prepared for Selkie. The rest of the essay is excluded."
    ),
    ReadingResource(
        id = "franklin-habits",
        titleFr = "Autobiographie · trois habitudes",
        titleEn = "Autobiography · three habits",
        author = "Benjamin Franklin (1706–1790)",
        descriptionFr = "L’ordre, les engagements et l’emploi du temps : trois repères concrets.",
        descriptionEn = "Order, commitments and time: three practical guidelines.",
        sourceUrl = "https://raw.githubusercontent.com/standardebooks/benjamin-franklin_the-autobiography-of-benjamin-franklin/master/src/epub/text/chapter-9.xhtml",
        assetName = "franklin-habits",
        sourceLabel = "Autobiography, chapitre IX · Standard Ebooks",
        editionNoteFr = "Seulement Order, Resolution et Industry de l’original anglais du domaine public, sans les autres passages ni les notes. Traduction française préparée pour Selkie.",
        editionNoteEn = "Only Order, Resolution and Industry from the public-domain English original, without other passages or notes. French translation prepared for Selkie."
    ),
    ReadingResource(
        id = "ghazali-observation",
        titleFr = "Al-Ghazali · observer une ombre",
        titleEn = "Al-Ghazali · observing a shadow",
        author = "Al-Ghazali (1058–1111)",
        descriptionFr = "Un auteur musulman : distinguer une impression immédiate d’une observation patiente.",
        descriptionEn = "A Muslim author: distinguish an immediate impression from patient observation.",
        sourceUrl = "https://raw.githubusercontent.com/GITenberg/The-Confessions-of-Al-Ghazzali_58977/master/58977-0.txt",
        assetName = "ghazali-observation",
        sourceLabel = "The Confessions of Al Ghazzali, traduction Claud Field (1909) · GITenberg",
        editionNoteFr = "Seul l’exemple de l’ombre est retenu. Traduction anglaise historique de Claud Field (1863–1941), dans le domaine public ; version française préparée pour Selkie. Aucune introduction ni note reprise.",
        editionNoteEn = "Only the shadow example is selected. Historical public-domain English translation by Claud Field (1863–1941); French version prepared for Selkie. No introduction or notes reproduced."
    ),
    ReadingResource(
        id = "fontaine-patience",
        titleFr = "Le Lion et le Rat · patience et entraide",
        titleEn = "The Lion and the Rat · patience and help",
        author = "Jean de La Fontaine (1621–1695)",
        descriptionFr = "Une courte fable sur la patience, l’entraide et la force des petits gestes.",
        descriptionEn = "A short fable about patience, helping others and the strength of small actions.",
        sourceUrl = "https://raw.githubusercontent.com/GITenberg/Fables-de-La-FontaineTome-Second_17942/master/17942-8.txt",
        assetName = "fontaine-patience",
        sourceLabel = "Fables (1668), livre II, fable XI · GITenberg",
        editionNoteFr = "Texte français complet de cette seule fable, du domaine public ; les autres fables sont exclues. Traduction anglaise en prose préparée pour Selkie.",
        editionNoteEn = "Complete public-domain French text of this one fable; other fables are excluded. English prose translation prepared for Selkie."
    ),
    ReadingResource(
        id = "smiles-method",
        titleFr = "Self-Help · attention et méthode",
        titleEn = "Self-Help · attention and method",
        author = "Samuel Smiles (1812–1904)",
        descriptionFr = "Prendre soin des petites actions répétées et travailler avec méthode.",
        descriptionEn = "Care for small repeated actions and work methodically.",
        sourceUrl = "https://raw.githubusercontent.com/GITenberg/Self-Help-with-Illustrations-of-Conduct-and-Perseverance_935/master/935-0.txt",
        assetName = "smiles-method",
        sourceLabel = "Self-Help (1859), édition John Murray 1897, chapitre IX · GITenberg",
        editionNoteFr = "Un paragraphe de l’édition John Murray 1897, première publication 1859, dans le domaine public. Traduction française préparée pour Selkie, sans reproduire l’ouvrage entier.",
        editionNoteEn = "One paragraph from the public-domain John Murray 1897 edition, first published in 1859. French translation prepared for Selkie, without reproducing the complete book."
    ),
    ReadingResource(
        id = "taymiyya-worship",
        titleFr = "Ibn Taymiyya · le sens des actes",
        titleEn = "Ibn Taymiyya · the meaning of actions",
        author = "Ibn Taymiyya (1263–1328)",
        descriptionFr = "Une courte définition de l’adoration reliant les paroles et les actes, intérieurs comme extérieurs.",
        descriptionEn = "A short definition of worship connecting inward and outward words and deeds.",
        sourceUrl = "https://raw.githubusercontent.com/OpenITI/0750AH/master/data/0728IbnTaymiyya/0728IbnTaymiyya.Cubudiyya/0728IbnTaymiyya.Cubudiyya.Shamela0022647-ara1",
        assetName = "taymiyya-worship",
        sourceLabel = "Al-ʿUbūdiyya, p. 44 · également Majmūʿ al-fatāwā, 10/149 · OpenITI/Shamela",
        editionNoteFr = "Courte citation de l’original arabe médiéval, ponctuation normalisée. Traductions FR/EN préparées pour Selkie. Références : Al-ʿUbūdiyya, p. 44, et Majmūʿ al-fatāwā, 10/149 ; citation seule, sans notes modernes.",
        editionNoteEn = "Short quotation from the medieval Arabic original, punctuation normalised. FR/EN translations prepared for Selkie. References: Al-ʿUbūdiyya, p. 44, and Majmūʿ al-fatāwā, 10/149; quotation only, without modern notes."
    ),
    ReadingResource(
        id = "taymiyya-good-deeds",
        titleFr = "Ibn Taymiyya · les bonnes actions quotidiennes",
        titleEn = "Ibn Taymiyya · everyday good deeds",
        author = "Ibn Taymiyya (1263–1328)",
        descriptionFr = "Dire vrai, être digne de confiance et tenir ses engagements : un fragment sur les gestes du quotidien.",
        descriptionEn = "Speak truthfully, be trustworthy and keep commitments: a fragment about everyday actions.",
        sourceUrl = "https://raw.githubusercontent.com/OpenITI/0750AH/master/data/0728IbnTaymiyya/0728IbnTaymiyya.Cubudiyya/0728IbnTaymiyya.Cubudiyya.Shamela0022647-ara1",
        assetName = "taymiyya-good-deeds",
        sourceLabel = "Al-ʿUbūdiyya, p. 44 · également Majmūʿ al-fatāwā, 10/149 · OpenITI/Shamela",
        editionNoteFr = "Fragment sélectionné d’une liste dans l’original arabe médiéval : Al-ʿUbūdiyya, p. 44, également Majmūʿ al-fatāwā, 10/149. Les autres éléments sont omis et les coupures signalées. Traductions FR/EN préparées pour Selkie ; aucune annotation moderne reprise.",
        editionNoteEn = "Selected fragment of a list in the medieval Arabic original: Al-ʿUbūdiyya, p. 44, also Majmūʿ al-fatāwā, 10/149. Other items are omitted and cuts indicated. FR/EN translations prepared for Selkie; no modern annotations reproduced."
    )
)

/** Build only the chosen passage and its references; no personal data or other books. */
internal fun readingExportContent(resource: ReadingResource, language: AppLocale, body: String): String {
    val english = language == AppLocale.ENGLISH
    return buildString {
        appendLine(if (english) resource.titleEn else resource.titleFr)
        appendLine(resource.author)
        appendLine(if (english) "Selected excerpt — not the whole book." else "Extrait sélectionné — livre non intégral.")
        appendLine(if (english) "Source: ${resource.sourceLabel}" else "Source : ${resource.sourceLabel}")
        appendLine(resource.sourceUrl)
        appendLine(if (english) resource.editionNoteEn else resource.editionNoteFr)
        appendLine()
        append(body)
        appendLine()
    }
}
