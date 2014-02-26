/*
*	New project by ....... 
*
*/

var number = "12345";

android.onSmsReceived(function(number, msg) { 
   console.log(number + " " + msg); 
   android.smsSend(number, generate(i));
});

for (var i = 0; i < 10; i++) { 
    //console.log(generate(i));
    //android.smsSend("8054723353", generate(Math.round(Math.random() * 10)));

    //console.log(Math.round(Math.sin(i * 0.02) * 10));
} 


function generate(num) {
    var str = "8";
    for(var i = 0; i < num; i++) { 
        str += "="
    } 
    str+="D";
    
    return str;
}

//android.smsSend("8054723353", ":/");

//android.smsSend("5103316788", "hello");
