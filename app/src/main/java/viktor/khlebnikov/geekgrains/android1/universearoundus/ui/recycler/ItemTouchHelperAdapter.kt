package viktor.khlebnikov.geekgrains.android1.universearoundus.ui.recycler

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
}