package com.example.appliedreseachproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AssignedSuitesActivity : AppCompatActivity() {

    private lateinit var rvAssignedSuites: RecyclerView
    private lateinit var tvEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assigned_suites)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        rvAssignedSuites = findViewById(R.id.rvAssignedSuites)
        tvEmptyState = findViewById(R.id.tvEmptyState)

        btnBack.setOnClickListener {
            finish()
        }

        rvAssignedSuites.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        loadAssignedSuites()
    }

    private fun loadAssignedSuites() {
        val assignedSuites = SuiteRepository.getAssignedSuites()

        if (assignedSuites.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            rvAssignedSuites.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            rvAssignedSuites.visibility = View.VISIBLE

            rvAssignedSuites.adapter = SuiteAdapter(assignedSuites, isCompletedView = false) { suite ->
                val intent = Intent(this, SuiteTasksActivity::class.java).apply {
                    putExtra(SuiteTasksActivity.EXTRA_SUITE_ID, suite.id)
                }
                startActivity(intent)
            }
        }
    }
}
