To start the game

Go to the Game class scroll dow to the last function the Public static void man and lauch it. 

If you are getting errors like \

" Cannot invoke "Woralcoholics.game.GameManager.render(java.awt.Graphics)" because "Woralcoholics.game.Game.handler" is null" \
or something with cannot invoke just relaunch the game\
this is a known bug we are trying to solve at the Moment 


Moving is binded to the key 

W = up \
A = left \
S = down \
D = rigth 

Curently the Game is using way to much ram, so please add a run configutation \
This is next to your green hammer

Select Game.java as class as main class
Then press Alt + V for vm Parameters and add -XX:+UseZGC 

This is just a garbage collector for helping out a bit with ram usage



Shooting is handeld just by having the mouse in the game window and pressing left Mose button
 
