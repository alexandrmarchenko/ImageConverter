package com.example.imageconverter.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.imageconverter.R
import com.example.imageconverter.mvp.model.imageApi.Converter.Converter
import com.example.imageconverter.mvp.model.imageApi.Loader.ImageLoader
import com.example.imageconverter.mvp.presenter.Presenter
import com.example.imageconverter.mvp.view.ImageView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.io.File


class MainActivity : MvpAppCompatActivity(), ImageView {

    private var convertDialog: Dialog? = null

    private val CHOOSE_IMAGE_CODE = 0
    private val READ_PERMISSION_REQUEST_CODE = 10
    private val WRITE_PERMISSION_REQUEST_CODE = 20

    private val READ_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    private val WRITE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE

    @InjectPresenter
    lateinit var mPresenter: Presenter

    @ProvidePresenter
    fun providePresenter(): Presenter {
        return Presenter(
            AndroidSchedulers.mainThread(),
            Schedulers.computation(),
            Converter(
                this
            ),
            ImageLoader(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initListeners()
    }

    private fun initListeners() {
        chooseImage.setOnClickListener(mChooseImageClickListener)
        convertImage.setOnClickListener(mConvertImageClickListener)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CHOOSE_IMAGE_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = data?.dataString

                    uri?.let {
                        mPresenter.loadImage(uri)

                        val trg = Uri.fromFile(
                            File(
                                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                File.separator + "result.png"
                            )
                        ).toString()

                        mPresenter.setTarget(trg)
                    }
                }
            }
        }
    }

    private val mChooseImageClickListener =
        View.OnClickListener {
            if (checkPermission(READ_PERMISSION)) {
                chooseImage()
            } else {
                requestExternalStoragePermissions(READ_PERMISSION_REQUEST_CODE, READ_PERMISSION)
            }
        }

    private val mConvertImageClickListener =
        View.OnClickListener {
            if (checkPermission(WRITE_PERMISSION)) {
                convertImage()
            } else {
                requestExternalStoragePermissions(WRITE_PERMISSION_REQUEST_CODE, WRITE_PERMISSION)
            }
        }

    private fun showPermissionAlertDialog(permission: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)
            .setTitle(getString(R.string.warning))
            .setMessage(getString(R.string.need_permission))
            .setPositiveButton(getString(R.string.request_permission)) { dialogInterface, i ->
                requestExternalStoragePermissions(
                    requestCode,
                    permission
                )
            }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    chooseImage()
                } else {
                    showPermissionAlertDialog(READ_PERMISSION, READ_PERMISSION_REQUEST_CODE)
                }
            }
            WRITE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    convertImage()
                } else {
                    showPermissionAlertDialog(WRITE_PERMISSION, WRITE_PERMISSION_REQUEST_CODE)
                }
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestExternalStoragePermissions(requestCode: Int, vararg permissions: String) {
        ActivityCompat.requestPermissions(
            this,
            permissions,
            requestCode
        )
    }

    override fun chooseImage() {

        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            .apply {
                type = "image/*"
            }
        startActivityForResult(pickPhoto, CHOOSE_IMAGE_CODE)
    }

    override fun convertImage() {
        mPresenter.convert()
    }

    override fun showConvertDialog() {
        convertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.converting_title))
            .setMessage(getString(R.string.converting_msg))
            .setNegativeButton(getString(R.string.cancel)) { dialogInterface, i ->
                mPresenter.stopConverting()
            }
            .create()

        convertDialog?.show()
    }

    override fun dismissConvertDialog() {
        convertDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    override fun showConvertFail() {
        showAlertDialog(getString(R.string.Conversion), getString(R.string.conversion_failed))
    }

    override fun showConvertSuccess() {
        showAlertDialog(getString(R.string.Conversion), getString(R.string.conversion_succeed))
    }

    override fun setImage(bitmap: Bitmap) {
        imageView.setImageBitmap(bitmap)
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .create()
            .show()
    }


}