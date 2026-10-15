# Selkie 0.16.2 — apparence pastel et volumes flottants

Le mode Clair conserve le fond blanc doux et les lavis lavande/pêche de 0.16.0. Deux larges voiles satinés ondulent derrière les cartes, sans changer les couleurs ni la mise en page. Leur intensité est légèrement renforcée en 0.16.2 : environ 20 % d’opacité relative supplémentaire par rapport à 0.16.1, avec les mêmes cinq bords progressifs et sans nouvelle couche de dessin. Les cartes sont opaques, arrondies, avec une bordure et une ombre légère. Les touches lavande, pêche et menthe distinguent les cartes ; le texte violet foncé et la navigation prune gardent une hiérarchie lisible. Les boutons prennent une forme de pilule et une légère élévation qui diminue à l’appui.

## Illustration à effet 3D

Un anneau lavande, une sphère pêche, des billes menthe/lilas et une capsule flottent doucement. Le relief provient de dégradés, d’ombres portées et de reflets dessinés en Canvas Android dans `SculptureDecoration.kt`. Il s’agit d’une illustration procédurale à effet 3D, sans moteur 3D, texture ou image distante. Aucun actif Dribbble n’est repris ; les captures servent de références d’ambiance.

Les objets colorés sont placés dans des zones réservées, distinctes du texte, sur l’accueil et dans le parcours de bienvenue. Derrière les libellés ordinaires, les deux lavis peuvent se superposer aux voiles : alpha maximal 24/255 et 14/255 pour les lavis ; bornes conservatives 53/255 et 31/255 pour les voiles. Chaque voile comporte cinq formes remplies imbriquées, aux opacités constantes, qui adoucissent les bords sans filtre de flou ni calque intermédiaire. La somme des opacités des passes borne leur composition effective. Les chemins et dégradés sont réutilisés ; aucun point, trait fin ou clignotement n’est ajouté. Le test de contraste utilise ces constantes réelles, dans l’ordre du dessin, avec un seuil de 4,5:1 pour le texte normal. Les fonds des cartes restent opaques ; les ombres et séparateurs décoratifs ne remplacent pas les contours des contrôles.

## Accueil et mode Nuit

L’accueil présente l’objectif de Selkie et son illustration, puis un bandeau compact avec les points et l’état du suivi. La liste des applications vient ensuite, avant les raccourcis et les explications détaillées des points. Les fonctions, règles, données et liens de navigation restent disponibles.

Nuit conserve le renderer de vagues pointillées et ses ondulations. La palette associe désormais bleu nuit, prune et accents bleus/lilas. Les illustrations ont le même dessin, dans leur zone réservée ; le fond général reste sombre. Le logo du phoque et l’icône Android ne sont pas modifiés ; seul l’encadrement du logo dans l’interface devient lavande clair.

## Mouvement et vérification

`SelkieMotion` partage une seule horloge entre le fond, l’accueil et les étapes de bienvenue. Sa boucle dure douze secondes, avec une cadence visée de vingt images par seconde. Elle tourne seulement lorsque l’activité est RESUMED et que les animations système sont autorisées ; une image fixe demeure lorsqu’elles sont désactivées. Ce décor ne lit ni ne modifie la progression. Aucune dépendance, permission, API réseau ou compte n’est ajouté.

Les calculs de contraste ne remplacent pas les essais avec grandes polices, TalkBack et luminosité variable sur téléphone. Les résultats et les limites des essais de cette version sont consignés dans [VALIDATION-0.16.2.md](../downloads/VALIDATION-0.16.2.md).

## Archive 0.15.0

La version 0.15.0 utilisait des octogones anthracite aux lueurs corail, avec dérive et rotation limitées par cellule. Ce dessin est remplacé dans 0.16.0 ; son APK, ses sources et sa [validation](../downloads/VALIDATION-0.15.0.md) restent archivés.
