package com.example.application


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.application.databinding.ActivityMainBinding
import com.example.application.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity:AppCompatActivity() ,ProfileFragment.FragmentChangeListener{
    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var mDatabase: DatabaseReference
    private lateinit var user1: user_student_data_class
    private lateinit var uid:String
    private lateinit var menuButton:ImageView
    private lateinit var sideMenu:NavigationView
    private lateinit var bottomNavView:BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newAppName = "Kam Pay (Student)"
        val appInfo = applicationInfo
        appInfo.labelRes = 0
        appInfo.nonLocalizedLabel = newAppName
        recreate()


        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser!!.uid
        mDatabase= FirebaseDatabase.getInstance().getReference("user_student")

        showFragment(HomeFragment())



        if (uid.isNotEmpty()) {
                getUserData()


        }

        menuButton = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout.openDrawer(GravityCompat.START)
            window.statusBarColor = this.resources.getColor(R.color.dark_teal)
        }

        sideMenu = findViewById(R.id.navigation_view)
        sideMenu.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_signout ->{
                    Firebase.auth.signOut()
                    finish()
                    startActivity(Intent(this@MainActivity, SignInActivity::class.java))

                }

                R.id.edit_profile -> showFragment(ProfileCompletionFragment())
//                R.id.nav_settings -> showFragment(SettingsFragment())
                R.id.nav_profile -> showFragment(ProfileFragment())
                R.id.nav_about -> showFragment(AboutApplicationFragment())
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }



        bottomNavView = findViewById(R.id.bottomNavView)
        bottomNavView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_earnings-> replaceFragment(EarningsFragment())

                R.id.nav_support -> {
                    replaceFragment(SupportFragment())
                }

                R.id.nav_applied -> replaceFragment(AppliedFragment())
            }
            true
        }
    }


    override fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // Replace with the provided fragment instance
            .addToBackStack(null)
            .commit()

        when (fragment) {
            is EarningsFragment -> {
                binding.bottomNavView.menu.findItem(R.id.nav_earnings)?.let { earningsMenuItem ->
                    earningsMenuItem.isChecked = true
                    earningsMenuItem.isEnabled = true
                }
                binding.bottomNavView.menu.findItem(R.id.nav_support)?.isEnabled = false
            }
            is SupportFragment -> {
                binding.bottomNavView.menu.findItem(R.id.nav_support)?.let { supportMenuItem ->
                    supportMenuItem.isChecked = true
                    supportMenuItem.isEnabled = true
                }
                binding.bottomNavView.menu.findItem(R.id.nav_earnings)?.isEnabled = false

//            is ProfileFragment->{
//                binding.navi.menu.findItem(R.id.nav_support)?.let { supportMenuItem ->
//                    supportMenuItem.isChecked = true
//                    supportMenuItem.isEnabled = true
//                }
        }
            else -> {
                // Handle other fragments
                binding.bottomNavView.menu.findItem(R.id.nav_earnings)?.isEnabled = true
                binding.bottomNavView.menu.findItem(R.id.nav_support)?.isEnabled = true
            }
        }
    }

    private fun getUserData() {

        mDatabase.child(uid).child("info").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    sideMenu.visibility = View.VISIBLE
                    menuButton.visibility = View.VISIBLE
                    bottomNavView.visibility = View.VISIBLE
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

                    user1 = snapshot.getValue(user_student_data_class::class.java)!!

                    val headerView = findViewById<NavigationView>(R.id.navigation_view).getHeaderView(0)
                    val userNameTextView = headerView.findViewById<TextView>(R.id.user_name_side_menu)
                    val userSheerIdStatus = headerView.findViewById<TextView>(R.id.user_sheer_id_status)
                    val user_ratings = headerView.findViewById<TextView>(R.id.user_ratings_side_menu)
                    val user_mobile_no = headerView.findViewById<TextView>(R.id.user_mobile_no)
                    val user_email = headerView.findViewById<TextView>(R.id.user_email_side_menu)


                    val profileImageUrlSnapshot = snapshot
                        .child("profileImageUrl")

                    if (profileImageUrlSnapshot.exists()) {
                        val profileImageUrl = profileImageUrlSnapshot.value.toString()
                        // call a function to load the image using the URL
                        loadImage(profileImageUrl)
                    }
                    else{
                        showFragment(ProfileCompletionFragment())
                                            }

                    userNameTextView.text = user1.name

                    user_mobile_no.text= user1.mobileNo

                    user_email.text = user1.email
                    val sheerId = user1?.sheerID
                    if (sheerId == true) {
                        userSheerIdStatus.setText("Fully Verified")
                        val greenColor = ContextCompat.getColor(applicationContext, R.color.dark_green) // Get the green color from colors.xml
                        userSheerIdStatus.setTextColor(greenColor)
                    } else {
                        userSheerIdStatus.setText("Verification Pending")
                        val greenColor = ContextCompat.getColor(applicationContext, R.color.dark_yellow) // Get the green color from colors.xml
                        userSheerIdStatus.setTextColor(greenColor)

                    }
                    val linearLayoutRating = headerView.findViewById<LinearLayout>(R.id.linear_layout_rating_side_menu)
// ...

                    val ratings = user1?.ratings?.toFloat() ?: 0f

                    val formattedRatings = if (ratings > 5.0) "5.0"
                    else ratings.toString().toDouble()

                    linearLayoutRating.setBackgroundColor(when {
                        ratings >= 4.50 -> Color.parseColor("#318F2F") // light green
                        ratings >= 4.00 -> Color.parseColor("#3CC93A") // green
                        ratings >= 3.50 -> Color.parseColor("#B1AB16") // yellow
                        ratings >= 3.00 -> Color.parseColor("#E5DE21") // light yellow
                        ratings >= 2.50 -> Color.parseColor("#B18616") // orange
                        ratings >= 2.00 -> Color.parseColor("#FFC225") // light orange
                        else -> Color.parseColor("#FF3F25") // red
                    })

                    user_ratings.text = formattedRatings.toString()


                }

                else {
                    menuButton.visibility = View.GONE
                    bottomNavView.visibility = View.GONE

                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    showFragment(ProfileCompletionFragment())
                    Toast.makeText(this@MainActivity, "User data not found, Please Update your Profile",
                        Toast.LENGTH_LONG
                    ).show()
                    onBackPressed()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                showFragment(ProfileCompletionFragment())
                Toast.makeText(this@MainActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun loadImage(profileImageUrl: String) {
        val headerView = findViewById<NavigationView>(R.id.navigation_view).getHeaderView(0)
        val userProfileImageView = headerView.findViewById<CircleImageView>(R.id.profile_pic_user_sidemenu)

        Glide.with(this)
            .load(profileImageUrl)
            .into(userProfileImageView)
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager?.let { fragmentManager ->
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onBackPressed() {
        // Do nothing to disable the back button
    }


}
