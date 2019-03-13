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
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
    var b_1000: String? = null
    var b_0000: String? = null
    var b_0100: String? = null
    var b_0010: String? = null
    var im_connection:ImageView?=null
    var tv_english:TextView?=null

    private var mList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        jurisdictionGet()
        Thread(Runnable { connect() }).start()//连接机器人
        findImage()
        initView()
        SetAdaptManager()
        sendRound()//运行连接程序
        initMessage()
    }

    private fun initMessage() {
        b_0000 = FinalValueJava.getB_0000()
        b_1000 = FinalValueJava.getB_1000()
        b_0100 = FinalValueJava.getB_0100()
        b_0010 = FinalValueJava.getB_0010()
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
        im_connection= findViewById(R.id.im_main_connection) as ImageView?
        dView = findViewById(R.id.dView_main) as DoodleView?
        mRV = findViewById(R.id.rv_mian) as RecyclerView?
        btn_con = findViewById(R.id.btn_con) as Button?
        tv_english= findViewById(R.id.tv_con_english) as TextView?
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
        mRV!!.itemAnimator = DefaultItemAnimator() as RecyclerView.ItemAnimator?
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
                ImageOption(null,null,null,false).saveJPG_After(serverBitmap)//保存图片
                //                //保存上传到服务器的图片
//                ImageOption(serverBitmap, this@MainActivity, object : ImageOption.SaveImageSuccess {
//                    override fun success(path: String) {
//                        imagePath = path
//                    }
//                }, true)
            }

            override fun SaveBitmap(saveBitmap: Bitmap) {
                //保存画廊里要存的图片
//                ImageOption(saveBitmap, this@MainActivity, object : ImageOption.SaveImageSuccess {
//                    override fun success(path: String) {
//                        mList.add(0, path)
//                        mAdapt!!.notifyDataSetChanged()
//                    }
//                }, false)
            }
        })

        dView!!.isDrawingCacheEnabled = false  //禁用DrawingCache否则会影响性能

        dView!!.clearCaves()
        imagePath=Environment.getExternalStorageDirectory().absolutePath + "/GIOPPL/Server/"+ "serve.jpg"
        //上传图片
        if (imagePath != null) {
            FinalValue.successMessage(imagePath + ",")
            UploadUtil(File(imagePath), FinalValue.SERVER_ADDRESS + FinalValue.SERVER_PORT, this, object : UploadUtil.UpImageSuccessful {
                override fun uploadImageError() {
                    FinalValue.toast(this@MainActivity, "上传失败!")
                }

                override fun uploadImageSuccessful() {//上传成功的回调方法
                    FinalValue.toast(this@MainActivity, "上传成功!")
                    btn_con!!.visibility = View.VISIBLE
                    FinalValue.successMessage("图片上传成功哦！")
                }
            })
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
    //    private var robotSate = true//机器人是否在上料,点击按钮的时候转换，所以初始值为true
    private var bfWriter: BufferedWriter? = null
    private var receive_robot = ""
    //    private var stop_sculpture = true//停止上料,true为可以发送指令，false为不能发送指令，机器已经停止
    private var allowSculpture = false
    private var allowSendMessage = true
    private var tableStatus = TableStatus.S_1000
    private var netConnectStatus=BtnStatus.NoNet//按钮连接状态


    //定义平板发送的枚举类
    private enum class TableStatus {
        S_1000, S_0000,S_0100//总共有两中状态，第一发送1000，第二发送0000,第三个是复位状态
    }

    //定义发送按钮的枚举类
    private enum class BtnStatus {
        NoNet, Already, Sculpture,Reset
    }

    //按钮点击事件
    public fun conRobot(v: View) {
        if (netConnectStatus==BtnStatus.NoNet){
            Thread(Runnable { connect() }).start()//连接机器人
        }else{
            allowSculpture = true
            allowSendMessage = true
            btn_con!!.isClickable = false
        }
    }

    //连接TCP
    private fun connect() {
        val su = SocketUtil(this, FinalValue.SERVER_ROBOT, FinalValue.ROBOT_PORT, object : SocketUtil.MessageSocket {
            override fun connectSuccess(bfWriter: BufferedWriter) {//机器人连接成功
                isConnectSuccess = true
                this@MainActivity.bfWriter = bfWriter
                btnSetText("点击雕刻")
                btnSetEnglishText("Start",20.0f);
                sendButtonSetBackground(BtnStatus.Already)
                netConnectStatus=BtnStatus.Already
                val msg = Message()
                msg.arg1 = 0x10
                handler.sendMessage(msg)
            }

            override fun backMessage(message: String?) {
                FinalValue.successMessage("成功接收到信息")
                receive_robot = message!!
                val msg = Message()
                if (message == b_0000) {//说明正在雕刻 00 00 00 00
                    btn_con!!.isClickable = false
                    msg.arg1 = 0xa
                    tableStatus = TableStatus.S_1000
                } else if (message == b_1000) {//第一次如果给我返回1000,说明第一次初始化结束  10 00 00 00
                    btn_con!!.isClickable = true
                    msg.arg1 = 0xa
                    tableStatus = TableStatus.S_0000
                } else if(message==b_0100){//雕刻完成，没有复位
                    btn_con!!.isClickable = false
                    msg.arg1=0xf
                    tableStatus = TableStatus.S_0100
                }else if (message == b_0010) {//复位完成
                    btn_con!!.isClickable = true
                    msg.arg1 = 0xc
                    tableStatus = TableStatus.S_1000
                    allowSculpture = false
                }
                handler.sendMessage(msg)
            }
        });
    }

    //循环发送消息
    fun sendRound() {
        Thread(Runnable {
            while (true) {
                if (bfWriter != null && allowSculpture && allowSendMessage) {//bfWrite不为空，连接成功，是否可以继续发送指令，是否能发指令（按钮点击之后才发指令）
                    val msg = Message()
                    if (tableStatus == TableStatus.S_1000) {//如果是第一次就会触发这个方法，一直到机器人返回0000的时候
                        sendOption(b_1000!!, bfWriter!!)
                        msg.arg1 = 0xa
                    } else if (tableStatus==TableStatus.S_0000){
                        sendOption(b_0000!!, bfWriter!!)
                        msg.arg1 = 0xa
                    }else if(tableStatus==TableStatus.S_0100){
                        sendOption(b_0100!!, bfWriter!!)
                        msg.arg1 = 0xf
                    }
//                        sendOption(b_1000!!, bfWriter!!)


                    handler.sendMessage(msg)
                } else {
                    val msg = Message()
                    msg.arg1 = 0xd
                }
                Thread.sleep(1000)
            }
        }).start()
    }

    //发送操作
    fun sendOption(message: String, bfWriter: BufferedWriter) {
        try {
            bfWriter.write(message)
            bfWriter.flush()
            FinalValue.successMessage("成功发送" + message)
        } catch (e: IOException) {
            Toast.makeText(this, "无法建立连接", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    //机器人按钮设置文字
    private fun btnSetText(text: String) {
        btn_con!!.text = text
    }
    //机器人按钮设置文字
    private fun btnSetEnglishText(text: String,textSize:Float) {
        tv_english!!.text = text
//        tv_english!!.textSize=textSize
    }

    private var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.arg1) {
                0xA -> {
                    btnSetText("正在雕刻")
                    btnSetEnglishText("Ongoing",20.0f);//ONGOING
                    sendButtonSetBackground(BtnStatus.Sculpture)
                    im_connection!!.setImageResource(R.mipmap.circle_btn)
                }
//                0xB->btnSetText("已停止雕刻")
                0xc -> {
                    btnSetText("点击雕刻")
                    btnSetEnglishText("Start",20.0f);//START
                    FinalValue.toast(this@MainActivity, "已完成雕刻")
                    sendButtonSetBackground(BtnStatus.Already)
                    im_connection!!.setImageResource(R.mipmap.circle_btn)
                }
                0xd -> {
                    btnSetText("未连接")
                    btnSetEnglishText("Unconnected",13.0f);//CONNECTION
                    sendButtonSetBackground(BtnStatus.NoNet)
                    im_connection!!.setImageResource(R.mipmap.circle_error)
                }
                0xe -> {
                    btnSetText("点击雕刻")
                    btnSetEnglishText("Start",20.0f);//ENGRAVING
                    sendButtonSetBackground(BtnStatus.Already)
                    im_connection!!.setImageResource(R.mipmap.circle_btn)
                }
                0xf -> {//这个状态是复位
                    btnSetText("正在复位")
                    btnSetEnglishText("Resetting",20.0f);//RESETTING
                    sendButtonSetBackground(BtnStatus.Reset)
                    im_connection!!.setImageResource(R.mipmap.circle_btn)
                }
                0x10->{
                    im_connection!!.setImageResource(R.mipmap.circle_btn)
                }
            }
        }
    }

    private fun sendButtonSetBackground(status: BtnStatus) {
        when (status) {
            BtnStatus.NoNet -> btn_con!!.background = resources.getDrawable(R.drawable.btn_shape_noconnect)
            BtnStatus.Already -> btn_con!!.background = resources.getDrawable(R.drawable.btn_shape_normal)
            BtnStatus.Sculpture -> btn_con!!.background = resources.getDrawable(R.drawable.btn_shape_pressed)
            BtnStatus.Reset -> btn_con!!.background = resources.getDrawable(R.drawable.btn_shape_pressed)
        }
    }
}
