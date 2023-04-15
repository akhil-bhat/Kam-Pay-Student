package com.example.application


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity:AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var mDatabase: DatabaseReference
    private lateinit var user1: user_student_data_class
    private lateinit var uid:String
    lateinit var recyclerView: RecyclerView
    lateinit var myAdapter: MyAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView = findViewById(R.id.recyclerView)

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser!!.uid.toString()
        mDatabase= FirebaseDatabase.getInstance().getReference("user_student")


        if (uid.isNotEmpty()){
            getUserData()
        }

        binding.signOutBtn.setOnClickListener {
            Firebase.auth.signOut()
            finish()
            startActivity(Intent(this@MainActivity
                , SignInActivity::class.java)) }

        val retrofitData = retrofitBuilder.getProductData()

        retrofitData.enqueue(object : Callback<job_details_data_class?> {

            override fun onResponse(call: Call<job_details_data_class?>, response: Response<job_details_data_class?>) {
                // if api call is a success, then use the data of API and show in your app
                var responseBody = response.body()
                val productList = responseBody?.products!!

                myAdapter = MyAdapter(this@MainActivity, productList)
                recyclerView.adapter = myAdapter
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            }

            override fun onFailure(call: Call<job_details_data_class?>, t: Throwable) {
                // if api call fails
                Log.d("Main Activity ", "onFailure: " + t.message)
            }
        })

    }


    private fun getUserData() {
        mDatabase.child(uid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

//                    user1 = snapshot.getValue(user_student::class.java)!!
//                    binding.textView.setText(user1.name)
//                    binding.textView1.setText(user1.age)
//                    binding.textView2.setText(user1.mobileNo)
                }else {
                    Toast.makeText(this@MainActivity, "User data not found", Toast.LENGTH_SHORT)
                        .show()

                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
            }

        })
    }


}
