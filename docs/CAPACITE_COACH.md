# Coach connecté : capacité partagée et protection des quotas

Préparation du 13 octobre 2026. Le compte Groq créé par l’utilisateur n’est pas relié à cet environnement. Aucun projet Supabase, clé privée, déploiement ou appel IA réel n’est configuré ici. Les quotas réels du compte et du modèle restent à recopier depuis leur tableau de bord ; aucune valeur de gratuité n’est inventée dans le serveur.

## Ce qui consomme

Mille personnes utilisant Selkie ne font pas nécessairement mille appels IA. Le suivi Android et les conseils préparés restent sur leurs téléphones. Une question envoyée au coach génératif consomme des demandes et des tokens ; son historique augmente le texte traité. Les quotas Groq sont partagés selon le compte/organisation et le modèle, y compris avec d’autres clés ou applications du même compte. Les limites Supabase (authentification, base, fonctions, e-mails et trafic) sont indépendantes.

## Protections ajoutées au serveur

- Admission globale dans PostgreSQL, avec verrou de ligne partagé entre les instances Edge Function.
- Plafonds de demandes et de tokens réservés sur des fenêtres glissantes de 60 secondes et 24 heures.
- Plafond de simultanéité : les appels en cours prennent une place pendant au plus 45 secondes ; l’appel HTTP au fournisseur a un timeout de 20 secondes.
- Configuration absente, désactivée ou base inaccessible : aucun appel IA n’est effectué.
- Refus du fournisseur avec HTTP 429 : pause partagée utilisant Retry-After, ou une minute par défaut. Les appels déjà admis peuvent encore se terminer ; aucun changement automatique vers un service payant.
- Les appels échoués restent comptés dans le budget fournisseur, car une erreur ne prouve pas qu’aucune ressource n’a été consommée. Une fin de bail libère uniquement sa place de simultanéité.
- La restitution du crédit personnel Selkie est demandée lorsqu’aucune réponse utilisable n’est obtenue, y compris si l’admission globale refuse. Cela ne rembourse pas les éventuels frais du fournisseur. Un crash ou un échec de base peut empêcher la restitution : vérifier les codes opérationnels, sans promettre un succès inexistant.

Tous les utilisateurs, y compris ceux ayant un droit payant vérifié, partagent ces limites. Une offre ne peut pas garantir des ressources IA gratuites infinies. Le statut Premium simulé sur le téléphone ne modifie pas le quota serveur. Le serveur reste en bêta fermée avec allowlist ; ce changement ne l’ouvre pas à des milliers de comptes publics.

## Renseigner les vrais plafonds

1. Dans Groq, sélectionner le modèle réellement disponible et relever demandes/minute, demandes/jour, tokens/minute et tokens/jour, ainsi que toute limite plus stricte de l’organisation. Vérifier les frais, la facturation et les périodes de remise à zéro. Ne pas confondre compte gratuit et API illimitée.
2. Garder une marge sous ces limites pour les autres consommations, les différences de comptage et les erreurs. Le token budget du serveur utilise volontairement une estimation généreuse : taille UTF-8 du corps, 1 024 unités de marge et 220 pour la sortie. Ce n’est pas un tokenizer certifié ; les usages rapportés par le fournisseur supérieurs à l’estimation sont conservés et signalés.
3. Appliquer les migrations dans l’ordre, y compris `20261013_coach_capacity.sql`, dans le projet réel après revue/sauvegarde adaptée. Le test local ne remplace pas cette opération.
4. Créer la ligne `coach_capacity_limits` dans SQL Editor ou via un administrateur. Aucun utilisateur Android n’a le droit de lire/modifier ces réglages ni de réserver un bail global.

Exemple **hypothétique de test**, à remplacer avec les plafonds vérifiés avant activation :

```sql
insert into public.coach_capacity_limits
  (scope, requests_per_minute, requests_per_day, tokens_per_minute,
   tokens_per_day, max_concurrent, enabled)
values ('groq', 30, 1000, 10000, 200000, 3, false);
```

Ces nombres ne sont PAS ceux du compte créé. La ligne reste désactivée. Activer uniquement après vérification du compte, du modèle, des contrats/données et d’un essai réel. Revoir les plafonds lors d’un changement de modèle. Ce compteur couvre ce projet Supabase ; plusieurs projets utilisant le même compte Groq doivent coordonner leurs budgets ou utiliser une admission commune.

