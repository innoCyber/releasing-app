package ptml.releasing.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import ptml.releasing.R
import ptml.releasing.ui.base.BaseActivity
import ptml.releasing.databinding.ErrorBinding

class DeviceConfigErrorActivity:BaseActivity(){
    lateinit var binding: ErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.error_)
    }

}
