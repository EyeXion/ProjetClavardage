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
- MVC ok (avec controlleur et vue qui sont simulés et qui sont pas hyper utiles bvu qu'on a pas de vue réelle)

###A faire

- Quand on reçoit un messagPseudo de type 1, on vérifie toute la table des utilisateurs connectés.
- Envoyer à intervalle régulier des messages qui disent qu'on est bien connecté
- Commencer une petite interface graphique pour demander un pseudo et afficher les utilisateurs connectés
