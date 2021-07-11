package viktor.khlebnikov.geekgrains.android1.universearoundus.ui.picture

import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import coil.api.load
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_chips.*
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_fragment.chipGroup
import kotlinx.android.synthetic.main.main_fragment.view.*
import viktor.khlebnikov.geekgrains.android1.universearoundus.R
import viktor.khlebnikov.geekgrains.android1.universearoundus.databinding.MainFragmentBinding
import viktor.khlebnikov.geekgrains.android1.universearoundus.ui.MainActivity
import viktor.khlebnikov.geekgrains.android1.universearoundus.ui.api.ApiActivity
import viktor.khlebnikov.geekgrains.android1.universearoundus.ui.chips.ChipsFragment
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
@RequiresApi(Build.VERSION_CODES.O)
val datenow = LocalDate.now()
lateinit var date: LocalDate

class PictureOfTheDayFragment : Fragment() {

    private lateinit var bottomSheetHeader: TextView
    private lateinit var bottomSheetContent: TextView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val viewModel: PictureOfTheDayViewModel by lazy {
        ViewModelProviders.of(this).get(PictureOfTheDayViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return MainFragmentBinding.inflate(inflater, container, false).root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetBehavior(view.findViewById(R.id.bottom_sheet_container))
        input_layout.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://ru.wikipedia.org/wiki/${input_edit_text.text.toString()}")
            })
        }
        date = datenow
        chipGroup.setOnCheckedChangeListener { chipGroup, checkedChipIds ->
            chipGroup.findViewById<Chip>(checkedChipIds)?.let {

                when (it) {
                    chiptwodaysago -> {
                        date = datenow
                        var period = Period.of(0, 0, 2)
                        date = date.minus(period)
                        Toast.makeText(context, "Показать $date", Toast.LENGTH_LONG).show()
                        renderData()
                    }
                    chipyesterday -> {
                        date = datenow
                        var period = Period.of(0, 0, 1)
                        date = date.minus(period)
                        Toast.makeText(context, "Показать $date", Toast.LENGTH_LONG).show()
                        renderData()
                    }
                    chiptoday -> {
                        date = datenow
                        Toast.makeText(context, "Показать $date", Toast.LENGTH_LONG).show()
                        renderData()
                    }
                }
            }
        }

        bottomSheetHeader = view.findViewById(R.id.bottom_sheet_description_header)
        bottomSheetContent = view.findViewById(R.id.bottom_sheet_description)
        setBottomAppBar(view)

        renderData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun renderData() {
        viewModel.getData()
            .observe(viewLifecycleOwner, { renderData(it) })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_bottom_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_fav -> toast("Favourite")
            R.id.app_bar_settings -> activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.container, ChipsFragment())?.addToBackStack(null)?.commit()
            android.R.id.home -> {
                activity?.let {
                    val bottomNavigationDrawerFragment = BottomNavigationDrawerFragment()
                    bottomNavigationDrawerFragment.show(it.supportFragmentManager, "tag")
                    view?.postDelayed({ bottomNavigationDrawerFragment.dismiss() }, 3000)
                }
            }
            R.id.app_bar_api -> activity?.let { startActivity(Intent(it, ApiActivity::class.java)) }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun renderData(data: PictureOfTheDayData) {
        when (data) {
            is PictureOfTheDayData.Success -> {
                main.visibility = View.VISIBLE
                loadingLayout.visibility = View.GONE
                val serverResponseData = data.serverResponseData
                val url = serverResponseData.url
                if (url.isNullOrEmpty()) {
                    //showError("Сообщение, что ссылка пустая")
                    toast("ссылка пустая")
                } else {
                    //showSuccess()
                    image_view.load(url) {
                        lifecycle(this@PictureOfTheDayFragment)
                        error(R.drawable.ic_load_error_vector)
                        placeholder(R.drawable.ic_no_photo_vector)
                    }
                    bottomSheetHeader.text = serverResponseData.title
                    bottomSheetContent.text = serverResponseData.explanation
                }
            }
            is PictureOfTheDayData.Loading -> {
                main.visibility = View.GONE
                loadingLayout.visibility = View.VISIBLE
            }
            is PictureOfTheDayData.Error -> {
                main.visibility = View.VISIBLE
                loadingLayout.visibility = View.GONE
                toast(data.error.message)
            }
        }
    }

    private fun setBottomAppBar(view: View) {
        val context = activity as MainActivity
        context.setSupportActionBar(view.findViewById(R.id.bottom_app_bar))
        setHasOptionsMenu(true)
        fab.setOnClickListener {
            if (isMain) {
                isMain = false
                bottom_app_bar.navigationIcon = null
                bottom_app_bar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_back_fab))
                bottom_app_bar.replaceMenu(R.menu.menu_bottom_bar_other_screen)
            } else {
                isMain = true
                bottom_app_bar.navigationIcon =
                    ContextCompat.getDrawable(context, R.drawable.ic_hamburger_menu_bottom_bar)
                bottom_app_bar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_plus_fab))
                bottom_app_bar.replaceMenu(R.menu.menu_bottom_bar)
            }
        }
    }

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun Fragment.toast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.BOTTOM, 0, 300)
            show()
        }
    }

    companion object {
        fun newInstance() = PictureOfTheDayFragment()
        private var isMain = true
    }
}
