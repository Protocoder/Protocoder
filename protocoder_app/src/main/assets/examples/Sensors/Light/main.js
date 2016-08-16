/*
 * \\\ Example: Light Sensor
 */

var plot = ui.addPlot(0, 0, 1, 0.2).range(-15, 15)

sensors.light.onChange(function (data) {
  plot.update('intensity', data.intensity)
  console.log(data.intensity)
})

// stop light
ui.addButton('STOP', 0, 0.2).onClick(function () {
  sensors.intensity.stop()
})