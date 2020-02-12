package com.triangle.barcodereader

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        result.text = intent.getStringExtra("result")

        open_btn.setOnClickListener {
            val browserIntent =  Intent(Intent.ACTION_VIEW, Uri.parse(result.text.toString()))
            startActivity(browserIntent)
        }
    }
}
