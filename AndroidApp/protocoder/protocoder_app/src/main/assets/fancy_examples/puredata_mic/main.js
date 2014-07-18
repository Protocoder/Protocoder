/* 
*	Pure Data sound engine example 
*	transfer your pd patch and send values to it 
* 	with the sendFloat, sendBang methods 
*/

var pd = media.initPDPatch("main_mic.pd", function(type, data) { 
    console.log(type, data);
});

ui.addButton("BNG", 0, 0, 200, 100, function () { 
    pd.sendBang("button0");
});


ui.addButton("BNG2", 0, 125, 200, 100, function () { 
    pd.sendBang("button1");
});
