package ptml.releasing.damages.view

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import permissions.dispatcher.*
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.app.exception.ErrorHandler
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.Status
import ptml.releasing.cargo_info.model.LARGE
import ptml.releasing.cargo_info.model.SMALL
import ptml.releasing.damages.model.ReleasingAssignedDamage
import ptml.releasing.damages.view_model.SelectDamageViewModel
import ptml.releasing.databinding.ActivityReleasingSelectDamagesBinding
import ptml.releasing.download_damages.model.Damage
import timber.log.Timber

@RuntimePermissions
class ReleasingDamagesSelectDamageActivity :
    BaseActivity<SelectDamageViewModel, ActivityReleasingSelectDamagesBinding>() {
    internal lateinit var adapter: CargoDamagesAdapter


    internal inner class CargoDamagesAdapter(var context: Context) : BaseAdapter() {
        var damages = mutableListOf<Damage>()

        fun setData(damagesList: List<Damage>) {
            damages.clear()
            damages.addAll(damagesList)
            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return damages.size
        }

        override fun getItem(position: Int): Any {
            return damages[position]
        }

        override fun getItemId(position: Int): Long {
            return damages[position].id?.toLong() ?: 0
        }

        @Suppress("NAME_SHADOWING")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView

            if (convertView == null) {
                convertView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.cell_cargo_select_damages, parent, false)
            }

            val txtDamageName =
                convertView?.findViewById<View>(R.id.CellCargoDamagesTxtDamage) as TextView
            val btnHigh = convertView.findViewById<View>(R.id.CellCargoDamagesBtnHigh) as Button
            val btnLow = convertView.findViewById<View>(R.id.CellCargoDamagesBtnLow) as Button

            txtDamageName.text = damages[position].description

            btnHigh.setOnClickListener {
                val d = damages[position]
                Timber.d("Damage: %s", d)
                DamagesActivity.currentDamages.add(
                    ReleasingAssignedDamage(
                        d.id ?: 0,
                        d.description,
                        "",
                        1,
                        d.typeContainer,
                        d.position,
                        DamagesActivity.currentDamageZone + DamagesActivity.currentDamagePoint,
                        LARGE
                    )
                )
                finish()
            }

            btnLow.setOnClickListener {
                val d = damages[position]
                DamagesActivity.currentDamages.add(
                    ReleasingAssignedDamage(
                        d.id ?: 0,
                        d.description,
                        "",
                        1,
                        d.typeContainer,
                        d.position,
                        DamagesActivity.currentDamageZone + DamagesActivity.currentDamagePoint,
                        SMALL
                    )
                )
                finish()
            }

            txtDamageName.setOnClickListener {
                AlertDialog.Builder(context)
                    .setMessage(damages[position].description)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        // continue with delete
                    }
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show()
            }


            return convertView
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.releasing_damages_select_damage_title)
        showUpEnabled(true)
        adapter = CargoDamagesAdapter(this)
        binding.ReleasingDamagesLstDamages.adapter = adapter
        binding.includeEmpty.tvMessage.text = getString(R.string.empty_damages_error_message)
        binding.ReleasingDamagesLstDamages.emptyView = binding.includeEmpty.root

        setupListeners()

        viewModel.damagesList.observe(this, Observer {
            adapter.setData(it)
        })


        viewModel.networkState.observe(this, Observer {event->
            event.getContentIfNotHandled()?.let {
                if (it == NetworkState.LOADING) {
                    showLoading(
                        binding.includeProgress.root,
                        binding.includeProgress.tvMessage,
                        R.string.loading
                    )
                } else {
                    hideLoading(binding.includeProgress.root)
                }

                if (it.status == Status.FAILED) {
                    val error = ErrorHandler(this).getErrorMessage(it.throwable)
                    showLoading(binding.includeError.root, binding.includeError.tvMessage, error)
                } else {
                    hideLoading(binding.includeError.root)
                }
            }

        })


        getDataWithPermissionCheck()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)


        val searchMenuItem = menu?.findItem(R.id.action_search)

        val searchView = searchMenuItem?.actionView as SearchView

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = getString(R.string.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.filter(newText)
                return true
            }
        })


        return true
    }

    private fun setupListeners() {

        binding.btnBack.setOnClickListener {
            setResult(-1)
            finish()
        }

        binding.includeEmpty.btnReload.setOnClickListener {
            setResult(-1)
            finish()
        }
    }


    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun getData() {
        viewModel.getDamages(
            imei ?: "",
            DamagesActivity.typeContainer
        )
    }

    @OnShowRationale(android.Manifest.permission.READ_PHONE_STATE)
    fun showPhoneStateRationale(request: PermissionRequest) {
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

    @OnPermissionDenied(android.Manifest.permission.READ_PHONE_STATE)
    fun showDeniedForPhoneState() {
        notifyUser(binding.root, getString(R.string.phone_state_permission_denied))
    }

    @OnNeverAskAgain(android.Manifest.permission.READ_PHONE_STATE)
    fun neverAskForPhoneState() {
        notifyUser(binding.root, getString(R.string.phone_state_permission_never_ask))
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }


    override fun getLayoutResourceId() = R.layout.activity_releasing_select_damages

    override fun getBindingVariable() = BR.viewModel

    override fun getViewModelClass() = SelectDamageViewModel::class.java
}
