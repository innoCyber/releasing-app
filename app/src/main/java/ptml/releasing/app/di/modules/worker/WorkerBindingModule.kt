package ptml.releasing.app.di.modules.worker



import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ptml.releasing.images.worker.ImageUploadWorker
import ptml.releasing.save_time_worker.CheckLoginWorker

@Module
interface WorkerBindingModule {

    @Binds
    @IntoMap
    @WorkerKey(CheckLoginWorker::class)
    fun bindCheckLoginWorker(factory: CheckLoginWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(ImageUploadWorker::class)
    fun bindImageUploadWorker(factory: ImageUploadWorker.Factory): ChildWorkerFactory

}
