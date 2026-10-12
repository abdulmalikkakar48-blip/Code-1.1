# Selkie 0.10.4 — confidentialité, Coach par offre et animation claire

APK de test construit le 12 octobre 2026. Aucun achat, compte serveur ou dialogue IA distant actif.

## Artefacts

- APK : `Selkie-Android-0.10.4.apk`, 25 659 490 octets (environ 26 Mo).
- SHA-256 : `8a0de3103aa2a5d5f14a063006065683873375e66acef1027a8c21634d8c3260`.
- Package `com.maree.app`, versionName 0.10.4, versionCode 19, Android 10+, cible 34.
- Signature v2 vérifiée par l’outil officiel Android. Certificat `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`, identique à 0.10.3. Aucune clé privée publiée.
- Commit exact des sources : `3037178bce09cf93e163b4a5561efa33e8baa1f2`.
- Archive `Selkie-Android-0.10.4-source.zip` : 745 584 octets, 116 fichiers ; SHA-256 `731a4372d158d1b8a4ebd60af4d2223956fb50507be7f947e33d413b0176c1c4`.
- CRC ZIP, égalité de chaque fichier au commit, droits exécutables de gradlew et présence des pages legal-site vérifiés. APK, caches et clés privées exclus.

## Changements

Confidentialité et données rassemble les conditions d’utilisation, abonnements et résiliation, et les mentions de l’éditeur en FR/EN. L’export local JSON contient réglages et progression, sans phrases du Coach, e-mail de compte ou jetons ; sa copie reste à supprimer séparément. L’effacement confirme ses écritures et retire les alertes. Contrastes des contrôles, titres et labels accessibles améliorés. Aucun consentement global obligatoire à la politique, ni bannière de cookies ajoutée pour ces fonctions locales.

La résiliation arrête les prochains renouvellements dans un futur parcours réel. Aucun remboursement automatique ni geste commercial n’est promis pour une période déjà utilisée ; les droits obligatoires, dont rétractation applicable, non-conformité et sommes indûment prélevées, restent réservés. Aucun abonnement n’est vendu dans cet APK. Premium 10 €/mois ou 120 €/an ; Ultra 20 €/mois ou 240 €/an, sans réduction.

Coach : 1 152 questions préparées en Gratuit, 5 760 en Premium et 10 368 en Ultra dans chaque langue. 48 situations de base × 24 intentions ; quatre contraintes additionnelles en Premium et quatre autres en Ultra. Il s’agit de combinaisons préparées, pas de milliers de textes indépendants ou d’un modèle génératif. Saisie standard, FAQ et aide humaine pour détresse reconnue restent accessibles. IDs anciens conservés ; textes et IDs exclusifs filtrés selon l’offre. Changer d’offre/langue réinitialise la recherche, la page et l’ancienne réponse. Seules les 20 questions visibles sont lues ; recherche indexée et caches bornés évitent de composer toutes les réponses.

Clair : 48 formes séparées, dont 24 étoiles, 16 losanges et 8 anneaux ; contours de 0,95 dp, diamètres de 6–14 dp, alpha 16–30/255, dérive et scintillement lent. La marge cellulaire de 2 dp, rotation comprise, évite la superposition. Contraste calculé prudent du texte secondaire : 4,684:1. Nuit : même nappe de points que 0.10.3, alpha 0,32. Boucle de 12 s, maximum 20 rafraîchissements/s, buffers et peintures réutilisés, phase lue dans Canvas, respect du cycle de vie et des animations système.

Groq est préparé comme fournisseur optionnel sur le serveur, OpenAI conservé. Les clés privées restent côté serveur ; destinations fixes et aucun repli automatique payant. Le nom public du fournisseur annoncé au consentement Android doit correspondre au serveur : header contrôlé avant quota et appel. Aucun service déployé ni modèle/quota gratuit actuel vérifié.

## Contrôles exécutés

`./gradlew --no-daemon --max-workers=2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug` : réussite finale, **117 tests**, zéro échec, erreur ou test ignoré. Les tests couvrent notamment export Unicode et fractions exactes sans mutation, filtrage de secrets synthétiques, échec de persistance/relance, unicité FR/EN, contrôle des offres et lecture différée, priorités de détresse et concordance du fournisseur.

