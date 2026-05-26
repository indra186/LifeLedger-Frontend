package com.example.untitled.models

data class AgentAction(
    val strategy: String,
    val action: String,
    val urgency: String,
    val isCritical: Boolean
)
data class AgentTask(
    val goalId: String,
    val strategy: String,
    val action: String,
    val status: TaskStatus,
    val createdAt: Long,
    val lastUpdated: Long,

    val result: String?,        // ✅ ADD THIS
    val retry_count: Int?
)
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,

    COMPLETED,
    FAILED;

    companion object {
        fun from(value: String): TaskStatus {
            return try {
                valueOf(value)
            } catch (e: Exception) {
                PENDING
            }
        }
    }
}
data class TaskResponse(
    val success: Boolean,
    val data: List<AgentTask>
)