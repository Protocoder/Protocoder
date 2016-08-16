/*
 * \\\ Example: OSC network protocol
 *
 * pretty protocol "to connect" different
 * devices / software together
 */

// create a osc server and listen to incoming messages
var oscServer = network.createOSCServer(9000).onNewData(function (event) {
  console.log(event.name + ' ' + event.data)
})

var client
ui.addButton('Connect', 0, 0, 1, 0.2).onClick(function () {
  client = network.connectOSC('127.0.0.1', 9000)
})

// send a osc message with and array as parameters
ui.addButton('Send', 0, 0.2, 1, 0.2).onClick(function () {
  var o = [':)', ':D', 2]
  client.send('/hello', o)
})
