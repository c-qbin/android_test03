package com.example.android_test03

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode as AppCompatActionMode
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class ActionModeActivity : AppCompatActivity() {

    private val data = mutableListOf("One", "Two", "Three", "Four", "Five")
    private val selected = SparseBooleanArray()
    private var actionMode: AppCompatActionMode? = null
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_mode)
        // targetSdk 35+ 强制 edge-to-edge：自己吃掉状态栏高度，列表才不会钻到状态栏底下
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

        // 白底页面下状态栏图标用深色，否则时间/电量是白字看不见
        val darkMode =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = !darkMode

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "任务4 ActionMode 多选"

        adapter = object : ArrayAdapter<String>(
            this, R.layout.item_simple, R.id.tvName, data
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                v.findViewById<ImageView>(R.id.ivIcon).setImageResource(R.mipmap.ic_launcher)
                v.setBackgroundColor(
                    if (selected.get(position)) getColor(R.color.selected_blue)
                    else getColor(android.R.color.transparent)
                )
                return v
            }
        }

        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter

        listView.setOnItemLongClickListener { _, _, position, _ ->
            if (actionMode == null) {
                actionMode = startSupportActionMode(actionModeCallback)
            }
            toggleSelection(position)
            true
        }
        listView.setOnItemClickListener { _, _, position, _ ->
            if (actionMode != null) toggleSelection(position)
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

    private fun toggleSelection(position: Int) {
        val nowSelected = !selected.get(position, false)
        selected.put(position, nowSelected)
        adapter.notifyDataSetChanged()
        val count = (0 until selected.size()).count { selected.valueAt(it) }
        actionMode?.title = getString(R.string.action_mode_selected, count)
        showOvalToast(
            getString(
                if (nowSelected) R.string.toast_item_selected else R.string.toast_item_unselected,
                data[position]
            )
        )
        if (count == 0) actionMode?.finish()
    }

    /**
     * 把 ActionMode 自带的上下文操作栏刷成深色底 + 白色文字。
     * Material3 主题里 actionModeBackground 属性对这根栏不生效，只能拿到视图后直接改。
     */
    private fun applyActionModeBarStyle() {
        window.decorView.post {
            val bar = window.decorView.findViewById<View>(
                androidx.appcompat.R.id.action_mode_bar
            ) ?: return@post
            bar.setBackgroundColor(getColor(R.color.action_mode_bg))
            setTextColorRecursively(bar)
        }
    }

    private fun setTextColorRecursively(view: View) {
        if (view is TextView) {
            view.setTextColor(getColor(R.color.white))
            return
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) setTextColorRecursively(view.getChildAt(i))
        }
    }

    /** 自定义椭圆（胶囊形）Toast：用 toast_oval 布局替换系统默认矩形样式 */
    @Suppress("DEPRECATION")
    private fun showOvalToast(message: CharSequence) {
        val toastView = layoutInflater.inflate(R.layout.toast_oval, null)
        toastView.findViewById<TextView>(R.id.tvToast).text = message
        Toast(this).apply {
            duration = Toast.LENGTH_SHORT
            view = toastView
        }.show()
    }

    private val actionModeCallback = object : AppCompatActionMode.Callback {
        override fun onCreateActionMode(mode: AppCompatActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.context_menu, menu)
            applyActionModeBarStyle()
            return true
        }

        override fun onPrepareActionMode(mode: AppCompatActionMode, menu: Menu): Boolean = false

        override fun onActionItemClicked(mode: AppCompatActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_delete -> {
                    // 从后往前删，避免索引错位
                    var deleted = 0
                    for (i in data.indices.reversed()) {
                        if (selected.get(i, false)) {
                            data.removeAt(i)
                            deleted++
                        }
                    }
                    selected.clear()
                    adapter.notifyDataSetChanged()
                    mode.finish()
                    if (deleted > 0) showOvalToast(getString(R.string.toast_deleted, deleted))
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: AppCompatActionMode) {
            selected.clear()
            actionMode = null
            adapter.notifyDataSetChanged()
        }
    }
}
