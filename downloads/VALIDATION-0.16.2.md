# Selkie 0.16.2 — voiles un peu plus visibles

Source : `1fdb83f6de171ae7b5af55cc5bdbabea7794fcd9`. VersionCode 29, minSdk 29, cible API 34. APK : 24983440 octets, SHA-256 `16b656b4e4a60bf5a2409d60dd021aad95a11f807a5a0589b5c1741f6de18ca2`. Certificat debug conservé : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. Archive source : 914105 octets, SHA-256 `1d9994d3c7755706cf2e09a0f23758249897a615213b4a63c6ab53aac5f1ee0f`. Chaque entrée du ZIP a été comparée au blob du commit source correspondant (hors downloads), CRC validé.

## Retouche limitée

Seules quatre définitions d’opacité changent dans AmbientStyle : passes lilas [3,5,8,12,16] → [4,6,10,14,19], borne 44 → 53 ; passes pêche [2,3,5,7,9] → [2,4,6,8,11], borne 26 → 31. Cela correspond à environ 20 % d’opacité relative supplémentaire (environ 3,5 et 2 points de pourcentage en valeur absolue sur les bornes), pas à 20 % de luminosité supplémentaire sur tous les pixels. Aucun blanc éblouissant ni nouvelle couche de lumière ajouté. Les cinq bords progressifs sont conservés.

Une comparaison source a vérifié que le reste du renderer, les RGB, les courbes, la vitesse, la sculpture, le thème, les cartes, les textes, la mise en page et le mode Nuit restent identiques à 0.16.1. Aucun nouveau timer, dessin, compte, droit Android, réseau ou dépendance ; seules la version Gradle et la documentation accompagnent les quatre valeurs d’opacité. Les données, offres, prix, coach et lectures sont conservés.

## Contrôles

Compilation debug, suite JVM et Lint sur les sources finales : **218 tests réussis, zéro échec, erreur ou ignoré** ; Lint zéro erreur, 5 avertissements et 4 informations. Le test existant vérifie automatiquement les nouvelles opacités réelles : lavis 24/255 et 14/255, bornes des voiles 53/255 et 31/255. Chaque borne de voile surestime la superposition effective de ses passes ; le shader ne peut que diminuer l’alpha. Tous les textes normaux restent au-dessus du seuil 4,5:1 sur ce cumul conservateur. Aucun test n’a été ajouté ou désactivé pour cette retouche.

## Essai natif

Mise à jour versionCode29 installée sur Android14, même certificat que0.16.1. [PhaseA](Selkie-0.16.2-clair-phase-a.png), [phaseB](Selkie-0.16.2-clair-phase-b.png) et [Quotidien](Selkie-0.16.2-quotidien.png) ont été examinés : voiles légèrement accentués, textes et boutons lisibles, cartes et style conservés. Une seconde relecture visuelle indépendante a confirmé que le renforcement reste léger.

Une vérification mathématique indépendante des nouvelles passes trouve un contraste conservateur supérieur à5,33:1, y compris avec arrondis répétés ; une simulation des vraies géométries à60phases reste au-dessus de5,98:1 pour le texte violet. Cette simulation n’est pas un test graphique sur téléphone. L’opacité composée des cinq passes croît d’environ18–19%, ce qui est proche de la hausse relative de leurs sommes ; ce pourcentage ne mesure pas une hausse uniforme de luminosité.

Entre les deux captures animées,12 292pixels sur12 840 changent dans la marge gauche x0–24,y145–680, sans texte ni sculpture ; écart maximal12 par canal. La [comparaison chiffrée](Selkie-0.16.2-mouvement.json) garde cette région. Après désactivation des animations système, les images [fixeA](Selkie-0.16.2-clair-fixe-a.png) et [fixeB](Selkie-0.16.2-clair-fixe-b.png) sont identiques entre y38 et680 ; l’horloge système a changé de minute et reste exclue de cette comparaison.

Préférences de thème restaurées et comparées octet pour octet. Échelle d’animation initiale0 et délai écran initial2147483647 rétablis. Données de suivi/progression non réécrites par les scripts. La fluidité et la lisibilité sur SamsungA16 restent à essayer physiquement ; aucune nouvelle validation exhaustive de Nuit, de TalkBack ou des grandes polices n’est affirmée.

## Limites

Les images et tests ne prouvent pas toutes les interactions, TalkBack, les grandes polices ou la fluidité sur chaque téléphone. L’essai physique sur Samsung A16 reste nécessaire. Le prototype debug cible API 34 ; ce livrable ne publie ni l’application sur Play ni un paiement réel. Les liens GitHub requièrent l’accès au dépôt, sans promesse de téléchargement anonyme.
