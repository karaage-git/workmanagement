package com.karaageumai.workmanagement

import android.app.Application
import android.content.Context

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // バージョンを出力しておく
        Log.version()
        Log.i("onCreate()")
    }

    // 初期化
    init {
        mInstance = this
    }

    companion object {
        // どのクラスからもContextを取得できるようにする
        private var mInstance: MainApplication? = null
        fun getContext() : Context {
            return mInstance!!.applicationContext
        }
    }

}