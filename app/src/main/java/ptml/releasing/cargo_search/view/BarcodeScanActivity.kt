package ptml.releasing.cargo_search.view

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.FrameLayout
import dagger.android.support.DaggerAppCompatActivity
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import ptml.releasing.R
import ptml.releasing.app.utils.Constants

class BarcodeScanActivity : DaggerAppCompatActivity(), ZBarScannerView.ResultHandler {

    private var viewScanner: ZBarScannerView? = null
    private var mp: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_releasing_scan)
        setTitle(R.string.releasing_scan_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val frmScan = findViewById<FrameLayout>(R.id.ReleasingFrmScan)
        viewScanner = ZBarScannerView(this)
        frmScan.addView(viewScanner)

        mp = MediaPlayer.create(applicationContext, R.raw.sound)
    }

    public override fun onResume() {
        super.onResume()
        viewScanner?.setResultHandler(this)
        viewScanner?.startCamera()
    }

    public override fun onPause() {
        super.onPause()
        viewScanner?.stopCamera()
    }

    override fun handleResult(rawResult: Result) {
        // Play Sound
        mp?.start()
        val resultIntent = Intent()
        resultIntent.putExtra(Constants.BAR_CODE, rawResult.contents)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
