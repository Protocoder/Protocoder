/*
 * \\\ Example: Play a Sound
 *
 *	Supports wav, mp3, and ogg (recommended) files
 */

ui.addButton('Meow', 0, 0).onClick(function() {
	media.playSound('meow.ogg')
})