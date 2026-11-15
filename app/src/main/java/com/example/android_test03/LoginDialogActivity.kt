package com.example.android_test03

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class LoginDialogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_dialog)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "任务2 创建自定义布局的AlertDialog"

        findViewById<Button>(R.id.btnOpenDialog).setOnClickListener { showLoginDialog() }

        // 返回主页面
        findViewById<Button>(R.id.btnBackHome).setOnClickListener { backToHome() }
    }

    private fun backToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }



    private fun showLoginDialog() {
        // 加载自定义布局
        val view = layoutInflater.inflate(R.layout.dialog_login, null)
        val etUser = view.findViewById<EditText>(R.id.etUsername)
        val etPwd = view.findViewById<EditText>(R.id.etPassword)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnSignIn = view.findViewById<Button>(R.id.btnSignIn)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        btnCancel.setOnClickListener {
            Toast.makeText(this, R.string.login_cancel, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        btnSignIn.setOnClickListener {
            val u = etUser.text.toString()
            val p = etPwd.text.toString()
            Toast.makeText(
                this,
                getString(R.string.login_success_template, u.ifEmpty { "(空)" }, p),
                Toast.LENGTH_SHORT
            ).show()
            dialog.dismiss()
        }
        dialog.show()
    }
}
