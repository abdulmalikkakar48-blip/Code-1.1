# Coach à conseils préparés — Selkie 0.9.0

Le Coach est accessible sans compte, connexion, crédit API ni abonnement. Il choisit parmi 120 conseils écrits à l’avance dans 40 sujets ; il ne contient pas ChatGPT ni un modèle génératif. Les abonnements restent en démonstration, et le dialogue IA distant reste facultatif, séparé et non configuré.

## Fonctionnement

Les minuscules, accents, apostrophes et ponctuations sont normalisés. Les mots et expressions connus sont notés, avec une tolérance limitée à certaines petites fautes (dont « tiktoc »). Le sujet le mieux noté fournit une variante de réponse ; la dernière variante de ce sujet n’est pas répétée dans le même onglet. Le moteur n’invente pas une réponse à une question nouvelle et ne comprend pas toutes les formulations.

Les sujets couvrent les applications, moments de la journée, émotions, astuces, règles de Selkie et motivation. Les conseils d’aide respectent les limites 10 / 50 / sans plafond commercial, les restrictions à zéro point, la réinitialisation à dix points et les durées et récompenses des défis.

Une réponse peut reprendre le solde exact de points ou le compteur d’une application réellement suivie. Les durées sont celles de la **période de suivi en cours**, qui ne correspond pas nécessairement à aujourd’hui. Quand le suivi est en pause ou les autorisations absentes, les durées ne sont pas utilisées. Aucun compteur ni bilan n’est envoyé à un serveur.

Une phrase inconnue reçoit une explication et jusqu’à quatre boutons de sujets connus. Le choix d’un bouton donne un conseil directement ; aucun message d’erreur technique n’est présenté comme une réponse.

Les expressions de détresse grave reconnues donnent une réponse bienveillante invitant à une aide humaine (personne de confiance, 3114 en France, 112 en danger immédiat). Elles ne reçoivent pas de conseil d’écran ni de rappel de points. Ces règles ne constituent pas une détection exhaustive ni un diagnostic.

## Conservation facultative

La conversation reste en mémoire pendant l’ouverture de l’onglet et n’est pas archivée. Dans **Coach → Mes choix de confidentialité**, l’utilisateur peut activer la conservation locale des seules phrases non comprises : option désactivée au départ, 50 phrases maximum, 800 caractères chacune, doublons retirés et plus anciennes remplacées.

Les messages identifiés comme sensibles sont exclus par précaution, sans garantie de détecter toute donnée privée. Les phrases ne sont pas transmises automatiquement à l’éditeur. Elles peuvent être consultées et effacées dans Coach ; désactiver l’option ou utiliser l’effacement global retire aussi leur stockage. Les préférences sont exclues des sauvegardes et transferts Android. Un compteur de génération empêche une ancienne écriture en attente de recréer les données après effacement.

## Contrôles avant diffusion

```sh
source /workspace/.cloud-setup/activate.sh
./gradlew --no-daemon --max-workers=2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

Les tests du moteur couvrent les formulations françaises, fautes admises, limites de mots, classement, variantes, suggestions, contexte réel et priorité de la détresse. Sur téléphone, essayer aussi le clavier, le défilement, les boutons de sujets, l’option locale, son effacement et la mise à jour depuis 0.8.1. Ne pas décrire ces essais sur Samsung comme faits tant qu’ils n’ont pas été observés.
