package ptml.releasing.images.upload


import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.RecyclerView
import ptml.releasing.R
import ptml.releasing.app.utils.beVisibleIf
import ptml.releasing.app.utils.image.ImageLoader
import ptml.releasing.databinding.ItemImageBinding
import ptml.releasing.images.model.Image
import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedHashSet

/**
Created by kryptkode on 8/5/2019
 */

class UploadImageAdapter(
    private val activity: UploadImagesActivity,
    private val imageLoader: ImageLoader,
    private val listener: UploadImageListener
) : RecyclerView.Adapter<UploadImageAdapter.UploadImagesViewHolder>() {

    private var positions: MutableList<Int>? = null
    private var removeMedia: MutableList<Image>? = null
    private var imagesMap = mutableMapOf<String?, Image>()

    protected var selectedKeys = LinkedHashSet<Int>()
    protected var positionOffset = 0

    private var actMode: ActionMode? = null
    private var actBarTextView: TextView? = null
    private var lastLongPressedItem = -1

    private val actionModeCallback = object : MyActionModeCallback() {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            handleActionItemClick(item)
            return true
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            isSelectable = true
            actMode = mode
            actBarTextView =
                activity.layoutInflater.inflate(R.layout.action_bar_title, null) as TextView
            actBarTextView?.layoutParams = ActionBar.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            actMode?.customView = actBarTextView
            actBarTextView?.setOnClickListener {
                if (itemCount == selectedKeys.size) {
                    finishActMode()
                } else {
                    selectAll()
                }
            }
            activity.menuInflater.inflate(R.menu.menu_image_upload, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {

            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            isSelectable = false
            (selectedKeys.clone() as HashSet<Int>).forEach {
                val position = getItemKeyPosition(it)
                if (position != -1) {
                    toggleItemSelection(false, position, false)
                }
            }
            updateTitle()
            selectedKeys.clear()
            actBarTextView?.text = ""
            actMode = null
            lastLongPressedItem = -1
        }
    }

    private fun getItemKeyPosition(key: Int): Int {
        return imagesMap.values.toList().indexOfFirst { it.imageUri?.hashCode() == key }
    }


    private fun handleActionItemClick(item: MenuItem?) {
        if (selectedKeys.isEmpty()) {
            return
        }

        if (item?.itemId == R.id.action_delete) {
            listener.showDeleteConfirm(selectedKeys.size > 1)
        }


    }

    fun restoreRecentlyDeletedItems() {
        removeMedia?.forEach {
            imagesMap.put(it.name, it)
        }

        positions?.forEach {
            notifyItemInserted(it)
        }
    }

    fun deleteSelectedFiles() {
        if (selectedKeys.isEmpty()) {
            return
        }
        removeMedia = ArrayList(selectedKeys.size)
        positions = getSelectedItemPositions()

        getSelectedItems().forEach {
            removeMedia?.add(it)
        }


        removeMedia?.forEach {
            imagesMap.remove(it.name)
        }
        listener.tryDeleteFiles(removeMedia ?: listOf())
        removeSelectedItems(positions ?: mutableListOf())
    }

    private fun getSelectedItems() = selectedKeys.mapNotNull { getItemWithKey(it) }

    private fun getItemWithKey(key: Int): Image? =
        imagesMap.values.toList().firstOrNull { it.imageUri?.hashCode() == key }

    fun setImageList(list: List<Image>) {
        imagesMap.clear()
        list.forEach {
            imagesMap.put(it.name, it)
        }
        notifyDataSetChanged()
    }

    fun add(file: Image) {
        imagesMap.put(file.name, file)
        notifyDataSetChanged()
    }

    fun remove(file: Image) {
        imagesMap.remove(file.name)
        notifyDataSetChanged()
    }


    private fun toggleItemSelection(select: Boolean, pos: Int, updateTitle: Boolean = true) {

        val itemKey = getItemSelectionKey(pos) ?: return
        if ((select && selectedKeys.contains(itemKey)) || (!select && !selectedKeys.contains(itemKey))) {
            return
        }

        if (select) {
            selectedKeys.add(itemKey)
        } else {
            selectedKeys.remove(itemKey)
        }

        notifyItemChanged(pos + positionOffset)

        if (updateTitle) {
            updateTitle()
        }

        if (selectedKeys.isEmpty()) {
            finishActMode()
        }
    }

    private fun getItemSelectionKey(pos: Int): Int? {
        return imagesMap.values.toList()[pos].imageUri?.hashCode()
    }

    private fun updateTitle() {
        val selectableItemCount = itemCount
        val selectedCount = Math.min(selectedKeys.size, selectableItemCount)
        val oldTitle = actBarTextView?.text
        val newTitle = "$selectedCount / $selectableItemCount"
        if (oldTitle != newTitle) {
            actBarTextView?.text = newTitle
            actMode?.invalidate()
        }
    }

    fun itemLongClicked(position: Int) {
        lastLongPressedItem = if (lastLongPressedItem == -1) {
            position
        } else {
            val min = Math.min(lastLongPressedItem, position)
            val max = Math.max(lastLongPressedItem, position)
            for (i in min..max) {
                toggleItemSelection(true, i, false)
            }
            updateTitle()
            position
        }
    }

    private fun getSelectedItemPositions(sortDescending: Boolean = true): ArrayList<Int> {
        val positions = ArrayList<Int>()
        val keys = selectedKeys.toList()
        keys.forEach {
            val position = getItemKeyPosition(it)
            if (position != -1) {
                positions.add(position)
            }
        }

        if (sortDescending) {
            positions.sortDescending()
        }
        return positions
    }

    private fun selectAll() {
        val cnt = itemCount - positionOffset
        for (i in 0 until cnt) {
            toggleItemSelection(true, i, false)
        }
        lastLongPressedItem = -1
        updateTitle()
    }

    private fun removeSelectedItems(positions: MutableList<Int>) {
        positions.forEach {
            notifyItemRemoved(it)
        }
        finishActMode()
    }

    fun finishActMode() {
        actMode?.finish()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadImagesViewHolder {
        return UploadImagesViewHolder(
            ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            imageLoader, listener
        )
    }

    override fun getItemCount(): Int {
        return imagesMap.size
    }

    override fun onBindViewHolder(holder: UploadImagesViewHolder, position: Int) {
        holder.performBind(imagesMap.values.toList()[position])
    }

    interface UploadImageListener {
        fun onItemClick(image: Image, position: Int)
        fun tryDeleteFiles(imageList: List<Image>)
        fun showDeleteConfirm(moreThanOne: Boolean)
    }

    inner class UploadImagesViewHolder(
        private val binding: ItemImageBinding,
        private val imageLoader: ImageLoader,
        private val listener: UploadImageListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun performBind(image: Image) {
            imageLoader.loadImage(image.imageUri, binding.image)

            val currentPosition = adapterPosition - positionOffset
            val isSelected = selectedKeys.contains(getItemSelectionKey(currentPosition))
            binding.mediumCheck.beVisibleIf(isSelected)

            binding.image.setOnClickListener {
                if (actionModeCallback.isSelectable) {

                    toggleItemSelection(!isSelected, currentPosition, true)
                } else {
                    listener.onItemClick(image, adapterPosition)
                }
                lastLongPressedItem = -1

            }

            binding.image.setOnLongClickListener {

                if (!actionModeCallback.isSelectable) {
                    activity.startSupportActionMode(actionModeCallback)
                }

                toggleItemSelection(true, currentPosition, true)
                itemLongClicked(currentPosition)
                true
            }
        }
    }
}