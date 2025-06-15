// In: main/com/ShavguLs/chess/view/ImageManager.java
// Replaces the ENTIRE old file.

package main.com.ShavguLs.chess.view;

// IMPORT YOUR LOGIC.PIECE, NOT THE OLD ONE
import main.com.ShavguLs.chess.logic.Piece;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageManager {
    private static ImageManager instance;
    // The Map now uses a Character (the FEN char) as its key
    private final Map<Character, Image> images = new HashMap<>();

    private ImageManager() {
        loadAllImages();
    }

    public static synchronized ImageManager getInstance() {
        if (instance == null) {
            instance = new ImageManager();
        }
        return instance;
    }

    private void loadAllImages() {
        // Load images using their standard FEN character as the key
        // White Pieces (UPPERCASE)
        loadImage('K', "/images/wking.png");
        loadImage('Q', "/images/wqueen.png");
        loadImage('R', "/images/wrook.png");
        loadImage('B', "/images/wbishop.png");
        loadImage('N', "/images/wknight.png");
        loadImage('P', "/images/wpawn.png");

        // Black Pieces (lowercase)
        loadImage('k', "/images/bking.png");
        loadImage('q', "/images/bqueen.png");
        loadImage('r', "/images/brook.png");
        loadImage('b', "/images/bbishop.png");
        loadImage('n', "/images/bknight.png");
        loadImage('p', "/images/bpawn.png");
    }

    private void loadImage(Character key, String path) {
        try {
            Image img = ImageIO.read(getClass().getResource(path));
            if (img != null) {
                images.put(key, img);
            } else {
                System.err.println("Could not load image resource: " + path);
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Exception while loading image: " + path);
            e.printStackTrace();
        }
    }

    /**
     * Gets the corresponding image for a given piece.
     * This method now accepts the new logic.Piece.
     * @param piece The piece object from the logic package.
     * @return The Image for the piece, or null if not found.
     */
    public Image getPieceImage(Piece piece) {
        if (piece == null) {
            return null;
        }
        // We use the handy getFenChar() method we created earlier!
        return images.get(piece.getFenChar());
    }
}