# Activer le vrai dialogue IA de Selkie

Depuis 0.9.0, le coach à règles fonctionne déjà hors ligne, sans compte ni frais API. Ce guide concerne seulement le dialogue génératif distant facultatif, pas l’utilisation du [coach à conseils préparés](COACH_HORS_LIGNE.md).

## État réel

Le client Android et le serveur sont implémentés : connexion par code e-mail, conversation, transmission HTTPS, quota, erreurs et renouvellement des sessions. Le serveur prépare deux adaptateurs, OpenAI et Groq, sélectionnés explicitement dans sa configuration. Les conseils fixes restent séparés et explicitement hors ligne.

Le service n’est pas activé : aucun fournisseur ni compte n’a été choisi pour cette nouvelle préparation. Aucun accès, secret ou crédit API n’est configuré dans cet environnement. Un APK sans configuration affiche l’activation nécessaire et ne simule aucune réponse IA. L’ajout de l’adaptateur Groq n’active aucun compte et ne lance aucun appel.

Les nouveaux builds Android choisissent le fournisseur annoncé dans le consentement avec `SELKIE_COACH_PROVIDER` ou la propriété Gradle `selkieCoachProvider`, limité à `openai` ou `groq`. Ce choix doit correspondre à la configuration du serveur. Vérifier le consentement et les conditions réelles de traitement avant activation ; le changement de variable serveur ne remplace pas l’information de l’utilisateur.

## À faire avec le parent titulaire

