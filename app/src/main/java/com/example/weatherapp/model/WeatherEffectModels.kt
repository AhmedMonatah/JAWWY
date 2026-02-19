package com.example.weatherapp.model

import kotlin.random.Random

data class Snowflake(
    val x: Float = Random.nextFloat(),
    val y: Float = Random.nextFloat(),
    val radius: Float = Random.nextFloat() * 5f + 2f,
    val speed: Float = Random.nextFloat() * 0.5f + 0.1f,
    val wobble: Float = Random.nextFloat() * 10f
)

data class Raindrop(
    val x: Float = Random.nextFloat(),
    val y: Float = Random.nextFloat(),
    val length: Float = Random.nextFloat() * 20f + 10f,
    val speed: Float = Random.nextFloat() * 1.5f + 1.0f
)

data class Particle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float,
    val rotation: Float = 0f
)
