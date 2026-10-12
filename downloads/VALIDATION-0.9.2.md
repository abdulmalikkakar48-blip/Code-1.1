# Selkie 0.9.2 — français et anglais

APK de test construit le 12 octobre 2026.

- Fichier : `Selkie-Android-0.9.2.apk`, 23800300 octets (environ 24 Mo).
- SHA-256 : `9e21481832c15bba60f71bbafec98b581482fdde469be7392f6277638e15d576`.
- Package : `com.maree.app`, versionCode 13, versionName 0.9.2, Android 10+, cible 34.
- Signature Android v2 vérifiée : certificat `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`, identique aux APK 0.8.1, 0.9.0 et 0.9.1. L’ancienne signature fixe de 0.8.0 reste incompatible avec une mise à jour directe.

## Contrôles exécutés

`./gradlew --no-daemon --max-workers=2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug` : compilation réussie, **69 tests Android**, dont 25 tests du moteur de réponses, zéro échec, erreur ou test ignoré. Android Lint : zéro erreur, deux avertissements et une information.

Les tests ajoutés vérifient le choix de langue initial/enregistré et sa priorité, l’égalité des 67 IDs et des 125 réponses entre les catalogues, les questions anglaises sur les offres et le suivi, les petites fautes, les suggestions inconnues, les mêmes points et compteurs réels avec format adapté, la langue d’une instance du coach indépendante de la langue globale, la priorité de la détresse anglaise et le filtrage local de phrases privées dans les deux langues. Les 57 tests précédents sont conservés et exécutés.

Une revue indépendante statique de l’ensemble des écrans a repéré les journées de défis encore françaises ; elles ont été traduites avant la compilation complète. Les libellés mémorisés du Coach sont liés à la langue ou à un identifiant stable. Les titres et étapes personnalisés ne sont pas traduits ou réécrits. Les mêmes indices des vingt défis, clés de stockage, règles, limites, points et prix sont conservés.

Aucun essai de cette version sur téléphone ou émulateur n’a été effectué. La bascule FR/EN et sa persistance après relance, le défilement, les menus, le clavier, les notifications et le rendu Samsung restent à essayer sur le téléphone. Les tests du choix de langue vérifient sa logique ; ils ne constituent pas une observation de l’interface ou de la persistance Android sur Samsung.

## Fonctionnement livré et limites

Français / English est disponible dès l’accueil et dans Plus → Langue / Language. Sans choix enregistré, l’anglais est choisi si le téléphone est en anglais, le français sinon. La préférence reste uniquement sur l’appareil et l’effacement global la supprime. Les écrans, offres, messages, défis proposés, jours de validation, politique et coach hors ligne sont traduits. Chaque langue comprend 29 sujets de conseils avec trois variantes et 38 FAQ complètes en sept rubriques.

Le moteur reconnaît les expressions prévues des deux langues et répond dans la langue choisie. Les conversations en mémoire sont recréées à la bascule. Les phrases sauvegardées sur option, textes personnalisés et anciens avertissements restent dans leur langue d’origine. Le suivi actualise sa notification persistante sans être redémarré.

**Aucun paiement, compte serveur, restauration d’achat ou dialogue IA distant n’est activé.** Les paramètres publics Supabase compilés sont vides. Le futur serveur IA vise encore des réponses françaises ; cette version ajoute l’anglais au Coach local, sans prétendre un service distant bilingue. Le contact, le phoque, les couleurs et les règles des points sont conservés. Voir [les langues](../docs/LANGUES.md) et [la politique anglaise](../docs/PRIVACY_POLICY_EN.md).

## Sources conservées

- Commit exact du code : `067f8f1bb62e540e27b52d9c2ccf738ac81ff014`.
- Archive : `Selkie-Android-0.9.2-source.zip`, 608843 octets, 87 fichiers.
- SHA-256 : `a844ff20dd8c725ba3fc8e684c644cd79d0014350c226f05a01a4714b5893760`.
- CRC ZIP et égalité de chaque fichier avec le commit vérifiés ; script Gradle exécutable conservé. Aucun APK, cache de compilation ou fichier de signature privée dans les sources.
