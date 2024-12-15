package com.vincie.view

import com.vincie.controller.CombinedRatingService
import com.vincie.model.Rating
import java.util.*

class Cli(val service: CombinedRatingService) {

    val scanner: Scanner = Scanner(System.`in`)

    fun run() {
        println("Add ratings in the format 'LEFT_LEG,20' and press enter, stop with a rating of 0")
        val input = mutableListOf<Rating>()

        do {
            val ratingString = scanner.next()
            try {
                val rating = Rating.fromString(ratingString)
                input.add(rating)
            } catch (_: IllegalArgumentException) {}
        } while(ratingString.split(",")[1] != "0")

        service.calculateFinalRating(input)
        service.printReport()
    }
}