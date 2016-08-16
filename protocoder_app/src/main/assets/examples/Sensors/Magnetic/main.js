/*
 * \\\ Example: Magnetic Sensor
 */

var plot = ui.addPlot(0, 0, 1, 0.2).range(0, 10)

sensors.magnetic.onChange(function (data) {
  plot.update('x', data.x)
  plot.update('y', data.y)
  plot.update('z', data.z)
})

// stop magnetic sensor
ui.addButton('STOP', 0, 0.2).onClick(function () {
  sensors.magnetic.stop()
})