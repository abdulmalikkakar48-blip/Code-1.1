# Selkie 0.23.0 — collection de 45 dessins, cuisine et dé corrigé

Validation du 17 octobre 2026. Source exacte : `af63a1266146afe284c9f5bd683b72be373a0d9b`. VersionCode 36, Android 10+ (minSdk 29), cible API 34. APK : 39801599 octets, SHA-256 `9543fe37e18c96e1ccbd5c8c6addeb9ace450151bb2a6ec316dabb83614f9b56`. Certificat de développement conservé : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. ZIP source : 10121019octets, SHA-256 `7c9c30cebb3d69c3086261f3b3d3c1739c837cf8d72b9c5c110f3f06bfde6eb2` ; ses 307 entrées ont été comparées aux blobs du commit hors downloads, CRC validé.

## Dessins fournis

Les 45 PNG du ZIP transmis par l’éditeur sont intégrés avec son autorisation, tous conservés octet pour octet. Ils sont maintenant dans Quotidien → Coloriage → Collection, séparés des 20 illustrations et 12 grilles précédentes. Les 44 premières images sont petites (environ 248 × 248 ou 254 × 292), la dernière 1142 × 1378 ; le zoom ne recrée pas de détails absents. Pas de conversion générative ni redessin des contours.

Une segmentation des zones claires délimitées par les contours forts crée 7571 zones, avec un masque stable par dessin et des ancres intérieures. Des fissures de contour sont refermées pour limiter les fuites ; de minuscules îlots ne sont pas proposés comme zones indépendantes. Les traits gris plus légers restent dans l’image. Le remplissage multiplie la couleur choisie par les pixels RGB originaux, préservant encre et nuances. Les aperçus colorés ne préremplissent pas les feuilles personnelles ; le fichier collection-exemples.png est un rendu autonome des 45 masques, pas une capture Android. Les zones suivent les contours détectés, sans compréhension sémantique des pétales ou bâtiments.

Galerie différée, choix premier/précédent/suivant/dernier, seize couleurs/gomme, toucher, zoom 100–300 %, déplacement au zoom, repères facultatifs et Annuler/Rétablir 50 gestes. Masques/images préparés hors du thread UI, cache limité à trois sources ; repères trop serrés masqués. Échec de chargement : message et nouvelle tentative. Sauvegarde après chaque zone, échec/effacement distincts, protection contre les anciennes sessions après effacement global. Clé séparée importedColoring_v1, 45 IDs fixes, taille exacte des zones et indices 0..16, borne de 100000 caractères. Export JSON schéma 7 : storedData.importedColoring contient seulement id/colors. Effacement global couvre le format ; les copies exportées restent indépendantes.

## Cuisine et points

50 recettes originales FR/EN : 10 entrées, 15 plats, 15 desserts, 10 boissons. Recherche par titre/ingrédient, accents et ligatures, catégories et pagination. Quantités, portions, durées, étapes et information allergènes. Recettes sans alcool ni porc, sans mention religieuse dans l’interface. Aucune collecte, compte, historique de recettes ou recherche persistante ; aucun dé de recettes ajouté.

Nouveaux lancers Points : face 1→−10,2→−20,3→−30,4→−40,5→−50,6→−60, limité au solde restant. Toujours 0..100 points ; pas de nouveau lancer à zéro. Même UUID pour confirmer une tentative interrompue, sans nouveau retrait ; bonus déjà dû traité avant la perte. Nouveau champ pointsPerFace=10 dans les reçus. Les anciens reçus sans ce champ gardent leur barème initial, restent lisibles et ne sont pas débités une deuxième fois. Les trois autres modes du dé ne changent pas les points.

Aucune dépendance/permission supplémentaire ; les sept permissions restent identiques à 0.22.0, sans Internet. Les 32 assets antérieurs sont conservés octet pour octet. Suivi, défis, 20 illustrations, 12 grilles, lectures, coach préparé et offres conservés. Prix 5/10 €/mois et 60/120 €/an, sans réduction ; aucun achat réel ni IA distante. Politiques, aide et descriptions FR/EN alignées, sans certification juridique.

## Contrôles

Compilation debug et 289 tests JVM réussis, zéro échec/erreur/ignoré ; Lint 0 erreur, 5 avertissements, 5 informations. Tests : lecture des 45 masques et7571ancres, dimensions et limites, données malformées, stockage/export de toutes les pages, anciennes couleurs et points préservés, reprise d’écriture et effacement,50 recettes bilingues et recherche, six faces/toutes offres, reliquat et reçus historiques sans nouveau débit. Les autres tests existants restent actifs.

Le premier essai Android a trouvé une erreur réelle manquée par les seuls tests JVM : aapt transforme les fichiers terminant par .gz, rendant les noms de masques demandés absents du paquet. Les masques utilisent désormais .scmz, en gardant le flux gzip, et tools/verify_coloring_assets.py vérifie après assemble les 45 PNG et 45 masques dans l’APK, leurs noms, SHA, en-têtes et tailles. Ce contrôle passe sur le livrable. Les fichiers DEX ne contiennent aucune sonde de diagnostic temporaire.

## Essais Android et limites

Émulateur Android 14 logiciel, 480 × 800 redémarré sans effacer ses données. Un dialogue d’ANR SystemUI a été observé au démarrage, puis quitté avec Attendre. Ce n’était pas un message d’erreur de Selkie ; aucune cause exclusive n’est déduite. Attendre les rendus effectifs sur cet émulateur lent.

Sur la construction corrigée (source 5100520), français/Nuit : collection 45 accessible ; dessin 001, région 15, ancre 159/93 → rose 2 au toucher. Bouton Dernier ouvre 045 ; son format vertical est respecté et la région 263 (699/1043) devient rose 2. À 150 %, un second toucher remplit exactement 189 (584/899). Les traits d’origine restent visibles. Les trois zones sont encore présentes après arrêt du processus, confirmées dans le stockage.

Cuisine affiche50 recettes et ouvre Salade tomate et concombre avec portions, durée et ingrédients. Le dé affiche le barème 10–60 et le bouton annonce le retrait. Lancer réel testé : face 1, perte10points, solde90/100, reçu avec pointsPerFace=10. Les captures distribuées sont de cette construction corrigée ; le dernier ajustement ne change que la persistance de la recherche et la normalisation des ligatures, validées par les tests. L’APK final a ensuite été réinstallé ; son SHA correspond au livrable.

Après essais, seuls les deux dessins de test, leur reçu et les points retirés pour cet essai ont été nettoyés/remboursés par modification ciblée des préférences courantes après arrêt. Aucune ancienne progression globale restaurée. Toutes les autres préférences identiques au départ, thème Nuit octet pour octet, échelles d’animation 0 et délai 2147483647 préservés ; points 100. Préférences/traces privées hors dépôt, app relancée.

Les 45 pages ne sont pas toutes colorées manuellement ; les 45 masques sont contrôlés automatiquement. Pas de preuve de fluidité sur appareil réel, ni de revalidation native complète de TalkBack, grande police, anglais, tous les repères, pan ou undo/redo. Samsung A16 physique à essayer. Export en image et dessin libre non inclus. APK debug API 34, pas de publication Play ni facturation réelle. Identité publique et hébergement légal restent à finaliser. Les liens GitHub nécessitent l’accès au dépôt ; brouillon cloud sauvegardé ne signifie pas publication ou restauration dans une nouvelle tâche.
