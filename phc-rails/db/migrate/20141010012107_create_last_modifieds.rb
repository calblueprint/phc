class CreateLastModifieds < ActiveRecord::Migration
  def change
    create_table :last_modifieds do |t|
      t.datetime :last_modified_datetime

      t.timestamps
    end
  end
end
