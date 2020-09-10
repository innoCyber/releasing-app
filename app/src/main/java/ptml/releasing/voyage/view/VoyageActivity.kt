package ptml.releasing.voyage.view

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.data.domain.model.voyage.ReleasingVoyage
import ptml.releasing.app.exception.ErrorHandler
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.Status
import ptml.releasing.databinding.ActivityVoyageBinding
import ptml.releasing.voyage.viewmodel.VoyageViewModel
import timber.log.Timber

class VoyageActivity : BaseActivity<VoyageViewModel, ActivityVoyageBinding>() {

    private val listener = object : VoyageClickListener {
        override fun onItemClick(item: ReleasingVoyage?) {
            Timber.d("clicked: %s", item)
            notifyUser(
                binding.root,
                getString(R.string.quick_remark_click_message, item?.vesselName)
            )
        }
    }

    private val adapter = VoyageAdapter(listener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUpEnabled(true)
        initRecyclerView()
        initObservers()

        binding.includeError.btnReloadLayout.setOnClickListener {
            viewModel.downloadVoyages()
        }
        binding.fab.setOnClickListener {
            viewModel.downloadVoyages()
        }
    }

    private fun initObservers() {
        viewModel.getNetworkState().observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                if (NetworkState.LOADING == it) {
                    showLoading(
                        binding.includeProgress.root,
                        binding.includeProgress.tvMessage,
                        R.string.downloading_voyages
                    )
                    Timber.e("Loading...")
                } else {
                    hideLoading(binding.includeProgress.root)
                }

                //disable the fab when data is loading
                binding.fab.isEnabled = it != NetworkState.LOADING

                if (it.status == Status.FAILED) {
                    val error = ErrorHandler(this).getErrorMessage(it.throwable)
                    showLoading(binding.includeError.root, binding.includeError.tvMessage, error)
                } else {
                    hideLoading(binding.includeError.root)
                }
            }
        })

        viewModel.getResponse().observe(this, Observer {
            adapter.setItems(it)
        })
    }

    private fun initRecyclerView() {
        binding.recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        val decorator =
            DividerItemDecoration(binding.recyclerView.context, layoutManager.orientation)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addItemDecoration(decorator)
    }

    override fun getLayoutResourceId() = R.layout.activity_voyage
    override fun getBindingVariable() = BR.viewModel
    override fun getViewModelClass() = VoyageViewModel::class.java


}