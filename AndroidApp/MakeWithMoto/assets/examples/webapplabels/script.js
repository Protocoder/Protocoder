//labels need an unique id in order to be identified in the webapp
var label1 = webapp.addLabel("id1", "default_value", 100, 100, 200, 100);
var label2 = webapp.addLabel("id2", "default_value", 100, 400, 200, 100);


ui.button("hola", 0, 0,500,100, function(){
    label1.setText("hola");
    label2.setText("lalallala");

});

ui.button("adios", 0, 200,500,100, function(){
    label1.setText("adios");
    label2.setText("bububububu");
});

