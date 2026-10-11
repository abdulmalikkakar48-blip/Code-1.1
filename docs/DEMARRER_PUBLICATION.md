# Selkie : passer du prototype à une application publiée

**Selkie** est le nouveau nom choisi par l’éditeur. Il remplace Milo (anciennement Marée) dans les écrans ; sa disponibilité commerciale doit encore être vérifiée avant lancement.

## La prochaine action à faire

L’éditeur a 17 ans et atteindra 18 ans le 20 décembre 2026. La règle connue de Google impose au moins 18 ans pour créer son propre compte développeur. Il peut préparer le projet maintenant puis ouvrir [Google Play Console](https://play.google.com/console/) à ses 18 ans. Un parent majeur peut éventuellement porter la publication avant cela s’il est réellement titulaire et responsable des contrats, coordonnées bancaires et obligations fiscales. Le tarif connu est de **25 USD une seule fois**, à confirmer dans la console. Les informations d’identité doivent correspondre au titulaire réel.

Les mots APK, AAB, tests, soumission et déclaration d’activité sont expliqués dans [le guide simple de la version retenue](VERSION_REFERENCE.md#publication-expliquée-simplement).

Ensuite, suis dans cet ordre :

1. [Publier sur Google Play](PUBLICATION_GOOGLE_PLAY.md) : compte, bundle signé, fiche, confidentialité, tests et soumission. Le projet cible encore Android API 34 ; il faut l’adapter aux exigences actuelles avant de soumettre une version.
2. [Activer les abonnements](ABONNEMENTS.md) : profil marchand, Premium à 10 €/mois et Ultra à 20 €/mois, intégration Google Play Billing et vérification des achats. L’APK actuel propose une démonstration gratuite, sans prélèvement.

## Quel service de paiement choisir ?

| Où le client achète | Parcours recommandé pour ce projet |
| --- | --- |
| Dans l’application distribuée sur Google Play | Google Play Billing pour les abonnements numériques, dans le parcours standard |
| Sur un site web séparé | Stripe peut convenir ; cela ne permet pas automatiquement d’ajouter son lien de paiement dans l’application |

Les programmes de paiement alternatif dépendent du pays et de l’éligibilité. Pour débuter sur Google Play, ne construis pas le lancement sur une exception encore non vérifiée. La commission standard connue des abonnements Google est de 15 %, à reconfirmer ; le montant reçu dépend aussi des taxes, remboursements et autres ajustements.

Le suivi local de 50 applications ou de toutes les applications compatibles n’a pas de coût facturé par application. Il utilise le téléphone et peut consommer davantage de batterie. Les coûts externes viennent surtout du futur coach IA, du serveur éventuel, des comptes développeur et des paiements. Un coach « illimité » doit avoir un modèle économique viable avant sa commercialisation.

Si tu encaisses régulièrement des revenus en France, il faut déclarer l’activité sous un statut adapté ; une micro-entreprise peut être une piste selon ta situation. Le compte développeur et le profil bancaire ne remplacent pas ces démarches. Consulte [le portail officiel de création d’entreprise](https://formalites.entreprises.gouv.fr/) pour préparer cette étape.

## Où en est le projet ?

La refonte moderne de Selkie 0.8.0 est la [version de référence choisie](VERSION_REFERENCE.md). Elle conserve les plafonds 10 / 50 / illimité et le phoque noir. L’identifiant Android reste le même, mais cet APK utilise une nouvelle signature fixe : une ancienne installation avec un autre certificat doit être désinstallée, ce qui efface ses données locales. Les limites, les points et les défis sont déjà présents. L’écran Coach est accessible avec des conseils hors ligne. Les vrais achats, le dialogue IA et la synchronisation restent à développer. La version iPhone est reportée ; la priorité est la publication Android.

Les liens officiels figurent dans chaque guide. Les pages Google ont été bloquées par le proxy de cet environnement le 10 octobre 2026 : les tarifs et règles connus sont donc à reconfirmer dans Play Console avant publication. Aucun compte ni paiement n’a été créé.
