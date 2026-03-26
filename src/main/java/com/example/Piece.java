package com.example;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

//you will need to implement two functions in this file.
public class Piece {
    private final boolean color;
    private BufferedImage img;

    public Piece(boolean isWhite, String img_file) {
        this.color = isWhite;

        try {
            if (this.img == null) {
                this.img = ImageIO.read(new File(System.getProperty("user.dir") + img_file));
            }
        } catch (IOException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }

    public boolean getColor() {
        return color;
    }

    public Image getImage() {
        return img;
    }

    //precondition: g and currentSquare must be non-null valid objects.
    //postcondition: the image stored in the img property of this object is drawn to the screen.
    public void draw(Graphics g, Square currentSquare) {
        int x = currentSquare.getX();
        int y = currentSquare.getY();

        g.drawImage(this.img, x, y, null);
    }

    // return a list of every square that is "controlled" by this piece.
    // a square is controlled if the piece could capture into it legally.
    public ArrayList<Square> getControlledSquares(Square[][] board, Square start) {
        ArrayList<Square> controlled = new ArrayList<>();

        if (board == null || start == null) {
            return controlled;
        }

        int startRow = start.getRow();
        int startCol = start.getCol();

        // 8 directions: up, down, left, right, and diagonals
        int[][] directions = {
            {-1,  0}, // up
            { 1,  0}, // down
            { 0, -1}, // left
            { 0,  1}, // right
            {-1, -1}, // up-left
            {-1,  1}, // up-right
            { 1, -1}, // down-left
            { 1,  1}  // down-right
        };

        // move up to 5 squares in each direction
        for (int d = 0; d < directions.length; d++) {
            int dr = directions[d][0];
            int dc = directions[d][1];

            for (int step = 1; step <= 5; step++) {
                int r = startRow + dr * step;
                int c = startCol + dc * step;

                // in bounds?
                if (r < 0 || r >= board.length || c < 0 || c >= board[0].length) {
                    break; // stop in this direction
                }

                Square square = board[r][c];

                if (!square.isOccupied()) {
                    // empty square is controlled (we could move there)
                    controlled.add(square);
                } else {
                    // occupied: if enemy, we control that square, then stop
                    if (square.getOccupyingPiece().getColor() != color) {
                        controlled.add(square);
                    }
                    // friend or enemy blocks any further squares in this direction
                    break;
                }
            }
        }

        return controlled;
    }

    // implement the move function here
    // returns an ArrayList of squares which are legal to move to
    public ArrayList<Square> getLegalMoves(Board b, Square start) {
        ArrayList<Square> moves = new ArrayList<>();

        if (b == null || start == null) {
            return moves;
        }

        // get double array out of Board
        Square[][] allSquares = b.getSquareArray();

        // where you are is start.getRow(), start.getCol()
        int row = start.getRow();
        int col = start.getCol();

        // try going up (this is what YOU started)
        for (int i = 1; i <= 5; i++) {
            // in bounds?
            if (row - i >= 0) {
                Square square = allSquares[row - i][col];
                if (!square.isOccupied()) {
                    moves.add(square);
                } else {
                    // occupied square: can only move here if enemy piece
                    if (square.getOccupyingPiece().getColor() != color) {
                        moves.add(square);
                    }
                    // stop going further up
                    break;
                }
            } else {
                break;
            }
        }

        // try going down
        for (int i = 1; i <= 5; i++) {
            if (row + i < allSquares.length) {
                Square square = allSquares[row + i][col];
                if (!square.isOccupied()) {
                    moves.add(square);
                } else {
                    if (square.getOccupyingPiece().getColor() != color) {
                        moves.add(square);
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // try going left
        for (int i = 1; i <= 5; i++) {
            if (col - i >= 0) {
                Square square = allSquares[row][col - i];
                if (!square.isOccupied()) {
                    moves.add(square);
                } else {
                    if (square.getOccupyingPiece().getColor() != color) {
                        moves.add(square);
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // try going right
        for (int i = 1; i <= 5; i++) {
            if (col + i < allSquares[0].length) {
                Square square = allSquares[row][col + i];
                if (!square.isOccupied()) {
                    moves.add(square);
                } else {
                    if (square.getOccupyingPiece().getColor() != color) {
                        moves.add(square);
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // up-left
        for (int i = 1; i <= 5; i++) {
            int r = row - i;
            int c = col - i;
            if (r >= 0 && c >= 0) {
                Square square = allSquares[r][c];
                if (!square.isOccupied()) {
                    moves.add(square);
                } else {
                    if (square.getOccupyingPiece().getColor() != color) {
                        moves.add(square);
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // up-right
        for (int i = 1; i <= 5; i++) {
            int r = row - i;
            int c = col + i;
            if (r >= 0 && c < allSquares[0].length) {
                Square square = allSquares[r][c];
                if (!square.isOccupied()) {
                    moves.add(square);
                } else {
                    if (square.getOccupyingPiece().getColor() != color) {
                        moves.add(square);
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // down-left
        for (int i = 1; i <= 5; i++) {
            int r = row + i;
            int c = col - i;
            if (r < allSquares.length && c >= 0) {
                Square square = allSquares[r][c];
                if (!square.isOccupied()) {
                    moves.add(square);
                } else {
                    if (square.getOccupyingPiece().getColor() != color) {
                        moves.add(square);
                    }
                    break;
                }
            } else {
                break;
            }
        }

        // down-right
        for (int i = 1; i <= 5; i++) {
            int r = row + i;
            int c = col + i;
            if (r < allSquares.length && c < allSquares[0].length) {
                Square square = allSquares[r][c];
                if (!square.isOccupied()) {
                    moves.add(square);
                } else {
                    if (square.getOccupyingPiece().getColor() != color) {
                        moves.add(square);
                    }
                    break;
                }
            } else {
                break;
            }
        }

        return moves;
    }
}