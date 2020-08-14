package jp.tsumura.takuya.cameradefault

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.provider.MediaStore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class Movies (
    val thumbnail:Bitmap,
    val title:String){
}