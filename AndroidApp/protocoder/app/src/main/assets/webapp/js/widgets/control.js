//Widgets.register();


function addButton(name, userFunction) {
	$('<button class = "widget_button" id = "' + name + '"> '+ name +' </button>')
		.click(userFunction)
		.appendTo("#widgets");
}


function addSlider(name, min, max, userFunction) {

	$('<input id="defaultSlider" type="range" min="0" max="500" />')
		.change(userFunction)
		.appendTo("#widgets");

}