class ChangeEventRegistrationUpdatedToModified < ActiveRecord::Migration
  def change
    rename_column :accounts, :updated, :modified
    change_column_default :accounts, :modified, false
  end
end
