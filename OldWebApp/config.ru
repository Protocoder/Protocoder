
# EventMachine.run do
  require "./config/boot.rb"

  # EventMachine::WebSocket.start(host: '0.0.0.0', port: 8081) do |ws|
    # WebsocketApp.new(ws)
  # end

  App.run!(port: 8079)
# end
