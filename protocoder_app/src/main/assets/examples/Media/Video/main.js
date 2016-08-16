/*
 * \\\ Example:	Video playback
 *
 * video of cityfireflies by uncoded.es
 * Sergio Galan and Victor Diaz
 */

var video = ui.addVideo('cityfireflies.m4v', 0, 0, 1, 1)

video.onLoaded(function() {
  video.play()
})

video.onUpdate(function(ms, totalDuration) {
  // console.log(ms + ' ' + totalDuration)
})

ui.addButton('Play', 0, 0).onClick(function() {
  video.play()
})

ui.addButton('Pause', 0, 0.2).onClick(function() {
  video.pause()
})

ui.addSlider(0, 0.4).range(0, 1).onChange(function(val) {
  var pos = val * video.getDuration() / 100
  console.log(pos)
  video.seekTo(pos)
})
