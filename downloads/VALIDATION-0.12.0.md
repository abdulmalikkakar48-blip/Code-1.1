# Selkie 0.12.0 — validation du prototype local

Source : `7715251c5f50141e47101ce341e9f515254c2701`. VersionCode 21, Android 10+ (minSdk 29), cible API 34. APK : 26565026 octets, SHA-256 `afe8a134d3def58880cd6e8ae94d1a26f5a1718d8e8b3ebda3e7cc3c96c5bd6c`. Certificat debug inchangé : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. Le ZIP contient les fichiers suivis de ce commit, hors archives downloads ; chaque entrée a été comparée au blob Git correspondant et son CRC vérifié.

## Contrôles passés

- Compilation debug, tests unitaires et Lint exécutés sur les sources finales : **204 tests, zéro échec, erreur ou test ignoré**. Android Lint : zéro erreur, cinq avertissements et trois informations.
- Les nouveaux tests couvrent les différences d’observation, les jours et fuseaux, la couverture inconnue, les fenêtres de conservation et leur nettoyage, les bilans, les plages horaires de nuit et leurs chevauchements, les favoris et leur accès par offre, les validations guidées, l’export et les données du widget. Les tests existants de points, défis, langues et limites restent exécutés.
- Signature vérifiée avec apksigner ; certificat de test conservé. Manifest compilé inspecté : aucune permission Internet ni nouvel accès aux contacts, photos ou calendrier du téléphone.
- Mise à jour 0.12.0 installée avec succès sur l’émulateur Android 14 sans désinstallation. Le gestionnaire de paquets confirme la version 21 et enregistre le fournisseur de widget, avec une période système de 30 minutes.
- Accueil et vue Jour de Quotidien affichés et inspectés : 130 points, offre Gratuit, français, thème Nuit conservés. Aucun effacement des points, défis ou données de test.
- Relecture indépendante des interactions entre suivi, limites habituelles/créneaux et effacement : aucun problème concret identifié. Aucun nouveau composant tiers ; l’inventaire des dépendances de la version précédente reste applicable.

## Fonctions et limites

Calendrier local sur 90 jours, résumé hebdomadaire et favoris accessibles gratuitement. Premium ajoute les détails du bilan, les créneaux et le widget. Ultra ajoute les programmes de 7, 14 et 30 jours, soit 51 activités rédigées en français et en anglais. Voir [les fonctions](../docs/FONCTIONS_QUOTIDIENNES.md).

L’historique débute avec les observations de cette version, sans remplissage rétroactif. Les mesures partielles ne représentent pas automatiquement une journée entière. Les lectures et l’export filtrent la fenêtre de 90 jours ; le nettoyage durable reprend à la prochaine utilisation ou observation, si l’écriture réussit. Aucun délai de suppression physique pendant la fermeture de l’application n’est garanti. L’export schéma 2 et l’effacement global couvrent les nouvelles données.

L’émulateur logiciel sans accélération matérielle a encore retourné des dumps UiAutomator sans racine valide. Les essais n’ont pas confirmé les parcours complets Calendrier, Bilan, Outils, favoris ni le placement du widget. Ne pas présenter la seule présence du fournisseur comme un widget réellement placé et vérifié. Les essais Samsung A16, grandes polices, TalkBack, clavier et blocage réel restent à réaliser. L’absence d’ANR sur un bref relevé ne prouve pas leur disparition.

## Diffusion

APK de test debug hors ligne, sans IA, compte serveur ou paiement réel. Prix proposés conservés : Premium 5 €/mois ou 60 €/an ; Ultra 10 €/mois ou 120 €/an, sans réduction. Les offres de démonstration ne prouvent aucun achat. Les liens GitHub nécessitent l’accès au dépôt ; le téléchargement public anonyme n’est pas certifié.

L’identité publique, le public visé, la facturation/restauration, l’hébergement des politiques et la publication Play restent à finaliser. La cible API 34 et la signature debug ne constituent pas une préparation Play suffisante en 2026. Le bilan des 19 points reste 9 en place, 6 partiels et 4 hors périmètre ; il ne certifie pas la conformité juridique. Les pages de confidentialité décrivent les nouvelles données locales.
