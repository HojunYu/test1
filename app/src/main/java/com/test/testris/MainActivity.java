package com.test.testris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int[][] iBlock = new int[][]{

            {1, 1, 1, 1}
    };

    private int[][] zBlock = new int[][]{
            {2, 2, 0},
            {0, 2, 2}
    };

    private int[][] sBlock = new int[][]{
            {0, 3, 3},
            {3, 3, 0}
    };

    private int[][] lBlock = new int[][]{
            {0, 0, 4},
            {4, 4, 4}
    };

    private int[][] jBlock = new int[][]{
            {5, 0, 0},
            {5, 5, 5}
    };

    private int[][] tBlock = new int[][]{
            {6, 6, 6},
            {0, 6, 0}
    };

    private int[][] oBlock = new int[][]{
            {7, 7},
            {7, 7}
    };

    private int[][][] blocks = {
            iBlock,
            sBlock,
            jBlock,
            lBlock,
            tBlock,
            oBlock,
            zBlock,
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

    private boolean checkMove(int row, int col, boolean rotate) {
        boolean check = true;

        int[][] block = curBlock;



        if( row < 0) {
            check = false;

        }
        if(col + block[0].length > mGameBoard[0].length  ) {
            check =  false;
        }

        if (rotate) {
            block = rotateBlock(block);
        }

        for (int i = 0; i < block.length; i++) {
            if (i + row < mGameBoard.length && i+row >= 0) {
                for (int j = 0; j < block[i].length; j++) {
                    if (block[i][j] * mGameBoard[row + i][col + j] != 0) {
                        check = false;
                    }
                }
            }
        }
        return check;
    }

    private int[][] rotateBlock(int[][] block) {
        int[][] newBlock = new int[block[0].length][block.length];

        for (int y = 0; y < newBlock.length; y++) {
            for (int x = 0; x < newBlock[y].length; x++) {
                newBlock[y][x] = block[x][newBlock.length - y - 1];
            }
        }

        if(col + newBlock[0].length <= mGameBoard[0].length){
            return newBlock;
        }else{
            return block;
        }
    }



    private void onTimer() {
        if (row > 0) {
            if (checkMove(row - 1, col, false)) {
                row--;

            } else {
                stitchBlock();
                clearLine();
                setRandomBlock();
                row = mGameBoard.length;
                col = 4;
            }
        } else {
            stitchBlock();
            clearLine();
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

    private void clearLine(){
        int count = 0;
        int score = 0;
        for(int i = 0; i< mGameBoard.length; i++){

            for (int j = 0; j < mGameBoard[i].length; j++) {
                if (mGameBoard[i][j] != 0) {
                    count++;
                }
            }
            int k;
            if(count == 12 ){
                for(k = i+1; k < mGameBoard.length ; k++) {
                    for (int j = 0; j < mGameBoard[i].length; j++) {
                        mGameBoard[k-1][j] = mGameBoard[k][j];
                        count = 0;
                        score ++;
                    }
                }
            }else{
                count = 0;
            }
        }
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
                    if (checkMove(row, col - 1, false)) {
                        col--;
                        tetrisView.setMoveBlock(col, row, curBlock);
                    } else {
                        tetrisView.setMoveBlock(col, row, curBlock);
                    }
                }
                break;
            case R.id.buttonR: //Right
                if (col + curBlock[0].length < 12) {
                    if (checkMove(row, col + 1, false)) {
                        col++;
                        tetrisView.setMoveBlock(col, row, curBlock);
                    } else {
                        tetrisView.setMoveBlock(col, row, curBlock);
                    }
                }
                break;

            case R.id.buttonU: //Up
                if (checkMove(row, col, true)) {
                    curBlock = rotateBlock(curBlock);
                    if(col + curBlock[0].length>mGameBoard[0].length){
                        col = col-(col+curBlock[0].length - mGameBoard[0].length);
                    }
                    tetrisView.setMoveBlock(col, row, curBlock);
                }
///////////////////////////////////////////
                break;

            case R.id.buttonD: //Down
                if (row > 0) {
                    if (checkMove(row - 1, col, false)) {
                        row--;
                        tetrisView.setMoveBlock(col, row, curBlock);
                    } else {
                        tetrisView.setMoveBlock(col, row, curBlock);
                    }
                } else {
                    stitchBlock();
                    clearLine();
                    setRandomBlock();
                    row = mGameBoard.length;
                    col = 4;
                }
                break;

            case R.id.buttonEnter:
                if (row > 0) {
                    int r = row;
                    while (checkMove(--r, col, false) == true) ;
                    row = r + 1;
                } else {
                    stitchBlock();
                    clearLine();
                    setRandomBlock();
                    row = mGameBoard.length;
                    col = 4;
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
