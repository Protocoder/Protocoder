/*
 * \\\ Example: Record and Playback a Sound
 *
 */

ui.addButton('Record', 0, 0, 1, 0.5).onClick(function () {
  media.recordSound('recording.mp4', true)
})

ui.addButton('Play', 0, 0.5, 1, 0.5).onClick(function () {
  media.playSound('recording.mp4')
})