class RemoveLastModifiedDateTimeFromLastModifieds < ActiveRecord::Migration
  def change
    remove_column :last_modifieds, :last_modified_datetime
  end
end
