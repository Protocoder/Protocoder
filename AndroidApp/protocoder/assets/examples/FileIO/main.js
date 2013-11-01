var q = new Array();
q.push("hello 1");
q.push("hello 2");
q.push("hello 3");
q.push("hello 4");
q.push("hello 5");

fileio.saveStrings("file.txt", q);

var read = fileio.loadStrings("file.txt");

for(var i = 0; i < read.length; i++) { 
  console.log(read[i]);  
} 