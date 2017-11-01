package com.wabalub.cs65.litlist

import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.wabalub.cs65.litlist.ActionbarPagerAdapter
import com.wabalub.cs65.litlist.MyLibs.InternetMgmtLib

import java.io.File
import com.wabalub.cs65.litlist.R.layout.activity_main

class MainActivity : AppCompatActivity(), InternetMgmtLib.InternetListener {
    override fun onErrorResponse(requestCode: Int, res: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResponse(requestCode: Int, res: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // adapters
    private var pagerAdapter: ActionbarPagerAdapter? = null

    // for networking
    lateinit internal var queue: RequestQueue

    lateinit internal var profile: ContactsContract.Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // networking setup
        queue = Volley.newRequestQueue(this)
    }
}
