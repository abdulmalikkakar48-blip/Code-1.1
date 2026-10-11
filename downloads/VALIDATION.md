# Validation de Marée 0.6.0

Vérifications effectuées le 11 octobre 2026.

- Compilation debug réussie en 50 secondes.
- 31 tests unitaires : aucun échec, erreur ou test ignoré.
- Test des plafonds actualisé : Gratuit accepte 10 applications et refuse la 11e ; Premium accepte 50 et refuse la 51e ; Ultra accepte 151 applications fictives sans plafond commercial et refuse les doublons.
- Les mêmes données sont conservées après relance et une baisse d’Ultra vers Premium puis Gratuit ; les nouveaux ajouts dépassant le plafond sont refusés.
- Les tests des règles à 0 point, des points, de l’effacement, des permissions et des défis restent valides.
- Android Lint : aucune erreur, 13 avertissements existants.
- APK : `com.maree.app`, versionCode 7, versionName 0.6.0, Android 10+, target API 34, nom affiché Marée.
- Signature vérifiée et identique aux APK précédents : installer sans désinstaller conserve les données locales.
- Intégrité ZIP vérifiée.

Taille : 23,495,092 octets.

SHA-256 de l’APK : `4878b3592c21d089afff7a213d9c54cebd749e25bbd87032481c6e8c6c737937`.

SHA-256 du certificat de test : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`.

La validation visuelle et la performance du suivi sur une grande liste d’applications réelles ne sont pas vérifiées dans cet environnement. Le test avec 151 entrées fictives porte sur les règles locales ; il ne constitue pas un test de batterie ou de suivi sur 151 applications installées. Essai sur Samsung A16 nécessaire.

Les offres sont en démonstration gratuite. Aucun paiement réel, coach IA ou synchronisation n’est connecté. Avant Google Play : adapter le SDK à l’exigence actuelle, préparer le bundle de production signé, la politique publique, la fiche, les déclarations et les tests. Voir `docs/DEMARRER_PUBLICATION.md`.

Les tentatives de comparaison des sites concurrents et de lecture de l’aide Google ont été bloquées par le proxy CONNECT HTTP 403. Aucun chiffre concurrent actuel n’est présenté comme vérifié ; le choix 10 / 50 / illimité est documenté dans `docs/CALIBRAGE_OFFRES.md`.
