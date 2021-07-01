package viktor.khlebnikov.geekgrains.android1.universearoundus.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import viktor.khlebnikov.geekgrains.android1.universearoundus.R
import viktor.khlebnikov.geekgrains.android1.universearoundus.ui.picture.PictureOfTheDayFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PictureOfTheDayFragment.newInstance())
                .commitNow()
        }
    }
}
