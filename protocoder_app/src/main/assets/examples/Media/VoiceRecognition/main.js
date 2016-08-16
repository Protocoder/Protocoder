/*
 * \\\ Example: Voice recognition
 *
 *	Using the Android built in voice recognizer
 *  it will show and play back the recognized text
 *
 *	You might have internet connection on certain Android devices
 */

media.voiceRecognition(function (text) {
  console.log(text)
  media.textToSpeech(text)
})
