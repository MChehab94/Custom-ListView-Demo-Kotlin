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
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.ListView
import android.widget.ProgressBar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.parceler.Parcels
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

    var menuItemSearch: MenuItem? = null
    var menuItemDelete: MenuItem? = null

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
        setListOnLongPressListener()
        setFloatingActionButtonAddListener()
        setListViewOnItemClickListener()
    }

    private fun setListViewOnItemClickListener(){
        listView.setOnItemClickListener { parent, view, position, id ->
            val person = listViewAdapter.getItem(position)
            val intent = Intent(this@MainActivity, ActivityAdd::class.java)
            val bundle = Bundle()
            bundle.putParcelable("person", Parcels.wrap(person))
            intent.putExtras(bundle)
            startActivityForResult(intent, 102)
        }
    }

    private fun setFloatingActionButtonAddListener(){
        floatingActionButtonAdd.setOnClickListener {
            val intent = Intent(this, ActivityAdd::class.java)
            startActivityForResult(intent, 101)
        }
    }

    private fun loadData() {
        if(asyncTask == null || asyncTask?.status != AsyncTask.Status.RUNNING){
            progressBar.visibility = View.VISIBLE
            asyncTask = AsyncTaskWait(WeakReference(this@MainActivity
                    .applicationContext))
            asyncTask!!.execute()
        }
    }

    private fun setListOnLongPressListener() {
        listView.setOnItemLongClickListener { parent, view, position, id ->
            listViewAdapter.handleLongPress(position, view)
            if (listViewAdapter.listPersonsSelected.size > 0) {
                showDeleteMenu(true)
            } else {
                showDeleteMenu(false)
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)

        menuItemSearch = menu!!.findItem(R.id.action_search)
        menuItemDelete = menu.findItem(R.id.action_delete)

        menuItemDelete!!.setVisible(false)

        val searchView = menuItemSearch!!.actionView as SearchView

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

        menuItemDelete!!.setOnMenuItemClickListener {
            runOnUiThread {
                listViewAdapter.removeSelectedPersons()
                listViewAdapter.notifyDataSetChanged()
                if (listViewAdapter.count <= 5) {
                    loadData()
                }
            }
            showDeleteMenu(false)
            true
        }

        return true
    }

    private fun showDeleteMenu(show: Boolean){
        menuItemDelete!!.setVisible(show)
        menuItemSearch!!.setVisible(!show)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101 && resultCode == Activity.RESULT_OK){
            val person = Parcels.unwrap<Person>(data!!.extras.getParcelable("person"))
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
                    loadData()
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