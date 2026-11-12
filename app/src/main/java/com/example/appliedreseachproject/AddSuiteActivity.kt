package com.example.appliedreseachproject

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AddSuiteActivity : AppCompatActivity() {

    private lateinit var etSuiteName: EditText
    private lateinit var etSuiteAddress: EditText
    private lateinit var etTaskTitle: EditText
    private lateinit var containerTasksList: LinearLayout
    private val manualTasks = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_suite)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        etSuiteName = findViewById(R.id.etSuiteName)
        etSuiteAddress = findViewById(R.id.etSuiteAddress)
        etTaskTitle = findViewById(R.id.etTaskTitle)
        containerTasksList = findViewById(R.id.containerTasksList)
        val btnAddTask = findViewById<MaterialButton>(R.id.btnAddTask)
        val btnLoadDefaultTasks = findViewById<MaterialButton>(R.id.btnLoadDefaultTasks)
        val btnCreateSuite = findViewById<MaterialButton>(R.id.btnCreateSuite)

        btnBack.setOnClickListener {
            finish()
        }

        btnAddTask.setOnClickListener {
            val taskText = etTaskTitle.text.toString().trim()
            if (taskText.isNotEmpty()) {
                addTask(taskText)
                etTaskTitle.text?.clear()
            } else {
                Toast.makeText(this, getString(R.string.msg_enter_task), Toast.LENGTH_SHORT).show()
            }
        }

        btnLoadDefaultTasks.setOnClickListener {
            loadDefaultTasks()
        }

        btnCreateSuite.setOnClickListener {
            createAndSaveSuite()
        }
    }

    private fun addTask(taskText: String) {
        manualTasks.add(taskText)
        renderTasksList()
    }

    private fun loadDefaultTasks() {
        val defaultTasks = listOf(
            getString(R.string.task_strip_bed),
            getString(R.string.task_clean_bathroom),
            getString(R.string.task_replace_towels),
            getString(R.string.task_vacuum_mop),
            getString(R.string.task_dust_surfaces),
            getString(R.string.task_empty_trash),
            getString(R.string.task_restock_amenities)
        )
        for (task in defaultTasks) {
            if (!manualTasks.contains(task)) {
                manualTasks.add(task)
            }
        }
        renderTasksList()
        Toast.makeText(this, getString(R.string.msg_default_tasks_loaded), Toast.LENGTH_SHORT).show()
    }

    private fun renderTasksList() {
        containerTasksList.removeAllViews()
        for ((index, taskText) in manualTasks.withIndex()) {
            val itemView = LayoutInflater.from(this).inflate(R.layout.item_add_task_row, containerTasksList, false)
            val tvTaskText = itemView.findViewById<TextView>(R.id.tvTaskText)
            val btnRemoveTask = itemView.findViewById<ImageButton>(R.id.btnRemoveTask)

            tvTaskText.text = taskText
            btnRemoveTask.setOnClickListener {
                manualTasks.removeAt(index)
                renderTasksList()
            }
            containerTasksList.addView(itemView)
        }
    }

    private fun createAndSaveSuite() {
        val name = etSuiteName.text.toString().trim()
        val address = etSuiteAddress.text.toString().trim()

        if (name.isEmpty()) {
            etSuiteName.error = getString(R.string.error_required)
            return
        }

        if (address.isEmpty()) {
            etSuiteAddress.error = getString(R.string.error_required)
            return
        }

        if (manualTasks.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_add_at_least_one_task), Toast.LENGTH_SHORT).show()
            return
        }

        val suiteId = "suite_${System.currentTimeMillis()}"
        val housekeepingTasks = manualTasks.mapIndexed { i, title ->
            HousekeepingTask(id = "${suiteId}_$i", title = title, isCompleted = false)
        }.toMutableList()

        val newSuite = Suite(
            id = suiteId,
            name = name,
            address = address,
            isCompleted = false,
            tasks = housekeepingTasks
        )

        SuiteRepository.addSuite(newSuite)
        Toast.makeText(this, getString(R.string.msg_suite_created, name), Toast.LENGTH_SHORT).show()
        finish()
    }
}
