package com.maree.app

/** Conseils préparés, utilisés uniquement sur le téléphone par le coach à règles. */
internal val coachTopics: List<CoachTopic> = listOf(
    CoachTopic(
        id = "tiktok", label = "TikTok",
        keywords = listOf("tiktok", "tik tok", "tiktoc", "pour toi", "scroll tiktok"),
        replies = listOf(
            "Avant d’ouvrir TikTok, choisis ce que tu viens regarder et une heure de sortie. Quand l’heure arrive, pose le téléphone et change de pièce.",
            "Le fil TikTok propose toujours une vidéo de plus. Essaie de fermer l’application à la fin de la vidéo actuelle, puis fais une pause de cinq minutes.",
            "Pour réduire TikTok sans tout supprimer, retire son raccourci de l’écran d’accueil et coupe ses notifications. Une petite difficulté à l’ouverture peut aider."
        ),
        appAliases = listOf("tiktok", "tik tok", "tiktok lite")
    ),
    CoachTopic(
        id = "instagram", label = "Instagram",
        keywords = listOf("instagram", "insta", "reels", "stories", "story"),
        replies = listOf(
            "Sur Instagram, sépare les messages du fil : réponds aux personnes que tu voulais contacter, puis ferme l’application avant de passer aux Reels.",
            "Choisis un moment précis pour consulter Instagram. En dehors de ce moment, coupe les notifications qui t’invitent à revenir sans raison.",
            "Si les Reels t’entraînent longtemps, essaie une pause juste après celui que tu regardes. Mets le téléphone hors de portée pendant quelques minutes."
        ),
        appAliases = listOf("instagram", "insta", "instagram lite")
    ),
    CoachTopic(
        id = "youtube", label = "YouTube",
        keywords = listOf("youtube", "you tube", "youtub", "shorts", "lecture automatique"),
        replies = listOf(
            "Sur YouTube, choisis une vidéo avant d’ouvrir le fil. Désactive la lecture automatique pour que la fin de cette vidéo devienne une vraie sortie.",
            "Pour les Shorts, prépare une activité très courte après la consultation : boire un verre d’eau ou faire quelques pas. Commence dès que tu fermes YouTube.",
            "Tu peux noter les vidéos intéressantes dans une liste à regarder plus tard. Choisis-en une seule maintenant, puis garde les autres pour un autre moment."
        ),
        appAliases = listOf("youtube", "you tube", "youtube go")
    ),
    CoachTopic(
        id = "snapchat", label = "Snapchat",
        keywords = listOf("snapchat", "snap", "flammes", "snap streak", "snap map"),
        replies = listOf(
            "Sur Snapchat, tu peux choisir de garder les échanges qui comptent et de couper les alertes du reste. Tu n’as pas besoin de répondre à chaque notification.",
            "Si les flammes te font revenir sans envie, préviens un ami que tu fais une pause. Une série dans une application n’est pas une obligation envers lui.",
            "Essaie de consulter Snapchat à un moment choisi plutôt qu’à chaque alerte. Réponds à tes messages, puis ferme avant d’ouvrir le contenu suggéré."
        ),
        appAliases = listOf("snapchat", "snap")
    ),
    CoachTopic(
        id = "whatsapp", label = "WhatsApp",
        keywords = listOf("whatsapp", "what sapp", "groupe whatsapp", "groupes whatsapp", "messages whatsapp"),
        replies = listOf(
            "Sur WhatsApp, mets en silencieux les groupes qui interrompent souvent ta journée. Garde les appels ou messages importants selon tes besoins.",
            "Tu peux prévenir tes proches que tu répondras à un moment précis. Cela rend la pause plus simple sans couper les échanges utiles.",
            "Avant d’ouvrir WhatsApp, choisis la conversation que tu viens lire. Réponds à cette personne, puis reviens à ce que tu faisais."
        ),
        appAliases = listOf("whatsapp", "whatsapp business")
    ),
    CoachTopic(
        id = "games", label = "Jeux",
        keywords = listOf("jeux", "jeu video", "jeux video", "gaming", "free fire", "roblox", "fortnite", "minecraft", "brawl stars", "pubg"),
        replies = listOf(
            "Avant de lancer un jeu, choisis une fin claire : une partie ou une heure précise. Préviens tes amis avant de commencer pour faciliter ton départ.",
            "Une partie terminée est un bon moment pour faire une pause. Lève-toi avant de relancer : tu pourras décider plus tranquillement si tu veux continuer.",
            "Garde du temps pour jouer, mais prépare aussi ce qui vient après. Par exemple : une partie, puis dix minutes de marche ou une tâche courte."
        ),
        appAliases = listOf("free fire", "roblox", "fortnite", "minecraft", "brawl stars", "pubg", "clash of clans", "candy crush")
    ),
    CoachTopic(
        id = "wake_up", label = "Au réveil",
        keywords = listOf("reveil", "au reveil", "des le matin", "premiere chose le matin", "lever du lit"),
        replies = listOf(
            "Demain au réveil, essaie de faire une seule chose avant d’ouvrir un réseau : boire un verre d’eau, ouvrir les volets ou te préparer.",
            "Prépare ce soir un petit début de matinée sans écran. Même cinq minutes avant la première consultation peuvent créer une nouvelle habitude.",
            "Si tu consultes ton téléphone dès le réveil, pose-le un peu plus loin cette nuit. Garde ton réveil audible et commence par te lever."
        )
    ),
    CoachTopic(
        id = "meals", label = "Pendant les repas",
        keywords = listOf("repas", "manger", "dejeuner", "diner", "petit dejeuner", "a table"),
        replies = listOf(
            "Pour le prochain repas, pose le téléphone hors de la table. Essaie simplement de le laisser là jusqu’à la fin, sans viser toute la journée.",
            "Choisis un repas sans écran aujourd’hui. Si tu manges avec quelqu’un, propose de ranger les téléphones ensemble.",
            "Si manger sans écran te semble difficile, commence par les cinq premières minutes. Regarde ton repas ou parle avec la personne à côté de toi."
        )
    ),
    CoachTopic(
        id = "evening", label = "La soirée",
        keywords = listOf("soiree", "le soir", "fin de journee", "apres le diner", "rentrer chez moi"),
        replies = listOf(
            "Pour ce soir, prévois une activité avant de t’installer avec ton téléphone : musique, lecture ou rangement pendant dix minutes.",
            "Choisis un moment de consultation et un moment de fermeture pour la soirée. Pose ensuite le téléphone à un endroit où il ne reste pas dans ta main.",
            "Une soirée moins chargée d’écran peut commencer par un petit créneau. Essaie vingt minutes pour une activité que tu apprécies hors du téléphone."
        )
    ),
    CoachTopic(
        id = "sleep", label = "Le sommeil",
        keywords = listOf("sommeil", "dormir", "endormir", "coucher", "insomnie", "nuit blanche", "arrive pas a dormir", "je n arrive pas a dormir", "arrive pas a m endormir", "du mal a dormir"),
        replies = listOf(
            "Essaie de choisir une heure pour poser le téléphone avant de dormir. Prépare une autre activité calme, comme lire quelques pages ou écouter de la musique.",
            "Pour ce soir, évite d’emporter le fil de vidéos dans le lit. Pose le téléphone sur une table éloignée et garde ce dont tu as besoin pour la nuit.",
            "Tu peux commencer par une courte pause d’écran avant le coucher. Si tes difficultés de sommeil durent ou t’inquiètent, demande conseil à un professionnel de santé."
        )
    ),
    CoachTopic(
        id = "work", label = "Au travail",
        keywords = listOf("travail", "travailler", "bureau", "reunion", "concentration", "productivite"),
        replies = listOf(
            "Choisis une tâche précise et fais dix minutes dessus avec le téléphone hors de portée. Tu pourras ensuite décider de continuer ou de faire une pause.",
            "Avant de travailler, coupe les alertes inutiles et garde seulement les contacts nécessaires. Fixe un moment pour consulter les messages.",
            "Note sur papier la prochaine petite action de ton travail. Quand tu veux ouvrir un réseau, reviens à cette action pendant deux minutes."
        )
    ),
    CoachTopic(
        id = "school", label = "École et révisions",
        keywords = listOf("ecole", "lycee", "college", "cours", "devoirs", "revisions", "reviser", "examens"),
        replies = listOf(
            "Pour tes révisions, commence par un exercice ou une page avec le téléphone rangé. Fais une pause après ce petit objectif plutôt qu’au milieu.",
            "Si tu as besoin du téléphone pour un cours, ouvre seulement le document utile. Tu peux noter les autres envies et les garder pour la pause.",
            "Prépare une courte séance de devoirs : matériel prêt, une tâche choisie et notifications coupées. Dix minutes pour démarrer, c’est déjà un début."
        )
    ),
    CoachTopic(
        id = "boredom", label = "L’ennui",
        keywords = listOf("ennui", "ennuie", "ennuyer", "rien a faire", "je sais pas quoi faire", "je ne sais pas quoi faire"),
        replies = listOf(
            "Quand tu t’ennuies, choisis une activité de deux minutes : dessiner, ranger un coin ou faire quelques pas. Tu n’as pas besoin d’un grand projet.",
            "Prépare une petite liste de choses agréables hors écran. Quand l’ennui arrive, essaie la plus facile avant d’ouvrir un fil.",
            "Tu peux aussi laisser l’ennui passer quelques instants. Pose le téléphone, regarde autour de toi et choisis ensuite ce que tu veux vraiment faire."
        )
    ),
    CoachTopic(
        id = "stress", label = "Le stress",
        keywords = listOf("stress", "stresse", "angoisse", "anxieux", "pression", "tendu", "panique"),
        replies = listOf(
            "Si tu ouvres le téléphone quand tu es stressé, essaie d’abord une courte pause : pose-le, respire tranquillement et nomme ce qui te préoccupe.",
            "Choisis une petite chose concrète que tu peux faire maintenant. Un message à une personne de confiance ou quelques minutes de marche peuvent être un début.",
            "Un fil de vidéos peut distraire un moment sans résoudre ce qui te pèse. Si le stress devient difficile à vivre ou revient souvent, parle-en à une personne de confiance ou à un professionnel."
        )
    ),
    CoachTopic(
        id = "loneliness", label = "La solitude",
        keywords = listOf("solitude", "seul", "seule", "isole", "personne a qui parler", "manque de compagnie"),
        replies = listOf(
            "Si tu cherches de la compagnie sur le téléphone, essaie un échange direct : propose à une personne un appel ou une petite sortie.",
            "Tu peux envoyer un message simple à quelqu’un : « Tu as quelques minutes pour parler ? » Un échange choisi peut être plus agréable qu’un long fil.",
            "Choisis une petite occasion de voir du monde : une activité, un proche ou un lieu que tu apprécies. Si la solitude te pèse beaucoup, parle-en à quelqu’un de confiance."
        )
    ),
    CoachTopic(
        id = "fomo", label = "Peur de rater quelque chose",
        keywords = listOf("fomo", "peur de rater", "peur de manquer", "rater quelque chose", "manquer quelque chose", "tout savoir", "rester au courant"),
        replies = listOf(
            "Tu peux choisir quand te tenir au courant plutôt que vérifier sans cesse. Fixe un prochain moment de consultation et laisse les alertes inutiles en silence.",
            "Demande-toi ce qui serait vraiment urgent. Pour le reste, une consultation plus tard suffit souvent ; garde un moyen de contact pour les personnes importantes.",
            "Essaie une courte pause de dix minutes sans vérifier. À ton retour, remarque ce qui nécessitait réellement ton attention."
        )
    ),
    CoachTopic(
        id = "tiredness", label = "La fatigue",
        keywords = listOf("fatigue", "fatiguee", "epuise", "epuisee", "sans energie", "plus d energie"),
        replies = listOf(
            "Quand tu es fatigué, choisis une pause qui demande peu d’effort : poser le téléphone, boire de l’eau ou t’asseoir quelques minutes au calme.",
            "Tu n’as pas besoin de remplir chaque pause avec un écran. Essaie quelques minutes sans contenu, puis décide de ce dont tu as besoin.",
            "Pour aujourd’hui, garde un objectif d’écran simple plutôt qu’un grand changement. Si une fatigue inhabituelle dure ou t’inquiète, demande conseil à un professionnel de santé."
        )
    ),
    CoachTopic(
        id = "notifications", label = "Les notifications",
        keywords = listOf("notifications", "notification", "alertes", "alerte", "telephone sonne", "vibre", "vibrations"),
        replies = listOf(
            "Dans les paramètres Android, cherche Notifications et choisis une application qui t’interrompt souvent. Coupe une catégorie inutile en gardant les alertes importantes.",
            "Commence par les notifications de recommandations, de promotions ou de nouveautés. Tu pourras toujours ouvrir l’application au moment choisi.",
            "Essaie une période en mode Ne pas déranger. Vérifie les exceptions pour laisser passer les appels ou contacts dont tu as besoin."
        )
    ),
    CoachTopic(
        id = "grayscale", label = "Écran en noir et blanc",
        keywords = listOf("noir et blanc", "niveaux de gris", "ecran gris", "grayscale", "sans couleurs"),
        replies = listOf(
            "Sur ton Samsung, ouvre les paramètres et cherche « niveaux de gris ». Selon la version, le réglage peut être dans Accessibilité ou dans un mode de coucher.",
            "Tu peux essayer les niveaux de gris sur une courte période pour voir si les fils t’attirent moins. Ce réglage n’est pas un blocage et tu peux le remettre en couleur.",
            "Cherche « niveaux de gris » dans les paramètres de ton téléphone. Le nom et l’emplacement varient selon Android ; je ne change pas ce réglage à ta place."
        )
    ),
    CoachTopic(
        id = "bedroom", label = "Téléphone dans la chambre",
        keywords = listOf("chambre", "dans le lit", "pres du lit", "loin du lit", "table de nuit", "dormir avec mon telephone"),
        replies = listOf(
            "Prépare un endroit pour charger le téléphone loin du lit. Si tu l’utilises comme réveil, assure-toi de pouvoir l’entendre ou prévois un autre réveil.",
            "Essaie de garder le lit pour te reposer plutôt que pour parcourir un fil. Commence en posant le téléphone sur une table hors de portée.",
            "Tu peux tester une seule nuit avec le téléphone hors de la chambre. Garde accessibles les moyens de contact ou de réveil dont tu as réellement besoin."
        )
    ),
    CoachTopic(
        id = "uninstall", label = "Supprimer une application",
        keywords = listOf("desinstaller", "desinstallation", "supprimer une application", "supprimer une app", "enlever une application", "retirer du telephone"),
        replies = listOf(
            "Avant de désinstaller, vérifie si tu veux garder des fichiers ou tes identifiants. Sur Android, un appui long sur l’icône permet souvent d’accéder à la désinstallation.",
            "Tu peux commencer par retirer le raccourci de l’écran d’accueil et couper les notifications. Si cela ne suffit pas, décide ensuite d’une désinstallation.",
            "Retirer une application du suivi Selkie ne la désinstalle pas. Pour la supprimer du téléphone, passe par les paramètres Android de cette application."
        )
    ),
    CoachTopic(
        id = "distance", label = "Mettre le téléphone à distance",
        keywords = listOf("hors de portee", "loin de moi", "autre piece", "ranger mon telephone", "poser mon telephone", "telephone loin"),
        replies = listOf(
            "Choisis un endroit précis pour poser le téléphone pendant ta prochaine activité. Le mettre hors de portée évite de le reprendre sans y penser.",
            "Essaie dix minutes avec le téléphone dans une autre pièce. Garde les appels nécessaires audibles si tu en as besoin.",
            "Quand tu termines une consultation, pose le téléphone au même endroit. Cette petite routine peut rendre la sortie du fil plus facile."
        )
    ),
    CoachTopic(
        id = "breaks", label = "Faire une pause",
        keywords = listOf("faire une pause", "pause ecran", "pause sans ecran", "pause du telephone", "deconnecter", "deconnexion"),
        replies = listOf(
            "Choisis une pause courte et fais quelque chose avec tes mains ou tes pieds : marcher, dessiner ou préparer une boisson. Tu peux commencer par cinq minutes.",
            "Pour ta prochaine pause, essaie de laisser le téléphone posé. Une pause de travail n’a pas besoin de devenir une autre séance d’écran.",
            "Prépare une sortie simple du téléphone : ferme l’application, lève-toi et fais quelques pas. Tu décideras ensuite si tu veux revenir."
        )
    ),
    CoachTopic(
        id = "add_app", label = "Ajouter une application au suivi",
        keywords = listOf("ajouter une application", "ajouter une app", "ajouter au suivi", "suivre une application", "suivre une app", "bouton ajouter", "selectionner une application"),
        replies = listOf(
            "Dans Accueil, appuie sur Ajouter, choisis une application compatible et fixe sa limite. Il faut un solde supérieur à 0 point pour l’ajouter.",
            "Selkie permet de suivre 10 applications en Gratuit et 50 en Premium. Ultra n’a pas de plafond commercial pour les applications compatibles ; l’ajout reste bloqué à 0 point.",
            "Si une application n’apparaît pas dans la liste, elle peut ne pas être compatible avec le suivi. Vérifie aussi les autorisations depuis Plus avant d’activer le suivi."
        )
    ),
    CoachTopic(
        id = "limits", label = "Fixer une limite",
        keywords = listOf("fixer une limite", "choisir une limite", "modifier une limite", "changer ma limite", "limite quotidienne", "combien de minutes choisir", "regler une limite"),
        replies = listOf(
            "Choisis une limite réaliste pour commencer. Dans Selkie, les durées sont des multiples de 5 minutes, avec un minimum de 5 minutes.",
            "Pour modifier une limite, ouvre le menu de l’application suivie dans Accueil. À 0 point, la modification est bloquée dans toutes les offres.",
            "Commence par une application qui te prend souvent plus de temps que prévu. Fixe une limite que tu peux essayer aujourd’hui, puis observe comment elle te convient."
        )
    ),
    CoachTopic(
        id = "red_screen", label = "L’écran de pause",
        keywords = listOf("ecran rouge", "ecran de pause", "ecran bloque", "ecran de blocage", "verrou", "application bloquee", "bloque 24 heures"),
        replies = listOf(
            "L’écran de pause apparaît quand une application suivie atteint sa limite, si le suivi et les autorisations sont actifs. Le verrou dure 24 heures à partir du dépassement.",
            "Quand l’écran de pause apparaît, ferme l’application et choisis une activité hors écran. Tu peux voir le temps de verrou restant sur sa carte dans Accueil.",
            "Le verrou Selkie dure 24 heures après la limite atteinte. Une réinitialisation peut le lever, mais elle coûte 10 points et nécessite ce solde."
        )
    ),
    CoachTopic(
        id = "reset", label = "Réinitialiser une limite",
        keywords = listOf("reinitialiser", "reinitialisation", "reset", "lever le verrou", "debloquer", "remettre le compteur", "recommencer ma limite", "je veux reinitialiser", "comment reinitialiser"),
        replies = listOf(
            "Réinitialiser une application coûte 10 points. Cela lève son verrou et remet à disposition sa limite complète, sans effacer les statistiques Android.",
            "Dans Accueil, ouvre le menu de l’application puis Réinitialiser. Selkie te demande confirmation avant de retirer les 10 points.",
            "La réinitialisation n’est pas gratuite en Premium ou Ultra. Il faut au moins 10 points dans toutes les offres ; sinon, garde la pause ou termine des défis pour en regagner."
        )
    ),
    CoachTopic(
        id = "points", label = "Mes points",
        keywords = listOf("points", "solde", "capital discipline", "points d honneur", "zero point", "0 point", "gagner des points", "perdre des points"),
        replies = listOf(
            "Tes points font partie des règles Selkie. À 0 point, l’ajout et la modification d’une limite sont bloqués dans toutes les offres. Tu peux toujours retirer une application du suivi.",
            "Pour les défis standard, cinq défis entièrement terminés donnent un bonus de 5 points. Les validations des journées intermédiaires n’ajoutent pas de points.",
            "Une réinitialisation coûte 10 points. Les défis personnalisés disponibles en Premium et Ultra rapportent 0,25 point au total, une seule fois quand ils sont terminés."
        )
    ),
    CoachTopic(
        id = "challenges", label = "Les défis",
        keywords = listOf("defis", "defi", "valider un defi", "lire 10 pages", "cinq jours", "5 jours", "serie de defis", "bonus des defis"),
        replies = listOf(
            "Un défi standard se valide sur cinq dates différentes, une fois par jour. Les jours peuvent être espacés ; valide seulement les journées où tu l’as réellement fait.",
            "Dans Défis, choisis une activité facile à répéter. Termine ses cinq journées pour finir le défi ; chaque groupe de cinq défis terminés donne 5 points.",
            "Les journées intermédiaires d’un défi standard ne donnent pas de points. Le bonus arrive après cinq défis entièrement terminés, et les défis terminés se cumulent entre les séries."
        )
    ),
    CoachTopic(
        id = "custom_challenges", label = "Créer mon défi",
        keywords = listOf("defi personnalise", "defis personnalises", "creer un defi", "creer mon defi", "mes propres defis", "trois etapes", "3 etapes", "0 25 point"),
        replies = listOf(
            "En Premium ou Ultra, tu peux créer un défi avec exactement trois étapes et une durée de 2 à 365 jours. Choisis des étapes simples que tu peux refaire chaque journée.",
            "Pour valider une journée de ton défi personnalisé, termine ses trois étapes. Le défi rapporte 0,25 point au total, une seule fois à la fin de la durée choisie.",
            "Un défi personnalisé dure au moins deux jours et contient trois étapes. Par exemple : préparer ton espace, faire quelques exercices adaptés et ranger ton matériel. Le bonus final reste 0,25 point."
        )
    ),
    CoachTopic(
        id = "premium", label = "Premium et Ultra",
        keywords = listOf("premium", "ultra", "abonnement", "payer", "paiement", "offres", "gratuit", "prix"),
        replies = listOf(
            "Les offres prévues sont Gratuit avec 10 applications, Premium à 10 €/mois avec 50, et Ultra à 20 €/mois sans plafond commercial sur les applications compatibles. Les paiements ne sont pas encore actifs.",
            "Premium et Ultra permettent les défis personnalisés. Aucune offre ne permet de contourner les règles à 0 point, et une réinitialisation coûte toujours 10 points.",
            "Le choix d’offre de cette version est une démonstration locale : il ne crée aucun abonnement et ne prélève rien. Ce coach à conseils préparés fonctionne sur le téléphone sans service IA payant."
        )
    ),
    CoachTopic(
        id = "permissions", label = "Les autorisations",
        keywords = listOf("autorisation", "autorisations", "permission", "permissions", "acces utilisation", "acces aux donnees d utilisation", "superposition", "par dessus", "apparaitre au dessus"),
        replies = listOf(
            "Depuis Plus, ouvre Gérer les autorisations. L’accès aux données d’utilisation sert à mesurer les applications suivies ; l’affichage par-dessus les autres applications permet l’écran de pause.",
            "Tu peux vérifier les autorisations dans Plus, puis décider de les activer dans les paramètres Android. Je ne les accorde pas et je ne change pas tes réglages à ta place.",
            "Sans les autorisations nécessaires, Selkie ne peut pas assurer le suivi et l’écran de pause. Tu peux lire les explications avant de choisir ; le coach à conseils préparés reste utilisable sans ces accès."
        )
    ),
    CoachTopic(
        id = "pause_tracking", label = "Mettre le suivi en pause",
        keywords = listOf("mettre le suivi en pause", "pause du suivi", "arreter le suivi", "desactiver le suivi", "reprendre le suivi", "activer le suivi", "suivi en pause"),
        replies = listOf(
            "Dans Plus, choisis Mettre le suivi en pause. Selkie arrête alors le suivi et les écrans de pause ; tes applications et réglages restent enregistrés.",
            "Pour reprendre, ouvre Plus puis Activer le suivi. Vérifie les autorisations demandées avant de revenir à tes limites.",
            "Quand le suivi est en pause, les anciennes mesures ne prouvent pas ton usage actuel. Reprends le suivi depuis Plus si tu veux mesurer et appliquer tes limites."
        )
    ),
    CoachTopic(
        id = "usage", label = "Mon utilisation",
        keywords = listOf("mon utilisation", "temps d ecran", "combien de minutes", "minutes utilisees", "usage aujourd hui", "statistiques", "voir mon usage", "quotidien"),
        replies = listOf(
            "Consulte les cartes dans Accueil et la rubrique Quotidien pour voir les informations disponibles. Les mesures dépendent du suivi actif et des autorisations Android.",
            "Les minutes d’une application suivie sont mesurées à partir de son ajout ou de sa dernière réinitialisation. Elles peuvent donc différer du total affiché par Android.",
            "Regarde d’abord une application et le moment où tu l’ouvres souvent. Choisis ensuite une petite limite ou une pause adaptée, plutôt que de tout changer en même temps."
        )
    ),
    CoachTopic(
        id = "exceeded", label = "Limite dépassée",
        keywords = listOf("limite depassee", "depasse ma limite", "depasse la limite", "limite atteinte", "trop scrolle", "trop utilise", "encore depasse"),
        replies = listOf(
            "Une limite dépassée est l’occasion de faire une pause maintenant. Ferme l’application, pose le téléphone et choisis une activité courte hors écran.",
            "Essaie de repérer le moment où tu as continué plus longtemps que prévu. Pour la prochaine fois, prépare une sortie précise : fin d’une vidéo, d’un message ou d’une partie.",
            "Tu peux repartir avec un petit objectif pour le reste de la journée. Si Selkie a verrouillé l’application, respecte la pause plutôt que de chercher immédiatement à la réinitialiser."
        )
    ),
    CoachTopic(
        id = "relapse", label = "Reprendre après une rechute",
        keywords = listOf("rechute", "rechuter", "recommence a scroller", "rate mon objectif", "echec", "culpabilise", "tout rate", "j ai craque"),
        replies = listOf(
            "Une journée difficile n’efface pas tes efforts précédents. Choisis une seule petite action maintenant, comme poser le téléphone pendant cinq minutes.",
            "Au lieu de te juger, regarde ce qui a rendu la pause difficile : une alerte, l’ennui ou un moment de fatigue. Prépare une aide simple pour la prochaine fois.",
            "Reprends avec un objectif plus facile aujourd’hui. Tu peux aussi choisir un défi standard ; ses cinq journées n’ont pas besoin d’être consécutives."
        )
    ),
    CoachTopic(
        id = "streak", label = "Les jours réussis",
        keywords = listOf("serie de jours", "jours reussis", "jours consecutifs", "garder ma serie", "rompu ma serie", "streak", "regularite"),
        replies = listOf(
            "Pour garder une habitude, prépare une petite action que tu peux répéter même les jours chargés. Une journée moins réussie ne t’empêche pas de reprendre demain.",
            "Les défis standard demandent cinq dates différentes, avec des jours espacés possibles. Tu n’as pas besoin d’une série parfaite de jours consécutifs pour les terminer.",
            "Regarde ce qui t’a aidé les jours où ta pause a tenu, puis reproduis ce détail. Les jours intermédiaires d’un défi n’ajoutent pas de points : le bonus suit les règles des défis terminés."
        )
    ),
    CoachTopic(
        id = "start", label = "Commencer",
        keywords = listOf("commencer", "debuter", "par ou commencer", "premier pas", "bonjour", "salut", "aide moi", "coach", "qui es tu", "intelligence artificielle"),
        replies = listOf(
            "Je propose des conseils préparés sur le téléphone, pas des réponses d’une IA générative. Pour commencer, choisis un sujet : une application, le sommeil ou une petite pause.",
            "Commence par une seule habitude : choisir un moment pour poser le téléphone ou suivre une application dans Selkie. Dis-moi ce qui te semble le plus difficile.",
            "Mon aide repose sur des sujets et réponses prévus à l’avance. Tu peux me parler de TikTok, des défis, de tes points ou d’un moment où tu voudrais moins consulter ton téléphone."
        )
    ),
    CoachTopic(
        id = "goals", label = "Choisir un objectif",
        keywords = listOf("objectif", "objectifs", "scroller", "scroll", "defilement", "moins scroller", "scroll sans reflechir", "je scrolle sans reflechir", "arreter de scroller", "reduire mon ecran", "reduire le temps", "bonne habitude", "changer mes habitudes", "envie de regarder", "j arrive pas a m arreter"),
        replies = listOf(
            "Choisis un objectif précis pour aujourd’hui : un repas sans écran, dix minutes de lecture ou une consultation à heure fixée. Garde-le assez petit pour pouvoir l’essayer.",
            "Formule ton objectif avec un moment et une action : « Après le repas, je pose mon téléphone et je marche cinq minutes. » Ce sera plus simple à suivre.",
            "Pour réduire le défilement, prépare ce que tu feras à la place. Une pause devient plus facile quand une activité concrète t’attend."
        )
    ),
    CoachTopic(
        id = "success", label = "Une réussite",
        keywords = listOf("j ai reussi", "objectif atteint", "j ai tenu", "fier de moi", "fiere de moi", "moins utilise aujourd hui", "victoire", "progres"),
        replies = listOf(
            "Tu peux prendre un moment pour reconnaître cette réussite. Note ce qui t’a aidé aujourd’hui afin de pouvoir le refaire demain.",
            "Garde le même petit objectif encore un peu avant de le rendre plus difficile. Une habitude utile se construit aussi en répétant ce qui fonctionne.",
            "Profite du temps retrouvé pour quelque chose qui te plaît. Je n’ajoute pas de points depuis la conversation : les récompenses suivent les règles de Selkie."
        )
    )
)
