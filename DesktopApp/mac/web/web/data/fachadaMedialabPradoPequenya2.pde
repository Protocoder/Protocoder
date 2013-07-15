/*
* Emulador de la fachada del medialab prado 
*
* @author: Victor Diaz Barrales victormdb@gmail.com
*   		Sergio Galán sergio.galan@gmail.com
*
* Haz lo que quieras con el codigo! Esto tiene licencia BSD to loca! 
* 
* 
*/ 


void setup() {
  //size(screen.width, screen.height); 
  //size(192, 157); 
  size($('canvas').width(), $('canvas').width()); 
  


}

void draw () {

  pushMatrix(); 
  translate(0, 0); 
  for (int i = 0; i < width; i += 4) { 
    line(i, 0, i, height); 
  } 
  
  
  for (int j = 0; j < height; j += 4) { 
    line(0, j, width, j); 
  } 
  
  fill(0); 
  rect(0, 0, 72, 16); 
  rect(0 + (72 + 48), 0, (72 + 48 + 72), 16); 
  rect(0, 16, 36, 16);  
  rect(0 + (72 + 48 + 36), 16, (36 + 36+ 48 + 36), 16);  
  popMatrix(); 
  
}



