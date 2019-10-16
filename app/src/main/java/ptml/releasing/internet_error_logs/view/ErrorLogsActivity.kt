package ptml.releasing.internet_error_logs.view

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.views.ItemDivider
import ptml.releasing.databinding.ActivityErrorLogsBinding
import ptml.releasing.internet_error_logs.view_model.ErrorLogsViewModel
import javax.inject.Inject

class ErrorLogsActivity : BaseActivity<ErrorLogsViewModel, ActivityErrorLogsBinding>() {

    @Inject
    lateinit var adapter: ErrorLogsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUpEnabled(true)
        initErrorDrawable(binding.includeEmpty.imgError)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(ItemDivider(this@ErrorLogsActivity))
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setEmptyView(binding.includeEmpty.root)
        binding.includeEmpty.tvMessage.text = getString(R.string.no_error_logs)

        viewModel.errorLogs.observe(this, Observer {
            adapter.submitList(it)
        })

        binding.includeEmpty.btnReloadLayout.setOnClickListener {
            onBackPressed()
        }
    }

    override fun getLayoutResourceId() = R.layout.activity_error_logs

    override fun getBindingVariable() = BR.viewModel

    override fun getViewModelClass() = ErrorLogsViewModel::class.java
}