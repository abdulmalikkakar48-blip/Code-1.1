# Selkie 0.11.0 — validation du prototype local

Source : `fc2366b2507b4a37a06e33a1cde48ab72a7da660`. VersionCode 20, Android 10+ (minSdk 29), cible API 34. APK : 25660710 octets, SHA-256 `b9f5127dd00b5bb1c3cb87b0955a148e8fd51212acbe36d9ca496fb26ed4cfd4`. Certificat debug inchangé : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. Les fichiers JSON et ZIP voisins décrivent les artefacts exacts ; les entrées de l’archive ont été comparées aux blobs de ce commit et leur CRC vérifié.

## Contrôles passés

- assembleDebug, testDebugUnitTest et lintDebug exécutés sur les sources finales : 107 tests, zéro échec/erreur/ignoré ; Lint zéro erreur, quatre avertissements et une information. Les tests du client IA retiré ne font plus partie du produit ; ils ne sont pas désactivés pour masquer un échec.
- Signature APK v2 vérifiée, certificat identique à 0.10.4. Le versionCode augmente, sans désinstallation nécessaire pour les versions de test signées avec cette clé.
- Manifest compilé inspecté : aucune permission Internet, contacts, photos ou localisation. Les options Gradle de connexion ont été supprimées et les anciennes sessions sont effacées au démarrage.
- Unicité des identifiants, questions normalisées et réponses dans les deux langues, accès par offre, correspondance des traductions, recherche et cache d’au plus 256 réponses vérifiés. Gratuit 1 152, Premium 23 040, Ultra 50 688. Les contenus sont des combinaisons rédigées de profils, décisions et contextes, pas autant de textes indépendants.
- Contraste des rôles du texte dans les deux thèmes et sur les quatre couleurs claires au maximum d’opacité testé à 4,5:1. Les cellules de formes n’empiètent pas les unes sur les autres.
- 57 coordonnées distinctes du classpath debug inventoriées avec leurs POM ; Apache-2.0 directement ou via parent. Licence consultable dans Confidentialité et données. Ce relevé ne certifie pas les droits du nom/logo ni toutes les notices d’un futur APK de distribution.
- Mise à jour installée avec succès sur l’émulateur Android 14, écran d’accueil affiché, progression du test conservée (130 points, Gratuit, français). Aucun effacement des points ou défis n’a été effectué.

## Animation et limite des essais d’interface

Pendant la préparation 0.11.0, les mêmes classes de décoration claire ont donné 3 698 pixels modifiés avec mouvement, zéro à l’arrêt système et 3 667 après reprise, hors barres système. Ces observations précèdent le dernier correctif du coach ; la décoration n’a pas changé dans ce correctif. Le réglage d’animation et le thème Nuit initiaux de l’appareil de test sont restaurés. Les formes multicolores ont été inspectées visuellement sur écran clair.

Le logiciel d’émulation sans accélération matérielle affiche encore des ANR et certains dumps UiAutomator sans racine valide. Un essai au clavier a bloqué ; les réponses et la recherche ont ensuite été déplacées hors du thread principal, avec annulation des demandes obsolètes et temporisation de recherche. Tab et Ctrl+Entrée sont implémentés, mais le parcours complet sur émulateur n’est pas déclaré validé après ce correctif. Ne pas attribuer définitivement ces ANR à une seule cause ni annoncer leur disparition. TalkBack n’est pas installé sur cet appareil de test. Grandes polices, clavier physique complet, Samsung A16 et surcouche à limite réelle restent à vérifier.

## Diffusion et droit

L’APK est un prototype debug gratuit. Aucun compte utilisateur, paiement, publication Play ou hébergement public de politique n’a été activé. Prix proposés : Premium 5 €/mois ou 60 €/an ; Ultra 10 €/mois ou 120 €/an, sans réduction. Les décisions d’éditeur/public et les opérations nécessaires restent détaillées dans TACHES_RESTANTES.md. Le bilan 9 en place / 6 partiels / 4 hors périmètre ne certifie pas la conformité et ne garantit pas l’absence de sanction. Vérifier la cible SDK requise au moment du dépôt ; API 34 n’est pas présentée comme suffisante pour un dépôt public en 2026.
