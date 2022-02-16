package com.vritant.foodon

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registration.*


class RegistrationActivity : AppCompatActivity() {

    private val maharashtra = arrayOf("Mumbai","Pune","Nashik")
    private val madhyaPradesh = arrayOf("Bhopal","Indore","Ujjain")
    private val delhi = arrayOf("New Delhi","Old Delhi")
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var fName: String? = null
    private var lName: String? = null
    private var emailId:String? = null
    private var password:String? = null
    private var confPassword:String? = null
    private var mobile:String? = null
    private var house:String? = null
    private var area:String? = null
    private var pinCode:String? = null
    private var state:String? = null
    private var city:String? = null
    private lateinit var role : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        role = intent.getStringExtra("User").toString().trim()

        Statee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val value = p0?.getItemAtPosition(p2)
                state = value.toString().trim()
                if(state == "Maharashtra")
                {
                    val arrayAdapter = ArrayAdapter(applicationContext,android.R.layout.simple_spinner_item,maharashtra)
                    Citys.adapter = arrayAdapter
                }
                if(state == "Madhya Pradesh")
                {
                    val arrayAdapter = ArrayAdapter(applicationContext,android.R.layout.simple_spinner_item,madhyaPradesh)
                    Citys.adapter = arrayAdapter
                }
                if(state == "Delhi")
                {
                    val arrayAdapter = ArrayAdapter(applicationContext,android.R.layout.simple_spinner_item,delhi)
                    Citys.adapter=arrayAdapter
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        Citys.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val value = p0?.getItemAtPosition(p2)
                city = value.toString().trim()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference(role)
        firebaseAuth = FirebaseAuth.getInstance()
        

        Signup.setOnClickListener {
            fName = Firstname.editText?.text.toString().trim()
            lName = Lastname.editText?.text.toString().trim()
            emailId = Email.editText?.text.toString().trim()
            password = Pwd.editText?.text.toString().trim()
            confPassword = Cpass.editText?.text.toString().trim()
            mobile = Mobileno.editText?.text.toString().trim()
            house = houseNo.editText?.text.toString().trim()
            area = Area.editText?.text.toString().trim()
            pinCode = Pincode.editText?.text.toString().trim()


            if (isValid()) {

                val dialog = getAlertDialog(this, R.layout.progress_dialog_layout)
                dialog.setCancelable(false)
                dialog.show()

                emailId?.let {
                    password?.let { it1 ->
                        firebaseAuth.createUserWithEmailAndPassword(it, it1)
                            .addOnCompleteListener { it ->
                                if (it.isSuccessful) {
                                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
                                    databaseReference =
                                        FirebaseDatabase.getInstance().getReference("User")
                                            .child(userId)
                                    val map: HashMap<String, String> = HashMap()
                                    map["Role"] = role
                                    databaseReference.setValue(map).addOnCompleteListener {
                                        val mp: HashMap<String, String> = HashMap()
                                        mp["First Name"] = fName!!
                                        mp["Last Name"] = lName!!
                                        mp["EmailId"] = emailId!!
                                        mp["Password"] = password!!
                                        mp["Mobile No"] = mobile!!
                                        mp["House"] = house!!
                                        mp["Area"] = area!!
                                        mp["PinCode"] = pinCode!!
                                        mp["State"] = state!!
                                        mp["City"] = city!!

                                        firebaseDatabase.getReference(role)
                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                            .setValue(mp).addOnCompleteListener {

                                                dialog.dismiss()

                                                firebaseAuth.currentUser!!.sendEmailVerification()
                                                    .addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            val builder = AlertDialog.Builder(this)
                                                            builder.setMessage("You Have Registered! Make Sure To Verify Your Email")
                                                            builder.setCancelable(false)
                                                            builder.setPositiveButton(
                                                                "OK",
                                                                DialogInterface.OnClickListener { dialogInterface, i ->
                                                                    dialogInterface.dismiss()

                                                                    val phone = CountryCode.selectedCountryCodeWithPlus + mobile
                                                                    val intent = Intent(this,PhoneVerification::class.java)
                                                                    intent.putExtra("PhoneNumber",phone)
                                                                    startActivity(intent)
                                                                })
                                                            val alert = builder.create()
                                                            alert.show()
                                                        } else {
                                                            dialog.dismiss()
                                                            val build = AlertDialog.Builder(this)
                                                            build.setCancelable(false)
                                                            build.setPositiveButton(
                                                                "OK",
                                                                DialogInterface.OnClickListener { dialogInterface, i ->
                                                                    dialogInterface.dismiss()
                                                                }).setTitle("Error")
                                                                .setMessage(task.exception?.message)
                                                                .show()

                                                        }
                                                    }
                                            }
                                    }
                                }
                            }
                    }
                }
            }
        }

        email.setOnClickListener {
            val intent = Intent(this,EmailLoginActivity::class.java)
            intent.putExtra("User",role)
            startActivity(intent)
            finish()
        }

        phone.setOnClickListener {
            val intent = Intent(this,PhoneLoginActivity::class.java)
            intent.putExtra("User",role)
            startActivity(intent)
            finish()
        }

    }

    private val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

    private fun isValid() : Boolean
    {
        Email.isErrorEnabled = false
        Firstname.isErrorEnabled = false
        Lastname.isErrorEnabled = false
        Pwd.isErrorEnabled = false
        Mobileno.isErrorEnabled = false
        Cpass.isErrorEnabled = false
        Area.isErrorEnabled = false
        houseNo.isErrorEnabled = false
        Pincode.isErrorEnabled = false

        var isValidHouseNo=false
        var isValidLName=false
        var isValidName=false
        var isValidEmail=false
        var isValidPassword=false
        var isValidConfPassword=false
        var isValidMobileNum=false
        var isValidArea=false
        var isValidPinCode=false

        if(TextUtils.isEmpty(fName)){
            Firstname.isErrorEnabled = true
            Firstname.error = "Enter First Name"
        }else{
            isValidName = true
        }
        if(TextUtils.isEmpty(lName)){
            Lastname.isErrorEnabled = true
            Lastname.error = "Enter Last Name"
        }else{
            isValidLName = true
        }
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
        if(TextUtils.isEmpty(confPassword)){
            Cpass.isErrorEnabled = true
            Cpass.error = "Enter Password Again"
        }else{
            if(password != confPassword){
                Cpass.isErrorEnabled = true
                Cpass.error = "Password Doesn't Match"
            }else{
                isValidConfPassword = true
            }
        }
        if(TextUtils.isEmpty(mobile)){
            Mobileno.isErrorEnabled = true
            Mobileno.error = "Mobile Number Is Required"
        }else{
            if(mobile!!.length<10){
                Mobileno.isErrorEnabled = true
                Mobileno.error = "Invalid Mobile Number"
            }else{
                isValidMobileNum = true
            }
        }
        if(TextUtils.isEmpty(area)){
            Area.isErrorEnabled = true
            Area.error = "Area Is Required"
        }else{
            isValidArea = true
        }
        if(TextUtils.isEmpty(pinCode)){
            Pincode.isErrorEnabled = true
            Pincode.error = "Please Enter PinCode"
        }else{
            isValidPinCode = true
        }
        if(TextUtils.isEmpty(house)){
            houseNo.isErrorEnabled = true
            houseNo.error = "Fields Can't Be Empty"
        }else{
            isValidHouseNo = true
        }

        return isValidArea && isValidConfPassword && isValidPassword && isValidPinCode && isValidEmail && isValidMobileNum && isValidName && isValidHouseNo && isValidLName
    }

    private fun getAlertDialog(context: Context, layout: Int): AlertDialog
    {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val customLayout: View = layoutInflater.inflate(layout, null)
        builder.setView(customLayout)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
}