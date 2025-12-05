package com.example.xoproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private TextView resultText, winnerText, xWinsText, oWinsText, drawsText;
    private Button saveBtn, homeBtn;
    private int xWins, oWins, draws;
    private boolean isLastTournament = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initializeUI();
        getIntentData();
        displayResults();
        setupButtons();
    }

    private void initializeUI() {
        resultText = findViewById(R.id.resultText);
        winnerText = findViewById(R.id.winnerText);
        xWinsText = findViewById(R.id.xWinsText);
        oWinsText = findViewById(R.id.oWinsText);
        drawsText = findViewById(R.id.drawsText);
        saveBtn = findViewById(R.id.saveBtn);
        homeBtn = findViewById(R.id.homeBtn);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            xWins = intent.getIntExtra("xWins", 0);
            oWins = intent.getIntExtra("oWins", 0);
            draws = intent.getIntExtra("draws", 0);
            isLastTournament = intent.getBooleanExtra("isLastTournament", false);
        }
    }

    private void displayResults() {
        xWinsText.setText(String.valueOf(xWins));
        oWinsText.setText(String.valueOf(oWins));
        drawsText.setText(String.valueOf(draws));

        String winner;
        if (xWins > oWins) {
            winner = "PLAYER X";
        } else if (oWins > xWins) {
            winner = "PLAYER O";
        } else {
            winner = "IT'S A TIE";
        }

        winnerText.setText(winner);

        if (isLastTournament) {
            saveBtn.setText("BACK TO HOME");
            saveBtn.setOnClickListener(v -> goHome());
            homeBtn.setVisibility(View.GONE);
        }
    }

    private void setupButtons() {
        if (!isLastTournament) {
            saveBtn.setOnClickListener(v -> saveTournament());
            homeBtn.setOnClickListener(v -> goHome());
        }
    }

    private void saveTournament() {
        SharedPreferences prefs = getSharedPreferences("tournament_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("x_wins", xWins);
        editor.putInt("o_wins", oWins);
        editor.putInt("draws", draws);
        editor.putLong("timestamp", System.currentTimeMillis());
        editor.apply();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tournament Saved")
                .setMessage("Tournament results have been saved successfully!")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    goHome();
                })
                .show();
    }

    private void goHome() {
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}