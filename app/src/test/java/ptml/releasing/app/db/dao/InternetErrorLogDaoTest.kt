package ptml.releasing.app.db.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ptml.releasing.app.db.ReleasingDb
import ptml.releasing.data.InternetLogsFactory
import ptml.releasing.data.InternetLogsFactory.getFirstDate

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class InternetErrorLogDaoTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        ReleasingDb::class.java
    )
        .allowMainThreadQueries()
        .build()

    private val internetErrorLogDao = database.internetErrorDao()

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun getLogsReturnsData() {
        val testLog = InternetLogsFactory.makeLog()
        internetErrorLogDao.insert(testLog)

        val result = internetErrorLogDao.getLogsList()
        assertThat(result).isNotEmpty()
    }

    @Test
    fun getLogsReturnsDataWithStartDateFirst(){
        val testLogToday = InternetLogsFactory.makeLog()
        val testLogYesterday = InternetLogsFactory.makeLog(1)
        val testLogTwoDaysAgo = InternetLogsFactory.makeLog(2)
        internetErrorLogDao.insert(listOf(testLogToday, testLogYesterday, testLogTwoDaysAgo))

        val result = internetErrorLogDao.getLogsList()
        assertThat(result.size).isEqualTo(6)
        assertThat(result[0].date).isEqualTo(getFirstDate(date = testLogToday.date))
        assertThat(result[2].date).isEqualTo(getFirstDate(date = testLogYesterday.date))
        assertThat(result[4].date).isEqualTo(getFirstDate(date = testLogTwoDaysAgo.date))

    }

}