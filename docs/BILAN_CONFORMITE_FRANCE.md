# Selkie 0.10.4 — bilan des 19 points de la capture

Bilan technique et documentaire du 12 octobre 2026. Périmètre : APK de test gratuit, natif Android, suivi et coach locaux, aucune vente ni compte serveur actif. L’éditeur de la future version publique n’a pas encore été choisi. Ce document ne certifie pas la conformité du service ni l’absence de sanction.

La capture est une liste de contrôle générale. Elle ne constitue pas une source de droit et ses 19 points ne sont pas 19 obligations identiques pour chaque application française. Les règles dépendent des traitements, du public, du statut de l’éditeur, du service et de la présence réelle de ventes. Une application gratuite n’est pas automatiquement exemptée des règles sur les données, les pratiques trompeuses ou les droits d’auteur.

## Résultat pour le prototype corrigé

**9 points en place, 6 à compléter ou valider, 4 hors du périmètre actuel.** « En place » signifie un élément ou contrôle identifié dans cette version, avec les limites précisées ci-dessous ; cela ne certifie pas tous les écrans ou une publication commerciale. Les résultats de compilation et les tests effectués sont dans [la validation de cette version](../downloads/VALIDATION-0.10.4.md).

| Nº | Point de la capture | État | Preuve, portée et suite |
| --- | --- | --- | --- |
| 1 | Check colour contrast | En place | Tests des couples de texte ≥4,5:1 et nouveau rôle de contours de contrôle ≥3:1 ; points d’eau limités pour préserver les petits textes. La revue de tous les rendus et grandes tailles de texte reste incluse dans le contrôle d’accessibilité. |
| 2 | Alt text on images | En place | Équivalent natif : boutons icônes nommés ; logo décoratif accompagné du nom Selkie ; icônes d’applications accompagnées de leurs noms. Une image décorative n’a pas besoin d’une annonce répétée. |
| 3 | Refund policy | Hors périmètre actuel | Aucun paiement ni abonnement réel. Une page explique les futures demandes, distingue résiliation et remboursement, et rappelle les droits de rétractation sous conditions. Elle devra être adaptée au vrai contrat avant vente. |
| 4 | Privacy policy page | À compléter ou valider | Politique FR/EN accessible avant le suivi ; données, finalités, bases, conservation, droits et contact décrits, export et effacement disponibles. Restent le responsable public définitif, la messagerie/transferts, le public mineur et l’adresse publique de politique pour Play. |
| 5 | Fix accessibility | À compléter ou valider | Consentement local correctement nommé, cibles tactiles, titres sémantiques, labels −/+5 min, réponse du coach annoncée poliment, animations système respectées. Essais TalkBack, taille de police et navigation complète encore nécessaires ; aucune certification d’accessibilité. |
| 6 | Remove fake reviews | En place | Aucun témoignage ou avis client fictif trouvé dans les écrans inspectés. Ne pas ajouter d’avis inventés ni de notes présentées comme réelles lors de la fiche Play. |
| 7 | T&C’s page | En place | Conditions d’utilisation du test gratuit FR/EN dans Confidentialité et données. Ce texte n’est pas une CGV de vente : les conditions commerciales devront être finalisées avant paiement. |
| 8 | Check 3rd party embeds | Hors périmètre actuel | Pas de WebView, vidéo intégrée ou carte externe dans le prototype. Les bibliothèques Android et les liens ouverts volontairement restent à distinguer des contenus incorporés. Toute future intégration nécessite un nouvel examen. |
| 9 | Check copyright on images | À compléter ou valider | Provenance des éléments graphiques et bibliothèques à conserver ; vérifier les droits de diffusion, les avis de licence et la disponibilité du nom/logo Selkie. Les icônes des applis suivies sont obtenues sur l’appareil et ne constituent pas un partenariat. |
| 10 | Cookies policy | Hors périmètre actuel | Aucun cookie publicitaire ou mesure d’audience trouvé. Le stockage fonctionnel et l’option du coach sont expliqués dans la politique. Un site public ou de nouveaux SDK devront être examinés séparément. |
| 11 | Check tracking | En place | Suivi d’utilisation explicitement expliqué et conditionné aux accès Android ; événements d’apps non suivies utilisés temporairement pour l’avant-plan, sans historique détaillé persistant. Pas de SDK publicitaire/analytique ni envoi automatique trouvé ; APK local sans permission Internet. |
| 12 | Form consent | En place | Pas de consentement global obligatoire à la politique. Sauvegarde des questions inconnues désactivée par défaut, choix explicite libre et révocable, base Article 6(1)(a) précisée et case accessible. Les modalités applicables aux mineurs doivent être examinées avant un public plus large. |
| 13 | Check local laws | À compléter ou valider | Obligations France/UE, statut d’activité, droits consommateurs, public mineur, portée de l’accessibilité et conditions Play doivent être confirmés pour le lancement réel. Les sources officielles actuelles n’ont pas pu être consultées en ligne dans cet environnement. |
| 14 | Clear button labels | En place | Boutons de suivi/effacement/export explicites ; badge annonce l’accès aux abonnements ; −/+ annoncent le pas de 5 min ; trois documents légaux portent leur propre titre. |
| 15 | Check for cookie consent | Hors périmètre actuel | Une bannière n’est pas nécessaire pour le seul stockage strictement fonctionnel décrit ici. Les règles françaises sur les traceurs concernent aussi les applications, pas seulement les cookies web ; un SDK non nécessaire pourrait imposer information et consentement préalable. |
| 16 | Add real business details | À compléter ou valider | Nom et contact fournis pour le test : Abdul Malik Kakar, 1kabdm@gmail.com. L’éditeur public est indécis ; aucune entreprise, adresse, immatriculation, médiation ou identité de parent n’a été inventée. Compléter les mentions applicables avant publication/vente. |
| 17 | Only collect necessary data | En place | Données locales limitées aux fonctions expliquées ; aucun accès contacts/photos/localisation ; phrases du coach facultatives et plafonnées ; export filtré, sans jetons, e-mail de compte ou phrases du coach. |
| 18 | Keyboard friendly forms | À compléter ou valider | Champ coach nommé, action Envoyer et gestion du clavier logiciel présentes. Ordre de focus, clavier physique, TalkBack et grandes polices à tester réellement. |
| 19 | Remove unsupported claims | En place | Coach présenté comme conseils préparés, offres comme démonstrations, points comme virtuels. CGU précisent limites et absence de résultat médical ou de blocage infaillible ; aucun label « certifié conforme » ou promesse d’absence d’amende. |

