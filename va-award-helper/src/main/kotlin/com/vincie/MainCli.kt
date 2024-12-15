package com.vincie

import com.vincie.controller.CombinedRatingService
import com.vincie.model.CombinedRatingsTable
import com.vincie.view.Cli

    fun main(args: Array<String>) {
        val cli = Cli(
            service = CombinedRatingService(
                ratingsTable = CombinedRatingsTable()
            )
        )
        cli.run()
    }