1. Créer un compte sur [Supabase](https://supabase.com/dashboard), puis un **nouveau projet Selkie**. Conserver le mot de passe de la base en privé et choisir une région adaptée, notamment en Europe pour un lancement français. Le suivi local n’a pas besoin d’être transféré dans cette base.
2. Choisir un fournisseur avec le titulaire autorisé pour ses contrats. Pour OpenAI, créer un compte sur [OpenAI Platform](https://platform.openai.com/), ajouter le crédit ou le moyen de paiement demandé et créer une clé de projet ; un abonnement ChatGPT ne paie pas ces appels API. Pour essayer une offre avec quota gratuit, consulter le [guide Groq](IA_AVEC_QUOTA_GRATUIT.md) et vérifier les conditions actuelles du compte avant de créer une clé.
3. Configurer le fournisseur et sa clé **uniquement sur le serveur Supabase**, selon le tableau ci-dessous. Ne pas coller de clé dans le chat, GitHub ou l’APK. Configurer également `COACH_ALLOWED_EMAILS` avec les seules adresses e-mail autorisées à tester le coach, séparées par des virgules. Ce filtrage reste obligatoire pour cette première bêta. La clé et le modèle du fournisseur non sélectionné ne sont pas utilisés.
4. Activer l’authentification e-mail de Supabase. Le modèle de courriel de connexion doit afficher `{{ .Token }}` pour que l’utilisateur puisse recopier le code dans Selkie. Vérifier les limites d’envoi et configurer un service SMTP adapté avant un lancement public.
5. Dans ce **nouveau projet**, appliquer dans l’ordre `supabase/migrations/20261010_maree.sql` puis `supabase/migrations/20261011_coach_connection.sql`, et déployer `supabase/functions/coach`. Pour un projet contenant déjà des données, sauvegarder et vérifier les migrations avant application. Les réglages de `supabase/config.toml` désactivent la vérification JWT du gateway : le handler vérifie lui-même chaque utilisateur avec `auth.getUser`, son e-mail confirmé et la liste de testeurs. Ne pas désactiver cette vérification dans le handler.
6. Récupérer **l’URL du projet et sa clé publique `publishable` ou `anon`**. Ce sont les seules informations d’accès à fournir pour construire l’APK connecté ; le nom public du fournisseur se configure séparément à l’étape suivante. Ne jamais utiliser la clé `service_role`, `sb_secret_…` ou une clé de fournisseur IA dans Android.
7. Configurer `SELKIE_SUPABASE_URL` et `SELKIE_SUPABASE_PUBLIC_KEY` au moment de la compilation, ou les propriétés Gradle `selkieSupabaseUrl` et `selkieSupabasePublicKey`. Configurer aussi `SELKIE_COACH_PROVIDER=groq` ou `selkieCoachProvider=groq` si le serveur utilise Groq ; le défaut Android est `openai`. Ces valeurs publiques peuvent être intégrées à l’APK ; les clés privées restent sur le serveur.
8. Construire et vérifier l’application, augmenter le numéro de version pour la distribution et signer avec la [clé fixe conservée](SIGNATURE_ANDROID.md). Installer sur le téléphone, accepter l’envoi des échanges, recevoir un code e-mail puis envoyer une vraie question. Vérifier la réponse, une seconde question utilisant le contexte, les erreurs réseau, la reconnexion et le quota. Tant que ce test n’a pas réussi, ne pas annoncer le coach comme connecté.

L’activation peut se faire depuis les tableaux de bord ou avec la CLI Supabase. Pour un déploiement dans cet environnement, l’accès Supabase doit être configuré comme secret sécurisé et les destinations réseau du projet autorisées. Aucun compte ou moyen de paiement n’est créé automatiquement.

| Fournisseur | Sélection serveur | Secret serveur | Modèle configurable | Défaut du code |
| --- | --- | --- | --- | --- |
| OpenAI | `COACH_AI_PROVIDER=openai` | `OPENAI_API_KEY` | `OPENAI_MODEL` | `gpt-4.1-mini` |
| Groq | `COACH_AI_PROVIDER=groq` | `GROQ_API_KEY` | `GROQ_MODEL` | `llama-3.3-70b-versatile` |

Une variable `COACH_AI_PROVIDER` absente conserve OpenAI pour les configurations existantes. Une valeur inconnue, une clé sélectionnée absente ou un modèle mal formé rendent le serveur non configuré, avant tout débit du quota Selkie. Les modèles par défaut ne garantissent pas leur disponibilité actuelle : vérifier le catalogue et les droits du compte avant activation. Les URL sont fixées dans le code ; aucune URL de fournisseur ne peut être fournie par le téléphone ou une variable de configuration. Aucun basculement automatique vers OpenAI n’a lieu si Groq échoue.

Android joint `X-Selkie-Coach-Provider` à la seule requête du coach pour déclarer le fournisseur présenté dans le consentement. Le serveur compare cette valeur à son propre choix avant le quota et l’appel IA. Une divergence renvoie `PROVIDER_MISMATCH` avec HTTP 503, sans consommer de question ni contacter un fournisseur. Les anciens clients sans ce header restent acceptés uniquement par un serveur OpenAI ; Groq exige `groq`. Le header ne change jamais le fournisseur du serveur.

## Coût et droits d’accès

La bêta limite un compte non abonné à 100 questions, sans paiement demandé au testeur, avec au plus une demande par utilisateur toutes les cinq secondes. Ce quota Selkie ne signifie pas que l’API ou l’hébergement sont gratuits pour l’éditeur. Une réponse du fournisseur absente ou en erreur déclenche une demande de restitution du crédit réservée au serveur ; le délai de cinq secondes reste applicable. Le fonctionnement transactionnel de ces migrations doit être vérifié dans le projet créé avant activation.

Les boutons de démonstration Premium/Ultra de l’APK ne sont **jamais** envoyés au serveur et n’accordent pas d’accès IA illimité. Le champ historique `profiles.premium` ne doit être modifié que par un administrateur de confiance ; ne pas l’activer pour une bêta sans budget. Avant vente, remplacer cette logique historique par des droits des trois offres vérifiés et expirables selon les achats Google Play.

Les plafonds/budgets proposés par un fournisseur peuvent être des alertes plutôt qu’un arrêt garanti des appels : vérifier leurs conditions. La liste fermée de testeurs, les quotas et le modèle choisi limitent les appels de cette bêta ; surveiller les consommations réelles avant de l’ouvrir davantage.

Un quota gratuit Groq éventuel est défini par le fournisseur et le compte, notamment en requêtes et en tokens. Il peut être atteint avant les 100 questions Selkie. L’application affiche alors une indisponibilité temporaire et ne migre pas la requête vers un service payant. Les frais et limites de Supabase et des e-mails de connexion restent distincts.

## Données

Le suivi, les points et les défis restent locaux. La connexion envoie l’adresse e-mail à Supabase. Une question transmet au serveur la saisie et au plus douze messages précédents, puis les transmet au fournisseur choisi, OpenAI ou Groq ; aucun bilan du téléphone n’est joint automatiquement. L’e-mail, l’identifiant du compte et le jeton Supabase ne sont pas ajoutés à la requête IA. Le code n’écrit aucun journal des messages, adresses ou jetons.

L’adaptateur OpenAI conserve `store: false`. L’adaptateur Groq utilise Chat Completions sans streaming ; cela ne prouve pas une absence de conservation ou d’utilisation des données par Groq. Vérifier les conditions, réglages, sous-traitants et transferts de chaque fournisseur avant de renseigner la politique. Aucun des adaptateurs ne garantit à lui seul une suppression des traces techniques côté fournisseur.

La conversation reste en mémoire pendant l’ouverture de l’onglet, sans archive du chat sur l’appareil. Les jetons de session restent dans le stockage privé du téléphone, exclu des sauvegardes Android. La déconnexion sur le téléphone et l’effacement local suppriment ces jetons ; ils ne suppriment pas le compte Supabase ni ses traces serveur. Les demandes de suppression du compte doivent être traitées par le responsable avant diffusion publique.

Avant une ouverture publique, compléter la politique avec la région et les contrats réellement choisis, la conservation, les transferts éventuels, les modalités de suppression et l’identité du véritable responsable. Le texte embarqué décrit le dialogue facultatif ; ce guide et le code ne constituent pas une certification juridique.

## Vérifications reproductibles

```sh
./gradlew --no-daemon :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
node --test supabase/functions/coach/protocol.test.mjs supabase/functions/coach/provider.test.mjs
# Avec TypeScript disponible localement :
tsc --noEmit --strict --target ES2022 --module ESNext --moduleResolution bundler \
  --allowImportingTsExtensions supabase/functions/coach/protocol.ts supabase/functions/coach/provider.ts
```

Les tests serveur couvrent les entrées, l’historique limité, l’allowlist, le choix de fournisseur, ses clés et modèles, la concordance du header de consentement, les destinations fixes, les champs réellement transmis et l’extraction du texte de réponse. Les tests Android couvrent notamment les sessions, les erreurs et les configurations refusées. Ils ne remplacent pas un appel réel au serveur, les essais SQL de concurrence ou les contrôles sur téléphone. Le contrôle TypeScript ci-dessus couvre les modules de protocole et d’adaptation ; il ne vérifie pas les dépendances Deno/Supabase du handler déployé.

Contrôles de cette préparation serveur du 12 octobre 2026 : **14 tests serveur réussis**, contrôle TypeScript strict de `protocol.ts` et `provider.ts` réussi, sans requête vers un fournisseur ni secret réel. Aucun build Android n’a été lancé pour cet adaptateur.

La connexion réelle Supabase/fournisseur, les migrations SQL et le dialogue connecté sur téléphone n’ont pas encore été testés, faute de comptes et d’accès configurés. Le coach à conseils préparés reste utilisable hors ligne ; aucun coach génératif actif n’est annoncé sur la seule base de ces tests locaux.
