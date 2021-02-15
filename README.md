#Projet Clavardage TALI Elies et FAURE Paul

Bienvenue ! Ceci est le repo de l'application JigglyPuff Messenger. Vous trouverez dans ce README les explications vous permettant de cloner et de builder vous même notre application.

Pour plus d'informations concernant l'utilisation et la configuration de JigglyPuff Messenger, nous vous invitons à vous rendre sur https://www.etud.insa-toulouse.fr/~tali/DownloadPageJigglyMessenger/ et de télécharger le manuel. Voilà, on espère que vous vous amuserez autant que Rondoudou en utilisant notre application ! Enjoy :)


<p style="text-align:center">
<img src="https://i.pinimg.com/originals/89/75/c3/8975c3e90d96605cba7e9ec80c4a6c3f.gif" alt="alt text" width="300" height="250">
<img src="https://www.etud.insa-toulouse.fr/~tali/imgs/chat.jpg" alt="alt text" width="300" height="250">
</p>

#Cloner JigglyPuff Messenger

Dans cette partie, nous vous expliquerons comment cloner le repo et builder le projet chez vous ! Tout d'abord, lancez les commandes suivantes : 

```bash
git clone https://git.etud.insa-toulouse.fr/tali/ProjetClavardage.git
cd ProjetClavardage
```

Ensuite, vous allez devoir compiler le projet avec gradle. Pour ce faire, lancez la commande 

```bash
./gradlew build
```
Cela va créer dans le répertoire ProjetClavardage/build/libs le fichier Clavardage-1.0-all.jar. Vous pouvez alors suivre le manuel pour configurer l'application (notamment remplir le fichier config.properties situé à côté du jar) et puis lancer l'application ! Si jamais, pour aller dans le répertoire où se trouvent les builds et lancer l'application, c'est 

```bash
cd ./build/libs
java -jar <nomdujar>
```

#Conception

Pour la conception de notre projet, c'est par ici : https://www.etud.insa-toulouse.fr/~tali/DownloadPageJigglyMessenger/ . Vous trouverez en bas de la page une archive zip à télécharger pour obtenir nos diagrammes UML en format image.

#Servlet

Vous trouverez le repo contenant le servlet à cette adresse : https://git.etud.insa-toulouse.fr/pfaure/ServeurClavardage .

#Technos

- Java 11.0.9
- JavaFX 11.0.2
- JFoenix 9.0.9
- Ikonli 11.5.0
