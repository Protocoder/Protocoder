
//test.vibrate(500);
test.toast_and_callback("Hello Gopi!!!",500,"foo1");

function foo(){
  test.vibrate(500);
  test.toast("Back in Javascript!!!",500);
}

function foo1(){
  test.vibrate(1000);
}

android.button(100,100,100,100,foo1);