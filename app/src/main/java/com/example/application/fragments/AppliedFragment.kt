package com.example.application.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.example.application.adapters.AppliedAdapter
import com.example.application.adapters.Horizontal_Scroll_Adapter
import com.example.application.dataClasses.AppliedJob
import com.example.application.dataClasses.AppliedJobsHolder
import com.example.application.job_details_data_class
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AppliedFragment : Fragment() {

private lateinit var recyclerViewApplied1 :RecyclerView
private lateinit var appliedAdapter1: AppliedAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_applied, container, false)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val jobDetailsDataClass = mutableListOf<job_details_data_class>()
        val appliedJobsList = AppliedJobsHolder.appliedJobsList

        recyclerViewApplied1 = view.findViewById( R.id.recyclerViewApplied )
        appliedAdapter1 = AppliedAdapter(requireActivity(), appliedJobsList,jobDetailsDataClass)
        recyclerViewApplied1.adapter = appliedAdapter1
        recyclerViewApplied1.layoutManager = LinearLayoutManager(requireActivity())

        val databaseRef = FirebaseDatabase.getInstance().reference.child("user_student").
        child(uid!!).child("applied")

        databaseRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                appliedJobsList.clear()

                for (appliedJobSnapshot in dataSnapshot.children) {
                    val jobId = appliedJobSnapshot.key.toString()
                    val businessName = appliedJobSnapshot.child("businessName").value.toString()
                    val businessLogo = appliedJobSnapshot.child("businessLogo").value.toString()
                    val address = appliedJobSnapshot.child("address").value.toString()
                    val ratings = appliedJobSnapshot.child("ratings").value.toString().toDouble()
                    val formattedRatings = if (ratings > 5.0) "5.0".toDouble()
                    else ratings.toString().toDouble()
                    val jobTitle = appliedJobSnapshot.child("jobTitle").value.toString()
                    val payPerHour = appliedJobSnapshot.child("payPerHour").value.toString()
                    val startTime = appliedJobSnapshot.child("startTime").value.toString()
                    val endTime = appliedJobSnapshot.child("endTime").value.toString()
                    val description = appliedJobSnapshot.child("description").value.toString()
                    val durationInSeconds = appliedJobSnapshot.child("durationInSeconds").value.toString().toLong()
                    val payPerDay = appliedJobSnapshot.child("payPerDay").value.toString().toDouble()
                    val userId = appliedJobSnapshot.child("userId").value.toString()



                    val job = job_details_data_class(
                        businessName,
                        address,
                        businessLogo,
                        formattedRatings,
                        jobId,
                        jobTitle,
                        payPerHour,
                        startTime,
                        endTime,
                        description,
                        durationInSeconds,
                        payPerDay.toInt(),
                        userId
                    )
                    jobDetailsDataClass.add(job)
                }

                appliedAdapter1.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })


        return view
    }
}