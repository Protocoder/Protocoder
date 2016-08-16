/*
 * \\\ Example: Looper / Repeat Interval
 * 
 * Executes a function every T milliseconds
 */

var txt = ui.addText('', 0, 0)

var loop = util.loop(5000, function () {
  txt.append('repeating every 5000 ms \n')
})
loop.start()

ui.addButton('STOP', 0, 0.8, function () {
  loop.stop()
})

// this is how you change the speed of the looper
// l2.speed(5000)