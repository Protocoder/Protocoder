/*
* Start coding and have fun! 
* if you want you can use this header
* to explain your project!
* 
*/ 

Q q;

void setup() {
  size(192, 157); 
  background(0); 
  frameRate(25);
    
  q = new Q();

    q.addLayer(function() { 
        rect(55, 55, 55, 55); 
    });
    
    q.addFunction(function() {
        this.x = this.x + 110;
        console.log(Q.x);
    });
    
}

void draw () {
 
   q.draw();
}

class Q {
    var layer = [];
    var f  = [];
    var x = 25; 
    var y = 25;
    
    Q() {
 
    }
    
    void draw() {
        
        for (var i in f) {
         f[i]();   
        }

        ellipse(x, y, 25, 25);
        
        for (var i in layer) {
         layer[i]();   
        }


    }
    
    void addFunction(m) {
        f.push(m);

    }
 
    void addLayer(m) {
     layer.push(m);
    }
}

