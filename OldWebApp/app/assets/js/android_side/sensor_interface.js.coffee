class (exports ? this).SensorInterface extends BaseInterface
  
  startAccelerometer: (cb) ->
    ## Listen for socket events because this sensor
    ## happens in the background
    @proxy.startAccelerometer(cb)
  
  stopAccelerometer: () ->
    @proxy.stopAccelerometer("")

SensorInterface.instance(window, "sensors", "_sensors", false)
