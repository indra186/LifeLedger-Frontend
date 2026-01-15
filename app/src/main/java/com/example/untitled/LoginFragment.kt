package com.example.untitled

import android.content.Context
import android.os.Bundle
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
import com.example.untitled.models.LoginRequest
import com.example.untitled.models.LoginResponse
import com.example.untitled.network.RetrofitClient
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etEmail = view.findViewById(R.id.et_email)
        etPass = view.findViewById(R.id.et_password)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)
        val tvSignUp = view.findViewById<TextView>(R.id.tv_signup)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPass.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(email, password)

            RetrofitClient.instance.login(request).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (!isAdded) return

                    if (response.isSuccessful && response.body() != null) {
                        val responseBodyString = response.body()!!.string()
                        Log.d("LoginFragment", "Raw response: $responseBodyString")

                        try {
                            // Check if response is a JSON object
                            if (responseBodyString.trim().startsWith("{")) {
                                val loginResponse = Gson().fromJson(responseBodyString, LoginResponse::class.java)
                                
                                if (loginResponse.success && loginResponse.data != null) {
                                    val userData = loginResponse.data!!
                                    val sharedPreferences =
                                        requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    val token = userData.authToken
                                    val userId = userData.userId.toString()

                                    editor.putString("token", token)
                                    editor.putString("userId", userId)
                                    editor.apply()

                                    RetrofitClient.setAuthToken(token)


                                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
                                } else {
                                    Toast.makeText(context, loginResponse.message, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // Handle non-JSON response (likely an error message from PHP)
                                val errorMessage = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    android.text.Html.fromHtml(responseBodyString, android.text.Html.FROM_HTML_MODE_LEGACY)
                                } else {
                                    @Suppress("DEPRECATION")
                                    android.text.Html.fromHtml(responseBodyString)
                                }

                                context?.let { ctx ->
                                    androidx.appcompat.app.AlertDialog.Builder(ctx)
                                        .setTitle("Server Error")
                                        .setMessage(errorMessage)
                                        .setPositiveButton("OK", null)
                                        .show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("LoginFragment", "JSON Parsing error", e)
                            Toast.makeText(context, "Error parsing response: $responseBodyString", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Handle non-200 responses
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        Toast.makeText(context, "Login failed: $errorBody (${response.code()})", Toast.LENGTH_LONG).show()
                        Log.e("LoginError", "Code: ${response.code()}, Body: $errorBody")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (isAdded) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("LoginFailure", t.message ?: "Unknown error")
                    }
                }
            })
        }

        tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
    }
}
