# Selkie 0.15.0 — poésies et octogones lumineux

Source : `954143039543444bad6d2306adf72fbf14aaa170`. VersionCode 26, minSdk 29, cible API 34. APK : 24937520 octets, SHA-256 `82cb68828aa50f91894a38f1c7040d6c525d4d00dff70a89b303ce3fa6bc467b`. Certificat debug conservé : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. Le ZIP contient les fichiers suivis du commit source hors downloads ; chaque entrée a été comparée au blob Git correspondant et son CRC vérifié.

## Contrôles

Compilation debug, suite JVM et Lint sur les sources finales : **217 tests sans échec, erreur ou test ignoré** ; Lint zéro erreur, 5 avertissements et 4 informations. Les tests du catalogue vérifient seize ressources, trente-deux fichiers FR/EN, les empreintes de provenance, les liens HTTPS sans identifiants et l’export limité à la lecture choisie. Les nouveaux tests couvrent recherche accent/casse/mots multiples, filtres de genre et maintien des huit anciens identifiants. Le test d’apparence utilise les limites réelles des cinq couches de l’octogone pour vérifier le contraste maximal des textes à 4,5:1. Les autres tests existants de questions par offre, points, défis, calendrier, autorisations et données restent exécutés.

## Lectures

Huit poèmes complets ajoutés : Stevenson (Rain, The Wind, The Swing), Blake (The Echoing Green), Longfellow (The Arrow and the Song), La Fontaine (Le Laboureur et ses Enfants, Le Renard et les Raisins), Verlaine (Dans l’interminable ennui de la plaine). Les originaux ont été comparés aux sources sélectionnées en normalisant seulement les espaces et indentations. Une relecture indépendante a vérifié les traductions préparées pour Selkie, les attributions, les dates et l’absence de romance, sexualité ou moquerie religieuse dans chaque poème sélectionné. Quelques textes évoquent la mort naturelle ou un paysage mélancolique. Il ne s’agit pas d’une certification religieuse.

Les auteurs sont morts entre 1695 et 1896 ; seuls leurs textes originaux sont reproduits, avec traductions nouvelles, sans illustrations, introductions, traductions commerciales actuelles ni notes modernes. Les questions pour réfléchir ajoutées sont clairement séparées des poèmes. Sources, éditions, limites de sélection et empreintes dans [LECTURES.md](../docs/LECTURES.md), [LECTURES_PROVENANCE.json](../docs/LECTURES_PROVENANCE.json) et [POESIES_PROVENANCE.json](../docs/POESIES_PROVENANCE.json). Les recueils externes contiennent d’autres textes qui ne sont ni sélectionnés ni approuvés selon les exclusions de l’utilisateur.

Lecture reste gratuite hors ligne. Recherche par titre/auteur et filtres Tout, Réflexion, Poésie ; neuf poésies/fables en vers et sept autres sélections au total. Chargement et export .txt existants conservés. Aucun texte personnel ou historique de lecture ajouté.

## Apparence et validation native

Le mode Clair remplace les six contours ondulants par des octogones aux contours anthracite, facettes et lumières corail progressives sur les arêtes. Les tuiles dérivent et tournent doucement ; tracés et gradients sont réutilisés, cellules séparées et nombre borné à 100. La boucle partagée reste limitée au cycle RESUMED et respecte les animations Android. Nuit conserve son renderer d’eau. Voir [APPARENCE_CLAIRE.md](../docs/APPARENCE_CLAIRE.md).

La mise à jour versionCode 26 a été installée avec succès sur Android 14 avec la même signature debug. Une capture valide de l’accueil en Clair montre le champ d’octogones, les lueurs corail, les cartes opaques et l’offre Gratuit conservée : [aperçu natif](Selkie-0.15.0-accueil-clair.png). Le nom interne de cette capture était daily.png, mais elle montre bien l’accueil ; le parcours Quotidien/Lecture n’est pas validé par cette image.

Le premier essai de capture a été interrompu par la disparition de l’ancien émulateur. Après redémarrage, les premières images montraient seulement le splash, puis l’accueil a effectivement été affiché. Le processus hôte du second émulateur SwiftShader a ensuite terminé avec une erreur de segmentation (exit 139). Ce n’est pas un échec JVM ni une preuve de crash de l’application sur téléphone. Aucune paire d’images en mouvement ou avec animations désactivées n’est utilisable pour conclure : leurs premières captures étaient encore le splash ou un écran noir. Le mouvement, son arrêt effectif, les filtres graphiques et l’export sur appareil restent à vérifier physiquement. Les gardes système/cycle de vie existantes n’ont pas été modifiées.

Après le dernier redémarrage avec le backend swangle, le fichier original de préférences d’apparence a été restauré et comparé octet pour octet à sa sauvegarde. Le réglage d’animation système a été remis à sa valeur initiale 0 puis relu pour confirmation. Les données de suivi/progression n’ont pas été réécrites par les scripts de capture. Cette restauration ne constitue pas un nouvel essai complet du rendu ni une preuve de stabilité du backend.

## Maintien et limites

Accueil, guide, tâches, points, défis, limites, coach, offres et prix 5/10 €/mois, 60/120 €/an sans réduction conservés. Les 1 152 / 23 040 / 50 688 questions combinées du coach ne sont pas supprimées. Aucun nouveau compte, paiement, permission, dépendance ou composant tiers. L’APK ne déclare pas Internet ; les sources sont ouvertes volontairement dans le navigateur.

Les résultats JVM et les captures n’établissent pas les grandes polices, TalkBack, le parcours graphique complet ni tous les fournisseurs de fichiers. Essais Samsung A16 à réaliser physiquement. La clé debug et l’API 34 ne constituent pas une préparation Play suffisante ; publication publique distincte. Les liens GitHub demandent l’accès au dépôt ; aucun téléchargement anonyme public n’est garanti.
