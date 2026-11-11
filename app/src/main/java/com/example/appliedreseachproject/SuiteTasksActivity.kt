package com.example.appliedreseachproject

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class SuiteTasksActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SUITE_ID = "extra_suite_id"
    }

    private lateinit var tvProgress: TextView
    private var suite: Suite? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suite_tasks)

        val suiteId = intent.getStringExtra(EXTRA_SUITE_ID) ?: ""
        suite = SuiteRepository.getSuiteById(suiteId)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val tvTitle = findViewById<TextView>(R.id.tvSuiteTitle)
        val tvAddress = findViewById<TextView>(R.id.tvSuiteAddress)
        tvProgress = findViewById(R.id.tvProgress)
        val rvTasks = findViewById<RecyclerView>(R.id.rvTasks)
        val btnSaveProgress = findViewById<MaterialButton>(R.id.btnSaveProgress)

        val currentSuite = suite
        if (currentSuite == null) {
            Toast.makeText(this, "Suite not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvTitle.text = currentSuite.name
        tvAddress.text = currentSuite.address

        btnBack.setOnClickListener {
            finish()
        }

        val adapter = TaskAdapter(currentSuite.tasks) { _ ->
            updateProgressText()
        }

        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = adapter

        updateProgressText()

        btnSaveProgress.setOnClickListener {
            val allCompleted = currentSuite.tasks.all { it.isCompleted }
            if (allCompleted) {
                SuiteRepository.markSuiteCompleted(currentSuite.id)
                Toast.makeText(
                    this,
                    getString(R.string.msg_suite_completed_moved, currentSuite.name),
                    Toast.LENGTH_LONG
                ).show()

                // Return directly to the Main Menu (MainActivity)
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
                finish()
            } else {
                val completedCount = currentSuite.tasks.count { it.isCompleted }
                val totalCount = currentSuite.tasks.size
                Toast.makeText(
                    this,
                    getString(R.string.msg_tasks_saved, completedCount, totalCount),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun updateProgressText() {
        val currentSuite = suite ?: return
        val completed = currentSuite.tasks.count { it.isCompleted }
        val total = currentSuite.tasks.size
        tvProgress.text = getString(R.string.progress_tasks, completed, total)
    }
}
