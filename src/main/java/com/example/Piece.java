package com.example;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

// Piece class with different movement rules depending on type.
// Types supported: EAGLE (custom), KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN.
public class Piece {

    // Type of this piece
    public enum Type {
        EAGLE,
        KING,
        QUEEN,
        ROOK,
        BISHOP,
        KNIGHT,
        PAWN
    }

    private final boolean color;   // true = white, false = black
    private final Type type;       // what kind of piece this is
    private BufferedImage img;

    // precondition: img_file is the image path for this piece; type is non-null.
    // postcondition: a new piece is created with the given color, image, and type.
    public Piece(boolean isWhite, String img_file, Type type) {
        this.color = isWhite;
        this.type = type;

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

    public Type getType() {
        return type;
    }

    public Image getImage() {
        return img;
    }

    // precondition: g and currentSquare must be non-null valid objects.
    // postcondition: the image stored in the img property of this object is drawn to the screen.
    public void draw(Graphics g, Square currentSquare) {
        int x = currentSquare.getX();
        int y = currentSquare.getY();

        g.drawImage(this.img, x, y, null);
    }

    // =======================================================
    // Helper methods for sliding / controlled moves
    // =======================================================

    // Adds legal squares in a sliding direction (dr, dc) up to maxSteps.
    private void addDirectionalMoves(ArrayList<Square> moves,
                                     Square[][] board,
                                     int startRow,
                                     int startCol,
                                     int dr,
                                     int dc,
                                     int maxSteps) {
        int r = startRow + dr;
        int c = startCol + dc;
        int steps = 0;

        while (r >= 0 && r < 8 && c >= 0 && c < 8 && steps < maxSteps) {
            Square target = board[r][c];

            if (!target.isOccupied()) {
                // Empty square: legal move, keep going
                moves.add(target);
            } else {
                // Occupied: capture only if enemy, then stop
                Piece other = target.getOccupyingPiece();
                if (other.getColor() != this.getColor()) {
                    moves.add(target);
                }
                break; // cannot go past any piece
            }

            r += dr;
            c += dc;
            steps++;
        }
    }

    // Adds controlled squares along a direction up to maxSteps
    private void addDirectionalControlled(ArrayList<Square> controlled,
                                          Square[][] board,
                                          int startRow,
                                          int startCol,
                                          int dr,
                                          int dc,
                                          int maxSteps) {
        int r = startRow + dr;
        int c = startCol + dc;
        int steps = 0;

        while (r >= 0 && r < 8 && c >= 0 && c < 8 && steps < maxSteps) {
            Square target = board[r][c];

            controlled.add(target); // controlled up to first piece

            if (target.isOccupied()) {
                break; // stop past first piece
            }

            r += dr;
            c += dc;
            steps++;
        }
    }

    // =======================================================
    // CONTROLLED SQUARES
    // =======================================================

    // return a list of every square that is "controlled" by this piece.
    // A square is controlled if the piece can capture into it (or lies along
    // its attack line up to the first blocker for sliding pieces).
    public ArrayList<Square> getControlledSquares(Square[][] board, Square start) {
        ArrayList<Square> controlled = new ArrayList<Square>();

        int startRow = start.getRow();
        int startCol = start.getCol();

        switch (type) {
            case EAGLE: {
                // Eagle: up to 5 squares in any direction
                int maxSteps = 5;
                int[][] dirs = {
                    {-1,  0}, {1,  0}, {0, -1}, {0,  1},
                    {-1, -1}, {-1, 1}, {1, -1}, {1,  1}
                };
                for (int[] d : dirs) {
                    addDirectionalControlled(controlled, board, startRow, startCol, d[0], d[1], maxSteps);
                }
                break;
            }

            case QUEEN: {
                int maxSteps = 7;
                int[][] dirs = {
                    {-1,  0}, {1,  0}, {0, -1}, {0,  1},
                    {-1, -1}, {-1, 1}, {1, -1}, {1,  1}
                };
                for (int[] d : dirs) {
                    addDirectionalControlled(controlled, board, startRow, startCol, d[0], d[1], maxSteps);
                }
                break;
            }

            case ROOK: {
                int maxSteps = 7;
                int[][] dirs = {
                    {-1, 0}, {1, 0}, {0, -1}, {0, 1}
                };
                for (int[] d : dirs) {
                    addDirectionalControlled(controlled, board, startRow, startCol, d[0], d[1], maxSteps);
                }
                break;
            }

            case BISHOP: {
                int maxSteps = 7;
                int[][] dirs = {
                    {-1, -1}, {-1, 1}, {1, -1}, {1,  1}
                };
                for (int[] d : dirs) {
                    addDirectionalControlled(controlled, board, startRow, startCol, d[0], d[1], maxSteps);
                }
                break;
            }

            case KING: {
                int[][] offsets = {
                    {-1,-1},{-1,0},{-1,1},
                    {0,-1},       {0,1},
                    {1,-1},{1,0},{1,1}
                };
                for (int[] off : offsets) {
                    int r = startRow + off[0];
                    int c = startCol + off[1];
                    if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                        controlled.add(board[r][c]);
                    }
                }
                break;
            }

            case KNIGHT: {
                int[][] offsets = {
                    {-2,-1},{-2,1},{-1,-2},{-1,2},
                    {1,-2},{1,2},{2,-1},{2,1}
                };
                for (int[] off : offsets) {
                    int r = startRow + off[0];
                    int c = startCol + off[1];
                    if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                        controlled.add(board[r][c]);
                    }
                }
                break;
            }

            case PAWN: {
                // pawn controls diagonals forward
                int dir = (color ? -1 : 1); // white up (-1), black down (+1)
                int r = startRow + dir;
                int[] dcs = {-1, 1};
                for (int dc : dcs) {
                    int c = startCol + dc;
                    if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                        controlled.add(board[r][c]);
                    }
                }
                break;
            }
        }

        return controlled;
    }

    // =======================================================
    // LEGAL MOVES
    // =======================================================

    // returns an arraylist of squares which are legal to move to
    public ArrayList<Square> getLegalMoves(Board b, Square start) {
        ArrayList<Square> moves = new ArrayList<Square>();

        Square[][] board = b.getSquareArray();
        int startRow = start.getRow();
        int startCol = start.getCol();

        switch (type) {
            case EAGLE: {
                // Rules for Eagle:
                // - Up to 5 squares in any of 8 directions.
                // - Cannot jump.
                // - Cannot land on same color; can capture enemy.
                int maxSteps = 5;
                int[][] dirs = {
                    {-1,  0}, {1,  0}, {0, -1}, {0,  1},
                    {-1, -1}, {-1, 1}, {1, -1}, {1,  1}
                };
                for (int[] d : dirs) {
                    addDirectionalMoves(moves, board, startRow, startCol, d[0], d[1], maxSteps);
                }
                break;
            }

            case QUEEN: {
                int maxSteps = 7;
                int[][] dirs = {
                    {-1,  0}, {1,  0}, {0, -1}, {0,  1},
                    {-1, -1}, {-1, 1}, {1, -1}, {1,  1}
                };
                for (int[] d : dirs) {
                    addDirectionalMoves(moves, board, startRow, startCol, d[0], d[1], maxSteps);
                }
                break;
            }

            case ROOK: {
                int maxSteps = 7;
                int[][] dirs = {
                    {-1, 0}, {1, 0}, {0, -1}, {0, 1}
                };
                for (int[] d : dirs) {
                    addDirectionalMoves(moves, board, startRow, startCol, d[0], d[1], maxSteps);
                }
                break;
            }

            case BISHOP: {
                int maxSteps = 7;
                int[][] dirs = {
                    {-1, -1}, {-1, 1}, {1, -1}, {1,  1}
                };
                for (int[] d : dirs) {
                    addDirectionalMoves(moves, board, startRow, startCol, d[0], d[1], maxSteps);
                }
                break;
            }

            case KING: {
                int[][] offsets = {
                    {-1,-1},{-1,0},{-1,1},
                    {0,-1},       {0,1},
                    {1,-1},{1,0},{1,1}
                };
                for (int[] off : offsets) {
                    int r = startRow + off[0];
                    int c = startCol + off[1];
                    if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                        Square target = board[r][c];
                        if (!target.isOccupied() || target.getOccupyingPiece().getColor() != this.getColor()) {
                            moves.add(target);
                        }
                    }
                }
                break;
            }

            case KNIGHT: {
                int[][] offsets = {
                    {-2,-1},{-2,1},{-1,-2},{-1,2},
                    {1,-2},{1,2},{2,-1},{2,1}
                };
                for (int[] off : offsets) {
                    int r = startRow + off[0];
                    int c = startCol + off[1];
                    if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                        Square target = board[r][c];
                        if (!target.isOccupied() || target.getOccupyingPiece().getColor() != this.getColor()) {
                            moves.add(target);
                        }
                    }
                }
                break;
            }

            case PAWN: {
                // Simple pawn:
                // - moves 1 square forward if empty
                // - captures 1 square diagonally forward
                int dir = (color ? -1 : 1); // white up, black down
                int rForward = startRow + dir;
                if (rForward >= 0 && rForward < 8) {
                    // forward move
                    Square forward = board[rForward][startCol];
                    if (!forward.isOccupied()) {
                        moves.add(forward);
                    }

                    // captures
                    int[] dcs = {-1, 1};
                    for (int dc : dcs) {
                        int c = startCol + dc;
                        if (c >= 0 && c < 8) {
                            Square diag = board[rForward][c];
                            if (diag.isOccupied() && diag.getOccupyingPiece().getColor() != this.getColor()) {
                                moves.add(diag);
                            }
                        }
                    }
                }
                break;
            }
        }

        return moves;
    }
}