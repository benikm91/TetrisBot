# TetrisBot with Q-Learning
<p>
Goal of this project is to develope a simple Tetris bot.<br>
As Tetris application we used a existing project: TODO LINK<br>
The learning is done with the Q-learning technique.
</p>
<h2>State</h2>
<p>
A Tetris state has following parts:
<ol>
<li>field state : The depth of the current player field relative to the lowest part. E.g. (2, 1, 4, 5, 1, 3, 5, 1, 1, 4)
<li>The current piece (Tetris block).
<li>The final position of the current piece (before letting it fall).
<li>The final rotation of the current piece (before letting it fall).
</ol>
</p>
<h2>Problem with State space</h2>
<p>
With the above defined state we get a total state space of around:<br>
25 ^ 10 (field states) * 7 (current piece) * 10 (final position) * 4 (final rotation) ~= 2.6e+16<br>
Which is way to high for our computers to handle! - So we have to replace our missing computer power with brain power :).<br>
We use two little tricks:
<ol>
<li>We reduce the max depth we are looking at from 25 down to 7. This will result in 7 ^ 10 possible field states.
<li>We chop the play field into pieces with a width of 4. This will result in 7 ^ 4 possible field states.
</ol>
The main idea from 2) is that we always look only at parts of the Tetris field.<br>
While playing the bot chops his current play field into pieces of width 4 and checks with his knowledge (his knowledge only has pieces of width 4 as well).<br>
From the found actions the bot picks the best he knows.

With this tricks and a possible field states of 7 ^ 4 we get a total state space of around:<br>
7 ^ 4 * 7 (current piece) * 4 (final position) * 4 (final rotation) = 268912<br>
<br>
which is a 1/ 99299699750 of the original state space. 
</p>
<h2>Code</h2>
<p>
TODO
</p>
