package com.vincie.model

/**
 * The lookup table that gives the combined rating given the current rating and the next AwardPercentage
 * Ref: Title 38, Ch 1, Part 4, Subpart A, Table 1 - "Combined Ratings Table"
 * Also handles the edge case when two ratings of 10% must be combined
 * Note to self: never auto-format this bad boy
 */
class CombinedRatingsTable {

    private val x =   listOf(19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94)
    private val y10 = listOf(27,28,29,30,31,32,33,33,34,35,36,37,38,39,40,41,42,42,43,44,45,46,47,48,49,50,51,51,52,53,54,55,56,57,58,59,60,60,61,62,63,64,65,66,67,68,69,69,70,71,72,73,74,75,76,77,78,78,79,80,81,82,83,84,85,86,87,87,88,89,90,91,92,93,94,95)
    private val y20 = listOf(35,36,37,38,38,39,40,41,42,42,43,44,45,46,46,47,48,49,50,50,51,52,53,54,54,55,56,57,58,58,59,60,61,62,62,63,64,65,66,66,67,68,69,70,70,71,72,73,74,74,75,76,77,78,78,79,80,81,82,82,83,84,85,86,86,87,88,89,90,90,91,92,93,94,94,95)
    private val y30 = listOf(43,44,45,45,46,47,48,48,49,50,50,51,52,52,53,54,55,55,56,57,57,58,59,59,60,61,62,62,63,64,64,65,66,66,67,68,69,69,70,71,71,72,73,73,74,75,76,76,77,78,78,79,80,80,81,82,83,83,84,85,85,86,87,87,88,89,90,90,91,92,92,93,94,94,95,96)
    private val y40 = listOf(51,52,53,53,54,54,55,56,56,57,57,58,59,59,60,60,61,62,62,63,63,64,65,65,66,66,67,68,68,69,69,70,71,71,72,72,73,74,74,75,75,76,77,77,78,78,79,80,80,81,81,82,83,83,84,84,85,86,86,87,87,88,89,89,90,90,91,92,92,93,93,94,95,95,96,96)
    private val y50 = listOf(60,60,61,61,62,62,63,63,64,64,65,65,66,66,67,67,68,68,69,69,70,70,71,71,72,72,73,78,74,74,75,75,76,76,77,77,78,78,79,79,80,80,81,81,82,82,83,83,84,84,85,85,86,86,87,87,88,88,89,89,90,90,91,91,92,92,93,93,94,94,95,95,96,96,97,97)
    private val y60 = listOf(68,68,68,69,69,70,70,70,71,71,72,72,72,73,73,74,74,74,75,75,76,76,76,77,77,78,78,78,79,79,80,80,80,81,81,82,82,82,83,83,84,84,84,85,85,86,86,86,87,87,88,88,88,89,89,90,90,90,91,91,92,92,92,93,93,94,94,94,95,95,96,96,96,97,97,98)
    private val y70 = listOf(76,76,76,77,77,77,78,78,78,78,79,79,79,80,80,80,81,81,81,81,82,82,82,83,83,83,84,84,84,84,85,85,85,86,86,86,87,87,87,87,88,88,88,89,89,89,90,90,90,90,91,91,91,92,92,92,93,93,93,93,94,94,94,95,95,95,96,96,96,96,97,97,97,98,98,99)
    private val y80 = listOf(84,84,84,84,85,85,85,85,85,86,86,86,86,86,87,87,87,87,87,88,88,88,88,88,89,89,89,89,89,90,90,90,90,90,91,91,91,91,91,92,92,92,92,92,93,93,93,93,93,94,94,94,94,94,95,95,95,95,95,96,96,96,96,96,97,97,97,97,97,98,98,98,98,98,99,99)
    private val y90 = listOf(92,92,92,92,92,92,93,93,93,93,93,93,93,93,93,93,94,94,94,94,94,94,94,94,94,94,95,95,95,95,95,95,95,95,95,95,96,96,96,96,96,96,96,96,96,96,97,97,97,97,97,97,97,97,97,97,98,98,98,98,98,98,98,98,98,98,99,99,99,99,99,99,99,99,99,99)

    private val map10 = hashMapOf<Int, Int>()
    private val map20 = hashMapOf<Int, Int>()
    private val map30 = hashMapOf<Int, Int>()
    private val map40 = hashMapOf<Int, Int>()
    private val map50 = hashMapOf<Int, Int>()
    private val map60 = hashMapOf<Int, Int>()
    private val map70 = hashMapOf<Int, Int>()
    private val map80 = hashMapOf<Int, Int>()
    private val map90 = hashMapOf<Int, Int>()

    init {buildMaps()}

    fun combineRating(currentRating: Int, nextRating: AwardPercentage): Int? {

        if (currentRating == 10 && nextRating == AwardPercentage.TEN) {
            return 19 //special edge-case if the claimant has 2 of the lowest possible ratings
        }

        return when(nextRating) {
            AwardPercentage.TEN -> map10[currentRating]
            AwardPercentage.TWENTY -> map20[currentRating]
            AwardPercentage.THIRTY -> map30[currentRating]
            AwardPercentage.FORTY -> map40[currentRating]
            AwardPercentage.FIFTY -> map50[currentRating]
            AwardPercentage.SIXTY -> map60[currentRating]
            AwardPercentage.SEVENTY -> map70[currentRating]
            AwardPercentage.EIGHTY -> map80[currentRating]
            AwardPercentage.NINETY -> map90[currentRating]
        }
    }

    private fun buildMaps() {
        for (i in x.indices) {
            map10[x[i]] = y10[i]
            map20[x[i]] = y20[i]
            map30[x[i]] = y30[i]
            map40[x[i]] = y40[i]
            map50[x[i]] = y50[i]
            map60[x[i]] = y60[i]
            map70[x[i]] = y70[i]
            map80[x[i]] = y80[i]
            map90[x[i]] = y90[i]
        }
    }
}