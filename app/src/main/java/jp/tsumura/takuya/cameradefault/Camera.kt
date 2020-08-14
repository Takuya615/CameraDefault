package jp.tsumura.takuya.cameradefault

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.TextureView
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*

import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@SuppressLint("RestrictedApi, ClickableViewAccessibility")
class Camera : AppCompatActivity(), LifecycleOwner {
    private lateinit var viewFinder: TextureView
    private lateinit var captureButton: ImageButton
    private lateinit var ResultButton: Button
    private lateinit var SwichButton: ImageButton
    private lateinit var videoCapture: VideoCapture
    private lateinit var storage: FirebaseStorage
    private var Flag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        storage = Firebase.storage
        viewFinder = findViewById(R.id.view_finder)
        captureButton = findViewById(R.id.capture_button)
        ResultButton = findViewById(R.id.result_button)
        SwichButton = findViewById(R.id.camera_switch_button)

        // Request camera permissions
        if (allPermissionsGranted()) {
            viewFinder.post {
                startCamera()
            }
        } else {
            ActivityCompat.requestPermissions(
                this, Camera.REQUIRED_PERMISSIONS, Camera.REQUEST_CODE_PERMISSIONS
            )
        }
//クリックすると、活動の記録をダイアログでみられるしくみ
        ResultButton.setOnClickListener(){
            showDialog()
        }
        //撮影開始ボタン、2度目のクリックで停止後、すぐにFirebaseへ保存される。

        captureButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val file = File(externalMediaDirs.first(),
                    "${System.currentTimeMillis()}.mp4")
                captureButton.setBackgroundColor(Color.GREEN)

                videoCapture.startRecording(file,object:VideoCapture.OnVideoSavedListener{
                    override fun onVideoSaved(file: File?) {
                        //FirebaseStorageへ　アップロード
                        val storageRef = storage.reference
                        val photoRef = storageRef.child("images/${file?.path}")
                        val movieUri = Uri.fromFile(file)
                        val uploadTask = photoRef.putFile(movieUri)
                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener {
                            Log.e(tag,"ストレージへ保存失敗")
                        }.addOnSuccessListener {
                            Log.e(tag,"ストレージへ保存成功")
                            Log.e(tag,photoRef.toString())
                        }
                    }
                    override fun onError(useCaseError: VideoCapture.UseCaseError?, message: String?, cause: Throwable?) {
                        Log.e(tag, "Video Error: $message")
                    }
                })
            }else {
                if (event.action == MotionEvent.ACTION_UP) {
                    captureButton.setBackgroundColor(Color.RED)
                    videoCapture.stopRecording()
                    Log.e(tag, "Video File stopped")
                }
            }
            false
        }
    }

    //ココからパーミッション系
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        if (requestCode == Camera.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(this,
                    "許可がないと動作しません",
                    Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = Camera.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

//ここからカメラ

    private fun startCamera(){
        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().build()
// Build the viewfinder use case
        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            viewFinder.surfaceTexture = it.surfaceTexture
        }
        // Create a configuration object for the video use case
        val videoCaptureConfig = VideoCaptureConfig.Builder().apply {
            setTargetRotation(viewFinder.display.rotation)
        }.build()
        videoCapture = VideoCapture(videoCaptureConfig)

// Bind use cases to lifecycle
        CameraX.bindToLifecycle(this, preview,videoCapture)
    }


    private fun showDialog(){
        // AlertDialog.Builderクラスを使ってAlertDialogの準備をする
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("連続継続日数")
        alertDialogBuilder.setMessage("メッセージ")
        alertDialogBuilder.setTitle("総活動日数")
        alertDialogBuilder.setMessage("メッセージ")
        alertDialogBuilder.setTitle("総活動時間")
        alertDialogBuilder.setMessage("メッセージ")


        // 肯定ボタンに表示される文字列、押したときのリスナーを設定する
        alertDialogBuilder.setPositiveButton("メイン画面へ"){dialog, which ->
            Log.d("UI_PARTS", "肯定ボタン")
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        // 中立ボタンに表示される文字列、押したときのリスナーを設定する
        // 使わない引数の場合は「_」と記述するのがkotlinの慣習
        alertDialogBuilder.setNeutralButton("中立"){_,_ ->
            Log.d("UI_PARTS", "中立ボタン")
        }

        // 否定ボタンに表示される文字列、押したときのリスナーを設定する
        alertDialogBuilder.setNegativeButton("否定"){_,_ ->
            Log.d("UI_PARTS", "否定ボタン")
        }

        // AlertDialogを作成して表示する
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    companion object {
        private const val tag = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO)
    }
}