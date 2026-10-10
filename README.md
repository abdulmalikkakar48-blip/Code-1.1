# Marée 🌊 — application Android de réduction du temps d'écran

**Version actuelle : 0.2.0 (APK de test et code source).** Interface entièrement en français.

Ce projet suit le cahier des charges fourni, en Kotlin + Jetpack Compose Material 3, `minSdk 29` (Android 10), `targetSdk 34`, package `com.maree.app`, Gradle 8.7, AGP 8.5.2, Kotlin 1.9.24.

## Mise à jour 0.2.0

- Premier lancement : explication des autorisations avant l’accueil, avec accès à la confidentialité. L’accès aux données d’utilisation et la surcouche sont nécessaires au suivi ; les notifications restent facultatives. Un mode découverte fonctionne sans suivi. Une mise à jour depuis 0.1.0 présente aussi cet accueil, sans effacer les limites, points ou défis.
- Quatre onglets : Accueil, Quotidien, Défis et Plus. Quotidien regroupe le bilan des compteurs suivis, une idée de pause et la carte du futur coach. Les mesures sont masquées lorsque le suivi est en pause.
- Thème clair crème, vert, corail, lavande et sable ; icônes de navigation vectorielles.
- Pause du suivi et gestion des autorisations depuis Plus. Ni le démarrage du téléphone ni une permission déjà accordée ne démarrent le suivi avant la fin du parcours et son activation.
- Politique hors ligne dans l’accueil et Plus, contact fourni par l’éditeur, suppression locale avec confirmation. La suppression arrête le suivi et retire limites, verrous, points, défis et choix d’accueil, puis revient au parcours initial. Elle ne révoque pas les permissions Android et ne supprime pas les statistiques détenues par Android.

La politique est une information pour la version locale, pas une certification juridique. Le responsable n’a pas encore fourni son identité réelle ; elle est explicitement à compléter avant diffusion publique. Les conditions et transferts de la messagerie de contact restent également à vérifier. Voir [le texte de confidentialité](docs/CONFIDENTIALITE.md) et [les points avant publication](docs/CONFIDENTIALITE_AVANT_PUBLICATION.md).

## Ce qui est codé dans l'application Android

