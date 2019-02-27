package ptml.releasing.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ptml.releasing.MainActivity
import ptml.releasing.di.modules.ui.MainModule


@Module
abstract class ReleasingBuilderModule {


    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun bindMainActivity(): MainActivity




}