require 'active_support'
namespace :fixtures do
  desc "Create fixture data"
  task :create do
    Dir["#{File.dirname(__FILE__)}/../../test/fixtures/*.yaml"].each do |file|
      loaded = YAML::load_file(file)
      kls = Object.const_get File.basename(file, File.extname(file)).capitalize
      kls.all.each do |obj|
        obj.destroy
      end
      loaded.each do |obj|
        kls.create obj
      end
    end
  end
end
