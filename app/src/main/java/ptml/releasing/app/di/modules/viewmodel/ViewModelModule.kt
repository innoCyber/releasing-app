package ptml.releasing.app.di.modules.viewmodel

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ptml.releasing.admin_config.viewmodel.AdminConfigViewModel
import ptml.releasing.app.di.mapkeys.ViewModelKey
import ptml.releasing.configuration.viewmodel.ConfigViewModel
import ptml.releasing.login.viewmodel.LoginViewModel
import ptml.releasing.download_damages.viewmodel.DamageViewModel
import ptml.releasing.device_configuration.viewmodel.DeviceConfigViewModel
import ptml.releasing.cargo_info.view_model.CargoInfoViewModel
import ptml.releasing.cargo_search.viewmodel.SearchViewModel
import ptml.releasing.damages.view_model.DummyViewModel
import ptml.releasing.damages.view_model.SelectDamageViewModel
import ptml.releasing.images.ImagesViewModel
import ptml.releasing.printer.viewmodel.PrinterSettingsViewModel
import ptml.releasing.quick_remarks.viewmodel.QuickRemarkViewModel

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
    @ViewModelKey(ConfigViewModel::class)
    abstract fun bindConfigViewModel(myViewModel: ConfigViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(DamageViewModel::class)
    abstract fun bindDamageViewModel(myViewModel: DamageViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(myViewModel: SearchViewModel): ViewModel



    @Binds
    @IntoMap
    @ViewModelKey(AdminConfigViewModel::class)
    abstract fun bindAdminConfigViewModel(myViewModel: AdminConfigViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(CargoInfoViewModel::class)
    abstract fun bindFindCargoViewModel(viewModel: CargoInfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SelectDamageViewModel::class)
    abstract fun bindSelectDamageViewModel(viewModel: SelectDamageViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(PrinterSettingsViewModel::class)
    abstract fun bindPrinterSettingsViewModel(viewModel: PrinterSettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DummyViewModel::class)
    abstract fun bindDummyViewModel(viewModel: DummyViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(QuickRemarkViewModel::class)
    abstract fun bindQuickRemarkViewModel(viewModel: QuickRemarkViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(ImagesViewModel::class)
    abstract fun bindUploadImagesViewModel(viewModel: ImagesViewModel): ViewModel



}