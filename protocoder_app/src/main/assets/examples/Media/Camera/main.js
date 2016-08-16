/*
 * \\\ Example: Camera
 *
 * Show camera view and capture and image when button is clicked
 * if you are in the WebIDE an image will be shown in the console
 */

// add camera
var camera = ui.addCameraView('back', 0, 0, 1, 1)

// take a picture and save it
ui.addButton('Take pic', 0, 0, 1, 0.2).onClick(function () {
    camera.takePicture('picture.png', function () {
        console.log('<img src="" + app.servingUrl() + "picture.png"/>')
    })
})

// toggle flash on and off
ui.addToggle('Flash', 0, 0.2, 1, 0.2, false).onChange(function (state) {
  camera.turnOnFlash(state)
})
