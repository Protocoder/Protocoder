/*
 * \\\ Example: GPS 
 */
 
var txt = ui.addText('', 0, 0)

// we start in latitude and longitude 0, 0
var map	= ui.addImage('https://maps.googleapis.com/maps/api/staticmap?center=0,0&zoom=20&size=700x500&sensor=false', 0, 400, 700, 500)

// for each GPS update the image and values are changed
sensors.gps.onChange(function (data) {
  txt.clear()
  txt.append('Latitude : ' + data.latitude)
  txt.append('\nLongitude : ' + data.longitude)
  txt.append('\nAltitude : ' + data.altitude)
  var distance = sensors.gps.distance(data.latitude, data.longitude, 37.1773, 3.5986)
  txt.append('\n\nYour are ' + distance + ' km far away from Granada, Spain')

  var url = 'https://maps.googleapis.com/maps/api/staticmap?center=' + data.latitude + ',' + data.longitude + '&zoom=20&size=700x500&sensor=false'
  map.load(url)
})