package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.untitled.adapters.NotificationsAdapter
import com.example.untitled.databinding.FragmentNotificationsBinding
import com.example.untitled.models.NotificationResponse
import com.example.untitled.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsFragment : Fragment() {

    private var _binding:
            FragmentNotificationsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter:
            NotificationsAdapter

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding =
            FragmentNotificationsBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        binding.btnBack.setOnClickListener {

            findNavController().navigateUp()
        }

        adapter =
            NotificationsAdapter(
                mutableListOf()
            ) { notification ->

                handleNotificationClick(
                    notification
                )
            }

        binding.rvNotifications.layoutManager =
            LinearLayoutManager(context)

        binding.rvNotifications.adapter =
            adapter
        setupSwipeDelete()

        fetchNotifications()
    }

    private fun fetchNotifications() {

        RetrofitClient.instance
            .getNotifications()
            .enqueue(object :
                Callback<NotificationResponse> {

                override fun onResponse(
                    call: Call<NotificationResponse>,
                    response: Response<NotificationResponse>
                ) {

                    if(
                        response.isSuccessful &&
                        response.body()?.success == true
                    ) {

                        adapter.updateData(
                            response.body()?.data
                                ?: emptyList()
                        )

                    } else {

                        Toast.makeText(
                            context,
                            "Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<NotificationResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        context,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
    private fun handleNotificationClick(
        notification:
        com.example.untitled.models.Notification
    ) {

        when(notification.type) {

            "task_reminder",
            "task_overdue" -> {

                val bundle = Bundle()

                bundle.putInt(
                    "taskId",
                    notification.related_id ?: 0
                )

                findNavController().navigate(
                    R.id.tasksFragment,
                    bundle
                )
            }

            "habit_reminder" -> {

                val bundle = Bundle()

                bundle.putInt(
                    "habitId",
                    notification.related_id ?: 0
                )

                findNavController().navigate(
                    R.id.habitDetailFragment,
                    bundle
                )
            }

            "ai_insight" -> {

                // stay here
            }
        }
    }
    private fun setupSwipeDelete() {

        val callback =

            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or
                        ItemTouchHelper.RIGHT
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {

                    val position =
                        viewHolder.adapterPosition

                    val notification =
                        adapter.getNotification(position)

                    deleteNotification(
                        notification.id,
                        position
                    )
                }
            }

        ItemTouchHelper(callback)
            .attachToRecyclerView(
                binding.rvNotifications
            )
    }
    private fun deleteNotification(

        notificationId: Int,
        position: Int

    ) {

        RetrofitClient.instance
            .deleteNotification(

                com.example.untitled.models
                    .DeleteNotificationRequest(
                        notificationId
                    )
            )

            .enqueue(

                object :
                    Callback<com.example.untitled.models.GenericResponse> {

                    override fun onResponse(
                        call: Call<com.example.untitled.models.GenericResponse>,
                        response: Response<com.example.untitled.models.GenericResponse>
                    ) {

                        if(
                            response.isSuccessful &&
                            response.body()?.success == true
                        ) {

                            adapter.removeNotification(
                                position
                            )
                        }
                    }

                    override fun onFailure(
                        call: Call<com.example.untitled.models.GenericResponse>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            context,
                            "Delete failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}