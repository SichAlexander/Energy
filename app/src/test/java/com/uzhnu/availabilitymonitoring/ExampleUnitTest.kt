package com.uzhnu.availabilitymonitoring

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}

fun search(nums: IntArray, target: Int): Int {
    var left = 0
    var right = nums.size - 1
    var mid = 0
    while (left < right) {
        mid = left + (right - left) / 2
        if (nums[mid] == target) {
            return mid
        }
        if (nums[mid] < target) {
            left = mid + 1
        } else {
            right = mid - 1
        }
    }
    return mid
}


