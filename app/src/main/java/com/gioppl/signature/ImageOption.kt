package com.gioppl.signature

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Environment
import android.text.format.Time
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * 对图片的一些操作，包括保存图片，删除图片
 * Created by GIOPPL on 2017/10/27.
 */

class ImageOption(bitmap: Bitmap?, mContext: Context?, var saveSuccess: SaveImageSuccess?, var isServer: Boolean) {
    //下面开始保存图片
    init {
        val appDir = File(Environment.getExternalStorageDirectory().absolutePath + "/GIOPPL/")
        val appDir2 = File(Environment.getExternalStorageDirectory().absolutePath + "/GIOPPL/Server")
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        if (!appDir2.exists()) {
            appDir2.mkdir()
        }
        if (bitmap == null) {
//            Toast.makeText(mContext, "保存图片失败啦,无法保存图片", Toast.LENGTH_SHORT).show()
        } else {
            val t = Time() // or Time t=new Time("GMT+8"); 加上Time Zone资料
            t.setToNow() // 取得系统时间。
            val year = t.year
            val month = t.month
            val date = t.monthDay + 1
            val hour = t.hour
            val minute = t.minute

            val name = year.toString() + "_" + month + "_" + date + "_" + hour + "_" + minute + "_" + FinalValue.getImageIncrement()
            var fileName = name + ".jpg"

            var file: File? = null
            if (isServer) {
                fileName = "serve.jpg"
                file = File(appDir2, fileName)
            } else {
                file = File(appDir, fileName)
            }

            try {
                val fos = FileOutputStream(file)
                assert(bitmap != null)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
                if (isServer) {
//                saveSuccess.success(Environment.getExternalStorageDirectory().absolutePath + "/GIOPPL/Server/"+ fileName)
                    saveSuccess!!.success(Environment.getExternalStorageDirectory().absolutePath + "/GIOPPL/Server/" + "serve.jpg")
                } else {
                    saveSuccess!!.success(Environment.getExternalStorageDirectory().absolutePath + "/GIOPPL/" + fileName)
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    //保存成功的回调接口
    public interface SaveImageSuccess {
        fun success(path: String)
    }

    //删除图片，静态
    companion object {
        public fun deleteOption(path: String) {
            var myDeleteFile = File(path)
            if (myDeleteFile.exists()) {
                myDeleteFile.delete()
            }
        }

        //删除Server目录下所有文件
        public fun deleteServerAllImage() {
            val rootFile = File(Environment.getExternalStorageDirectory().absolutePath + "/GIOPPL/Server/")
            if (rootFile.exists()) {
                val fileList = rootFile.listFiles()
                if (fileList != null)
                    for (file in fileList) {
                        if (!file.isDirectory && file.exists()) {
                            file.delete()
                            FinalValue.successMessage("已删除文件" + file.absoluteFile)
                        }
                    }
            }
        }

    }

    /**
     * 保存为白色图片
     */
    public fun saveJPG_After(bitmap: Bitmap, newImgpath: String? = Environment.getExternalStorageDirectory().absolutePath + "/GIOPPL/Server/" + "serve.jpg") {
        //复制Bitmap  因为png可以为透明，jpg不支持透明，把透明底明变成白色
        //主要是先创建一张白色图片，然后把原来的绘制至上去
        val outB = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outB)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null)
        val file = File(newImgpath)
        try {
            val out = FileOutputStream(file)
            if (outB.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush()
                out.close()
                saveLocalImage(outB)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
    public fun saveLocalImage(bitmap: Bitmap){
        val t = Time() // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow() // 取得系统时间。
        val year = t.year
        val month = t.month
        val date = t.monthDay + 1
        val hour = t.hour
        val minute = t.minute
        val name = year.toString() + "_" + month + "_" + date + "_" + hour + "_" + minute + "_" + FinalValue.getImageIncrement()
        val fileName = name + ".jpg"
        val appDir = File(Environment.getExternalStorageDirectory().absolutePath + "/GIOPPL/")
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            assert(bitmap != null)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            saveSuccess!!.success(Environment.getExternalStorageDirectory().absolutePath + "/GIOPPL/" + fileName)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
