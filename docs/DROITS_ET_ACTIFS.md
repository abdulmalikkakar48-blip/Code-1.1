# Selkie — actifs, nom et droits à vérifier

Inventaire de travail pour la publication. Il ne constitue pas une attestation d’originalité, de disponibilité de marque ou de licence complète.

- Le phoque et le nom Selkie ont été retenus avec l’éditeur pendant le développement. Conserver les fichiers de création et l’historique. L’assistance d’un outil génératif ne prouve pas à elle seule l’absence de droits antérieurs ou la disponibilité de la marque ; vérifier le nom, le logo et les droits de diffusion avant publication.
- Les icônes des applications choisies sont obtenues auprès d’Android sur le téléphone pour identifier les applications. Elles ne sont pas présentées comme des partenaires de Selkie ni revendiquées comme créations de Selkie. Examiner séparément tout logo utilisé dans des visuels marketing ou captures publiques.
- La typographie utilise les familles fournies par Android/Compose ; inventorier tout fichier de police ajouté ultérieurement et sa licence.
- L’application utilise AndroidX/Compose, Kotlin et leurs dépendances. Conserver les avis et licences applicables à la distribution de l’APK et contrôler l’ensemble des dépendances effectivement embarquées. Ne pas déduire qu’une bibliothèque gratuite est dépourvue d’obligations.
- Ne pas utiliser de photos, vidéos, témoignages ou avis téléchargés sans droits ou présentés de façon trompeuse. Aucun faux avis ou témoignage n’a été trouvé dans les écrans inspectés du prototype.

Avant la fiche Play : confirmer les droits des captures, l’origine des fichiers, les obligations des dépendances et la disponibilité du nom/logo. Le point correspondant de la liste reste « à compléter ou valider ».

## Inventaire observé dans la version 0.10.4

Le seul fichier image inclus dans les ressources examinées est `app/src/main/res/drawable-nodpi/brand_mark.png` (SHA-256 `45c62cf5b7c406e833543b4c7e61d12cff33dac465ea44537ffc72ad121f1fad`). Les icônes adaptatives et de navigation sont des ressources vectorielles. Aucun fichier de police TTF/OTF personnalisé n’est embarqué dans ces ressources. Cet inventaire identifie les actifs présents ; il ne prouve ni leur originalité ni la disponibilité du nom Selkie.

## Complément 0.11.0

57 coordonnées distinctes du classpath Android debug ont été inventoriées ; leurs POM déclarent Apache-2.0, directement ou via leur parent pour Guava ListenableFuture. L’inventaire et les empreintes des POM sont dans DEPENDANCES-0.11.0.json. Le texte Apache-2.0 est embarqué et consultable dans Confidentialité et données → Licences des bibliothèques. Source du texte : dépôt JetBrains/kotlin, tag v1.9.24, license/LICENSE.txt (HTTPS vérifié). Ce relevé du classpath debug n’est pas une liste certifiée des classes de l’APK release ; renouveler l’inventaire de distribution et vérifier les notices supplémentaires avant Play. Les droits du phoque et la disponibilité de la marque restent à confirmer.
