package com.test.testris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int[][] iBlock1 = new int[][]{

            {1, 1, 1, 1},
            {0, 0, 0, 0}
    };
    private int[][] iBlock2 = new int[][]{
            {1, 0},
            {1, 0},
            {1, 0},
            {1, 0}

    };

    private int[][] zBlock1 = new int[][]{
            {2, 2, 0},
            {0, 2, 2}
    };

    private int[][] zBlock2 = new int[][]{
            {0, 2},
            {2, 2},
            {2, 0}
    };

    private int[][] sBlock1 = new int[][]{
            {0, 3, 3},
            {3, 3, 0}
    };

    private int[][] sBlock2 = new int[][]{
            {3, 0},
            {3, 3},
            {0, 3}
    };

    private int[][] lBlock1 = new int[][]{
            {0, 0, 4},
            {4, 4, 4}
    };

    private int[][] lBlock2 = new int[][]{
            {4, 4},
            {0, 4},
            {0, 4}
    };

    private int[][] lBlock3 = new int[][]{
            {4, 4, 4},
            {4, 0, 0}
    };

    private int[][] lBlock4 = new int[][]{
            {4, 0},
            {4, 0},
            {4, 4}
    };

    private int[][] jBlock1 = new int[][]{
            {5, 0, 0},
            {5, 5, 5}
    };

    private int[][] jBlock2 = new int[][]{
            {0, 5},
            {0, 5},
            {5, 5}
    };

    private int[][] jBlock3 = new int[][]{
            {5, 5, 5},
            {0, 0, 5}
    };

    private int[][] jBlock4 = new int[][]{
            {5, 5},
            {5, 0},
            {5, 0}
    };

    private int[][] tBlock1 = new int[][]{
            {6, 6, 6},
            {0, 6, 0}
    };

    private int[][] tBlock2 = new int[][]{
            {6, 0},
            {6, 6},
            {6, 0}
    };

    private int[][] tBlock3 = new int[][]{
            {0, 6, 0},
            {6, 6, 6}
    };

    private int[][] tBlock4 = new int[][]{
            {0, 6},
            {6, 6},
            {0, 6}
    };

    private int[][] oBlock = new int[][]{
            {7, 7},
            {7, 7}
    };

    private int[][][] blocks = {
            iBlock1,
            sBlock1,
            jBlock1,
            lBlock1,
            tBlock1,
            oBlock,
            zBlock1,
            zBlock2
    };

    private int[][] mGameBoard = new int[18][12]; //18행 12열짜리 이중배열 0으로 초기화되어 있음
    TetrisView tetrisView;
    Random random = new Random();
    private TimerTask mTask;
    private Timer mTimer;
    private int row = mGameBoard.length; //블록의 좌상단 처음 시작하는 부분
    private int col = 4;
    private int[][] curBlock;


    private void startTimer(int period) {
        stopTimer();
        //row = mGameBoard.length;
        mTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onTimer();
                    }
                });
            }
        };

        mTimer = new Timer();

        mTimer.schedule(mTask, period, period);
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private boolean checkBlow() {
        boolean check = true;
        for (int i = 0; i < curBlock[0].length; i++) {
            if (curBlock[0][i] * mGameBoard[row - 1][col + i] != 0) {
                check = false;
            }
        }
        return check;
    }

    private boolean checkLeft() {
        boolean check = true;
        for (int i = 0; i < curBlock.length; i++) {
                if (curBlock[i][0] * mGameBoard[row+i][col] != 0) {
                    check = false;
                }
        }
        return check;
    }

    private boolean checkRight(){
        boolean check = true;
        for(int i = 0; i < curBlock.length; i++){
            if(curBlock[i][curBlock[0].length-1] * mGameBoard[row][col+curBlock[0].length] !=0){
                check = false;
            }
        }
        return check;
    }

    private void onTimer() {
        if (row > 0) {
            if(checkBlow()){
                row--;
            }else{
                stitchBlock();
                setRandomBlock();
                row = mGameBoard.length;
                col = 4;
            }
        }else{
            stitchBlock();
            setRandomBlock();
            row = mGameBoard.length;
            col = 4;
        }
        tetrisView.setMoveBlock(col, row, curBlock);
        tetrisView.invalidate();
    }

    private void setRandomBlock() {
        int i = random.nextInt(7);
        curBlock = blocks[i];
    }

    private void stitchBlock() {
        for (int i = 0; i < curBlock.length; i++) {
            for (int j = 0; j < curBlock[i].length; j++) {
                if (mGameBoard[row + i][col + j] == 0) {
                    mGameBoard[row + i][col + j] = curBlock[i][j];
                }
            }
        }
        curBlock = null;
    }

    //
    //***************************//
    //여기서 시작
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tetrisView = (TetrisView) findViewById(R.id.tetrisView);
        tetrisView.setGameBoard(mGameBoard);
    }

    @Override
    protected void onResume() {
        if (curBlock == null) {
            setRandomBlock();
        }
        startTimer(1000);
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopTimer();
        super.onPause();
    }

    @Override
    //***************************//
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonL: //Left
                if (col > 0) {
                    if(checkLeft()){
                        col--;
                        tetrisView.setMoveBlock(col, row, curBlock);
                    }else{
                        tetrisView.setMoveBlock(col, row, curBlock);
                    }
                }
                break;
            case R.id.buttonR: //Right
                if (col + curBlock[0].length < 12) {
                    if (checkRight()) {
                        col++;
                        tetrisView.setMoveBlock(col,row, curBlock);
                    } else {
                        tetrisView.setMoveBlock(col, row, curBlock);
                    }
                }
                break;

            case R.id.buttonU: //Up
///////////////////////////////////////////
                break;

            case R.id.buttonD: //Down
                tetrisView.setMoveBlock(5, 13, new int[][]{
                        {3, 3, 0},
                        {0, 3, 3}
                });
                break;

            case R.id.buttonEnter:
                if (mTimer != null) {
                    onPause();
                } else {
                    onResume();
                }
                break;
        }
        tetrisView.invalidate();
    }



    //***************************//
    // 타이머




//    private void checkBoard(int [][] curBlock){
//        int r = curBlock.length;
//        int c = curBlock[r].length;
//
//        for(int i = 0; i < r; i++) {
//            if(curBlock[r][i]!=0 && mGameBoard)
//        }
//    }



 //   private boolean checkBoard() {
//        boolean check = true;
//
//            for (int i = 0; i < curBlock.length; i++) {
//                for (int j = 0; j < curBlock[0].length; j++) {
//                    if (curBlock[i][0] != 0 && col > 0) {
//                        if (mGameBoard[row + i][col - 1] != 0) {
//                            check = false;
//                        }
//                    } else if (curBlock[curBlock.length-1][j] != 0) {
//                        if (mGameBoard[row + curBlock.length][col + j] != 0) {
//                            check = false;
//                        }
//                    } else if (curBlock[i][col] !=0 && col < mGameBoard.length){
//                        if (mGameBoard[row+i][col+curBlock[0].length]!=0){
//                            check = false;
//                        }
//                    }
//                }
//            }
//        return check;
//    }


//                    if(row-1>=0){
//                        if (curBlock[i][j] * mGameBoard[row - 1 + i][col + j] != 0) {
//                            stitchBlock();
//                            setRandomBlock();
//                            row = mGameBoard.length;
//                            col = 4;
//                        }
//                    }
}
