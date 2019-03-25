package ptml.releasing.app.di.modules.viewmodel

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ptml.releasing.app.di.mapkeys.ViewModelKey
import ptml.releasing.admin_configuration.viewmodel.AdminConfigViewModel
import ptml.releasing.auth.viewmodel.LoginViewModel
import ptml.releasing.device_configuration.viewmodel.DeviceConfigViewModel
import ptml.releasing.home.viewmodel.HomeViewModel

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
}