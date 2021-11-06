package com.meiri.caloriecalculator

/**
 * Created by Tiago Ornelas on 18/04/2020.
 * Model that holds the segment state
 */
class Segment(private val full: Boolean) {

    var state: SegmentState = if (full) SegmentState.FULL else SegmentState.EMPTY

//    private var animationProgress: Int = 0

//    var animationState: AnimationState = AnimationState.IDLE
//        set(value) {
//            animationProgress = when (value) {
//                AnimationState.ANIMATED -> 100
//                AnimationState.IDLE -> 0
//                else -> animationProgress
//            }
//            field = value
//        }

    /**
     * Represents possible drawing states of the segment
     */
//    enum class AnimationState {
//        ANIMATED,
//        ANIMATING,
//        IDLE
//    }

    enum class SegmentState {
        EMPTY,
        FULL
    }

//    val progressPercentage: Float
//        get() = animationProgress.toFloat() / 100

//    fun progress() = animationProgress++
}