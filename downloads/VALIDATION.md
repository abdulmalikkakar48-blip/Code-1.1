# Validation de Marée 0.5.0

Vérifications effectuées le 11 octobre 2026.

- Compilation debug réussie ; dernier build Gradle : 25 secondes.
- 31 tests unitaires : 26 Store et 5 SetupPreferences ; aucun échec, erreur ou test ignoré.
- Android Lint : aucune erreur, 13 avertissements. Les avertissements existants concernent les traductions et les règles de sauvegarde. L’avertissement ajouté porte sur le dossier d’icône `mipmap-anydpi-v26`, conservé pour cette chaîne de compilation.
- APK : `com.maree.app`, versionCode 6, versionName 0.5.0, Android 10+, target API 34.
- Signature vérifiée et identique à celle des APK 0.1.0 à 0.4.0 : une mise à jour conserve les données locales.
- Intégrité ZIP et ressources du logo vérifiées. Nom affiché Marée conservé ; icône adaptative, masque monochrome et notification monochrome intégrés.

Taille : 23,495,948 octets.

SHA-256 de l’APK : `aa9ff1935c95242b25a712d0aa3511ea0b53306cf36b3013c25c958c14431464`.

SHA-256 du certificat de test : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`.

L’installation et la validation visuelle de cette version sur un téléphone n’ont pas été effectuées dans cet environnement. Les essais antérieurs sur l’émulateur avaient été limités par le blocage de son système Android. Vérifier le nouveau logo et le suivi sur le Samsung A16.

Aucun achat réel, coach IA ou synchronisation n’est connecté. Le prototype reste une version de test, avec des offres de démonstration gratuites. La publication Google Play exige encore la mise à niveau du SDK, la signature de production, les déclarations, la politique publique et le parcours de tests. Les guides sont dans `docs/DEMARRER_PUBLICATION.md`.
