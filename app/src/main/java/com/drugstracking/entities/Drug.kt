package com.drugstracking.entities

data class Drug(val name: String, val time: String, val comment: String) {
    override fun toString(): String {
        return "$name     $time     $comment"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Drug

        if (name != other.name) return false
        if (time != other.time) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + time.hashCode()
        return result
    }
}