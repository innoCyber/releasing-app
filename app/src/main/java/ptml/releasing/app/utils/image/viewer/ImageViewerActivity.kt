package ptml.releasing.app.utils.image.viewer

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import ptml.releasing.BR

import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.utils.image.ImageLoader
import ptml.releasing.app.views.ZoomOutPageTransformer
import ptml.releasing.databinding.ActivityImageViewerBinding
import ptml.releasing.images.model.Image
import javax.inject.Inject

class ImageViewerActivity : BaseActivity<ImageViewerViewModel, ActivityImageViewerBinding>(),
    ImageViewerFragment.ImageViewListener {

    @Inject
    lateinit var imageLoader: ImageLoader

    private var fullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initObservers()
        viewModel.init(getRootPathExtra(getDataFromIntent()))
    }

    private fun initObservers() {
        viewModel.getImageFilesState().observe(this, Observer {
            initViewPager(it)
        })

        viewModel.getLoadingState().observe(this, Observer {

        })
    }

    private fun initViewPager(it: List<Image>) {
        val adapter = ImageViewPager(supportFragmentManager, it)
        binding.viewPager.adapter = adapter
        binding.viewPager.setPageTransformer(false, ZoomOutPageTransformer())

    }

    private fun initViews() {
        showSystemUi(true)
        setActionBarTitle(null)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }
    }

    override fun onPhotoClick() {
        checkSystemUI()
    }

    private fun checkSystemUI() {
        fullScreen = !fullScreen
        if (fullScreen) {
            hideSystemUI(true)
        } else {
            showSystemUi(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_image_viewer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (super.onOptionsItemSelected(item)) {
            return true
        }
        return if (item.itemId == R.id.action_delete) {
            //TODO: Handle delete
            true
        } else false
    }

    override fun getLayoutResourceId() = R.layout.activity_image_viewer
    override fun getBindingVariable() = BR.viewModel
    override fun getViewModelClass() = ImageViewerViewModel::class.java

    internal inner class ImageViewPager(fm: FragmentManager, private val imageList: List<Image>) :
        FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            return ImageViewerFragment.newInstance(imageList[position], imageLoader)
        }

        override fun getCount(): Int {
            return imageList.size
        }
    }

    private fun hideSystemUI(toggleActionBarVisibility: Boolean) {
        if (toggleActionBarVisibility && supportActionBar != null) {
            supportActionBar!!.hide()
        }
        /*        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);////////MAGICAL LINE*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN

        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun showSystemUi(toggleActionBarVisibility: Boolean) {
        if (toggleActionBarVisibility && supportActionBar != null) {
            supportActionBar!!.show()
        }

        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);////////MAGICAL LINE*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val decorView = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
            )
        }


    }

    companion object {
        private const val POSITION_EXTRA = "POSITION_EXTRA"
        private const val ROOT_PATH_EXTRA = "ROOT_PATH_EXTRA"
        fun createExtras(rootPath: String, position: Int): Bundle {
            val data = Bundle()
            data.putString(ROOT_PATH_EXTRA, rootPath)
            data.putInt(POSITION_EXTRA, position)
            return data
        }

        fun getPositionExtra(data: Bundle?): Int {
            return data?.getInt(POSITION_EXTRA) ?: 0
        }

        fun getRootPathExtra(data: Bundle?): String {
            return data?.getString(ROOT_PATH_EXTRA) ?: ""
        }
    }
}
