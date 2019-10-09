package ptml.releasing.internet_error_logs.model

import androidx.recyclerview.widget.DiffUtil
import java.util.*

data class ErrorLog(
    val id: Int,
    val description: String,
    val url: String,
    val error:String,
    val date: Date
){
    constructor(description: String,
                url: String,
                error: String,
                date: Date): this(0, description, url, error, date)

    companion object{
        val diffUtil = object: DiffUtil.ItemCallback<ErrorLog>(){
            override fun areContentsTheSame(oldItem: ErrorLog, newItem: ErrorLog): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: ErrorLog, newItem: ErrorLog): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}