package com.vritant.foodon

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_phone_login_detail.*


class PhoneLoginDetailFragment(user:String) : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private val role : String = user
    private var phoneNumber:String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
     {
        return inflater.inflate(R.layout.fragment_phone_login_detail, container, false)
     }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        otp.setOnClickListener {
            phoneNumber = number.text.toString().trim()
            val num = CountryCode.selectedCountryCodeWithPlus+phoneNumber
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.framelayout,PhoneOtpFragment(num))
            transaction?.commit()
        }

        acsignup.setOnClickListener {
            val intent = Intent(this.context,RegistrationActivity::class.java)
            intent.putExtra("User",role)
            startActivity(intent)
            activity?.finish()
        }

        btnEmail.setOnClickListener {
            val intent = Intent(this.context,EmailLoginActivity::class.java)
            intent.putExtra("User",role)
            startActivity(intent)
            activity?.finish()
        }
    }

}