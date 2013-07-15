class WebsocketApp
  attr_reader :ws
  def initialize(ws)
    @ws = ws

    ws.onopen do
      sockets.push ws
      
      # Thread.new {
      #   IO.popen("/usr/bin/tail -f /private/var/log/system.log") do |d|
      #     p [:d]
      #     while line = d.gets
      #       p [:line, line]
      #       ws.send JSON.dump(msg: line, type: 'log_event')
      #     end
      #   end
      # }
    end

    ws.onmessage do |msg|
      handle_message(JSON.parse(msg), ws)
    end

    ws.onclose do
      sockets.delete(ws)
    end
  end

  def handle_message(msg, ws)
    data = {}
    p [:handle_message, msg]
    case msg['type']
    when 'create_new_project'
      name = msg['name']
      app = Application.create(name: name)
      data['project'] = {name: app.name, id: app.id, code: app.code}
    when 'get_new_code'
      data['code'] = open(File.join(templates_dir, "new.js")).read
    when 'save_file'
      code = msg['code']
      app = Application.find(name: msg['name'])
      app.new_code = code
      app.save
      data['id'] = app
      data['success'] = true
    when 'run_project'
      data['success'] = true
    when 'get_code'
      if msg['name']
        app = Application.find(name: msg['name'])
        data['code'] = app.code
      end
    when 'get_projects'
      data['projects'] = Application.all
    else
      data['error'] = "Unknown message type"
    end
    data['callback_id'] = msg['callback_id']
    ws.send JSON.dump(data)
  end

  def sockets
    @sockets ||= []
  end

  def root_dir
    @root_dir ||= File.expand_path("../../", File.dirname(__FILE__))
  end

  def assets_dir
    @assets_dir ||= File.join(root_dir, "app", "assets")
  end

  def templates_dir
    @templates_dir ||= File.join(assets_dir, "templates")
  end
end
