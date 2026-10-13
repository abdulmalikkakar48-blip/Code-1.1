package com.maree.app

/** Informational pages for this free test version, distinct from future sales terms. */
internal enum class LegalDocument(private val frenchTitle: String, private val englishTitle: String) {
    TERMS("Conditions d’utilisation", "Terms of use"),
    SUBSCRIPTIONS("Abonnements et résiliation", "Subscriptions and cancellation"),
    PUBLISHER("Éditeur et mentions", "Publisher information"),
    LICENSES("Licences des bibliothèques", "Library licences");

    val title: String get() = tr(frenchTitle, englishTitle)
}

internal object LegalContent {
    val updatedOn: String get() = tr("13 octobre 2026", "13 October 2026")

    fun introduction(document: LegalDocument): String = when (document) {
        LegalDocument.TERMS -> tr(
            "Les conditions de cette version de test gratuite de Selkie. Aucun achat ni abonnement payant n’est proposé ici.",
            "The terms for this free Selkie test version. No purchases or paid subscriptions are offered here."
        )
        LegalDocument.SUBSCRIPTIONS -> tr(
            "Les offres sont actuellement des démonstrations gratuites. Voici les tarifs envisagés et les informations à connaître avant un éventuel achat futur.",
            "Plans are currently free demonstrations. Here are the proposed prices and information to know before any possible future purchase."
        )
        LegalDocument.LICENSES -> tr("Bibliothèques utilisées et texte de leur licence.", "Libraries used and their licence text.")
        LegalDocument.PUBLISHER -> tr(
            "Le contact actuel de Selkie et l’état de préparation de sa future diffusion publique.",
            "Selkie’s current contact and the status of its future public release."
        )
    }

    fun sections(document: LegalDocument): List<PrivacySection> = when (document) {
        LegalDocument.TERMS -> terms()
        LegalDocument.SUBSCRIPTIONS -> subscriptions()
        LegalDocument.PUBLISHER -> publisher()
        LegalDocument.LICENSES -> ThirdPartyNotices.sections()
    }

    private fun terms() = listOf(
        PrivacySection(tr("Une aide personnelle au temps d’écran", "A personal screen-time aid"), listOf(
            tr(
                "Selkie permet de choisir des applications à suivre, de fixer des limites et de pratiquer des défis. Le coach propose des conseils préparés sur le temps d’écran et répond aux questions prévues sur l’application.",
                "Selkie lets you choose apps to track, set limits and practise challenges. The coach provides prepared screen-time advice and answers anticipated questions about the app."
            ),
            tr(
                "Les limites et les écrans de pause sont une aide volontaire. Ils ne garantissent ni un blocage impossible à contourner, ni une réduction précise du temps d’écran.",
                "Limits and pause screens are a voluntary aid. They do not guarantee a block that cannot be bypassed or a particular reduction in screen time."
            )
        )),
        PrivacySection(tr("Version gratuite et points virtuels", "Free version and virtual points"), listOf(
            tr(
                "Cette version de test est gratuite. Choisir Premium ou Ultra dans la démonstration ne crée aucun achat, paiement, prélèvement ou renouvellement.",
                "This test version is free. Choosing Premium or Ultra in the demonstration creates no purchase, payment, charge or renewal."
            ),
            tr(
                "Les points indiquent ta progression dans Selkie. Ils ne sont pas de l’argent, ne s’achètent pas dans cette version et ne peuvent pas être échangés contre de l’argent. Les mêmes règles de points s’appliquent à toutes les offres.",
                "Points reflect your progress in Selkie. They are not money, cannot be purchased in this version and cannot be exchanged for money. The same point rules apply to all plans."
            ),
            tr(
                "Les règles détaillées des limites, des points et des défis sont consultables dans le menu ☰ → Règles de Selkie.",
                "The detailed rules for limits, points and challenges are available in the ☰ menu → Selkie rules."
            )
        )),
        PrivacySection(tr("Tu gardes le contrôle", "You stay in control"), listOf(
            tr(
                "Le suivi demande tes autorisations Android. Tu peux le mettre en pause dans le menu ☰ → Accès Android et retirer les autorisations dans les réglages du téléphone. Les défis et le coach local restent utilisables sans activer le suivi.",
                "Tracking requires your Android permissions. You can pause it in the ☰ menu → Android access and revoke permissions in your phone’s settings. Challenges and the local coach remain available without enabling tracking."
            ),
            tr(
                "Le suivi peut être interrompu par Android, les réglages de batterie, le retrait d’un accès ou une erreur. Vérifie l’état du suivi avant de compter sur une limite ; les minutes affichées dépendent aussi des informations fournies par ton téléphone.",
                "Tracking can be interrupted by Android, battery settings, revoked access or an error. Check tracking status before relying on a limit; displayed minutes also depend on information provided by your phone."
            ),
            tr(
                "Les informations de confidentialité et l’effacement local sont disponibles dans le menu ☰ → Confidentialité et données. L’effacement demande une confirmation et supprime la progression de Selkie sur ce téléphone.",
                "Privacy information and local data deletion are available in the ☰ menu → Privacy and data. Deletion requires confirmation and removes Selkie progress on this phone."
            )
        )),
        PrivacySection(tr("Conseils et droits", "Advice and your rights"), listOf(
            tr(
                "Selkie ne fournit pas de diagnostic, de traitement ou de conseil médical. Le coach ne remplace pas un professionnel ni une aide humaine en cas de difficulté ou d’urgence. Aucun résultat personnel n’est garanti.",
                "Selkie does not provide diagnosis, treatment or medical advice. The coach does not replace a professional or human help in a difficult or urgent situation. No personal outcome is guaranteed."
            ),
            tr(
                "Ces informations ne retirent aucun droit obligatoire prévu par la loi, notamment les droits des consommateurs lorsqu’ils s’appliquent. Elles ne prévoient pas d’exclusion générale de responsabilité.",
                "This information does not remove any mandatory statutory rights, including consumer rights where applicable. It does not provide a blanket exclusion of liability."
            ),
            tr(
                "Pour signaler un problème ou poser une question, écris à ${PrivacyIdentity.contactEmail}. N’envoie pas de mot de passe, de coordonnées bancaires ou d’informations sensibles.",
                "To report a problem or ask a question, write to ${PrivacyIdentity.contactEmail}. Do not send passwords, banking details or sensitive information."
            )
        ))
    )

