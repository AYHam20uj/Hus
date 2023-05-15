package com.st.bluenrgmesh

import android.util.Log

class Shared {
    companion object {
        @JvmField
        var start : Long= 0

        @JvmField
        var finish : Long= 0
        @JvmField
        var timeElapsed = finish - start
        @JvmField
        val starts = mutableMapOf<String, Long>()
        @JvmField
        var numberOfresponses =0;
        fun printTime (){
            Log.i("round trip tiik",timeElapsed.toString())
        }
    }
}