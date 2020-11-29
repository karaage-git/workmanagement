package com.karaageumai.workmanagement

import android.util.Log

class Log {

    companion object {

        // ログ呼び出しメソッドをstackTraceで特定するために使用する定数
        private const val TARGET_STACK_DEPTH = 4

        // ログ出力
        fun i(aString: String) {
            if (BuildConfig.DEBUG){
                // デバッグビルドの時のみログ出力
                val message = aString + getStackTraceInfo()
                Log.i(MainApplication.getContext().getString(R.string.log_tag), message)
            }
        }

        // ログ出力
        fun i(aString: String, aThrowable: Throwable) {
            if (BuildConfig.DEBUG){
                // デバッグビルドの時のみログ出力
                Log.i(MainApplication.getContext().getString(R.string.log_tag), aString, aThrowable)
            }
        }

        // バージョン出力用のログ（リリースビルドでも出力可能）
        fun version() {
            val versionCode: Int = BuildConfig.VERSION_CODE
            val versionName: String = BuildConfig.VERSION_NAME
            val message: String = "versionCode:$versionCode, versionName:$versionName"
            Log.i(MainApplication.getContext().getString(R.string.log_tag), message)
        }

        // トレース情報から呼び出し元のメソッド情報を取得するためのメソッド
        private fun getStackTraceInfo() : String {
            // stackTraceのネストの深さを考慮する必要がある。
            // 0:VMStack
            // 1:Thread
            // 2:getStackTraceInfo()(本メソッド)
            // 3:companion objectで定義したメソッド（本メソッドの呼び出し元）
            // 4:ログメソッドを呼び出したメソッド（上記3をコールしたメソッド）
            val element: StackTraceElement = Thread.currentThread().stackTrace[TARGET_STACK_DEPTH]

            // パッケージ全部を含むクラス名
            val fullName = element.className
            // クラス名のみ抽出
            val className = fullName.substring(fullName.lastIndexOf(".") + 1)
            // メソッド名
            val methodName = element.methodName
            // 実行行
            val lineNumber = element.lineNumber

            return "[$className#$methodName at $lineNumber]"

        }
    }



}