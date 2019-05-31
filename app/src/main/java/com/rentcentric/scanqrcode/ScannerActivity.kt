package com.rentcentric.scanqrcode

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class ScannerActivity : AppCompatActivity(), SurfaceHolder.Callback, Detector.Processor<Barcode> {

    private lateinit var svScanner: SurfaceView
    private lateinit var shScanner: SurfaceHolder
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource

    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        tvResult = findViewById(R.id.Scanner_tvResult)

        svScanner = findViewById(R.id.Scanner_sv)
        svScanner.setZOrderMediaOverlay(true)

        shScanner = svScanner.holder

        barcodeDetector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build()

        cameraSource =
            CameraSource.Builder(this, barcodeDetector).setFacing(CameraSource.CAMERA_FACING_BACK).setRequestedFps(24f)
                .setAutoFocusEnabled(true).setRequestedPreviewSize(1920, 1024).build()

        svScanner.holder.addCallback(this)

        barcodeDetector.setProcessor(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            cameraSource.start(svScanner.holder)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            cameraSource.start(svScanner.holder)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun release() {
    }

    override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
        if (p0!!.detectedItems.size() > 0) {
            val barcode: Barcode = p0.detectedItems.valueAt(0)
            tvResult.text = barcode.displayValue
            finish()
        } else {
            Toast.makeText(this, "The QR Code isn't clear", Toast.LENGTH_LONG).show()
        }
    }
}