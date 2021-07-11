package viktor.khlebnikov.geekgrains.android1.universearoundus.ui.api

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import coil.api.load
import kotlinx.android.synthetic.main.main_fragment.*
import viktor.khlebnikov.geekgrains.android1.universearoundus.R
import viktor.khlebnikov.geekgrains.android1.universearoundus.ui.picture.PictureOfTheDayData
import viktor.khlebnikov.geekgrains.android1.universearoundus.ui.picture.PictureOfTheDayViewModel
import viktor.khlebnikov.geekgrains.android1.universearoundus.ui.picture.date
import viktor.khlebnikov.geekgrains.android1.universearoundus.ui.picture.datenow
import java.time.Period

class TwoDaysAgoFragment : Fragment() {

    private lateinit var PictureTitle: TextView


    private val viewModel: PictureOfTheDayViewModel by lazy {
        ViewModelProviders.of(this).get(PictureOfTheDayViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_two_days_ago, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        date = datenow
        var period = Period.of(0, 0, 2)
        date = date.minus(period)
        PictureTitle = view.findViewById(R.id.title)
        renderData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun renderData() {
        viewModel.getData()
            .observe(viewLifecycleOwner, { renderData(it) })
    }

    private fun renderData(data: PictureOfTheDayData) {
        when (data) {
            is PictureOfTheDayData.Success -> {
                loadingLayout.visibility = View.GONE
                val serverResponseData = data.serverResponseData
                val url = serverResponseData.url
                if (url.isNullOrEmpty()) {

                } else {
                    //showSuccess()
                    image_view.load(url) {
                        lifecycle(this@TwoDaysAgoFragment)
                        error(R.drawable.ic_load_error_vector)
                        placeholder(R.drawable.ic_no_photo_vector)
                    }
                    PictureTitle.text = serverResponseData.title
                }
            }
            is PictureOfTheDayData.Loading -> {
                loadingLayout.visibility = View.VISIBLE
            }
            is PictureOfTheDayData.Error -> {
                loadingLayout.visibility = View.GONE
            }
        }
    }
}
