/*
 * \\\ Example:	Text To Speech
 *
 * Speak using the system's TextToSpeech
 */

ui.addButton('Speak!', 0, 0).onClick(function () {
	media.textToSpeech('hola amigos')
})
