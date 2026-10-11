# Selkie : version retenue le 11 octobre 2026

## Version de travail 0.9.0

Le Coach gratuit à règles remplace l’écran d’activation distant comme entrée principale. Il propose 40 sujets et 120 réponses préparées, sans compte ni réseau, avec données locales réelles et suggestions pour les phrases inconnues. Le futur dialogue IA distant reste distinct. Voir [son fonctionnement](COACH_HORS_LIGNE.md).

APK et sources : `downloads/Selkie-Android-0.9.0.apk` et `downloads/Selkie-Android-0.9.0-source.zip`, versionCode 11, package `com.maree.app`, signature de test identique à 0.8.1. Les empreintes et contrôles sont dans [la validation 0.9.0](../downloads/VALIDATION-0.9.0.md). Le phoque, son cadrage corrigé et le design de référence sont conservés.

## Version de travail 0.8.1

À la demande de l’éditeur, le correctif de cadrage du logo est fourni immédiatement sous forme d’un APK de test distinct, même si sa signature impose de réinstaller la version précédente.

- Application : Selkie 0.8.1, versionCode 10, package `com.maree.app`, Android 10+.
- APK à essayer : `downloads/Selkie-Android-0.8.1.apk`, 23 475 888 octets.
- SHA-256 APK : `8d534fdfcde170fed1857e5406410a8afd8e21c502a48b236f8ace34548c090d`.
- Certificat de développement : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`.
- Sources de cette compilation : `downloads/Selkie-Android-0.8.1-source.zip`.
- Contrôles et limites : [validation 0.8.1](../downloads/VALIDATION-0.8.1.md).

Le phoque reste entier dans la zone sûre adaptative grâce aux marges proportionnelles ; le PNG, le fond et l’interface de la refonte restent conservés. La conversation IA préparée est incluse, sans configuration ni service actif. Les abonnements restent en démonstration. La désinstallation d’une version ayant l’autre signature efface ses données locales ; elle n’est pas effectuée automatiquement par ce dépôt.

Les artefacts 0.8.0 ci-dessous restent une archive. La signature de test 0.8.1 ne doit pas être confondue avec la clé fixe 0.8.0 ni une clé de publication Google Play.

## Archive de la refonte 0.8.0

L’éditeur a choisi de conserver la nouvelle refonte comme base du travail à venir, même si sa première installation nécessite de désinstaller l’ancienne application.

## Référence exacte

- Branche de travail : `maree-apk-download`.
- Code adopté : commit `51331aa`, qui contient la refonte Android du commit `58a8f0d46e4f49d281adb3f5d9bbcc1cfba95fff` et sa publication à signature fixe.
- Application : Selkie 0.8.0, versionCode 9, package `com.maree.app`, Android 10+.
- APK : `downloads/Selkie-Android.apk`, 23 401 584 octets.
- SHA-256 APK : `71572ec8c3eb85f3db0fe5a1559716ef4a58df1509868afb10bd44b6777df209`.
- Certificat fixe : `c3def84958aae0971760964b987090c787c1796926d716a91131d69396c25826`.
- Sources : `downloads/Selkie-Android-source.zip`. Les captures existantes montrent la version précédente.

Le code adopte la navigation sombre, la menthe lumineuse, les nouveaux titres et l’accueil avec ses deux compteurs. Conserver cette refonte lors des prochaines modifications ; ne pas repartir de l’ancien APK ou des anciennes captures.

Pour une future mise à jour, augmenter le versionCode et signer avec la même clé fixe selon [la procédure](SIGNATURE_ANDROID.md). La clé privée est sauvegardée séparément : le certificat public ne permet pas de signer une nouvelle compilation. Ne jamais ajouter une clé privée ou son mot de passe au dépôt ou à l’archive des sources.

## Avancement

Estimation de travail : **environ 65 % pour une première version Android commercialisée avec abonnements et dialogue IA**. C’est une estimation de périmètre, pas une mesure d’heures ou une certification de qualité.

Déjà présents : interface native, permissions expliquées avant suivi, limites locales, points, défis sur plusieurs journées, défis personnels et démonstration des trois offres. Le Coach propose des conseils fixes hors ligne.

Restent notamment : vérification de cette refonte sur téléphone, retouches de lisibilité, vraie facturation Google Play et droits d’abonnement vérifiés, activation et essais réels du dialogue IA et de son serveur, adaptation aux exigences Android/Google Play actuelles, confidentialité adaptée aux services effectivement activés et publication. La connexion du coach est [préparée dans le code](ACTIVER_COACH_IA.md), sans modification de l’APK de référence tant que les comptes et services ne sont pas configurés.

Deux observations de la revue du code restent à examiner avec l’éditeur : le bloc de points peut repousser trop bas la liste d’applications ; le texte secondaire de la carte quotidienne sur menthe a un contraste de 4,34:1. Elles ne sont pas corrigées dans cet APK afin de conserver la version choisie.

## Publication, expliquée simplement

La priorité est **Google Play Store**, la boutique Android. L’App Store désigne la boutique iPhone, dont le développement est reporté.

- **APK** : fichier à installer directement sur un téléphone Android pour essayer Selkie.
- **AAB** : fichier destiné à Google Play pour préparer les installations distribuées dans sa boutique. Il ne s’installe pas directement comme l’APK ; sa préparation relève du développement.
- **Tester** : faire essayer l’application, vérifier ses écrans, les limites, notifications et verrous, puis corriger les problèmes. La Console peut exiger un groupe de testeurs avant la publication publique.
- **Soumettre** : envoyer la version et sa fiche à Google pour examen avant leur publication.
- **Déclarer l’activité** : en France, donner un cadre administratif et fiscal à la vente régulière d’abonnements. Un compte Google et un RIB ne remplacent pas cette démarche. Le statut adapté dépend du véritable vendeur ; une micro-entreprise peut être une possibilité à examiner sur le [portail officiel](https://formalites.entreprises.gouv.fr/).

L’éditeur a indiqué avoir 17 ans et atteindre 18 ans le 20 décembre 2026. La règle connue de Google demande au moins 18 ans pour créer un compte développeur Play Console. Il peut préparer l’application maintenant et créer son propre compte à ses 18 ans. Avant cela, un parent majeur peut éventuellement porter la publication, à condition d’être réellement titulaire et responsable des contrats, informations légales, banque et obligations fiscales ; il ne s’agit pas simplement de prêter son nom.

Si un parent porte la publication, identifier ce vendeur et adapter les documents au rôle de chacun avant la commercialisation. La possibilité de changer ensuite de titulaire demande une démarche distincte ; elle ne se résume pas à remplacer le RIB.

Pour encaisser les abonnements dans le parcours standard Google Play : créer le compte développeur, configurer son profil de paiement marchand et les coordonnées bancaires acceptées, créer Premium/Ultra, puis intégrer Google Play Billing dans Selkie. Le RIB reste dans le profil de paiement Google ; il n’est pas intégré au code de l’application. Aucun encaissement n’est actif dans cet APK.

Les critères de compte, frais, tests, banque et exigences techniques actuels sont à confirmer dans [Play Console](https://play.google.com/console/). Les pages officielles Google ne sont pas accessibles depuis cet environnement ; ces indications ne prétendent pas vérifier leur version de 2026.
