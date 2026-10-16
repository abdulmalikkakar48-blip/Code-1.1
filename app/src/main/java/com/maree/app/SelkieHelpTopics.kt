package com.maree.app

/** Rubriques de l’aide embarquée, consultables sans compte ni connexion. */
internal enum class SelkieHelpSection(private val frenchLabel: String, private val englishLabel: String) {
    DISCOVER("Découvrir Selkie", "Discover Selkie"),
    TRACKING("Suivi et autorisations", "Tracking and permissions"),
    POINTS("Les points", "Points"),
    CHALLENGES("Les défis", "Challenges"),
    OFFERS("Offres et abonnements", "Plans and subscriptions"),
    ACCOUNT("Compte et téléphone", "Account and phone"),
    PRIVACY("Données et contact", "Data and contact");

    val label: String get() = tr(frenchLabel, englishLabel)
}

internal data class SelkieHelpEntry(val section: SelkieHelpSection, val topic: CoachTopic)

/**
 * Chaque question a une réponse de référence complète. Une réponse unique évite
 * qu’un tirage de variantes ne masque un prix, une condition ou une limite réelle.
 * Les comptes et paiements futurs sont toujours distingués de la version de test.
 */
internal val selkieHelpEntriesFrench: List<SelkieHelpEntry> = listOf(
    help(
        SelkieHelpSection.DISCOVER, "app_overview", "À quoi sert Selkie ?",
        listOf("selkie", "maree", "a quoi sert selkie", "qu est ce que selkie", "presentation de selkie", "comment fonctionne selkie", "description de l application", "que fait cette application", "a quoi sert l application", "a quoi sert cette application", "comment fonctionne l application", "comment utiliser selkie"),
        "Selkie t’aide à retrouver du temps hors écran : choisis des applications à suivre, fixe leurs limites et fais des défis sur plusieurs jours. " +
            "Le suivi peut afficher une pause quand une limite est atteinte. Les points encouragent la régularité, et le Coach propose des conseils préparés et l’aide de l’application, gratuitement et hors ligne."
    ),
    help(
        SelkieHelpSection.DISCOVER, "navigation", "Où trouver chaque rubrique ?",
        listOf("navigation", "rubriques", "onglets", "ou trouver", "les icones", "boutons en bas", "accueil", "menu plus", "menu en haut", "menu hamburger", "trois traits", "ecrans de l application"),
        "Les quatre onglets sont en bas : Accueil pour tes applications et tes points ; Quotidien pour une idée de pause et ton bilan ; Défis pour valider les journées ; Coach pour les conseils et l’aide. " +
            "Le menu ☰ en haut regroupe Langue, Apparence, Accès Android, Abonnements, Règles de Selkie et Confidentialité et données."
    ),
    help(
        SelkieHelpSection.DISCOVER, "daily", "Que trouve-t-on dans Quotidien ?",
        listOf("quotidien", "bilan du jour", "pause du jour", "conseil du jour", "bilan quotidien", "que mesure ce bilan", "exercices quotidiens", "activites quotidiennes", "routine quotidienne", "calendrier selkie", "bilan hebdomadaire", "historique observe"),
        "Quotidien propose une idée de pause sans points ni validation, additionne les compteurs des applications suivies et montre ta progression dans les défis. " +
            "Ce bilan ne mesure pas tout ton temps d’écran. Les compteurs peuvent repartir à zéro après un ajout, une réinitialisation ou la fin d’un verrou ; le suivi doit être actif pour les actualiser. " +
            "Le calendrier et le bilan hebdomadaire sont inclus dès Gratuit : ils gardent au maximum 90 jours locaux de durées et validations observées, sans inventer de passé. Seules les applications choisies et les périodes observées pendant le suivi actif sont comptées ; les pauses et lacunes sont exclues de cet historique. Les moyennes portent sur les seuls jours observés, et les badges de dépassements observés ne prouvent pas une journée parfaite. Premium et Ultra ajoutent le détail par application. Le calendrier du téléphone n’est ni lu ni modifié."
    ),
    help(
        SelkieHelpSection.DISCOVER, "coach_help", "Comment fonctionne le Coach ?",
        listOf("coach", "coach ia", "ia", "chatgpt", "intelligence artificielle", "comment fonctionne le coach", "coach gratuit", "coach hors ligne", "reponses preparees", "favoris du coach", "conseils favoris"),
        "Écris ta question dans Coach ou choisis un sujet de l’aide. Ce coach reconnaît des mots et expressions pour proposer des conseils préparés et expliquer Selkie. " +
            "La saisie standard et l’aide fonctionnent gratuitement sur ton téléphone, sans compte ni Internet. La bibliothèque propose ${PlanType.FREE.coachQuestionCount} questions préparées en Gratuit, ${PlanType.PREMIUM.coachQuestionCount} en Premium et ${PlanType.ULTRA.coachQuestionCount} en Ultra. Ce coach ne comprend pas toutes les questions et ce n’est pas ChatGPT. " +
            "Dès Gratuit, tu peux sélectionner jusqu’à 50 favoris de conseils rédigés et préparés. Aucune saisie brute, phrase non comprise ou phrase sensible n’est copiée dans les favoris. Leur langue d’origine et leur offre minimale sont gardées ; un favori réservé à une offre supérieure reste enregistré mais sa lecture est bloquée après un passage à une offre inférieure."
    ),
    help(
        SelkieHelpSection.DISCOVER, "coach_unknown", "Si le Coach ne comprend pas ?",
        listOf("coach ne comprend pas", "question inconnue", "phrase non comprise", "phrases non comprises", "questions non comprises", "enregistrer mes questions", "historique du coach", "conversation enregistree"),
        "Si le Coach ne reconnaît pas ta phrase, il te propose des boutons de sujets : choisis-en un ou reformule. La conversation complète n’est pas archivée. " +
            "Tu peux activer la conservation locale des phrases non comprises dans les options du Coach : au maximum 50 phrases de 800 caractères, sans envoi à l’éditeur. " +
            "Tu peux les voir et les effacer ; désactiver l’option les efface aussi. Les filtres de messages sensibles peuvent être imparfaits : évite les informations privées."
    ),
    help(
        SelkieHelpSection.TRACKING, "add_app", "Ajouter une application au suivi",
        listOf("ajouter une application", "ajouter une app", "ajouter au suivi", "suivre une application", "suivre une app", "bouton ajouter", "selectionner une application"),
        "Dans Accueil, appuie sur Ajouter, choisis une application disponible puis sa limite par pas de 5 minutes. Il faut plus de 0 point pour l’ajouter. " +
            "Gratuit permet 10 applications, Premium 50 et Ultra n’a pas de plafond commercial sur les applications compatibles. Si une application manque, elle peut ne pas être compatible ou accessible dans la liste Android."
    ),
    help(
        SelkieHelpSection.TRACKING, "limits", "Fixer ou modifier une limite",
        listOf("fixer une limite", "choisir une limite", "modifier une limite", "changer ma limite", "changer la limite", "limite quotidienne", "combien de minutes choisir", "regler une limite", "plages horaires", "limite selon les heures", "horaire de nuit"),
        "À l’ajout, choisis une limite adaptée à ton usage : de 5 à 1 440 minutes, par pas de 5 minutes. Pour la changer, appuie sur Modifier la limite sur sa carte dans Accueil. " +
            "La modification demande un solde supérieur à 0 point, même en Premium ou Ultra. Modifier la limite ne lève pas un verrou déjà actif : il faut attendre sa fin ou réinitialiser avec 10 points. " +
            "Dans Quotidien, Premium et Ultra permettent jusqu’à 20 plages horaires pour des applications déjà suivies : choisis les jours, des heures HH:mm et une limite de 5 à 240 minutes. Une plage nocturne finit le lendemain du jour sélectionné ; deux plages actives de la même application ne peuvent pas se chevaucher. La plage change la limite du compteur existant sans remise à zéro, et un verrou de 24 h continue après sa fin. À 0 point, créer, modifier, activer ou supprimer une plage reste bloqué. En Gratuit, ces plages sont conservées mais ne s’appliquent pas."
    ),
    help(
        SelkieHelpSection.TRACKING, "red_screen", "Pourquoi une application est-elle bloquée ?",
        listOf("ecran rouge", "ecran de pause", "ecran bloque", "ecran de blocage", "verrou", "application bloquee", "bloque 24 heures", "duree du blocage"),
        "Quand une application suivie atteint sa limite, le suivi actif crée un verrou de 24 heures à partir de ce moment. L’écran de pause apparaît si tu ouvres cette application et si les autorisations sont présentes. " +
            "Accueil affiche le temps restant. Attends la fin du verrou ou réinitialise pour 10 points. Selkie est une aide personnelle : ce n’est pas un blocage impossible à contourner dans Android."
    ),
    help(
        SelkieHelpSection.TRACKING, "reset", "Réinitialiser une limite",
        listOf("reinitialiser", "reinitialisation", "reset", "lever le verrou", "debloquer", "remettre le compteur", "recommencer ma limite", "je veux reinitialiser", "comment reinitialiser"),
        "Dans Accueil, ouvre le menu ⋮ de l’application puis Réinitialiser et confirme. Il faut au moins 10 points ; 10 points sont retirés, le verrou est levé et le compteur repart avec la limite complète. " +
            "Cela ne supprime pas les statistiques Android ni un dépassement déjà enregistré pour le bonus de discipline. Le coût est le même en Gratuit, Premium et Ultra."
    ),
    help(
        SelkieHelpSection.TRACKING, "remove_app", "Retirer une application du suivi",
        listOf("retirer du suivi", "retirer une application du suivi", "supprimer du suivi", "enlever du suivi", "ne plus suivre", "arreter de suivre une application"),
        "Dans Accueil, ouvre le menu ⋮ de l’application, choisis Retirer du suivi et confirme. Son entrée, sa limite et son verrou sont supprimés de Selkie ; l’application reste installée sur le téléphone. " +
            "Tu peux la retirer même à 0 point. Cela ne rembourse pas de points et n’efface pas un dépassement déjà enregistré pour le bonus de discipline."
    ),
    help(
        SelkieHelpSection.TRACKING, "permissions", "Quelles autorisations faut-il ?",
        listOf("autorisation", "autorisations", "permission", "permissions", "acces utilisation", "acces aux donnees d utilisation", "superposition", "par dessus", "apparaitre au dessus"),
        "L’accès aux données d’utilisation sert à mesurer les durées et reconnaître l’application ouverte ; l’affichage par-dessus les autres applications sert à montrer l’écran de pause. " +
            "Ces deux accès sont nécessaires pour activer le suivi. Choisis-les sur l’écran d’accueil initial ou dans le menu ☰ → Accès Android → Activer le suivi / Gérer les autorisations. Tu peux découvrir l’application, utiliser les défis et le Coach sans activer le suivi."
    ),
    help(
        SelkieHelpSection.TRACKING, "notifications_permission", "Les alertes sont-elles obligatoires ?",
        listOf("alertes de selkie", "notifications de selkie", "autoriser les notifications", "refuser les notifications", "alertes facultatives", "avant la limite", "80 pour cent", "prevenir avant la limite"),
        "Les notifications sont facultatives. Avec elles, Selkie peut prévenir lorsqu’environ 80 % de la limite est utilisé et rendre visible son service de suivi. " +
            "Sans elles, le suivi et l’écran de pause peuvent fonctionner avec les deux accès nécessaires. Dans le menu ☰ → Accès Android, choisis Activer les alertes facultatives, ou règle leur visibilité dans les paramètres Android."
    ),
    help(
        SelkieHelpSection.TRACKING, "battery", "Le suivi s’arrête ou consomme de la batterie",
        listOf("batterie", "economie d energie", "economiseur de batterie", "suivi s arrete", "suivi ne fonctionne pas", "blocage ne marche pas", "suivi en arriere plan", "optimisation de batterie"),
        "Vérifie dans le menu ☰ → Accès Android que le suivi est actif et que ses deux accès Android sont accordés. Certains téléphones limitent les applications en arrière-plan : dans Paramètres → Applications → Selkie → Batterie, " +
            "vérifie les restrictions selon ton modèle, puis rouvre Selkie. Un service de suivi utilise des ressources ; sa consommation et sa continuité dépendent du téléphone. Tu peux le mettre en pause dans le menu ☰ → Accès Android."
    ),
    help(
        SelkieHelpSection.TRACKING, "pause_tracking", "Mettre le suivi en pause ou le reprendre",
        listOf("mettre le suivi en pause", "pause du suivi", "arreter le suivi", "desactiver le suivi", "reprendre le suivi", "activer le suivi", "suivi en pause"),
        "Dans le menu ☰ → Accès Android, choisis Mettre le suivi en pause : la surveillance et les écrans de pause s’arrêtent, mais tes applications, limites et verrous enregistrés restent conservés. " +
            "Pour reprendre, choisis Activer le suivi et vérifie les accès Android. À la reprise, Android peut fournir l’usage de la journée : une pause du service ne garantit pas que toutes ces minutes seront exclues des compteurs. L’historique du calendrier et du bilan hebdomadaire est distinct : il exclut les pauses et les intervalles non observés, sans reconstituer les journées manquantes."
    ),
    help(
        SelkieHelpSection.TRACKING, "usage", "Que mesurent les minutes affichées ?",
        listOf("mon utilisation", "temps d ecran", "combien de minutes", "minutes utilisees", "usage aujourd hui", "statistiques", "voir mon usage", "compteur", "temps affiche"),
        "Accueil affiche le compteur de chaque application suivie et Quotidien les additionne. Selkie utilise les événements Android de la journée, avec une référence remise à zéro lors d’un ajout, d’une réinitialisation ou de la fin d’un verrou. " +
            "Ces compteurs ne représentent donc pas toujours le total affiché par Android ni tout ton temps d’écran. Ils dépendent des autorisations, du suivi actif et des informations fournies par le téléphone."
    ),
    help(
        SelkieHelpSection.TRACKING, "compatibility", "Quels téléphones et applications sont compatibles ?",
        listOf("compatible", "compatibilite", "android 10", "samsung", "a16", "iphone", "ios", "application introuvable", "application absente", "app absente"),
        "Cette version est destinée à Android 10 ou plus récent, dont les Samsung Galaxy A16. Il n’y a pas de version iPhone active. " +
            "La liste de suivi affiche les applications accessibles depuis le lanceur Android, à l’exception de Selkie. Une application sans icône de lancement peut manquer. Les durées et l’affichage de pause peuvent varier selon Android et les restrictions du constructeur."
    ),
    help(
        SelkieHelpSection.POINTS, "points", "À quoi servent mes points ?",
        listOf("points", "solde", "capital discipline", "points d honneur", "combien de points", "regles des points"),
        "Consulte le menu ☰ → Règles de Selkie pour retrouver les règles. Tu commences avec 100 points et ton solde reste entre 0 et 100. Une réinitialisation coûte 10 points ; une réouverture détectée d’une application verrouillée coûte 5 points. " +
            "Tu peux regagner des points avec le bonus de discipline et les défis, jusqu’au plafond de 100. Le mode Points du Dé Selkie est facultatif : il retire le chiffre obtenu, de 1 à 6 points, après confirmation du lancer, sans passer sous zéro. À 0 point, ajouter une application ou modifier une limite est bloqué pour toutes les offres. Les points ne sont pas de l’argent."
    ),
    help(
        SelkieHelpSection.POINTS, "earn_points", "Comment gagner des points ?",
        listOf("gagner des points", "regagner des points", "recuperer des points", "obtenir des points", "augmenter mes points", "recompenses", "bonus de points"),
        "Trois gains existent : +10 points par période de 24 heures sans dépassement enregistré ; +5 points à chaque groupe de cinq défis standard entièrement terminés ; " +
            "+0,25 point une seule fois à la fin d’un défi personnalisé en Premium ou Ultra. Tous ces gains complètent ton solde jusqu’à 100 points maximum ; les validations restent enregistrées même au plafond. Les journées intermédiaires ne donnent pas de points, et écrire au Coach n’en ajoute pas. Les programmes guidés Ultra ne donnent aucun point supplémentaire."
    ),
    help(
        SelkieHelpSection.POINTS, "lose_points", "Pourquoi ai-je perdu des points ?",
        listOf("perdre des points", "perdu des points", "perte de points", "moins de points", "points retires", "penalite", "reouvrir une application", "rouvrir une application", "de points", "mode points du de", "de d honneur"),
        "Réinitialiser une limite retire 10 points après confirmation. Une réouverture détectée d’une application encore verrouillée peut retirer 5 points ; " +
            "ces pénalités sont espacées d’au moins 30 secondes pour la même application. Atteindre la limite n’enlève pas à lui seul 5 points, mais enregistre un dépassement qui empêche le bonus de discipline de cette période. Dans Quotidien → Dé → Points, tu peux aussi choisir de lancer le dé : après confirmation du lancer, le chiffre obtenu retire de 1 à 6 points, sans passer sous zéro. Ce mode est gratuit et facultatif, sans gain ni paiement. Les trois modes Activité, Application et Semaine ne changent pas tes points."
    ),
    help(
        SelkieHelpSection.POINTS, "zero_points", "Que faire à 0 point ?",
        listOf("zero point", "0 point", "plus de points", "aucun point", "bloque a zero", "a zero", "solde nul"),
        "À 0 point, tu ne peux pas ajouter une application ni modifier une limite, même en Premium ou Ultra. Tu ne peux pas non plus créer, modifier, activer ou supprimer une plage horaire. Réinitialiser demande au moins 10 points. " +
            "Tu peux toujours retirer une application, mettre le suivi en pause, utiliser le Coach et terminer des défis pour regagner des points. Le bonus de discipline peut aussi en ajouter si aucun dépassement n’est enregistré sur la période."
    ),
    help(
        SelkieHelpSection.POINTS, "discipline_bonus", "Quand arrive le bonus de discipline ?",
        listOf("bonus de discipline", "bonus quotidien", "10 points", "dix points", "bonus 24 heures", "periode de discipline", "bonus sans depassement"),
        "Le bonus est de 10 points à la fin d’une période de 24 heures commencée par Selkie, si aucun dépassement ou réouverture pénalisée n’a été enregistré dans cette période. " +
            "Ce n’est pas forcément à minuit. Le solde reste plafonné à 100 points. Le calcul est actualisé lors du suivi ou de l’ouverture de l’application, avec au plus sept périodes rattrapées. Retirer une application ou la réinitialiser n’efface pas un dépassement déjà enregistré."
    ),
    help(
        SelkieHelpSection.CHALLENGES, "challenges", "Comment valider un défi proposé ?",
        listOf("defis", "defi", "valider un defi", "lire 10 pages", "cinq jours", "5 jours", "defis proposes", "bonus des defis"),
        "Dans Défis → Défis proposés, réalise l’activité puis valide la journée. Un défi demande cinq dates différentes, une validation maximum par jour ; les jours peuvent être espacés. " +
            "Les journées intermédiaires ne donnent pas de points. À chaque groupe de cinq défis entièrement terminés, tu gagnes 5 points dans la limite du plafond de 100 ; le total des défis terminés continue entre les séries, même au plafond."
    ),
    help(
        SelkieHelpSection.CHALLENGES, "challenge_cycle", "Quand les défis proposés changent-ils ?",
        listOf("serie de defis", "nouvelle serie", "renouvellement des defis", "defis changent", "21 jours", "vingt et un jours", "progression remise a zero", "jour saute"),
        "Selkie propose dix défis par série, parmi deux groupes qui alternent tous les 21 jours. À ce renouvellement, les validations et la progression de la série sont remises à zéro. " +
            "Tes points et le total de défis entièrement terminés restent conservés. Un jour sauté ne remet pas un défi à zéro avant le renouvellement. Les défis personnalisés ne sont pas réinitialisés par ce changement."
    ),
    help(
        SelkieHelpSection.CHALLENGES, "custom_challenges", "Créer et terminer mon propre défi",
        listOf("defi personnalise", "defis personnalises", "creer un defi", "creer mon defi", "mes propres defis", "mes defis", "trois etapes", "3 etapes", "0 25 point"),
        "Avec Premium ou Ultra, ouvre Défis → Mes défis : choisis un titre, exactement trois étapes et une durée de 2 à 365 jours. Tu peux avoir dix défis personnalisés actifs. " +
            "Chaque journée, coche les trois étapes puis valide ; une seule validation par date, avec des jours espacés possibles. Le défi rapporte 0,25 point au total, une seule fois à la fin, dans la limite d’un solde de 100 points. En Gratuit, sa poursuite nécessite de revenir à une offre qui les permet."
    ),
    help(
        SelkieHelpSection.CHALLENGES, "challenge_photos", "Faut-il une photo pour valider ?",
        listOf("preuve", "photo", "photos", "prendre en photo", "prouver le defi", "preuve du defi", "camera", "appareil photo"),
        "Non : cette version ne demande pas de photo pour valider un défi. Les validations sont déclarées par l’utilisateur et ne sont pas vérifiées automatiquement. " +
            "Selkie ne demande pas l’accès à tes photos ou à l’appareil photo pour les défis. Valide seulement les journées où tu as réalisé l’activité."
    ),
    help(
        SelkieHelpSection.OFFERS, "premium", "Que contient Premium ?",
        listOf("premium", "offres", "offre gratuite", "gratuit", "difference des offres", "combien d applications", "limite d applications", "prix premium", "abonnement", "abonnements", "prix des offres", "prix des abonnements"),
        "Les offres sont dans le menu ☰ → Abonnements. Gratuit permet 10 applications suivies. Premium est prévu à " +
            "${PlanType.PREMIUM.monthlyPriceEuros} €/mois ou ${PlanType.PREMIUM.annualPriceEuros} €/an, avec 50 applications, les défis personnalisés, le détail hebdomadaire par application, 20 plages horaires maximum et un widget. Gratuit inclut le calendrier, le bilan hebdomadaire et jusqu’à 50 favoris préparés. " +
            "Ultra est prévu à ${PlanType.ULTRA.monthlyPriceEuros} €/mois ou ${PlanType.ULTRA.annualPriceEuros} €/an, sans plafond commercial sur les applications compatibles. " +
            "La bibliothèque préparée comprend ${PlanType.FREE.coachQuestionCount} questions en Gratuit, ${PlanType.PREMIUM.coachQuestionCount} en Premium et ${PlanType.ULTRA.coachQuestionCount} en Ultra. La saisie standard, l’aide du Coach et les défis proposés sont inclus dans toutes les offres. Pour installer le widget Premium ou Ultra, appuie longuement sur l’écran d’accueil Android → Widgets → Selkie : tes points et un défi proposé y seront visibles. Le choix d’offre est une démonstration locale dans l’APK DEBUG : aucun abonnement ni prélèvement. L’année correspond à douze mensualités, sans réduction. Les règles de points restent les mêmes, y compris à 0 point."
    ),
    help(
        SelkieHelpSection.OFFERS, "ultra", "Que contient Ultra ?",
        listOf("ultra", "ultra premium", "prix ultra", "applications illimitees", "sans plafond", "tri du suivi", "recherche du suivi", "coach illimite", "programmes guides", "programme sommeil", "programme revisions", "programme etudes", "programme reseaux sociaux"),
        "Ultra est prévu à ${PlanType.ULTRA.monthlyPriceEuros} €/mois ou ${PlanType.ULTRA.annualPriceEuros} €/an : pas de plafond commercial sur les applications compatibles, " +
            "toutes les fonctions Premium, recherche et tri du suivi, et ${PlanType.ULTRA.coachQuestionCount} questions/réponses préparées dans le Coach. " +
            "Dans Quotidien, choisis un programme guidé Sommeil de 7 jours, Études de 14 jours ou Réseaux sociaux de 30 jours. Après avoir réalisé l’activité, valide manuellement : au maximum une validation par jour entre tous les programmes, même si tu changes de programme. Ces validations ne donnent pas de points supplémentaires. La progression reste locale et est conservée si tu changes d’offre ; sa poursuite nécessite Ultra. Aucun paiement n’est possible dans cette version de test, et Ultra ne contourne pas les règles de points."
    ),
    help(
        SelkieHelpSection.OFFERS, "subscription_monthly", "Comment fonctionnerait l’abonnement mensuel ?",
        listOf("mensuel", "mensuelle", "tous les mois", "chaque mois", "par mois", "abonnement mensuel", "renouvellement mensuel", "frequence de paiement"),
        "L’offre mensuelle est prévue à ${PlanType.PREMIUM.monthlyPriceEuros} € pour Premium ou ${PlanType.ULTRA.monthlyPriceEuros} € pour Ultra, facturée chaque mois. " +
            "Lorsqu’elle sera activée, elle se renouvellera automatiquement sauf résiliation ; Google Play affichera le prix, la date et les conditions avant confirmation. " +
            "Dans cette version, aucune souscription, facturation ni reconduction n’est active."
    ),
    help(
        SelkieHelpSection.OFFERS, "subscription_annual", "Quel est le prix de l’offre annuelle ?",
        listOf("annuel", "annuelle", "par an", "par annee", "chaque annee", "tous les ans", "abonnement annuel", "reduction", "120 euros", "240 euros"),
        "L’offre annuelle est prévue à ${PlanType.PREMIUM.annualPriceEuros} € pour Premium ou ${PlanType.ULTRA.annualPriceEuros} € pour Ultra, payés en une fois pour un an. " +
            "Cela correspond à douze mensualités, sans réduction, avec les mêmes fonctions. " +
            "Elle se renouvellera chaque année sauf résiliation lorsque les paiements seront activés. Aucun abonnement annuel n’est vendu ici."
    ),
    help(
        SelkieHelpSection.OFFERS, "cancel_subscription", "Comment arrêter un abonnement ?",
        listOf("resilier", "resiliation", "annuler mon abonnement", "annuler l abonnement", "arreter mon abonnement", "arreter l abonnement", "desabonner", "desabonnement", "annulation", "desinstaller selkie annule"),
        "Aucun abonnement réel n’est créé dans cette version. Pour un futur abonnement acheté sur Google Play : ouvre Play Store → ta photo de profil → Paiements et abonnements → Abonnements → Selkie → Annuler l’abonnement. " +
            "Cela arrête le prochain renouvellement ; l’accès reste habituellement ouvert jusqu’à la fin de la période déjà payée. Désinstaller Selkie ou supprimer ses données n’annule pas un abonnement. Une résiliation ou un mois déjà utilisé ne signifie pas un remboursement automatique. Les droits obligatoires restent applicables : voir Confidentialité et données → Abonnements et résiliation."
    ),
    help(
        SelkieHelpSection.OFFERS, "restore_subscription", "Puis-je restaurer un achat ?",
        listOf("restaurer", "restauration", "restaurer mon achat", "restaurer abonnement", "recuperer mon abonnement", "deja paye", "achat introuvable", "premium disparu"),
        "Aucun achat réel ni restauration d’abonnement n’est actif dans cette version : l’offre choisie est seulement enregistrée sur le téléphone pour les essais. " +
            "Une future restauration devra vérifier l’achat avec le compte Google Play utilisé et les droits côté serveur. Elle ne restaurera pas automatiquement tes points, limites ou défis : cette sauvegarde des données n’existe pas encore."
    ),
    help(
        SelkieHelpSection.OFFERS, "payments", "Comment paiera-t-on les abonnements ?",
        listOf("payer", "paiement", "paiements", "prelevement", "facturation", "carte bancaire", "google play billing", "google pay", "stripe", "acheter un abonnement"),
        "Dans cette version, aucun paiement n’est possible et essayer une offre de démonstration ne prélève rien. Pour les abonnements numériques de la future version distribuée sur le Play Store, " +
            "l’intégration prévue est Google Play Billing, avec validation des achats côté serveur. Google Pay est un portefeuille de paiement distinct. Selkie ne doit pas te demander ton numéro de carte dans le Coach."
    ),
    help(
        SelkieHelpSection.ACCOUNT, "account", "Faut-il créer un compte ?",
        listOf("compte", "connexion", "connecter", "inscription", "creer un compte", "ouvrir un compte", "mot de passe", "identifiant", "se connecter"),
        "Tu peux utiliser Selkie et son Coach local sans compte. La création de compte et la connexion à un serveur ne sont pas encore actives. " +
            "Un compte est prévu pour faciliter la gestion des droits d’abonnement et un changement de téléphone ; les données de suivi restent locales dans cette version. " +
            "Un compte Selkie n’est pas, à lui seul, un paiement ni une sauvegarde, et Google Play peut gérer un achat sans compte Selkie séparé."
    ),
    help(
        SelkieHelpSection.ACCOUNT, "single_device", "Puis-je utiliser le compte sur plusieurs téléphones ?",
        listOf("plusieurs telephones", "plusieurs appareils", "deux telephones", "deux appareils", "un seul appareil", "un seul telephone", "partager mon compte", "compte partage", "multi appareil"),
        "La règle prévue est un seul téléphone actif par compte Selkie : activer un nouveau téléphone devra remplacer l’ancien, sans usage simultané du même compte. " +
            "Cette restriction nécessite des comptes et une vérification serveur ; elle n’est pas encore appliquée. Dans la version actuelle, chaque installation garde ses propres données locales et aucun compte commun ne les synchronise."
    ),
    help(
        SelkieHelpSection.ACCOUNT, "change_phone", "Que se passe-t-il si je change de téléphone ?",
        listOf("changer de telephone", "nouveau telephone", "autre telephone", "transfert", "transferer", "sauvegarde", "synchronisation", "recuperer mes donnees", "migrer"),
        "Aujourd’hui, tes points, applications et défis restent sur le téléphone : Selkie n’a pas de sauvegarde ou synchronisation serveur active, et sa sauvegarde Android est désactivée. " +
            "Un changement d’appareil peut donc perdre cette progression. Le futur compte devra permettre de remplacer l’appareil actif et vérifier un éventuel abonnement ; le transfert des données de suivi devra être développé séparément."
    ),
    help(
        SelkieHelpSection.PRIVACY, "privacy", "Où sont mes données et la confidentialité ?",
        listOf("confidentialite", "politique de confidentialite", "donnees personnelles", "mes donnees", "rgpd", "cnil", "responsable", "nom et prenom", "nom prenom", "mon vrai nom", "vie privee", "messages lus"),
        "Lis la politique de Selkie 0.12.0 dans le menu ☰ → Confidentialité et données. Tes applications suivies, limites, points, défis, historique de 90 jours, plages horaires, favoris et programmes restent dans le stockage privé du téléphone ; le Coach local ne transmet pas tes questions à l’éditeur. " +
            "Selkie ne lit ni les messages de tes autres applications, ni tes photos, ni le contenu de l’écran. La politique doit permettre d’identifier le responsable réel et de le contacter : ici, " +
            "${PrivacyIdentity.controllerName}, ${PrivacyIdentity.contactEmail}. Elle décrit cette version ; l’ouverture de comptes ou paiements nécessitera une mise à jour."
    ),
    help(
        SelkieHelpSection.PRIVACY, "delete_data", "Comment effacer mes données ?",
        listOf("effacer mes donnees", "effacer toutes mes donnees", "supprimer mes donnees", "supprimer toutes mes donnees", "effacement", "tout effacer", "repartir de zero", "suppression de donnees", "effacer mon compte", "supprimer mon compte"),
        "Dans le menu ☰ → Confidentialité et données → Effacer mes données, confirme pour arrêter le suivi et supprimer les réglages, points, défis, historique, plages horaires, favoris, programmes, choix locaux et questions du Coach sauvegardées. " +
            "C’est irréversible ; les points reviennent à leur valeur initiale à la prochaine utilisation. Les statistiques et autorisations Android restent inchangées. " +
            "Aucun compte serveur actif n’est supprimé ici, et cet effacement n’annulerait pas un futur abonnement Google Play. Exporter mes données crée une copie JSON locale avec les observations, règles, favoris préparés et identifiants et dates de programmes, sans saisie brute du Coach, jeton ni adresse e-mail. Une copie exportée doit être supprimée séparément."
    ),
    help(
        SelkieHelpSection.PRIVACY, "contact", "Comment contacter Selkie ?",
        listOf("contact", "contacter", "email", "e mail", "courriel", "support", "assistance", "ecrire au developpeur", "adresse mail", "signaler un probleme"),
        "Pour une question ou un problème, écris à ${PrivacyIdentity.contactEmail}. Le bouton Contacter par e-mail se trouve dans le menu ☰ → Confidentialité et données. " +
            "Explique le problème et, si utile, la version de Selkie et ton modèle de téléphone. Évite d’envoyer des mots de passe, coordonnées bancaires ou captures contenant des informations privées. " +
            "Les phrases du Coach ne sont pas transmises automatiquement à cette adresse."
    )
)

internal fun helpEntriesFor(language: AppLocale): List<SelkieHelpEntry> = when (language) {
    AppLocale.FRENCH -> selkieHelpEntriesFrench
    AppLocale.ENGLISH -> selkieHelpEntriesEnglish
}

internal val selkieHelpEntries: List<SelkieHelpEntry>
    get() = helpEntriesFor(AppLanguage.current)

internal val selkieHelpTopics: List<CoachTopic>
    get() = selkieHelpEntries.map { it.topic }

private fun help(
    section: SelkieHelpSection,
    id: String,
    label: String,
    keywords: List<String>,
    answer: String
): SelkieHelpEntry = SelkieHelpEntry(section, CoachTopic(
    id = id, label = label, keywords = keywords, replies = listOf(answer), appHelp = true
))
