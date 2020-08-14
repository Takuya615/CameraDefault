package jp.tsumura.takuya.cameradefault

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList

class MoviesListAdapter(context: Context,val mMoviesArrayList:MutableList<Movies>) : BaseAdapter(){

    private var mLayoutInflater: LayoutInflater? = null

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        if(mMoviesArrayList==null||mMoviesArrayList.count()>0)

        return mMoviesArrayList.count()
        else{
            Log.e("TAG","カウントがゼロ")
        }
        return 0
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItem(position: Int): Any {
        return mMoviesArrayList[position]
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        if (convertView == null) {
            convertView = mLayoutInflater!!.inflate(R.layout.list_movies, parent, false)!!
        }

        val title = mMoviesArrayList[position].title
        val titleTextView = convertView.findViewById<View>(R.id.title) as TextView
        titleTextView.text = title


        val thumbnail = mMoviesArrayList[position].thumbnail
        if (thumbnail!=null) {
            //val image = BitmapFactory.decodeByteArray(thum, 0, thum.size).copy(Bitmap.Config.ARGB_8888, true)
            val imageView = convertView.findViewById<ImageView>(R.id.image_view)
            imageView.setImageBitmap(thumbnail)
        }else{
            Log.e("TAG","サムネがない！")
        }



        return convertView
    }


}