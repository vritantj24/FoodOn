package com.vritant.foodon.chefUtils

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
import com.vritant.foodon.customerUtils.CustomerHomeFragment
import kotlinx.android.synthetic.main.activity_chef_pannel.*

class ChefPanel : AppCompatActivity(),NavigationBarView.OnItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chef_pannel)

        this.title = "Home"

        chef_bottom_navigation.setOnItemSelectedListener(this)

        val name = intent.getStringExtra("PAGE")
        if (name!=null)
        {
            when (name) {
                "OrderPage" -> {
                    setCurrentFragment(ChefPendingOrdersFragment())
                }
                "ConfirmPage" -> {
                    setCurrentFragment(ChefOrdersFragment())
                }
                "AcceptOrderPage" -> {
                    setCurrentFragment(ChefOrdersFragment())
                }
                "DeliveredPage" -> {
                    setCurrentFragment(CustomerHomeFragment())
                }
            }
        }
        else
        {
            setCurrentFragment(ChefHomeFragment())
        }
    }

    private fun setCurrentFragment(fragment: Fragment) : Boolean
    {
        supportFragmentManager.beginTransaction().replace(R.id.chef_frag_container,fragment).commit()
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
            R.id.chefHome ->setCurrentFragment(ChefHomeFragment())
            R.id.pendingOrders ->setCurrentFragment(ChefPendingOrdersFragment())
            R.id.orders ->setCurrentFragment(ChefOrdersFragment())
            R.id.chefProfile ->setCurrentFragment(ChefProfileFragment())
        }
        return true
    }

}