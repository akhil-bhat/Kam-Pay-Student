package com.example.application.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.application.R
import com.example.application.user_student_data_class
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProfileFragment : Fragment() {
    private lateinit var nameTextView: TextView
    private lateinit var mobileNoTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var ratingTextView: TextView
    private lateinit var ratingLinearLayout: LinearLayout
    private lateinit var profileImageView: ShapeableImageView
    private lateinit var SheerIdStatusIcon: ImageView
    private lateinit var milestoneProgress1: SeekBar
    private lateinit var milestoneSetted:TextView
    private lateinit var textViewCompletedMilestone:TextView
    private lateinit var textViewPercentageMilestoneProgress:TextView
    private lateinit var textViewBtnGoToEarnings :TextView
    private var fragmentChangeListener: FragmentChangeListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)


        nameTextView = view.findViewById(R.id.user_name)
        mobileNoTextView = view.findViewById(R.id.user_mobile_no_)
        emailTextView = view.findViewById(R.id.user_email)
        ratingTextView = view.findViewById(R.id.user_ratings)
        ratingLinearLayout = view.findViewById(R.id.linear_layout_rating)
        profileImageView = view.findViewById(R.id.profile_page_image)
        SheerIdStatusIcon = view.findViewById(R.id.sheerdIdStatusIcon)
        milestoneProgress1 = view.findViewById(R.id.custom_seekbar)
        textViewCompletedMilestone =view.findViewById(R.id.start_from_0)
        milestoneSetted = view.findViewById(R.id.milestone_setted)
        textViewPercentageMilestoneProgress= view.findViewById(R.id.percentageMilestoneProgress)
        textViewBtnGoToEarnings = view.findViewById(R.id.go_to_earnings_button)

        textViewBtnGoToEarnings.setOnClickListener {
            val earningsFragment = EarningsFragment()
            fragmentChangeListener?.replaceFragment(earningsFragment)
        }

        fetchUserData()

        return view
    }



    interface FragmentChangeListener {
        fun replaceFragment(fragment: Fragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentChangeListener) {
            fragmentChangeListener = context
        } else {
            throw RuntimeException("$context must implement FragmentChangeListener")
        }
    }

    private fun fetchUserData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val databaseRef = FirebaseDatabase.getInstance().getReference("user_student").child(uid!!)
            .child("info")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(user_student_data_class::class.java)

                    nameTextView.text = user?.name
                    mobileNoTextView.text = user?.mobileNo
                    emailTextView.text = user?.email
                    val earningsPerDay = user?.earningsPerDay!!.toInt()
                    val earningsPerDayWithCut = earningsPerDay - (earningsPerDay * 0.1)
                    textViewCompletedMilestone.text = earningsPerDayWithCut.toInt().toString()


                    val milestoneSetted1 = user?.milestone ?: 0
                    val milestoneText = "Rs. $milestoneSetted1"
                    milestoneSetted.text = milestoneText
// ...
                    val ratings = user?.ratings?.toFloat() ?: 0f

                    val formattedRatings = if (ratings > 5.0) "5.0"
                    else ratings.toString().toDouble()

                    ratingLinearLayout.setBackgroundColor(
                        when {
                            ratings >= 4.50 -> Color.parseColor("#318F2F") // light green
                            ratings >= 4.00 -> Color.parseColor("#3CC93A") // green
                            ratings >= 3.50 -> Color.parseColor("#B1AB16") // yellow
                            ratings >= 3.00 -> Color.parseColor("#E5DE21") // light yellow
                            ratings >= 2.50 -> Color.parseColor("#B18616") // orange
                            ratings >= 2.00 -> Color.parseColor("#FFC225") // light orange
                            else -> Color.parseColor("#FF3F25") // red
                        }
                    )
                    ratingTextView.text = formattedRatings.toString()


                    val sheerId = user?.sheerID
                    if (sheerId == true) {
                        SheerIdStatusIcon.setImageResource(R.drawable.verified_icon_green)
                    }
                    else {
                        SheerIdStatusIcon.setImageResource(R.drawable.pending_icon_yellow) // Set your default image resource
                    }
                    //
                    val milestone = earningsPerDayWithCut
                    milestoneProgress1.max = milestoneSetted1
                    milestoneProgress1.progress = milestone.toInt()



                    milestoneProgress1.setOnTouchListener(OnTouchListener { v, event -> true })

                    val MilestonePercentage = (milestone.toFloat() / milestoneSetted1.toFloat()) * 100
                    textViewPercentageMilestoneProgress.text = "${MilestonePercentage.toInt()} %"

                    val color = when {
                        MilestonePercentage < 20 -> ContextCompat.getColor(requireContext(), R.color.dark_red)
                        MilestonePercentage in 20.0..39.9 -> ContextCompat.getColor(requireContext(), R.color.dark_orange)
                        MilestonePercentage in 40.0..59.9 -> ContextCompat.getColor(requireContext(), R.color.dark_yellow)
                        else -> ContextCompat.getColor(requireContext(), R.color.dark_green)
                    }

                    textViewPercentageMilestoneProgress.setTextColor(color)


                    textViewPercentageMilestoneProgress.setTextColor(color)



                    val imageUrl = user?.profileImageUrl
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.profile_default_photo)
                            .into(profileImageView)


                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error fetching data: ${error.message}")
            }
        })
    }

    }

