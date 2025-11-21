# 2.5 Reflection

**Question:**  
Why can you change the type of the returned value in promptForPlayer without changing the return type in the function signature?

**Answer:**  
The `promptForPlayer` method returns a `Player`, which is the superclass of all player types. Since the classes 
I created (like `RandyPlayer` and `CornerPlayer`) extend `Player`, I can return any of them without changing the method’s 
return type. This works because of polymorphism, Java lets us return a subclass object when the method expects the superclass.

# 2.9 Reflection

**Question:**  
Explain why the error occurred initially and why adding the abstract method signature fixes the error.  
(HINT: What is the type of the ‘whoseTurn’ variable in `TicTacToeGame.doNextTurn`?)

**Answer:**  
The error happened because the `Player` class didn’t have the method `pickNextMove`. In the `doNextTurn` method, 
the variable `whoseTurn` is a `Player`, so Java only allows calling methods that are defined in that class. 
Even though the subclasses had `pickNextMove`, Java didn’t know that. When I added the abstract method to the `Player` 
class, the error was fixed because now Java knows that all players must have that method.

# 3.6 Reflection

**Question:**  
Verify that your code works by trying a game using your computer player. Can you outsmart it? 
Can you have a game where both players are the computer player? What happens?

**Answer:**  
Yes, I tested the game using `@randy` and `@corner` as computer players. I was able to beat `@randy` because it plays 
randomly, so I could use strategy to win. I also tried a game with two computer players, and it worked fine. 
The game played automatically until the end and showed the result normally. Everything worked without errors. 
I also played as X and used `@corner` as O. It always tried to play in the corners first, and I was able to win by blocking it.

# 5 Reflection

**Question:**  
Explain in detail (using the terminology we have discussed in class) how it is possible that neither our main method 
nor our `TicTacToeGame` class need change at all when adding new `Player` types to our game. 
Your discussion must include an explanation of how the single call to `pickNextMove` in `TicTacToeGame.doNextTurn` 
works correctly no matter whose turn it is or which types the players are.

**Answer:**  

This lab helped me understand how object-oriented design makes a project easier to expand and maintain.
I was able to add new player types like `RandyPlayer` and `CornerPlayer` without changing the main game logic.
That’s because all players inherit from the abstract `Player` class and implement the `pickNextMove(Board)` method.
In the `doNextTurn()` method, the variable `whoseTurn` is typed as `Player`, so the game can call `pickNextMove()` 
without knowing which specific subclass is being used. Thanks to **polymorphism**, Java automatically calls the 
correct method at runtime, whether it’s a human or an AI.
This also showed me the power of **abstraction**, the `Player` class defines what every player must do, 
but each subclass decides how. It follows the **Open/Closed Principle**: I can add new player types without modifying 
the existing game logic. All I need to do is create a new subclass and update `promptForPlayer()` to recognize it.
This experience made me more confident in using **polymorphism, abstraction, and clean design**. It also showed me how
planning for flexibility early on makes future changes much easier, something I’ll carry into my next projects.
