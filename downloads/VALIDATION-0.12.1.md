# Selkie 0.12.1 — calendrier de tâches et apparence claire

Source : `43a3355010ff6cf85ac6d96834f50f7515bff832`. VersionCode 22, minSdk 29, cible API 34. APK : 26722250 octets, SHA-256 `b33254aee91c950c7a098138c80dc7695a8ce9758325c009df94725bba7febeb`. Certificat debug conservé : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. Le ZIP contient les fichiers suivis du commit source hors downloads ; chaque entrée a été comparée au blob Git correspondant et son CRC vérifié.

## Contrôles automatisés

Compilation debug, suite JVM et Lint exécutés sur les sources finales : **211 tests sans échec, erreur ou test ignoré** ; Lint zéro erreur, 5 avertissements et 4 informations. Les sept nouveaux tests vérifient les dates passées/futures, la persistance, les modifications et cases terminé, l’absence de points attribués, les entrées invalides et les limites, l’export filtré, les erreurs de stockage et l’effacement avec invalidation des écritures en attente. Les tests existants restent exécutés, dont les catalogues du Coach par offre. Aucun composant tiers ajouté.

## Comportement livré

Toutes les dates entre 1900 et 2200 sont sélectionnables par navigation des mois ou le bouton Choisir une date. Les tâches personnelles sont gratuites : ajout, modification, case terminé, suppression confirmée ; au plus 500 tâches de 240 caractères, affichées progressivement. Elles restent jusqu’à leur suppression ou l’effacement global. Elles n’accordent pas de points et ne produisent pas de rappel automatique. L’historique observé reste distinct, avec sa fenêtre de 90 jours. Export JSON schéma 3 : nouvelles tâches incluses, questions libres du Coach toujours exclues.

Le fond Clair remplace les 96 formes multicolores scintillantes par six lignes ondulantes monochromes discrètes. Le fond Nuit, le réglage système d’animation et la pause hors activité sont conservés. Coach inchangé : 1 152 / 23 040 / 50 688 questions combinées par langue selon l’offre, sans IA. Prix prévus inchangés : 5/10 €/mois, 60/120 €/an sans réduction ; pas de paiement réel.

## Installation

Signature vérifiée avec apksigner, certificat debug identique à la version précédente. Mise à jour installée sans désinstallation sur l’émulateur Android 14 ; le gestionnaire de paquets confirme 0.12.1 et versionCode 22. Le manifeste compilé ne déclare pas Internet ni accès au calendrier, aux photos ou aux contacts. Ce contrôle d’installation ne valide pas le parcours graphique.

## Limites des essais

Les résultats JVM ne prouvent pas le parcours graphique complet ni l’absence de bug sur un téléphone. L’émulateur logiciel a déjà montré des blocages et des dumps sans racine valide. Le parcours de sélection de dates, la saisie, les grandes polices, TalkBack et le Samsung A16 restent à vérifier physiquement. Le placement réel du widget reste à vérifier. Les documents de confidentialité couvrent désormais le texte, la date, l’état et l’export des tâches personnelles.

APK debug de test, pas une publication Play. Identité publique, audience, facturation/restauration, hébergement des politiques, cible SDK et signature de distribution restent à finaliser. Les liens GitHub demandent l’accès au dépôt ; aucun téléchargement public anonyme n’est garanti.
