package com.test.testris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Random;

/**
 * Created by yuhojun on 2017. 2. 1..
 */

public class TetrisView extends View {
    private int[][] mGameBoard;

    private Bitmap mBlock[] = new Bitmap[7];
    private Paint paintBorder = new Paint();
    private float density;
    private RectF rectF = new RectF();
    private int mMovingBlockX = 0;
    private int mMovingBlockY = 0;
    private int[][] mMovingSahpe = null;


    public TetrisView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        density = metrics.density;
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setColor(Color.argb(120, 0, 0, 0));
        paintBorder.setStrokeWidth(3 * density);

        BitmapDrawable bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.block_base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

        }
        RectF rectF1 = new RectF(0, 0, 64, 64);
        Paint p1 = new Paint();
        int[] colors = new int[]{
                getResources().getColor(R.color.block1),
                getResources().getColor(R.color.block2),
                getResources().getColor(R.color.block3),
                getResources().getColor(R.color.block4),
                getResources().getColor(R.color.block5),
                getResources().getColor(R.color.block6),
                getResources().getColor(R.color.block7)
        };
        for (int i = 0; i < colors.length; i++) {
            Bitmap bm = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            p1.setColor(colors[i]);
            c.drawRoundRect(rectF1, 10.0f, 10.0f, p1);
            c.drawBitmap(bitmapDrawable.getBitmap(), null, rectF1, null);
            mBlock[i] = bm;
        }


    }

    public TetrisView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setGameBoard(int[][] gameBoard) {
        mGameBoard = gameBoard;
    }

    public void setMoveBlock(int x, int y, int[][] shape) {

        mMovingBlockX = x;
        mMovingBlockY = y;
        mMovingSahpe = shape;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mGameBoard == null || mGameBoard.length == 0 || mGameBoard[0].length == 0) {
            if (isInEditMode()) {
                mGameBoard = new int[18][12];
                Random random = new Random();
                for (int i = 0; i < 60; i++) {
                    int r = random.nextInt(mGameBoard.length * mGameBoard[0].length);
                    mGameBoard[r / mGameBoard[0].length][r % mGameBoard[0].length] = random.nextInt(7) + 1;
                }
                for (int i = 0; i < mGameBoard[0].length; i++) {
                    mGameBoard[5][i] = random.nextInt(7) + 1;
                }

                mMovingBlockX = 2;
                mMovingBlockY = 13;
                mMovingSahpe = new int[][]{
                        {0, 1, 0},
                        {1, 1, 1}
                };
            } else {
                return;
            }

        }
        int w = canvas.getWidth();
        int h = canvas.getHeight();


        float borderWidth = 3 * density;

        int cx = mGameBoard[0].length;
        int cy = mGameBoard.length;

        int blockSize = (int) Math.min((h - borderWidth) / cy, (w - borderWidth * 2) / cx);

        float offsetY = borderWidth;
        float offsetX = (w - cx * blockSize) / 2;
        float bw_2 = borderWidth / 2;

        float bl = offsetX - bw_2;
        float br = offsetX + cx * blockSize + bw_2;

        canvas.drawLine(bl, 0, bl, h, paintBorder);
        canvas.drawLine(bl, h - bw_2, br, h - bw_2, paintBorder);
        canvas.drawLine(br, 0, br, h, paintBorder);

        for (int y = 0; y < cy; y++) {
            for (int x = 0; x < cx; x++) {
                rectF.set(offsetX + x * blockSize, h - offsetY - blockSize * (y + 1), offsetX + (x + 1) * blockSize, h - offsetY - blockSize * y);
                int blockType = mGameBoard[y][x];
                if (blockType > 0 && blockType <= 7) {
                    canvas.drawBitmap(mBlock[blockType - 1], null, rectF, null);
                }
            }
        }

        if (mMovingSahpe != null) {
            for (int y = 0; y < mMovingSahpe.length; y++) {
                for (int x = 0; x < mMovingSahpe[0].length; x++) {
                    int x2 = x + mMovingBlockX;
                    int y2 = y + mMovingBlockY;
                    rectF.set(offsetX + x2 * blockSize, h - offsetY - blockSize * (y2 + 1), offsetX + (x2 + 1) * blockSize, h - offsetY - blockSize * y2);
                    int blockType = mMovingSahpe[y][x];
                    if (blockType > 0 && blockType <= 7) {
                        canvas.drawBitmap(mBlock[blockType - 1], null, rectF, null);
                    }
                }
            }
        }
        super.onDraw(canvas);
    }
}
