package com.example.traindriver.ui.screen.adding_screen.state_holder

enum class WorkTimeType {
    START, END
}

data class WorkTimeState(
    val time: Long? = null,
    val type: WorkTimeType,
)

sealed class WorkTimeEvent {
    data class EnteredStartTime(val value: Long?) : WorkTimeEvent()
    data class EnteredEndTime(val value: Long?) : WorkTimeEvent()
    data class FocusChange(val fieldName: WorkTimeType) : WorkTimeEvent()
}

data class WorkTimeEditState(
    val startTime: WorkTimeState = WorkTimeState(type = WorkTimeType.START),
    val endTime: WorkTimeState = WorkTimeState(type = WorkTimeType.END),
    val formValid: Boolean,
    val errorMessage: String = "Время явки позже сдачи"
)