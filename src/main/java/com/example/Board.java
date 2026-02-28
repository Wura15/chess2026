package com.example;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Board extends JPanel implements MouseListener, MouseMotionListener {
    // Resource location constants for piece images
    public static final String PICTURE_PATH = "/src/main/java/com/example/Pictures/";
    private static final String RESOURCES_WBISHOP_PNG = PICTURE_PATH + "wbishop.png";
    private static final String RESOURCES_BBISHOP_PNG = PICTURE_PATH + "bbishop.png";
    private static final String RESOURCES_WKNIGHT_PNG = PICTURE_PATH + "wknight.png";
    private static final String RESOURCES_BKNIGHT_PNG = PICTURE_PATH + "bknight.png";
    private static final String RESOURCES_WROOK_PNG = PICTURE_PATH + "wrook.png";
    private static final String RESOURCES_BROOK_PNG = PICTURE_PATH + "brook.png";
    private static final String RESOURCES_WKING_PNG = PICTURE_PATH + "wking.png";
    private static final String RESOURCES_BKING_PNG = PICTURE_PATH + "bking.png";
    private static final String RESOURCES_BQUEEN_PNG = PICTURE_PATH + "bqueen.png";
    private static final String RESOURCES_WQUEEN_PNG = PICTURE_PATH + "wqueen.png";
    private static final String RESOURCES_WPAWN_PNG = PICTURE_PATH + "wpawn.png";
    private static final String RESOURCES_BPAWN_PNG = PICTURE_PATH + "bpawn.png";
    private static final String RESOURCES_WEAGLE_PNG = PICTURE_PATH + "weagle.png";
    private static final String RESOURCES_BEAGLE_PNG = PICTURE_PATH + "beagle.png";
    // constant used to keep track of where the piece should be drawn when the user is dragging it
    private static final int PIECE_OFFSET = 24;

    // Logical and graphical representations of board
    private final Square[][] board;
    private final GameWindow g;

    // contains true if it's white's turn.
    private boolean whiteTurn;

    // if the player is currently dragging a piece this variable contains it.
    private Piece currPiece;
    // the square your piece came from when the user tries to move it.
    private Square fromMoveSquare;
    // the square your piece tries to go to when the user tries to move it.
    private Square endSquare;

    // used to keep track of the x/y coordinates of the mouse.
    private int currX;
    private int currY;

    public Board(GameWindow g) {
        this.g = g;
        board = new Square[8][8];
        setLayout(new GridLayout(8, 8, 0, 0));

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        // Create 8x8 squares, alternating colors, row-major order
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // true = light square, false = dark square
                boolean isLight = (row + col) % 2 == 0;

                board[row][col] = new Square(this, isLight, row, col);
                this.add(board[row][col]);
            }
        }

        initializePieces();

        this.setPreferredSize(new Dimension(400, 400));
        this.setMaximumSize(new Dimension(400, 400));
        this.setMinimumSize(this.getPreferredSize());
        this.setSize(new Dimension(400, 400));

        whiteTurn = true;
    }

    // set up the board such that the black pieces are on one side and the white
    // pieces are on the other.
    // Since we only have one kind of piece for now, we just place bishops symmetrically.
   void initializePieces() {
    // Clear pieces just in case (optional if board is empty at start)
    for (int r = 0; r < 8; r++) {
        for (int c = 0; c < 8; c++) {
            board[r][c].removePiece();
        }
    }

    // ---------- WHITE PIECES (bottom, row 7 and 6) ----------

    // Rooks
    board[7][0].put(new Piece(true, RESOURCES_WROOK_PNG, Piece.Type.ROOK));
    board[7][7].put(new Piece(true, RESOURCES_WROOK_PNG, Piece.Type.ROOK));

    // Knights
    board[7][1].put(new Piece(true, RESOURCES_WKNIGHT_PNG, Piece.Type.KNIGHT));
    board[7][6].put(new Piece(true, RESOURCES_WKNIGHT_PNG, Piece.Type.KNIGHT));

    // Bishops
    board[7][2].put(new Piece(true, RESOURCES_WBISHOP_PNG, Piece.Type.BISHOP));
    board[7][5].put(new Piece(true, RESOURCES_WBISHOP_PNG, Piece.Type.BISHOP));

    // Queen
    board[7][3].put(new Piece(true, RESOURCES_WQUEEN_PNG, Piece.Type.QUEEN));

    // King
    board[7][4].put(new Piece(true, RESOURCES_WKING_PNG, Piece.Type.KING));

    // Pawns
    for (int c = 0; c < 8; c++) {
        board[6][c].put(new Piece(true, RESOURCES_WPAWN_PNG, Piece.Type.PAWN));
    }

    // White Eagle (for example, in front of the queen’s bishop)
    board[5][2].put(new Piece(true, RESOURCES_WEAGLE_PNG, Piece.Type.EAGLE));

    // ---------- BLACK PIECES (top, row 0 and 1) ----------

    // Rooks
    board[0][0].put(new Piece(false, RESOURCES_BROOK_PNG, Piece.Type.ROOK));
    board[0][7].put(new Piece(false, RESOURCES_BROOK_PNG, Piece.Type.ROOK));

    // Knights
    board[0][1].put(new Piece(false, RESOURCES_BKNIGHT_PNG, Piece.Type.KNIGHT));
    board[0][6].put(new Piece(false, RESOURCES_BKNIGHT_PNG, Piece.Type.KNIGHT));

    // Bishops
    board[0][2].put(new Piece(false, RESOURCES_BBISHOP_PNG, Piece.Type.BISHOP));
    board[0][5].put(new Piece(false, RESOURCES_BBISHOP_PNG, Piece.Type.BISHOP));

    // Queen
    board[0][3].put(new Piece(false, RESOURCES_BQUEEN_PNG, Piece.Type.QUEEN));

    // King
    board[0][4].put(new Piece(false, RESOURCES_BKING_PNG, Piece.Type.KING));

    // Pawns
    for (int c = 0; c < 8; c++) {
        board[1][c].put(new Piece(false, RESOURCES_BPAWN_PNG, Piece.Type.PAWN));
    }

    // Black Eagle
    board[2][2].put(new Piece(false, RESOURCES_BEAGLE_PNG, Piece.Type.EAGLE));
}
    public Square[][] getSquareArray() {
        return this.board;
    }

    public boolean getTurn() {
        return whiteTurn;
    }

    public void setCurrPiece(Piece p) {
        this.currPiece = p;
    }

    public Piece getCurrPiece() {
        return this.currPiece;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col].draw(g);
            }
        }

        if (currPiece != null) {
            if (currPiece.getColor() == whiteTurn) {
                final Image img = currPiece.getImage();
                g.drawImage(img, currX, currY, null);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        currX = e.getX();
        currY = e.getY();

        Square sq = (Square) this.getComponentAt(new Point(e.getX(), e.getY()));

        if (sq instanceof Square && sq.isOccupied() && sq.getOccupyingPiece().getColor() == whiteTurn) {
            currPiece = sq.getOccupyingPiece();
            fromMoveSquare = sq;
            sq.setDisplay(false);
        }
        repaint();
    }

    // should move the piece to the desired location only if this is a legal move.
    @Override
    public void mouseReleased(MouseEvent e) {
        endSquare = (Square) this.getComponentAt(new Point(e.getX(), e.getY()));

        if (currPiece != null && fromMoveSquare != null && endSquare instanceof Square) {
            // Get all legal moves for this piece from its starting square
            ArrayList<Square> legalMoves = currPiece.getLegalMoves(this, fromMoveSquare);

            boolean isLegal = false;
            for (Square s : legalMoves) {
                if (s == endSquare) { // same Square instance
                    isLegal = true;
                    break;
                }
            }

            if (isLegal) {
                // Legal move: remove from old square, put on new
                fromMoveSquare.removePiece();
                endSquare.put(currPiece);

                // Switch turn
                whiteTurn = !whiteTurn;
            } else {
                // Illegal move: snap back
                fromMoveSquare.setDisplay(true);
            }
        } else if (fromMoveSquare != null) {
            // No valid move attempted, just restore display
            fromMoveSquare.setDisplay(true);
        }

        // Clear drag state
        currPiece = null;
        fromMoveSquare = null;
        endSquare = null;

        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        currX = e.getX() - PIECE_OFFSET;
        currY = e.getY() - PIECE_OFFSET;

        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}