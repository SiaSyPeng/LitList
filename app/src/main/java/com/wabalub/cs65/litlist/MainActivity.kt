package com.wabalub.cs65.litlist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.wabalub.cs65.litlist.MyLibs.InternetMgmtLib

import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import com.wabalub.cs65.litlist.GsonClasses.Playlist
import com.wabalub.cs65.litlist.GsonClasses.Song


class MainActivity : AppCompatActivity(), InternetMgmtLib.InternetListener, MapFragment.OnFragmentInteractionListener,
        PlaylistFragment.OnListFragmentInteractionListener {

    override fun onListFragmentInteraction(item: Song?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFragmentInteraction(uri: Uri?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        @JvmStatic var playlist : Playlist = Playlist(emptyList(), "")
    }

    override fun onErrorResponse(requestCode: Int, res: String?) {
    }

    override fun onResponse(requestCode: Int, res: String?) {
    }

    // adapters
    private var pagerAdapter : ActionbarPagerAdapter? = null

    // for networking
    lateinit private var queue: RequestQueue

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

    fun onStartExploringClicked(view : View) {
        intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }
}
