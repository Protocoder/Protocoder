/* @pjs transparent="true"; */ //this adds transparency to the sketch 

/* 
 *
 */


float count = 0;

void setup() {  
  // BASIC SETUP STUFF
  size($(document).width(), $(document).height());
  smooth();
  noStroke();


} 

void draw() { 

  background(0, 0, 0, 0); //transparent 
  fill(0, 10);

  for (int i = 0; i < width; i += 30) {
    for (int j = 0; j < height; j += 30) {
      float s = 15 * sin(count + (i * j));
      ellipse(i, j, s, s);
    }
  }

  count = count + 0.05;
}

//just a little helper function
//aparently processing.js shows a console that we might not want
//so its better sometimes to use console.log
public void debug(String str) {
	if(debugOut) {
		if(debugConsole) {
			console.log(str);
		} else {
			println(str);
		}
	
	}

}