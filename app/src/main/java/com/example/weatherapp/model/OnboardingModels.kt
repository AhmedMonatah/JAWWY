package com.example.weatherapp.model

import androidx.annotation.StringRes
import com.example.weatherapp.R

data class OnboardingPageData(
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int,
    val type: PageType
)

enum class PageType {
    GLOBAL_WEATHER,
    WEATHER_DAY,
    ALERTS
}

val onboardingPages = listOf(
    OnboardingPageData(
        titleResId = R.string.onboarding_title_1,
        descriptionResId = R.string.onboarding_desc_1,
        type = PageType.WEATHER_DAY
    ),
    OnboardingPageData(
        titleResId = R.string.onboarding_title_2,
        descriptionResId = R.string.onboarding_desc_2,
        type = PageType.ALERTS
    ),
    OnboardingPageData(
        titleResId = R.string.onboarding_title_3,
        descriptionResId = R.string.onboarding_desc_3,
        type = PageType.GLOBAL_WEATHER
    )
)
