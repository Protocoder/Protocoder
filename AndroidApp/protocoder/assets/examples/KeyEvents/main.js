/* 
*	KeyEvents are really awesome it seems that only work 
* 	pressing buttons such as Volume UP & Down but the events 
* 	are triggered as well using Bluetooth keyboards, Makey Makeys 
*   and some game controllers  
*	
*/ 

android.onKeyDown(function(key) {
    ui.toast("pressed key " +  key, 1000);
});