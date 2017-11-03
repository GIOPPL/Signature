package com.gioppl.signature

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.View
import com.gioppl.signature.seting_windows.Setting
import java.io.File

class MainActivity : AppCompatActivity() {
    var dView: DoodleView? = null//画板
    var mRV: RecyclerView? = null//画廊
    var mAdapt: ImageAdapt? = null//适配器
    var imagePath: String? = null//用于保存的图片保存成功的回调值

//    //画笔颜色
//    var paint_blue: ImageView? = null
//    var paint_green: ImageView? = null
//    var paint_red: ImageView? = null
//    var paint_black: ImageView? = null
//    var paint_purple: ImageView? = null
//    var paint_yellow: ImageView? = null

    private var mList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!JudgePhoneOrTable.isTabletDevice(this)){
            setContentView(R.layout.activity_main_phone)
            FinalValue.successMessage("this is phone")
        }else{
            setContentView(R.layout.activity_main)
            FinalValue.successMessage("this is table")
        }

        findImage()
        initView()
        SetAdaptManager()

    }

    /**
     * 初始化一般控件
     */
    private fun initView() {
        dView = findViewById(R.id.dView_main) as DoodleView?
        mRV = findViewById(R.id.rv_mian) as RecyclerView?
//        initPaint()
    }




    /**
     * 清除画板按钮
     */
    public fun clear(v: View?) {
        dView!!.clearCaves()
    }

    /**
     * 设置画廊适配器
     */
    private fun SetAdaptManager() {
        val layoutManager = LinearLayoutManager(this)
        mRV!!.setLayoutManager(layoutManager)
        mRV!!.setHasFixedSize(true)
        layoutManager.orientation = OrientationHelper.VERTICAL
        mAdapt = ImageAdapt(this, mList, object : ImageAdapt.AdaptOption {
            override fun loadImage(path: String, position: Int) {
                val file = File(path)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(path)
                    dView!!.loadBitmap(path)
                }
            }

            override fun deleteImage(path: String, position: Int) {
                ImageOption.deleteOption(path)
                mList.removeAt(position)
                mAdapt!!.notifyDataSetChanged()
            }
        })
        mRV!!.setAdapter(mAdapt)
        mRV!!.setItemAnimator(DefaultItemAnimator())
    }

    /**
     * 在APP初始化的时候把以前保存的图片找出来
     */
    private fun findImage() {
        val rootFile = File(Environment.getExternalStorageDirectory().absolutePath + "/GIOPPL/")
        if (rootFile.exists()) {
            var fileList = rootFile.listFiles()
            for (file in fileList) {
                if (!file.isDirectory){
                    mList.add(file.absolutePath)
                    FinalValue.successMessage(file.absolutePath)
                }
            }
        }
    }


    /**
     * 保存图片按钮
     */
    public fun save(v: View?) {
        dView!!.setDrawingCacheEnabled(true)
        dView!!.buildDrawingCache()  //启用DrawingCache并创建位图
        var bitmap = Bitmap.createBitmap(dView!!.getDrawingCache()) //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        BmpOption(bitmap,object : BmpOption.SplitBmpOption{
            override fun SplitSuccess(newBmp: Bitmap) {
                bitmap=newBmp

            }

        })
        ImageOption(bitmap, this@MainActivity, object : ImageOption.SaveImageSuccess {
            override fun success(path: String) {
                mList.add(0, path)
                mAdapt!!.notifyDataSetChanged()
                imagePath = path
            }
        })
        dView!!.setDrawingCacheEnabled(false)  //禁用DrawingCahce否则会影响性能

        dView!!.clearCaves()

        if (imagePath != null) {
            FinalValue.successMessage(imagePath + ",")
            Thread(Runnable {
                UploadUtil(File(imagePath), FinalValue.SERVER_ADDRESS+FinalValue.SERVER_PORT, this,object : UploadUtil.UpImageSuccessful{
                    override fun uploadImageError() {
                        FinalValue.toast(this@MainActivity, "上传失败!")
                        FinalValue.successMessage("上传成功")
                    }

                    override fun uploadImageSuccessful() {//上传成功的回调方法
                        FinalValue.toast(this@MainActivity, "上传成功!")
                        FinalValue.successMessage("上传成功")
                    }

                })
            }).start()
        }
    }


