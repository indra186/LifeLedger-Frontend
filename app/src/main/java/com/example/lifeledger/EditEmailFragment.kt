package com.example.lifeledger

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lifeledger.databinding.FragmentEditEmailBinding
import com.example.lifeledger.models.BasicApiResponse
import com.example.lifeledger.models.SendOtpRequest
import com.example.lifeledger.models.SendOtpResponse
import com.example.lifeledger.models.UpdateEmailRequest
import com.example.lifeledger.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditEmailFragment : Fragment() {

    private var _binding:
            FragmentEditEmailBinding? = null

    private val binding get() = _binding!!

    /*
    TRACK OTP STATE
    */

    private var otpSent = false

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding =
            FragmentEditEmailBinding.inflate(
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

        binding.btnUpdateEmail.setOnClickListener {

            if(!otpSent) {

                sendOtp()

            } else {

                verifyAndUpdateEmail()
            }
        }
    }

    /*
    SEND OTP
    */

    private fun sendOtp() {

        val email =
            binding.etEmail.text
                .toString()
                .trim()

        if(email.isEmpty()) {

            Toast.makeText(
                context,
                "Enter email",
                Toast.LENGTH_SHORT
            ).show()

            return
        }
        android.util.Log.d(
            "OTP_DEBUG",
            "Sending OTP to: $email"
        )
        RetrofitClient.instance.sendOtp(

            SendOtpRequest(email)

        ).enqueue(object :
            Callback<SendOtpResponse> {

            override fun onResponse(
                call: Call<SendOtpResponse>,
                response: Response<SendOtpResponse>
            ) {android.util.Log.d(
                "OTP_DEBUG",
                "CODE = ${response.code()}"
            )

                android.util.Log.d(
                    "OTP_DEBUG",
                    "BODY = ${response.body()}"
                )

                if(response.isSuccessful &&
                    response.body()?.success == true) {

                    otpSent = true

                    /*
                    SHOW OTP FIELD
                    */

                    binding.etOtp.visibility =
                        View.VISIBLE

                    /*
                    CHANGE BUTTON TEXT
                    */

                    binding.btnUpdateEmail.text =
                        "Verify & Update"

                    Toast.makeText(
                        context,
                        "OTP sent successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        context,
                        "Failed to send OTP",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<SendOtpResponse>,
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

    /*
    VERIFY + UPDATE
    */

    private fun verifyAndUpdateEmail() {

        val email =
            binding.etEmail.text
                .toString()
                .trim()

        val otp =
            binding.etOtp.text
                .toString()
                .trim()

        if(otp.isEmpty()) {

            Toast.makeText(
                context,
                "Enter OTP",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val prefs =
            requireActivity()
                .getSharedPreferences(
                    "UserPrefs",
                    Context.MODE_PRIVATE
                )

        val userId =
            prefs.getString(
                "userId",
                "0"
            )?.toInt() ?: 0

        val request =
            UpdateEmailRequest(
                userId,
                email,
                otp
            )

        RetrofitClient.instance
            .updateEmail(request)

            .enqueue(object :
                Callback<BasicApiResponse> {

                override fun onResponse(
                    call: Call<BasicApiResponse>,
                    response: Response<BasicApiResponse>
                ) {

                    if(response.isSuccessful &&
                        response.body()?.success == true) {

                        /*
                        UPDATE LOCAL EMAIL
                        */

                        prefs.edit()
                            .putString(
                                "email",
                                email
                            )
                            .apply()

                        Toast.makeText(
                            context,
                            "Email updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        findNavController()
                            .navigateUp()

                    } else {

                        Toast.makeText(
                            context,
                            response.body()?.message
                                ?: "Update failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<BasicApiResponse>,
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