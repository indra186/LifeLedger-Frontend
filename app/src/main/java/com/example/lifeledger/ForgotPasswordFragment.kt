package com.example.lifeledger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lifeledger.databinding.FragmentForgotPasswordBinding
import com.example.lifeledger.models.SendOtpRequest
import com.example.lifeledger.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordFragment : Fragment() {

    private var _binding:
            FragmentForgotPasswordBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding =
            FragmentForgotPasswordBinding.inflate(
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

        binding.btnSendOtp.setOnClickListener {

            val email =
                binding.etEmail.text.toString()

            RetrofitClient.instance
                .sendResetOtp(
                    SendOtpRequest(email)
                ).enqueue(object :
                    Callback<com.example.lifeledger.models.SendOtpResponse> {

                    override fun onResponse(

                        call: Call<com.example.lifeledger.models.SendOtpResponse>,
                        response: Response<com.example.lifeledger.models.SendOtpResponse>

                    ) {

                        if (
                            response.isSuccessful &&
                            response.body()?.success == true
                        ) {

                            Toast.makeText(
                                context,
                                "OTP sent successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            val errorMessage =

                                response.body()?.message
                                    ?: "Email not registered"

                            Toast.makeText(
                                context,
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<com.example.lifeledger.models.SendOtpResponse>,
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

        binding.btnResetPassword.setOnClickListener {

            resetPassword()
        }
    }

    private fun resetPassword() {

        val email =
            binding.etEmail.text.toString()

        val otp =
            binding.etOtp.text.toString()

        val newPassword =
            binding.etNewPassword.text.toString()

        RetrofitClient.instance
            .resetPassword(
                com.example.lifeledger.models.ResetPasswordRequest(
                    email,
                    otp,
                    newPassword
                )
            )

            .enqueue(object :
                Callback<com.example.lifeledger.models.GenericResponse> {

                override fun onResponse(

                    call: Call<com.example.lifeledger.models.GenericResponse>,
                    response: Response<com.example.lifeledger.models.GenericResponse>

                ) {

                    if(response.isSuccessful &&
                        response.body()?.success == true
                    ) {

                        Toast.makeText(
                            context,
                            "Password reset successful",
                            Toast.LENGTH_SHORT
                        ).show()

                        findNavController().navigateUp()

                    } else {

                        Toast.makeText(
                            context,
                            "Invalid OTP",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<com.example.lifeledger.models.GenericResponse>,
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

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}