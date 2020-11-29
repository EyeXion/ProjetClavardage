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

###A faire

- Quand on reçoit un messagPseudo de type 1, on vérifie toute la table des utilisateurs connectés. (maybe?)
- Envoyer à intervalle régulier des messages qui disent qu'on est bien connecté
- MEssage de type 4 (confirmation envoi pseudo)
- Page d'acceuil pour premier pseudo + splash screen.
- Client TCP (reprendre les TD).
- Design fenêtre de clavardage
- Rendre l'UI jolie.

###Technos

- Java 11.0.9
- JavaFX 11.0.2
- JFoenix 9.0.9
- Ikonli 11.5.0
