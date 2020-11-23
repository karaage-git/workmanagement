package com.karaageumai.workmanagement

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.karaageumai.workmanagement.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i("初めてのログ")







    }
}