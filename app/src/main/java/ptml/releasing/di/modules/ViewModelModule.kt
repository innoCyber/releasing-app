package ptml.releasing.di.modules

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ptml.releasing.di.mapkeys.ViewModelKey
import ptml.releasing.ui.setup.SetupActivityViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SetupActivityViewModel::class)
    abstract fun bindSetupViewModel(myViewModel: SetupActivityViewModel): ViewModel
}