package ptml.releasing.ui.base

import android.content.Intent
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseActivity : DaggerAppCompatActivity(){

     fun startNewActivity(name:Class<*>, finish:Boolean){
        val intent = Intent(this, name)
        startActivity(intent)
        if(finish){
            finish()
        }
    }
}
