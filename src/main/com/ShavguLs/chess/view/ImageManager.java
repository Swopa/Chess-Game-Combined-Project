package main.com.ShavguLs.chess.view;

import main.com.ShavguLs.chess.model.Piece;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class ImageManager {
    private static ImageManager instance;
    private Map<String, Image> images = new HashMap<>();

    private ImageManager() {
        loadAllImages();
    }

    public static ImageManager getInstance() {
        if (instance == null) {
            instance = new ImageManager();
        }
        return instance;
    }

    private void loadAllImages() {
        // Load all piece images
        loadImage("bking", "/images/bking.png");
        loadImage("bqueen", "/images/bqueen.png");
        loadImage("brook", "/images/brook.png");
        loadImage("bbishop", "/images/bbishop.png");
        loadImage("bknight", "/images/bknight.png");
        loadImage("bpawn", "/images/bpawn.png");

        loadImage("wking", "/images/wking.png");
        loadImage("wqueen", "/images/wqueen.png");
        loadImage("wrook", "/images/wrook.png");
        loadImage("wbishop", "/images/wbishop.png");
        loadImage("wknight", "/images/wknight.png");
        loadImage("wpawn", "/images/wpawn.png");
    }

    private void loadImage(String key, String path) {
        try {
            Image img = ImageIO.read(getClass().getResource(path));
            images.put(key, img);
        } catch (Exception e) {
            System.err.println("Could not load image: " + path);
        }
    }

    public Image getPieceImage(Piece piece) {
        String color = piece.getColor() == 1 ? "w" : "b";
        String type = piece.getClass().getSimpleName().toLowerCase();
        return images.get(color + type);
    }
}