/*
*	New project by ....... 
*
*/
editor.showConsole(false);


ui.setAbsoluteLayout(false);

//ui.backgroundColor(185, 185, 185);
var cardm = ui.addCard();
cardm.setTitle("tabs");
var r = cardm.addRow(2);
r.addWidget(ui.addButton("show", function() { card.show(); }));
r.addWidget(ui.addButton("hide", function() { card.hide(); }));
var pad = ui.addXYPad(function(e) { 
    
});
ui.rotate(cardm, 0, 0, 0);
ui.move(cardm, 0, 0);
//ui.scale(card2, 1, 1);
//ui.resizeView(pad, 10, 20);

ui.addView(pad);
ui.resizeView(pad, 200);

var card = ui.addCard();
card.setTitle("hola");

card.addWidget(ui.addButton("hola", function() {  }));
card.addWidget(ui.addSlider(100, 1, function(p) { }));

var row = card.addRow(100);
row.addWidget(ui.addButton("lala", function() { }));

var row2 = card.addRow(3);
row2.addWidget(ui.addLabel("lala"));

var card2 = ui.addCard();
card2.addWidget(ui.addButton("hola", function() {  }));
//card.alpha(1)
//var plot = ui.addPlot(-12, 12); 


