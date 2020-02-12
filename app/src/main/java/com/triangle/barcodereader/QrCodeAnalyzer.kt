package com.triangle.barcodereader

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

class QrCodeAnalyzer(
    private val onQrCodesDetected : (qrCodes : List<FirebaseVisionBarcode>) -> Unit
) : ImageAnalysis.Analyzer {

    private val isBusy = AtomicBoolean(false)

    override fun analyze(image: ImageProxy?, rotationDegrees: Int) {
        if (image!!.image != null && isBusy.compareAndSet(false, true)) {
                val options = FirebaseVisionBarcodeDetectorOptions.Builder()
                    .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                    .build()
                val detecter = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
                val rotation = rotationDegreesToFirebaseRotation(rotationDegrees)
                val visionImage = FirebaseVisionImage.fromMediaImage(image!!.image!!, rotation)
                detecter.detectInImage(visionImage)
                    .addOnSuccessListener {
                        isBusy.set(false)
                        onQrCodesDetected(it)
                    }
                    .addOnFailureListener {
                        isBusy.set(false)
                        Log.e("QRCodeAnalyzer", "Something went wrong", it)
                    }
            }

    }

    private fun rotationDegreesToFirebaseRotation(rotationDegrees: Int) : Int{
        return when(rotationDegrees){
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> throw IllegalArgumentException("Not Supported")
        }
    }
}