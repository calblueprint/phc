class AddUpdatedFieldToAccounts < ActiveRecord::Migration
  def change
    add_column :accounts, :updated, :boolean
  end
end
