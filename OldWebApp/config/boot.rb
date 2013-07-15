require "rubygems"
require "bundler"
require "yaml"
require 'zurb-foundation'
require 'sinatra/asset_pipeline'

# require "bundle gems"
ENV["RACK_ENV"] ||= "development"
Bundler.require(:default, ENV["RACK_ENV"].to_sym)

# init database
DB = Sequel.connect(YAML.load_file("./config/database.yml")["default"]["url"])

require 'sinatra/asset_pipeline'
require 'sinatra-websocket'

class App < Sinatra::Base
  # init sinatra
  set :sessions, true
  set :session_secret, "889aa0f75ea09f3682bbe5f23325c3f1"
  set :root, File.expand_path(".")
  set :views, settings.root + "/app/views"
  
  ## ASSETS
  set :manifest_files, %w(
    application.js
    application.css
    vendor.js
    new.js
  )

  set :manifest_dirs, %w()
  
  set :runner_files, %w(
    android.js
  )
  set :runner_dirs, %w()

  set :assets_precompile, %w(*.js *.css *.html *.png *.jpg *.ttf)

  register Sinatra::AssetPipeline
  register Sinatra::Namespace
  set :assets_prefix, '/assets'
  set :assets_digest, false
  set :digest_assets, false
  set :assets_js_compressor, :uglifier
  sprockets.append_path File.join(root, 'app', 'assets', 'css')
  sprockets.append_path File.join(root, 'app', 'assets', 'js')
  sprockets.append_path File.join(root, 'app', 'assets', 'img')
  sprockets.append_path File.join(root, 'app', 'assets', 'templates')

  # sinatra reloader
  configure :development do
  require "sinatra/reloader"
    register Sinatra::Reloader
    #also_reload "lib/**/*.rb", "app/{controllers,models,helpers}/**/*.rb"
  end
end

# require project files
Dir.glob "./{lib,app/models,app/helpers,app/controllers}/**/*.rb" do |f|
  require f
end
