/*
 * \\\ Example: Proximity Sensor
 */

var plot = ui.addPlot(0, 0, 1, 0.2).range(0, 10)

sensors.proximity.onChange(function (data) {
  plot.update('distance', data.distance)
})

// stop proximity
ui.addButton('STOP', 0, 0.2).onClick(function () {
  sensors.proximity.stop()
})