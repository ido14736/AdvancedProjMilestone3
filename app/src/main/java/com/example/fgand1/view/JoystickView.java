package com.example.fgand1.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private float centerX;
    private float centerY;
    private float baseRadius;
    private float hatRadius;
    private joystickListener joystickMove;

    //setting the values of the joystick
    private void setupDimensions() {
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        baseRadius = Math.min(getWidth(), getHeight()) / 3;
        hatRadius = Math.min(getWidth(), getHeight()) / 6;
    }

    //ctor
    public JoystickView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);

        if(context instanceof joystickListener)
        {
            joystickMove = (joystickListener)context;
        }
    }

    //ctor
    public JoystickView(Context context, AttributeSet attributes, int style) {
        super(context, attributes, style);
        getHolder().addCallback(this);
        setOnTouchListener(this);

        if(context instanceof joystickListener)
        {
            joystickMove = (joystickListener)context;
        }
    }

    //ctor
    public JoystickView(Context context, AttributeSet attributes) {
        super(context, attributes);
        getHolder().addCallback(this);
        setOnTouchListener(this);

        if(context instanceof joystickListener)
        {
            joystickMove = (joystickListener)context;
        }
    }

    //drawing the joystick
    public void drawJoystick(float newX, float newY) {
        if (getHolder().getSurface().isValid()) {
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            myCanvas.drawColor(-1);
            colors.setARGB(255, 190, 190, 190);
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors);
            colors.setARGB(255, 0, 0, 0);
            myCanvas.drawCircle(newX, newY, hatRadius, colors);
            getHolder().unlockCanvasAndPost(myCanvas);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setupDimensions();
        drawJoystick(centerX, centerY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    //when touching the joystick
    public boolean onTouch(View v, MotionEvent e) {
        if (v.equals(this)) {
            if (e.getAction() != e.ACTION_UP) {
                float displacement = (float) Math.sqrt((Math.pow(e.getX() - centerX, 2)) + Math.pow(e.getY() - centerY, 2));
                if (displacement < baseRadius) {
                    drawJoystick(e.getX(), e.getY());
                    joystickMove.joytickMoved(-1*((centerX - e.getX())/baseRadius), (centerY - e.getY())/baseRadius);
                } else {
                    float ratio = baseRadius / displacement;
                    float constrainedX = centerX + (e.getX() - centerX) * ratio;
                    float constrainedY = centerY + (e.getY() - centerY) * ratio;
                    drawJoystick(constrainedX, constrainedY);
                    joystickMove.joytickMoved(-1*((centerX - constrainedX)/baseRadius), (centerY - constrainedY)/baseRadius);
                }
            }
        }
        return true;
    }

    //the listener of the joystick - for the "MainActivity"
    public interface joystickListener {
        void joytickMoved(float newAileron, float newElevator);
    }
}

