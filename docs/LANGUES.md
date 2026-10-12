# Français et anglais — Selkie 0.10.3

L’application propose les deux langues. Le choix **Français / English** est accessible dès l’accueil initial et dans **le menu ☰ en haut → Langue / Language**. Il est enregistré uniquement sur le téléphone. Sans choix enregistré, Selkie démarre en anglais si le téléphone est en anglais, en français sinon. Changer la langue n’efface pas les points, limites, validations ou défis personnels et n’active aucun accès Android, compte ou paiement.

Les écrans, dialogues, messages de validation, offres, unités, dates, politique de confidentialité, notification du suivi et écran de pause sont traduits. Les vingt défis proposés gardent les mêmes indices et règles dans les deux langues. Les titres et étapes écrits par l’utilisateur, les noms d’applications fournis par Android et les messages déjà enregistrés ne sont pas traduits automatiquement. Les pages externes de Google Play et les réglages Android utilisent leur propre choix de langue.

Chaque langue possède la bibliothèque de **1 152 questions et réponses préparées**, en plus des **67 sujets et 125 réponses** précédents : 29 sujets de conseils avec trois variantes et 38 questions d’aide complètes. Le moteur peut reconnaître les mots connus des deux langues ; la réponse suit la langue choisie dans Selkie. Les réponses inconnues, les suggestions, les mesures réelles et l’aide humaine en cas de détresse reconnue suivent aussi cette langue. Le changement de langue recrée la question et la réponse en mémoire ainsi que les menus ; les phrases non comprises enregistrées sur option restent dans leur langue d’origine jusqu’à leur effacement. Aucun traducteur en ligne ni modèle IA n’est appelé.

Les notifications futures suivent la nouvelle langue ; la notification persistante du suivi est actualisée sans redémarrer le service. Les avertissements déjà affichés peuvent conserver leur texte d’origine. Le suivi doit être actif pour son actualisation. L’effacement global retire aussi le choix de langue et revient au choix initial déterminé par le téléphone.

La politique anglaise est aussi disponible dans [PRIVACY_POLICY_EN.md](PRIVACY_POLICY_EN.md). Le dialogue IA distant reste non configuré et son serveur préparé vise encore des réponses françaises ; cette version ajoute l’anglais au coach local, sans annoncer un service distant bilingue actif.

## Entretien et vérification

Les textes des écrans utilisent `tr(fr, en)` et les catalogues du Coach utilisent les mêmes IDs dans les deux langues. `AppLanguage` initialise le choix local dans `SelkieApplication`, avant les activités et services. Les propriétés affichées restent dynamiques ; les menus mémorisés du Coach sont recréés pour la langue choisie. Les formats de stockage, clés, dates internes, prix et règles ne dépendent pas de la traduction.

Les tests vérifient le choix initial et enregistré, le catalogue anglais, les questions reconnues, les réponses inconnues, les points et compteurs réels, les messages de détresse et le filtre local de phrases privées dans les deux langues. La compilation Android et Lint ne remplacent pas les essais sur téléphone : bascule FR/EN à l’accueil et dans le menu ☰ → Langue / Language, relance, notification du suivi, clavier, menus d’aide, défis et politique complète. Le rendu et la persistance sur Samsung ne doivent être annoncés comme observés qu’après cet essai.

La version 0.10.2 modifie uniquement le fond géométrique en points ; les langues, les textes et les chemins du menu restent conservés. Compilation et 97 tests unitaires passent ; les essais sur téléphone restent nécessaires.

La version 0.10.3 remplace ce fond par une surface d’eau pointillée, plus visible en clair et en nuit. Les langues, les textes et les chemins du menu restent identiques. Compilation et 97 tests unitaires passent. Le fond et son mouvement ont été observés dans les deux thèmes sur émulateur ; les essais sur téléphone restent nécessaires.
