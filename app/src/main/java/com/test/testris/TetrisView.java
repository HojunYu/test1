package com.test.testris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
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
    private int[][] mMovingShape = null;



    public TetrisView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics(); //스크린 사이즈 가져오기

        density = metrics.density; //160dpi screen this density value will be 1.
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setColor(Color.argb(120, 0, 0, 0));
        paintBorder.setStrokeWidth(3 * density);

        BitmapDrawable bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.block_base);

      //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {}
        //직사각형 가로세로 64짜리를 하나 만들어둠
        RectF rectF1 = new RectF(0, 0, 64, 64);
        //Paint 객체도 하나 만들어둠.
        Paint p1 = new Paint();
        //xml에 설정해둔 색깔의 int 숫자를 colors 배열에 담음. 블록이 7개이므로 7색
        int[] colors = new int[]{
                getResources().getColor(R.color.block1),
                getResources().getColor(R.color.block2),
                getResources().getColor(R.color.block3),
                getResources().getColor(R.color.block4),
                getResources().getColor(R.color.block5),
                getResources().getColor(R.color.block6),
                getResources().getColor(R.color.block7)
        };
        //설정색을 가진 RoundRect블록 하나를 그림
        for (int i = 0; i < colors.length; i++) {
            //가로세로 64짜리 bitmap객체 bm 만든다
            Bitmap bm = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);//(블록 한칸의 가로, 세로, 해상도?설정)
            //Canvas 객체 c에 위에서 만든 bm을 붙인다.
            Canvas c = new Canvas(bm);
            //위에서 만든 p1 객체의 색깔을 colors 배열에 담긴 색으로 지정한다
            p1.setColor(colors[i]);
            //사각형 하나, 곡률, 곡률, 색상으로 둥근 직사각형 그린다.
            c.drawRoundRect(rectF1,10.0f,10.0f,p1);
            //
            c.drawBitmap(bitmapDrawable.getBitmap(), null, rectF1, null);
            //
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
        mMovingShape = shape;
    }

    public int[][] getmMovingShape() {
        return mMovingShape;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //mGameBoard가 아예 없다면 실행되는 코드
        if (mGameBoard == null || mGameBoard.length == 0 || mGameBoard[0].length == 0) {
            if (isInEditMode()) { //isInEditMode()==false
                mGameBoard = new int[18][12];
                Random random = new Random();
                //i가 왜 60이지?
                for (int i = 0; i < 60; i++) {
                    //mGameBoard.length == 18, mGameBoard[0].length == 12 , r < 216
                    int r = random.nextInt(mGameBoard.length * mGameBoard[0].length);
                    // r/mGameBoard[0].length는 18 미만의 정수, r%mGameboard[0].length는 12 미만의 정수값으로 나옴.
                    mGameBoard[r / mGameBoard[0].length][r % mGameBoard[0].length] = random.nextInt(7) + 1;

                }
                //mGameBoard[5][i]는 뭐지
                for (int i = 0; i < mGameBoard[0].length; i++) {
                    mGameBoard[5][i] = random.nextInt(7) + 1;
                }
                //이건 왜 이렇게 설정된 걸까요..
                mMovingBlockX = 2;
                mMovingBlockY = 13;
                mMovingShape = new int[][]{
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

        int cx = mGameBoard[0].length; //보드 가로 블럭갯수
        int cy = mGameBoard.length; // 보드 세로 블럭갯수

        //
        int blockSize = (int) Math.min((h - borderWidth) / cy, (w - borderWidth * 2) / cx);

        //좌표
        float offsetY = borderWidth;
        float offsetX = (w - cx * blockSize) / 2;
        float bw_2 = borderWidth / 2;

        float bl = offsetX - bw_2;
        float br = offsetX + cx * blockSize + bw_2;

        //테두리 선 그리기
        canvas.drawLine(bl, 0, bl, h, paintBorder);
        canvas.drawLine(bl, h - bw_2, br, h - bw_2, paintBorder);
        canvas.drawLine(br, 0, br, h, paintBorder);

        //블록
        for (int y = 0; y < cy; y++) {
            for (int x = 0; x < cx; x++) {
                //rectF.set(l,t,r,b);
                rectF.set(offsetX + x * blockSize, h - offsetY - blockSize * (y + 1), offsetX + (x + 1) * blockSize, h - offsetY - blockSize * y);
                int blockType = mGameBoard[y][x];
                if (blockType > 0 && blockType <= 7) {
                    canvas.drawBitmap(mBlock[blockType - 1], null, rectF, null);
                }
            }
        }

        if (mMovingShape != null) {
            for (int y = 0; y < mMovingShape.length; y++) {
                for (int x = 0; x < mMovingShape[0].length; x++) {
                    int x2 = x + mMovingBlockX;
                    int y2 = y + mMovingBlockY;
                    rectF.set(offsetX + x2 * blockSize, h - offsetY - blockSize * (y2 + 1), offsetX + (x2 + 1) * blockSize, h - offsetY - blockSize * y2);
                    int blockType = mMovingShape[y][x];
                    if (blockType > 0 && blockType <= 7) {
                        canvas.drawBitmap(mBlock[blockType - 1], null, rectF, null);
                    }
                }
            }
        }
        super.onDraw(canvas);
    }
}
