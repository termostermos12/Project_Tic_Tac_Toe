package com.example.xoproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private Button[] buttons = new Button[9];
    private String[] board = new String[9];
    private String currentPlayer = "X";
    private String playerSymbol = "X";
    private String aiSymbol = "O";
    private int gameCount = 1;
    private int totalGames = 5;
    private int xWins = 0;
    private int oWins = 0;
    private int draws = 0;
    private boolean gameOver = false;

    private TextView gameTitle, gameStatus, textScoreX, textScoreO, textNull, textPartie;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        if (intent != null) {
            playerSymbol = intent.getStringExtra("symbol");
            totalGames = intent.getIntExtra("rounds", 5);
            aiSymbol = playerSymbol.equals("X") ? "O" : "X";
        }

        initializeUI();
        initializeGame();
        setupButtonListeners();
    }

    private void initializeUI() {
        gameTitle = findViewById(R.id.gameTitle);
        gameStatus = findViewById(R.id.gameStatus);
        textScoreX = findViewById(R.id.textScoreX);
        textScoreO = findViewById(R.id.textScoreO);
        textNull = findViewById(R.id.textNull);
        textPartie = findViewById(R.id.textPartie);
        resetButton = findViewById(R.id.resetButton);

        buttons[0] = findViewById(R.id.b0);
        buttons[1] = findViewById(R.id.b1);
        buttons[2] = findViewById(R.id.b2);
        buttons[3] = findViewById(R.id.b3);
        buttons[4] = findViewById(R.id.b4);
        buttons[5] = findViewById(R.id.b5);
        buttons[6] = findViewById(R.id.b6);
        buttons[7] = findViewById(R.id.b7);
        buttons[8] = findViewById(R.id.b8);

        updateGameTitle();
        resetButton.setOnClickListener(v -> resetGame());
    }

    private void initializeGame() {
        for (int i = 0; i < 9; i++) {
            board[i] = "";
            buttons[i].setText("");
            buttons[i].setEnabled(true);
        }
        currentPlayer = "X";
        gameOver = false;
        updateGameStatus();
    }

    private void setupButtonListeners() {
        for (int i = 0; i < 9; i++) {
            int index = i;
            buttons[i].setOnClickListener(v -> onCellClick(index));
        }
    }

    private void onCellClick(int index) {
        if (board[index].isEmpty() && !gameOver && currentPlayer.equals(playerSymbol)) {
            board[index] = playerSymbol;
            buttons[index].setText(playerSymbol);
            buttons[index].setEnabled(false);

            if (checkWinner(playerSymbol)) {
                endGame(playerSymbol + " wins!");
                xWins++;
                return;
            }

            if (isBoardFull()) {
                endGame("Draw!");
                draws++;
                return;
            }

            currentPlayer = aiSymbol;
            updateGameStatus();
            makeAIMove();
        }
    }

    private void makeAIMove() {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < 9; i++) {
            if (board[i].isEmpty()) {
                board[i] = aiSymbol;
                int score = minimax(0, false);
                board[i] = "";

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }

        if (bestMove != -1) {
            board[bestMove] = aiSymbol;
            buttons[bestMove].setText(aiSymbol);
            buttons[bestMove].setEnabled(false);

            if (checkWinner(aiSymbol)) {
                endGame(aiSymbol + " wins!");
                oWins++;
                return;
            }

            if (isBoardFull()) {
                endGame("Draw!");
                draws++;
                return;
            }

            currentPlayer = playerSymbol;
            updateGameStatus();
        }
    }

    private int minimax(int depth, boolean isMaximizing) {
        String winner = getWinner();

        if (winner != null) {
            if (winner.equals(aiSymbol)) return 10 - depth;
            if (winner.equals(playerSymbol)) return depth - 10;
            return 0;
        }

        if (isBoardFull()) return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i].isEmpty()) {
                    board[i] = aiSymbol;
                    bestScore = Math.max(bestScore, minimax(depth + 1, false));
                    board[i] = "";
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i].isEmpty()) {
                    board[i] = playerSymbol;
                    bestScore = Math.min(bestScore, minimax(depth + 1, true));
                    board[i] = "";
                }
            }
            return bestScore;
        }
    }

    private boolean checkWinner(String player) {
        for (int i = 0; i < 3; i++) {
            if (board[i*3].equals(player) && board[i*3+1].equals(player) && board[i*3+2].equals(player)) {
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (board[i].equals(player) && board[i+3].equals(player) && board[i+6].equals(player)) {
                return true;
            }
        }
        if (board[0].equals(player) && board[4].equals(player) && board[8].equals(player)) {
            return true;
        }
        if (board[2].equals(player) && board[4].equals(player) && board[6].equals(player)) {
            return true;
        }
        return false;
    }

    private String getWinner() {
        if (checkWinner(playerSymbol)) return playerSymbol;
        if (checkWinner(aiSymbol)) return aiSymbol;
        return null;
    }

    private boolean isBoardFull() {
        for (String cell : board) {
            if (cell.isEmpty()) return false;
        }
        return true;
    }

    private void endGame(String message) {
        gameOver = true;
        gameStatus.setText(message);

        for (Button button : buttons) {
            button.setEnabled(false);
        }

        updateScores();

        if (gameCount < totalGames) {
            resetButton.setText("NEXT GAME");
            resetButton.setOnClickListener(v -> {
                gameCount++;
                updateGameTitle();
                initializeGame();
                setupButtonListeners();
                resetButton.setText("NEW GAME");
            });
        } else {
            resetButton.setText("TOURNAMENT FINISHED");
            resetButton.setOnClickListener(v -> goToResults());
        }
    }

    private void resetGame() {
        gameCount = 1;
        xWins = 0;
        oWins = 0;
        draws = 0;
        updateGameTitle();
        initializeGame();
        setupButtonListeners();
        resetButton.setText("NEW GAME");
        resetButton.setOnClickListener(v -> resetGame());
    }

    private void updateGameStatus() {
        gameStatus.setText("Current Turn: " + currentPlayer);
    }

    private void updateGameTitle() {
        gameTitle.setText("TIC TAC TOE");
        textPartie.setText("Game " + gameCount + " of " + totalGames);
    }

    private void updateScores() {
        textScoreX.setText(String.valueOf(xWins));
        textScoreO.setText(String.valueOf(oWins));
        textNull.setText(String.valueOf(draws));
    }

    private void goToResults() {
        Intent intent = new Intent(GameActivity.this, ResultActivity.class);
        intent.putExtra("xWins", xWins);
        intent.putExtra("oWins", oWins);
        intent.putExtra("draws", draws);
        startActivity(intent);
        finish();
    }
}