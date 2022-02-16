package com.vritant.foodon

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.vritant.foodon.chefUtils.ChefPanel
import com.vritant.foodon.customerUtils.CustomerPanel
import com.vritant.foodon.deliveryUtils.DeliveryPanel

class IntroActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var databaseReference  : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({

            firebaseAuth = FirebaseAuth.getInstance()
            database = FirebaseDatabase.getInstance()

            if(firebaseAuth.currentUser!=null)
            {
                if(firebaseAuth.currentUser!!.isEmailVerified)
                {
                    databaseReference = database.getReference("User").child(FirebaseAuth.getInstance().uid.toString()+"/Role")
                    databaseReference.addListenerForSingleValueEvent(object : ValueEventListener
                    {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val role = snapshot.getValue(String::class.java)
                            if(role=="chefUtils")
                            {
                                startActivity(Intent(this@IntroActivity, ChefPanel::class.java))
                                finish()
                            }
                            if(role=="Customer")
                            {
                                startActivity(Intent(this@IntroActivity, CustomerPanel::class.java))
                                finish()
                            }
                            if(role=="Delivery")
                            {
                                startActivity(Intent(this@IntroActivity, DeliveryPanel::class.java))
                                finish()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@IntroActivity, error.message, Toast.LENGTH_LONG).show()
                        }
                    })
                }
                else
                {
                    val builder =  AlertDialog.Builder(this)
                    builder.setMessage("Check Whether You Have Verified Your Detail , Otherwise Please Verify")
                    builder.setCancelable(false)
                    builder.setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialogInterface, i ->

                        dialogInterface.dismiss()
                            val intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        //add intent for main SignIn options page
                    })
                    val alertDialog = builder.create()
                    alertDialog.show()
                    firebaseAuth.signOut()
                }
            }
            else
            {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
                //add intent for main SignIn options page
            }
        },3000)
    }


}