package com.example.untitled

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.untitled.models.VerifyOtpRequest
import com.example.untitled.models.VerifyOtpResponse
import com.example.untitled.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpFragment : Fragment() {

    private lateinit var etOtp1: EditText
    private lateinit var etOtp2: EditText
    private lateinit var etOtp3: EditText
    private lateinit var etOtp4: EditText
    private lateinit var etOtp5: EditText
    private lateinit var etOtp6: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etOtp1 = view.findViewById(R.id.et_otp_1)
        etOtp2 = view.findViewById(R.id.et_otp_2)
        etOtp3 = view.findViewById(R.id.et_otp_3)
        etOtp4 = view.findViewById(R.id.et_otp_4)
        etOtp5 = view.findViewById(R.id.et_otp_5)
        etOtp6 = view.findViewById(R.id.et_otp_6)
        
        setupOtpInputs()

        view.findViewById<Button>(R.id.btn_verify).setOnClickListener {
            val otpCode = "${etOtp1.text}${etOtp2.text}${etOtp3.text}${etOtp4.text}${etOtp5.text}${etOtp6.text}"
            
            if (otpCode.length < 6) {
                Toast.makeText(context, "Please enter complete OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // In a real scenario, you'd get the email passed from the previous fragment
            val email = "user@example.com" // TODO: Get email from arguments

            val request = VerifyOtpRequest(email = email, otp = otpCode)
            
            RetrofitClient.instance.verifyOtp(request).enqueue(object : Callback<VerifyOtpResponse> {
                override fun onResponse(
                    call: Call<VerifyOtpResponse>,
                    response: Response<VerifyOtpResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                         val verifyResponse = response.body()!!
                         if (verifyResponse.success) {
                             Toast.makeText(context, verifyResponse.message, Toast.LENGTH_SHORT).show()
                             findNavController().navigate(R.id.action_otpFragment_to_dashboardFragment)
                         } else {
                             Toast.makeText(context, verifyResponse.message, Toast.LENGTH_SHORT).show()
                         }
                    } else {
                        Toast.makeText(context, "Verification failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setupOtpInputs() {
        val editTexts = listOf(etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6)
        
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                     if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }
}
