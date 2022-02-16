package com.vritant.foodon

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_phone_verification.*
import kotlinx.android.synthetic.main.activity_phone_verification.Resendotp
import kotlinx.android.synthetic.main.activity_phone_verification.code
import kotlinx.android.synthetic.main.activity_phone_verification.text
import java.util.concurrent.TimeUnit

class PhoneVerification : AppCompatActivity() {

    private lateinit var verificationId : String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var phoneNumber : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_verification)

        phoneNumber = intent.getStringExtra("PhoneNumber").toString().trim()

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
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(mCallbacks)
            .setActivity(this)
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
            Toast.makeText(this@PhoneVerification,p0.message, Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            verificationId = p0
        }
    }

    private fun verifyCode(otp : String)
    {
        val credential = PhoneAuthProvider.getCredential(verificationId,otp)
        linkCredential(credential)
    }

    private fun linkCredential(credential: PhoneAuthCredential)
    {
        firebaseAuth.currentUser?.linkWithCredential(credential)
            ?.addOnCompleteListener {
                if(it.isSuccessful) {
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    val build = AlertDialog.Builder(this)
                    build.setCancelable(false)
                    build.setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        }).setTitle("Error")
                        .setMessage(it.exception?.message)
                        .show()
                }
            }
    }
}