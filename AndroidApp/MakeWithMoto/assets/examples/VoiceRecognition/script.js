media.startVoiceRecognition(function(text) { 
    console.log(text);
    media.textToSpeech(text);
});