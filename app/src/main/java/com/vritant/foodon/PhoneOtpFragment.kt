package com.vritant.foodon

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.vritant.foodon.chefUtils.ChefPanel
import kotlinx.android.synthetic.main.fragment_phone_otp.*
import java.util.concurrent.TimeUnit


class PhoneOtpFragment(phone : String) : Fragment() {

    private val phoneNumber: String = phone
    private lateinit var firebaseAuth: FirebaseAuth
    private var verificationId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        Resendotp.visibility = View.INVISIBLE
        text.visibility = View.INVISIBLE

        sendVerificationCode(phoneNumber)

        Verify.setOnClickListener {
            val otp = code.text.toString().trim()
            Resendotp.visibility = View.INVISIBLE

            if(otp.isEmpty() && otp.length<6)
            {
                code.error = "Enter Code"
                code.requestFocus()
                return@setOnClickListener
            }

            verifyCode(otp)
        }

        object : CountDownTimer(6000,1000)
        {
            override fun onTick(p0: Long) {
                text.visibility = View.VISIBLE
                text.text = "Resend Code Within "+p0/1000+" Seconds"
            }

            override fun onFinish() {
                Resendotp.visibility = View.VISIBLE
                text.visibility = View.INVISIBLE
            }

        }.start()

        Resendotp.setOnClickListener {
            Resendotp.visibility = View.INVISIBLE

            resendOtp(phoneNumber)

            object : CountDownTimer(6000,1000)
            {
                override fun onTick(p0: Long) {
                    text.visibility = View.VISIBLE
                    text.text = "Resend Code Within "+p0/1000+" Seconds"
                }

                override fun onFinish() {
                    Resendotp.visibility = View.VISIBLE
                    text.visibility = View.INVISIBLE
                }

            }.start()
        }
    }

    private fun resendOtp(phoneNum : String)
    {
        sendVerificationCode(phoneNum)
    }

    private fun sendVerificationCode(number : String)
    {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(number)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setCallbacks(mCallbacks)
            .setActivity(requireActivity())
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallbacks = object  : PhoneAuthProvider.OnVerificationStateChangedCallbacks()
    {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            val otpCode = p0.smsCode
            if(otpCode!=null)
            {
                code.setText(otpCode)
                verifyCode(otpCode)
            }
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Toast.makeText(context,p0.message,Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            verificationId = p0
        }
    }

    private fun verifyCode(otp : String)
    {
        val credential = PhoneAuthProvider.getCredential(verificationId!!,otp)
        signInWithPhone(credential)
    }

    private fun signInWithPhone(credential: PhoneAuthCredential)
    {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful)
            {
                val intent = Intent(context, ChefPanel::class.java)
                startActivity(intent)
                activity?.finish()
            }
            else
            {
                val build = context?.let { it1 -> AlertDialog.Builder(it1) }
                build?.setCancelable(false)
                build?.setPositiveButton(
                    "OK",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    })?.setTitle("Error")?.setMessage(it.exception?.message)?.show()
            }
        }
    }
}