- Sélection des applications installées disposant d'une icône de lancement ; limite quotidienne par paliers de 5 minutes, à partir de 5 minutes, 30 minutes par défaut.
- Maximum de 5 applications en gratuit ; modification et suppression de la liste.
- Mesure en millisecondes de l'utilisation au premier plan à partir de `UsageStatsManager` / `UsageEvents` ; affichage du temps en minutes et d'une barre de progression.
- Avertissement à 80 % (une fois par application et par jour, sous réserve de l'autorisation des notifications).
- Verrou de 24 h à partir de l'atteinte de la limite, surcouche rouge `TYPE_APPLICATION_OVERLAY` au-dessus de l'application suivie, bouton d'accueil, compte à rebours. Le service réévalue le suivi toutes les 2 secondes.
- Déblocage après 24 h et remise de la limite complète via une base locale (Android ne remet pas ses propres compteurs à zéro). Réinitialisation anticipée à −10 points, retrait de l'application qui supprime son verrou.
- Points d'honneur : 100 au départ, minimum 0 ; +10 par période de 24 h respectant les limites, −5 pour la réouverture d'une app verrouillée (une fois au maximum par 30 s), −10 pour la réinitialisation, +5 tous les 5 défis cumulés.
- Deux séries de 10 défis, en alternance tous les 21 jours. Défis validables une seule fois par cycle.
- Blocage des ajouts, changements de limite et réinitialisations à 0 point hors premium ; les défis permettent de regagner des points.
- Redémarrage du service après démarrage du téléphone *lorsque le système l'autorise*.

## Ce qui reste à activer / développer

- **Coach IA** : interface informative uniquement dans l'APK. Un exemple de fonction sécurisée Supabase se trouve dans `supabase/functions/coach/index.ts`, avec quota des 100 questions sur le serveur, mais le chat Android et l'authentification ne sont pas encore connectés.
- **Premium** : l’offre est à l’étude, sans prix ni achat actif. Aucun abonnement n’est vendu. L'achat demandera Google Play Billing ou l'équivalent Galaxy Store, une vérification serveur et des comptes développeur actifs. **Ne pas utiliser Stripe** pour cet achat.
- **Compte email et sauvegarde cloud** : tables RLS Supabase préparées mais pas encore reliées à l'APK. Les données actuelles sont locales dans `SharedPreferences` et `allowBackup=false`.
- **Tests sur un téléphone Samsung réel et distribution** : nécessaires avant mise en production.

## Obtenir l'APK avec GitHub Actions

1. Créer un dépôt GitHub vide et y envoyer **le contenu** de ce répertoire (`app/`, `.github/`, etc.).
2. Ouvrir l'onglet **Actions**, sélectionner **Construire l'APK Android**, puis lancer **Run workflow** (ou envoyer un commit sur `main`).
3. À la fin de la tâche `android`, récupérer l'artefact `Maree-Android-debug-APK` ; il contient `app-debug.apk`.
4. Transférer l'APK sur un smartphone Android 10+ et autoriser son installation depuis la source choisie.
5. Ouvrir Marée, lire les explications, puis activer les accès nécessaires au suivi. Les notifications sont facultatives, et le mode découverte reste disponible.

Le projet a été compilé dans l'environnement cloud. L'APK de test est généré dans `app/build/outputs/apk/debug/app-debug.apk`. Le wrapper Gradle 8.7 est inclus, avec vérification SHA-256 de sa distribution ; GitHub Actions utilise ce wrapper pour compiler, exécuter les tests unitaires et lancer Android Lint.

## Compilation locale avec Android Studio

1. Ouvrir le dossier dans **Android Studio** avec **JDK 17** et Android SDK API 34 installé.
2. Laisser Android Studio synchroniser le projet avec le wrapper Gradle fourni.
3. Lancer **Build > Build APK(s)**, ou utiliser :

```sh
./gradlew --no-daemon :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

Sous Windows, utiliser `gradlew.bat`. Définir `ANDROID_HOME` vers le SDK installé ou renseigner `sdk.dir` dans un fichier local `local.properties` non versionné.

## Développement dans cet environnement cloud

Java 17, Gradle 8.7 et le SDK Android 34 sont installés sous `/workspace/.cloud-setup`. Les caches et la configuration Java restent hors du code source. Pour réactiver les outils dans un nouveau terminal :

```sh
source /workspace/.cloud-setup/activate.sh
cd /workspace/Code-1.1
./gradlew --no-daemon --max-workers=2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

L'activation utilise le proxy et le certificat de confiance fournis par la plateforme, avec vérification TLS conservée. Aucun secret n'est nécessaire pour les fonctions locales de cette version. Le script d'installation et les instructions de démarrage sont également enregistrés dans la configuration de l'environnement.

Les tests de `SetupPreferencesTest` couvrent l’absence de démarrage avant l’accueil, le mode découverte, la pause/reprise et le caractère facultatif des notifications. Le test de suppression de `StoreTest` vérifie l’effacement des verrous, points, défis et clés supplémentaires, puis une reprise propre. Les autres tests de `StoreTest` couvrent les avertissements à 80 %, les verrous de 24 h, les récompenses et pénalités, les réinitialisations et les cycles de défis. Le rafraîchissement de l'interface laisse au service la création des alertes et verrous ; un redémarrage du service n'entraîne plus de pénalité à sa première observation. La liste des applications est chargée en arrière-plan, avec un indicateur de chargement et une action de nouvelle tentative en cas d'erreur.

## Limites de fonctionnement Android et précautions

- Android ne garantit pas un blocage inviolable : la désinstallation de Marée, la révocation de ses autorisations, certains modes économie d'énergie Samsung, la mise en veille du service ou les restrictions de fenêtre superposée peuvent interrompre le suivi et le blocage. L'application **ne prétend pas empêcher ces actions**.
- Une surcouche `TYPE_APPLICATION_OVERLAY` ne recouvre pas nécessairement toutes les zones système et Android peut intercepter le geste de retour ou protéger l'écran verrouillé. Le bouton d'accueil est prévu ; la gestion des gestes système dépend du téléphone.
- Les statistiques `UsageEvents` peuvent avoir un délai, des événements manquants et des particularités sur plusieurs fenêtres. Les sessions encore ouvertes **depuis avant minuit** peuvent être sous-comptées après minuit. Le projet est un prototype à tester sur Samsung avant diffusion.
- Le suivi lit les événements d'utilisation à partir de minuit à chaque contrôle. Ce fonctionnement simple peut être optimisé pour limiter le coût énergétique sur les téléphones plus lents.
- Les récompenses de 24 h se basent sur les incidents observés pendant l'exécution ; après une longue interruption, certaines limites historiques ne peuvent plus être vérifiées parfaitement. Au maximum 7 périodes sont rattrapées par traitement.
- Lorsqu'une application est retirée pendant une période, une limite précédemment franchie demeure considérée comme une rupture de discipline pour la période (choix explicite sur un point resté ouvert dans le cahier).
- Avant Google Play, vérifier les politiques actuelles concernant `PACKAGE_USAGE_STATS`, les surcouches et le type `FOREGROUND_SERVICE_SPECIAL_USE`. Une justification du service au premier plan est incluse dans le manifeste.

## Backend Supabase préparé (non branché)

Le document décrit un projet Supabase déjà créé, ainsi que la table `public.days` du prototype web. Le script `supabase/migrations/20261010_maree.sql` crée de nouvelles tables **sans modifier la table `days`** :

- `profiles` (statut premium géré côté serveur),
- `user_state` (charge utile JSON de sauvegarde),
- `coach_usage` (quota IA, avec sécurité par ligne),
- `consume_coach_credit()` (RPC transactionnelle atomique et limitée aux utilisateurs authentifiés).

Pour déployer l'exemple côté serveur :

```sh
supabase link --project-ref lzusszoaxrgfkqcfpwrw
supabase db push
supabase secrets set OPENAI_API_KEY="VOTRE_CLE_PRIVEE"
supabase functions deploy coach
```

**Ne pas utiliser ces commandes sans vérifier et sauvegarder les données préexistantes.** Déployer le backend ne suffit pas à activer le chat dans cet APK : il faut également implémenter la connexion email, l'appel authentifié à la fonction et la synchronisation Android. La clé OpenAI ne doit jamais être incorporée dans l'application mobile.

## Structure du projet

```text
app/src/main/java/com/maree/app/
  MainActivity.kt       → navigation, limites, choix de suivi et suppression
  OnboardingScreen.kt  → explications et autorisations avant l’accueil
  SetupPreferences.kt  → fin de parcours et activation/pause du suivi
  DailyScreen.kt        → bilan du jour, conseils et futur coach
  PrivacyContent.kt    → texte et identité de confidentialité
  PrivacyScreen.kt     → information hors ligne et contact
  MareeTheme.kt         → palette claire et accents
  Store.kt              → SharedPreferences, règles des points, cycles et verrous
  UsageReader.kt        → événements d'activité et temps d'écran
  MonitorService.kt     → surveillance, notifications, pénalités
  LockActivity.kt       → écran rouge et surcouche Android
  BootReceiver.kt       → tentative de reprise après redémarrage
.github/workflows/build.yml → génération de l'APK de test
supabase/                   → migration SQL et fonction coach (préparation)
```

## Confidentialité

Le moteur de suivi fonctionne en local. L'APK ne contient pas de clé API, n'envoie pas les statistiques à un serveur et ne sollicite pas de paiement. Le backend Supabase est fourni à titre de code de préparation ; aucune fonction cloud n'est exécutée par cette version de l'APK.

L’APK de mise à jour distribué dans `downloads/Maree-Android.apk` conserve la signature de l’APK 0.1.0 construit dans cet environnement. Installer la mise à jour sans désinstaller préserve les données. Les builds GitHub Actions utilisent une signature debug propre au runner : ne pas les présenter comme des mises à jour compatibles tant qu’une signature de distribution stable n’est pas configurée. Aucune clé de signature n’est enregistrée dans le dépôt.
