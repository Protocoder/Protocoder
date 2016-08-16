/*
 * \\\ Example: Livecoding feedback
 *
 * We can execute any line of code whenever we want from the 
 * WebIDE, just press 
 * Control + Shift + X in Linux or Windows
 * Cmd + Shift + X in Mac to live execute the line
 *
 * Using the following code we can see an overlay in the screen
 */

var l = app.liveCodingFeedback()
						.autoHide(true)
						.textSize(25)
						.write('hello')
						.backgroundColor('#55000055')
						.show(true);

device.vibrate(100) // Execute this line!