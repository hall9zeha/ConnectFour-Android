package com.barryzea.connectfour;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.barryzea.connectfour.common.Constants;
import com.barryzea.connectfour.common.MCallback;
import com.barryzea.connectfour.common.Utils;
import com.barryzea.connectfour.databinding.ActivityMainBinding;
import com.barryzea.connectfour.entities.Piece;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding bind;
    private Piece[][] arrayPieces;
    private LinearLayout lnColumn;
    private boolean switchTurn = true;
    private final int ROWS = 6;
    private final int COLUMNS = 7;
    private int currentPlayColor = 0;
    private boolean beginPiecesTurn = true;
    private int MY_PLAYER_SCORE = 0;
    private int OTHER_PLAYER_SCORE = 0;
    private ArrayList<ArrayList<Pair<Integer, Integer>>> winningCoord = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Base_Theme_ConnectFour);
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        populateGameBoard();
        setUpListeners();
    }

    private void populateGameBoard() {
        loadOrClearScore(true);
        bind.lnContent.removeAllViews();
        if (lnColumn != null) lnColumn.removeAllViews();
        arrayPieces = new Piece[COLUMNS][ROWS];

        for (int i = 0; i < COLUMNS; i++) {
            lnColumn = Utils.createLinearLayout(MainActivity.this);
            for (int j = 0; j < ROWS; j++) {
                ImageView iv = Utils.createImageviewPiece(MainActivity.this, i, j, this::setPieceOnClickListener);
                lnColumn.addView(iv);
                addImageViewToArray(i, j, iv);
            }
            bind.lnContent.addView(lnColumn);
        }
    }

    private void loadOrClearScore(boolean load) {
        if (load) {
            bind.btnMyPlayer.setIconResource(R.drawable.green_circle);
            bind.btnOtherPlayer.setIconResource(R.drawable.yellow_circle);
        } else {
            MY_PLAYER_SCORE = 0;
            OTHER_PLAYER_SCORE = 0;
        }
        bind.btnMyPlayer.setText(String.valueOf(MY_PLAYER_SCORE));
        bind.btnOtherPlayer.setText(String.valueOf(OTHER_PLAYER_SCORE));
        //cargamos por primera vez el estado de los botones que corresponden a cada jugador
        bind.btnMyPlayer.setEnabled(switchTurn);
        bind.btnOtherPlayer.setEnabled(!switchTurn);
    }

    private void addImageViewToArray(int column, int row, ImageView iv) {
        arrayPieces[column][row] = new Piece(iv);
    }

    private void setPieceOnClickListener(String tag) {
        int column = Integer.parseInt(tag.substring(0,tag.indexOf(",")));
        Log.e("COLUMN_TAG", column + "");
        for(int i=ROWS-1; i>-1; i--){
            if(arrayPieces[column][i].getColor()==Constants.WITHOUT_COLOR){
                setColorPiece(column,i);
                checkWinner();
                break;
            }
        }
        updateGameBoard(column);
    }

    private void setUpListeners() {
        bind.btnResetGame.setOnClickListener(v -> {
            Utils.showResetDialog(this, getString(R.string.resetGameMsg), () -> {
                currentPlayColor = 0;
                switchTurn = true;
                populateGameBoard();
            });
        });
        bind.btnResetStatistics.setOnClickListener(v -> {
            Utils.showResetDialog(this, getString(R.string.resetScore), () -> {
                loadOrClearScore(false);
            });
        });
    }

    private void showGameOverDialog(int colorWinn, int victories) {
        for (ArrayList<Pair<Integer, Integer>> pairs : winningCoord) {
            if (pairs.size() >= 4) {
                for (Pair<Integer, Integer> pair : pairs) {
                    LinearLayout lnCol = (LinearLayout) bind.lnContent.getChildAt(pair.first);
                    ImageView iv = (ImageView) lnCol.getChildAt(pair.second);
                    iv.setBackgroundResource(R.drawable.bg_win_piece);
                }
            }
        }
        Utils.postDelay(1100, () -> Utils.gameOverDialog(this, colorWinn, victories, () -> {
            populateGameBoard();
            loadOrClearScore(true);
            beginPiecesTurn = !beginPiecesTurn;
            switchTurn = beginPiecesTurn;
        }));
    }

    private void updateGameBoard(int column) {
        LinearLayout lnCol = (LinearLayout) bind.lnContent.getChildAt(column);
        long count = 1;
        int colorRes = !switchTurn ? R.drawable.green_circle : R.drawable.yellow_circle;
        for (int i = 0; i < ROWS; i++) {
            ImageView iv = (ImageView) lnCol.getChildAt(i);
            iv.setPadding(8, 8, 8, 8);
            int color = arrayPieces[column][i].getColor();

            Utils.postDelay(count * 90, () -> {
                if (color == Constants.WITHOUT_COLOR) iv.setImageResource(colorRes);
            });
            Utils.postDelay(count * 110, () -> {
                if (color == Constants.WITHOUT_COLOR) iv.setImageResource(0);
            });
            Utils.postDelay(count * 110, () -> {
                switch (color){
                    case Constants.MY_PLAYER_COLOR:
                        iv.setImageResource(R.drawable.green_circle);
                        break;
                    case Constants.OTHER_PLAYER_COLOR:
                        iv.setImageResource(R.drawable.yellow_circle);
                        break;
                }

            });
            count++;
        }
    }

    private void setColorPiece(int column, int row) {
        if (switchTurn) {
            arrayPieces[column][row].setColor(Constants.MY_PLAYER_COLOR);
            changeTurn();
        } else {
            arrayPieces[column][row].setColor(Constants.OTHER_PLAYER_COLOR);
            changeTurn();
        }
    }

    private void changeTurn() {
        currentPlayColor = switchTurn ? Constants.MY_PLAYER_COLOR : Constants.OTHER_PLAYER_COLOR;
        switchTurn = !switchTurn;
        bind.btnMyPlayer.setEnabled(switchTurn);
        bind.btnOtherPlayer.setEnabled(!switchTurn);
    }

    private void checkWinner() {
        boolean winn = false;
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++) {
                if (checkEachDirections(i, j)) {
                    winn = true;
                    break;
                }
            }
        }
        if(winn){
            switch (currentPlayColor){
                case Constants.MY_PLAYER_COLOR:{
                    MY_PLAYER_SCORE++;
                    showGameOverDialog(currentPlayColor,MY_PLAYER_SCORE);
                }
                break;
                case Constants.OTHER_PLAYER_COLOR:{
                    OTHER_PLAYER_SCORE++;
                    showGameOverDialog(currentPlayColor,OTHER_PLAYER_SCORE);
                }
                break;
            }
        }
        winningCoord.clear();

    }
    private boolean checkEachDirections(int col, int row){
        return (checkCoordinates(col,row,1,0) ||
        checkCoordinates(col,row,-1,0) ||
        checkCoordinates(col,row,0,1) ||
        checkCoordinates(col,row,0,-1) ||
        checkCoordinates(col,row,1,1) ||
        checkCoordinates(col,row,1,-1) ||
        checkCoordinates(col,row,-1,1) ||
        checkCoordinates(col,row,-1,-1));
    }
    private boolean checkCoordinates(int col, int row, int col1, int row1){
        ArrayList<Pair<Integer, Integer>> coordsList= new ArrayList<>();
        coordsList.add(new Pair<>(col, row));
        for(int i=1; i<4; i++ ){
            if(checkNextPiece(col,row,col+(i*col1),row+(i*row1)))coordsList.add(new Pair<>(col+(i*col1),row+(i*row1)));
            if(!checkNextPiece(col,row,col+(i*col1),row+(i*row1))) break;
            else if(i>=3){
                winningCoord.add(coordsList);
                return true;
            }
        }
        return false;
    }
    private boolean checkNextPiece(int col, int row, int col1, int row1){
        return(checkIfNotOutIndexRange(col1,row1) && arrayPieces[col][row].getColor()==arrayPieces[col1][row1].getColor());
    }
    private boolean checkIfNotOutIndexRange(int col, int row){
         try{return arrayPieces[col][row].getColor()==currentPlayColor;}catch(ArrayIndexOutOfBoundsException e){return false;}

    }
}