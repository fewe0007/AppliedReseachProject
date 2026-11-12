package com.example.appliedreseachproject

object SuiteRepository {

    private val suites = mutableListOf(
        Suite(
            id = "suite_1",
            name = "Suite 1",
            address = "101 Ocean Drive, Suite 1",
            isCompleted = false,
            tasks = mutableListOf(
                HousekeepingTask("1_1", "Strip bed linens and remake with fresh sheets"),
                HousekeepingTask("1_2", "Disinfect and scrub bathroom & mirrors"),
                HousekeepingTask("1_3", "Replace used bath towels and washcloths"),
                HousekeepingTask("1_4", "Vacuum carpets and mop hard surface floors"),
                HousekeepingTask("1_5", "Dust furniture, TV, and window sills"),
                HousekeepingTask("1_6", "Empty trash cans and replace liners"),
                HousekeepingTask("1_7", "Restock toiletries and complimentary items")
            )
        ),
        Suite(
            id = "suite_2",
            name = "Suite 2",
            address = "204 Sunset Blvd, Suite 2",
            isCompleted = false,
            tasks = mutableListOf(
                HousekeepingTask("2_1", "Strip bed linens and remake with fresh sheets"),
                HousekeepingTask("2_2", "Disinfect and scrub bathroom & mirrors"),
                HousekeepingTask("2_3", "Replace used bath towels and washcloths"),
                HousekeepingTask("2_4", "Vacuum carpets and mop hard surface floors"),
                HousekeepingTask("2_5", "Dust furniture, TV, and window sills"),
                HousekeepingTask("2_6", "Empty trash cans and replace liners"),
                HousekeepingTask("2_7", "Restock toiletries and complimentary items")
            )
        )
    )

    fun getAssignedSuites(): List<Suite> {
        return suites.filter { !it.isCompleted }
    }

    fun getCompletedSuites(): List<Suite> {
        return suites.filter { it.isCompleted }
    }

    fun getSuiteById(id: String): Suite? {
        return suites.find { it.id == id }
    }

    fun addSuite(suite: Suite) {
        suites.add(suite)
    }

    fun markSuiteCompleted(id: String) {
        val suite = getSuiteById(id)
        suite?.let {
            it.isCompleted = true
            it.tasks.forEach { task -> task.isCompleted = true }
        }
    }
}
