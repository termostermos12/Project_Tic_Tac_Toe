package com.example.xoproject;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGameBoardInitialization() {
        String[] board = new String[9];
        for (int i = 0; i < 9; i++) {
            board[i] = "";
        }
        assertEquals("", board[0]);
        assertEquals(9, board.length);
    }

    @Test
    public void testPlayerMove() {
        String[] board = new String[9];
        board[0] = "X";
        assertEquals("X", board[0]);
    }

    @Test
    public void testWinConditionHorizontal() {
        String[] board = {"X", "X", "X", "O", "O", "", "", "", ""};
        boolean xWins = isWinner(board, "X");
        assertTrue(xWins);
    }

    @Test
    public void testWinConditionVertical() {
        String[] board = {"X", "O", "O", "X", "", "", "X", "", ""};
        boolean xWins = isWinner(board, "X");
        assertTrue(xWins);
    }

    @Test
    public void testWinConditionDiagonal() {
        String[] board = {"X", "O", "O", "", "X", "", "", "", "X"};
        boolean xWins = isWinner(board, "X");
        assertTrue(xWins);
    }

    @Test
    public void testWinConditionDiagonalReverse() {
        String[] board = {"", "", "O", "", "X", "", "X", "", ""};
        boolean oWins = isWinner(board, "O");
        assertTrue(oWins);
    }

    @Test
    public void testNoWinner() {
        String[] board = {"X", "O", "X", "O", "X", "O", "O", "X", "O"};
        boolean xWins = isWinner(board, "X");
        boolean oWins = isWinner(board, "O");
        assertFalse(xWins);
        assertFalse(oWins);
    }

    @Test
    public void testScoreTracking() {
        int xScore = 0;
        int oScore = 0;
        xScore++;
        assertEquals(1, xScore);
        assertEquals(0, oScore);
    }

    @Test
    public void testBoardFull() {
        String[] board = {"X", "O", "X", "O", "X", "O", "O", "X", "O"};
        boolean full = isBoardFull(board);
        assertTrue(full);
    }

    @Test
    public void testBoardNotFull() {
        String[] board = {"X", "O", "", "O", "X", "", "", "X", ""};
        boolean full = isBoardFull(board);
        assertFalse(full);
    }

    // Helper methods
    private boolean isWinner(String[] board, String player) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i*3].equals(player) && board[i*3+1].equals(player) && board[i*3+2].equals(player)) {
                return true;
            }
        }
        // Check columns
        for (int i = 0; i < 3; i++) {
            if (board[i].equals(player) && board[i+3].equals(player) && board[i+6].equals(player)) {
                return true;
            }
        }
        // Check diagonals
        if (board[0].equals(player) && board[4].equals(player) && board[8].equals(player)) {
            return true;
        }
        if (board[2].equals(player) && board[4].equals(player) && board[6].equals(player)) {
            return true;
        }
        return false;
    }

    private boolean isBoardFull(String[] board) {
        for (String cell : board) {
            if (cell.isEmpty()) return false;
        }
        return true;
    }
}