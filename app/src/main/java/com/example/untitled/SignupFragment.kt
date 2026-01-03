package com.example.untitled

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.untitled.models.SignupRequest
import com.example.untitled.models.SignupResponse
import com.example.untitled.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<EditText>(R.id.et_name)
        val etEmail = view.findViewById<EditText>(R.id.et_email)
        val etPassword = view.findViewById<EditText>(R.id.et_password)
        val btnSignup = view.findViewById<Button>(R.id.btn_signup)
        val tvLogin = view.findViewById<TextView>(R.id.tv_login)
        val cbTerms = view.findViewById<CheckBox>(R.id.cb_terms)

        btnSignup.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!cbTerms.isChecked) {
                Toast.makeText(context, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = SignupRequest(name = name, email = email, pass = password)

            RetrofitClient.instance.signup(request).enqueue(object : Callback<SignupResponse> {
                override fun onResponse(
                    call: Call<SignupResponse>,
                    response: Response<SignupResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val signupResponse = response.body()!!
                        if (signupResponse.success) {
                            Toast.makeText(context, signupResponse.message, Toast.LENGTH_SHORT).show()
                            // Navigate to OTP fragment or Login
                            findNavController().navigate(R.id.action_signupFragment_to_otpFragment)
                        } else {
                             Toast.makeText(context, signupResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Registration failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                        Log.e("SignupError", "Response Code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("SignupFailure", t.message ?: "Unknown error")
                }
            })
        }
        
        tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
    }
}
