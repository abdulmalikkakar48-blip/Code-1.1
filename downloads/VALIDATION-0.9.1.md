# Selkie 0.9.1 — aide intégrée et offres annuelles

APK de test construit le 12 octobre 2026.

- Fichier : `Selkie-Android-0.9.1.apk`, 23696628 octets (environ 24 Mo).
- SHA-256 : `d8e52f9d5e8c8bd7f9a9e4b9d7720120909d3544fd73e8e92707f4ae17615a7c`.
- Package : `com.maree.app`, versionCode 12, versionName 0.9.1, Android 10+, cible 34.
- Signature Android v2 vérifiée : certificat `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`, identique aux APK 0.8.1 et 0.9.0. L’ancienne signature fixe de 0.8.0 reste incompatible avec une mise à jour directe.

## Contrôles exécutés

`./gradlew --no-daemon --max-workers=2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug` : compilation réussie, **57 tests Android**, dont 19 tests du moteur de réponses, zéro échec, erreur ou test ignoré. Android Lint : zéro erreur, treize avertissements et une information.

Les tests du moteur couvrent les sujets du catalogue réel, les variantes de conseils, le contexte local, les fautes admises, les questions inconnues, la priorité de la détresse, la navigation, le quotidien, les questions sur les abonnements mensuels/annuels, l’annulation, la restauration, le paiement, les comptes et la confidentialité. Ils distinguent une question sur Selkie d’une demande de conseil avec le nom Selkie dans la phrase. Le serveur et Google Play ne sont pas appelés par ces tests.

Une revue indépendante statique du navigateur d’aide, du défilement, de la présentation des offres et du contact n’a trouvé aucun défaut bloquant. Aucun essai de cette version sur téléphone ou émulateur n’a été effectué : installation, clavier, défilement, choix de rubrique, lien Google Play et affichage annuel restent à essayer sur le Samsung A16.

## Fonctionnement livré et limites

Le même Coach fournit 29 sujets de conseils avec trois variantes, plus 38 réponses d’aide complètes en sept rubriques : 67 sujets et 125 réponses au total. L’aide est consultable via Coach → Comprendre Selkie et par les phrases reconnues. Les questions inconnues centrées sur l’application proposent des rubriques d’aide ; aucune réponse à une nouvelle question n’est inventée.

Le contact de confidentialité devient `1kabdm@gmail.com`. Le responsable actuellement déclaré est conservé. Les prix prévus sont Premium 10 €/mois ou 110 €/an, Ultra 20 €/mois ou 220 €/an, soit l’équivalent d’un mois offert sur l’année. Le choix de durée modifie la présentation ; les essais d’offres restent des démonstrations gratuites. L’aide et les offres expliquent le renouvellement prévu, la résiliation Google Play et le fait que désinstaller n’annule pas un abonnement acheté.

**Aucun paiement, compte serveur, restauration d’achat ou restriction à un téléphone connecté n’est activé.** Cette dernière règle est décrite comme prévue et nécessite une vérification serveur. Les paramètres publics Supabase compilés sont vides ; le dialogue IA distant reste non configuré. Voir [offres et comptes](../docs/ABONNEMENTS_ET_COMPTES.md).

Le suivi, les règles des points et défis, la conservation locale facultative du Coach, le phoque et le design retenu restent conservés.
