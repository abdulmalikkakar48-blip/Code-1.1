# Publier l’application sur Google Play

Ce guide prépare la publication. Aucun compte développeur n’a été créé, aucun paiement n’a été effectué et aucune application n’a été envoyée à Google Play par ce travail. Le nom de travail est Milo ; sa disponibilité commerciale doit encore être vérifiée.

La version actuelle est un prototype Android : APK de démonstration, données locales, offres simulées gratuitement. Elle ne permet pas d’encaisser un abonnement. Le guide [Abonnements](ABONNEMENTS.md) explique le travail nécessaire pour cela.

## Date et vérification des règles

Guide préparé le **10 octobre 2026**. Les tentatives d’ouverture des pages officielles Google Support et Android Developers ont toutes été refusées par le proxy de cet environnement : **CONNECT HTTP 403**, avec la vérification TLS conservée. Les pages ci-dessous sont donc des références à consulter ; leur contenu actuel n’a pas pu être lu ici.

Les montants et conditions mentionnés sont les dernières règles connues, à reconfirmer dans Play Console avant de payer ou de publier. Nous ne pouvons pas certifier l’exigence de niveau Android/API applicable en octobre 2026.

## 1. Créer ton compte développeur

1. Sur un ordinateur, ouvre [Google Play Console](https://play.google.com/console/). Connecte-toi avec un compte Google que tu garderas durablement.
2. Choisis le type correspondant à ta situation réelle : personnel ou organisation. Une organisation peut nécessiter d’autres justificatifs, notamment un numéro D‑U‑N‑S. Le compte personnel n’est pas un moyen d’éviter les obligations d’une activité professionnelle.
3. Le tarif d’inscription connu est de **25 USD, une seule fois**, et non chaque mois. Vérifie le montant et les moyens de paiement proposés à l’écran. Le titulaire crée et paie lui-même son compte.
4. Termine les vérifications demandées : identité, coordonnées et, selon le compte, organisation. Utilise des informations exactes ; certaines informations du développeur sont affichées sur Google Play.
5. Pour les nouveaux comptes personnels, Google impose aussi, selon ses règles connues, une vérification d’accès à un véritable appareil Android via l’application Play Console. Suis la procédure affichée : ne compte pas sur un émulateur pour cette vérification.

Références officielles : [Créer un compte développeur](https://support.google.com/googleplay/android-developer/answer/6112435?hl=fr), [Vérification d’appareil](https://support.google.com/googleplay/android-developer/answer/14316361?hl=fr).

## 2. Prévoir le test fermé avant la mise en ligne

La règle connue pour les **comptes personnels créés après le 13 novembre 2023** demande un test fermé avec **au moins 12 testeurs inscrits pendant 14 jours consécutifs** avant de demander l’accès à la production. Une autre situation de compte peut suivre un parcours différent : Play Console indique celui qui s’applique à toi.

1. Recrute de vraies personnes disposant d’un compte Google et d’un téléphone compatible. Un APK envoyé par message ne remplace pas l’inscription au test fermé Google Play.
2. Crée une piste de test fermé, ajoute les testeurs ou leur groupe, puis distribue le lien d’inscription fourni par Play Console.
3. Demande-leur de s’inscrire et de tester réellement les autorisations, limites, blocages, défis et changements d’offre. Recueille leurs problèmes et corrige-les.
4. Conserve le nombre requis de testeurs inscrits durant toute la période demandée.
5. Une fois les critères remplis, demande l’accès à la production et réponds aux questions sur les tests et la préparation de l’application. Les 14 jours ne garantissent pas une acceptation automatique.

Un test interne peut aider avant le test fermé, mais ne remplace pas les critères de production. Référence : [Tests requis pour les nouveaux comptes personnels](https://support.google.com/googleplay/android-developer/answer/14151465?hl=fr).

## 3. Préparer une vraie version de publication

**L’APK de démonstration actuel n’est pas le fichier à publier.** Il faut un **Android App Bundle `.aab` de variante `release`, signé**, puis configurer **Play App Signing** pour une nouvelle application.

Les points techniques à traiter avant cette étape :

- Le projet utilise actuellement `compileSdk = 34` et `targetSdk = 34`. La règle connue à partir du 31 août 2025 exigeait au moins API 35 pour les nouvelles applications et mises à jour Android classiques, avec des exceptions pour certains appareils. Cela ne confirme pas la règle de 2026. Vérifie [l’exigence officielle de target API](https://support.google.com/googleplay/android-developer/answer/11926878?hl=fr) et l’avertissement dans Play Console, puis adapte SDK, outils et comportement Android. Ne publie pas en supposant qu’API 34 ou 35 suffisent aujourd’hui.
- `minSdk = 29` signifie que l’application fonctionne à partir d’Android 10. Ce seuil de compatibilité est distinct du `targetSdk` demandé par Google Play.
- Le projet n’a pas encore de configuration de signature de production. Il faut créer une clé d’envoi, conserver le fichier de clé et ses mots de passe hors du dépôt, et en garder une sauvegarde sûre.
- Fixe l’identifiant technique avant la première publication. Il est actuellement `com.maree.app` ; un changement de nom visible ne nécessite pas forcément de le changer. L’identifiant d’une application publiée ne se remplace pas simplement ensuite.
- Chaque nouvelle version doit augmenter `versionCode`. Conserve le même identifiant et la chaîne de signature prévue pour ses mises à jour.

Dans Android Studio : **Build → Generate Signed Bundle / APK → Android App Bundle**, puis sélectionne le module `app`, ta clé d’envoi et la variante `release`. Les libellés peuvent varier selon la version d’Android Studio. Avec une configuration de signature déjà prête, la commande Gradle est `./gradlew :app:bundleRelease` ; le fichier se trouve habituellement dans `app/build/outputs/bundle/release/`. La configuration actuelle ne suffit pas à garantir un bundle signé.

Play App Signing conserve la clé qui signe les installations distribuées par Google Play ; la clé d’envoi sert à authentifier tes prochains bundles. Ne confonds pas ces deux rôles. Références : [Signer l’application et Play App Signing](https://developer.android.com/studio/publish/app-signing), [Créer et configurer l’application dans Play Console](https://support.google.com/googleplay/android-developer/answer/9859152?hl=fr).

**Pour le téléphone qui possède déjà l’APK de test :** une version Google Play peut utiliser une signature différente. Android peut alors refuser de l’installer comme mise à jour. Ne désinstalle pas l’APK actuel avant d’avoir prévu la conservation de tes données : la désinstallation efface les données locales et cette application n’a pas encore d’export ou de synchronisation.

## 4. Créer la fiche et remplir les déclarations

Dans Play Console, crée l’application avec son nom définitif, sa langue et sa catégorie. Prépare :

- une description fidèle, une icône et des captures provenant de la vraie application ;
- une adresse de contact et une **politique de confidentialité accessible par une URL publique** ; les documents du dépôt sont une base de préparation, pas une publication automatique de cette page ;
- les réponses sur la sécurité des données, le public visé, les publicités, l’accès à l’application et la classification du contenu ;
- les déclarations que la console demande pour les autorisations et services utilisés.

Cette application demande l’accès aux statistiques d’utilisation et une surcouche pour ses limites choisies par l’utilisateur. Son service de suivi utilise un service au premier plan de type `specialUse`. Explique précisément son utilité, le contrôle donné à l’utilisateur et fournis une démonstration si Play Console la demande. Ces fonctions peuvent faire l’objet d’une vérification ; leur présence dans un APK compilé ne garantit pas l’acceptation par Google Play. Référence : [Types de services au premier plan](https://developer.android.com/develop/background-work/services/fgs/service-types).

Réponds selon la version réellement envoyée. Aujourd’hui, le prototype n’a pas de compte, de publicité, de coach connecté ou de paiement actif. L’ajout d’un serveur et d’abonnements changera les données et destinataires à décrire dans la politique et la déclaration de sécurité des données. Voir aussi [les préparatifs de confidentialité](CONFIDENTIALITE_AVANT_PUBLICATION.md).

## 5. Envoyer le bundle aux tests, puis demander la production

1. Charge le bundle `release` signé dans la piste de test choisie et active Play App Signing selon les instructions de la console.
2. Corrige les erreurs de bundle, de niveau API et de déclarations signalées.
3. Installe depuis Google Play sur de vrais téléphones : premier lancement, pause du suivi, reprise, permissions refusées, limites atteintes, redémarrage, mise à jour avec données conservées et effacement des données.
4. Si les abonnements sont prêts, effectue aussi les tests d’achats décrits dans [Abonnements](ABONNEMENTS.md). Sinon, conserve une version gratuite fidèle à ses fonctions ; ne propose pas de faux achat.
5. Remplis les critères de test fermé si ton compte y est soumis. Demande ensuite l’accès à la production et soumets la version à la vérification Google Play.

La publication et son calendrier dépendent de la vérification du compte, des tests et de l’examen de Google. Ce guide ne donne ni accès au compte ni autorisation de publier à ta place.

## Ce qui reste à faire pour ce projet

Vérifier la disponibilité du nom Milo, confirmer le niveau API actuel, adapter les outils si nécessaire, créer et sécuriser la signature de production, valider sur téléphone, mettre la politique en ligne, remplir les déclarations et effectuer le parcours de tests du compte. Pour vendre Premium/Ultra, il faut également développer et tester les vrais abonnements : le prototype local ne les facture pas.
