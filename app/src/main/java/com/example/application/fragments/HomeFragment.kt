package com.example.application

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.adapters.Horizontal_Scroll_Adapter
import com.example.application.dataClasses.AppliedJob
import com.example.application.dataClasses.AppliedJobsHolder
import com.example.application.dataClasses.sort_header_list_data_class
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private lateinit var textrViewTopSearches :TextView
    private lateinit var textrViewJobsToExplore :TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: MyAdapter

    private lateinit var Horizontal_Scroll_Adapter1: Horizontal_Scroll_Adapter
    private lateinit var recyclerViewHorizontal : RecyclerView

    private lateinit var filterRecyclerView: RecyclerView
    private lateinit var Sort_header_recycler_Adpater1: Sort_header_recycler_Adpater

    private lateinit var sorts_list_array: ArrayList<sort_header_list_data_class>
    private lateinit var sort_headings_list: Array<String>

    private lateinit var loadingProgressBar : ProgressBar
    private lateinit var nothing_to_show_text_view:TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataInitialized()
        val layoutManager= LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
        val layoutManager1 = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        nothing_to_show_text_view = view.findViewById(R.id.nothig_to_show_tv)

        textrViewTopSearches = view.findViewById(R.id.text_view_top_searches)
        textrViewJobsToExplore = view.findViewById(R.id.text_view_jobs_explore)


        filterRecyclerView = view.findViewById(R.id.recyclerViewFilter)
        filterRecyclerView.layoutManager = layoutManager
        filterRecyclerView.setHasFixedSize(true)
        Sort_header_recycler_Adpater1= Sort_header_recycler_Adpater(sorts_list_array)
        filterRecyclerView.adapter = Sort_header_recycler_Adpater1


        val jobsList = mutableListOf<job_details_data_class>()
        val jobsListHorizontal = mutableListOf<job_details_data_class>()
        val appliedJobsList = AppliedJobsHolder.appliedJobsList


        recyclerViewHorizontal = view.findViewById( R.id.recylerViewHorizontalScroll )
        Horizontal_Scroll_Adapter1 = Horizontal_Scroll_Adapter(requireActivity(), jobsListHorizontal)
                   recyclerViewHorizontal.adapter = Horizontal_Scroll_Adapter1
                   recyclerViewHorizontal.layoutManager = layoutManager1


        recyclerView = view.findViewById( R.id.recyclerView_Vertical_Scrolls )

        myAdapter = MyAdapter(requireActivity(), jobsList)
                recyclerView.adapter = myAdapter
                recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val databaseRef1 = FirebaseDatabase.getInstance().reference.child("user_student").
        child(uid!!).child("applied")

        databaseRef1.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                appliedJobsList.clear()

                for (appliedJobSnapshot in dataSnapshot.children) {
                    val jobId = appliedJobSnapshot.key.toString()
                    val appliedJob = AppliedJob(jobId)
                    appliedJobsList.add(appliedJob)
                }}

            override fun onCancelled(error: DatabaseError) {

            }
        })


        val databaseRef = FirebaseDatabase.getInstance().reference.child("user_business")
        databaseRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                jobsListHorizontal.clear()
                jobsList.clear()

                for (userBusinessSnapshot in dataSnapshot.children) {
                    val businessName = userBusinessSnapshot.child("businessName").value.toString()
                    val businessLogo = userBusinessSnapshot.child("businessLogo").value.toString()
                    val address = userBusinessSnapshot.child("address").value.toString()
                    val ratings = userBusinessSnapshot.child("ratings").value.toString().toDouble()

                    val formattedRatings = if (ratings > 5.0) "5.0".toDouble()
                    else ratings.toString().toDouble()


                    val jobsSnapshot = userBusinessSnapshot.child("Jobs")

                    for (jobSnapshot in jobsSnapshot.children) {
                        val jobId = jobSnapshot.key.toString()

                        val isJobApplied = appliedJobsList.any { it.jobId1 == jobId }
                        if (!isJobApplied) {

                        val jobTitle = jobSnapshot.child("jobTitle").value.toString()
                        val payPerHour = jobSnapshot.child("payPerHour").value.toString()
                        val startTime = jobSnapshot.child("startTime").value.toString()
                        val endTime = jobSnapshot.child("endTime").value.toString()
                        val description = jobSnapshot.child("description").value.toString()
                        val durationInSeconds = jobSnapshot.child("durationInSeconds").value.toString().toLong()
                        val payPerDay = jobSnapshot.child("payPerDay").value.toString().toDouble()
                        val payPerDayWithCut = payPerDay - (payPerDay * 0.1)
                        val userId = jobSnapshot.child("userId").value.toString()

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
                            payPerDayWithCut.toInt(),
                            userId
                        )
                            jobsList.add(job)
                            if (payPerDayWithCut > 200 && formattedRatings > 4.0) {
                                jobsListHorizontal.add(job)
                            }
                        }
                    }
                }

                if (jobsList.isEmpty() ){
                    textrViewJobsToExplore.visibility = View.GONE
                    nothing_to_show_text_view.visibility = View.VISIBLE

                } else if(jobsListHorizontal.isEmpty()){
                    textrViewTopSearches.visibility = View.GONE
                    nothing_to_show_text_view.visibility = View.GONE
                }else{
                    textrViewJobsToExplore.visibility = View.VISIBLE
                    textrViewTopSearches.visibility = View.VISIBLE
                    nothing_to_show_text_view.visibility = View.GONE
                }

                myAdapter.notifyDataSetChanged()
                Horizontal_Scroll_Adapter1.notifyDataSetChanged()

                recyclerView.visibility = View.VISIBLE
                recyclerViewHorizontal.visibility = View.VISIBLE
                textrViewJobsToExplore.visibility= View.VISIBLE
                loadingProgressBar.visibility =View.GONE


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "unable to retrive data",
                    Toast.LENGTH_LONG).show()
            }
        })

           }




    private fun dataInitialized(){
        sorts_list_array = arrayListOf()

        sort_headings_list = arrayOf(
                "Remote",
                "OnField",
                "Rating 4.0+",
                "High Paying",
                "Near You"
        )
        for (i in sort_headings_list.indices){
            val sorts= sort_header_list_data_class( sort_headings_list[i] )
            sorts_list_array.add(sorts)
        }
    }



}


