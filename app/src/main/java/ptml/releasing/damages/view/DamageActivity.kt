package ptml.releasing.damages.view

import android.os.Bundle
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.damages.model.Damage
import ptml.releasing.damages.viewmodel.DamageViewModel
import ptml.releasing.databinding.ActivityDamageBinding

class DamageActivity : BaseActivity<DamageViewModel, ActivityDamageBinding>() {

    private val listener = object: DamageListener{
        override fun onItemClick(item: Damage?) {

        }
    }
    private  val adapter = DamageAdapter(listener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.recyclerView.adapter = adapter

    }

    override fun getLayoutResourceId() = R.layout.activity_damage
    override fun getBindingVariable() = BR.viewModel
    override fun getViewModelClass() = DamageViewModel::class.java


}