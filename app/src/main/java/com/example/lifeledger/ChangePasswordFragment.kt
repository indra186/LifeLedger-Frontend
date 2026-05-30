package com.example.lifeledger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lifeledger.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment : Fragment() {

    private var _binding:
            FragmentChangePasswordBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding =
            FragmentChangePasswordBinding.inflate(
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

        binding.ivBack.setOnClickListener {

            findNavController().navigateUp()
        }

        binding.btnUpdatePassword.setOnClickListener {

            val current =
                binding.etCurrentPassword.text.toString()

            val newPass =
                binding.etNewPassword.text.toString()

            val confirm =
                binding.etConfirmPassword.text.toString()

            if(
                current.isEmpty() ||
                newPass.isEmpty() ||
                confirm.isEmpty()
            ) {

                Toast.makeText(
                    context,
                    "Fill all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if(newPass != confirm) {

                Toast.makeText(
                    context,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }
            val prefs =
                requireActivity()
                    .getSharedPreferences(
                        "UserPrefs",
                        android.content.Context.MODE_PRIVATE
                    )

            val userId =
                prefs.getString(
                    "userId",
                    "0"
                )?.toInt() ?: 0

            val request =
                com.example.lifeledger.models.ChangePasswordRequest(

                    userId,
                    current,
                    newPass
                )

            com.example.lifeledger.network.RetrofitClient
                .instance
                .changePassword(request)

                .enqueue(object :
                    retrofit2.Callback<com.example.lifeledger.models.BasicApiResponse> {

                    override fun onResponse(
                        call: retrofit2.Call<com.example.lifeledger.models.BasicApiResponse>,
                        response: retrofit2.Response<com.example.lifeledger.models.BasicApiResponse>
                    ) {

                        if(
                            response.isSuccessful &&
                            response.body()?.success == true
                        ) {

                            Toast.makeText(
                                context,
                                "Password updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            findNavController().navigateUp()

                        } else {

                            Toast.makeText(
                                context,
                                response.body()?.message
                                    ?: "Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: retrofit2.Call<com.example.lifeledger.models.BasicApiResponse>,
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
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}