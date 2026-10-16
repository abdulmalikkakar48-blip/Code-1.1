# Selkie 0.18.0 — dé, calendrier et dessins à colorier

Source exacte : `6cfeab9774f87de4b545b6daad91bf7dc8c02a96`. VersionCode 31, minSdk 29 (Android 10+), cible API 34. APK : 27766460 octets, SHA-256 `050d4b074e3aa6afcbd9b4d6a2577d310079f29b969f7884747d446587d08cc9`. Certificat de développement conservé : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. Archive source : 948424 octets, SHA-256 `7b7b06ace4c5cc9d70bae12f028add87c7b708146f0d12bcd486de6ed519de95`. Les 189 entrées du ZIP ont été comparées aux blobs du commit source, hors `downloads/`, et le CRC est validé.

## Changements

Le dé sépare trois zones : mode et applications choisies, proposition avec petit dé en relief et confirmation, puis explications repliables. Après un lancer, le début de la proposition est amené dans la zone visible et annoncé poliment aux lecteurs d’écran. « Ajouter à mon calendrier » explique directement l’action. Les choix d’applications peuvent être repliés pour garder la proposition proche des commandes. Les résultats restent facultatifs et les confirmations idempotentes ; aucun point, limite ou verrouillage n’est modifié.

Le calendrier reçoit un en-tête lilas avec illustration originale, navigation de mois, accès à une date et retour à aujourd’hui. Les sept colonnes tiennent dans la largeur à police normale ; en grande police, la grille peut défiler et les actions s’empilent. La sélection et aujourd’hui ont des repères distincts. Les cellules sans observation n’affichent plus de point d’interrogation répété ; le texte conserve la distinction entre une absence de données et zéro usage. La date choisie, les tâches, leurs états et l’historique du suivi sont distincts. Les actions d’édition, coche et suppression, les dates 1900–2200 et les plafonds sont conservés.

L’atelier ajoute quatre dessins originaux à carreaux — arbre, fleur, livre ouvert, paysage — aux huit motifs existants. Une galerie en miniature présente les douze pages, avec les dessins en tête et leurs noms. Ses exemples colorés ne préremplissent pas la page personnelle. Dix couleurs et une gomme, accessibles au-dessus de la grille, précèdent les 144 cases à toucher ou parcourir. Les commandes ligne/colonne et Annuler/Rétablir restent disponibles ; sauvegarde locale et reprise après échec conservées. Les anciens identifiants, régions et couleurs enregistrées ne sont pas remplacés. Les trois nouveaux tests vérifient cette compatibilité, la séparation entre aperçu et page et la capacité bornée pour les douze pages.

Les rubriques de Quotidien occupent une bande compacte, avec la rubrique choisie amenée dans la zone visible. Les modes du dé conservent leur largeur naturelle pour éviter de couper leurs noms. Le grand titre générique laisse sa place aux titres de Dé/Calendrier/Coloriage. Depuis Jour, un bouton explicite ouvre les dessins. Guide, offres et politiques FR/EN indiquent les douze pages. Aucun SDK, image extérieure, permission ou service supplémentaire. Le fond, les seize lectures, le coach local et les règles de points sont conservés. Dé et coloriages gratuits dans toutes les offres ; prix prévus Premium 5 €/mois ou 60 €/an et Ultra 10 €/mois ou 120 €/an, sans réduction ni paiement réel. Export JSON schéma 4 inchangé.

## Vérification automatisée

Compilation debug, suite JVM et Lint sur les sources livrées : **245 tests réussis**, zéro échec, erreur ou ignoré. Lint : **zéro erreur**, cinq avertissements et quatre informations, comme la référence précédente. Aucun test désactivé pour cette livraison. apksigner vérifie le certificat ; aapt confirme l’identité/version, les SDK et les permissions, sans Internet ni accès aux photos. Relecture séparée des chemins de sauvegarde, des changements de page et des invariants du calendrier et du dé : aucun blocage concret identifié ; ce constat ne remplace pas un essai sur téléphone.

