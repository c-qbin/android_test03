package com.example.android_test03

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnTask1).setOnClickListener {
            startActivity(Intent(this, ListViewActivity::class.java))
        }
        findViewById<Button>(R.id.btnTask2).setOnClickListener {
            startActivity(Intent(this, LoginDialogActivity::class.java))
        }
        findViewById<Button>(R.id.btnTask3).setOnClickListener {
            startActivity(Intent(this, OptionsMenuActivity::class.java))
        }
        findViewById<Button>(R.id.btnTask4).setOnClickListener {
            startActivity(Intent(this, ActionModeActivity::class.java))
        }
        findViewById<Button>(R.id.btnTask5).setOnClickListener {
            startActivity(Intent(this, RecyclerViewActivity::class.java))
        }
    }
}
