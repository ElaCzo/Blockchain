### Lancement de la blockchain avec serveur

- Rentrer la commande suivante (premier paramètre : adresse, deuxième paramètre, port). Les ports **1988** et **1990** sont réservés (cf. ci-dessous)
     `make && ./launch_client.sh 127.0.0.1 12348`
- Pour terminer l'exécution, rentrer la commande suivante
     `./killalljavabuteclipse`

Attention, tous les processus doivent être **terminés** (par la commande 2) pour relancer une exécution (les serveurs ajoutés se connectent à une adresse fixe par commodité). Pour modifier ces adresses fixes, modifier l'attribut **PORT** dans les classes **DicoServer** et **ScoreBlockchainServer**.

