package com.example;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.net.URL;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.*;

//You will be implmenting a part of a function and a whole function in this document. Please follow the directions for the 
//suggested order of completion that should make testing easier.
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
    Piece currPiece;
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

        // populate the board with squares here. Note that the board is composed of 64
        // squares alternating from white to black.
        // IMPORTANT: for each square you create you HAVE to do "this.add(<your new square here>)"
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                // decide if this square is white or black
                boolean isWhite = ((row + column) % 2 == 0);

                // create a new Square using (Board, boolean, row, col)
                Square sq = new Square(this, isWhite, row, column);
                
                
                // store it in the board array
                board[row][column] = sq;

                // add it so it will be displayed
                this.add(sq);
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
    // since we only have one kind of piece for now you need only set the same
    // number of pieces on either side.
    // it's up to you how you wish to arrange your pieces.
    void initializePieces() {
        // place white eagle on row 7, col 4
        board[7][4].put(new Piece(true, RESOURCES_WEAGLE_PNG));

        // place black eagle on row 0, col 4
        board[0][4].put(new Piece(false, RESOURCES_BEAGLE_PNG));

        // a row of white eagles on row 6
        for (int i = 0; i < 8; i++) {
            board[6][i].put(new Piece(true, RESOURCES_WEAGLE_PNG));
        }

        // a row of black eagles on row 1
        for (int i = 0; i < 8; i++) {
            board[1][i].put(new Piece(false, RESOURCES_BEAGLE_PNG));
        }

        
        board[0][0].put(new Piece(false, RESOURCES_BEAGLE_PNG));
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

        if (sq.isOccupied() && sq.getOccupyingPiece().getColor() == whiteTurn) {
            currPiece = sq.getOccupyingPiece();
            fromMoveSquare = sq;
            for(Square s: currPiece.getLegalMoves(this, fromMoveSquare)){
                s.setBorder(BorderFactory.createMatteBorder(4, 4,4,4, Color.white));
            }
            sq.setDisplay(false);
        }
        repaint();
    }

    // TO BE IMPLEMENTED!
    // should move the piece to the desired location only if this is a legal move.
    // use the pieces "legal move" function to determine if this move is legal, then
    // complete it by moving the new piece to it's new board location.
    @Override
public void mouseReleased(MouseEvent e) {
    // where the mouse was released
    endSquare = (Square) this.getComponentAt(new Point(e.getX(), e.getY()));

    if (currPiece != null && fromMoveSquare != null && endSquare != null) {
        // ask the piece for all legal moves from its starting square
        ArrayList<Square> legalMoves = currPiece.getLegalMoves(this, fromMoveSquare);

        // check if endSquare is one of the legal moves
        boolean isLegal = legalMoves.contains(endSquare);

        if (isLegal) {
            // handle capture if there is a piece on endSquare
            if (endSquare.isOccupied()) {
                // remove the captured piece
                endSquare.removePiece();
            }

            // move the piece: remove from old square, put on new square
            fromMoveSquare.removePiece();
            endSquare.put(currPiece);

            // switch turns (white <-> black)
            whiteTurn = !whiteTurn;
        }

        // no matter what, show the piece again on its square
        fromMoveSquare.setDisplay(true);
    }

    // clear the dragging state
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