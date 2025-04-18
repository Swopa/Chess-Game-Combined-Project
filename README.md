This is a Java implementation of a chess game that I refactored from the original project (https://github.com/jlundstedt/chess-java). The game follows standard chess rules and includes some features.

## How this project is Organized

I organized the code following the MVC pattern:

The model contains all the game logic and chess rules:
- Board.java - Represents the chess board and piece positions
- Piece.java and its subclasses (Bishop, King, Knight, etc.) - Define how pieces move
- CheckmateDetector.java - Logic for detecting check and checkmate situations
- Clock.java - Handles the chess clock functionality

The view handles the user interface:
- ChessBoardPanel.java - Displays the chess board and handles piece dragging
- GameWindow.java - The main game window with controls and information
- StartMenu.java - Initial game setup screen

The controller connects the model and view:
- GameController.java - Manages game flow and user interactions

## How to Run the Game

1. Clone this repository
2. Open the project in your Java IDE (Eclipse, IntelliJ, etc...)
3. Run the Main.java file
4. The start menu will appear where you can:
   - Enter white player name
   - Enter black player name
   - Set the time control
   - Start a new game

![image](https://github.com/user-attachments/assets/83d41cf5-f7a7-4321-a80c-624cbef0a49d)
![image](https://github.com/user-attachments/assets/7a7bb235-e668-4c6d-856e-2f8580735f46)


## Testing

I created unit tests to verify the chess rules and game mechanics:
To run the tests, you'll need JUnit. The tests can be found in the test package.
