package com.example.kyon.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.security.SecureRandom;
import java.util.Random;

public class JuliaSetTestView extends View implements View.OnTouchListener {
    static {
        System.loadLibrary("juliaset");
    }
    public native void HelloJni(int[] pixels, int x, int y, double ar, double ai);

    private Bitmap bmpBuf;
    private Canvas canvasBuf;
    private int juliaSetScreenSize = 0;
    private double xmin = -2.0;
    private double xmax = 2.0;
    private double ymin = -2.0;
    private double ymax = 2.0;

    private int pix[][];

    public JuliaSetTestView(Context context) {
        super(context);

        setOnTouchListener(this);

        WindowManager wm = (WindowManager)context.getSystemService(context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point realSize = new Point();
        disp.getSize(realSize);

        juliaSetScreenSize = (realSize.x <= realSize.y) ? realSize.x-1 : realSize.y-1;

//        pix = new int[juliaSetScreenSize][juliaSetScreenSize];
        bmpBuf = Bitmap.createBitmap(realSize.x,realSize.y, Bitmap.Config.ARGB_8888);
        canvasBuf = new Canvas(bmpBuf);
//
//        Paint paint = new Paint();
//        paint.setTextAlign(Paint.Align.LEFT);
//        paint.setTextSize(50);
//        paint.setColor(Color.BLACK);
//        canvasBuf.drawText("jni:" + String.valueOf(endMSec-startMSec) + "ms", 10,50, paint);
//        for (int i = 0; i < test.length; i++) {
//            if (test[i] == -1) {
//                int a = 0;
//            }
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(JuliaSetTestView.class.getName(), "onDraw Start");
        canvas.drawBitmap(bmpBuf,0,0,null);
        Log.d(JuliaSetTestView.class.getName(), "onDraw End");
    }

    private void juliasetJava(int[] cnt_arr, int w, int h, double ar, double ai) {
        final double zr_min=-2.0, zi_min=-2.0, zr_max=2.0, zi_max=2.0;
        final double dx = (zr_max - zr_min) / w;
        final double dy = (zi_max - zi_min) / h;
        double zr,zi,wr,wi,val;
        int x,y,cnt;
        for (y = 0; y < h; y++) {
            for (x = 0; x < w; x++) {
                zr = zr_min + x * dx;
                zi = zi_min + y * dy;
                cnt = 0;
                do {
                    wr = (zr + zi) * (zr - zi) + ar;
                    wi = 2 * zr * zi + ai;
                    val = (wr * wr) + (wi * wi);
                    zr = wr;
                    zi = wi;
                    if (cnt++ > 64) {
                        cnt = -1;
                        break;
                    }
                } while (val < 4);
                cnt_arr[(y*w)+(x)] = cnt;
            }
        }
    }

//    @Override
//    public void onClick(View v) {
//        Log.d(JuliaSetTestView.class.getName(), "onClick start:");
//        synchronized (isCalc) {
//            if (isCalc) {
//                return;
//            }
//            try {
//                if (eTouchEvent == TouchEvent.doubleJuliaSet) {
//                    eTouchEvent = TouchEvent.nothing;
//                    if (drawOrder == null) {
//                        drawOrder = new DrawOrder();
//                        Thread t1 = new Thread(drawOrder);
//                        t1.run();
////                        t1.start();
////                        t1.join();
//                    }
//                    drawOrder.touchXY(touchX, touchY);
//                    Thread t2 = new Thread(drawOrder);
//                    t2.run();
////                    t2.start();
////                    t2.join();
//
//                } else if (eTouchEvent == TouchEvent.newJuliaSet) {
//                    eTouchEvent = TouchEvent.nothing;
//                    drawOrder = new DrawOrder();
//                    Thread t3 = new Thread(drawOrder);
//                    t3.run();
////                    t3.start();
////                    t3.join();
//                }
//            } catch (Exception ex) {
//
//            }
//            invalidate();
//        }
//
//        Log.d(JuliaSetTestView.class.getName(), "onClick end:");
//    }
//
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Paint bk = new Paint();
        bk.setColor(Color.WHITE);
        //canvasBuf.drawRect(0, 0, juliaSetScreenSize, 200, bk);
        canvasBuf.drawRect(0, 0, canvasBuf.getWidth(), canvasBuf.getHeight(), bk);

        Paint paint = new Paint();
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(50);
        paint.setColor(Color.BLACK);
        long startMSec,endMSec;
        int[] test = new int[juliaSetScreenSize*juliaSetScreenSize];

        startMSec = System.currentTimeMillis();
        HelloJni(test,juliaSetScreenSize,juliaSetScreenSize,0.5,0.5);
        endMSec = System.currentTimeMillis();
        canvasBuf.drawText("jni :" + String.valueOf(endMSec-startMSec) + "ms", 10,50, paint);

        startMSec = System.currentTimeMillis();
        juliasetJava(test,juliaSetScreenSize,juliaSetScreenSize,0.5,0.5);
        endMSec = System.currentTimeMillis();
        canvasBuf.drawText("java:" + String.valueOf(endMSec-startMSec) + "ms", 10,110, paint);

        invalidate();
        return false;
    }
//
//    private class DrawOrder implements Runnable {
//        private double ar = 0, ai = 0;
//
//        double m_XMin;
//        double m_YMin;
//        double m_XMax;
//        double m_YMax;
//
//        int m_Pitch = 1;
//
//        DrawOrder() {
//            Random rand = new SecureRandom();
//            ar = (rand.nextDouble() * (xmax - xmin)) - xmax;
//            ai = (rand.nextDouble() * (ymax - ymin)) - ymax;
//
//            Paint paint = new Paint();
//            paint.setTextAlign(Paint.Align.LEFT);
//            paint.setTextSize(50);
//            paint.setColor(Color.BLACK);
//
//            Paint bk = new Paint();
//            bk.setColor(Color.WHITE);
//            int start = 50;
//            int height = 50;
//            canvasBuf.drawRect(10, juliaSetScreenSize + start - height, juliaSetScreenSize, juliaSetScreenSize + start + (height * 2), bk);
//            canvasBuf.drawText("ar:" + ar, 10, juliaSetScreenSize + start ,paint);
//            canvasBuf.drawText("ai:" + ai, 10, juliaSetScreenSize + start + height ,paint);
//
//            this.m_XMin = xmin;
//            this.m_YMin = ymin;
//            this.m_XMax = xmax;
//            this.m_YMax = ymax;
//
//            m_Pitch = 1;
//            while ((juliaSetScreenSize / m_Pitch) > 600) {
//                m_Pitch++;
//            }
//        }
//
//        public void touchXY(int x, int y) {
//            // 座標
//            final double xmin = m_XMin;
//            final double ymin = m_YMin;
//            final double xmax = m_XMax;
//            final double ymax = m_YMax;
//
//            // 解像度
//            final int pitch = m_Pitch;
//
//            final double dx = (m_XMax - m_XMin) / (juliaSetScreenSize / pitch);
//            final double dy = (m_YMax - m_YMin) / (juliaSetScreenSize / pitch);
//
//            double tX = m_XMin + ((x / pitch) * dx);
//            double tY = m_YMin + ((y / pitch) * dy);
//            double moveX = tX;
//            double moveY = tY;
//            m_XMin += moveX;
//            m_XMax += moveX;
//            m_YMin += moveY;
//            m_YMax += moveY;
//
//            m_XMin /= 2;
//            m_XMax /= 2;
//            m_YMin /= 2;
//            m_YMax /= 2;
//        }
//
//        @Override
//        public void run() {
//            synchronized (isCalc) {
//                if (isCalc) {
//                    return;
//                }
//                isCalc = true;
//                Log.d(JuliaSetTestView.class.getName(), "run calc start");
//
//                Random rand = new Random();
//                Log.d(JuliaSetTestView.class.getName(), "run ar:" + ar);
//                Log.d(JuliaSetTestView.class.getName(), "run ai:" + ai);
//
//                Thread t1 = new Thread(new CalcJuliaSet(0,0,juliaSetScreenSize,juliaSetScreenSize
//                        ,m_XMin,m_YMin,m_XMax,m_YMax
//                        ,juliaSetScreenSize,juliaSetScreenSize,ar,ai,pix));
//
////                Thread t1 = new Thread(new CalcJuliaSet(0,0,juliaSetScreenSize,juliaSetScreenSize/2
////                        ,m_XMin,m_YMin,m_XMax,m_YMax
////                        ,juliaSetScreenSize,juliaSetScreenSize,ar,ai,pix));
//
////                Thread t2 = new Thread(new CalcJuliaSet(0,juliaSetScreenSize/2,juliaSetScreenSize,juliaSetScreenSize
////                        ,m_XMin,m_YMin,m_XMax,m_YMax
////                        ,juliaSetScreenSize,juliaSetScreenSize,ar,ai,pix));
//                  t1.run();
////                t1.start();
////                t2.start();
////                try {
////                    t1.join();
////                    t2.join();
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
//                isCalc = false;
//
//                //invalidate();
//                Log.d(JuliaSetTestView.class.getName(), "onClick calc end");
//            }
//
//        }
//    }
//
//    private class CalcJuliaSet implements Runnable {
//        private int m_sx;
//        private int m_sy;
//        private int m_ex;
//        private int m_ey;
//        int m_ScreenSizeX;
//        int m_ScreenSizeY;
//        double m_XMin;
//        double m_YMin;
//        double m_XMax;
//        double m_YMax;
//        double m_Ar;
//        double m_Ai;
//        private int[][] m_pix;
//
//        int m_Pitch = 1;
//
//        CalcJuliaSet(int sx, int sy, int ex, int ey, double xmin, double ymin, double xmax, double ymax
//                , int ScreenSizeX, int ScreenSizeY, double ar, double ai, int[][] pix) {
//            this.m_sx = sx;
//            this.m_sy = sy;
//            this.m_ex = ex;
//            this.m_ey = ey;
//
//            this.m_XMin = xmin;
//            this.m_YMin = ymin;
//            this.m_XMax = xmax;
//            this.m_YMax = ymax;
//            this.m_ScreenSizeX = ScreenSizeX;
//            this.m_ScreenSizeY = ScreenSizeY;
//
//            this.m_Ar = ar;
//            this.m_Ai = ai;
//
//            this.m_pix = pix;
//
//            m_Pitch = 1;
//            while ((m_ScreenSizeX / m_Pitch) > 600) {
//                m_Pitch++;
//            }
//        }
//
//        @Override
//        public void run() {
//            String tag = CalcJuliaSet.class.getName() + ":" + Thread.currentThread().getName();
//            Log.d(tag, "run Start");
//            // 座標
//            final double xmin = m_XMin;
//            final double ymin = m_YMin;
//            final double xmax = m_XMax;
//            final double ymax = m_YMax;
//
//            // 解像度
//            final int pitch = m_Pitch;
//
//            final int sx = m_sx / pitch;
//            final int sy = m_sy / pitch;
//            final int ex = m_ex / pitch;
//            final int ey = m_ey / pitch;
//
//            final double dx = (xmax - xmin) / (m_ScreenSizeX / pitch);
//            final double dy = (ymax - ymin) / (m_ScreenSizeY / pitch);
//            final double ar = m_Ar;
//            final double ai = m_Ai;
//
//            double wr;
//            double wi;
//            double val = 0;
//            int x, y,cnt=0;
//            for (x = sx; x < ex; x++) {
//                for (y = sy; y < ey; y++) {
//                    double zr = xmin + x * dx;
//                    double zi = ymin + y * dy;
//                    cnt = 0;
//                    do {
//                        wr = (zr + zi) * (zr - zi) + ar;
//                        wi = 2 * zr * zi + ai;
//                        val = (wr * wr) + (wi * wi);
//                        zr = wr;
//                        zi = wi;
//                        if (cnt++ > 64) {
//                            cnt = -1;
//                            break;
//                        }
//                    } while(val < 4);
//                    m_pix[x][y] = countToColor(cnt, 32);
//                }
//            }
//
//            Paint paint = new Paint();
//            paint.setStrokeWidth(pitch);
//            for (x=sx; x<ex; x++) {
//                for (y=sy;y<ey; y++) {
//                    paint.setColor(pix[x][y]);
//                    canvasBuf.drawPoint((x+1)*pitch, (y+1)*pitch, paint);
//                }
//            }
//
//            Log.d(tag, "run End");
//        }
//
//        private int countToColor(int cnt, int base) {
//            int color = Color.rgb(255,255,255);
//
//            if (cnt < 0) {
//                return color;
//            }
//
//            int d = (cnt % base) * 256 / base;
//            int m = ((int)(d / 42.667));
//            switch (m) {
//                case 0:
//                    color = Color.rgb(0, 6 * d, 255);
//                    break;
//                case 1:
//                    color = Color.rgb(0, 0, 255 - 6 * (d - 43));
//                    break;
//                case 2:
//                    color = Color.rgb(6 * (d - 86), 255, 0);
//                    break;
//                case 3:
//                    color = Color.rgb(255, 255 - 6 * (d - 129), 0);
//                    break;
//                case 4:
//                    color = Color.rgb(255, 0, 6 * (d - 171));
//                    break;
//                case 5:
//                    color = Color.rgb(255 - 6 * (d - 214), 0, 255);
//                    break;
//                default:
//                    color = Color.rgb(0, 0, 0);
//                    break;
//            }
//            return color;
//        }
//
//    }
}
