package com.cs407.lab09

import android.hardware.Sensor
import android.hardware.SensorEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BallViewModel : ViewModel() {
    private var ball: Ball? = null
    private var lastTimestamp: Long = 0L
    private val _ballPosition = MutableStateFlow(Offset.Zero)
    val ballPosition: StateFlow<Offset> = _ballPosition.asStateFlow()

    // 임계값 추가 (0.5 이하의 기울기는 무시)
    private val THRESHOLD = 0.5f

    fun initBall(fieldWidth: Float, fieldHeight: Float, ballSizePx: Float) {
        if (ball == null) {
            ball = Ball(fieldWidth, fieldHeight, ballSizePx)
            _ballPosition.value = Offset(ball!!.posX, ball!!.posY)
        }
    }

    fun onSensorDataChanged(event: SensorEvent) {
        val currentBall = ball ?: return
        if (event.sensor.type == Sensor.TYPE_GRAVITY) {
            if (lastTimestamp != 0L) {
                val NS2S = 1f / 1_000_000_000f
                val dT = (event.timestamp - lastTimestamp) * NS2S

                // 센서 값에 임계값 적용
                var xAcc = -event.values[0]
                var yAcc = event.values[1]

                // 임계값보다 작은 움직임은 0으로 처리
                if (kotlin.math.abs(xAcc) < THRESHOLD) {
                    xAcc = 0f
                }
                if (kotlin.math.abs(yAcc) < THRESHOLD) {
                    yAcc = 0f
                }

                // 스케일 팩터 (필요시 조정)
                val scaleFactor = 100f
                xAcc *= scaleFactor
                yAcc *= scaleFactor

                // Update ball physics
                currentBall.updatePositionAndVelocity(xAcc, yAcc, dT)

                // Update UI
                _ballPosition.update {
                    Offset(currentBall.posX, currentBall.posY)
                }
            }
            lastTimestamp = event.timestamp
        }
    }

    fun reset() {
        ball?.reset()
        ball?.let {
            _ballPosition.value = Offset(it.posX, it.posY)
        }
        lastTimestamp = 0L
    }
}