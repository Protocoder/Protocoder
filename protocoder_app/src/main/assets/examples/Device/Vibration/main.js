/*
 * \\\ Example: Vibration	
 */

ui.addButton('brbrbrbrbrbrrr', 0, 0, 1, 1).onClick(function () {
  device.vibrate(500)
})