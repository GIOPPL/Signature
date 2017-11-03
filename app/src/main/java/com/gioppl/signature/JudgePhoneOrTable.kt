package com.gioppl.signature

import android.content.Context
import android.content.res.Configuration



/**
 * 判断平板还是手机
 * Created by GIOPPL on 2017/11/1.
 */
class JudgePhoneOrTable {
    companion object {
        /**
        * @param context
        * @return true:平板,false:手机
        */
        public fun isTabletDevice(context: Context): Boolean {
            return context.getResources().getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
        }
    }
}