Avant ces corrections, plusieurs éléments ci-dessus existaient déjà (suivi expliqué, conservation facultative, suppression, contact, traduction), mais il n’y avait pas ces pages de conditions/remboursements, cet export, cette vérification de persistance ni ces améliorations de libellés et contours. Les pages sont utiles seulement si les pratiques réelles leur correspondent.

## Avant la diffusion publique

1. Choisir le véritable éditeur et responsable, avec sa capacité à contracter et les mentions applicables. Tester gratuitement une application ne suppose pas automatiquement la création d’une société. Vendre régulièrement des abonnements demande un cadre administratif et fiscal adapté ; un compte Play et un RIB ne remplacent pas ce cadre.
2. Examiner le traitement des e-mails Gmail : destinataires, contrats/rôles, conservation effective, sécurité et transferts hors EEE. Appliquer le registre et la procédure de demandes de droits, pas seulement afficher une phrase.
3. Définir le public visé. En France, le seuil de 15 ans concerne certains consentements de mineurs pour les services de la société de l’information ; en dessous, la situation et l’accord parental doivent être traités selon les règles applicables. Une permission Android ou une case générique ne suffit pas à résoudre ce sujet.
4. Vérifier les actifs, le nom/logo et les licences, puis tester l’accessibilité réelle. Les obligations légales d’accessibilité ont un périmètre précis, notamment selon le service et le statut ; elles ne se résument pas à une règle identique pour chaque APK.
5. Héberger publiquement la politique finalisée, compléter les déclarations Play et les exigences techniques actuelles, puis finaliser la vraie facturation. Avant vente : prix et taxes applicables, renouvellement, résiliation accessible, rétractation et ses exceptions exactes, garanties légales, réclamations, médiation et CGV adaptées. Ne pas vendre tant que ces parcours ne sont pas opérationnels.

Les comptes, paiements et IA distante sont préparés ou envisagés mais non activés. Les données et obligations de ces futurs services devront faire l’objet d’une nouvelle analyse ; ce bilan ne les valide pas.

## Sources officielles à contrôler

- [CNIL — applications mobiles](https://www.cnil.fr/fr/les-applications-mobiles).
- [RGPD — texte officiel UE](https://eur-lex.europa.eu/eli/reg/2016/679/oj/fra), notamment articles 5, 6, 7, 8, 12, 13, 20, 25, 28 et 32 ; régime des transferts articles 44 et suivants.
- [Code de la consommation](https://www.legifrance.gouv.fr/codes/id/LEGITEXT000006069565/), notamment information précontractuelle, rétractation, exceptions numériques, résiliation et médiation.
- [DGCCRF — délai de rétractation](https://www.economie.gouv.fr/particuliers/delai-retractation).
- [Service public — démarches professionnelles](https://entreprendre.service-public.gouv.fr/).
- [Google Play — aide officielle](https://support.google.com/googleplay/).

**Limite de vérification :** les connexions HTTPS aux sites officiels ont reçu une erreur de proxy 403 le 12 octobre 2026. Les principes généraux ci-dessus ne sont pas présentés comme une vérification des textes consolidés de 2026. Les huit domaines nécessaires ont été enregistrés dans le brouillon réseau cloud, mais sa sauvegarde n’applique pas la politique de la machine. Revoir/enregistrer les changements dans les réglages de l’environnement cloud, puis publier cet environnement pour activer le réseau — cette opération est distincte de publier Selkie sur Play. Une relecture juridique avant commercialisation reste recommandée.
