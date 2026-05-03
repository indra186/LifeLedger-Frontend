package com.example.untitled.utils

import android.util.Log
import com.example.untitled.models.TaskResponse
import com.example.untitled.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AgentRunner {

    fun fetchTasks(
        goalId: Int,
        api: ApiService,
        onUpdate: (String) -> Unit
    ) {

        Log.d("AI_DEBUG", "Fetching tasks for goalId: $goalId")

        api.getTasks(goalId).enqueue(object : Callback<TaskResponse> {

            override fun onResponse(
                call: Call<TaskResponse>,
                response: Response<TaskResponse>
            ) {

                Log.d("AI_DEBUG", "Raw response = ${response.body()}")

                if (response.isSuccessful && response.body()?.success == true) {

                    val tasks = response.body()?.data ?: emptyList()

                    if (tasks.isEmpty()) {
                        onUpdate("No active AI tasks")
                        return
                    }

                    val text = tasks.joinToString("\n\n") {
                        """
🧠 Strategy: ${it.strategy}
⚡ Action: ${it.action}
📊 Status: ${it.status}
""".trimIndent()
                    }

                    onUpdate(text)

                } else {
                    onUpdate("Failed to load AI tasks")
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Log.e("AI_DEBUG", "Error: ${t.message}")
                onUpdate("Error: ${t.message}")
            }
        })
    }
}