package com.meiri.caloriecalculator

class Segment(full: Boolean) {

    var state: SegmentState = if (full) SegmentState.FULL else SegmentState.EMPTY

    enum class SegmentState {
        EMPTY,
        FULL
    }
}