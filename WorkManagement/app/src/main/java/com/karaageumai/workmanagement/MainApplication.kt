package com.karaageumai.workmanagement

import android.app.Application
import android.content.Context
import com.karaageumai.workmanagement.presenter.IBasePresenter

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
        // Contextを共有する仕組み
        private var mInstance: MainApplication? = null
        // BasePresenterを共有する仕組み
        private var mPresenter: IBasePresenter? = null

        fun getContext() : Context {
            return mInstance!!.applicationContext
        }

        fun setPresenter(aPresenter: IBasePresenter?) {
            mPresenter = aPresenter
        }

        fun getPresenter(): IBasePresenter? {
            return mPresenter
        }
    }

}