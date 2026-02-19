package com.example.weatherapp.ui.onboarding

data class OnboardingPageData(
    val title: String,
    val description: String,
    val type: PageType
)

enum class PageType {
    RAMADAN_NIGHT,
    WEATHER_DAY,
    ALERTS
}

val onboardingPages = listOf(
    OnboardingPageData(
        title = "Ramadan Blessings",
        description = "Experience the holy month with accurate prayer times and weather guidance for your spiritual journey.",
        type = PageType.RAMADAN_NIGHT
    ),
    OnboardingPageData(
        title = "Accurate Forecast",
        description = "Plan your day with precise weather updates. Know exactly when to seek shade or enjoy the sun.",
        type = PageType.WEATHER_DAY
    ),
    OnboardingPageData(
        title = "Stay Informed",
        description = "Get timely weather alerts to keep you and your family safe during sudden weather changes.",
        type = PageType.ALERTS
    )
)
