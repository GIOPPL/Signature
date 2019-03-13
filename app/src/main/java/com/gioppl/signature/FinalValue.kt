package com.gioppl.signature

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.util.regex.Pattern

/**
 * 全局类方法，包括提示成功，提示错误，还有全局变量
 * Created by GIOPPL on 2017/10/28.
 */
class FinalValue {

    companion object {
        internal var BITMAP_SIZE = 300
        internal var IMAGE_NAME_INCREMENT = 0
        internal var SERVER_ADDRESS="http://192.168.2.77:8080"//192.168.2.77:8080
        internal var SERVER_PORT="/sign/main?method=efort"
        internal var SERVER_ROBOT="10.21.4.222"//10.21.4.222
        internal var ROBOT_PORT=4002//4002
        internal var BITMAP_WEIGHT=282
        internal var BITMAP_HEIGHT=262
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

        /**
         * 查看是否是数字
         */
        fun isNumerIczidai(str: String): Boolean {
            val pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*")
            val isNum = pattern.matcher(str)
            return isNum.matches()
        }
    }
}