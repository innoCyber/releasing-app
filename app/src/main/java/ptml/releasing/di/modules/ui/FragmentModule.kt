package ptml.releasing.di.modules.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ptml.releasing.ui.configuration.ConfigurationFragment
import ptml.releasing.ui.login.LoginFragment

@Module
abstract class LoginModule {

    @ContributesAndroidInjector
    abstract fun provideLoginFragment(): LoginFragment
}


@Module
abstract class ConfigurationModule {
    @ContributesAndroidInjector
    abstract fun provideFragment(): ConfigurationFragment
}