    private fun subscriptions() = listOf(
        PrivacySection(tr("Aucun abonnement vendu ici", "No subscriptions are sold here"), listOf(
            tr(
                "Premium et Ultra sont des démonstrations gratuites dans cet APK. Aucun abonnement réel, prélèvement, renouvellement ou remboursement n’est déclenché par le choix d’une offre.",
                "Premium and Ultra are free demonstrations in this APK. Selecting a plan triggers no real subscription, charge, renewal or refund."
            ),
            tr(
                "Une future vente nécessitera des conditions de vente complètes et un écran d’achat indiquant le prix, la durée, le renouvellement et les droits applicables avant confirmation.",
                "Future sales will require complete sales terms and a purchase screen showing the price, duration, renewal and applicable rights before confirmation."
            )
        )),
        PrivacySection(tr("Tarifs envisagés", "Proposed prices"), listOf(
            tr(
                "Premium : ${PlanType.PREMIUM.monthlyPriceEuros} €/mois ou ${PlanType.PREMIUM.annualPriceEuros} €/an. Ultra : ${PlanType.ULTRA.monthlyPriceEuros} €/mois ou ${PlanType.ULTRA.annualPriceEuros} €/an.",
                "Premium: €${PlanType.PREMIUM.monthlyPriceEuros}/month or €${PlanType.PREMIUM.annualPriceEuros}/year. Ultra: €${PlanType.ULTRA.monthlyPriceEuros}/month or €${PlanType.ULTRA.annualPriceEuros}/year."
            ),
            tr(
                "L’année serait payée en une fois. Son prix correspond à douze mensualités, sans réduction, avec les mêmes fonctionnalités. Les montants restent des tarifs proposés ; le prix final et ses conditions devront être affichés avant tout futur achat.",
                "The annual plan would be paid upfront. Its price equals twelve monthly payments, without a discount, with the same features. These are proposed prices; the final price and its terms must be displayed before any future purchase."
            )
        )),
        PrivacySection(tr("Renouvellement et annulation", "Renewal and cancellation"), listOf(
            tr(
                "Le modèle prévu est un renouvellement chaque mois pour le mensuel, chaque année pour l’annuel, sauf annulation. Aucun renouvellement n’est actuellement actif.",
                "The planned model renews monthly for the monthly plan and yearly for the annual plan, unless cancelled. No renewal is currently active."
            ),
            tr(
                "Pour un futur abonnement acheté via Google Play : ouvre Play Store → photo de profil → Paiements et abonnements → Abonnements → Selkie → Annuler l’abonnement. Utilise le compte Google qui a effectué l’achat.",
                "For a future subscription purchased through Google Play: open Play Store → profile picture → Payments & subscriptions → Subscriptions → Selkie → Cancel subscription. Use the Google account that made the purchase."
            ),
            tr(
                "L’annulation arrête normalement le prochain renouvellement ; l’accès reste habituellement disponible jusqu’à la fin de la période déjà payée. Google Play indique l’échéance applicable. Désinstaller Selkie ou effacer ses données n’annule pas un abonnement.",
                "Cancellation normally stops the next renewal; access usually remains available until the end of the paid period. Google Play shows the applicable end date. Uninstalling Selkie or deleting its data does not cancel a subscription."
            )
        )),
        PrivacySection(tr("Pas de remboursement automatique", "No automatic refunds"), listOf(
            tr(
                "Pour une future offre payante : pas de remboursement automatique pour une période déjà utilisée. Une simple annulation ne rembourse pas automatiquement un mois ou une période déjà écoulée. Aucun geste commercial ni remboursement discrétionnaire n’est promis.",
                "For a future paid plan: there is no automatic refund for a period already used. Simply cancelling does not automatically refund a month or period that has already elapsed. No goodwill gesture or discretionary refund is promised."
            ),
            tr(
                "Cette règle ne retire aucun droit obligatoire, notamment la rétractation lorsqu’elle s’applique, les garanties légales en cas de non-conformité et la restitution de sommes indûment prélevées. Les règles applicables du magasin d’achat doivent également être respectées. Les conditions de la future offre devront être finalisées avant sa vente.",
                "This rule does not remove any mandatory rights, including withdrawal where applicable, statutory rights when the service does not conform to the contract, and repayment of incorrectly charged amounts. Applicable purchase-store rules must also be respected. The future plan’s terms must be finalised before it is sold."
            )
        )),
        PrivacySection(tr("Faire valoir tes droits", "Exercising your rights"), listOf(
            tr(
                "Annuler un abonnement et demander un remboursement sont deux démarches différentes. Une annulation n’entraîne pas automatiquement un remboursement.",
                "Cancelling a subscription and requesting a refund are separate actions. Cancellation does not automatically issue a refund."
            ),
            tr(
                "Pour un achat Google Play réel, retrouve la commande dans l’historique des commandes du compte Google utilisé, puis suis l’option de signalement d’un problème ou de demande de remboursement lorsqu’elle est proposée. Tu peux aussi écrire à ${PrivacyIdentity.contactEmail} avec le numéro de commande, la date et le problème rencontré.",
                "For a real Google Play purchase, find the order in the order history of the Google account used, then follow the report-a-problem or refund-request option when available. You can also write to ${PrivacyIdentity.contactEmail} with the order number, date and the problem you encountered."
            ),
            tr(
                "Ne communique jamais ton mot de passe, ton numéro de carte ou ton RIB pour cette demande. Son examen dépend notamment du droit applicable, du moment de la demande et des conditions de l’achat ; aucun remboursement n’est garanti par cette page.",
                "Never share your password, card number or bank details for this request. Assessment depends on applicable law, the timing of the request and purchase conditions; this page does not guarantee a refund."
            )
        )),
        PrivacySection(tr("Rétractation en France", "Withdrawal rights in France"), listOf(
            tr(
                "En France, un consommateur qui conclut un contrat à distance dispose en principe de 14 jours pour se rétracter. Pour un service ou un contenu numérique, le délai commence en principe à la conclusion du contrat. Des exceptions existent et doivent respecter leurs conditions légales.",
                "In France, a consumer entering into a distance contract normally has 14 days to withdraw. For a service or digital content, this period normally begins when the contract is concluded. Exceptions exist and must meet their legal conditions."
            ),
            tr(
                "Pour un service payant entièrement exécuté avant la fin du délai, la perte de ce droit suppose notamment ton accord préalable et exprès pour commencer et ta reconnaissance de la perte du droit une fois le service entièrement exécuté. Si tu demandes un début de service pendant le délai puis te rétractes, un montant proportionnel peut être dû seulement lorsque les conditions légales sont remplies.",
                "For a paid service fully performed before the period ends, losing this right requires, in particular, your prior express agreement to start and your acknowledgment that the right is lost once the service is fully performed. If you request the service to start during this period and then withdraw, a proportionate amount may be payable only when the legal conditions are met."
            ),
            tr(
                "Pour un contenu numérique payant sans support matériel fourni avant la fin du délai, l’exception suppose notamment ton consentement préalable et exprès au commencement, ta reconnaissance expresse de la perte du droit et une confirmation du contrat sur un support durable incluant cet accord et cette reconnaissance. Ouvrir l’application ou choisir une démonstration gratuite ne constitue pas une renonciation automatique.",
                "For paid digital content without a physical medium supplied before the period ends, the exception requires, in particular, your prior express consent to starting, your express acknowledgment of losing the right and confirmation of the contract on a durable medium including that consent and acknowledgment. Opening the app or selecting a free demonstration is not an automatic waiver."
            ),
            tr(
                "La qualification et les conditions de la future offre Selkie devront être précisées avant sa vente. Les règles de remboursement d’une plateforme ne remplacent pas tes droits légaux. Pour exercer une demande, précise la commande concernée et ce que tu demandes, sans fournir de données bancaires.",
                "The classification and conditions of any future Selkie offer must be specified before it is sold. A platform’s refund rules do not replace your statutory rights. When making a request, identify the order and what you are requesting, without providing banking details."
            )
        ))
    )

