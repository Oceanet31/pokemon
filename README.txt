This project has been build by Jules MIAUD and Océane TOTO.

To compile this project we used the command line: javac -d classes $(find | grep -E "./com/.*\.java")
To execute this project we used the command line: java -cp $(pwd)/classes com.esiea.pootp.PokemonApp
To write the javadoc we used the command line: javadoc -d docs $(find | grep -E "./com/.*\.java")

This program is a game inspired from the real game Pokemon.
It's a Player vs Computer game.

The goal of this game is to defeat your opponent with your own Monsters.
At the start of the game you will have to choose 6 Monsters and all of them will have 4 random attacks.
Your opponent is also a player with 6 random Monsters with 4 random attacks.

During the fight, you have 4 actions possible, you can:
    - run away (coward)
    - choose an attack (between the 4 that your active Monster has learned)
    - change your active Monster
    - use an Item of your inventory

At the begining of the game random Items will be added to your inventory.

You won a battle when the opponent's monster have no more Pv, and you lose the whole game when no one of your Monsters is alive.
During the game you can use Items on your non active monsters to heal or revive them.

Everytime you won a battle, your active Monster gain Xp to level up.
At each new level, your active Monster get all his Hp back, learn a new attack, and all his stats are increased by 10%.

You can add your own monsters attacks and items in the files dataBase/attacks.txt items.txt or pokemon.txt.
For the monster, you will also need to add in the repertories resources/icons and pokemon the files that represent your new monsters.


