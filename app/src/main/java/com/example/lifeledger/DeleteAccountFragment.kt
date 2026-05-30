package com.example.lifeledger

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lifeledger.databinding.FragmentDeleteAccountBinding
import com.example.lifeledger.models.DeleteAccountResponse
import com.example.lifeledger.network.RetrofitClient

class DeleteAccountFragment : Fragment() {

    private var _binding:
            FragmentDeleteAccountBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding =
            FragmentDeleteAccountBinding.inflate(
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

        binding.btnDelete.setOnClickListener {

            RetrofitClient.instance
                .deleteAccount()

                .enqueue(object :
                    retrofit2.Callback<DeleteAccountResponse> {

                    override fun onResponse(

                        call: retrofit2.Call<DeleteAccountResponse>,
                        response: retrofit2.Response<DeleteAccountResponse>

                    ) {

                        if(response.isSuccessful &&
                            response.body()?.success == true
                        ) {

                            val prefs =
                                requireActivity()
                                    .getSharedPreferences(
                                        "UserPrefs",
                                        Context.MODE_PRIVATE
                                    )

                            prefs.edit().clear().apply()

                            RetrofitClient.setAuthToken(null)

                            Toast.makeText(
                                context,
                                "Account deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            findNavController().navigate(
                                R.id.loginFragment
                            )

                        } else {

                            Toast.makeText(
                                context,
                                "Failed to delete account",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(

                        call: retrofit2.Call<DeleteAccountResponse>,
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