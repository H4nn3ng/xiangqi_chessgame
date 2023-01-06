package de.tuberlin.sese.swtpp.gameserver.test.xiangqi;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;

public class TryMoveIntegrationTest {


	User user1 = new User("Alice", "alice");
	User user2 = new User("Bob", "bob");
	
	Player redPlayer = null;
	Player blackPlayer = null;
	Game game = null;
	GameController controller;
	
	@Before
	public void setUp() throws Exception {
		controller = GameController.getInstance();
		controller.clear();
		
		int gameID = controller.startGame(user1, "", "xiangqi");
		
		game =  controller.getGame(gameID);
		redPlayer = game.getPlayer(user1);

	}
	
	public void startGame() {
		controller.joinGame(user2, "xiangqi");		
		blackPlayer = game.getPlayer(user2);
	}
	
	public void startGame(String initialBoard, boolean redNext) {
		startGame();
		
		game.setBoard(initialBoard);
		game.setNextPlayer(redNext? redPlayer:blackPlayer);
	}
	
	public void assertMove(String move, boolean red, boolean expectedResult) {
		if (red)
			assertEquals(expectedResult, game.tryMove(move, redPlayer));
		else 
			assertEquals(expectedResult,game.tryMove(move, blackPlayer));
	}
	
	public void assertGameState(String expectedBoard, boolean redNext, boolean finished, boolean redWon) {
		assertEquals(expectedBoard,game.getBoard());
		assertEquals(finished, game.isFinished());

		if (!game.isFinished()) {
			assertEquals(redNext, game.getNextPlayer() == redPlayer);
		} else {
			assertEquals(redWon, redPlayer.isWinner());
			assertEquals(!redWon, blackPlayer.isWinner());
		}
	}
	

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 *******************************************/
	
