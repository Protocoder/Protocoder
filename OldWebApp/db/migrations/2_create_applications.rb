puts "HI HI HI applications #{__FILE__}"
Sequel.migration do
  change do
    create_table(:applications) do
      primary_key :id
      String :name, :null=>false
      String :url, :null=>false
    end
  end
end

