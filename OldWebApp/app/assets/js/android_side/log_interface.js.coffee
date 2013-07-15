class (exports ? this).LogInterface extends BaseInterface
  
  log: (msg) -> 
    @proxy.log(msg)

LogInterface.instance(window, "console", "_console", false)