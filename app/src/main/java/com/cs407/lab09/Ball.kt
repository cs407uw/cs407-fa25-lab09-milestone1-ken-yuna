package com.cs407.lab09

class Ball(
    private val backgroundWidth: Float,
    private val backgroundHeight: Float,
    private val ballSize: Float
) {
    var posX = 0f
    var posY = 0f
    var velocityX = 0f
    var velocityY = 0f
    private var accX = 0f
    private var accY = 0f
    private var isFirstUpdate = true

    init {
        reset()
    }

    fun updatePositionAndVelocity(xAcc: Float, yAcc: Float, dT: Float) {
        if (isFirstUpdate) {
            isFirstUpdate = false
            accX = xAcc
            accY = yAcc
            return
        }

        val v0x = velocityX
        val v0y = velocityY
        val a0x = accX
        val a0y = accY
        val a1x = xAcc
        val a1y = yAcc

        // Update velocity — Eq(1)
        velocityX = v0x + 0.5f * (a0x + a1x) * dT
        velocityY = v0y + 0.5f * (a0y + a1y) * dT

        // Update position — Eq(2)
        posX += v0x * dT + (1f / 6f) * (3f * a0x + a1x) * dT * dT
        posY += v0y * dT + (1f / 6f) * (3f * a0y + a1y) * dT * dT

        // Store latest acceleration (경계 체크 전에 저장)
        accX = a1x
        accY = a1y

        checkBoundaries()
    }

    fun checkBoundaries() {
        // Left boundary
        if (posX < 0f) {
            posX = 0f
            velocityX = 0f  // 속도만 0으로
            // accX = 0f  // 제거! 가속도는 유지해야 함
        }

        // Right boundary
        if (posX > backgroundWidth - ballSize) {
            posX = backgroundWidth - ballSize
            velocityX = 0f
            // accX = 0f  // 제거!
        }

        // Top boundary
        if (posY < 0f) {
            posY = 0f
            velocityY = 0f
            // accY = 0f  // 제거!
        }

        // Bottom boundary
        if (posY > backgroundHeight - ballSize) {
            posY = backgroundHeight - ballSize
            velocityY = 0f
            // accY = 0f  // 제거!
        }
    }

    fun reset() {
        posX = (backgroundWidth - ballSize) / 2f
        posY = (backgroundHeight - ballSize) / 2f
        velocityX = 0f
        velocityY = 0f
        accX = 0f
        accY = 0f
        isFirstUpdate = true
    }
}