	@Test
	public void exampleTest() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    assertMove("e3-e4",true,true);
	    assertGameState("rheagaehr/9/1c5c1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR",false,false,false);
	}

	//TODO: implement test cases of same kind as example here
	@Test
	public void moveSyntaxTest() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    assertMove("u0-u1-7",true,false);
	    assertMove("u99-u1",true,false);
		assertMove("u-u1",true,false);
		assertMove("u1-1",true,false);
	    assertMove("u0-u1",true,false);
	    assertMove("a0-u1",true,false);
	    assertMove("aa-a1",true,false);
	    assertMove("a1-aa",true,false);
	    assertGameState("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true,false,false);
	}
	
	@Test
	public void generalMoveTest() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    assertMove("e0-e2",true,false);
	    assertMove("e0-f0",true,false);
	    assertMove("e0-f1",true,false);
	    assertMove("e0-e1",true,true);
	    assertMove("e9-d9",false,false);
	    assertMove("e9-d8",false,false);
	    assertMove("e9-e8",false,true);
	    assertMove("e1-e2",true,true);
	    assertMove("e8-e7",false,true);
	    assertMove("e2-e3",true,false);
	    assertMove("e2-f2",true,true);
	    assertMove("e7-e6",false,false);
	    assertMove("e7-f7",false,false); //death look
	    assertMove("e7-d7",false,true);
	    assertGameState("rhea1aehr/9/1c1g3c1/s1s1s1s1s/9/9/S1S1S1S1S/1C3G1C1/9/RHEA1AEHR",true,false,false);
	}
	
	@Test
	public void advisorMoveTest() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    assertMove("d0-c1",true,false);
	    assertMove("d0-d1",true,false);
	    assertMove("d0-f2",true,false);
	    assertMove("d0-e1",true,true);
	    assertMove("d9-c8",false,false);
	    assertMove("d9-d8",false,false);
	    assertMove("d9-f7",false,false);
	    assertMove("d9-e8",false,true);
	    assertGameState("rhe1gaehr/4a4/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/4A4/RHE1GAEHR",true,false,false);
	}
	
	@Test
	public void elephantMoveTest1() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    assertMove("c0-e2",true,true); 
	    assertMove("g9-e7",false,true); 
	    assertMove("e2-g4",true,true); 
	    assertMove("e7-g5",false,true); 
	    assertMove("g4-i6",true,false); //red elephant do not cross the river!
	    assertMove("i0-i1",true,true);
	    assertMove("g5-i3",false,false); //black elephant do not cross the river!
	    assertMove("a9-a8",false,true);
	    assertMove("i1-d1",true,true);
	    assertMove("a8-f8",false,true);
	    assertMove("g4-e2",true,true);
	    assertMove("g5-e7",false,true);
	    assertMove("e2-c0",true,false);  
	    assertMove("e2-c4",true,true);
	    assertMove("i6-i5",false,true);
	    assertMove("d1-d3",true,true);
	    assertMove("e7-g9",false,false); 
	    assertMove("i5-i4",false,true);
	    assertMove("c4-e2",true,false);
	    assertMove("c4-a2",true,true);
	    assertMove("e7-g5",false,true);   
	    assertMove("g0-d3",true,false); //too many steps
	    assertMove("d3-d1",true,true);
	    assertGameState("1heaga1hr/5r3/1c5c1/s1s1s1s2/6e2/8s/S1S1S1S1S/EC5C1/3R5/RH1AGAEH1",false,false,false);
	}
	
	@Test
	public void elephantMoveTest2() {
	    startGame("1heaga1hr/5r3/1c5c1/s1s1s1s2/6e2/8s/S1S1S1S1S/EC5C1/3R5/RH1AGAEH1",false);
	    assertMove("h9-i7",false,true);
	    assertMove("d1-f1",true,true);
	    assertMove("f9-e8",false,true);
	    assertMove("g0-e2",true,false); // LEFT_TOP blocked
	    assertGameState("1heag3r/4ar3/1c5ch/s1s1s1s2/6e2/8s/S1S1S1S1S/EC5C1/5R3/RH1AGAEH1",true,false,false);
	}
	
	@Test
	public void horseMoveTest1() {	    
		startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    assertMove("h0-g2",true,true); 
	    assertMove("h9-g7",false,true); 
	    assertMove("g2-h4",true,false); //red horse TOP_RIGHT blocked
	    assertMove("g2-f4",true,false); //red horse TOP_LEFT blocked
	    assertMove("g2-e1",true,true);
	    assertMove("g7-h5",false,false); //black horse RIGHT_BUTTOM blocked
	    assertMove("g7-f5",false,false); //black horse LEFT_BUTTOM blocked
	    assertMove("g7-e8",false,true);
	    assertMove("e1-f4",true,false); //too many steps
	    assertMove("a0-a1",true,true); 
	    assertMove("a9-a8",false,true);
	    assertMove("a1-d1",true,true); 
	    assertMove("a8-d8",false,true); 
	    assertMove("e1-c0",true,false); 
	    assertMove("e1-c2",true,false); 
	    assertMove("e1-d3",true,true); 
	    assertMove("e8-c7",false,false); 
	    assertMove("e8-g7",false,true); 
	    assertGameState("1heagae1r/3r5/1c4hc1/s1s1s1s1s/9/9/S1SHS1S1S/1C5C1/3R5/1HEAGAE1R",true,false,false);	
    }

	@Test
	public void horseMoveTest2() {	
		startGame("1heagae1r/3r5/1c4hc1/s1s1s1s1s/9/9/S1SHS1S1S/1C5C1/3R5/1HEAGAE1R",true);
	    assertMove("d3-e5",true,true); 
	    assertMove("g7-d7",false,false); 
	    assertMove("g7-f5",false,false); 
	    assertMove("g7-h9",false,true); 
	    assertMove("e5-d7",true,false); 
	    assertMove("e5-g6",true,true); 
	    assertMove("h7-h6",false,true); 
	    assertMove("g6-i6",true,false); 
	    assertMove("b0-c2",true,true); 
	    assertMove("h9-g7",false,true); 
	    assertMove("g6-f8",true,false); 
	    assertMove("g6-h8",true,false); 
	    assertMove("c2-b0",true,true); 
	    assertMove("g7-h5",false,false); 
	    assertMove("g7-f5",false,false); 
	    assertGameState("1heagae1r/3r5/1c4h2/s1s1s1Hcs/9/9/S1S1S1S1S/1C5C1/3R5/1HEAGAE1R",false,false,false); 
	}

	@Test
	public void rookMoveTest() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    assertMove("i0-i6",true,false);
	    assertMove("i0-i1",true,true);
	    assertMove("a9-a3",false,false);
	    assertMove("a9-a8",false,true);
	    assertMove("i1-i1",true,false);
	    assertMove("a0-b1",true,false);
	    assertMove("i1-d1",true,true);
	    assertMove("a8-a7",false,true);
	    assertMove("d1-d3",true,true);
	    assertMove("a7-c7",false,false);
	    assertMove("i9-i7",false,true);
	    assertMove("d3-b3",true,false);
	    assertMove("d3-d6",true,true);
	    assertMove("f9-e8",false,true);
	    assertMove("d6-e6",true,true);
	    assertMove("g6-g5",false,true);
	    assertMove("e6-g6",true,true);
	    assertMove("a7-c7",false,false);
	    assertMove("a7-b8",false,false);
	    assertGameState("1heag1eh1/4a4/rc5cr/s1s3R1s/6s2/9/S1S1S1S1S/1C5C1/9/RHEAGAEH1",false,false,false);
	}

	
	@Test
	public void cannonMoveTest() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    assertMove("h2-h7",true,false);
	    assertMove("h2-h8",true,false);
	    assertMove("h2-h6",true,true);
	    assertMove("b7-b2",false,false);
	    assertMove("b7-b1",false,false);
	    assertMove("b7-b3",false,true);	
	    assertMove("h6-f6",true,false);
	    assertMove("h6-g6",true,false);
	    assertMove("h6-d6",false,false);
	    assertMove("h6-e6",true,true);
	    assertMove("h7-g7",false,true);
	    assertMove("e6-h6",true,false);
	    assertMove("e6-e6",true,false);
	    assertMove("e6-f6",true,true);
	    assertMove("b3-c3",false,false);
	    assertMove("b3-c3",false,false);
	    assertMove("b3-g3",false,false);
	    assertMove("b3-f3",false,false);
	    assertMove("b3-e3",false,true);
	    assertMove("b2-c2",true,true);
	    assertMove("g7-c7",false,true);
	    assertMove("c2-c6",true,true);
	    assertMove("c7-c3",false,true);
	    assertGameState("rheagaehr/9/9/s1C2Cs1s/9/9/S1c1c1S1S/9/9/RHEAGAEHR",true,false,false);
	}
	
	@Test
	public void soldierMoveTest() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    assertMove("e3-f3",true,false); //red RIGHT not possible
	    assertMove("e3-e2",true,false); //red BACK not possible
	    assertMove("e3-e4",true,true);
	    assertMove("e6-f6",false,false);
	    assertMove("e6-e7",false,false);
	    assertMove("e6-e5",false,true);
	    assertMove("e4-e5",true,true);
	    assertMove("g6-g5",false,true);
	    assertMove("e5-e6",true,true);
	    assertMove("g5-g4",false,true);
	    assertMove("e6-e7",true,true);
	    assertMove("g4-f4",false,true);
	    assertGameState("rheagaehr/9/1c2S2c1/s1s5s/9/5s3/S1S3S1S/1C5C1/9/RHEAGAEHR",true,false,false);
	}
	
	@Test
	public void checkTestRed() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    assertMove("i0-i1",true,true);
	    assertMove("i6-i5",false,true);
	    assertMove("i1-f1",true,true);
	    assertMove("i5-i4",false,true);
	    assertMove("f1-f9",true,true);
	    assertMove("e9-e8",false,true);
	    assertMove("f9-g9",true,true);
	    assertMove("e8-e7",false,true);
	    assertMove("g9-h9",true,true);
	    assertMove("e7-e8",false,true);
	    assertMove("h9-i9",true,true);
	    assertMove("e8-f8",false,true);
	    assertMove("a0-a1",true,true);
	    assertMove("a9-a7",false,true);
	    assertMove("a1-d1",true,true);
	    assertMove("i4-i3",false,true);
	    assertMove("i9-i8",true,true);
	    assertMove("f8-f9",false,true);
	    assertMove("d1-d9",true,true);
	    assertMove("g6-g5",false,false); //try a move after game end
	    assertGameState("1heR1g3/8R/rc5c1/s1s1s1s2/9/9/S1S1S1S1s/1C5C1/9/1HEAGAEH1",true,true,true);
	}
	
	@Test
	public void checkTestBlack() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    assertMove("i3-i4",true,true);
	    assertMove("i9-i8",false,true);
	    assertMove("i4-i5",true,true);
	    assertMove("i8-f8",false,true);
	    assertMove("i5-i6",true,true);
	    assertMove("f8-f0",false,true);
	    assertGameState("rheagaeh1/9/1c5c1/s1s1s1s1S/9/9/S1S1S1S2/1C5C1/9/RHEAGrEHR",true,false,false);
	}
	
	@Test
	public void moveIntoCheckBlackTest() {
		startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    assertMove("i0-i1",true,true);
	    assertMove("e9-e8",false,true);
	    assertMove("i1-f1",true,true);
	    assertMove("e8-f8",false,false);
	    assertGameState("rhea1aehr/4g4/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/5R3/RHEAGAEH1",false,false,false);
	}
	
	@Test
	public void moveIntoCheckRedTest() {
		startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    assertMove("i6-i5",true,false); //move enemy piece
	    assertMove("e0-e1",true,true);
	    assertMove("i9-i8",false,true);
	    assertMove("i0-i1",true,true);
	    assertMove("i8-f8",false,true);
	    assertMove("e1-f1",true,false);
	    assertMove("h1-g1",true,false); //empty startfield
	    assertGameState("rheagaeh1/5r3/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/4G3R/RHEA1AEH1",true,false,false);
	}
}
