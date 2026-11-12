package com.example.appliedreseachproject

data class HousekeepingTask(
    val id: String,
    val title: String,
    var isCompleted: Boolean = false,
    var photoUri: String? = null
)
