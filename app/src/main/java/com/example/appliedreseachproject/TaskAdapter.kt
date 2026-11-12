package com.example.appliedreseachproject

import android.graphics.BitmapFactory
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.io.File

class TaskAdapter(
    private val tasks: List<HousekeepingTask>,
    private val isReadOnly: Boolean = false,
    private val onTaskChanged: (HousekeepingTask) -> Unit,
    private val onAttachPhotoClick: (HousekeepingTask) -> Unit,
    private val onViewPhotoClick: (HousekeepingTask) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbTask: CheckBox = itemView.findViewById(R.id.cbTask)
        val tvTaskTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        val btnAttachPhoto: MaterialButton = itemView.findViewById(R.id.btnAttachPhoto)
        val layoutPhotoPreview: LinearLayout = itemView.findViewById(R.id.layoutPhotoPreview)
        val ivPhotoThumbnail: ImageView = itemView.findViewById(R.id.ivPhotoThumbnail)
        val tvPhotoStatus: TextView = itemView.findViewById(R.id.tvPhotoStatus)
        val btnViewPhoto: TextView = itemView.findViewById(R.id.btnViewPhoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_housekeeping_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.tvTaskTitle.text = task.title

        holder.cbTask.setOnCheckedChangeListener(null)
        holder.cbTask.isChecked = task.isCompleted
        holder.cbTask.isEnabled = !isReadOnly

        updateStrikethrough(holder.tvTaskTitle, task.isCompleted)

        if (!isReadOnly) {
            holder.cbTask.setOnCheckedChangeListener { _, isChecked ->
                task.isCompleted = isChecked
                updateStrikethrough(holder.tvTaskTitle, isChecked)
                onTaskChanged(task)
            }

            holder.itemView.setOnClickListener {
                holder.cbTask.isChecked = !holder.cbTask.isChecked
            }

            holder.btnAttachPhoto.visibility = View.VISIBLE
            holder.btnAttachPhoto.setOnClickListener {
                onAttachPhotoClick(task)
            }
        } else {
            holder.itemView.setOnClickListener(null)
            holder.btnAttachPhoto.visibility = if (task.photoUri == null) View.GONE else View.VISIBLE
            holder.btnAttachPhoto.text = holder.itemView.context.getString(R.string.btn_change_photo)
            holder.btnAttachPhoto.setOnClickListener {
                onAttachPhotoClick(task)
            }
        }

        if (!task.photoUri.isNullOrEmpty()) {
            holder.layoutPhotoPreview.visibility = View.VISIBLE
            holder.btnAttachPhoto.text = holder.itemView.context.getString(R.string.btn_change_photo)

            val photoFile = File(task.photoUri!!)
            if (photoFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                holder.ivPhotoThumbnail.setImageBitmap(bitmap)
            }

            holder.btnViewPhoto.setOnClickListener {
                onViewPhotoClick(task)
            }
            holder.ivPhotoThumbnail.setOnClickListener {
                onViewPhotoClick(task)
            }
        } else {
            holder.layoutPhotoPreview.visibility = View.GONE
            holder.btnAttachPhoto.text = holder.itemView.context.getString(R.string.btn_add_photo)
        }
    }

    private fun updateStrikethrough(textView: TextView, isCompleted: Boolean) {
        if (isCompleted) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textView.alpha = 0.6f
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textView.alpha = 1.0f
        }
    }

    override fun getItemCount(): Int = tasks.size
}
