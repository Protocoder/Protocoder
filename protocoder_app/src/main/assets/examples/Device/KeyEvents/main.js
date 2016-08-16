/*
 * \\\ Example: KeyEvents
 */

device.onKeyUp(function (data) {
  console.log(data)
})

device.onKeyDown(function (data) {
  console.log(data)
})

device.onKeyEvent(function (data) {
  console.log(data)
})