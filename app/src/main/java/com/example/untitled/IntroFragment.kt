package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2

class IntroFragment : Fragment() {

    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var layoutOnboardingIndicator: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutOnboardingIndicator = view.findViewById(R.id.dots_indicator)

        val onboardingItems = listOf(
            OnboardingItem(
                title = getString(R.string.onboarding_finance_title),
                description = getString(R.string.onboarding_finance_desc),
                imageResId = R.drawable.ic_finance // Replaced placeholder
            ),
            OnboardingItem(
                title = getString(R.string.onboarding_habits_title),
                description = getString(R.string.onboarding_habits_desc),
                imageResId = R.drawable.ic_habits // Replaced placeholder
            ),
            OnboardingItem(
                title = getString(R.string.onboarding_goals_title),
                description = getString(R.string.onboarding_goals_desc),
                imageResId = R.drawable.ic_goals // Replaced placeholder
            )
        )

        onboardingAdapter = OnboardingAdapter(onboardingItems)

        val onboardingViewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        onboardingViewPager.adapter = onboardingAdapter

        setupOnboardingIndicators(onboardingItems.size)
        setCurrentOnboardingIndicator(0)

        onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentOnboardingIndicator(position)
            }
        })

        view.findViewById<View>(R.id.btn_next).setOnClickListener {
            if (onboardingViewPager.currentItem + 1 < onboardingAdapter.itemCount) {
                onboardingViewPager.currentItem += 1
            } else {
                findNavController().navigate(R.id.action_introFragment_to_signupFragment)
            }
        }

        view.findViewById<View>(R.id.tv_sign_in).setOnClickListener {
            // Navigate to Login instead of Signup
             findNavController().navigate(R.id.action_introFragment_to_signupFragment) 
             // Ideally this should go to a LoginFragment if one exists or handle it inside SignupFragment
        }
    }

    private fun setupOnboardingIndicators(count: Int) {
        val indicators = arrayOfNulls<ImageView>(count)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext())
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.indicator_inactive
                )
            )
            indicators[i]?.layoutParams = layoutParams
            layoutOnboardingIndicator.addView(indicators[i])
        }
    }

    private fun setCurrentOnboardingIndicator(index: Int) {
        val childCount = layoutOnboardingIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = layoutOnboardingIndicator.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.indicator_active)
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.indicator_inactive)
                )
            }
        }
    }
}
