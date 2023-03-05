package com.example.watch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
            hourHandColor.run {
                settingTitle.text = getString(R.string.hour_hand)
                sliderTime.addOnChangeListener { _, value, _ ->
                    clock.colorHourHand = requireContext().getColor(colors[value.toInt()])
                }
            }
            minuteScaleColor.run {
                settingTitle.text = getString(R.string.scale)
                sliderTime.addOnChangeListener { _, value, _ ->
                    clock.colorMinuteScale = requireContext().getColor(colors[value.toInt()])
                }
            }
            secondHandColor.run {
                settingTitle.text = getString(R.string.second_hand)

                sliderTime.addOnChangeListener { _, value, _ ->
                    clock.colorSecondHand = requireContext().getColor(colors[value.toInt()])
                }
            }
            innerBackgroundColor.run {
                settingTitle.text = getString(R.string.background)
                sliderTime.addOnChangeListener { _, value, _ ->
                    clock.colorInnerBackground = requireContext().getColor(colors[value.toInt()])
                }
            }
            outerCircleColor.run {
                settingTitle.text = getString(R.string.outer_circle)
                sliderTime.addOnChangeListener { _, value, _ ->
                    clock.colorOuterCircle = requireContext().getColor(colors[value.toInt()])
                }
            }
            textNumbersColor.run {
                settingTitle.text = getString(R.string.text_numbers)
                sliderTime.addOnChangeListener { _, value, _ ->
                    clock.colorText = requireContext().getColor(colors[value.toInt()])
                }
            }
            minuteHandColor.run {
                settingTitle.text = getString(R.string.minute_hand)
                sliderTime.addOnChangeListener { _, value, _ ->
                    clock.colorMinuteHand =requireContext(). getColor(colors[value.toInt()])
                }
            }
        }
    }
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
