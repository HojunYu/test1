package com.test.testris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final static int SIZE_Y = 18;
    final static int SIZE_X = 12;

    private int[][] mGameBoard = new int[SIZE_Y][SIZE_X];
    TetrisView tetrisView;
    Random random = new Random();
    private TimerTask mTask;
    private Timer mTimer;

    private int mPosX;
    private int mPosY;
    private int[][] mMovingBlock;


    static final int BLOCK_SHAPES[][][] = {
            {
                    {1, 1, 1},
                    {0, 1, 0}

            },
            {
                    {2, 2, 2},
                    {0, 0, 2}

            },
            {
                    {3, 3, 3},
                    {3, 0, 0}

            },
            {
                    {4, 4, 4, 4}
            },
            {
                    {5, 5},
                    {5, 5}

            },
            {
                    {6, 6, 0},
                    {0, 6, 6}

            },
            {
                    {0, 7, 7},
                    {7, 7, 0}

            },
    };


    //***************************//
    //여기서 시작
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tetrisView = (TetrisView) findViewById(R.id.tetrisView);
        tetrisView.setGameBoard(mGameBoard);

        mMovingBlock = newBlock();
        mPosX = SIZE_X / 2;
        mPosY = SIZE_Y - 1;

    }

    @Override
    protected void onPause() {
        stopTimer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        startTimer(1000);
        super.onResume();
    }

    @Override
    //***************************//
    public void onClick(View view) {
        int dx = 0, dy = 0;
        boolean rotate = false;
        switch (view.getId()) {
            case R.id.buttonL: //Left
                dx = -1;
                break;
            case R.id.buttonR: //Right
                dx = 1;
                break;
            case R.id.buttonU: //Up
                rotate = true;
                break;
            case R.id.buttonD: //Down
                dy = -1;
                break;
            case R.id.buttonEnter:
                while (checkMoving(--dy, 0, false) == 0) ;
                if (dy < 0) {
                    mPosY += dy + 1;
                }
                break;

        }
        int check = checkMoving(dy, dx, rotate);
        if (check == 2) {
            next();
        } else if (check == 0) {
            mPosY += dy;
            mPosX += dx;
            if (rotate) {
                mMovingBlock = rotateBlock(mMovingBlock);
            }
        }

        tetrisView.setMoveBlock(mPosX, mPosY, mMovingBlock);
        tetrisView.invalidate();
    }


    //***************************//
    // 타이머
    private void onTimer() {

        int check = checkMoving(-1, 0, false);
        if (check == 0) {
            mPosY -= 1;
        }

        if (check == 2) {
            next();
        }

        tetrisView.setMoveBlock(mPosX, mPosY, mMovingBlock);
        tetrisView.invalidate();

    }


    private void startTimer(int period) {
        stopTimer();


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

    private int[][] newBlock() {
        int[][] block = BLOCK_SHAPES[random.nextInt(7)];
        int[][] block2 = new int[block.length][block[0].length];
        for (int y = 0; y < block.length; y++) {
            for (int x = 0; x < block[0].length; x++) {
                block2[y][x] = block[y][x];
            }
        }
        return block2;
    }

    private void insertBlock(int x, int y, int[][] block) {
        for (int j = 0; j < block.length; j++) {
            for (int i = 0; i < block[0].length; i++) {
                if (y + j < mGameBoard.length && x + i < mGameBoard[0].length) {
                    if(block[j][i] > 0) {
                        mGameBoard[y + j][x + i] = block[j][i];
                    }
                }
            }
        }
    }

    private int[][] rotateBlock(int[][] block) {
        int block2[][] = new int[block[0].length][block.length];
        for (int y = 0; y < block.length; y++) {
            for (int x = 0; x < block[0].length; x++) {
                block2[x][block.length - y - 1] = block[y][x];
            }
        }
        return block2;
    }

    private int checkMoving(int dy, int dx, boolean rotate) {
        int nextY = mPosY + dy;
        int nextX = mPosX + dx;
        int[][] block = mMovingBlock;
        int result = dy != 0 ? 2 : 1;

        if (rotate) {
            block = rotateBlock(mMovingBlock);
        }

        if (nextX < 0 || nextX + block[0].length > mGameBoard[0].length) {
            return result;
        }
        if (nextY < 0) {
            return result;
        }

        for (int y = 0; y < block.length; y++) {
            if (nextY + y < mGameBoard.length) {
                for (int x = 0; x < block[0].length; x++) {
                    if (block[y][x] > 0) {
                        if (mGameBoard[nextY + y][nextX + x] > 0) {
                            return result;
                        }
                        if (nextY + y >= mGameBoard.length) {
                            return result;
                        }
                    }
                }
            }
        }

        return 0;
    }

    private int checkAndRemoveLine() {
        List<Integer> removes = new ArrayList<Integer>();
        for (int y = 0; y < mGameBoard.length; y++) {
            boolean full = true;
            for (int x = 0; x < mGameBoard[0].length; x++) {
                if (mGameBoard[y][x] == 0) {
                    full = false;
                    break;
                }
            }

            if (full) {
                removes.add(y);
            }
        }

        for (int lineNum : removes) {
            for (int y = lineNum + 1; y < mGameBoard.length; y++) {
                mGameBoard[y - 1] = mGameBoard[y];
            }

            mGameBoard[SIZE_Y - 1] = new int[SIZE_X];
        }

        return removes.size();
    }

    private void next() {
        insertBlock(mPosX, mPosY, mMovingBlock);
        checkAndRemoveLine();
        mMovingBlock = newBlock();
        mPosX = SIZE_X / 2;
        mPosY = SIZE_Y - 1;
        if (checkMoving(0, 0, false) != 0) {
            //gameEnd
        }
    }
}
