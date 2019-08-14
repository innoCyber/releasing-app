package ptml.releasing.app.di.modules.worker



import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ptml.releasing.images.worker.ImageUploadWorker

@Module
interface WorkerBindingModule {

    @Binds
    @IntoMap
    @WorkerKey(ImageUploadWorker::class)
    fun bindImageUploadWorker(factory: ImageUploadWorker.Factory): ChildWorkerFactory


}

