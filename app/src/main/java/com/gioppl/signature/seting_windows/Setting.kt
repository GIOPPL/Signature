package com.gioppl.signature.seting_windows

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.gioppl.signature.FinalValue
import com.gioppl.signature.R

/**
 * Created by GIOPPL on 2017/11/3.
 */
class Setting: AppCompatActivity() {
    private var layout_1:View?=null
    private var layout_2:View?=null
    private var btn_net:Button?=null
    private var btn_cmpIntro:Button?=null
    private var ed_server:EditText?=null
    private var ed_robot:EditText?=null
    private var ed_size:EditText?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)
        initView()
        netSet(null)
        serverShow()
    }

    private fun serverShow() {
        ed_robot!!.hint = FinalValue.SERVER_ROBOT
        ed_server!!.hint=FinalValue.SERVER_ADDRESS
        ed_size!!.hint=FinalValue.BITMAP_SIZE.toString()
    }

    private fun initView() {
        layout_1=findViewById(R.id.la_set_1)
        layout_2=findViewById(R.id.la_set_2)
        btn_net= findViewById(R.id.btn_set_net) as Button?
        btn_cmpIntro= findViewById(R.id.btn_set_companyIntroduce) as Button?
        ed_server= findViewById(R.id.ed_set_server) as EditText?
        ed_robot= findViewById(R.id.ed_set_robot) as EditText?
        ed_size= findViewById(R.id.ed_set_size) as EditText?
        //
    }
    public fun netSet(view: View?){
        btn_net!!.setBackgroundColor(Color.parseColor("#4CA1FF"))
        btn_cmpIntro!!.setBackgroundColor(Color.parseColor("#ffffff"))
        btn_net!!.setTextColor(Color.parseColor("#ffffff"))
        btn_cmpIntro!!.setTextColor(Color.parseColor("#585858"))
        layout_1!!.visibility=View.VISIBLE
        layout_2!!.visibility=View.GONE
    }
    public fun companyIntroduce(view: View){
        btn_cmpIntro!!.setBackgroundColor(Color.parseColor("#4CA1FF"))
        btn_net!!.setBackgroundColor(Color.parseColor("#ffffff"))
        btn_cmpIntro!!.setTextColor(Color.parseColor("#ffffff"))
        btn_net!!.setTextColor(Color.parseColor("#585858"))
        layout_1!!.visibility=View.GONE
        layout_2!!.visibility=View.VISIBLE
    }

    public fun update1(view: View){
        val address=ed_server!!.text.toString()
        if (address != ""){
            FinalValue.SERVER_ADDRESS=address
            FinalValue.toast(this@Setting,"修改服务器端口成功")
        }
    }
    public fun update2(view: View){
        val address=ed_robot!!.text.toString()
        if (address != ""){
            FinalValue.SERVER_ROBOT=address
            FinalValue.toast(this@Setting,"修改机器人端口成功")
        }
    }
    public fun back(view: View){
        finish()
    }
    public fun updateBitmapSize(view:View){
        try {
            FinalValue.BITMAP_SIZE=Integer.parseInt(ed_size!!.text.toString())
            FinalValue.toast(this@Setting,"修改图片大小成功")
        }catch (e:Exception){
            FinalValue.toast(this@Setting,"修改图片大小失败，请用正确数字")
        }


    }
}