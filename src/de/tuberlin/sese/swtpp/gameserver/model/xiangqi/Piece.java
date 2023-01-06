package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Piece implements Serializable {
	
	private static final long serialVersionUID = 8055340290805032160L;
	private String pieceLetter;
    private boolean isRed;
    XiangqiGame game;
    
    /**
     * This method gets a String that represents the piece, it will always be a single char.
     * It first defines the value of isRed by checking if the char is uppercased or lowercased.
     * Then it defines the 
     * 
     * @param pieceAsString: figure described as a single char e.g. "G" for the red General
     */
    public Piece(String pieceLetter) {
    	Pattern pattern = Pattern.compile("([A-Z])");
        Matcher matcher = pattern.matcher(pieceLetter);

        if (matcher.find()) {
    		this.isRed = true;
    	} else {
    		this.isRed = false;
        }
        
        this.pieceLetter = pieceLetter;
    }
    
    public String getPieceLetter() {
    	return pieceLetter;
    }
    
    public boolean isRed() {
    	return this.isRed;
    }

    public String getPlayerLetter() {
        return this.isRed ? "r" : "b";
    }

    /*
     * instantiate the specific figure and return the value of its checkIfMoveIsValid() method
     */
    public boolean checkIfMoveIsValid(String moveString, Piece[][] board, Direction moveDirection, int numberOfSteps, String playerLetter) {
    	if(pieceLetter.toLowerCase().equals("g")) {
        	General general = new General(pieceLetter);
        	return general.checkIfMoveIsValid(moveString, board, moveDirection, numberOfSteps, playerLetter);
        }
    	if(pieceLetter.toLowerCase().equals("a")) {
        	Advisor advisor = new Advisor(pieceLetter);
        	return advisor.checkIfMoveIsValid(moveString, board, moveDirection, numberOfSteps, playerLetter);
        }
    	if(pieceLetter.toLowerCase().equals("e")) {
        	Elephant elephant = new Elephant(pieceLetter);
        	return elephant.checkIfMoveIsValid(moveString, board, moveDirection, numberOfSteps, playerLetter);
        }
        if(pieceLetter.toLowerCase().equals("h")) {
        	Horse horse = new Horse(pieceLetter);
        	return horse.checkIfMoveIsValid(moveString, board, moveDirection, numberOfSteps, playerLetter);
        }
        if(pieceLetter.toLowerCase().equals("r")) {
        	Rook rook = new Rook(pieceLetter);
        	return rook.checkIfMoveIsValid(moveString, board, moveDirection, numberOfSteps, playerLetter);
        }
        return checkIfMoveIsValid2(moveString, board, moveDirection, numberOfSteps, playerLetter);
    }
    
    public boolean checkIfMoveIsValid2(String moveString, Piece[][] board, Direction moveDirection, int numberOfSteps, String playerLetter) {
    	if(pieceLetter.toLowerCase().equals("c")) {
        	Cannon cannon = new Cannon(pieceLetter);
        	return cannon.checkIfMoveIsValid(moveString, board, moveDirection, numberOfSteps, playerLetter);
        } else {
        	Soldier soldier = new Soldier(pieceLetter);
        	return soldier.checkIfMoveIsValid(moveString, board, moveDirection, numberOfSteps, playerLetter);
        }
    }

}
