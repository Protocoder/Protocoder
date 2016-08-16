/*
 * \\\ Example: Other Sensors
 *
 * Barometer and StepDetector
 * Only few devices have them 
 */

sensors.pressure.onChange(function (data) {
  console.log('barometer ', data.bar)
})

sensors.stepDetector.onChange(function (data) {
  console.log('step detected')
})