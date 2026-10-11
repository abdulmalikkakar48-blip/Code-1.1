# Validation de Milo 0.7.0

Vérifications effectuées le 11 octobre 2026.

- Compilation debug réussie en 48 secondes après correction d’un import.
- 31 tests unitaires : aucun échec, erreur ou test ignoré. Les règles des points, plafonds 10/50/illimité, défis, baisses d’offre, effacement et permissions restent couvertes.
- Android Lint : aucune erreur, 13 avertissements existants.
- APK : `com.maree.app`, versionCode 8, versionName 0.7.0, nom affiché Milo, Android 10+, target API 34.
- Signature vérifiée et identique aux APK Marée précédents : une mise à jour sans désinstallation conserve les données locales.
- Intégrité ZIP vérifiée ; icône Coach présente dans l’APK ; nouveau logo RGBA 1254 × 1254 avec transparence.
- Contrastes calculés : texte principal sur fond 11,47:1 ; texte secondaire 5,03:1 ; texte blanc du bouton vert 6,63:1 ; texte vert sur carte teintée 5,86:1.

Taille : 23,342,647 octets.

SHA-256 de l’APK : `4c0a9662b0fedbe430b2c8dadacdf1d2286fecc88b560cd771096f078804233a`.

SHA-256 du certificat de test : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`.

La validation visuelle sur téléphone ou émulateur n’est pas réalisée. Aucun appareil Android n’était connecté pendant cette tâche ; les essais antérieurs de l’émulateur avaient été limités par le blocage de son système. Vérifier sur Samsung A16 : cinq onglets, Coach, lignes de journées et d’étapes, texte agrandi, politique complète, retour, effacement et mise à jour avec données conservées. Les contrastes calculés et la compilation ne prouvent pas l’ensemble du rendu visuel.

L’onglet Coach propose des conseils fixes hors ligne ; aucune réponse n’est présentée comme produite par une IA. Le dialogue IA, les achats réels et la synchronisation restent à connecter. Aucun accès Internet ni secret de serveur n’est ajouté à l’APK. La fonction de coach Supabase reste un exemple à intégrer et déployer séparément.

Avant Google Play : vérifier la disponibilité du nom Milo, mettre à niveau le SDK, préparer la signature de production, la politique publique, les déclarations et le parcours de tests. Aucun compte développeur, paiement ou publication Google Play n’est créé par cette tâche.
