class CreateServiceLists < ActiveRecord::Migration
  def change
    create_table :service_lists do |t|
      t.string :name
      t.string :salesforce_name

      t.timestamps null: false
    end
    add_index :service_lists, :name, unique: true
    add_index :service_lists, :salesforce_name, unique: true
  end
end
