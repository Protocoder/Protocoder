/*
*	New project by ....... 
*
*/

if (false) { 
    media.playSound("bass_c.ogg");
    media.playSound("bass_f.ogg");
    media.playSound("bass_g.ogg");
    media.playSound("bass_low_c.ogg");
    
    media.playSound("trumpet_1.wav");
    media.playSound("trumpet_2.wav");
    media.playSound("trumpet_3.wav");
    media.playSound("trumpet_4.wav");
    
    var l = android.loop(500, function() { 
        media.playSound("trumpet_" + Math.round(1 + 3*Math.random()) + ".wav" );    
        media.playSound("trumpet_" + Math.round(1 + 3*Math.random()) + ".wav" );    
    }); 
    
    l.stop();
    
    media.playSound("120_bpm_bike.wav");
    media.playSound("ride_loop.ogg");
}
