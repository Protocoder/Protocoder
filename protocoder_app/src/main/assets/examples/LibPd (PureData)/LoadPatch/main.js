/*
 * \\\ Example: LibPd (Pure Data)
 *
 * Pure Data is an awesome sound engine
 *
 * Just transfer your pd patch and send values to it
 * with the sendFloat, sendBang methods
 *
 * More Info about Pure Data
 * http://puredata.info 
 */

var pd = media.initPdPatch('sinwave.pd')

pd.onNewData(function(data) {
  console.log(data)
})

// add plot setting the limits from -12 to 12
var plot = ui.addPlot(0, 0, 1, 1).range(-12, 12)

sensors.accelerometer.onChange(function (data) {
  pd.sendFloat('value', 82 + Math.round(data.x))
  plot.update(data.x)
})
