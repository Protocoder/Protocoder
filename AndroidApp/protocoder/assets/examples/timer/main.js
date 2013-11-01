/*
*	Timer Executes a task repeatedly 
*   Delay Delays a function for a especific time 
*/

util.loop(1000, function () { 
    console.log("repeating every 1000 ms");
}); 

util.loop(5000, function () { 
    console.log("repeating every 5000 ms");
});

util.delay(1000, function() {
   console.log("delayed 1000 ms"); 
});

util.delay(2000, function() {
   console.log("delayed 2000 ms"); 
}); 