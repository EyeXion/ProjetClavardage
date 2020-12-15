#Projet Clavardage TALI Elies et FAURE Paul

##Conception 

Faite sur Modelio, il suffit d'importer le fichier zip contenu dans Conception/ dans Modelio.
Notre conception est pour le moment pas très bonne car nous avions mal compris le CDC. Nous allons donc la changer au fur et à mesure des TP

##Notes pour nous même

###Fait (plus ou moins)

- Classe Messages.Message (dont les message spécifiques vont hériter) ok.
- Classe Messages.MessagePseudo de type 1 (demande validation)
- Classes reseau.UDPInput et reseau.UDPOutput ok
- Tests avec 3 machines sur localhost avec 3 ports différents ok
- Quand on reçoit un msgPseudo, on répond avec un messagePseudo d'un autre type (type 2 ou 3)
- Quand on recoit un Messages.MessagePseudo de n'importe quel type, on met à jour sa table des utilisateurs connectés si le pseudo n'est pas utilisé
- Si pseudo déjà utilisé, on envoie un messagePseudo (type 3)
- MVC ok.
- Migration projet Gradle
- UI commecencée avec mainWindow, petite animation menu et PseudoWindow
- Ajout table des users connetés (pas hyper cool, quand on change de pseudo 2 fois on se duplique dans la ListView)
- Message de type 4 (confirmation envoi pseudo)
- Splash screen
- Page d'acceuil connection/inscription
- Client TCP (reprendre les TD).
- Fix le bug quand on ouvre 2 fenêtres de changement de pseudo (mettre un flag pour pouvoir en ouvrir qu'une seule)
- Design fenêtre de clavardage (draft)
- Envoyer les messages dans la BDD
- Horadater les messages
- Serveur distant utilisateurs extérieurs (demander au prof)
- Handler fermetures des fenêtres
- Bouton déconnexion
- Concevoir archi BDD pour les messages

###A faire

- Envoyer à intervalle régulier des messages qui disent qu'on est bien connecté
- Tester le broadcast
- Effacer un utilisateur
- Mots de passe
- Design UI

###A faire si tenemos el tiempo y no cagna

- Vérifier la liste des users connectés quand on reçoit un msg de type 1 (au lieu de juste comparer avec son propre pseudo)
- Envoi fichiers texte/images
- Envoi audio ?


###Technos

- Java 11.0.9
- JavaFX 11.0.2
- JFoenix 9.0.9
- Ikonli 11.5.0
