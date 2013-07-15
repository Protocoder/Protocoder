class (exports ? this).AndroidInterface extends BaseInterface
  
  vibrate: (duration) -> 
    @proxy.vibrate(duration)

AndroidInterface.instance(window, "android", "_android", false)