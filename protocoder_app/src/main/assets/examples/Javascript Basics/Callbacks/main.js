/*
 * \\\ Example: Callbacks 
 *
 * Callbacks are basically functions that we
 * can pass to a function as an argument
 *
 */

function myfunction(callback) {
  callback(2)
}

myfunction(function (value) {
  console.log(value)
})