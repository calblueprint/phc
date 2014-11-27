class AddLastModifiedToLastModifieds < ActiveRecord::Migration
  def change
    add_column :last_modifieds, :last_modified, :string
  end
end
