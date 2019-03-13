package com.gioppl.signature

import android.content.Context

/**
 * Created by GIOPPL on 2017/12/27.
 */

class ShareData() {
    companion object {
        //前两个是端口数据
        fun WriteData(SERVER_ADDRESS: String,SERVER_ROBOT:String,context: Context) {
            val editor = context.getSharedPreferences("server", Context.MODE_PRIVATE).edit()
            editor.putString("SERVER_ADDRESS", SERVER_ADDRESS)
            editor.putString("SERVER_ROBOT", SERVER_ROBOT)
            editor.commit()
        }

        fun ReadData(context: Context) {
            val preferences = context.getSharedPreferences("server", Context.MODE_PRIVATE)
            FinalValue.SERVER_ADDRESS=preferences.getString("SERVER_ADDRESS","http://10.21.4.224:8080")
            FinalValue.SERVER_ROBOT=preferences.getString("SERVER_ROBOT","10.21.4.222")
        }
        //后两个是尺寸数据
        fun WriteSizeData(weight:Int,height:Int,context: Context) {
            val editor = context.getSharedPreferences("size", Context.MODE_PRIVATE).edit()
            editor.putInt("BITMAP_WEIGHT", weight)
            editor.putInt("BITMAP_HEIGHT", height)
            editor.commit()
        }
        fun ReadSizeData(context: Context) {
            val preferences = context.getSharedPreferences("size", Context.MODE_PRIVATE)
            FinalValue.BITMAP_WEIGHT=preferences.getInt("BITMAP_WEIGHT",286)
            FinalValue.BITMAP_HEIGHT=preferences.getInt("BITMAP_HEIGHT",262)
        }
    }

}
