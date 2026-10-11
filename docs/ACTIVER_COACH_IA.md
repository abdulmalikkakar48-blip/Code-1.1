# Activer le vrai dialogue IA de Selkie

Depuis 0.9.0, le coach à règles fonctionne déjà hors ligne, sans compte ni frais API. Ce guide concerne seulement le dialogue génératif distant facultatif, pas l’utilisation du [coach à conseils préparés](COACH_HORS_LIGNE.md).

## État réel

Le client Android et le serveur sont implémentés : connexion par code e-mail, conversation, transmission HTTPS, réponses OpenAI, quota, erreurs et renouvellement des sessions. Les conseils fixes restent séparés et explicitement hors ligne.

Le service n’est pas activé : l’éditeur a indiqué n’avoir encore aucun compte Supabase ou OpenAI API. Aucun accès, secret ou crédit API n’est configuré dans cet environnement. L’APK de test 0.8.1 inclut le client sans configuration : il affiche l’activation nécessaire et ne simule aucune réponse IA. L’APK archivé 0.8.0 est antérieur à cette connexion.

## À faire avec le parent titulaire

1. Créer un compte sur [Supabase](https://supabase.com/dashboard), puis un **nouveau projet Selkie**. Conserver le mot de passe de la base en privé et choisir une région adaptée, notamment en Europe pour un lancement français. Le suivi local n’a pas besoin d’être transféré dans cette base.
2. Créer un compte API sur [OpenAI Platform](https://platform.openai.com/), avec le titulaire autorisé pour les contrats et paiements. Ajouter du crédit ou le moyen de paiement demandé et créer une clé de projet. Un abonnement ChatGPT ne paie pas ces appels API.
3. Configurer cette clé **uniquement sur le serveur Supabase**, sous le nom `OPENAI_API_KEY`. Ne pas la coller dans le chat, GitHub ou l’APK. Configurer également `COACH_ALLOWED_EMAILS` avec les seules adresses e-mail autorisées à tester le coach, séparées par des virgules. Ce filtrage est obligatoire pour cette première bêta. `OPENAI_MODEL` peut sélectionner un modèle disponible ; le défaut est `gpt-4.1-mini`.
4. Activer l’authentification e-mail de Supabase. Le modèle de courriel de connexion doit afficher `{{ .Token }}` pour que l’utilisateur puisse recopier le code dans Selkie. Vérifier les limites d’envoi et configurer un service SMTP adapté avant un lancement public.
5. Dans ce **nouveau projet**, appliquer dans l’ordre `supabase/migrations/20261010_maree.sql` puis `supabase/migrations/20261011_coach_connection.sql`, et déployer `supabase/functions/coach`. Pour un projet contenant déjà des données, sauvegarder et vérifier les migrations avant application. Les réglages de `supabase/config.toml` désactivent la vérification JWT du gateway : le handler vérifie lui-même chaque utilisateur avec `auth.getUser`, son e-mail confirmé et la liste de testeurs. Ne pas désactiver cette vérification dans le handler.
6. Récupérer **l’URL du projet et sa clé publique `publishable` ou `anon`**. Ce sont les seules valeurs à fournir pour construire l’APK connecté. Ne jamais utiliser la clé `service_role`, `sb_secret_…` ou la clé OpenAI dans Android.
7. Configurer `SELKIE_SUPABASE_URL` et `SELKIE_SUPABASE_PUBLIC_KEY` au moment de la compilation, ou les propriétés Gradle `selkieSupabaseUrl` et `selkieSupabasePublicKey`. Ces valeurs publiques peuvent être intégrées à l’APK ; les clés privées restent sur le serveur.
8. Construire et vérifier l’application, augmenter le numéro de version pour la distribution et signer avec la [clé fixe conservée](SIGNATURE_ANDROID.md). Installer sur le téléphone, accepter l’envoi des échanges, recevoir un code e-mail puis envoyer une vraie question. Vérifier la réponse, une seconde question utilisant le contexte, les erreurs réseau, la reconnexion et le quota. Tant que ce test n’a pas réussi, ne pas annoncer le coach comme connecté.

L’activation peut se faire depuis les tableaux de bord ou avec la CLI Supabase. Pour un déploiement dans cet environnement, l’accès Supabase doit être configuré comme secret sécurisé et les destinations réseau du projet autorisées. Aucun compte ou moyen de paiement n’est créé automatiquement.

## Coût et droits d’accès

La bêta utilise les 100 questions gratuites vérifiées sur le serveur, avec au plus une demande par utilisateur toutes les cinq secondes. Une réponse OpenAI absente ou en erreur déclenche une demande de restitution du crédit réservée au serveur. Le fonctionnement transactionnel de ces migrations doit être vérifié dans le projet créé avant activation.

Les boutons de démonstration Premium/Ultra de l’APK ne sont **jamais** envoyés au serveur et n’accordent pas d’accès IA illimité. Le champ historique `profiles.premium` ne doit être modifié que par un administrateur de confiance ; ne pas l’activer pour une bêta sans budget. Avant vente, remplacer cette logique historique par des droits des trois offres vérifiés et expirables selon les achats Google Play.

Les plafonds/budgets proposés par un fournisseur peuvent être des alertes plutôt qu’un arrêt garanti des appels : vérifier leurs conditions. La liste fermée de testeurs, les quotas et le modèle choisi limitent les appels de cette bêta ; surveiller les consommations réelles avant de l’ouvrir davantage.

## Données

Le suivi, les points et les défis restent locaux. La connexion envoie l’adresse e-mail à Supabase. Une question transmet au serveur la saisie et au plus douze messages précédents, puis les transmet à OpenAI ; aucun bilan du téléphone n’est joint automatiquement. Le serveur utilise `store: false`, sans garantie sur la conservation technique des fournisseurs.

La conversation reste en mémoire pendant l’ouverture de l’onglet, sans archive du chat sur l’appareil. Les jetons de session restent dans le stockage privé du téléphone, exclu des sauvegardes Android. La déconnexion sur le téléphone et l’effacement local suppriment ces jetons ; ils ne suppriment pas le compte Supabase ni ses traces serveur. Les demandes de suppression du compte doivent être traitées par le responsable avant diffusion publique.

Avant une ouverture publique, compléter la politique avec la région et les contrats réellement choisis, la conservation, les transferts éventuels, les modalités de suppression et l’identité du véritable responsable. Le texte embarqué décrit le dialogue facultatif ; ce guide et le code ne constituent pas une certification juridique.

## Vérifications reproductibles

```sh
./gradlew --no-daemon :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
node --test supabase/functions/coach/protocol.test.mjs
```

Les tests couvrent les entrées, clés et destinations refusées, les sessions, l’historique limité, les réponses invalides, les erreurs de quota et l’extraction du vrai texte fournisseur. Ils ne remplacent pas un appel réel au serveur, les essais SQL de concurrence ou les contrôles sur téléphone.

Contrôles locaux du 11 octobre 2026 : compilation réussie, **38 tests Android** sans échec, **5 tests du protocole serveur** réussis, contrôle TypeScript strict du protocole réussi, Android Lint sans erreur avec 13 avertissements. La configuration de compilation refuse une clé privée avant de produire l’APK ; ce refus a également été vérifié avec une valeur synthétique sans secret réel.

La connexion réelle Supabase/OpenAI, les migrations SQL et le fonctionnement sur téléphone n’ont pas encore été testés, faute de comptes et d’accès configurés. L’APK 0.8.1 est distribué pour essayer l’icône et le fonctionnement local, sans annoncer un coach actif. L’APK de design 0.8.0 reste archivé.
