package viktor.khlebnikov.geekgrains.android1.universearoundus.ui.api

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_api.*
import viktor.khlebnikov.geekgrains.android1.universearoundus.R

private const val TODAY = 0
private const val YESTERDAY = 1
private const val TWO_DAYS_AGO = 2

class ApiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api)
        view_pager.adapter = ViewPagerAdapter(supportFragmentManager)
        view_pager.setPageTransformer(true, ZoomOutPageTransformer())
        tab_layout.setupWithViewPager(view_pager)
        setHighlightedTab(TODAY)

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                setHighlightedTab(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }
        })
    }

    private fun setHighlightedTab(position: Int) {
        val layoutInflater = LayoutInflater.from(this@ApiActivity)

        tab_layout.getTabAt(TODAY)?.customView = null
        tab_layout.getTabAt(YESTERDAY)?.customView = null
        tab_layout.getTabAt(TWO_DAYS_AGO)?.customView = null

        when (position) {
            TODAY -> {
                setTodayTabHighlighted(layoutInflater)
            }
            YESTERDAY -> {
                setYesterdayTabHighlighted(layoutInflater)
            }
            TWO_DAYS_AGO -> {
                setTwoDaysAgoTabHighlighted(layoutInflater)
            }
            else -> {
                setTodayTabHighlighted(layoutInflater)
            }
        }
    }

    private fun setTodayTabHighlighted(layoutInflater: LayoutInflater) {
        val earth =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_today, null)
        earth.findViewById<AppCompatTextView>(R.id.tab_image_textview)
            .setTextColor(
                ContextCompat.getColor(
                    this@ApiActivity,
                    R.color.colorAccent
                )
            )
        tab_layout.getTabAt(TODAY)?.customView = earth
        tab_layout.getTabAt(YESTERDAY)?.customView =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_yesterday, null)
        tab_layout.getTabAt(TWO_DAYS_AGO)?.customView =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_two_days_ago, null)
    }

    private fun setYesterdayTabHighlighted(layoutInflater: LayoutInflater) {
        val mars =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_yesterday, null)
        mars.findViewById<AppCompatTextView>(R.id.tab_image_textview)
            .setTextColor(
                ContextCompat.getColor(
                    this@ApiActivity,
                    R.color.colorAccent
                )
            )
        tab_layout.getTabAt(TODAY)?.customView =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_today, null)
        tab_layout.getTabAt(YESTERDAY)?.customView = mars
        tab_layout.getTabAt(TWO_DAYS_AGO)?.customView =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_two_days_ago, null)
    }

    private fun setTwoDaysAgoTabHighlighted(layoutInflater: LayoutInflater) {
        val weather =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_two_days_ago, null)
        weather.findViewById<AppCompatTextView>(R.id.tab_image_textview)
            .setTextColor(
                ContextCompat.getColor(
                    this@ApiActivity,
                    R.color.colorAccent
                )
            )
        tab_layout.getTabAt(TODAY)?.customView =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_today, null)
        tab_layout.getTabAt(YESTERDAY)?.customView =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_yesterday, null)
        tab_layout.getTabAt(TWO_DAYS_AGO)?.customView = weather
    }

    override fun onBackPressed() {
        if (view_pager.currentItem == 0) {
            super.onBackPressed()
        } else {
            view_pager.setCurrentItem(view_pager.currentItem-1, true)
        }
    }

}
