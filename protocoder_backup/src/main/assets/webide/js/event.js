/*
*	Event
*
*/


var Event = function() { 

};

Event.prototype.listen = function(name, e) { 
	document.addEventListener(name, e);
};


Event.prototype.remove = function (name, event) {
	document.removeEventListener(name, event, false);
};

Event.prototype.send = function (nameEvent, s) { 

	var o = {};
	o.detail = s;
	o.bubbles = false;
	o.cancelable = true;

	var e = new CustomEvent(nameEvent, o);
	//console.log(e);
	document.dispatchEvent(e);
};
