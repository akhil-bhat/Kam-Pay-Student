package com.example.application.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.example.application.R
import com.example.application.user_student_data_class
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.HashMap


class EarningsFragment : Fragment() {

    private lateinit var textViewBalance:TextView
    private lateinit var textViewTodayEarning:TextView
    private lateinit var total_earnings:TextView
    private lateinit var textViewMonthlyEarning:TextView

    private lateinit var textViewMilestoneSettedForWeek :TextView
    private lateinit var editTextMilestoneSettedForWeek :EditText
    private lateinit var edit_btn_milestone : ImageView
    private lateinit var tick_btn_milestone : ImageView
    private lateinit var textViewContactSupportBtn:TextView
    private var balance: Int = 0
    private var earningsPerDay = 0
    private var totalEarnings = 0
    private var earningsPerMonth = 0
    private lateinit var databaseRef:DatabaseReference
    private lateinit var refesh_btn_balance :ImageView
    private lateinit var withdraw_btn : RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_earnings, container, false)

        textViewBalance = view.findViewById(R.id.balance_text_view)
        textViewTodayEarning = view.findViewById(R.id.today_earning_text_view)
        total_earnings = view.findViewById(R.id.total_earnings)
        textViewMonthlyEarning = view.findViewById(R.id.this_month_earning_text_view)
        textViewMilestoneSettedForWeek = view.findViewById(R.id.milestone_setted_for_week)
        editTextMilestoneSettedForWeek = view.findViewById(R.id.edit_text_milestone_set)
        edit_btn_milestone = view.findViewById(R.id.milestone_edit_btn)
        tick_btn_milestone = view.findViewById(R.id.tick_icon_btn_milestonSET)
        textViewContactSupportBtn = view.findViewById(R.id.contact_suppot_text_btn)
        withdraw_btn = view.findViewById(R.id.withdraw_button_relative_layout)
        refesh_btn_balance = view.findViewById(R.id.balance_refresh_btn)

        fetchUserData()

        withdraw_btn.setOnClickListener {

            Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT)
                .show()

        }

        textViewContactSupportBtn.setOnClickListener {
            val supportFragment = SupportFragment()
            (requireActivity() as? ProfileFragment.FragmentChangeListener)?.replaceFragment(
                supportFragment
            )
        }

        return view

    }

    private fun fetchUserData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
       databaseRef = FirebaseDatabase.getInstance()
            .getReference("user_student").child(uid!!).child("info")

        databaseRef.addValueEventListener(object : ValueEventListener {

            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(user_student_data_class::class.java)

                    val milestoneSetted = user?.milestone ?: 0
                    val milestoneText = "Rs. $milestoneSetted"


                    val realEarningsPerDay = user?.earningsPerDay!!

                    val payPerDayWithCut = realEarningsPerDay - (realEarningsPerDay * 0.1)

                    earningsPerDay += payPerDayWithCut.toInt()


                    textViewTodayEarning.text = earningsPerDay.toString()

                     total_earnings.text = earningsPerDay.toString()
                    textViewMonthlyEarning.text = earningsPerDay.toString()
                    textViewBalance.text = earningsPerDay.toString()

                    refesh_btn_balance.setOnClickListener {
//                        updateEarnings(payPerDayWithCut)
                    }
//                    totalEarnings += payPerDayWithCut.toInt()
//                    balance += payPerDayWithCut.toInt()
//
//
//                    user.totalEarnings = totalEarnings
//                    user.balance = balance
//                    getTotal_Earnings(totalEarnings)
//
//                    getBalance(balance)
//                    textViewMonthlyEarning.setText("Coming Very Soon")



                    if (milestoneSetted != null) {
                        editTextMilestoneSettedForWeek.visibility = View.GONE
                        tick_btn_milestone.visibility = View.GONE
                        textViewMilestoneSettedForWeek.visibility = View.VISIBLE
                        textViewMilestoneSettedForWeek.text = milestoneText
                        edit_btn_milestone.visibility = View.VISIBLE

                        edit_btn_milestone.setOnClickListener {
                            tick_btn_milestone.setOnClickListener {
                                updateMilestone()
                                textViewMilestoneSettedForWeek.text = milestoneText
                            }
                            editTextMilestoneSettedForWeek.visibility = View.VISIBLE
                            tick_btn_milestone.visibility = View.VISIBLE
                            textViewMilestoneSettedForWeek.visibility = View.GONE
                            edit_btn_milestone.visibility = View.GONE
                        }
                    }



                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "Error fetching data: ${error.message}")
            }
        })
    }

    private fun updateEarnings(payPerDayWithCut: Double) {

        totalEarnings += payPerDayWithCut.toInt()
        balance += payPerDayWithCut.toInt()

        databaseRef.child("totalEarnings").setValue(total_earnings)
        databaseRef.child("balance").setValue(balance)

        textViewMonthlyEarning.text = earningsPerDay.toString()
        textViewBalance.text = earningsPerDay.toString()
    }

    private fun updateMilestone() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val databaseRef = FirebaseDatabase.getInstance().getReference("user_student").child(uid!!)
        val newMilestoneValueText = editTextMilestoneSettedForWeek.text.toString()

        if (newMilestoneValueText.isNotBlank()) {
            val newMilestoneValue = newMilestoneValueText.toInt()
            if (newMilestoneValue < 100) {
                Toast.makeText(context, "Milestone can't be smaller than 100", Toast.LENGTH_SHORT)
                    .show()
            } else {


                val childUpdates = HashMap<String, Any>()
                childUpdates["milestone"] = newMilestoneValue

                databaseRef.child("info").updateChildren(childUpdates)
                    .addOnSuccessListener {
                        Toast.makeText(
                            context, "Milestone Updated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        editTextMilestoneSettedForWeek.visibility = View.GONE
                        tick_btn_milestone.visibility = View.GONE
                        textViewMilestoneSettedForWeek.visibility = View.VISIBLE
                        edit_btn_milestone.visibility = View.VISIBLE

                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "error updating the milestone", Toast.LENGTH_SHORT)
                            .show()

                    }
            }
        }
        else {
            Toast.makeText(context, " Empty Field :( ", Toast.LENGTH_SHORT).show()

        }
}


}