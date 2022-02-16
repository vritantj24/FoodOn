package com.vritant.foodon.chefUtils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageActivity
import com.theartofdev.edmodo.cropper.CropImageView
import com.vritant.foodon.DishModel
import com.vritant.foodon.FoodDetails
import com.vritant.foodon.R
import kotlinx.android.synthetic.main.activity_delete_dish.*
import kotlinx.android.synthetic.main.chef_menu_delete.*
import kotlinx.android.synthetic.main.fragment_chef_profile.*
import kotlinx.android.synthetic.main.progress_dialog_layout.view.*
import java.util.*


class UpdateDeleteDish : AppCompatActivity() {

    private lateinit var imageUri : Uri
    private lateinit var dbUri : String
    private lateinit var mCropImageUri: Uri
    private lateinit var description: String
    private lateinit var quantity: String
    private lateinit var price : String
    private lateinit var dishes:String
    private lateinit var chefId:String
    private lateinit var randomUID: String
    private lateinit var ref: StorageReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private lateinit var id: String
    private lateinit var data: DatabaseReference
    private lateinit var state: String
    private lateinit var city:String
    private lateinit var area:String
    private lateinit var dialog: AlertDialog

    private var res = registerForActivityResult(ActivityResultContracts.RequestPermission())
    {
        if(it)
        {
            startCropImageActivity(mCropImageUri)
        }
        else
        {
            Toast.makeText(this,"Cancelling! Permission Not Granted",Toast.LENGTH_LONG).show()
        }
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if(it.resultCode== RESULT_OK && it.data!=null)
            {
                imageUri = CropImage.getPickImageResultUri(this, it.data)
                if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                    mCropImageUri = imageUri
                    res.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    startCropImageActivity(imageUri)
                }
            }
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_dish)

        id = intent.getStringExtra("updateDeleteDish").toString()
        fAuth = FirebaseAuth.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        firebaseDatabase = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        data = firebaseDatabase.getReference("Chef").child(userId)
        data.addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val chef = snapshot.getValue(Chef::class.java)
                if(chef!=null)
                {
                    state = chef.State.toString()
                    city = chef.City.toString()
                    area = chef.Area.toString()
                }

                Update_dish.setOnClickListener {
                    description = Description.editText?.text.toString().trim()
                    quantity = QuanTity.editText?.text.toString().trim()
                    price = PrIce.editText?.text.toString().trim()

                    if(isValid())
                    {
                        if(imageUri!=null)
                        {
                            uploadImage()
                        }
                        else
                        {
                            updatedEsc(dbUri)
                        }
                    }
                }

                Delete_dish.setOnClickListener {
                    val builder = AlertDialog.Builder(this@UpdateDeleteDish)
                    builder.setMessage("Are you sure you want to delete this dish ?")
                    builder.setPositiveButton("Yes",DialogInterface.OnClickListener { dialogInterface, i ->

                        firebaseDatabase.getReference("FoodDetails").child(state).child(city).child(area).child(fAuth.currentUser?.uid.toString())
                            .child(id).removeValue()
                        val food = AlertDialog.Builder(this@UpdateDeleteDish)
                        food.setMessage("Your Dish has been deleted!")
                        food.setPositiveButton("OK",DialogInterface.OnClickListener { dialogInterfac, j ->
                            startActivity(Intent(this@UpdateDeleteDish,ChefPanel::class.java))
                        })
                        val alert = food.create()
                        alert.show()
                    })
                    builder.setNegativeButton("No",DialogInterface.OnClickListener { dialogInterfa, k ->
                        dialogInterfa.cancel()
                    })
                    val alert  = builder.create()
                    alert.show()
                }

                val user = fAuth.currentUser?.uid.toString()
                databaseReference = firebaseDatabase.getReference("FoodDetails").child(state).child(city).child(area)
                    .child(user).child(id)
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val dishModel = snapshot.getValue(DishModel::class.java)
                        Description.editText?.setText(dishModel?.Description)
                        QuanTity.editText?.setText(dishModel?.Quantity)
                        dish_Name.text = "Dish Name : "+dishModel?.Dishes
                        dishes = dishModel?.Dishes.toString()
                        PrIce.editText?.setText(dishModel?.Price)
                        Glide.with(this@UpdateDeleteDish).load(dishModel?.ImageURL).into(image_Upload)
                        dbUri = dishModel?.ImageURL.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
                fAuth = FirebaseAuth.getInstance()
                databaseReference = firebaseDatabase.getReference("FoodDetails")
                storage = FirebaseStorage.getInstance()
                storageReference = storage.reference
                image_Upload.setOnClickListener {
                    onSelectImageClick()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun isValid() : Boolean
    {

        Description.isErrorEnabled = false
        QuanTity.isErrorEnabled = false
        PrIce.isErrorEnabled = false
        var isValidDescription = false
        var isValidPrice = false
        var isValidQuantity = false

        if (TextUtils.isEmpty(description)) {
            Description.isErrorEnabled = true
            Description.error = "Description is Required"
        } else {
            Description.error = null
            isValidDescription = true
        }
        if (TextUtils.isEmpty(quantity)) {
            QuanTity.isErrorEnabled = true
            QuanTity.error = "Enter number of Plates or Items"
        } else {
            isValidQuantity = true
        }
        if (TextUtils.isEmpty(price)) {
            PrIce.isErrorEnabled = true
            PrIce.error = "Please Mention Price"
        } else {
            isValidPrice = true
        }

        return isValidDescription && isValidPrice && isValidQuantity
    }

    private fun uploadImage()
    {
        dialog = getAlertDialog(this, R.layout.progress_dialog_layout,"Uploading............")
        dialog.setCancelable(false)
        dialog.show()


        randomUID = UUID.randomUUID().toString()
        ref = storageReference.child(randomUID)
        ref.putFile(mCropImageUri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                updatedEsc(it.toString())
            }
        }.addOnFailureListener {
            dialog.dismiss()
            Toast.makeText(this,"Failed"+it.message,Toast.LENGTH_LONG).show()
        }.addOnProgressListener {
            val progress = 100.0 * it.bytesTransferred/it.totalByteCount
            dialog.setMessage("Uploaded "+progress.toInt() + "%")
            dialog.setCanceledOnTouchOutside(false)
        }
    }

    private fun updatedEsc(uri : String)
    {
        chefId = fAuth.currentUser?.uid.toString()
        val info = FoodDetails(dishes,quantity,price,description,uri,id,chefId)
        firebaseDatabase.getReference("FoodDetails").child(state).child(city).child(area).child(chefId).child(id)
            .setValue(info).addOnCompleteListener {
                dialog.dismiss()
                Toast.makeText(this,"Dish Updated Successfully",Toast.LENGTH_LONG).show()
            }
    }

    private fun startCropImageActivity(imageUri : Uri)
    {
        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .start(this)

    }

    @Suppress("DEPRECATION")
    @SuppressLint("NewApi")

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == CropImageActivity.RESULT_OK) {
                image_upload.setImageURI(result.uri)
                mCropImageUri=result.uri
                Toast.makeText(this, "Cropped Successfully!", Toast.LENGTH_SHORT).show()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Failed To Crop" + result.error, Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onSelectImageClick()
    {
        resultLauncher.launch(CropImage.getPickImageChooserIntent(this))
    }

    private fun getAlertDialog(context: Context, layout: Int, message : String): AlertDialog
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
}