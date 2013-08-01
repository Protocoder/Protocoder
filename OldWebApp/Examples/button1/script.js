//Works!!!            ui.button("B1", 500,400,500,100,"function foo(){ android.vibrate(500); }");
//Works!!!            ui.button("B2", 500,400,500,100,function foo(){ android.vibrate(500); });
//Does not work!!!    ui.button("B3", 500,400,500,100,"function { android.vibrate(500); }");
//Does not work       ui.button("B4", 500,400,500,100,function { android.vibrate(500); });
//Works!!!            ui.button("B3", 500,400,500,100,"function() { android.vibrate(500); }");
//Works!!!            ui.button("B4", 500,400,500,100,function() { android.vibrate(500); });
//Works!!!            ui.button("B5", 500,400,500,100,"android.vibrate(500);");
//Does not work       ui.button("B6", 500,400,500,100,android.vibrate(500););
//Works!!!            ui.button("B7", 500,400,500,100,"foo();");
//Does not work       ui.button("B8", 500,400,500,100,foo(););
//Does not work but i can make it work     ui.button("B9", 500,400,500,100,"foo");
//Works!!!              ui.button("B10", 500,400,500,100,foo);


function foo(){
   android.vibrate(500);
}
