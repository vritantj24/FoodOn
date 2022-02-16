package com.vritant.foodon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import kotlinx.android.synthetic.main.activity_choose_one.*


class ChooseOne : AppCompatActivity() {

    private lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_one)

        type = intent.getStringExtra("Home").toString().trim()

        chef.setOnClickListener {
            if (type == "Email")
            {
                val loginEmailIntent = Intent(this,EmailLoginActivity::class.java)
                startActivity(loginEmailIntent)
                loginEmailIntent.putExtra("User","chefUtils")
                finish()
            }
            if (type == "Phone")
            {
                val loginPhoneIntent = Intent(this,PhoneLoginActivity::class.java)
                loginPhoneIntent.putExtra("User","chefUtils")
                startActivity(loginPhoneIntent)
                finish()
            }
            if (type == "SignUp")
            {
                val register = Intent(this,RegistrationActivity::class.java)
                register.putExtra("User","chefUtils")
                startActivity(register)
            }
        }

        customer.setOnClickListener {

                if (type == "Email") {
                    val loginEmailIntent = Intent(this, EmailLoginActivity::class.java)
                    loginEmailIntent.putExtra("User","Customer")
                    startActivity(loginEmailIntent)
                    finish()
                }
                if (type == "Phone") {
                    val loginPhoneIntent = Intent(this, PhoneLoginActivity::class.java)
                    loginPhoneIntent.putExtra("User","Customer")
                    startActivity(loginPhoneIntent)
                    finish()
                }
                if (type == "SignUp") {
                    val registerCustomer = Intent(this, RegistrationActivity::class.java)
                    registerCustomer.putExtra("User","Customer")
                    startActivity(registerCustomer)
                }

        }

        delivery.setOnClickListener {

            if (type == "Email") {
                val loginEmailIntent = Intent(this, EmailLoginActivity::class.java)
                loginEmailIntent.putExtra("User","Delivery")
                startActivity(loginEmailIntent)
                finish()
            }
            if (type == "Phone") {
                val loginPhoneIntent = Intent(this, PhoneLoginActivity::class.java)
                loginPhoneIntent.putExtra("User","Delivery")
                startActivity(loginPhoneIntent)
                finish()
            }
            if (type == "SignUp") {
                val registerDelivery = Intent(this, RegistrationActivity::class.java)
                registerDelivery.putExtra("User","Delivery")
                startActivity(registerDelivery)
            }

        }
    }
}