package com.example.android_test03

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar

class OptionsMenuActivity : AppCompatActivity() {

    private lateinit var tvTest: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options_menu)
        // targetSdk 35+ 强制 edge-to-edge，必须自己吃掉状态栏高度，否则标题栏和右上角 ⋮ 会被压到状态栏下面（点不到）
        val root = findViewById<View>(R.id.root)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = bars.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.requestApplyInsets(root)

        // 白底页面下让状态栏图标显示为深色，否则时间/电量是白字看不见
        val darkMode =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = !darkMode

        // 用布局里的 Toolbar 充当 ActionBar，才有右上角的溢出菜单按钮
        setSupportActionBar(findViewById<MaterialToolbar>(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "任务3 XML定义菜单"
        tvTest = findViewById(R.id.tvTest)

        // 返回主页面
        findViewById<Button>(R.id.btnBackHome).setOnClickListener { backToHome() }
    }

    private fun backToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_small -> {
                tvTest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                Toast.makeText(this, getString(R.string.msg_size_changed, 10), Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_medium -> {
                tvTest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                Toast.makeText(this, getString(R.string.msg_size_changed, 16), Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_large -> {
                tvTest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                Toast.makeText(this, getString(R.string.msg_size_changed, 20), Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_normal -> {
                Toast.makeText(this, R.string.msg_normal_menu, Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_red -> {
                tvTest.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                Toast.makeText(this, getString(R.string.msg_color_changed, "红色"), Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_black -> {
                tvTest.setTextColor(ContextCompat.getColor(this, R.color.black))
                Toast.makeText(this, getString(R.string.msg_color_changed, "黑色"), Toast.LENGTH_SHORT).show()
                true
            }
            android.R.id.home -> { finish(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
