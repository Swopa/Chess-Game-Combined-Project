import java.io.IOException;
import java.util.List;
/*
public class Main {
    public static void main(String[] args) {
        String pgn = "[Event \"Wch U10\"]\n" +
                "[Site \"Duisburg\"]\n" +
                "[Date \"1992.??.??\"]\n" +
                "[Round \"5\"]\n" +
                "[White \"Bacrot, Etienne\"]\n" +
                "[Black \"Vallejo Pons, Francisco\"]\n" +
                "[Result \"1/2-1/2\"]\n" +
                "[WhiteElo \"\"]\n" +
                "[BlackElo \"\"]\n" +
                "[ECO \"A89\"]\n" +
                "\n" +
                "1.d4 f5 2.Nf3 Nf6 3.g3 g6 4.Bg2 Bg7 5.c4 O-O 6.O-O d6 7.Nc3 Nc6 8.d5 Ne5\n" +
                "9.Nxe5 dxe5 10.e4 f4 11.b3 g5 12.Qe2 a6 13.Bd2 Qe8 14.Rac1 Qg6 15.c5 Qh6\n" +
                "16.f3 fxg3 17.hxg3 Nh5 18.Qf2 Qg6 19.Be3 g4 20.f4 exf4 21.gxf4 g3 22.Qd2 Nf6\n" +
                "23.e5 Ng4 24.Rf3 Nh2 25.d6 Nxf3+ 26.Bxf3 cxd6 27.Nd5 Rf7 28.cxd6 Bg4 29.Bxg4 Qxg4\n" +
                "30.Nxe7+ Kh8 31.d7 Rff8 32.Rc8 Raxc8 33.dxc8=Q Rxc8 34.Nxc8 Qxc8 35.Kg2 h5\n" +
                "36.Kxg3 Qg4+ 37.Kh2 Qh4+ 38.Kg1 Qg3+ 39.Kf1 Qf3+ 40.Qf2 Qh1+ 41.Ke2 Qb1 42.Kf3 Qf5\n" +
                "43.Ke2 Kh7 44.Qf3 b5 45.Bd2 h4 46.Be1 h3 47.Kf2 Bh6 48.Qb7+ Kg6 49.Qc6+ Kh5\n" +
                "50.Qf3+ Kg6 51.Qc6+ Kh7 52.Qb7+ Bg7 53.Qf3 h2 54.Kg2  1/2-1/2\n";

        List<String> moves = PGNParser.parseMoves(pgn);

        System.out.println("Game Start:");
        printBoard(new Board());

        Board board = new Board();
        MoveInterpreter interpreter = new MoveInterpreter(board);

        for (String move : moves) {
            System.out.println("\nMove: " + move);


            try {
                interpreter.interpretMove(move);
            } catch (IllegalMoveException e){
                System.out.println("Game Over: " + e.getMessage());
                break;
            }


            // Print the board after each move
            printBoard(board);
        }

        System.out.println("Game Over!");
    }

    public static void printBoard(Board board) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.board[row][col];
                if (piece == null) {
                    System.out.print(" . ");
                } else {
                    char symbol;
                    if (piece instanceof Knight) {
                        symbol = 'N';
                    } else {
                        symbol = piece.getClass().getSimpleName().charAt(0);
                    }
                    System.out.print(symbol + (piece.isWhite ? "W" : "B") + " ");
                }
            }
            System.out.println();
        }
    }
}
*/


public class Main {
    public static void main(String[] args) {
        try {
            String filePath = "src\\Tbilisi2015.pgn";
            List<String> games = PGNFileReader.readGames(filePath);

            int gameCount = 1;
            for (String game : games) {
                System.out.println("=== Game " + gameCount + " Start ===");
                List<String> moves = PGNParser.parseMoves(game);

                Board board = new Board();
                MoveInterpreter interpreter = new MoveInterpreter(board);

                System.out.println("Initial Position:");
                printBoard(board);

                for (String move : moves) {
                    System.out.println("\nMove: " + move);
                    try {
                        interpreter.interpretMove(move);
                    } catch (IllegalMoveException e) {
                        System.out.println("Illegal move detected: " + e.getMessage());
                        break;
                    }
                    printBoard(board);
                }

                System.out.println("=== Game " + gameCount + " End ===\n");
                gameCount++;
            }

        } catch (IOException e) {
            System.err.println("Error reading PGN file: " + e.getMessage());
        }
    }

    public static void printBoard(Board board) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.board[row][col];
                if (piece == null) {
                    System.out.print(" . ");
                } else {
                    char symbol = piece instanceof Knight ? 'N' : piece.getClass().getSimpleName().charAt(0);
                    System.out.print(symbol + (piece.isWhite ? "W" : "B") + " ");
                }
            }
            System.out.println();
        }
    }
}
