package com.example.lifeledger

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
import com.example.lifeledger.models.SignupRequest
import com.example.lifeledger.models.SignupResponse
import com.example.lifeledger.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.lifecycle.lifecycleScope
import com.example.lifeledger.models.GoogleLoginRequest
import com.example.lifeledger.models.LoginResponse
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val etName = view.findViewById<EditText>(R.id.et_name)
        val etEmail = view.findViewById<EditText>(R.id.et_email)
        val etPassword = view.findViewById<EditText>(R.id.et_password)
        val btnSignup = view.findViewById<Button>(R.id.btn_signup)
        val tvLogin = view.findViewById<TextView>(R.id.tv_login)
        val cbTerms = view.findViewById<CheckBox>(R.id.cb_terms)
        val ivBack =
            view.findViewById<View>(R.id.iv_back)


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

            val request = SignupRequest(name, email, password)

            RetrofitClient.instance.signup(request).enqueue(object : Callback<SignupResponse> {
                override fun onResponse(
                    call: Call<SignupResponse>,
                    response: Response<SignupResponse>
                ) {
                    if (!isAdded) return

                    if (response.isSuccessful) {
                        val signupResponse = response.body()
                        if (signupResponse != null && signupResponse.success) {
                            val email = signupResponse.data!!.email

                            val bundle = Bundle()
                            bundle.putString("email", email)

                            findNavController().navigate(
                                R.id.action_signupFragment_to_otpFragment,
                                bundle
                            )
                        }
                        else {
                             val msg = signupResponse?.message ?: "Registration failed"
                             Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Registration failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                        Log.e("SignupError", "Response Code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                    if (!isAdded) return
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("SignupFailure", t.message ?: "Unknown error")
                }
            })
        }
        val btnGoogle =
            view.findViewById<View>(R.id.btn_google)

        btnGoogle.setOnClickListener {

            signInWithGoogle()
        }
        tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
        ivBack.setOnClickListener {

            findNavController().navigateUp()
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

                    val prefs =
                        requireActivity()
                            .getSharedPreferences(
                                "UserPrefs",
                                android.content.Context.MODE_PRIVATE
                            )

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
                                            android.content.Context.MODE_PRIVATE
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

                                findNavController().navigate(
                                    R.id.nav_dashboard
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
                        "Google Sign-In Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
