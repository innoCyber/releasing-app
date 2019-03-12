package ptml.releasing.di.modules.ui.fragments

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ptml.releasing.ui.login.LoginFragment

@Module
abstract class LoginModule {

    @ContributesAndroidInjector
    abstract fun provideLoginFragment(): LoginFragment
}