package com.example;

import java.awt.Graphics;
import java.awt.Image;

/**
 * Eagle chess piece.
 *
 * The eagle may move exactly five squares in any of the eight principal
 * directions (horizontal, vertical, or diagonal). It cannot jump over
 * other pieces – all intervening squares between the start and end
 * squares must be empty.
 *
 * The image you pass in should be the eagle image for this piece
 * (for example: "weagle.png" for a white eagle or "beagle.png" for a
 * black eagle in your Pictures folder).
 */
public class Piece {

    // true == white eagle, false == black eagle
    private boolean color;

    // the eagle graphic that will be drawn on the board
    private Image image;

    /**
     * Creates an eagle piece of the given color using the given eagle image path.
     *
     * @param color     true for white eagle, false for black eagle
     * @param imagePath path to the eagle image file (e.g. "weagle.png" / "beagle.png")
     */
    public Piece(boolean color, String imagePath) {
        this.color = color;
        this.image = Utils.loadImage(imagePath);  // loads the eagle image
    }

    public boolean getColor() {
        return color;
    }

    public Image getImage() {
        return image;
    }

    /**
     * Determines whether a move from {@code from} to {@code to} is legal
     * for this eagle on the supplied board.
     */
    public boolean isLegalMove(Square from, Square to, Square[][] board) {
        if (from == null || to == null) {
            return false;
        }

        // no movement
        if (from == to) {
            return false;
        }

        // sanity check: this piece must actually be on the from-square
        if (from.getOccupyingPiece() != this) {
            return false;
        }

        // cannot capture own color
        Piece dest = to.getOccupyingPiece();
        if (dest != null && dest.getColor() == this.color) {
            return false;
        }

        int r1 = from.getRow();
        int c1 = from.getCol();
        int r2 = to.getRow();
        int c2 = to.getCol();

        int dr = Math.abs(r2 - r1);
        int dc = Math.abs(c2 - c1);

        // EAGLE MOVEMENT:
        // must move exactly 5 squares either vertically, horizontally, or diagonally
        boolean correctDistance =
                (dr == 5 && dc == 0) ||   // vertical
                (dr == 0 && dc == 5) ||   // horizontal
                (dr == 5 && dc == 5);     // diagonal

        if (!correctDistance) {
            return false;
        }

        // eagle cannot jump over pieces – path must be clear
        return pathClear(from, to, board);
    }

    /**
     * Returns true if every square between {@code from} and {@code to}
     * (exclusive) is empty. Assumes a straight or diagonal line.
     */
    private boolean pathClear(Square from, Square to, Square[][] board) {
        int r1 = from.getRow();
        int c1 = from.getCol();
        int r2 = to.getRow();
        int c2 = to.getCol();

        int dr = Integer.signum(r2 - r1);
        int dc = Integer.signum(c2 - c1);
        int steps = Math.max(Math.abs(r2 - r1), Math.abs(c2 - c1));

        for (int i = 1; i < steps; i++) {
            int rr = r1 + dr * i;
            int cc = c1 + dc * i;
            if (board[rr][cc].isOccupied()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Draws the eagle image on the given square.
     */
    public void draw(Graphics g, Square square) {
        if (image == null || g == null || square == null) {
            return;
        }

        int x = square.getX();
        int y = square.getY();
        int w = square.getWidth();
        int h = square.getHeight();

        g.drawImage(image, x, y, w, h, null);
    }
}