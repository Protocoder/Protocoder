/*
 * \\\ Example: Accelerometer
 */

var plot = ui.addPlot(0, 0, 1, 0.2).range(-15, 15)

sensors.accelerometer.onChange(function (data) {
  plot.update('x', data.x)
  plot.update('y', data.y)
  plot.update('z', data.z)
})

// stop accelerometer
ui.addButton('STOP', 0, 0.2).onClick(function () {
  sensors.accelerometer.stop()
})