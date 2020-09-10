package ptml.releasing.images.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import ptml.releasing.app.TestApplication
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.data.Repository
import ptml.releasing.app.di.component.DaggerTestComponent
import ptml.releasing.app.di.component.TestComponent
import ptml.releasing.utils.notUploadedImages
import ptml.releasing.utils.uploadedImages


/**
 * Created by kryptkode on 8/6/2019
 */

@Suppress("EXPERIMENTAL_API_USAGE")
class ImageUploadWorkerTest {

    private lateinit var worker: ImageUploadWorker
    private lateinit var component: TestComponent
    private lateinit var context: Context
    private lateinit var repository: Repository
    private val testCargoCode = "1234"
    private val testCargoId = 22
    private val testCargoType = 3

    @Before
    fun init() {
        context = ApplicationProvider.getApplicationContext()
        component = DaggerTestComponent.builder()
            .bindApplication(context as TestApplication)
            .build()

        val inputData = Data.Builder()
            .putString(ImageUploadWorker.CARGO_CODE_KEY, testCargoCode)
            .putInt(ImageUploadWorker.CARGO_ID_KEY, testCargoId)
            .putInt(ImageUploadWorker.CARGO_TYPE_KEY, testCargoType)
            .build()

        worker = TestListenableWorkerBuilder<ImageUploadWorker>(context)
            .setWorkerFactory(component.factory())
            .setInputData(inputData)
            .build() as ImageUploadWorker

        repository = worker.repository
    }

    @Test
    fun uploadImagesSuccessfully() {

        coEvery {
            repository.getImages(any())
        } returns uploadedImages

        coEvery {
            repository.uploadImage(testCargoType, testCargoCode, testCargoId, any(), any())
        } returns BaseResponse("", true)

        coEvery {
            repository.addImage(any(), any())
        } returns Unit

        runBlocking {
           val result =  worker.doWork()
            assertThat(result, `is`(ListenableWorker.Result.success()))
        }
    }

    @Test
    fun uploadImagesIsRetriedWhenInitialUploadFails(){
        val testImages = notUploadedImages

        val keys = testImages.keys.toList()
        //make index 2 not uploaded and make the uri a file uri
        testImages[keys[2]]!!.uploaded = false
        testImages[keys[2]]!!.imageUri = "file://whatever/image.jpg"

        coEvery {
            repository.getImages(any())
        } returns testImages

        coEvery {
            repository.uploadImage(testCargoType, testCargoCode, testCargoId, any(), any())
        } returns BaseResponse("", false)

        coEvery {
            repository.addImage(any(), any())
        } returns Unit

        runBlocking {
            val result =  worker.doWork()
            assertThat(result, `is`(ListenableWorker.Result.retry()))
        }

    }
}