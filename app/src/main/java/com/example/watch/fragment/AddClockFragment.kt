package com.example.watch.fragment

import android.app.ActionBar.LayoutParams
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.watch.databinding.AddClockFragmentBinding
import com.example.watch.view.ClockView

class AddClockFragment : Fragment() {
    private var _binding: AddClockFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddClockFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btAdd.setOnClickListener {
            val height =
                if (binding.etHeight.text.isNotEmpty()) binding.etHeight.text.toString().toInt()
                else 0
            val width =
                if (binding.etWidth.text.isNotEmpty()) binding.etWidth.text.toString().toInt()
                else 0

            addClock(width, height)
        }
    }

    private fun addClock(width: Int, height: Int) {
        val clock = ClockView(requireContext())
        val w = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            width.toFloat(),
            resources.displayMetrics
        )
        val h = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            height.toFloat(),
            resources.displayMetrics
        )

        val layoutParams = if (w > 0 && h > 0) LayoutParams(
            w.toInt(),
            h.toInt()
        ) else LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        clock.setBackgroundColor(Color.argb(255, 0, 255, 255))
        clock.layoutParams = layoutParams
        binding.llClockList.addView(clock)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
