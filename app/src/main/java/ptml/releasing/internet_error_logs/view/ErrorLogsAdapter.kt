package ptml.releasing.internet_error_logs.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import ptml.releasing.app.db.model.InternetErrorLogModel.Companion.EMPTY_ID
import ptml.releasing.app.utils.DateTimeUtils
import ptml.releasing.databinding.ItemErrorLogBinding
import ptml.releasing.databinding.ItemErrorLogHeaderBinding
import ptml.releasing.internet_error_logs.model.ErrorLog
import javax.inject.Inject
import android.text.style.URLSpan
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod


class ErrorLogsAdapter @Inject constructor(private val dateTimeUtils: DateTimeUtils) :
    PagedListAdapter<ErrorLog, RecyclerView.ViewHolder>(ErrorLog.diffUtil) {
    companion object {
        const val HEADER = 112
        const val ITEM = 113
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.id ?: EMPTY_ID == EMPTY_ID) HEADER else ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            bindToViewHolder(item, holder, position)
        }
        //TODO: Handle placeholders
    }

    private fun bindToViewHolder(item: ErrorLog, holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            HEADER -> (holder as HeaderViewHolder).performBind(item)
            ITEM -> (holder as ItemViewHolder).performBind(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER -> HeaderViewHolder(ItemErrorLogHeaderBinding.inflate(inflater, parent, false))
            else -> ItemViewHolder(ItemErrorLogBinding.inflate(inflater, parent, false))
        }
    }

    inner class HeaderViewHolder(private val binding: ItemErrorLogHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun performBind(item: ErrorLog) {
            binding.dateTextView.text = dateTimeUtils.toTimeStampRelative(item.date)
        }
    }

    inner class ItemViewHolder(private val binding: ItemErrorLogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun performBind(item: ErrorLog) {
            binding.dateTextView.text = dateTimeUtils.formatDate(item.date)
            binding.descriptionTextView.text = item.description
            binding.errorTextView.text = item.error
            applyUrlSpan(item.url)
        }

        private fun applyUrlSpan(url:String){
            val urlSpan = URLSpan(url)
            val ssBuilder = SpannableStringBuilder(url)
            ssBuilder.setSpan(
                urlSpan,
                0,
                url.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.urlTextView.text = ssBuilder
            binding.urlTextView.movementMethod = LinkMovementMethod.getInstance()
        }
    }


}