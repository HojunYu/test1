package com.test.testris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int[][] mGameBoard = new int[18][12];
    TetrisView tetrisView;
    Random random = new Random();
    private TimerTask mTask;
    private Timer mTimer;


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
        switch (view.getId()) {
            case R.id.buttonL: //Left
                for (int i = 0; i < mGameBoard[0].length; i++) {
                    mGameBoard[4][i] = random.nextInt(7) + 1;
                }
                break;
            case R.id.buttonR: //Right
                for (int i = 0; i < mGameBoard[0].length; i++) {
                    mGameBoard[0][i] = random.nextInt(7) + 1;
                }
                break;
            case R.id.buttonU: //Up
                tetrisView.setMoveBlock(2, 13, new int[][]{
                        {0, 1, 0},
                        {1, 1, 1}
                });
                break;
            case R.id.buttonD: //Down
                tetrisView.setMoveBlock(5, 13, new int[][]{
                        {3, 3, 0},
                        {0, 3, 3}
                });
                break;
            case R.id.buttonEnter:

                tetrisView.setMoveBlock(0, 0, null);
                break;

        }
        tetrisView.invalidate();
    }


    //***************************//
    // 타이머
    private void onTimer() {
        if (test > 0) {
            test--;
        }
        tetrisView.setMoveBlock(4, test, testBlock);
        tetrisView.invalidate();
    }

    private int test;
    private int[][] testBlock = new int[][]{
            {4, 0, 4},
            {4, 4, 4}
    };

    private void startTimer(int period) {
        stopTimer();

        test = mGameBoard.length;

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
}
