package mchehab.com.customlistviewimage

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.AbsListView
import android.widget.ListView
import android.widget.ProgressBar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.ref.WeakReference



class MainActivity : AppCompatActivity() {

    val listCompleteData = mutableListOf<Person>()
    val listPerson = mutableListOf<Person>()

    val listView: ListView by lazy {
        findViewById<ListView>(R.id.listView)
    }

    val listViewAdapter: ListViewAdapter by lazy {
        ListViewAdapter(this, listPerson)
    }

    val floatingActionButtonAdd by lazy {
        findViewById<FloatingActionButton>(R.id.floatingActionButtonAdd)
    }

    val broadcastReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            addMoreItems()
            progressBar.visibility = View.GONE
        }
    }

    val progressBar: ProgressBar by lazy{
        val progressBarView = LayoutInflater.from(this).inflate(R.layout
                .bottom_listview_progressbar, null)
        progressBarView.findViewById<ProgressBar>(R.id.progressBar)
    }

    var asyncTask: AsyncTaskWait? = null

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter
        ("done"))
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listCompleteData.addAll(readListFromFile())

        for (i in 0..10){
            listPerson.add(listCompleteData[i])
        }
        listView.addFooterView(progressBar)
        listView.adapter = listViewAdapter
        setListViewOnScrollListener()
        setFloatingActionButtonAddListener()
    }

    private fun setFloatingActionButtonAddListener(){
        floatingActionButtonAdd.setOnClickListener {
            val intent = Intent(this, ActivityAdd::class.java)
            startActivityForResult(intent, 101)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.activity_main_menu, menu)

        val searchView = menu!!.findItem(R.id.action_search).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                progressBar.visibility = View.GONE
                listViewAdapter.filter(newText)
                return true
            }
        })

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101 && resultCode == Activity.RESULT_OK){
            val person = Person(data!!.getStringExtra("firstName"), data!!.getStringExtra
            ("lastName"), data!!.getStringExtra("description"), data!!.getStringExtra
            ("imageName"))
            listViewAdapter.addItem(person)
            listViewAdapter.notifyDataSetChanged()
        }
    }

    private fun setListViewOnScrollListener(){
        listView.setOnScrollListener(object: AbsListView.OnScrollListener{
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && listView
                        .lastVisiblePosition == listPerson.size){
                    if(asyncTask == null || asyncTask?.status != AsyncTask.Status.RUNNING){
                        progressBar.visibility = View.VISIBLE
                        asyncTask = AsyncTaskWait(WeakReference(this@MainActivity
                                .applicationContext))
                        asyncTask!!.execute()
                    }
                }
            }
        })
    }

    private fun addMoreItems(){
        val size = listPerson.size
        for (i in 1..10) {
            if ((size + i) < listCompleteData.size) {
                listViewAdapter.addItem(listCompleteData[size + i])
            }
        }
    }

    private fun readListFromFile(): List<Person>{
        val jsonString = assets.open("persons.txt")
                .bufferedReader()
                .readText()

        return Gson().fromJson(jsonString, object: TypeToken<List<Person>>(){}.type)
    }
}