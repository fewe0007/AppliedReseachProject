package com.example.appliedreseachproject

data class Suite(
    val id: String,
    val name: String,
    val address: String,
    var isCompleted: Boolean = false,
    val tasks: MutableList<HousekeepingTask> = mutableListOf()
)
