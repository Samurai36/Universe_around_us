package viktor.khlebnikov.geekgrains.android1.universearoundus.ui.picture

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.*
import android.widget.ImageView
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
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import kotlinx.android.synthetic.main.fragment_chips.*
import kotlinx.android.synthetic.main.fragment_note_editor.*
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_fragment.chipGroup
import kotlinx.android.synthetic.main.main_fragment.view.*
import viktor.khlebnikov.geekgrains.android1.universearoundus.R
import viktor.khlebnikov.geekgrains.android1.universearoundus.databinding.MainFragmentBinding
import viktor.khlebnikov.geekgrains.android1.universearoundus.ui.MainActivity
import viktor.khlebnikov.geekgrains.android1.universearoundus.ui.api.ApiActivity
import viktor.khlebnikov.geekgrains.android1.universearoundus.ui.chips.ChipsFragment
import viktor.khlebnikov.geekgrains.android1.universearoundus.ui.recycler.RecyclerActivity
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

@RequiresApi(Build.VERSION_CODES.O)
val datenow = LocalDate.now()
lateinit var date: LocalDate

class PictureOfTheDayFragment : Fragment() {

    private var isExpanded = false
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

        image_view.setOnClickListener {
            isExpanded = !isExpanded
            TransitionManager.beginDelayedTransition(
                main, TransitionSet()
                    .addTransition(ChangeBounds())
                    .addTransition(ChangeImageTransform())
            )

            val params: ViewGroup.LayoutParams = image_view.layoutParams
            params.height =
                if (isExpanded) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
            image_view.layoutParams = params
            image_view.scaleType =
                if (isExpanded) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_CENTER
        }

        wiki_button.setOnClickListener {
            if (!isExpanded) {
                isExpanded = true
                ObjectAnimator.ofFloat(wiki_button, "rotation", 0f, 360f).start()
            }
        }

        save_btn_nasa.setOnClickListener {
            val intent = Intent(activity, RecyclerActivity::class.java)
            intent.putExtra("title", bottom_sheet_description_header.toString())
            intent.putExtra("description", bottom_sheet_description.toString())
            intent.putExtra("image", textview_url.toString())
            intent.putExtra("date", datenow)
        }

        activity?.let {
            bottomSheetContent.typeface =
                Typeface.createFromAsset(it.assets, "falling-sky-font/FallingSkyBoldplus-6GZ1.otf")
        }

        renderData()
    }

    fun spanVidelenieSlov() {

        val videlenie = requireContext().getString(R.string.span_that)
        val videlenie2 = requireContext().getString(R.string.span_color)

        val spannable = SpannableStringBuilder(bottomSheetContent.text)
        val matcher = videlenie.toRegex()
        val matcher2 = videlenie2.toRegex()
        val first = matcher.find(spannable.toString())?.range?.first
        val last = matcher.find(spannable.toString())?.range?.last

        val foregroundSpan = ForegroundColorSpan(Color.RED)


        if (first != null && last!= null) {
            spannable.setSpan(foregroundSpan, first, last+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }

        if (first != null && last != null) {
            spannable.setSpan(
                BackgroundColorSpan(
                    ContextCompat.getColor(requireContext(), R.color.anti_colorAccent)
                ),
                first,
                last + 1,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }

        matcher2.find(spannable.toString())?.range?.first?.let {
            matcher2.find(spannable.toString())?.range?.last?.let { it1 ->
                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(requireContext(), R.color.anti_teal_700)
                    ),
                    it,
                    it1 + 1,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
            }
        }

        bottomSheetContent.text = spannable
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
                    textview_url.text = url
                    image_view.load(url) {
                        lifecycle(this@PictureOfTheDayFragment)
                        error(R.drawable.ic_load_error_vector)
                        placeholder(R.drawable.ic_no_photo_vector)
                    }
                    bottomSheetHeader.text = serverResponseData.title
                    bottomSheetContent.text = serverResponseData.explanation
                    spanVidelenieSlov()
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
