/*
 * \\\ Example: Basic Widgets
 *
 * All widgets available in Protocoder
 */
 
 
ui.background(0, 0, 255)
ui.toast("hola")
ui.addButton('btn 1', 0, 0, 1, 0.1)
ui.addText('text', 0, 0.1, 1, 0.1)
ui.addInput('write here yout text', 0, 0.2, 1, 0.1)
ui.addCheckbox('check me', 0, 0.3, 1, 0.1)
ui.addToggle('toggle', 0, 0.4, 1, 0.1)
ui.addSwitch('Switch', 0, 0.5, 1, 0.1)
ui.addSlider(0, 0.6, 1, 0.1)
ui.addProgressBar(0, 0.7, 1, 0.1).progress(50)
var radioGroup = ui.addRadioButtonGroup(0, 0.8)
radioGroup.add('Option 1')
radioGroup.add('Option 2')
radioGroup.onSelected(function (d) {
  ui.toast('selected: ' + d)
})

ui.addImage('patata2.png', 0, 0.9)

ui.toolbar.title('UI examples ---> lalalallala ')
ui.toolbar.background(55, 155, 155, 255)
ui.toolbar.show(true)

// Add a generic button
var btn = ui.addButton('Button', 0, 0, 1, 0.1).onClick(function (){

})

// Add a seekbar
var slider = ui.addSlider(0, 0.1, 1, 0.1, 100, 50).onChange(function (val) {
  console.log(val)
})

// Add a label with text
ui.addText('I love ice cream', 0, 0.2, 1, 0.1)

// Add an edit text
ui.addInput('Type something here', 0, 0.3, 1, 0.1).onChange(function (val){
  console.log(val)
})

// Add a toggle button
ui.addToggle('I am toggleable', 0, 0.4, 1, 0.1, true).onChange(function (val) {
  console.log(val)
})

// Add an image
ui.addImage('patata2.png', 0, 0, 0.2, 0.2)

// Add an image button with a background
ui.addImageButton(400, 1050, 300, 300,'patata2.png', false).onClick(function (val){
  console.log(val)
})

var htmlText = ui.addText('lala', 0, 2100, 500, 100)
htmlText.html('This is a <strong> HTML </strong> text')

var font = util.loadFont('visitor2.ttf')
var label = ui.addText('hola fonts', 20, 2200, 300, 200)
label.textSize(80)
label.color('#222222')
label.font(font)

// apparently some devices cannot load svg files
var img = ui.addImage('awesome_tiger.svg', 0, 2300, 500, 500)
