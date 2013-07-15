class (exports ? this).UiInterface extends BaseInterface
  
  addButton: (text, callback) ->
    @proxy.addButton text, callback
    
  addLabel: (text) ->
    @proxy.addTextLabel(text)
    
  addSeekBar: (label, to, start, callback) ->
    @proxy.addSeekBar(label, to, start, callback)
    
  addCheckbox: (label, selected, callback) ->
    @proxy.addACheckbox(label, selected, callback)
  
  addToggleButton: (label, callback) ->
    @proxy.addAToggleButton(label, callback)
    
  showToast: (text, duration=1000) ->
    @proxy.showToast(text, duration)

  addPlot: (callback) ->
    @proxy.addPlot(callback)

  addPlotValue: (value) ->
    @proxy.addPlotValue value


UiInterface.instance(window, "ui", "_ui", false)
