# Selkie 0.12.1 — calendrier, bilans et outils locaux

## Où les trouver

L’onglet Quotidien propose quatre vues : Jour, avec les compteurs existants ; Calendrier ; Bilan ; et Outils, qui regroupe les créneaux, les programmes guidés et le widget. Dans Coach, Mes favoris permet de retrouver les conseils enregistrés ; un bouton sous les réponses reconnues permet de les ajouter. Les autres onglets et le menu supérieur sont conservés.

| Fonction | Gratuit | Premium | Ultra |
| --- | --- | --- | --- |
| Historique du calendrier sur 90 jours | Oui | Oui | Oui |
| Tâches sur dates passées ou futures | Oui | Oui | Oui |
| Résumé hebdomadaire observé | Oui | Oui | Oui |
| Favoris du Coach (50 au maximum) | Oui | Oui | Oui |
| Détail hebdomadaire par application et comparaison | — | Oui | Oui |
| Créneaux de limites (20 au maximum) | — | Oui | Oui |
| Widget avec points et défi | Aperçu des offres | Oui | Oui |
| Programmes Sommeil (7 jours), Études (14 jours), Réseaux sociaux (30 jours) | Aperçu | Aperçu | Oui |

Les prix prévus restent de 5 €/mois ou 60 €/an pour Premium, et de 10 €/mois ou 120 €/an pour Ultra, sans réduction annuelle. Dans l’APK DEBUG, ces offres sont explicitement des démonstrations gratuites : aucun paiement ni compte de restauration n’est activé.

## Historique et calendrier

L’historique commence avec les nouvelles observations : aucune durée passée n’est inventée. Lorsque le suivi est actif, le service enregistre les différences entre deux relevés consécutifs des applications sélectionnées, espacés de 30 secondes au maximum. Le premier relevé, un changement de jour ou de fuseau horaire, une baisse du compteur ou une longue interruption établissent seulement une nouvelle base. Les pauses arrêtent les relevés. Les validations des défis standard, des défis personnels et des programmes guidés sont enregistrées comme des événements distincts.

Les journées sans observation restent inconnues. Un intervalle réellement observé avec une utilisation nulle, y compris sans application sélectionnée, est distingué d’une absence de mesure. Les bilans indiquent les totaux, le nombre de jours observés et la couverture ; les comparaisons ne prouvent pas une baisse d’usage sur des journées entières. Le compteur courant et cet historique ont des modes de calcul distincts.

Seuls les 90 derniers jours locaux glissants sont affichés et exportés. Le nettoyage des anciennes entrées dans le stockage reprend à la prochaine utilisation, lors d’un changement de jour traité ou d’une observation, si l’écriture réussit. Aucun délai exact de suppression physique n’est garanti lorsque l’application est fermée. L’historique applique des limites techniques de 500 identifiants d’applications et de 64 observations de validation par jour ; ces limites ne constituent pas un plafond commercial pour le suivi avec Ultra. Le dernier échantillon technique sert uniquement au calcul des différences. Il est exclu de l’export et supprimé ou remplacé lors d’une pause, du prochain relevé ou du nettoyage.

Le calendrier indique les dates et leurs états. Les zones tactiles des dates restent larges, et la grille peut défiler sur un petit écran. Des symboles accompagnent les couleurs. Selkie ne lit pas le calendrier Android du téléphone.

## Créneaux

Pour créer un créneau, choisir une application déjà suivie, les jours et les heures, puis une limite. L’heure de début est incluse et l’heure de fin est exclue. Les créneaux qui passent la nuit, y compris du dimanche au lundi, sont pris en charge. Les règles actives pour une même application ne peuvent pas se chevaucher. Créer, modifier, activer, désactiver ou supprimer un créneau exige Premium ou Ultra et un solde de points positif.

Le créneau change la limite du compteur en cours : il ne remet pas sa durée à zéro et ne modifie pas la limite habituelle enregistrée. Il ne lève pas non plus un verrou de 24 heures déjà déclenché ; ce verrou continue après la fin du créneau ou un retour à l’offre Gratuit. Une limite plus basse peut donc déclencher le verrou immédiatement. Hors créneau, la limite habituelle s’applique. Après un retour à l’offre Gratuit, les règles sont conservées mais restent inactives. Le bouton Modifier dans Accueil préremplit la limite habituelle réelle.

## Favoris et programmes

Les favoris contiennent la référence d’un conseil préparé, avec sa langue et son offre minimale, ou un conseil reconnu rédigé à l’avance. La question libre saisie, les phrases non comprises et les réponses sensibles ne sont pas ajoutées aux favoris. Un contenu préparé payant reste verrouillé après le passage à une offre inférieure. La langue de sauvegarde est conservée et indiquée. Le nombre de favoris est limité à 50 ; au-delà, les plus anciens sont remplacés.

Les trois programmes contiennent 51 activités concrètes bilingues, affichées dans l’ordre, chacune sur sa ligne. La validation de l’activité du jour est manuelle, avec au plus une validation guidée par date locale pour l’ensemble des programmes. Une journée sautée ne remet pas la progression à zéro. Réinitialiser un programme ou en changer ne permet pas une deuxième validation le même jour. Ces programmes n’accordent pas de points supplémentaires, ne demandent pas de preuve photo et ne font aucune promesse médicale. Ultra est nécessaire pour commencer ou valider un programme ; le retour à une offre inférieure conserve la progression. Le marqueur quotidien reste enregistré jusqu’à l’effacement complet, même après l’abandon des programmes.

## Widget et données

Dans Outils, Ajouter le widget utilise la demande native du lanceur lorsque celui-ci la prend en charge. Sinon, faire un appui long sur une zone vide de l’écran d’accueil, puis choisir Widgets → Selkie. Avec l’offre Gratuit, le widget affiche une invitation à consulter les offres ; avec Premium ou Ultra, il affiche les points et un défi proposé. Ses boutons ouvrent Selkie : ils ne valident rien et ne dépensent rien. Les mises à jour ordinaires sont limitées à une par minute lorsque l’application est active. Android contrôle les mises à jour périodiques, qui peuvent être différées. Un changement d’offre, de langue ou l’effacement des données force une mise à jour. Les informations affichées sont visibles par toute personne qui peut voir cet écran d’accueil.

Toutes ces données restent dans le stockage privé local « maree », sans permission Internet ni nouvel accès aux contacts, aux photos ou au calendrier du téléphone. L’export JSON utilise le schéma 3 et inclut les données structurées validées de ces fonctions. Il exclut les messages libres, les données d’authentification et le dernier échantillon brut. L’effacement global couvre tous les nouveaux champs et met le widget à jour ; un échec d’écriture ou de mise à jour n’est pas annoncé comme un succès. Les copies exportées doivent être supprimées séparément. Voir les politiques de confidentialité en français et en anglais, ainsi que le registre.

## Planifier une date — 0.12.1

Calendrier permet maintenant de parcourir les mois et de choisir une date passée ou future avec « Choisir une date » (1900–2200). Les tâches personnelles sont gratuites : ajout, modification, case terminé et suppression avec confirmation. Le symbole ○ indique les journées avec des tâches. Jusqu’à 500 tâches au total ; les listes longues s’affichent progressivement. Ces tâches restent jusqu’à leur suppression, l’effacement global ou la désinstallation, indépendamment des 90 jours d’historique. Elles sont incluses dans l’export, sans points ni notification automatique.

Le mode Clair remplace les 96 formes scintillantes par six lignes ondulantes, fines, monochromes et discrètes. Le mode Nuit conserve ses vagues de points. Le respect des animations système et du cycle de vie reste actif.
