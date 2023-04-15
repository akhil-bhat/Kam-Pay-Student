package com.example.application

import retrofit2.Call
import retrofit2.http.GET


interface ApiInterface {

    @GET("products")

    fun getProductData() : Call<job_details_data_class>

}