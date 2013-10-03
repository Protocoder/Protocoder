/* 
* 	Voice recognition example 
*	It repeates whatever is recognized 
*	you might have internet connection on certain android versions
*
*/ 

media.startVoiceRecognition(function(text) { 
    console.log(text);
    media.textToSpeech(text);
});