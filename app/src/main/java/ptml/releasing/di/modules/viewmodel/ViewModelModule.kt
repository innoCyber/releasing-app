package ptml.releasing.di.modules.viewmodel

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ptml.releasing.di.mapkeys.ViewModelKey
import ptml.releasing.ui.configuration.ConfigurationViewModel
import ptml.releasing.ui.login.LoginViewModel
import ptml.releasing.ui.setup.SetupActivityViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SetupActivityViewModel::class)
    abstract fun bindSetupViewModel(myViewModel: SetupActivityViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(myViewModel: LoginViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(ConfigurationViewModel::class)
    abstract fun bindConfigViewModel(myViewModel: ConfigurationViewModel): ViewModel
}