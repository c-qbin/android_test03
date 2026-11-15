package com.example.android_test03

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Animal(val name: String, val iconRes: Int)

class RecyclerViewActivity : AppCompatActivity() {

    private val animals = mutableListOf(
        Animal("Lion", R.drawable.lion),
        Animal("Tiger", R.drawable.tiger),
        Animal("Monkey", R.drawable.monkey),
        Animal("Dog", R.drawable.dog),
        Animal("Cat", R.drawable.cat),
        Animal("Elephant", R.drawable.elephant)
    )
    private val poolIcons = listOf(R.drawable.lion, R.drawable.tiger, R.drawable.monkey,
        R.drawable.dog, R.drawable.cat, R.drawable.elephant)
    private var addedCount = 0
    private lateinit var adapter: AnimalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        // targetSdk 35+ 强制 edge-to-edge：不消费状态栏高度的话，"增加一条记录"按钮会被画到状态栏下面（点不到）
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

        // 白底页面下让状态栏图标显示为深色
        val darkMode =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = !darkMode

        val recycler = findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = AnimalAdapter(animals)
        recycler.adapter = adapter

        findViewById<View>(R.id.btnAdd).setOnClickListener {
            addedCount++
            val newAnimal = Animal("New Animal #$addedCount", poolIcons[(addedCount - 1) % poolIcons.size])
            animals.add(newAnimal)
            adapter.notifyItemInserted(animals.size - 1)
            recycler.scrollToPosition(animals.size - 1)
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

    inner class AnimalAdapter(
        private val data: List<Animal>
    ) : RecyclerView.Adapter<AnimalAdapter.AnimalVH>() {

        inner class AnimalVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val iv: ImageView = itemView.findViewById(R.id.ivIcon)
            val tv: TextView = itemView.findViewById(R.id.tvName)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalVH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_animal, parent, false)
            return AnimalVH(v)
        }

        override fun onBindViewHolder(holder: AnimalVH, position: Int) {
            val animal = data[position]
            holder.iv.setImageResource(animal.iconRes)
            holder.tv.text = animal.name
            holder.itemView.setOnClickListener {
                Toast.makeText(this@RecyclerViewActivity, animal.name, Toast.LENGTH_SHORT).show()
            }
        }

        override fun getItemCount(): Int = data.size
    }
}
