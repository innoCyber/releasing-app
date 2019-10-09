package ptml.releasing.data

import ptml.releasing.app.db.converter.DateConverter
import ptml.releasing.app.db.model.InternetErrorLogModel
import ptml.releasing.data.DataFactory.randomInt
import ptml.releasing.data.DataFactory.randomString
import java.util.*

object InternetLogsFactory {

    fun makeLog(daysBeforeNow:Int = 0): InternetErrorLogModel {
        return InternetErrorLogModel(
            randomInt(), randomString(), randomString(), randomString(), createDate(daysBeforeNow)
        )
    }

    fun makeLogs(days:Int): MutableList<InternetErrorLogModel> {
        val logsList = mutableListOf<InternetErrorLogModel>()
        repeat(days){
            val log= makeLog(it)
            logsList.add(log)
        }
        return logsList
    }

    fun createDate(daysBeforeNow:Int = 0, date: Date=Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, -daysBeforeNow)
        return calendar.time
    }

    fun getFirstDate(date: Date=Date()): Date {
        val dateString = DateConverter.toText(date)
        val dateSubStr = "${dateString.substring(0, 9)}00000"
        return DateConverter.toDate(dateSubStr)
    }
}