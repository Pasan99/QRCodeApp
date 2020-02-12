package com.triangle.barcodereader

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.triangle.barcodereader.utils.ImageSaver
import kotlinx.android.synthetic.main.activity_create_qr_code_activty.*


private const val REQUEST_CODE_PERMISSIONS = 12
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

class CreateQrCodeActivity : AppCompatActivity() {

    // This is an array of all the permission specified in the manifest.

    private var mImageBitmap : Bitmap? = null
    private var name : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_qr_code_activty)

        generate_qr_btn.setOnClickListener {
            if (generate_data_text2.text.toString().isNotEmpty()){
                generateQrCode(generate_data_text2.text.toString())
            }
        }
        generate_save_btn.setOnClickListener {
            if (allPermissionsGranted()) {
                selectName()
            }
            else{
                ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }
    }

    private fun generateQrCode(stringData : String) {
        val multiFormatWritter = MultiFormatWriter()
        try{
            val bitMatrix = multiFormatWritter.encode(
                stringData,
                BarcodeFormat.QR_CODE, 200, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            mImageBitmap = bitmap
            generated_image.setImageBitmap(bitmap)
            name = stringData
            generated_image.visibility = View.VISIBLE
            generate_save_btn.visibility = View.VISIBLE
        }catch (i : Exception){
            Toast.makeText(this, i.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun selectName(){
        val d = MaterialDialog(this).show {
            title(R.string.file_name_select)
            input()
            positiveButton(R.string.save)
            negativeButton(R.string.cancel)
        }
        d.show()
        d.getInputField().setText(name)
        d.negativeButton {
            d.dismiss()
        }
        d.positiveButton {
            saveImage(d.getInputField().text.toString())
        }
    }

    private fun saveImage(fileName : String){
        if (mImageBitmap != null && name != null){
            val saver = ImageSaver(this)
            saver.setFileName("$fileName.png")
            saver.setDirectoryName("QR_CODES")
            if (!saver.getAvailableFileNames().contains("$fileName.png")) {
                if (saver.save(mImageBitmap!!)) {
                    Snackbar.make(
                        generate_save_btn, "QR Code Saved Successfully.",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    Snackbar.make(
                        generate_save_btn, "Error Occurred",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
            else{
                val d = MaterialDialog(this).show {
                    title(R.string.file_exist)
                    input()
                    positiveButton(R.string.save)
                    negativeButton(R.string.cancel)
                    icon(android.R.drawable.stat_notify_error)
                }
                d.show()
                d.getInputField().setText(name)
                d.negativeButton {
                    d.dismiss()
                }
                d.positiveButton {
                    saveImage(d.getInputField().text.toString())
                }
                Snackbar.make(
                    generate_save_btn, "File Already Exist",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                selectName()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
}