## Essais Android

Essai natif sur Android 14, émulateur logiciel 480 × 800 sans KVM. APK final installé en mise à jour ; le SHA-256 du base.apk de l’appareil correspond exactement au livrable : `050d4b074e3aa6afcbd9b4d6a2577d310079f29b969f7884747d446587d08cc9`.

Sur le binaire final, en français et mode Clair : accueil et Quotidien affichés ; les trois modes du dé sont lisibles sans couper leurs noms. Un lancer réel a donné la face 2 et « Marcher dix minutes sans écran ». La proposition et le bouton « Ajouter à mon calendrier » apparaissent ensemble dans une carte distincte. Aucune confirmation n’a été effectuée, donc aucune tâche de test n’a été créée. Les confirmations et leur idempotence restent couvertes par les tests et l’essai natif de la version précédente ; ce parcours n’a pas été refait intégralement ici.

Calendrier : en-tête illustré, navigation et grille du mois affichés. Les sept colonnes tiennent dans la largeur à la taille de texte normale ; le 16 octobre est sélectionné et les dates restent lisibles. Les actions d’édition de tâches, toutes les dates et les grandes polices n’ont pas été parcourues pendant cette vérification.

Coloriage : galerie de douze pages affichée avec les aperçus Arbre et Fleur. Palette de couleurs, trois étapes et dessin Arbre blanc à carreaux affichés. Une case a été peinte en menthe ; la sauvegarde réelle contient exactement une case colorée sur la page tree. Après avoir quitté puis rouvert l’atelier, cette sauvegarde est toujours présente. Le dessin libre, l’export PNG, la peinture de toutes les pages et les boutons Annuler/Rétablir n’ont pas été testés nativement dans cette session ; leurs parties de stockage/historique sont couvertes séparément par les tests JVM.

Des délais de rendu, des échecs de lecture UiAutomator et des dialogues ANR ont été observés lors des essais préparatoires. Les traces lisibles montrent des threads principaux Runnable en chargement de classes ou mesure/composition de texte avec une forte charge CPU de l’émulateur ; aucun interblocage précis n’a été identifié, mais ces observations ne prouvent pas une cause exclusivement liée à l’émulation. Les captures finales ont été examinées et montrent les vrais écrans, sans dialogue ANR ni splash. Les essais ne constituent pas une validation de fluidité sur téléphone.

La case de test a ensuite été retirée après arrêt de l’app, par modification ciblée de la seule clé de coloriage qui était absente avant l’essai. Les autres préférences ont été comparées et sont identiques ; les points sont restés à 160 et aucun objectif de calendrier n’a été ajouté. Le thème original Nuit est restauré octet pour octet, les trois échelles d’animation restent à zéro et le délai écran à 2147483647. Les copies privées de préférences et les traces brutes ne sont pas publiées. L’app a été relancée après restauration. Le rapport restoration.json vérifie le SHA installé, la suppression de la case de test et la préservation des données.

## Limites

Les contrôles automatisés et les captures ne prouvent pas toutes les interactions, TalkBack, toutes les grandes polices ou la fluidité sur Samsung A16. Seuls les essais explicitement décrits ci-dessus sont validés. L’APK debug API 34 ne constitue pas une publication Play. Les pages juridiques statiques restent à héberger et ne sont pas une certification. Les liens GitHub demandent l’accès au dépôt ; aucun téléchargement anonyme n’est promis.

## Captures finales vérifiées

- [de-modes](Selkie-0.18.0-de-modes.png)
- [de-resultat](Selkie-0.18.0-de-resultat.png)
- [calendrier](Selkie-0.18.0-calendrier.png)
- [calendrier-grille](Selkie-0.18.0-calendrier-grille.png)
- [coloriages](Selkie-0.18.0-coloriages.png)
- [palette](Selkie-0.18.0-palette.png)
- [dessin](Selkie-0.18.0-dessin.png)
