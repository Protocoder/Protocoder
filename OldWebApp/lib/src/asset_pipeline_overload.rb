require 'rake'
require 'rake/tasklib'
require 'rake/sprocketstask'

module Sinatra
  module AssetPipeline
    class Task < Rake::TaskLib
      def initialize(app)
        namespace :assets do
          desc "Precompile assets and copy"
          task :precompile_and_copy => [:clean] do
            android_assets_root = File.join(File.dirname(__FILE__), "..", "..", "..", "android_scripting", "MakeWithMoto", "assets", "assets")
            runner_root = File.join(File.dirname(__FILE__), "..", "..", "..", "android_scripting", "MakeWithMotoAppRunner", "assets", "assets")
            
            # Setup sprockets manifest
            environment = app.sprockets
            manifest = Sprockets::Manifest.new(environment.index, app.assets_path)
            manifest.compile(app.assets_precompile)
            # Clean out android root
            unless android_assets_root.empty?
              FileUtils.rm_rf(Dir["#{android_assets_root}/*"])
            end
            unless runner_root.empty?
              FileUtils.rm_rf(Dir["#{runner_root}/*"])
            end
            manifest.files.map do |name, f|
              if should_save_file?(app.manifest_files, app.manifest_dirs, f)
                copy_manifest_file(app, f, name, android_assets_root)
              elsif should_save_file?(app.runner_files, app.runner_dirs, f)
                copy_manifest_file(app, f, name, runner_root)
              end
            end

            req = Rack::MockRequest.new(app)
            index = req.get('/').body
            File.open(File.join(android_assets_root, "..", "index.html"), 'w') do |f|
              f << index
            end
            index = req.get('/android').body
            File.open(File.join(android_assets_root, "..", "script_template.html"), 'w') do |f|
              f << index
            end
            index = req.get('/android_help').body
            File.open(File.join(android_assets_root, "..", "help.html"), 'w') do |f|
              f << index
            end
            ## Finally, docs
            docs = File.join(File.dirname(__FILE__), "..", "..", "config", "docs.yml")
            yml = YAML::load_file(docs)
            File.open(File.join(android_assets_root, "..", "docs.json"), "w") do |f|
              f << yml.to_json
            end
            
            puts <<-EOE
            ALL DONE!
            EOE
          end

          desc "Clean assets"
          task :clean do
            FileUtils.rm_rf(app.assets_path)
          end
        end
        
        ### PRIVATE
        def copy_manifest_file(app, f, name, root)
          src = File.join(app.assets_path, name)
          dest = File.join(root, name)
          clean_dest = File.join(root, f['logical_path']);
          FileUtils.mkdir_p File.dirname(dest)# if File.dirname(name) != "."
          # FileUtils.cp src, dest
          FileUtils.cp src, clean_dest
        end
        
        def should_save_file?(arr_files, arr_dirs, f)
          arr_files.include?(f['logical_path']) ||  ## if we've configured the file to be saved
            File.extname(f['logical_path']) == ".html" || ## Always save .html
            File.extname(f['logical_path']) == ".png" || ## Always save images
            arr_dirs.include?(File.dirname(f['logical_path'])) ## If the directory should be saved
        end
      end

      def self.define!(app)
        self.new app
      end
    end
  end
end
