Lignes de commandes
Redaction de la javadoc: javadoc -d docs $(find | grep -E "./com/.*\.java")
Compiler le code: javac -d classes $(find | grep -E "./com/.*\.java")
Executer le code: java -cp $(pwd)/classes com.esiea.pootp.PokemonApp

This program is a game inspired from the real game Pokemon.
It's a Player vs Computer game.
The goal of this game is to defeat your opponent with your own Monsters.
At the start of the game you will be given a basic Monster with 4 random attacks.
Your opponent is also a random Monster with 4 random attacks.
At first the opponent is a wild Monster and you can defeat him or try to catch him with a Pokeball.

During the fight, you have 4 actions possible, you can:
    - run away (coward)
    - choose an attack (between the 4 that your active Monster has learned)
    - change your active Monster
    - use an Item of your inventory

At the begining of the game 5 Pokeballs and a random Item will be added to your inventory.
The less your active Monster has Pv the bigger chance you have to catch the wild Monster.

You won a battle when the wild monster have no more Pv, and you lose the whole game when no one of your Monster is alive.
(Use Items on Monster of the Team)

Everytime you won a battle, your active Monster gain Xp to level up.
At each new level, your active Monster get all his Hp back, learn a new attack, and all his stats are increased by 10%.
