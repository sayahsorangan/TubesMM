package com.example.tubesm.Utils

import android.content.Context

object Sessions {
    @JvmStatic
    fun save(context: Context, value: String?, key: String?) {
        val sharedPreferences = context.getSharedPreferences("clipcodea", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    @JvmStatic
    operator fun get(context: Context, key: String?): String? {
        val sharedPreferences = context.getSharedPreferences("clipcodea", Context.MODE_PRIVATE)
        return sharedPreferences.getString(StaticVar.KEYLOGIN, "false")
    }
}