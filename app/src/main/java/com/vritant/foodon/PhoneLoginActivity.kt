package com.vritant.foodon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class PhoneLoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var role : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)

        role = intent.getStringExtra("User").toString().trim()

        firebaseAuth = FirebaseAuth.getInstance()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.framelayout,PhoneLoginDetailFragment(role))
        transaction.commit()
    }
}