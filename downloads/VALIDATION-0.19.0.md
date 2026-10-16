# Selkie 0.19.0 — plafond 100 et dé Points

Source exacte : `12dfa7fd039d161d7bbb5d1f07a3aea901d1f973`. VersionCode32, Android10+ (minSdk29), cible API34. APK : 27857160 octets, SHA-256 `ea09fbebbe8f13d2ba1ece56da2c29a507b49751e4fe608de44566cc40685722`. Certificat de développement conservé : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. ZIP source : 962354 octets, SHA-256 `a24c767771e624f08ca217c1046ace8f67f6fa5af8f52e796d9d1495823f4760`. Ses 192 entrées, hors téléchargements, sont comparées aux blobs du commit ; CRC validé.

## Règles et interface

Le solde démarre à100 et ne peut dépasser100. Une ancienne valeur supérieure revient à100 ; les soldes inférieurs, les défis et les autres progrès sont conservés. Les bonus24h, défis standard et récompenses de0,25point sont plafonnés, sans crédit différé au-delà du maximum. Les pénalités restent actives et les mêmes règles valent dans les trois offres. Accueil, widget, aide et coach indiquent ce plafond.

Quotidien → Dé ajoute un quatrième mode gratuit, **Points**, distinct d’Activité, Appli et Semaine. Avant le bouton, l’écran annonce face1=−1point jusqu’à face6=−6points. Le retrait est immédiat et limité au reliquat, sans solde négatif. Aucun nouveau lancer à zéro, aucun gain, achat ou argent. Les trois modes classiques gardent leurs tâches volontaires sans perte de points et leurs limites habituelles. Aucun changement du fond, des couleurs, du calendrier, des douze coloriages, des lectures ou du catalogue préparé du coach.

La transaction locale confirme ensemble le retrait et le dernier reçu. La tentative garde son UUID dans l’activité et reprend ce même reçu si l’écran est interrompu. Une erreur de persistance peut déjà avoir changé la mémoire Android : Vérifier ce lancer réutilise l’opération au lieu de relancer le dé. Le solde courant n’est jamais remplacé par le solde historique du reçu. À zéro sans reçu correspondant, la tentative est libérée pour laisser accessibles les modes classiques. L’effacement global invalide les anciennes écritures. Seul le dernier reçu est conservé ; les anciens UUID après un nouveau lancer ne constituent pas un historique dédupliqué.

Export JSON schéma5 : `storedData.points.maximumValue=100`, `storedData.honorDice` contient le dernier reçu filtré ou null. Effacement global, guide et politiques FR/EN alignés. Aucune nouvelle dépendance ou permission ; sept permissions identiques à0.18.0, sans Internet. Prix prévus Premium5€/mois ou60€/an, Ultra10€/mois ou120€/an, sans réduction et sans achat réel dans cet APK debug.

## Contrôles automatisés

Compilation debug, **259 tests JVM réussis**, zéro échec, erreur ou test ignoré. Lint : zéro erreur, cinq avertissements et cinq informations. Les tests couvrent les deux formats de migration160→100, les soldes inférieurs et la conservation des progrès, chaque gain au plafond et sous le plafond, les six faces dans toutes les offres, le reliquat fractionnaire, zéro, le bonus avant la perte, les échecs de commit avant/après mutation mémoire, la recréation du Store et les nouvelles tentatives sans double retrait, l’export filtré et l’effacement. Les tests de gains existants commencent sous le plafond pour vérifier une vraie récompense. Le widget et l’aide FR/EN sont également contrôlés. `apksigner` valide la signature et `aapt` confirme l’identité, la version et les permissions.

## Essai natif Android14

APK final installé en mise à jour sur emulator-5554,480×800, même certificat. Le SHA-256 du base.apk installé est identique au livrable. L’ancienne préférence contenait640quarts, soit160points : l’accueil affiche100/100 et le stockage persiste400quarts. La première lecture disque avant la fin du démarrage voyait encore l’ancienne valeur ; la vérification après rendu confirme la migration persistée.

En français et mode Nuit, les quatre modes sont lisibles sur deux rangées. Ouvrir Points ne retire rien. Les règles et le bouton Lancer et retirer1à6points sont visibles après défilement. Un appui produit la face1 : retrait de1point, solde99/100, reçu et résultat cohérents. Après fermeture/réouverture, l’accueil affiche99/100 et le même reçu reste stocké sans nouveau retrait. Captures vérifiées : accueil100, avertissement/action, résultat et accueil99 ; aucune capture de splash n’est distribuée.

Après l’essai, seul ce retrait de test a été rendu et son reçu de test supprimé depuis les préférences courantes après arrêt, en conservant le plafond100. Aucun ancien fichier de progression n’a été restauré. Toutes les autres préférences sont identiques ; applications, tâches, défis, coloriages et offre sont conservés. Thème Nuit inchangé octet pour octet, trois échelles d’animation à0 et délai écran2147483647 préservés. App relancée. Les sauvegardes de préférences et UUID restent hors dépôt.

## Limites

Le démarrage de l’émulateur logiciel est lent ; les captures attendent un vrai écran rendu. Les animations système étant à zéro, cet essai ne prouve pas la fluidité du dé. Rotation, erreur disque réelle, anglais sur ce nouvel écran, TalkBack, grande police et essai physique SamsungA16 ne sont pas validés nativement ; les scénarios métier correspondants explicitement cités plus haut sont couverts par les tests JVM. APK debug API34, sans publication Play ni facturation réelle. Pages juridiques statiques à héberger ; aucune certification juridique. Les liens GitHub nécessitent l’accès au dépôt.
