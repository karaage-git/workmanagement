package com.karaageumai.workmanagement

import android.app.Application
import android.content.Context
import com.karaageumai.workmanagement.presenter.input.IBaseInputPresenter

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
        private var mInputPresenter: IBaseInputPresenter? = null

        /**
         * Contextの取得
         */
        fun getContext() : Context {
            return mInstance!!.applicationContext
        }

        /**
         * 情報入力用のPresenterを共有用にセットする
         */
        fun setPresenter(aInputPresenter: IBaseInputPresenter?) {
            mInputPresenter = aInputPresenter
        }

        /**
         * 情報入力用のPresenterを取得する
         */
        fun getPresenter(): IBaseInputPresenter? {
            return mInputPresenter
        }
    }

}