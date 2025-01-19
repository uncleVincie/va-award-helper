package com.vincie.model

enum class AwardPercentage(val value: Int) {
    TEN(10),
    TWENTY(20),
    THIRTY(30),
    FORTY(40),
    FIFTY(50),
    SIXTY(60),
    SEVENTY(70),
    EIGHTY(80),
    NINETY(90);

    companion object {
        fun fromInt(value: Int) = entries.first { it.value == value }
    }
}