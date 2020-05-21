package ptml.releasing.internet_error_logs.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import permissions.dispatcher.*
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.app.utils.Status
import ptml.releasing.app.views.ItemDivider
import ptml.releasing.databinding.ActivityErrorLogsBinding
import ptml.releasing.internet_error_logs.view_model.ErrorLogsViewModel
import java.io.File
import javax.inject.Inject

@RuntimePermissions
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

        viewModel.getExportLoadingState().observe(this, Observer {
            when (it.status) {
                Status.RUNNING -> {
                    notifyUser(getString(R.string.logs_exporting_msg))
                }

                Status.SUCCESS -> {

                }

                Status.FAILED -> {
                    notifyUser(getString(R.string.logs_exported_error_msg, it.msg))
                }
            }
        })

        viewModel.getCheckStoragePermission().observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                exportLogsWithPermissionCheck()
            }
        })

        viewModel.getShareExportedFile().observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                notifyUser(getString(R.string.logs_exported_success_msg, it.absolutePath))
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/csv"
                intent.putExtra(Intent.EXTRA_STREAM, getFileUri(it))
                startActivity(
                    Intent.createChooser(
                        intent,
                        resources.getString(R.string.share_with)
                    )
                )
            }
        })

        binding.includeEmpty.btnReloadLayout.setOnClickListener {
            onBackPressed()
        }
    }

    @NeedsPermission(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun exportLogs() {
        viewModel.exportLogsToCSV()
    }

    @OnShowRationale(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun showPermissionsRationale(request: PermissionRequest) {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.allow_permission),
            message = getString(R.string.allow_phone_state_permission_msg),
            buttonText = getString(android.R.string.ok),
            listener = object : InfoDialog.InfoListener {
                override fun onConfirm() {
                    request.proceed()
                }
            })
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    @OnPermissionDenied(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun showDeniedForPermissions() {
        notifyUser(binding.root, getString(R.string.phone_state_permission_denied))
    }

    @OnNeverAskAgain(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun neverAskForPermissions() {
        notifyUser(binding.root, getString(R.string.phone_state_permission_never_ask))
    }

    private fun getFileUri(file: File): Uri {
        return FileProvider.getUriForFile(this, "${packageName}.provider", file)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun getLayoutResourceId() = R.layout.activity_error_logs

    override fun getBindingVariable() = BR.viewModel

    override fun getViewModelClass() = ErrorLogsViewModel::class.java
}