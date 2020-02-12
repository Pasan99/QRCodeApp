package com.triangle.barcodereader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        home_scan_btn.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
        home_generate_btn.setOnClickListener{
            startActivity(Intent(this, CreateQrCodeActivity::class.java))
        }
    }
}
