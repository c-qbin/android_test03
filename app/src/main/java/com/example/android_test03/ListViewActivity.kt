package com.example.android_test03

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat

class ListViewActivity : AppCompatActivity() {

    private val channelId = "animal_list_channel"
    private var notifId = 0

    private var pendingNotifName: String? = null
    private val animals = listOf(
        "Lion" to R.drawable.lion,
        "Tiger" to R.drawable.tiger,
        "Monkey" to R.drawable.monkey,
        "Dog" to R.drawable.dog,
        "Cat" to R.drawable.cat,
        "Elephant" to R.drawable.elephant
    )

    private var selectedPos = animals.indexOfFirst { it.first == "Elephant" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "任务1 ListView"

        createNotificationChannel()
        requestNotificationPermission()

        val data: List<Map<String, Any>> = animals.map { (name, icon) ->
            mapOf("name" to name, "icon" to icon)
        }

        val adapter = object : SimpleAdapter(
            this,
            data,
            R.layout.item_animal,
            arrayOf("name", "icon"),
            intArrayOf(R.id.tvName, R.id.ivIcon)
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                v.findViewById<TextView>(R.id.tvName)?.setTextColor(
                    if (position == selectedPos) getColor(R.color.white) else getColor(R.color.black)
                )
                v.setBackgroundColor(
                    if (position == selectedPos) getColor(R.color.highlight_red)
                    else getColor(R.color.white)
                )
                return v
            }
        }

        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter


        val btnElephant = findViewById<Button>(R.id.btnElephant)
        btnElephant.text = animals[selectedPos].first

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedPos = position
            adapter.notifyDataSetChanged()
            // 按钮文本同步为"当前选中"的列表项内容
            btnElephant.text = animals[selectedPos].first

            Toast.makeText(this, getString(R.string.toast_selected_template, animals[position].first), Toast.LENGTH_SHORT).show()
            // 通知仍显示当前点击的动物
            sendAnimalNotification(animals[position].first)
        }


        btnElephant.setOnClickListener {
            val pos = animals.indexOfFirst { it.first == "Elephant" }
            if (pos >= 0) {
                listView.setSelection(pos)
                listView.performItemClick(
                    listView.getChildAt(pos - listView.firstVisiblePosition),
                    pos,
                    listView.getItemIdAtPosition(pos)
                )
            }
        }

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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = getString(R.string.channel_desc) }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100
                )
            }
        }
    }

    private fun sendAnimalNotification(name: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            pendingNotifName = name   // 记录本次通知，授权成功后立即补发
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100
            )
            return
        }
        val nm = getSystemService(NotificationManager::class.java)
        val n = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(name)
            .setContentText(getString(R.string.notif_content_template, name, name))
            .setAutoCancel(true)
            .build()
        nm.notify(++notifId, n)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            pendingNotifName?.let { sendAnimalNotification(it) }
            pendingNotifName = null
        }
    }
}
