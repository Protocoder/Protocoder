/*
 * \\\ Example: Classes	
 *
 * Javascript doesnt have classes per se, 
 * but we can get similar results using the prototype 
 *
 */

var Robot = function (name) {
  this.name = name;
};

Robot.prototype.say = function(text) {
  media.textToSpeech('My name is ' + this.name + '. ' + text)
};

var robot1 = new Robot("WKM")
var robot2 = new Robot("1010110")

robot1.say('hello')
