package viktor.khlebnikov.geekgrains.android1.universearoundus.ui.picture

sealed class PictureOfTheDayData {
    data class Success(val serverResponseData: PODServerResponseData) : PictureOfTheDayData()
    data class Error(val error: Throwable) : PictureOfTheDayData()
    object Loading: PictureOfTheDayData()
}
