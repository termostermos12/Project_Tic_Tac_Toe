package com.example.xoproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private RadioGroup radioSymbols;
    private RadioButton radioX, radioO;
    private Spinner spinnerRounds;
    private Button btnPlay, btnPrinciple, btnLastScore;
    private String selectedSymbol = "X";
    private int selectedRounds = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        setupSpinner();
        setupRadioGroup();
        setupButtons();
    }

    private void initializeUI() {
        radioSymbols = findViewById(R.id.radioSymbols);
        radioX = findViewById(R.id.radioX);
        radioO = findViewById(R.id.radioO);
        spinnerRounds = findViewById(R.id.spinnerRounds);
        btnPlay = findViewById(R.id.btnPlay);
        btnPrinciple = findViewById(R.id.btnPrinciple);
        btnLastScore = findViewById(R.id.btnLastScore);
    }

    private void setupSpinner() {
        Integer[] roundOptions = {1, 3, 5, 7, 9};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                roundOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRounds.setAdapter(adapter);
        spinnerRounds.setSelection(2);
    }

    private void setupRadioGroup() {
        radioX.setChecked(true);
        selectedSymbol = "X";

        radioSymbols.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioX) {
                selectedSymbol = "X";
            } else if (checkedId == R.id.radioO) {
                selectedSymbol = "O";
            }
        });
    }

    private void setupButtons() {
        btnPlay.setOnClickListener(v -> startGame());
        btnPrinciple.setOnClickListener(v -> showHowToPlay());
        btnLastScore.setOnClickListener(v -> showLastTournament());
    }

    private void startGame() {
        selectedRounds = (Integer) spinnerRounds.getSelectedItem();

        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("symbol", selectedSymbol);
        intent.putExtra("rounds", selectedRounds);

        saveGamePreferences();
        startActivity(intent);
    }

    private void saveGamePreferences() {
        SharedPreferences prefs = getSharedPreferences("game_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("last_symbol", selectedSymbol);
        editor.putInt("last_rounds", selectedRounds);
        editor.apply();
    }

    private void showHowToPlay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How to Play")
                .setMessage("Tic Tac Toe Rules:\n\n" +
                        "1. You and the AI take turns marking spaces\n" +
                        "2. The first player to get 3 marks in a row (horizontally, vertically, or diagonally) wins\n" +
                        "3. If all spaces are filled and no one has won, the game is a draw\n" +
                        "4. Play the specified number of games in tournament mode\n" +
                        "5. The player with the most wins wins the tournament")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showLastTournament() {
        SharedPreferences prefs = getSharedPreferences("tournament_data", MODE_PRIVATE);
        int xWins = prefs.getInt("x_wins", -1);

        if (xWins == -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Tournament Data")
                    .setMessage("No previous tournament data found.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            int oWins = prefs.getInt("o_wins", 0);
            int draws = prefs.getInt("draws", 0);

            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("xWins", xWins);
            intent.putExtra("oWins", oWins);
            intent.putExtra("draws", draws);
            intent.putExtra("isLastTournament", true);

            startActivity(intent);
        }
    }
}