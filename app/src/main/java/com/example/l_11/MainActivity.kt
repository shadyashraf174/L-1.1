package com.example.l_11

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private lateinit var btnRunSimulation: Button
    private lateinit var tableLayout: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRunSimulation = findViewById(R.id.btnRunSimulation)
        tableLayout = findViewById(R.id.tableLayout)

        btnRunSimulation.setOnClickListener {
            runSimulation()
        }
    }

    private fun runSimulation() {
        tableLayout.removeAllViews()

        val initialVelocityText = findViewById<EditText>(R.id.editTextVelocity).text.toString()
        val initialAngleText = findViewById<EditText>(R.id.editTextAngle).text.toString()
        val initialHeightText = findViewById<EditText>(R.id.editTextHeight).text.toString()
        val sizeText = findViewById<EditText>(R.id.editTextSize).text.toString()
        val weightText = findViewById<EditText>(R.id.editTextWeight).text.toString()

        val initialVelocity = initialVelocityText.toDoubleOrNull() ?: return
        val initialAngle = initialAngleText.toDoubleOrNull() ?: return
        val initialHeight = initialHeightText.toDoubleOrNull() ?: return
        val size = sizeText.toDoubleOrNull() ?: return
        val weight = weightText.toDoubleOrNull() ?: return

        val initialStep = 0.05
        val finalStep = 0.5
        val stepSize = 0.05

        var step = initialStep
        while (step <= finalStep) {
            val (timeStep, nestedPair) = simulateFlight(step, initialVelocity, initialAngle, initialHeight, size, weight)
            val (distance, nestedPair2) = nestedPair
            val (maxHeight, speedAtEndPoint) = nestedPair2
            displayResults(timeStep, distance, maxHeight, speedAtEndPoint)
            step += stepSize
        }
    }

    private fun simulateFlight(step: Double, initialVelocity: Double, initialAngle: Double, initialHeight: Double, size: Double, weight: Double): Pair<Double, Pair<Double, Pair<Double, Double>>> {
        val g = 9.81
        val radians = Math.toRadians(initialAngle)
        val vx = initialVelocity * cos(radians)
        val vy = initialVelocity * sin(radians)

        var time = 0.0
        var x = 0.0
        var y = initialHeight
        var maxHeight = initialHeight
        var endSpeed = 0.0

        while (y >= 0) {
            val k1x = vx
            val k1y = vy - g
            val k2x = vx + k1x * step / 2
            val k2y = vy - g + k1y * step / 2
            val k3x = vx + k2x * step / 2
            val k3y = vy - g + k2y * step / 2
            val k4x = vx + k3x * step
            val k4y = vy - g + k3y * step

            x += (k1x + 2 * k2x + 2 * k3x + k4x) * step / 6
            y += (k1y + 2 * k2y + 2 * k3y + k4y) * step / 6

            if (y > maxHeight) {
                maxHeight = y
            }

            endSpeed = sqrt(vx * vx + (vy - g * time) * (vy - g * time))
            time += step
        }

        return step to (x to (maxHeight to endSpeed))
    }

    @SuppressLint("SetTextI18n")
    private fun displayResults(timeStep: Double, distance: Double, maxHeight: Double, speedAtEndPoint: Double) {
        val row = TableRow(this)
        val timeStepText = TextView(this).apply { text = "| %.2f".format(timeStep) }
        val distanceText = TextView(this).apply { text = " | %.2f".format(distance) }
        val maxHeightText = TextView(this).apply { text = " | %.2f".format(maxHeight) }
        val speedAtEndPointText = TextView(this).apply { text = " %.2f".format(speedAtEndPoint) }
        row.addView(timeStepText)
        row.addView(distanceText)
        row.addView(maxHeightText)
        row.addView(speedAtEndPointText)
        tableLayout.addView(row)
    }
}
