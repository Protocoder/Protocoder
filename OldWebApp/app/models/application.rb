class Application < Sequel::Model
  plugin :json_serializer

  attr_writer :new_code

  def before_create
    new_file = File.join(App.root, "test", "fixtures", "#{name.gsub(/\W+/, '')}.js")
    FileUtils.cp File.join(App.root, "app", "assets", "templates", "new.js"), new_file
    self.url = new_file
    super
  end

  def after_save
    if @new_code
      File.open(full_url, "w") do |f|
        f << @new_code
      end
      @new_code = nil
    end
  end

  def code
    open(full_url).read
  end

  def full_url
    File.expand_path(url, App.settings.root)
  end
end

