# Selkie 0.17.0 — dé et coloriage

Source exacte : `63b16556b8c3a53fffb269cfaa8504cc469811e6`. VersionCode 30, Android 10+ (minSdk 29), cible API 34. APK : 27587632 octets, SHA-256 `fc85976e57c2ace0bba7b5ebda574b7568994e4b73b027a412f9255a94f79a50`. Certificat de développement conservé : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. Archive source : 941241 octets, SHA-256 `b281c0a9a84317c855aec314b6e548001ae57472feffd61807d1999e0bcf47fc` ; ses 189 entrées ont été comparées aux blobs du commit source correspondant, hors `downloads/`, et le CRC du ZIP est validé.

## Fonctions livrées

Dé gratuit dans Quotidien et le guide : Activité (six idées), Application (tirage uniforme parmi une à six applications suivies, objectif de vingt minutes) et Semaine (une application, une à trois dates choisies parmi les sept prochains jours). La confirmation seule ajoute des tâches personnelles au calendrier. Aucun historique des tirages, bonus de points, changement de limites, blocage supplémentaire ou rappel automatique. Une confirmation écrit tout son groupe ou le refuse ; les identifiants stables du résultat évitent de créer des doublons lors d’une nouvelle tentative après un commit Android échoué ayant déjà modifié la mémoire.

Coloriage gratuit : huit motifs originaux de 12 × 12 cases, dix couleurs et gomme, toucher/glisser interpolé, commandes ligne/colonne/appliquer et annuler/rétablir sur les cinquante derniers gestes de la page ouverte. Enregistrement privé sur le téléphone après un geste terminé, avec erreur visible et nouvelle tentative ; effacement d’une page confirmé et nouvelle tentative dédiée si nécessaire. Les commandes de coordonnées conservent la place des boutons sur une largeur étroite et laissent leur libellé revenir à la ligne. Le dessin à main levée et l’export PNG ne sont pas inclus.

JSON schéma 4 : ajout de `storedData.coloringPages`, données filtrées par identifiants et indices de couleurs ; les objectifs du dé utilisent `calendarTasks`. L’effacement global couvre les deux fonctions et invalide les anciens objets de stockage. Les copies exportées se suppriment séparément. Politiques FR/EN embarquées, pages statiques et registre mis à jour avec une explication accessible. Aucun nouveau plugin, SDK, service ou droit Android ; APK toujours sans Internet. Prix prévus inchangés : Premium 5 €/mois ou 60 €/an, Ultra 10 €/mois ou 120 €/an, sans réduction. Aucune facturation réelle dans ce prototype.

Les seize lectures, le coach local (1 152 / 23 040 / 50 688 questions combinées par langue), les défis, points, verrous, tâches et offres sont conservés. Le fond et ses animations restent ceux de 0.16.2.

## Contrôles automatisés

Compilation debug, suite JVM et Android Lint sur les sources livrées : **242 tests réussis**, zéro échec, erreur ou test ignoré. Lint : **zéro erreur**, cinq avertissements et quatre informations, comme la référence précédente. Les avertissements restants concernent les commits persistants existants, les règles d’extraction, une variante de ressources et une icône inutilisée ; aucun test ignoré ou désactivé.

Les vingt-quatre nouveaux tests portent sur les correspondances du dé, les dates exactes, le plafond et l’écriture groupée, les nouvelles tentatives sans doublons, la persistance et l’isolation des pages, les formats rejetés, l’export filtré, l’effacement et les écritures tardives, l’historique de gestes et l’intégration sans mutation des points ou limites. Les cas d’échec incluent `commit(false)` après mise à jour de la mémoire, comportement possible d’Android. Signature vérifiée par apksigner ; identité/version/minSdk/cible et permissions vérifiées par aapt.

## Essais Android et limites

# Selkie 0.17.0 native validation — 16 October 2026

Environment: emulator-5554, Android 14, software graphics, no KVM, 480×800. French and dark appearance. System animator scale remained 0; timeout remained 2147483647. No theme or language changes. This check does not establish physical-device performance.

## Verified on first 0.17.0 build

