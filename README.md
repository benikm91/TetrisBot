# TetrisBot
Tetris bot with Q-Learning

Goal of this project is to develope a simple Tetris bot.
As Tetris application we used a existing project: TODO LINK
The learning is done with the Q-learning technique.

* State *
A Tetris state has following parts:
1) field state : The depth of the current player field relative to the lowest part. E.g. (2, 1, 4, 5, 1, 3, 5, 1, 1, 4)
2) The current piece (Tetris block).
3) The final position of the current piece (before letting it fall).
4) The final rotation of the current piece (before letting it fall).

* Problem with State space *
With the above defined state we get a total state space of around:
25 ^ 10 (field states) * 7 (current piece) * 10 (final position) * 4 (final rotation) ~= 2.6e+16
Which is way to high for our computers to handle! - So we have to replace our missing computer power with brain power :).
We use two little tricks:
1) We reduce the max depth we are looking at from 25 down to 7. This will result in 7 ^ 10 possible field states.
2) We chop the play field into pieces with a width of 4. This will result in 7 ^ 4 possible field states.

The main idea from 2) is that we always look only at parts of the Tetris field. 
While playing the bot chops his current play field into pieces of width 4 and checks with his knowledge (his knowledge only has pieces of width 4 as well).
From the found actions the bot picks the best he knows.

With this tricks and a possible field states of 7 ^ 4 we get a total state space of around:
7 ^ 4 * 7 (current piece) * 4 (final position) * 4 (final rotation) = 268912

which is a 1/ 99299699750 of the original state space. 

* Code *

TODO 

