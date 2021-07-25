package viktor.khlebnikov.geekgrains.android1.universearoundus.ui.recycler

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import kotlinx.android.synthetic.main.activity_recycler_item_nasa.view.*
import kotlinx.android.synthetic.main.activity_recycler_item_note.view.*
import viktor.khlebnikov.geekgrains.android1.universearoundus.R

class RecyclerActivityAdapter(
    private var onListItemClickListener: OnListItemClickListener,
    private var data: MutableList<Pair<Data, Boolean>>,
    private val dragListener: OnStartDragListener
) :
    RecyclerView.Adapter<BaseViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_NASA) {
            NasaViewHolder(
                inflater.inflate(R.layout.activity_recycler_item_nasa, parent, false) as View
            )
        } else {
            NoteViewHolder(
                inflater.inflate(R.layout.activity_recycler_item_note, parent, false) as View
            )
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads)
        else {
            val combinedChange =
                createCombinedPayload(payloads as List<Change<Pair<Data, Boolean>>>)
            val oldData = combinedChange.oldData
            val newData = combinedChange.newData

            if (newData.first.someTitle != oldData.first.someTitle) {
                holder.itemView.title.text = newData.first.someTitle
            } else holder.itemView.nasaTitle.text = newData.first.someTitle
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position].first.someDate.isNullOrBlank() || data[position].first.someDate == "Date") TYPE_NOTE else TYPE_NASA
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        data.removeAt(fromPosition).apply {
            data.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, this)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    fun setItems(newItems: List<Pair<Data, Boolean>>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(data, newItems))
        result.dispatchUpdatesTo(this)
        data.clear()
        data.addAll(newItems)
    }

    fun appendItem() {
        data.add(addNote())
        notifyItemInserted(itemCount - 1)
    }

    private fun addNote() = Pair(Data(0, "Note", "description"), false)

    inner class DiffUtilCallback(
        private var oldItems: List<Pair<Data, Boolean>>,
        private var newItems: List<Pair<Data, Boolean>>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].first.id == newItems[newItemPosition].first.id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].first.someTitle == newItems[newItemPosition].first.someTitle

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]

            return Change(
                oldItem,
                newItem
            )
        }
    }

    inner class NasaViewHolder(view: View) : BaseViewHolder(view), ItemTouchHelperViewHolder {

        override fun bind(dataItem: Pair<Data, Boolean>) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.nasaTitle.text = dataItem.first.someTitle
                itemView.nasaDescriptionTextView.text = dataItem.first.someDescription
                itemView.date_recycler.text = dataItem.first.someDate
                itemView.url_recycler.text = dataItem.first.someURL
                itemView.nasaImageView.load(dataItem.first.someImage)
                itemView.nasaImageView.setOnClickListener {
                    onListItemClickListener.onItemClick(
                        dataItem.first
                    )
                }
            }
            itemView.nasaImageView.setOnClickListener { onListItemClickListener.onItemClick(dataItem.first) }
            itemView.removeItemImageView.setOnClickListener { removeItem() }
            itemView.moveItemDown.setOnClickListener { moveDown() }
            itemView.moveItemUp.setOnClickListener { moveUp() }
            itemView.nasaDescriptionTextView.visibility =
                if (dataItem.second) View.VISIBLE else View.GONE
            itemView.nasaTitle.setOnClickListener { toggleText() }
            itemView.dragHandleImageView.setOnTouchListener { _, event ->
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    dragListener.onStartDrag(this)
                }
                false
            }
        }

        private fun removeItem() {
            data.removeAt(layoutPosition)
            notifyItemRemoved(layoutPosition)
        }

        private fun moveUp() {
            layoutPosition.takeIf { it > 1 }?.also { currentPosition ->
                data.removeAt(currentPosition).apply {
                    data.add(currentPosition - 1, this)
                }
                notifyItemMoved(currentPosition, currentPosition - 1)
            }
        }

        private fun moveDown() {
            layoutPosition.takeIf { it < data.size - 1 }?.also { currentPosition ->
                data.removeAt(currentPosition).apply {
                    data.add(currentPosition + 1, this)
                }
                notifyItemMoved(currentPosition, currentPosition + 1)
            }
        }

        private fun toggleText() {
            data[layoutPosition] = data[layoutPosition].let {
                it.first to !it.second
            }
            notifyItemChanged(layoutPosition)
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

    inner class NoteViewHolder(view: View) : BaseViewHolder(view), ItemTouchHelperViewHolder {

        override fun bind(dataItem: Pair<Data, Boolean>) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.title.text = dataItem.first.someTitle
                itemView.description.text = dataItem.first.someDescription
                itemView.description.setOnClickListener {
                    onListItemClickListener.onItemClick(
                        dataItem.first
                    )
                }
            }
        }
        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

    interface OnListItemClickListener {
        fun onItemClick(data: Data)
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    companion object {
        private const val TYPE_NASA = 0
        private const val TYPE_NOTE = 1
    }
}

