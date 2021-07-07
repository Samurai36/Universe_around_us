package viktor.khlebnikov.geekgrains.android1.universearoundus.ui.chips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_chips.*
import viktor.khlebnikov.geekgrains.android1.universearoundus.R
import viktor.khlebnikov.geekgrains.android1.universearoundus.databinding.FragmentChipsBinding

class ChipsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentChipsBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chipGroup.setOnCheckedChangeListener { chipGroup, position ->
            chipGroup.findViewById<Chip>(position)?.let {
                Toast.makeText(context, "Выбран ${it.text}", Toast.LENGTH_SHORT).show()
            }
        }

        chip_close.setOnCloseIconClickListener {
            Toast.makeText(
                context,
                "Close is Clicked",
                Toast.LENGTH_SHORT
            ).show()
        }

        switch_theme.setOnClickListener {
            requireActivity().recreate()
        }

        switch_theme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ThemeHolder.theme = R.style.Theme_UniverseAroundUs
            } else {
                ThemeHolder.theme = R.style.Theme_Wow
            }
        }

    }

}


object ThemeHolder {
    @StyleRes
    var theme: Int = R.style.Theme_UniverseAroundUs
}
