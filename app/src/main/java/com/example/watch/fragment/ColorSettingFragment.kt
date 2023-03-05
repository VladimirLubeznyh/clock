package com.example.watch.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.watch.R
import com.example.watch.databinding.ColorSettingFragmentBinding

class ColorSettingFragment : Fragment() {
    private var _binding: ColorSettingFragmentBinding? = null
    private val binding get() = _binding!!

    private val colors = listOf(
        R.color.black,
        R.color.white,
        R.color.brightGray,
        R.color.darkGray,
        R.color.beige,
        R.color.purple_500,
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ColorSettingFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listingSetting()
        binding.next.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, AddClockFragment())
                .addToBackStack("TEG")
                .commit()
        }
    }

    private fun listingSetting() {

        with(binding) {
            hourHandColor.settingTitle.text = getString(R.string.hour_hand)
            hourHandColor.sliderTime.addOnChangeListener { _, value, _ ->
                clock.colorHourHand = requireContext().getColor(colors[value.toInt()])
            }

            minuteScaleColor.settingTitle.text = getString(R.string.scale)
            minuteScaleColor.sliderTime.addOnChangeListener { _, value, _ ->
                clock.colorMinuteScale = requireContext().getColor(colors[value.toInt()])
            }

            secondHandColor.settingTitle.text = getString(R.string.second_hand)

            secondHandColor.sliderTime.addOnChangeListener { _, value, _ ->
                clock.colorSecondHand = requireContext().getColor(colors[value.toInt()])
            }

            innerBackgroundColor.settingTitle.text = getString(R.string.background)
            innerBackgroundColor.sliderTime.addOnChangeListener { _, value, _ ->
                clock.colorInnerBackground = requireContext().getColor(colors[value.toInt()])
            }

            outerCircleColor.settingTitle.text = getString(R.string.outer_circle)
            outerCircleColor.sliderTime.addOnChangeListener { _, value, _ ->
                clock.colorOuterCircle = requireContext().getColor(colors[value.toInt()])
            }

            textNumbersColor.settingTitle.text = getString(R.string.text_numbers)
            textNumbersColor.sliderTime.addOnChangeListener { _, value, _ ->
                clock.colorText = requireContext().getColor(colors[value.toInt()])
            }

            minuteHandColor.settingTitle.text = getString(R.string.minute_hand)
            minuteHandColor.sliderTime.addOnChangeListener { _, value, _ ->
                clock.colorMinuteHand = requireContext().getColor(colors[value.toInt()])
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
