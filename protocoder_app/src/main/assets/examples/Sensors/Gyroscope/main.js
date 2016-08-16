/*
 * \\\ Example: Gyroscope
 */

var plot = ui.addPlot(0, 0, 1, 0.2).range(-15, 15)

sensors.gyroscope.onChange(function (data) {
  plot.update('x', data.x)
  plot.update('y', data.y)
  plot.update('z', data.z)
})

// stop gyroscope
ui.addButton('STOP', 0, 0.2).onClick(function () {
  sensors.gyroscope.stop()
})