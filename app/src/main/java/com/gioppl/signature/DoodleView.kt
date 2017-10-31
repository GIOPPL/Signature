package com.gioppl.signature

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View




/**
 * Created by GIOPPL on 2017/10/27.
 */

class DoodleView : View {
    // 画笔，定义绘制属性
    private var myPaint: Paint? = null
    private var mBitmapPaint: Paint? = null
    var myPath:Path?=null

    // 记录宽度和高度
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mX: Float = 0.toFloat()
    var mY: Float = 0.toFloat()
    var TOUCH_TOLERANCE = 4f


    // 画布及其底层位图
    private var myBitmap: Bitmap? = null
    private var myCanvas: Canvas? = null

    constructor(context: Context) : super(context) {initView()}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {initView()}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {initView()}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {initView()}

    private fun initView() {
        // 绘制自由曲线用的画笔
        myPaint = Paint()
        myPaint!!.setAntiAlias(true)
        myPaint!!.setDither(true)
        myPaint!!.setColor(Color.BLACK)
        myPaint!!.setStyle(Paint.Style.STROKE)
        myPaint!!.setStrokeJoin(Paint.Join.ROUND)
        myPaint!!.setStrokeCap(Paint.Cap.ROUND)
        myPaint!!.setStrokeWidth(12f)

        myPath = Path()
        mBitmapPaint = Paint(Paint.DITHER_FLAG)

    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
//        canvas!!.
//        var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // 如果不调用这个方法，绘制结束后画布将清空
        canvas!!.drawBitmap(myBitmap, 0f, 0f, mBitmapPaint);

        // 绘制路径
        canvas!!.drawPath(myPath, myPaint);

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        postInvalidate()//刷新界面

        val x = event!!.getX()
        val y = event.getY()

        when (event.getAction()) {
            MotionEvent.ACTION_DOWN -> {
                touch_start(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touch_move(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touch_up()
                invalidate()
            }
        }
        return true
        invalidate();
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        myBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        myCanvas = Canvas(myBitmap)
    }

    private fun touch_start(x: Float, y: Float) {
        myPath!!.reset()
        myPath!!.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touch_move(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            myPath!!.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun touch_up() {
        myPath!!.lineTo(mX, mY)
        // commit the path to our offscreen
        // 如果少了这一句，笔触抬起时myPath重置，那么绘制的线将消失
        myCanvas!!.drawPath(myPath, myPaint)
        // kill this so we don't double draw
        myPath!!.reset()
    }
    public fun clearCaves(){
        //调用初始化画布函数以清空画布
        myBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        myCanvas = Canvas(myBitmap)
        invalidate();//刷新
    }
    public fun loadBitmap(path:String){
//        myBitmap = bitmap
        myBitmap=BitmapFactory.decodeFile(path).copy(Bitmap.Config.ARGB_8888, true);
        myCanvas = Canvas(myBitmap)
        invalidate();
    }
    public fun paintColorSet(color: Int){
        myPaint!!.color=color
        invalidate()
    }
}
