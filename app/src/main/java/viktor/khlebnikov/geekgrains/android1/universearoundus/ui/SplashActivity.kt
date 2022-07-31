package viktor.khlebnikov.geekgrains.android1.universearoundus.ui

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash.*
import viktor.khlebnikov.geekgrains.android1.universearoundus.R

class SplashActivity : AppCompatActivity() {

    var handler = Looper.myLooper()?.let { Handler(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        image_view.animate().rotationBy(750f).scaleY(-1f).scaleX(-1f)
            .setInterpolator(AccelerateDecelerateInterpolator()).setDuration(3000)
            .setListener(object : Animator.AnimatorListener {

                override fun onAnimationEnd(animation: Animator?) {
                    Toast.makeText(this@SplashActivity, "AnimationEnd", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }

                override fun onAnimationRepeat(animation: Animator?) {
                    Toast.makeText(this@SplashActivity, "onAnimationRepeat", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAnimationCancel(animation: Animator?) {
                    Toast.makeText(this@SplashActivity, "onAnimationCancel", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAnimationStart(animation: Animator?) {
                    Toast.makeText(this@SplashActivity, "Start", Toast.LENGTH_SHORT).show()
                }
            })

    }

    override fun onDestroy() {
        handler?.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}