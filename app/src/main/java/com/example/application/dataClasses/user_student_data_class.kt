package com.example.application


data class user_student_data_class(
    val name: String = "",
    val age: String = "",
    val mobileNo: String = "",
    val profileImageUrl: String = "",
    val email: String? = "",
    val sheerID: Boolean,
    val earningsPerDay:Int,
    var totalEarnings:Int,
    val earningsPerMonth:Int,
    val balance: Int,
    val ratings: Double,
    val milestone: Int,
    val userId:String
    )
{

    // Default constructor required for Firebase
    constructor() : this("", "", "","","",
        true,0,0,0,0,0.0,100,"")
}