Lint : zéro erreur ou fatale, quatre avertissements et une information. ApplySharedPref sur reset() conserve commit volontairement : l’effacement doit confirmer la persistance et s’exécute en IO. Les autres avertissements concernent DataExtractionRules, SDK d’icônes et ressource inutilisée ; information sur état entier boxed. Aucun contrôle désactivé.

Les **14 tests serveur** protocol/provider passent avec fixtures synthétiques et sans réseau. TypeScript strict réussi pour protocol.ts et provider.ts. Ce contrôle ne valide pas un déploiement Deno, le SDK distant, les migrations, une authentification réelle ou un modèle génératif.

Le véritable APK ne déclare pas INTERNET ; URL et clé publique Supabase sont vides. Le nom public inactif par défaut est openai. Une fixture publique factice avec provider=groq a vérifié la génération du manifeste connecté, puis le manifeste hors ligne a été restauré. Aucun APK connecté factice livré ni appel au fournisseur effectué.

## Observations Android

Le fichier final a été installé en mise à jour sur l’émulateur Android 14 ; le SHA-256 du base.apk installé correspond à celui livré. 120 points, langue française, thème Nuit et offre FREE ont été observés comme conservés. Première capture prise pendant le splash écartée ; seules les captures stabilisées servent de preuve.

Un candidat précédent de cette même version, avant les ajouts d’animation claire, d’offres Coach et de fournisseur, avait permis d’ouvrir les trois pages légales et de revenir, puis d’enregistrer réellement un export de 6 065 octets avec le sélecteur Android. Son JSON, version, points, langue/thème et champs filtrés ont été vérifiés ; le fichier privé n’est pas publié. Cette observation reste distinguée des contrôles du fichier final.

Le rendu Clair a été observé sur l’accueil immobile. Deux captures fraîches à deux secondes d’intervalle, hors les 40 pixels supérieurs, donnent 2 728 pixels changés avec animations actives, zéro avec animations désactivées et 3 023 après réactivation. Le scale initial 0 est restauré et relu. Ces mesures prouvent un déplacement, pas une fluidité ou une consommation de batterie sur tout téléphone. Nuit a été inspecté visuellement, sans réattribuer les anciennes mesures de 0.10.3 à cette version.

Le Coach gratuit stabilisé affiche 1 152 questions/réponses préparées. Une première capture après changement d’onglet montrait encore l’accueil et ne sert pas de preuve ; le retry frais valide est retenu. La saisie « tiktok » et le bouton Envoyer ont produit un conseil local ; la hiérarchie et une capture stabilisée montrent la question au-dessus de la réponse. [Capture du Coach](Selkie-Android-0.10.4-coach-answer.png). Premium/Ultra ne sont pas validés via l’interface de l’émulateur ; leurs contenus et contrôles d’accès sont vérifiés par les tests.

[Accueil Clair](Selkie-Android-0.10.4-light-stars.png) · [Accueil Nuit](Selkie-Android-0.10.4-night-water.png).

## Limites de publication

Le [bilan des 19 points](../docs/BILAN_CONFORMITE_FRANCE.md) indique 9 en place dans le prototype, 6 à compléter ou valider, 4 hors périmètre actuel ; ce n’est pas un taux ou certificat de conformité. L’éditeur public n’est pas décidé. Gmail, droits/logo/marque/licences, public mineur, accessibilité, formulaires, mentions et future vente restent à finaliser. Les pages statiques existent sans hébergement public.

Les consultations des sources officielles France/UE et des conditions Groq ont été bloquées par le proxy (403). Domaines nécessaires et instructions de démarrage cloud enregistrés dans le brouillon, mais non appliqués/publiés par cette sauvegarde. Les lois consolidées et exigences Play de 2026 ne sont pas présentées comme vérifiées. Aucune garantie contre une amende.

Pas d’essai Samsung physique, TalkBack, clavier physique, grandes polices, parcours complet, paiement réel ou dialogue IA réel. Les mesures d’animation de 0.10.3 restent dans leur propre validation ; elles ne sont pas reprises comme observations de ce nouvel APK.

[Page GitHub de l’APK actuel](https://github.com/abdulmalikkakar48-blip/Code-1.1/blob/maree-apk-download/downloads/Selkie-Android-0.10.4.apk), puis Download raw file avec un compte autorisé. Les versions précédentes sont conservées.
