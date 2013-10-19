var q = new Array();
q.push("hello 1");
q.push("hello 2");
q.push("hello 3");
q.push("hello 4");
q.push("hello 5");

fileIO.saveStrings("file.txt", q);

var read = fileIO.loadStrings("qq.txt");

for(var i = 0; i < read.length; i++) { 
  console.log(read[i]);  
} 