# Préparer une vraie IA avec un quota gratuit

Selkie dispose d’un adaptateur serveur optionnel pour **Groq**, en plus d’OpenAI. Il s’agit d’un dialogue génératif distant : le fournisseur produit une réponse à la saisie et au contexte transmis. La bibliothèque préparée reste séparée, locale et sans frais API.

L’adaptateur est prêt à configurer, mais **aucun compte, clé, déploiement ni appel réel n’a été réalisé**. Groq est une option à évaluer, pas un fournisseur définitivement choisi. L’existence d’une offre gratuite ne garantit ni une IA gratuite illimitée, ni la disponibilité permanente d’un modèle, ni l’absence de frais d’infrastructure.

## Choix préparé pour le coach

Le modèle Groq par défaut est maintenant `llama-3.3-70b-versatile`, candidat pour des réponses plus nuancées que le précédent modèle 8B. Cette préférence n’est pas le résultat d’un essai comparatif en direct : aucun compte ni clé n’est configuré. Le modèle plus petit `llama-3.1-8b-instant` reste sélectionnable explicitement avec `GROQ_MODEL`. Changer de modèle ne change pas le nom du fournisseur annoncé dans le consentement. Aucun basculement automatique vers un modèle ou fournisseur payant n’est ajouté.

Groq (avec un Q) est le service qui exécute ces modèles ; Grok (avec un K) est une autre IA, de xAI. La gratuité d’un chatbot pour un particulier ne signifie pas que son API est gratuite pour une application. Gemini et d’autres services peuvent également proposer des quotas selon leur offre, mais leur disponibilité, leurs conditions applicables en France, le traitement des données et leurs frais doivent être vérifiés avant de les retenir. Il n’y a pas de fournisseur « meilleur et gratuit pour toujours » établi dans cette préparation.

Avant activation, confirmer le modèle et son quota dans le compte puis comparer ses réponses sur des questions françaises et anglaises représentatives : conseils concrets, explication des règles de Selkie et limites médicales. Comparer aussi la latence, les erreurs, les limites de tokens et les conditions de traitement des données. Le nombre de paramètres ne constitue pas une garantie de qualité, de sûreté ou de gratuité.

## Vérifier l’offre avant activation

Avec le titulaire autorisé du compte, consulter les pages officielles et les conditions du compte :

- [Catalogue des modèles](https://console.groq.com/docs/models) : vérifier le modèle accessible et sa disponibilité.
- [Limites de requêtes et de tokens](https://console.groq.com/docs/rate-limits) : relever les quotas réellement appliqués au compte et au modèle.
- [Traitement des données](https://console.groq.com/docs/your-data) : vérifier conservation, utilisation, contrats et transferts.

Ces pages n’ont pas pu être consultées depuis cet environnement le 12 octobre 2026, car ce domaine est bloqué par sa politique réseau. Les limites, conditions et disponibilités actuelles ne sont donc pas certifiées par ce guide. Le nom `llama-3.3-70b-versatile` est le défaut configurable du code et doit être confirmé avant activation ; le changer via `GROQ_MODEL` si nécessaire.

## Configuration uniquement sur le serveur

Suivre le [guide d’activation complet](ACTIVER_COACH_IA.md) pour créer le projet Supabase, activer l’authentification et appliquer les migrations. Dans la configuration sécurisée de la fonction `coach` :

| Nom | Valeur à configurer |
| --- | --- |
| `COACH_AI_PROVIDER` | `groq` |
| `GROQ_API_KEY` | La clé privée du compte, comme secret serveur |
| `GROQ_MODEL` | Un modèle dont l’accès a été vérifié ; sinon le défaut indiqué ci-dessus |
| `COACH_ALLOWED_EMAILS` | Les seules adresses autorisées à tester, séparées par des virgules |

Ne jamais mettre `GROQ_API_KEY` dans l’APK, le dépôt, une capture d’écran ou le chat. Android reçoit l’URL du projet Supabase, sa clé publique et le nom public du fournisseur, sans clé privée. Supabase vérifie la session, l’e-mail confirmé et l’allowlist avant de traiter la question. La sélection du fournisseur et du modèle n’est pas acceptée depuis les données du téléphone.

La destination Groq est fixée à `https://api.groq.com/openai/v1/chat/completions`. OpenAI reste disponible si l’administrateur choisit explicitement `openai`, avec sa propre clé serveur. **Aucun secours automatique vers OpenAI ou une offre payante n’est prévu.** Si Groq refuse une demande ou atteint ses limites, Selkie renvoie une indisponibilité temporaire et demande la restitution du crédit de question réservé sur son serveur.

Construire également Android avec `SELKIE_COACH_PROVIDER=groq`, ou la propriété Gradle `selkieCoachProvider=groq`. Le défaut Android est `openai`. L’APK annonce le fournisseur correspondant dans le consentement et joint `X-Selkie-Coach-Provider` à la requête du coach. Ce choix public n’ajoute aucune clé et ne commande pas le fournisseur serveur.

En cas de différence entre Android et le serveur, le serveur renvoie `PROVIDER_MISMATCH` (HTTP 503) **avant tout débit de question et avant tout appel IA**. Les anciens APK sans ce header sont compatibles uniquement avec OpenAI. Pour Groq, les deux côtés doivent explicitement indiquer `groq`.

Le quota Selkie de la bêta et le quota du fournisseur sont indépendants : 100 questions pour un compte Selkie non abonné, au plus une demande toutes les cinq secondes, contre les limites du fournisseur selon son compte et son modèle. Une longue conversation utilise davantage de tokens qu’une question courte. Les messages de connexion et Supabase ont leurs propres limites et coûts éventuels. Les boutons locaux de démonstration Premium/Ultra ne modifient aucun droit côté serveur.

## Informer avant tout envoi

Vérifier que le consentement et les textes du build Android nomment Groq lorsque ce fournisseur est choisi, et renseigner les conditions réellement vérifiées avant activation. Le serveur transmet la question et au plus douze messages précédents ; il n’ajoute pas le bilan du téléphone, les points, les défis, l’e-mail, l’identifiant Supabase ou le jeton de session. Le texte saisi peut toutefois contenir des informations personnelles choisies par la personne.

L’adaptateur ne garantit pas une conservation nulle chez Groq. Ne pas transposer automatiquement le paramètre `store: false` d’OpenAI aux conditions de Groq. Vérifier et renseigner les contrats, réglages, durées, sous-traitants, transferts et procédures de suppression réellement applicables avant publication.

## Vérifications locales et réelles

```sh
node --test supabase/functions/coach/protocol.test.mjs supabase/functions/coach/provider.test.mjs
tsc --noEmit --strict --target ES2022 --module ESNext --moduleResolution bundler \
  --allowImportingTsExtensions supabase/functions/coach/protocol.ts supabase/functions/coach/provider.ts
```

Les 14 tests locaux utilisent des secrets synthétiques, sans contacter un fournisseur. Ils vérifient le choix explicite, la concordance du header de consentement, l’absence de basculement par configuration, les URL fixes, les formats, les limites et les champs transmis. Ils ne prouvent pas que le modèle existe encore, que le compte offre un quota gratuit ou que les migrations sont déployées.

Après configuration et information de l’utilisateur, valider un dialogue réel en bêta fermée, le contexte d’une seconde question, l’épuisement du quota Selkie, les refus et erreurs du fournisseur, la restitution du crédit et la déconnexion. Ne présenter le service comme actif qu’après cette validation.
