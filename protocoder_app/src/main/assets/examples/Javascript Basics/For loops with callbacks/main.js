/*
 * \\\ Example: For Loop with callbacks
 *
 * Javascript has a strange behaviour when attaching
 * a callback inside a loop. It always attach the last
 *
 * We need to do it the following way using a 'closure'
 */

for (var i = 0; i < 5; i++) {
  (function (i) {
    ui.addButton(i, 0, 0.2 * i, 1, 0.2).onClick(function () {
      console.log(i)
    })    
  })(i);
}