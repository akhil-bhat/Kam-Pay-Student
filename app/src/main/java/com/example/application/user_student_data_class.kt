package com.example.application

data class user_student_data_class(val name: String,
                                   val age: String,
                                   val mobileNo: String)
{
    // Default constructor required for Firebase
    constructor() : this("", "", "")
}