    private fun publisher() = listOf(
        PrivacySection(tr("Contact actuel", "Current contact"), listOf(
            tr(
                "Développeur et responsable actuellement déclaré pour les données : ${PrivacyIdentity.controllerName}. Contact : ${PrivacyIdentity.contactEmail}.",
                "Developer and currently declared data controller: ${PrivacyIdentity.controllerName}. Contact: ${PrivacyIdentity.contactEmail}."
            ),
            tr(
                "Ces coordonnées correspondent à cette version de test. L’éditeur de la future diffusion publique n’a pas encore été désigné. Aucune entreprise ni identité d’un futur vendeur n’est annoncée ici.",
                "These details relate to this test version. The publisher for a future public release has not yet been designated. No company or future seller’s identity is announced here."
            )
        )),
        PrivacySection(tr("Avant une version commerciale", "Before a commercial release"), listOf(
            tr(
                "La version commerciale n’est pas ouverte. Avant toute vente, l’identité et les coordonnées du véritable éditeur et vendeur, les informations commerciales et d’immatriculation requises, les mentions d’hébergement et le médiateur de la consommation applicable devront être complétés et vérifiés.",
                "The commercial version is not open. Before any sale, the actual publisher and seller’s identity and contact details, required commercial and registration information, hosting information and the applicable consumer mediator must be completed and checked."
            ),
            tr(
                "Les conditions de vente définitives devront préciser les offres, les prix, les renouvellements, l’annulation, les demandes de remboursement et les droits applicables. Cette page ne remplace pas ces conditions futures.",
                "Final sales terms must specify plans, prices, renewals, cancellation, refund requests and applicable rights. This page does not replace those future terms."
            )
        )),
        PrivacySection(tr("Questions et données personnelles", "Questions and personal data"), listOf(
            tr(
                "La politique de confidentialité décrit les données et les choix de cette version. Elle est disponible sans activer le suivi, dans le menu ☰ → Confidentialité et données.",
                "The privacy policy describes this version’s data and choices. It is available without enabling tracking in the ☰ menu → Privacy and data."
            ),
            tr(
                "Pour une question sur Selkie, ses conditions ou tes données, écris à ${PrivacyIdentity.contactEmail}. Évite les informations sensibles et les captures contenant des messages privés.",
                "For a question about Selkie, its terms or your data, write to ${PrivacyIdentity.contactEmail}. Avoid sensitive information and screenshots containing private messages."
            )
        ))
    )
}
