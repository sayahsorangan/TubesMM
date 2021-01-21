package com.example.tubesm.Utils

import android.content.Context
import com.example.tubesm.Utils.Sessions.save
import com.example.tubesm.Utils.StaticVar.KEYLOGIN

object User {
    fun isLogin(c: Context?): Boolean {
        return java.lang.Boolean.valueOf(Sessions[c!!, KEYLOGIN])
    }

    fun setLogin(c: Context?) {
        save(c!!, "true", KEYLOGIN)
    }

    fun userLogOut(c: Context?) {
        save(c!!, "false", KEYLOGIN)
    }
}