package com.vritant.foodon.customerUtils

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.vritant.foodon.MainActivity
import com.vritant.foodon.R
import kotlinx.android.synthetic.main.activity_customer_panel.*

class CustomerPanel : AppCompatActivity(),NavigationBarView.OnItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_panel)

        this.title = "Home"

        customer_bottom_navigation.setOnItemSelectedListener(this)

        val name = intent.getStringExtra("PAGE")

        if(name!=null)
        {
            when (name) {
                "HomePage" -> {
                    setCurrentFragment(CustomerHomeFragment())
                }
                "PreparingPage" -> {
                    setCurrentFragment(CustomerTrackFragment())
                }
                "DeliveryOrderPage" -> {
                    setCurrentFragment(CustomerTrackFragment())
                }
                "ThankYouPage" -> {
                    setCurrentFragment(CustomerHomeFragment())
                }
            }
        }
        else
        {
            setCurrentFragment(CustomerHomeFragment())
        }
    }

    private fun setCurrentFragment(fragment: Fragment) : Boolean
    {
        supportFragmentManager.beginTransaction().replace(R.id.customer_frag_container,fragment).commit()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.logout,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId== R.id.logout)
        {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            R.id.customer_Home ->setCurrentFragment(CustomerHomeFragment())
            R.id.cart ->setCurrentFragment(CustomerCartFragment())
            R.id.customer_order ->setCurrentFragment(CustomerOrdersFragment())
            R.id.track ->setCurrentFragment(CustomerTrackFragment())
            R.id.customer_profile ->setCurrentFragment(CustomerProfileFragment())
        }
        return true
    }
}