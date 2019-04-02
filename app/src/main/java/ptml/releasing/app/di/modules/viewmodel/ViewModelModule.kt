package ptml.releasing.app.di.modules.viewmodel

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ptml.releasing.app.di.mapkeys.ViewModelKey
import ptml.releasing.admin_configuration.viewmodel.AdminConfigViewModel
import ptml.releasing.auth.viewmodel.LoginViewModel
import ptml.releasing.damages.viewmodel.DamageViewModel
import ptml.releasing.device_configuration.viewmodel.DeviceConfigViewModel
import ptml.releasing.home.viewmodel.HomeViewModel
import ptml.releasing.search.viewmodel.SearchViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(DeviceConfigViewModel::class)
    abstract fun bindSetupViewModel(myViewModel: DeviceConfigViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(myViewModel: LoginViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(AdminConfigViewModel::class)
    abstract fun bindConfigViewModel(myViewModel: AdminConfigViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(myViewModel: HomeViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(DamageViewModel::class)
    abstract fun bindDamageViewModel(myViewModel: DamageViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(myViewModel: SearchViewModel): ViewModel
}