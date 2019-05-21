# 2018 Final assignment for the Advanced Object Oriented Programming course
## Deneral description
For this project the goal was to extend working 2D Asteroids game. The task included implementing stable multiplayer functionality on the basis of UDP for the game to end when only one player is left alive. Moreover, it suggested adding a main menu in order to organize the states of the game better, a remote table showing the highest scores, and distinguishing players using nicknames and colors.  
  
  
#### Game contains the following modes:  
* single player 
* join a game
* spectate a game
* host a game  
  

Single player mode of the game runs normally with the score increased every time tick. When selecting to spectate the game a user should add IP address and the port of the game that he wants to spectate if it exists. Then an instance of the game being played should be ruined on the Spectator without him altering the state of the model.   
Hosting a game or joining a game also require IP address and a port. When an online game ends the winner should be notified about his win, other players return back to the Main Menu. If a player has won then his name is included in a database and his name is placed on the leaderboard.    

## Technology: 
Java, MySQL
