package com.example.application.fragments

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.application.HomeFragment
import com.example.application.R
import com.example.application.user_student_data_class
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class ProfileCompletionFragment : Fragment() {
    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etMobileNo: EditText
    private lateinit var btnChooseImage: Button
    private lateinit var btnSave: Button
    private lateinit var ivProfileImage: CircleImageView
    private lateinit var imageUri: Uri
    private lateinit var dialog: Dialog
    private val PICK_IMAGE_REQUEST_CODE = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile_completion, container, false)

        etName =  root.findViewById(R.id.etName)
        etAge =  root.findViewById(R.id.etAge)
        etMobileNo =  root.findViewById(R.id.etMobileNo)
        btnChooseImage =  root.findViewById(R.id.btnSelectImage)
        btnSave =  root.findViewById(R.id.saveBtn)
        ivProfileImage =  root.findViewById(R.id.ivProfilePic)

        btnChooseImage.setOnClickListener { openImagePicker() }


        if (etName != null && etAge != null && etMobileNo != null ){

            btnSave.setOnClickListener {
                showProgress()
                saveProfileData()
                }
        }else(

                Toast.makeText(activity,"Either you are updating or first time, EMPTY FILED NOT ALLOWED :) "
                    , Toast.LENGTH_SHORT)
                    .show()
                )

        return root

    }

    private fun saveProfileData() {
        val userEmail = FirebaseAuth.getInstance().currentUser!!.email!!
        val name = etName.text.toString().trim()
        val age = etAge.text.toString().trim()
        val mobileNo = etMobileNo.text.toString().trim()
//        val email = arguments?.getString("email")
        val sheerID = false
        val earningsPerDay = 0
        val earningsPerWeek = 0
        val earningsPerMonth = 0
        val balance = 0
        val ratings = 0.0
        val milestone = 100


        if (name.isEmpty() || age.isEmpty() || mobileNo.isEmpty()) {
            hideProgress()
            Toast.makeText(activity, "Please enter all the details", Toast.LENGTH_SHORT).show()
            return
        }


        if (!::imageUri.isInitialized) {

            hideProgress()
            Toast.makeText(activity, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            hideProgress()
            Toast.makeText(activity, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        val mAuth = FirebaseAuth.getInstance()

        val ref = FirebaseStorage.getInstance().getReference("user_student/"+mAuth.currentUser?.uid+".png")



        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val  profileData = user_student_data_class(name, age, mobileNo,
                        uri.toString(),
                        userEmail,sheerID,earningsPerDay,earningsPerWeek,earningsPerMonth,
                        balance, ratings,milestone,user.uid)


                    val db = Firebase.firestore
                    db.collection("user_student/student_info/info")
                        .document(user.uid)
                        .set(profileData)
                        .addOnSuccessListener {
                            val database = Firebase.database.reference
                            database.child("user_student").child(user.uid)
                                .child("info").setValue(profileData)
                                .addOnSuccessListener {
                                    hideProgress()
                                    Toast.makeText(activity, "Profile data saved to Realtime Database",
                                        Toast.LENGTH_SHORT).show()

                                    val homeFragment = HomeFragment()
                                    parentFragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, homeFragment)
                                        .commit()
                                }
                                .addOnFailureListener { e ->
                                    hideProgress()
                                    Toast.makeText(activity, "Error saving profile data to Realtime Database: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            hideProgress()
                            Toast.makeText(activity, "Error saving profile data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            .addOnFailureListener { e ->
                Toast.makeText(activity, "Error uploading image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            imageUri = data.data!!
            Glide.with(this).load(imageUri).into(ivProfileImage)
        }
    }

    private fun showProgress(){
        dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgress(){
        dialog.dismiss()
    }

}