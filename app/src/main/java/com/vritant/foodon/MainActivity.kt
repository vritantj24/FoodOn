package com.vritant.foodon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        SignwithEmail.setOnClickListener {
            val intent = Intent(this,ChooseOne::class.java)
            intent.putExtra("Home","Email")
            startActivity(intent)
            finish()
        }

        SignwithPhone.setOnClickListener {
            val intent = Intent(this,ChooseOne::class.java)
            intent.putExtra("Home","Phone")
            startActivity(intent)
            finish()
        }

        Signup.setOnClickListener {
            val intent = Intent(this,ChooseOne::class.java)
            intent.putExtra("Home","SignUp")
            startActivity(intent)
            finish()
        }
    }
}