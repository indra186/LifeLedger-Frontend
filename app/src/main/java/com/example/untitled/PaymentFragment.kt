package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentPaymentBinding
import com.example.untitled.models.*
import com.example.untitled.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject


class PaymentFragment : Fragment(), PaymentResultListener {


    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private var paymentMethod = "card"
    private var selectedAmount = 99

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* -----------------------------
           1️⃣ PLAN DROPDOWN (Spinner)
        ------------------------------ */

        val plans = listOf(
            "Annual – ₹99 / year",
            "Monthly – ₹10 / month"
        )

        binding.spinnerPlan.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            plans
        )

        // Default → Annual
        binding.spinnerPlan.setSelection(0)
        selectedAmount = 99

        binding.spinnerPlan.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedAmount = if (position == 0) 99 else 10
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        /* -----------------------------
           2️⃣ PAYMENT METHOD SELECTION
        ------------------------------ */
//        binding.cardMethodCard.setOnClickListener { selectCard() }
//        binding.cardMethodUpi.setOnClickListener { selectUpi() }
//
//        // Default selection
//        selectCard()
        /* -----------------------------
           3️⃣ COMPLETE PAYMENT BUTTON
        ------------------------------ */
        binding.btnCompletePayment.setOnClickListener {
            startRazorpayPayment()
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun startRazorpayPayment() {

        val checkout = Checkout()
        checkout.setKeyID("rzp_test_xxxxxxxx") // TEST key

        val options = JSONObject()
        options.put("name", "LifeLedger Premium")
        options.put("description", "Premium Subscription")
        options.put("currency", "INR")

        // Amount must be in paise
        options.put("amount", selectedAmount * 100)

        val prefill = JSONObject()
        prefill.put("email", "user@email.com")
        prefill.put("contact", "9999999999")

        options.put("prefill", prefill)

        try {
            checkout.open(requireActivity(), options)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to start payment", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        if (razorpayPaymentId == null) return
        sendPaymentToBackend(razorpayPaymentId)
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(context, "Payment Failed", Toast.LENGTH_LONG).show()
    }

    private fun sendPaymentToBackend(paymentId: String) {

        val request = PaymentRequest(
            amount = selectedAmount.toDouble(),
            method = "razorpay",
            gateway_payment_id = paymentId
        )

        RetrofitClient.instance.processPayment(request)
            .enqueue(object : Callback<GenericResponse> {

                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    Toast.makeText(context, "Payment Successful", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(context, "Verification Failed", Toast.LENGTH_LONG).show()
                }
            })
    }


//    private fun selectCard() {
//        paymentMethod = "card"
//        binding.layoutCardDetails.visibility = View.VISIBLE
//        binding.etUpiId.visibility = View.GONE
//        binding.icCardSelected.visibility = View.VISIBLE
//        binding.icUpiSelected.visibility = View.GONE
//    }
//
//    private fun selectUpi() {
//        paymentMethod = "upi"
//        binding.layoutCardDetails.visibility = View.GONE
//        binding.etUpiId.visibility = View.VISIBLE
//        binding.icCardSelected.visibility = View.GONE
//        binding.icUpiSelected.visibility = View.VISIBLE
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
