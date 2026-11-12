package com.example.appliedreseachproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.io.File
import java.io.FileOutputStream

class SuiteTasksActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SUITE_ID = "extra_suite_id"
    }

    private lateinit var tvProgress: TextView
    private var suite: Suite? = null
    private var activeTaskForPhoto: HousekeepingTask? = null
    private lateinit var adapter: TaskAdapter

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null && activeTaskForPhoto != null) {
            val file = saveBitmapToFile(bitmap, activeTaskForPhoto!!.id)
            if (file != null) {
                activeTaskForPhoto!!.photoUri = file.absolutePath
                adapter.notifyDataSetChanged()
                Toast.makeText(this, getString(R.string.msg_photo_attached), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val getContentLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null && activeTaskForPhoto != null) {
            val file = saveUriToFile(uri, activeTaskForPhoto!!.id)
            if (file != null) {
                activeTaskForPhoto!!.photoUri = file.absolutePath
                adapter.notifyDataSetChanged()
                Toast.makeText(this, getString(R.string.msg_photo_attached), Toast.LENGTH_SHORT).show()
            }
        }
    }

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

        val isReadOnly = currentSuite.isCompleted

        if (isReadOnly) {
            btnSaveProgress.visibility = View.GONE
        }

        adapter = TaskAdapter(
            tasks = currentSuite.tasks,
            isReadOnly = isReadOnly,
            onTaskChanged = { _ ->
                updateProgressText()
            },
            onAttachPhotoClick = { task ->
                activeTaskForPhoto = task
                showPhotoOptionsDialog(task)
            },
            onViewPhotoClick = { task ->
                showPhotoViewDialog(task)
            }
        )

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

    private fun showPhotoOptionsDialog(task: HousekeepingTask) {
        val options = arrayOf(
            getString(R.string.option_take_photo),
            getString(R.string.option_choose_from_files)
        )
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_attach_photo_title, task.title))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePictureLauncher.launch(null)
                    1 -> getContentLauncher.launch("image/*")
                }
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    private fun showPhotoViewDialog(task: HousekeepingTask) {
        val photoUriStr = task.photoUri ?: return
        val file = File(photoUriStr)
        if (!file.exists()) {
            Toast.makeText(this, "Photo file not found", Toast.LENGTH_SHORT).show()
            return
        }

        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_photo_view, null)
        val ivPhoto = dialogView.findViewById<ImageView>(R.id.ivFullPhoto)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvPhotoDialogTitle)

        tvTitle.text = task.title
        ivPhoto.setImageBitmap(bitmap)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton(getString(R.string.btn_close), null)
            .show()
    }

    private fun saveBitmapToFile(bitmap: Bitmap, taskId: String): File? {
        return try {
            val photosDir = File(filesDir, "task_photos").apply { if (!exists()) mkdirs() }
            val photoFile = File(photosDir, "photo_${suite?.id}_${taskId}_${System.currentTimeMillis()}.jpg")
            FileOutputStream(photoFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            photoFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveUriToFile(uri: Uri, taskId: String): File? {
        return try {
            val photosDir = File(filesDir, "task_photos").apply { if (!exists()) mkdirs() }
            val photoFile = File(photosDir, "photo_${suite?.id}_${taskId}_${System.currentTimeMillis()}.jpg")
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(photoFile).use { output ->
                    input.copyTo(output)
                }
            }
            photoFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
