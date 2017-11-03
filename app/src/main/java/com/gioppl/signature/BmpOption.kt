package com.gioppl.signature

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color


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
        fun SplitSuccess(bitmap: Bitmap)
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
            drawBitmapToBitmap(newBmp)
        }catch (e:Exception){

        }
    }
    private fun drawBitmapToBitmap(frontBitmap: Bitmap){
        val bitmap_blank = Bitmap.createBitmap(width!!, height!!,Bitmap.Config.ARGB_8888)
        bitmap_blank.eraseColor(Color.parseColor("#ffffff"))//填充颜色
        val canvas = Canvas(bitmap_blank)
        canvas.drawBitmap(frontBitmap, calculateWidth(frontBitmap.width.toFloat()), calculateHeight(frontBitmap.height.toFloat()), null);
        splitBmpOption.SplitSuccess(bitmap_blank)
    }

    private fun calculateHeight(h:Float)=(height!! - h) /2
    private fun calculateWidth(w:Float)=(width!!-w) /2
}