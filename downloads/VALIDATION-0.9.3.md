# Selkie 0.9.3 — abonnement annuel sans réduction

APK de test construit le 12 octobre 2026.

- Fichier : `Selkie-Android-0.9.3.apk`, 23800964 octets (environ 24 Mo).
- SHA-256 : `771a1e9d9bbdb0964c963d08d977c09ad19c8b6be65ce0521980136df1c3d740`.
- Package : `com.maree.app`, versionCode 14, versionName 0.9.3, Android 10+, cible 34.
- Signature Android v2 vérifiée : certificat `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`, identique aux APK 0.8.1 à 0.9.2. L’ancienne signature fixe de 0.8.0 reste incompatible avec une mise à jour directe.

## Modification et contrôles

L’année coûte douze mensualités : Premium 10 €/mois ou 120 €/an ; Ultra 20 €/mois ou 240 €/an. Les annonces de remise, de mois offert et d’économie sont retirées de la présentation des offres et des réponses du Coach en français et en anglais. Les fonctions sont identiques pour les deux durées.

`./gradlew --no-daemon --max-workers=2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug` : compilation réussie, **69 tests Android**, zéro échec, erreur ou test ignoré. Le test des tarifs annuels dans la réponse du Coach anglais attend désormais 120 et 240. Android Lint : zéro erreur, deux avertissements et une information. Une revue indépendante statique confirme la cohérence des offres et FAQ ; les anciens prix conservés appartiennent aux archives explicitement identifiées.

Aucun essai de cette version sur téléphone ou émulateur n’a été effectué. L’installation et la présentation des deux durées et langues restent à essayer sur le Samsung.

Les langues, le suivi, les points, les défis et le design restent conservés. Aucun paiement réel ni compte serveur n’est activé ; les essais d’offres restent gratuits. Voir [les offres actuelles](../docs/ABONNEMENTS_ET_COMPTES.md).
