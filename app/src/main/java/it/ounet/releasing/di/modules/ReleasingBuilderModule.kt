package it.ounet.releasing.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.ounet.releasing.MainActivity
import it.ounet.releasing.di.modules.ui.MainModule


@Module
abstract class ReleasingBuilderModule {


    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun bindMainActivity(): MainActivity




}