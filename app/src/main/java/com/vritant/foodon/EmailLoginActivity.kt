package com.vritant.foodon

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.vritant.foodon.chefUtils.ChefPanel
import kotlinx.android.synthetic.main.activity_email_login.*
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.progress_dialog_layout.view.*

class EmailLoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private var emailId:String? = null
    private var password:String? = null
    private lateinit var role : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_login)

        role = intent.getStringExtra("User").toString().trim()

        firebaseAuth = FirebaseAuth.getInstance()

        button4.setOnClickListener {

            emailId = Lemail.editText?.text.toString().trim()
            password = Lpassword.editText?.text.toString().trim()

            if(isValid())
            {
                val dialog = getAlertDialog(this, R.layout.progress_dialog_layout,"Signing In Please Wait............")
                dialog.setCancelable(false)
                dialog.show()

                firebaseAuth.signInWithEmailAndPassword(emailId!!, password!!).addOnCompleteListener {

                    if(it.isSuccessful)
                    {
                        dialog.dismiss()

                        if(firebaseAuth.currentUser!!.isEmailVerified)
                        {
                            dialog.dismiss()
                            Toast.makeText(this,"Congratulation! You Are Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, ChefPanel::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else
                        {
                            val builder = AlertDialog.Builder(this)
                            builder.setMessage("Verification Failed, You Have Not Verified Your Email")
                            builder.setCancelable(false)
                            builder.setPositiveButton(
                                "OK",
                                DialogInterface.OnClickListener { dialogInterface, i ->
                                    dialogInterface.dismiss()
                                })
                            val alert = builder.create()
                            alert.show()
                        }
                    }
                    else
                    {
                        dialog.dismiss()
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

        textView3.setOnClickListener {
            val intent = Intent(this,RegistrationActivity::class.java)
            intent.putExtra("User",role)
            startActivity(intent)
            finish()
        }

        forgotpass.setOnClickListener {
            val intent = Intent(this,ForgotPassword::class.java)
            intent.putExtra("User",role)
            startActivity(intent)
            finish()
        }

        btnphone.setOnClickListener {
            val intent = Intent(this,PhoneLoginActivity::class.java)
            intent.putExtra("User",role)
            startActivity(intent)
            finish()
        }
    }

    private fun getAlertDialog(context: Context, layout: Int,message : String): AlertDialog
    {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val customLayout: View = layoutInflater.inflate(layout, null)
        customLayout.text_progress.text = message
        builder.setView(customLayout)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setMessage(message)
        return dialog
    }

    private val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

    private fun isValid() : Boolean
    {
        Lemail.isErrorEnabled = false
        Lpassword.isErrorEnabled = false

        var isValidEmail=false
        var isValidPassword=false


        if(TextUtils.isEmpty(emailId)){
            Email.isErrorEnabled = true
            Email.error = "Email Is Required"
        }else{
            if(emailId!!.matches(emailPattern)){
                isValidEmail = true
            }else{
                Email.isErrorEnabled = true
                Email.error = "Enter a Valid Email Id"
            }
        }
        if(TextUtils.isEmpty(password)){
            Pwd.isErrorEnabled = true
            Pwd.error = "Enter Password"
        }else{
            if(password!!.length <8)
            {
                Pwd.isErrorEnabled = true
                Pwd.error = "Password is Weak"
            }else{
                isValidPassword = true
            }
        }

        return isValidPassword && isValidEmail
    }
}