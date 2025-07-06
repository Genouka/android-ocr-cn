package com.genouka.ocr

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.genouka.ocr.ocr.OCRTextRecognizer
import com.genouka.ocr.utils.Constants
import com.genouka.ocr.utils.Language
import com.genouka.ocr.utils.SpUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private val dirBest: File? = null
    private val dirStandard: File? = null
    private val dirFast: File? = null
    private val currentDirectory: File? = null

    /**
     * TrainingDataType: i.e Best, Standard, Fast
     */
    private val mTrainingDataType: String? = null
    private val mPageSegMode = 0
    private val parameters: MutableMap<String?, String?>? = null

    /**
     * AlertDialog for showing when language data doesn't exists
     */
    private var dialog: AlertDialog? = null
    private var mImageView: ImageView? = null
    private var mProgressIndicator: LinearProgressIndicator? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var mFloatingActionButton: FloatingActionButton? = null
    private var mDownloadLayout: LinearLayout? = null

    /**
     * Language name to be displayed
     */
    private var mLanguageName: TextView? = null
    private var executorService: ExecutorService? = null
    private var handler: Handler? = null
    private var mProgressBar: LinearProgressIndicator? = null
    private var mProgressMessage: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SpUtil.getInstance().init(this)

        mImageView = findViewById<ImageView>(R.id.source_image)
        mProgressIndicator = findViewById<LinearProgressIndicator>(R.id.progress_indicator)
        mSwipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh)
        mFloatingActionButton = findViewById<FloatingActionButton>(R.id.btn_scan)

        mProgressBar = findViewById<LinearProgressIndicator>(R.id.progress_bar)
        mProgressMessage = findViewById<TextView>(R.id.progress_message)
        mDownloadLayout = findViewById<LinearLayout>(R.id.download_layout)

        executorService = Executors.newFixedThreadPool(1)
        handler = Handler(Looper.getMainLooper())

        initViews()
    }

    private fun initViews() {
        mFloatingActionButton!!.setOnClickListener(View.OnClickListener { v: View? ->
            selectImage()
        })
        mSwipeRefreshLayout!!.setOnRefreshListener(OnRefreshListener {
            val drawable = mImageView!!.getDrawable()
            if (drawable != null) {
                val bitmap = (drawable as BitmapDrawable).getBitmap()
                if (bitmap != null) {
                    isRefresh = true
                    executorService!!.submit(ConvertImageToText(bitmap))
                }
            }
            mSwipeRefreshLayout!!.setRefreshing(false)
        })
    }

    override fun onResume() {
        super.onResume()
    }

    private fun selectImage() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this)
    }

    private fun convertImageToText(imageUri: Uri?) {
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri)
        } catch (e: IOException) {
            Log.e(TAG, "convertImageToText: " + e.getLocalizedMessage())
        }
        mImageView!!.setImageURI(imageUri)
        executorService!!.submit(ConvertImageToText(bitmap!!))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (result != null) {
                    convertImageToText(result.getUri())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executorService!!.shutdownNow()
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if (id == R.id.action_settings) {
            startActivityForResult(
                Intent(this, SettingsActivity::class.java),
                REQUEST_CODE_SETTINGS
            )
        }
        return super.onOptionsItemSelected(item)
    }


    fun saveBitmapToStorage(bitmap: Bitmap) {
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = openFileOutput("last_file.jpeg", MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fileOutputStream)
            fileOutputStream.close()
        } catch (e: IOException) {
            Log.e(TAG, "loadBitmapFromStorage: " + e.getLocalizedMessage())
        }
    }

    fun loadBitmapFromStorage(): Bitmap? {
        var bitmap: Bitmap? = null
        val fileInputStream: FileInputStream
        try {
            fileInputStream = openFileInput("last_file.jpeg")
            bitmap = BitmapFactory.decodeStream(fileInputStream)
            fileInputStream.close()
        } catch (e: IOException) {
            Log.e(TAG, "loadBitmapFromStorage: " + e.getLocalizedMessage())
        }
        return bitmap
    }

    fun showOCRResult(text: String?) {
        val bottomSheetResultsFragment = BottomSheetResultsFragment.newInstance(text)
        bottomSheetResultsFragment.show(
            getSupportFragmentManager(),
            "bottomSheetResultsFragment"
        )
    }

    private inner class ConvertImageToText(private var bitmap: Bitmap) : Runnable {
        override fun run() {
            // Pre-execute on UI thread
            handler!!.post(Runnable {
                mProgressIndicator!!.setProgress(0)
                mProgressIndicator!!.setVisibility(View.VISIBLE)
                animateImageViewAlpha(0.2f)
            })
            isRefresh = false
            saveBitmapToStorage(bitmap)
            GlobalScope.launch {
                val text = OCRTextRecognizer.getPicText(
                    findViewById<EditText>(R.id.editTextText).getText()
                        .toString(),
                    bitmap,
                    if(findViewById<Switch>(R.id.switch1).isChecked) 0 else 1
                ) as String?
                // Post-execution on UI thread
                handler!!.post(Runnable {
                    mProgressIndicator!!.setVisibility(View.GONE)
                    animateImageViewAlpha(1f)
                    showOCRResult(text)
                    updateImageView()
                })
            }
        }

        fun animateImageViewAlpha(alpha: Float) {
            mImageView!!.animate().alpha(alpha).setDuration(450).start()
        }

        fun updateImageView() {
            val bitmap = loadBitmapFromStorage()
            if (bitmap != null) {
                mImageView!!.setImageBitmap(bitmap)
            }
        }
    }

    companion object {
        const val TAG: String = "MainActivity"
        private const val REQUEST_CODE_SETTINGS = 797
        private var isRefresh = false
    }
}
