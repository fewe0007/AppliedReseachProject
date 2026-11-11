package com.example.appliedreseachproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var btnAssignedSuites: MaterialButton
    private lateinit var btnCompletedSuites: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAssignedSuites = findViewById(R.id.btnAssignedSuites)
        btnCompletedSuites = findViewById(R.id.btnCompletedSuites)
        val btnSyncServer = findViewById<MaterialButton>(R.id.btnSyncServer)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        btnAssignedSuites.setOnClickListener {
            val intent = Intent(this, AssignedSuitesActivity::class.java)
            startActivity(intent)
        }

        btnCompletedSuites.setOnClickListener {
            val intent = Intent(this, CompletedSuitesActivity::class.java)
            startActivity(intent)
        }

        btnSyncServer.setOnClickListener {
            Toast.makeText(this, getString(R.string.msg_sync_server), Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            Toast.makeText(this, getString(R.string.msg_logout), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        updateButtonBadges()
    }

    private fun updateButtonBadges() {
        val assignedCount = SuiteRepository.getAssignedSuites().size
        val completedCount = SuiteRepository.getCompletedSuites().size

        btnAssignedSuites.text = getString(R.string.btn_assigned_suites_count, assignedCount)
        btnCompletedSuites.text = getString(R.string.btn_completed_suites_count, completedCount)
    }
}
