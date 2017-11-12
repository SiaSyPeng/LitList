package com.wabalub.cs65.litlist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.wabalub.cs65.litlist.MyLibs.InternetMgmtLib

import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager



class MainActivity : AppCompatActivity(), InternetMgmtLib.InternetListener {
    override fun onErrorResponse(requestCode: Int, res: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResponse(requestCode: Int, res: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // adapters
    private var pagerAdapter : ActionbarPagerAdapter? = null

    // for networking
    lateinit internal var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // networking setup
        queue = Volley.newRequestQueue(this)

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        val viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        pagerAdapter = ActionbarPagerAdapter(supportFragmentManager,
                this@MainActivity)
        viewPager.adapter = pagerAdapter

        // Give the TabLayout the ViewPager
        val tabLayout = findViewById<View>(R.id.sliding_tabs) as TabLayout
        tabLayout.setupWithViewPager(viewPager)

        // Start on the only tab we have implemented
        val tab = tabLayout.getTabAt(0)!!
        tab.select()
    }
}
