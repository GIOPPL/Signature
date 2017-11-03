package com.gioppl.signature

import android.content.Context
import android.util.Log
import android.widget.Toast

/**
 * 全局类方法，包括提示成功，提示错误，还有全局变量
 * Created by GIOPPL on 2017/10/28.
 */
class FinalValue {


    companion object {
        internal var IMAGE_NAME_INCREMENT = 0
        internal var SERVER_ADDRESS="http://192.168.1.104:8080"
        internal var SERVER_PORT="/sign/main?method=efort"
        internal var SERVER_ROBOT="http://192.168.1.104:8080"
        internal var ROBOT_PORT="/sign/main?method=efort"

        public fun toast(context: Context, msg: String = context.packageName) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        public fun errorMessage(s: String = "ERROR") {
            Log.i("ERROR", s)
        }

        public fun successMessage(s: String = "SUCCESS") {
            Log.i("SUCCESS", s)
        }

        public fun getImageIncrement(): Int {
            return IMAGE_NAME_INCREMENT++
        }
    }

    public fun toast(context: Context, msg: String = context.packageName) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    public fun errorMessage(s: String = "ERROR") {
        Log.i("ERROR", s)
    }

    public fun successMessage(s: String = "SUCCESS") {
        Log.i("SUCCESS", s)
    }


}