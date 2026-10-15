# Selkie 0.16.1 — ondulations du fond Clair

Source : `b9d72873fd5486391b3fc999d9c2bda88ada898a`. VersionCode 28, minSdk 29, cible API 34. APK : 24983436 octets, SHA-256 `5971e75ed5f69fa57261ec686920854dfd3ed58d4d89a449f7f2b981221f56e3`. Certificat debug conservé : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. Archive source : 913715 octets, SHA-256 `fe6a1e64ef28261865ad4ec46bed1fbfadefe798d7a9a63f03fd46fc66cf6ff7`. Chaque entrée du ZIP a été comparée au blob du commit source correspondant (hors downloads), CRC validé.

## Portée

Seuls SelkieAmbientField et AmbientStyle changent le rendu : deux voiles satinés lavande/pêche, larges et lentement ondulants. Chacun utilise cinq formes remplies imbriquées pour adoucir les bords, sans filtre de flou, trait fin, particule, clignotement ou couche intermédiaire. Les déplacements couvrent environ 20–22% de la hauteur entre leurs extrêmes, avec courbure et largeur légèrement variables. Chemins, peintures, gradients et tableaux sont réutilisés ; aucune allocation d’objet de dessin par image. Opacités fixes et harmoniques entières sur la phase existante : boucle continue, sans saut.

La palette, les cartes, la mise en page, le logo, les formes en relief, le clock et son réglage de réduction des animations, ainsi que le renderer Nuit sont inchangés par rapport à 0.16.0. Une comparaison source a confirmé ces fichiers et la partie sculpture octet pour octet ; revue indépendante du diff effectuée. Le seul autre fichier runtime modifié est la version Gradle. Les données, offres, prix, coach et lectures restent identiques. Aucun réseau, compte, permission, asset ou dépendance ajouté.

## Tests

Compilation debug, suite JVM et Lint sur les sources finales : **218 tests réussis, zéro échec, erreur ou ignoré** ; Lint zéro erreur, 5 avertissements et 4 informations. Le test de contraste existant inclut maintenant le cumul des deux lavis (24/255 et 14/255) et les bornes des deux voiles (somme des passes 44/255 et 26/255). La composition effective des passes est inférieure à ces sommes ; les dégradés et l’anticrénelage réduisent encore les opacités. La marge conservative minimale calculée reste environ 5,59:1, au-dessus du seuil 4,5:1. Les autres tests existants sont conservés, sans désactivation ni nouvelle suite artificielle.

## Essai natif du fond Clair

Mise à jour versionCode 28 installée sur Android 14 avec le certificat de0.16.0. [Phase A](Selkie-0.16.1-clair-phase-a.png) et [phase B](Selkie-0.16.1-clair-phase-b.png) de l’accueil examinées : les voiles se déplacent derrière le titre et dans les marges, tandis que cartes, couleurs et textes gardent le dessin précédent. [Quotidien](Selkie-0.16.1-quotidien.png) affiche aussi ce fond. Une relecture visuelle indépendante a confirmé l’aspect calme et cohérent avec le thème.

La comparaison des deux phases dans la seule marge gauche (x0–24, y145–680), sans texte, horloge ou sculpture, relève12 824pixels modifiés sur12 840, avec un écart maximal de11 par canal. Elle vérifie bien le fond, pas seulement le mouvement déjà présent de l’illustration. La [comparaison chiffrée](Selkie-0.16.1-mouvement.json) conserve cette zone. Les captures [fixe A](Selkie-0.16.1-clair-fixe-a.png) et [fixe B](Selkie-0.16.1-clair-fixe-b.png), après désactivation des animations Android, ont un contenu identique entre y38 et680. La revue indépendante a également trouvé zéro pixel différent sur les images fixes complètes.

Le fichier de thème a été restauré et comparé octet pour octet à sa sauvegarde. L’échelle d’animation initiale0 et le délai d’extinction initial2147483647 ont été rétablis ; les scripts n’ont pas réécrit les données de suivi/progression. La fluidité en temps réel sur Samsung A16 reste à vérifier. Ces captures ne constituent pas une mesure de fréquence d’images ni un essai exhaustif du cycle de vie.

## Limites

Les images et tests ne prouvent pas toutes les interactions, TalkBack, les grandes polices ou la fluidité sur chaque téléphone. L’essai physique sur Samsung A16 reste nécessaire. Le prototype debug cible API 34 ; ce livrable ne publie ni l’application sur Play ni un paiement réel. Les liens GitHub requièrent l’accès au dépôt, sans promesse de téléchargement anonyme.
