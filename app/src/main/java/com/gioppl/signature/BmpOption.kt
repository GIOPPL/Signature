package com.gioppl.signature

import android.graphics.*


/**
 * Created by GIOPPL on 2017/11/1.
 */
class BmpOption(var bitmap: Bitmap, var splitBmpOption: SplitBmpOption) {
    var width: Int? = null
    var height: Int? = null

    init {
        if (!(bitmap.width==0)||!(bitmap.height==0)) {
            weightAndHeight()
            bmpFactory()
        }
    }

    public interface SplitBmpOption {
        fun ServerBitmap(serverBitmap: Bitmap)
        fun SaveBitmap(saveBitmap: Bitmap)
    }

    private fun weightAndHeight() {
        width = bitmap.width
        height = bitmap.height
//        FinalValue.successMessage("the bitmap width and height is $width,$height")
    }

    private fun bmpFactory() {

        //pixels,这逼玩意是一行一行的存的，-1是白色
        val pixels = IntArray(bitmap.width * bitmap.height)//保存所有的像素的数组，图片宽×高
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height);
        val b = ArrayOption.twoArray(pixels, bitmap.width)

        val row_count_left = ArrayOption.split_row_left(b)//左行需要减的数量
        val col_count_left = ArrayOption.split_col_left(b)//左列需要减的数量
        val row_count_right = ArrayOption.split_row_right(b)//右列需要减的数量
        val col_count_right = ArrayOption.split_col_right(b)//右列需要减的数量

        try {
            val newBmp = Bitmap.createBitmap(bitmap, col_count_left, row_count_left, width!! - col_count_left - 1 - col_count_right, height!! - row_count_left - 1 - row_count_right, null, false)
//            drawFixationSizeBitmap(newBmp,FinalValue.BITMAP_SIZE)
            drawFixationSizeBitmap(bitmap,FinalValue.BITMAP_SIZE)
            drawBitmapToBitmap(newBmp)
        }catch (e:Exception){
                FinalValue.errorMessage(e.toString())
        }
    }
    private fun drawBitmapToBitmap(frontBitmap: Bitmap){
        val bitmap_blank = Bitmap.createBitmap(width!!, height!!-100,Bitmap.Config.ARGB_8888)
        bitmap_blank.eraseColor(Color.parseColor("#ffffff"))//填充颜色
        val canvas = Canvas(bitmap_blank)
        canvas.drawBitmap(frontBitmap, calculateWidth(frontBitmap.width.toFloat()), calculateHeight(frontBitmap.height.toFloat()), null);
//        splitBmpOption.SaveBitmap(bitmap_blank)
    }

    //画固定尺寸的bitmap
    private fun drawFixationSizeBitmap(bgImage: Bitmap,newWidth:Int){
//        val matrix = Matrix()
//        val ratio=newWidth/bgImage.width
//        FinalValue.successMessage("缩放的比例是$ratio，原来的长宽是"+bgImage.width+","+ bgImage.height)
//        matrix.postScale(ratio, ratio) //长和宽放大缩小的比例
//        val resizeBmp = Bitmap.createBitmap(bgImage, 0, 0, bgImage.width, bgImage.height, matrix, true)
//        FinalValue.successMessage("现在的图片大小是"+resizeBmp.width+","+resizeBmp.height)

//        val matrix = Matrix()
//        matrix.postScale(0.2f, 0.2f)
//        val temp = Bitmap.createBitmap(bgImage, 0, 0, bgImage.width, bgImage.height, matrix, true)
        var newBmp=bgImage;
//        while (newBmp.width >=newWidth){
//            val matrix = Matrix()
//            matrix.postScale(0.9f, 0.9f)
//            matrix.setScale(1.3f, 1.3f,(newBmp.width/2).toFloat(), (newBmp.height/2).toFloat());
//            matrix.postScale(0.2f, 0.2f, (newBmp.width/2).toFloat(), (newBmp.height/2).toFloat())
//            val bd = BitmapDrawable(newBmp)//接收bitmap
//            bd.setAntiAlias(true)//消除锯齿
//            newBmp = Bitmap.createBitmap(newBmp, 0, 0, newBmp.width, newBmp.height, matrix, true)
//        }
//        while(newBmp.width>FinalValue.BITMAP_WEIGHT||newBmp.height>FinalValue.BITMAP_HEIGHT){
//            val matrix = Matrix()
//            matrix.postScale(0.95f, 0.95f)
//            matrix.setScale(0.95f, 0.95f,(newBmp.width/2).toFloat(), (newBmp.height/2).toFloat());
//            newBmp = Bitmap.createBitmap(newBmp, 0, 0, newBmp.width, newBmp.height, matrix, true)
//        }
        println("bitmap的长："+newBmp.width+"宽："+newBmp.height)
//        saveJPG_After(newBmp)
        splitBmpOption.SaveBitmap(bgImage)
        splitBmpOption.ServerBitmap(bgImage)
    }

    /**
     * 镜像文件
     */
    private fun drawServerBitmap(frontBitmap: Bitmap){
        val final_bitmap=frontBitmap//用于不传镜像文件
        val bitmap_blank = Bitmap.createBitmap(width!!, height!!-100,Bitmap.Config.ARGB_8888)
        bitmap_blank.eraseColor(Color.parseColor("#ffffff"))//填充颜色
        val canvas = Canvas(bitmap_blank)
        canvas.drawBitmap(frontBitmap, calculateWidth(frontBitmap.width.toFloat()), calculateHeight(frontBitmap.height.toFloat()), null);
        val m = Matrix()
        m.postScale(-1f, 1f);   //镜像水平翻转
        val new2 = Bitmap.createBitmap(bitmap_blank, 0, 0,width!!, height!!-100, m, true)//这个new2就是镜像文件
        canvas.drawBitmap(new2, Rect(0, 0, new2.width, new2.height), Rect(0, 0,width!!, height!!-100), null)
        splitBmpOption.ServerBitmap(final_bitmap)
    }


    private fun calculateHeight(h:Float)=(height!! - h) /2
    private fun calculateWidth(w:Float)=(width!!-w) /2
}