package com.example.appliedreseachproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CompletedSuitesActivity : AppCompatActivity() {

    private lateinit var rvCompletedSuites: RecyclerView
    private lateinit var tvEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_suites)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        rvCompletedSuites = findViewById(R.id.rvCompletedSuites)
        tvEmptyState = findViewById(R.id.tvEmptyState)

        btnBack.setOnClickListener {
            finish()
        }

        rvCompletedSuites.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        loadCompletedSuites()
    }

    private fun loadCompletedSuites() {
        val completedSuites = SuiteRepository.getCompletedSuites()

        if (completedSuites.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            rvCompletedSuites.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            rvCompletedSuites.visibility = View.VISIBLE

            rvCompletedSuites.adapter = SuiteAdapter(completedSuites, isCompletedView = true) { suite ->
                val intent = Intent(this, SuiteTasksActivity::class.java).apply {
                    putExtra(SuiteTasksActivity.EXTRA_SUITE_ID, suite.id)
                }
                startActivity(intent)
            }
        }
    }
}
