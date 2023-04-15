package com.example.application

data class job_details_data_class(
    val limit: Int,
    val products: List<Product>,
    val skip: Int,
    val total: Int
)