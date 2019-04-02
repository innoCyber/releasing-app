package ptml.releasing.app.ui

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity

class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


}
