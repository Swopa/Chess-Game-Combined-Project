# Java Chess Game

This project is a refactored implementation of a chess game originally created by jlundstedt (https://github.com/jlundstedt/chess-java). The refactoring focused on improving code structure, enhancing maintainability, and ensuring proper separation of concerns using the Model-View-Controller (MVC) architectural pattern.

## Refactoring Changes

My refactoring work included:

- Reorganizing the codebase to follow MVC architecture
- Extracting game logic into properly encapsulated model classes
- Improving the move validation system
- Creating a robust checkmate detection algorithm
- Implementing proper timer functionality
- Adding unit tests for chess rules verification
- Fixing various logical bugs in the original implementation

## Project Structure

### Model (Game Logic)
- `Board.java` - Manages the 8x8 chess board and tracks piece positions
- `Piece.java` - Abstract base class for all chess pieces
   - `Bishop.java`, `King.java`, `Knight.java`, `Pawn.java`, `Queen.java`, `Rook.java` - Concrete piece implementations with specific movement rules
- `Square.java` - Represents a single square on the chess board
- `CheckmateDetector.java` - Analyzes the board state to detect check and checkmate conditions
- `Clock.java` - Handles chess clock timing functionality

### View (User Interface)
- `ChessBoardPanel.java` - Renders the chess board and handles piece dragging interactions
- `GameWindow.java` - Main game window with player information, clocks, and control buttons
- `StartMenu.java` - Initial configuration screen for player names and time settings

### Controller
- `GameController.java` - Coordinates between the model and view, processes user inputs, and manages game state

## Features

- Drag and drop piece movement
- Legal move validation
- Check and checkmate detection
- Castling (kingside and queenside)
- Pawn promotion
- Configurable chess clock with time controls
- Two-player gameplay on the same device

## Running the Application

1. Clone this repository to your local machine
2. Open the project in your Java IDE (Eclipse, IntelliJ IDEA, NetBeans, etc.)
3. Ensure you have Java Development Kit (JDK) installed (Java 8 or higher recommended)
4. Compile and run the `Main.java` file
5. In the start menu:
   - Enter names for both players
   - Set your desired time control
   - Click "Start Game" to begin playing

## Game Interface

The chess game features a clean interface with:

![Start Menu](https://github.com/user-attachments/assets/83d41cf5-f7a7-4321-a80c-624cbef0a49d)
![Game Board](https://github.com/user-attachments/assets/7a7bb235-e668-4c6d-856e-2f8580735f46)

## Testing

The project includes JUnit tests that verify the correctness of chess rules implementation:

- `BishopTest.java` - Tests diagonal movement patterns
- `BoardTest.java` - Verifies board initialization and state management
- `CheckmateDetectorTest.java` - Confirms check and checkmate situations
- `ClockTest.java` - Tests timer functionality
- `KingTest.java` - Validates king movement including castling rules
- `KnightTest.java` - Tests L-shaped knight movements
- `PawnTest.java` - Verifies pawn movement including first-move double step
- `QueenTest.java` - Tests combined rook and bishop movement patterns
- `RookTest.java` - Checks horizontal and vertical movement rules

To run tests, execute them through your IDE's test runner or via JUnit directly.

## Building

The project uses standard Java SE without external dependencies beyond JUnit for testing. Any Java IDE should be able to build and run the project without additional configuration.