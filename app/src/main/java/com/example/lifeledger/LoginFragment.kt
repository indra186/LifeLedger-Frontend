package com.example.lifeledger

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
import com.example.lifeledger.models.LoginRequest
import com.example.lifeledger.models.LoginResponse
import com.example.lifeledger.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.lifeledger.models.GoogleLoginRequest
import kotlinx.coroutines.launch
class LoginFragment : Fragment() {

    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        etEmail = view.findViewById(R.id.et_email)
        etPass = view.findViewById(R.id.et_password)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)
        val tvSignUp = view.findViewById<TextView>(R.id.tv_signup)
        val tvForgot =
            view.findViewById<TextView>(
                R.id.tv_forgot_password
            )
        val btnGoogle =
            view.findViewById<View>(
                R.id.btn_google
            )
        btnGoogle.setOnClickListener {

            signInWithGoogle()
        }
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPass.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(email, password)
            Log.d("LOGIN_DEBUG", "Request -> Email: $email Password: $password")

            RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    if (!isAdded) return

                    if (
                        response.isSuccessful &&
                        response.body()?.success == true
                    ) {

                        val userData =
                            response.body()!!.data!!

                        Log.d(
                            "LOGIN_DEBUG",
                            "NAME = ${userData.name}"
                        )

                        Log.d(
                            "LOGIN_DEBUG",
                            "EMAIL = ${userData.email}"
                        )

                        val prefs =
                            requireActivity()
                                .getSharedPreferences(
                                    "UserPrefs",
                                    Context.MODE_PRIVATE
                                )

                        prefs.edit()

                            .putString(
                                "token",
                                userData.authToken
                            )

                            .putString(
                                "userId",
                                userData.userId.toString()
                            )

                            .putString(
                                "name",
                                userData.name
                            )

                            .putString(
                                "email",
                                userData.email
                            )

                            .apply()

                        RetrofitClient.setAuthToken(
                            userData.authToken
                        )

                        Toast.makeText(
                            context,
                            "Login Successful",
                            Toast.LENGTH_SHORT
                        ).show()

                        findNavController().navigate(
                            R.id.action_loginFragment_to_dashboardFragment
                        )

                    } else {

                        Toast.makeText(
                            context,
                            response.body()?.message
                                ?: "Login failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("LOGIN_DEBUG", "Failure: ${t.message}", t)
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
        tvForgot.setOnClickListener {

            findNavController().navigate(
                R.id.forgotPasswordFragment
            )
        }
    }
    private fun signInWithGoogle() {

        val googleIdOption = GetGoogleIdOption.Builder()

            .setFilterByAuthorizedAccounts(false)

            .setServerClientId(
                getString(R.string.default_web_client_id)
            )

            .build()

        val request = GetCredentialRequest.Builder()

            .addCredentialOption(googleIdOption)

            .build()

        val credentialManager =
            CredentialManager.create(requireContext())

        lifecycleScope.launch {

            try {

                val result = credentialManager.getCredential(
                    requireContext(),
                    request
                )

                val credential = result.credential

                if (
                    credential is CustomCredential &&
                    credential.type ==
                    GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {

                    val googleCredential =
                        GoogleIdTokenCredential
                            .createFrom(credential.data)

                    firebaseAuthWithGoogle(
                        googleCredential.idToken
                    )
                }

            } catch (e: Exception) {

                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun firebaseAuthWithGoogle(
        idToken: String
    ) {

        val credential =
            GoogleAuthProvider.getCredential(
                idToken,
                null
            )

        auth.signInWithCredential(credential)

            .addOnCompleteListener(requireActivity()) { task ->

                if(task.isSuccessful) {

                    val user =
                        auth.currentUser

                    val name =
                        user?.displayName ?: ""

                    val email =
                        user?.email ?: ""

                    RetrofitClient.instance.googleLogin(

                        GoogleLoginRequest(
                            name,
                            email
                        )

                    ).enqueue(object : Callback<LoginResponse> {

                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: Response<LoginResponse>
                        ) {

                            if(
                                response.isSuccessful &&
                                response.body()?.success == true
                            ) {

                                val userData =
                                    response.body()!!.data!!

                                val prefs =
                                    requireActivity()
                                        .getSharedPreferences(
                                            "UserPrefs",
                                            Context.MODE_PRIVATE
                                        )

                                prefs.edit()

                                    .putString(
                                        "token",
                                        userData.authToken
                                    )

                                    .putString(
                                        "userId",
                                        userData.userId.toString()
                                    )

                                    .putString(
                                        "name",
                                        userData.name
                                    )

                                    .putString(
                                        "email",
                                        userData.email
                                    )

                                    .apply()

                                RetrofitClient.setAuthToken(
                                    userData.authToken
                                )

                                Toast.makeText(
                                    context,
                                    "Google Login Success",
                                    Toast.LENGTH_SHORT
                                ).show()

                                findNavController().navigate(
                                    R.id.action_loginFragment_to_dashboardFragment
                                )

                            } else {

                                Toast.makeText(
                                    context,
                                    "Backend login failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(
                            call: Call<LoginResponse>,
                            t: Throwable
                        ) {

                            Toast.makeText(
                                context,
                                t.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

                } else {

                    Toast.makeText(
                        context,
                        "Google Login Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
