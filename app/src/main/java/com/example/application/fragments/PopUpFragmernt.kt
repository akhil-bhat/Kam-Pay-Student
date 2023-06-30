package com.example.application.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.application.R
import com.example.application.job_details_data_class
import com.example.application.user_student_data_class
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PopUpFragmernt (private val position: Int,
                      private val jobDetailsDataClass: List<job_details_data_class>)
    : DialogFragment() {

    private val mdatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val mUser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        getDialog()!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val view = inflater.inflate(R.layout.fragment_pop_up_fragmernt, container, false)
        val brandNameTextView = view.findViewById<TextView>(R.id.business_name_popUp)
        val ratings1 = view.findViewById<TextView>(R.id.user_ratings_popUp)
        val description = view.findViewById<TextView>(R.id.description_popUp)
        val rates = view.findViewById<TextView>(R.id.rates_popUp)
        val startTime = view.findViewById<TextView>(R.id.start_time_popUp)
        val endTime = view.findViewById<TextView>(R.id.end_time_popUp)
        val address = view.findViewById<TextView>(R.id.textView_address_popUp)
        val jobTitle = view.findViewById<TextView>(R.id.job_title_popUp)
        val durationTv = view.findViewById<TextView>(R.id.text_view_duration_popUp)
        val ratings_linear_layouts= view.findViewById<LinearLayout>(R.id.linear_layout_rating_popUp)
        val not_now_btn = view.findViewById<CardView>(R.id.close_btn_popUp)
        val apply_btn_popUp = view.findViewById<CardView>(R.id.apply_btn_popUp)
        val applied_btn = view.findViewById<CardView>(R.id.applied_btn_popUp)
        val not_now_tv = view.findViewById<TextView>(R.id.tv_close_popUp)
        val mCurrentUser = FirebaseAuth.getInstance().currentUser?.uid


        val currentItem = jobDetailsDataClass[position]

        apply_btn_popUp.setOnClickListener {

            val currentUserRef1 = FirebaseDatabase.getInstance().getReference("user_student").child(mUser!!.uid)

            currentUserRef1.child("info").addValueEventListener(object : ValueEventListener {

                @SuppressLint("SuspiciousIndentation")

                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.exists()){

                      val student_info_applied = snapshot.getValue(user_student_data_class::class.java)!!

                        if (student_info_applied.sheerID == true && student_info_applied.ratings > 0.0) {

                            val student_nesscessary_data_only = StudentNecessaryData(
                                student_info_applied.name,
                                student_info_applied.sheerID.toString(),
                                student_info_applied.mobileNo,
                                student_info_applied.email.toString(),
                                student_info_applied.profileImageUrl,
                                student_info_applied.ratings,
                                student_info_applied.userId
                            )

                            val applicantsRef =
                                mdatabase.child("user_business").child(currentItem.userID)
                                    .child("Jobs")
                            applicantsRef.child(currentItem.jobId).child("applicants")
                                .child(mCurrentUser!!)
                                .setValue(student_nesscessary_data_only)

                            val currentUserRef = mdatabase.child("user_student").child(mUser!!.uid)

                            currentUserRef.child("applied").child(currentItem.jobId)
                                .setValue(currentItem)
                            not_now_tv.setText("close")
                            apply_btn_popUp.visibility = View.GONE
                            applied_btn.visibility = View.VISIBLE

                        }
                        else{
                            Toast.makeText(context,"Please wait until you get fully verified by SHEER ID:) "
                                , Toast.LENGTH_LONG).show()
                        } }

                    else{
                        Toast.makeText(context,"Data not available to sent", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context,"Data not available to sent", Toast.LENGTH_SHORT).show()
                                    }
            })

        }

        jobTitle.text = currentItem.jobTitle
        brandNameTextView.text = currentItem.businessName
        ratings1.text = currentItem.ratings.toString()
        description.text = currentItem.description
        rates.text = currentItem.payPerDay.toString()
        startTime.text = currentItem.startTime
        endTime.text = currentItem.endTime
        address.text = currentItem.Address

        val ratings = currentItem.ratings.toFloat() ?: 0f

        ratings_linear_layouts.setBackgroundColor(
            when {
                ratings >= 4.50 -> Color.parseColor("#318F2F") // light green
                ratings >= 4.00 -> Color.parseColor("#61B61F") // green
                ratings >= 3.50 -> Color.parseColor("#B1AB16") // yellow
                ratings >= 3.00 -> Color.parseColor("#E5DE21") // light yellow
                ratings >= 2.50 -> Color.parseColor("#B18616") // orange
                ratings >= 2.00 -> Color.parseColor("#FFC225") // light orange
                else -> Color.parseColor("#FF3F25") // red
            }
        )

        val durationInSeconds = currentItem.durationInSeconds
        val hours = durationInSeconds / 3600
        durationTv.text = "~ ${hours} hrs"

        if (hours >= 9) {
            durationTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_red))
        }else if(hours > 6) {
            durationTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_orange))
        } else if(hours > 3) {
            durationTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_yellow))
        }else{
            durationTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_green))
        }

        not_now_btn.setOnClickListener {
            dialog!!.dismiss()
        }

        return view
    }


    data class StudentNecessaryData (
        val name: String = "",
        val sheerId: String = "",
        val mobileNo: String = "",
        val email: String = "",
        val profileImage:String = "",
        val ratings:Double = 0.0,
        val userId: String = ""
    )

}