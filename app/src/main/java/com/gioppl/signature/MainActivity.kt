package com.gioppl.signature

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.gioppl.signature.seting_windows.Setting
import com.gioppl.signature.tcp.SocketUtil
import java.io.BufferedWriter
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    var dView: DoodleView? = null//画板
    var mRV: RecyclerView? = null//画廊
    var mAdapt: ImageAdapt? = null//适配器
    var imagePath: String? = null//用于保存的图片保存成功的回调值
    var btn_con: Button? = null//连接机器人按钮

    private var mList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        jurisdictionGet()
        Thread(Runnable { connect() }).start()//连接机器人
        findImage()
        initView()
        SetAdaptManager()
        sendRound()//运行连接程序
    }

    private fun jurisdictionGet() {
        /**
         * 动态获取权限，Android 6.0 新特性，一些保护权限，除了要在AndroidManifest中声明权限，还要使用如下代码动态获取
         */
        if (Build.VERSION.SDK_INT >= 23) {
            val REQUEST_CODE_CONTACT = 101
            val permissions = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            //验证是否许可权限
            for (str in permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT)
                    return
                }
            }
        }
    }

    /**
     * 初始化一般控件
     */
    private fun initView() {
        dView = findViewById(R.id.dView_main) as DoodleView?
        mRV = findViewById(R.id.rv_mian) as RecyclerView?
        btn_con = findViewById(R.id.btn_con) as Button?
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
        mRV!!.layoutManager = layoutManager
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
        mRV!!.adapter = mAdapt
        mRV!!.itemAnimator = DefaultItemAnimator()
    }

    private fun splitFileName(path: String): String {
        val nameList = path.split("/");
        val name = nameList[nameList.size - 1]
        return name
    }

    /**
     * 在APP初始化的时候把以前保存的图片找出来
     */
    private fun findImage() {
        val rootFile = File(Environment.getExternalStorageDirectory().absolutePath + "/GIOPPL")

        if (rootFile.exists()) {
            val fileList = rootFile.listFiles()
            if (fileList != null)
                for (file in fileList) {
                    if (!file.isDirectory) {
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
        dView!!.isDrawingCacheEnabled = true
        dView!!.buildDrawingCache()  //启用DrawingCache并创建位图
        val bitmap = Bitmap.createBitmap(dView!!.drawingCache) //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        BmpOption(bitmap, object : BmpOption.SplitBmpOption {

            override fun ServerBitmap(serverBitmap: Bitmap) {
                //保存上传到服务器的图片
                ImageOption(serverBitmap, this@MainActivity, object : ImageOption.SaveImageSuccess {
                    override fun success(path: String) {
                        imagePath = path
                    }
                }, true)
            }

            override fun SaveBitmap(saveBitmap: Bitmap) {
                //保存画廊里要存的图片
                ImageOption(saveBitmap, this@MainActivity, object : ImageOption.SaveImageSuccess {
                    override fun success(path: String) {
                        mList.add(0, path)
                        mAdapt!!.notifyDataSetChanged()
                    }
                }, false)
            }
        })



        dView!!.isDrawingCacheEnabled = false  //禁用DrawingCahce否则会影响性能

        dView!!.clearCaves()

        //上传图片
        if (imagePath != null) {
            FinalValue.successMessage(imagePath + ",")
            Thread(Runnable {
                UploadUtil(File(imagePath), FinalValue.SERVER_ADDRESS + FinalValue.SERVER_PORT, this, object : UploadUtil.UpImageSuccessful {
                    override fun uploadImageError() {
                        FinalValue.toast(this@MainActivity, "上传失败!")
                    }

                    override fun uploadImageSuccessful() {//上传成功的回调方法
                        FinalValue.toast(this@MainActivity, "上传成功!")
                        btn_con!!.visibility=View.VISIBLE
                        FinalValue.successMessage("图片上传成功哦！！！！")
                    }

                })
            }).start()
        }
//        ImageOption.deleteServerAllImage()//删除server文件夹全部文件
    }

    /**
     * APP设置
     */
    public fun appSet(v: View) {
        startActivity(Intent(this@MainActivity, Setting::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (imagePath != "")
            ImageOption.deleteOption(imagePath!!)
    }


    /**
     *  下面为机器人编程
     */
    private var isConnectSuccess = false;//是否连接成功
    private var robotSate = true//机器人是否在上料,点击按钮的时候转换，所以初始值为true
    private var bfWriter: BufferedWriter? = null
    private var receive_robot = ""
    private var stop_sculpture = true//停止上料,true为可以发送指令，false为不能发送指令，机器已经停止
    private var robot_connection_text = "未连接机器人"
    private var allowSculpture=false


    //按钮点击事件
    public fun conRobot(v: View) {
        allowSculpture=true
        robotSate=!robotSate
    }



    //连接TCP
    private fun connect() {
        val su = SocketUtil(this, FinalValue.SERVER_ROBOT, FinalValue.ROBOT_PORT, object : SocketUtil.MessageSocket {
            override fun connectSuccess(bfWriter: BufferedWriter) {
                isConnectSuccess = true
                this@MainActivity.bfWriter = bfWriter
                robot_connection_text = "已连接机器人"
            }

            override fun backMessage(message: String?) {
                FinalValue.successMessage("成功接收到信息")
                FinalValue.toast(this@MainActivity, message!!)
                receive_robot = message
                robot_connection_text = "正在操作"
                if(message=="0000"){
                    stop_sculpture=false
                    val msg=Message()
                    msg.arg1=0xc
                    handler.sendMessage(msg)
                }
            }
        });
    }

    //循环发送消息
    fun sendRound() {
        Thread(Runnable {
            while (true) {
                if (bfWriter!=null&&isConnectSuccess&&stop_sculpture&&allowSculpture) {//bfWrite不为空，连接成功，是否可以继续发送指令，是否能发指令（按钮点击之后才发指令）
                    if (robotSate) {//时候已经上料，如果上料再次点击则会停止上料
                        sendOption("0000", bfWriter!!)//0000为发给机器人停止上料
                        val msg=Message()
                        msg.arg1=0xb
                        handler.sendMessage(msg)
                    } else {
                        sendOption("1000", bfWriter!!)
                        val msg=Message()
                        msg.arg1=0xa
                        handler.sendMessage(msg)
                    }
                } else {
                    FinalValue.toast(this, "未连接机器人，请重试")
                }
                Thread.sleep(2000)
            }
        }).start()
    }

    //发送操作
    fun sendOption(message: String, bfWriter: BufferedWriter) {
        try {
            bfWriter.write(message + "\n")
            bfWriter.flush()
            FinalValue.successMessage("成功发送" + message)
        } catch (e: IOException) {
            Toast.makeText(this, "无法建立连接", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }
    //机器人按钮设置文字
    private fun btnSetText(text:String){
        btn_con!!.text=text
    }

    private var handler=object :Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.arg1){
                0xA->btnSetText("正在雕刻")
                0xB->btnSetText("已停止雕刻")
                0xc->btnSetText("已完成雕刻")
            }
        }
    }
}
