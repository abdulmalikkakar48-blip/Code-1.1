# Selkie 0.15.0 — octogones du mode Clair

Le fond clair dessine un champ de formes à huit côtés, construites comme des carrés aux coins coupés. Les contours anthracite portent une lueur corail qui se déplace le long des arêtes. Les tuiles dérivent individuellement de 4,5 dp au maximum et tournent doucement de 2,8 degrés ; les intervalles laissent apparaître le fond clair. Aucun clignotement brutal.

Les cartes et la navigation restent opaques. Le dessin natif conserve ses chemins, peintures, matrices et dégradés entre les images ; le nombre de tuiles est borné à 100. Chaque tuile est découpée dans sa cellule pour limiter la superposition des opacités. Les constantes réelles du renderer sont utilisées dans le test de contraste des textes, avec un seuil de 4,5:1 sur le cumul maximal de couches.

La boucle existante dure douze secondes, avec une cadence visée de vingt images par seconde, sans minuterie supplémentaire. Elle ne tourne que lorsque l’activité est RESUMED et que le système autorise les animations. Une image fixe reste affichée quand elles sont désactivées. Le renderer ne gère pas l’état de l’utilisateur.

Le fond Nuit garde ses vagues pointillées et ses ondulations ; aucune modification de ses équations ou couleurs. Pas d’actif téléchargé, plugin, bibliothèque ou nouvelle permission.

Le contrôle de contraste JVM ne remplace pas une vérification avec grandes polices, TalkBack et luminosité variable sur un vrai téléphone. Les observations de rendu et limites de l’émulation sont enregistrées dans [VALIDATION-0.15.0.md](../downloads/VALIDATION-0.15.0.md).