## Combien d’utilisateurs ?

Avec les hypothèses ci-dessus et une réservation moyenne de **2 000 tokens par question** :

| Ressource | Calcul | Capacité hypothétique |
| --- | --- | --- |
| Demandes par minute | 30 | 30 questions/minute |
| Tokens par minute | 10 000 ÷ 2 000 | 5 questions/minute |
| Demandes par jour | 1 000 | 1 000 questions/jour |
| Tokens par jour | 200 000 ÷ 2 000 | 100 questions/jour |
| Utilisateurs actifs à 3 questions/jour | 100 ÷ 3 | Environ 33/jour |

La ressource la plus contraignante gagne. Mille personnes posant chacune trois questions demanderaient 3 000 réponses/jour : cela ne tient pas dans cet exemple. Mille installations qui utilisent surtout le suivi local ne représentent pas ce volume d’IA. Ces moyennes ne garantissent pas l’absorption d’une pointe ; la simultanéité, la latence et les fenêtres par minute comptent aussi.

## Voir l’approche des limites

Dans le projet réel, l’administrateur peut consulter :

```sql
select public.coach_capacity_health('groq');
```

Le rapport affiche plafonds, demandes sur une minute et 24 heures, tokens réservés ou rapportés, tokens effectivement rapportés, appels actifs, cooldown et dépassements d’estimation. Les indicateurs sont `normal`, `warning` à partir de 80 %, `critical` à partir de 95 %, `provider_cooldown` ou `disabled`. Ce sont des indicateurs SQL privés, pas une notification e-mail déjà activée. Programmer leur surveillance et les alertes opérateur dans le projet réel ; surveiller aussi le tableau de bord Groq et les ressources Supabase.

Les erreurs comptables émettent uniquement les codes `COACH_CAPACITY_ADMISSION_FAILED`, `COACH_CAPACITY_COMPLETION_FAILED` et `COACH_CREDIT_REFUND_FAILED`, sans message utilisateur, identité, clé ou corps d’erreur du fournisseur. Vérifier ces codes dans les logs opérationnels avant d’ouvrir la bêta.

Les refus incluent Retry-After. L’APK actuel n’effectue pas de boucle de réessais automatiques ; son coach local reste une option. Une grande conversation peut dépasser à elle seule le budget d’une minute et être refusée : commencer une conversation plus courte ou ajuster le budget réellement disponible. Les clients futurs devraient respecter le délai et informer précisément de la reprise. Aucun fichier d’attente illimité de messages n’est créé.

## Conservation et limites de cette protection

Les tables de capacité enregistrent seulement bail aléatoire, scope fournisseur, timestamps, réservations, usages rapportés et résultat technique ; pas de texte, e-mail, ID utilisateur ou jeton de connexion. Les métriques sont privées. La purge des données de plus de 48 h est effectuée à l’admission et disponible via `select public.prune_coach_capacity();` ; programmer cette purge régulièrement dans le projet réel pour les périodes sans appels. Sans cette tâche, le nettoyage n’est pas garanti à 48 h d’inactivité. Documenter ces mesures et les logs de l’hébergeur avant activation.

Cette protection ne garantit pas une facture nulle. Les appels entrants, validations d’authentification et requêtes à la base utilisent Supabase même quand l’IA est refusée. Compléter avec limites d’infrastructure, contrôle des inscriptions, protection contre les abus et un test de charge réaliste. Aucun fournisseur, endpoint public, e-mail, alerting externe ou service payant n’a été activé par cette préparation.

## Tests locaux

```sh
node --test supabase/functions/coach/*.test.mjs
python3 tools/test_coach_capacity.py
```

Le second script utilise le Docker local du cloud et une image PostgreSQL 16 épinglée par digest. Il crée une base isolée sans réseau ni port publié et supprime son conteneur/volume en fin d’essai. Il vérifie notamment budgets, fenêtres glissantes, RLS/privilèges, expiration des places, cooldown, compteurs, purge, seuils et deux séries de 80 réservations concurrentes. Ces essais ne valident pas les performances de milliers d’utilisateurs sur le projet Supabase réel ni les quotas du compte Groq.
