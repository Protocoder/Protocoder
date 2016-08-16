/*
 * \\\ Example: Functions
 */

function saySomething (msg) {
  device.vibrate(100)
  ui.toast(msg)
}

saySomething('Hola Mundo!')