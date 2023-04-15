package com.example.application

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.example.application.databinding.ActivityProfileCompletionBinding
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
class Profile_completion_activity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileCompletionBinding
    private lateinit var dialog: Dialog
    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileCompletionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDatabase = FirebaseDatabase.getInstance().getReference("user_student")
        auth = FirebaseAuth.getInstance()

        val authId = auth.currentUser!!.uid


        binding.saveBtn.setOnClickListener {
            showProgressBar()
            val name = binding.etName.text.toString()
            val age = binding.etAge.text.toString()
            val mobileNo = binding.etMobileNo.text.toString()
            val user = user_student_data_class(name, age, mobileNo)

            mDatabase.child(authId).setValue(user).addOnCompleteListener{
                if (it.isSuccessful){
                    hideProgressBar()
                    Toast.makeText(this@Profile_completion_activity,
                        "Profile Completed Successfully",Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }


            }
        .addOnFailureListener {
                hideProgressBar()
                Toast.makeText(this@Profile_completion_activity,"Failed to Upload",Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showProgressBar(){
    dialog= Dialog(this@Profile_completion_activity)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_wait)
    dialog.setCanceledOnTouchOutside(false)
    dialog.show()
}
private fun hideProgressBar(){
    dialog.dismiss()
}


}