package com.vritant.foodon.chefUtils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageActivity
import com.theartofdev.edmodo.cropper.CropImageView
import com.vritant.foodon.FoodDetails
import com.vritant.foodon.R
import kotlinx.android.synthetic.main.fragment_chef_profile.*
import kotlinx.android.synthetic.main.progress_dialog_layout.view.*
import java.util.*


@Suppress("DEPRECATION")
class ChefProfileFragment : Fragment() {

    private lateinit var description : String
    private lateinit var quantity : String
    private lateinit var price :String
    private lateinit var dishes : String
    private lateinit var imageUri : Uri
    private lateinit var mCropImageUri : Uri
    private lateinit var storage : FirebaseStorage
    private lateinit var storageReference : StorageReference
    private lateinit var firebaseDatabase : FirebaseDatabase
    private lateinit var databaseReference : DatabaseReference
    private lateinit var data : DatabaseReference
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var ref : StorageReference
    private lateinit var chefId : String
    private lateinit var randomUid : String
    private lateinit var state : String
    private lateinit var city : String
    private lateinit var area : String

    private var res = registerForActivityResult(ActivityResultContracts.RequestPermission())
    {
        if(it)
        {
            startCropImageActivity(mCropImageUri)
        }
        else
        {
            Toast.makeText(requireContext(),"Cancelling! Permission Not Granted",Toast.LENGTH_LONG).show()
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if(it.resultCode==CropImageActivity.RESULT_OK && it.data!=null)
            {
                imageUri = CropImage.getPickImageResultUri(requireContext(), it.data)
                if (CropImage.isReadExternalStoragePermissionsRequired(requireContext(), imageUri)) {
                    mCropImageUri = imageUri
                    res.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    startCropImageActivity(imageUri)
                }
            }
        })

    /*private val cropActivityResultContracts = object : ActivityResultContract<Any?,Uri?>()
    {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            val result = CropImage.getActivityResult(intent)
        }
     youtube link - https://www.youtube.com/watch?v=ATj6tq5HQZU
    }

    private val cropRes = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {

            val result = CropImage.getActivityResult(it.data)
            if (it.resultCode== CropImageActivity.RESULT_OK)
            {
                image_upload.setImageURI(result.uri)
                mCropImageUri=result.uri
                Toast.makeText(requireContext(), "Cropped Successfully!", Toast.LENGTH_SHORT).show()
            }else if (it.resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(requireContext(), "Failed To Crop" + result.error, Toast.LENGTH_SHORT).show()
            }
        })*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = "Post Dish"
        return inflater.inflate(R.layout.fragment_chef_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("FoodDetails")

        try {
            val userId = firebaseAuth.currentUser!!.uid
            data = firebaseDatabase.getReference("chefUtils").child(userId)
            data.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chef = snapshot.getValue(Chef::class.java)
                    if (chef != null) {
                        state = chef.State.toString()
                        city = chef.City.toString()
                        area = chef.Area.toString()
                    }
                    image_upload.setOnClickListener {
                        onSelectImageClick()
                    }
                    post.setOnClickListener {
                        dishes = dish_name.editText?.text.toString().trim()
                        description = desCription.editText?.text.toString().trim()
                        quantity = Quantity.editText?.text.toString().trim()
                        price = Price.editText?.text.toString().trim()

                        if (isValid()) {
                            uploadImage()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }catch (e: Exception)
        {
            Log.e("Error",e.message.toString())
        }

    }

    private fun uploadImage()
    {
        val dialog = getAlertDialog(requireContext(), R.layout.progress_dialog_layout,"Uploading............")
        dialog.setCancelable(false)
        dialog.show()

        randomUid = UUID.randomUUID().toString()
        ref = storageReference.child(randomUid)
        chefId = firebaseAuth.currentUser?.uid.toString()
        ref.putFile(mCropImageUri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                val info = FoodDetails(dishes,quantity,price,description, it.toString(),randomUid,chefId)
                firebaseDatabase.getReference("FoodDetails").child(state).child(city).child(area).child(
                    firebaseAuth.currentUser!!.uid).child(randomUid)
                    .setValue(info).addOnCompleteListener {
                        dialog.dismiss()
                        Toast.makeText(requireContext(),"Dish Posted Successfully",Toast.LENGTH_LONG).show()
                    }
            }
        }.addOnFailureListener {
            dialog.dismiss()
            Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
        }.addOnProgressListener {
            val progress = 100.0 * it.bytesTransferred/it.totalByteCount
            dialog.setMessage("Uploaded "+progress.toInt() + "%")
            dialog.setCanceledOnTouchOutside(false)
        }
    }

    private fun isValid() : Boolean
    {
        dish_name.isErrorEnabled = false
        desCription.isErrorEnabled = false
        Quantity.isErrorEnabled = false
        Price.isErrorEnabled = false
        var isValidDishName = false
        var isValidDescription = false
        var isValidPrice = false
        var isValidQuantity = false
        if (TextUtils.isEmpty(dishes)) {
            dish_name.isErrorEnabled = true
            dish_name.error = "Description is Required"
        } else {
            dish_name.error = null
            isValidDishName = true
        }
        if (TextUtils.isEmpty(description)) {
            desCription.isErrorEnabled = true
            desCription.error = "Description is Required"
        } else {
            desCription.error = null
            isValidDescription = true
        }
        if (TextUtils.isEmpty(quantity)) {
            Quantity.isErrorEnabled = true
            Quantity.error = "Enter number of Plates or Items"
        } else {
            isValidQuantity = true
        }
        if (TextUtils.isEmpty(price)) {
            Price.isErrorEnabled = true
            Price.error = "Please Mention Price"
        } else {
            isValidPrice = true
        }

        return isValidDishName  && isValidDescription && isValidPrice && isValidQuantity
    }

    private fun startCropImageActivity(imageUri : Uri)
    {
        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .start(requireContext(),this)

    }

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
                Toast.makeText(requireContext(), "Cropped Successfully!", Toast.LENGTH_SHORT).show()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(requireContext(), "Failed To Crop" + result.error, Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onSelectImageClick()
    {
        resultLauncher.launch(CropImage.getPickImageChooserIntent(requireContext()))
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

}