Installed with adb install -r, preserving application data. The native app home and Daily page rendered. Logcat recorded a cold start of 34.381 seconds. The free creative-break card opened Selkie Dice. In Activity mode, a real random roll displayed face 5 and “Faire deux minutes d’étirements doux”. Confirmation displayed “Enregistré dans ton calendrier”. A read-only preferences check showed exactly one new calendar task for 2026-10-16 with that title and completed=false, while pointUnits stayed 640 (160 points). The task was also visibly present in the calendar with manual check/edit/delete actions.

The first build had APK SHA-256 e06f6a1065701257184b3488b671bf6e7e5bcbea2a5eb39c728da6bc83b43220. A final layout/privacy-copy rebuild was subsequently supplied; final-build check below.

## Limitations observed

The emulator repeatedly displayed “Selkie isn't responding” while entering/scrolling the calendar. Choosing Wait allowed further rendering. One attempted tap hit a delayed Close app dialog and returned to the launcher. Do not interpret screenshots as evidence of smooth execution or dismiss these ANRs as a proven hardware-only issue. No physical Samsung A16 check was performed.

## Screens visually verified (first build)

- daily-ready.png: actual Daily page and creative-break card, French/dark.
- dice-result.png: face 5 and the matching stretching activity.
- dice-saved-final.png: explicit successful save and calendar link.
- calendar-delete-visible.png: the created task visible with manual actions.

Preference backups and raw test data remain outside the repository and must not be published. The PNGs above are the only feature screenshots recommended for publication; label them as the first 0.17.0 build. No coloring screenshot is verified.

## Final build and cleanup

The final APK was installed in-place successfully. A device-side SHA-256 of its installed base.apk matched the delivered file exactly: `fc85976e57c2ace0bba7b5ebda574b7568994e4b73b027a412f9255a94f79a50`. Native speed compilation (`cmd package compile -m speed -f com.maree.app`) returned Success. A subsequent launch still encountered an application-not-responding dialog. Attempts to reach Coloring were blocked; painting, on-device persistence, reset, coordinate-control fit and English/light-mode rendering remain unverified in this native session. Unit checks are separate evidence and do not replace this missing UI coverage.

Logcat confirms an input-dispatch timeout waiting 5009ms for a MotionEvent in MainActivity, with CPU-pressure avg10 around23.64%. This establishes the symptom, not the cause; neither a software-emulator-only cause nor a physical-device defect has been proved.

The sole task created for this test was removed after force-stopping the app, by reading the current preferences and editing only its verified UUID/date/title entry. No old whole-state snapshot was restored. A fresh read confirmed all other keys and values semantically identical, the remaining task list exact, and pointUnits640 preserved. Final comparison against the pre-test map confirms all pre-existing business values unchanged. Coloring was never painted and `coloringPages_v1` remains absent. Theme preferences stayed byte-semantically equivalent; animation scale0 and screen timeout2147483647 were rechecked unchanged. The calendar task key now contains an empty list (it was previously absent); no original task was removed.

Les tests et captures ne garantissent pas toutes les interactions, TalkBack, toutes les grandes polices ou la fluidité sur chaque téléphone. L’essai physique sur Samsung A16 reste nécessaire. L’APK de test cible API 34 et n’est pas une publication Google Play. Les pages juridiques statiques ne sont pas hébergées par cette livraison. Les liens GitHub demandent l’accès au dépôt ; aucun accès anonyme n’est promis.

## Captures conservées

Ces quatre captures ont été examinées sur le premier build 0.17.0, avant le seul ajustement des commandes de coordonnées du coloriage et la simplification du texte de confidentialité. Elles montrent les fonctions du dé inchangées dans le build final ; aucune capture du coloriage final n’est annoncée. Le SHA de l’APK final installé a été vérifié séparément.

- [Quotidien](Selkie-0.17.0-quotidien.png)
- [Résultat du dé](Selkie-0.17.0-de-resultat.png)
- [Objectif enregistré](Selkie-0.17.0-de-enregistre.png)
- [Tâche dans le calendrier](Selkie-0.17.0-calendrier.png)
