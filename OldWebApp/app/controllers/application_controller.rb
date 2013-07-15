class App < Sinatra::Base
  register Sinatra::RespondWith
  
  get '/docs.json' do
    json YAML::load_file(File.join(App.settings.root, "config", "docs.yml"))
  end
  get '/android_help' do
    respond_to do |t|
      t.html { erb :'android/help', layout: false }
      t.on('*/*') { "UH OH" }
    end
  end
  get '/android' do
    respond_to do |t|
      t.html { erb :'android/index', layout: false }
      t.on('*/*') { "UH OH" }
    end
  end
  get '*' do
    respond_to do |t|
      t.html { erb :'home/index' }
      t.on('*/*') { "UH OH" }
    end
  end
end
