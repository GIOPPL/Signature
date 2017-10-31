package com.gioppl.signature

import android.content.Context
import android.util.Log
import android.widget.Toast

/**
 * Created by GIOPPL on 2017/10/28.
 */
class FinalValue {


    companion object {
        internal var IMAGE_NAME_INCREMENT = 0
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