//    /**
//     * 上传图片
//     */
//    public fun upload(v: View) {
//        dView!!.isDrawingCacheEnabled = true
//        dView!!.buildDrawingCache()  //启用DrawingCache并创建位图
////        var bitmap = Bitmap.createBitmap(dView!!.getDrawingCache()) //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
//        dView!!.isDrawingCacheEnabled = false  //禁用DrawingCahce否则会影响性能
//        dView!!.clearCaves()
//        if (imagePath != null) {
//            FinalValue.successMessage(imagePath + ",")
//            Thread(Runnable {
//                UploadUtil.uploadFile(File(imagePath), "http://192.168.1.104:8080/sign/main?method=efort", this,object : UploadUtil.UpImageSuccessful{
//                    override fun uploadImageError() {
//                        FinalValue.toast(this@MainActivity, "上传失败!")
//                    }
//
//                    override fun uploadImageSuccessful() {//上传成功的回调方法
//                        FinalValue.toast(this@MainActivity, "上传成功!")
//                        FinalValue.successMessage("上传成功")
//                    }
//
//                })
//            }).start()
//        }
//    }
    /**
     * APP设置
     */
    public fun appSet(v:View){
        startActivity(Intent(this@MainActivity,Setting::class.java))
    }




//    /**
//     * 初始化画笔，用于画笔调色
//     */
//    private fun initPaint() {
//        paint_black = findViewById(R.id.im_main_black) as ImageView?
//        paint_blue = findViewById(R.id.im_main_blue) as ImageView?
//        paint_red = findViewById(R.id.im_main_red) as ImageView?
//        paint_yellow = findViewById(R.id.im_main_yellow) as ImageView?
//        paint_purple = findViewById(R.id.im_main_purple) as ImageView?
//        paint_green = findViewById(R.id.im_main_green) as ImageView?
//        imageScale(paint_black)
//        paint_black!!.setOnClickListener {dView!!.paintColorSet(Color.rgb(43, 43, 43))
//            imageScale(paint_black)}
//        paint_blue!!.setOnClickListener { dView!!.paintColorSet(Color.rgb(49, 96, 242))
//            imageScale(paint_blue)}
//        paint_red!!.setOnClickListener { dView!!.paintColorSet(Color.rgb(250, 33, 33))
//            imageScale(paint_red) }
//        paint_yellow!!.setOnClickListener { dView!!.paintColorSet(Color.rgb(238, 233, 36))
//            imageScale(paint_yellow)}
//        paint_purple!!.setOnClickListener { dView!!.paintColorSet(Color.rgb(196, 57, 231))
//            imageScale(paint_purple)}
//        paint_green!!.setOnClickListener { dView!!.paintColorSet(Color.rgb(35, 243, 79))
//            imageScale(paint_green)}
//    }
//    /**
//     * 放大缩小图片，用于在点击画笔的时候，缩小该画笔图片，还原其余画笔图片
//     */
//    private fun imageScale(view: ImageView?) {
//        paint_black!!.scaleX=1.0f
//        paint_black!!.scaleY=1.0f
//
//        paint_blue!!.scaleX=1.0f
//        paint_blue!!.scaleY=1.0f
//
//        paint_red!!.scaleX=1.0f
//        paint_red!!.scaleY=1.0f
//
//        paint_yellow!!.scaleX=1.0f
//        paint_yellow!!.scaleY=1.0f
//
//        paint_purple!!.scaleX=1.0f
//        paint_purple!!.scaleY=1.0f
//
//        paint_green!!.scaleX=1.0f
//        paint_green!!.scaleY=1.0f
//
//        view!!.scaleX = 0.6f
//        view.scaleY = 0.6f
//    }
}
