/*
 * \\\ Example: Accelerometer
 */

var plot = ui.addPlot(0, 0, 1, 0.2).range(-15, 15)

sensors.orientation.onChange(function (data) {
  plot.update('azimuth', data.azimuth)
  plot.update('pitch', data.pitch)
  plot.update('roll', data.roll)
})

// stop orientation
ui.addButton('STOP', 0, 0.2).onClick(function () {
  sensors.orientation.stop()
})