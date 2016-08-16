/*
 * \\\ Example: NFC
 */

var infoTxt = ui.addText('tap to get NFC id and content', 0, 0, 1, 1)

// when tapping on a nfc the id and content will be displayed on the label
device.nfc.onNewData(function (id, data) {
  console.log('the nfc id is: ' + id, data)
  infoTxt.html('<strong>id: </strong>' + id + '<br /> <strong>data: </strong>' + data)
})

// when we click
// the next touched nfc will be written with the data
ui.addButton('Write to NFC', 0, 0.8, 1, 0.5).onClick(function (){
	device.nfc.write('this is a test', function () {
		infoTxt.text('data written')
	})
})
