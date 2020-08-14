package jp.tsumura.takuya.cameradefault

import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.BaseAdapter
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.get
import com.google.android.gms.tasks.Task
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.collections.ArrayList

class MoviesListActivity : AppCompatActivity() {
    //private lateinit var mMovies:Movies
    private lateinit var mAdapter: MoviesListAdapter
    val mMoviesArrayList = mutableListOf<Movies>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies_list)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

/*
        val storage = Firebase.storage
        val listRef = storage.reference.child("images/storage/emulated/0/Android/media")
        val items = listRef.listAll()

        items?.let {
            for(item in it){
                val path = item.path
                val thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MICRO_KIND)
                val tit = "No."+"$item.size"
                mMoviesArrayList.add(Movies(thumb,tit))
            }
        }
*/

        // ListViewの設定
        val listView = findViewById<GridView>(R.id.gridview)
        mAdapter = MoviesListAdapter(this,mMoviesArrayList)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }
}