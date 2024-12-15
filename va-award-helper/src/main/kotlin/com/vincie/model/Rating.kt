package com.vincie.model

data class Rating(
    val bilateral: Bilateral,
    val awardPercentage: AwardPercentage
) {

    companion object {

        /**
         * creates rating object from string
         * for use with CLI
         * input format: "LEFT_LEG,TEN"
         */
        fun fromString(input: String): Rating {

            val splitInput = input.split(",")

            return Rating(
                bilateral = Bilateral.valueOf(splitInput.get(0)),
                awardPercentage = AwardPercentage.valueOf(splitInput.get(1))
            )
        }
    }
}
