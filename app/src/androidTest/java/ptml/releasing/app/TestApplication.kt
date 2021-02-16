package ptml.releasing.app

import ptml.releasing.app.di.component.DaggerTestComponent
import ptml.releasing.app.di.components.AppComponent

/**
Created by kryptkode on 8/6/2019
 */

class TestApplication : ReleasingApplication(){

    override val appComponent: AppComponent
        get() = DaggerTestComponent.builder()
            .bindApplication(this)
            .build()
}