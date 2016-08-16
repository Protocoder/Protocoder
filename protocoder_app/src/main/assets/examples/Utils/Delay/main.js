/*
 * \\\ Example: Delay
 * 
 * Execute a function after T milliseconds
 */

var txt = ui.addText('wait 5000 ms --> ', 0, 0)

util.delay(5000, function() {
  txt.text('helloooo \n')
})